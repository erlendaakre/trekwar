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
import com.frostvoid.trekwar.common.ShipTemplate;
import com.frostvoid.trekwar.common.exceptions.ServerCommunicationException;
import com.frostvoid.trekwar.common.exceptions.ShipException;
import com.frostvoid.trekwar.common.exceptions.SlotException;
import com.frostvoid.trekwar.common.shipComponents.*;
import com.frostvoid.trekwar.common.shipHulls.HullClass;
import com.frostvoid.trekwar.common.utils.Language;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
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
import java.util.Comparator;

/**
 * Window that lets the user view existing and create new ship templates
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ShipDesignerWindow extends JInternalFrame {

    private static final long serialVersionUID = 6452812185043406733L;
    private ShipDesignerWindow shipDesignerWindow;
    private JPanel contentPanel;
    private JPanel shipPanel;
    private JPanel statsPanel;
    private JPanel controlPanel;
    private JPanel componentDetailPanel;
    private JPanel componentListPanel;
    private JPanel componentListContentPanel;
    private JTable templateTable;
    private TransferHandler th;
    private JButton saveDesignButton;
    private ShipTemplate currentTemplate;
    private boolean unsavedChanges = false;
    private Language lang;

    public ShipDesignerWindow(String name, Icon icon, int x, int y) {
        super(name,
                false, //resizable
                true, //closable
                false, //maximizable
                false);//iconifiable

        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
        this.lang = Client.getLanguage();
        shipDesignerWindow = this;

        th = new TransferHandler() {

            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                return new StringSelection((String) c.getClientProperty("componentname"));
            }

            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
                // Do cleanup here if any
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport ts) {
                if (ts.getComponent() instanceof JLabel) {
                    if (((JLabel) ts.getComponent()).getClientProperty("slotid") != null) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport ts) {
                Transferable t = ts.getTransferable();
                JLabel target = (JLabel) ts.getComponent();
                ShipComponent c = null;
                try {
                    c = findComponent((String) (t.getTransferData(DataFlavor.stringFlavor)));
                    addComponent(target, c);
                    unsavedChanges = true;
                    return true;
                } catch (SlotException e) {
                    Client.getInstance().showError("Unable to add component " + c.getName() + " to template " + currentTemplate.getName(), e, false, false);
                } catch (UnsupportedFlavorException e) {
                    Client.getInstance().showError("Unable to add component to template", e, false, false);
                } catch (IOException e) {
                    Client.getInstance().showError("Unable to add component to template", e, false, false);
                }
                return false;
            }
        };

        setBackground(Colors.TREKWAR_BG_COLOR);

        makeGUI();
        getContentPane().add(contentPanel);
        setFrameIcon(new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(-1, 18, 0)));

        setSize(new Dimension(1100, 705));
        setLocation(x, y);
    }

    public final void makeGUI() {
        if (contentPanel == null) {
            // FIRST TIME
            contentPanel = new JPanel();
            contentPanel.setLayout(null);
            contentPanel.setBackground(Colors.TREKWAR_BG_COLOR);
            saveDesignButton = new JButton(lang.get("shipdesign_save"));
            saveDesignButton.setEnabled(false);
            saveDesignButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (hasComponent(currentTemplate, MiningLaser.class)
                            && !hasComponent(currentTemplate, Cargo.class)) {
                        JOptionPane.showInternalMessageDialog(controlPanel, lang.get("shipdesign_mining_present_cargo_missing"), lang.get("warning"), JOptionPane.WARNING_MESSAGE);
                    }

                    boolean updatingExistingTemplate = false;
                    if (Client.getInstance().getLocalUser().getShipTemplates().contains(currentTemplate)) {
                        updatingExistingTemplate = true;
                    }
                    try {
                        Client.getInstance().getComm().server_sendTemplate(currentTemplate);
                        if (!updatingExistingTemplate) {
                            Client.getInstance().getLocalUser().addShipTemplate(currentTemplate);
                        }
                        populateTemplateList();
                        unsavedChanges = false;
                        saveDesignButton.setEnabled(false);
                    } catch (ServerCommunicationException sce) {
                        Client.getInstance().showError(lang.get("shipdesign_error_saving_template") + ":\n" + sce.getMessage(), null, false, true);
                    }
                }
            });

        } else {
            contentPanel.removeAll();
        }

        selectShipTemplate(null);

        makeControlPanel();
        contentPanel.add(controlPanel);

        populateTemplateList();

        getContentPane().invalidate();
        getContentPane().repaint();
    }

    private void makeStatsPanel(ShipTemplate t) {
        if (statsPanel != null) {
            contentPanel.remove(statsPanel);
        }

        statsPanel = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                Image img = ImageManager.getInstance().getImage("graphics/shipdesigner_bottom.png").getImage();
                g.drawImage(img, 0, 0, null);
            }
        };
        statsPanel.setBounds(0, 600, 800, 79);
        statsPanel.setLayout(new GridLayout(1, 5));

        if (t != null) {
            JPanel first = new JPanel();
            first.setOpaque(false);
            first.setLayout(new BoxLayout(first, BoxLayout.Y_AXIS));
            JLabel slots = new JLabel(lang.get("shipdesign_slots") + ": " + t.getComponents().values().size() + "/" + t.getHullClass().getSlots());
            JLabel crew = new JLabel(lang.get("shipdesign_crew") + ": " + t.getHullClass().getMaxCrew());
            JLabel maintenance = new JLabel(lang.get("shipdesign_upkeep") + ": " + t.getUpkeepCost());
            JLabel cargo = new JLabel(lang.get("shipdesign_cargo") + ": " + t.getMaxCargoSpace());
            first.add(slots);
            first.add(crew);
            first.add(maintenance);
            first.add(cargo);

            JPanel second = new JPanel();
            second.setOpaque(false);
            second.setLayout(new BoxLayout(second, BoxLayout.Y_AXIS));
            JLabel deuteriumCapacity = new JLabel(lang.get("shipdesign_deuterium_capacity") + ": " + t.getMaxDeuterium());
            JLabel deuteriumUsage = new JLabel(lang.get("shipdesign_deuterium_usage") + ": " + t.getDeuteriumUsage());
            JLabel speed = new JLabel(lang.get("speed") + ": " + ((double) t.getSpeed()) / 10);
            if (t.getSpeed() < 2) {
                speed.setForeground(Colors.SHIPDESIGNER_FONT_ERROR);
            }
            JLabel range = new JLabel(lang.get("range") + ": " + t.getMaxDeuterium() / t.getDeuteriumUsage());
            second.add(deuteriumCapacity);
            second.add(deuteriumUsage);
            second.add(speed);
            second.add(range);


            JPanel third = new JPanel();
            third.setOpaque(false);
            third.setLayout(new BoxLayout(third, BoxLayout.Y_AXIS));
            JLabel shield = new JLabel(lang.get("shields") + ": " + t.getMaxShield());
            if (t.getMaxShield() < 0) {
                shield.setForeground(Colors.SHIPDESIGNER_FONT_ERROR);
            }
            JLabel armor = new JLabel(lang.get("armor") + ": " + t.getMaxArmor());
            if (t.getMaxArmor() < 0) {
                armor.setForeground(Colors.SHIPDESIGNER_FONT_ERROR);
            }
            JLabel hp = new JLabel(lang.get("shipdesign_hitpoints") + ": " + t.getMaxHitpoints());
            if (t.getMaxHitpoints() <= 0) {
                hp.setForeground(Colors.SHIPDESIGNER_FONT_ERROR);
            }
            JLabel troopCapacity = new JLabel(lang.get("shipdesign_troopcapacity") + ": " + t.getTroopCapacity());
            third.add(shield);
            third.add(armor);
            third.add(hp);
            third.add(troopCapacity);

            JPanel fourth = new JPanel();
            fourth.setOpaque(false);
            fourth.setLayout(new BoxLayout(fourth, BoxLayout.Y_AXIS));
            JLabel agility = new JLabel(lang.get("shipdesign_agility") + ": " + t.getManeuverability());
            JLabel sensors = new JLabel(lang.get("shipdesign_sensors") + ": " + t.getSensorStrength());
            JLabel signature = new JLabel(lang.get("shipdesign_signature") + ": " + t.getSignatureStrength());
            fourth.add(agility);
            fourth.add(sensors);
            fourth.add(signature);

            JPanel fifth = new JPanel();
            fifth.setOpaque(false);
            fifth.setLayout(new BoxLayout(fifth, BoxLayout.Y_AXIS));
            JLabel energy = new JLabel(lang.get("shipdesign_energy") + ": " + t.getEnergy());
            if (t.getEnergy() < 0) {
                energy.setForeground(Colors.SHIPDESIGNER_FONT_ERROR);
            }
            JLabel weapons = new JLabel(lang.get("shipdesign_weapons") + ": " + t.getTotalWeaponStrength());
            JLabel cost = new JLabel(lang.get("shipdesign_cost") + ": " + t.getCost());
            fifth.add(energy);
            fifth.add(weapons);
            fifth.add(cost);
            fifth.add(saveDesignButton);

            statsPanel.add(first);
            statsPanel.add(second);
            statsPanel.add(third);
            statsPanel.add(fourth);
            statsPanel.add(fifth);
        }
        contentPanel.add(statsPanel);
        statsPanel.validate();
        statsPanel.repaint();
        contentPanel.repaint();
    }

    private void selectShipTemplate(ShipTemplate template) {
        currentTemplate = template;
        if (shipPanel != null) {
            contentPanel.remove(shipPanel);
        }

        final String fileName = ((template != null) ? "graphics/ships/" + template.getHullClass().getShipdesignerImageFileName() : "graphics/shipdesigner_noship.png");
        shipPanel = new JPanel() {

            private static final long serialVersionUID = -21398175940212231L;

            @Override
            public void paintComponent(Graphics g) {
                Image img = ImageManager.getInstance().getImage(fileName).getImage();
                g.drawImage(img, 0, 0, null);
            }
        };
        shipPanel.setLayout(null);
        shipPanel.setBounds(0, 0, 800, 600);

        if (template != null) {
            String html = "<html>"
                    + "<h1>" + template.getName() + "</h1>"
                    + "</html>";

            JLabel textLabel = new JLabel(html);
            textLabel.setHorizontalAlignment(JLabel.LEFT);
            textLabel.setVerticalAlignment(JLabel.TOP);
            textLabel.setBounds(10, 0, 700, 500);
            shipPanel.add(textLabel);

            JLabel hullInfoButton = new JLabel(lang.get("shipdesign_hull_info"), ImageManager.getInstance().getImage("graphics/misc_icons/info.png"), SwingConstants.RIGHT);
            hullInfoButton.setHorizontalTextPosition(JLabel.LEFT);
            hullInfoButton.setBounds(680, 5, 120, 20);
            shipPanel.add(hullInfoButton);

            hullInfoButton.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    HullInfoWindow hullInfoWIndow = new HullInfoWindow(lang.get("shipdesign_hull_info"),
                            ImageManager.getInstance().getImage("graphics/ship_icons/" + currentTemplate.getHullClass().getIconFileName()),
                            200, 200, currentTemplate.getHullClass());
                    hullInfoWIndow.setVisible(true);
                    Client.getInstance().getDesktop().add(hullInfoWIndow);
                    Client.getInstance().getDesktop().moveToFront(hullInfoWIndow);
                }
            });


            // set up labels over ship slots
            for (int i : template.getHullClass().getSlotMap().keySet()) {
                final int slot = i;
                Point p = template.getHullClass().getSlotMap().get(i);
                JLabel slotLabel = new JLabel();
                slotLabel.setBounds(p.x, p.y, 39, 39);
                slotLabel.setTransferHandler(th);

                final ShipComponent c = template.getComponents().get(i);
                if (c != null) {
                    slotLabel.setIcon(ImageManager.getInstance().getImage("graphics/ship_components/" + c.getIconFileName()));
                }

                slotLabel.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            removeComponent(slot);
                            validateDesign(currentTemplate);
                            selectShipTemplate(currentTemplate);
                        }
                        selectComponent(c);

                    }
                });
                if (((String) Client.getInstance().getUserProperties().get("debug.ShipDesigner.showSlotBackground")).equals("true")) {
                    slotLabel.setBackground(Color.GREEN);
                    slotLabel.setText("" + i);
                    slotLabel.setOpaque(true);
                }

                slotLabel.putClientProperty("slotid", slot);
                shipPanel.add(slotLabel);
            }

        }

        makeStatsPanel(template);

        contentPanel.add(shipPanel);
        shipPanel.repaint();
        contentPanel.repaint();
    }

    private void makeControlPanel() {
        controlPanel = new JPanel();
        controlPanel.setBackground(Colors.TREKWAR_BG_COLOR);
        controlPanel.setBounds(800, 0, 300, 679);
        controlPanel.setLayout(null);

        templateTable = new JTable();
        templateTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane templateTableSP = new JScrollPane(templateTable);
        templateTableSP.setBounds(5, 5, 280, 120);
        controlPanel.add(templateTableSP);

        final JButton newTemplateButton = new JButton(lang.get("new"));
        JButton duplicateTemplateButton = new JButton(lang.get("shipdesign_duplicate_button"));
        JButton deleteTemplateButton = new JButton(lang.get("delete"));

        newTemplateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.YES_OPTION;
                if (unsavedChanges) {
                    answer = JOptionPane.showInternalConfirmDialog(shipDesignerWindow, lang.get("shipdesign_confirm_new_template"),
                            lang.get("shipdesign_unsaved_changes"), JOptionPane.YES_NO_OPTION);
                }
                if (answer == JOptionPane.YES_OPTION) {
                    NewTemplateDialog cDialog = new NewTemplateDialog(true);
                    String output = cDialog.getInputText();
                    cDialog.dispose();
                    if (output.length() > 3) {
                        String hull = output.substring(0, output.indexOf(":"));
                        String name = output.substring(output.indexOf(":") + 1);

                        HullClass hullClass = HullClass.getHullClassByName(hull);
                        if (hullClass == null) {
                            JOptionPane.showInternalMessageDialog(shipDesignerWindow, lang.get("shipdesign_invalid_hull_selected") + ": "
                                    + hull, lang.get("error"), JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (validateTemplateName(name)) {
                            currentTemplate = new ShipTemplate(Client.getInstance().getLocalUser(), name, hullClass);
                            unsavedChanges = true;
                            selectShipTemplate(currentTemplate);
                            templateTable.clearSelection();
                        } else {
                            JOptionPane.showInternalMessageDialog(shipDesignerWindow, lang.get("shipdesign_unique_and_3_chars_long"),
                                    lang.get("error"), JOptionPane.ERROR_MESSAGE);
                        }

                    }
                }
            }
        });

        duplicateTemplateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.YES_OPTION;
                int row = templateTable.getSelectedRow();
                if (row != -1) {
                    String template = templateTable.getModel().getValueAt(row, 0).toString();
                    if (unsavedChanges) {
                        answer = JOptionPane.showInternalConfirmDialog(shipDesignerWindow, lang.get("shipdesign_confirm_new_template"),
                                lang.get("shipdesign_unsaved_changes"), JOptionPane.YES_NO_OPTION);
                    }
                    if (answer == JOptionPane.YES_OPTION) {
                        String name = JOptionPane.showInternalInputDialog(shipDesignerWindow, lang.get("shipdesign_enter_new_template_name"),
                                lang.get("shipdesign_duplicate") + " " + template + "?", JOptionPane.QUESTION_MESSAGE);

                        if (validateTemplateName(name)) {
                            for (ShipTemplate t : Client.getInstance().getLocalUser().getShipTemplates()) {
                                if (t.getName().equalsIgnoreCase(template)) {
                                    try {
                                        currentTemplate = t.cloneTemplate(name);
                                        unsavedChanges = true;
                                        saveDesignButton.setEnabled(true);
                                        selectShipTemplate(currentTemplate);
                                        templateTable.clearSelection();
                                    } catch (ShipException se) {
                                        Client.getInstance().showError("Unable to duplicate template " + t.getName(), se, false, false);
                                    }
                                    break;
                                }
                            }
                        } else {
                            JOptionPane.showInternalMessageDialog(shipDesignerWindow, lang.get("shipdesign_error_name_unique"),
                                    lang.get("error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        deleteTemplateButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int row = templateTable.getSelectedRow();
                if (row != -1) {
                    String templateName = templateTable.getModel().getValueAt(row, 0).toString();

                    int answer = JOptionPane.showInternalConfirmDialog(shipDesignerWindow,
                            lang.get("shipdesign_confirm_delete_this_template") + "?", lang.get("shipdesign_confirm_delete"), JOptionPane.YES_NO_OPTION);
                    if (answer == JOptionPane.YES_OPTION) {
                        Client.getInstance().getComm().server_deleteTemplate(templateName);
                        Client.getInstance().getLocalUser().removeShipTemplate(Client.getInstance().getLocalUser().getShipTemplate(templateName));
                        populateTemplateList();
                        selectShipTemplate(null);
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(newTemplateButton);
        buttonPanel.add(duplicateTemplateButton);
        buttonPanel.add(deleteTemplateButton);

        buttonPanel.setBounds(5, 130, 280, 20);
        controlPanel.add(buttonPanel);

        componentDetailPanel = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                Image img = ImageManager.getInstance().getImage("graphics/shipdesigner_compdetail.png").getImage();
                g.drawImage(img, 0, 0, null);
            }
        };
        componentDetailPanel.setBounds(5, 160, 300, 115);
        componentDetailPanel.setLayout(null);

        componentListPanel = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                Image img = ImageManager.getInstance().getImage("graphics/shipdesigner_list.png").getImage();
                g.drawImage(img, 0, 0, null);
            }
        };
        componentListPanel.setBounds(5, 280, 300, 400);
        componentListPanel.setLayout(null);

        componentListContentPanel = new JPanel();
        componentListContentPanel.setOpaque(false);
        componentListContentPanel.setLayout(new BoxLayout(componentListContentPanel, BoxLayout.Y_AXIS));

        populateComponentList();

        JScrollPane componentListSP = new JScrollPane(componentListContentPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        componentListSP.setBounds(18, 20, 253, 353);
        componentListSP.setBorder(BorderFactory.createEmptyBorder());

        componentListPanel.add(componentListSP);

        controlPanel.add(componentDetailPanel);
        controlPanel.add(componentListPanel);

    }

    private void selectComponent(ShipComponent c) {
        componentDetailPanel.removeAll();

        JLabel nameLabel = new JLabel(c.getName());
        nameLabel.setBounds(15, 5, 240, 18);
        componentDetailPanel.add(nameLabel);

        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(ImageManager.getInstance().getImage("graphics/ship_components/" + c.getIconFileName()));
        iconLabel.setBounds(15, 35, 32, 32);
        componentDetailPanel.add(iconLabel);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setWrapStyleWord(true);
        detailsArea.setLineWrap(true);
        detailsArea.setEditable(false);
        detailsArea.setText(c.getDescription());
        JScrollPane detailsAreaSP = new JScrollPane(detailsArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        detailsAreaSP.setBounds(50, 35, 205, 50);
        componentDetailPanel.add(detailsAreaSP);

        JPanel selectedComponentStatsPanel = new JPanel();
        selectedComponentStatsPanel.setLayout(new BoxLayout(selectedComponentStatsPanel, BoxLayout.LINE_AXIS));
        selectedComponentStatsPanel.setBounds(50, 85, 205, 20);
        selectedComponentStatsPanel.setOpaque(false);

        JLabel powerIconLabel = new JLabel("" + c.getEnergy());
        powerIconLabel.setToolTipText(lang.get("shipdesign_power"));
        powerIconLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
        if (c.getEnergy() < 0) {
            powerIconLabel.setForeground(Colors.SHIPDESIGNER_FONT_ERROR);
        }
        powerIconLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/lightning.png"));
        powerIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        selectedComponentStatsPanel.add(powerIconLabel);

        JLabel costIconLabel = new JLabel("" + c.getCost());
        costIconLabel.setToolTipText(lang.get("shipdesign_cost"));
        costIconLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
        costIconLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/cog.png"));
        costIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        selectedComponentStatsPanel.add(costIconLabel);

        if (c instanceof Armor) {
            JLabel armorLabel = new JLabel("" + ((Armor) c).getArmor());
            armorLabel.setToolTipText(lang.get("shipdesign_armor_strength"));
            armorLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
            armorLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/shield.png"));
            armorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            selectedComponentStatsPanel.add(armorLabel);
        }

        if (c instanceof ShieldEmitter) {
            JLabel shieldLabel = new JLabel("" + ((ShieldEmitter) c).getStrength());
            shieldLabel.setToolTipText(lang.get("shipdesign_shield_strength"));
            shieldLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
            shieldLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/shield.png"));
            shieldLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            selectedComponentStatsPanel.add(shieldLabel);
        }

        if (c instanceof BeamEmitter) {
            JLabel damageLabel = new JLabel("" + ((BeamEmitter) c).getDamage());
            damageLabel.setToolTipText(lang.get("shipdesign_weapon_strength"));
            damageLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
            damageLabel.setIcon(ImageManager.getInstance().getImage("graphics/ship_components/beamemitter_16x16.png"));
            damageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            selectedComponentStatsPanel.add(damageLabel);
        }

        JLabel civilianLabel = new JLabel(lang.get("shipdesign_civilian") + ": " + (c.isCivilian() ? lang.get("yes") : lang.get("no")));
        civilianLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
        selectedComponentStatsPanel.add(civilianLabel);

        componentDetailPanel.add(selectedComponentStatsPanel);
        componentDetailPanel.repaint();
        componentDetailPanel.validate();
    }

    private void removeComponent(int slot) {
        if (currentTemplate != null) {
            currentTemplate.getComponents().remove(slot);
        }
    }

    private void populateTemplateList() {
        DefaultTableModel dtm = new DefaultTableModel() {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dtm.addColumn(lang.get("shipdesign_name"));
        dtm.addColumn(lang.get("shipdesign_hull"));
        dtm.addColumn(lang.get("shipdesign_cost"));

        for (ShipTemplate t : Client.getInstance().getLocalUser().getShipTemplates()) {
            dtm.addRow(new Object[]{t.getName(), t.getHullClass().getName(), t.getCost()});
        }
        templateTable.setModel(dtm);
        templateTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {

                    int answer = JOptionPane.YES_OPTION;
                    if (unsavedChanges) {
                        answer = JOptionPane.showInternalConfirmDialog(shipDesignerWindow, lang.get("shipdesign_move_to_new_template"),
                                lang.get("shipdesign_unsaved_changes"), JOptionPane.YES_NO_OPTION);
                    }
                    if (answer == JOptionPane.YES_OPTION) {
                        int selectedRow = lsm.getMinSelectionIndex();
                        String name = (String) templateTable.getValueAt(selectedRow, 0);
                        for (ShipTemplate t : Client.getInstance().getLocalUser().getShipTemplates()) {
                            if (t.getName().equalsIgnoreCase(name)) {
                                unsavedChanges = false;
                                saveDesignButton.setEnabled(false);
                                selectShipTemplate(t);
                            }
                        }
                    } else {
                        templateTable.clearSelection();
                    }
                }
            }
        });
    }

    public void populateComponentList() {
        componentListContentPanel.removeAll();

        ArrayList<ShipComponent> components = Client.getInstance().getLocalUser().getAvailableShipComponents();
        Collections.sort(components, new Comparator<ShipComponent>() {

            @Override
            public int compare(ShipComponent o1, ShipComponent o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });

        boolean oddEven = true;
        for (final ShipComponent c : Client.getInstance().getLocalUser().getAvailableShipComponents()) {
            final boolean dark = oddEven;
            oddEven = !oddEven;
            JPanel cPanel = new JPanel() {

                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Color ppColor = new Color(10, 10, 10, dark ? 50 : 80); //r,g,b,alpha
                    g.setColor(ppColor);
                    g.fillRect(0, 0, 235, 40); //x,y,width,height
                }
            };
            cPanel.setOpaque(false);
            cPanel.setMaximumSize(new Dimension(235, 40));
            cPanel.setMinimumSize(new Dimension(235, 40));
            cPanel.setPreferredSize(new Dimension(235, 40));
            cPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            cPanel.setLayout(null);

            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(ImageManager.getInstance().getImage("graphics/ship_components/" + c.getIconFileName()));
            iconLabel.putClientProperty("componentname", c.getName());
            iconLabel.setBounds(2, 2, 32, 32);
            iconLabel.setTransferHandler(th);
            iconLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent evt) {
                    JComponent c = (JComponent) evt.getSource();
                    TransferHandler th = c.getTransferHandler();
                    th.exportAsDrag(c, evt, TransferHandler.COPY);
                }
            });

            JLabel nameLabel = new JLabel(c.getName());
            nameLabel.setBounds(40, 0, 200, 20);

            JLabel infoLabel = new JLabel();
            infoLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/info.png"));
            infoLabel.setBounds(215, 2, 16, 16);

            JPanel cStatsPanel = new JPanel();
            cStatsPanel.setLayout(new BoxLayout(cStatsPanel, BoxLayout.LINE_AXIS));
            cStatsPanel.setBounds(40, 20, 205, 20);
            cStatsPanel.setOpaque(false);


            JLabel powerIconLabel = new JLabel("" + c.getEnergy());
            powerIconLabel.setToolTipText(lang.get("shipdesign_power"));
            powerIconLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
            if (c.getEnergy() < 0) {
                powerIconLabel.setForeground(Colors.SHIPDESIGNER_FONT_ERROR);
            }
            powerIconLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/lightning.png"));
            powerIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            cStatsPanel.add(powerIconLabel);

            JLabel costIconLabel = new JLabel("" + c.getCost());
            costIconLabel.setToolTipText(lang.get("shipdesign_cost"));
            costIconLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
            costIconLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/cog.png"));
            costIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            cStatsPanel.add(costIconLabel);

            if (c instanceof Armor) {
                JLabel armorLabel = new JLabel("" + ((Armor) c).getArmor());
                armorLabel.setToolTipText(lang.get("shipdesign_armor_strength"));
                armorLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
                armorLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/shield.png"));
                armorLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                cStatsPanel.add(armorLabel);
            }

            if (c instanceof ShieldEmitter) {
                JLabel shieldLabel = new JLabel("" + ((ShieldEmitter) c).getStrength());
                shieldLabel.setToolTipText(lang.get("shipdesign_shield_strength"));
                shieldLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
                shieldLabel.setIcon(ImageManager.getInstance().getImage("graphics/misc_icons/shield.png"));
                shieldLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                cStatsPanel.add(shieldLabel);
            }

            if (c instanceof BeamEmitter) {
                JLabel damageLabel = new JLabel("" + ((BeamEmitter) c).getDamage());
                damageLabel.setToolTipText(lang.get("shipdesign_weapon_strength"));
                damageLabel.setForeground(Colors.SHIPDESIGNER_FONT_OK);
                damageLabel.setIcon(ImageManager.getInstance().getImage("graphics/ship_components/beamemitter_16x16.png"));
                damageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                cStatsPanel.add(damageLabel);
            }

            cPanel.add(cStatsPanel);
            cPanel.add(iconLabel);
            cPanel.add(infoLabel);
            cPanel.add(nameLabel);
            componentListContentPanel.add(cPanel);


            cPanel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    selectComponent(c);
                }
            });
        }
    }

    private boolean validateDesign(ShipTemplate template) {
        saveDesignButton.setEnabled(template.isValid());
        return template.isValid();
    }

    private void addComponent(JLabel targetLabel, ShipComponent c) throws SlotException {
        int slotTarget = Integer.parseInt(targetLabel.getClientProperty("slotid").toString());
        currentTemplate.setComponent(slotTarget, c);
        unsavedChanges = true;
        selectShipTemplate(currentTemplate);
        validateDesign(currentTemplate);
    }

    private ShipComponent findComponent(String s) {
        for (ShipComponent c : Client.getInstance().getLocalUser().getAvailableShipComponents()) {
            if (c.getName().equals(s)) {
                return c;
            }
        }
        return null;
    }

    private boolean validateTemplateName(String name) {
        if (name.length() < 3) {
            return false;
        }

        for (ShipTemplate s : Client.getInstance().getLocalUser().getShipTemplates()) {
            if (s.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasComponent(ShipTemplate t, Class c) {
        for (ShipComponent sc : t.getComponents().values()) {
            if (sc.getClass().equals(c)) {
                return true;
            }
        }
        return false;
    }
}
