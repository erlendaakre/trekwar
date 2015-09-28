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
package com.frostvoid.trekwar.client.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.border.AbstractBorder;
import com.frostvoid.trekwar.client.ImageManager;

/**
 * Used to draw the map background image
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MapBorder extends AbstractBorder {

    private ImageIcon borderImage = ImageManager.getInstance().getImage("graphics/bg.jpg");
    private Insets borderInsets;

    public MapBorder(Insets borderInsets) {
        this.borderInsets = borderInsets;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        int offsetX = 0;
        int offsetY = 0;
        while(offsetY < height) {
        while(offsetX < width) {
            g.drawImage(borderImage.getImage(),offsetX,offsetY, null);
            offsetX += borderImage.getIconWidth();
        }
        offsetY += borderImage.getIconHeight();
        offsetX = 0;
        }
    }
    

    @Override
    public Insets getBorderInsets(Component c) {
        return borderInsets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}