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
package com.frostvoid.trekwar.common.shipComponents;

import java.io.Serializable;
import java.util.Arrays;

import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.User;

import java.util.ArrayList;

/**
 * Base class for all starship components, a component can be put in a slot
 * of a template and will be present in starships built with that template
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ShipComponent implements Serializable {

    private String name;
    private String description;
    private String iconFileName;
    protected String icon16x16Filename;
    private ArrayList<Faction> factionsRequired; // this component can be buildt by anyone in this list
    ArrayList<Technology> techsRequired; // techs required to build structure
    private int cost;
    private int energy; // negative for usage.. positive for power generation
    private boolean civilian;

    public ShipComponent(String name, String description, String iconFileName, int cost, int energy, boolean civillian) {
        this.name = name;
        this.description = description;
        this.iconFileName = iconFileName;
        this.factionsRequired = new ArrayList<Faction>();
        this.techsRequired = new ArrayList<Technology>();
        this.cost = cost;
        this.energy = energy;
        this.civilian = civillian;
    }

    public void addFaction(Faction... factions) {
        factionsRequired.addAll(Arrays.asList(factions));
    }

    public void addTech(Technology... techs) {
        techsRequired.addAll(Arrays.asList(techs));
    }

    /**
     * Checks if a given faction can use this component
     * @param f the faction to check
     * @return true if faction can use this component
     */
    public boolean canUse(Faction f) {
        boolean res = factionsRequired.contains(f);
        return res;
    }

    public boolean canUse(User u) {
        for (Technology shipTech : techsRequired) {
            if (!u.getTechs().contains(shipTech)) {
                return false;
            }
        }
        return true;
    }

    public boolean isCivilian() {
        return civilian;
    }

    public int getCost() {
        return cost;
    }

    public String getDescription() {
        return description;
    }

    public int getEnergy() {
        return energy;
    }

    public ArrayList<Faction> getFactionsRequired() {
        return factionsRequired;
    }

    public String getIconFileName() {
        return iconFileName;
    }

    public String getSmallIconFileName() {
        return icon16x16Filename;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Technology> getTechsRequired() {
        return techsRequired;
    }
}