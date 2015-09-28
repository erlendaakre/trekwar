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
 * Tells server to rename a player fleet
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class RenameFleetRequest extends Request {
    private String currentName;
    private String newName;

    public RenameFleetRequest(String currentName, String newName) {
        this.currentName = currentName.trim();
        this.newName = newName.trim();
    }

    /**
     * @return the currentName
     */
    public String getCurrentName() {
        return currentName;
    }

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }
}