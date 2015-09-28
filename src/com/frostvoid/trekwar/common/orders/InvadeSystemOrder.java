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
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.TurnReportItem;
import com.frostvoid.trekwar.server.TrekwarServer;
import com.frostvoid.trekwar.server.turnExec.GroundCombatResolver;

import java.util.logging.Level;

/**
 * Order that makes all ship in a fleet use its troops to invade an enemy system
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class InvadeSystemOrder extends Order {
    private Fleet attackingFleet;
    private StarSystem target;
    private GroundCombatResolver gcr;

    public InvadeSystemOrder(Fleet attackingFleet, StarSystem target) {
        this.attackingFleet = attackingFleet;
        this.target = target;
        turnsLeft = 1;
    }

    @Override
    public void execute() {

        if (!attackingFleet.getUser().getFaction().equals(target.getUser().getFaction()) &&
                attackingFleet.getX() == target.getX() && attackingFleet.getY() == target.getY()) {
            gcr = new GroundCombatResolver(attackingFleet, target);
            gcr.resolve(false);
        }
        orderCompleted = true;
    }

    @Override
    public void onComplete() {
        if (gcr != null) {
            if (gcr.didAttackerWin()) {
                TurnReportItem atr = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), target.getX(), target.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                atr.setSummary("Invasion of the " + target.getName() + " system was successful");
                atr.setDetailed("Our troops have secured the " + target.getName() + " system, with " + gcr.getAttackerLosses() + " casualties");
                gcr.getWinner().addTurnReport(atr);

                TurnReportItem dtr = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), target.getX(), target.getY(), TurnReportItem.TurnReportSeverity.CRITICAL);
                dtr.setSummary("We've lost the " + target.getName() + " system");
                dtr.setDetailed(gcr.getWinner().getUsername() + " of the " + gcr.getWinner().getFaction().getName() +
                        " has defeated our forces in the " + target.getName() + " system, the system is no longer under our control");
                gcr.getLooser().addTurnReport(dtr);

                gcr.getWinner().addSystem(target);
                gcr.getLooser().removeSystem(target);
                target.setUser(gcr.getWinner());
                target.getBuildQueue().clear();

                int troopTransfer = attackingFleet.getTroops() / 2;
                if (troopTransfer > target.getTroopCapacity()) {
                    troopTransfer = target.getTroopCapacity();
                }
                target.setTroopCount(troopTransfer);
                for (; troopTransfer > 0; troopTransfer--) {
                    attackingFleet.decrementTroops();
                }
                TrekwarServer.getLog().log(Level.FINE, "{0} has invaded the {1} system, owned by {2}", new Object[]{gcr.getWinner().getUsername(), target.getName(), gcr.getLooser().getUsername()});
            } else {
                TurnReportItem atr = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), target.getX(), target.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                atr.setSummary("Invasion of the " + target.getName() + " failed");
                atr.setDetailed("All our " + gcr.getAttackerLosses() + " troops were lost, trying to secure the " + target.getName() +
                        " system, the enemy suffered " + gcr.getDefenderLosses() + " casualties");
                gcr.getLooser().addTurnReport(atr);

                TurnReportItem dtr = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), target.getX(), target.getY(), TurnReportItem.TurnReportSeverity.HIGH);
                dtr.setSummary("Our troops have defended the " + target.getName() + " system");
                dtr.setDetailed(gcr.getWinner().getUsername() + " of the " + gcr.getWinner().getFaction().getName() +
                        " was repelled while trying to invade the " + target.getName() + " system, our forces took " + gcr.getDefenderLosses() + " casualties");
                gcr.getWinner().addTurnReport(dtr);

                TrekwarServer.getLog().log(Level.FINE, "{0} failed in invading the {1} system, owned by {2}", new Object[]{gcr.getLooser().getUsername(), target.getName(), gcr.getWinner().getUsername()});
            }
        }
        attackingFleet.setOrder(null);
    }

    @Override
    public void onCancel() {
        attackingFleet.setOrder(null);
    }

    @Override
    public String toString() {
        return Client.getLanguage().get("order_invading_system") + ": " + target.getName();
    }
}