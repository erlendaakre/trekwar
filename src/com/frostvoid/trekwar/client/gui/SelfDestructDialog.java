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
import com.frostvoid.trekwar.common.Ship;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.orders.BuildStructureOrder;
import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.common.orders.BuildShipOrder;
import com.frostvoid.trekwar.common.utils.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog for confirming self destruction / decommissioning of a single ship.
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SelfDestructDialog extends JDialog {
    private boolean doDestroy = false;
    private boolean isInPlayerOwnedSystem = false;
    private Ship ship;
    private StarSystem system;

    /**
     * Sets up and displays the dialog
     *
     * @param modal set to true if this is a modal dialog
     * @param ship the ship to self destruct / decommission
     */
    public SelfDestructDialog(boolean modal, Ship ship) {
        super(Client.getInstance(), modal);
        this.ship = ship;
        system = Client.getInstance().getLocalMap()[ship.getFleet().getX()][ship.getFleet().getY()];
        if(system.getUser().equals(ship.getFleet().getUser())) {
            setTitle(Client.getLanguage().get("confirm_decommission"));
            isInPlayerOwnedSystem = true;
        }
        else {
            setTitle(Client.getLanguage().get("confirm_self_destruct"));
        }
        initComponents();
        setSize(300, 200);
        setLocationRelativeTo(Client.getInstance());
        setVisible(true);
    }

    /**
     * Sets up all the components
     */
    private void initComponents() {
        setLayout(null);

        JLabel heading = new JLabel(Client.getLanguage().get("confirm_self_destruct") + "?");
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        heading.setHorizontalAlignment(JLabel.CENTER);
        heading.setBounds(0,0,300, 25);

        String text = Language.pop(Client.getLanguage().get("confirm_self_destruct_text"), ship.getUpkeepCost());
        if(isInPlayerOwnedSystem) {
            int industryBonus = 0;
            if(Client.getInstance().getLocalGalaxy().getSystem(ship.getFleet()).getBuildQueue().size() > 0) {
                if (Client.getInstance().getLocalGalaxy().getSystem(ship.getFleet()).getBuildQueue().get(0) instanceof BuildShipOrder) {
                    industryBonus = ship.getCost() / StaticData.SHIP_DECOMMISSION_FACTOR_INDUSTRY_TO_SHIP;
                }
                if (Client.getInstance().getLocalGalaxy().getSystem(ship.getFleet()).getBuildQueue().get(0) instanceof BuildStructureOrder) {
                    industryBonus = ship.getCost() / StaticData.SHIP_DECOMMISSION_FACTOR_INDUSTRY_TO_STRUCTURE;
                }
            }
            text = Language.pop(Client.getLanguage().get("confirm_decommission_text"), ship.getUpkeepCost(), industryBonus, system.getName());
        }
        JLabel textLabel = new JLabel("<html>" + text + "</html>");
        textLabel.setBounds(5, 25, 290, 85);

        JButton destroyButton = new JButton(Client.getLanguage().get(isInPlayerOwnedSystem ? "decommission" : "self_destruct"));
        destroyButton.setBounds(25, 120, 100, 25);
        destroyButton.setBackground(Colors.SELFDESTRUCT_DIALOG_RED);
        destroyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                destroyAction();
            }
        });

        JButton cancelButton = new JButton(Client.getLanguage().get("cancel"));
        cancelButton.setBounds(175, 120, 100, 25);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelAction();
            }
        });

        add(heading);
        add(textLabel);
        add(destroyButton);
        add(cancelButton);
    }

    private void destroyAction() {
        doDestroy = true;
        this.setVisible(false);
    }

    private void cancelAction() {
        doDestroy = false;
        this.setVisible(false);
    }

    /**
     * Returns true if the user clicked destruct, false if cancel
     *
     * @return true if user wants to destroy the ship
     */
    public boolean doSelfDestruct() {
        return doDestroy;
    }
}