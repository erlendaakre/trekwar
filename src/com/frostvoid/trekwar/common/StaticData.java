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

import com.frostvoid.trekwar.common.orders.ColonizeOrder;
import com.frostvoid.trekwar.common.shipComponents.*;
import com.frostvoid.trekwar.common.shipHulls.*;
import com.frostvoid.trekwar.common.structures.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Holds all the static information in the game (for both server and client).
 * for example: factions, hulls, structures and ship components
 * <p>
 * TODO: move data to XML file
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class StaticData {

    public static final DecimalFormat DECIMAL_FORMAT_2D = new DecimalFormat("0.00");

    public static final int MAX_NEBULA_RESOURCES = 65000;
    public static final int MAX_ASTEROID_RESOURCES = 50000;

    public static final int MAX_STRUCTURES_NEEDED_TO_COLONIZE = 10; // a system can only be colonized if it has at least this many structure slots
    public static final int RESEARCH_LOSS_CHANCE = 5; //If a system has negative research, this is the chance that this will cause a empire wide research loss

    public static final int SHIP_UPKEEP_BASE_VALUE = 10; // fixed amount of upkeep generated for user
    public static final int SHIP_UPKEEP_COST_FACTOR = 200; // ship upkeep is ship cost (including components) divided by this amount
    public static final int SHIP_UPKEEP_PENALTY_CONSTRUCTION_DELAY_CHANCE = 25; // chance to not progress ship build order

    public static final int SHIP_DECOMMISSION_FACTOR_INDUSTRY_TO_SHIP = 4; // decommissioned ship returned industry is divided by this amount.. 4 = 25% industry back
    public static final int SHIP_DECOMMISSION_FACTOR_INDUSTRY_TO_STRUCTURE = 8;

    public static final int SHIP_UPKEEP_PENALTY_REFUEL_FAIL_CHANCE = 30;
    public static final int SHIP_UPKEEP_PENALTY_REPAIR_FAIL_CHANCE = 70;
    public static final int SHIP_UPKEEP_PENALTY_RECREW_FAIL_CHANCE = 30;
    public static final int SHIP_UPKEEP_PENALTY_MORALE_LOSS_CHANCE = 25;

    public static final int SHIP_UPKEEP_PENALTY_SENSOR_FAIL_CHANCE = 30;
    public static final int SHIP_UPKEEP_PENALTY_SHIELD_FAIL_CHANCE = 10;
    public static final int SHIP_UPKEEP_PENALTY_LIGHT_DAMAGE_CHANCE = 6;
    public static final int SHIP_UPKEEP_PENALTY_MODERATE_DAMAGE_CHANCE = 4;
    public static final int SHIP_UPKEEP_PENALTY_HEAVY_DAMAGE_CHANCE = 2;

    // special users
    public static final User nobodyUser; // owns all empty starsystems by default

    // factions
    public static final Faction federation;
    public static final Faction klingon;
    public static final Faction romulan;
    public static final Faction cardassian;
    public static final Faction dominion;
    public static final Faction nobodyFaction;

    // hull classes
    public static final ArrayList<HullClass> allHullClasses;
    public static final HullClass civilian_scoutship;
    public static final HullClass civilian_colonyship;
    public static final HullClass civilian_freighter;
    public static final HullClass civilian_barge;
    public static final HullClass civilian_carrier;
    public static final HullClass federation_oberth;
    public static final HullClass federation_miranda;
    public static final HullClass federation_constitution;
    public static final HullClass federation_excelsior;
    public static final HullClass federation_galaxy;
    public static final HullClass federation_defiant;
    public static final HullClass klingon_raptor;
    public static final HullClass klingon_brel;
    public static final HullClass klingon_kvort;
    public static final HullClass klingon_ktinga;
    public static final HullClass klingon_vorcha;
    public static final HullClass klingon_neghvar;

    // structures
    public static final ArrayList<Structure> allStructures;
    public static final Farm farm1;
    public static final Factory factory1;
    public static final PowerPlant power1;
    public static final Laboratory lab1;
    public static final OreRefinery oreRefinery1;
    public static final OreSilo oreSilo1, oreSilo2, oreSilo3;
    public static final MilitaryOutpost militaryOutpost1;
    public static final SubspaceScanner subspaceScanner1;
    public static final Shipyard shipyard1;
    public static final DeuteriumSilo deuteriumSilo1, deuteriumSilo2, deuteriumSilo3;
    public static final DeuteriumPlant deuteriumProcessingPlant1;
    public static final WeatherModificationGrid weatherModificationNetwork1;
    public static final Bunker bunker1, bunker2, bunker3;

    // ship components
    public static final ArrayList<ShipComponent> allShipComponents;
    public static final Armor armor1, armor2, armor3;
    public static final BeamEmitter beamEmitter1, beamEmitter2, beamEmitter3, beamEmitter4, beamEmitter5;
    public static final BeamEmitter basicLaser1, basicLaser2;
    public static final ShieldEmitter shieldEmitter1, shieldEmitter2, shieldEmitter3, shieldEmitter4;
    public static final WarpCore warpCore1, warpCore2, warpCore3;
    public static final ColonizationModule colonizationModule1, colonizationModule2, colonizationModule3;
    public static final DeuteriumTank deuteriumTank1, deuteriumTank2, deuteriumTank3, deuteriumTank4, deuteriumTank5;
    public static final BussardCollector bussardCollector1, bussardCollector2, bussardCollector3;
    public static final Sensor sensor1, sensor2, sensor3, sensor4;
    public static final Sensor basicSensor1, basicSensor2;
    public static final Cargo cargo1, cargo2, cargo3, cargo4;
    public static final FusionReactor fusionReactor1, fusionReactor2, fusionReactor3, fusionReactor4;
    public static final MiningLaser miningLaser1, miningLaser2, miningLaser3;
    public static final TorpedoLauncher torpedoLauncher1, torpedoLauncher2, torpedoLauncher3, torpedoLauncher4;
    public static final ImpulseDrive impulseDrive1, impulseDrive2, impulseDrive3;
    public static final TroopTransport troopTransport1, troopTransport2, troopTransport3;

    static {
        // -------------- FACTIONS --------------
        federation = new Faction("Federation", "The united federation of planets is made up of dozens of worlds, including Earth,Vulcan,Andoria,Betazed",
                15, 15, -10, 10, 15, 10);
        klingon = new Faction("Klingon", "The Klingon empire is a warrior society where honor and combat are key elements",
                -15, 20, 25, 15, 5, -5);
        romulan = new Faction("Romulan", "The Romulan empire is a secretive isolationist culture, where dicipline and work are key elements",
                10, 10, 10, 5, 10, 0);
        cardassian = new Faction("Cardassian", "The Cardassian union is a military expantionist culture where service to the state is the highest honor",
                0, 10, 15, 10, 20, 0);
        dominion = new Faction("Dominion", "The Cardassian union is a military expantionist culture where service to the state is the highest honor",
                -5, 20, 25, 20, 0, -10);
        nobodyFaction = new Faction("nobody", "nobody", -100, -100, -100, -100, -100, -100);


        // -------------- SPECIAL USERS --------------
        nobodyUser = new User("nobody", "sa%\"das-)!i210wqj", StaticData.nobodyFaction);


        // -------------- SHIP HULLS --------------
        allHullClasses = new ArrayList<HullClass>();

        civilian_scoutship = new CivilianScoutship();
        civilian_scoutship.setSlotMapPoint(0, new Point(300, 281));
        civilian_scoutship.setSlotMapPoint(1, new Point(384, 193));
        civilian_scoutship.setSlotMapPoint(2, new Point(468, 281));
        allHullClasses.add(civilian_scoutship);

        civilian_colonyship = new CivilianColonyship();
        civilian_colonyship.setSlotMapPoint(0, new Point(322, 414));
        civilian_colonyship.setSlotMapPoint(1, new Point(320, 230));
        civilian_colonyship.setSlotMapPoint(2, new Point(383, 133));
        civilian_colonyship.setSlotMapPoint(3, new Point(443, 228));
        civilian_colonyship.setSlotMapPoint(4, new Point(445, 412));
        allHullClasses.add(civilian_colonyship);

        civilian_freighter = new CivilianFreighter();
        civilian_freighter.setSlotMapPoint(0, new Point(310, 118));
        civilian_freighter.setSlotMapPoint(1, new Point(303, 211));
        civilian_freighter.setSlotMapPoint(2, new Point(383, 144));
        civilian_freighter.setSlotMapPoint(3, new Point(450, 117));
        civilian_freighter.setSlotMapPoint(4, new Point(462, 209));
        civilian_freighter.setSlotMapPoint(5, new Point(381, 403));
        civilian_freighter.setSlotMapPoint(6, new Point(358, 477));
        civilian_freighter.setSlotMapPoint(7, new Point(410, 477));
        allHullClasses.add(civilian_freighter);

        civilian_barge = new CivilianBarge();
        civilian_barge.setSlotMapPoint(0, new Point(261, 427));
        civilian_barge.setSlotMapPoint(1, new Point(280, 321));
        civilian_barge.setSlotMapPoint(2, new Point(380, 96));
        civilian_barge.setSlotMapPoint(3, new Point(481, 319));
        civilian_barge.setSlotMapPoint(4, new Point(505, 426));
        civilian_barge.setSlotMapPoint(5, new Point(330, 443));
        civilian_barge.setSlotMapPoint(6, new Point(349, 358));
        civilian_barge.setSlotMapPoint(7, new Point(355, 291));
        civilian_barge.setSlotMapPoint(8, new Point(381, 215));
        civilian_barge.setSlotMapPoint(9, new Point(407, 291));
        civilian_barge.setSlotMapPoint(10, new Point(416, 356));
        civilian_barge.setSlotMapPoint(11, new Point(436, 442));
        allHullClasses.add(civilian_barge);

        civilian_carrier = new CivilianCarrier();
        civilian_carrier.setSlotMapPoint(0, new Point(221, 551));
        civilian_carrier.setSlotMapPoint(1, new Point(218, 464));
        civilian_carrier.setSlotMapPoint(2, new Point(285, 349));
        civilian_carrier.setSlotMapPoint(3, new Point(281, 205));
        civilian_carrier.setSlotMapPoint(4, new Point(330, 28));
        civilian_carrier.setSlotMapPoint(5, new Point(444, 29));
        civilian_carrier.setSlotMapPoint(6, new Point(489, 204));
        civilian_carrier.setSlotMapPoint(7, new Point(483, 347));
        civilian_carrier.setSlotMapPoint(8, new Point(549, 465));
        civilian_carrier.setSlotMapPoint(9, new Point(543, 554));
        civilian_carrier.setSlotMapPoint(10, new Point(386, 38));
        civilian_carrier.setSlotMapPoint(11, new Point(360, 131));
        civilian_carrier.setSlotMapPoint(12, new Point(414, 132));
        civilian_carrier.setSlotMapPoint(13, new Point(345, 187));
        civilian_carrier.setSlotMapPoint(14, new Point(425, 187));
        civilian_carrier.setSlotMapPoint(15, new Point(384, 254));
        civilian_carrier.setSlotMapPoint(16, new Point(384, 312));
        civilian_carrier.setSlotMapPoint(17, new Point(383, 393));
        civilian_carrier.setSlotMapPoint(18, new Point(318, 431));
        civilian_carrier.setSlotMapPoint(19, new Point(383, 449));
        civilian_carrier.setSlotMapPoint(20, new Point(450, 431));
        civilian_carrier.setSlotMapPoint(21, new Point(328, 489));
        civilian_carrier.setSlotMapPoint(22, new Point(383, 503));
        civilian_carrier.setSlotMapPoint(23, new Point(439, 489));
        allHullClasses.add(civilian_carrier);

        federation_oberth = new FederationOberth();
        federation_oberth.setSlotMapPoint(0, new Point(331, 288));
        federation_oberth.setSlotMapPoint(1, new Point(331, 115));
        federation_oberth.setSlotMapPoint(2, new Point(381, 67));
        federation_oberth.setSlotMapPoint(3, new Point(436, 115));
        federation_oberth.setSlotMapPoint(4, new Point(436, 289));
        federation_oberth.setSlotMapPoint(5, new Point(382, 414));
        allHullClasses.add(federation_oberth);

        federation_miranda = new FederationMiranda();
        federation_miranda.setSlotMapPoint(0, new Point(289, 462));
        federation_miranda.setSlotMapPoint(1, new Point(335, 343));
        federation_miranda.setSlotMapPoint(2, new Point(325, 243));
        federation_miranda.setSlotMapPoint(3, new Point(297, 159));
        federation_miranda.setSlotMapPoint(4, new Point(334, 92));
        federation_miranda.setSlotMapPoint(5, new Point(439, 92));
        federation_miranda.setSlotMapPoint(6, new Point(476, 158));
        federation_miranda.setSlotMapPoint(7, new Point(451, 243));
        federation_miranda.setSlotMapPoint(8, new Point(441, 344));
        federation_miranda.setSlotMapPoint(9, new Point(489, 462));
        allHullClasses.add(federation_miranda);

        federation_constitution = new FederationConstitution();
        federation_constitution.setSlotMapPoint(0, new Point(275, 536));
        federation_constitution.setSlotMapPoint(1, new Point(279, 377));
        federation_constitution.setSlotMapPoint(2, new Point(382, 380));
        federation_constitution.setSlotMapPoint(3, new Point(357, 289));
        federation_constitution.setSlotMapPoint(4, new Point(341, 211));
        federation_constitution.setSlotMapPoint(5, new Point(271, 137));
        federation_constitution.setSlotMapPoint(6, new Point(295, 59));
        federation_constitution.setSlotMapPoint(7, new Point(382, 19));
        federation_constitution.setSlotMapPoint(8, new Point(473, 58));
        federation_constitution.setSlotMapPoint(9, new Point(494, 137));
        federation_constitution.setSlotMapPoint(10, new Point(424, 211));
        federation_constitution.setSlotMapPoint(11, new Point(410, 288));
        federation_constitution.setSlotMapPoint(12, new Point(486, 378));
        federation_constitution.setSlotMapPoint(13, new Point(488, 536));
        allHullClasses.add(federation_constitution);

        federation_excelsior = new FederationExcelsior();
        federation_excelsior.setSlotMapPoint(0, new Point(307, 528));
        federation_excelsior.setSlotMapPoint(1, new Point(294, 100));
        federation_excelsior.setSlotMapPoint(2, new Point(339, 24));
        federation_excelsior.setSlotMapPoint(3, new Point(421, 24));
        federation_excelsior.setSlotMapPoint(4, new Point(473, 101));
        federation_excelsior.setSlotMapPoint(5, new Point(453, 526));
        federation_excelsior.setSlotMapPoint(6, new Point(337, 167));
        federation_excelsior.setSlotMapPoint(7, new Point(427, 167));
        federation_excelsior.setSlotMapPoint(8, new Point(358, 236));
        federation_excelsior.setSlotMapPoint(9, new Point(405, 236));
        federation_excelsior.setSlotMapPoint(10, new Point(358, 284));
        federation_excelsior.setSlotMapPoint(11, new Point(405, 284));
        federation_excelsior.setSlotMapPoint(12, new Point(343, 354));
        federation_excelsior.setSlotMapPoint(13, new Point(420, 354));
        federation_excelsior.setSlotMapPoint(14, new Point(380, 408));
        federation_excelsior.setSlotMapPoint(15, new Point(380, 458));
        allHullClasses.add(federation_excelsior);

        federation_galaxy = new FederationGalaxy();
        federation_galaxy.setSlotMapPoint(0, new Point(260, 362));
        federation_galaxy.setSlotMapPoint(1, new Point(260, 286));
        federation_galaxy.setSlotMapPoint(2, new Point(204, 156));
        federation_galaxy.setSlotMapPoint(3, new Point(248, 56));
        federation_galaxy.setSlotMapPoint(4, new Point(342, 10));
        federation_galaxy.setSlotMapPoint(5, new Point(425, 10));
        federation_galaxy.setSlotMapPoint(6, new Point(521, 56));
        federation_galaxy.setSlotMapPoint(7, new Point(565, 156));
        federation_galaxy.setSlotMapPoint(8, new Point(506, 286));
        federation_galaxy.setSlotMapPoint(9, new Point(503, 362));
        federation_galaxy.setSlotMapPoint(10, new Point(316, 520));
        federation_galaxy.setSlotMapPoint(11, new Point(344, 463));
        federation_galaxy.setSlotMapPoint(12, new Point(335, 412));
        federation_galaxy.setSlotMapPoint(13, new Point(329, 362));
        federation_galaxy.setSlotMapPoint(14, new Point(285, 187));
        federation_galaxy.setSlotMapPoint(15, new Point(383, 66));
        federation_galaxy.setSlotMapPoint(16, new Point(480, 188));
        federation_galaxy.setSlotMapPoint(17, new Point(436, 361));
        federation_galaxy.setSlotMapPoint(18, new Point(430, 414));
        federation_galaxy.setSlotMapPoint(19, new Point(421, 463));
        federation_galaxy.setSlotMapPoint(20, new Point(449, 521));
        federation_galaxy.setSlotMapPoint(21, new Point(383, 227));
        allHullClasses.add(federation_galaxy);

        federation_defiant = new FederationDefiant();
        federation_defiant.setSlotMapPoint(0, new Point(355, 469));
        federation_defiant.setSlotMapPoint(1, new Point(309, 369));
        federation_defiant.setSlotMapPoint(2, new Point(233, 284));
        federation_defiant.setSlotMapPoint(3, new Point(322, 250));
        federation_defiant.setSlotMapPoint(4, new Point(323, 188));
        federation_defiant.setSlotMapPoint(5, new Point(384, 91));
        federation_defiant.setSlotMapPoint(6, new Point(446, 188));
        federation_defiant.setSlotMapPoint(7, new Point(446, 251));
        federation_defiant.setSlotMapPoint(8, new Point(534, 285));
        federation_defiant.setSlotMapPoint(9, new Point(457, 369));
        federation_defiant.setSlotMapPoint(10, new Point(412, 468));
        allHullClasses.add(federation_defiant);

        klingon_raptor = new KlingonRaptor();
        klingon_raptor.setSlotMapPoint(0, new Point(380, 102));
        klingon_raptor.setSlotMapPoint(1, new Point(380, 160));
        klingon_raptor.setSlotMapPoint(2, new Point(235, 418));
        klingon_raptor.setSlotMapPoint(3, new Point(379, 438));
        klingon_raptor.setSlotMapPoint(4, new Point(530, 418));
        allHullClasses.add(klingon_raptor);

        klingon_brel = new KlingonBrel();
        klingon_brel.setSlotMapPoint(0, new Point(188, 280));
        klingon_brel.setSlotMapPoint(1, new Point(324, 432));
        klingon_brel.setSlotMapPoint(2, new Point(381, 127));
        klingon_brel.setSlotMapPoint(3, new Point(381, 190));
        klingon_brel.setSlotMapPoint(4, new Point(382, 297));
        klingon_brel.setSlotMapPoint(5, new Point(382, 395));
        klingon_brel.setSlotMapPoint(6, new Point(438, 431));
        klingon_brel.setSlotMapPoint(7, new Point(577, 279));
        allHullClasses.add(klingon_brel);

        klingon_kvort = new KlingonKvort();
        klingon_kvort.setSlotMapPoint(0, new Point(188, 280));
        klingon_kvort.setSlotMapPoint(1, new Point(324, 432));
        klingon_kvort.setSlotMapPoint(2, new Point(381, 127));
        klingon_kvort.setSlotMapPoint(3, new Point(381, 190));
        klingon_kvort.setSlotMapPoint(4, new Point(382, 297));
        klingon_kvort.setSlotMapPoint(5, new Point(438, 431));
        klingon_kvort.setSlotMapPoint(6, new Point(577, 279));
        klingon_kvort.setSlotMapPoint(7, new Point(186, 328));
        klingon_kvort.setSlotMapPoint(8, new Point(325, 351));
        klingon_kvort.setSlotMapPoint(9, new Point(439, 350));
        klingon_kvort.setSlotMapPoint(10, new Point(576, 328));
        allHullClasses.add(klingon_kvort);

        klingon_ktinga = new KlingonKtinga();
        klingon_ktinga.setSlotMapPoint(0, new Point(199, 537));
        klingon_ktinga.setSlotMapPoint(1, new Point(259, 374));
        klingon_ktinga.setSlotMapPoint(2, new Point(295, 299));
        klingon_ktinga.setSlotMapPoint(3, new Point(362, 31));
        klingon_ktinga.setSlotMapPoint(4, new Point(351, 337));
        klingon_ktinga.setSlotMapPoint(5, new Point(351, 428));
        klingon_ktinga.setSlotMapPoint(6, new Point(386, 82));
        klingon_ktinga.setSlotMapPoint(7, new Point(386, 266));
        klingon_ktinga.setSlotMapPoint(8, new Point(412, 32));
        klingon_ktinga.setSlotMapPoint(9, new Point(422, 338));
        klingon_ktinga.setSlotMapPoint(10, new Point(422, 426));
        klingon_ktinga.setSlotMapPoint(11, new Point(475, 299));
        klingon_ktinga.setSlotMapPoint(12, new Point(509, 373));
        klingon_ktinga.setSlotMapPoint(13, new Point(564, 540));
        allHullClasses.add(klingon_ktinga);

        klingon_vorcha = new KlingonVorcha();
        klingon_vorcha.setSlotMapPoint(0, new Point(201, 328));
        klingon_vorcha.setSlotMapPoint(1, new Point(200, 475));
        klingon_vorcha.setSlotMapPoint(2, new Point(263, 404));
        klingon_vorcha.setSlotMapPoint(3, new Point(325, 379));
        klingon_vorcha.setSlotMapPoint(4, new Point(325, 427));
        klingon_vorcha.setSlotMapPoint(5, new Point(439, 379));
        klingon_vorcha.setSlotMapPoint(6, new Point(440, 427));
        klingon_vorcha.setSlotMapPoint(7, new Point(501, 404));
        klingon_vorcha.setSlotMapPoint(8, new Point(562, 328));
        klingon_vorcha.setSlotMapPoint(9, new Point(563, 477));
        klingon_vorcha.setSlotMapPoint(10, new Point(385, 30));
        klingon_vorcha.setSlotMapPoint(11, new Point(357, 83));
        klingon_vorcha.setSlotMapPoint(12, new Point(408, 82));
        klingon_vorcha.setSlotMapPoint(13, new Point(384, 158));
        klingon_vorcha.setSlotMapPoint(14, new Point(382, 235));
        klingon_vorcha.setSlotMapPoint(15, new Point(382, 284));
        klingon_vorcha.setSlotMapPoint(16, new Point(382, 346));
        klingon_vorcha.setSlotMapPoint(17, new Point(382, 419));
        allHullClasses.add(klingon_vorcha);

        klingon_neghvar = new KlingonNeghvar();
        klingon_neghvar.setSlotMapPoint(0, new Point(147, 242));
        klingon_neghvar.setSlotMapPoint(1, new Point(141, 360));
        klingon_neghvar.setSlotMapPoint(2, new Point(188, 294));
        klingon_neghvar.setSlotMapPoint(3, new Point(246, 323));
        klingon_neghvar.setSlotMapPoint(4, new Point(246, 460));
        klingon_neghvar.setSlotMapPoint(5, new Point(303, 324));
        klingon_neghvar.setSlotMapPoint(6, new Point(381, 428));
        klingon_neghvar.setSlotMapPoint(7, new Point(381, 557));
        klingon_neghvar.setSlotMapPoint(8, new Point(461, 323));
        klingon_neghvar.setSlotMapPoint(9, new Point(516, 323));
        klingon_neghvar.setSlotMapPoint(10, new Point(520, 460));
        klingon_neghvar.setSlotMapPoint(11, new Point(575, 294));
        klingon_neghvar.setSlotMapPoint(12, new Point(614, 244));
        klingon_neghvar.setSlotMapPoint(13, new Point(621, 360));
        klingon_neghvar.setSlotMapPoint(14, new Point(325, 59));
        klingon_neghvar.setSlotMapPoint(15, new Point(440, 57));
        klingon_neghvar.setSlotMapPoint(16, new Point(383, 7));
        klingon_neghvar.setSlotMapPoint(17, new Point(382, 98));
        allHullClasses.add(klingon_neghvar);


        // -------------- STRUCTURES --------------
        allStructures = new ArrayList<Structure>();

        farm1 = new Farm("Farm", "Farms produce food for the planets population, allows population and troops to generate",
                650, -1, -1, 50, -1, "farm");
        farm1.addTech(TechnologyGenerator.BIO_TECH0);
        allStructures.add(farm1);

        factory1 = new Factory("Factory", "Industrial complex that produces starship parts and planetary structures",
                1450, -10, -3, 0, 82, "factory");
        factory1.addTech(TechnologyGenerator.CONSTRUCTION_TECH0);
        allStructures.add(factory1);

        power1 = new PowerPlant("Plasma reactor", "These gigantic powerplants provides the planet with energy",
                1250, 45, -3, 0, -3, "power");
        power1.addTech(TechnologyGenerator.ENERGY_TECH0);
        allStructures.add(power1);

        lab1 = new Laboratory("Laboratory 1", "These research centers will improve your starsystems research output, allowing you to quicker gain new technologies",
                1370, -5, 60, 0, -4, "lab");
        lab1.addTech(TechnologyGenerator.COMPUTER_TECH0);
        allStructures.add(lab1);

        oreRefinery1 = new OreRefinery("Ore refinery", "This facillity allows you to process ore from mining ships in this system, it also allows for the storage of some ore",
                1730, -15, -2, 0, -8, "ore", 3000);
        oreRefinery1.addTech(TechnologyGenerator.CONSTRUCTION_TECH2);
        oreRefinery1.setSpecial("allows ore processing");
        allStructures.add(oreRefinery1);

        oreSilo1 = new OreSilo("Ore Silo 1",
                1450, -1, 0, 0, -2, "oresilo1", 1700);
        oreSilo1.addTech(TechnologyGenerator.CONSTRUCTION_TECH3);
        allStructures.add(oreSilo1);

        oreSilo2 = new OreSilo("Ore Silo 2",
                2175, -2, 0, 0, -3, "oresilo2", 2225);
        oreSilo2.addTech(TechnologyGenerator.CONSTRUCTION_TECH5);
        allStructures.add(oreSilo2);

        oreSilo3 = new OreSilo("Ore Silo 3",
                2750, -3, 0, 0, -5, "oresilo3", 2950);
        oreSilo3.addTech(TechnologyGenerator.CONSTRUCTION_TECH6);
        allStructures.add(oreSilo3);


        deuteriumProcessingPlant1 = new DeuteriumPlant("Deuterium Processing Plant", "This facillity allows you to process harvested deuterium into usable starship fuel, it also allows for some fuel storage",
                1890, -2, -1, 0, -2, "deuteriumplant", 3200);
        allStructures.add(deuteriumProcessingPlant1);

        deuteriumSilo1 = new DeuteriumSilo("Deuterium Silo 1",
                1665, -1, 0, 0, -2, "deuteriumsilo1", 1050);
        deuteriumSilo1.addTech(TechnologyGenerator.ENERGY_TECH2, TechnologyGenerator.CONSTRUCTION_TECH2);
        allStructures.add(deuteriumSilo1);

        deuteriumSilo2 = new DeuteriumSilo("Deuterium Silo 2",
                1940, -2, 0, 0, -3, "deuteriumsilo2", 1300);
        deuteriumSilo2.addTech(TechnologyGenerator.ENERGY_TECH2, TechnologyGenerator.CONSTRUCTION_TECH4);
        allStructures.add(deuteriumSilo2);

        deuteriumSilo3 = new DeuteriumSilo("Deuterium Silo 3",
                2535, -3, 0, 0, -5, "deuteriumsilo3", 1775);
        deuteriumSilo3.addTech(TechnologyGenerator.ENERGY_TECH4, TechnologyGenerator.CONSTRUCTION_TECH7);
        allStructures.add(deuteriumSilo3);

        militaryOutpost1 = new MilitaryOutpost("Military outpost",
                2100, -15, -5, -12, -12, "militaryoutpost", 5, 2);
        militaryOutpost1.addTech(TechnologyGenerator.WEAPON_TECH1);
        allStructures.add(militaryOutpost1);

        bunker1 = new Bunker("Bunker 1", 2950, -2, -1, -8, -2, "bunker1", 4);
        bunker1.addTech(TechnologyGenerator.WEAPON_TECH1, TechnologyGenerator.CONSTRUCTION_TECH2);
        allStructures.add(bunker1);

        bunker2 = new Bunker("Bunker 2", 3650, -4, -2, -10, -3, "bunker2", 6);
        bunker2.addTech(TechnologyGenerator.WEAPON_TECH3, TechnologyGenerator.CONSTRUCTION_TECH5);
        allStructures.add(bunker2);

        bunker3 = new Bunker("Bunker 3", 5150, -8, -5, -14, -6, "bunker3", 10);
        bunker3.addTech(TechnologyGenerator.WEAPON_TECH5, TechnologyGenerator.CONSTRUCTION_TECH9);
        allStructures.add(bunker3);

        subspaceScanner1 = new SubspaceScanner("Subspace scanner", "Orbital satellites and planetary facilities that improve the scan strength in the area surrounding this system",
                3900, -18, -8, 0, -5, "subspace_scanner");
        subspaceScanner1.addTech(TechnologyGenerator.ENERGY_TECH2, TechnologyGenerator.COMPUTER_TECH4);
        allStructures.add(subspaceScanner1);

        shipyard1 = new Shipyard("Shipyard", "Orbital drydocks and planetary facilities that allows for the construction and repair of starships",
                5150, -15, -5, -5, -22, "shipyard");
        allStructures.add(shipyard1);

        weatherModificationNetwork1 = new WeatherModificationGrid("Weather Modification Grid", "This complex environmental system will greatly increase food output throughout the system",
                13750, -20, -10, 150, -5, "weathergrid");
        weatherModificationNetwork1.addTech(TechnologyGenerator.BIO_TECH5, TechnologyGenerator.ENERGY_TECH3, TechnologyGenerator.CONSTRUCTION_TECH3);
        allStructures.add(weatherModificationNetwork1);


        // -------------- SHIP COMPONENTS --------------
        allShipComponents = new ArrayList<ShipComponent>();

        armor1 = new Armor("Armor 1", "Duranium armor protects the ship's hull, good against energy weapons", "armor1.png", 90, 30);
        armor1.addFaction(federation, klingon, romulan, cardassian, dominion);
        armor1.addTech(TechnologyGenerator.WEAPON_TECH1, TechnologyGenerator.CONSTRUCTION_TECH1);
        allShipComponents.add(armor1);

        armor2 = new Armor("Armor 2", "Duranium armor protects the ship's hull, good against energy weapons", "armor2.png", 140, 50);
        armor2.addFaction(federation, klingon, romulan, cardassian, dominion);
        armor2.addTech(TechnologyGenerator.WEAPON_TECH3, TechnologyGenerator.CONSTRUCTION_TECH2);
        allShipComponents.add(armor2);

        armor3 = new Armor("Armor 3", "Duranium armor protects the ship's hull, good against energy weapons", "armor3.png", 200, 80);
        armor3.addFaction(federation, klingon, romulan, cardassian, dominion);
        armor3.addTech(TechnologyGenerator.WEAPON_TECH6, TechnologyGenerator.CONSTRUCTION_TECH4, TechnologyGenerator.COMPUTER_TECH2);
        allShipComponents.add(armor3);

        warpCore1 = new WarpCore("Warp Core 1", "Warp core makes your ship go wheeeeee", "warpcore1.png", 280, 5, 11);
        warpCore1.addFaction(federation, klingon, romulan, cardassian, dominion);
        allShipComponents.add(warpCore1);

        warpCore2 = new WarpCore("Warp Core 2", "Warp core makes your ship go whoooooo", "warpcore2.png", 400, 10, 13);
        warpCore2.addFaction(federation, klingon, romulan, cardassian, dominion);
        warpCore2.addTech(TechnologyGenerator.PROPULSION_TECH3, TechnologyGenerator.ENERGY_TECH2);
        allShipComponents.add(warpCore2);

        warpCore3 = new WarpCore("Warp Core 3", "Warp core makes your ship go WHEEEEE!", "warpcore3.png", 900, 20, 16);
        warpCore3.addFaction(federation, klingon, romulan, cardassian, dominion);
        warpCore3.addTech(TechnologyGenerator.PROPULSION_TECH8, TechnologyGenerator.ENERGY_TECH6, TechnologyGenerator.CONSTRUCTION_TECH7);
        allShipComponents.add(warpCore3);

        basicLaser1 = new BeamEmitter("Basic laser 1", "Low powered laser", "basiclaser1.png", 40, -1, 2, 2);
        basicLaser1.addFaction(federation, klingon, romulan, cardassian, dominion);
        allShipComponents.add(basicLaser1);

        basicLaser2 = new BeamEmitter("Basic laser 2", "Low powered laser", "basiclaser2.png", 70, -3, 4, 3);
        basicLaser2.addFaction(federation, klingon, romulan, cardassian, dominion);
        basicLaser2.addTech(TechnologyGenerator.WEAPON_TECH1);
        allShipComponents.add(basicLaser2);

        beamEmitter1 = new BeamEmitter("Beam emitter 1", "Fires a beam of directed energy, good against shields", "beamemitter1.png", 140, -7, 10, 5);
        beamEmitter1.addFaction(federation, klingon, romulan, cardassian, dominion);
        beamEmitter1.addTech(TechnologyGenerator.WEAPON_TECH2, TechnologyGenerator.ENERGY_TECH1);
        allShipComponents.add(beamEmitter1);

        beamEmitter2 = new BeamEmitter("Beam emitter 2", "Fires a beam of directed energy, good against shields", "beamemitter2.png", 275, -10, 15, 7);
        beamEmitter2.addFaction(federation, klingon, romulan, cardassian, dominion);
        beamEmitter2.addTech(TechnologyGenerator.WEAPON_TECH3, TechnologyGenerator.ENERGY_TECH1);
        allShipComponents.add(beamEmitter2);

        beamEmitter3 = new BeamEmitter("Beam emitter 3", "Fires a beam of directed energy, good against shields", "beamemitter3.png", 410, -13, 25, 9);
        beamEmitter3.addFaction(federation, klingon, romulan, cardassian, dominion);
        beamEmitter3.addTech(TechnologyGenerator.WEAPON_TECH4, TechnologyGenerator.ENERGY_TECH2);
        allShipComponents.add(beamEmitter3);

        beamEmitter4 = new BeamEmitter("Beam emitter 4", "Fires a beam of directed energy, good against shields", "beamemitter4.png", 615, -19, 40, 13);
        beamEmitter4.addFaction(federation, klingon, romulan, cardassian, dominion);
        beamEmitter4.addTech(TechnologyGenerator.WEAPON_TECH6, TechnologyGenerator.ENERGY_TECH3, TechnologyGenerator.COMPUTER_TECH2);
        allShipComponents.add(beamEmitter4);

        beamEmitter5 = new BeamEmitter("Beam emitter 5", "Fires a beam of directed energy, good against shields", "beamemitter5.png", 1120, -27, 65, 15);
        beamEmitter5.addFaction(federation, klingon, romulan, cardassian, dominion);
        beamEmitter5.addTech(TechnologyGenerator.WEAPON_TECH8, TechnologyGenerator.ENERGY_TECH6, TechnologyGenerator.COMPUTER_TECH4);
        allShipComponents.add(beamEmitter5);

        shieldEmitter1 = new ShieldEmitter("Shield emitter 1", "Force fields protecting a ship", "shield1.png", 240, 40, -14);
        shieldEmitter1.addFaction(federation, klingon, romulan, cardassian, dominion);
        shieldEmitter1.addTech(TechnologyGenerator.ENERGY_TECH2, TechnologyGenerator.WEAPON_TECH1);
        allShipComponents.add(shieldEmitter1);

        shieldEmitter2 = new ShieldEmitter("Shield emitter 2", "Force fields protecting a ship", "shield2.png", 450, 80, -25);
        shieldEmitter2.addFaction(federation, klingon, romulan, cardassian, dominion);
        shieldEmitter2.addTech(TechnologyGenerator.ENERGY_TECH4, TechnologyGenerator.WEAPON_TECH2);
        allShipComponents.add(shieldEmitter2);

        shieldEmitter3 = new ShieldEmitter("Shield emitter 3", "Force fields protecting a ship", "shield3.png", 770, 140, -35);
        shieldEmitter3.addFaction(federation, klingon, romulan, cardassian, dominion);
        shieldEmitter3.addTech(TechnologyGenerator.ENERGY_TECH6, TechnologyGenerator.WEAPON_TECH3, TechnologyGenerator.COMPUTER_TECH2);
        allShipComponents.add(shieldEmitter3);

        shieldEmitter4 = new ShieldEmitter("Shield emitter 4", "Force fields protecting a ship", "shield4.png", 1205, 230, -48);
        shieldEmitter4.addFaction(federation, klingon, romulan, cardassian, dominion);
        shieldEmitter4.addTech(TechnologyGenerator.ENERGY_TECH8, TechnologyGenerator.WEAPON_TECH4, TechnologyGenerator.COMPUTER_TECH4);
        allShipComponents.add(shieldEmitter4);

        colonizationModule1 = new ColonizationModule("Colonization module 1", "Carries people and equipment to start a new colony", "colony1.png", 1050, 45, -10);
        colonizationModule1.addFaction(federation, klingon, romulan, cardassian, dominion);
        allShipComponents.add(colonizationModule1);

        colonizationModule2 = new ColonizationModule("Colonization module 2", "Carries people and equipment to start a new colony", "colony2.png", 2450, 60, -15);
        colonizationModule2.addFaction(federation, klingon, romulan, cardassian, dominion);
        colonizationModule2.addTech(TechnologyGenerator.BIO_TECH4, TechnologyGenerator.CONSTRUCTION_TECH2);
        allShipComponents.add(colonizationModule2);

        colonizationModule3 = new ColonizationModule("Colonization module 3", "Carries people and equipment to start a new colony", "colony3.png", 3600, 120, -20);
        colonizationModule3.addFaction(federation, klingon, romulan, cardassian, dominion);
        colonizationModule3.addTech(TechnologyGenerator.BIO_TECH8, TechnologyGenerator.CONSTRUCTION_TECH5, TechnologyGenerator.COMPUTER_TECH3);
        allShipComponents.add(colonizationModule3);

        deuteriumTank1 = new DeuteriumTank("Deuterium tank 1", "Tank that holds additional fuel for starships", "tank1.png", 60, 50);
        deuteriumTank1.addFaction(federation, klingon, romulan, cardassian, dominion);
        deuteriumTank1.addTech(TechnologyGenerator.PROPULSION_TECH1, TechnologyGenerator.ENERGY_TECH1);
        allShipComponents.add(deuteriumTank1);

        deuteriumTank2 = new DeuteriumTank("Deuterium tank 2", "Tank that holds additional fuel for starships", "tank2.png", 85, 65);
        deuteriumTank2.addFaction(federation, klingon, romulan, cardassian, dominion);
        deuteriumTank2.addTech(TechnologyGenerator.PROPULSION_TECH3, TechnologyGenerator.ENERGY_TECH2);
        allShipComponents.add(deuteriumTank2);

        deuteriumTank3 = new DeuteriumTank("Deuterium tank 3", "Tank that holds additional fuel for starships", "tank3.png", 150, 80);
        deuteriumTank3.addFaction(federation, klingon, romulan, cardassian, dominion);
        deuteriumTank3.addTech(TechnologyGenerator.PROPULSION_TECH5, TechnologyGenerator.ENERGY_TECH3);
        allShipComponents.add(deuteriumTank3);

        deuteriumTank4 = new DeuteriumTank("Deuterium tank 4", "Tank that holds additional fuel for starships", "tank4.png", 280, 115);
        deuteriumTank4.addFaction(federation, klingon, romulan, cardassian, dominion);
        deuteriumTank4.addTech(TechnologyGenerator.PROPULSION_TECH8, TechnologyGenerator.ENERGY_TECH5);
        allShipComponents.add(deuteriumTank4);

        deuteriumTank5 = new DeuteriumTank("Deuterium tank 5", "Tank that holds additional fuel for starships", "tank5.png", 495, 155);
        deuteriumTank5.addFaction(federation, klingon, romulan, cardassian, dominion);
        deuteriumTank5.addTech(TechnologyGenerator.PROPULSION_TECH10, TechnologyGenerator.ENERGY_TECH7);
        allShipComponents.add(deuteriumTank5);

        bussardCollector1 = new BussardCollector("Bussard Collector 1", "Enables a ship to gather deuterium from nebulae or gas giants", "bussard1.png", 260, -20, 25);
        bussardCollector1.addFaction(federation, klingon, romulan, cardassian, dominion);
        bussardCollector1.addTech(TechnologyGenerator.ENERGY_TECH3);
        allShipComponents.add(bussardCollector1);

        bussardCollector2 = new BussardCollector("Bussard Collector 2", "Enables a ship to gather deuterium from nebulae or gas giants", "bussard2.png", 820, -35, 80);
        bussardCollector2.addFaction(federation, klingon, romulan, cardassian, dominion);
        bussardCollector2.addTech(TechnologyGenerator.ENERGY_TECH6);
        allShipComponents.add(bussardCollector2);

        bussardCollector3 = new BussardCollector("Bussard Collector 3", "Enables a ship to gather deuterium from nebulae or gas giants", "bussard3.png", 1350, -30, 210);
        bussardCollector3.addFaction(federation, klingon, romulan, cardassian, dominion);
        bussardCollector3.addTech(TechnologyGenerator.ENERGY_TECH8);
        allShipComponents.add(bussardCollector3);

        basicSensor1 = new Sensor("Basic Sensors 1", "Sensor enables ships to see other ships and planets from a distance", "basicsensor1.png", 35, -1, 20);
        basicSensor1.addFaction(federation, klingon, romulan, cardassian, dominion);
        allShipComponents.add(basicSensor1);

        basicSensor2 = new Sensor("Basic Sensors 2", "Sensor enables ships to see other ships and planets from a distance", "basicsensor2.png", 70, -2, 30);
        basicSensor2.addFaction(federation, klingon, romulan, cardassian, dominion);
        basicSensor2.addTech(TechnologyGenerator.COMPUTER_TECH1);
        allShipComponents.add(basicSensor2);

        sensor1 = new Sensor("Sensor Array 1", "Sensor enables ships to see other ships and planets from a distance", "sensor1.png", 135, -5, 35);
        sensor1.addFaction(federation, klingon, romulan, cardassian, dominion);
        sensor1.addTech(TechnologyGenerator.COMPUTER_TECH2);
        allShipComponents.add(sensor1);

        sensor2 = new Sensor("Sensor Array 2", "Sensor enables ships to see other ships and planets from a distance", "sensor2.png", 255, -6, 39);
        sensor2.addFaction(federation, klingon, romulan, cardassian, dominion);
        sensor2.addTech(TechnologyGenerator.COMPUTER_TECH4);
        allShipComponents.add(sensor2);

        sensor3 = new Sensor("Sensor Array 3", "Sensor enables ships to see other ships and planets from a distance", "sensor3.png", 420, -8, 44);
        sensor3.addFaction(federation, klingon, romulan, cardassian, dominion);
        sensor3.addTech(TechnologyGenerator.COMPUTER_TECH6);
        allShipComponents.add(sensor3);

        sensor4 = new Sensor("Sensor Array 4", "Sensor enables ships to see other ships and planets from a distance", "sensor4.png", 680, -10, 49);
        sensor4.addFaction(federation, klingon, romulan, cardassian, dominion);
        sensor4.addTech(TechnologyGenerator.COMPUTER_TECH8);
        allShipComponents.add(sensor4);

        cargo1 = new Cargo("Cargo module 1", "Cargo bay that holds raw materials", "cargo1.png", 200, 500);
        cargo1.addFaction(federation, klingon, romulan, cardassian, dominion);
        cargo1.addTech(TechnologyGenerator.CONSTRUCTION_TECH2);
        allShipComponents.add(cargo1);

        cargo2 = new Cargo("Cargo module 2", "Cargo bay that holds raw materials", "cargo2.png", 320, 800);
        cargo2.addFaction(federation, klingon, romulan, cardassian, dominion);
        cargo2.addTech(TechnologyGenerator.CONSTRUCTION_TECH4);
        allShipComponents.add(cargo2);

        cargo3 = new Cargo("Cargo module 3", "Cargo bay that holds raw materials", "cargo3.png", 600, 1100);
        cargo3.addFaction(federation, klingon, romulan, cardassian, dominion);
        cargo3.addTech(TechnologyGenerator.CONSTRUCTION_TECH6);
        allShipComponents.add(cargo3);

        cargo4 = new Cargo("Cargo module 4", "Cargo bay that holds raw materials", "cargo4.png", 1320, 2250);
        cargo4.addFaction(federation, klingon, romulan, cardassian, dominion);
        cargo4.addTech(TechnologyGenerator.CONSTRUCTION_TECH8);
        allShipComponents.add(cargo4);

        fusionReactor1 = new FusionReactor("Fusion Reactor 1", "Provides the ship with power", "fusion1.png", 180, 17);
        fusionReactor1.addFaction(federation, klingon, romulan, cardassian, dominion);
        allShipComponents.add(fusionReactor1);

        fusionReactor2 = new FusionReactor("Fusion Reactor 2", "Provides the ship with power", "fusion2.png", 260, 25);
        fusionReactor2.addFaction(federation, klingon, romulan, cardassian, dominion);
        fusionReactor2.addTech(TechnologyGenerator.ENERGY_TECH2);
        allShipComponents.add(fusionReactor2);

        fusionReactor3 = new FusionReactor("Fusion Reactor 3", "Provides the ship with power", "fusion3.png", 400, 42);
        fusionReactor3.addFaction(federation, klingon, romulan, cardassian, dominion);
        fusionReactor3.addTech(TechnologyGenerator.ENERGY_TECH5);
        allShipComponents.add(fusionReactor3);

        fusionReactor4 = new FusionReactor("Fusion Reactor 4", "Provides the ship with power", "fusion4.png", 920, 65);
        fusionReactor4.addFaction(federation, klingon, romulan, cardassian, dominion);
        fusionReactor4.addTech(TechnologyGenerator.ENERGY_TECH8);
        allShipComponents.add(fusionReactor4);

        miningLaser1 = new MiningLaser("Mining Laser 1", "Collects raw ore from asteroids", "mining1.png", 150, -10, 70);
        miningLaser1.addFaction(federation, klingon, romulan, cardassian, dominion);
        miningLaser1.addTech(TechnologyGenerator.CONSTRUCTION_TECH2, TechnologyGenerator.WEAPON_TECH1);
        allShipComponents.add(miningLaser1);

        miningLaser2 = new MiningLaser("Mining Laser 2", "Collects raw ore from asteroids", "mining2.png", 320, -13, 160);
        miningLaser2.addFaction(federation, klingon, romulan, cardassian, dominion);
        miningLaser2.addTech(TechnologyGenerator.CONSTRUCTION_TECH3, TechnologyGenerator.WEAPON_TECH2);
        allShipComponents.add(miningLaser2);

        miningLaser3 = new MiningLaser("Mining Laser 3", "Collects raw ore from asteroids", "mining3.png", 690, -20, 250);
        miningLaser3.addFaction(federation, klingon, romulan, cardassian, dominion);
        miningLaser3.addTech(TechnologyGenerator.CONSTRUCTION_TECH5, TechnologyGenerator.WEAPON_TECH3);
        allShipComponents.add(miningLaser3);

        torpedoLauncher1 = new TorpedoLauncher("Torpedo Launcher 1", "Fires torpedoes on enemy ships or planets", "torpedo1.png", 290, -15, 80, 8, 8);
        torpedoLauncher1.addFaction(federation, klingon, romulan, cardassian, dominion);
        torpedoLauncher1.addTech(TechnologyGenerator.WEAPON_TECH2);
        allShipComponents.add(torpedoLauncher1);

        torpedoLauncher2 = new TorpedoLauncher("Torpedo Launcher 2", "Fires torpedoes on enemy ships or planets", "torpedo2.png", 490, -20, 120, 10, 10);
        torpedoLauncher2.addFaction(federation, klingon, romulan, cardassian, dominion);
        torpedoLauncher2.addTech(TechnologyGenerator.WEAPON_TECH4);
        allShipComponents.add(torpedoLauncher2);

        torpedoLauncher3 = new TorpedoLauncher("Torpedo Launcher 3", "Fires torpedoes on enemy ships or planets", "torpedo3.png", 800, -28, 190, 12, 15);
        torpedoLauncher3.addFaction(federation, klingon, romulan, cardassian, dominion);
        torpedoLauncher3.addTech(TechnologyGenerator.WEAPON_TECH6);
        allShipComponents.add(torpedoLauncher3);

        torpedoLauncher4 = new TorpedoLauncher("Torpedo Launcher 4", "Fires torpedoes on enemy ships or planets", "torpedo4.png", 1300, -40, 290, 14, 20);
        torpedoLauncher4.addFaction(federation, klingon, romulan, cardassian, dominion);
        torpedoLauncher4.addTech(TechnologyGenerator.WEAPON_TECH9);
        allShipComponents.add(torpedoLauncher4);

        impulseDrive1 = new ImpulseDrive("Impulse Drive 1", "Makes the ship more maneuverable in combat", "impulse1.png", 260, 10, 10, 15);
        impulseDrive1.addFaction(federation, klingon, romulan, cardassian, dominion);
        impulseDrive1.addTech(TechnologyGenerator.PROPULSION_TECH2);
        allShipComponents.add(impulseDrive1);

        impulseDrive2 = new ImpulseDrive("Impulse Drive 2", "Makes the ship more maneuverable in combat", "impulse1.png", 430, 20, 15, 25);
        impulseDrive2.addFaction(federation, klingon, romulan, cardassian, dominion);
        impulseDrive2.addTech(TechnologyGenerator.PROPULSION_TECH5);
        allShipComponents.add(impulseDrive2);

        impulseDrive3 = new ImpulseDrive("Impulse Drive 3", "Makes the ship more maneuverable in combat", "impulse1.png", 840, 35, 22, 40);
        impulseDrive3.addFaction(federation, klingon, romulan, cardassian, dominion);
        impulseDrive3.addTech(TechnologyGenerator.PROPULSION_TECH7);
        allShipComponents.add(impulseDrive3);

        troopTransport1 = new TroopTransport("Troop Transport 1", "Allows this ship to carry troops", "trooptransport1.png", 315, -3, 8);
        troopTransport1.addFaction(federation, klingon, romulan, cardassian, dominion);
        troopTransport1.addTech(TechnologyGenerator.WEAPON_TECH1, TechnologyGenerator.BIO_TECH1);
        allShipComponents.add(troopTransport1);

        troopTransport2 = new TroopTransport("Troop Transport 2", "Allows this ship to carry troops", "trooptransport2.png", 595, -5, 12);
        troopTransport2.addFaction(federation, klingon, romulan, cardassian, dominion);
        troopTransport2.addTech(TechnologyGenerator.WEAPON_TECH3, TechnologyGenerator.BIO_TECH3);
        allShipComponents.add(troopTransport2);

        troopTransport3 = new TroopTransport("Troop Transport 3", "Allows this ship to carry troops", "trooptransport3.png", 905, -7, 20);
        troopTransport3.addFaction(federation, klingon, romulan, cardassian, dominion);
        troopTransport3.addTech(TechnologyGenerator.WEAPON_TECH6, TechnologyGenerator.BIO_TECH6);
        allShipComponents.add(troopTransport3);
    }

    /**
     * Gets a structure object by name
     *
     * @param name the name of the structure
     * @return the structure object, or null if not found
     */
    public static Structure getStructureByName(String name) {
        for (Structure s : allStructures) {
            if (s.getName().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Counts the number of fleets that is currently executing a colonizeOrder
     * in a given system
     *
     * @param starSystem the system
     * @return number of fleets colonizing the system
     */
    public static int countNumberOfColonizeOrdersInSystem(StarSystem starSystem) {
        int res = 0;
        for (Fleet f : starSystem.getFleets()) {
            if (f.getOrder() instanceof ColonizeOrder) {
                res++;
            }
        }
        return res;
    }

    public static Iterable<ShipComponent> getShipComponentsRequiringTechnology(Technology tech) {
        ArrayList<ShipComponent> res = new ArrayList<ShipComponent>();

        for (ShipComponent component : allShipComponents) {
            if (component.getTechsRequired().contains(tech)) {
                res.add(component);
            }
        }
        return res;
    }

    public static Iterable<HullClass> getHullClassesRequiringTechnology(Technology tech) {
        ArrayList<HullClass> res = new ArrayList<HullClass>();

        for (HullClass hull : allHullClasses) {
            if (hull.getTechsRequired().contains(tech)) {
                res.add(hull);
            }
        }
        return res;
    }

    public static Iterable<Structure> getStructuresRequiringTechnology(Technology tech) {
        ArrayList<Structure> res = new ArrayList<Structure>();

        for (Structure structure : allStructures) {
            if (structure.getTechsRequired().contains(tech)) {
                res.add(structure);
            }
        }
        return res;
    }
}