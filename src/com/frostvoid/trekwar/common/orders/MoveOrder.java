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

import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.Ship;
import com.frostvoid.trekwar.server.TrekwarServer;

import java.util.logging.Level;

/**
 * An order for a fleet to move to a specified destination (x,y)
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MoveOrder extends Order {

    private int x; // Target 
    private int y; // Target
    private Fleet fleet;

    public MoveOrder(Fleet f, int x, int y) {
        this.fleet = f;
        this.x = x;
        this.y = y;
    }

    // this execute is called each turn, until order is completed
    @Override
    public void execute() {
        if (!orderCompleted) {

            // TODO calculate turns to completion ?
            int tilesToGo = Math.abs(x - fleet.getX()) + Math.abs(y - fleet.getY());
            int eta = (int) Math.ceil(tilesToGo / fleet.getDeuteriumUsage());

            // handle speed
            fleet.setMovementLeft(fleet.getMovementLeft() + fleet.getSpeed());
            int moves = fleet.getMovementLeft() / 10;

            // remove  orders to move to same spot
            if (x == fleet.getX() && y == fleet.getY()) {
                orderCompleted = true;
                fleet.setMovementLeft(0);
            }

            for (; moves > 0; moves--) {
                // handle fuel
                int deuteriumRequired = fleet.getDeuteriumUsage();

                // check if fleet has enough fuel to move
                if (fleet.getDeuteriumLeft() < deuteriumRequired) {
                    // TODO send message in turn report... fleet out of fuel
                    fleet.setMovementLeft(0);
                    break;
                }

                // move fleet
                for (Ship s : fleet.getShips()) {
                    s.setCurrentDeuterium(s.getCurrentDeuterium() - s.getDeuteriumUsage());
                    // borrow deuterium if out
                    if (s.getCurrentDeuterium() <= 0) {
                        Ship borrowFrom = null;
                        // borrow from ship with most deuterium
                        for (Ship s2 : fleet.getShips()) {
                            if (borrowFrom == null) {
                                borrowFrom = s2;
                            } else {
                                if (s2.getCurrentDeuterium() > borrowFrom.getCurrentDeuterium()) {
                                    borrowFrom = s2;
                                }
                            }
                        }
                        // take the negative amount of s from borrowFrom
                        borrowFrom.setCurrentDeuterium(borrowFrom.getCurrentDeuterium() - s.getDeuteriumUsage());
                        s.setCurrentDeuterium(0);
                    }
                }

                // move ship
                fleet.setMovementLeft(fleet.getMovementLeft() - 10);
                int pathX = fleet.getX();
                int pathY = fleet.getY();
                if (x > pathX) {
                    pathX += 1;
                }
                if (x < pathX) {
                    pathX -= 1;
                }
                if (y > pathY) {
                    pathY += 1;
                }
                if (y < pathY) {
                    pathY -= 1;
                }
                fleet.move(TrekwarServer.getGalaxy().getMap(), pathX, pathY);
                TrekwarServer.getLog().log(Level.FINER, "Fleet {0} moved to location {1}:{2}", new Object[]{fleet.getName(), fleet.getX(), fleet.getY()});

                if (x == fleet.getX() && y == fleet.getY()) {
                    orderCompleted = true;
                    fleet.setMovementLeft(0);
                    break;
                }
            }
        }
    }

    @Override
    public void onComplete() {
        fleet.setOrder(null);
        // TODO report to turnreport system when implemented
    }

    @Override
    public String toString() {
        return "moving to " + x + ":" + y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void onCancel() {
        fleet.setOrder(null);
    }
}