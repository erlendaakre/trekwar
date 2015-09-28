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
public class KlingonNeghvar extends HullClass {

    public KlingonNeghvar() {
        super("Negh'Var class", "A huge warship", "kli_16x16.png", "kli_neghvar.png",
                210, 12300, 990, 1280, 0, 18, false, 1200, 22);

        addFaction(StaticData.klingon);

        addTechRequirement(TechnologyGenerator.BIO_TECH8,
                TechnologyGenerator.CONSTRUCTION_TECH10,
                TechnologyGenerator.ENERGY_TECH9,
                TechnologyGenerator.PROPULSION_TECH9,
                TechnologyGenerator.COMPUTER_TECH10,
                TechnologyGenerator.WEAPON_TECH9);
    }
}