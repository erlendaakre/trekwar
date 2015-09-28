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
package com.frostvoid.trekwar.common.orders;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.TurnReportItem.TurnReportSeverity;
import com.frostvoid.trekwar.common.exceptions.InvalidOrderException;
import com.frostvoid.trekwar.common.shipComponents.ColonizationModule;
import com.frostvoid.trekwar.common.shipComponents.ShipComponent;
import com.frostvoid.trekwar.common.structures.Structure;
import com.frostvoid.trekwar.common.utils.Calculations;
import com.frostvoid.trekwar.common.utils.Language;
import com.frostvoid.trekwar.server.TrekwarServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * An order for a ship with a colonization module to start a new colony
 * in a uninhabited starsystem
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ColonizeOrder extends Order {

    private User user;
    private StarSystem starsystem;
    private Ship colonyship;
    private Fleet fleet;
    private int completion; // 0 - 100
    private int numberOfColonyModules;
    private int progressPerTurn; // this amount is added each turn until it reaches 100.

    public ColonizeOrder(User user, StarSystem system, Fleet fleet, Ship colonyship) throws InvalidOrderException {
        this.user = user;
        this.starsystem = system;
        this.fleet = fleet;
        this.colonyship = colonyship;

        if (!colonyship.canColonize()) {
            throw new InvalidOrderException("The ship is not equipped to colonize a system");
        }
        if (!starsystem.getUser().equals(StaticData.nobodyUser)) {
            throw new InvalidOrderException("The starsystem is already owned by " + starsystem.getUser().getUsername());
        }
        if (!starsystem.getStarSystemClassification().equals(StarSystemClassification.starSystem)) {
            throw new InvalidOrderException("Not a normal Starsystem: " + starsystem.getStarSystemClassification());
        }
        if (starsystem.getMaxPopulation() < 500) {
            throw new InvalidOrderException("Starsystem to small, maxpop = " + starsystem.getMaxPopulation());
        }
        if (starsystem.getMaxStructures() < 8) {
            throw new InvalidOrderException("Starsystem to small, max structures = " + starsystem.getMaxStructures());
        }


        numberOfColonyModules = 0;
        for (ShipComponent c : colonyship.getComponents().values()) {
            if (c instanceof ColonizationModule) {
                numberOfColonyModules++;
            }
        }
        switch (numberOfColonyModules) {
            case 1:
                progressPerTurn = 15;
                break;
            case 2:
                progressPerTurn = 20;
                break;
            case 3:
                progressPerTurn = 25;
                break;
            default:
                progressPerTurn = 15;
                break;
        }

        turnsLeft = Calculations.turnsLeft(100, 0, progressPerTurn);
    }

    @Override
    public void execute() {
        if (!orderCompleted) {
            if (!starsystem.getUser().equals(StaticData.nobodyUser)) {
                // somebody beat us to it and already colonized this system
                onCancel();
                return;
            }
            completion += progressPerTurn;
            if (completion >= 100) {
                orderCompleted = true;
            }
        }
        turnsLeft = Calculations.turnsLeft(100, completion, progressPerTurn);
        // WTF is the line below good for? TODO obsolete???
        //turnsLeft = (int) (Math.ceil((((double) 100) - completion) / progressPerTurn));        
    }

    @Override
    public void onComplete() {
        // make sure this system is uninhabited before claiming it
        if (!starsystem.getUser().equals(StaticData.nobodyUser)) {
            onCancel();
            TrekwarServer.getLog().log(Level.SEVERE, "User {0} completed colonization order of {1}, but it is no longer owned by nobody", new Object[]{user.getUsername(), starsystem.getName()});
            return;
        }

        starsystem.setUser(user);
        user.addSystem(starsystem);
        starsystem.setMorale(75);

        // add population
        int habitablePlanets = starsystem.getHabitablePlanets().size();
        for (Planet p : starsystem.getHabitablePlanets()) {
            p.setPopulation(colonyship.getColonists() / habitablePlanets); // any rounding errors are due to horrible transporter accidents
        }
        colonyship.setColonists(0);

        // Sort planets by size, then make list of all planet / slots combos
        // this makes buildings favor being built on the larger planets.
        ArrayList<Planet> sortedPlanets = (ArrayList<Planet>) starsystem.getPlanets().clone();
        Collections.sort(sortedPlanets, new Comparator<Planet>() {
            @Override
            public int compare(Planet o1, Planet o2) {
                return o2.getMaximumStructures() - o1.getMaximumStructures();
            }

        });

        ArrayList<String> locs = new ArrayList<String>(); //"planet-number slot-number"
        for (Planet p : sortedPlanets) {
            for (int i : p.getSurfaceMap().keySet()) {
                locs.add(p.getPlanetNumber() + " " + i);
            }
        }

        // DEFAULT STRUCTURES
        ArrayList<Structure> structuresToBuild = new ArrayList<Structure>(15);
        structuresToBuild.add(StaticData.factory1);
        structuresToBuild.add(StaticData.factory1);
        structuresToBuild.add(StaticData.power1);
        structuresToBuild.add(StaticData.power1);
        structuresToBuild.add(StaticData.farm1);
        structuresToBuild.add(StaticData.lab1);
        structuresToBuild.add(StaticData.lab1);
        structuresToBuild.add(StaticData.deuteriumProcessingPlant1);

        for (int i = 1; i < numberOfColonyModules; i++) {
            structuresToBuild.add(StaticData.factory1);
            structuresToBuild.add(StaticData.factory1);
            structuresToBuild.add(StaticData.power1);
        }

        try {
            // Add structures
            for (int i = 0; i < structuresToBuild.size(); i++) {
                String loc = locs.remove(0);
                StringTokenizer st = new StringTokenizer(loc);
                Planet p = starsystem.getPlanetByNumber(Integer.parseInt(st.nextToken()));
                int slot = Integer.parseInt(st.nextToken());
                starsystem.addStructure(p, slot, structuresToBuild.remove(i));
                p.setStructureEnabled(slot, true);
            }
        } catch (IndexOutOfBoundsException ioobe) {
            // small system, one or more bonus structures not created
        }

        // add some troops
        starsystem.setTroopCount(getInitialTroopCount());

        starsystem.setDeuterium(colonyship.getCurrentDeuterium());

        // notify turn report
        TurnReportItem tr = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), starsystem.getX(), starsystem.getY(), TurnReportSeverity.MEDIUM);
        tr.setSummary(Language.pop(Client.getLanguage().get("turn_report_colonized_1"), starsystem.getName()));
        tr.setDetailed(Language.pop(Client.getLanguage().get("turn_report_colonized_2"), starsystem.getPopulation(), starsystem.getTroopCount()));
        user.addTurnReport(tr);

        TrekwarServer.getLog().log(Level.INFO, "User {0} has colonized system {1}", new Object[]{user.getUsername(), starsystem.getName()});

        colonyship.destroy();
        fleet.setOrder(null);
    }

    private int getInitialTroopCount() {
        int troops = 0;
        for (Planet p : starsystem.getPlanets()) {
            troops += p.getMaximumStructures() / 3;
        }
        return troops;
    }

    @Override
    public String toString() {
        return Client.getLanguage().get("order_colonizing") + " (" + completion + "%)";
    }

    @Override
    public void onCancel() {
        fleet.setOrder(null);
    }
}