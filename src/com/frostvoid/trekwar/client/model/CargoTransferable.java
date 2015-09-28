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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * Transferable D&D support class used for transferring cargo
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class CargoTransferable implements Transferable {
    private CargoTransferDataObject data;

    /**
     * Default constructor
     *
     * @param data the data relating to this particular transfer
     */
    public CargoTransferable(CargoTransferDataObject data) {
        this.data = data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{new DataFlavor(this.getClass(), "Cargo")};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
            return true;       
    }

    @Override
    public CargoTransferDataObject getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return data;
    }
}