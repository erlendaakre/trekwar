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
package com.frostvoid.trekwar.common.net.messaging.requests;

import com.frostvoid.trekwar.common.net.messaging.Request;

/**
 * Tells server to move a ship form one fleet to another
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MoveShipToFleetRequest extends Request {
    private String sourceFleet;
    private int shipID;
    private String destinationFleet;
    
    public MoveShipToFleetRequest(String sourceFleet, int shipID, String destinationFleet) {
        this.sourceFleet = sourceFleet;
        this.shipID = shipID;
        this.destinationFleet = destinationFleet;
    }

    /**
     * @return the sourceFleet
     */
    public String getSourceFleet() {
        return sourceFleet;
    }

    /**
     * @return the shipID
     */
    public int getShipID() {
        return shipID;
    }

    /**
     * @return the destinationFleet
     */
    public String getDestinationFleet() {
        return destinationFleet;
    }   
}