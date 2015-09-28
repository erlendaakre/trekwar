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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.gui.SimplePieChart;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.client.ImageManager;

/**
 * Uninhabited system info for bottom menu
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class UninhabitedPanel extends JPanel implements Runnable {
    private Image backgroundImage;
    private int backgroundX = 0;
    private long frameCount = 0;
    private Graphics buffer;
    private Image offscreen;
    Thread animThread;
    private boolean doAnim;
    private SensorPanel sensorPanel;
    
    private JLabel nameLabel, sectorLabel, systemStatsLabel;
    SimplePieChart popChart;

    public UninhabitedPanel() {
        setBounds(5, 5, 770, 165);
        setLayout(null);

        backgroundImage = ImageManager.getInstance().getImage("graphics/empty_background.jpg").getImage();

        animThread = new Thread(this);
        doAnim = true;
        animThread.start();

        nameLabel = new JLabel(Client.getLanguage().get("uninhabited_system"));
        nameLabel.setFont(FontFactory.getInstance().getHeading1());
        nameLabel.setBounds(15, 10, 300, 50);
        add(nameLabel);

        sectorLabel = new JLabel(Client.getLanguage().get("sector"));
        sectorLabel.setFont(FontFactory.getInstance().getHeading3());
        sectorLabel.setBounds(25, 35, 200, 50);
        add(sectorLabel);
        
        popChart = new SimplePieChart(80, 80, 30000);
        popChart.setBounds(15,80, 100, 100);
        add(popChart);
        
        systemStatsLabel = new JLabel();
        systemStatsLabel.setVerticalAlignment(SwingConstants.TOP);
        systemStatsLabel.setBounds(110, 73, 220, 90);
        add(systemStatsLabel);
        
        sensorPanel = new SensorPanel();
        sensorPanel.setLocation(340, 20);
        add(sensorPanel);
        
        JLabel placeHolder = new JLabel("<html>Uninhabited system view will be extended<br> to show invididual planets in Trekwar 0.5.0</html>");
        placeHolder.setHorizontalAlignment(SwingConstants.CENTER);
        placeHolder.setOpaque(true);
        placeHolder.setBackground(Color.DARK_GRAY);
        placeHolder.setBounds(475, 30, 290, 120);
        
        add(placeHolder);
    }

    public void setSystem(StarSystem s) {
        sectorLabel.setText(Client.getLanguage().get("sector") + ": " + s.getX() + ":" + s.getY());
        nameLabel.setText(s.getName());
        popChart.setToolTipText(s.getName());
        sensorPanel.setSystem(s);
        BottomUIComponentFactory.addPopulationSlices(popChart, s);
        double maxPopDouble = ((double) s.getMaxPopulation()) / 1000;
        systemStatsLabel.setText("<html>" + Client.getLanguage().get("planets") + ": " + s.getPlanets().size() + "<br>"
                + Client.getLanguage().get("structures") + ": 0 / " + s.getMaxStructures() + "<br>"
                + Client.getLanguage().get("population") + ": 0 / " + StaticData.DECIMAL_FORMAT_2D.format(maxPopDouble) + " " + Client.getLanguage().get("billion") + "<br>"
                + Client.getLanguage().get("deuterium") + ": " + s.getDeuteriumPerTurn() + " " + Client.getLanguage().get("per_turn") + "<br>"
                +  Client.getLanguage().get("fertility") + ": " + StaticData.DECIMAL_FORMAT_2D.format(s.getAvgFertility()) + "%"
                + "</html>");
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