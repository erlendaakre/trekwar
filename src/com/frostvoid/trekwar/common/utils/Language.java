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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * I18N, using a simple properties file
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Language {
    public static final String ENGLISH = "english.txt";
    public static final String GERMAN = "german.txt";
    public static final String FRENCH = "french.txt";

    private Properties properties;

    /**
     * Loads a specific language file
     *
     * @param filename the filename of the language file
     * @throws IOException if unable to read file
     */
    public Language(String filename) throws IOException {
        File languageFile = new File(filename);
        if (!languageFile.canRead())
            throw new IOException("Can't read from file");
        properties = new Properties();
        properties.load(new FileInputStream(languageFile));
    }

    /**
     * Gets a value from the language file, or ###key### if not found
     *
     * @param key the key to find value for
     * @return the value
     */
    public String get(String key) {
        return properties.getProperty(key, "###" + key + "###");
    }

    /**
     * Gets value from language file, forces first character to be upper case.
     * returns ###key### if value not found
     *
     * @param key the key to find value for
     * @return the value, first char in upper case
     */
    public String getU(String key) {
        String s = get(key);
        return s.toUpperCase().charAt(0) + s.substring(1);
    }


    /**
     * Gets value from language file, converter to upper case
     * returns ###key### if value not found
     *
     * @param key the key to find value for
     * @return the value in upper case
     */
    public String getUC(String key) {
        return get(key).toUpperCase();
    }


    /**
     * Gets value from language file, converter to camel case
     * returns ###key### if value not found
     *
     * @param key the key to find value for
     * @return the value in camel case
     */
    public String getCC(String key) {
        String value = get(key);
        StringBuilder sb = new StringBuilder();
        for (String s : value.split(" ")) {
            sb.append(s.substring(0, 1).toUpperCase());
            sb.append(s.substring(1).toLowerCase());
            sb.append(" ");
        }
        return sb.toString().trim();
    }


    /**
     * Allow variable in the translation values, example: "you have %1 mails"
     * <p>
     * Takes in an arbitrary number of Objects:
     * First object must be a string with the substring "%x" where x is a
     * number from 1 to n.
     * <p>
     * all other objects will have their toString() inserted into the first string
     * at the matching %x tag.
     * <p>
     * example:
     * pop("all %1 your %2 are %3", "your", "base", 42) will return the string:
     * "all your base are 42"
     *
     * @param args arbitrary number of objects, first must be string
     * @return the first argument (string) with the other values inserted
     */
    public static String pop(Object... args) {
        String main = args[0].toString();

        for (int i = 1; i < args.length; i++) {
            main = main.replace("%" + i, args[i].toString());
        }

        return main;
    }
}
