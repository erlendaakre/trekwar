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
 * Weapon that does high damage against enemy ships armor and hull. Also
 * allows ship to bombard planets
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TorpedoLauncher extends ShipComponent {
    private int baseDamage;
    private int actionPointsRequired;
    private int structureHitChance;

    public TorpedoLauncher(String name, String description, String iconFileName, int cost, int energy, int damage, int actionPointsRequired, int structureHitChance) {
        super(name, description, iconFileName, cost, energy, false);
        this.baseDamage = damage;
        this.actionPointsRequired = actionPointsRequired;
        this.structureHitChance = structureHitChance;
        icon16x16Filename = "torpedo_16x16.png";
    }

    public int getDamage() {
        return baseDamage;
    }

    /**
     * Gets the number of AP's required to fire this weapon
     *
     * @return action points required to fire
     */
    public int getActionPointsRequired() {
        return actionPointsRequired;
    }

    /**
     * Gets this torpedo launchers chance to hit a structure when bombarding planet
     *
     * @return chance (percent) to hit a structure
     */
    public int getStructureHitChance() {
        return structureHitChance;
    }
}