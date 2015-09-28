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
public class FederationConstitution extends HullClass {

    public FederationConstitution() {
        super("Constitution class", "A really nice looking cruiser", "fed_16x16.png", "fed_constitution.png", 
                190, 3300, 310, 330, 30, 14, false, 300, 8);
        
        addFaction(StaticData.federation);

        addTechRequirement(TechnologyGenerator.BIO_TECH3,
                TechnologyGenerator.CONSTRUCTION_TECH4,
                TechnologyGenerator.ENERGY_TECH4, 
                TechnologyGenerator.PROPULSION_TECH4,
                TechnologyGenerator.COMPUTER_TECH4, 
                TechnologyGenerator.WEAPON_TECH4);
    }
}