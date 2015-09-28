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
 * Enables the ship to move faster than light, the more warp cores the faster
 * the ship can move across the map
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class WarpCore extends ShipComponent {
    private int baseSpeed;

    public WarpCore(String name, String description, String iconFileName, int cost, int energy, int speed) {
        super(name, description, iconFileName, cost, energy, false);
        this.baseSpeed = speed;
        icon16x16Filename = "warpcore_16x16.png";
    }

    public int getSpeed() {
        return baseSpeed;
    }
}