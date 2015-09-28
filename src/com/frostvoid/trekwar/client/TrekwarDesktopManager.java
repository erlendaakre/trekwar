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

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;

/**
 * This desktopManager defines custom rules for the movement of internal frames
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TrekwarDesktopManager extends DefaultDesktopManager {

    private boolean preventMoveWindowsOutOfDesktop;

    @Override
    public void endResizingFrame(JComponent f) {
        // store size for some windows
        /*if (f instanceof MapPanel) {
        client.getUserProperties().setProperty("window." + f.getClass().getSimpleName() + ".width", "" + f.getWidth());
        client.getUserProperties().setProperty("window." + f.getClass().getSimpleName() + ".height", "" + f.getHeight());
        }*/
    }

    @Override
    public void dragFrame(JComponent f, int x, int y) {
        // Boxes that can't be moved
        //if (f instanceof PlanetDetailsBox) {      return;        }

        // no boxes can move out of the desktop area
        if (preventMoveWindowsOutOfDesktop) {
            if (x < 0
                    || y < 0
                    || (x + f.getSize().getWidth()) > Client.getInstance().getDesktop().getWidth()
                    || (y + f.getSize().getHeight() > Client.getInstance().getDesktop().getHeight())) {
                return;
            }
        }

        // store location for windows
        Client.getInstance().getUserProperties().setProperty("window." + f.getClass().getSimpleName() + ".x", "" + x);
        Client.getInstance().getUserProperties().setProperty("window." + f.getClass().getSimpleName() + ".y", "" + y);

        f.setLocation(x, y);
    }

    public void preventMoveWindowsOutOfDesktop(boolean b) {
        preventMoveWindowsOutOfDesktop = b;
    }
}