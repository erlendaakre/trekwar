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
package com.frostvoid.trekwar.server.utils;

import java.util.ArrayList;
import com.frostvoid.trekwar.common.Galaxy;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.User;

/**
 * Methods for doing stuff on the map (sensors, line of sight, etc..)
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MapTools {

    
    /**
     * Does Line Of Sight for a starsystem, ship or other space entity
     *
     * @param user the user to do LOS for (updates this user fog of war)
     * @param sourceX the source x coordinate
     * @param sourceY the source y coordinate
     * @param sensorStrength the sensor strength
     */
    public static void doLOS(Galaxy galaxy, User user, int sourceX, int sourceY, int sensorStrength) {
        ArrayList<StarSystem> boundingBox = getBoundingBox(galaxy.getMap(), galaxy.getMap()[sourceX][sourceY], sensorStrength/10);

        // ALWAYS remove fog of war in adjacent spaces
        ArrayList<StarSystem> box = getBoundingBox(galaxy.getMap(), galaxy.getMap()[sourceX][sourceY], 1);
        for (StarSystem s : box) {
            updateTileSensorStrength(user, s.getX(), s.getY(), -1);
        }

        // ALWAYS remove fog of war AND show tile, for the starsystem the sensor source is in
        updateTileSensorStrength(user, sourceX, sourceY, 0);

        BresenhamLine b = new BresenhamLine();
        for (StarSystem s : boundingBox) {
            b.plot(sourceX, sourceY, s.getX(), s.getY());
            int strength = sensorStrength;
            updateTileSensorStrength(user, sourceX, sourceY, strength - galaxy.getMap()[sourceX][sourceY].getSensorCost());
            strength -= galaxy.getMap()[sourceX][sourceY].getSensorCost();
            
            while (b.next()) {
                strength -= galaxy.getMap()[b.getX()][b.getY()].getSensorCost();
                if (strength <= 0) {
                    break;
                }
                else {
                    updateTileSensorStrength(user, b.getX(), b.getY(), strength);
                }
            }
        }
    }
    
    /**
     * Updates the sensor strength of a tile, IF new value is higher than existing one
     * 
     * @param user the user to update sensor strength map for
     * @param x tile x coordinate
     * @param y tile y coordinate
     * @param strength sensor strength for tile
     */
    private static void updateTileSensorStrength(User user, int x, int y,  int strength) {
        user.getSensorOverlay()[x][y] = Math.max(user.getSensorOverlay()[x][y], strength);
    }

    /**
     * Gets all the system as a bounding box from a specific game tile
     * Used by the LineOfSight method (doLOS(...))
     *
     * @param map the map
     * @param s the starsystem (sensor source)
     * @param r the radius of the box (from the source)
     *
     * @return A list of all systems that make up the bounding box
     */
    public static ArrayList<StarSystem> getBoundingBox(StarSystem[][] map, StarSystem s, int r) {
        ArrayList<StarSystem> bounds = new ArrayList<StarSystem>();

        // top
        int x = s.getX() - r;
        if(x < 0)
            x = 0;
        int y = s.getY() - r;
        if(y < 0)
            y = 0;
        while (x < s.getX() + r && x < map.length) {
            bounds.add(map[x][y]);
            x += 1;
        }

        //left
        x = s.getX() + r;
        if(x >= map.length)
            x = map.length-1;
        y = s.getY() - r;
        if(y < 0)
            y = 0;
        while (y < s.getY() + r && y < map[x].length) {
            bounds.add(map[x][y]);
            y += 1;
        }

        // bottom
        x = s.getX() - r;
        if(x < 0)
            x = 0;
        y = s.getY() + r;
        if(y >= map[x].length)
            y = map[x].length-1;
        while (x <= s.getX() + r && x < map.length) {
            bounds.add(map[x][y]);
            x += 1;
        }

        //right
        x = s.getX() - r;
        if(x < 0)
            x = 0;
        y = s.getY() - r;
        if(y < 0)
            y = 0;
        while (y < s.getY() + r && y < map[x].length) {
            bounds.add(map[x][y]);
            y += 1;
        }
        return bounds;
    }
}
