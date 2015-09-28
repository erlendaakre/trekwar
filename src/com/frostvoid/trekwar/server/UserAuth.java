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
package com.frostvoid.trekwar.server;

/**
 * Handles user authentication
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class UserAuth {
    public static String AUTHSERVER_HOST = "http://localhost";
    public static int AUTHSERVER_PORT = 8080;
    public static String AUTHSERVER_PATH = "/TrekwarOnline/services/auth";

    public Object authenticate(String authenticationCode) {

        String requestURL = AUTHSERVER_HOST + ":" + AUTHSERVER_PORT + AUTHSERVER_PATH + "?code=" + authenticationCode;

        // TODO
        // 1. Send auth code to Trekwar server
        // 2. Parse returned user data (user_id + username)
        // 3. (Make and) map common.User object to new user data
        return "";
    }
}
