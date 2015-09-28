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
 * Tells server to decommission (if in user owned system) or self destruct (anywhere else)
 * a ship.
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class DestroyDecommissionShipRequest extends Request {
    private String sourceFleet;
    private int shipID;

    public DestroyDecommissionShipRequest(String sourceFleet, int shipID) {
        this.sourceFleet = sourceFleet;
        this.shipID = shipID;
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
}