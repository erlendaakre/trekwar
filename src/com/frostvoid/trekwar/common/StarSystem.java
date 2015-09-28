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

import com.frostvoid.trekwar.common.exceptions.InvalidOrderException;
import com.frostvoid.trekwar.common.orders.BuildShipOrder;
import com.frostvoid.trekwar.common.orders.BuildStructureOrder;
import com.frostvoid.trekwar.common.orders.Order;
import com.frostvoid.trekwar.common.structures.Structure;

import java.util.ArrayList;

/**
 * This class represent a starsystem, and has a list of planets and fleets
 * Nebulae and Asteroids are also of this class
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class StarSystem extends SpaceObject {
    private String imageFile;
    private ArrayList<Planet> planets;
    private ArrayList<Fleet> fleets;
    private ArrayList<Order> buildQueue;
    private StarSystemClassification starSystemType;

    private int morale;
    private int resourcesLeft; // used for asteroid fields and nebulas
    private int ore; // used for colonized systems that store ore
    private int deuterium; // used for colonized systems that store deuterium
    private int troops; // used for colonized systems, this is the owners troops

    /**
     * Constructor to make a new StarSystem
     *
     * @param user           the owner of this system (Nobody user)
     * @param x              the x location
     * @param y              the y location
     * @param name           the name of the TYPE of system
     * @param starSystemType the type of system
     */
    public StarSystem(User user, int x, int y,
                      String name, StarSystemClassification starSystemType) {
        super(user, "", SpaceObjectClassification.starsystem, x, y);

        this.name = name;

        planets = new ArrayList<Planet>();
        fleets = new ArrayList<Fleet>();
        buildQueue = new ArrayList<Order>();

        this.starSystemType = starSystemType;
        this.ore = 0;
    }

    /**
     * Gets the number of items in the build queue
     *
     * @return build queue size
     */
    public int countItemsInBuildQueue() {
        return buildQueue.size();
    }

    /**
     * Gets and Removes the next (first) in the build queue
     *
     * @return the next item to build
     */
    public Order removeNextFromBuildQueue() {
        return buildQueue.remove(0);
    }

    /**
     * Gets the next (first) order from the build queue
     *
     * @return the next item to build
     */
    public Order getNextFromBuildQueue() {
        return buildQueue.get(0);
    }

    /**
     * Gets all the items in the build queue
     *
     * @return the build queue
     */
    public ArrayList<Order> getBuildQueue() {
        return buildQueue;
    }

    /**
     * Adds a build order to the end of the build queue
     *
     * @param buildOrder the order to add
     * @throws InvalidOrderException if Order is not a bulidStructure or buildShip order
     */
    public void addBuildOrder(Order buildOrder) throws InvalidOrderException {
        if (buildOrder instanceof BuildStructureOrder ||
                buildOrder instanceof BuildShipOrder) {
            // TODO check constraints (one per planet/system, etc...) + tech level
            buildQueue.add(buildOrder);
        } else {
            throw new InvalidOrderException("starsystem can only add a structure or ship order to it's build queue, you tried to add a object of class: " + buildOrder.getClass().getName());
        }

    }

    /**
     * Gets the starsystem clasification
     *
     * @return the classification
     */
    public StarSystemClassification getStarSystemClassification() {
        return starSystemType;
    }

    /**
     * Sets the starsystem classification for this system
     *
     * @param ssc the classification
     */
    public void setStarSystemClassification(StarSystemClassification ssc) {
        starSystemType = ssc;
    }

    /**
     * Sets the image filename for this system
     *
     * @param file the filename
     */
    public void setImageFile(String file) {
        imageFile = file;
    }

    /**
     * Gets the image filename for this system
     *
     * @return the filename
     */
    public String getImageFile() {
        return imageFile;
    }

    /**
     * Adds a planet to this system
     *
     * @param p planet to add
     */
    public void addPlanet(Planet p) {
        planets.add(p);
    }

    /**
     * Gets all the planets in this system
     *
     * @return all the planets
     */
    public ArrayList<Planet> getPlanets() {
        return planets;
    }

    /**
     * Gets all the habitable planets in this system
     *
     * @return all the habitable planets
     */
    public ArrayList<Planet> getHabitablePlanets() {
        ArrayList<Planet> habitable = new ArrayList<Planet>(planets.size());
        for (Planet p : planets) {
            if (p.getMaximumPopulation() > 0) {
                habitable.add(p);
            }
        }
        return habitable;
    }

    /**
     * Adds a fleet to this system
     *
     * @param f the fleet to add
     */
    public void addFleet(Fleet f) {
        fleets.add(f);
    }

    /**
     * Removes a fleet from this syste,
     *
     * @param f the fleet to remove
     */
    public void removeFleet(Fleet f) {
        fleets.remove(f);
    }

    /**
     * Gets all the fleets in this system
     *
     * @return all the fleets
     */
    public ArrayList<Fleet> getFleets() {
        return fleets;
    }

    /**
     * Gets a specific ship in this starsystem by user and id
     *
     * @param u  the user
     * @param id the ship id
     * @return the ship, or null if not found
     */
    public Ship getShipById(User u, long id) {
        for (Fleet f : fleets) {
            if (f.getUser().equals(u)) {
                for (Ship s : f.getShips()) {
                    if (s.getShipId() == id) {
                        return s;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Gets the maximum number of structures this system can have
     *
     * @return maximum number of structures
     */
    public int getMaxStructures() {
        int maxStr = 0;
        for (Planet p : planets) {
            maxStr += p.getMaximumStructures();
        }
        return maxStr;
    }

    /**
     * Counts the number of structures presently in this system
     *
     * @return number of structures
     */
    public int countStructures() {
        int structures = 0;
        for (Planet p : planets) {
            structures += p.getStructuresMap().size();
        }
        return structures;
    }

    /**
     * Gets the current number of structures in this system that is active/enabled
     *
     * @return number of enabled structures
     */
    public int countActiveStructures() {
        int structures = 0;
        for (Planet p : planets) {
            structures += p.countActiveStructures();
        }
        return structures;
    }

    /**
     * Gets the maximum possible population of this system
     *
     * @return the maximum population
     */
    public int getMaxPopulation() {
        int pop = 0;
        for (Planet p : planets) {
            pop += p.getMaximumPopulation();
        }
        return pop;
    }

    /**
     * Gets the current population of this system
     *
     * @return the population
     */
    public int getPopulation() {
        int pop = 0;
        for (Planet p : planets) {
            pop += p.getPopulation();
        }
        return pop;
    }

    /**
     * Gets the average fertility for the habitable planets in this system
     *
     * @return the average fertility (percentage)
     */
    public double getAvgFertility() {
        double fertility = 0;
        int habitablePlanets = 0;
        for (Planet p : planets) {
            if (p.getMaximumPopulation() > 0) {
                habitablePlanets++;
                fertility += p.getFertility();
            }
        }
        fertility /= habitablePlanets;

        return fertility;
    }

    /**
     * Gets the amount of industry produced in this system
     *
     * @return industry produced
     */
    public int getSystemIndustryProduced() {
        int totalIndustry = 0;
        for (Planet p : planets) {
            totalIndustry += p.getIndustryProduced();
        }
        return totalIndustry;
    }

    /**
     * Gets the amount of industry consumed in this system
     *
     * @return industry consumed
     */
    public int getSystemIndustryConsumed() {
        int totalIndustry = 0;
        for (Planet p : planets) {
            totalIndustry += p.getIndustryConsumed();
        }
        return totalIndustry;
    }

    /**
     * Gets the amount of power produced in this system
     *
     * @return power produced
     */
    public int getSystemPowerProduced() {
        int totalPower = 0;
        for (Planet p : planets) {
            totalPower += p.getPowerProduced();
        }
        return totalPower;
    }

    /**
     * Gets the amount of power consumed in this system
     *
     * @return power consumed
     */
    public int getSystemPowerConsumed() {
        int totalPower = 0;
        for (Planet p : planets) {
            totalPower += p.getPowerConsumed();
        }
        return totalPower;
    }

    /**
     * Gets the amount of research produced in this system
     *
     * @return reserach produced
     */
    public int getSystemResearchProduced() {
        int totalResearch = 0;
        for (Planet p : planets) {
            totalResearch += p.getResearchProduced();
        }
        return totalResearch;
    }

    /**
     * Gets the amount of research consumed in this system
     *
     * @return research consumed
     */
    public int getSystemResearchConsumed() {
        int totalResearch = 0;
        for (Planet p : planets) {
            totalResearch += p.getResearchConsumed();
        }
        return totalResearch;
    }


    /**
     * Gets the defense rating for this system (ground combat)
     *
     * @return the defense rating
     */
    public int getDefenseRating() {
        int totalCombat = 0;
        for (Planet p : planets) {
            totalCombat += p.getDefenseRating();
        }
        return totalCombat;
    }

    /**
     * Gets the food producsed in this system
     *
     * @return food produced
     */
    public int getSystemFoodProduced() {
        int totalFood = 0;
        for (Planet p : planets) {
            totalFood += p.getFoodProduced();
        }
        return totalFood;
    }

    /**
     * Gets the food consumed in this system
     *
     * @return food consumed
     */
    public int getSystemFoodConsumed() {
        int totalFood = 0;
        for (Planet p : planets) {
            totalFood += p.getFoodConsumed();
        }
        return totalFood;
    }

    /**
     * Gets the food surplus of this system
     *
     * @return food surplus
     */
    public int getSystemFoodSurplus() {
        int totalFood = 0;
        for (Planet p : planets) {
            totalFood += p.getFoodSurplus();
        }
        return totalFood;
    }

    /**
     * Gets the power surplus of this system
     *
     * @return power surplus
     */
    public int getSystemPowerSurplus() {
        return getSystemPowerProduced() - getSystemPowerConsumed();
    }

    /**
     * Gets the industry surplus of this system
     *
     * @return industry surplus
     */
    public int getSystemIndustrySurplus() {
        int res = getSystemIndustryProduced() - getSystemIndustryConsumed();
        if (getSystemPowerSurplus() < 0) {
            res = res / 2;
        }
        return res;
    }

    /**
     * Gets the research surplus of this system
     *
     * @return research surplus
     */
    public int getSystemResearchSurplus() {
        int totalResearch = getSystemResearchProduced() - getSystemResearchConsumed();

        // reduce if missing power or industry
        if (totalResearch > 1) {
            if (getSystemPowerSurplus() < 0) {
                totalResearch = (int) ((totalResearch / 100D) * 20);
            }
        }
        if (totalResearch > 1) {
            if (getSystemIndustrySurplus() < 0) {
                totalResearch = (int) ((totalResearch / 100D) * 60);
            }
        }

        return totalResearch;
    }

    /**
     * Sets the morale of this system
     *
     * @param morale the morale
     */
    public void setMorale(int morale) {
        this.morale = morale;

        if (morale < 0) {
            morale = 0;
        }

        if (morale > 100) {
            morale = 100;
        }
    }

    /**
     * Gets the morale of this system
     *
     * @return the morale
     */
    public int getMorale() {
        return morale;
    }

    /**
     * Gets the deuterium per turn for this system (gas planets)
     *
     * @return deuterium per turn
     */
    public int getDeuteriumPerTurn() {
        int res = 0;
        for (Planet p : planets) {
            res += p.getDeuteriumPerTurn();
        }
        return res;
    }

    /**
     * Gets the resources left for this system (If system is a asteroid field or nebula)
     *
     * @return resources left
     */
    public int getResourcesLeft() {
        return resourcesLeft;
    }

    /**
     * Sets the number of resources left in this system (asteroid or nebula)
     * If the amount is negative or 0, the starsystem is changed to type "empty"
     *
     * @param resourcesLeft the new resource amount
     */
    public void setResourcesLeft(int resourcesLeft) {
        this.resourcesLeft = resourcesLeft;
        if (resourcesLeft <= 0) {
            resourcesLeft = 0;
            starSystemType = StarSystemClassification.empty;
            imageFile = "";
        }
    }

    /**
     * Gets a planet by number
     *
     * @param num the planet number
     * @return the planet
     */
    public Planet getPlanetByNumber(int num) {
        for (Planet p : planets) {
            if (p.getPlanetNumber() == num) {
                return p;
            }
        }
        return null;
    }

    /**
     * Adds a structure to a planet
     *
     * @param p          the planet
     * @param planetSlot the slot on the planet to make the structure
     * @param s          the structure
     */
    public void addStructure(Planet p, int planetSlot, Structure s) {
        if (p.getStructuresMap().size() < p.getMaximumStructures()) {
            p.getStructuresMap().put(planetSlot, s);
        }
    }

    /**
     * Checks if this starsystem has a shipyard
     *
     * @return true if shipyard is present
     */
    public boolean hasShipyard() {
        for (Planet p : planets) {
            if (p.hasStructure(StaticData.shipyard1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this starsystem has an active shipyard
     *
     * @return true if shipyard is present
     */
    public boolean hasActiveShipyard() {
        boolean activeShipyardFound = false;
        for (Planet p : planets) {
            for (int i : p.getStructuresMap().keySet()) {
                Structure s = p.getStructuresMap().get(i);
                if (s.equals(StaticData.shipyard1)) {
                    if (p.isStructureEnabled(i)) {
                        activeShipyardFound = true;
                    }
                }
            }
        }
        return activeShipyardFound;
    }

    /**
     * Get maximum ore storage for this system
     *
     * @return max ore storage
     */
    public int getMaxOreStorage() {
        int maxOre = 0;
        for (Planet p : planets) {
            maxOre += p.getOreStorage();
        }
        return maxOre;
    }

    /**
     * Gets the amount of ore stored in this system
     *
     * @return ore amount
     */
    public int getOre() {
        return ore;
    }

    /**
     * Sets the amount of ore stored in this system
     *
     * @param ore new amount
     */
    public void setOre(int ore) {
        this.ore = ore;
        int max = getMaxOreStorage();
        if (this.ore > max) {
            this.ore = max;
        }
    }

    /**
     * Adds ore to this system
     *
     * @param ore amount to add
     */
    public void addOre(int ore) {
        setOre(ore + getOre());
    }

    /**
     * Removes ore from this system
     *
     * @param ore amount to remove
     */
    public void removeOre(int ore) {
        setOre(getOre() - ore);
    }

    /**
     * Gets the maximum amount of deuterium for this system
     *
     * @return maximum deuterium capacity
     */
    public int getMaxDeuterium() {
        int max = 0;
        for (Planet p : planets) {
            max += p.getDeuteriumStorage();
        }
        return max;
    }

    /**
     * Gets the current amount of deuterium in this system
     *
     * @return deteurium amount
     */
    public int getDeuterium() {
        return deuterium;
    }

    /**
     * Gets the number of troops (for starsystem owner) in this system
     *
     * @return number of troops
     */
    public int getTroopCount() {
        return troops;
    }

    /**
     * Sets the number of troops (for starsystem owner) in this system
     *
     * @param troops new troop value
     */
    public void setTroopCount(int troops) {
        this.troops = troops;

        int max = getTroopCapacity();
        if (this.troops > max) {
            this.troops = max;
        }
        if (this.troops < 0) {
            this.troops = 0;
        }
    }

    /**
     * Adds troops (for starsystem owner) to this system
     *
     * @param troops
     */
    public void addTroops(int troops) {
        this.troops += troops;

        int max = getTroopCapacity();
        if (this.troops > max) {
            this.troops = max;
        }
    }

    /**
     * Gets the number of troops produced by this system per turn
     *
     * @return troop production
     */
    public int getTroopProduction() {
        int troopProd = 1;
        for (Planet p : planets) {
            troopProd += p.getTroopProduction();
        }
        return troopProd;
    }

    /**
     * Gets the maximum number of troops this system can sustain
     *
     * @return maximum number of troops
     */
    public int getTroopCapacity() {
        int maxTroops = 3 * planets.size();
        for (Planet p : planets) {
            maxTroops += p.getTroopCapacity();
        }
        if (maxTroops > 150) {
            maxTroops = 150;
        }
        return maxTroops;
    }

    /**
     * Sets the current deuterium amount for this system
     *
     * @param deuterium new amount
     */
    public void setDeuterium(int deuterium) {
        this.deuterium = deuterium;
        if (this.deuterium > getMaxDeuterium()) {
            this.deuterium = getMaxDeuterium();
        }
    }

    /**
     * Adds deuterium to this system
     *
     * @param deuterium amount to add
     */
    public void addDeuterium(int deuterium) {
        setDeuterium(deuterium + getDeuterium());
    }

    /**
     * Removes deuterium from this system
     *
     * @param deuterium amount to remove
     */
    public void removeDeuterium(int deuterium) {
        this.setDeuterium(getDeuterium() - deuterium);
    }

    /**
     * Checks if this system has a ore refinery
     *
     * @return true if system has ore refinery
     */
    public boolean hasOreRefinery() {
        for (Planet p : planets) {
            if (p.hasStructure(StaticData.oreRefinery1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this system has a deuterium plant
     *
     * @return true if deuterium plant found
     */
    public boolean hasDeuteriumPlant() {
        for (Planet p : planets) {
            if (p.hasStructure(StaticData.deuteriumProcessingPlant1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the sensor cost for this system
     *
     * @return the sensor cost
     */
    public int getSensorCost() {
        return sensorCost;
    }

    /**
     * Sets the sensor cost for this system
     *
     * @param sensorCost the sensor cost
     */
    public void setSensorCost(int sensorCost) {
        this.sensorCost = sensorCost;
    }

    /**
     * Gets the scan strength for this star system, 0 to 100
     *
     * @return the sensor strength
     */
    public int getSystemScanStrength() {
        int strength = 0;
        if (!getUser().equals(StaticData.nobodyUser)) {
            strength += 30;
        }

        for (Planet p : getPlanets()) {
            strength += p.getScanStrength();
        }
        if (strength < 0) {
            strength = 0;
        }
        return strength;
    }

    /**
     * Calculates how much industry this star system contributes to the
     * ship upkeep pool each turn
     *
     * @return ship upkeep from this system
     */
    public int getShipUpkeepContribution() {
        int upkeep = 0;

        if (hasActiveShipyard()) {
            upkeep += getSystemIndustrySurplus() / 9;
        } else {
            upkeep += getSystemIndustrySurplus() / 12;
        }
        return upkeep;
    }

    public boolean equals(StarSystem otherSystem) {
        if (x == otherSystem.x &&
                y == otherSystem.y &&
                classification.equals(otherSystem.classification) &&
                user.equals(otherSystem.user)) {
            return true;
        }
        return false;
    }

    public boolean equals(Object o) {
        if (o instanceof StarSystem) {
            return equals((StarSystem) o);
        }
        return false;
    }

    /**
     * Gets the number of bunkers in this system
     *
     * @return number of bunkers
     */
    public int getNumberOfBunkers() {
        int bunkers = 0;
        for (Planet p : planets) {
            bunkers += p.countBunkers();
        }
        return bunkers;
    }

    /**
     * Removes part of system population, distributed amongst individual planets
     * used for: colony ships
     *
     * @param popToRemove million of people to remove
     */
    public void removePopulationDistributed(int popToRemove) {
        int systemPop = getPopulation();
        double onePercentOfSystemPop = systemPop / 100D;

        for (Planet p : getHabitablePlanets()) {
            if (p.getPopulation() > 5) {
                p.setPopulation((int) (p.getPopulation() - ((p.getPopulation() / onePercentOfSystemPop) * (popToRemove / 100D))));
            }
        }
    }
}