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

import com.frostvoid.trekwar.common.shipHulls.HullClass;
import com.frostvoid.trekwar.common.shipComponents.ShipComponent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import com.frostvoid.trekwar.common.exceptions.SlotException;
import com.frostvoid.trekwar.common.shipComponents.Armor;
import com.frostvoid.trekwar.common.shipComponents.BeamEmitter;
import com.frostvoid.trekwar.common.shipComponents.BussardCollector;
import com.frostvoid.trekwar.common.shipComponents.Cargo;
import com.frostvoid.trekwar.common.shipComponents.ColonizationModule;
import com.frostvoid.trekwar.common.shipComponents.DeuteriumTank;
import com.frostvoid.trekwar.common.shipComponents.ImpulseDrive;
import com.frostvoid.trekwar.common.shipComponents.MiningLaser;
import com.frostvoid.trekwar.common.shipComponents.Sensor;
import com.frostvoid.trekwar.common.shipComponents.ShieldEmitter;
import com.frostvoid.trekwar.common.shipComponents.TorpedoLauncher;
import com.frostvoid.trekwar.common.shipComponents.TroopTransport;
import com.frostvoid.trekwar.common.shipComponents.WarpCore;
import com.frostvoid.trekwar.server.TrekwarServer;

/**
 * A ship is a particular Hull with a bunch of ShipComponents.
 * Ships are always in fleets.
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Ship implements Serializable {

    protected String templateName;
    protected int shipId;
    protected User user;
    private Fleet fleet;
    protected HullClass hullClass;
    protected HashMap<Integer, ShipComponent> components;
    private int xp; // 0 - 100
    private int morale; // 0 - 100
    private int crew;
    private int currentHullStrength;
    private int currentArmorStrength;
    private int currentShieldStrength;
    private int currentDeuterium;
    private int cargoDeuterium;
    private int cargoOre;
    private int troops;
    private int colonists;

    private int actionPoints; // used to do actions in combat

    public Ship() {
        this.components = new HashMap<Integer, ShipComponent>();
    }

    /**
     * Creates a new ship object
     *
     * @param user the owner of the ship
     * @param templateName the name of the template used to build this ship
     * @param shipId the unique (per user) ID of the ship
     * @param hull the base hull class
     */
    public Ship(User user, Fleet fleet, String templateName, int shipId, HullClass hull) {
        this.templateName = templateName;
        this.shipId = shipId;
        this.user = user;
        this.fleet = fleet;
        this.hullClass = hull;
        this.xp = 0; // TODO experience from system
        this.morale = 50; // TODO get morale based on faction, starsystem buildt in
        this.components = new HashMap<Integer, ShipComponent>();
        
        battle_restoreActionPoints();
    }

    /**
     * Should be run at ship creation, AFTER components are added
     */
    public void initShip() {
        this.currentHullStrength = getMaxHitpoints();
        this.currentShieldStrength = getMaxShield();
        this.currentArmorStrength = getMaxArmor();
        this.setCrew(hullClass.getMaxCrew());
        setCurrentDeuterium(getMaxDeuterium());
    }

    /**
     * Gets the speed of this ship
     *
     * @return ship speed
     */
    public int getSpeed() {
        int speed = 0;
        int numWarpcores = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof WarpCore) {
                numWarpcores++;
                int addition = ((WarpCore) c).getSpeed();
                addition -= (numWarpcores*2);
                if(addition < 1) {
                    addition = 1;
                }
                speed += addition;
            } else if (c instanceof Armor) {
                speed -= 2;
            } else if (c instanceof ColonizationModule) {
                speed -= 2;
            } else if (c instanceof Cargo) {
                speed -= 2;
            } else {
                speed -= 1;
            }
        }

        // add bonus from propulsion tech
        double bonus = user.getHighestTech(TechnologyGenerator.techType.propulsiontech).getLevel() / 20;
        return speed + (int) bonus;
    }

    /**
     * Gets the map of components in this ship
     *
     * @return ship components
     */
    public HashMap<Integer, ShipComponent> getComponents() {
        return components;
    }

    /**
     * Sets a component at a particular slot in this ship
     *
     * @param slot the slot
     * @param c the component to place there
     * @throws SlotException if invalid slot used
     */
    public void setComponent(int slot, ShipComponent c) throws SlotException {
        if (slot < hullClass.getSlots()) {
            components.put(slot, c);
        } else {
            throw new SlotException("Invalid slot address.. hulltype " + hullClass.getName() + " only has " + hullClass.getSlots() + " slots");
        }
    }

    /**
     * Applies a template to a ship, setting all the tempaltes components at their corresponding places
     *
     * @param template the template to use
     * @throws SlotException if the ship template is incompatible with this ships base hull class
     */
    public void applyTemplate(ShipTemplate template) throws SlotException {
        if (!template.getHullClass().equals(hullClass)) {
            throw new SlotException("Can not apply template " + template.getName() + ". Template has class " + template.getHullClass().getName() + ", while ship has class " + hullClass.getName());
        }
        for (int key : template.getComponents().keySet()) {
            setComponent(key, template.getComponents().get(key));
        }
    }

    /**
     * Gets the current armor strength
     *
     * @return armor strength
     */
    public int getCurrentArmorStrength() {
        return currentArmorStrength;
    }
    
    /**
     * Sets the current armor strength
     * 
     * @param armorStrength 
     */
    public void setCurrentArmorStrength(int armorStrength) {
        currentArmorStrength = armorStrength;
        if(currentArmorStrength < 0) {
            currentArmorStrength = 0;
        }
        if(currentArmorStrength > getMaxArmor()) {
            currentArmorStrength = getMaxArmor();
        }
    }

    /**
     * Gets the current deuterium reserve
     *
     * @return deuterium
     */
    public int getCurrentDeuterium() {
        return currentDeuterium;
    }

    /**
     * Gets the current hull strength
     *
     * @return hull strength
     */
    public int getCurrentHullStrength() {
        return currentHullStrength;
    }
    
    /**
     * Sets the current hull strength 
     * 
     * @param hullStrength 
     */
    public void setCurrentHullStrength(int hullStrength) {
        currentHullStrength = hullStrength;
        if(currentHullStrength < 0) {
            currentHullStrength = 0;
        }
        if(currentHullStrength > getMaxHitpoints()) {
            currentHullStrength = getMaxHitpoints();
        }
    }

    /**
     * Gets the current shield strength
     *
     * @return shield strength
     */
    public int getCurrentShieldStrength() {
        return currentShieldStrength;
    }
    
    /**
     * Sets the current shield strength
     * 
     * @param shieldStrength 
     */
    public void setCurrentShieldStrength(int shieldStrength) {
        currentShieldStrength = shieldStrength;
        if(currentShieldStrength < 0) {
            currentShieldStrength = 0;
        } 
        if(currentShieldStrength > getMaxShield()) {
            currentShieldStrength = getMaxShield();
        }
    }

    /**
     * Gets the name of this ship (name of template used to build it)
     *
     * @return the ship name
     */
    public String getName() {
        return templateName;
    }

    /**
     * Gets the base hull class for this ship
     *
     * @return the hull class
     */
    public HullClass getHullClass() {
        return hullClass;
    }

    /**
     * Gets the amount of deuterium needed to move this ship 1 tile
     *
     * @return deuterium usage
     */
    public int getDeuteriumUsage() {
        double res = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof Armor) {
                res += 0.5;
            }
            if (c instanceof WarpCore) {
                res += 2;
            } else {
                res += 0.1;
            }
        }

        // tech bonus
        double bonus = user.getHighestTech(TechnologyGenerator.techType.energytech).getLevel() / 5;
        bonus += user.getHighestTech(TechnologyGenerator.techType.propulsiontech).getLevel() / 5;
        res += hullClass.getBaseDeuteriumUseage();
        res -= bonus;
        
        if (res < 1) {
            res = 1;
        }
        return (int) res;
    }

    /**
     * Sets the current deuterium of this ship
     *
     * @param newAmount the amount
     */
    public void setCurrentDeuterium(int newAmount) {
        currentDeuterium = newAmount;
        if (currentDeuterium < 0) {
            currentDeuterium = 0;
        }
        if (currentDeuterium > getMaxDeuterium()) {
            currentDeuterium = getMaxDeuterium();
        }
    }

    /**
     * Adds deuterium to this ship
     *
     * @param amount amount to add
     */
    public void addDeuterium(int amount) {
        currentDeuterium += amount;
        if (currentDeuterium > getMaxDeuterium()) {
            currentDeuterium = getMaxDeuterium();
        }
    }

    /**
     * Gets the maximum deuterium this ship can hold
     *
     * @return maximum deuterium
     */
    public int getMaxDeuterium() {
        int res = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof DeuteriumTank) {
                res += ((DeuteriumTank) c).getCapacity();
            }
        }

        // bonus from technology
        int bonus = user.getHighestTech(TechnologyGenerator.techType.energytech).getLevel() * 5;
        bonus += user.getHighestTech(TechnologyGenerator.techType.propulsiontech).getLevel() * 3;
        return hullClass.getBaseDeuteriumStorage() + res + bonus;
    }

    /**
     * Gets the maximum armor value of this ship
     *
     * @return maximum armor
     */
    public int getMaxArmor() {
        int res = hullClass.getBaseArmor();
        for (ShipComponent c : components.values()) {
            if (c instanceof Armor) {
                res += ((Armor) c).getArmor();
            } else {
                res -= 10;
            }
        }

        res += user.getHighestTech(TechnologyGenerator.techType.constructiontech).getLevel() * 6;
        res += user.getHighestTech(TechnologyGenerator.techType.weaponstech).getLevel() * 4;

        if (res < 0) {
            res = 0;
        }
        return res;
    }

    /**
     * Gets the maximum shield value of this ship
     *
     * @return maximum shield
     */
    public int getMaxShield() {
        int res = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof ShieldEmitter) {
                res += ((ShieldEmitter) c).getStrength();
                // energytech bonus
                res += user.getHighestTech(TechnologyGenerator.techType.energytech).getLevel() * 2;
            }
        }
        if (res < 0) {
            res = 0;
        }
        return res;
    }

    /**
     * Gets the maximum number of hitpoints for this ship
     *
     * @return maximum hitpoints
     */
    public int getMaxHitpoints() {
        int res = hullClass.getBaseHitpoints();
        res += (components.values().size() * 10); // more equiptment = more hitpoints

        // bonus for construction
        res += user.getHighestTech(TechnologyGenerator.techType.constructiontech).getLevel() * 10;

        if (res < 0) {
            res = 0;
        }
        return res;
    }

    /**
     * Gets the cost / industry required to build this ship
     *
     * @return ship cost
     */
    public int getCost() {
        int cost = hullClass.getBaseCost();
        for (ShipComponent c : components.values()) {
            cost += c.getCost();
        }
        return cost;
    }

    /**
     * Gets the energy surplus of this ship
     *
     * @return energy
     */
    public int getEnergy() {
        int res = 0;
        int numWarpcores = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof WarpCore) {
                numWarpcores++;
                int addition = (int)((double)c.getEnergy() - (numWarpcores*1.5D));
                if(addition < 1) {
                    addition = 1;
                }
                res += addition;
            }
            else {
                res += c.getEnergy();
            }
        }
        int bonus = user.getHighestTech(TechnologyGenerator.techType.energytech).getLevel() * 2;
        return res + bonus;
    }

    /**
     * Gets the sensor strength of this ship
     *
     * @return sensor strength
     */
    public int getSensorStrength() {
        int res = 0;
        int numSensors = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof Sensor) {
                numSensors++;
                res += ((Sensor) c).getStrength() / numSensors;
            }
        }
        return res;
    }

    /**
     * Gets the signature strength of this ship
     *
     * @return signature strength
     */
    public int getSignatureStrength() {
        int res = (getMaxHitpoints() + getMaxArmor() + getMaxShield()) / 10;
        for (ShipComponent c : components.values()) {
            if (c instanceof Sensor) {
                res += 25;
            } else if (c instanceof WarpCore) {
                res += 15;
            } else {
                res += 4;
            }
        }
        return res;
    }

    /**
     * Gets the maneuverability/agility of this ship
     *
     * @return maneuverability
     */
    public int getManeuverability() {
        int maneuverability = hullClass.getBaseManoeuvrability();
        for (ShipComponent c : components.values()) {
            if (c instanceof ImpulseDrive) {
                maneuverability += ((ImpulseDrive) c).getPower();
            } else if (c instanceof Armor) {
                maneuverability -= 10;
            } else {
                maneuverability -= 2;
            }
        }

        // add bonus from propulsion tech
        int bonus = user.getHighestTech(TechnologyGenerator.techType.propulsiontech).getLevel() * 2;
        return maneuverability + bonus;
    }

    /**
     * Checks if this ship can colonize starsystems
     *
     * @return true if ship can colonize
     */
    public boolean canColonize() {
        for (ShipComponent c : components.values()) {
            if (c instanceof ColonizationModule) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this ship can mine asteroids
     *
     * @return true if ship can mine
     */
    public boolean canMine() {
        for (ShipComponent c : components.values()) {
            if (c instanceof MiningLaser) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this ship can transport troops / invade systems
     *
     * @return true if ship is a troop transport
     */
    public boolean hasTroopTransport() {
        for (ShipComponent c : components.values()) {
            if (c instanceof TroopTransport) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the mining output of this ship (per turn)
     *
     * @return mining output
     */
    public int getMiningOutput() {
        int cap = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof MiningLaser) {
                cap += ((MiningLaser) c).getCapacity();
            }
        }
        return cap;
    }

    /**
     * Checks if this ship can harvest deuterium (has bussard collector AND cargo bay)
     *
     * @return true if ship can harvest deuterium
     */
    public boolean canHarvestDeuterium() {
        return canLoadUnloadCargo() && canUseBussardCollector();
    }

    /**
     * Checks if this ship has a torpedo launcher (and can bomb planets)
     *
     * @return true if torpedo launcher present
     */
    public boolean hasTorpedoLauncher() {
        for (ShipComponent c : components.values()) {
            if (c instanceof TorpedoLauncher) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the torpedo launchers in this fleet
     * 
     * @return the fleets torpedo launchers
     */
    public ArrayList<TorpedoLauncher> getTorpedoLaunchers() {
        ArrayList<TorpedoLauncher> res = new ArrayList<TorpedoLauncher>();
        
        for (ShipComponent c : components.values()) {
            if (c instanceof TorpedoLauncher) {
                res.add((TorpedoLauncher)c);
            }
        }
        
        return res;
    }
    
    /*
     * Counts the number of torpedo launchers on this ship
     * 
     * @return number of torpedo launchers
     */
    public int countTorpedoLaunchers() {
        int res = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof TorpedoLauncher) {
                res++;
            }
        }
        return res;
    }
    
    /**
     * Counts the number of beam emitters on this ship
     * 
     * @return number of beam emitters
     */
    public int countBeamEmitters() {
        int res = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof BeamEmitter || c instanceof MiningLaser) {
                res++;
            }
        }
        return res;
    }
    
    /**
     * Checks if this ship has a beam emitter
     * 
     * @return true if beam emitter present
     */
    public boolean hasBeamWeapons() {
        for (ShipComponent c : components.values()) {
            if (c instanceof BeamEmitter || c instanceof MiningLaser) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this ship has the ability to transport cargo
     *
     * @return true if ship can transport cargo
     */
    public boolean canLoadUnloadCargo() {
        for (ShipComponent c : components.values()) {
            if (c instanceof Cargo) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the maximum cargo space of this ship
     *
     * @return cargo space
     */
    public int getMaxCargoSpace() {
        int cap = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof Cargo) {
                cap += ((Cargo) c).getCapacity();
            }
        }
        return cap;
    }

    /**
     * Gets the currently available cargo space of this ship
     *
     * @return available cargo space
     */
    public int getAvailableCargoSpace() {
        return getMaxCargoSpace() - getCargoDeuterium() - getCargoOre();
    }

    /**
     * Checks if this ship can refuel itself by using bussard collectors
     *
     * @return true if ship can refuel itself
     */
    public boolean canUseBussardCollector() {
        if (getBussardCollectorCapacity() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the capacity of this ships bussard collectors
     *
     * @return bussard collector capacity
     */
    public int getBussardCollectorCapacity() {
        int cap = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof BussardCollector) {
                cap += ((BussardCollector) c).getCapacity();
            }
        }
        return cap;
    }

    /**
     * Checks if this ship can build a starbase
     *
     * @return true if ship can build starbase
     */
    public boolean canBuildStarbase() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Checks if this ship can cloak
     *
     * @return true if ship can cloak
     */
    public boolean canCloak() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Checks if this ship has a scanner
     *
     * @return true if ship can scan
     */
    public boolean canScan() {
        for (ShipComponent c : components.values()) {
            if (c instanceof Sensor) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the current crew of this ship
     *
     * @return the crew
     */
    public int getCrew() {
        return crew;
    }

    /**
     * Sets the current crew of this ship
     *
     * @param crew crew count
     */
    public void setCrew(int crew) {
        this.crew = crew;
    }

    /**
     * Gets the current XP of this ship
     *
     * @return the experience
     */
    public int getXp() {
        return xp;
    }

    /**
     * Sets the experience points (XP) of this ship
     *
     * @param xp the experience points
     */
    public void setXp(int xp) {
        this.xp = xp;
        if(xp < 0) {
            this.xp = 0;
        }
        if(xp > 100) {
            this.xp = 100;
        }
    }

    /**
     * Gets the owner of this ship
     *
     * @return the ship owner
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Gets the fleet this ship belongs to
     * 
     * @return the fleet
     */
    public Fleet getFleet() {
        return fleet;
    }
    
    /**
     * Puts this ship in a fleet
     * 
     * @param fleet the fleet
     */
    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }
    
    /**
     * Destroys this ship
     */
    public void destroy() {
        TrekwarServer.LOG.log(Level.FINE, "Destroying ship {0} with ID: {1}", new Object[]{getName(), getShipId()});
        if(fleet != null) {
            this.fleet.removeShip(this);
        }
        this.fleet = null;
    }

    /**
     * Gets the morale of this ship
     *
     * @return the morale
     */
    public int getMorale() {
        return morale;
    }

    /**
     * Sets the morale of this ship
     *
     * @param morale the morale to set
     */
    public void setMorale(int morale) {
        this.morale = morale;
    }

    /**
     * Gets the total weapon strength of this ship
     *
     * @return weapon strength
     */
    public int getTotalWeaponStrength() {
        int wep = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof BeamEmitter) {
                wep += ((BeamEmitter) c).getDamage();
            } else if (c instanceof TorpedoLauncher) {
                wep += ((TorpedoLauncher) c).getDamage();
            } else if (c instanceof MiningLaser) {
                wep += ((MiningLaser) c).getCapacity() / 10;
            }
        }
        return wep;
    }

    /**
     * Gets this ships unique id (unique for the owner)
     *
     * @return ship id
     */
    public int getShipId() {
        return shipId;
    }

    /**
     * Gets the amount of detuerium in this ships cargo hold
     *
     * @return deuterium amount
     */
    public int getCargoDeuterium() {
        return cargoDeuterium;
    }

    /**
     * Sets the amount of deuterium in this ships cargo hold
     *
     * @param cargoDeuterium deuterium in cargo
     */
    public void setCargoDeuterium(int cargoDeuterium) {
        this.cargoDeuterium = cargoDeuterium;
    }

    /**
     * Gets the amount of ore in this ships cargo hold
     *
     * @return ore amount
     */
    public int getCargoOre() {
        return cargoOre;
    }

    /**
     * Sets the amount of ore in this ships cargo hold
     *
     * @param cargoOre ore amount
     */
    public void setCargoOre(int cargoOre) {
        this.cargoOre = cargoOre;
    }

    /**
     * Gets the amount of troops this ship can carry
     *
     * @return troop capacity
     */
    public int getTroopCapacity() {
        int cap = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof TroopTransport) {
                cap += ((TroopTransport)c).getCapacity();
            }
        }
        return cap;
    }
    
    /**
     * Gets the amount of Colonist this ship can carry
     *
     * @return Colonist capacity
     */
    public int getColonistCapacity() {
        int cap = 0;
        for (ShipComponent c : components.values()) {
            if (c instanceof ColonizationModule) {
                cap += ((ColonizationModule)c).getPopulationCapacity();
            }
        }
        return cap;
    }

    /**
     * Gets the amount of troops currently on this ship
     *
     * @return number of troops
     */
    public int getTroops() {
        return troops;
    }

    /**
     * Sets the number of troops on this ship
     *
     * @param troops number of troops
     */
    public void setTroops(int troops) {
        this.troops = troops;

        if(this.troops > getTroopCapacity()) {
            this.troops = getTroopCapacity();
        }
    }
    
    /**
     * Gets the amount of colonists currently on this ship
     *
     * @return number of colonists
     */
    public int getColonists() {
        return colonists;
    }

    /**
     * Sets the number of Colonists on this ship
     *
     * @param colonists number of Colonists
     */
    public void setColonists(int colonists) {
        this.colonists = colonists;

        if(this.colonists > getColonistCapacity()) {
            this.colonists = getColonistCapacity();
        }
    }

    /**
     * Gets the number of action points this ship currently has
     *
     * @return action points
     */
    public int getActionPoints() {
        return actionPoints;
    }
    
    /**
     * Spend some actions points doing battle
     * 
     * @param points number of spoints to spend
     */
    public void spendActionPoints(int points) {
        actionPoints -= points;
        if(actionPoints < 0) {
            actionPoints = 0;
        }
    }

    /**
     * Restores this ships action points
     */
    public final void battle_restoreActionPoints() {
        // base points
        actionPoints = (hullClass.getSlots()/2) + 5;
        
        // points from components
        for (ShipComponent c : components.values()) {
            if (c instanceof BeamEmitter) {
                actionPoints += ((BeamEmitter)c).getActionPointsRequired();
            }
            else if (c instanceof MiningLaser) {
                actionPoints += 2;
            }
            else if (c instanceof TorpedoLauncher) {
                actionPoints += ((TorpedoLauncher)c).getActionPointsRequired();
            }
            else if (c instanceof ImpulseDrive) {
                actionPoints += ((ImpulseDrive)c).getActionPointBonus();
            }
        }
        
        // points from XP:
        actionPoints += (xp / 10);
        
        // penalty for crew loss
        double crewLossPercentage = (100.0D/((double)hullClass.getMaxCrew())) * ((double)crew);
        actionPoints = (int) ((((double)actionPoints) / 100.0) * crewLossPercentage);
        
        // penatly for structural damage
        double hitpointLossPercentage = (100.0D/((double)getMaxHitpoints())) * ((double)getCurrentHullStrength());
        
        actionPoints = (int) ((((double)actionPoints) / 100.0) * hitpointLossPercentage);
    }

    /**
     * Checks if this ship is armed
     * 
     * @return true if ship has weapons
     */
    public boolean isArmed() {
        return (hasBeamWeapons() || hasTorpedoLauncher());
    }
    
    /**
     * Gets the upkeep cost (industry) needed to maintain this ship each turn
     * 
     * @return ship upkeep cost 
     */
    public int getUpkeepCost() {
        return getCost() / StaticData.SHIP_UPKEEP_COST_FACTOR;
    }
    
    
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Ship) {
            Ship other = (Ship)o;
            if(getShipId() == other.getShipId() && getUser().equals(other.getUser())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.templateName != null ? this.templateName.hashCode() : 0);
        hash = 59 * hash + this.shipId;
        hash = 59 * hash + (this.user != null ? this.user.hashCode() : 0);
        return hash;
    }
}