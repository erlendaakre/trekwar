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
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.StarSystemClassification;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The toolbar panel over the Bottom Menu that holds the Buttons (empire, system, fleets...)
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BottomMenuToolbarPanel extends JPanel {

    private ArrayList<BottomMenuToolbarIcon> toolbarButtons;
    private ToolbarActionListener toolbarListener;

    public BottomMenuToolbarPanel() {
        toolbarButtons = new ArrayList<BottomMenuToolbarIcon>();
        toolbarListener = new ToolbarActionListener();
        setSize(60, 60);
        setOpaque(false);
        setLayout(null);
    }

    public void populateTabs(StarSystem system) {
        for (BottomMenuToolbarIcon button : toolbarButtons) {
            remove(button);
        }
        toolbarButtons.clear();

        populateTab_empire();
        populateTab_starsystem(system);
        populateTab_fleets(system);

        addTabsToComponent();
        repaint();
    }

    private void addTabsToComponent() {
        int x = 0;
        for (BottomMenuToolbarIcon button : toolbarButtons) {
            button.setBounds(x, 0, button.getWidth(), button.getHeight());
            add(button);

            x += 70;

            // TODO handle very large values of X (lots of icons)
        }
        setSize(new Dimension(x, 60));
    }

    private void populateTab_empire() {
        BottomMenuEmpireIcon empire = new BottomMenuEmpireIcon(Client.getInstance().getLocalUser().getAvatarFilename(), Client.getInstance().getLocalUser().getFaction());
        registerAction(empire, ToolbarActionListener.Actions.VIEW_EMPIRE);
        toolbarButtons.add(empire);
    }

    private void populateTab_starsystem(StarSystem system) {
        BottomMenuToolbarIcon systemButton = null;
        if (system.getStarSystemClassification().equals(StarSystemClassification.starSystem)) {
            boolean ally = false;
//            if(system != null && system.getUser() != null && system.getUser().getFaction() != null &&
//                    system.getUser().getFaction().equals(Client.getInstance().getLocalUser().getFaction())) {
//                ally = true;
//            }
            systemButton = new BottomMenuStarsystemIcon(system, system.getUser().getFaction(), ally);
        } else if (system.getStarSystemClassification().equals(StarSystemClassification.empty)) {
            systemButton = new BottomMenuEmptyIcon();
        } else if (system.getStarSystemClassification().equals(StarSystemClassification.nebula)) {
            systemButton = new BottomMenuNebulaIcon();
        } else if (system.getStarSystemClassification().equals(StarSystemClassification.asteroid)) {
            systemButton = new BottomMenuAsteroidIcon();
        }

        if (systemButton != null) {
            systemButton.putClientProperty("starsystem", system);
            registerAction(systemButton, ToolbarActionListener.Actions.VIEW_STARSYSTEM);
            toolbarButtons.add(systemButton);
        }
    }

    private void populateTab_fleets(StarSystem system) {
        // own fleets
        for (Fleet f : system.getFleets()) {
            if (f.getUser().equals(Client.getInstance().getLocalUser())) {
                BottomMenuToolbarIcon fleetButton = new BottomMenuFleetIcon(f, false);
                registerAction(fleetButton, ToolbarActionListener.Actions.VIEW_FLEET);
                fleetButton.putClientProperty("fleet", f);
                toolbarButtons.add(fleetButton);
            }
        }

        // enemy fleets
        for (Fleet f : system.getFleets()) {
            if (!f.getUser().equals(Client.getInstance().getLocalUser())
                    && !f.getUser().getFaction().equals(Client.getInstance().getLocalUser().getFaction())) {
                BottomMenuToolbarIcon fleetButton = new BottomMenuFleetIcon(f, false);
                registerAction(fleetButton, ToolbarActionListener.Actions.VIEW_FLEET);
                fleetButton.putClientProperty("fleet", f);
                toolbarButtons.add(fleetButton);
            }
        }

        // allied fleets
        for (Fleet f : system.getFleets()) {
            if (!f.getUser().equals(Client.getInstance().getLocalUser())
                    && f.getUser().getFaction().equals(Client.getInstance().getLocalUser().getFaction())) {
                BottomMenuToolbarIcon fleetButton = new BottomMenuFleetIcon(f, true);
                registerAction(fleetButton, ToolbarActionListener.Actions.VIEW_FLEET);
                fleetButton.putClientProperty("fleet", f);
                toolbarButtons.add(fleetButton);
            }
        }
    }

    private void registerAction(JButton component, ToolbarActionListener.Actions action) {
        component.setActionCommand(action.toString());
        component.addActionListener(toolbarListener);
    }
}