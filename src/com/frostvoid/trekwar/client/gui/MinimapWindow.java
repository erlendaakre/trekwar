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

import javax.swing.*;
import java.awt.event.*;

/**
 * A internal window for displaying a zoomable minimap
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MinimapWindow extends JInternalFrame {

    private static final long serialVersionUID = 3860125896963469360L;
    private MinimapComponent minimap;
    private long lastUpdatedTurn;

    public MinimapWindow(int x, int y) {
        super(Client.getLanguage().get("minimap_minimap") + " - 2x",
                false, //resizable
                true, //closable
                false, //maximizable
                false);//iconifiable

        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setFrameIcon(ImageManager.getInstance().getImage("graphics/misc_icons/map.png"));
        this.minimap = new MinimapComponent(100, 100, false);


        setBackground(Colors.TREKWAR_BG_COLOR);

        setLocation(x, y);
        makeGUI();
        pack();
    }

    private void makeGUI() {
        JButton normalButton = new JButton(Client.getLanguage().get("minimap_mode_faction"));
        JButton ownButton = new JButton(Client.getLanguage().get("minimap_mode_own"));

        JPanel modePanel = new JPanel();
        modePanel.add(normalButton);
        modePanel.add(ownButton);

        minimap.setData(Client.getInstance().getLocalMap());


        getContentPane().setSize(minimap.getPreferredSize());
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        getContentPane().add(modePanel);
        getContentPane().add(minimap);

        minimap.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    minimap.zoomIn();
                } else {
                    minimap.zoomOut();
                }
                minimap.repaint();
                getContentPane().setSize(minimap.getPreferredSize());
                setTitle(Client.getLanguage().get("minimap_minimap") + " - " + minimap.getZoom() + "x");
                pack();

            }
        });

        minimap.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() == -1) {
                    minimap.zoomIn();
                } else {
                    minimap.zoomOut();
                }
                minimap.repaint();
                getContentPane().setSize(minimap.getPreferredSize());
                setTitle(Client.getLanguage().get("minimap_minimap") + " - " + minimap.getZoom() + "x");
                pack();
            }
        });

        normalButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                minimap.setMode(MinimapComponent.MODE_FACTION);
                minimap.repaint();
            }
        });

        ownButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                minimap.setMode(MinimapComponent.MODE_OWN_SYSTEMS);
                minimap.repaint();
            }
        });
    }

    public void reloadData(long turn) {
        if (turn == lastUpdatedTurn) {
            return;
        }

        lastUpdatedTurn = turn;
        minimap.setData(Client.getInstance().getLocalMap());
        minimap.repaint();
    }
}