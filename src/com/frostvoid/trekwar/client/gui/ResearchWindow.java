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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.TechnologyGenerator;
import com.frostvoid.trekwar.common.User;
import com.frostvoid.trekwar.common.exceptions.ServerCommunicationException;
import com.frostvoid.trekwar.common.utils.Calculations;
import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.shipComponents.ShipComponent;
import com.frostvoid.trekwar.common.shipHulls.HullClass;
import com.frostvoid.trekwar.common.utils.Language;

/**
 * A window that allows user to select and manage research goals
 * 
 * Contains lots of OLD crappy code, should be rewritten some day!
 * 
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public final class ResearchWindow extends JInternalFrame {

    private static final long serialVersionUID = -1701997950990938138L;
    private JPanel techTreePanel;
    private JPanel componentPanel;
    private JTextArea techDescriptionArea;
    private JProgressBar techProgressBar;
    private JLabel techProgressLabel;
    private JLabel techNameLabel;
    private JLabel techLvlLabel;
    private JLabel techImageLabel;
    private JButton techStartButton;
    private Technology selectedTech;
    private JLabel currentlySelectedTechBlock;
    private static Color techActiveColor = Color.GREEN;
    private static Color techNextColor = new Color(0, 82, 194);
    private static Color techCompletedColor = new Color(109, 109, 109);
    private Language lang;

    public ResearchWindow(int x, int y) {
        super("",
                false, //resizable
                true, //closable
                false, //maximizable
                false);//iconifiable

        lang = Client.getLanguage();

        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);

        setBackground(Colors.TREKWAR_BG_COLOR);

        makeTechTree(Client.getInstance().getLocalUser());
        getContentPane().add(techTreePanel);
        setFrameIcon(new ImageIcon(ImageManager.getInstance().getImage("graphics/misc_icons/research.png").getImage().getScaledInstance(-1, 18, 0)));

        setSize(new Dimension(780, 500));
        setLocation(x, y);
    }

    public final void makeTechTree(final User user) {
        updateWindowTitle(user);
        currentlySelectedTechBlock = null;
        selectedTech = user.getCurrentResearch();

        if (techTreePanel == null) {
            // FIRST TIME
            techTreePanel = new JPanel();
            techTreePanel.setLayout(null);
            techTreePanel.setBackground(Colors.TREKWAR_BG_COLOR);
        } else {
            techTreePanel.removeAll();
        }

        int techY = 400;

        // BIOTECH
        JLabel biotechLabel = new JLabel(lang.get("researchwindow_biological"));
        biotechLabel.setBounds(7, 430, 60, 20);
        int bioTechX = 5;

        for (int i = 0; i <= user.getHighestTech(TechnologyGenerator.techType.biotech).getLevel(); i++) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + i);
            techBlock.setBounds(bioTechX, techY, 60, 30);
            techBlock.setBackground(techCompletedColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            techTreePanel.add(techBlock);
            techY -= 35;
            final int tmplvl = i;
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.biotech, tmplvl);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });
        }
        techTreePanel.add(biotechLabel);
        if (user.getHighestTech(TechnologyGenerator.techType.biotech).getLevel() < TechnologyGenerator.MAX_BIOTECH) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + (user.getHighestTech(TechnologyGenerator.techType.biotech).getLevel() + 1));
            techBlock.setBounds(bioTechX, techY, 60, 30);
            techBlock.setBackground(techNextColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            if (user.getCurrentResearch() != null
                    && user.getCurrentResearch().getType() == TechnologyGenerator.techType.biotech) {
                techBlock.setBackground(techActiveColor);
                techBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                currentlySelectedTechBlock = techBlock;
            }
            techTreePanel.add(techBlock);
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.biotech,
                            user.getHighestTech(TechnologyGenerator.techType.biotech).getLevel() + 1);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });
        }


        // COMPUTERTECH
        JLabel computerLabel = new JLabel(lang.get("researchwindow_computer"));
        computerLabel.setBounds(77, 430, 60, 20);
        int computerTechX = 75;
        techY = 400;

        for (int i = 0; i <= user.getHighestTech(TechnologyGenerator.techType.computertech).getLevel(); i++) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + i);
            techBlock.setBounds(computerTechX, techY, 60, 30);
            techBlock.setBackground(techCompletedColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            techTreePanel.add(techBlock);
            techY -= 35;
            final int tmplvl = i;
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.computertech, tmplvl);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });
        }
        techTreePanel.add(computerLabel);
        if (user.getHighestTech(TechnologyGenerator.techType.computertech).getLevel() < TechnologyGenerator.MAX_COMPUTERTECH) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + (user.getHighestTech(TechnologyGenerator.techType.computertech).getLevel() + 1));
            techBlock.setBounds(computerTechX, techY, 60, 30);
            techBlock.setBackground(techNextColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            if (user.getCurrentResearch() != null
                    && user.getCurrentResearch().getType() == TechnologyGenerator.techType.computertech) {
                techBlock.setBackground(techActiveColor);
                techBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                currentlySelectedTechBlock = techBlock;
            }
            techTreePanel.add(techBlock);
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.computertech,
                            user.getHighestTech(TechnologyGenerator.techType.computertech).getLevel() + 1);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });
        }


        // ENERGY
        JLabel energyLabel = new JLabel(lang.get("researchwindow_energy"));
        energyLabel.setBounds(153, 430, 60, 20);
        techY = 400;
        int energyTechX = 145;

        for (int i = 0; i <= user.getHighestTech(TechnologyGenerator.techType.energytech).getLevel(); i++) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + i);
            techBlock.setBounds(energyTechX, techY, 60, 30);
            techBlock.setBackground(techCompletedColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            techTreePanel.add(techBlock);
            techY -= 35;
            final int tmplvl = i;
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.energytech, tmplvl);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });
        }
        techTreePanel.add(energyLabel);
        if (user.getHighestTech(TechnologyGenerator.techType.energytech).getLevel() < TechnologyGenerator.MAX_ENERGYTECH) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + (user.getHighestTech(TechnologyGenerator.techType.energytech).getLevel() + 1));
            techBlock.setBounds(energyTechX, techY, 60, 30);
            techBlock.setBackground(techNextColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            if (user.getCurrentResearch() != null
                    && user.getCurrentResearch().getType() == TechnologyGenerator.techType.energytech) {
                techBlock.setBackground(techActiveColor);
                techBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                currentlySelectedTechBlock = techBlock;
            }
            techTreePanel.add(techBlock);
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.energytech,
                            user.getHighestTech(TechnologyGenerator.techType.energytech).getLevel() + 1);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));

                }
            });
        }


        // PROPULSION
        JLabel propLabel = new JLabel(lang.get("researchwindow_propulsion"));
        propLabel.setBounds(215, 430, 80, 20);
        techY = 400;
        int propTechX = 215;

        for (int i = 0; i <= user.getHighestTech(TechnologyGenerator.techType.propulsiontech).getLevel(); i++) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + i);
            techBlock.setBounds(propTechX, techY, 60, 30);
            techBlock.setBackground(techCompletedColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            techTreePanel.add(techBlock);
            techY -= 35;
            final int tmplvl = i;
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.propulsiontech, tmplvl);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });
        }
        techTreePanel.add(propLabel);
        if (user.getHighestTech(TechnologyGenerator.techType.propulsiontech).getLevel() < TechnologyGenerator.MAX_PROPULSIONTECH) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + (user.getHighestTech(TechnologyGenerator.techType.propulsiontech).getLevel() + 1));
            techBlock.setBounds(propTechX, techY, 60, 30);
            techBlock.setBackground(techNextColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            if (user.getCurrentResearch() != null
                    && user.getCurrentResearch().getType() == TechnologyGenerator.techType.propulsiontech) {
                techBlock.setBackground(techActiveColor);
                techBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                currentlySelectedTechBlock = techBlock;
            }
            techTreePanel.add(techBlock);
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.propulsiontech,
                            user.getHighestTech(TechnologyGenerator.techType.propulsiontech).getLevel() + 1);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });
        }


        // CONSTRUCTION
        JLabel constrLabel = new JLabel(lang.get("researchwindow_construction"));
        constrLabel.setBounds(280, 430, 80, 20);
        techY = 400;
        int constrTechX = 285;

        for (int i = 0; i <= user.getHighestTech(TechnologyGenerator.techType.constructiontech).getLevel(); i++) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + i);
            techBlock.setBounds(constrTechX, techY, 60, 30);
            techBlock.setBackground(techCompletedColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            techTreePanel.add(techBlock);
            techY -= 35;
            final int tmplvl = i;
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.constructiontech, tmplvl);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });

        }
        techTreePanel.add(constrLabel);
        if (user.getHighestTech(TechnologyGenerator.techType.constructiontech).getLevel() < TechnologyGenerator.MAX_CONSTRUCTIONTECH) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + (user.getHighestTech(TechnologyGenerator.techType.constructiontech).getLevel() + 1));
            techBlock.setBounds(constrTechX, techY, 60, 30);
            techBlock.setBackground(techNextColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            if (user.getCurrentResearch() != null
                    && user.getCurrentResearch().getType() == TechnologyGenerator.techType.constructiontech) {
                techBlock.setBackground(techActiveColor);
                techBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                currentlySelectedTechBlock = techBlock;
            }
            techTreePanel.add(techBlock);
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.constructiontech,
                            user.getHighestTech(TechnologyGenerator.techType.constructiontech).getLevel() + 1);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });
        }


        // WEAPONS
        JLabel wepLabel = new JLabel(lang.get("researchwindow_weapons"));
        wepLabel.setBounds(358, 430, 80, 20);
        techY = 400;
        int wepTechX = 355;

        for (int i = 0; i <= user.getHighestTech(TechnologyGenerator.techType.weaponstech).getLevel(); i++) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + i);
            techBlock.setBounds(wepTechX, techY, 60, 30);
            techBlock.setBackground(techCompletedColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            techTreePanel.add(techBlock);
            techY -= 35;
            final int tmplvl = i;
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.weaponstech, tmplvl);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));

                }
            });
        }
        techTreePanel.add(wepLabel);
        if (user.getHighestTech(TechnologyGenerator.techType.weaponstech).getLevel() < TechnologyGenerator.MAX_WEAPONTECH) {
            final JLabel techBlock = new JLabel(lang.get("researchwindow_lvl") + " " + (user.getHighestTech(TechnologyGenerator.techType.weaponstech).getLevel() + 1));
            techBlock.setBounds(wepTechX, techY, 60, 30);
            techBlock.setBackground(techNextColor);
            techBlock.setOpaque(true);
            techBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            techBlock.setVerticalAlignment(SwingConstants.CENTER);
            techBlock.setHorizontalAlignment(SwingConstants.CENTER);
            if (user.getCurrentResearch() != null
                    && user.getCurrentResearch().getType() == TechnologyGenerator.techType.weaponstech) {
                techBlock.setBackground(techActiveColor);
                techBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                currentlySelectedTechBlock = techBlock;
            }
            techTreePanel.add(techBlock);
            techBlock.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent me) {
                    selectedTech = TechnologyGenerator.getTech(TechnologyGenerator.techType.weaponstech,
                            user.getHighestTech(TechnologyGenerator.techType.weaponstech).getLevel() + 1);
                    selectTech(Client.getInstance().getLocalUser(), selectedTech);

                    if (currentlySelectedTechBlock != null) {
                        currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                    }
                    currentlySelectedTechBlock = techBlock;
                    currentlySelectedTechBlock.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            });
        }


        techProgressBar = new JProgressBar(JProgressBar.VERTICAL, 0, 3000);
        techProgressBar.setValue(900);
        techProgressBar.setBounds(450, 5, 25, 450);
        techProgressBar.setForeground(new Color(8, 135, 25));
        techProgressBar.setString(null);

        JLabel seperatorLabel = new JLabel();
        seperatorLabel.setBorder(BorderFactory.createMatteBorder(0, 2, 0, 0, Color.BLACK));
        seperatorLabel.setBounds(480, 5, 2, 480);



        techNameLabel = new JLabel(lang.get("researchwindow_no_reserach_set"));
        techNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        techNameLabel.setBounds(500, 5, 240, 30);

        techLvlLabel = new JLabel();
        techLvlLabel.setBounds(500, 35, 240, 20);

        techImageLabel = new JLabel();
        techImageLabel.setBounds(500, 55, 220, 220);

        techProgressLabel = new JLabel();
        techProgressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        techProgressLabel.setBounds(500, 276, 220, 20);

        techDescriptionArea = new JTextArea();
        techDescriptionArea.setEditable(false);
        techDescriptionArea.setWrapStyleWord(true);
        techDescriptionArea.setLineWrap(true);
        techDescriptionArea.setMargin(new Insets(2, 2, 2, 2));

        JScrollPane descSP = new JScrollPane(techDescriptionArea);
        descSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        descSP.setBounds(500, 295, 220, 115);

        componentPanel = new JPanel();
        componentPanel.setBackground(Colors.TREKWAR_BG_COLOR);
        componentPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 2));
        componentPanel.setBounds(500, 413, 220, 20);

        techStartButton = new JButton(lang.get("researchwindow_start_researching"));
        techStartButton.setEnabled(false);
        techStartButton.setVerticalAlignment(SwingConstants.CENTER);
        techStartButton.setBounds(500, 435, 220, 30);

        techStartButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedTech != null) {
                    if (user.getCurrentResearch() != null) {
                        int answer = JOptionPane.showConfirmDialog(Client.getInstance(), lang.get("researchwindow_start_researching") + 
                                " " + selectedTech.getName() + "?\n" + lang.get("researchwindow_current_research_will_be_lost"), 
                                lang.get("researchwindow_confirm_research_change"), JOptionPane.YES_NO_OPTION);
                        if (answer == JOptionPane.YES_OPTION) {
                            tellServerToStartNewResearch(selectedTech);
                        }
                    } else {
                        tellServerToStartNewResearch(selectedTech);
                    }
                }
            }
        });

        techTreePanel.add(descSP);
        techTreePanel.add(techNameLabel);
        techTreePanel.add(techLvlLabel);
        techTreePanel.add(techImageLabel);
        techTreePanel.add(techProgressBar);
        techTreePanel.add(techProgressLabel);
        techTreePanel.add(componentPanel);
        techTreePanel.add(techStartButton);
        techTreePanel.add(seperatorLabel);

        if (user.getCurrentResearch() != null) {
            selectTech(user, user.getCurrentResearch());
        }

        getContentPane().invalidate();
        getContentPane().repaint();
    }

    private void tellServerToStartNewResearch(Technology targetTech) {
        try {
            Client.getInstance().getComm().server_changeResearch(selectedTech);

            // client.getResearchLabel().setText(TechnologyGenerator.getTechTypeName(selectedTech.getType()) + " " + selectedTech.getLevel());
            Client.getInstance().getLocalUser().setCurrentResearch(selectedTech);
            Client.getInstance().getLocalUser().setResearchPoints(0);
            makeTechTree(Client.getInstance().getLocalUser());
            Client.getInstance().getBottomGuiPanel().setResearchProgress(Client.getInstance().getLocalUser());
        } catch (IOException ioe) {
            Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_set_research"), ioe, false, true);
        } catch (ServerCommunicationException sce) {
            Client.getInstance().showError(Client.getLanguage().get("server_did_not_update_research") + ":\n" + sce.getMessage(), null, false, false);
        }
    }

    private void selectTech(User user, Technology tech) {
        componentPanel.removeAll();
        componentPanel.validate();
        componentPanel.repaint();

        if (tech == null) {
            techImageLabel.setIcon(null);
            techNameLabel.setText("");
            techLvlLabel.setText("");
            techStartButton.setEnabled(false);
            techProgressBar.setValue(0);
            techProgressLabel.setText("");
            techDescriptionArea.setText(lang.get("researchwindow_no_research_goal_set"));
            return;
        }
        String filename = "";

        if (tech.getType() == TechnologyGenerator.techType.biotech) {
            filename = "biotech";
        }
        if (tech.getType() == TechnologyGenerator.techType.computertech) {
            filename = "computer";
        }
        if (tech.getType() == TechnologyGenerator.techType.energytech) {
            filename = "energy";
        }
        if (tech.getType() == TechnologyGenerator.techType.constructiontech) {
            filename = "construction";
        }
        if (tech.getType() == TechnologyGenerator.techType.propulsiontech) {
            filename = "propulsion";
        }
        if (tech.getType() == TechnologyGenerator.techType.weaponstech) {
            filename = "weapon";
        }
        techNameLabel.setText(tech.getName());
        techLvlLabel.setText(lang.get("researchwindow_level") + " " + tech.getLevel() + " " + TechnologyGenerator.getTechTypeName(tech.getType()));
        techImageLabel.setIcon(ImageManager.getInstance().getImage("graphics/techs/" + filename + tech.getLevel() + ".png"));
        techDescriptionArea.setText(tech.getDesscription());
        techDescriptionArea.setCaretPosition(1);

        if (user.getTechs().contains(tech)) {
            techStartButton.setEnabled(false);
            techProgressBar.setValue(techProgressBar.getMaximum());
            techProgressLabel.setText(lang.get("researchwindow_research_completed"));
        } else {
            
            ArrayList<ShipComponent> shipComponentsFromResearch = user.getShipComponentsFromResearchingTechnolog(tech);
            ArrayList<HullClass> hullsFromResearch = user.getHullsFromResearchingTechnolog(tech);
            
            for (ShipComponent s : shipComponentsFromResearch) {
                JLabel cl = new JLabel(ImageManager.getInstance().getImage("graphics/ship_components/" + s.getSmallIconFileName()));
                cl.setToolTipText(s.getName());
                cl.setOpaque(true);
                componentPanel.add(cl);
            }
            for (HullClass c : hullsFromResearch) {
                JLabel cl = new JLabel(ImageManager.getInstance().getImage("graphics/ship_icons/" + c.getIconFileName()));
                cl.setToolTipText(c.getName());
                cl.setOpaque(true);
                componentPanel.add(cl);
            }
            for (ShipComponent s : StaticData.getShipComponentsRequiringTechnology(tech)) {
                if(shipComponentsFromResearch.contains(s)) {
                    continue; // this component was already displayed in the for loop above
                }
                JLabel cl = new JLabel(ImageManager.getInstance().getImage("graphics/ship_components/" + s.getSmallIconFileName()));
                cl.setEnabled(false);
                cl.setToolTipText(s.getName());
                cl.setOpaque(true);
                componentPanel.add(cl);
            }
            for (HullClass c : user.getHullsFromResearchingTechnolog(tech)) {
                if(hullsFromResearch.contains(c)) {
                    continue; // this hull was already displayed in the for loop above
                }
                JLabel cl = new JLabel(ImageManager.getInstance().getImage("graphics/ship_icons/" + c.getIconFileName()));
                cl.setEnabled(false);
                cl.setToolTipText(c.getName());
                cl.setOpaque(true);
                componentPanel.add(cl);
            }
            

            if (user.getCurrentResearch() != null && user.getCurrentResearch().equals(tech)) {
                techStartButton.setEnabled(false);
                techProgressBar.setMaximum(tech.getResearchCost());
                techProgressBar.setValue(user.getResearchPoints());
                int turnsLeft = Calculations.turnsLeftToResearch(user, tech);

                techProgressLabel.setText(turnsLeft + " " + lang.get("researchwindow_turns_until_research_complete"));
            } else {
                int turnsLeft = Calculations.turnsLeft(tech.getResearchCost(), 0, user.getResearchOutput());

                techStartButton.setEnabled(true);
                techProgressBar.setValue(techProgressBar.getMinimum());
                techProgressLabel.setText(lang.get("researchwindow_turns_to_research") + ": " + turnsLeft);
            }
        }
    }

    @Override
    public void removeNotify() {
        setVisible(false);
    }

    private void updateWindowTitle(User user) {
        setTitle(Client.getLanguage().get("research") + " - " + (int) user.getResearchOutput() + " "
                + Client.getLanguage().get("per_turn") + " (" + Client.getInstance().getLocalUser().getResearchBonus()
                + " " + Client.getLanguage().get("from_bonus") + ")");
    }
}