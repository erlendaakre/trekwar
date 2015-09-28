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

import com.frostvoid.trekwar.common.TechnologyGenerator;
import com.frostvoid.trekwar.common.StaticData;

/**
 * Klingon ship
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class KlingonRaptor extends HullClass {

    public KlingonRaptor() {
        super("Raptor class", "A small warship", "fkli_16x16.png", "kli_raptor.png",
                20, 600, 175, 135, 45, 5, false, 100, 2);
        
        addFaction(StaticData.klingon);
        
        addTechRequirement(TechnologyGenerator.BIO_TECH1,
                TechnologyGenerator.CONSTRUCTION_TECH1,
                TechnologyGenerator.ENERGY_TECH1,
                TechnologyGenerator.PROPULSION_TECH1,
                TechnologyGenerator.COMPUTER_TECH1,
                TechnologyGenerator.WEAPON_TECH1);
    }
}