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
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.client.gui.MapTile;
import com.frostvoid.trekwar.client.gui.MinimapComponent;
import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.utils.Calculations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The main class for the bottom menu in the user interface (always visible)
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BottomMenuPanel extends JPanel {

    private MinimapComponent minimap;
    private JProgressBar researchProgress;
    private JPanel contentPanel; // changes if viewing system / fleet / etc..
    private BottomMenuToolbarPanel toolbarPanel;
    private EmpirePanel empirePanel;
    private EmptyPanel starsystemEmptyPanel;
    private NebulaPanel starsystemNebulaPanel;
    private AsteroidPanel starsystemAsteroidPanel;
    private UninhabitedPanel starsystemUninhabitedPanel;
    private InhabitedPanel starsystemInhabitedPanel;
    private FleetPanel fleetPanel;

    public BottomMenuPanel(BottomMenuToolbarPanel toolbarPanel) {
        this.toolbarPanel = toolbarPanel;
        setOpaque(false);
        setSize(1028, 175);
        setLayout(null);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // blocks click on empty panel to anything below it in desktop pane
            }
        });

        initButtons();
        initResearchBar();

        contentPanel = new JPanel() {
            private Image img = ImageManager.getInstance().getImage("graphics/bottom_gui_generic.png").getImage();

            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(img, 0, 0, null);
            }
        };
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 0, 780, 175);
        add(contentPanel);
    }

    public BottomMenuToolbarPanel getToolBar() {
        return toolbarPanel;
    }

    public void displaySystem(StarSystem system) {
        toolbarPanel.populateTabs(system);

        if (system.getStarSystemClassification().equals(StarSystemClassification.empty)) {
            boolean fleetFound = false;
            for (Fleet f : system.getFleets()) {
                if (f.getUser().equals(Client.getInstance().getLocalUser())) {
                    fleetFound = true;
                    showFleet(f);
                    break;
                }
            }
            if (!fleetFound) {
                showStarsystem();
            }
        } else {
            showStarsystem();
        }

        updateMinimap();
        repaint();
    }

    public void updateBuildList() {
        if (starsystemInhabitedPanel != null) {
            starsystemInhabitedPanel.generateBuildList(getCurrentlySelectedSystemOnMap());
        }
    }

    public void updateMinimap() {
        if (minimap == null) {
            minimap = new MinimapComponent(165, 165, true);
            minimap.setMode(MinimapComponent.MODE_FACTION);
            minimap.setBounds(852, 4, 165, 165);
            add(minimap);
        }
        if (Client.getInstance().getLocalMap() != null) {
            minimap.autoZoom();
            minimap.setData(Client.getInstance().getLocalMap());
        }
    }

    public void updateInhabitedSystemView() {
        if (starsystemInhabitedPanel != null) {
            StarSystem s = getCurrentlySelectedSystemOnMap();
            if (s.getStarSystemClassification().equals(StarSystemClassification.starSystem)
                    && !s.getUser().equals(StaticData.nobodyUser)) {
                starsystemInhabitedPanel.setSystem(s);
            }

        }

    }

    public void showEmpireView() {
        if (empirePanel == null) {
            empirePanel = new EmpirePanel();
        } else {
            empirePanel.updateInfo();
        }
        contentPanel.removeAll();
        contentPanel.add(empirePanel);
        contentPanel.validate();
        contentPanel.repaint();
    }

    public void showFleet(Fleet fleet) {
        if (fleetPanel == null) {
            fleetPanel = new FleetPanel();
        }

        fleetPanel.showFleet(fleet);

        contentPanel.removeAll();
        contentPanel.add(fleetPanel);
        contentPanel.validate();
        contentPanel.repaint();
    }

    public void showStarsystem() {
        StarSystem s = getCurrentlySelectedSystemOnMap();

        pauseAllAnims();

        if (s.getStarSystemClassification().equals(StarSystemClassification.empty)) {
            showStarsystemEmpty();
        }
        if (s.getStarSystemClassification().equals(StarSystemClassification.starSystem)) {
            if (s.getUser().equals(StaticData.nobodyUser)) {
                showStarSystemUninhabited();
            } else {
                showStarSystemInhabited();
            }
        }
        if (s.getStarSystemClassification().equals(StarSystemClassification.nebula)) {
            showStarsystemNebula();
        }
        if (s.getStarSystemClassification().equals(StarSystemClassification.asteroid)) {
            showStarsystemAsteroid();
        }
    }

    private void showStarSystemInhabited() {
        if (starsystemInhabitedPanel == null) {
            starsystemInhabitedPanel = new InhabitedPanel();
        }
        starsystemInhabitedPanel.setSystem(getCurrentlySelectedSystemOnMap());
        contentPanel.removeAll();
        contentPanel.add(starsystemInhabitedPanel);
        contentPanel.validate();
        contentPanel.repaint();
        starsystemInhabitedPanel.animStart();
    }

    private void showStarSystemUninhabited() {
        if (starsystemUninhabitedPanel == null) {
            starsystemUninhabitedPanel = new UninhabitedPanel();
        }
        starsystemUninhabitedPanel.setSystem(getCurrentlySelectedSystemOnMap());
        contentPanel.removeAll();
        contentPanel.add(starsystemUninhabitedPanel);
        contentPanel.validate();
        contentPanel.repaint();
        starsystemUninhabitedPanel.animStart();
    }

    private void showStarsystemNebula() {
        if (starsystemNebulaPanel == null) {
            starsystemNebulaPanel = new NebulaPanel();
        }
        starsystemNebulaPanel.setSystem(getCurrentlySelectedSystemOnMap());
        contentPanel.removeAll();
        contentPanel.add(starsystemNebulaPanel);
        contentPanel.validate();
        contentPanel.repaint();
        starsystemNebulaPanel.animStart();
    }

    private void showStarsystemAsteroid() {
        if (starsystemAsteroidPanel == null) {
            starsystemAsteroidPanel = new AsteroidPanel();
        }
        starsystemAsteroidPanel.setSystem(getCurrentlySelectedSystemOnMap());
        contentPanel.removeAll();
        contentPanel.add(starsystemAsteroidPanel);
        contentPanel.validate();
        contentPanel.repaint();
        starsystemAsteroidPanel.animStart();
    }

    private void showStarsystemEmpty() {
        if (starsystemEmptyPanel == null) {
            starsystemEmptyPanel = new EmptyPanel();
        }
        starsystemEmptyPanel.setSystem(getCurrentlySelectedSystemOnMap());
        contentPanel.removeAll();
        contentPanel.add(starsystemEmptyPanel);
        contentPanel.validate();
        contentPanel.repaint();
        starsystemEmptyPanel.animStart();
    }

    private void pauseAllAnims() {
        if (starsystemNebulaPanel != null) {
            starsystemNebulaPanel.animPause();
        }

        if (starsystemAsteroidPanel != null) {
            starsystemAsteroidPanel.animPause();
        }

        if (starsystemEmptyPanel != null) {
            starsystemEmptyPanel.animPause();
        }

        if (starsystemUninhabitedPanel != null) {
            starsystemUninhabitedPanel.animPause();
        }

        if (starsystemInhabitedPanel != null) {
            starsystemInhabitedPanel.animPause();
        }
    }

    private void initButtons() {
        JLabel chatButton = new JLabel();
        JLabel mailButton = new JLabel();
        JLabel shipDesignerButton = new JLabel();
        JLabel reportButton = new JLabel();
        JLabel minimapButton = new JLabel();
        JLabel researchButton = new JLabel();

        chatButton.setToolTipText(Client.getLanguage().get("chat"));
        mailButton.setToolTipText(Client.getLanguage().get("mail"));
        shipDesignerButton.setToolTipText(Client.getLanguage().get("ship_designer"));
        reportButton.setToolTipText(Client.getLanguage().get("reports"));
        minimapButton.setToolTipText(Client.getLanguage().get("minimap"));
        researchButton.setToolTipText(Client.getLanguage().get("research"));

        minimapButton.setBounds(822, 143, 26, 25);
        add(minimapButton);

        mailButton.setBounds(822, 110, 26, 26);
        add(mailButton);

        chatButton.setBounds(822, 78, 26, 26);
        add(chatButton);

        reportButton.setBounds(822, 43, 26, 26);
        add(reportButton);

        shipDesignerButton.setBounds(822, 10, 26, 26);
        add(shipDesignerButton);

        researchButton.setBounds(788, 143, 26, 25);
        add(researchButton);

        researchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Client.getInstance().getComm().isLoggedIn() && Client.getInstance().getLocalUser() != null) {
                    Client.getInstance().openResearchView();
                }
            }
        });

        shipDesignerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Client.getInstance().getComm().isLoggedIn() && Client.getInstance().getLocalUser() != null) {
                    Client.getInstance().openShipDesignerView();
                }
            }
        });

        chatButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Client.getInstance().getComm().isLoggedIn() && Client.getInstance().getLocalUser() != null) {
                    Client.getInstance().openChatWindow();
                }
            }
        });

        reportButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                showEmpireView();
                empirePanel.selectTurnReportTab();
            }

            ;
        });


        minimapButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Client.getInstance().showMinimapWindow();
            }
        });

    }

    private void initResearchBar() {
        researchProgress = new JProgressBar(JProgressBar.VERTICAL, 0, 100);
        researchProgress.setMaximum(100);
        researchProgress.setValue(0);
        researchProgress.setBounds(788, 10, 26, 125);
        add(researchProgress);
    }

    public void updateToolbar() {
        getToolBar().populateTabs(getCurrentlySelectedSystemOnMap());
    }

    public void setResearchProgress(User localUser) {
        int turnsLeft = Calculations.turnsLeftToResearch(localUser, localUser.getCurrentResearch());

        researchProgress.setMaximum(localUser.getCurrentResearch().getResearchCost());
        researchProgress.setValue(localUser.getResearchPoints());
        researchProgress.setBackground(Color.black);
        researchProgress.setIndeterminate(false);
        researchProgress.setToolTipText(turnsLeft + " " + Client.getLanguage().get("turns_until_research_complete"));

        researchProgress.setStringPainted(true);
        researchProgress.setString(turnsLeft + " " + Client.getLanguage().get("turns_left"));
    }

    public void setNoResearch(boolean warning) {
        researchProgress.setMaximum(100);
        researchProgress.setValue(0);
        researchProgress.setToolTipText(Client.getLanguage().get("no_research_goal"));
        if (warning) {
            researchProgress.setBackground(Color.red);
            researchProgress.setIndeterminate(true);
        }
        researchProgress.setStringPainted(false);
        researchProgress.repaint();
    }

    public StarSystem getCurrentlySelectedSystemOnMap() {
        MapTile last = Client.getInstance().getMapPanel().getLastClickedTile();
        if (last != null) {
            return Client.getInstance().getLocalMap()[last.getXloc()][last.getYloc()];
        } else {
            return null;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(ImageManager.getInstance().getImage("graphics/bottom_gui_buttons_and_minimap.png").getImage(), 780, 0, null);
    }
}