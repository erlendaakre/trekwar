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
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.Planet;
import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.utils.Language;

/**
 * Shows detailed stats and info about a single planet
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class PlanetInfoWindow extends JInternalFrame {

    private static final long serialVersionUID = -3860515896964469360L;
    private Planet planet;

    public PlanetInfoWindow(int x, int y, Planet planet) {
        super(Client.getLanguage().get("planetinfo_planet_info") + ": " + planet.getStarSystem().getName() + " " + planet.getPlanetNumber(),
                false, //resizable
                true, //closable
                false, //maximizable
                false);//iconifiable

        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setFrameIcon(ImageManager.getInstance().getImage("graphics/misc_icons/info.png"));
        this.planet = planet;

        setBackground(Colors.TREKWAR_BG_COLOR);

        setLocation(x, y);
        makeGUI();
        pack();
    }

    private void makeGUI() {
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
        dtm.addColumn(lang.get("planetinfo_stat"));
        dtm.addColumn(lang.get("planetinfo_value"));
        infoTable.setModel(dtm);

        Vector<String> data = new Vector<String>(2);
        data.add(lang.get("planetinfo_type"));
        data.add("" + planet.getType());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("planetinfo_max_population"));
        data.add("" + planet.getMaximumPopulation());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("planetinfo_population"));
        data.add("" + planet.getPopulation());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("planetinfo_fertility"));
        data.add("" + Client.getNumberFormat().format(planet.getFertility()));
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("planetinfo_max_structures"));
        data.add("" + planet.getMaximumStructures());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("planetinfo_structures"));
        data.add("" + planet.getStructuresMap().size());
        dtm.addRow(data);

        data = new Vector<String>(2);
        data.add(lang.get("planetinfo_deuterium_per_turn"));
        data.add("" + planet.getDeuteriumPerTurn());
        dtm.addRow(data);

        getContentPane().setSize(new Dimension(350, 160));
        getContentPane().setPreferredSize(new Dimension(350, 160));
        getContentPane().setMinimumSize(new Dimension(350, 160));
        setLayout(new GridLayout(1, 1));
        getContentPane().add(infoTableSP);
    }
}