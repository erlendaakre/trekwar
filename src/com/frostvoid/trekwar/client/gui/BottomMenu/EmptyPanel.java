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
 * Empty space system info for bottom menu
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class EmptyPanel extends JPanel implements Runnable {

    private Image backgroundImage;
    private int backgroundX = 0;
    private long frameCount = 0;
    private Graphics buffer;
    private Image offscreen;
    Thread animThread;
    private boolean doAnim;
    private JLabel textLabel, sectorLabel;
    private SensorPanel sensorPanel;

    public EmptyPanel() {
        setBounds(5, 5, 770, 165);
        setLayout(null);

        backgroundImage = ImageManager.getInstance().getImage("graphics/empty_background.jpg").getImage();

        animThread = new Thread(this);
        doAnim = true;
        animThread.start();

        textLabel = new JLabel(Client.getLanguage().get("empty_space"));
        textLabel.setFont(FontFactory.getInstance().getHeading1());
        textLabel.setBounds(15, 10, 300, 50);
        add(textLabel);

        sectorLabel = new JLabel(Client.getLanguage().get("sector"));
        sectorLabel.setFont(FontFactory.getInstance().getHeading3());
        sectorLabel.setBounds(25, 35, 200, 50);
        add(sectorLabel);

        sensorPanel = new SensorPanel();
        sensorPanel.setLocation(340, 20);
        add(sensorPanel);
    }

    public void setSystem(StarSystem s) {
        sectorLabel.setText(Client.getLanguage().get("sector") + ": " + s.getX() + ":" + s.getY());
        sensorPanel.setSystem(s);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
            }
            if (doAnim) {
                frameCount++;
                if (frameCount % 2 == 0) {
                    backgroundX -= 1;
                }


                if (backgroundX <= -2200) {
                    backgroundX = 0;
                }

                repaint();
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        if (offscreen == null) {
            offscreen = createImage(this.getSize().width, this.getSize().height);
            buffer = offscreen.getGraphics();
        }
        buffer.clearRect(0, 0, 780, 175);

        buffer.drawImage(backgroundImage, backgroundX, 0, this);

        g.drawImage(offscreen, 0, 0, this);

        paintComponents(g);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public void animStart() {
        doAnim = true;
        backgroundX = 0;
    }

    public void animPause() {
        doAnim = false;
    }
}