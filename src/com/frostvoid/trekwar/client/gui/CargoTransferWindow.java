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
import com.frostvoid.trekwar.client.model.CargoTransferDataObject;
import com.frostvoid.trekwar.client.model.CargoTransferable;
import com.frostvoid.trekwar.common.CargoClassification;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.Ship;
import com.frostvoid.trekwar.common.StarSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Window that lets user transfer cargo between ship and star system
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class CargoTransferWindow extends JInternalFrame {

    private JPanel content;
    private TransferHandler th;
    private StarSystem starSystem;

    public CargoTransferWindow(String name, Icon icon, int x, int y) {
        super(name,
                false, //resizable
                true, //closable
                false, //maximizable
                false);//iconifiable

        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);

        th = new TransferHandler() {

            @Override
            public int getSourceActions(JComponent c) {
                return TransferHandler.COPY;
            }

            @Override
            protected Transferable createTransferable(JComponent c) {
                if (c.getClientProperty("type") != null) {
                    String type = c.getClientProperty("type").toString();
                    String cargoString = c.getClientProperty("cargotype").toString();
                    CargoClassification cargo = CargoClassification.deuterium;
                    if (cargoString.equals("ore")) {
                        cargo = CargoClassification.ore;
                    }

                    if (type.equals("shipsource")) {
                        int shipid = Integer.parseInt(c.getClientProperty("shipid").toString());
                        CargoTransferDataObject ctdo = new CargoTransferDataObject(shipid, true, cargo);
                        return new CargoTransferable(ctdo);
                    }

                    if (type.equals("systemsource")) {
                        CargoTransferDataObject ctdo = new CargoTransferDataObject(-1, false, cargo);
                        return new CargoTransferable(ctdo);
                    }
                }
                return null;
            }

            @Override
            protected void exportDone(JComponent source, Transferable data, int action) {
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport ts) {
                return true; // can drop on anything handled by this transfer handler
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport ts) {
                Transferable ct = ts.getTransferable();
                try {
                    CargoTransferDataObject ctdo = (CargoTransferDataObject) ts.getTransferable().getTransferData(ct.getTransferDataFlavors()[0]);
                    int shipId = ctdo.getShipId();
                    if (!ctdo.isToSystem()) {
                        shipId = Integer.parseInt(((JComponent) ts.getComponent()).getClientProperty("shipid").toString());
                    }
                    Ship ship = starSystem.getShipById(Client.getInstance().getLocalUser(), shipId);

                    CargoClassification cargo = ctdo.getCargo();
                    int maxAmount = -1;

                    if (ctdo.isToSystem()) {
                        if (cargo.equals(CargoClassification.ore)) {
                            if (!starSystem.hasOreRefinery()) {
                                return false;
                            }
                            maxAmount = starSystem.getMaxOreStorage() - starSystem.getOre();
                            if (maxAmount > ship.getCargoOre()) {
                                maxAmount = ship.getCargoOre();
                            }
                        }
                        if (cargo.equals(CargoClassification.deuterium)) {
                            if (!starSystem.hasDeuteriumPlant()) {
                                return false;
                            }
                            maxAmount = starSystem.getMaxDeuterium() - starSystem.getDeuterium();
                            if (maxAmount > ship.getCargoDeuterium()) {
                                maxAmount = ship.getCargoDeuterium();
                            }
                        }
                    } else {
                        if (cargo.equals(CargoClassification.ore)) {
                            if (!starSystem.hasOreRefinery()) {
                                return false;
                            }
                            maxAmount = ship.getAvailableCargoSpace();
                            if (maxAmount > starSystem.getOre()) {
                                maxAmount = starSystem.getOre();
                            }
                        }
                        if (cargo.equals(CargoClassification.deuterium)) {
                            if (!starSystem.hasDeuteriumPlant()) {
                                return false;
                            }
                            maxAmount = ship.getAvailableCargoSpace();
                            if (maxAmount > starSystem.getDeuterium()) {
                                maxAmount = starSystem.getDeuterium();
                            }
                        }
                    }

                    String transferType = "";
                    if (cargo == CargoClassification.deuterium) {
                        transferType = Client.getLanguage().get("deuterium");
                    } else if (cargo == CargoClassification.ore) {
                        transferType = Client.getLanguage().get("ore");
                    }

                    TransferAmountDialog cDialog = new TransferAmountDialog(true, maxAmount,
                            Client.getLanguage().get("cargo_transfer"), transferType, starSystem.getName());
                    String output = cDialog.getInputText();
                    cDialog.dispose();

                    try {
                        int amount = Integer.parseInt(output);
                        if (amount > 0) {
                            if (ctdo.isToSystem()) {
                                Client.getInstance().getComm().server_transferCargoFromShipToSystem(starSystem, shipId, cargo, amount);
                                if (cargo.equals(CargoClassification.deuterium)) {
                                    ship.setCargoDeuterium(ship.getCargoDeuterium() - amount);
                                    starSystem.addDeuterium(amount);
                                }
                                if (cargo.equals(CargoClassification.ore)) {
                                    ship.setCargoOre(ship.getCargoOre() - amount);
                                    starSystem.addOre(amount);
                                }
                            } else {
                                Client.getInstance().getComm().server_transferCargoFromSystemToShip(starSystem, shipId, cargo, amount);
                                if (cargo.equals(CargoClassification.deuterium)) {
                                    ship.setCargoDeuterium(ship.getCargoDeuterium() + amount);
                                    starSystem.removeDeuterium(amount);
                                }
                                if (cargo.equals(CargoClassification.ore)) {
                                    ship.setCargoOre(ship.getCargoOre() + amount);
                                    starSystem.removeOre(amount);
                                }
                            }
                            Client.getInstance().getBottomGuiPanel().showFleet(ship.getFleet());
                            setSystem(starSystem);
                        }
                        return true;
                    } catch (NumberFormatException nfe) {
                        System.out.println(nfe);
                    }
                    return false;
                } catch (UnsupportedFlavorException e) {
                    System.out.println(e);
                } catch (IOException e) {
                    System.out.println(e);
                }
                return false;
            }
        };


        content = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                Image img = ImageManager.getInstance().getImage("graphics/cargotransfer.png").getImage();
                g.drawImage(img, 0, 0, null);
            }
        };
        content.setLayout(null);
        content.setBounds(0, 0, 320, 156);
        content.setPreferredSize(new Dimension(320, 156));
        content.setSize(new Dimension(320, 156));
        getContentPane().add(content);

        setFrameIcon(new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(-1, 18, 0)));
        setLocation(x, y);
        pack();
    }

    public void setSystem(final StarSystem s) {
        content.removeAll();

        starSystem = s;
        drawSystem(s);
        drawFleet(s);
        pack();
    }

    private void drawFleet(StarSystem s) {
        JPanel shipListContentPanel = new JPanel();
        shipListContentPanel.setOpaque(false);
        shipListContentPanel.setLayout(new BoxLayout(shipListContentPanel, BoxLayout.Y_AXIS));

        JScrollPane shipListSP = new JScrollPane(shipListContentPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        shipListSP.setBounds(8, 12, 177, 133);
        shipListSP.setBorder(BorderFactory.createEmptyBorder());

        content.add(shipListSP);

        ArrayList<Ship> ships = new ArrayList<Ship>();
        for (Fleet f : s.getFleets()) {
            if (f.getUser().equals(Client.getInstance().getLocalUser())) {
                for (Ship ship : f.getShips()) {
                    if (ship.canLoadUnloadCargo()) {
                        ships.add(ship);
                    }
                }
            }
        }

        boolean oddEven = true;
        for (Ship ship : ships) {
            final boolean dark = oddEven;
            oddEven = !oddEven;
            JPanel shipPanel = new JPanel() {

                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Color ppColor = new Color(10, 10, 10, dark ? 50 : 80); //r,g,b,alpha
                    g.setColor(ppColor);
                    g.fillRect(0, 0, 177, 48); //x,y,width,height
                }
            };
            shipPanel.putClientProperty("shipid", ship.getShipId());
            shipPanel.putClientProperty("type", "droptarget");
            shipPanel.setTransferHandler(th);
            shipPanel.setOpaque(false);
            shipPanel.setMaximumSize(new Dimension(177, 48));
            shipPanel.setMinimumSize(new Dimension(177, 48));
            shipPanel.setPreferredSize(new Dimension(177, 48));
            shipPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
            shipPanel.setLayout(null);

            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(ImageManager.getInstance().getImage("graphics/ship_icons/45x45/" + ship.getHullClass().getShipdesignerImageFileName()));
            iconLabel.setBounds(0, 0, 45, 45);
            shipPanel.add(iconLabel);

            JLabel deuteriumLabel = new JLabel("" + ship.getCargoDeuterium(), ImageManager.getInstance().getImage("graphics/misc_icons/deuterium.png"), SwingConstants.LEFT);
            deuteriumLabel.setBounds(50, 2, 50, 30);
            shipPanel.add(deuteriumLabel);
            if (ship.getCargoDeuterium() > 0 && starSystem.hasDeuteriumPlant()) {
                deuteriumLabel.putClientProperty("cargotype", "deuterium");
                deuteriumLabel.putClientProperty("shipid", ship.getShipId());
                deuteriumLabel.putClientProperty("type", "shipsource");
                deuteriumLabel.setTransferHandler(th);
                deuteriumLabel.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent evt) {
                        JComponent c = (JComponent) evt.getSource();
                        TransferHandler th = c.getTransferHandler();
                        th.exportAsDrag(c, evt, TransferHandler.COPY);
                    }
                });
            }


            JLabel oreLabel = new JLabel("" + ship.getCargoOre(), ImageManager.getInstance().getImage("graphics/misc_icons/mineral.png"), SwingConstants.LEFT);
            oreLabel.setBounds(100, 2, 50, 30);
            shipPanel.add(oreLabel);
            if (ship.getCargoOre() > 0 && starSystem.hasOreRefinery()) {
                oreLabel.putClientProperty("cargotype", "ore");
                oreLabel.putClientProperty("shipid", ship.getShipId());
                oreLabel.putClientProperty("type", "shipsource");
                oreLabel.setTransferHandler(th);
                oreLabel.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent evt) {
                        JComponent c = (JComponent) evt.getSource();
                        TransferHandler th = c.getTransferHandler();
                        th.exportAsDrag(c, evt, TransferHandler.COPY);
                    }
                });
            }

            SimpleBar shipCargoBar = new SimpleBar(8, 100, (int) ((100D / ship.getMaxCargoSpace()) * (ship.getCargoDeuterium() + ship.getCargoOre())), Colors.BARCOLOR_ORE, Colors.BARCOLOR_BACKGROUND, SimpleBar.Alignment.HORIZONTAL);
            shipCargoBar.setBounds(50, 38, 100, 8);
            shipPanel.add(shipCargoBar);

            shipListContentPanel.add(shipPanel);
        }
    }

    private void drawSystem(StarSystem s) {
        JPanel sysPanel = new JPanel();
        sysPanel.putClientProperty("type", "droptarget");
        sysPanel.setTransferHandler(th);
        sysPanel.setLayout(null);
        sysPanel.setOpaque(false);
        sysPanel.setBounds(200, 0, 120, 156);
        content.add(sysPanel);

        if (s.getUser().equals(Client.getInstance().getLocalUser())) {

            JLabel sysNameLabel = new JLabel(s.getName(), null, SwingConstants.CENTER);
            sysNameLabel.setBounds(5, 5, 110, 22);
            sysPanel.add(sysNameLabel);

            if (s.hasDeuteriumPlant()) {
                int deuterium = (int) ((100D / s.getMaxDeuterium()) * s.getDeuterium());
                JLabel sysDeuteriumLabel = new JLabel("" + s.getDeuterium(), ImageManager.getInstance().getImage("graphics/misc_icons/deuterium.png"), SwingConstants.LEFT);
                sysDeuteriumLabel.setBounds(10, 40, 90, 22);
                sysPanel.add(sysDeuteriumLabel);

                sysDeuteriumLabel.putClientProperty("cargotype", "deuterium");
                sysDeuteriumLabel.putClientProperty("type", "systemsource");
                sysDeuteriumLabel.setTransferHandler(th);
                sysDeuteriumLabel.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent evt) {
                        JComponent c = (JComponent) evt.getSource();
                        TransferHandler th = c.getTransferHandler();
                        th.exportAsDrag(c, evt, TransferHandler.COPY);
                    }
                });

                SimpleBar deuteriumBar = new SimpleBar(10, 90, deuterium, Colors.BARCOLOR_DEUTERIUM, Colors.BARCOLOR_BACKGROUND, SimpleBar.Alignment.HORIZONTAL);
                deuteriumBar.setToolTipText("" + s.getDeuterium() + "/" + s.getMaxDeuterium());
                deuteriumBar.setBounds(10, 62, 90, 10);
                sysPanel.add(deuteriumBar);
            }

            if (s.hasOreRefinery()) {
                int ore = (int) ((100D / s.getMaxOreStorage()) * s.getOre());
                JLabel sysOreLabel = new JLabel("" + s.getOre(), ImageManager.getInstance().getImage("graphics/misc_icons/mineral.png"), SwingConstants.LEFT);
                sysOreLabel.setBounds(10, 80, 90, 22);
                sysPanel.add(sysOreLabel);

                sysOreLabel.putClientProperty("cargotype", "ore");
                sysOreLabel.putClientProperty("type", "systemsource");
                sysOreLabel.setTransferHandler(th);
                sysOreLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent evt) {
                        JComponent c = (JComponent) evt.getSource();
                        TransferHandler th = c.getTransferHandler();
                        th.exportAsDrag(c, evt, TransferHandler.COPY);
                    }
                });

                SimpleBar oreBar = new SimpleBar(10, 90, ore, Colors.BARCOLOR_ORE, Colors.BARCOLOR_BACKGROUND, SimpleBar.Alignment.HORIZONTAL);
                oreBar.setToolTipText("" + s.getOre() + "/" + s.getMaxOreStorage());
                oreBar.setBounds(10, 102, 90, 10);
                sysPanel.add(oreBar);
            }

        } else {
            // display nothing in system panel if not in player owned system
        }
    }

    @Override
    public void removeNotify() {
        Client.getInstance().showCargoTransferWindow(false);
    }
}