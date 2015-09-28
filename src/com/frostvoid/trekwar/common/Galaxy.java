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

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.exceptions.AddUserException;
import com.frostvoid.trekwar.common.exceptions.UserNotFoundException;
import com.frostvoid.trekwar.common.utils.Language;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * A huge super awesome object that holds all the static stuff (structures, hulls, technologies)
 * But also the live data  like map (with systems and fleets/ships) and users at server level.
 * One galaxy object represents one Trekwar game
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Galaxy implements Serializable {

    private StarSystem[][] map;
    private ArrayList<User> users;
    private ArrayList<User> loggedInUsers; // TODO move to serveR?
    public int maxUsersInGalaxy;
    public long turnSpeed; // in milliseconds
    public boolean executingTurn = false; // blocks client commands when doing turn execution TODO move to server
    private long currentTurn;
    public long lastTurnDate; // unix time
    public long nextTurnDate; // unix time

    /**
     * Create a new galaxy
     *
     * @param map       the actual game map
     * @param turnSpeed time between turns (in seconds)
     * @param maxUsers  maximum number of users allowed
     */
    public Galaxy(StarSystem[][] map, long turnSpeed, int maxUsers) {
        this.map = map;
        this.turnSpeed = turnSpeed * 1000;
        this.maxUsersInGalaxy = maxUsers;

        this.currentTurn = 0;


        users = new ArrayList<User>();
        users.add(StaticData.nobodyUser);

        loggedInUsers = new ArrayList<User>();
    }

    /**
     * This method is called every time a galaxy object is started
     * (deserialized from disk).
     */
    public void startup() {
        lastTurnDate = System.currentTimeMillis();
        nextTurnDate = lastTurnDate + turnSpeed;
        loggedInUsers.clear();
    }

    /**
     * Deletes a user from the game
     *
     * @param username the user to delete
     */
    public void delUser(String username) throws UserNotFoundException {
        if (username.equals("nobody")) {
            throw new UserNotFoundException("\"nobody\" is a special user, can not delete");
        }
        User u = getUser(username);

        // Do we ever need to delete users?

        // if so, what should be done with users structures?

        // TODO
        // Remove ALL the users SHIPS TODO
        // Change the owner of users planets, all systems TODO
        // MAKE SURE THERE ARE NO REFERENCES LEFT!!!! TODO TODO TODO
        users.remove(u);
    }

    /**
     * Gets the user object when a username is given
     *
     * @param username which user object to get
     * @return the appropriate user object
     * @throws UserNotFoundException if the user is not found
     */
    public User getUser(String username) throws UserNotFoundException {
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        throw new UserNotFoundException();
    }

    /**
     * Adds a new user to this galaxy/game
     *
     * @param u the new user
     * @throws AddUserException if user could not be added
     */
    public synchronized void addUser(User u) throws AddUserException {

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(u.getUsername())) {
                throw new AddUserException("That username allready exists");
            }
        }
        // username is unique
        // TODO check if user limit is reached
        // TODO check if startup system can be found for user
        users.add(u);
    }

    /**
     * Gets all the users in this galaxy
     *
     * @return all users
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Counts the number of users in this galaxy
     *
     * @return user count
     */
    public int getUserCount() {
        return users.size();
    }

    /**
     * Gets a system by coordinates
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return the starsystem at given coordinates, or null if invalid coordinates
     */
    public StarSystem getSystem(int x, int y) {
        if (x < map.length && y < map[0].length && x >= 0 && y >= 0) {
            return map[x][y];
        }
        return null;
    }

    /**
     * Gets a starsystem by fleet
     *
     * @param f the fleet to get the starsystem for
     * @return the starsystem holding the specified fleet
     */
    public StarSystem getSystem(Fleet f) {
        return getSystem(f.getX(), f.getY());
    }

    /**
     * Gets the map matrix
     *
     * @return the map
     */
    public StarSystem[][] getMap() {
        return map;
    }

    /**
     * Gets the width (in tiles) of the map
     *
     * @return map width
     */
    public int getWidth() {
        return map.length;
    }

    /**
     * Gets the height (in tiles) of the map
     *
     * @return map height
     */
    public int getHeight() {
        return map[0].length;
    }

    /**
     * Gets the maximum number of users allowed in this galaxy
     *
     * @return maximum number of users
     */
    public int getMaxUsers() {
        return maxUsersInGalaxy;
    }

    /**
     * Adds a user to list of logged in users
     *
     * @param u the user to add
     */
    public void loginUser(User u, String ipString) {
        TurnReportItem tr = new TurnReportItem(currentTurn, -1, -1, TurnReportItem.TurnReportSeverity.HIGH);
        tr.setSummary(Client.getLanguage().get("turn_report_login1"));
        tr.setDetailed(Language.pop(Client.getLanguage().get("turn_report_login2"), new Date(), ipString));
        u.addTurnReport(tr);
        loggedInUsers.add(u);
    }

    /**
     * Removes a user form the list of logged in users
     *
     * @param u the user to remove
     */
    public void logoutUser(User u) {
        loggedInUsers.remove(u);
    }

    /**
     * Gets a list of currently logged in users
     *
     * @return logged in users
     */
    public ArrayList<User> getLoggedInUsers() {
        return loggedInUsers;
    }

    /**
     * Gets the current turn number for this galaxy
     *
     * @return turn number
     */
    public long getCurrentTurn() {
        return currentTurn;
    }

    public void incrementCurrentTurn() {
        currentTurn++;
    }


    public void setExecutingTurn(boolean executingTurn) {
        this.executingTurn = executingTurn;
    }

    public boolean getExecutingTurn() {
        return executingTurn;
    }

    public long getTurnSpeed() {
        return turnSpeed;
    }
}