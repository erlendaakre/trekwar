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
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.server.turnExec.GroundCombatResolver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Shows a dialog box allowing a user to see info and simulations of a ground
 * attack, then proceeding with the invasion or aborting.
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class InvasionConfirmationDialog extends JDialog {
    private boolean invade = false;

    private GroundCombatResolver gcr; // used for calculations

    private Fleet attacker;
    private StarSystem defender;

    /**
     * Sets up and displays the dialog
     *
     * @param modal    set to true if this is a modal dialog
     * @param attacker the attacking fleet
     * @param defender the defending starsystem
     */
    public InvasionConfirmationDialog(boolean modal, Fleet attacker, StarSystem defender) {
        super(Client.getInstance(), modal);
        this.attacker = attacker;
        this.defender = defender;
        setTitle(Client.getLanguage().get("invading") + " " + defender.getName());
        gcr = new GroundCombatResolver(attacker, defender);
        initComponents();
        setSize(300, 250);
        setLocationRelativeTo(Client.getInstance());
        setVisible(true);
    }

    /**
     * Sets up all the components
     */
    private void initComponents() {
        setLayout(null);

        JLabel heading = new JLabel(Client.getLanguage().get("invading") + " " + defender.getName());
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        heading.setHorizontalAlignment(JLabel.CENTER);
        heading.setBounds(0, 0, 300, 25);

        JLabel attackerLabel = new JLabel(Client.getLanguage().get("attacker"));
        attackerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        attackerLabel.setHorizontalAlignment(JLabel.CENTER);
        attackerLabel.setBounds(0, 25, 150, 20);

        JLabel defenderLabel = new JLabel(Client.getLanguage().get("defender"));
        defenderLabel.setFont(new Font("Arial", Font.BOLD, 14));
        defenderLabel.setHorizontalAlignment(JLabel.CENTER);
        defenderLabel.setBounds(150, 25, 150, 20);

        JTextArea attackerDetails = new JTextArea(attacker.getTroops() + " " + Client.getLanguage().get("troops") + "\n" +
                Client.getLanguage().get("tech_level") + ": " + gcr.getAttackerWeaponTechLevel() + "\n\n\n" +
                Client.getLanguage().get("strength") + ": " + gcr.getAttackerStrength());
        attackerDetails.setEditable(false);
        attackerDetails.setBackground(getBackground());
        attackerDetails.setBounds(25, 50, 100, 90);

        JTextArea defenderDetails = new JTextArea(defender.getTroopCount() + " " + Client.getLanguage().get("troops") + "\n" +
                Client.getLanguage().get("tech_level") + ": " + gcr.getDefenderWeaponTechLevel() + "\n" +
                Client.getLanguage().get("morale") + ": " + defender.getMorale() + "\n" +
                Client.getLanguage().get("bunkers") + ": " + defender.getNumberOfBunkers() + "\n" +
                Client.getLanguage().get("strength") + ": " + gcr.getDefenderStrength());
        defenderDetails.setBounds(175, 50, 100, 90);


        JButton invadeButton = new JButton(Client.getLanguage().get("invade"));
        invadeButton.setBounds(25, 185, 100, 25);
        invadeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                invadeAction();
            }
        });

        int numSims = 200;
        int simRes[] = gcr.simulate(numSims);

        JLabel attackerPercentage = new JLabel(simRes[2] / (numSims / 100) + " %");
        attackerPercentage.setFont(new Font("Arial", Font.BOLD, 16));
        attackerPercentage.setBounds(25, 140, 100, 25);
        attackerPercentage.setHorizontalAlignment(JLabel.CENTER);

        JLabel defenderPercentage = new JLabel((numSims - simRes[2]) / (numSims / 100) + " %");
        defenderPercentage.setFont(new Font("Arial", Font.BOLD, 16));
        defenderPercentage.setBounds(175, 140, 100, 25);
        defenderPercentage.setHorizontalAlignment(JLabel.CENTER);

        if (attacker.getTroops() * gcr.getAttackerStrength() > defender.getTroopCount() * gcr.getDefenderStrength()) {
            attackerPercentage.setForeground(Colors.INVASION_DIALOG_GREEN);
            defenderPercentage.setForeground(Colors.INVASION_DIALOG_RED);
        } else {
            attackerPercentage.setForeground(Colors.INVASION_DIALOG_RED);
            defenderPercentage.setForeground(Colors.INVASION_DIALOG_GREEN);
        }

        JLabel attackerCasualties = new JLabel(simRes[0] / numSims + " " + Client.getLanguage().get("casualties"));
        attackerCasualties.setFont(new Font("Arial", Font.BOLD, 12));
        attackerCasualties.setBounds(25, 162, 100, 20);
        attackerCasualties.setHorizontalAlignment(JLabel.CENTER);

        JLabel defenderCasualties = new JLabel(simRes[1] / numSims + " " + Client.getLanguage().get("casualties"));
        defenderCasualties.setFont(new Font("Arial", Font.BOLD, 12));
        defenderCasualties.setBounds(175, 162, 100, 20);
        defenderCasualties.setHorizontalAlignment(JLabel.CENTER);

        JButton cancelButton = new JButton(Client.getLanguage().get("cancel"));
        cancelButton.setBounds(175, 185, 100, 25);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelAction();
            }
        });

        add(heading);
        add(attackerLabel);
        add(defenderLabel);
        add(attackerDetails);
        add(defenderDetails);
        add(attackerPercentage);
        add(defenderPercentage);
        add(attackerCasualties);
        add(defenderCasualties);
        add(invadeButton);
        add(cancelButton);
    }

    private void invadeAction() {
        invade = true;
        this.setVisible(false);
    }

    private void cancelAction() {
        invade = false;
        this.setVisible(false);
    }

    /**
     * Returns true if the user clicked invade, false if cancel
     *
     * @return true if user wants to invade
     */
    public boolean doInvade() {
        return invade;
    }
}