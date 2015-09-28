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

import com.frostvoid.trekwar.client.Animation;
import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.orders.MoveOrder;
import com.frostvoid.trekwar.common.orders.Order;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * Panel for drawing the main game board
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MapPanel extends JPanel {

    private static final long serialVersionUID = -9777213984434116L;
    public static final int ACTION_NONE = 0;
    public static final int ACTION_MOVEFLEET = 1;
    private int nextBlackTile = 1;
    private ArrayList<Integer> tileNumbers; // for random star background
    private MouseMotionListener mapScrollerMouseListener;
    private boolean isDraggingMap = false;
    private int lastMouseX;
    private int lastMouseY;
    private MapTile lastClicked; // used to remove visible border
    private MapTile[][] mapTiles;
    private int currentAction = 0;
    private ImageIcon moveToTileGraphicsImage; // shows distance in tile, when moving (mouse over)
    private Object currentActionObject;
    private JScrollPane scrollpane;
    public int borderHeight, borderWidth;
    private ImageIcon shipIconDominion;
    private ImageIcon shipIconCardassian;
    private ImageIcon shipIconRomulan;
    private ImageIcon shipIconKlingon;
    private ImageIcon shipIconFederation;
    private ImageIcon shipIconDominionBG;
    private ImageIcon shipIconCardassianBG;
    private ImageIcon shipIconRomulanBG;
    private ImageIcon shipIconKlingonBG;
    private ImageIcon shipIconFederationBG;
    private ImageIcon shipIconDominionFriendly;
    private ImageIcon shipIconCardassianFriendly;
    private ImageIcon shipIconRomulanFriendly;
    private ImageIcon shipIconKlingonFriendly;
    private ImageIcon shipIconFederationFriendly;
    private ImageIcon shipPathIcon6;
    private ImageIcon shipPathIcon4;
    private ImageIcon shipPathIcon8;
    private ImageIcon shipPathIcon2;
    private ImageIcon shipPathIcon7;
    private ImageIcon shipPathIcon9;
    private ImageIcon shipPathIcon1;
    private ImageIcon shipPathIcon3;
    private ImageIcon shipPathIcon6_red;
    private ImageIcon shipPathIcon4_red;
    private ImageIcon shipPathIcon8_red;
    private ImageIcon shipPathIcon2_red;
    private ImageIcon shipPathIcon7_red;
    private ImageIcon shipPathIcon9_red;
    private ImageIcon shipPathIcon1_red;
    private ImageIcon shipPathIcon3_red;
    private ImageIcon shipPathIconTerminus;
    private ImageIcon shipPathIconTerminus_red;

    public MapPanel() {
        super();

        tileNumbers = new ArrayList<Integer>();
        Random prng = new Random();

        mapTiles = new MapTile[Client.getInstance().getLocalMap().length][Client.getInstance().getLocalMap()[0].length];
        for (int i = 0; i < mapTiles.length; i++) {
            for (int j = 0; j < mapTiles[i].length; j++) {
                mapTiles[i][j] = new MapTile(i, j);
                add(mapTiles[i][j]);
                tileNumbers.add(prng.nextInt(25) + 1);
            }
        }

        moveToTileGraphicsImage = new ImageIcon(new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB));

        shipIconCardassian = ImageManager.getInstance().getImage("graphics/map_icons/car.png");
        shipIconRomulan = ImageManager.getInstance().getImage("graphics/map_icons/rom.png");
        shipIconFederation = ImageManager.getInstance().getImage("graphics/map_icons/fed.png");
        shipIconKlingon = ImageManager.getInstance().getImage("graphics/map_icons/kli.png");
        shipIconDominion = ImageManager.getInstance().getImage("graphics/map_icons/dom.png");

        shipIconCardassianBG = ImageManager.getInstance().getImage("graphics/map_icons/bg_car_ship.png");
        shipIconRomulanBG = ImageManager.getInstance().getImage("graphics/map_icons/bg_rom_ship.png");
        shipIconFederationBG = ImageManager.getInstance().getImage("graphics/map_icons/bg_fed_ship.png");
        shipIconKlingonBG = ImageManager.getInstance().getImage("graphics/map_icons/bg_kli_ship.png");
        shipIconDominionBG = ImageManager.getInstance().getImage("graphics/map_icons/bg_dom_ship.png");

        shipIconCardassianFriendly = ImageManager.getInstance().getImage("graphics/map_icons/friendly_car.png");
        shipIconRomulanFriendly = ImageManager.getInstance().getImage("graphics/map_iconsfriendly_/rom.png");
        shipIconFederationFriendly = ImageManager.getInstance().getImage("graphics/map_icons/friendly_fed.png");
        shipIconKlingonFriendly = ImageManager.getInstance().getImage("graphics/map_icons/friendly_kli.png");
        shipIconDominionFriendly = ImageManager.getInstance().getImage("graphics/map_icons/friendly_dom.png");

        shipPathIcon6 = ImageManager.getInstance().getImage("graphics/map_icons/path_6.png");
        shipPathIcon4 = ImageManager.getInstance().getImage("graphics/map_icons/path_4.png");
        shipPathIcon8 = ImageManager.getInstance().getImage("graphics/map_icons/path_8.png");
        shipPathIcon2 = ImageManager.getInstance().getImage("graphics/map_icons/path_2.png");
        shipPathIcon7 = ImageManager.getInstance().getImage("graphics/map_icons/path_7.png");
        shipPathIcon9 = ImageManager.getInstance().getImage("graphics/map_icons/path_9.png");
        shipPathIcon1 = ImageManager.getInstance().getImage("graphics/map_icons/path_1.png");
        shipPathIcon3 = ImageManager.getInstance().getImage("graphics/map_icons/path_3.png");

        shipPathIcon6_red = ImageManager.getInstance().getImage("graphics/map_icons/path_6_red.png");
        shipPathIcon4_red = ImageManager.getInstance().getImage("graphics/map_icons/path_4_red.png");
        shipPathIcon8_red = ImageManager.getInstance().getImage("graphics/map_icons/path_8_red.png");
        shipPathIcon2_red = ImageManager.getInstance().getImage("graphics/map_icons/path_2_red.png");
        shipPathIcon7_red = ImageManager.getInstance().getImage("graphics/map_icons/path_7_red.png");
        shipPathIcon9_red = ImageManager.getInstance().getImage("graphics/map_icons/path_9_red.png");
        shipPathIcon1_red = ImageManager.getInstance().getImage("graphics/map_icons/path_1_red.png");
        shipPathIcon3_red = ImageManager.getInstance().getImage("graphics/map_icons/path_3_red.png");

        shipPathIconTerminus = ImageManager.getInstance().getImage("graphics/map_icons/path_end.png");
        shipPathIconTerminus_red = ImageManager.getInstance().getImage("graphics/map_icons/path_end_red.png");

        setLayout(new GridLayout(Client.getInstance().getLocalMap().length, Client.getInstance().getLocalMap()[0].length, 0, 0));

        int width = 40 * Client.getInstance().getLocalMap().length;
        int height = 40 * Client.getInstance().getLocalMap()[0].length;

        borderHeight = (Client.getInstance().getHeight() - height) / 2;
        borderWidth = (Client.getInstance().getWidth() - width) / 2;

        if (borderHeight < 100) {
            borderHeight = 100;
        }
        if (borderWidth < 50) {
            borderWidth = 50;
        }

        // map border
        Border border = new MapBorder(new Insets(borderHeight, borderWidth, borderHeight * 3, borderWidth));
        setBorder(border);


        scrollpane = new JScrollPane(this);
        scrollpane.setWheelScrollingEnabled(true);
        scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollpane.getVerticalScrollBar().setUnitIncrement(20);
        scrollpane.getHorizontalScrollBar().setUnitIncrement(20);


        mapScrollerMouseListener = new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                isDraggingMap = true;
                Point p = new Point(e.getXOnScreen(), e.getYOnScreen());
                SwingUtilities.convertPointFromScreen(p, scrollpane.getViewport());

                int deltaX = (int) p.getX() - lastMouseX;
                int deltaY = (int) p.getY() - lastMouseY;

                JViewport view = scrollpane.getViewport();
                Point pos = view.getViewPosition();
                int newX = pos.x - deltaX;
                int newY = pos.y - deltaY;
                if (newX < 0) {
                    newX = 0;
                }
                if (newY < 0) {
                    newY = 0;
                }
                view.setViewPosition(new Point(newX, newY));


                lastMouseX = (int) p.getX();
                lastMouseY = (int) p.getY();

                view.repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        };
    }

    public void drawMap() {
        nextBlackTile = 1;
        long drawMapStart = System.currentTimeMillis();
        // remove old actionlisteners and reset tiles
        for (int i = 0; i < mapTiles.length; i++) {
            for (int j = 0; j < mapTiles[i].length; j++) {
                for (MouseListener ml : mapTiles[i][j].getMouseListeners()) {
                    mapTiles[i][j].removeMouseListener(ml);
                }
                for (MouseMotionListener ml : mapTiles[i][j].getMouseMotionListeners()) {
                    mapTiles[i][j].removeMouseMotionListener(ml);
                }
                mapTiles[i][j].reset();
            }
        }

        StarSystem[][] map = Client.getInstance().getLocalMap();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                final MapTile tile = mapTiles[i][j];

                // clean up dirty ships (has 0 or lower hitpoints)
                for (int f = 0; f < map[i][j].getFleets().size(); f++) {
                    Fleet fleet = map[i][j].getFleets().get(f);
                    if (fleet.getUser() != null && fleet.getUser().equals(Client.getInstance().getLocalUser())) {
                        for (int i2 = 0; i2 < fleet.getShips().size(); i2++) {
                            Ship s = fleet.getShips().get(i2);
                            if (s.getCurrentHullStrength() <= 0) {
                                s.destroy();
                                if (fleet.getShips().contains(s)) {
                                    Client.LOG.log(Level.WARNING, "MapPanel deleted dirty ship from fleet: {0}", fleet.getName());
                                    fleet.removeShip(s);
                                    i2--;
                                }
                            }
                        }
                        // clean up dirty fleets (no ships)
                        if (fleet.getShips().isEmpty()) {
                            Client.LOG.log(Level.WARNING, "MapPanel deleted dirty fleet: {0}", fleet.getName());
                            fleet.getUser().removeFleet(fleet);
                            map[i][j].removeFleet(fleet);
                            j--;
                            continue;
                        }
                    }
                }

                if (Client.getInstance().getLocalUser().getSensorOverlay()[i][j] != Integer.MIN_VALUE) {
                    // tile has been discovered
                    if (map[i][j].getStarSystemClassification() == StarSystemClassification.empty) {
                        tile.addImage(getRandomBlackTile());
                    } else if (map[i][j].getStarSystemClassification() == StarSystemClassification.asteroid) {
                        tile.addImage(getRandomBlackTile());
                        StringTokenizer st = new StringTokenizer(map[i][j].getImageFile(), ".");
                        while (st.hasMoreTokens()) {
                            tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/asteroid" + st.nextToken() + ".png"));
                        }
                    } else if (map[i][j].getStarSystemClassification() == StarSystemClassification.nebula) {
                        tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/" + map[i][j].getImageFile()));
                    } else if (map[i][j].getStarSystemClassification() == StarSystemClassification.starSystem) {
                        tile.addImage(getRandomBlackTile());

                        // add faction background
                        Faction ownerFaction = map[i][j].getUser().getFaction();
                        if (ownerFaction == null) {
                        } else if (ownerFaction.equals(StaticData.federation)) {
                            if (ownerFaction.equals(Client.getInstance().getLocalUser().getFaction())) {
                                if (map[i][j].getUser().equals(Client.getInstance().getLocalUser())) {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_fed.png"));
                                } else {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_fed_bw.png"));
                                }
                            } else {
                                tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_fed.png"));
                            }
                        } else if (ownerFaction.equals(StaticData.klingon)) {
                            if (ownerFaction.equals(Client.getInstance().getLocalUser().getFaction())) {
                                if (map[i][j].getUser().equals(Client.getInstance().getLocalUser())) {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_kli.png"));
                                } else {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_kli_bw.png"));
                                }
                            } else {
                                tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_kli.png"));
                            }
                        } else if (ownerFaction.getName().equals("Romulan")) {
                            if (ownerFaction.equals(Client.getInstance().getLocalUser().getFaction())) {
                                if (map[i][j].getUser().equals(Client.getInstance().getLocalUser())) {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_rom.png"));
                                } else {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_rom_bw.png"));
                                }
                            } else {
                                tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_rom.png"));
                            }
                        } else if (ownerFaction.getName().equals("Cardassian")) {
                            if (ownerFaction.equals(Client.getInstance().getLocalUser().getFaction())) {
                                if (map[i][j].getUser().equals(Client.getInstance().getLocalUser())) {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_car.png"));
                                } else {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_car_bw.png"));
                                }
                            } else {
                                tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_car.png"));
                            }
                        } else if (ownerFaction.getName().equals("Dominon")) {
                            if (ownerFaction.equals(Client.getInstance().getLocalUser().getFaction())) {
                                if (map[i][j].getUser().equals(Client.getInstance().getLocalUser())) {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_dom.png"));
                                } else {
                                    tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_dom_bw.png"));
                                }
                            } else {
                                tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/bg_dom.png"));
                            }
                        }

                        // add star image
                        tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/" + map[i][j].getImageFile()));

                        // add shipyard icon
                        if (!map[i][j].getUser().equals(StaticData.nobodyUser) && map[i][j].hasShipyard()) {
                            tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/shipyard.png"));
                        }
                    }

                    // fog of war
                    if (Client.getInstance().getLocalUser().getSensorOverlay()[i][j] < 1) {
                        tile.addImage(ImageManager.getInstance().getImage("graphics/map_icons/black_transparent.png"));
                    }

                    // Add fleet icons
                    if (map[i][j].getFleets().size() > 0) {
                        addOwnFleetImages(tile, map[i][j].getFleets(), Client.getInstance().getLocalUser());
                        addAlliesFleetImages(tile, map[i][j].getFleets(), Client.getInstance().getLocalUser());
                        addEnemyFleetImages(tile, map[i][j].getFleets(), Client.getInstance().getLocalUser());
                    }
                }

                // UI MOUSE LISTENER
                final StarSystem s = map[i][j];
                tile.addMouseMotionListener(mapScrollerMouseListener);
                tile.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent me) {
                        isDraggingMap = false;
                        // used for scrolling with the mouse
                        Point p = new Point(me.getXOnScreen(), me.getYOnScreen());
                        SwingUtilities.convertPointFromScreen(p, scrollpane.getViewport());
                        lastMouseX = (int) p.getX();
                        lastMouseY = (int) p.getY();
                    }

                    @Override
                    public void mouseReleased(MouseEvent me) {

                        if (isDraggingMap) {
                            isDraggingMap = false;
                            return;
                        }

                        // Normal selection mouse click
                        if (currentAction == ACTION_NONE) {

                            // ignore clicks in LOS
                            if (Client.getInstance().getLocalUser().getSensorOverlay()[s.getX()][s.getY()] == Integer.MIN_VALUE) {
                                return;
                            }

                            if (lastClicked != null) {
                                lastClicked.setSelected(false);
                            }

                            tile.setSelected(true);
                            lastClicked = tile;
                            Client.getInstance().getBottomGuiPanel().displaySystem(s);
                        } // Fleet move order
                        else if (currentAction == ACTION_MOVEFLEET) {
                            mapTiles[s.getX()][s.getY()].removeAnimation(Animation.fleetMovementCursorAnimation);
                            Fleet f = (Fleet) currentActionObject;
                            currentAction = ACTION_NONE;
                            currentActionObject = null;
                            Client.getInstance().getSoundSystem().play_movefleet();
                            Client.getInstance().getComm().server_moveFleet(f.getName(), s.getX(), s.getY());

                            ArrayList<Fleet> localFleets = Client.getInstance().getLocalUser().getFleets();
                            for (Fleet localFleet : localFleets) {
                                if (localFleet.getName().equals(f.getName()) && f.getUser().equals(localFleet.getUser())) {
                                    localFleet.setOrder(new MoveOrder(localFleet, s.getX(), s.getY()));
                                }
                            }

                            Client.getInstance().getBottomGuiPanel().showFleet(f);

                            drawMap();
                            repaint();
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (currentAction == ACTION_MOVEFLEET) {
                            mapTiles[s.getX()][s.getY()].addAnimation(Animation.fleetMovementCursorAnimation);

                            if (moveToTileGraphicsImage.getImage().getGraphics() instanceof Graphics2D) {
                                Graphics2D g2d = (Graphics2D) moveToTileGraphicsImage.getImage().getGraphics();
                                g2d.setBackground(new Color(0, 0, 0, 0));
                                g2d.clearRect(0, 0, 40, 40);

                                g2d.setColor(Color.BLACK);
                                g2d.fillRect(0, 2, 20, 12);

                                g2d.setColor(Color.red);
                                g2d.setFont(FontFactory.getInstance().getPocketPixel(Font.BOLD, 10));
                                int srcX = ((Fleet) currentActionObject).getX();
                                int srcY = ((Fleet) currentActionObject).getY();
                                int dst = Math.max(Math.abs(srcX - s.getX()), Math.abs(srcY - s.getY()));
                                g2d.drawString("" + dst, 0, 12);
                            }
                            mapTiles[s.getX()][s.getY()].addImage(moveToTileGraphicsImage);

                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (currentAction == ACTION_MOVEFLEET) {
                            mapTiles[s.getX()][s.getY()].removeAnimation(Animation.fleetMovementCursorAnimation);
                            mapTiles[s.getX()][s.getY()].removeImage(moveToTileGraphicsImage);
                        }
                    }
                    // Draw path
//                            Fleet f = (Fleet) currentActionObject;
//                            int startX = f.getX();
//                            int startY = f.getY();
//                            ArrayList<Point> pathLabels = new ArrayList<Point>();
//                            pathLabels.add(new Point(f.getX(), f.getY())); // starting point
//                            System.out.println(" ############## start = " + f.getX() + ", " + f.getY());
//                            while (true) {
//                                if (startX > pathX) {
//                                    pathX += 1;
//                                }
//                                if (startX < pathX) {
//                                    pathX -= 1;
//                                }
//                                if (startY > pathY) {
//                                    pathY += 1;
//                                }
//                                if (startY < pathY) {
//                                    pathY -= 1;
//                                }
//                                System.out.println("... " + pathX + ", " + pathY);
//                                pathLabels.add(new Point(pathX, pathY));
//                                Collections.reverse(pathLabels);
//
//                                if (startY == pathY && startX == pathX) {
//                                    System.out.println("############# END = " + pathX + ", " + pathY + "   paths = " + pathLabels.size());
//                                    break;
//                                }
//                            }
//                            drawPath(pathLabels, f.getDeuteriumLeft(), f.getDeuteriumUsage());
//                            repaint();
//                        }
                });
            }
        }

        for (Fleet fleet : Client.getInstance().getLocalUser().getFleets()) {
            Order o = fleet.getOrder();
            if (o instanceof MoveOrder) {
                int pathX = fleet.getX();
                int pathY = fleet.getY();
                MoveOrder mo = (MoveOrder) o;
                ArrayList<Point> pathLabels = new ArrayList<Point>();
                pathLabels.add(new Point(pathX, pathY)); // starting point
                while (true) {
                    if (mo.getX() > pathX) {
                        pathX += 1;
                    }
                    if (mo.getX() < pathX) {
                        pathX -= 1;
                    }
                    if (mo.getY() > pathY) {
                        pathY += 1;
                    }
                    if (mo.getY() < pathY) {
                        pathY -= 1;
                    }

                    pathLabels.add(new Point(pathX, pathY));

                    if (mo.getY() == pathY && mo.getX() == pathX) {
                        break;
                    }
                }
                drawPath(pathLabels, fleet.getDeuteriumLeft(), fleet.getDeuteriumUsage());
            }
        }

        if (lastClicked != null) {
            lastClicked.setSelected(true);
        }

        long drawMapStop = System.currentTimeMillis();
        Client.LOG.log(Level.FINER, "Drawmap completed in: {0} MS", (drawMapStop - drawMapStart));
    }

    private void drawPath(ArrayList<Point> pathLabels, int fuel, int usage) {
        fuel += usage; // compensate for drawing line in tile fleet is already in

        for (int i = 0; i < pathLabels.size(); i++) {
            Point last = null;
            Point current = pathLabels.get(i);
            Point next = null;

            if (i > 0) {
                last = pathLabels.get(i - 1);
            }
            if (i < pathLabels.size() - 1) {
                next = pathLabels.get(i + 1);
            }

            MapTile tile = mapTiles[current.x][current.y];
            if (tile != null) {
                ImageIcon iconLast = null;
                ImageIcon iconNext = null;

                boolean outOfRange = false;
                if (fuel < usage) {
                    outOfRange = true;
                }

                if (last == null) {
                    // start of path
                    // dont draw first if going "up and left", path would then cover starship symbol
                    if (!(next.x < current.x && next.y < current.y)) {
                        iconNext = getNextPathImage(current, next, outOfRange);
                    }
                } else if (next == null) {
                    // end of path
                    if (outOfRange) {
                        iconNext = shipPathIconTerminus_red;
                    } else {
                        iconNext = shipPathIconTerminus;
                    }
                    iconLast = getNextPathImage(current, last, outOfRange);
                } else {
                    // middle of path
                    iconNext = getNextPathImage(current, next, outOfRange);
                    iconLast = getNextPathImage(current, last, outOfRange);
                }

                if (iconLast != null) {
                    tile.addImage(iconLast);
                }
                if (iconNext != null) {
                    tile.addImage(iconNext);
                }
            }
            fuel -= usage;
        }
    }

    private ImageIcon getNextPathImage(Point current, Point next, boolean outOfRange) {
        if (outOfRange) {
            if (current.x == next.x && current.y < next.y) {
                return shipPathIcon6_red;
            }
            if (current.x == next.x && current.y > next.y) {
                return shipPathIcon4_red;
            }
            if (current.y == next.y && current.x < next.x) {
                return shipPathIcon2_red;
            }
            if (current.y == next.y && current.x > next.x) {
                return shipPathIcon8_red;
            }
            if (current.x > next.x && current.y > next.y) {
                return shipPathIcon7_red;
            }
            if (current.x > next.x && current.y < next.y) {
                return shipPathIcon9_red;
            }
            if (current.y > next.y && current.x < next.x) {
                return shipPathIcon1_red;
            }
            if (current.y < next.y && current.x < next.x) {
                return shipPathIcon3_red;
            }
        } else {
            if (current.x == next.x && current.y < next.y) {
                return shipPathIcon6;
            }
            if (current.x == next.x && current.y > next.y) {
                return shipPathIcon4;
            }
            if (current.y == next.y && current.x < next.x) {
                return shipPathIcon2;
            }
            if (current.y == next.y && current.x > next.x) {
                return shipPathIcon8;
            }
            if (current.x > next.x && current.y > next.y) {
                return shipPathIcon7;
            }
            if (current.x > next.x && current.y < next.y) {
                return shipPathIcon9;
            }
            if (current.y > next.y && current.x < next.x) {
                return shipPathIcon1;
            }
            if (current.y < next.y && current.x < next.x) {
                return shipPathIcon3;
            }
        }
        return null; // current/next == same point
    }

    public ImageIcon getRandomBlackTile() {
        return ImageManager.getInstance().getImage("graphics/map_icons/bg_black_" + tileNumbers.get(nextBlackTile++) + ".png");

    }

    public void setAction(int action, Object o) {
        currentAction = action;
        currentActionObject = o;
    }

    public JScrollPane getScrollPane() {
        return scrollpane;
    }

    public MapTile getLastClickedTile() {
        return lastClicked;
    }

    public void setSelection(int x, int y) {
        lastClicked.setSelected(false);

        StarSystem s = Client.getInstance().getLocalMap()[x][y];
        if (s == null) {
            return;
        }

        if (Client.getInstance().getSystemControlWindow() != null && Client.getInstance().getSystemControlWindow().isVisible()) {
            if (s.getStarSystemClassification() == StarSystemClassification.starSystem) {
                Client.getInstance().getSystemControlWindow().setStarSystem(s);
            } else {
                Client.getInstance().getSystemControlWindow().doDefaultCloseAction();
            }
        }

        mapTiles[x][y].setSelected(true);
        lastClicked = mapTiles[x][y];
        drawMap();
    }

    private void addOwnFleetImages(MapTile tile, ArrayList<Fleet> fleets, User localUser) {
        int ownCount = 0;
        for (Fleet f : fleets) {
            if (f.getUser().equals(localUser)) {
                ownCount++;
            }
        }
        if (ownCount > 0) {
            tile.addImage(getFleetBGIconByFaction(localUser.getFaction()));
            tile.addImage(getFleetIconByFaction(localUser.getFaction()));
            if (ownCount > 1) {
                tile.addImage(MakeFleetCountIcon(localUser.getFaction(), ownCount, FleetCountType.OWN));
            }
        }
    }

    private void addAlliesFleetImages(MapTile tile, ArrayList<Fleet> fleets, User localUser) {
        int allyCount = 0;
        for (Fleet f : fleets) {
            if (!f.getUser().equals(localUser) && f.getUser().getFaction().equals(localUser.getFaction())) {
                allyCount++;
            }
        }
        if (allyCount > 0) {
            tile.addImage(getFriendlyFleetIconByFaction(localUser.getFaction()));
            if (allyCount > 1) {
                tile.addImage(MakeFleetCountIcon(localUser.getFaction(), allyCount, FleetCountType.ALLY));
            }
        }
    }

    private void addEnemyFleetImages(MapTile tile, ArrayList<Fleet> fleets, User localUser) {
        // NOTE: a tile can not hold multiple enemy factions (they would already have done combat and killed off one)
        int enemyCount = 0;
        Faction enemy = null;
        for (Fleet f : fleets) {
            if (!f.getUser().equals(localUser) && !f.getUser().getFaction().equals(localUser.getFaction())) {
                enemyCount++;
                enemy = f.getUser().getFaction();
            }
        }
        if (enemyCount > 0) {
            tile.addImage(getFleetIconByFaction(enemy));

            if (enemyCount > 1) {
                tile.addImage(MakeFleetCountIcon(localUser.getFaction(), enemyCount, FleetCountType.ENEMY));
            }
        }
    }

    private ImageIcon getFleetIconByFaction(Faction faction) {
        if (faction.equals(StaticData.federation)) {
            return shipIconFederation;
        }
        if (faction.equals(StaticData.klingon)) {
            return shipIconKlingon;
        }
        if (faction.equals(StaticData.romulan)) {
            return shipIconRomulan;
        }
        if (faction.equals(StaticData.cardassian)) {
            return shipIconCardassian;
        }
        if (faction.equals(StaticData.dominion)) {
            return shipIconDominion;
        }
        Client.getInstance().showError("Unable to get fleet icon for faction: " + faction.getName(), null, false, false);
        return shipIconFederation;
    }

    private ImageIcon getFleetBGIconByFaction(Faction faction) {
        if (faction.equals(StaticData.federation)) {
            return shipIconFederationBG;
        }
        if (faction.equals(StaticData.klingon)) {
            return shipIconKlingonBG;
        }
        if (faction.equals(StaticData.romulan)) {
            return shipIconRomulanBG;
        }
        if (faction.equals(StaticData.cardassian)) {
            return shipIconCardassianBG;
        }
        if (faction.equals(StaticData.dominion)) {
            return shipIconDominionBG;
        }
        Client.getInstance().showError("Unable to get fleet background icon for faction: " + faction.getName(), null, false, false);
        return shipIconFederationBG;
    }

    private ImageIcon getFriendlyFleetIconByFaction(Faction faction) {
        if (faction.equals(StaticData.federation)) {
            return shipIconFederationFriendly;
        }
        if (faction.equals(StaticData.klingon)) {
            return shipIconKlingonFriendly;
        }
        if (faction.equals(StaticData.romulan)) {
            return shipIconRomulanFriendly;
        }
        if (faction.equals(StaticData.cardassian)) {
            return shipIconCardassianFriendly;
        }
        if (faction.equals(StaticData.dominion)) {
            return shipIconDominionFriendly;
        }
        Client.getInstance().showError("Unable to get friendly fleet icon for faction: " + faction.getName(), null, false, false);
        return shipIconFederationFriendly;
    }

    private ImageIcon MakeFleetCountIcon(Faction faction, int count, FleetCountType type) {
        int x = 0;
        int y = 10;
        if (type.equals(FleetCountType.ALLY)) {
            y += 20;
        }

        if (faction.equals(StaticData.federation) || faction.equals(StaticData.klingon)) {
            x = 15;
        }
        if (faction.equals(StaticData.romulan)) {
            x = 19;
            y = 19;
        }
        if (faction.equals(StaticData.cardassian)) {
            x = 13;
        }
        if (faction.equals(StaticData.dominion)) {
            x = 20;
        }

        BufferedImage bi = new BufferedImage(39, 39, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Dialog", Font.BOLD, 12));
        g2d.drawString("" + count, x, y);
        g2d.dispose();

        return new ImageIcon((Image) bi);
    }

    private enum FleetCountType {
        OWN,
        ALLY,
        ENEMY
    }
}