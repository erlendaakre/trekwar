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
package com.frostvoid.trekwar.common.orders;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.exceptions.InvalidOrderException;

/**
 * An order that makes a fleets ship with deuterium collectors harvest
 * deuterium into the cargo bay
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class HarvestDeuteriumOrder extends Order {

    private User user;
    private StarSystem starsystem;
    private Fleet fleet;

    private int totalAmountHarvested;

    public HarvestDeuteriumOrder(User user, StarSystem system, Fleet fleet) throws InvalidOrderException {
        this.user = user;
        this.starsystem = system;
        this.fleet = fleet;


        if (!fleet.canHarvestDeuterium()) {
            throw new InvalidOrderException("The fleet is not equipped to harvest deuterium");
        }
        if (!starsystem.getStarSystemClassification().equals(StarSystemClassification.nebula)) {
            throw new InvalidOrderException("Not a nebula: " + starsystem.getStarSystemClassification());
        }
    }

    @Override
    public void execute() {
        if (!orderCompleted) {
            int amountHarvested = 0;
            for (Ship s : fleet.getShips()) {
                if (starsystem.getResourcesLeft() <= 0) {
                    starsystem.setStarSystemClassification(StarSystemClassification.empty);
                    orderCompleted = true;
                    break;
                }
                if (s.canHarvestDeuterium() && s.getAvailableCargoSpace() > 0) {
                    int amount = s.getBussardCollectorCapacity();
                    if (amount > starsystem.getResourcesLeft())
                        amount = starsystem.getResourcesLeft();
                    if (amount > s.getAvailableCargoSpace())
                        amount = s.getAvailableCargoSpace();

                    s.setCargoDeuterium(s.getCargoDeuterium() + amount);
                    starsystem.setResourcesLeft(starsystem.getResourcesLeft() - amount);
                    amountHarvested += amount;

                }
            }
            totalAmountHarvested += amountHarvested;

            // stop if no ships can fit more deuterium in cargo hold
            if (amountHarvested <= 0) {
                orderCompleted = true;
            }
        }
    }

    @Override
    public void onComplete() {
        // TODO TURN REPORT
        fleet.setOrder(null);
    }

    @Override
    public String toString() {
        return Client.getLanguage().get("order_harvestingdeuterium") + " (" + totalAmountHarvested + ")";
    }

    @Override
    public void onCancel() {
        fleet.setOrder(null);
    }

}