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

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.frostvoid.trekwar.common.Ship;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.Galaxy;
import com.frostvoid.trekwar.common.StarSystemClassification;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.TurnReportItem;
import com.frostvoid.trekwar.common.User;
import com.frostvoid.trekwar.common.orders.Order;
import com.frostvoid.trekwar.common.utils.Language;
import com.frostvoid.trekwar.server.TrekwarServer;

/**
 * Handles the turn execution for all user objects and their fleets / ships
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class UserAndFleetHandler implements Callable<UserAndFleetHandlerResult> {

    private Galaxy galaxy;
    
    private ArrayList<Fleet> fleetsFound;
    private ArrayList<Order> ordersFound;

    public UserAndFleetHandler(Galaxy galaxy) {
        this.galaxy = galaxy;
        
        fleetsFound = new ArrayList<Fleet>();
        ordersFound = new ArrayList<Order>();
    }

    @Override
    public UserAndFleetHandlerResult call() {
        for (User u : galaxy.getUsers()) {
            TrekwarServer.LOG.log(Level.FINE, "UserAndFleetHandler running for: {0}", u.getUsername());
            
            boolean upkeepPenalty = u.hasUpkeepPenalty();
            if(upkeepPenalty) {
                TrekwarServer.LOG.log(Level.FINE, "User {0} has upkeep penalty. upkeep surplus: {1}", new Object[] {u.getUsername(), u.getShipUpkeepSurplus()});    
            }

            // DO RESEARCH
            if (u.getCurrentResearch() != null) {

                double researchAdd = u.getResearchOutput();
                researchAdd += ((researchAdd / 100D) * u.getFaction().getResearchBonus());
                u.setResearchPoints((int) (u.getResearchPoints() + researchAdd));

                if (u.getCurrentResearch().getResearchCost() <= u.getResearchPoints()) {
                    Technology research = u.getCurrentResearch();
                    TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), -1, -1, TurnReportItem.TurnReportSeverity.MEDIUM);
                    tr.setSummary(TrekwarServer.getLanguage().get("turn_report_research_1"));
                    tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_research_2"), research.getType(), research.getName()));
                    u.addTurnReport(tr);
                    TrekwarServer.getLog().log(Level.FINE, "User {0} has discovered: {1}", new Object[]{u.getUsername(), u.getCurrentResearch().getName()});

                    u.addTech(research);
                    u.setResearchPoints(u.getResearchPoints() - research.getResearchCost());
                    u.setCurrentResearch(null);
                }
            }

            // FOR USERS FLEETS
            for (int j = 0; j < u.getFleets().size(); j++) {
                
                Fleet fleet = u.getFleets().get(j);
                TrekwarServer.LOG.log(Level.FINER, "Handling fleet {0} owned by {1}", new Object[]{fleet.getName(), u.getUsername()});

                // clean up dirty ships (has 0 or lower hitpoints)
                for (int i = 0; i < fleet.getShips().size(); i++) {
                    Ship s = fleet.getShips().get(i);
                    if (s.getCurrentHullStrength() <= 0) {
                        s.destroy();
                        if (fleet.getShips().contains(s)) {
                            TrekwarServer.LOG.log(Level.WARNING, "TurnExecutor deleted dirty ship from fleet {0}, owned by {1}", new Object[]{fleet.getName(), u.getUsername()});
                            fleet.removeShip(s);
                            i--;
                        }
                    }
                }
                // clean up dirty fleets (no ships)
                if (fleet.getShips().isEmpty()) {
                    TrekwarServer.LOG.log(Level.WARNING, "TurnExecutor deleted dirty (empty) fleet named {0}", fleet.getName());
                    fleet.getUser().removeFleet(fleet);
                    galaxy.getSystem(fleet).removeFleet(fleet);
                    j--;
                    continue;
                }


                fleetsFound.add(fleet);

                // FLEET IN OWN STARSYSTEM
                if (galaxy.getMap()[fleet.getX()][fleet.getY()].getUser().equals(fleet.getUser())) {
                    StarSystem system = galaxy.getMap()[fleet.getX()][fleet.getY()];

                    // refuel deuterium if fleet needs it, and system has power
                    if (fleet.needsRefuel() &&  system.getSystemPowerSurplus() >= 0) {
                        TrekwarServer.LOG.log(Level.FINER, "Refueling fleet {0} in friendly system {1}", new Object[]{fleet.getName(), system.getName()});
                        int amount = fleet.getMaxDeuterium() - fleet.getDeuteriumLeft();
                        if (amount > galaxy.getMap()[fleet.getX()][fleet.getY()].getDeuterium()) {
                            amount = galaxy.getMap()[fleet.getX()][fleet.getY()].getDeuterium();
                        }
                        TrekwarServer.LOG.log(Level.FINEST, "Refueling with {0} deuterium. Fleet has {1} deuterium", new Object[]{amount,fleet.getDeuteriumLeft()});
                        if(upkeepPenalty && TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_REFUEL_FAIL_CHANCE) {
                            TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), fleet.getX(), fleet.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                            tr.setSummary(TrekwarServer.getLanguage().get("turn_report_refuel_fail_upkeep_1"));
                            tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_refuel_fail_upkeep_2"), fleet.getName(), system.getName()));
                            u.addTurnReport(tr);
                            TrekwarServer.LOG.log(Level.FINE, "Refueling fleet {0} failed because of upkeep penalty", fleet.getName());
                        }
                        else {
                            if (fleet.addDeuteriumFair(amount)) {
                                TrekwarServer.LOG.log(Level.FINEST, "Fleet {0} refueled, now has {1}/{2} deuterium", new Object[]{fleet.getName(), fleet.getDeuteriumLeft(), fleet.getMaxDeuterium()});
                                system.setDeuterium(system.getDeuterium() - amount);
                            }
                        }
                    }

                    // add crew if system not in hunger
                    if(fleet.needsMoreCrew() && system.getSystemFoodSurplus() >= 0) {
                        TrekwarServer.LOG.log(Level.FINER, "Adding crew to fleet {1} in friendly system {2}", new Object[]{fleet.getName(), system.getName()});
                        if(upkeepPenalty && TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_REPAIR_FAIL_CHANCE) {
                            TrekwarServer.LOG.log(Level.FINEST, "Adding crew to fleet {0} failed because of upkeep penalty", fleet.getName());
                            TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), fleet.getX(), fleet.getY(), TurnReportItem.TurnReportSeverity.MEDIUM);
                            tr.setSummary(TrekwarServer.getLanguage().get("turn_report_recrew_fail_upkeep_1"));
                            tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_recrew_fail_upkeep_2"), fleet.getName(), system.getName()));
                            u.addTurnReport(tr);
                        }
                        else {
                            for (Ship s : fleet.getShips()) {
                                if (s.getCrew() < s.getHullClass().getMaxCrew()) {
                                    s.setCrew(s.getHullClass().getMaxCrew());
                                }
                            }
                            TrekwarServer.LOG.log(Level.FINEST, "Crew added to all ships in fleet {0}", fleet.getName());
                        }
                    }

                    // repair damaged ships in own system
                    if(fleet.needsRepair()) {
                        TrekwarServer.LOG.log(Level.FINER, "Repairing fleet {0} in friendly system {1}", new Object[]{fleet.getName(), system.getName()});
                        if(upkeepPenalty && TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_REPAIR_FAIL_CHANCE) {
                            TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), fleet.getX(), fleet.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                            tr.setSummary(TrekwarServer.getLanguage().get("turn_report_repair_fail_upkeep_1"));
                            tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_repair_fail_upkeep_2"), fleet.getName(), system.getName()));
                            u.addTurnReport(tr);
                            TrekwarServer.LOG.log(Level.FINEST, "Repairing fleet {0} failed because of upkeep penalty", fleet.getName());
                        }
                        else {
                            fleet.repairShipsHullArmorShields(true, system.hasShipyard(),
                                (system.getSystemPowerSurplus() >= 0 && system.getSystemIndustrySurplus() >= 0 && system.getSystemResearchSurplus() >= 0));
                            TrekwarServer.LOG.log(Level.FINEST, "Fleet {0} repaired. armor: {1},  hull: {2}", new Object[]{fleet.getName(), fleet.getArmor(), fleet.getHP()});
                        }
                    }
                } else {
                    // repair in space (not own system)
                    if(fleet.needsRepair()) {
                        TrekwarServer.LOG.log(Level.FINER, "Repairing fleet {0} in deep space", fleet.getName());
                        if(upkeepPenalty && TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_REPAIR_FAIL_CHANCE) {
                            TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), fleet.getX(), fleet.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                            tr.setSummary(TrekwarServer.getLanguage().get("turn_report_repair_fail_upkeep_1"));
                            tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_repair_fail_upkeep_3"), fleet.getName()));
                            u.addTurnReport(tr);
                            TrekwarServer.LOG.log(Level.FINEST, "Repairing fleet {0} in failed because of upkeep penalty", fleet.getName());
                        }
                        else {
                            fleet.repairShipsHullArmorShields(false, false, true);
                            TrekwarServer.LOG.log(Level.FINEST, "Fleet {0} repaired. armor: {1},  hull: {2}", new Object[]{fleet.getName(), fleet.getArmor(), fleet.getHP()});
                        }
                    }
                }
                
                if(upkeepPenalty) {
                    for(Ship ship : fleet.getShips()) {
                        // MORALE PENALTY FOR UPKEEP SHORTAGE
                        if(TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_MORALE_LOSS_CHANCE) {
                            ship.setMorale(ship.getMorale() - 3);
                            TrekwarServer.LOG.log(Level.FINE, "A {0} class ship in fleet {1} suffered a morale loss due to upkeep penalty. morale: {2}", new Object[]{ship.getName(), fleet.getName(), ship.getMorale()});
                        }
                        
                        // SHIP DAMAGE PENALTY FOR UPKEEP SHORTAGE
                        if(TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_LIGHT_DAMAGE_CHANCE) {
                            int armorDmg = 0;
                            if(ship.getCurrentArmorStrength() > 0) {
                                armorDmg = TrekwarServer.PRNG.nextInt(ship.getCurrentArmorStrength()/8);
                            }
                            int hullDmg = TrekwarServer.PRNG.nextInt(ship.getCurrentHullStrength()/10);
                            if(armorDmg > 0 || hullDmg > 0) {
                                ship.setCurrentArmorStrength(ship.getCurrentArmorStrength() - armorDmg);
                                ship.setCurrentHullStrength(ship.getCurrentHullStrength() - hullDmg);
                                ship.setMorale(ship.getMorale() - 6);
                                TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), fleet.getX(), fleet.getY(), TurnReportItem.TurnReportSeverity.CRITICAL);
                                tr.setSummary(TrekwarServer.getLanguage().get("turn_report_ship_damage_upkeep_1"));
                                tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_ship_damage_upkeep_2"), fleet.getName(), ship.getHullClass().getName(), armorDmg, hullDmg));
                                u.addTurnReport(tr);
                                TrekwarServer.LOG.log(Level.FINE, "A {0} class ship in fleet {1} suffered light damage due to upkeep penalty. armor/hull dmg: {2}/{3}", new Object[]{ship.getName(), fleet.getName(), armorDmg, hullDmg});
                                TrekwarServer.LOG.log(Level.FINER, "Armor: {0}/{1}, Hull: {2}/{3}", new Object[]{ship.getMaxArmor(), ship.getCurrentArmorStrength(), ship.getMaxHitpoints(), ship.getCurrentHullStrength()});
                            }
                        }
                        if(TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_MODERATE_DAMAGE_CHANCE) {
                            int armorDmg = 0;
                            if(ship.getCurrentArmorStrength() > 0) {
                                armorDmg = TrekwarServer.PRNG.nextInt(ship.getCurrentArmorStrength()/5);
                            }
                            int hullDmg = TrekwarServer.PRNG.nextInt(ship.getCurrentHullStrength()/5);
                            if(armorDmg > 0 || hullDmg > 0) {
                                ship.setCurrentArmorStrength(ship.getCurrentArmorStrength() - armorDmg);
                                ship.setCurrentHullStrength(ship.getCurrentHullStrength() - hullDmg);
                                ship.setMorale(ship.getMorale() - 10);
                                TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), fleet.getX(), fleet.getY(), TurnReportItem.TurnReportSeverity.CRITICAL);
                                tr.setSummary(TrekwarServer.getLanguage().get("turn_report_ship_damage_upkeep_1"));
                                tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_ship_damage_upkeep_2"), fleet.getName(), ship.getHullClass().getName(), armorDmg, hullDmg));
                                u.addTurnReport(tr);
                                TrekwarServer.LOG.log(Level.FINE, "A {0} class ship in fleet {1} suffered moderate damage due to upkeep penalty. armor/hull dmg: {2}/{3}", new Object[]{ship.getName(), fleet.getName(), armorDmg, hullDmg});
                                TrekwarServer.LOG.log(Level.FINER, "Armor: {0}/{1}, Hull: {2}/{3}", new Object[]{ship.getMaxArmor(), ship.getCurrentArmorStrength(), ship.getMaxHitpoints(), ship.getCurrentHullStrength()});
                            }
                        }
                        if(TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_HEAVY_DAMAGE_CHANCE) {
                            int armorDmg = 0;
                            if(ship.getCurrentArmorStrength() > 0) {
                                armorDmg = TrekwarServer.PRNG.nextInt(ship.getCurrentArmorStrength());
                            }
                            int hullDmg = TrekwarServer.PRNG.nextInt(ship.getCurrentHullStrength()/2);
                            if(armorDmg > 0 || hullDmg > 0) {
                                ship.setCurrentArmorStrength(ship.getCurrentArmorStrength() - armorDmg);
                                ship.setCurrentHullStrength(ship.getCurrentHullStrength() - hullDmg);
                                ship.setMorale(ship.getMorale() - 30);
                                TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), fleet.getX(), fleet.getY(), TurnReportItem.TurnReportSeverity.CRITICAL);
                                tr.setSummary(TrekwarServer.getLanguage().get("turn_report_ship_damage_upkeep_1"));
                                tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_ship_damage_upkeep_2"), fleet.getName(), ship.getHullClass().getName(), armorDmg, hullDmg));
                                u.addTurnReport(tr);
                                TrekwarServer.LOG.log(Level.FINE, "A {0} class ship in fleet {1} suffered heavy damage due to upkeep penalty. armor/hull dmg: {2}/{3}", new Object[]{ship.getName(), fleet.getName(), armorDmg, hullDmg});
                                TrekwarServer.LOG.log(Level.FINER, "Armor: {0}/{1}, Hull: {2}/{3}", new Object[]{ship.getMaxArmor(), ship.getCurrentArmorStrength(), ship.getMaxHitpoints(), ship.getCurrentHullStrength()});
                            }
                        }
                    }
                }

                // REFUEL IF IN NEBUAL AND HAS HYDROGEN COLLECTOR
                if (galaxy.getMap()[fleet.getX()][fleet.getY()].getStarSystemClassification() == StarSystemClassification.nebula && fleet.canUseBussardCollector() && fleet.getDeuteriumLeft() < fleet.getMaxDeuterium()) {
                    int amount = fleet.getBussardCollectorCapacity();
                    TrekwarServer.LOG.log(Level.FINER, "Refueling fleet {0} in nebula with {1} deuterium", new Object[]{fleet.getName(), amount});
                    if (amount > galaxy.getMap()[fleet.getX()][fleet.getY()].getResourcesLeft()) {
                        amount = galaxy.getMap()[fleet.getX()][fleet.getY()].getResourcesLeft();
                    }

                    fleet.addDeuteriumFair(amount);
                    galaxy.getMap()[fleet.getX()][fleet.getY()].setResourcesLeft(galaxy.getMap()[fleet.getX()][fleet.getY()].getResourcesLeft() - amount);
                }

                // ADD ORDERS TO QUEUE
                if (fleet.getOrder() != null) {
                    ordersFound.add(fleet.getOrder());
                }
            }
        }
        return new UserAndFleetHandlerResult(fleetsFound, ordersFound);
    }
}

class UserAndFleetHandlerResult {
    private ArrayList<Fleet> fleetsFound;
    private ArrayList<Order> ordersFound;
    
    public UserAndFleetHandlerResult(ArrayList<Fleet> fleetsFound, ArrayList<Order> ordersFound) {
        this.fleetsFound = fleetsFound;
        this.ordersFound = ordersFound;
    }
    
    public ArrayList<Fleet> getFleetsFound() {
        return fleetsFound;
    }
    
    public ArrayList<Order> getOrdersFound() {
        return ordersFound;
    }
}