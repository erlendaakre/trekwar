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
package com.frostvoid.trekwar.server.tools.export;

import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.TechnologyGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Exports all technologies in the game into a table using JSPWiki markup
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Tech2html {

    public static void main(String[] args) throws Exception {

        File htmlfile = new File("techs.txt");

        if (htmlfile.exists()) {
            System.out.println("file: " + htmlfile + " allready exists");
            System.exit(1);
        }
        System.out.println("Generating file:");

        //---------------------------------------------------------
        FileWriter fw = new FileWriter(htmlfile);
        PrintWriter out = new PrintWriter(fw);

        ArrayList<Technology> biotech = new ArrayList<Technology>();
        biotech.add(TechnologyGenerator.BIO_TECH0);
        biotech.add(TechnologyGenerator.BIO_TECH1);
        biotech.add(TechnologyGenerator.BIO_TECH2);
        biotech.add(TechnologyGenerator.BIO_TECH3);
        biotech.add(TechnologyGenerator.BIO_TECH4);
        biotech.add(TechnologyGenerator.BIO_TECH5);
        biotech.add(TechnologyGenerator.BIO_TECH6);
        biotech.add(TechnologyGenerator.BIO_TECH7);
        biotech.add(TechnologyGenerator.BIO_TECH8);
        biotech.add(TechnologyGenerator.BIO_TECH9);
        biotech.add(TechnologyGenerator.BIO_TECH10);


        ArrayList<Technology> energytech = new ArrayList<Technology>();
        energytech.add(TechnologyGenerator.ENERGY_TECH0);
        energytech.add(TechnologyGenerator.ENERGY_TECH1);
        energytech.add(TechnologyGenerator.ENERGY_TECH2);
        energytech.add(TechnologyGenerator.ENERGY_TECH3);
        energytech.add(TechnologyGenerator.ENERGY_TECH4);
        energytech.add(TechnologyGenerator.ENERGY_TECH5);
        energytech.add(TechnologyGenerator.ENERGY_TECH6);
        energytech.add(TechnologyGenerator.ENERGY_TECH7);
        energytech.add(TechnologyGenerator.ENERGY_TECH8);
        energytech.add(TechnologyGenerator.ENERGY_TECH9);


        ArrayList<Technology> computertech = new ArrayList<Technology>();
        computertech.add(TechnologyGenerator.COMPUTER_TECH0);
        computertech.add(TechnologyGenerator.COMPUTER_TECH1);
        computertech.add(TechnologyGenerator.COMPUTER_TECH2);
        computertech.add(TechnologyGenerator.COMPUTER_TECH3);
        computertech.add(TechnologyGenerator.COMPUTER_TECH4);
        computertech.add(TechnologyGenerator.COMPUTER_TECH5);
        computertech.add(TechnologyGenerator.COMPUTER_TECH6);
        computertech.add(TechnologyGenerator.COMPUTER_TECH7);
        computertech.add(TechnologyGenerator.COMPUTER_TECH8);
        computertech.add(TechnologyGenerator.COMPUTER_TECH9);
        computertech.add(TechnologyGenerator.COMPUTER_TECH10);

        ArrayList<Technology> propulsiontech = new ArrayList<Technology>();
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH0);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH1);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH2);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH3);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH4);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH5);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH6);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH7);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH8);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH9);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH10);
        propulsiontech.add(TechnologyGenerator.PROPULSION_TECH11);

        ArrayList<Technology> constructiontech = new ArrayList<Technology>();
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH0);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH1);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH2);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH3);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH4);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH5);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH6);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH7);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH8);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH9);
        constructiontech.add(TechnologyGenerator.CONSTRUCTION_TECH10);

        ArrayList<Technology> weapontech = new ArrayList<Technology>();
        weapontech.add(TechnologyGenerator.WEAPON_TECH0);
        weapontech.add(TechnologyGenerator.WEAPON_TECH1);
        weapontech.add(TechnologyGenerator.WEAPON_TECH2);
        weapontech.add(TechnologyGenerator.WEAPON_TECH3);
        weapontech.add(TechnologyGenerator.WEAPON_TECH4);
        weapontech.add(TechnologyGenerator.WEAPON_TECH5);
        weapontech.add(TechnologyGenerator.WEAPON_TECH6);
        weapontech.add(TechnologyGenerator.WEAPON_TECH7);
        weapontech.add(TechnologyGenerator.WEAPON_TECH8);
        weapontech.add(TechnologyGenerator.WEAPON_TECH9);


        out.println("[{TableOfContents}]\\\\");
        out.println("Technologies from Trekwar2, generated: " + new Date() + "\\\\");
        out.println("!!Biotechnology");
        for (int i = 0; i < biotech.size(); i++) {
            Technology t = biotech.get(i);
            out.println("!Level " + t.getLevel() + ": " + t.getName());
            out.println("Costs " + t.getResearchCost() + " research points)\\\\");
            out.println("[http://www.trekwar.org/images/techs/biotech" + t.getLevel() + ".png]\\\\\\");
            out.println(t.getDesscription() + "\\\\");

        }
        out.println("");
        out.println("");
        out.println("");


        out.println("[{TableOfContents}]\\\\");
        out.println("Technologies from Trekwar2, generated: " + new Date() + "\\\\");
        out.println("!!Energy Technology");
        for (int i = 0; i < energytech.size(); i++) {
            Technology t = energytech.get(i);
            out.println("!Level " + t.getLevel() + ": " + t.getName());
            out.println("Costs " + t.getResearchCost() + " research points)\\\\");
            out.println("[http://www.trekwar.org/images/techs/energy" + t.getLevel() + ".png]\\\\\\");
            out.println(t.getDesscription() + "\\\\");
        }
        out.println("");
        out.println("");
        out.println("");


        out.println("[{TableOfContents}]\\\\");
        out.println("Technologies from Trekwar2, generated: " + new Date() + "\\\\");
        out.println("!!Computer Technology");
        for (int i = 0; i < computertech.size(); i++) {
            Technology t = computertech.get(i);
            out.println("!Level " + t.getLevel() + ": " + t.getName());
            out.println("Costs " + t.getResearchCost() + " research points)\\\\");
            out.println("[http://www.trekwar.org/images/techs/computer" + t.getLevel() + ".png]\\\\\\");
            out.println(t.getDesscription() + "\\\\");
        }
        out.println("");
        out.println("");
        out.println("");


        out.println("[{TableOfContents}]\\\\");
        out.println("Technologies from Trekwar2, generated: " + new Date() + "\\\\");
        out.println("!!Propulsion Technology");
        for (int i = 0; i < propulsiontech.size(); i++) {
            Technology t = propulsiontech.get(i);
            out.println("!Level " + t.getLevel() + ": " + t.getName());
            out.println("Costs " + t.getResearchCost() + " research points)\\\\");
            out.println("[http://www.trekwar.org/images/techs/propulsion" + t.getLevel() + ".png]\\\\\\");
            out.println(t.getDesscription() + "\\\\");
        }
        out.println("");
        out.println("");
        out.println("");


        out.println("[{TableOfContents}]\\\\");
        out.println("Technologies from Trekwar2, generated: " + new Date() + "\\\\");
        out.println("!!Industrial Technology");
        for (int i = 0; i < constructiontech.size(); i++) {
            Technology t = constructiontech.get(i);
            out.println("!Level " + t.getLevel() + ": " + t.getName());
            out.println("Costs " + t.getResearchCost() + " research points)\\\\");
            out.println("[http://www.trekwar.org/images/techs/construction" + t.getLevel() + ".png]\\\\\\");
            out.println(t.getDesscription() + "\\\\");
        }
        out.println("");
        out.println("");
        out.println("");


        out.println("[{TableOfContents}]\\\\");
        out.println("Technologies from Trekwar2, generated: " + new Date() + "\\\\");
        out.println("!!Weapon Technology");
        for (int i = 0; i < weapontech.size(); i++) {
            Technology t = weapontech.get(i);
            out.println("!Level " + t.getLevel() + ": " + t.getName());
            out.println("Costs " + t.getResearchCost() + " research points)\\\\");
            out.println("[http://www.trekwar.org/images/techs/weapon" + t.getLevel() + ".png]\\\\\\");
            out.println(t.getDesscription() + "\\\\");
        }


        out.close();
    }
}

