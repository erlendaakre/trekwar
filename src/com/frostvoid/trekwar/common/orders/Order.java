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

import java.io.Serializable;

/**
 * The base order class, indicates that an action (fleet action for example)
 * is to take place
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public abstract class Order implements Serializable {

    protected String name;
    protected boolean orderCompleted;
    protected int turnsLeft;

    /**
     * this method is called on every turn, should at one
     * point change the orderCompleted flag to true
     * @see orderCompleted
     */
    public abstract void execute();

    /**
     * this method is called when the isCompleted() method
     * return true, will cause the order object to be destroyed
     * @see isCompleted
     */
    public abstract void onComplete();

    /**
     * Must be run if order is canceled, to do cleanup (if any)
     */
    public abstract void onCancel();

    /**
     * Gets a string representation of this order
     * @return description of the order
     */
    public abstract String toString();

    /**
     * Checks if the order has been completed
     * @return true if order has been completed
     */
    public boolean isCompleted() {
        return orderCompleted;
    }

    /**
     * Gets the number of turns until this order is completed/done
     * @return number of turns to completion
     */
    public int getTurnsLeft() {
        return turnsLeft;
    }

    /**
     * Sets number of turns left until order is executed.
     * 
     * @param turnsLeft number of turns left
     */
    public void setTurnsLeft(int turnsLeft) {
        this.turnsLeft = turnsLeft;
    }
}