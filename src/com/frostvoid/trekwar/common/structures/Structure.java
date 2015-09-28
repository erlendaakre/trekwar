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
package com.frostvoid.trekwar.common.structures;

import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents a structure which can be built on a planet
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public abstract class Structure implements Serializable {

    private String name;
    private String description;
    private String imageFileName;
    private ArrayList<Faction> factionsRequired;
    ArrayList<Technology> techsRequired;
    private boolean special; // can be disabled/enabled in starsystem view
    private String specialText; // short text like "allows starship construction"
    private mode structureMode;
    /**
     * The Price (industry) it takes to build this structure
     */
    private int cost;
    /**
     * The power output for this structure
     * POSITIVE for power plants/production structures
     * NEGATIVE for structures that uses power
     */
    private int powerOutput;
    /**
     * The food production output for this structure
     * POSITIVE for farms and food producing structures
     * NEGATIVE for structures that decreases food production
     */
    private int foodOutput;
    /**
     * The industry output for this structure
     * POSITIVE for industrial structures
     * NEGATIVE for structures that needs industrial maitenance
     */
    private int industryOutput;
    /**
     * The research bonus of this structure
     * POSITIVE for research structures
     * NEGATIVE for structures that needs scientific resources
     */
    private int researchOutput;

    /**
     * Constructor to make a new structure (only called once, cause all structures are static)
     * and planets only have references to them.
     *
     * @param name          the name of the structure
     * @param description   a long description of the structure
     * @param cost          the industry cost required to build this structure
     * @param power         the power input/output of this structure
     * @param research      the research input/output of this structure
     * @param food          the food input/output of this structure
     * @param industry      the industry input/output of this structure
     * @param imageFileName the filename of the image
     * @param structureMode the mode of this structure
     */
    public Structure(String name, String description,
                     int cost, int power, int research, int food, int industry, String imageFileName, mode structureMode) {

        this.name = name;
        this.description = description;
        this.cost = cost;
        this.powerOutput = power;
        this.researchOutput = research;
        this.foodOutput = food;
        this.industryOutput = industry;
        this.imageFileName = imageFileName;
        this.factionsRequired = new ArrayList<Faction>();
        this.techsRequired = new ArrayList<Technology>();
        this.special = false;
        this.specialText = "";
        this.structureMode = structureMode;
    }

    /**
     * Sets a short special text for this structure
     *
     * @param specialText the special text
     */
    public void setSpecial(String specialText) {
        this.special = true;
        this.specialText = specialText;
    }

    /**
     * Gets the name of the structure
     *
     * @return structure name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the industry cost required to build this structure
     *
     * @return build cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * Gets the power output of this structure
     * negative if the structure uses power
     * positive if the structure generates power
     *
     * @return power output
     */
    public int getPowerOutput() {
        return powerOutput;
    }

    /**
     * Gets the research output of this structure
     * negative if the structure consumes research
     * positive if the structure generates research
     *
     * @return research output
     */
    public int getResearchOutput() {
        return researchOutput;
    }

    /**
     * Gets the food output of this structure
     * negative if the structure produces food
     * positive if the structure consumes food
     *
     * @return food output
     */
    public int getFoodOutput() {
        return foodOutput;
    }

    /**
     * Gets the industry output of this structure
     * negative if the structure consumes industry
     * positive if the structure generates industry
     *
     * @return industry output
     */
    public int getIndustryOutput() {
        return industryOutput;
    }

    /**
     * Gets the image filename for this structure
     *
     * @return image filename
     */
    public String getImageFilename() {
        return imageFileName;
    }

    /**
     * Returns the name of the structure
     *
     * @return structure name
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * Gets the long description of this structure
     *
     * @return structure description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Adds factions that can build this structure
     *
     * @param factions factions that can build this structure
     */
    public void addFaction(Faction... factions) {
        factionsRequired.addAll(Arrays.asList(factions));
    }

    /**
     * Sets which technologies are required to build this structure
     *
     * @param techs technologies required to build
     */
    public void addTech(Technology... techs) {
        techsRequired.addAll(Arrays.asList(techs));
    }

    /**
     * Checks if a given faction can build this structure
     *
     * @param faction the faction
     * @return true if this faction can use the building
     */
    public boolean canUse(Faction faction) {
        return factionsRequired.contains(faction);
    }

    /**
     * Checks if a given user can build this structure, checks both faction and technology restrictions
     *
     * @param user the user to check
     * @return true if user can build structure
     */
    public boolean canUse(User user) {
        if (!canUse(user.getFaction())) {
            return false;
        }

        for (Technology shipTech : techsRequired) {
            if (!user.getTechs().contains(shipTech)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the technologies required to build this structure
     *
     * @return all the technologies required
     */
    public ArrayList<Technology> getTechsRequired() {
        return techsRequired;
    }

    /**
     * Gets the factions that can build this structure
     *
     * @return all the factions that can build the structure
     */
    public ArrayList<Faction> getFactionsRequired() {
        return factionsRequired;
    }

    /**
     * Gets the short special text for this structure, or empty string for non-special buildings
     *
     * @return the special text
     */
    public String getSpecialText() {
        return specialText;
    }

    /**
     * checks if this structure is special (one in system only, needs easy enabling/disabling)
     *
     * @return true if structure is special
     */
    public boolean isSpecial() {
        return special;
    }

    /**
     * The types a building can have, indicating how many times it can be built
     */
    public enum mode {

        NORMAL, ONEPERPLANET, ONEPERSYSTEM, ONEPERPLAYER
    }

    /**
     * Gets the build mode of this structure
     *
     * @return the build mode
     * @see mode
     */
    public mode getStructureMode() {
        return structureMode;
    }

    /**
     * Compares two structures (by name)
     *
     * @param s2 the second structure
     * @return true if they have the same name
     */
    public boolean equals(Structure s2) {
        return name.equals(s2.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Structure)
            return equals((Structure) o);
        return false;
    }
}
