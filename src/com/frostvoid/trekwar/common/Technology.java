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

/**
 * This represents a single technology, that can be researched by a user.
 * Technologies give access to new hulls/components/structures
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Technology implements Serializable {

    private TechnologyGenerator.techType type;
    private int level;
    private int researchCost;
    private String name;
    private String description;

    /**
     * Creates a new technology, all technologies are created only once, and users only
     * have references to these "global" technologies
     *
     * @param type the technology type
     * @param level the level (1-12) of this tech, higher is more advanced
     * @param researchCost the cost in reasearch points to discover this technology
     * @param name the name of the technology
     * @param description the description of this technology
     */
    public Technology(TechnologyGenerator.techType type, int level, int researchCost, String name, String description) {
        this.type = type;
        this.level = level;
        this.researchCost = researchCost;
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the technology level of this technology
     *
     * @return the technology level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets the cost in research points required to discover this technology
     *
     * @return the research cost
     */
    public int getResearchCost() {
        return researchCost;
    }

    /**
     * Gets the name of this technology
     *
     * @return technology name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the technology description
     *
     * @return technology description
     */
    public String getDesscription() {
        return description;
    }

    /**
     * Gets the technology type
     *
     * @return technology type
     */
    public TechnologyGenerator.techType getType() {
        return type;
    }

    /**
     * Gets text representation of this technology
     *
     * @return the technology type and level
     */
    @Override
    public String toString() {
        return type + "" + level;
    }

    /**
     * Computes a hashcode for this technology
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.level;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    /**
     * Compared two technologies based on name and level
     *
     * @param t2 the technology to check against
     * @return true if the technologies are equal
     */
    public boolean equals(Technology t2) {
        if (t2.getName().equals(name) && t2.getLevel() == level) {
            return true;
        }
        return false;
    }

    public boolean equals(Object o) {
        if(o instanceof Technology) {
            return equals((Technology)o);
        }
        return false;
    }
}
