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
 * Weapon component, fires a beam weapon (good against shields) on enemy ships
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BeamEmitter extends ShipComponent {
    private int baseDamage;
    private int actionPointsRequired;
    
    public BeamEmitter(String name, String description, String iconFileName, int cost, int energy, int damage, int actionPointsRequired) {
        super(name, description, iconFileName, cost, energy, false);
        this.baseDamage = damage;
        this.actionPointsRequired = actionPointsRequired;
        icon16x16Filename = "beamemitter_16x16.png";
    }
    
    public int getDamage() {
        return baseDamage;
    }
    
    /**
     * Gets the number of AP's required to fire this weapon
     * 
     * @return ap's required to fire
     */
    public int getActionPointsRequired() {
        return actionPointsRequired;
    }
}