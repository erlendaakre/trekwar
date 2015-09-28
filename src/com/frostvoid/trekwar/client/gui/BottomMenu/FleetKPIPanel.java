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
import com.frostvoid.trekwar.client.gui.SimpleBar;
import com.frostvoid.trekwar.common.Fleet;

import javax.swing.*;
import java.awt.*;

/**
 * Shows the textarea with fleet info + bars with shield/hp/armor/fuel/etc..
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class FleetKPIPanel extends JPanel {

    private JTextArea fleetInfoArea;
    private JPanel barPanel;

    public FleetKPIPanel() {
        setLayout(null);

        fleetInfoArea = new JTextArea();
        fleetInfoArea.setOpaque(false);
        fleetInfoArea.setEditable(false);
        JScrollPane fleetInfoPane = new JScrollPane(fleetInfoArea);
        fleetInfoPane.setBounds(0, 0, 210, 90);
        add(fleetInfoPane);

        barPanel = new JPanel() {

            @Override
            public void paint(Graphics g) {
                paintComponents(g);
            }
        };
        barPanel.setBounds(215, 0, 80, 90);
        barPanel.setLayout(null);
        add(barPanel);
    }

    public void setFleet(Fleet fleet) {
        setFleetInfoText(fleet);
        makeFleetStatsBars(fleet);
    }

    @Override
    public void paint(Graphics g) {
        paintComponents(g);
    }

    private void setFleetInfoText(Fleet fleet) {
        int fleetStrength = (fleet.getDefence() / 2) + fleet.getWeapons();
        StringBuilder sb = new StringBuilder();
        sb.append(Client.getLanguage().getU("owner")).append(": ").append(fleet.getUser().getUsername()).append("\n");
        sb.append(Client.getLanguage().getU("number_of_ships")).append(": ").append(fleet.getShips().size()).append("\n");
        sb.append(Client.getLanguage().getU("fleet_strength")).append(": ").append(fleetStrength).append("\n");
        sb.append(Client.getLanguage().getU("attack")).append("/").append(Client.getLanguage().getU("defense")).append(": ").append(fleet.getWeapons()).append("/").append(fleet.getDefence()).append("\n");
        sb.append(Client.getLanguage().getU("troops")).append(": ").append(fleet.getTroops()).append(" (").append(fleet.getTroopCapacity()).append(")\n");
        sb.append(Client.getLanguage().getU("fuel")).append(": ").append(fleet.getDeuteriumLeft()).append(" (").append(fleet.getMaxDeuterium()).append(")\n");
        sb.append(Client.getLanguage().getU("fuel_usage")).append(": ").append(fleet.getDeuteriumUsage()).append("\n");
        sb.append(Client.getLanguage().getU("cargo")).append(": ").append(fleet.getCargoDeuterium() + fleet.getCargoOre()).append(" (").append(fleet.getMaxCargoSpace()).append(")\n");
        sb.append(Client.getLanguage().getU("cargo_free_space")).append(": ").append(fleet.getMaxCargoSpace() - (fleet.getCargoDeuterium() + fleet.getCargoOre())).append("\n");
        sb.append(Client.getLanguage().getU("cargo_ore")).append(": ").append(fleet.getCargoOre()).append("\n");
        sb.append(Client.getLanguage().getU("cargo_deuteurium")).append(": ").append(fleet.getCargoDeuterium());
        fleetInfoArea.setText(sb.toString());
        fleetInfoArea.setCaretPosition(0);
    }

    private void makeFleetStatsBars(Fleet f) {
        barPanel.removeAll();

        int barWidth = 80;
        int barHeight = 9;

        SimpleBar fleetShieldBar = new SimpleBar(barHeight, barWidth, (int) ((100D / f.getMaxShields()) * f.getShields()), Colors.BARCOLOR_SHIELD, Color.DARK_GRAY, SimpleBar.Alignment.HORIZONTAL);
        fleetShieldBar.setToolTipText(Client.getLanguage().get("shields") + ": " + f.getShields() + " / " + f.getMaxShields());
        fleetShieldBar.setBounds(0, 0, barWidth, barHeight);
        barPanel.add(fleetShieldBar);

        SimpleBar fleetArmorBar = new SimpleBar(barHeight, barWidth, (int) ((100D / f.getMaxArmor()) * f.getArmor()), Colors.BARCOLOR_ARMOR, Color.DARK_GRAY, SimpleBar.Alignment.HORIZONTAL);
        fleetArmorBar.setToolTipText(Client.getLanguage().get("armor") + ": " + f.getArmor() + " / " + f.getMaxArmor());
        fleetArmorBar.setBounds(0, 12, barWidth, barHeight);
        barPanel.add(fleetArmorBar);

        SimpleBar fleetHPBar = new SimpleBar(barHeight, barWidth, (int) ((100D / f.getMaxHP()) * f.getHP()), Colors.BARCOLOR_HITPOINTS, Color.DARK_GRAY, SimpleBar.Alignment.HORIZONTAL);
        fleetHPBar.setToolTipText(Client.getLanguage().get("hp") + ": " + f.getHP() + " / " + f.getMaxHP());
        fleetHPBar.setBounds(0, 24, barWidth, barHeight);
        barPanel.add(fleetHPBar);

        SimpleBar fleetFuelBar = new SimpleBar(barHeight, barWidth, (int) ((100D / f.getMaxDeuterium()) * f.getDeuteriumLeft()), Colors.BARCOLOR_FUEL, Color.DARK_GRAY, SimpleBar.Alignment.HORIZONTAL);
        fleetFuelBar.setToolTipText(Client.getLanguage().get("fuel") + ": " + f.getDeuteriumLeft() + " / " + f.getMaxDeuterium());
        fleetFuelBar.setBounds(0, 36, barWidth, barHeight);
        barPanel.add(fleetFuelBar);


        if (f.canLoadUnloadCargo()) {
            // if cargo space is full, no background for ore/deuterium bar
            int spaceLeft = f.getMaxCargoSpace() - (f.getCargoDeuterium() + f.getCargoOre());
            Color barBackground = Color.DARK_GRAY;
            if (spaceLeft < 1) {
                barBackground = new Color(0, 0, 0, 0); // black, 100% transparent
            }

            SimpleBar oreCargoBar = new SimpleBar(barHeight, barWidth, (int) ((100D / f.getMaxCargoSpace()) * f.getCargoOre()), Colors.BARCOLOR_ORE, barBackground, SimpleBar.Alignment.HORIZONTAL);
            oreCargoBar.setToolTipText(Client.getLanguage().get("ore") + ": " + f.getCargoOre() + " / " + f.getMaxCargoSpace());
            oreCargoBar.setBounds(0, 55, barWidth, barHeight);

            barPanel.add(oreCargoBar);

            SimpleBar deuteriumCargoBar = new SimpleBar(barHeight, barWidth, (int) ((100D / f.getMaxCargoSpace()) * f.getCargoDeuterium()), Colors.BARCOLOR_DEUTERIUM, barBackground, SimpleBar.Alignment.HORIZONTAL);
            deuteriumCargoBar.setToolTipText(Client.getLanguage().get("deuterium") + ": " + f.getCargoDeuterium() + " / " + f.getMaxCargoSpace());
            deuteriumCargoBar.setBounds(0, 67, barWidth, barHeight);
            barPanel.add(deuteriumCargoBar);
        }

        SimpleBar troopBar = new SimpleBar(barHeight, barWidth, (int) ((100D / f.getTroopCapacity()) * f.getTroops()), Colors.BARCOLOR_TROOPS, Color.DARK_GRAY, SimpleBar.Alignment.HORIZONTAL);
        troopBar.setToolTipText(Client.getLanguage().get("troops") + ": " + f.getTroops() + " / " + f.getTroopCapacity());
        troopBar.setBounds(0, 79, barWidth, barHeight);
        if (f.canTransportTroops()) {
            barPanel.add(troopBar);
        }
    }
}