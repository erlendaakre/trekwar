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
package com.frostvoid.trekwar.server.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import com.frostvoid.trekwar.common.Ship;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.exceptions.SlotException;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.Galaxy;
import com.frostvoid.trekwar.common.Planet;
import com.frostvoid.trekwar.common.ShipTemplate;
import com.frostvoid.trekwar.common.SpaceObjectClassification;
import com.frostvoid.trekwar.common.StarSystemClassification;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.TechnologyGenerator;
import com.frostvoid.trekwar.common.User;
import com.frostvoid.trekwar.common.exceptions.NotUniqueException;
import com.frostvoid.trekwar.server.UniverseGenerator;

/**
 * Generates the pre-alpha testing scenario for single player gameplay test
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class GeneratePreAlphaSPScenario {

    public static void main(String[] args) throws SlotException, NotUniqueException {

        // TODO get username password from command line?
        String username = "komodo";
        String password = "komodomq51";
        String filename = "komodo.dat";

        
        Galaxy galaxy = null;
        UniverseGenerator ug = null;
        User player = new User(username, password, StaticData.federation);
        User enemy1 = new User("lursa", "2i4D9OxNumhwCYYFxZx8JkMe6aqVKCq18M4Ovarb", StaticData.klingon);
        enemy1.setAvatarFilename("klingon_lursa.png");
        User enemy2 = new User("gowron", "uHYMTiPkEDcDiRFcnCuye4HfPZQMfiRjATVRmmD3", StaticData.klingon);
        enemy2.setAvatarFilename("klingon_gowron.png");
        User enemy3 = new User("chang", "MssSqQDeoTT9tkTSR3izdf0tTjddme3OhrYWjcDS", StaticData.klingon);
        enemy3.setAvatarFilename("klingon_chang.png");
        try {
            ug = new UniverseGenerator();
            galaxy = ug.makeGalaxy(30, 30, 30, 10, 4, 6, 6);
            ug.initUser(galaxy, player);
            ug.initUser(galaxy, enemy1);
            ug.initUser(galaxy, enemy2);
            ug.initUser(galaxy, enemy3);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        
        
        
        // Lursa structures
        Planet p1 = enemy1.getStarSystems().get(0).getPlanetByNumber(3);
        enemy1.getStarSystems().get(0).addStructure(p1, 1 , StaticData.militaryOutpost1);
        p1.setStructureEnabled(1, true);
        enemy1.getStarSystems().get(0).addStructure(p1, 9 , StaticData.farm1);
        p1.setStructureEnabled(9, true);
        enemy1.getStarSystems().get(0).addStructure(p1, 10 , StaticData.farm1);
        p1.setStructureEnabled(10, true);
        enemy1.getStarSystems().get(0).setTroopCount(9999999);
        
        // Gowron structures
        Planet p2 = enemy2.getStarSystems().get(0).getPlanetByNumber(3);
        enemy2.getStarSystems().get(0).addStructure(p2, 1 , StaticData.militaryOutpost1);
        p2.setStructureEnabled(1, true);
        enemy2.getStarSystems().get(0).addStructure(p2, 9 , StaticData.farm1);
        p2.setStructureEnabled(9, true);
        enemy2.getStarSystems().get(0).addStructure(p2, 10 , StaticData.farm1);
        p2.setStructureEnabled(10, true);
        enemy2.getStarSystems().get(0).addStructure(p2, 11 , StaticData.farm1);
        p2.setStructureEnabled(11, true);
        enemy2.getStarSystems().get(0).addStructure(p2, 12 , StaticData.power1);
        p2.setStructureEnabled(12, true);
        enemy2.getStarSystems().get(0).addStructure(p2, 2 , StaticData.bunker2);
        p2.setStructureEnabled(2, true);
        enemy2.getStarSystems().get(0).addStructure(p2, 3 , StaticData.bunker2);
        p2.setStructureEnabled(3, true);
        enemy2.getStarSystems().get(0).setTroopCount(9999999);
        
        // Chang structures
        enemy3.getStarSystems().get(0).addStructure(enemy3.getStarSystems().get(0).getPlanetByNumber(1), 1 , StaticData.militaryOutpost1);
        enemy3.getStarSystems().get(0).getPlanetByNumber(1).setStructureEnabled(1, true);
        enemy3.getStarSystems().get(0).addStructure(enemy3.getStarSystems().get(0).getPlanetByNumber(1), 2 , StaticData.bunker3);
        enemy3.getStarSystems().get(0).getPlanetByNumber(1).setStructureEnabled(2, true);
        enemy3.getStarSystems().get(0).addStructure(enemy3.getStarSystems().get(0).getPlanetByNumber(1), 3 , StaticData.bunker3);
        enemy3.getStarSystems().get(0).getPlanetByNumber(1).setStructureEnabled(3, true);
        
        Planet p3 = enemy3.getStarSystems().get(0).getPlanetByNumber(3);
        enemy3.getStarSystems().get(0).addStructure(p3, 1 , StaticData.militaryOutpost1);
        p3.setStructureEnabled(1, true);
        enemy3.getStarSystems().get(0).addStructure(p3, 9 , StaticData.farm1);
        p3.setStructureEnabled(9, true);
        enemy3.getStarSystems().get(0).addStructure(p3, 10 , StaticData.farm1);
        p3.setStructureEnabled(10, true);
        enemy3.getStarSystems().get(0).addStructure(p3, 11 , StaticData.farm1);
        p3.setStructureEnabled(11, true);
        enemy3.getStarSystems().get(0).addStructure(p3, 12 , StaticData.farm1);
        p3.setStructureEnabled(12, true);
        enemy3.getStarSystems().get(0).addStructure(p3, 7 , StaticData.power1);
        p3.setStructureEnabled(7, true);
        enemy3.getStarSystems().get(0).addStructure(p3, 2 , StaticData.bunker3);
        p3.setStructureEnabled(2, true);
        enemy3.getStarSystems().get(0).addStructure(p3, 3 , StaticData.bunker3);
        p3.setStructureEnabled(3, true);
        enemy3.getStarSystems().get(0).setTroopCount(9999999);
        
        
        
        // Lursa techs
        enemy1.addTech(TechnologyGenerator.BIO_TECH1);
        enemy1.addTech(TechnologyGenerator.BIO_TECH2);
        enemy1.addTech(TechnologyGenerator.BIO_TECH3);
        enemy1.addTech(TechnologyGenerator.COMPUTER_TECH1);
        enemy1.addTech(TechnologyGenerator.COMPUTER_TECH2);
        enemy1.addTech(TechnologyGenerator.COMPUTER_TECH3);
        enemy1.addTech(TechnologyGenerator.CONSTRUCTION_TECH1);
        enemy1.addTech(TechnologyGenerator.CONSTRUCTION_TECH2);
        enemy1.addTech(TechnologyGenerator.CONSTRUCTION_TECH3);
        enemy1.addTech(TechnologyGenerator.ENERGY_TECH1);
        enemy1.addTech(TechnologyGenerator.ENERGY_TECH2);
        enemy1.addTech(TechnologyGenerator.ENERGY_TECH3);
        enemy1.addTech(TechnologyGenerator.PROPULSION_TECH1);
        enemy1.addTech(TechnologyGenerator.PROPULSION_TECH2);
        enemy1.addTech(TechnologyGenerator.PROPULSION_TECH3);
        enemy1.addTech(TechnologyGenerator.WEAPON_TECH1);
        enemy1.addTech(TechnologyGenerator.WEAPON_TECH2);
        enemy1.addTech(TechnologyGenerator.WEAPON_TECH3);
        
        // Gowron techs
        enemy2.addTech(TechnologyGenerator.BIO_TECH1);
        enemy2.addTech(TechnologyGenerator.BIO_TECH2);
        enemy2.addTech(TechnologyGenerator.BIO_TECH3);
        enemy2.addTech(TechnologyGenerator.BIO_TECH4);
        enemy2.addTech(TechnologyGenerator.BIO_TECH5);
        enemy2.addTech(TechnologyGenerator.BIO_TECH6);
        enemy2.addTech(TechnologyGenerator.COMPUTER_TECH1);
        enemy2.addTech(TechnologyGenerator.COMPUTER_TECH2);
        enemy2.addTech(TechnologyGenerator.COMPUTER_TECH3);
        enemy2.addTech(TechnologyGenerator.COMPUTER_TECH4);
        enemy2.addTech(TechnologyGenerator.COMPUTER_TECH5);
        enemy2.addTech(TechnologyGenerator.COMPUTER_TECH6);
        enemy2.addTech(TechnologyGenerator.CONSTRUCTION_TECH1);
        enemy2.addTech(TechnologyGenerator.CONSTRUCTION_TECH2);
        enemy2.addTech(TechnologyGenerator.CONSTRUCTION_TECH3);
        enemy2.addTech(TechnologyGenerator.CONSTRUCTION_TECH4);
        enemy2.addTech(TechnologyGenerator.CONSTRUCTION_TECH5);
        enemy2.addTech(TechnologyGenerator.CONSTRUCTION_TECH5);
        enemy2.addTech(TechnologyGenerator.ENERGY_TECH1);
        enemy2.addTech(TechnologyGenerator.ENERGY_TECH2);
        enemy2.addTech(TechnologyGenerator.ENERGY_TECH3);
        enemy2.addTech(TechnologyGenerator.ENERGY_TECH4);
        enemy2.addTech(TechnologyGenerator.ENERGY_TECH5);
        enemy2.addTech(TechnologyGenerator.ENERGY_TECH6);
        enemy2.addTech(TechnologyGenerator.PROPULSION_TECH1);
        enemy2.addTech(TechnologyGenerator.PROPULSION_TECH2);
        enemy2.addTech(TechnologyGenerator.PROPULSION_TECH3);
        enemy2.addTech(TechnologyGenerator.PROPULSION_TECH4);
        enemy2.addTech(TechnologyGenerator.PROPULSION_TECH5);
        enemy2.addTech(TechnologyGenerator.PROPULSION_TECH6);
        enemy2.addTech(TechnologyGenerator.WEAPON_TECH1);
        enemy2.addTech(TechnologyGenerator.WEAPON_TECH2);
        enemy2.addTech(TechnologyGenerator.WEAPON_TECH3);
        enemy2.addTech(TechnologyGenerator.WEAPON_TECH4);
        enemy2.addTech(TechnologyGenerator.WEAPON_TECH5);
        enemy2.addTech(TechnologyGenerator.WEAPON_TECH6);
        
        
        // Gowron techs
        for(Technology t : TechnologyGenerator.getAllTechs()) {
            enemy3.addTech(t);
        }

        // Lursa templates
        ShipTemplate template_1_1 = new ShipTemplate(enemy1, "attacker", StaticData.klingon_brel);
        template_1_1.setComponent(0, StaticData.warpCore1);
        template_1_1.setComponent(1, StaticData.beamEmitter1);
        template_1_1.setComponent(2, StaticData.beamEmitter1);
        template_1_1.setComponent(3, StaticData.fusionReactor2);
        template_1_1.setComponent(4, StaticData.torpedoLauncher1);
        template_1_1.setComponent(5, StaticData.armor1);
        template_1_1.setComponent(6, StaticData.shieldEmitter1);
        template_1_1.setComponent(7, StaticData.fusionReactor2);
        enemy1.addShipTemplate(template_1_1);
        
        // Gowron tempaltes
        ShipTemplate template_2_1 = new ShipTemplate(enemy2, "heavyAttacker", StaticData.klingon_ktinga);
        template_2_1.setComponent(0, StaticData.warpCore2);
        template_2_1.setComponent(1, StaticData.warpCore2);
        template_2_1.setComponent(2, StaticData.armor2);
        template_2_1.setComponent(3, StaticData.shieldEmitter2);
        template_2_1.setComponent(4, StaticData.fusionReactor2);
        template_2_1.setComponent(5, StaticData.fusionReactor2);
        template_2_1.setComponent(6, StaticData.fusionReactor2);
        template_2_1.setComponent(7, StaticData.sensor2);
        template_2_1.setComponent(8, StaticData.fusionReactor2);
        template_2_1.setComponent(9, StaticData.beamEmitter3);
        template_2_1.setComponent(10, StaticData.beamEmitter3);
        template_2_1.setComponent(11, StaticData.beamEmitter3);
        template_2_1.setComponent(12, StaticData.torpedoLauncher2);
        template_2_1.setComponent(13, StaticData.torpedoLauncher2);
        enemy2.addShipTemplate(template_2_1);
        
        // Chan templates
        ShipTemplate template_3_1 = new ShipTemplate(enemy3, "battleship", StaticData.klingon_neghvar);
        template_3_1.setComponent(0, StaticData.warpCore3);
        template_3_1.setComponent(1, StaticData.warpCore3);
        template_3_1.setComponent(2, StaticData.fusionReactor4);
        template_3_1.setComponent(3, StaticData.fusionReactor4);
        template_3_1.setComponent(4, StaticData.fusionReactor4);
        template_3_1.setComponent(5, StaticData.fusionReactor4);
        template_3_1.setComponent(6, StaticData.armor3);
        template_3_1.setComponent(7, StaticData.armor3);
        template_3_1.setComponent(8, StaticData.sensor4);
        template_3_1.setComponent(9, StaticData.fusionReactor4);
        template_3_1.setComponent(10, StaticData.fusionReactor4);
        template_3_1.setComponent(11, StaticData.shieldEmitter4);
        template_3_1.setComponent(12, StaticData.shieldEmitter4);
        template_3_1.setComponent(13, StaticData.beamEmitter5);
        template_3_1.setComponent(14, StaticData.beamEmitter5);
        template_3_1.setComponent(15, StaticData.beamEmitter5);
        template_3_1.setComponent(16, StaticData.torpedoLauncher4);
        template_3_1.setComponent(17, StaticData.torpedoLauncher4);
        enemy3.addShipTemplate(template_3_1);
        
        ShipTemplate template_3_2 = new ShipTemplate(enemy3, "heavyAttacker", StaticData.klingon_ktinga);
        template_3_2.setComponent(0, StaticData.warpCore3);
        template_3_2.setComponent(1, StaticData.warpCore3);
        template_3_2.setComponent(2, StaticData.armor2);
        template_3_2.setComponent(3, StaticData.shieldEmitter4);
        template_3_2.setComponent(4, StaticData.fusionReactor4);
        template_3_2.setComponent(5, StaticData.fusionReactor4);
        template_3_2.setComponent(6, StaticData.fusionReactor2);
        template_3_2.setComponent(7, StaticData.sensor3);
        template_3_2.setComponent(8, StaticData.fusionReactor4);
        template_3_2.setComponent(9, StaticData.beamEmitter4);
        template_3_2.setComponent(10, StaticData.beamEmitter4);
        template_3_2.setComponent(11, StaticData.beamEmitter3);
        template_3_2.setComponent(12, StaticData.torpedoLauncher2);
        template_3_2.setComponent(13, StaticData.torpedoLauncher4);
        enemy3.addShipTemplate(template_3_2);
        
        
        // Lursa fleet
        Fleet f1_1 = new Fleet(enemy1, "lursaAlpha", enemy1.getStarSystems().get(0));
        Ship s11_1 = new Ship(enemy1, f1_1, template_1_1.getName(), enemy1.getNextShipId(), template_1_1.getHullClass());
        Ship s11_2 = new Ship(enemy1, f1_1, template_1_1.getName(), enemy1.getNextShipId(), template_1_1.getHullClass());
        Ship s11_3 = new Ship(enemy1, f1_1, template_1_1.getName(), enemy1.getNextShipId(), template_1_1.getHullClass());
        s11_1.applyTemplate(template_1_1);
        s11_2.applyTemplate(template_1_1);
        s11_3.applyTemplate(template_1_1);
        s11_1.initShip();
        s11_2.initShip();
        s11_3.initShip();
        f1_1.addShip(s11_1);
        f1_1.addShip(s11_2);
        f1_1.addShip(s11_3);
        enemy1.addFleet(f1_1);
        enemy1.getStarSystems().get(0).addFleet(f1_1);
        
        
        // Gowron fleet
        Fleet f2_1 = new Fleet(enemy2, "gowronAlpha", enemy2.getStarSystems().get(0));
        Ship s21_1 = new Ship(enemy2, f2_1, template_2_1.getName(), enemy2.getNextShipId(), template_2_1.getHullClass());
        Ship s21_2 = new Ship(enemy2, f2_1, template_2_1.getName(), enemy2.getNextShipId(), template_2_1.getHullClass());
        Ship s21_3 = new Ship(enemy2, f2_1, template_2_1.getName(), enemy2.getNextShipId(), template_2_1.getHullClass());
        Ship s21_4 = new Ship(enemy2, f2_1, template_2_1.getName(), enemy2.getNextShipId(), template_2_1.getHullClass());
        Ship s21_5 = new Ship(enemy2, f2_1, template_2_1.getName(), enemy2.getNextShipId(), template_2_1.getHullClass());
        Ship s21_6 = new Ship(enemy2, f2_1, template_2_1.getName(), enemy2.getNextShipId(), template_2_1.getHullClass());
        s21_1.applyTemplate(template_2_1);
        s21_2.applyTemplate(template_2_1);
        s21_3.applyTemplate(template_2_1);
        s21_4.applyTemplate(template_2_1);
        s21_5.applyTemplate(template_2_1);
        s21_6.applyTemplate(template_2_1);
        s21_1.initShip();
        s21_2.initShip();
        s21_3.initShip();
        s21_4.initShip();
        s21_5.initShip();
        s21_6.initShip();
        f2_1.addShip(s21_1);
        f2_1.addShip(s21_2);
        f2_1.addShip(s21_3);
        f2_1.addShip(s21_4);
        f2_1.addShip(s21_5);
        f2_1.addShip(s21_6);
        enemy2.addFleet(f2_1);
        enemy2.getStarSystems().get(0).addFleet(f2_1);
        
        
        
        
        // Chang fleet
        Fleet f3_1 = new Fleet(enemy3, "changAlpha", enemy3.getStarSystems().get(0));
        Ship s31_1 = new Ship(enemy3, f3_1, template_3_1.getName(), enemy3.getNextShipId(), template_3_1.getHullClass());
        Ship s31_2 = new Ship(enemy3, f3_1, template_3_1.getName(), enemy3.getNextShipId(), template_3_1.getHullClass());
        Ship s31_3 = new Ship(enemy3, f3_1, template_3_1.getName(), enemy3.getNextShipId(), template_3_1.getHullClass());
        Ship s31_4 = new Ship(enemy3, f3_1, template_3_1.getName(), enemy3.getNextShipId(), template_3_1.getHullClass());
        Ship s31_5 = new Ship(enemy3, f3_1, template_3_1.getName(), enemy3.getNextShipId(), template_3_1.getHullClass());
        Ship s31_6 = new Ship(enemy3, f3_1, template_3_1.getName(), enemy3.getNextShipId(), template_3_1.getHullClass());
        Ship s31_7 = new Ship(enemy3, f3_1, template_3_1.getName(), enemy3.getNextShipId(), template_3_1.getHullClass());
        Ship s31_8 = new Ship(enemy3, f3_1, template_3_2.getName(), enemy3.getNextShipId(), template_3_2.getHullClass());
        Ship s31_9 = new Ship(enemy3, f3_1, template_3_2.getName(), enemy3.getNextShipId(), template_3_2.getHullClass());
        Ship s31_10 = new Ship(enemy3, f3_1, template_3_2.getName(), enemy3.getNextShipId(), template_3_2.getHullClass());
        Ship s31_11 = new Ship(enemy3, f3_1, template_3_2.getName(), enemy3.getNextShipId(), template_3_2.getHullClass());
        Ship s31_12 = new Ship(enemy3, f3_1, template_3_2.getName(), enemy3.getNextShipId(), template_3_2.getHullClass());
        s31_1.applyTemplate(template_3_1);
        s31_2.applyTemplate(template_3_1);
        s31_3.applyTemplate(template_3_1);
        s31_4.applyTemplate(template_3_1);
        s31_5.applyTemplate(template_3_1);
        s31_6.applyTemplate(template_3_1);
        s31_7.applyTemplate(template_3_1);
        s31_8.applyTemplate(template_3_2);
        s31_9.applyTemplate(template_3_2);
        s31_10.applyTemplate(template_3_2);
        s31_11.applyTemplate(template_3_2);
        s31_12.applyTemplate(template_3_2);
        s31_1.initShip();
        s31_2.initShip();
        s31_3.initShip();
        s31_4.initShip();
        s31_5.initShip();
        s31_6.initShip();
        s31_7.initShip();
        s31_8.initShip();
        s31_9.initShip();
        s31_10.initShip();
        s31_11.initShip();
        s31_12.initShip();
        f3_1.addShip(s31_1);
        f3_1.addShip(s31_2);
        f3_1.addShip(s31_3);
        f3_1.addShip(s31_4);
        f3_1.addShip(s31_5);
        f3_1.addShip(s31_6);
        f3_1.addShip(s31_7);
        f3_1.addShip(s31_8);
        f3_1.addShip(s31_9);
        f3_1.addShip(s31_10);
        f3_1.addShip(s31_11);
        f3_1.addShip(s31_12);
        enemy3.addFleet(f3_1);
        enemy3.getStarSystems().get(0).addFleet(f3_1);
        
        
        // Chang's roaming fleet
        StarSystem s = null;
        while(s == null) {
            Random prng = new Random();
            int x = prng.nextInt(galaxy.getHeight());
            int y = prng.nextInt(galaxy.getWidth());
            
            StarSystem tmp = galaxy.getSystem(x, y);
            if(tmp.getUser().equals(StaticData.nobodyUser) && tmp.getClassification().equals(SpaceObjectClassification.starsystem) &&
                    (tmp.getStarSystemClassification().equals(StarSystemClassification.empty) || tmp.getClassification().equals(StarSystemClassification.starSystem))) {
                s = tmp;
            }
            
        }
        
        Fleet f3_2 = new Fleet(enemy3, "changBeta", s);
        Ship s32_1 = new Ship(enemy3, f3_2, template_3_1.getName(), enemy3.getNextShipId(), template_3_1.getHullClass());
        Ship s32_2 = new Ship(enemy3, f3_2, template_3_1.getName(), enemy3.getNextShipId(), template_3_1.getHullClass());
        Ship s32_3 = new Ship(enemy3, f3_2, template_3_2.getName(), enemy3.getNextShipId(), template_3_2.getHullClass());
        Ship s32_4 = new Ship(enemy3, f3_2, template_3_2.getName(), enemy3.getNextShipId(), template_3_2.getHullClass());
        Ship s32_5 = new Ship(enemy3, f3_2, template_3_2.getName(), enemy3.getNextShipId(), template_3_2.getHullClass());
        Ship s32_6 = new Ship(enemy3, f3_2, template_3_2.getName(), enemy3.getNextShipId(), template_3_2.getHullClass());
        s32_1.applyTemplate(template_3_1);
        s32_2.applyTemplate(template_3_1);
        s32_3.applyTemplate(template_3_2);
        s32_4.applyTemplate(template_3_2);
        s32_5.applyTemplate(template_3_2);
        s32_6.applyTemplate(template_3_2);
        s32_1.initShip();
        s32_2.initShip();
        s32_3.initShip();
        s32_4.initShip();
        s32_5.initShip();
        s32_6.initShip();
        f3_2.addShip(s32_1);
        f3_2.addShip(s32_2);
        f3_2.addShip(s32_3);
        f3_2.addShip(s32_4);
        f3_2.addShip(s32_5);
        f3_2.addShip(s32_6);
        enemy3.addFleet(f3_2);
        s.addFleet(f3_2);
        
        
        File galaxyFile = new File(filename);
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(galaxyFile));
            oos.writeObject(galaxy);
            System.out.println("Galaxy successfully stored");
        } catch (IOException ex) {
            System.out.println("ERROR: unable to write galaxy file to disk");
            System.out.println(ex.getMessage());
        }
    }
}