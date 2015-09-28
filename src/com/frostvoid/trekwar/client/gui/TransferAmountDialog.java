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

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dialog for selecting amount (troops/cargo) to transfer, using a slider
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TransferAmountDialog extends JDialog {

    private boolean abort = false;
    private JLabel label;
    private JButton okButton;
    private JButton allButton;
    private JSlider amountSlider;
    private int targetFreeCargoSpace;
    private String targetName;
    private String resourceType;

    public TransferAmountDialog(boolean modal, int targetFreeCargoSpace, String title, String resourceType, String target) {
        super(Client.getInstance(), modal);
        this.targetFreeCargoSpace = targetFreeCargoSpace;
        this.targetName = target;
        this.resourceType = resourceType;

        setTitle(title);
        initComponents();
        setSize(300, 150);
        setLocationRelativeTo(Client.getInstance());
        setVisible(true);
    }

    private void initComponents() {
        setLayout(null);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                amountSlider.setValue(0);
                abort = true;
            }
        });

        label = new JLabel(Client.getLanguage().get("transfer") + " 0 " + resourceType + " " + Client.getLanguage().get("to") + " " + targetName);
        JPanel labelPanel = new JPanel();
        labelPanel.add(label);

        amountSlider = new JSlider(SwingConstants.HORIZONTAL, 0, targetFreeCargoSpace, 0);
        amountSlider.setMajorTickSpacing(targetFreeCargoSpace / 5);
        amountSlider.setMinorTickSpacing(targetFreeCargoSpace / 10);
        amountSlider.setPaintTicks(true);
        amountSlider.setPaintLabels(true);
        amountSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                label.setText(Client.getLanguage().get("transfer") + " " + amountSlider.getValue() + " " + resourceType + " "
                        + Client.getLanguage().get("to") + " " + targetName);
            }
        });

        okButton = new JButton(Client.getLanguage().get("ok"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        allButton = new JButton(Client.getLanguage().get("all"));
        allButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amountSlider.setValue(amountSlider.getMaximum());
            }
        });


        labelPanel.setBounds(0, 0, 300, 25);
        amountSlider.setBounds(10, 30, 280, 40);
        okButton.setBounds(200, 90, 80, 25);
        allButton.setBounds(115, 90, 80, 25);
        add(labelPanel);
        add(amountSlider);
        add(okButton);
        add(allButton);
    }

    private void okActionPerformed(ActionEvent evt) {
        this.setVisible(false);
    }

    public String getInputText() {
        if (abort) {
            return "";
        }
        return "" + amountSlider.getValue();
    }
}