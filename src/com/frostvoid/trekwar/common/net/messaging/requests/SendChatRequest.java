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
 * Sends a chat line from the client to the server
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SendChatRequest extends Request {
    private String destination; // channel or user
    private String message;
    private boolean privateMessage;
    
    /**
     * 
     * @param destination a channel or user name
     * @param message the message
     * @param privateMessage if true, destination is a user. otherwise it's a channel
     */
    public SendChatRequest(String destination, String message, boolean privateMessage) {
        this.destination = destination;
        this.message = message;
        this.privateMessage = privateMessage;
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the privateMessage
     */
    public boolean isPrivateMessage() {
        return privateMessage;
    }   
}