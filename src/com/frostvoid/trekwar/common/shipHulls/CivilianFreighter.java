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
 * Civilian freighter
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class CivilianFreighter extends HullClass {
    public CivilianFreighter() {
        super("Civilian Freighter", "A medium sized freighter", "civ_freighter_16x16.png", "civ_freighter.png",
                25, 1000, 90, 30, 5, 8, true, 80, 3);

        addFaction(StaticData.federation, StaticData.klingon, StaticData.romulan, StaticData.cardassian, StaticData.dominion);

        addTechRequirement(TechnologyGenerator.BIO_TECH2,
                TechnologyGenerator.CONSTRUCTION_TECH2,
                TechnologyGenerator.ENERGY_TECH2,
                TechnologyGenerator.PROPULSION_TECH2,
                TechnologyGenerator.COMPUTER_TECH2,
                TechnologyGenerator.WEAPON_TECH0);
    }
}