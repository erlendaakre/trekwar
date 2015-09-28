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

import java.io.Serializable;
import java.util.Date;

/**
 * A single line of text in the chat
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ChatLine implements Serializable {

    private static final long serialVersionUID = 710930472679180587L;
    private String userName;
    private Date timestamp;
    private String channel;
    private String message;

    /**
     * Creates a new chat line
     *
     * @param userName  the user who wrote the text
     * @param timestamp when it was written
     * @param channel   the target channel (or user)
     * @param message   the message
     */
    public ChatLine(String userName, Date timestamp, String channel, String message) {
        this.userName = userName;
        this.timestamp = timestamp;
        this.channel = channel;
        this.message = message;
    }

    /**
     * Gets the channel this message was sent to
     * or "priv USERNAME" if it's a private message
     *
     * @return target for message
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Sets the channel (or user) which is the recipient of this message
     *
     * @param channel message target
     */
    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     * Gets the message
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message (actual text being written)
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the time at which this line was written
     *
     * @return
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the author of this message
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the author of this message
     *
     * @param userName the username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Checks if this is a private messare, or a message intended for a channel
     *
     * @return true if private message
     */
    public boolean isPrivateMsg() {
        return channel.toLowerCase().startsWith("priv");
    }
}
