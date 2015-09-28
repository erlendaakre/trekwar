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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.ShipTemplate;

/**
 * Dialog for making a new Ship template, asks for name and shows
 * available ship hulls
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class NewTemplateDialog extends JDialog {

    private boolean abort = false;
    private JLabel label;
    private JButton okButton;
    private JComboBox templateBox;
    private JTextField nameInputField;

    public NewTemplateDialog(boolean modal) {
        super(Client.getInstance(), modal);
        setTitle(Client.getLanguage().get("shipdesign_new_template"));
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
                nameInputField.setText("");
                templateBox.setSelectedIndex(-1);
                abort = true;
            }
        });

        label = new JLabel(Client.getLanguage().get("shipdesign_select_a_hull_and_enter_a_template_name"));
        JPanel labelPanel = new JPanel();
        labelPanel.add(label);

        nameInputField = new JTextField();

        String[] hullNames = new String[Client.getInstance().getLocalUser().getAvailableShipHulls().size()];
        for (int i = 0; i < Client.getInstance().getLocalUser().getAvailableShipHulls().size(); i++) {
            hullNames[i] = Client.getInstance().getLocalUser().getAvailableShipHulls().get(i).getName();
        }
        templateBox = new JComboBox(hullNames);

        okButton = new JButton(Client.getLanguage().get("ok"));
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });


        labelPanel.setBounds(0, 0, 300, 25);
        templateBox.setBounds(40, 30, 200, 25);
        nameInputField.setBounds(40, 60, 200, 25);
        okButton.setBounds(200, 90, 80, 25);
        add(labelPanel);
        add(templateBox);
        add(nameInputField);
        add(okButton);
    }

    private void okActionPerformed(ActionEvent evt) {
        String templateName = nameInputField.getText();
        if (templateName.length() < 3) {
            JOptionPane.showMessageDialog(rootPane, Client.getLanguage().get("shipdesign_the_name_of_the_template_must_be_at_least_3_characters"),
                    Client.getLanguage().get("shipdesign_error_name_short"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (ShipTemplate s : Client.getInstance().getLocalUser().getShipTemplates()) {
            if (s.getName().equalsIgnoreCase(templateName)) {
                JOptionPane.showMessageDialog(rootPane, Client.getLanguage().get("shipdesign_you_allready_have_a_template_named") + ":\n" + templateName,
                        Client.getLanguage().get("shipdesign_error_name_unique"), JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        this.setVisible(false);
    }

    public String getInputText() {
        if (abort) {
            return "";
        }
        return templateBox.getSelectedItem().toString() + ":" + nameInputField.getText();
    }
}