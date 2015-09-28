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

import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.StaticData;

import javax.swing.*;
import java.awt.*;

/**
 * Base class for icons used in the toolbar
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BottomMenuToolbarIcon extends JButton {

    Image buttonImage;

    public BottomMenuToolbarIcon(String text, Faction faction, boolean ally) {
        String ext = "";
        if (faction.equals(StaticData.federation)) {
            ext = "_fed";
        }
        if (faction.equals(StaticData.klingon)) {
            ext = "_kli";
        }
        if (faction.equals(StaticData.romulan)) {
            ext = "_rom";
        }
        if (faction.equals(StaticData.cardassian)) {
            ext = "_car";
        }
        if (faction.equals(StaticData.dominion)) {
            ext = "_dom";
        }
        if (ally) {
            ext = "_ally";
        }

        buttonImage = ImageManager.getInstance().getImage("graphics/bottommenutoolbar" + ext + ".png").getImage();
        setLayout(null);
        setSize(new Dimension(60, 60));

        if (text != null && text.length() > 0) {
            JLabel textLabel = new JLabel(text);
            textLabel.setBounds(2, 35, 58, 20);
            textLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(textLabel);
        }

        setModel(new DefaultButtonModel());
    }

    public void setButtonImage(Image img) {
        buttonImage = img;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.drawImage(buttonImage, 0, 0, this);

        paintComponents(g);
    }
}