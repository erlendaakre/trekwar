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
 * Klingon ship
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class KlingonKvort extends HullClass {

    public KlingonKvort() {
        super("K'vort class", "A larger version of the B'rel class ship", "kli_16x16.png", "kli_kvort.png",
                60, 3000, 320, 335, 30, 11, false, 280, 9);
        
        addFaction(StaticData.klingon);
        
        addTechRequirement(TechnologyGenerator.BIO_TECH3,
                TechnologyGenerator.CONSTRUCTION_TECH4,
                TechnologyGenerator.ENERGY_TECH4,
                TechnologyGenerator.PROPULSION_TECH4,
                TechnologyGenerator.COMPUTER_TECH4,
                TechnologyGenerator.WEAPON_TECH4);
    }
}