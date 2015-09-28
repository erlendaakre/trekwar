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
package com.frostvoid.trekwar.common.utils;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.User;
import com.frostvoid.trekwar.common.exceptions.ValidationException;

/**
 * Assorted methods for validating data / input
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Validator {

    public static boolean validateFleetName(String name, User user) throws ValidationException {
        if (name.length() < 3) {
            throw new ValidationException(Client.getLanguage().getU("name_of_fleet_to_short_must_be_3_characters_or_more"));
        }

        // fleet name must contain ONLY these characters
        String allowedchars = "abcdefghijklmnopqrstuvwxyz0123456789 ";
        for (int i = 0; i < name.length(); i++) {
            if (!allowedchars.contains("" + ("" + name.charAt(i)).toLowerCase())) {
                throw new ValidationException(Client.getLanguage().getU("name_of_fleet_contains_invalid_characters"));
            }
        }

        // if fleet with that name already exists, return false
        if (user.getFleetByName(name) != null) {
            throw new ValidationException(Client.getLanguage().getU("name_of_fleet_must_be_unique"));
        }
        return true;
    }
}