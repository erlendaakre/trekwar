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
package com.frostvoid.trekwar.common.structures;

import com.frostvoid.trekwar.common.StaticData;

/**
 * Structure that protects troops and civilians from orbital bombardment, also
 * strengthens against invading troops
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Bunker extends Structure {

    private int troopCapacity;

    public Bunker(String name, int cost, int power, int research, int food, int industry, String imageFileName, int troopCapacity) {
        super(name, "Bunkers allows for a greater military presence on the ground, and shields troop/population against enemy orbital bombardment",
                cost, power, research, food, industry, imageFileName, Structure.mode.ONEPERPLANET);

        this.troopCapacity = troopCapacity;
        addFaction(StaticData.federation, StaticData.klingon, StaticData.romulan, StaticData.cardassian, StaticData.dominion);
    }

    public int getTroopCapacity() {
        return troopCapacity;
    }
}