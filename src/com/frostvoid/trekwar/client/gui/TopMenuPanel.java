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
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.client.ImageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The top menu of the game (always visible), has option button, current turn
 * and turn progress bar
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TopMenuPanel extends JPanel {

    private JLabel optionLabel;
    private JProgressBar turnProgressBar;
    private JLabel currentTurnLabel;
    private JLabel upkeepLabel;

    public TopMenuPanel() {
        setOpaque(false);
        setBounds(0, 0, 660, 30);
        setLayout(null);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // blocks click on empty panel to anything below it in desktop pane
            }
        });

        optionLabel = new JLabel();
        optionLabel.setBounds(13, 2, 21, 21);
        add(optionLabel);

        currentTurnLabel = new JLabel("Trekwar " + Client.VERSION);
        currentTurnLabel.setHorizontalAlignment(JLabel.CENTER);
        currentTurnLabel.setBounds(48, 3, 104, 19);
        add(currentTurnLabel);

        turnProgressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        turnProgressBar.setValue(0);
        turnProgressBar.setStringPainted(true);
        turnProgressBar.setBounds(163, 2, 340, 23);
        add(turnProgressBar);

        upkeepLabel = new JLabel("0 / 0 ", ImageManager.getInstance().getImage("graphics/misc_icons/upkeep.png"), SwingConstants.LEFT);
        upkeepLabel.setBounds(508, 4, 100, 20);
        upkeepLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        add(upkeepLabel);

        optionLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), e.getComponent());
                JPopupMenu popup = new JPopupMenu();

                JMenuItem connectItem = new JMenuItem(Client.getLanguage().get("connect_to_server"));
                JMenuItem quitItem = new JMenuItem(Client.getLanguage().get("quit"));

                popup.add(connectItem);
                popup.addSeparator();
                popup.add(quitItem);

                connectItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Client.getInstance().connect();
                    }
                });

                quitItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Client.getInstance().confirmExit();
                    }
                });

                popup.show(optionLabel, pt.x, pt.y);
            }
        });
    }

    public void setCurrentTurn(long turn) {
        currentTurnLabel.setText(Client.getLanguage().get("turn") + " " + turn);
    }

    public void setTurnProgress(String text, int value, int max) {
        turnProgressBar.setString(text);
        turnProgressBar.setMaximum(max);
        turnProgressBar.setValue(value);

        if (value + 8 > max) {
            turnProgressBar.setBackground(Color.ORANGE);
            if (value + 4 > max) {
                turnProgressBar.setBackground(Color.RED);
            }
        } else {
            turnProgressBar.setBackground(Color.GRAY);
        }
    }

    public void setUpkeep(int produced, int used) {
        upkeepLabel.setText("" + used + " / " + produced);
        if (used > produced) {
            upkeepLabel.setForeground(Color.red);
        } else {
            upkeepLabel.setForeground(Color.white);
        }
    }

    public void setTurnProgressIndeterminate(boolean state) {
        turnProgressBar.setIndeterminate(state);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(ImageManager.getInstance().getImage("graphics/menu.png").getImage(), 0, 0, null);
    }
}