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
package com.frostvoid.trekwar.client.gui.BottomMenu;

import java.awt.Graphics;
import java.awt.Image;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.StaticData;

/**
 * Icon for Fleets
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BottomMenuFleetIcon extends BottomMenuToolbarIcon {
    
    String shipImageFilename;
    
    public BottomMenuFleetIcon(Fleet f, boolean ally) {
        super(f.getName(), f.getUser().getFaction(), ally);
        String ext = "";
        if(f.getUser().getFaction().equals(StaticData.federation)) {
            ext = "fed";
        }
        if(f.getUser().getFaction().equals(StaticData.klingon)) {
            ext = "kli";
        }
        if(f.getUser().getFaction().equals(StaticData.romulan)) {
            ext = "rom";
        }
        if(f.getUser().getFaction().equals(StaticData.cardassian)) {
            ext = "car";
        }
        if(f.getUser().getFaction().equals(StaticData.dominion)) {
            ext = "dom";
        }
        shipImageFilename = ext + ".png";
    }

    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Image img = ImageManager.getInstance().getImage("graphics/map_icons/" + shipImageFilename).getImage();
        g.drawImage(img, 20, 15, this);
    }
}