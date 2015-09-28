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
import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.StarSystem;

/**
 * Icon for starsystems
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BottomMenuStarsystemIcon extends BottomMenuToolbarIcon  {
    
    private StarSystem system;
    
    public BottomMenuStarsystemIcon(StarSystem s, Faction f, boolean ally) {
        super(s.getName(), f, ally);
        system = s;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Image img = ImageManager.getInstance().getImage("graphics/map_icons/" + system.getImageFile()).getImage();        
        g.drawImage(img, 5, 0, this);
        if(system.hasShipyard()) {
            Image img2 = ImageManager.getInstance().getImage("graphics/map_icons/shipyard.png").getImage();        
            g.drawImage(img2, 15, 0, this);
        }
    }
}