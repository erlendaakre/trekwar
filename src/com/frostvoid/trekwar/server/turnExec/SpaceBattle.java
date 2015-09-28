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

import com.frostvoid.trekwar.common.Ship;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.User;
import com.frostvoid.trekwar.server.TrekwarServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

/**
 * Represents a single battle taking place in space
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SpaceBattle {
    ArrayList<Ship> ships;
    private StarSystem location;
    private long turn;
    private int round = 0;

    /**
     * Creates a new space battle
     *
     * @param location the starsystem the battle takes place in
     * @param turn     the turn this battle is taking place on
     */
    public SpaceBattle(StarSystem location, long turn) {
        ships = new ArrayList<Ship>();
        this.location = location;
        this.turn = turn;
    }

    /**
     * Gets a random attacking ship (has action points)
     * from all this ships in this battle
     *
     * @return An attacking ship
     */
    public Ship getRandomAttacker() {
        int rand = TrekwarServer.PRNG.nextInt(ships.size());
        boolean looped = false;
        for (int i = rand; i < ships.size(); i++) {
            if (ships.get(i).getActionPoints() > 0) {
                return ships.get(i);
            }
            if (i == ships.size() - 1) {
                if (looped) {
                    // no ships have action points, start next round
                    nextRound();
                }
                i = -1;
                looped = true;
            }
        }
        return null; // this could THEORETICALLY happen if all ships are destroyed at once by some effect.
    }


    /**
     * Finds a ship to defend against an attacker
     *
     * @param attacker the attacker to find a ship for
     * @return the defending ship
     */
    public Ship findTarget(Ship attacker) {
        if (attacker == null) {
            return null;
        }

        int rand = TrekwarServer.PRNG.nextInt(ships.size());
        boolean looped = false;
        Ship weakestShip = null; // TODO find weakest ship
        for (int i = rand; i < ships.size(); i++) {
            if (!ships.get(i).getUser().getFaction().equals(attacker.getUser().getFaction())) {
                return ships.get(i);
            }
            if (i == ships.size() - 1) {
                if (looped) {
                    // no ships to fight == single faction left
                    return null;
                }
                i = -1;
                looped = true;
            }
        }
        return null; // this could THEORETICALLY happen if all ships are destroyed.
    }


    /**
     * Adds a starship to this battle
     *
     * @param ship The ship to add
     */
    public void addShip(Ship ship) {
        ships.add(ship);
    }

    /**
     * Removes a (destroyed) ship from battle
     *
     * @param ship the ship to remove
     */
    public void removeShip(Ship ship) {
        TrekwarServer.LOG.log(Level.FINE, "Removing ship from SpaceBattle: {0}", ship.getName());
        ships.remove(ship);
    }

    /**
     * Gets all the ships in this battle
     *
     * @return list of all ships
     */
    public ArrayList<Ship> getShips() {
        return ships;
    }

    /**
     * Gets the location of this battle
     *
     * @return starsystem where it takes place
     */
    public StarSystem getLocation() {
        return location;
    }

    /**
     * The turn the battle is taking place
     *
     * @return turn number
     */
    public long getTurn() {
        return turn;
    }

    /**
     * Gets the combatants taking part in the battle
     *
     * @return the users fighting in this battle
     */
    public ArrayList<User> getCombatants() {
        ArrayList<User> users = new ArrayList<User>();

        for (Ship s : ships) {
            if (!users.contains(s.getUser())) {
                users.add(s.getUser());
            }
        }

        return users;
    }

    /**
     * End this round of combat
     */
    private void nextRound() {
        round++;
        Collections.shuffle(ships);

        for (Ship ship : ships) {
            ship.battle_restoreActionPoints();
        }
    }

    public int getRound() {
        return round;
    }
}
