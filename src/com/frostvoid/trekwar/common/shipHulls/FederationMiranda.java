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
 * Federation ship
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class FederationMiranda extends HullClass {

    public FederationMiranda() {
        super("Miranda class", "A lightly armed destroyer", "fed_16x16.png", "fed_miranda.png",
                80, 1250, 260, 230, 40, 10, false, 180, 5);

        addFaction(StaticData.federation);

        addTechRequirement(TechnologyGenerator.BIO_TECH2,
                TechnologyGenerator.CONSTRUCTION_TECH2,
                TechnologyGenerator.ENERGY_TECH2,
                TechnologyGenerator.PROPULSION_TECH2,
                TechnologyGenerator.COMPUTER_TECH2,
                TechnologyGenerator.WEAPON_TECH2);
    }
}