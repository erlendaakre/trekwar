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
import com.frostvoid.trekwar.common.net.messaging.TroopTransferRequestType;

/**
 * Transfer troops between system <---> ship
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TroopTransferRequest extends Request {
    private TroopTransferRequestType type;
    private int x;
    private int y;
    private int shipID;
    private int amount;
    
    public TroopTransferRequest(TroopTransferRequestType type, int x, int y, int shipID, int amount) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.shipID = shipID;
        this.amount = amount;
    }

    /**
     * @return the type
     */
    public TroopTransferRequestType getType() {
        return type;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the shipID
     */
    public int getShipID() {
        return shipID;
    }

    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }
}