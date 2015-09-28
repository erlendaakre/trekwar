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
import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.client.gui.SelfDestructDialog;
import com.frostvoid.trekwar.client.gui.ShipInfoWindow;
import com.frostvoid.trekwar.client.gui.SimpleBar;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.Ship;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.exceptions.NotUniqueException;
import com.frostvoid.trekwar.common.exceptions.ValidationException;
import com.frostvoid.trekwar.common.utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;

/**
 * Shows icons + bars for all the ships in a fleet.
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class FleetShipsPanelShipComponent extends JPanel {

    private static final int BAR_WIDTH = 6;
    private static final int BAR_HEIGHT = 45;
    private static final int BAR_SPACE = 3;

    public FleetShipsPanelShipComponent(final Ship ship) {
        setLayout(null);

        setSize(150, 45);

        JLabel iconLabel = new JLabel(new ImageIcon("graphics/ship_icons/45x45/" + ship.getHullClass().getShipdesignerImageFileName()));
        System.out.println(" FILE = " + "graphics/ship_icons/45x45/" + ship.getHullClass().getIconFileName());
        iconLabel.setBounds(0, 0, 45, 45);
        add(iconLabel);

        int barX = 48;

        int shield_percent = (int) ((100D / ship.getMaxShield()) * ship.getCurrentShieldStrength());
        SimpleBar shieldBar = new SimpleBar(BAR_HEIGHT, BAR_WIDTH, shield_percent, Colors.BARCOLOR_SHIELD, Colors.BARCOLOR_BACKGROUND, SimpleBar.Alignment.VERTICAL);
        shieldBar.setBounds(barX, 0, BAR_WIDTH, BAR_HEIGHT);
        shieldBar.setToolTipText(Client.getLanguage().getU("shields") + ": " + ship.getCurrentShieldStrength() + " / " + ship.getMaxShield());
        add(shieldBar);
        barX += BAR_WIDTH + BAR_SPACE;

        int armor_percent = (int) ((100D / ship.getMaxArmor()) * ship.getCurrentArmorStrength());
        SimpleBar armorBar = new SimpleBar(BAR_HEIGHT, BAR_WIDTH, armor_percent, Colors.BARCOLOR_ARMOR, Colors.BARCOLOR_BACKGROUND, SimpleBar.Alignment.VERTICAL);
        armorBar.setBounds(barX, 0, BAR_WIDTH, BAR_HEIGHT);
        armorBar.setToolTipText(Client.getLanguage().getU("armor") + ": " + ship.getCurrentArmorStrength() + " / " + ship.getMaxArmor());
        add(armorBar);
        barX += BAR_WIDTH + BAR_SPACE;

        int hp_percent = (int) ((100D / ship.getMaxHitpoints()) * ship.getCurrentHullStrength());
        SimpleBar hpBar = new SimpleBar(BAR_HEIGHT, BAR_WIDTH, hp_percent, Colors.BARCOLOR_HITPOINTS, Colors.BARCOLOR_BACKGROUND, SimpleBar.Alignment.VERTICAL);
        hpBar.setBounds(barX, 0, BAR_WIDTH, BAR_HEIGHT);
        hpBar.setToolTipText(Client.getLanguage().getU("hitpoints") + ": " + ship.getCurrentHullStrength() + " / " + ship.getMaxHitpoints());
        add(hpBar);
        barX += BAR_WIDTH + BAR_SPACE;

        int fuel_percent = (int) ((100D / ship.getMaxDeuterium()) * ship.getCurrentDeuterium());
        SimpleBar fuelBar = new SimpleBar(BAR_HEIGHT, BAR_WIDTH, fuel_percent, Colors.BARCOLOR_FUEL, Colors.BARCOLOR_BACKGROUND, SimpleBar.Alignment.VERTICAL);
        fuelBar.setBounds(barX, 0, BAR_WIDTH, BAR_HEIGHT);
        fuelBar.setToolTipText(Client.getLanguage().get("fuel") + ": " + ship.getCurrentDeuterium() + " / " + ship.getMaxDeuterium());
        add(fuelBar);
        barX += BAR_WIDTH + BAR_SPACE;

        if (ship.canLoadUnloadCargo()) {
            // if cargo space is full, no background for ore/deuterium bar
            int spaceLeft = ship.getMaxCargoSpace() - (ship.getCargoDeuterium() + ship.getCargoOre());
            Color barBackground = Color.DARK_GRAY;
            if (spaceLeft < 1) {
                barBackground = new Color(0, 0, 0, 0); // black, 100% transparent
            }

            barX += BAR_SPACE;
            SimpleBar oreBar = new SimpleBar(BAR_HEIGHT, BAR_WIDTH, (int) ((100D / ship.getMaxCargoSpace()) * ship.getCargoOre()), Colors.BARCOLOR_ORE, barBackground, SimpleBar.Alignment.VERTICAL);
            oreBar.setToolTipText(Client.getLanguage().get("ore") + ": " + ship.getCargoOre() + " / " + ship.getMaxCargoSpace());
            oreBar.setBounds(barX, 0, BAR_WIDTH, BAR_HEIGHT);
            add(oreBar);
            barX += BAR_WIDTH + BAR_SPACE;

            SimpleBar deuteriumBar = new SimpleBar(BAR_HEIGHT, BAR_WIDTH, (int) ((100D / ship.getMaxCargoSpace()) * ship.getCargoDeuterium()), Colors.BARCOLOR_DEUTERIUM, barBackground, SimpleBar.Alignment.VERTICAL);
            deuteriumBar.setToolTipText(Client.getLanguage().get("deuterium") + ": " + ship.getCargoDeuterium() + " / " + ship.getMaxCargoSpace());
            deuteriumBar.setBounds(barX, 0, BAR_WIDTH, BAR_HEIGHT);
            add(deuteriumBar);
            barX += BAR_WIDTH + BAR_SPACE;
        }

        if (ship.hasTroopTransport()) {
            barX += BAR_SPACE;
            SimpleBar shipTroopBar = new SimpleBar(BAR_HEIGHT, BAR_WIDTH, (int) ((100D / ship.getTroopCapacity()) * ship.getTroops()), Colors.BARCOLOR_TROOPS, Color.DARK_GRAY, SimpleBar.Alignment.VERTICAL);
            shipTroopBar.setToolTipText(Client.getLanguage().get("troops") + ": " + ship.getTroops() + " / " + ship.getTroopCapacity());
            shipTroopBar.setBounds(barX, 0, BAR_WIDTH, BAR_HEIGHT);
            add(shipTroopBar);
        }

        final FleetShipsPanelShipComponent instance = this;


        // Add popup menu for own fleets (move ships between fleets, ship info)
        if (ship.getFleet().getUser().equals(Client.getInstance().getLocalUser())) {
            iconLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), e.getComponent());

                    JPopupMenu popup = new JPopupMenu();

                    JMenuItem info = new JMenuItem(Client.getLanguage().get("ship_info"));
                    JMenu moveToFleet = new JMenu(Client.getLanguage().get("move_to_fleet"));
                    JMenuItem decommission = new JMenuItem(Client.getLanguage().get("decommission"));
                    JMenuItem destroy = new JMenuItem(Client.getLanguage().get("self_destruct"));

                    ActionListener moveToFleetActionListener = new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // moving ship to a new fleet
                            if (e.getActionCommand().equals("_________________________newfleet_________________")) {
                                String newFleetName = JOptionPane.showInternalInputDialog(instance, Client.getLanguage().get("name_of_the_new_fleet"));
                                newFleetName = newFleetName.toLowerCase().trim();
                                try {
                                    // new fleet name is OK
                                    if (Validator.validateFleetName(newFleetName, Client.getInstance().getLocalUser())) {
                                        Fleet oldFleet = ship.getFleet();

                                        Client.getInstance().getComm().server_newFleet(newFleetName, oldFleet.getX(), oldFleet.getY());
                                        Client.getInstance().getComm().server_moveShipToFleet(oldFleet, ship, newFleetName);

                                        // update local content
                                        StarSystem starsystem = Client.getInstance().getLocalMap()[ship.getFleet().getX()][ship.getFleet().getY()];


                                        Fleet newFleet = new Fleet(Client.getInstance().getLocalUser(), newFleetName, starsystem);
                                        newFleet.addShip(ship);

                                        try {
                                            starsystem.addFleet(newFleet);
                                            Client.getInstance().getLocalUser().addFleet(newFleet);
                                            oldFleet.removeShip(ship);
                                        } catch (NotUniqueException ex) {
                                            Client.LOG.log(Level.SEVERE, "Failed to add fleet {0} to user, even though name has been validated", newFleet.getName());
                                        }


                                        if (oldFleet.getShips().size() > 1) {
                                            Client.getInstance().getBottomGuiPanel().showFleet(oldFleet);
                                        } else {
                                            Client.getInstance().getBottomGuiPanel().showFleet(newFleet);
                                        }
                                        Client.getInstance().getBottomGuiPanel().getToolBar().populateTabs(starsystem);

                                    } else {
                                        throw new ValidationException("UNKNOWN VALIDATION EXCEPTION");
                                    }
                                } catch (ValidationException ex) {
                                    Client.getInstance().showMessage(Client.getLanguage().getU(ex.getMessage()));
                                }
                            } // Moving fleet to existing fleet in same starsystem
                            else {
                                String targetFleetName = e.getActionCommand();
                                Fleet targetFleet = Client.getInstance().getLocalUser().getFleetByName(targetFleetName);
                                Fleet oldFleet = ship.getFleet();

                                if (targetFleet != null) {
                                    StarSystem starsystem = Client.getInstance().getLocalMap()[oldFleet.getX()][oldFleet.getY()];
                                    Client.getInstance().getComm().server_moveShipToFleet(oldFleet, ship, targetFleetName);

                                    // update local content
                                    targetFleet.addShip(ship);
                                    oldFleet.removeShip(ship);

                                    if (oldFleet.getShips().size() > 1) {
                                        Client.getInstance().getBottomGuiPanel().showFleet(oldFleet);
                                    } else {
                                        Client.getInstance().getBottomGuiPanel().showFleet(targetFleet);
                                    }
                                    Client.getInstance().getBottomGuiPanel().getToolBar().populateTabs(starsystem);
                                }
                            }
                        }
                    };


                    // make list of all fleets in same system
                    Fleet fleet = ship.getFleet();
                    StarSystem starsystem = Client.getInstance().getLocalMap()[fleet.getX()][fleet.getY()];

                    for (Fleet f : starsystem.getFleets()) {
                        // cant move ship to the fleet it's currently in, or enemy fleet
                        if (!f.equals(fleet) || !fleet.getUser().equals(Client.getInstance().getLocalUser())) {
                            JMenuItem fleet_item = new JMenuItem(f.getName());
                            fleet_item.setActionCommand(f.getName());
                            fleet_item.addActionListener(moveToFleetActionListener);
                            moveToFleet.add(fleet_item);
                        }
                    }

                    JMenuItem fleet_new = new JMenuItem(Client.getLanguage().get("new"));
                    fleet_new.setActionCommand("_________________________newfleet_________________");
                    fleet_new.addActionListener(moveToFleetActionListener);
                    moveToFleet.add(fleet_new);

                    info.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ShipInfoWindow shipInfoWIndow = new ShipInfoWindow(Client.getLanguage().get("fleetbox_shipinfo"), ImageManager.getInstance().getImage("graphics/ship_icons/" + ship.getHullClass().getIconFileName()), 200, 200, ship);
                            shipInfoWIndow.setVisible(true);
                            Client.getInstance().getDesktop().add(shipInfoWIndow);
                            Client.getInstance().getDesktop().moveToFront(shipInfoWIndow);
                        }
                    });

                    popup.add(new JMenuItem(ship.getName()));
                    popup.addSeparator();
                    popup.add(info);
                    popup.add(moveToFleet);

                    popup.addSeparator();

                    ActionListener destroyDecommissionListener = new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SelfDestructDialog destructDialog = new SelfDestructDialog(true, ship);
                            if (destructDialog.doSelfDestruct()) {
                                if (Client.getInstance().getComm().server_decommissionDestroyShip(ship)) {
                                    Fleet fleet = ship.getFleet();
                                    int numberOfShips = fleet.getShips().size();
                                    ship.destroy();
                                    Client.getInstance().getSoundSystem().play_yes();
                                    Client.getInstance().getMapPanel().drawMap();
                                    Client.getInstance().getMapPanel().repaint();

                                    if (numberOfShips > 1) {
                                        Client.getInstance().getBottomGuiPanel().showFleet(fleet);
                                    } else {
                                        Client.getInstance().getBottomGuiPanel().updateToolbar();
                                        Client.getInstance().getBottomGuiPanel().showStarsystem();
                                    }
                                }
                            }
                            destructDialog.dispose();
                        }
                    };

                    if (starsystem.getUser().equals(Client.getInstance().getLocalUser())) {
                        popup.add(decommission);
                        decommission.addActionListener(destroyDecommissionListener);
                    } else {
                        popup.add(destroy);
                        destroy.addActionListener(destroyDecommissionListener);
                    }

                    popup.show(e.getComponent(), pt.x, pt.y);
                }
            });
        }
    }

    @Override
    public void paint(Graphics g) {
        paintComponents(g);
    }
}