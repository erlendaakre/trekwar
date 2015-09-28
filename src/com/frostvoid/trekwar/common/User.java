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

import java.io.Serializable;

import com.frostvoid.trekwar.common.structures.Structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.frostvoid.trekwar.common.exceptions.NotUniqueException;
import com.frostvoid.trekwar.common.shipComponents.ShipComponent;
import com.frostvoid.trekwar.common.shipHulls.HullClass;

/**
 * This class represents a user of the game
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class User implements Serializable {
    private static final long serialVersionUID = -8069793158507319037L;

    private String username;
    private String password;
    private String avatarFilename;
    private ArrayList<ChatLine> chat;
    private ArrayList<TurnReportItem> turnReport;
    private long points; // points.. for highscore
    private Faction faction;
    private int[][] sensorOverlay;
    private ArrayList<ShipTemplate> shipTemplates;
    private ArrayList<StarSystem> starSystems;
    private ArrayList<Fleet> fleets;
    private int researchPoints; // spent to gain techlevel, = resets to 0 when spent
    private ArrayList<Technology> techs;
    private Technology currentResearch;
    private int nextShipId = 1;

    /**
     * Constructor to make a new user
     * 
     * @param name the username
     * @param password the password
     * @param faction the faction object
     */
    public User(String name, String password, Faction faction) {
        this.username = name;
        this.password = password;
        chat = new ArrayList<ChatLine>();
        turnReport = new ArrayList<TurnReportItem>();
        points = 0;
        this.faction = faction;

        this.sensorOverlay = new int[2][2];

        starSystems = new ArrayList<StarSystem>();
        fleets = new ArrayList<Fleet>();
        shipTemplates = new ArrayList<ShipTemplate>();
        researchPoints = 0;
        techs = new ArrayList<Technology>();
    }

    /**
     * Checks if two user objects are the same (based on username)
     *
     * @param u2 the user to check against
     * @return true if usernames are identical
     */
    public boolean equals(User u2) {
        return this.username.equals(u2.getUsername());
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof User)
            return equals((User)o);
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.username != null ? this.username.hashCode() : 0);
        hash = 41 * hash + (this.password != null ? this.password.hashCode() : 0);
        return hash;
    }

    /**
     * Sets the users sensor overlay / fog of war
     *
     * @param sensorOverlay a matrix with sensor strenght values
     */
    public void setSensorOverlay(int[][] sensorOverlay) {
        this.sensorOverlay = sensorOverlay;
    }

    /**
     * Gets the sensor overlay
     *
     * @return the sensor overlay matrix
     */
    public int[][] getSensorOverlay() {
        return sensorOverlay;
    }

    /**
     * Gets the username
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the player faction
     *
     * @return the faction
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Gets the players score
     *
     * @return the score
     */
    public long getPoints() {
        return points;
    }

    /**
     * Adds points to this users highscore
     *
     * @param addition score to add
     */
    public void addPoints(int addition) {
        points += addition;
    }

    /**
     * Gets a list of starsystems currently owned by this player
     *
     * @return players starsystems
     */
    public ArrayList<StarSystem> getStarSystems() {
        return starSystems;
    }

    /**
     * Adds a starsystem to the list of starsystems owned by player
     *
     * @param starSystem Starsystem to add
     */
    public void addSystem(StarSystem starSystem) {
        starSystems.add(starSystem);
    }

    /**
     * Removes a starsystem from the list of starsystems owned by player
     *
     * @param starSystem Starsystem to remove
     */
    public void removeSystem(StarSystem starSystem) {
        starSystems.remove(starSystem);
    }

    /**
     * Gets a list of the fleets owned by this player
     *
     * @return players fleets
     */
    public ArrayList<Fleet> getFleets() {
        return fleets;
    }

    /**
     * Add a fleet to the list of player owned fleets
     *
     * @param fleet the fleet to add
     * @throws NotUniqueException if the name of the new fleet is not unique for this player
     */
    public void addFleet(Fleet fleet) throws NotUniqueException {
        for (Fleet f : fleets) {
            if (f.getName().equalsIgnoreCase(fleet.getName())) {
                throw new NotUniqueException("fleet name");
            }
        }
        fleets.add(fleet);
    }

    /**
     * Removes a fleet from the list of user owned fleets
     *
     * @param fleet
     */
    public void removeFleet(Fleet fleet) {
        fleets.remove(fleet);
    }

    /**
     * Gets the current amount of research points this user has
     *
     * @return research points
     */
    public int getResearchPoints() {
        return researchPoints;
    }

    /**
     * sets the research points for this user
     *
     * @param researchPoints new amount of research points
     */
    public void setResearchPoints(int researchPoints) {
        this.researchPoints = researchPoints;
        if(this.researchPoints < 0) {
            this.researchPoints = 0;
        }
    }

    /**
     * Gets the technology currently being researched by this user
     *
     * @return current research goal
     */
    public Technology getCurrentResearch() {
        return currentResearch;
    }

    /**
     * Sets what technology this user should research
     *
     * @param currentResearch the technology to research
     */
    public void setCurrentResearch(Technology currentResearch) {
        this.currentResearch = currentResearch;
    }

    /**
     * Gets the highest technology for a given technology type
     *
     * @param type which type to get highest tech for
     * @return the highest technology found
     */
    public Technology getHighestTech(TechnologyGenerator.techType type) {
        Technology result = TechnologyGenerator.getTech(type, 0);
        for (Technology t : techs) {
            if (t.getType() == type && t.getLevel() > result.getLevel()) {
                result = t;
            }
        }
        return result;
    }

    /**
     * Adds a technology to this user
     *
     * @param tech the technology to add
     */
    public void addTech(Technology tech) {
        if(!techs.contains(tech)) {
            techs.add(tech);
        }
    }

    /**
     * Gets a list of all the technologies this user has
     *
     * @return all technologies
     */
    public ArrayList<Technology> getTechs() {
        return techs;
    }

    /**
     * Calculates and returns the total research output for this user
     * includes research bonuses from (faction)
     *
     * @return research output per turn
     */
    public int getResearchOutput() {
        int research = 0;
        for (StarSystem s : starSystems) {
            research += s.getSystemResearchSurplus();
        }
        research += getResearchBonus();

        // prevent negative research
        if (research < 5) {
            research = 5 + faction.getResearchBonus();
        }

        // prevent bad faction research bonus from generating negative research
        if (research < 0) {
            research = 5;
        }

        return research;
    }
    
    public int getResearchBonus() {
        int research = 0;
        for (StarSystem s : starSystems) {
            research += s.getSystemResearchSurplus();
        }
        double bonus = ((research / 100D) * getFaction().getResearchBonus());
        return (int)Math.ceil(bonus);
    }

    /**
     * Gets all ship templates designed by this user
     *
     * @return all templates
     */
    public ArrayList<ShipTemplate> getShipTemplates() {
        return shipTemplates;
    }

    /**
     * Adds a template to this users list of ship templates
     *
     * @param template the tempalte to add
     */
    public synchronized void addShipTemplate(ShipTemplate template) {
        shipTemplates.add(template);
    }

    /**
     * Removes a template from this users list of ship templates
     *
     * @param template the template to remove
     */
    public synchronized void removeShipTemplate(ShipTemplate template) {
        shipTemplates.remove(template);
    }

    /**
     * Gets a ship template by name
     *
     * @param name the name of the template to get
     * @return the template, or null if not found
     */
    public synchronized ShipTemplate getShipTemplate(String name) {
        for (ShipTemplate s : shipTemplates) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Gets the total deuterium stored in this users starsystems
     *
     * @return deteurium amount
     */
    public int getDeuterium() {
        int deuterium = 0;
        for (StarSystem s : starSystems) {
            deuterium += s.getDeuterium();
        }
        return deuterium;
    }

    /**
     * Gets the max deuterium amount for all the users starsystems combined
     *
     * @return max deuterium capacity
     */
    public int getMaxDeuterium() {
        int max = 0;
        for (StarSystem s : starSystems) {
            max += s.getMaxDeuterium();
        }
        return max;
    }
    
    /**
     * Gets the total ship upkeep supply generated by systems/structures for this user
     * @return total upkeep supply (per turn)
     */
    public int getShipUpkeepSupply() {
        int upkeep = StaticData.SHIP_UPKEEP_BASE_VALUE;
        for(StarSystem s : starSystems) {
            upkeep += s.getShipUpkeepContribution();
        }
        return upkeep;
    }
    
    /**
     * Gets the total ship upkeep used by this users ships
     * @return total upkeep used (per turn)
     */
    public int getShipUpkeepUsed() {
        int upkeep = 0;
        for(Fleet f : fleets) {
            for(Ship s : f.getShips()) {
                upkeep += s.getUpkeepCost();
            }
        }
        return upkeep;
    }
    
    /**
     * Gets the ship upkeep surplus for this user
     * @return ship upkeep surplus
     */
    public int getShipUpkeepSurplus() {
        return getShipUpkeepSupply() - getShipUpkeepUsed();
    }
    
    /**
     * Checks if this user currently has a upkeep penalty
     * @return true if user has upkeep penalty
     */
    public boolean hasUpkeepPenalty() {
        return getShipUpkeepSurplus() < 0;
    }

    /**
     * Gets all the base hulls this user has discovered
     *
     * @return all hull classes
     */
    public ArrayList<HullClass> getAvailableShipHulls() {
        ArrayList<HullClass> hulls = new ArrayList<HullClass>();

        for (HullClass h : StaticData.allHullClasses) {
            if (h.canUse(faction) && h.canUse(this)) {
                hulls.add(h);
            }
        }
        return hulls;
    }

    /**
     * Returns the hulls the user would gain from researching a specific technology
     *
     * @param technology the technology
     * @return hulls gained by researching the technology
     */
    public ArrayList<HullClass> getHullsFromResearchingTechnolog(Technology technology) {
        ArrayList currentHulls = getAvailableShipHulls();
        addTech(technology);
        ArrayList<HullClass> hullsWithNewTech = getAvailableShipHulls();
        techs.remove(technology);

        hullsWithNewTech.removeAll(currentHulls);
        return hullsWithNewTech;
    }

    /**
     * Returns all the ship components available to this user
     *
     * @return all components
     */
    public ArrayList<ShipComponent> getAvailableShipComponents() {
        ArrayList<ShipComponent> components = new ArrayList<ShipComponent>();
        for (ShipComponent c : StaticData.allShipComponents) {
            if (c.canUse(faction) && c.canUse(this)) {
                components.add(c);
            }
        }
        return components;
    }

    /**
     * Returns all the structures this user can build
     *
     * @return all structures
     */
    public ArrayList<Structure> getAvailableStructures() {
        ArrayList<Structure> structures = new ArrayList<Structure>();
        for (Structure s : StaticData.allStructures) {
            if (s.canUse(faction) && s.canUse(this)) {
                structures.add(s);
            }
        }
        return structures;
    }

    /**
     * Returns the ship components that the user would gain from researching a specific technology
     *
     * @param technology the technology
     * @return the components gained by researching technology
     */
    public ArrayList<ShipComponent> getShipComponentsFromResearchingTechnolog(Technology technology) {
        ArrayList currentComponents = getAvailableShipComponents();
        addTech(technology);
        ArrayList<ShipComponent> componentsWithNewTech = getAvailableShipComponents();
        techs.remove(technology);

        componentsWithNewTech.removeAll(currentComponents); // get complement
        return componentsWithNewTech;
    }

    /**
     * Gets this users avatar image filename
     *
     * @return the avatar filename
     */
    public String getAvatarFilename() {
        return avatarFilename;
    }

    /**
     * Sets this users avatar image filename, just filename (file must be in graphics/avatars/)
     *
     * @param avatarFilename the avatar filename
     */
    public void setAvatarFilename(String avatarFilename) {
        this.avatarFilename = avatarFilename;
    }

    /**
     * Adds a single line of text from chat to this user
     * @param chatline the line to add
     */
    public synchronized void addChat(ChatLine chatline) {
        chat.add(chatline);
    }

    /**
     * Gets all the chatlines this user has received
     *
     * @return the chat lines
     */
    public synchronized ArrayList<ChatLine> getChat() {
        return chat;
    }

    /**
     * Adds a turn report item to this users list of turn reports
     *
     * @param item The turn report item to add
     */
    public void addTurnReport(TurnReportItem item) {
        turnReport.add(item);
        if(turnReport.size() > 100) {
            turnReport.remove(0);
        }
    }

    /**
     * Gets the latest 100 turn reports for this users
     *
     * @return a list of turn report items
     */
    public ArrayList<TurnReportItem> getTurnReports() {
        Collections.sort(turnReport, new Comparator<TurnReportItem>() {
            @Override
            public int compare(TurnReportItem o1, TurnReportItem o2) {
                if(o1.getTurn() < o2.getTurn()) {
                    return 1;
                }
                else if(o1.getTurn() > o2.getTurn()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        return turnReport;
    }

    /**
     * Checks if a fleet name is available (not already used by this user)
     *
     * @param name the name to check
     *
     * @return true if name is not already used, false otherwise
     */
    public boolean isFleetNameAvailable(String name) {
        for (Fleet f : fleets) {
            if (f.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets a fleet owned by this user by name
     *
     * @param fleetName the name of the fleet
     * @return the fleet, or null if not found
     */
    public Fleet getFleetByName(String fleetName) {
        for (Fleet f : fleets) {
            if (f.getName().equalsIgnoreCase(fleetName)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Generates the next unique ship id for this user
     *
     * @return a unique ship id
     */
    public int getNextShipId() {
        return nextShipId++;
    }

    /**
     * Clears all the sensor strength values for this user,
     * called each turn before doing LOS on all systems/ships
     */
    public void clearSensorStrenghts() {
        for(int i = 0; i < sensorOverlay.length; i++) {
            for(int j = 0; j < sensorOverlay[i].length; j++) {
                // set all explored tiles to 0
                if(sensorOverlay[i][j] > Integer.MIN_VALUE) {
                    sensorOverlay[i][j] = 0;
                }
            }
        }
    }
}
