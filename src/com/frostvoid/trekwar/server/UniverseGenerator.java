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

import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.exceptions.*;

import java.awt.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Contains all the support functions used when making a new galaxy/game
 * <p>
 * TODO: replace sysout's with proper logging
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class UniverseGenerator {

    private SecureRandom rand;
    private ArrayList<String> starNames;
    private static final HashMap<Integer, Point> TERRAN_SURFACE_MAP = new HashMap<Integer, Point>();
    private static final HashMap<Integer, Point> ARCTIC_SURFACE_MAP = new HashMap<Integer, Point>();
    private static final HashMap<Integer, Point> BARREN_SURFACE_MAP = new HashMap<Integer, Point>();
    private static final HashMap<Integer, Point> DESERT_SURFACE_MAP = new HashMap<Integer, Point>();
    private static final HashMap<Integer, Point> GASGIANT_SURFACE_MAP = new HashMap<Integer, Point>();
    private static final HashMap<Integer, Point> JUNGLE_SURFACE_MAP = new HashMap<Integer, Point>();
    private static final HashMap<Integer, Point> OCEANIC_SURFACE_MAP = new HashMap<Integer, Point>();
    private static final HashMap<Integer, Point> VOLCANIC_SURFACE_MAP = new HashMap<Integer, Point>();

    private static final int VOLCANIC_MAXPOP = 175;
    private static final int VOLCANIC_MAXSTRUCTURE = 3;
    private static final double VOLCANIC_FERTILITY = 0.3;

    private static final int TERRAN_MAXPOP = 4000;
    private static final int TERRAN_MAXSTRUCTURE = 12;
    private static final double TERRAN_FERTILITY = 1;

    private static final int ARCTIC_MAXPOP = 100;
    private static final int ARCTIC_MAXSTRUCTURE = 2;
    private static final double ARCTIC_FERTILITY = 0.15;

    private static final int DESERT_MAXPOP = 200;
    private static final int DESERT_MAXSTRUCTURE = 4;
    private static final double DESERT_FERTILITY = 0.3;

    private static final int BARREN_MAXPOP = 280;
    private static final int BARREN_MAXSTRUCTURE = 6;
    private static final double BARREN_FERTILITY = 0.4;

    private static final int JUNGLE_MAXPOP = 750;
    private static final int JUNGLE_MAXSTRUCTURE = 3;
    private static final double JUNGLE_FERTILITY = 1.8;

    private static final int OCEANIC_MAXPOP = 160;
    private static final int OCEANIC_MAXSTRUCTURE = 2;
    private static final double OCEANIC_FERTILITY = 0.6;

    private static final int GASGIANT_MAXPOP = 0;
    private static final int GASGIANT_MAXSTRUCTURE = 0;
    private static final double GASGIANT_FERTILITY = 0;

    private static final int SENSORCOST_EMPTY_SPACE = 10;
    private static final int SENSORCOST_STARSYSTEM = 15;
    private static final int SENSORCOST_ASTEROID = 25;
    private static final int SENSORCOST_NEBULA = 60;

    public UniverseGenerator() throws NoSuchAlgorithmException, NoSuchProviderException {
        rand = SecureRandom.getInstance("SHA1PRNG", "SUN");

        TERRAN_SURFACE_MAP.put(3, new Point(132, 6));
        TERRAN_SURFACE_MAP.put(4, new Point(194, 6));
        TERRAN_SURFACE_MAP.put(6, new Point(70, 46));
        TERRAN_SURFACE_MAP.put(7, new Point(132, 46));
        TERRAN_SURFACE_MAP.put(8, new Point(194, 46));
        TERRAN_SURFACE_MAP.put(9, new Point(8, 86));
        TERRAN_SURFACE_MAP.put(10, new Point(70, 86));
        TERRAN_SURFACE_MAP.put(11, new Point(132, 86));
        TERRAN_SURFACE_MAP.put(12, new Point(194, 86));

        ARCTIC_SURFACE_MAP.put(1, new Point(8, 6));
        ARCTIC_SURFACE_MAP.put(2, new Point(8, 86));

        BARREN_SURFACE_MAP.put(1, new Point(70, 6));
        BARREN_SURFACE_MAP.put(2, new Point(132, 6));
        BARREN_SURFACE_MAP.put(3, new Point(194, 6));
        BARREN_SURFACE_MAP.put(4, new Point(70, 46));
        BARREN_SURFACE_MAP.put(5, new Point(8, 86));

        DESERT_SURFACE_MAP.put(1, new Point(8, 6));
        DESERT_SURFACE_MAP.put(2, new Point(194, 6));
        DESERT_SURFACE_MAP.put(3, new Point(8, 46));
        DESERT_SURFACE_MAP.put(4, new Point(8, 86));

        JUNGLE_SURFACE_MAP.put(4, new Point(194, 6));
        JUNGLE_SURFACE_MAP.put(8, new Point(194, 46));
        JUNGLE_SURFACE_MAP.put(12, new Point(194, 86));

        OCEANIC_SURFACE_MAP.put(8, new Point(194, 46));
        OCEANIC_SURFACE_MAP.put(9, new Point(8, 86));

        VOLCANIC_SURFACE_MAP.put(7, new Point(132, 46));
        VOLCANIC_SURFACE_MAP.put(8, new Point(194, 46));
        VOLCANIC_SURFACE_MAP.put(11, new Point(132, 86));
    }

    /**
     * Generates a new galaxy object
     *
     * @param length        the length of the map
     * @param width         the width of the map
     * @param turnSpeed     the turn speed in seconds
     * @param maxUsers      maximum number of allowed users
     * @param starDensity   the star density (percentage)
     * @param asteroidCount number of asteroid fields
     * @param nebulaCount   number of nebula
     * @return the galaxy object
     * @throws UniverseGeneratorException
     */
    public Galaxy makeGalaxy(int length, int width, long turnSpeed, int maxUsers, int starDensity, int asteroidCount, int nebulaCount) throws UniverseGeneratorException {

        if (length * width < 225) {
            throw new UniverseGeneratorException("Galaxy must have at least 225 tiles (15x15)");
        }

        StarSystem[][] map = new StarSystem[length][width];
        Galaxy galaxy = new Galaxy(map, turnSpeed, maxUsers);

        // Make base galaxy, with normal starsystems
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                if (rand.nextInt(100) < starDensity) {
                    map[i][j] = makeSystem(StaticData.nobodyUser, i, j);
                    System.out.print("# ");
                } else {
                    map[i][j] = new StarSystem(StaticData.nobodyUser, i, j, "", StarSystemClassification.empty);
                    map[i][j].setSensorCost(SENSORCOST_EMPTY_SPACE);
                    System.out.print(". ");
                }
            }
            System.out.println();
        }

        generateAsteroidBelts(galaxy, asteroidCount, 5);
        generateNebulas(galaxy, nebulaCount, 6);

        return galaxy;
    }

    /**
     * Makes a single normal starsystem
     *
     * @param user the owner of the system
     * @param x    system location in galaxy (x)
     * @param y    system location in galaxy (y)
     * @return the starsystem object
     */
    private StarSystem makeSystem(User user, int x, int y) {
        int numberOfPlanets = rand.nextInt(7) + 4; // between 4 and 10 planets

        StarSystem starsystem = new StarSystem(user, x, y, getRandomStarSystemName(), StarSystemClassification.starSystem);
        starsystem.setSensorCost(SENSORCOST_STARSYSTEM);
        starsystem.setImageFile(getRandomStarSystemImage());

        ArrayList<Planet> planets = new ArrayList<Planet>(numberOfPlanets);
        for (int i = 0; i < numberOfPlanets; i++) {
            planets.add(makePlanet(starsystem, i));
        }

        // sort planets
        Collections.sort(planets, new Comparator<Planet>() {
            @Override
            public int compare(Planet o1, Planet o2) {
                return o1.getSortOrder() - o2.getSortOrder();
            }
        });

        for (int i = 0; i < numberOfPlanets; i++) {
            planets.get(i).setPlanetNumber(i + 1);
            starsystem.addPlanet(planets.get(i));
        }

        // uncomment for statistical whimsy
        // System.out.println("" + numberOfPlanets + "," + starsystem.getMaxStructures() + "," + starsystem.getDeuteriumPerTurn() + "," + starsystem.getMaxPopulation());

        return starsystem;
    }

    /**
     * Makes a single planet
     *
     * @param system    the system this planet is in
     * @param planetNum the planets number in the system
     * @return a freshly baked Planet object
     */
    private Planet makePlanet(StarSystem system, int planetNum) {
        Planet newPlanet;
        PlanetClassification type = PlanetClassification.terran;
        int sortOrder = 0;
        int maximumPopulation = 0;
        int maximumStructures = 0;
        double fertility = rand.nextDouble() / 4;
        HashMap<Integer, Point> surfaceMap = null;

        int planetType = rand.nextInt(8) + 1;
        switch (planetType) {
            case 1:
                type = PlanetClassification.arctic;
                sortOrder = 10;
                maximumPopulation = ARCTIC_MAXPOP;
                maximumStructures = ARCTIC_MAXSTRUCTURE;
                fertility += ARCTIC_FERTILITY;
                surfaceMap = ARCTIC_SURFACE_MAP;
                break;
            case 2:
                type = PlanetClassification.desert;
                sortOrder = 2;
                maximumPopulation = DESERT_MAXPOP;
                maximumStructures = DESERT_MAXSTRUCTURE;
                fertility += DESERT_FERTILITY;
                surfaceMap = DESERT_SURFACE_MAP;
                break;
            case 3:
                type = PlanetClassification.terran;
                sortOrder = 5;
                maximumPopulation = TERRAN_MAXPOP;
                maximumStructures = TERRAN_MAXSTRUCTURE;
                fertility += TERRAN_FERTILITY;
                surfaceMap = TERRAN_SURFACE_MAP;
                break;
            case 4:
                type = PlanetClassification.volcanic;
                sortOrder = 1;
                maximumPopulation = VOLCANIC_MAXPOP;
                maximumStructures = VOLCANIC_MAXSTRUCTURE;
                fertility += VOLCANIC_FERTILITY;
                surfaceMap = VOLCANIC_SURFACE_MAP;
                break;
            case 5:
                type = PlanetClassification.jungle;
                sortOrder = 4;
                maximumPopulation = JUNGLE_MAXPOP;
                maximumStructures = JUNGLE_MAXSTRUCTURE;
                surfaceMap = JUNGLE_SURFACE_MAP;
                fertility += JUNGLE_FERTILITY;
                break;
            case 6:
                type = PlanetClassification.barren;
                sortOrder = 3;
                maximumPopulation = BARREN_MAXPOP;
                maximumStructures = BARREN_MAXSTRUCTURE;
                fertility += BARREN_FERTILITY;
                surfaceMap = BARREN_SURFACE_MAP;
                break;
            case 7:
                type = PlanetClassification.oceanic;
                sortOrder = 6;
                maximumPopulation = OCEANIC_MAXPOP;
                maximumStructures = OCEANIC_MAXSTRUCTURE;
                fertility += OCEANIC_FERTILITY;
                surfaceMap = OCEANIC_SURFACE_MAP;
                break;

            case 8:
                type = PlanetClassification.gasGiant;
                sortOrder = 20;
                maximumPopulation = GASGIANT_MAXPOP;
                maximumStructures = GASGIANT_MAXSTRUCTURE;
                fertility = GASGIANT_FERTILITY;
                surfaceMap = GASGIANT_SURFACE_MAP;
                break;

            default:
                System.err.println("ERROR: invalid random number used in:\n" + "UniverseGenerator.makePlanet() -> planetType");
                System.exit(1);
        }

        newPlanet = new Planet(type, system, planetNum, maximumPopulation, fertility, maximumStructures);
        newPlanet.setSurfaceMap(surfaceMap);
        newPlanet.setSortOrder(sortOrder);

        if (type.equals(PlanetClassification.gasGiant)) {
            newPlanet.setDeuteriumPerTurn(getGasGiantDeuteriumProduction());
        }
        return newPlanet;
    }

    /**
     * Generates a number of asteroid belts in a galaxy
     *
     * @param galaxy        the galaxy to generate belts in
     * @param numberOfBelts number of belts to generate
     * @param baseBeltSize  the base size of each belt
     */
    private void generateAsteroidBelts(Galaxy galaxy, int numberOfBelts, int baseBeltSize) {
        System.out.println("generating " + numberOfBelts + " asteroid belts");
        if (numberOfBelts <= 0) {
            return;
        }

        for (int i = 0; i < numberOfBelts; i++) {
            int beltSize = (baseBeltSize * 2) + ((rand.nextInt(baseBeltSize) / 2) - baseBeltSize); // TODO +- 50% rand.getDouble() ?
            System.out.println("making new belt, size = " + beltSize);
            StarSystem startLoc = null;
            while (startLoc == null) {
                int randx = rand.nextInt(galaxy.getHeight());
                int randy = rand.nextInt(galaxy.getWidth());
                startLoc = galaxy.getSystem(randx, randy);

                if (startLoc != null && startLoc.getStarSystemClassification() != StarSystemClassification.empty) {
                    startLoc = null;
                }
            }

            StarSystem current = startLoc;
            int lockPreventer = 2000;
            for (int j = 0; j < beltSize; j++) {
                lockPreventer--;
                int randx = rand.nextInt(4);
                int randy = rand.nextInt(4);

                StarSystem next = galaxy.getSystem(
                        (current.getX() - 1) + randx,
                        (current.getY() - 1) + randy);

                if (next != null && next.getStarSystemClassification() == StarSystemClassification.empty) {
                    current = next;
                    current.setStarSystemClassification(StarSystemClassification.asteroid);
                    current.setName("Asteroids");
                    current.setSensorCost(SENSORCOST_ASTEROID);
                    int numberOfResources = (StaticData.MAX_ASTEROID_RESOURCES / 5) + (rand.nextInt(StaticData.MAX_ASTEROID_RESOURCES) / 2);
                    String filename = "";
                    for (int k = 0; k < numberOfResources / 4000; k++) {
                        int imgfile = 1 + rand.nextInt(10); // 1 - 10
                        filename += imgfile + ".";
                    }
                    current.setImageFile(filename);
                    current.setResourcesLeft(numberOfResources);

                } else {
                    j--;
                }
                if (lockPreventer < 0) {
                    break;
                }
            }
        }
        System.out.println("Done generating asteroid fields");
    }


    /**
     * Generates a set of nebulae in a galaxy
     *
     * @param galaxy          the galaxy to put the nebulae in
     * @param numberOfNebulae number of nebulae
     * @param baseNebulaSize  the base size of each nebula
     */
    private void generateNebulas(Galaxy galaxy, int numberOfNebulae, int baseNebulaSize) {
        System.out.println("generating " + numberOfNebulae + " nebulae");
        if (numberOfNebulae <= 0) {
            return;
        }

        for (int i = 0; i < numberOfNebulae; i++) {
            int nebulaSize = (baseNebulaSize * 2) + ((rand.nextInt(baseNebulaSize) / 2) - baseNebulaSize);
            System.out.println("nebula size = " + nebulaSize);
            StarSystem startLoc = null;
            while (startLoc == null) {
                int randx = rand.nextInt(galaxy.getHeight());
                int randy = rand.nextInt(galaxy.getWidth());
                startLoc = galaxy.getSystem(randx, randy);

                if (startLoc != null && startLoc.getStarSystemClassification() != StarSystemClassification.empty) {
                    startLoc = null;
                }
            }

            StarSystem current = startLoc;
            int lockPreventer = 2000;
            int nebulaType = 1 + rand.nextInt(4);// 1 - 4

            for (int j = 0; j < nebulaSize; j++) {
                lockPreventer--;
                int randx = rand.nextInt(3);
                int randy = rand.nextInt(3);

                StarSystem next = galaxy.getSystem(
                        (current.getX() - 1) + randx,
                        (current.getY() - 1) + randy);

                if (next != null && next.getStarSystemClassification() == StarSystemClassification.empty) {
                    current = next;
                    current.setStarSystemClassification(StarSystemClassification.nebula);
                    current.setName("Nebula");
                    current.setSensorCost(SENSORCOST_NEBULA);
                    int numberOfResources = (StaticData.MAX_NEBULA_RESOURCES / 5) + (rand.nextInt(StaticData.MAX_NEBULA_RESOURCES / 2));
                    int imgfile = 1 + rand.nextInt(11); // 1 - 11
                    String filename = "nebula_" + nebulaType + "_" + imgfile + ".png";
                    current.setImageFile(filename);
                    current.setResourcesLeft(numberOfResources);

                } else {
                    j--;
                }
                if (lockPreventer < 0) {
                    break;
                }
            }
        }
        System.out.println("Done generating nebulas");
    }

    /**
     * Generates a random star system name
     *
     * @return a starsystem name
     */
    private String getRandomStarSystemName() {
        if (starNames == null) {
            starNames = new ArrayList<String>(1800);
            BufferedReader b = null;
            try {
                b = new BufferedReader(new FileReader(new File("src/org/aakretech/trekwar2/server/starnames.txt")));
                String name = b.readLine();
                while (name != null) {
                    if (!name.startsWith("#")) {
                        starNames.add(name);
                    }
                    name = b.readLine();
                }
                b.close();
            } catch (FileNotFoundException ex) {
                System.err.println(ex);
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }

        if (starNames.isEmpty()) {
            return "M " + rand.nextInt(500) + 100;
        }
        return starNames.remove(rand.nextInt(starNames.size()));
    }

    /**
     * This method will take a User object, validate it
     * and make the User object a valid user in the galaxy
     * Setting up a start planet with a few basic structures
     * and adding a few ships to the User
     *
     * @param g       the Galaxy object
     * @param newUser the user to add
     * @throws AddUserException if the user can not be added
     */
    public void initUser(Galaxy g, User newUser) throws AddUserException {
        StarSystem s = null;

        if (g.getUserCount() > g.getMaxUsers()) {
            throw new AddUserException("No more users allowed for this galaxy");
        }
        try {
            g.getUser(newUser.getUsername());
            throw new AddUserException("The username is already in use");
        } catch (UserNotFoundException unfe) {
        }

        // locate an empty starsystem for new user
        int notFound = 100; // maxiumum 100 tries (on valid systems) to prevent deadlock
        while (notFound >= 0) {
            int galaxyX = rand.nextInt(g.getMap().length);
            int galaxyY = rand.nextInt(g.getMap()[0].length);

            s = g.getMap()[galaxyX][galaxyY];
            if (s.getStarSystemClassification().equals(StarSystemClassification.empty)) {
                if (makeStartingSystem(g, newUser, galaxyX, galaxyY)) {
                    // Create Starter system, add population
                    for (Planet p : s.getPlanets()) {
                        p.setPopulation(p.getMaximumPopulation() / 10);
                    }

                    s.setMorale(90);
                    s.setTroopCount(20 + s.getPlanets().size()); // add some troops
                    s.setDeuterium(1600); // initial deuterium
                    setUpStartingTechs(newUser, s);
                    setUpStartingShips(newUser, s);

                    // create fog of war over entire map for user
                    int[][] sensorOverlay = new int[g.getWidth()][g.getHeight()];
                    for (int i = 0; i < sensorOverlay.length; i++) {
                        for (int j = 0; j < sensorOverlay[i].length; j++) {
                            sensorOverlay[i][j] = Integer.MIN_VALUE;
                        }
                    }
                    newUser.setSensorOverlay(sensorOverlay);

                    // add user to galaxy
                    g.addUser(newUser);

                    System.out.println("initUser() Found: " + galaxyX + ":" + galaxyY + " for user: " + newUser.getUsername());
                    break;
                } else {
                    TrekwarServer.LOG.log(Level.SEVERE, "UniverseGenerator unable to make starting system from empty system at {0}:{1}", new Object[]{galaxyX, galaxyY});
                }
            } else {
                notFound--;
                continue;
            }
        }
    }

    /**
     * Turns an empty starsystem into a default user star system
     *
     * @param g the galaxy object
     * @param u the owner (user) of this system
     * @param x starsystem x location
     * @param y starsystem y location
     * @return true if successful, false if failed
     */
    private boolean makeStartingSystem(Galaxy g, User u, int x, int y) {
        StarSystem s = g.getMap()[x][y];
        if (s.getStarSystemClassification().equals(StarSystemClassification.empty)) {
            s.setStarSystemClassification(StarSystemClassification.starSystem);
            s.setSensorCost(SENSORCOST_STARSYSTEM);
            s.setName(getRandomStarSystemName());
            s.setImageFile(getRandomStarSystemImage());
            s.setUser(u);
            u.addSystem(s);

            int p = 0;
            Planet p0 = new Planet(PlanetClassification.volcanic, s, p++, VOLCANIC_MAXPOP, VOLCANIC_FERTILITY, VOLCANIC_MAXSTRUCTURE);
            p0.setSurfaceMap(VOLCANIC_SURFACE_MAP);
            Planet p1 = new Planet(PlanetClassification.desert, s, p++, DESERT_MAXPOP, DESERT_FERTILITY, DESERT_MAXSTRUCTURE);
            p1.setSurfaceMap(DESERT_SURFACE_MAP);
            Planet p2 = new Planet(PlanetClassification.terran, s, p++, TERRAN_MAXPOP, TERRAN_FERTILITY, TERRAN_MAXSTRUCTURE);
            p2.setSurfaceMap(TERRAN_SURFACE_MAP);
            Planet p4 = new Planet(PlanetClassification.jungle, s, p++, JUNGLE_MAXPOP, JUNGLE_FERTILITY, JUNGLE_MAXSTRUCTURE);
            p4.setSurfaceMap(JUNGLE_SURFACE_MAP);
            Planet p5 = new Planet(PlanetClassification.oceanic, s, p++, OCEANIC_MAXPOP, OCEANIC_FERTILITY, OCEANIC_MAXSTRUCTURE);
            p5.setSurfaceMap(OCEANIC_SURFACE_MAP);
            Planet p6 = new Planet(PlanetClassification.barren, s, p++, BARREN_MAXPOP, BARREN_FERTILITY, BARREN_MAXSTRUCTURE);
            p6.setSurfaceMap(BARREN_SURFACE_MAP);
            Planet p8 = new Planet(PlanetClassification.gasGiant, s, p++, GASGIANT_MAXPOP, GASGIANT_FERTILITY, GASGIANT_MAXSTRUCTURE);
            p8.setDeuteriumPerTurn(65);
            p8.setSurfaceMap(GASGIANT_SURFACE_MAP);
            Planet p9 = new Planet(PlanetClassification.gasGiant, s, p++, GASGIANT_MAXPOP, GASGIANT_FERTILITY, GASGIANT_MAXSTRUCTURE);
            p9.setDeuteriumPerTurn(65);
            p9.setSurfaceMap(GASGIANT_SURFACE_MAP);
            Planet p10 = new Planet(PlanetClassification.arctic, s, p++, ARCTIC_MAXPOP, ARCTIC_FERTILITY, ARCTIC_MAXSTRUCTURE);
            p10.setSurfaceMap(ARCTIC_SURFACE_MAP);

            s.addPlanet(p0);
            s.addPlanet(p1);
            s.addPlanet(p2);
            s.addPlanet(p4);
            s.addPlanet(p5);
            s.addPlanet(p6);
            s.addPlanet(p8);
            s.addPlanet(p9);
            s.addPlanet(p10);


            // Populate starting system with structures
            s.addStructure(p2, 1, StaticData.factory1);
            p2.setStructureEnabled(1, true);

            s.addStructure(p2, 2, StaticData.power1);
            p2.setStructureEnabled(2, true);

            s.addStructure(p2, 3, StaticData.power1);
            p2.setStructureEnabled(3, true);

            s.addStructure(p2, 6, StaticData.power1);
            p2.setStructureEnabled(6, true);

            s.addStructure(p2, 5, StaticData.factory1);
            p2.setStructureEnabled(5, true);

            s.addStructure(p2, 9, StaticData.factory1);
            p2.setStructureEnabled(9, true);

            s.addStructure(p2, 10, StaticData.deuteriumProcessingPlant1);
            p2.setStructureEnabled(10, true);

            s.addStructure(p2, 8, StaticData.farm1);
            p2.setStructureEnabled(8, true);

            s.addStructure(p2, 4, StaticData.farm1);
            p2.setStructureEnabled(4, true);

            s.addStructure(p2, 12, StaticData.farm1);
            p2.setStructureEnabled(12, true);

            s.addStructure(p2, 7, StaticData.shipyard1);
            p2.setStructureEnabled(7, true);

            s.addStructure(p2, 11, StaticData.lab1);
            p2.setStructureEnabled(11, true);

            s.addStructure(p1, 4, StaticData.lab1);
            p1.setStructureEnabled(4, true);

            return true;
        }
        return false;
    }


    /**
     * Generates a random deuterium output for a gas giant
     *
     * @return random deuterium output
     */
    private int getGasGiantDeuteriumProduction() {
        return 10 + rand.nextInt(30);
    }

    /**
     * Sets up the default starting techs for a user
     *
     * @param u the user
     * @param s the start system
     */
    private void setUpStartingTechs(User u, StarSystem s) {

        // FOR TESTING
//        for(Technology t : TechnologyGenerator.getAllTechs()) {
//            u.addTech(t);
//        }

        u.addTech(TechnologyGenerator.BIO_TECH0);
        u.addTech(TechnologyGenerator.COMPUTER_TECH0);
        u.addTech(TechnologyGenerator.CONSTRUCTION_TECH0);
        u.addTech(TechnologyGenerator.ENERGY_TECH0);
        u.addTech(TechnologyGenerator.PROPULSION_TECH0);
        u.addTech(TechnologyGenerator.WEAPON_TECH0);


        if (u.getFaction().equals(StaticData.federation)) {
            u.addTech(TechnologyGenerator.COMPUTER_TECH1);
            u.addTech(TechnologyGenerator.ENERGY_TECH1);
        } else if (u.getFaction().equals(StaticData.klingon)) {
            u.addTech(TechnologyGenerator.WEAPON_TECH1);
            s.addTroops(25);
        } else if (u.getFaction().equals(StaticData.romulan)) {
            u.addTech(TechnologyGenerator.ENERGY_TECH1);
        } else if (u.getFaction().equals(StaticData.cardassian)) {
            u.addTech(TechnologyGenerator.CONSTRUCTION_TECH1);
            s.addTroops(10);
        } else if (u.getFaction().equals(StaticData.dominion)) {
            u.addTech(TechnologyGenerator.PROPULSION_TECH1);
            s.addTroops(25);
        }
    }


    /**
     * Sets up default starting templates and ships + fleets for a user
     *
     * @param u the user
     * @param s the home star system
     * @return true if successful
     */
    private boolean setUpStartingShips(User u, StarSystem s) {
        ShipTemplate starterScout = new ShipTemplate(u, "scoutship", StaticData.civilian_scoutship);
        ShipTemplate starterColony = new ShipTemplate(u, "colonyship", StaticData.civilian_colonyship);

        try {
            starterScout.setComponent(0, StaticData.warpCore1);
            starterScout.setComponent(1, StaticData.basicSensor1);
            starterScout.setComponent(2, StaticData.basicLaser1);
            u.addShipTemplate(starterScout);

            starterColony.setComponent(0, StaticData.warpCore1);
            starterColony.setComponent(1, StaticData.colonizationModule1);
            starterColony.setComponent(2, StaticData.basicSensor1);
            starterColony.setComponent(4, StaticData.fusionReactor1);
            u.addShipTemplate(starterColony);
        } catch (SlotException e) {
            System.err.println("Unable to make default starting ship templates!");
            System.exit(1);
        }

        Fleet f1 = new Fleet(u, "exploration", s);
        Fleet f2 = new Fleet(u, "colonization", s);

        Ship scout = new Ship(u, f1, "scoutship", u.getNextShipId(), StaticData.civilian_scoutship);
        Ship colonyship = new Ship(u, f2, "colonyship", u.getNextShipId(), StaticData.civilian_colonyship);

        try {
            scout.applyTemplate(starterScout);
            scout.initShip();
            colonyship.applyTemplate(starterColony);
            colonyship.initShip();
            colonyship.setColonists(100);

            f1.addShip(scout);
            f2.addShip(colonyship);
            try {
                u.addFleet(f1);
                u.addFleet(f2);
            } catch (NotUniqueException ex) {
                TrekwarServer.LOG.log(Level.SEVERE, "UniverseGenerator unable to add starter ships to player fleet");
                return false;
            }

            s.addFleet(f1);
            s.addFleet(f2);
            return true;
        } catch (SlotException ex) {
            TrekwarServer.LOG.log(Level.SEVERE, "Unable to put components in slots: {0}", ex.getMessage());
            return false;
        }
    }


    /**
     * Gets a random background image for a star system
     *
     * @return random image file
     */
    private String getRandomStarSystemImage() {
        switch (rand.nextInt(6) + 1) {
            case 1:
                return "star1.png";
            case 2:
                return "star2.png";
            case 3:
                return "star3.png";
            case 4:
                return "star4.png";
            case 5:
                return "star5.png";
            case 6:
                return "star6.png";
            case 7:
                return "star7.png";
            default:
                return "star1.png";
        }
    }
}