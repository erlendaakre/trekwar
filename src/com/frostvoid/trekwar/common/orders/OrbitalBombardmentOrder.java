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
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.exceptions.InvalidOrderException;
import com.frostvoid.trekwar.server.turnExec.OrbitalBombardmentResolver;

/**
 * Tells a fleet to bomb the shit out of some noob ass planets.
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class OrbitalBombardmentOrder extends Order {
    
    private Fleet fleet;
    private StarSystem system;
    
    public OrbitalBombardmentOrder(Fleet fleet, StarSystem system) throws InvalidOrderException {
        this.fleet = fleet;
        this.system = system;
        
        if(!fleet.canBombPlanets()) {
            throw new InvalidOrderException("Fleet can not bomb planets");
        }
        if(system.getUser().equals(StaticData.nobodyUser)) {
            throw new InvalidOrderException("Target system is not owned by anybody");
        }
        if(fleet.getUser().getFaction().equals(system.getUser().getFaction())) {
            throw new InvalidOrderException("Target systme is not an enemy");
        }
    }
    
    public void execute() {
        if(!orderCompleted) {
            // make sure we don't attack the system if it's invaded
            // and that the fleet is still able to bomb (ship with torpedoes not destroyed)
            if(! fleet.getUser().getFaction().equals(system.getUser().getFaction()) && fleet.canBombPlanets()) {
                OrbitalBombardmentResolver obr = new OrbitalBombardmentResolver(fleet, system);
                obr.resolve();
                
                if(system.getPopulation() == 0) {
                    system.getUser().removeSystem(system);
                    system.setUser(StaticData.nobodyUser);
                    orderCompleted = true;
                }
            }
            else {
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
        return Client.getLanguage().get("order_orbital_bombardment");
    }

    @Override
    public void onCancel() {
        fleet.setOrder(null);
    }
}