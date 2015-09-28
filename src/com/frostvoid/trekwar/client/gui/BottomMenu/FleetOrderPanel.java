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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.gui.InvasionConfirmationDialog;
import com.frostvoid.trekwar.common.exceptions.InvalidOrderException;
import com.frostvoid.trekwar.common.exceptions.ServerCommunicationException;
import com.frostvoid.trekwar.common.orders.ColonizeOrder;
import com.frostvoid.trekwar.common.orders.HarvestDeuteriumOrder;
import com.frostvoid.trekwar.common.orders.MiningOrder;
import com.frostvoid.trekwar.common.orders.OrbitalBombardmentOrder;
import com.frostvoid.trekwar.client.gui.MapPanel;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.Ship;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.StarSystemClassification;
import com.frostvoid.trekwar.common.StaticData;

/**
 * Holds all the order buttons for a given Fleet
 * 
 * @author Erlend Aakre
 */
public class FleetOrderPanel extends JPanel {

    private static final int buttonWidth = 85;
    private static final int buttonHeight = 20;
    private static final Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);
    private static final int buttonSpacing = 2;

    public FleetOrderPanel() {
        BoxLayout layout = new BoxLayout(this, SwingConstants.VERTICAL);
        setLayout(layout);

    }

    public void empty() {
        removeAll();
    }

    public void setFleet(final Fleet fleet) {
        empty();
        final StarSystem system = Client.getInstance().getLocalMap()[fleet.getX()][fleet.getY()];


        // MOVE
        JButton moveOrder = new JButton(Client.getLanguage().getU("move_fleet"));
        moveOrder.setPreferredSize(buttonSize);
        moveOrder.setMinimumSize(buttonSize);
        moveOrder.setMaximumSize(buttonSize);
        moveOrder.setToolTipText(Client.getLanguage().get("move_this_fleet_to_another_system"));
        moveOrder.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Client.getInstance().getMapPanel().setAction(MapPanel.ACTION_MOVEFLEET, fleet);                
            }
        });
        add(moveOrder);

        if (buttonSpacing > 0) {
            add(Box.createVerticalStrut(buttonSpacing));
        }

        // COLONIZE
        if (fleet.canColonize()) {
            JButton colonizeOrder = new JButton(Client.getLanguage().getU("colonize"));

            colonizeOrder.setEnabled(system.getStarSystemClassification() == StarSystemClassification.starSystem && system.getUser().equals(StaticData.nobodyUser));
            
            // don't show colnize button as enabled if fleet is already colonizing
            if(fleet.getOrder() instanceof ColonizeOrder) {
                colonizeOrder.setEnabled(false);
            }

            colonizeOrder.setPreferredSize(buttonSize);
            colonizeOrder.setMinimumSize(buttonSize);
            colonizeOrder.setMaximumSize(buttonSize);
            colonizeOrder.setToolTipText(Client.getLanguage().get("start_a_new_colony_in_this_system"));
            colonizeOrder.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        // update local content
                        try {
                            Ship colonyShip = null;
                            for (Ship s : fleet.getShips()) {
                                if (s.canColonize()) {
                                    colonyShip = s;
                                    break;
                                }
                            }
                            ColonizeOrder co = new ColonizeOrder(fleet.getUser(), system, fleet, colonyShip);
                            fleet.setOrder(co);
                            
                            Client.getInstance().getMapPanel().drawMap();
                            Client.getInstance().getMapPanel().repaint();
                            
                        } catch (InvalidOrderException ex) {
                            Logger.getLogger(FleetOrderPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        // notify server
                        Client.getInstance().getComm().server_colonize(fleet.getName());
                        
                        // play sound + update UI
                        Client.getInstance().getSoundSystem().play_colonizeSystem();
                        Client.getInstance().getBottomGuiPanel().showFleet(fleet);
                    } catch (ServerCommunicationException sce) {
                        Client.getInstance().showMessage(sce.getMessage());
                        Client.getInstance().getSoundSystem().play_no();
                    }
                }
            });
            add(colonizeOrder);
            if (buttonSpacing > 0) {
                add(Box.createVerticalStrut(buttonSpacing));
            }
        }


        // MINE ORE
        if (fleet.canMine()) {
            JButton mineOrder = new JButton(Client.getLanguage().getU("mine"));
            mineOrder.setEnabled(system.getStarSystemClassification() == StarSystemClassification.asteroid && (fleet.getMaxCargoSpace() > fleet.getCargoDeuterium() + fleet.getCargoOre()));
            mineOrder.setPreferredSize(buttonSize);
            mineOrder.setMinimumSize(buttonSize);
            mineOrder.setMaximumSize(buttonSize);
            mineOrder.setToolTipText(Client.getLanguage().get("mine_asteroid_field_for_resources"));
            mineOrder.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // update local data
                    try {
                        MiningOrder mo = new MiningOrder(fleet.getUser(), system, fleet);
                        fleet.setOrder(mo);
                        Client.getInstance().getMapPanel().drawMap();
                        Client.getInstance().getMapPanel().repaint();
                    } catch (InvalidOrderException ex) {
                        Logger.getLogger(FleetOrderPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    Client.getInstance().getComm().server_mine(fleet.getName());
                    Client.getInstance().getSoundSystem().play_miningAsteroid();
                    Client.getInstance().getBottomGuiPanel().showFleet(fleet);
                }
            });
            add(mineOrder);
            if (buttonSpacing > 0) {
                add(Box.createVerticalStrut(buttonSpacing));
            }
        }


        // HARVEST DEUTERIUM (TO CARGO BAY)
        if (fleet.canHarvestDeuterium()) {
            JButton harvestOrder = new JButton(Client.getLanguage().getU("harvest"));
            harvestOrder.setEnabled(system.getStarSystemClassification() == StarSystemClassification.nebula && (fleet.getMaxCargoSpace() > fleet.getCargoDeuterium() + fleet.getCargoOre()));
            harvestOrder.setPreferredSize(buttonSize);
            harvestOrder.setMinimumSize(buttonSize);
            harvestOrder.setMaximumSize(buttonSize);
            harvestOrder.setToolTipText(Client.getLanguage().get("harvest_raw_deuterium"));
            harvestOrder.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        // update local content
                        HarvestDeuteriumOrder hdo = new HarvestDeuteriumOrder(fleet.getUser(), system, fleet);
                        fleet.setOrder(hdo);
                        Client.getInstance().getMapPanel().drawMap();
                        Client.getInstance().getMapPanel().repaint();
                    } catch (InvalidOrderException ex) {
                        Logger.getLogger(FleetOrderPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    Client.getInstance().getComm().server_harvestDeuterium(fleet.getName());
                    Client.getInstance().getBottomGuiPanel().showFleet(fleet);
                }
            });
            add(harvestOrder);
            if (buttonSpacing > 0) {
                add(Box.createVerticalStrut(buttonSpacing));
            }
        }


        // TRANSFER CARGO
        if (fleet.canLoadUnloadCargo()) {
            JButton cargoOrder = new JButton(Client.getLanguage().getU("cargo"));
            cargoOrder.setEnabled(system.getUser().equals(Client.getInstance().getLocalUser()));
            cargoOrder.setPreferredSize(buttonSize);
            cargoOrder.setMinimumSize(buttonSize);
            cargoOrder.setMaximumSize(buttonSize);
            cargoOrder.setToolTipText(Client.getLanguage().get("transfer_cargo_between_ship_and_system"));
            cargoOrder.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Client.getInstance().getCargoTransferWindow().setSystem(system);
                    Client.getInstance().showCargoTransferWindow(true);
                }
            });
            add(cargoOrder);
            if (buttonSpacing > 0) {
                add(Box.createVerticalStrut(buttonSpacing));
            }
        }


        // TRANSFER TROOPS
        if (fleet.canTransportTroops()) {
            JButton troopTransferOrder = new JButton(Client.getLanguage().getU("troops"));
            troopTransferOrder.setEnabled(system.getUser().equals(Client.getInstance().getLocalUser()));
            troopTransferOrder.setPreferredSize(buttonSize);
            troopTransferOrder.setMinimumSize(buttonSize);
            troopTransferOrder.setMaximumSize(buttonSize);
            troopTransferOrder.setToolTipText(Client.getLanguage().get("transfer_troops_between_ship_and_system"));
            troopTransferOrder.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Client.getInstance().getTroopTransferWindow().setSystem(system);
                    Client.getInstance().showTroopTransferWindow(true);
                }
            });
            add(troopTransferOrder);
            if (buttonSpacing > 0) {
                add(Box.createVerticalStrut(buttonSpacing));
            }
        }


        // INVADE SYSTEM
        if (fleet.canTransportTroops() && !system.getUser().getFaction().equals(Client.getInstance().getLocalUser().getFaction())
                && !system.getUser().equals(StaticData.nobodyUser)) {
            JButton invadeOrder = new JButton(Client.getLanguage().get("invade"));
            invadeOrder.setPreferredSize(buttonSize);
            invadeOrder.setMinimumSize(buttonSize);
            invadeOrder.setMaximumSize(buttonSize);
            invadeOrder.setToolTipText(Client.getLanguage().get("invade_starsystem"));
            invadeOrder.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    InvasionConfirmationDialog icd = new InvasionConfirmationDialog(true, fleet, system);
                    if (icd.doInvade()) {
                        Client.getInstance().getSoundSystem().play_invadeSystem();
                        Client.getInstance().getComm().server_invade(fleet.getName());
                        Client.getInstance().getBottomGuiPanel().showFleet(fleet);
                    }
                    icd.dispose();
                }
            });
            add(invadeOrder);
            Client.getInstance().getMapPanel().drawMap();
            Client.getInstance().getMapPanel().repaint();
            if (buttonSpacing > 0) {
                add(Box.createVerticalStrut(buttonSpacing));
            }
        }
        
        
        // ORBITAL BOMBARDMENT
        if (fleet.canBombPlanets() && !system.getUser().getFaction().equals(Client.getInstance().getLocalUser().getFaction())
                && !system.getUser().equals(StaticData.nobodyUser)) {
            JButton bombOrder = new JButton(Client.getLanguage().get("bomb"));
            bombOrder.setPreferredSize(buttonSize);
            bombOrder.setMinimumSize(buttonSize);
            bombOrder.setMaximumSize(buttonSize);
            bombOrder.setToolTipText(Client.getLanguage().get("bomb_starsystem"));
            bombOrder.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
//                    InvasionConfirmationDialog icd = new InvasionConfirmationDialog(true, fleet, system);
//                    if (icd.doInvade()) {
                    try {
                        OrbitalBombardmentOrder obo = new OrbitalBombardmentOrder(fleet, system);
                        
                        if(Client.getInstance().getComm().server_bombSystem(fleet.getName())) {
                            fleet.setOrder(obo);
                            Client.getInstance().getSoundSystem().play_orbitalBombardment();
                            Client.getInstance().getBottomGuiPanel().showFleet(fleet);
                        }
                        else {
                            Client.LOG.severe("server_bombSystem in client communication returned false");
                        }
                    }
                    catch(InvalidOrderException ioe) {
                        Client.LOG.log(Level.SEVERE, "Invalid bomb system order: {0}", ioe.getMessage());
                    }
//                    }
//                    icd.dispose();
                }
            });
            add(bombOrder);
            Client.getInstance().getMapPanel().drawMap();
            Client.getInstance().getMapPanel().repaint();
            if (buttonSpacing > 0) {
                add(Box.createVerticalStrut(buttonSpacing));
            }
        }
        
        
        
        // CANCEL ORDERS
        if (fleet.getOrder() != null) {
            JButton cancelOrder = new JButton(Client.getLanguage().getU("cancel"));
            cancelOrder.setPreferredSize(buttonSize);
            cancelOrder.setMinimumSize(buttonSize);
            cancelOrder.setMaximumSize(buttonSize);
            cancelOrder.setToolTipText(Client.getLanguage().get("cancel_current_order_for_this_fleet"));
            cancelOrder.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(Client.getInstance().getComm().server_cancelFleetOrders(fleet.getName())) {
                        Client.getInstance().getSoundSystem().play_cancelFleetOrders();
                        fleet.setOrder(null);
                        Client.getInstance().getBottomGuiPanel().showFleet(fleet);
                        Client.getInstance().getMapPanel().drawMap();
                        Client.getInstance().getMapPanel().repaint();                        
                    }
                }
            });
            add(cancelOrder);

            if (buttonSpacing > 0) {
                add(Box.createVerticalStrut(buttonSpacing));
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        paintComponents(g);
    }
}