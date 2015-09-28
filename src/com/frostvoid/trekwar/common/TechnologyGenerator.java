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

import java.util.ArrayList;

/**
 * Generates all the technology objects used in the game
 * 
 * TODO: used XML files instead of hardcoded data
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TechnologyGenerator {

    /**
     * Gets a list of all technologies
     *
     * @return all technologies
     */
    public static ArrayList<Technology> getAllTechs() {
        return allTechs;
    }

    /**
     * A list of technology types
     */
    public enum techType {
        biotech,
        computertech,
        energytech,
        propulsiontech,
        constructiontech,
        weaponstech
    }

    public static final String BIOTECH = "Biotechnology";
    public static final String COMPUTERTECH = "Computer technology";
    public static final String ENERGYTECH = "Energy technology";
    public static final String PROPULSIONTECH = "Propulsion technology";
    public static final String CONSTRUCTIONTECH = "Construction technology";
    public static final String WEAPONSTECH = "Weapons technology";
    public static final int MAX_BIOTECH = 10;
    public static final int MAX_COMPUTERTECH = 10;
    public static final int MAX_ENERGYTECH = 9;
    public static final int MAX_PROPULSIONTECH = 11;
    public static final int MAX_CONSTRUCTIONTECH = 10;
    public static final int MAX_WEAPONTECH = 9;
    private static final ArrayList<Technology> allTechs = new ArrayList<Technology>();
    public static final Technology BIO_TECH0 = makeBioTech0();
    public static final Technology BIO_TECH1 = makeBioTech1();
    public static final Technology BIO_TECH2 = makeBioTech2();
    public static final Technology BIO_TECH3 = makeBioTech3();
    public static final Technology BIO_TECH4 = makeBioTech4();
    public static final Technology BIO_TECH5 = makeBioTech5();
    public static final Technology BIO_TECH6 = makeBioTech6();
    public static final Technology BIO_TECH7 = makeBioTech7();
    public static final Technology BIO_TECH8 = makeBioTech8();
    public static final Technology BIO_TECH9 = makeBioTech9();
    public static final Technology BIO_TECH10 = makeBioTech10();
    public static final Technology COMPUTER_TECH0 = makeComputerTech0();
    public static final Technology COMPUTER_TECH1 = makeComputerTech1();
    public static final Technology COMPUTER_TECH2 = makeComputerTech2();
    public static final Technology COMPUTER_TECH3 = makeComputerTech3();
    public static final Technology COMPUTER_TECH4 = makeComputerTech4();
    public static final Technology COMPUTER_TECH5 = makeComputerTech5();
    public static final Technology COMPUTER_TECH6 = makeComputerTech6();
    public static final Technology COMPUTER_TECH7 = makeComputerTech7();
    public static final Technology COMPUTER_TECH8 = makeComputerTech8();
    public static final Technology COMPUTER_TECH9 = makeComputerTech9();
    public static final Technology COMPUTER_TECH10 = makeComputerTech10();
    public static final Technology ENERGY_TECH0 = makeEnergyTech0();
    public static final Technology ENERGY_TECH1 = makeEnergyTech1();
    public static final Technology ENERGY_TECH2 = makeEnergyTech2();
    public static final Technology ENERGY_TECH3 = makeEnergyTech3();
    public static final Technology ENERGY_TECH4 = makeEnergyTech4();
    public static final Technology ENERGY_TECH5 = makeEnergyTech5();
    public static final Technology ENERGY_TECH6 = makeEnergyTech6();
    public static final Technology ENERGY_TECH7 = makeEnergyTech7();
    public static final Technology ENERGY_TECH8 = makeEnergyTech8();
    public static final Technology ENERGY_TECH9 = makeEnergyTech9();
    public static final Technology PROPULSION_TECH0 = makePropulsionTech0();
    public static final Technology PROPULSION_TECH1 = makePropulsionTech1();
    public static final Technology PROPULSION_TECH2 = makePropulsionTech2();
    public static final Technology PROPULSION_TECH3 = makePropulsionTech3();
    public static final Technology PROPULSION_TECH4 = makePropulsionTech4();
    public static final Technology PROPULSION_TECH5 = makePropulsionTech5();
    public static final Technology PROPULSION_TECH6 = makePropulsionTech6();
    public static final Technology PROPULSION_TECH7 = makePropulsionTech7();
    public static final Technology PROPULSION_TECH8 = makePropulsionTech8();
    public static final Technology PROPULSION_TECH9 = makePropulsionTech9();
    public static final Technology PROPULSION_TECH10 = makePropulsionTech10();
    public static final Technology PROPULSION_TECH11 = makePropulsionTech11();
    public static final Technology CONSTRUCTION_TECH0 = makeConstructionTech0();
    public static final Technology CONSTRUCTION_TECH1 = makeConstructionTech1();
    public static final Technology CONSTRUCTION_TECH2 = makeConstructionTech2();
    public static final Technology CONSTRUCTION_TECH3 = makeConstructionTech3();
    public static final Technology CONSTRUCTION_TECH4 = makeConstructionTech4();
    public static final Technology CONSTRUCTION_TECH5 = makeConstructionTech5();
    public static final Technology CONSTRUCTION_TECH6 = makeConstructionTech6();
    public static final Technology CONSTRUCTION_TECH7 = makeConstructionTech7();
    public static final Technology CONSTRUCTION_TECH8 = makeConstructionTech8();
    public static final Technology CONSTRUCTION_TECH9 = makeConstructionTech9();
    public static final Technology CONSTRUCTION_TECH10 = makeConstructionTech10();
    public static final Technology WEAPON_TECH0 = makeWeaponTechnology0();
    public static final Technology WEAPON_TECH1 = makeWeaponTechnology1();
    public static final Technology WEAPON_TECH2 = makeWeaponTechnology2();
    public static final Technology WEAPON_TECH3 = makeWeaponTechnology3();
    public static final Technology WEAPON_TECH4 = makeWeaponTechnology4();
    public static final Technology WEAPON_TECH5 = makeWeaponTechnology5();
    public static final Technology WEAPON_TECH6 = makeWeaponTechnology6();
    public static final Technology WEAPON_TECH7 = makeWeaponTechnology7();
    public static final Technology WEAPON_TECH8 = makeWeaponTechnology8();
    public static final Technology WEAPON_TECH9 = makeWeaponTechnology9();

    static {
        getAllTechs().add(BIO_TECH0);
        getAllTechs().add(BIO_TECH1);
        getAllTechs().add(BIO_TECH2);
        getAllTechs().add(BIO_TECH3);
        getAllTechs().add(BIO_TECH4);
        getAllTechs().add(BIO_TECH5);
        getAllTechs().add(BIO_TECH6);
        getAllTechs().add(BIO_TECH7);
        getAllTechs().add(BIO_TECH8);
        getAllTechs().add(BIO_TECH9);
        getAllTechs().add(BIO_TECH10);

        getAllTechs().add(COMPUTER_TECH0);
        getAllTechs().add(COMPUTER_TECH1);
        getAllTechs().add(COMPUTER_TECH2);
        getAllTechs().add(COMPUTER_TECH3);
        getAllTechs().add(COMPUTER_TECH4);
        getAllTechs().add(COMPUTER_TECH5);
        getAllTechs().add(COMPUTER_TECH6);
        getAllTechs().add(COMPUTER_TECH7);
        getAllTechs().add(COMPUTER_TECH8);
        getAllTechs().add(COMPUTER_TECH9);
        getAllTechs().add(COMPUTER_TECH10);


        getAllTechs().add(CONSTRUCTION_TECH0);
        getAllTechs().add(CONSTRUCTION_TECH1);
        getAllTechs().add(CONSTRUCTION_TECH2);
        getAllTechs().add(CONSTRUCTION_TECH3);
        getAllTechs().add(CONSTRUCTION_TECH4);
        getAllTechs().add(CONSTRUCTION_TECH5);
        getAllTechs().add(CONSTRUCTION_TECH6);
        getAllTechs().add(CONSTRUCTION_TECH7);
        getAllTechs().add(CONSTRUCTION_TECH8);
        getAllTechs().add(CONSTRUCTION_TECH9);
        getAllTechs().add(CONSTRUCTION_TECH10);

        getAllTechs().add(ENERGY_TECH0);
        getAllTechs().add(ENERGY_TECH1);
        getAllTechs().add(ENERGY_TECH2);
        getAllTechs().add(ENERGY_TECH3);
        getAllTechs().add(ENERGY_TECH4);
        getAllTechs().add(ENERGY_TECH5);
        getAllTechs().add(ENERGY_TECH6);
        getAllTechs().add(ENERGY_TECH7);
        getAllTechs().add(ENERGY_TECH8);
        getAllTechs().add(ENERGY_TECH9);

        getAllTechs().add(PROPULSION_TECH0);
        getAllTechs().add(PROPULSION_TECH1);
        getAllTechs().add(PROPULSION_TECH2);
        getAllTechs().add(PROPULSION_TECH3);
        getAllTechs().add(PROPULSION_TECH4);
        getAllTechs().add(PROPULSION_TECH5);
        getAllTechs().add(PROPULSION_TECH6);
        getAllTechs().add(PROPULSION_TECH7);
        getAllTechs().add(PROPULSION_TECH8);
        getAllTechs().add(PROPULSION_TECH9);
        getAllTechs().add(PROPULSION_TECH10);
        getAllTechs().add(PROPULSION_TECH11);

        getAllTechs().add(WEAPON_TECH0);
        getAllTechs().add(WEAPON_TECH1);
        getAllTechs().add(WEAPON_TECH2);
        getAllTechs().add(WEAPON_TECH3);
        getAllTechs().add(WEAPON_TECH4);
        getAllTechs().add(WEAPON_TECH5);
        getAllTechs().add(WEAPON_TECH6);
        getAllTechs().add(WEAPON_TECH7);
        getAllTechs().add(WEAPON_TECH8);
        getAllTechs().add(WEAPON_TECH9);
    }

    //#########################  B I O T E C H   =########################\\
    private static Technology makeBioTech0() {
        return new Technology(techType.biotech, 0, 0, "Basic Biotechnology",
                "This field covers the basic sciences of biotechnology, " +
                "physiology, genetics, meteorology and terraforming");
    }

    private static Technology makeBioTech1() {
        return new Technology(techType.biotech, 1, 600, "Hydroponics",
                "Allows plants and useful micro-organisms " + "to grow in completely controlled environments, " + "using an moist inert medium instead of soil.\n" + "Allows the production of large amount of fruit " + "and vegetables in confined spaces like starships, " + "outposts, and on inhospitable planets");
    }

    private static Technology makeBioTech2() {
        return new Technology(techType.biotech, 2, 1400, "Bionics",
                "Bionics allows for the synthetic growth of organic " + "tissue, and stimulating existing tissue into " + "regenerating faster");
    }

    private static Technology makeBioTech3() {
        return new Technology(techType.biotech, 3, 3000, "Advanced genetics",
                "Aids scientists in creating organisms with a " + "specific function, and even writing DNA from scratch.\n" + "New micro-organisms can benefit agriculture and terraforming");
    }

    private static Technology makeBioTech4() {
        return new Technology(techType.biotech, 4, 5000, "Metagenics",
                "Incredible advance in terraforming, allows for " + "protogenic matter to be used safely, to create " + "a stable planet with a fast growing ecosystem");
    }

    private static Technology makeBioTech5() {
        return new Technology(techType.biotech, 5, 9000, "Weather control",
                "Planet-wide system of substations that control " + "atmospheric humidity, cloud patterns, thermal " + "changes and wind currents." + "Makes agriculture more efficient, and is also used " + "in terraforming new worlds");
    }

    private static Technology makeBioTech6() {
        return new Technology(techType.biotech, 6, 15000, "Atmospheric conversion",
                "Planet-wide system of automated stations, used in the early " + "phase of terraforming to change the pressure and " + "composition of a planets atmosphere");
    }

    private static Technology makeBioTech7() {
        return new Technology(techType.biotech, 7, 22000, "Biofiltration",
                "This advance in genetic research allows predicting " + "the behaviour of a viral/bacteriological agent in " + "an environment.  Can be used to remove harmful " + "organisms from a transporter signal and from " + "matter like water and agricultural products");
    }

    private static Technology makeBioTech8() {
        return new Technology(techType.biotech, 8, 34000, "Dynamic ecological modification",
                "New research in planetary ecology has provided a " + "greater understanding of how planets and ecosystems " + "function as a whole. This new knowledge can be " + "applied to terraforming to increase performance");
    }

    private static Technology makeBioTech9() {
        return new Technology(techType.biotech, 9, 50000, "Advanced Exobiology",
                "Studying the evolution of alien life forms, " + "scientists have gained a new understanding of " + "carbon-based genetics, and advanced the area of " + "bio-engineering years forward");
    }

    private static Technology makeBioTech10() {
        return new Technology(techType.biotech, 10, 80000, "Advanced nanotechnology",
                "These microscopic robots are small enough to " + "enter living cells, and manipulate them." + "A new generation of nanites have increased their " + "capacity and level of sophistication, so that they " + "can do extremely complicated operations, like " + "self-modification, and rewriting DNA");
    }

    //#########################  C O M P U T E R T E C H   =########################\\
    private static Technology makeComputerTech0() {
        return new Technology(techType.computertech, 0, 0, "Basic computer technology",
                "Computers do vast amounts of mathematical " + "calculations at very high speeds. Used for " + "scientific purposes, automating tasks and " + "providing control or logical behaviour to " + "a wide range of applications");
    }

    private static Technology makeComputerTech1() {
        return new Technology(techType.computertech, 1, 600, "Optical computing",
                "Using optical signals instead of electronic signals " + "allows for denser computer circuits to be manufactured, " + "thus increasing performance.");
    }

    private static Technology makeComputerTech2() {
        return new Technology(techType.computertech, 2, 1200, "Linguistic interfacing",
                "Linguistic interfacing allows people to " + "communicate with computer systems using natural " + "language.\n This means starship computers can be " + "programmed by officers without extensive training " + "in computer programming");
    }

    private static Technology makeComputerTech3() {
        return new Technology(techType.computertech, 3, 3100, "Molecular data imaging",
                "Allows computers to reliably store physical " + "matter, in the form of a data pattern");
    }

    private static Technology makeComputerTech4() {
        return new Technology(techType.computertech, 4, 8000, "Duotronics",
                "Revolutionary computer technology invented by " + "Dr. Richard Daystrom, used abord starships, and " + "in research laboratories");
    }

    private static Technology makeComputerTech5() {
        return new Technology(techType.computertech, 5, 13000, "Analogue computing",
                "Using new circuitry, it is possible to use other " + "number bases than binary, thus increasing the " + "speed of the computer without needing to add more " + "circuits, or increasing the frequency");
    }

    private static Technology makeComputerTech6() {
        return new Technology(techType.computertech, 6, 22000, "Isolinear optical chips",
                "These sophisticated information storage and " + "processing devices are composed of a linear " + "memory crystal, and has extremely low access time.\n " + "These chips can be combined to create large and " + "powerful computer systems with enormous amounts of " + "memory");
    }

    private static Technology makeComputerTech7() {
        return new Technology(techType.computertech, 7, 35000, "Circuit distortion",
                "A non propulsive warp field distorts the circuits of " + "a large computer system, the field does not move the " + "computer itself, but allows the signals inside to " + "operate at faster than the speed of light");
    }

    private static Technology makeComputerTech8() {
        return new Technology(techType.computertech, 8, 50000, "Applied Ultra conduction",
                "Ultra conductive materials used in industry and high " + "power systems, can now be used in computer systems.\n" + "This reduces energy usage, , and allows for computers to have more " + "logical and computational circuitry than before");
    }

    private static Technology makeComputerTech9() {
        return new Technology(techType.computertech, 9, 70000, "Bio-neural circuitry",
                "Advanced computer technology using synthetic neural " + "cells for data processing.\n" + "This technique Organizes and process complex information " + "faster and more efficiently than traditional optical processors");
    }

    private static Technology makeComputerTech10() {
        return new Technology(techType.computertech, 10, 120000, "Quantum computing",
                "Allows for extremely complex computer systems that use quantum states to compute " + "and store data. This allows for an extreme storage capacity and an astonishing increase " + "in computing speed");
    }

    //#########################  E N E R G Y    T E C H   =########################\\
    private static Technology makeEnergyTech0() {
        return new Technology(techType.energytech, 0, 0, "Basic energy technology",
                "The control and generation of power.\n" + "category includes standard general purpose power, " + "starship/planetary shields and shield generation");
    }

    private static Technology makeEnergyTech1() {
        return new Technology(techType.energytech, 1, 600, "Force fields",
                "Complex magnetic fields which impart the inertia " + "of charged particles, both energy and matter.\n" + "Used to contain/move hazardous materials " + "and deflect energy. Used in energy production " + "and defensive shields");
    }

    private static Technology makeEnergyTech2() {
        return new Technology(techType.energytech, 2, 1200, "Subspace physics",
                "Allows scientists to predict the behaviour " + "of energy in subspace bands, and exploit this " + "knowledge to increase energy production and " + "starship shielding");
    }

    private static Technology makeEnergyTech3() {
        return new Technology(techType.energytech, 3, 3100, "Advanced matter convertion",
                "Allows convertion between matter and energy very " + "fast and with low power consumption.\n" + "Advances transporter and replicator technology");
    }

    private static Technology makeEnergyTech4() {
        return new Technology(techType.energytech, 4, 7500, "Graviton physics",
                "Breaktrough in the control and generation of " + "artificial gravity");
    }

    private static Technology makeEnergyTech5() {
        return new Technology(techType.energytech, 5, 13000, "Advanced power distribution",
                "New containment procedures makes the use of " + "high-energy plasma safer and simpler.\n" + "allows for high-energy plasma power in populated " + "areas, as well as in more sections of starships");
    }

    private static Technology makeEnergyTech6() {
        return new Technology(techType.energytech, 6, 25000, "Antimatter fusion",
                "The advance in containment fields allows antimatter " + "to be fused into larger nuclei than anti-hydrogen.\n " + "these larger anti-nucleis produce more energy per " + "reaction unit, than simpler ones");
    }

    private static Technology makeEnergyTech7() {
        return new Technology(techType.energytech, 7, 40000, "Ultraconductors",
                "Allows for the creation of ultraconductive " + "materials, which have a negative resistance, " + "and actually add to the energy traversing " + "the conductor. Makes high-energy systems like " + "shields/weapons/engines more efficient");
    }

    private static Technology makeEnergyTech8() {
        return new Technology(techType.energytech, 8, 65000, "Wave function analasys",
                "All matter is energy, and all energy exist at " + "probabilistic waves, which in turn means that " + "everything can be treated as a wave function.\n" + "Wave function analysis is the direct observation " + "of these waves, and can be used to increase many " + "aspects of power generation and control");
    }

    private static Technology makeEnergyTech9() {
        return new Technology(techType.energytech, 9, 100000, "Quantum determinacy",
                "This remarkable science allows physicists to " + "force a space/time event, as long as its quantum " + "probability is above a certain threshold.\n" + "As this science advances, the threshold aproaches " + "zero");
    }

    //#########################  P R O P U L S I O N T E C H   =########################\\
    private static Technology makePropulsionTech0() {
        return new Technology(techType.propulsiontech, 0, 0, "Basic propulsion technology",
                "This science deals with the movement of starships " + "both impulse and faster than light (warp) speed.\n" + "Increases the speed and combat rating of ships");
    }

    private static Technology makePropulsionTech1() {
        return new Technology(techType.propulsiontech, 1, 600, "Warp drive",
                "Warp drive systems uses a controlled annihilation " + "of matter and antimatter, regulated by dilithium " + "chrystals, to form a warp field around a ship.\n " + "While inside this subspace \"bubble\" a ship does " + "not have to conform to the laws of physics, as " + "they are defined under space-normal conditions, " + "and is able move at speeds faster than light");
    }

    private static Technology makePropulsionTech2() {
        return new Technology(techType.propulsiontech, 2, 1200, "Advanced Structural integrity fields",
                "Shaped force fields on starships are used to " + "supplement the mechanical strength of the ship's " + "hull.\nAllows ships to increase accelerations, " + "and also add to the ships combat rating");
    }

    private static Technology makePropulsionTech3() {
        return new Technology(techType.propulsiontech, 3, 3000, "Dilithium recrystallization",
                "Allows dilithium to rechrystallize by exposing " + "it to high-energy photons (gamma radiation).\n" + "This allows for starships to increase the input " + "of the matter/antimatter reaction chamber, and " + "save dilithium which is extremely rare");
    }

    private static Technology makePropulsionTech4() {
        return new Technology(techType.propulsiontech, 4, 7000, "Theta-matrix compositing",
                "Advances in dilithium recrystallization techniques " + "allows for the creation of synthetic dilithium and " + "more efficient recrystallization.\n" + "Crystals can now be recomposited while in the " + "articulation frame");
    }

    private static Technology makePropulsionTech5() {
        return new Technology(techType.propulsiontech, 5, 12000, "Advanced field manipulation",
                "Instead of using a single warpfield, new improvements " + "in field manipulation allows a ship to form multiple " + "fields in a layerd fashion.\n" + "This saves power, and allows for ships to increase " + "warp speeds and, hold larger speeds for longer time " + "intervals");
    }

    private static Technology makePropulsionTech6() {
        return new Technology(techType.propulsiontech, 6, 20000, "Subspace field geometry",
                "Allows for better control of the warpfield.\n " + "The geometry of the field can be shaped after the " + "ship, thus using a smaller field that uses less " + "energy");
    }

    private static Technology makePropulsionTech7() {
        return new Technology(techType.propulsiontech, 7, 30000, "Advanced plasma injection",
                "By using new construction methods a new type " + "of plasma injector can be constructed, which will " + "provide larger amounts of plasma to the warp coils " + ", allowing stronger subspace fields, and faster " + "speeds");
    }

    private static Technology makePropulsionTech8() {
        return new Technology(techType.propulsiontech, 8, 45000, "Warpfield compression",
                "Allows starships to form fields with the form of " + "very narrow ellipses, bringing them even closer " + "to the ship, improving efficiency");
    }

    private static Technology makePropulsionTech9() {
        return new Technology(techType.propulsiontech, 9, 80000, "Advanced articulation frame",
                "This improvement of the old models, allow for the " + "crystal lattice direction to be adjusted\n" + "Allowing a larger matter/antimatter stream to" + "be used in the reaction chamber, increasing the " + "energy output");
    }

    private static Technology makePropulsionTech10() {
        return new Technology(techType.propulsiontech, 10, 110000, "Improved subspace coils",
                "Stronger alloys used in the construction of the " + "subspace/warp coils, allows them to be bombarded " + "by higher amounts of plasma than before.\n" + "This makes them able to create stronger warp fields");
    }

    private static Technology makePropulsionTech11() {
        return new Technology(techType.propulsiontech, 11, 160000, "Transwarp technology",
                "Scientists have managed to control the point of " + "exit to normal space when in transwarp, and have " + "developed shielding that allows organic matter to " + "exist safely in transwarp.\n" + "Starships using transwarp can travel at extremely " + "fast speeds");
    }

    //#########################  C O N S T R U C T I O N T E C H   =########################\\
    private static Technology makeConstructionTech0() {
        return new Technology(techType.constructiontech, 0, 0, "Basic construction technology",
                "Construction technology primarily covers " + "fabrication methods, architecture and design.\n" + "Increases production capability of factories, and " + "influences the durability of starships and equipment");
    }

    private static Technology makeConstructionTech1() {
        return new Technology(techType.constructiontech, 1, 600, "Gamma welding",
                "Unlike conventional welding, where materials are partially melted together, gamma welding bonds materials through nuclear interaction controlled by high-level gamma-radiation");
    }

    private static Technology makeConstructionTech2() {
        return new Technology(techType.constructiontech, 2, 1200, "Advanced Structural integrity",
                "Shaped force fields on starships are used to " + "supplement the mechanical strength of the ship's " + "hull.\nallowing the ship to accelerate more rapidly, " + "and adds to the ship combat rating");
    }

    private static Technology makeConstructionTech3() {
        return new Technology(techType.constructiontech, 3, 4000, "Duranium alloys",
                "This extremely strong metal alloy can be used in " + "ship construction to make more durable hulls");
    }

    private static Technology makeConstructionTech4() {
        return new Technology(techType.constructiontech, 4, 9000, "Phase transition bonding",
                "Materials can now be bonded together by altering " + "the phase of one of them momentarily, and phasing it " + "back when it has intersected with the other part.\n" + "The resulting constructions density at the intersection " + "point has the combined matter density of the joined parts");
    }

    private static Technology makeConstructionTech5() {
        return new Technology(techType.constructiontech, 5, 15000, "Nuclear epitax",
                "Nuclear epitax allows for solid matter to be " + "reconstructed at the subatomic level without using " + "conventional energy-consuming methods such as the " + "transporter or replicator.\n" + "This makes the construction of durable materials " + "more efficient");
    }

    private static Technology makeConstructionTech6() {
        return new Technology(techType.constructiontech, 6, 25000, "Diffusion bonding",
                "Advances in phase transition allows for phasing " + "matter to diffuse the density of the construction " + "over the entire compound, as opposed of just in the " + "intersection points.\n" + "This allows for uniform density in complex alloys " + "used for starship construction");
    }

    private static Technology makeConstructionTech7() {
        return new Technology(techType.constructiontech, 7, 40000, "Electron bonding",
                "Sophisticated welding process producing extremely " + "strong electron bonds in matter, without causing " + "damage to it");
    }

    private static Technology makeConstructionTech8() {
        return new Technology(techType.constructiontech, 8, 60000, "Quantum epitax",
                "Advancements in nuclear epitaxy, now allow us to " + "adjust the quantum states of materials on a large " + "scale.\nVirtually any matter can be constructed " + "using this technique");
    }

    private static Technology makeConstructionTech9() {
        return new Technology(techType.constructiontech, 9, 110000, "Holographic construction",
                "Advances in holographic technology allows for large scale " + "construction to be carried out by holographic " + "machinery, which can be created and modified at " + "an instance");
    }

    private static Technology makeConstructionTech10() {
        return new Technology(techType.constructiontech, 10, 180000, "Neutronium alloys",
                "It is now possible to force the formation of " + "neutronium, which previously has only existed in neutron stars.\n" + "This new alloy has an incredible density, and allow for " + "an extreme increase in the durability of starships and " + "planetary structures");
    }

    //#########################  W E A P O N S T E C H   =########################\\
    private static Technology makeWeaponTechnology0() {
        return new Technology(techType.weaponstech, 0, 0, "Basic weaponstechnology",
                "Weapons technology an applied physical science " + "primarily concerned with large-scale discharge and " + "absorbation of destructive enery.\n" + "Affects ship weapons, armor and shields");
    }

    private static Technology makeWeaponTechnology1() {
        return new Technology(techType.weaponstech, 1, 600, "Beam emitters",
                "Beam emitters release destructive energy in tightly focused beams with minimal dispersion.\nThis allows for powerful laser-like beams that with a range of thousands of kilometers");
    }

    private static Technology makeWeaponTechnology2() {
        return new Technology(techType.weaponstech, 2, 1200, "Torpedo hardening",
                "Hardened torpedo casings allow the warhead to " + "momentarily enter the enemy shield enough to " + "detonate inside it, and doing additional damage");
    }

    private static Technology makeWeaponTechnology3() {
        return new Technology(techType.weaponstech, 3, 4000, "Advanced shielding",
                "Advances in shield generator technology allows " + "for the new generation of shields to allow quick " + "nutation changes in the shield-frequency");
    }

    private static Technology makeWeaponTechnology4() {
        return new Technology(techType.weaponstech, 4, 9000, "Multifrequency beams",
                "Shields are less effective when their resonance " + "frequency is the same as the incoming beam.\n" + "Multifrequency beams operate at several frequencies" + ", and therefor increasing the chance that part of the " + "beam will match the shield harmonics and do more " + "damage");
    }

    private static Technology makeWeaponTechnology5() {
        return new Technology(techType.weaponstech, 5, 20000, "Shield regeneration", "Increases the regeneration time of damaged shields, by using a new type of shield generators");
    }

    private static Technology makeWeaponTechnology6() {
        return new Technology(techType.weaponstech, 6, 35000, "Micropulsing",
                "Beams are fired in very short bursts instead of " + "in a continous beam. The difference is visually undetectable, but energy drain on the shields are " + "increased");
    }

    private static Technology makeWeaponTechnology7() {
        return new Technology(techType.weaponstech, 7, 50000, "Ablative armor",
                "This new type of armor vaporizes when in it is " + "fired upon thereby dissipating energy and giving much better protection than the old type of armor");
    }

    private static Technology makeWeaponTechnology8() {
        return new Technology(techType.weaponstech, 8, 75000, "Ultraconductive emitters", "By constructing the beam emitters with " + "ultraconductive materials, energy is added to the current, as the beam is fired");
    }

    private static Technology makeWeaponTechnology9() {
        return new Technology(techType.weaponstech, 9, 130000, "Quantum-torpedos",
                "Advanced weaponry that employs an energetic " + "local release of the zero point energy field.\n" + "This quantum effect tends to be more effective against penetrating shields, than conventional antimatter explosions");
    }

    /**
     * Gets a technology by it's string representation
     *
     * @param stringRepresentation the name
     * @return the technology or null if not found
     */
    public static Technology getTech(String stringRepresentation) {
        for (Technology t : getAllTechs()) {
            if (t.toString().equals(stringRepresentation)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Gets the name of a technology type
     *
     * @param type the type
     * @return a human readable name
     */
    public static String getTechTypeName(techType type) {
        if (type == techType.biotech) {
            return BIOTECH;
        } else if (type == techType.computertech) {
            return COMPUTERTECH;
        } else if (type == techType.constructiontech) {
            return CONSTRUCTIONTECH;
        } else if (type == techType.energytech) {
            return ENERGYTECH;
        } else if (type == techType.propulsiontech) {
            return PROPULSIONTECH;
        } else if (type == techType.weaponstech) {
            return WEAPONSTECH;
        } else {
            return "INVALID TECH NAME";
        }
    }

    /**
     * Gets a Technology by type and level
     *
     * @param type the type of technology
     * @param level the level
     *
     * @return the technology or null if not found
     */
    public static Technology getTech(techType type, int level) {
        Technology selectedTechObject = null;
        if (type.equals(techType.biotech)) {
            switch (level) {
                case 0:
                    selectedTechObject = BIO_TECH0;
                    break;
                case 1:
                    selectedTechObject = BIO_TECH1;
                    break;
                case 2:
                    selectedTechObject = BIO_TECH2;
                    break;
                case 3:
                    selectedTechObject = BIO_TECH3;
                    break;
                case 4:
                    selectedTechObject = BIO_TECH4;
                    break;
                case 5:
                    selectedTechObject = BIO_TECH5;
                    break;
                case 6:
                    selectedTechObject = BIO_TECH6;
                    break;
                case 7:
                    selectedTechObject = BIO_TECH7;
                    break;
                case 8:
                    selectedTechObject = BIO_TECH8;
                    break;
                case 9:
                    selectedTechObject = BIO_TECH9;
                    break;
                case 10:
                    selectedTechObject = BIO_TECH10;
                    break;
            }
        }

        if (type.equals(techType.computertech)) {
            switch (level) {
                case 0:
                    selectedTechObject = COMPUTER_TECH0;
                    break;
                case 1:
                    selectedTechObject = COMPUTER_TECH1;
                    break;
                case 2:
                    selectedTechObject = COMPUTER_TECH2;
                    break;
                case 3:
                    selectedTechObject = COMPUTER_TECH3;
                    break;
                case 4:
                    selectedTechObject = COMPUTER_TECH4;
                    break;
                case 5:
                    selectedTechObject = COMPUTER_TECH5;
                    break;
                case 6:
                    selectedTechObject = COMPUTER_TECH6;
                    break;
                case 7:
                    selectedTechObject = COMPUTER_TECH7;
                    break;
                case 8:
                    selectedTechObject = COMPUTER_TECH8;
                    break;
                case 9:
                    selectedTechObject = COMPUTER_TECH9;
                    break;
                case 10:
                    selectedTechObject = COMPUTER_TECH10;
                    break;

            }
        }


        if (type.equals(techType.energytech)) {
            switch (level) {
                case 0:
                    selectedTechObject = ENERGY_TECH0;
                    break;
                case 1:
                    selectedTechObject = ENERGY_TECH1;
                    break;
                case 2:
                    selectedTechObject = ENERGY_TECH2;
                    break;
                case 3:
                    selectedTechObject = ENERGY_TECH3;
                    break;
                case 4:
                    selectedTechObject = ENERGY_TECH4;
                    break;
                case 5:
                    selectedTechObject = ENERGY_TECH5;
                    break;
                case 6:
                    selectedTechObject = ENERGY_TECH6;
                    break;
                case 7:
                    selectedTechObject = ENERGY_TECH7;
                    break;
                case 8:
                    selectedTechObject = ENERGY_TECH8;
                    break;
                case 9:
                    selectedTechObject = ENERGY_TECH9;
                    break;
            }
        }

        if (type.equals(techType.propulsiontech)) {
            switch (level) {
                case 0:
                    selectedTechObject = PROPULSION_TECH0;
                    break;
                case 1:
                    selectedTechObject = PROPULSION_TECH1;
                    break;
                case 2:
                    selectedTechObject = PROPULSION_TECH2;
                    break;
                case 3:
                    selectedTechObject = PROPULSION_TECH3;
                    break;
                case 4:
                    selectedTechObject = PROPULSION_TECH4;
                    break;
                case 5:
                    selectedTechObject = PROPULSION_TECH5;
                    break;
                case 6:
                    selectedTechObject = PROPULSION_TECH6;
                    break;
                case 7:
                    selectedTechObject = PROPULSION_TECH7;
                    break;
                case 8:
                    selectedTechObject = PROPULSION_TECH8;
                    break;
                case 9:
                    selectedTechObject = PROPULSION_TECH9;
                    break;
                case 10:
                    selectedTechObject = PROPULSION_TECH10;
                    break;
                case 11:
                    selectedTechObject = PROPULSION_TECH11;
                    break;
            }
        }

        if (type.equals(techType.constructiontech)) {
            switch (level) {
                case 0:
                    selectedTechObject = CONSTRUCTION_TECH0;
                    break;
                case 1:
                    selectedTechObject = CONSTRUCTION_TECH1;
                    break;
                case 2:
                    selectedTechObject = CONSTRUCTION_TECH2;
                    break;
                case 3:
                    selectedTechObject = CONSTRUCTION_TECH3;
                    break;
                case 4:
                    selectedTechObject = CONSTRUCTION_TECH4;
                    break;
                case 5:
                    selectedTechObject = CONSTRUCTION_TECH5;
                    break;
                case 6:
                    selectedTechObject = CONSTRUCTION_TECH6;
                    break;
                case 7:
                    selectedTechObject = CONSTRUCTION_TECH7;
                    break;
                case 8:
                    selectedTechObject = CONSTRUCTION_TECH8;
                    break;
                case 9:
                    selectedTechObject = CONSTRUCTION_TECH9;
                    break;
                case 10:
                    selectedTechObject = CONSTRUCTION_TECH10;
                    break;
            }
        }


        if (type.equals(techType.weaponstech)) {
            switch (level) {
                case 0:
                    selectedTechObject = WEAPON_TECH0;
                    break;
                case 1:
                    selectedTechObject = WEAPON_TECH1;
                    break;
                case 2:
                    selectedTechObject = WEAPON_TECH2;
                    break;
                case 3:
                    selectedTechObject = WEAPON_TECH3;
                    break;
                case 4:
                    selectedTechObject = WEAPON_TECH4;
                    break;
                case 5:
                    selectedTechObject = WEAPON_TECH5;
                    break;
                case 6:
                    selectedTechObject = WEAPON_TECH6;
                    break;
                case 7:
                    selectedTechObject = WEAPON_TECH7;
                    break;
                case 8:
                    selectedTechObject = WEAPON_TECH8;
                    break;
                case 9:
                    selectedTechObject = WEAPON_TECH9;
                    break;
            }
        }
        return selectedTechObject;
    }
}