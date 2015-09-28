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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.frostvoid.trekwar.client.gui.SimpleBar;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.StaticData;

/**
 * Graphical representation in the user interface when a tile
 * with a Nebula is selected
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class NebulaPanel extends JPanel implements Runnable {

    private Image backgroundImage;
    private Image foregroundImage; // based on http://dadrian.deviantart.com/art/Nebula-stock-187500026
    private int backgroundX = 0;
    private int backgroundY = 0;
    private int foregroundX = -950;
    private int foregroundY = 0;
    private long frameCount = 0;
    private Graphics buffer;
    private Image offscreen;
    Thread animThread;
    private boolean doAnim;
    
    private JLabel textLabel, sectorLabel, resourcesLabel;
    private SimpleBar resourcesBar;
    private SensorPanel sensorPanel;

    public NebulaPanel() {
        setBounds(5, 5, 770, 165);
        setLayout(null);

        backgroundImage = ImageManager.getInstance().getImage("graphics/nebula_background.jpg").getImage();
        foregroundImage = ImageManager.getInstance().getImage("graphics/nebula_foreground.png").getImage();

        animThread = new Thread(this);
        doAnim = true;
        animThread.start();
        
        textLabel = new JLabel(Client.getLanguage().get("nebula"));
        textLabel.setFont(FontFactory.getInstance().getHeading1());
        textLabel.setBounds(15, 10, 300, 50);
        add(textLabel);
        
        sectorLabel = new JLabel(Client.getLanguage().get("sector"));
        sectorLabel.setFont(FontFactory.getInstance().getHeading3());
        sectorLabel.setBounds(25, 35, 200, 50);
        add(sectorLabel);
        
        resourcesLabel = new JLabel(Client.getLanguage().get("resources_left"));
        resourcesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resourcesLabel.setBounds(0, 120, 770, 20);
        add(resourcesLabel);
        
        sensorPanel = new SensorPanel();
        sensorPanel.setLocation(340, 20);
        add(sensorPanel);
        
        resourcesBar = new SimpleBar(10, 200, 75, Colors.RESOURCES_VIEW_COLOR_FOREGROUND, Colors.RESOURCES_VIEW_COLOR_BACKGROUND, SimpleBar.Alignment.HORIZONTAL);
        resourcesBar.setBounds((getWidth()/2)-100, 145, 200, 20);
        add(resourcesBar);
    }

    public void setSystem(StarSystem s) {
        sectorLabel.setText(Client.getLanguage().get("sector") + ": " + s.getX() + ":" + s.getY());
        resourcesBar.setPercentage((int) ((100d/StaticData.MAX_NEBULA_RESOURCES)*s.getResourcesLeft()));
        resourcesBar.setToolTipText(Client.getLanguage().get("resources_left") + ": " + s.getResourcesLeft());
        sensorPanel.setSystem(s);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
            }
            if (doAnim) {
                frameCount++;
                if (frameCount % 2 == 0) {
                    foregroundX += 1;
                }

                if (frameCount % 1 == 0) {
                    backgroundX--;
                }
                if (backgroundX < -backgroundImage.getWidth(this)) {
                    backgroundX = 0;
                }


                if (foregroundX >= foregroundImage.getWidth(this)) {
                    foregroundX = -950;
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

        buffer.drawImage(backgroundImage, backgroundX, backgroundY, this);
        buffer.drawImage(backgroundImage, backgroundX + backgroundImage.getWidth(this), backgroundY, this);
        buffer.drawImage(foregroundImage, foregroundX, foregroundY, this);
        buffer.drawImage(foregroundImage, foregroundX - foregroundImage.getWidth(this), foregroundY, this);

        g.drawImage(offscreen, 0, 0, this);
        
        paintComponents(g);
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public void animStart() {
        doAnim = true;
        foregroundX = -950;
    }

    public void animPause() {
        doAnim = false;
    }
}