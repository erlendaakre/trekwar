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

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.StarSystem;

import javax.swing.*;
import java.awt.*;

/**
 * Displays a sensor icon + sensor value
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SensorPanel extends JPanel {

    private JLabel sensorLabel;

    public SensorPanel() {
        setOpaque(false);
        setSize(210, 140);
        setLayout(null);

        int width = 80;
        int x_offset = width + 10;
        int y = 0;
        int y_increment = 25;

        y = y_increment * 4;

        sensorLabel = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/remote.png"));
        sensorLabel.setHorizontalAlignment(SwingConstants.LEFT);
        sensorLabel.setBounds(x_offset, y, width, 25);
        sensorLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        sensorLabel.setForeground(Color.BLACK);
        add(sensorLabel);
    }

    public void setSystem(StarSystem s) {
        int num = Client.getInstance().getLocalUser().getSensorOverlay()[s.getX()][s.getY()];
        sensorLabel.setText("" + num);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        paintComponents(g);
    }
}