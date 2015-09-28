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
 * This is the super class for everything that exists in space
 * (ships, starsystems and starbases)
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SpaceObject implements Serializable {

    protected User user; // owner
    protected String name; // name of object
    protected SpaceObjectClassification classification; // classification
    protected int sensorCost; // cost of sensors to view area, open space = 10, nebula = 25, etc..
    protected int x; // x coordinate for this object
    protected int y; // y coordinate for this ojbect

    /**
     * Creates a new space object
     *
     * @param user           the owner of the object
     * @param name           the name of the object
     * @param classification the classification of the boject
     * @param x              the x location
     * @param y              the y location
     */
    public SpaceObject(User user, String name, SpaceObjectClassification classification, int x, int y) {
        this.user = user;
        this.name = name;
        this.classification = classification;
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the classification for this object
     *
     * @return classification
     */
    public SpaceObjectClassification getClassification() {
        return classification;
    }

    /**
     * Gets the name of this object
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this object
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the owner of this object
     *
     * @param user owner
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the owner of this object
     *
     * @return the owner
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the X location of this object
     *
     * @return x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the Y location of this object
     *
     * @return y coordinate
     */
    public int getY() {
        return y;
    }
}