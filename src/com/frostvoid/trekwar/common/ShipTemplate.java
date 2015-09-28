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
package com.frostvoid.trekwar.common;

import com.frostvoid.trekwar.common.exceptions.ShipException;
import com.frostvoid.trekwar.common.exceptions.SlotException;
import com.frostvoid.trekwar.common.shipComponents.ShipComponent;
import com.frostvoid.trekwar.common.shipHulls.HullClass;

import java.io.Serializable;
import java.util.HashMap;

/**
 * A template for ships that can be produced. is defined by base hull and components
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ShipTemplate extends Ship implements Serializable {

    /**
     * Creates a new Ship template
     *
     * @param user      the owner of the template
     * @param name      the name of the tempalte
     * @param hullClass the hull class of the ship
     */
    public ShipTemplate(User user, String name, HullClass hullClass) {
        this.user = user;
        this.templateName = name;
        this.hullClass = hullClass;
        this.components = new HashMap<Integer, ShipComponent>();
    }

    /**
     * Clones this template, making an identical one (same hull and components)
     *
     * @param newName the new name
     * @return the cloned template
     */
    public ShipTemplate cloneTemplate(String newName) throws ShipException {
        ShipTemplate clone = new ShipTemplate(user, newName, hullClass);

        for (int i = 0; i < hullClass.getSlots(); i++) {
            if (components.get(i) != null) {
                try {
                    clone.setComponent(i, components.get(i));
                } catch (SlotException e) {
                    throw new ShipException("Unable to clone template, can't set component in cloned object: " + e.getMessage());
                }
            }
        }
        return clone;
    }

    /**
     * Checks if this template is valid or not
     *
     * @return true if template is valid, false otherwise
     */
    public boolean isValid() {
        if (getSpeed() < 2) {
            return false;
        }
        if (getEnergy() < 0) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ShipTemplate) {
            ShipTemplate other = (ShipTemplate) o;
            if (getName() == other.getName() && getUser().equals(other.getUser())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.templateName != null ? this.templateName.hashCode() : 0);
        hash = 59 * hash + "template".hashCode();
        hash = 59 * hash + (this.user != null ? this.user.hashCode() : 0);
        return hash;
    }
}