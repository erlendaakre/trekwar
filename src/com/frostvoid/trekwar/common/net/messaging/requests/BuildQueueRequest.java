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

import com.frostvoid.trekwar.common.net.messaging.BuildQueueRequestType;
import com.frostvoid.trekwar.common.net.messaging.Request;

/**
 * Tells server to do something with a build queue item
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BuildQueueRequest extends Request {
    private int x;
    private int y;
    private int index; // index of build queue item to operate on
    private BuildQueueRequestType action;

    public BuildQueueRequest(int x, int y, int index, BuildQueueRequestType action) {
        this.x = x;
        this.y = y;
        this.index = index;
        this.action = action;
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
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return the action
     */
    public BuildQueueRequestType getAction() {
        return action;
    }
}