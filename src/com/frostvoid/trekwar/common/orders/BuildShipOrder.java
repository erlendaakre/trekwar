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

import java.util.Random;
import java.util.logging.Level;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.exceptions.ShipException;
import com.frostvoid.trekwar.common.exceptions.SlotException;
import com.frostvoid.trekwar.common.utils.Language;
import com.frostvoid.trekwar.server.utils.MiscTools;
import com.frostvoid.trekwar.common.exceptions.NotUniqueException;
import com.frostvoid.trekwar.server.TrekwarServer;

/**
 * An order to build a starship at a given starsystem
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BuildShipOrder extends Order {

    private User user;
    private StarSystem starsystem;
    private ShipTemplate template;
    private int industryInvested;
    private int positionInBuildQueue; // used by client only

    public BuildShipOrder(User user, StarSystem starsystem, ShipTemplate template) {
        this.user = user;
        this.starsystem = starsystem;
        this.template = template;
        this.industryInvested = 0;

        updateTurnsToCompletion();
    }

    @Override
    public void execute() {
        if (!orderCompleted) {
            if (starsystem.hasShipyard() && starsystem.hasActiveShipyard()) {
                int sysIndustryOutput = starsystem.getSystemIndustrySurplus();
                if (sysIndustryOutput > 0) {
                    industryInvested += sysIndustryOutput;
                }

                if (industryInvested >= template.getCost()) {
                    orderCompleted = true;
                }

                updateTurnsToCompletion();
            } else {
                TurnReportItem tr = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), starsystem.getX(), starsystem.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                tr.setSummary(Client.getLanguage().get("turn_report_buildqueue_cant_build_ship_1"));
                tr.setDetailed(Language.pop(Client.getLanguage().get("turn_report_buildqueue_cant_build_ship_2"), starsystem.getName()));
                starsystem.getUser().addTurnReport(tr);
            }
        }
    }

    @Override
    public void onComplete() {
        try {
            Fleet fleet = null;

            // try and find a shipyard fleet
            for (Fleet f : starsystem.getFleets()) {
                if (f.getName().length() == 10 &&
                        (f.getName().endsWith("th fleet") ||
                        f.getName().endsWith("st fleet") ||
                        f.getName().endsWith("nd fleet") ||
                        f.getName().endsWith("rd fleet"))
                        ) {
                    fleet = f;
                    break;
                }
            }

            // if not, auto generate a shipyard fleet
            if (fleet == null) {
                String fleetName = "";
                int lock_prevention = 0;
                do {
                    Random rand = new Random();
                    String num = "" + (rand.nextInt(99985) + 10);
                    fleetName = (num.substring(num.length() - 2));
                    switch (fleetName.charAt(1)) {
                        case '0':
                            fleetName = fleetName + "th fleet";
                            break;
                        case '1':
                            fleetName = fleetName + "st fleet";
                            break;
                        case '2':
                            fleetName = fleetName + "nd fleet";
                            break;
                        case '3':
                            fleetName = fleetName + "rd fleet";
                            break;
                        default:
                            fleetName = fleetName + "th fleet";
                            break;
                    }
                    lock_prevention++;
                    if (lock_prevention > 300) {
                        fleetName = num;
                    }
                } while (!user.isFleetNameAvailable(fleetName));
                fleet = new Fleet(user, fleetName, starsystem);
                starsystem.addFleet(fleet);
                user.addFleet(fleet);
            }

            // create ship and add it
            Ship s = new Ship(user, fleet, template.getName(), user.getNextShipId(), template.getHullClass());
            s.applyTemplate(template);
            s.initShip();
            fleet.addShip(s);
            
            // if colonyship, add population
            if (s.canColonize()) {
                int colonists = Math.min(s.getColonistCapacity(), starsystem.getPopulation() - 200);
                if (colonists < 20) {
                    // only launch if at least 20 million people on board
                    throw new ShipException("Unable to build colonyship because there are not enough population in system");
                }
                s.setColonists(colonists);
                starsystem.removePopulationDistributed(colonists);
            }

            // add fuel to the ship
            int amount = s.getMaxDeuterium();
            if (amount > starsystem.getDeuterium()) {
                amount = starsystem.getDeuterium();
            }
            s.setCurrentDeuterium(amount);
            starsystem.setDeuterium(starsystem.getDeuterium() - amount);

            // notify turn report
            TurnReportItem tr = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), starsystem.getX(), starsystem.getY(), TurnReportItem.TurnReportSeverity.MEDIUM);
            tr.setSummary(Client.getLanguage().get("turn_report_ship_constructed_1"));
            tr.setDetailed(Language.pop(Client.getLanguage().get("turn_report_ship_constructed_2"), template.getName(), starsystem.getName()));
            starsystem.getUser().addTurnReport(tr);

        } catch (SlotException se) {
            TrekwarServer.getLog().log(Level.SEVERE, "Unable to finalize the ship built in system " + starsystem.getName(), se);
        } catch (NotUniqueException ex) {
            TrekwarServer.getLog().log(Level.SEVERE, "Unable to add ship to flett, not unique ", ex);
        } catch (ShipException shipException) {
            TurnReportItem tr = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), starsystem.getX(), starsystem.getY(), TurnReportItem.TurnReportSeverity.MEDIUM);
            tr.setSummary(Client.getLanguage().get("turn_report_ship_too_few_colonists_1"));
            tr.setDetailed(Language.pop(Client.getLanguage().get("turn_report_ship_too_few_colonists_1"), starsystem.getName()));
            starsystem.getUser().addTurnReport(tr);
        }
    }

    @Override
    public String toString() {
        return getPositionInBuildQueue() + ". " + template.getName() + " class ship" + " - "
                + getTurnsLeft() + " " + Client.getLanguage().get("turns_left");
    }

    public StarSystem getStarSystem() {
        return starsystem;
    }

    public int getIndustryInvested() {
        return industryInvested;
    }

    public void setIndustryInvested(int amount) {
        industryInvested = amount;
    }

    public ShipTemplate getTemplate() {
        return template;
    }

    public int getPositionInBuildQueue() {
        return positionInBuildQueue;
    }

    public void setPositionInBuildQueue(int positionInBuildQueue) {
        this.positionInBuildQueue = positionInBuildQueue;
    }

    public final void updateTurnsToCompletion() {
        turnsLeft = MiscTools.calculateTurnsUntilCompletion(template.getCost() - industryInvested, starsystem.getSystemIndustrySurplus());
    }

    @Override
    public void onCancel() {
    }
}