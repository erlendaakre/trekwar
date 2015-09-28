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

import java.io.*;

/**
 * A faction that users can play as
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Faction implements Serializable {

    private static final long serialVersionUID = 1271624999837810660L;
    protected String name;
    protected String description;

    protected int researchBonus;
    protected int shipCombatBonus;
    protected int groundCombatBonus;
    protected int moraleBonus;
    protected int constructionBonus;
    protected int terraformingAndColonizationBonus;

    /**
     * Creates a new faction, this is done only once and only references to these global faction objects are used
     *
     * @param name the name of the faction
     * @param description a textual description
     * @param researchBonus the bonus/penalty for research
     * @param shipCombatBonus the bonus/penalty for ship combat
     * @param groundCombatBonus the bonus/penalty for ground combat
     * @param moraleBonus the bonus/penalty for morale
     * @param constructionBonus the bonus/penalty for starship/structure construction
     * @param terraformingAndColonizationBonus the bonus/penalty for terraforming/colonization
     */
    public Faction(String name, String description, int researchBonus, int shipCombatBonus,
            int groundCombatBonus, int moraleBonus, int constructionBonus, int terraformingAndColonizationBonus) {
        this.name = name;
        this.description = description;
        this.researchBonus = researchBonus;
        this.shipCombatBonus = shipCombatBonus;
        this.groundCombatBonus = groundCombatBonus;
        this.moraleBonus = moraleBonus;
        this.constructionBonus = constructionBonus;
        this.terraformingAndColonizationBonus = terraformingAndColonizationBonus;
    }

    /**
     * Gets the name of this faction
     *
     * @return faction name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the description of this faction
     *
     * @return faction description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets construction bonus
     *
     * @return construction bonus
     */
    public int getConstructionBonus() {
        return constructionBonus;
    }

    /**
     * Gets ground combat bonus
     *
     * @return ground combat bonus
     */
    public int getGroundCombatBonus() {
        return groundCombatBonus;
    }

    /**
     * Gets morale bonus
     *
     * @return morale bonus
     */
    public int getMoraleBonus() {
        return moraleBonus;
    }

    /**
     * Gets research bonus
     *
     * @return research bonus
     */
    public int getResearchBonus() {
        return researchBonus;
    }

    /**
     * Gets ship combat bonus
     *
     * @return ship combat bonus
     */
    public int getShipCombatBonus() {
        return shipCombatBonus;
    }

    /**
     * Gets terraforming and colonization bonus
     *
     * @return colonization and terraformning bonus
     */
    public int getTerraformingAndColonizationBonus() {
        return terraformingAndColonizationBonus;
    }

    /**
     * Computes hash code
     *
     * @return hash code
     */
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * Compares two factions based on name
     *
     * @param faction the faction to compare with
     * @return true if factions are the sanem
     */
    public boolean equals(Faction faction) {
        return faction.getName().equals(this.name);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Faction)
            return equals((Faction)o);
        return false;
    }
}
