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

import java.util.ArrayList;
import java.util.logging.Level;

import com.frostvoid.trekwar.common.orders.Order;
import com.frostvoid.trekwar.common.shipComponents.TorpedoLauncher;
import com.frostvoid.trekwar.server.TrekwarServer;

/**
 * A fleet is a player owned collection of ships that can do things (execute orders)
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Fleet extends SpaceObject {

    private StarSystem currentLocation;
    private ArrayList<Ship> ships;
    private Order currentOrder;
    private int movementLeft; // 10 = ship moves 1 extra tile

    /**
     * Gets "extra" movement points this fleet has saved up
     *
     * @return movement points left
     */
    public int getMovementLeft() {
        return movementLeft;
    }

    /**
     * Sets the "extra" movement points this fleet has saved up
     *
     * @param movementLeft points left
     */
    public void setMovementLeft(int movementLeft) {
        this.movementLeft = movementLeft;
    }

    /**
     * Creates a new fleet object
     *
     * @param user the owner of the fleet
     * @param name the unique name of the fleet
     * @param x the fleets x location
     * @param y the fleets y location
     */
    public Fleet(User user, String name, StarSystem location) {
        super(user, name, SpaceObjectClassification.fleet, location.getX(), location.getY());

        currentLocation = location;
        ships = new ArrayList<Ship>();
        movementLeft = 0;
    }

    /**
     * Moves the fleet on the map
     *
     * @param map the map
     * @param moveX new X location
     * @param moveY new Y location
     */
    public void move(StarSystem[][] map, int moveX, int moveY) {
        StarSystem src = map[x][y];
        StarSystem dst = map[moveX][moveY];

        src.removeFleet(this);
        dst.addFleet(this);
        x = moveX;
        y = moveY;
        
        currentLocation = dst;
    }

    /**
     * Gets all the ships in this fleet
     *
     * @return all ships
     */
    public ArrayList<Ship> getShips() {
        return ships;
    }

    /**
     * Adds a ship to this fleet
     *
     * @param s the ship to add
     */
    public void addShip(Ship s) {
        ships.add(s);
        s.setFleet(this);
    }

    /**
     * Removes a fleet from this fleet, if there are no more ships left
     * the fleet itself is removed
     *
     * @param s the ship to remove
     */
    public void removeShip(Ship s) {
        TrekwarServer.LOG.log(Level.FINE, "Removing ship {0} from fleet {1} with size {2} ", new Object[]{s.getName(), getName(), ships.size()});
        ships.remove(s);
        TrekwarServer.LOG.log(Level.FINE, "Number of ships left after removal: {0}", ships.size());
        if (ships.isEmpty()) {
            TrekwarServer.LOG.log(Level.FINE, "All ships removed from fleet {0}, deleting fleet from user {1} and system: {2}", new Object[]{getName(), user.getUsername(), currentLocation.getName()});
            user.removeFleet(this);
            currentLocation.removeFleet(this);
        }
    }

    /**
     * Gets the current order of this fleet
     *
     * @return the order
     */
    public Order getOrder() {
        return currentOrder;
    }

    /**
     * Sets the current order of this fleet
     *
     * @param o the order
     */
    public void setOrder(Order o) {
        currentOrder = o;
    }

    /**
     * Gets the maximum speed of this fleet (the slowest ship in the fleet)
     *
     * @return the speed
     */
    public int getSpeed() {
        int min = 999;

        // get slowest speed
        for (Ship s : ships) {
            if (s.getSpeed() < min) {
                min = s.getSpeed();
            }
        }
        return min;
    }
    
    public String getSpeedHumanreadable() {
        double speed = getSpeed();
        
        return "" + speed/10;
    }

    /**
     * Gets shared deuterium left in this fleet
     *
     * @return deuterium left
     */
    public int getDeuteriumLeft() {
        int res = 0;
        for (Ship s : ships) {
            res += s.getCurrentDeuterium();
        }
        return res;
    }

    /**
     * Gets the deuterium usage (per tile movement) of this fleet
     *
     * @return deuterium usage
     */
    public int getDeuteriumUsage() {
        int res = 0;
        for (Ship s : ships) {
            res += s.getDeuteriumUsage();
        }
        return res;
    }

    /**
     * Adds deuterium to the fleet, sharing it evenly amongst the ships
     *
     * @param deuterium the deuterium to add
     * @return true if amount to add was greater than 0
     */
    public boolean addDeuteriumFair(int deuterium) {
        if (deuterium <= 0) {
            return false;
        }

        ArrayList<Ship> shipsToFuel = new ArrayList<Ship>();
        for (Ship s : ships) {
            if (s.getCurrentDeuterium() < s.getMaxDeuterium()) {
                shipsToFuel.add(s);
            }
        }

        if (shipsToFuel.isEmpty()) {
            return false;
        }

        int portion = deuterium / shipsToFuel.size();
        if (portion == 0) {
            portion = 1;
        }

        while (shipsToFuel.size() > 0 && deuterium > 0) {
            for (int i = 0; i < shipsToFuel.size(); i++) {
                Ship ship = shipsToFuel.get(i);

                ship.addDeuterium(portion);
                deuterium -= portion;
                if (ship.getCurrentDeuterium() >= ship.getMaxDeuterium()) {
                    shipsToFuel.remove(i--);
                }
            }
        }
        return true;
    }

    /**
     * Gets the combined bussard collector capacity of all ships in the fleet
     *
     * @return bussard collector capacity
     */
    public int getBussardCollectorCapacity() {
        int cap = 0;
        for (Ship s : ships) {
            cap += s.getBussardCollectorCapacity();
        }
        return cap;
    }

    /**
     * Gets the maximum deuterium capacity for this fleet
     *
     * @return max deuterium capacity
     */
    public int getMaxDeuterium() {
        int res = 0;
        for (Ship s : ships) {
            res += s.getMaxDeuterium();
        }
        return res;

    }

    /**
     * Gets the current range of this fleet (how far it can go without refueling)
     *
     * @return range
     */
    public int getRange() {
        return (int) Math.floor((getDeuteriumLeft() / getDeuteriumUsage()));
    }

    /**
     * Checks if this fleet has a ship that can colonize a starsystem
     *
     * @return true if fleet has a colony ship
     */
    public boolean canColonize() {
        for (Ship s : ships) {
            if (s.canColonize()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this fleet has a ship that can mine asteroids
     *
     * @return true if fleet has a mining ship
     */
    public boolean canMine() {
        for (Ship s : ships) {
            if (s.canMine()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this fleet has a ship that can harvest deuterium
     *
     * @return true if fleet has a ship with bussard collectors AND cargo bay
     */
    public boolean canHarvestDeuterium() {
        for (Ship s : ships) {
            if (s.canHarvestDeuterium()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this fleet can do a planetary bombardment
     *
     * @return true if fleet can bomb planets
     */
    public boolean canBombPlanets() {
        for (Ship s : ships) {
            if (s.hasTorpedoLauncher()) {
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
        
        for (Ship s : ships) {
            if (s.hasTorpedoLauncher()) {
                res.addAll(s.getTorpedoLaunchers());
            }
        }
        return res;
    }

    /**
     * Checks if fleet has a ship that can do cargo transfers
     *
     * @return true if fleet can transfer cargo
     */
    public boolean canLoadUnloadCargo() {
        for (Ship s : ships) {
            if (s.canLoadUnloadCargo()) {
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
        int cargo = 0;
        for(Ship s : ships) {
            cargo += s.getMaxCargoSpace();
        }
        return cargo;
    }

    /**
     * Checks if fleet can build a starbase
     *
     * @return true if fleet can build a starbase
     */
    public boolean canBuildStarbase() {
        for (Ship s : ships) {
            if (s.canBuildStarbase()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the combined maximum shields strength for this fleet
     *
     * @return maximum shield strength
     */
    public int getMaxShields() {
        int shields = 0;
        for(Ship s : ships) {
            shields += s.getMaxShield();
        }
        return shields;
    }

    /**
     * Gets the combined shields strength for this fleet
     *
     * @return shield strength
     */
    public int getShields() {
        int shields = 0;
        for(Ship s : ships) {
            shields += s.getCurrentShieldStrength();
        }
        return shields;
    }

    /**
     * Gets the combined maximum armor strength for this fleet
     *
     * @return maximum armor strength
     */
    public int getMaxArmor() {
        int armor  = 0;
        for(Ship s : ships) {
            armor += s.getMaxArmor();
        }
        return armor;
    }

    /**
     * Gets the combined armor strength for this fleet
     *
     * @return armor strength
     */
    public int getArmor() {
        int armor  = 0;
        for(Ship s : ships) {
            armor += s.getCurrentArmorStrength();
        }
        return armor;
    }

    /**
     * Gets the combined maximum hitpoints for this fleet
     *
     * @return maximum hitpoints
     */
    public int getMaxHP() {
        int hp = 0;
        for(Ship s : ships) {
            hp += s.getMaxHitpoints();
        }
        return hp;
    }

    /**
     * Gets the combined hitpoints for this fleet
     *
     * @return hitpoints
     */
    public int getHP() {
        int hp = 0;
        for(Ship s : ships) {
            hp += s.getCurrentHullStrength();
        }
        return hp;
    }

    /**
     * Checks if any ship in the fleet can cloak
     *
     * @return true if has a ship that can cloak
     */
    public boolean canCloak() {
        for (Ship s : ships) {
            if (s.canCloak()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any ships in this fleet can use scanners
     *
     * @return true if a ship in the fleet can scan
     */
    public boolean canScan() {
        for (Ship s : ships) {
            if (s.canScan()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this fleet can use bussard collectors
     *
     * @return true if a ship in the fleet can use bussard collectors
     */
    public boolean canUseBussardCollector() {
        for (Ship s : ships) {
            if (s.canUseBussardCollector()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check is this fleet can transport troops
     *
     * @return true if a ship in this fleet can transport troops
     */
    public boolean canTransportTroops() {
        for (Ship s : ships) {
            if(s.hasTroopTransport()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the combined weapon strength of the entire flet
     *
     * @return weapon strength
     */
    public int getWeapons() {
        int wep = 0;
        for (Ship s : ships) {
            wep += s.getTotalWeaponStrength();
        }
        return wep;
    }

    /**
     * Gets the defensive rating (shields + armor) of the entire fleet
     *
     * @return defence strength
     */
    public int getDefence() {
        int def = 0;
        for (Ship s : ships) {
            def += s.getCurrentArmorStrength();
            def += s.getCurrentShieldStrength();
        }
        return def;
    }

    /**
     * Gets the sensor strength of this fleet
     *
     * @return sensor strength
     */
    public int getSensorStrength() {
        int max = 5;
        for(Ship s : ships) {
            if(s.getSensorStrength() > max) {
                max = s.getSensorStrength();
            }
        }
        return max;
    }

    /**
     * Gets the amount of troops this ship can carry
     *
     * @return troop capacity
     */
    public int getTroopCapacity() {
        int cap = 0;
        for (Ship s : ships) {
            cap += s.getTroopCapacity();
        }
        return cap;
    }

    /**
     * Gets the amount of troops currently on this ship
     *
     * @return number of troops
     */
    public int getTroops() {
        int troops = 0;
        for (Ship s : ships) {
            troops += s.getTroops();
        }
        return troops;
    }

    /**
     * Remove 1 troop from this fleet
     */
    public void decrementTroops() {
        for(Ship s : ships) {
            if(s.getTroops() > 0) {
                s.setTroops(s.getTroops() - 1);
                return;
            }
        }
    }

    /**
     * Gets the amount of cargo detuerium in this fleet
     *
     * @return deuterium amount
     */
    public int getCargoDeuterium() {
        int cargo = 0;
        for (Ship s : ships) {
            cargo += s.getCargoDeuterium();
        }
        return cargo;
    }

    /**
     * Gets the amount of cargo ore in this fleet
     *
     * @return deuterium amount
     */
    public int getCargoOre() {
        int cargo = 0;
        for (Ship s : ships) {
            cargo += s.getCargoOre();
        }
        return cargo;
    }

    /**
     * Gets a ship in this fleet by id
     *
     * @param id the id of the ship
     * @return the ship, or null if not found
     */
    public Ship getShipById(long id) {
        for (Ship s : ships) {
            if (s.getShipId() == id) {
                return s;
            }
        }
        return null;
    }
    
    /**
     * Checks if ANY ship in this fleet needs more crew
     * @return true if one or more ships needs more crew
     */
    public boolean needsMoreCrew() {
        for(Ship ship : ships) {
            if(ship.getCrew() < ship.getHullClass().getMaxCrew()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if ANY ship in this fleet needs to repair
     * @return true if fleet has a ship that needs to repair
     */
    public boolean needsRepair() {
        for(Ship ship : ships) {
            if(ship.getCurrentArmorStrength() < ship.getMaxArmor() ||
               ship.getCurrentHullStrength() < ship.getMaxHitpoints()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if any ships in this fleet needs to be refueled
     * @return true if any ship needs more fuel
     */
    public boolean needsRefuel() {
        return getDeuteriumLeft() < getMaxDeuterium();
    }

    /**
     * Repairs ALL the ships in this fleet (HP + armor), also regenerates shields
     * 
     * @param inFriendlySystem true if in own system.
     * @param hasShipyard true if system has a friendly shipyard
     * @param bonusesApply true if apply bonuses (false if in system with no power/industry, etc..)
     */
    public void repairShipsHullArmorShields( boolean inFriendlySystem, boolean hasShipyard, boolean bonusesApply) {
        int hullRepairPercentage = 2;
        int armorRepairPercentage = 3;
        int shieldRepairPercentage = 7;
        
        if(hasShipyard) {
            hullRepairPercentage += 10;
            armorRepairPercentage += 15;
        }
        if(bonusesApply) {
            hullRepairPercentage += (user.getHighestTech(TechnologyGenerator.techType.constructiontech).getLevel() / 2);
            hullRepairPercentage += (user.getFaction().getConstructionBonus() / 4);
            
            armorRepairPercentage += (user.getHighestTech(TechnologyGenerator.techType.constructiontech).getLevel() / 3);
            armorRepairPercentage += (user.getHighestTech(TechnologyGenerator.techType.weaponstech).getLevel() / 3);
            
            shieldRepairPercentage += (user.getHighestTech(TechnologyGenerator.techType.energytech).getLevel() / 2);
        }
        
        for(Ship s : ships) {
            // HULL
            int maxHPToRepair = s.getMaxHitpoints();
            if(!inFriendlySystem) {
                maxHPToRepair = maxHPToRepair / 5; // only repair hull to 20% while in space
            }
            if(s.getCurrentHullStrength() < maxHPToRepair) {
                int repairAmount = s.getMaxHitpoints() / 100;
                if(repairAmount < 1) { // always repair at least 'repairPercentage' hitpoints
                    repairAmount = 1;
                }
                repairAmount *= hullRepairPercentage;
                s.setCurrentHullStrength(s.getCurrentHullStrength() + repairAmount);
            }
            
            // ARMOR
            if(s.getMaxArmor() > 0) {
                int maxArmorToRepair = s.getMaxArmor();
                if(!inFriendlySystem) {
                    maxArmorToRepair = maxArmorToRepair / 4; // only repair armor to 25% while in space
                }
                if(s.getCurrentArmorStrength() < maxArmorToRepair) {
                    int repairAmount = s.getMaxArmor() / 100;
                    if(repairAmount < 1) { // always repair at least 'repairPercentage' armor
                        repairAmount = 1;
                    }
                    repairAmount *= armorRepairPercentage;
                    if(repairAmount < 1) {
                        repairAmount = 2;
                    }
                    s.setCurrentArmorStrength(s.getCurrentArmorStrength() + repairAmount);
                }
            }
            
            // SHIELDS
            if(s.getMaxShield() > 0) {
                double shieldRegen = s.getMaxShield() / 100.0D;
                if(shieldRegen < 5) {
                    shieldRegen = 5;
                }
                shieldRegen *= shieldRepairPercentage;
                s.setCurrentShieldStrength(s.getCurrentShieldStrength() + (int)shieldRegen);
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Fleet) {
            Fleet other = (Fleet)o;
            if(other.getName().equalsIgnoreCase(getName())  && getUser().equals(other.getUser())  ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.currentLocation != null ? this.currentLocation.hashCode() : 0);
        hash = 59 * hash + this.getName().hashCode();
        hash = 59 * hash + (this.user != null ? this.user.hashCode() : 0);
        return hash;
    }
}