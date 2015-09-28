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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.frostvoid.trekwar.common.exceptions.ServerCommunicationException;
import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.Fleet;

/**
 * Bottom UI panel for displaying Fleets
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class FleetPanel extends JPanel implements Runnable {
    private Image backgroundImage;
    private int backgroundX = 0;
    private long frameCount = 0;
    private Graphics buffer;
    private Image offscreen;
    Thread animThread;
    private boolean doAnim;
    
    private JTextField fleetNameLabel;
    private JLabel userIcon, factionIcon, speedLabel, rangeLabel, orderLabel;
    private FleetOrderPanel orderPanel;
    private FleetKPIPanel fleetKPIPanel;
    private FleetShipsPanel fleetShipsPanel;

    public FleetPanel() {
        setBounds(5, 5, 770, 165);
        setLayout(null);

        backgroundImage = ImageManager.getInstance().getImage("graphics/empty_background.jpg").getImage();

        animThread = new Thread(this);
        doAnim = true;
        animThread.start();
        
        // Fleet Name
        fleetNameLabel = new JTextField();
        fleetNameLabel.setOpaque(false);
        fleetNameLabel.setBorder(BorderFactory.createEmptyBorder());
        fleetNameLabel.setMargin(new Insets(0,0,0,0));
        fleetNameLabel.setFont(FontFactory.getInstance().getHeading1());
        fleetNameLabel.setBounds(80, 5, 225, 30);
        fleetNameLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = fleetNameLabel.getText();
                if (name != null) {
                    name = name.trim();
                    if (name.length() > 20 || name.length() < 2) {
                        Client.getInstance().showMessage(Client.getLanguage().get("name_must_be_between_2_and_20_characters"));
                    } else if (!Client.getInstance().getLocalUser().isFleetNameAvailable(name)) {
                        Client.getInstance().showMessage(Client.getLanguage().get("there_is_already_a_fleet_with_that_name"));
                    } else {
                        try {
                            Fleet fleet = (Fleet) fleetNameLabel.getClientProperty("fleet");
                            Client.getInstance().getComm().server_renameFleet(fleet.getName(), name);
                            fleet.setName(name);
                            Client.getInstance().getBottomGuiPanel().displaySystem(Client.getInstance().getLocalMap()[Client.getInstance().getMapPanel().getLastClickedTile().getXloc()]
                                                                                                                      [Client.getInstance().getMapPanel().getLastClickedTile().getYloc()]);
                            Client.getInstance().getBottomGuiPanel().showFleet(fleet);
                        }
                        catch(ServerCommunicationException sce) {
                            Client.getInstance().showMessage(Client.getLanguage().getU("unable_to_rename_fleet") + ":\n" + sce.getMessage());
                        }
                    }
                }
            }
        });
        add(fleetNameLabel);
        
        speedLabel = new JLabel("");
        speedLabel.setBounds(80, 35, 100, 20);
        speedLabel.setFont(FontFactory.getInstance().getFleetViewSpeedRangeFont());
        add(speedLabel);
        
        rangeLabel = new JLabel("");
        rangeLabel.setBounds(185, 35, 120, 20);
        rangeLabel.setFont(FontFactory.getInstance().getFleetViewSpeedRangeFont());
        add(rangeLabel);
        
        orderLabel = new JLabel("");
        orderLabel.setBounds(80, 55, 225, 20);
        orderLabel.setFont(FontFactory.getInstance().getHeading3());
        add(orderLabel);
        
        userIcon = new JLabel();
        userIcon.setBounds(5, 5, 70, 70);
        add(userIcon);

        factionIcon = new JLabel();
        factionIcon.setBounds(55, 50, 39, 39);
        add(factionIcon);
        
        setComponentZOrder(factionIcon, getComponentZOrder(userIcon)-1);
        
        // ORDER PANEL
        orderPanel = new FleetOrderPanel();
        JScrollPane orderPanelPane = new JScrollPane(orderPanel);
        orderPanelPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        orderPanelPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        orderPanelPane.setBounds(680, 0, 90, 165);
        orderPanelPane.setBorder(BorderFactory.createEmptyBorder());
        add(orderPanelPane);
        
        // FLEET KPI PANEL
        fleetKPIPanel = new FleetKPIPanel();
        fleetKPIPanel.setBounds(0, 75, 305, 90);
        add(fleetKPIPanel);
        
        // FLEET SHIPS PANEL
        fleetShipsPanel = new FleetShipsPanel();
        JScrollPane shipsPane = new JScrollPane(fleetShipsPanel);
        shipsPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        shipsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        shipsPane.setBounds(305, 0, 375, 165);
        add(shipsPane);
         
    }
    
    public void showFleet(Fleet fleet) {
        orderPanel.empty();
        Owner owner = null;
        if(fleet.getUser().equals(Client.getInstance().getLocalUser())) {
            owner = Owner.SELF;
            orderPanel.setFleet(fleet);
        }
        else {
            if(fleet.getUser().getFaction().equals(Client.getInstance().getLocalUser().getFaction())) {
                owner = Owner.ALLY;
            }
            else {
                owner = Owner.ENEMY;
            }
        }
        
        fleetKPIPanel.setFleet(fleet);
        
        fleetShipsPanel.setFleet(fleet);
        
        // name
        fleetNameLabel.setText(fleet.getName());
        if(owner.equals(Owner.SELF)) {
            fleetNameLabel.setEditable(true);
            fleetNameLabel.putClientProperty("fleet", fleet);
        }
        else {
            fleetNameLabel.setEditable(false);
            fleetNameLabel.putClientProperty("fleet", null);
        }
        
        // speed + Range
        speedLabel.setText(Client.getLanguage().getU("speed") + ":" + fleet.getSpeedHumanreadable());
        if(!owner.equals(Owner.ENEMY)) {
            rangeLabel.setText(Client.getLanguage().getU("range") + ":" + fleet.getRange());
        }
        else {
            rangeLabel.setText("");
        }
        
        // current order
        if(owner.equals(Owner.SELF)) {
            if(fleet.getOrder() == null) {
                orderLabel.setText(Client.getLanguage().getU("awaiting_orders"));
            }
            else {
                orderLabel.setText(fleet.getOrder().toString());
            }
        }
        else {
            orderLabel.setText("");
        }
        
        // user + faction icon
        userIcon.setIcon(new ImageIcon(ImageManager.getInstance().getImage("graphics/avatars/" + fleet.getUser().getAvatarFilename()).
                getImage().getScaledInstance(70, 70, 0)));
        factionIcon.setIcon(BottomUIComponentFactory.getFactionIcon(fleet.getUser().getFaction()));
        
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
    
    private enum Owner {
        SELF,
        ALLY,
        ENEMY
    }
}