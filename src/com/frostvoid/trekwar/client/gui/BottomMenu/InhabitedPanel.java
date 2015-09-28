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
import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.client.gui.SimpleBar;
import com.frostvoid.trekwar.client.gui.SimplePieChart;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.orders.BuildShipOrder;
import com.frostvoid.trekwar.common.orders.BuildStructureOrder;
import com.frostvoid.trekwar.common.orders.Order;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Inhabited system (own, enemy or ally) info for bottom menu
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class InhabitedPanel extends JPanel implements Runnable {

    private Image backgroundImage;
    private int backgroundX = 0;
    private long frameCount = 0;
    private Graphics buffer;
    private Image offscreen;
    private Thread animThread;
    private boolean doAnim;
    private JLabel usernameLabel, nameLabel, sectorLabel, systemStatsLabel, userIcon, factionIcon;
    private SimplePieChart popChart;
    private SimpleBar moraleBar, troopBar, troopBarBlack;
    private KPIPanel kpiPanel;
    private JScrollPane buildListSP; // for own systems only
    private JList buildQueueList; // for own systems only
    private DefaultListModel buildQueueModel; // for own systems only
    private JButton viewSystemButton; // for own systems only
    private EnemyTacticalPanel tacticalPanel; // for enemy systems only

    public InhabitedPanel() {
        setBounds(5, 5, 770, 165);
        setLayout(null);

        backgroundImage = ImageManager.getInstance().getImage("graphics/system_background.jpg").getImage();

        animThread = new Thread(this);
        doAnim = true;
        animThread.start();

        nameLabel = new JLabel("");
        nameLabel.setFont(FontFactory.getInstance().getHeading1());
        nameLabel.setBounds(80, 10, 300, 50);
        add(nameLabel);

        usernameLabel = new JLabel("");
        usernameLabel.setFont(FontFactory.getInstance().getHeading3());
        usernameLabel.setBounds(80, -5, 200, 30);
        add(usernameLabel);

        sectorLabel = new JLabel(Client.getLanguage().get("sector"));
        sectorLabel.setFont(FontFactory.getInstance().getHeading3());
        sectorLabel.setBounds(90, 35, 200, 50);
        add(sectorLabel);

        popChart = new SimplePieChart(80, 80, 20000);
        popChart.setBounds(15, 80, 100, 100);
        add(popChart);

        systemStatsLabel = new JLabel("");
        systemStatsLabel.setVerticalAlignment(SwingConstants.TOP);
        systemStatsLabel.setBounds(110, 73, 220, 90);
        add(systemStatsLabel);

        kpiPanel = new KPIPanel();
        kpiPanel.setLocation(340, 20);
        add(kpiPanel);

        userIcon = new JLabel();
        userIcon.setBounds(5, 5, 70, 70);
        add(userIcon);

        factionIcon = new JLabel();
        factionIcon.setBounds(55, 50, 39, 39);
        add(factionIcon);

        buildQueueModel = new DefaultListModel();
        buildQueueList = new JList(buildQueueModel);
        buildQueueList.setOpaque(false);
        buildQueueList.setCellRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel c = new JLabel();
                c.setText(value.toString());
                return c;
            }
        });

        buildListSP = new JScrollPane(buildQueueList);
        buildListSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        buildListSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        buildListSP.setBounds(540, 60, 225, 100);
        add(buildListSP);

        setComponentZOrder(factionIcon, getComponentZOrder(userIcon) - 1);
    }

    public void setSystem(final StarSystem s) {
        sectorLabel.setText(Client.getLanguage().get("sector") + ": " + s.getX() + ":" + s.getY());
        usernameLabel.setText(s.getUser().getUsername());
        nameLabel.setText(s.getName());
        popChart.setToolTipText(s.getName());
        BottomUIComponentFactory.addPopulationSlices(popChart, s);
        double maxPopDouble = ((double) s.getMaxPopulation()) / 1000;
        // base stats (text)
        String structuresFullText = "";
        if (s.countStructures() == s.getMaxStructures()) {
            structuresFullText = " (" + Client.getLanguage().getUC("full") + ")";
        }
        systemStatsLabel.setText("<html>" + Client.getLanguage().getU("planets") + ": " + s.getPlanets().size() + "<br>"
                + Client.getLanguage().getU("structures") + ": " + s.countStructures() + " / " + s.getMaxStructures() + structuresFullText + "<br>"
                + Client.getLanguage().getU("population") + ": " + StaticData.DECIMAL_FORMAT_2D.format(((double) s.getPopulation()) / 1000) + " / " + StaticData.DECIMAL_FORMAT_2D.format(maxPopDouble) + " " + Client.getLanguage().get("billion") + "<br>"
                + Client.getLanguage().getU("deuterium") + ": " + s.getDeuteriumPerTurn() + " " + Client.getLanguage().get("per_turn") + "<br>"
                + Client.getLanguage().getU("fertility") + ": " + StaticData.DECIMAL_FORMAT_2D.format(s.getAvgFertility()) + "%"
                + "</html>");

        // user + faction icon
        userIcon.setIcon(new ImageIcon(ImageManager.getInstance().getImage("graphics/avatars/" + s.getUser().getAvatarFilename()).
                getImage().getScaledInstance(70, 70, 0)));
        userIcon.setToolTipText(s.getUser().getUsername());
        factionIcon.setIcon(BottomUIComponentFactory.getFactionIcon(s.getUser().getFaction()));

        // KPI
        kpiPanel.setSystem(s);

        // morale + troop count
        if (moraleBar != null && troopBar != null && troopBarBlack != null) {
            remove(moraleBar);
            remove(troopBar);
            remove(troopBarBlack);
        }

        moraleBar = new SimpleBar(150, 10, s.getMorale(), Colors.BARCOLOR_FUEL, Colors.BARCOLOR_BACKGROUND, SimpleBar.Alignment.VERTICAL);
        moraleBar.setToolTipText(Client.getLanguage().getU("starsystem_morale") + ": " + s.getMorale());

        double troopPercentage = (100D / 150D) * s.getTroopCount();
        troopBar = new SimpleBar(150, 10, (int) troopPercentage, Colors.BARCOLOR_HITPOINTS, Colors.BARCOLOR_BACKGROUND, SimpleBar.Alignment.VERTICAL);
        troopBar.setToolTipText(Client.getLanguage().getU("troops") + " : " + s.getTroopCount() + " / " + s.getTroopCapacity() + " (" + s.getTroopProduction() + ")");

        Color veryDarkGray = new Color(30, 30, 30);
        troopBarBlack = new SimpleBar(150 - s.getTroopCapacity(), 10, 100, veryDarkGray, veryDarkGray, SimpleBar.Alignment.VERTICAL);

        moraleBar.setBounds(512, 10, 10, 150);
        troopBar.setBounds(525, 10, 10, 150);
        troopBarBlack.setBounds(525, 10, 10, 150);

        add(moraleBar);
        add(troopBar);
        add(troopBarBlack);

        setComponentZOrder(troopBarBlack, getComponentZOrder(troopBar) - 1);

        if (viewSystemButton != null) {
            remove(viewSystemButton);
        }
        if (buildListSP != null) {
            remove(buildListSP);
        }
        if (tacticalPanel != null) {
            remove(tacticalPanel);
        }

        if (s.getUser() != null && s.getUser().equals(Client.getInstance().getLocalUser())) {
            viewSystemButton = new JButton(Client.getLanguage().getCC("manage_system"));
            viewSystemButton.setBackground(Color.GREEN);
            viewSystemButton.setBounds(540, 10, 225, 45);
            viewSystemButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Client.getInstance().openSystemControlView(s);
                }
            });
            add(viewSystemButton);

            add(buildListSP);
            generateBuildList(s);
        } else {
            if (tacticalPanel == null) {
                tacticalPanel = new EnemyTacticalPanel();
                tacticalPanel.setLocation(540, 10);
            }
            tacticalPanel.setSystem(s);
            add(tacticalPanel);
        }
    }

    public void generateBuildList(StarSystem system) {
        buildQueueModel.removeAllElements();
        int buildNum = 1;

        for (Order o : system.getBuildQueue()) {
            if (o instanceof BuildStructureOrder) {
                ((BuildStructureOrder) o).setPositionInBuildQueue(buildNum++);
                buildQueueModel.addElement((BuildStructureOrder) o);
            } else if (o instanceof BuildShipOrder) {
                ((BuildShipOrder) o).setPositionInBuildQueue(buildNum++);
                buildQueueModel.addElement((BuildShipOrder) o);
            }
        }
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