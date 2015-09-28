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
package com.frostvoid.trekwar.common.net.messaging.responses;

import com.frostvoid.trekwar.common.net.messaging.Response;

/**
 * Tells the client how many seconds are left until next turn starts
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TimeToNextTurnResponse extends Response {
    private long millisecondsToNextTurn;
    private long currentTurn;
    private long nextTurn;

    public TimeToNextTurnResponse(long millisecondsToNextTurn, long currentTurn, long nextTurn) {
        this.millisecondsToNextTurn = millisecondsToNextTurn;
        this.currentTurn = currentTurn;
        this.nextTurn = nextTurn;
    }

    public long getMillisecondsToNextTurn() {
        return millisecondsToNextTurn;
    }

    public long getCurrentTurn() {
        return currentTurn;
    }

    public long getNextTurn() {
        return nextTurn;
    }
}