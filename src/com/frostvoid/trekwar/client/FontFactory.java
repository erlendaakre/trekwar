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

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Handles all custom fonts
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class FontFactory {
    private static FontFactory instance;

    private Font finalFrontierNew;
    private Font finalFrontierOld;
    private Font emuLogic;
    private Font pocketPixel;

    private FontFactory() {
        try {
            finalFrontierNew = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/final_new.ttf"));
            finalFrontierOld = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/final_old.ttf"));
            emuLogic = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/emulogic.ttf"));
            pocketPixel = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/pocket_pixel.ttf"));
        } catch (FontFormatException ex) {
            Client.LOG.log(Level.SEVERE, "Unable to load font: {0}", ex.getMessage());
        } catch (IOException ex) {
            Client.LOG.log(Level.SEVERE, "Unable to load font file: {0}", ex.getMessage());
        }
    }

    public static FontFactory getInstance() {
        if (instance == null) {
            instance = new FontFactory();
        }
        return instance;
    }

    /**
     * Gets a derived Final Frontier New font
     *
     * @param fontType Font.PLAIN, Font.BOLD, etc..
     * @param size     size of derived font
     * @return the derived font object
     */
    public Font getFinalFrontierNew(int fontType, float size) {
        return finalFrontierNew.deriveFont(fontType, size);
    }

    /**
     * Gets a derived Final Frontier Old font
     *
     * @param fontType Font.PLAIN, Font.BOLD, etc..
     * @param size     size of derived font
     * @return the derived font object
     */
    public Font getFinalFrontierOld(int fontType, float size) {
        return finalFrontierOld.deriveFont(fontType, size);
    }

    /**
     * Gets a derived EmuLogic font
     *
     * @param fontType Font.PLAIN, Font.BOLD, etc..
     * @param size     size of derived font
     * @return the derived font object
     */
    public Font getEmuLogic(int fontType, float size) {
        return emuLogic.deriveFont(fontType, size);
    }

    public Font getPocketPixel(int fontType, float size) {
        return pocketPixel.deriveFont(fontType, size);
    }


    public Font getHeading1() {
        return finalFrontierOld.deriveFont(Font.BOLD, 30);
    }

    public Font getHeading3() {
        return finalFrontierNew.deriveFont(Font.PLAIN, 15);
    }

    public Font getTacticalBoxFont() {
        return getEmuLogic(Font.PLAIN, 8);
    }

    public Font getFleetViewSpeedRangeFont() {
        return getEmuLogic(Font.PLAIN, 10);
    }

    public Font getLauncherHeading() {
        return getEmuLogic(Font.BOLD, 30);
    }

    public Font getSystemKPIMainFont() {
        return getPocketPixel(Font.BOLD, 14);
    }
}