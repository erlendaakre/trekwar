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
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.client.model.TroopTransferDataObject;
import com.frostvoid.trekwar.client.model.TroopTransferable;
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
 * Window that lets user transfer troops between ships and star systems
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TroopTransferWindow extends JInternalFrame {

    private JPanel content;
    private TransferHandler th;
    private StarSystem starSystem;

    public TroopTransferWindow(String name, Icon icon, int x, int y) {
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

                    if (type.equals("shipsource")) {
                        int shipid = Integer.parseInt(c.getClientProperty("shipid").toString());
                        TroopTransferDataObject ctdo = new TroopTransferDataObject(shipid, true);
                        return new TroopTransferable(ctdo);
                    }

                    if (type.equals("systemsource")) {
                        TroopTransferDataObject ctdo = new TroopTransferDataObject(-1, false);
                        return new TroopTransferable(ctdo);
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
                    TroopTransferDataObject ctdo = (TroopTransferDataObject) ts.getTransferable().getTransferData(ct.getTransferDataFlavors()[0]);
                    int shipId = ctdo.getShipId();
                    if (!ctdo.isToSystem()) {
                        shipId = Integer.parseInt(((JComponent) ts.getComponent()).getClientProperty("shipid").toString());
                    }
                    Ship ship = starSystem.getShipById(Client.getInstance().getLocalUser(), shipId);
                    int maxAmount = -1;

                    if (ctdo.isToSystem()) {
                        maxAmount = starSystem.getTroopCapacity() - starSystem.getTroopCount();
                        if (maxAmount > ship.getTroops()) {
                            maxAmount = ship.getTroops();
                        }
                    } else {
                        maxAmount = ship.getTroopCapacity() - ship.getTroops();
                        if (maxAmount > starSystem.getTroopCount()) {
                            maxAmount = starSystem.getTroopCount();
                        }
                    }

                    TransferAmountDialog cDialog = new TransferAmountDialog(true, maxAmount,
                            Client.getLanguage().get("troop_transfer"),
                            Client.getLanguage().get("troops"), starSystem.getName());
                    String output = cDialog.getInputText();
                    cDialog.dispose();

                    try {
                        int amount = Integer.parseInt(output);
                        if (amount > 0) {
                            if (ctdo.isToSystem()) {
                                Client.getInstance().getComm().server_transferTroopsFromShipToSystem(starSystem, shipId, amount);
                                ship.setTroops(ship.getTroops() - amount);
                                starSystem.setTroopCount(starSystem.getTroopCount() + amount);
                            } else {
                                Client.getInstance().getComm().server_transferTroopsFromSystemToShip(starSystem, shipId, amount);
                                ship.setTroops(ship.getTroops() + amount);
                                starSystem.setTroopCount(starSystem.getTroopCount() - amount);
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
                    if (ship.hasTroopTransport()) {
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

            JLabel troopLabel = new JLabel("" + ship.getTroops(), ImageManager.getInstance().getImage("graphics/misc_icons/transfertroops.png"), SwingConstants.LEFT);
            troopLabel.setBounds(50, 2, 50, 30);
            shipPanel.add(troopLabel);
            if (ship.getTroops() > 0) {
                troopLabel.putClientProperty("shipid", ship.getShipId());
                troopLabel.putClientProperty("type", "shipsource");
                troopLabel.setTransferHandler(th);
                troopLabel.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent evt) {
                        JComponent c = (JComponent) evt.getSource();
                        TransferHandler th = c.getTransferHandler();
                        th.exportAsDrag(c, evt, TransferHandler.COPY);
                    }
                });
            }

            Color barTroopColor = new Color(30, 188, 67);
            SimpleBar shipTroopBar = new SimpleBar(8, 100, (int) ((100D / ship.getTroopCapacity()) * ship.getTroops()), barTroopColor, Color.darkGray, SimpleBar.Alignment.HORIZONTAL);
            shipTroopBar.setBounds(50, 38, 100, 8);
            shipPanel.add(shipTroopBar);

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

            Color barColor = new Color(85, 146, 31);

            JLabel sysNameLabel = new JLabel(s.getName(), null, SwingConstants.CENTER);
            sysNameLabel.setBounds(5, 5, 110, 22);
            sysPanel.add(sysNameLabel);

            int troops = (int) ((100D / s.getTroopCapacity()) * s.getTroopCount());
            JLabel sysTroopLabel = new JLabel("" + s.getTroopCount(), ImageManager.getInstance().getImage("graphics/misc_icons/transfertroops.png"), SwingConstants.LEFT);
            sysTroopLabel.setBounds(10, 40, 90, 22);
            sysPanel.add(sysTroopLabel);

            sysTroopLabel.putClientProperty("type", "systemsource");
            sysTroopLabel.setTransferHandler(th);
            sysTroopLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent evt) {
                    JComponent c = (JComponent) evt.getSource();
                    TransferHandler th = c.getTransferHandler();
                    th.exportAsDrag(c, evt, TransferHandler.COPY);
                }
            });

            SimpleBar troopBar = new SimpleBar(10, 90, troops, barColor, Color.DARK_GRAY, SimpleBar.Alignment.HORIZONTAL);
            troopBar.setToolTipText("" + s.getTroopCount() + "/" + s.getTroopCapacity());
            troopBar.setBounds(10, 62, 90, 10);
            sysPanel.add(troopBar);
        } else {
            // display nothing in system panel if not in player owned system
        }
    }

    @Override
    public void removeNotify() {
        Client.getInstance().showTroopTransferWindow(false);
    }
}