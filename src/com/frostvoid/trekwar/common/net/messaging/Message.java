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
package com.frostvoid.trekwar.common.net.messaging;

import java.io.Serializable;

/**
 * Base class for all messages/commands being sent between client and server
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Message implements Serializable {
    private String username;
    private String sender_ip; // set by server when message received
    private long timestamp_sent; // unix time, set by server when message received

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the sender_ip
     */
    public String getSender_ip() {
        return sender_ip;
    }

    /**
     * @param sender_ip the sender_ip to set
     */
    public void setSender_ip(String sender_ip) {
        this.sender_ip = sender_ip;
    }

    /**
     * @return the timestamp_sent
     */
    public long getTimestamp_sent() {
        return timestamp_sent;
    }

    /**
     * @param timestamp_sent the timestamp_sent to set
     */
    public void setTimestamp_sent(long timestamp_sent) {
        this.timestamp_sent = timestamp_sent;
    }
}