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
package com.frostvoid.trekwar.common;

import com.frostvoid.trekwar.common.structures.*;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A planet is inside a starsystem, and has a population and structures
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Planet implements Serializable {

    private StarSystem starsystem; // the starsystem this planet is in
    private int planetNumber; // this planets number in the system
    private int sortOrder; // to have hot planets close to sun, gas giants further out, etc..
    private PlanetClassification type;
    private int currentPopulation;
    private int maximumPopulation;
    private double fertility; // population growthrate
    private int deuteriumPerTurn;
    private int maxStructures;
    private HashMap<Integer, Structure> structures;
    private HashMap<Integer, Boolean> structuresEnabled;
    private HashMap<Integer, Point> surfaceMap;

    /**
     * Creates a new planet object
     *
     * @param type              the type of planet
     * @param system            the system the planet is a part of
     * @param planetNumber      the number of this planet in the system
     * @param maximumPopulation the maximum population on this planet
     * @param fertility         how fast the population grow
     * @param maxStructures     maximum number of structures
     */
    public Planet(PlanetClassification type, StarSystem system, int planetNumber,
                  int maximumPopulation, double fertility, int maxStructures) {

        this.type = type;
        this.starsystem = system;
        this.planetNumber = planetNumber;
        this.maximumPopulation = maximumPopulation;
        this.fertility = fertility;
        this.maxStructures = maxStructures;
        this.deuteriumPerTurn = 0;
        this.sortOrder = 0;
        structures = new HashMap<Integer, Structure>();
        structuresEnabled = new HashMap<Integer, Boolean>();
    }

    /**
     * Gets the planet fertility
     *
     * @return the fertility
     */
    public double getFertility() {
        return fertility;
    }

    /**
     * Gets the planet classification
     *
     * @return the classification
     */
    public PlanetClassification getType() {
        return type;
    }

    /**
     * Sets the maximum population
     *
     * @param maximumPopulation
     */
    public void setMaximumPopulation(int maximumPopulation) {
        this.maximumPopulation = maximumPopulation;
    }

    /**
     * Gets the planet maximum population
     *
     * @return max population
     */
    public int getMaximumPopulation() {
        return maximumPopulation;
    }

    /**
     * Sets the maximum number of structures
     *
     * @param maxStructures max structures
     */
    public void setMaximumStructures(int maxStructures) {
        this.maxStructures = maxStructures;
    }

    /**
     * Gets the maximum numbers of structures that can be built on this planet
     *
     * @return maximum number of structures
     */
    public int getMaximumStructures() {
        return maxStructures;
    }

    /**
     * Gets the structure map
     *
     * @return a map of slot id's to structures
     */
    public HashMap<Integer, Structure> getStructuresMap() {
        return structures;
    }

    /**
     * Checks if a structure is enabled
     *
     * @param index the structure index
     * @return true if the structure is enabled
     */
    public boolean isStructureEnabled(int index) {
        return structuresEnabled.get(index);
    }

    /**
     * Sets if a structure should be enabled or not
     *
     * @param index   the index of the structure
     * @param enabled true to enable, false to disable
     * @throws IndexOutOfBoundsException if invalid index is given
     */
    public void setStructureEnabled(int index, boolean enabled) throws IndexOutOfBoundsException {
        if (structures.keySet().contains(index)) {
            structuresEnabled.put(index, enabled);
        } else {
            throw new IndexOutOfBoundsException("Invalid structure index: " + index);
        }
    }

    /**
     * Deletes a structure
     *
     * @param index structure to delete
     */
    public void delStructure(int index) {
        structures.remove(index);
    }

    /**
     * Sets the population of this planet
     *
     * @param population the population
     */
    public void setPopulation(int population) {
        this.currentPopulation = population;
        if (currentPopulation > maximumPopulation) {
            currentPopulation = maximumPopulation;
        }
        if (currentPopulation < 0) {
            currentPopulation = 0;
        }
    }

    /**
     * Gets the population of this planet
     *
     * @return the population
     */
    public int getPopulation() {
        return currentPopulation;
    }

    /**
     * Sets the starsystem this planet resides in
     *
     * @param starsystem the starsystem
     */
    public void setStarSystem(StarSystem starsystem) {
        this.starsystem = starsystem;
    }

    /**
     * Gets the starsystem this planet is a part of
     *
     * @return the starsystem
     */
    public StarSystem getStarSystem() {
        return starsystem;
    }

    /**
     * Gets the amount of deuterium produced per turn by this planet
     *
     * @return deuterium per turn
     */
    public int getDeuteriumPerTurn() {
        return deuteriumPerTurn;
    }

    /**
     * Sets the deuterium per turn produced by this planet
     *
     * @param deuteriumPerTurn
     */
    public void setDeuteriumPerTurn(int deuteriumPerTurn) {
        this.deuteriumPerTurn = deuteriumPerTurn;
    }

    /**
     * Gets the amount of industry produced by this planet
     *
     * @return industry produced
     */
    public int getIndustryProduced() {
        int totalIndustry = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s.getIndustryOutput() > 0) {
                    totalIndustry += s.getIndustryOutput();
                }
            }
        }
        return totalIndustry;
    }

    /**
     * Gets the amount of industry consumed by this planet
     *
     * @return industry consumed
     */
    public int getIndustryConsumed() {
        int totalIndustry = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s.getIndustryOutput() < 0) {
                    totalIndustry += s.getIndustryOutput();
                }
            }
        }
        return Math.abs(totalIndustry);
    }

    /**
     * Gets the surplus industry for this planet
     *
     * @return surplus industry
     */
    public int getSurplusIndustry() {
        return getIndustryProduced() - getIndustryConsumed();
    }

    /**
     * Gets the amount of power produced by this planet
     *
     * @return power produced
     */
    public int getPowerProduced() {
        int totalPower = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s.getPowerOutput() > 0) {
                    totalPower += s.getPowerOutput();
                }
            }
        }
        return totalPower;
    }

    /**
     * Gets the amount of power consumed by this planet
     *
     * @return power consumed
     */
    public int getPowerConsumed() {
        int totalPower = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s.getPowerOutput() < 0) {
                    totalPower += s.getPowerOutput();
                }
            }
        }
        return Math.abs(totalPower);
    }

    /**
     * Gets the power surplus for this planet
     *
     * @return power surplus
     */
    public int getSurplusPower() {
        return getPowerProduced() - getPowerConsumed();
    }

    /**
     * Gets the research output of this planet
     *
     * @return research produced
     */
    public int getResearchProduced() {
        int totalResearch = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s.getResearchOutput() > 0) {
                    totalResearch += s.getResearchOutput();
                }
            }
        }
        return totalResearch;
    }

    /**
     * Gets the research consumed by this planet
     *
     * @return research consumed
     */
    public int getResearchConsumed() {
        int totalResearch = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s.getResearchOutput() < 0) {
                    totalResearch += s.getResearchOutput();
                }
            }
        }
        return Math.abs(totalResearch);
    }

    /**
     * Gets the research surplus of this planet
     *
     * @return research surplus
     */
    public int getSurplusResearch() {
        return getResearchProduced() - getResearchConsumed();
    }

    /**
     * Gets the ground combat defense rating for this planet
     *
     * @return ground combat
     */
    public int getDefenseRating() {
        int result = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s instanceof Bunker) {
                    result += ((Bunker) s).getTroopCapacity();
                }
                if (s instanceof MilitaryOutpost) {
                    result += ((MilitaryOutpost) s).getTroopCapacity();
                }
            }
        }
        result += ((result / 100) * starsystem.getUser().getFaction().getGroundCombatBonus());
        return result;
    }

    /**
     * Gets the food produced by this system
     *
     * @return food produced
     */
    public int getFoodProduced() {
        int totalFood = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s.getFoodOutput() > 0) {
                    totalFood += s.getFoodOutput();
                }
            }
        }
        return totalFood;
    }

    /**
     * Gets the food consumption of this planet
     *
     * @return food consumed
     */
    public int getFoodConsumed() {
        int totalFood = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s.getFoodOutput() < 0) {
                    totalFood += s.getFoodOutput();
                }
            }
        }
        return Math.abs(totalFood) + (currentPopulation / 120); // 1 farm feeds 2,4 billion people
    }

    /**
     * Gets the food surplus of this planet
     *
     * @return the food surplus
     */
    public int getFoodSurplus() {
        return getFoodProduced() - getFoodConsumed();
    }

    /**
     * Gets the maximum ore storage on this planet
     *
     * @return ore storage
     */
    public int getOreStorage() {
        int ore = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s instanceof OreRefinery) {
                    ore += ((OreRefinery) s).getOreCapacity();
                }
                if (s instanceof OreSilo) {
                    ore += ((OreSilo) s).getOreCapacity();
                }
            }
        }
        return ore;
    }

    /**
     * Gets the maximum deuterium storage on this planet
     *
     * @return deuterium storage
     */
    public int getDeuteriumStorage() {
        int deuterium = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s instanceof DeuteriumPlant) {
                    deuterium += ((DeuteriumPlant) s).getDeuteriumCapacity();
                }
                if (s instanceof DeuteriumSilo) {
                    deuterium += ((DeuteriumSilo) s).getDeuteriumCapacity();
                }
            }
        }
        return deuterium;
    }

    /**
     * Gets the troop production for this planet
     *
     * @return troop production
     */
    public int getTroopProduction() {
        int troopProd = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s instanceof MilitaryOutpost) {
                    troopProd += ((MilitaryOutpost) s).getTroopProduction();
                }
            }
        }

        return troopProd;
    }

    /**
     * Gets the troop capacity for this planet
     *
     * @return troop capacity
     */
    public int getTroopCapacity() {
        int troopMax = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s instanceof MilitaryOutpost) {
                    troopMax += ((MilitaryOutpost) s).getTroopCapacity();
                }
                if (s instanceof Bunker) {
                    troopMax += ((Bunker) s).getTroopCapacity();
                }
            }
        }

        return troopMax;
    }

    /**
     * Gets the number of active bunkers on this planet
     *
     * @return number of active bunkers
     */
    public int countBunkers() {
        int bunkers = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s instanceof Bunker) {
                    bunkers++;
                }
            }
        }

        return bunkers;
    }


    /**
     * Gets the number of bunkers on this planet
     *
     * @return number of bunkers
     */
    public ArrayList<Bunker> getBunkers() {
        ArrayList<Bunker> res = new ArrayList<Bunker>();

        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s instanceof Bunker) {
                    res.add((Bunker) s);
                }
            }
        }

        return res;
    }

    /**
     * Gets this planet's number/id/index in it's starsystem
     *
     * @return planets index
     */
    public int getPlanetNumber() {
        return planetNumber;
    }

    /**
     * Sets this planets number
     */
    public void setPlanetNumber(int planetNumber) {
        this.planetNumber = planetNumber;
    }

    /**
     * Gets the sort order for planets,
     * used when making a galaxy to make sure usable planets have lower
     * planet numbers than gas giants, etc..
     *
     * @return planet sort order
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets this planets sort order
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }


    /**
     * Gets the surface map (index -> x/y coordinates) used for drawing the UI
     *
     * @return the surface map
     */
    public HashMap<Integer, Point> getSurfaceMap() {
        return surfaceMap;
    }

    /**
     * Sets the surface map for this planet
     *
     * @param newMap the map
     */
    public void setSurfaceMap(HashMap<Integer, Point> newMap) {
        this.surfaceMap = newMap;
    }

    /**
     * Counts the number of active structures on this planet
     *
     * @return number of active structures
     */
    public int countActiveStructures() {
        int count = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if this planet has a particular structure
     *
     * @param s the structure to check for
     * @return true if the structure is found
     */
    public boolean hasStructure(Structure s) {
        if (structures.values().contains(s)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if this planet has a structure of the same class as provided structure
     *
     * @param s the structure to check for
     * @return true if a structure with the same class exists on the planet
     */
    public boolean hasStructureClass(Structure s) {
        for (Structure o : structures.values()) {
            if (o.getClass().equals(s.getClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the sensor strength this planet has (from structures)
     *
     * @return sensor strength
     */
    public int getScanStrength() {
        int strength = 0;
        for (int i : structures.keySet()) {
            if (isStructureEnabled(i)) {
                Structure s = structures.get(i);
                if (s.equals(StaticData.subspaceScanner1)) {
                    strength += 38;
                }
            }
        }
        return strength;
    }

    public boolean equals(Planet secondPlanet) {
        if (starsystem.equals(secondPlanet.getStarSystem()) &&
                planetNumber == secondPlanet.planetNumber &&
                type.equals(secondPlanet.type)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Planet) {
            return equals((Planet) o);
        }
        return false;
    }
}