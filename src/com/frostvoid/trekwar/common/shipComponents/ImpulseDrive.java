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
package com.frostvoid.trekwar.common.shipComponents;

/**
 * Slower than light engine, used for maneuverability in combat
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ImpulseDrive extends ShipComponent {
    private int power;
    private int actionPointBonus;
    
    public ImpulseDrive(String name, String description, String iconFileName, int cost, int energy, int power, int actionPointBonus) {
        super(name, description, iconFileName, cost, energy, false);
        this.power = power;
        this.actionPointBonus = actionPointBonus;
        icon16x16Filename = "impulse_16x16.png";
    }
    
    public int getPower() {
        return power;
    }
    
    public int getActionPointBonus() {
        return actionPointBonus;
    }
}