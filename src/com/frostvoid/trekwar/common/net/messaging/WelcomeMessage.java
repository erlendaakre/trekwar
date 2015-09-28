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

/**
 * Server sends this object to all clients that connect
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class WelcomeMessage extends Response {
    private String serverName;
    private String serverVersion;
    private String serverURL;
    private String motd;
    
    public WelcomeMessage(String serverName, String serverVersion, String serverURL, String motd) {
        this.serverName = serverName;
        this.serverVersion = serverVersion;
        this.serverURL = serverURL;
        this.motd = motd;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }
    
    public String getServerVersion() {
        return serverVersion;
    }

    /**
     * @return the serverURL
     */
    public String getServerURL() {
        return serverURL;
    }

    /**
     * @return the motd
     */
    public String getMotd() {
        return motd;
    }
}