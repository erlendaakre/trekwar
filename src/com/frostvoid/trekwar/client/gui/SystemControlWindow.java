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
import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.exceptions.InvalidOrderException;
import com.frostvoid.trekwar.common.net.messaging.BuildQueueRequestType;
import com.frostvoid.trekwar.common.net.messaging.StructureStateChangeRequestType;
import com.frostvoid.trekwar.common.orders.BuildShipOrder;
import com.frostvoid.trekwar.common.orders.BuildStructureOrder;
import com.frostvoid.trekwar.common.orders.Order;
import com.frostvoid.trekwar.common.structures.Structure;
import com.frostvoid.trekwar.common.utils.Language;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Window that lets user control a single star system
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SystemControlWindow extends JInternalFrame {

    private StarSystem starsystem;
    private ShipTemplate currentlySelectedShipTemplate;
    private JPanel contentPanel;
    private TransferHandler structure_th;
    private ArrayList<JLabel> planetSlotLabels;
    private JPanel leftColumn;
    private JPanel planetPanel;
    private JScrollPane planetScrollPane;
    private JList buildQueueList;
    private JPanel middleColumn;
    private JTextArea structureDescriptionArea;
    private JLabel structureImageLabel;
    private JLabel structureNameHeadingLabel;
    private JLabel structureStatCostLabel, structureStatPowerLabel, structureStatIndustryLabel, structureStatResearchLabel,
            structureStatFoodLabel;
    private JLabel shipStatCostLabel, shipStatUpkeepLAbel, shipStatRangeLabel, shipStatHPLabel, shipStatArmorLabel, shipStatShieldLabel,
            shipStatWeaponLabel, shipStatSpeedLabel, shipStatSensorLabel, shipNameLabel;
    private JPanel rightColumn;
    private JPanel shipInfoPanel;
    private JLabel spacedockLabel;
    private JButton buildShipButton;
    private JButton shipInfoButton;
    private SwingWorker warningBlinkAnim;

    public SystemControlWindow(String name, Icon icon, int x, int y) {
        super(name,
                false, //resizable
                true, //closable
                false, //maximizable
                false);//iconifiable

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                if (warningBlinkAnim != null) {
                    warningBlinkAnim.cancel(false);
                }
            }
        });

        planetSlotLabels = new ArrayList<JLabel>();

        structure_th = new TransferHandler() {
            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection((String) c.getClientProperty("structurename"));
            }

            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                // Do cleanup here if any
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport ts) {
                if (ts.getComponent() instanceof JLabel) {
                    if (planetSlotLabels.contains((JLabel) ts.getComponent())) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport ts) {
                Transferable t = ts.getTransferable();
                JLabel target = (JLabel) ts.getComponent();
                try {
                    Planet planet = (Planet) target.getClientProperty("planet");
                    Integer slot = (Integer) target.getClientProperty("slot");
                    Structure structure = StaticData.getStructureByName("" + t.getTransferData(DataFlavor.stringFlavor));

                    if (structure.getStructureMode().equals(Structure.mode.ONEPERPLANET)) {
                        if (planet.hasStructureClass(structure)) {
                            Client.getInstance().showError("You can only have one '" + structure.getClass().getSimpleName() + "' per planet", null, false, false);
                            return false;
                        }
                        for (Order o : planet.getStarSystem().getBuildQueue()) {
                            if (o instanceof BuildStructureOrder) {
                                BuildStructureOrder bo = (BuildStructureOrder) o;
                                if (bo.getStructure().getClass().equals(structure.getClass()) && bo.getPlanet().equals(planet)) {
                                    Client.getInstance().showError("Already building '" + structure.getClass().getSimpleName() + "' there, and you can only have one per planet", null, false, false);
                                    return false;
                                }
                            }
                        }

                    }

                    boolean success = Client.getInstance().getComm().server_buildStructure(planet, structure, slot);
                    if (success) {
                        BuildStructureOrder bso = new BuildStructureOrder(starsystem, planet, slot, structure);
                        try {
                            starsystem.addBuildOrder(bso);
                            Client.getInstance().getSoundSystem().playClip("build.wav");
                        } catch (InvalidOrderException ex) {
                            Client.getInstance().showError("TransferHandler unable to add build structure order to system", ex, false, false);
                        }
                        makeGUI();
                        pack();
                        Client.getInstance().getBottomGuiPanel().updateBuildList();
                    }
                    return true;
                } catch (UnsupportedFlavorException e) {
                } catch (IOException e) {
                }
                return false;
            }
        };

        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        setBackground(Colors.TREKWAR_BG_COLOR);

        setFrameIcon(new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(-1, 18, 0)));

        setLocation(x, y);
    }

    public void setStarSystem(StarSystem s) {
        this.starsystem = s;
        makeGUI();
        pack();
    }

    private void makeGUI() {
        if (contentPanel == null) {
            contentPanel = new JPanel();
            contentPanel.setLayout(null);
            contentPanel.setBackground(Colors.TREKWAR_BG_COLOR);

            setLayout(new GridLayout(1, 1));
            getContentPane().add(contentPanel);
        } else {
            if (warningBlinkAnim != null) {
                warningBlinkAnim.cancel(false);
            }

            contentPanel.removeAll();
        }
        contentPanel.setLayout(null);
        int height = 600;
        int width = 954;


        boolean showWarning = false;
        String warningMessage = "";

        if (starsystem.getSystemResearchSurplus() < 0) {
            showWarning = true;
            warningMessage = Client.getLanguage().get("systemcontrol_warning_research");
        }

        if (starsystem.getSystemIndustrySurplus() < 0) {
            showWarning = true;
            warningMessage = Client.getLanguage().get("systemcontrol_warning_industry");
        }

        if (starsystem.getSystemPowerSurplus() < 0) {
            showWarning = true;
            warningMessage = Client.getLanguage().get("systemcontrol_warning_power");
        }

        if (starsystem.getSystemFoodSurplus() < 0) {
            showWarning = true;
            warningMessage = Client.getLanguage().get("systemcontrol_warning_food");
        }


        if (showWarning) {
            height = 630;
        }

        if (starsystem.getUser().equals(Client.getInstance().getLocalUser()) && starsystem.hasShipyard() == false) {
            width = 702; // no need for shipyard panel
        }

        contentPanel.setSize(new Dimension(width, height));
        contentPanel.setPreferredSize(new Dimension(width, height));
        contentPanel.setMinimumSize(new Dimension(width, height));

        //##################### USER OWNS SYSTEM #####################\\
        if (starsystem.getUser().equals(Client.getInstance().getLocalUser())) {
            contentPanel.setBackground(Colors.TREKWAR_BG_COLOR);
            makePlanetPanel();

            leftColumn = new JPanel();
            leftColumn.setLayout(null);
            leftColumn.setBackground(Colors.TREKWAR_BG_COLOR);
            leftColumn.setBounds(0, 0, 400, 600);

            // build queue List
            JLabel buildQueueLabel = new JLabel(Client.getLanguage().get("systemcontrol_build_queue"));
            buildQueueLabel.setBounds(5, 5, 150, 22);

            DefaultListModel buildQueueModel = new DefaultListModel();
            buildQueueList = new JList(buildQueueModel);
            int buildNum = 1;
            for (Order o : starsystem.getBuildQueue()) {
                if (o instanceof BuildStructureOrder) {
                    ((BuildStructureOrder) o).setPositionInBuildQueue(buildNum++);
                    buildQueueModel.addElement((BuildStructureOrder) o);
                } else if (o instanceof BuildShipOrder) {
                    ((BuildShipOrder) o).setPositionInBuildQueue(buildNum++);
                    buildQueueModel.addElement((BuildShipOrder) o);
                }
            }
            JScrollPane buildQueueListSP = new JScrollPane(buildQueueList);
            buildQueueListSP.setBounds(0, 28, 400, 100);
            buildQueueListSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            buildQueueListSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            buildQueueList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), e.getComponent());
                    buildQueueList.setSelectedIndex(buildQueueList.locationToIndex(e.getPoint()));

                    if (buildQueueList.getSelectedValue() == null) {
                        return;
                    }

                    JPopupMenu popup = new JPopupMenu();
                    int selectionPositionInQueue = -1;
                    int currentOrderIndustryRequired = -1;
                    int currentOrderIndustryInvested = -1;
                    if (buildQueueList.getSelectedValue() instanceof BuildStructureOrder) {
                        BuildStructureOrder o = (BuildStructureOrder) buildQueueList.getSelectedValue();
                        selectionPositionInQueue = o.getPositionInBuildQueue();
                        popup.add(o.getPositionInBuildQueue() + ". " + o.getStructure().getName()
                                + " (" + o.getIndustryInvested() + " / " + o.getStructure().getCost() + ")");
                        currentOrderIndustryRequired = o.getStructure().getCost();
                        currentOrderIndustryInvested = o.getIndustryInvested();
                    } else if (buildQueueList.getSelectedValue() instanceof BuildShipOrder) {
                        BuildShipOrder o = (BuildShipOrder) buildQueueList.getSelectedValue();
                        selectionPositionInQueue = o.getPositionInBuildQueue();
                        popup.add(o.getPositionInBuildQueue() + ". " + o.getTemplate().getName()
                                + " (" + o.getIndustryInvested() + " / " + o.getTemplate().getCost() + ")");
                        currentOrderIndustryRequired = o.getTemplate().getCost();
                        currentOrderIndustryInvested = o.getIndustryInvested();
                    }
                    popup.addSeparator();
                    final JMenuItem hurry = new JMenuItem(Client.getLanguage().get("systemcontrol_hurry"));
                    JMenuItem delete = new JMenuItem(Client.getLanguage().get("systemcontrol_delete"));
                    JMenuItem moveUp = new JMenuItem(Client.getLanguage().get("systemcontrol_moveup"));
                    final JMenuItem moveTo = new JMenuItem(Client.getLanguage().get("systemcontrol_moveto"));
                    JMenuItem moveDown = new JMenuItem(Client.getLanguage().get("systemcontrol_movedown"));

                    popup.add(hurry);
                    if (starsystem.getOre() <= 0) {
                        hurry.setEnabled(false);
                    }
                    popup.add(delete);
                    if (selectionPositionInQueue > 1) {
                        popup.add(moveUp);
                    }
                    if (buildQueueList.getModel().getSize() > 2) {
                        popup.add(moveTo);
                    }
                    if (selectionPositionInQueue < buildQueueList.getModel().getSize()) {
                        popup.add(moveDown);
                    }

                    final int index = selectionPositionInQueue; // WARNING: this index starts on 1.. decrement before use on buildQueue object
                    final int investedInOrder = currentOrderIndustryInvested;
                    final int orderCost = currentOrderIndustryRequired;

                    hurry.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (starsystem.getOre() <= 0) {
                                JOptionPane.showMessageDialog(hurry, Client.getLanguage().get("systemcontrol_no_ore"));
                                return;
                            }

                            int maxHurry = Math.min(orderCost - investedInOrder, starsystem.getOre());
                            String val = JOptionPane.showInputDialog(moveTo, Client.getLanguage().get("systemcontrol_ore_amount") + " (1 " + Client.getLanguage().get("to") + " " + maxHurry + ")");
                            if (val == null) {
                                return; // user clicked cancel
                            }
                            try {
                                int oreAmount = Integer.parseInt(val);
                                if (oreAmount < 1 || oreAmount > starsystem.getOre()
                                        || oreAmount > (orderCost - investedInOrder)) {
                                    throw new NumberFormatException("invalid value");
                                }

                                Client.getInstance().getComm().server_hurryProduction(starsystem, index, oreAmount);
                                starsystem.removeOre(oreAmount);

                                // update local copy
                                Order o = starsystem.getBuildQueue().get(index - 1);
                                if (o instanceof BuildStructureOrder) {
                                    BuildStructureOrder bso = (BuildStructureOrder) o;
                                    bso.setIndustryInvested(bso.getIndustryInvested() + oreAmount);
                                    bso.updateTurnsToCompletion();
                                }
                                if (o instanceof BuildShipOrder) {
                                    BuildShipOrder bso = (BuildShipOrder) o;
                                    bso.setIndustryInvested(bso.getIndustryInvested() + oreAmount);
                                    bso.updateTurnsToCompletion();
                                }
                                makeGUI();
                                pack();
                                Client.getInstance().getBottomGuiPanel().updateBuildList();
                            } catch (NumberFormatException nfe) {
                                Client.getInstance().showError(Client.getLanguage().get("systemcontrol_invalid_value"), null, false, false);
                            }
                        }
                    });

                    delete.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Client.getInstance().getComm().server_updateBuildQueue(starsystem, index, BuildQueueRequestType.REMOVE);
                            starsystem.getBuildQueue().remove(index - 1);
                            makeGUI();
                            pack();
                            Client.getInstance().getBottomGuiPanel().updateBuildList();
                        }
                    });

                    moveUp.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Client.getInstance().getComm().server_updateBuildQueue(starsystem, index, BuildQueueRequestType.MOVEUP);
                            Collections.swap(starsystem.getBuildQueue(), index - 1, index - 2);
                            makeGUI();
                            pack();
                            Client.getInstance().getBottomGuiPanel().updateBuildList();
                        }
                    });

                    moveTo.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String val = JOptionPane.showInputDialog(moveTo, Client.getLanguage().get("systemcontrol_new_pos_in_queue") + " (1 " + Client.getLanguage().get("to") + " " + starsystem.getBuildQueue().size() + ")");
                            if (val == null) {
                                return; // user clicked cancel
                            }
                            int newPos = -1;
                            int currentPos = index;
                            try {
                                newPos = Integer.parseInt(val);
                                if (newPos < 0 || newPos > starsystem.getBuildQueue().size()) {
                                    throw new NumberFormatException("invalid index");
                                }
                                if (currentPos < newPos) {
                                    while (currentPos < newPos) {
                                        Client.getInstance().getComm().server_updateBuildQueue(starsystem, currentPos, BuildQueueRequestType.MOVEDOWN);
                                        Collections.swap(starsystem.getBuildQueue(), currentPos - 1, currentPos);
                                        currentPos++;
                                    }
                                } else if (currentPos > newPos) {
                                    while (currentPos > newPos) {
                                        Client.getInstance().getComm().server_updateBuildQueue(starsystem, currentPos, BuildQueueRequestType.MOVEUP);
                                        Collections.swap(starsystem.getBuildQueue(), currentPos - 1, currentPos - 2);
                                        currentPos--;
                                    }
                                }
                                makeGUI();
                                pack();
                                Client.getInstance().getBottomGuiPanel().updateBuildList();
                            } catch (NumberFormatException nfe) {
                                Client.getInstance().showError(Client.getLanguage().get("systemcontrol_bad_value_or_invalid_index"), null, false, false);
                            }
                        }
                    });

                    moveDown.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Client.getInstance().getComm().server_updateBuildQueue(starsystem, index, BuildQueueRequestType.MOVEDOWN);
                            Collections.swap(starsystem.getBuildQueue(), index - 1, index);
                            makeGUI();
                            pack();
                            Client.getInstance().getBottomGuiPanel().updateBuildList();
                        }
                    });
                    popup.show(e.getComponent(), pt.x, pt.y);
                }
            });

            leftColumn.add(buildQueueLabel);
            leftColumn.add(buildQueueListSP);
            leftColumn.add(planetScrollPane);
            contentPanel.add(leftColumn);

            makeStructureList();
            contentPanel.add(middleColumn);

            if (starsystem.hasShipyard()) {
                makeShipList();
                contentPanel.add(rightColumn);
            }
        } //##################### UNINHABITED SYSTEM #####################\\
        else if (starsystem.getUser().equals(StaticData.nobodyUser)) {
            contentPanel.setLayout(new GridLayout(3, 4));
            contentPanel.setBackground(Color.BLACK);

            for (Planet p : starsystem.getPlanets()) {
                JPanel uninhabitedPlanetPanel = new JPanel();
                uninhabitedPlanetPanel.setLayout(new BoxLayout(uninhabitedPlanetPanel, BoxLayout.Y_AXIS));
                uninhabitedPlanetPanel.setBackground(Color.BLACK);
                String imgStub = "graphics/planets/";
                switch (p.getType()) {
                    case arctic:
                        imgStub += "arctic";
                        break;
                    case barren:
                        imgStub += "barren";
                        break;
                    case desert:
                        imgStub += "desert";
                        break;
                    case gasGiant:
                        imgStub += "gasgiant";
                        break;
                    case jungle:
                        imgStub += "jungle";
                        break;
                    case oceanic:
                        imgStub += "oceanic";
                        break;
                    case terran:
                        imgStub += "terran";
                        break;
                    case volcanic:
                        imgStub += "volcanic";
                        break;
                }
                JLabel planetImageLabel = new JLabel(ImageManager.getInstance().getImage(imgStub + ".png"));
                planetImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                uninhabitedPlanetPanel.add(planetImageLabel);

                if (p.getType() == PlanetClassification.gasGiant) {
                    JLabel h2Label = new JLabel("" + p.getDeuteriumPerTurn(), ImageManager.getInstance().getImage("graphics/misc_icons/deuterium.png"), SwingConstants.CENTER);
                    h2Label.setAlignmentX(Component.CENTER_ALIGNMENT);
                    uninhabitedPlanetPanel.add(h2Label);

                } else {
                    JLabel popLabel = new JLabel("" + p.getMaximumPopulation(), ImageManager.getInstance().getImage("graphics/misc_icons/user.png"), SwingConstants.CENTER);
                    JLabel fertLabel = new JLabel("" + Client.getNumberFormat().format(p.getFertility()), ImageManager.getInstance().getImage("graphics/misc_icons/fertility.png"), SwingConstants.CENTER);
                    JLabel structLabel = new JLabel("" + p.getMaximumStructures(), ImageManager.getInstance().getImage("graphics/misc_icons/house.png"), SwingConstants.CENTER);
                    popLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    fertLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    structLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    uninhabitedPlanetPanel.add(popLabel);
                    uninhabitedPlanetPanel.add(fertLabel);
                    uninhabitedPlanetPanel.add(structLabel);
                }
                contentPanel.add(uninhabitedPlanetPanel);
            }
        } //##################### ENEMY SYSTEM #####################\\
        else {
            contentPanel.setLayout(new FlowLayout());
            contentPanel.setBackground(Color.RED);
            contentPanel.add(new JLabel("Viewing Enemy systems is currently not implemented"));
        }


        // SHOW WARNINGS (IF ANY) FOR OWN SYSTEMS
        if (starsystem.getUser().equals(Client.getInstance().getLocalUser()) && showWarning) {
            JPanel warningPanel = new JPanel();
            warningPanel.setBackground(Color.RED);
            warningPanel.setBounds(0, 600, 954, 30);
            warningPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            final ImageIcon warningIcon = ImageManager.getInstance().getImage("graphics/misc_icons/warning.png");
            final JLabel warningIconLabel = new JLabel(warningIcon);
            warningIconLabel.setPreferredSize(new Dimension(20, 20));
            warningPanel.add(warningIconLabel);


            warningPanel.add(new JLabel(warningMessage));

            warningBlinkAnim = new SwingWorker() {
                private boolean show;

                @Override
                protected Object doInBackground() throws Exception {
                    while (!isCancelled()) {
                        if (show) {
                            warningIconLabel.setIcon(warningIcon);
                        } else {
                            warningIconLabel.setIcon(null);
                        }

                        show = !show;

                        Thread.sleep(500);
                    }
                    return null;
                }
            };
            warningBlinkAnim.execute();

            contentPanel.add(warningPanel);
        }

    }

    private void makeStructureList() {
        if (middleColumn == null) {
            middleColumn = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    Image img = ImageManager.getInstance().getImage("graphics/systemmanager_structurelist.png").getImage();
                    g.drawImage(img, 0, 0, null);

                }
            };
            middleColumn.setLayout(null);
            middleColumn.setBounds(400, 5, 302, 589);

        } else {
            middleColumn.removeAll();
        }


        JPanel structuresPanel = new JPanel();
        structuresPanel.setOpaque(false);
        structuresPanel.setLayout(new BoxLayout(structuresPanel, BoxLayout.Y_AXIS));
        JScrollPane structureListSP = new JScrollPane(structuresPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        structureListSP.setBorder(BorderFactory.createEmptyBorder());
        structureListSP.setBounds(18, 237, 265, 322);

        structureImageLabel = new JLabel();
        structureImageLabel.setBounds(13, 12, 102, 94);

        structureNameHeadingLabel = new JLabel();
        structureNameHeadingLabel.setVerticalAlignment(SwingConstants.TOP);
        structureNameHeadingLabel.setBounds(120, 12, 165, 30);
        structureNameHeadingLabel.setFont(new Font("", Font.BOLD, 14));
        structureNameHeadingLabel.setForeground(Color.BLACK);

        structureDescriptionArea = new JTextArea();
        structureDescriptionArea.setWrapStyleWord(true);
        structureDescriptionArea.setLineWrap(true);
        JScrollPane structureDescriptionAreaScrollPane = new JScrollPane(structureDescriptionArea);
        structureDescriptionAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        structureDescriptionAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        structureDescriptionAreaScrollPane.setBounds(120, 42, 165, 65);

        structureStatCostLabel = new JLabel("", SwingConstants.LEFT);
        structureStatCostLabel.setBounds(12, 110, 140, 22);
        structureStatCostLabel.setForeground(Color.BLACK);

        structureStatPowerLabel = new JLabel("", SwingConstants.LEFT);
        structureStatPowerLabel.setBounds(155, 110, 140, 22);
        structureStatPowerLabel.setForeground(Color.BLACK);


        structureStatIndustryLabel = new JLabel("", SwingConstants.LEFT);
        structureStatIndustryLabel.setBounds(12, 135, 140, 22);
        structureStatIndustryLabel.setForeground(Color.BLACK);

        structureStatResearchLabel = new JLabel("", SwingConstants.LEFT);
        structureStatResearchLabel.setBounds(155, 135, 140, 22);
        structureStatResearchLabel.setForeground(Color.BLACK);

        structureStatFoodLabel = new JLabel("", SwingConstants.LEFT);
        structureStatFoodLabel.setBounds(12, 160, 140, 22);
        structureStatFoodLabel.setForeground(Color.BLACK);

        middleColumn.add(structureStatCostLabel);
        middleColumn.add(structureStatPowerLabel);
        middleColumn.add(structureStatIndustryLabel);
        middleColumn.add(structureStatResearchLabel);
        middleColumn.add(structureStatFoodLabel);

        for (final Structure structure : Client.getInstance().getLocalUser().getAvailableStructures()) {
            if (structure.getStructureMode() == Structure.mode.ONEPERSYSTEM) {
                boolean found = false;

                // don't show one-per-system structures already buildt in this system
                for (Planet p : starsystem.getPlanets()) {
                    if (p.hasStructureClass(structure)) {
                        found = true;
                        continue;
                    }
                }

                // don't show one-per-system structures already in the build queue
                for (Order o : starsystem.getBuildQueue()) {
                    if (o instanceof BuildStructureOrder) {
                        BuildStructureOrder bo = (BuildStructureOrder) o;
                        if (structure.equals(bo.getStructure())) {
                            found = true;
                        }
                    }
                }

                if (found) {
                    continue;
                }
            }

            JPanel structurePanel = new JPanel();
            structurePanel.setOpaque(false);
            structurePanel.setLayout(null);
            structurePanel.setSize(new Dimension(260, 40));
            structurePanel.setPreferredSize(new Dimension(260, 40));
            structurePanel.setMinimumSize(new Dimension(260, 40));
            structurePanel.setMaximumSize(new Dimension(260, 40));

            JLabel structureIconLabel = new JLabel(ImageManager.getInstance().getImage("graphics/structures/58x37/" + structure.getImageFilename() + ".png"));
            structureIconLabel.putClientProperty("structurename", structure.getName());
            structureIconLabel.setTransferHandler(structure_th);
            structureIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent evt) {
                    showStructureInfo(structure);
                    JComponent c = (JComponent) evt.getSource();
                    TransferHandler th = c.getTransferHandler();
                    th.exportAsDrag(c, evt, TransferHandler.COPY);
                }
            });
            structureIconLabel.setBounds(1, 1, 58, 37);

            JLabel structureNameLabel = new JLabel(structure.getName());
            structureNameLabel.setBounds(60, 1, 200, 20);

            JLabel structureCostLabel = new JLabel(structure.getCost() + " " + Client.getLanguage().get("systemcontrol_industry")
                    + " - " + (int) (Math.ceil(((double) structure.getCost() / starsystem.getSystemIndustrySurplus()))) + " " + Client.getLanguage().get("systemcontrol_turns"));
            structureCostLabel.setBounds(60, 21, 200, 20);

            structurePanel.add(structureIconLabel);
            structurePanel.add(structureNameLabel);
            structurePanel.add(structureCostLabel);

            structurePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showStructureInfo(structure);
                }
            });

            structuresPanel.add(structurePanel);
        }

        middleColumn.add(structureImageLabel);
        middleColumn.add(structureNameHeadingLabel);
        middleColumn.add(structureDescriptionAreaScrollPane);
        middleColumn.add(structureListSP);

    }

    private void makeShipList() {
        if (rightColumn == null) {
            rightColumn = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    Image img = ImageManager.getInstance().getImage("graphics/systemmanager_shiplist.png").getImage();
                    g.drawImage(img, 0, 0, null);
                }
            };
            rightColumn.setLayout(null);
            rightColumn.setBounds(702, 5, 252, 589);
        } else {
            rightColumn.removeAll();
        }

        JPanel shipsPanel = new JPanel();
        shipsPanel.setOpaque(false);
        shipsPanel.setLayout(new BoxLayout(shipsPanel, BoxLayout.Y_AXIS));
        JScrollPane shipListSP = new JScrollPane(shipsPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        shipListSP.setBorder(BorderFactory.createEmptyBorder());
        shipListSP.setBounds(15, 187, 223, 383);

        spacedockLabel = new JLabel("", SwingConstants.CENTER);
        spacedockLabel.setBounds(177, 12, 45, 45);

        shipNameLabel = new JLabel();
        shipNameLabel.setBounds(12, 45, 140, 22);
        shipNameLabel.setForeground(Color.BLACK);

        shipInfoPanel = new JPanel();
        shipInfoPanel.setOpaque(false);
        shipInfoPanel.setLayout(new BoxLayout(shipInfoPanel, BoxLayout.Y_AXIS));
        JScrollPane shipInfoSP = new JScrollPane(shipInfoPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        shipInfoSP.setBorder(BorderFactory.createEmptyBorder());
        shipInfoSP.setBounds(15, 65, 223, 82);


        shipStatCostLabel = new JLabel("", SwingConstants.LEFT);
        shipStatCostLabel.setBounds(15, 70, 140, 22);
        shipStatCostLabel.setForeground(Color.BLACK);

        shipStatUpkeepLAbel = new JLabel("", SwingConstants.LEFT);
        shipStatUpkeepLAbel.setForeground(Color.BLACK);

        shipStatSensorLabel = new JLabel("", SwingConstants.LEFT);
        shipStatSensorLabel.setBounds(140, 70, 140, 22);
        shipStatSensorLabel.setForeground(Color.BLACK);

        shipStatSpeedLabel = new JLabel("", SwingConstants.LEFT);
        shipStatSpeedLabel.setBounds(12, 95, 140, 22);
        shipStatSpeedLabel.setForeground(Color.BLACK);

        shipStatRangeLabel = new JLabel("", SwingConstants.LEFT);
        shipStatRangeLabel.setBounds(140, 95, 140, 22);
        shipStatRangeLabel.setForeground(Color.BLACK);

        shipStatWeaponLabel = new JLabel("", SwingConstants.LEFT);
        shipStatWeaponLabel.setBounds(12, 120, 140, 22);
        shipStatWeaponLabel.setForeground(Color.BLACK);

        shipStatHPLabel = new JLabel("", SwingConstants.LEFT);
        shipStatHPLabel.setBounds(140, 120, 140, 22);
        shipStatHPLabel.setForeground(Color.BLACK);

        shipStatArmorLabel = new JLabel("", SwingConstants.LEFT);
        shipStatArmorLabel.setBounds(12, 145, 140, 22);
        shipStatArmorLabel.setForeground(Color.BLACK);

        shipStatShieldLabel = new JLabel("", SwingConstants.LEFT);
        shipStatShieldLabel.setBounds(140, 145, 140, 22);
        shipStatShieldLabel.setForeground(Color.BLACK);

        buildShipButton = new JButton(Client.getLanguage().get("build"));
        buildShipButton.setBounds(60, 150, 60, 22);

        shipInfoButton = new JButton(Client.getLanguage().get("info"));
        shipInfoButton.setBounds(130, 150, 60, 22);

        rightColumn.add(spacedockLabel);
        rightColumn.add(shipNameLabel);

        shipInfoPanel.add(shipStatCostLabel);
        shipInfoPanel.add(shipStatUpkeepLAbel);
        shipInfoPanel.add(shipStatRangeLabel);
        shipInfoPanel.add(shipStatHPLabel);
        shipInfoPanel.add(shipStatArmorLabel);
        shipInfoPanel.add(shipStatShieldLabel);
        shipInfoPanel.add(shipStatWeaponLabel);
        shipInfoPanel.add(shipStatSensorLabel);
        shipInfoPanel.add(shipStatSpeedLabel);

        rightColumn.add(shipInfoSP);

        rightColumn.add(buildShipButton);
        rightColumn.add(shipInfoButton);

        buildShipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentlySelectedShipTemplate != null) {
                    // If colonyship, warn if population is low.
                    if (currentlySelectedShipTemplate.canColonize()) {
                        int colonists = Math.min(currentlySelectedShipTemplate.getColonistCapacity(), starsystem.getPopulation() - 200);
                        if (colonists < 20) {
                            // there is not enough population to build the ship
                            Client.getInstance().showMessage(Language.pop(Client.getLanguage().get("not_enough_population_in_system_to_make_colonyship"), starsystem.getPopulation()));
                            return;
                        } else if (colonists < currentlySelectedShipTemplate.getColonistCapacity()) {
                            Client.getInstance().showMessage(Language.pop(Client.getLanguage().get("not_enough_population_in_system_to_fill_colonyship"), currentlySelectedShipTemplate.getColonistCapacity(), colonists));
                        } else if (starsystem.getPopulation() - 500 < colonists) {
                            // will leave less than 500 million people in system
                            Client.getInstance().showMessage(Language.pop(Client.getLanguage().get("not_enough_population_in_system_to_fill_colonyship_and_have_500m_extra"), starsystem.getPopulation() - colonists));
                        }
                    }

                    boolean success = Client.getInstance().getComm().server_buildShip(starsystem, currentlySelectedShipTemplate);
                    if (success) {
                        BuildShipOrder bso = new BuildShipOrder(Client.getInstance().getLocalUser(), starsystem, currentlySelectedShipTemplate);
                        try {
                            starsystem.addBuildOrder(bso);
                        } catch (InvalidOrderException ex) {
                            Client.getInstance().showError("Unable to add build ship order to system", ex, false, false);
                        }
                        makeGUI();
                        showShipInfo(currentlySelectedShipTemplate);
                        pack();
                        Client.getInstance().getBottomGuiPanel().updateBuildList();
                    }
                }
            }
        });

        shipInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ShipInfoWindow shipInfoWindow = new ShipInfoWindow(Client.getLanguage().get("template_info"), ImageManager.getInstance().getImage("graphics/ship_icons/" + currentlySelectedShipTemplate.getHullClass().getIconFileName()), 200, 200, currentlySelectedShipTemplate);
                shipInfoWindow.setVisible(true);
                Client.getInstance().getDesktop().add(shipInfoWindow);
                Client.getInstance().getDesktop().moveToFront(shipInfoWindow);
            }
        });

        if (currentlySelectedShipTemplate != null) {
            showShipInfo(currentlySelectedShipTemplate);
        }

        for (final ShipTemplate template : Client.getInstance().getLocalUser().getShipTemplates()) {
            JPanel templatePanel = new JPanel();
            templatePanel.setOpaque(false);
            templatePanel.setLayout(null);
            templatePanel.setSize(new Dimension(220, 48));
            templatePanel.setPreferredSize(new Dimension(220, 48));
            templatePanel.setMinimumSize(new Dimension(220, 48));
            templatePanel.setMaximumSize(new Dimension(220, 48));

            JLabel shipIconLabel = new JLabel(ImageManager.getInstance().getImage("graphics/ship_icons/45x45/" + template.getHullClass().getShipdesignerImageFileName()));
            shipIconLabel.putClientProperty("templatename", template.getName());
            shipIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent evt) {
                    showShipInfo(template);
                }
            });
            shipIconLabel.setBounds(1, 1, 45, 45);

            JLabel templateNameLabel = new JLabel(template.getName());
            templateNameLabel.setBounds(50, 1, 150, 20);


            JLabel shipCostLabel = new JLabel(template.getCost() + " " + Client.getLanguage().get("systemcontrol_industry") + " - "
                    + (int) (Math.ceil((double) template.getCost() / starsystem.getSystemIndustrySurplus())) + " " + Client.getLanguage().get("systemcontrol_turns"));
            shipCostLabel.setBounds(50, 21, 200, 20);

            templatePanel.add(shipIconLabel);
            templatePanel.add(templateNameLabel);
            templatePanel.add(shipCostLabel);

            templatePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showShipInfo(template);
                }
            });

            shipsPanel.add(templatePanel);
        }

        rightColumn.add(shipListSP);
    }

    private void makePlanetPanel() {
        planetSlotLabels.clear();

        if (planetPanel == null) {
            planetPanel = new JPanel();
            planetPanel.setBackground(Color.BLACK);
            planetPanel.setOpaque(true);
            planetPanel.setLayout(new BoxLayout(planetPanel, BoxLayout.Y_AXIS));
            planetPanel.setBounds(0, 130, 400, 470);
            planetScrollPane = new JScrollPane(planetPanel);
            planetScrollPane.setBounds(0, 130, 400, 470);
            planetScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            planetScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        } else {
            planetPanel.removeAll();
        }

        // FOR ALL PLANETS
        for (final Planet p : starsystem.getPlanets()) {
            JPanel planetHolder = new JPanel();
            planetHolder.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            planetHolder.setPreferredSize(new Dimension(400, 130));
            planetHolder.setSize(new Dimension(400, 130));
            planetHolder.setMaximumSize(new Dimension(400, 130));

            String imgStub = "graphics/planets/";
            switch (p.getType()) {
                case arctic:
                    imgStub += "arctic";
                    break;
                case barren:
                    imgStub += "barren";
                    break;
                case desert:
                    imgStub += "desert";
                    break;
                case gasGiant:
                    imgStub += "gasgiant";
                    break;
                case jungle:
                    imgStub += "jungle";
                    break;
                case oceanic:
                    imgStub += "oceanic";
                    break;
                case terran:
                    imgStub += "terran";
                    break;
                case volcanic:
                    imgStub += "volcanic";
                    break;
            }

            JLabel planetImg = new JLabel(ImageManager.getInstance().getImage(imgStub + ".png"));

            final String filename = imgStub;
            JPanel surfaceImg = new JPanel() {
                public void paintComponent(Graphics g) {
                    Image img = ImageManager.getInstance().getImage(filename + "_surface.png").getImage();
                    g.drawImage(img, 0, 0, null);
                }
            };
            surfaceImg.setLayout(null);

            // FOR ALL SLOTS ON THE SURFACE
            for (int i : p.getSurfaceMap().keySet()) {
                final int structureSlot = i;
                final Structure structure = p.getStructuresMap().get(i);
                Point cords = p.getSurfaceMap().get(i);
                // STRUCTURE EXISTS
                if (structure != null) {
                    final JLabel structureLabel = new JLabel();

                    if (p.isStructureEnabled(i)) {
                        structureLabel.setIcon(ImageManager.getInstance().getImage("graphics/structures/58x37/" + structure.getImageFilename() + ".png"));
                    } else {
                        structureLabel.setIcon(ImageManager.getInstance().getImage("graphics/structures/58x37/" + structure.getImageFilename() + "_disabled.png"));
                    }
                    structureLabel.setBounds(cords.x, cords.y, 58, 37);
                    surfaceImg.add(structureLabel);

                    structureLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            showStructureInfo(structure);
                            JPopupMenu popup = new JPopupMenu();
                            popup.add(structure.getName());
                            popup.addSeparator();

                            JMenuItem demolish = new JMenuItem(Client.getLanguage().get("systemcontrol_demolish"));
                            JMenuItem toggle = null;
                            if (p.isStructureEnabled(structureSlot)) {
                                toggle = new JMenuItem(Client.getLanguage().get("systemcontrol_disable"));
                            } else {
                                toggle = new JMenuItem(Client.getLanguage().get("systemcontrol_enable"));


                            }

                            popup.add(demolish);
                            popup.add(toggle);

                            demolish.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    int confirm = JOptionPane.showConfirmDialog(structureLabel, Client.getLanguage().get("systemcontrol_confirm_demolish2"), Client.getLanguage().get("systemcontrol_confirm_demolish"), JOptionPane.WARNING_MESSAGE);
                                    if (confirm == JOptionPane.YES_OPTION) {
                                        Client.getInstance().getComm().server_demolishStructure(p, structureSlot);
                                        p.delStructure(structureSlot);
                                        makeGUI();
                                        pack();
                                        Client.getInstance().getBottomGuiPanel().updateInhabitedSystemView();
                                    }
                                }
                            });

                            toggle.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    if (p.isStructureEnabled(structureSlot)) {
                                        Client.getInstance().getComm().server_changeStructureState(p, structureSlot, StructureStateChangeRequestType.DISABLE);
                                        p.setStructureEnabled(structureSlot, false);
                                        structureLabel.setIcon(ImageManager.getInstance().getImage("graphics/structures/58x37/" + structure.getImageFilename() + "_disabled.png"));
                                        Client.getInstance().getSoundSystem().playClip("powerdown.wav");
                                    } else {
                                        Client.getInstance().getComm().server_changeStructureState(p, structureSlot, StructureStateChangeRequestType.ENABLE);
                                        p.setStructureEnabled(structureSlot, true);
                                        structureLabel.setIcon(ImageManager.getInstance().getImage("graphics/structures/58x37/" + structure.getImageFilename() + ".png"));
                                        Client.getInstance().getSoundSystem().playClip("powerup.wav");
                                    }
                                    makeGUI();
                                    pack();
                                }
                            });


                            Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), e.getComponent());
                            popup.show(e.getComponent(), pt.x, pt.y);
                        }
                    });
                } else {
                    // CHECK IF STRUCTURE IS UNDER CONSTRUCTION
                    Structure structureUnderConstruction = null;
                    int structurePlaceInQueue = -1;
                    int buildQueueI = 0;
                    for (Order order : starsystem.getBuildQueue()) {
                        if (order instanceof BuildStructureOrder) {
                            BuildStructureOrder buildOrder = (BuildStructureOrder) order;
                            buildQueueI++;
                            if (buildOrder.getPlanet().equals(p) && buildOrder.getSlot() == i) {
                                structureUnderConstruction = buildOrder.getStructure();
                                structurePlaceInQueue = buildQueueI;
                                break;
                            }
                        }
                    }

                    JLabel slotLabel = null;
                    JLabel slotIcon = null;

                    // HAS BUILDING UNDER CONSTRUCTION
                    if (structureUnderConstruction != null) {
                        slotLabel = new JLabel(ImageManager.getInstance().getImage("graphics/structures/58x37/" + structureUnderConstruction.getImageFilename() + "_queue.png"));
                        slotLabel.setBounds(cords.x, cords.y, 58, 37);
                        if (structurePlaceInQueue == 1) {
                            slotIcon = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/maintenance.gif"));
                        } else {
                            slotIcon = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/hourglass.png"));
                        }

                    } // EMPTY SLOT
                    else {
                        slotLabel = new JLabel();
                        slotLabel.setBounds(cords.x, cords.y, 58, 37);
                        slotLabel.setTransferHandler(structure_th);
                        slotLabel.putClientProperty("planet", p);
                        slotLabel.putClientProperty("slot", i);

                    }

                    if (slotIcon != null) {
                        slotIcon.setBounds(cords.x + 38, cords.y + 16, 20, 20);
                        surfaceImg.add(slotIcon);
                    }

                    surfaceImg.add(slotLabel);
                    planetSlotLabels.add(slotLabel);
                }
            }

            surfaceImg.setSize(260, 130);
            surfaceImg.setPreferredSize(new Dimension(260, 130));


            planetImg.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getXOnScreen() - Client.getInstance().getX();
                    int y = e.getYOnScreen() - Client.getInstance().getY();
                    PlanetInfoWindow planetInfo = new PlanetInfoWindow(x, y, p);
                    planetInfo.setVisible(true);
                    Client.getInstance().getDesktop().add(planetInfo);
                    Client.getInstance().getDesktop().moveToFront(planetInfo);
                }
            });

            planetHolder.add(planetImg);
            planetHolder.add(surfaceImg);

            planetPanel.add(planetHolder);
        }
    }

    @Override
    public void doDefaultCloseAction() {
        super.doDefaultCloseAction();
    }

    private void showStructureInfo(Structure structure) {
        structureImageLabel.setIcon(ImageManager.getInstance().getImage("graphics/structures/" + structure.getImageFilename() + ".png"));
        structureNameHeadingLabel.setText(structure.getName());
        structureDescriptionArea.setText(structure.getDescription());
        structureDescriptionArea.moveCaretPosition(0);

        structureStatCostLabel.setText("" + structure.getCost());
        structureStatCostLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/coins.png"));
        structureStatCostLabel.setToolTipText(Client.getLanguage().get("systemcontrol_cost"));

        structureStatPowerLabel.setText("" + structure.getPowerOutput());
        structureStatPowerLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/lightning.png"));
        structureStatPowerLabel.setToolTipText(Client.getLanguage().get("systemcontrol_power"));

        structureStatIndustryLabel.setText("" + structure.getIndustryOutput());
        structureStatIndustryLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/cog.png"));
        structureStatIndustryLabel.setToolTipText(Client.getLanguage().get("systemcontrol_industry"));

        structureStatResearchLabel.setText("" + structure.getResearchOutput());
        structureStatResearchLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/research.png"));
        structureStatResearchLabel.setToolTipText(Client.getLanguage().get("systemcontrol_research"));

        structureStatFoodLabel.setText("" + structure.getFoodOutput());
        structureStatFoodLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/food.png"));
        structureStatFoodLabel.setToolTipText(Client.getLanguage().get("systemcontrol_food"));

        Client.getInstance().getSoundSystem().playClip("click_select.wav");
    }

    private void showShipInfo(ShipTemplate template) {
        currentlySelectedShipTemplate = template;

        shipNameLabel.setText(template.getName());

        shipStatCostLabel.setText(Client.getLanguage().get("cost") + ": " + template.getCost());
        shipStatCostLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/coins.png"));

        shipStatUpkeepLAbel.setText(Client.getLanguage().get("upkeep") + ": " + template.getUpkeepCost());
        shipStatUpkeepLAbel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/upkeep.png"));

        shipStatSensorLabel.setText(Client.getLanguage().get("sensor_strength") + ": " + template.getSensorStrength());
        shipStatSensorLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/remote.png"));

        shipStatSpeedLabel.setText(Client.getLanguage().get("speed") + ": " + template.getSpeed());
        shipStatSpeedLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/run.png"));

        shipStatRangeLabel.setText(Client.getLanguage().get("range") + ": " + template.getMaxDeuterium() / template.getDeuteriumUsage());
        shipStatRangeLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/chart_line.png"));

        shipStatWeaponLabel.setText(Client.getLanguage().get("weapons") + ": " + template.getTotalWeaponStrength());
        shipStatWeaponLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/bomb.png"));

        shipStatHPLabel.setText(Client.getLanguage().get("hull") + ": " + template.getMaxHitpoints());
        shipStatHPLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/brick.png"));

        shipStatArmorLabel.setText(Client.getLanguage().get("armor") + ": " + template.getMaxArmor());
        shipStatArmorLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/shield.png"));

        shipStatShieldLabel.setText(Client.getLanguage().get("shields") + ": " + template.getMaxShield());
        shipStatShieldLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/shield.png"));

        spacedockLabel.setIcon(ImageManager.getInstance().getImage("graphics/ship_icons/45x45/" + template.getHullClass().getShipdesignerImageFileName()));

        buildShipButton.setEnabled(true);
        shipInfoButton.setEnabled(true);

        Client.getInstance().getSoundSystem().playClip("click_select.wav");
    }
}