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

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.Ship;
import com.frostvoid.trekwar.common.ShipTemplate;
import com.frostvoid.trekwar.common.exceptions.SlotException;
import com.frostvoid.trekwar.common.utils.Language;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

/**
 * Shows detailed stats + info about a single ship
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ShipInfoWindow extends JInternalFrame {
    private static final long serialVersionUID = -3860515896964469360L;
    private Ship ship;

    public ShipInfoWindow(String name, Icon icon, int x, int y, Ship ship) {
        super(name + ": " + ship.getName(),
                false, //resizable
                true, //closable
                false, //maximizable
                false);//iconifiable

        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        this.ship = ship;

        setBackground(Colors.TREKWAR_BG_COLOR);

        setFrameIcon(new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(-1, 18, 0)));

        int random = (int) ((Math.random() * 30) + 5);
        setLocation(x + random, y + random);

        makeGUI();
        pack();
    }

    public ShipInfoWindow(String name, Icon icon, int x, int y, ShipTemplate template) {
        super(name + ": " + template.getName(),
                false, //resizable
                true, //closable
                false, //maximizable
                false);//iconifiable

        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        ship = new Ship(Client.getInstance().getLocalUser(), null, template.getName(), -1, template.getHullClass());
        try {
            ship.applyTemplate(template);
            ship.initShip();
        } catch (SlotException ex) {
        }

        setBackground(Colors.TREKWAR_BG_COLOR);

        setFrameIcon(new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(-1, 18, 0)));

        int random = (int) ((Math.random() * 30) + 5);
        setLocation(x + random, y + random);

        makeGUI();
        pack();
    }

    private void makeGUI() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setSize(new Dimension(400, 400));
        contentPanel.setPreferredSize(new Dimension(400, 400));
        contentPanel.setMinimumSize(new Dimension(400, 400));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(null);
        topPanel.setBounds(0, 0, 400, 100);

        JLabel hullImage = new JLabel();
        hullImage.setIcon(ImageManager.getInstance().getImage("graphics/ship_icons/120x91/" + ship.getHullClass().getShipdesignerImageFileName()));
        hullImage.setBounds(3, 3, 120, 91);

        JLabel nameLabel = new JLabel("<html><h3>" + ship.getName() + "</h3></html>");
        nameLabel.setBounds(130, 2, 275, 25);

        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setBounds(130, 35, 265, 60);
        descriptionArea.setText(ship.getHullClass().getDescription());

        topPanel.add(hullImage);
        topPanel.add(nameLabel);
        topPanel.add(descriptionArea);


        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new GridLayout(1, 1));
        dataPanel.setBounds(0, 100, 400, 300);


        JTable infoTable = new JTable();
        infoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        infoTable.setColumnSelectionAllowed(false);
        infoTable.setDragEnabled(false);
        infoTable.setShowHorizontalLines(true);
        infoTable.setShowVerticalLines(false);
        JScrollPane infoTableSP = new JScrollPane(infoTable);
        infoTableSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        DefaultTableModel dtm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Language lang = Client.getLanguage();
        dtm.addColumn(lang.get("hullinfo_stat"));
        dtm.addColumn(lang.get("hullinfo_value"));
        infoTable.setModel(dtm);

        Vector<String> data = new Vector<String>(2);
        data.add(lang.get("shipinfo_name"));
        data.add("" + ship.getName());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("fleet"));
        if (ship.getFleet() != null) {
            data.add(ship.getFleet().getName());
        } else {
            data.add("NULL");
        }
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_owner"));
        data.add("" + ship.getUser().getUsername());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_faction"));
        data.add("" + ship.getUser().getFaction().getName());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_cost"));
        data.add("" + ship.getCost());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_upkeep"));
        data.add("" + ship.getUpkeepCost());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_crew"));
        data.add("" + ship.getCrew() + " / " + ship.getHullClass().getMaxCrew());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_hp"));
        data.add("" + ship.getCurrentHullStrength() + " / " + ship.getMaxHitpoints());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_shields"));
        data.add("" + ship.getCurrentShieldStrength() + " / " + ship.getMaxShield());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_armor"));
        data.add("" + ship.getCurrentArmorStrength() + " / " + ship.getMaxArmor());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_deuterium"));
        data.add("" + ship.getCurrentDeuterium() + " / " + ship.getMaxDeuterium());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_deuterium_usage"));
        data.add("" + ship.getDeuteriumUsage());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_cargo"));
        data.add("" + (ship.getCargoDeuterium() + ship.getCargoOre()) + " / " + ship.getMaxCargoSpace());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_cargo_ore"));
        data.add("" + ship.getCargoOre());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_cargo_deuterium"));
        data.add("" + ship.getCargoDeuterium());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_troops"));
        data.add("" + ship.getTroops() + " / " + ship.getTroopCapacity());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_xp"));
        data.add("" + ship.getXp());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_morale"));
        data.add("" + ship.getMorale());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_speed"));
        data.add("" + ship.getSpeed());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_manouverability"));
        data.add("" + ship.getManeuverability());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_sensorstrength"));
        data.add("" + ship.getSensorStrength());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_signature"));
        data.add("" + ship.getSignatureStrength());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("shipinfo_id"));
        data.add("" + ship.getShipId());
        dtm.addRow(data);


        dataPanel.add(infoTableSP);

        contentPanel.add(topPanel);
        contentPanel.add(dataPanel);

        setLayout(new GridLayout(1, 1));
        getContentPane().add(contentPanel);
    }
}