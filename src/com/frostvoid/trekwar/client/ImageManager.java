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
package com.frostvoid.trekwar.client;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.ImageIcon;

/**
 * Responsible for loading and keeping the references to all the images
 * used in the game
 * 
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ImageManager {
    private HashMap<String, ImageIcon> data;
    private static ImageManager instance;

    private ImageManager() {
        instance = this;
        data = new HashMap<String, ImageIcon>();
    }

    public static ImageManager getInstance() {
        if(instance == null)
            instance = new ImageManager();
        return instance;
    }

    public void preloadImages() {
        //getImage("graphics/bg.jpg");
    }

    public ImageIcon getImage(String filename) {
        ImageIcon res = data.get(filename);
        if(res == null) {
            res = new ImageIcon(filename);

            if(res == null) {
                System.err.println("ERROR: UNABLE TO LOAD GRAPHICS: " + filename);
                System.exit(1);
            }
            data.put(filename, res);
        }
        return res;
    }

    public void storeAllImageFilenamesToFile() {
        FileWriter fw = null;
        try {
            fw = new FileWriter("allfilenames.txt");
            for (String filename : data.keySet()) {
                fw.write(filename + "\n");
            }
        } catch (IOException ex) {
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
            }
        }
    }
}