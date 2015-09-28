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
 * Structure that provides a system with research
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Laboratory extends Structure {

    public Laboratory(String name, String description,
            int cost, int power, int research, int food, int industry, String imageFileName) {
        super(name, description, cost, power, research, food, industry, imageFileName, Structure.mode.NORMAL);

        addFaction(StaticData.federation, StaticData.klingon, StaticData.romulan, StaticData.cardassian, StaticData.dominion);
    }
}