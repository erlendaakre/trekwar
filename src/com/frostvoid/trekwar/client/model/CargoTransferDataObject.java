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
package com.frostvoid.trekwar.client.model;

import com.frostvoid.trekwar.common.CargoClassification;

/**
 * This object holds information used for when doing cargo transfers.
 * Used then doing drag/drop in the CargoTransferWindow
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class CargoTransferDataObject {
    private int shipId;
    private boolean toSystem;
    private CargoClassification cargo;

    public CargoTransferDataObject(int shipId, boolean toSystem, CargoClassification cargo) {
        this.shipId = shipId;
        this.toSystem = toSystem;
        this.cargo = cargo;
    }

    public int getShipId() {
        return shipId;
    }

    public void setShipId(int shipId) {
        this.shipId = shipId;
    }

    /**
     * @return true if this transfer is from ship to system. false if from system to ship
     */
    public boolean isToSystem() {
        return toSystem;
    }

    public void setToSystem(boolean toSystem) {
        this.toSystem = toSystem;
    }

    public CargoClassification getCargo() {
        return cargo;
    }

    public void setCargo(CargoClassification cargo) {
        this.cargo = cargo;
    }
}