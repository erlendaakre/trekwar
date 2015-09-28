/*
 * Copyright 2012 FrostVoid Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.frostvoid.trekwar.server.turnExec;

import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.orders.Order;
import com.frostvoid.trekwar.common.utils.Language;
import com.frostvoid.trekwar.server.TrekwarServer;
import com.frostvoid.trekwar.server.utils.MapTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

/**
 * Handles every aspect of taking a galaxy (game) from one turn to the next
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TurnExecutor {

    private static final int NUM_TASKS = 4;
    private static ExecutorService exec = Executors.newFixedThreadPool(NUM_TASKS);


    /**
     * Executes a turn, updates all game objects (orders, research, population, combat, etc..)
     *
     * @param galaxy the galaxy object
     * @return time it took to execute the turn
     */
    public static long executeTurn(Galaxy galaxy) {
        galaxy.setExecutingTurn(true);
        long startTime = System.currentTimeMillis();
        TrekwarServer.getLog().info("TurnExecutor starting to execute turn " + (galaxy.getCurrentTurn() + 1));
        ArrayList<Order> fleetOrders = new ArrayList<Order>();
        ArrayList<Fleet> allFleetsInGame = new ArrayList<Fleet>();


        // FOR ALL TILES
        int chunkSize = galaxy.getMap().length / NUM_TASKS;
        int mod = galaxy.getMap().length % NUM_TASKS;

        int offset = 0;
        ArrayList<SystemHandler> threads = new ArrayList<SystemHandler>(NUM_TASKS);
        ArrayList<Future> systemhandler_future = new ArrayList<Future>();

        for (int i = 0; i < NUM_TASKS; i++) {
            threads.add(new SystemHandler(galaxy, offset, offset + chunkSize + mod));
            offset += (chunkSize + mod);
            if (i == 0) {
                mod = 0;
            }
        }
        for (SystemHandler handler : threads) {
            systemhandler_future.add(exec.submit(handler));
        }

        // HANDLE ALL USER + FLEET STUFF
        UserAndFleetHandler ufh = new UserAndFleetHandler(galaxy);
        Future<UserAndFleetHandlerResult> ufh_future = exec.submit(ufh);


        // WAIT FOR THREADS BEFORE EXECUTING FLEET ORDERS
        // user and fleet handler
        try {
            UserAndFleetHandlerResult ufh_res = ufh_future.get();
            allFleetsInGame = ufh_res.getFleetsFound();
            fleetOrders = ufh_res.getOrdersFound();
        } catch (Exception ee) {
            TrekwarServer.getLog().log(Level.SEVERE, "UserAndFleetHandler failed execution, fleet orders will NOT be executed", ee);
            TrekwarServer.getLog().log(Level.SEVERE, ee.getMessage());
            ee.printStackTrace();
        }
        // tile threads
        try {
            for (Future f : systemhandler_future) {
                f.get(); // null result, but get() bocks until task is completed
            }
        } catch (Exception ee) {
            TrekwarServer.getLog().log(Level.SEVERE, "Exception waiting for systemhandler_future to return", ee);
            TrekwarServer.getLog().log(Level.SEVERE, ee.getMessage());
            ee.printStackTrace();
        }


        // RANDOMIZE AND EXECUTE ALL FLEET ORDERS... ONLY THING THAT CAUSES STUFF TO MOVE BETWEEN DIFFERENT TILES
        Collections.shuffle(fleetOrders);
        TrekwarServer.getLog().log(Level.INFO, "Executing {0} fleet orders", fleetOrders.size());
        for (Order order : fleetOrders) {
            TrekwarServer.getLog().log(Level.FINE, "Executing order of type: {0}", new Object[]{order.getClass().getSimpleName()});
            order.execute();
            if (order.isCompleted()) {
                order.onComplete();
            }
        }


        // update sensor strength
        for (User u : galaxy.getUsers()) {
            u.clearSensorStrenghts();
            boolean upkeepPenalty = u.hasUpkeepPenalty();

            for (Fleet fleet : u.getFleets()) {
                if (upkeepPenalty && TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_SENSOR_FAIL_CHANCE) {
                    TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), fleet.getX(), fleet.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                    tr.setSummary(TrekwarServer.getLanguage().get("turn_report_sensor_fail_upkeep_1"));
                    tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_sensor_fail_upkeep_2"), fleet.getName()));
                    u.addTurnReport(tr);
                    TrekwarServer.getLog().log(Level.FINE, "Fleet {0} owned by {1} did not update sensors because of upkeep penalty", new Object[]{fleet.getName(), u.getUsername()});
                } else {
                    MapTools.doLOS(galaxy, u, fleet.getX(), fleet.getY(), fleet.getSensorStrength());
                }
            }

            for (StarSystem s : u.getStarSystems()) {
                MapTools.doLOS(galaxy, s.getUser(), s.getX(), s.getY(), s.getSystemScanStrength());
            }
        }


        // Find all systems that have opposing factions
        Set<StarSystem> combatSystems = new HashSet<StarSystem>();
        for (Fleet f : allFleetsInGame) {
            StarSystem s = galaxy.getMap()[f.getX()][f.getY()];
            if (SpaceCombatResolver.systemHasOpposingFleets(s) && SpaceCombatResolver.systemHasFleetWithArmedShips(s)) {
                if (!combatSystems.contains(s)) {
                    combatSystems.add(s);
                }
            }
        }

        // Resolve battle in all systems
        for (StarSystem system : combatSystems) {
            TrekwarServer.LOG.log(Level.INFO, "Creating new space battle at system {0} ({1},{2})", new Object[]{system.getName(), system.getX(), system.getY()});
            SpaceBattle battle = new SpaceBattle(system, galaxy.getCurrentTurn());

            // add all ships in system to the battle
            for (Fleet f : system.getFleets()) {
                for (Ship ship : f.getShips()) {
                    battle.addShip(ship);
                    TrekwarServer.LOG.log(Level.FINER, "Added ship to a battle: {0}", ship.getName());
                }
            }
            // TODO: space combat resolutions are not multithreaded.. Should be fixed at some point?
            TrekwarServer.LOG.log(Level.FINE, "Starting Space Combat Resolver");
            SpaceCombatResolver scr = new SpaceCombatResolver(battle);
            scr.doCombat();
        }

        galaxy.incrementCurrentTurn();
        galaxy.setExecutingTurn(false);
        long stopTime = System.currentTimeMillis();
        return stopTime - startTime;
    }
}