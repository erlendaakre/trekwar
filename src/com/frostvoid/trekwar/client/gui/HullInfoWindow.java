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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.TechnologyGenerator;
import com.frostvoid.trekwar.common.utils.Language;
import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.shipHulls.HullClass;

/**
 * Shows stats and info about a specific base Hull
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class HullInfoWindow extends JInternalFrame {
    private static final long serialVersionUID = -3860515896964469360L;
    private HullClass hull;

    public HullInfoWindow(String name, Icon icon, int x, int y, HullClass hull) {
        super(name + ": " + hull.getName(),
              false, //resizable
              true, //closable
              false, //maximizable
              false);//iconifiable

        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        this.hull = hull;

        setBackground(Colors.TREKWAR_BG_COLOR);
        
        setFrameIcon(new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(-1, 18, 0)));

        int random = (int) ((Math.random()*30) + 5);
        setLocation(x+random, y+random);
        
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
        topPanel.setBounds(0,0,400,100);
        
        JLabel hullImage = new JLabel();
        hullImage.setIcon(ImageManager.getInstance().getImage("graphics/ship_icons/120x91/" + hull.getShipdesignerImageFileName()));
        hullImage.setBounds(3, 3, 120, 91);
        
        JLabel nameLabel = new JLabel("<html><h3>" + hull.getName() + "</h3></html>");
        nameLabel.setBounds(130, 2, 275, 25);
        
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setBounds(130, 35, 265, 60);
        descriptionArea.setText(hull.getDescription());

        topPanel.add(hullImage);
        topPanel.add(nameLabel);
        topPanel.add(descriptionArea);
        
        
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new GridLayout(1,1));
        dataPanel.setBounds(0,100,400,300);
        
        
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
        data.add(lang.get("hullinfo_slots"));
        data.add("" + hull.getSlots());
        dtm.addRow(data);
        
        data = new Vector<String>(2);
        data.add(lang.get("hullinfo_cost"));
        data.add("" + hull.getBaseCost());
        dtm.addRow(data);
        
        data = new Vector<String>(2);
        data.add(lang.get("hullinfo_maintenance"));
        data.add("" + hull.getBaseMaintenanceCost());
        dtm.addRow(data);
        
        data = new Vector<String>(2);
        data.add(lang.get("hullinfo_hp"));
        data.add("" + hull.getBaseHitpoints());
        dtm.addRow(data);
        
        data = new Vector<String>(2);
        data.add(lang.get("hullinfo_armor"));
        data.add("" + hull.getBaseArmor());
        dtm.addRow(data);
        
        data = new Vector<String>(2);
        data.add(lang.get("hullinfo_manoeuvrability"));
        data.add("" + hull.getBaseManoeuvrability());
        dtm.addRow(data);
        
        data = new Vector<String>(2);
        data.add(lang.get("hullinfo_deuterium_usage"));
        data.add("" + hull.getBaseDeuteriumUseage());
        dtm.addRow(data);
        
        data = new Vector<String>(2);
        data.add(lang.get("hullinfo_deuterium_storage"));
        data.add("" + hull.getBaseDeuteriumStorage());
        dtm.addRow(data);
        
        data = new Vector<String>(2);
        data.add(lang.get("hullinfo_crew"));
        data.add("" + hull.getMaxCrew());
        dtm.addRow(data);

        for (Technology t : hull.getTechsRequired()) {
            data = new Vector<String>(2);
            data.add(lang.get("hullinfo_requires_tech"));
            data.add("" + TechnologyGenerator.getTechTypeName(t.getType()) + " " + lang.get("hullinfo_level") + " " + t.getLevel());
            dtm.addRow(data);
        }
        
        for (Faction f : hull.getFactions()) {
            data = new Vector<String>(2);
            data.add(lang.get("hullinfo_usable_by"));
            data.add("" + f.getName());
            dtm.addRow(data);
        }
        
        dataPanel.add(infoTableSP);
        
        contentPanel.add(topPanel);
        contentPanel.add(dataPanel);
        
        setLayout(new GridLayout(1,1));
        getContentPane().add(contentPanel);
    }
}