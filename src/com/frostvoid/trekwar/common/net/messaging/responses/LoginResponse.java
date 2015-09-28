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
 * Response by server to client authentication
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class LoginResponse extends Response {
    private boolean loginSuccessful = false;
    
    public LoginResponse(boolean successful) {
        loginSuccessful = successful;
    }
    
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
}