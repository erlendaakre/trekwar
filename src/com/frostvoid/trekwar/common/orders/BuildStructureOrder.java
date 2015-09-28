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
import com.frostvoid.trekwar.common.Planet;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.TurnReportItem;
import com.frostvoid.trekwar.common.TurnReportItem.TurnReportSeverity;
import com.frostvoid.trekwar.common.structures.Structure;
import com.frostvoid.trekwar.common.utils.Language;
import com.frostvoid.trekwar.server.TrekwarServer;
import com.frostvoid.trekwar.server.utils.MiscTools;

import java.util.logging.Level;

/**
 * An order to build a structure in a starsystem on a specific slot/planet
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BuildStructureOrder extends Order {

    private static final long serialVersionUID = -9178228608448985207L;
    private StarSystem starsystem;
    private Planet planet;
    private int slot;
    private Structure structure;
    private int industryInvested;
    private int positionInBuildQueue; // used by client only

    public BuildStructureOrder(StarSystem starsystem, Planet planet, int slot, Structure structure) {
        this.starsystem = starsystem;
        this.planet = planet;
        this.slot = slot;
        this.structure = structure;
        industryInvested = 0;

        updateTurnsToCompletion();
    }

    @Override
    public void execute() {
        if (!orderCompleted) {
            int sysIndustryOutput = starsystem.getSystemIndustrySurplus();
            if (sysIndustryOutput > 0) {
                industryInvested += sysIndustryOutput;
            }

            if (industryInvested >= structure.getCost()) {
                orderCompleted = true;
            }

            updateTurnsToCompletion();
        }
    }

    @Override
    public void onComplete() {
        if (structure.getStructureMode().equals(Structure.mode.ONEPERSYSTEM)) {
            for (Planet p : starsystem.getPlanets()) {
                if (p.hasStructureClass(structure)) {
                    return;
                }
            }
        }

        if (structure.getStructureMode().equals(Structure.mode.ONEPERPLANET)) {
            if (planet.hasStructureClass(structure)) {
                return;
            }
        }

        if (structure.getStructureMode().equals(Structure.mode.ONEPERPLAYER)) {
            for (StarSystem s : starsystem.getUser().getStarSystems()) {
                for (Planet p : s.getPlanets()) {
                    if (p.hasStructureClass(structure)) {
                        return;
                    }
                }
            }
        }

        starsystem.addStructure(planet, slot, structure);
        planet.setStructureEnabled(slot, true);

        // notify turn report
        TurnReportItem tr = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), starsystem.getX(), starsystem.getY(), TurnReportSeverity.MEDIUM);
        tr.setSummary(Client.getLanguage().get("turn_report_structure_1"));
        tr.setDetailed(Language.pop(Client.getLanguage().get("turn_report_structure_2"), starsystem.getName(), structure.getName()));
        starsystem.getUser().addTurnReport(tr);

        TrekwarServer.LOG.log(Level.FINE, "User {0} completed a {1} structure in the {2} system", new Object[]{starsystem.getUser().getUsername(), structure.getName(), starsystem.getName()});
    }

    @Override
    public String toString() {
        return getPositionInBuildQueue() + ". " + structure.getName() + " - " +
                getTurnsLeft() + " " + Client.getLanguage().get("turns_left");
    }

    public Structure getStructure() {
        return structure;
    }

    public StarSystem getStarSystem() {
        return starsystem;
    }

    public Planet getPlanet() {
        return planet;
    }

    public int getSlot() {
        return slot;
    }

    public int getIndustryInvested() {
        return industryInvested;
    }

    public void setIndustryInvested(int amount) {
        industryInvested = amount;
    }


    public int getPositionInBuildQueue() {
        return positionInBuildQueue;
    }


    public void setPositionInBuildQueue(int positionInBuildQueue) {
        this.positionInBuildQueue = positionInBuildQueue;
    }

    public final void updateTurnsToCompletion() {
        turnsLeft = MiscTools.calculateTurnsUntilCompletion(structure.getCost() - industryInvested, starsystem.getSystemIndustrySurplus());
    }

    @Override
    public void onCancel() {
    }
}