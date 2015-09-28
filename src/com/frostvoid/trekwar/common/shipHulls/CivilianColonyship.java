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
package com.frostvoid.trekwar.common.shipHulls;

import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.TechnologyGenerator;

/**
 * Civilian colonyship
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class CivilianColonyship extends HullClass {
    public CivilianColonyship() {
        super("Civilian Colonyship", "A ship used to colonize planets", "civ_colonyship_16x16.png", "civ_colonyship.png",
                30, 800, 70, 15, 5, 5, true, 80, 1);
        
        addFaction(StaticData.federation, StaticData.klingon, StaticData.romulan, StaticData.cardassian, StaticData.dominion);
        addTechRequirement(TechnologyGenerator.BIO_TECH0, TechnologyGenerator.CONSTRUCTION_TECH0,
                TechnologyGenerator.ENERGY_TECH0, TechnologyGenerator.PROPULSION_TECH0,
                TechnologyGenerator.COMPUTER_TECH0, TechnologyGenerator.WEAPON_TECH0);
    }
}