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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Dialog that gets the server address/ip + username/password
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class LoginDialog extends JDialog {

    private boolean abort = false;
    private JTextField user;
    private JPasswordField pass;
    private JTextField server;
    private JTextField port;
    private JButton connectButton;

    public LoginDialog(boolean modal) {
        super(Client.getInstance(), modal);
        setTitle(Client.getLanguage().get("login"));
        initComponents();
        setLocationRelativeTo(Client.getInstance());
        pack();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new GridLayout(0, 1));

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                user.setText("");
                pass.setText("");
                server.setText("");
                port.setText("");
                abort = true;
            }
        });

        server = new JTextField("127.0.0.1", 30);
        JPanel serverPanel = new JPanel();
        serverPanel.setLayout(new FlowLayout());
        serverPanel.add(new JLabel(Client.getLanguage().get("server")));
        serverPanel.add(server);

        port = new JTextField("8472", 30);
        JPanel portPanel = new JPanel();
        portPanel.setLayout(new FlowLayout());
        portPanel.add(new JLabel(Client.getLanguage().get("port")));
        portPanel.add(port);

        user = new JTextField("klogd", 30);
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new FlowLayout());
        userPanel.add(new JLabel(Client.getLanguage().get("username")));
        userPanel.add(user);

        pass = new JPasswordField("klogd", 30);
        JPanel passPanel = new JPanel();
        passPanel.setLayout(new FlowLayout());
        passPanel.add(new JLabel(Client.getLanguage().get("password")));
        passPanel.add(pass);


        connectButton = new JButton(Client.getLanguage().get("connect"));
        connectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });


        add(serverPanel);
        add(portPanel);
        add(userPanel);
        add(passPanel);
        add(connectButton);
    }

    private void okActionPerformed(ActionEvent evt) {
        try {
            int i = Integer.parseInt(port.getText());
            if (i < 0 || i > 65535) {
                throw new NumberFormatException("");
            }
        } catch (NumberFormatException nfe) {
            this.setVisible(false);
            Client.getInstance().showError(Client.getLanguage().get("invalid_port_number"), null, false, false);
            return;
        }
        this.setVisible(false);
    }

    public boolean isAborted() {
        return abort;
    }

    public String getServer() {
        return server.getText();
    }

    public int getPort() {
        try {
            return Integer.parseInt(port.getText());
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public String getUsername() {
        return user.getText();
    }

    public String getPassword() {
        return new String(pass.getPassword());
    }
}