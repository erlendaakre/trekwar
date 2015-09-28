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
package com.frostvoid.trekwar.server;

import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.Galaxy;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.User;
import com.frostvoid.trekwar.common.exceptions.AddUserException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Console application to generate a new galaxy / game
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MakeGalaxy {

    public static void main(String[] args) {
        int size = -1;
        int speed = -1;
        int maxUsers = -1;
        int starCountPercent = -1;
        int asteroidFieldCount = -1;
        int nebulaCount = -1;
        ArrayList<User> users = new ArrayList<User>();
        String filename = "gal.dat";

        Scanner input = new Scanner(System.in);
        System.out.println("Trekwar Make Galaxy " + TrekwarServer.VERSION);
        System.out.println("=========================");

        // Used for testing
        if (args.length == 1 && args[0].equals("default")) {
            size = 80;
            speed = 30;
            maxUsers = 5;
            starCountPercent = 4;
            asteroidFieldCount = 6;
            nebulaCount = 6;
            users.add(getUser(users, "x f klogd klogd"));
            users.add(getUser(users, "x k gowron gowron"));
            users.add(getUser(users, "x f picard picard"));
            users.add(getUser(users, "x c dukat dukat"));

            listParams(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, users.size());
            if (generateGalaxy(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, filename, users)) {
                System.exit(0);
            } else {
                System.out.println("ERROR: unable to generate galaxy");
                System.exit(1);
            }
        }

        String cmd = "";
        while (!cmd.equalsIgnoreCase("exit")) {
            System.out.print("> ");
            cmd = input.nextLine();
            if (cmd.equals("help")) {
                showHelp();
            } else if (cmd.startsWith("size")) {
                size = getSize(cmd);
            } else if (cmd.startsWith("maxuser")) {
                maxUsers = getMaxUser(cmd);
            } else if (cmd.startsWith("stars")) {
                starCountPercent = getStars(cmd);
            } else if (cmd.startsWith("speed")) {
                speed = getSpeed(cmd);
            } else if (cmd.startsWith("asteroids")) {
                asteroidFieldCount = getAsteroids(cmd);
            } else if (cmd.startsWith("nebulas")) {
                nebulaCount = getNebulas(cmd);
            } else if (cmd.startsWith("adduser")) {
                User u = getUser(users, cmd);
                if (users.size() >= maxUsers) {
                    System.out.println("ERROR: max users set to: " + maxUsers + " change this to add more users");
                } else {
                    if (u != null) {
                        users.add(u);
                    }
                }
            } else if (cmd.equals("list")) {
                listParams(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, users.size());
            } else if (cmd.equals("default")) {
                size = 40;
                speed = 60;
                maxUsers = 5;
                starCountPercent = 4;
                asteroidFieldCount = 6;
                nebulaCount = 6;
                System.out.println("Default values set (type list to show), add users and save");
            } else if (cmd.equals("save")) {
                listParams(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, users.size());
                if (users.isEmpty()) {
                    System.out.println("ERROR: No users added, add some before saving");

                } else {
                    System.out.print("Filename: ");
                    filename = input.nextLine();

                    File f = new File(filename);
                    if (!f.exists()) {
                        if (generateGalaxy(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, filename, users)) {
                            System.exit(0);
                        }
                    } else {
                        System.out.println("WARNING: file already exist: " + f.getAbsolutePath());
                        System.out.print("Enter \"yes\" to overwrite:");
                        String overwrite = input.nextLine();
                        if (overwrite.equalsIgnoreCase("yes")) {
                            if (generateGalaxy(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, filename, users)) {
                                System.exit(0);
                            }
                        }
                    }
                }
            } else if (cmd.equals("new")) {
                interactive();
            } else if (cmd.equals("clear")) {
                for (int i = 0; i < 100; i++) {
                    System.out.println();
                }
            } else if (cmd.equals("")) {
            } else if (cmd.equals("exit") || cmd.equals("quit")) {
                System.exit(0);
            } else {
                System.out.println("Invalid command: \"" + cmd + "\", type \"help\" for help");
            }
        }
    }

    private static void interactive() {
        // Achievement unlocked: Stupid redundant code :)
        int size = -1;
        int speed = -1;
        int maxUsers = -1;
        int starCountPercent = -1;
        int asteroidFieldCount = -1;
        int nebulaCount = -1;
        String filename = "gal.dat";

        Scanner input = new Scanner(System.in);

        System.out.println("Interactive mode!  as seen on TV!");
        System.out.println("press return to accept default value");
        String cmd = "";

        while (size < 15) {
            System.out.print("Enter size (default 40, minimum 15): ");
            cmd = input.nextLine();
            if (cmd.equals("")) {
                cmd = "40";
            }
            size = getSize("x " + cmd);
        }
        while (speed < 30) {
            System.out.print("Enter speed in seconds (default 60, minimum 30): ");
            cmd = input.nextLine();
            if (cmd.equals("")) {
                cmd = "60";
            }
            speed = getSpeed("x " + cmd);
        }
        while (maxUsers <= 0) {
            System.out.print("Enter maximum number of users (recommended " + ((size * size) / 300) + ", minimum 1): ");
            cmd = input.nextLine();
            if (cmd.equals("")) {
                cmd = "" + ((size * size) / 300);
            }
            maxUsers = getMaxUser("x " + cmd);
        }
        while (starCountPercent <= 0 || starCountPercent > 30) {
            System.out.println("Enter star density in percent (between 1 and 30) (default:4): ");
            cmd = input.nextLine();
            if (cmd.equals("")) {
                cmd = "4";
            }
            starCountPercent = getStars("x " + cmd);
        }
        while (asteroidFieldCount == -1) {
            System.out.println("Enter number of asteroid fields (recommended " + ((size * size) / 250) + "): ");
            cmd = input.nextLine();
            if (cmd.equals("")) {
                cmd = "" + ((size * size) / 250);
            }
            asteroidFieldCount = getAsteroids("x " + cmd);
        }
        while (nebulaCount == -1) {
            System.out.println("Enter number of nebulas (recommended " + ((size * size) / 250) + "): ");
            cmd = input.nextLine();
            if (cmd.equals("")) {
                cmd = "" + ((size * size) / 250);
            }
            nebulaCount = getNebulas("x " + cmd);
        }

        System.out.println("Enter filename (default gal.dat): ");
        cmd = input.nextLine();
        if (cmd.equals("")) {
            filename = "gal.dat";
        } else {
            filename = cmd;
        }

        // add users
        ArrayList<User> users = new ArrayList<User>(maxUsers);
        cmd = "";
        System.out.println("Add users by typing <faction> <name> <password>. faction must be either f,k,r,c or d");
        System.out.println("Example: f picard 1701ftw");
        System.out.println("enter \"exit\" when done adding users");
        while (!cmd.equals("exit") && users.size() <= maxUsers - 1) {
            cmd = input.nextLine();
            if (cmd.equalsIgnoreCase("exit")) {
                break;
            }
            User u = getUser(users, "x " + cmd);
            if (u != null) {
                users.add(u);
            }
        }

        System.out.println("Done adding users, generating galaxy...");

        File f = new File(filename);
        if (!f.exists()) {
            listParams(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, users.size());
            if (generateGalaxy(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, filename, users)) {
                System.exit(0);
            }
        } else {
            System.out.println("WARNING: file already exist: " + f.getAbsolutePath());
            System.out.print("Enter \"yes\" to overwrite:");
            String overwrite = input.nextLine();
            if (overwrite.equalsIgnoreCase("yes")) {
                listParams(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, users.size());
                if (generateGalaxy(size, speed, maxUsers, starCountPercent, asteroidFieldCount, nebulaCount, filename, users)) {
                    System.exit(0);
                }
            }
        }
    }


    private static boolean generateGalaxy(int size, int speed, int maxUser, int starDensity, int asteroids, int nebula, String filename, ArrayList<User> users) {
        boolean fail = false;
        if (size <= 10) {
            System.out.println("ERROR: size not set");
            fail = true;
        }
        if (speed < 30) {
            System.out.println("ERROR: speed not set");
            fail = true;
        }
        if (maxUser <= 0) {
            System.out.println("ERROR: max users not set");
            fail = true;
        }
        if (starDensity == -1) {
            System.out.println("ERROR: star density not set");
            fail = true;
        }

        if (fail) {
            return false;
        }

        // generate galaxy
        System.out.println("Generating galaxy: " + filename);
        long timer = System.currentTimeMillis();
        Galaxy galaxy = null;
        UniverseGenerator ug = null;
        try {
            ug = new UniverseGenerator();
            galaxy = ug.makeGalaxy(size, size, speed, maxUser, starDensity, asteroids, nebula);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return false;
        }
        if (ug == null || galaxy == null) {
            System.out.println("ERROR: failed to make UniverseGenerator or Galaxy object");
            return false;
        }
        timer = System.currentTimeMillis() - timer;
        System.out.println("Galaxy created in " + timer + " ms");

        try {
            for (User u : users) {
                ug.initUser(galaxy, u);
            }
        } catch (AddUserException aue) {
            System.out.println("ERROR: unable to add users:");
            System.out.println(aue.getMessage());
            return false;
        }

        // save galaxy
        File galaxyFile = new File(filename);
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(galaxyFile));
            oos.writeObject(galaxy);
        } catch (IOException ex) {
            System.out.println("ERROR: unable to write galaxy file to disk");
            System.out.println(ex.getMessage());
            return false;
        }

        System.out.println("Galaxy successfully stored");
        return true;
    }

    private static User getUser(ArrayList<User> users, String cmd) {
        StringTokenizer st = new StringTokenizer(cmd);
        String factionStr = "";
        Faction faction = null;
        String name = "";
        String password = "";
        try {
            st.nextToken();
            factionStr = st.nextToken();
            name = st.nextToken();
            password = st.nextToken();
        } catch (NoSuchElementException nsee) {
            if (cmd.startsWith("adduser")) {
                System.out.println("USAGE: adduser faction name password");
                System.out.println("EXAMPLE: adduser f picard 1701ftw");
            } else {
                System.out.println("USAGE: faction name password");
                System.out.println("EXAMPLE: f picard 1701ftw");
            }
            return null;
        }

        if (factionStr.equals("f")) {
            faction = StaticData.federation;
        }
        if (factionStr.equals("k")) {
            faction = StaticData.klingon;
        }
        if (factionStr.equals("r")) {
            faction = StaticData.romulan;
        }
        if (factionStr.equals("c")) {
            faction = StaticData.cardassian;
        }
        if (factionStr.equals("d")) {
            faction = StaticData.dominion;
        }
        if (faction == null) {
            System.out.println("ERROR: illegal faction, must be one of the following: f, k, r, c, d");
            return null;
        }
        if (name.length() <= 1) {
            System.out.println("ERROR: name to short");
            return null;
        }
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(name)) {
                System.out.println("ERROR: User with name \"" + name + "\" already added");
                return null;
            }
        }

        if (password.length() <= 1) {
            System.out.println("ERROR: password to short");
            return null;
        }

        User u = new User(name, password, faction);
        u.setAvatarFilename("default.png");
        return u;
    }

    private static int getAsteroids(String cmd) {
        int a = -1;
        try {
            a = getIntFromString(cmd, 1);
        } catch (Exception e) {
            System.out.println("ERROR: number of asteroids not a valid integer number.. example: asteroids 5");
        }

        if (a < 0) {
            a = 0;
            System.out.println("NOTE: negative number of asteroids interpeted as 0");
        }

        System.out.println("OK: number of asteroid fields set to " + a);

        return a;
    }

    private static int getNebulas(String cmd) {
        int n = -1;
        try {
            n = getIntFromString(cmd, 1);
        } catch (Exception e) {
            System.out.println("ERROR: number of nebulas not a valid integer number.. example: nebulas 5");
        }

        if (n < 0) {
            n = 0;
            System.out.println("NOTE: negative number of nebulas interpeted as 0");
        }

        System.out.println("OK: number of nebulas set to " + n);

        return n;
    }

    private static int getStars(String cmd) {
        int sc = -1;
        try {
            sc = getIntFromString(cmd, 1);
        } catch (Exception e) {
            System.out.println("ERROR: star density not a valid integer number.. example: starcount 4");
        }

        if (sc <= 0) {
            System.out.println("ERROR: star density must be larger than 0");
        } else if (sc > 30) {
            System.out.println("ERROR: refusing to make a galaxy with more than 30% stars, are you nuts?!");
        } else {
            System.out.println("OK: star density set to " + sc + "%");
        }
        return sc;
    }

    private static int getSpeed(String cmd) {
        int s = -1;
        try {
            s = getIntFromString(cmd, 1);
        } catch (Exception e) {
            System.out.println("ERROR: turn speed not a valid integer number.. example: speed 4");
        }

        if (s < 30) {
            System.out.println("ERROR: turn speed must be larger than 0");
        } else {
            System.out.println("OK: turn speed set to " + s + " seconds");
        }
        return s;
    }

    private static int getMaxUser(String cmd) {
        int mu = -1;
        try {
            mu = getIntFromString(cmd, 1);
        } catch (Exception e) {
            System.out.println("ERROR: maximum user count is not a valid integer number.. example: maxuser 10");
        }

        if (mu <= 0) {
            System.out.println("ERROR: maximum number of user must be larger than 0");
        } else {
            System.out.println("OK: max users set to " + mu);
        }
        return mu;
    }

    private static int getSize(String cmd) {
        int size = -1;
        try {
            size = getIntFromString(cmd, 1);
        } catch (Exception e) {
            System.out.println("ERROR: size not a valid integer number.. example: size 50");
        }

        if (size < 10) {
            System.out.println("ERROR: Size  must be at least 10");
        } else if (size >= 100 && size < 400) {
            System.out.println("WARNING: Large galaxy, users beware! :)");
        } else if (size >= 400 && size < 1000) {
            System.out.println("WARNING: Seriously! WTF, That galaxy is huge!");
        } else if (size >= 1000) {
            System.out.println("WARNING: Well, now your galaxy sizes are just getting silly :(");
        } else {
            System.out.println("OK, size set to " + size);
        }
        return size;
    }

    private static int getIntFromString(String cmd, int index) throws NumberFormatException {
        int res = -1;
        StringTokenizer st = new StringTokenizer(cmd);
        st.nextToken();
        int i = 1;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (i++ == index) {
                res = Integer.parseInt(token);
            }
        }
        return res;
    }

    private static void listParams(int size, int speed, int u, int star, int astr, int neb, int usersAdded) {
        int starEst = 0;
        if (size > 0) {
            starEst = (int) (((double) (size * size) / 100) * star);
        }
        System.out.println("Parameters");
        System.out.println("==========");
        System.out.println("galaxy size:              " + size + "x" + size + "  (" + (size * size) + " tiles)");
        System.out.println("turn speed:               " + speed + " seconds");
        System.out.println("max users:                " + u + " (" + usersAdded + " added)");
        System.out.println("star density:             " + star + "%  (estimated # stars: " + starEst + ")");
        System.out.println("number of asteroids       " + astr);
        System.out.println("number of nebulas         " + neb);
    }

    private static void showHelp() {
        System.out.println("Trekwar Make Galaxy 0.4.0 Help");
        System.out.println("==============================");
        System.out.println("size <number>       - set the size of the galaxy");
        System.out.println("speed <number>      - set the turn speed (in seconds)");
        System.out.println("maxuser <number>    - set maximum number of users");
        System.out.println("stars <number>      - set star density % (from 1 to 30)");
        System.out.println("astroids <number>   - set number of asteroid fields");
        System.out.println("nebulas <number>    - set number of nebulas");
        System.out.println("adduser x y z       - adds a user to the galaxy");
        System.out.println("                        - x = Faction [f,k,r,c,d]");
        System.out.println("                        - y = username");
        System.out.println("                        - z = password");
        System.out.println();
        System.out.println("list                - lists all parameters");
        System.out.println("save                - generate and store the galaxy to file");
        System.out.println();
        System.out.println("new                 - interactive mode, recommended");
        System.out.println("default             - generates default galaxy to gal.dat");
        System.out.println();
        System.out.println("exit                - exit program");
    }
}