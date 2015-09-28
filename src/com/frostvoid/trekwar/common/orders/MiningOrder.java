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
import com.frostvoid.trekwar.server.TrekwarServer;

import java.util.logging.Level;

/**
 * Makes all ships in a fleet with mining lasers mine asteroid ore into
 * the ships cargo bay
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MiningOrder extends Order {

    private User user;
    private StarSystem starsystem;
    private Fleet fleet;

    private int totalAmountMined;

    public MiningOrder(User user, StarSystem system, Fleet fleet) throws InvalidOrderException {
        this.user = user;
        this.starsystem = system;
        this.fleet = fleet;


        if (!fleet.canMine()) {
            throw new InvalidOrderException("The fleet is not equipped to mine asteroid belts");
        }
        if (!starsystem.getStarSystemClassification().equals(StarSystemClassification.asteroid)) {
            throw new InvalidOrderException("Not a asteroid belt: " + starsystem.getStarSystemClassification());
        }
    }

    @Override
    public void execute() {
        if (!orderCompleted) {
            int amountMined = 0;
            for (Ship s : fleet.getShips()) {
                if (starsystem.getResourcesLeft() <= 0) {
                    starsystem.setStarSystemClassification(StarSystemClassification.empty);
                    orderCompleted = true;
                    break;
                }
                if (s.canMine() && s.getAvailableCargoSpace() > 0) {
                    int amount = s.getMiningOutput();
                    if (amount > starsystem.getResourcesLeft())
                        amount = starsystem.getResourcesLeft();
                    if (amount > s.getAvailableCargoSpace())
                        amount = s.getAvailableCargoSpace();

                    s.setCargoOre(s.getCargoOre() + amount);
                    starsystem.setResourcesLeft(starsystem.getResourcesLeft() - amount);
                    amountMined += amount;

                }
            }
            totalAmountMined += amountMined;

            if (amountMined <= 0) {
                orderCompleted = true;
            }
        }
    }

    @Override
    public void onComplete() {
        // TODO TURN REPORT
        TrekwarServer.getLog().log(Level.INFO, "fleet {0} by user {1} completed mining in [{2},{3}]. Ore collected: {4}", new Object[]{fleet.getName(), fleet.getUser().getUsername(), fleet.getX(), fleet.getY(), totalAmountMined});
        fleet.setOrder(null);
    }

    @Override
    public String toString() {
        return Client.getLanguage().get("order_mining") + " (" + totalAmountMined + ")";
    }

    @Override
    public void onCancel() {
        fleet.setOrder(null);
    }

}
