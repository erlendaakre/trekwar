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
package com.frostvoid.trekwar.common.shipHulls;

import java.awt.Point;
import java.io.Serializable;

import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Defines a base Ship hull
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class HullClass implements Serializable {
    
    private String name;
    private String description;
    private String iconFileName;
    private String shipdesignerImageFileName;
    
    private ArrayList<Technology> techsRequired;
    private ArrayList<Faction> factions;
    private HashMap<Integer, Point> slotMap; // maps a slot location to a x,y coordinate on the image
    
    private int maxCrew;
    private int baseCost;
    private int baseHitpoints;
    private int baseArmor;
    private int baseManoeuvrability;
    private int baseDeuteriumStorage;
    private int baseDeuteriumUseage;
    
    private int slots;
    private boolean civillian;


    public HullClass(String name, String description, String iconFileName, String shipImageFileName, int maxCrew, int baseCost,
            int baseHitPoints, int baseArmor, int baseManoeuvrability, int slots, boolean civillian, int deuteriumStorage, int deuteriumUsage) {
        this.name = name;
        this.description = description;
        this.iconFileName = iconFileName;
        this.shipdesignerImageFileName = shipImageFileName;
        this.techsRequired = new ArrayList<Technology>();
        this.factions = new ArrayList<Faction>();
        this.slotMap = new HashMap<Integer, Point>(slots);
        this.maxCrew = maxCrew;
        this.baseCost = baseCost;
        this.baseHitpoints = baseHitPoints;
        this.baseArmor = baseArmor;
        this.baseManoeuvrability = baseManoeuvrability;
        this.slots = slots;
        this.civillian = civillian;
        this.baseDeuteriumStorage = deuteriumStorage;
        this.baseDeuteriumUseage = deuteriumUsage;
    }


    public void addTechRequirement(Technology... techs) {
        for(Technology t : techs) {
            techsRequired.add(t);
        }
    }


    public int getBaseArmor() {
        return baseArmor;
    }


    public int getBaseCost() {
        return baseCost;
    }


    public int getBaseHitpoints() {
        return baseHitpoints;
    }


    public int getBaseMaintenanceCost() {
        return baseCost / 300;
    }

    public int getBaseManoeuvrability() {
        return baseManoeuvrability;
    }


    public boolean isCivillian() {
        return civillian;
    }


    public String getDescription() {
        return description;
    }


    public String getIconFileName() {
        return iconFileName;
    }
    
    public String getShipdesignerImageFileName() {
        return shipdesignerImageFileName;
    }


    public int getMaxCrew() {
        return maxCrew;
    }

    public String getName() {
        return name;
    }


    public int getSlots() {
        return slots;
    }
    
    public void addFaction(Faction... factionsToAdd) {
        for(Faction f : factionsToAdd) {
            this.factions.add(f);
        }
    }
    
    public HashMap<Integer, Point> getSlotMap() {
        return slotMap;
    }
    public void setSlotMapPoint(int num, Point point) {
        slotMap.put(num, point);
    }
    
    public ArrayList<Faction> getFactions() {
        return factions;
    }
    
    public boolean canUse(Faction f) {
        return factions.contains(f);
    }
    
    public boolean canUse(User u) {
        for (Technology shipTech : techsRequired) {
            if (!u.getTechs().contains(shipTech)) {
                return false;
            }
        }
        return true;
    }


    public ArrayList<Technology> getTechsRequired() {
        return techsRequired;
    }
    
    public boolean equals(HullClass other) {
        return other.getName().equals(this.name);
    }


    public int getBaseDeuteriumStorage() {
        return baseDeuteriumStorage;
    }


    public int getBaseDeuteriumUseage() {
        return baseDeuteriumUseage;
    }
    
    public static HullClass getHullClassByName(String name) {
        for(HullClass h : StaticData.allHullClasses) {
            if(h.getName().equalsIgnoreCase(name))
                return h;
        }
        return null;
    }
    
}