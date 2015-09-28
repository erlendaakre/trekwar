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

import java.util.logging.Level;

import com.frostvoid.trekwar.common.Planet;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.orders.BuildStructureOrder;
import com.frostvoid.trekwar.server.utils.MiscTools;
import com.frostvoid.trekwar.common.utils.Language;
import com.frostvoid.trekwar.common.Galaxy;
import com.frostvoid.trekwar.common.PlanetClassification;
import com.frostvoid.trekwar.common.SpaceObjectClassification;
import com.frostvoid.trekwar.common.StarSystemClassification;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.TurnReportItem;
import com.frostvoid.trekwar.common.TurnReportItem.TurnReportSeverity;
import com.frostvoid.trekwar.common.orders.BuildShipOrder;
import com.frostvoid.trekwar.common.orders.Order;
import com.frostvoid.trekwar.server.TrekwarServer;

/**
 * Handles the tile specific upkeep actions during each new turn
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SystemHandler implements Runnable {

    private int rowStart;
    private int rowEnd;
    private Galaxy galaxy;

    public SystemHandler(Galaxy galaxy, int rowStart, int rowEnd) {
        this.galaxy = galaxy;
        this.rowStart = rowStart;
        this.rowEnd = rowEnd;
    }

    @Override
    public void run() {
        TrekwarServer.getLog().fine("SystemHandler running");
        for (int i = rowStart; i < rowEnd; i++) {
            for (int j = 0; j < galaxy.getMap()[i].length; j++) {
                StarSystem s = galaxy.getMap()[i][j];

                // STARSYSTEM FOUND
                if (s.getClassification() == SpaceObjectClassification.starsystem
                        && s.getStarSystemClassification() == StarSystemClassification.starSystem) {

                    // SYSTEM OWNED BY PLAYER
                    if (!s.getUser().equals(StaticData.nobodyUser)) {

                        TrekwarServer.getLog().fine("Found system at " + s.getX() + "," + s.getY() + " named " + s.getName());

                        // handle resource shortage/surplus
                        turnHandleStarsystemFood(galaxy, s);
                        turnHandleStarsystemPower(s);
                        turnHandleStarsystemIndustry(galaxy, s);
                        turnHandleStarsystemResearch(galaxy, s);


                        // Add deuterium from gas giants
                        for (Planet p : s.getPlanets()) {
                            if (p.getType() == PlanetClassification.gasGiant) {
                                int amount = p.getDeuteriumPerTurn();
                                s.setDeuterium(s.getDeuterium() + amount);
                            }
                        }
                    }


                    // BUILD QUEUES
                    Order order = null;
                    if (s.countItemsInBuildQueue() > 0) {
                        order = s.getNextFromBuildQueue();
                    }
                    if (order != null) {
                        TrekwarServer.getLog().log(Level.FINE, "executing build order for system: {0}", s.getName());

                        if(order instanceof BuildShipOrder &&  s.getUser().hasUpkeepPenalty()
                                && TrekwarServer.PRNG.nextInt(100) < StaticData.SHIP_UPKEEP_PENALTY_CONSTRUCTION_DELAY_CHANCE) {
                            TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), s.getX(), s.getY(), TurnReportItem.TurnReportSeverity.MEDIUM);
                            tr.setSummary(TrekwarServer.getLanguage().get("turn_report_ship_construction_fail_upkeep_1"));
                            tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_ship_construction_fail_upkeep_2"), s.getName()));
                            s.getUser().addTurnReport(tr);
                        }
                        else {
                            order.execute();
                        }
                        if (order.isCompleted()) {
                            TrekwarServer.getLog().finer("Build order complete, calling order onComplete()");
                            order.onComplete();
                            s.removeNextFromBuildQueue();
                        }
                    }
                    
                    // update build queue time to completion
                    if(s.countItemsInBuildQueue() > 0) {
                        for(Order o : s.getBuildQueue()) {
                            if(o instanceof BuildStructureOrder) {
                                BuildStructureOrder bso = (BuildStructureOrder)o;
                                bso.setTurnsLeft(MiscTools.calculateTurnsUntilCompletion(bso.getStructure().getCost() - bso.getIndustryInvested(), s.getSystemIndustrySurplus()));
                            }
                            else if (o instanceof BuildShipOrder) {
                                BuildShipOrder bso = (BuildShipOrder)o;
                                bso.setTurnsLeft(MiscTools.calculateTurnsUntilCompletion(bso.getTemplate().getCost()-bso.getIndustryInvested(), s.getSystemIndustrySurplus()));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle starsystem food shortage or surplus, will increase/decrease
     * population, troop count and morale
     * 
     * @param galaxy The galaxy object
     * @param s the starsystem
     */
    private static void turnHandleStarsystemFood(Galaxy galaxy, StarSystem s) {
        TrekwarServer.getLog().fine("System food surplus: " + s.getSystemFoodSurplus());
        if (s.getSystemFoodSurplus() > 0) {
            // population growth
            for (Planet p : s.getPlanets()) {
                if (p.getPopulation() == p.getMaximumPopulation()) {
                    continue;
                }
                int popAdd = (int) (((p.getPopulation() / 100) * p.getFertility()) * ((s.getMorale()+10)/100D) );
                if (popAdd < 5 && s.getMorale() > 75) {
                    popAdd = 5;  // very small population, help planet out in starting phase
                }
                TrekwarServer.getLog().fine("Added " + popAdd + " population");
                p.setPopulation(p.getPopulation() + popAdd);

                if (p.getPopulation() > p.getMaximumPopulation()) {
                    p.setPopulation(p.getMaximumPopulation());
                }

                //added to many people.. there is now starvation in system, dont add for other planets
                if (s.getSystemFoodSurplus() <= 0) {
                    break;
                }
            }

            // add troops unless there is hunger
            if (s.getSystemFoodSurplus() >= 0) {
                TrekwarServer.getLog().fine("Added " + s.getTroopProduction() + " troops");
                s.addTroops(s.getTroopProduction());
            }

            // increase morale
            updateSystemMorale(s, 1);

        } else if (s.getSystemFoodSurplus() < 0) {
            TrekwarServer.getLog().fine("Starvation in system");
            // starvation

            // remove 2% of population when hunger in system
            for (Planet p : s.getPlanets()) {
                p.setPopulation(p.getPopulation() - (p.getPopulation() / 50));
            }

            // remove 2 + 2% of troops
            int removeTroops = 2;
            removeTroops += s.getTroopCount() / 50;
            TrekwarServer.getLog().fine("Removed " + removeTroops + " troops");
            s.setTroopCount(s.getTroopCount() - removeTroops);
            if (s.getTroopCount() < 0) {
                s.setTroopCount(0);
            }

            // decrease starsystem morale
            updateSystemMorale(s, -5);


            TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), s.getX(), s.getY(), TurnReportSeverity.CRITICAL);
            tr.setSummary(Language.pop(TrekwarServer.getLanguage().get("turn_report_starvation_1"), s.getName()));
            tr.setDetailed(TrekwarServer.getLanguage().get("turn_report_starvation_2"));
            s.getUser().addTurnReport(tr);
        }
        else {
            // +1 morale if system has food/population balance
            updateSystemMorale(s, 1);
        }
    }

    /**
     * Handles effects caused by starsystem power surplus/deficiency
     * 
     * @param s the starsystem
     */
    private static void turnHandleStarsystemPower(StarSystem s) {
        if (s.getSystemPowerSurplus() > 0) {
            updateSystemMorale(s, 1);
        } else if (s.getSystemPowerSurplus() < 0) {
            updateSystemMorale(s, -1);
        }
    }

    /**
     * Update starsystem morale, positive to increase, negative to decrease.
     *
     * @param s starsystem to change morale for
     * @param moraleChange morale change
     */
    private static void updateSystemMorale(StarSystem s, int moraleChange) {
        TrekwarServer.getLog().fine("Updating morale for starsystem " + s.getName() + ", old morale = " + s.getMorale() + ", change = " + moraleChange);
        s.setMorale(s.getMorale() + moraleChange);
        if (s.getMorale() < 0) {
            s.setMorale(0);
        }
        if (s.getMorale() > 100) {
            s.setMorale(100);
        }
    }

    /**
     * Handle a starsystems industry output (surplus/shortage)
     * 
     * @param galaxy the galaxy object
     * @param s the starsystem
     */
    private static void turnHandleStarsystemIndustry(Galaxy galaxy, StarSystem s) {
        TrekwarServer.getLog().fine("System industry surplus: " + s.getSystemIndustrySurplus());
        if (s.getSystemIndustrySurplus() < 0) {
            if (s.getOre() > 0) {
                int rand = TrekwarServer.PRNG.nextInt(100);
                if (rand < 5) {
                    int lossPercentage = TrekwarServer.PRNG.nextInt(25) + 10;
                    int lossOre = (s.getOre() / 100) * lossPercentage;
                    s.removeOre(lossOre);

                    TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), s.getX(), s.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                    tr.setSummary(Language.pop(TrekwarServer.getLanguage().get("turn_report_oreloss_1"), s.getName()));
                    tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_oreloss_2"), lossOre));
                    s.getUser().addTurnReport(tr);
                }
            }
        }
    }

    /**
     * Handles starsystem research output (surplus/shortage)
     * 
     * @param galaxy the galaxy object
     * @param s the starsystem
     */
    private static void turnHandleStarsystemResearch(Galaxy galaxy, StarSystem s) {
        TrekwarServer.getLog().fine("System research surplus: " + s.getSystemResearchSurplus());
        if (s.getSystemResearchSurplus() < 0) {
            if (s.getUser().getCurrentResearch() != null) {
                int rand = TrekwarServer.PRNG.nextInt(100);
                if (rand < StaticData.RESEARCH_LOSS_CHANCE) {
                    TrekwarServer.getLog().finer("Research points lost due to research deficit in system");
                    int lossPercentage = TrekwarServer.PRNG.nextInt(60) + 20;
                    int lossResearch = (s.getUser().getResearchPoints() / 100) * lossPercentage;
                    s.getUser().setResearchPoints(s.getUser().getResearchPoints() - lossResearch);

                    TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), s.getX(), s.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                    tr.setSummary(Language.pop(TrekwarServer.getLanguage().get("turn_report_researchloss_1"), s.getName()));
                    tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_researchloss_2"), lossResearch, s.getUser().getCurrentResearch().getName()));
                    s.getUser().addTurnReport(tr);
                }
            }
        }
    }
}