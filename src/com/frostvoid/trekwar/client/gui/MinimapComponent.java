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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JComponent;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.StarSystemClassification;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.client.Colors;

/**
 * Displays a minimap of the game board
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MinimapComponent extends JComponent {

    private static final int FED = 1;
    private static final int KLI = 2;
    private static final int ROM = 3;
    private static final int CAR = 4;
    private static final int DOM = 5;
    private static final int NOBODY_STAR = 100;
    private static final int NOBODY_ASTEROID = 101;
    private static final int FOG_OF_WAR = 102;
    public static final int MODE_FACTION = 0;
    public static final int MODE_POPULATION = 1;
    public static final int MODE_OWN_SYSTEMS = 2;
    private ArrayList<MinimapSystem> minimapdata;
    private int viewMode = MODE_FACTION;
    private int mapHeight = 0;
    private int mapWidth = 0;
    private int height;
    private int width;
    private double zoomlevel = 2;
    private boolean viewportEnabled;

    public MinimapComponent(final int height, final int width, boolean viewportEnabled) {
        super();
        minimapdata = new ArrayList<MinimapSystem>();
        this.height = height;
        this.width = width;
        this.viewportEnabled = viewportEnabled;

        if (viewportEnabled) {
            addMouseListener(new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent me) {
                    Rectangle view = Client.getInstance().getMapPanel().getScrollPane().getViewport().getViewRect();
                    
                    Dimension mapSize = Client.getInstance().getMapPanel().getSize();
                    
//                    System.out.println("MINIMAP CLICKED AT: " + me.getX() + "," + me.getY());                    
//                    System.out.println("MINIMAP HEIGHT/WIDTH = " + height + " / " + width);

                    double xPercent = me.getX() * (100D / height);
                    double yPercent = me.getY() * (100D / width);
                    
//                    System.out.println("CLICKS LOC AS PERCENTAGES: " + xPercent + "," + yPercent);                    
//                    System.out.println("MAIN MAP VIEW HEIGHT/WIDTH = " + view.height + " / " + view.width);                    
//                    System.out.println("MAP SIZE = " + mapSize.height + ", " + mapSize.width);

                    view.x = (int) (xPercent * (mapSize.height / 100D));
                    view.y = (int) (yPercent * (mapSize.width / 100D));
                    
//                    System.out.println("VIEW LOCATION SET TO: " + view.x + "," + view.y);
                    
                    Client.getInstance().getMapPanel().getScrollPane().getViewport().setViewPosition(new Point(view.x, view.y));
                }
            });
        }
    }

    public void setData(StarSystem[][] map) {
        minimapdata.clear();
        mapWidth = map.length;
        mapHeight = map[0].length;

        for (int i = 0; i < mapWidth; i++) {
            for (int j = 0; j < mapHeight; j++) {
                StarSystem s = map[i][j];

                // skip systems in Fog Of War
                if (Client.getInstance().getLocalUser().getSensorOverlay()[i][j] == Integer.MIN_VALUE) {
                    minimapdata.add(new MinimapSystem(j, i, FOG_OF_WAR, false));
                    continue;
                }

                if (s.getUser() != null && !s.getUser().equals(StaticData.nobodyUser)) {
                    Faction f = s.getUser().getFaction();
                    int faction = -1;
                    if (f.equals(StaticData.federation)) {
                        faction = FED;
                    }
                    if (f.equals(StaticData.klingon)) {
                        faction = KLI;
                    }
                    if (f.equals(StaticData.romulan)) {
                        faction = ROM;
                    }
                    if (f.equals(StaticData.cardassian)) {
                        faction = CAR;
                    }
                    if (f.equals(StaticData.dominion)) {
                        faction = DOM;
                    }

                    boolean ownSystem = false;
                    if (s.getUser().equals(Client.getInstance().getLocalUser())) {
                        ownSystem = true;
                    }
                    minimapdata.add(new MinimapSystem(j, i, faction, ownSystem));
                } else {
                    if (s.getStarSystemClassification().equals(StarSystemClassification.starSystem)) {
                        minimapdata.add(new MinimapSystem(j, i, NOBODY_STAR, false));
                    } else if (s.getStarSystemClassification().equals(StarSystemClassification.asteroid)) {
                        minimapdata.add(new MinimapSystem(j, i, NOBODY_ASTEROID, false));
                    }
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Colors.MINIMAP_EMPTY_SPACE);
        g2d.fillRect(0, 0, width, height);
        g2d.fillRect(0, 0, (int) (mapWidth * zoomlevel), (int) (mapHeight * zoomlevel));

        for (MinimapSystem s : minimapdata) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

            //################################ FACTION #############################
            if (viewMode == MODE_FACTION) {
                switch (s.getFaction()) {
                    case FED:
                        g2d.setColor(Colors.FEDERATION_COLOR);
                        break;
                    case KLI:
                        g2d.setColor(Colors.KLINGON_COLOR);
                        break;
                    case ROM:
                        g2d.setColor(Colors.ROMULAN_COLOR);
                        break;
                    case CAR:
                        g2d.setColor(Colors.CARDASSIAN_COLOR);
                        break;
                    case DOM:
                        g2d.setColor(Colors.DOMINION_COLOR);
                        break;
                    case NOBODY_STAR:
                        g2d.setColor(Colors.MINIMAP_UNINHABITED_SYSTEM);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                        break;
                    case NOBODY_ASTEROID:
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                        g2d.setColor(Colors.MINIMAP_ASTEROID);
                        break;
                }


                int size = 1;
                g2d.fillOval((int) (s.getX() * zoomlevel) - ((int) (size * zoomlevel) / 2),
                        (int) (s.getY() * zoomlevel) - ((int) (size * zoomlevel) / 2),
                        Math.max((int) (size * zoomlevel), 2), Math.max((int) (size * zoomlevel), 2));

            } //################################ OWN SYSTEM #############################
            else if (viewMode == MODE_OWN_SYSTEMS) {
                switch (s.getFaction()) {
                    case FED:
                        g2d.setColor(Colors.FEDERATION_COLOR);
                        break;
                    case KLI:
                        g2d.setColor(Colors.KLINGON_COLOR);
                        break;
                    case ROM:
                        g2d.setColor(Colors.ROMULAN_COLOR);
                        break;
                    case CAR:
                        g2d.setColor(Colors.CARDASSIAN_COLOR);
                        break;
                    case DOM:
                        g2d.setColor(Colors.DOMINION_COLOR);
                        break;
                    case NOBODY_STAR:
                        g2d.setColor(Colors.MINIMAP_UNINHABITED_SYSTEM);
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                        break;
                    case NOBODY_ASTEROID:
                        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                        g2d.setColor(Colors.MINIMAP_ASTEROID);
                        break;
                }

                if (!s.getOwnSystem() && s.getFaction() != NOBODY_ASTEROID && s.getFaction() != NOBODY_ASTEROID) {
                    g2d.setColor(Color.WHITE);
                } else if (s.getOwnSystem()) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                }
                int size = 1;
                g2d.fillOval((int) (s.getX() * zoomlevel) - ((int) (size * zoomlevel) / 2),
                        (int) (s.getY() * zoomlevel) - ((int) (size * zoomlevel) / 2),
                        Math.max((int) (size * zoomlevel), 2), Math.max((int) (size * zoomlevel), 2));
            }

            if (s.getFaction() == FOG_OF_WAR) {
                int size = 1;
                g2d.setColor(Colors.MINIMAP_UNEXPLORED);
                g2d.fillRect((int) (s.getX() * zoomlevel) - ((int) (size * zoomlevel) / 2),
                        (int) (s.getY() * zoomlevel) - ((int) (size * zoomlevel) / 2),
                        (int) (size * zoomlevel), (int) (size * zoomlevel));
            }
        }

        

        // Viewport
        if (viewportEnabled) {
            g2d.setColor(Colors.MINIMAP_VIEWPORT);
            Rectangle view = Client.getInstance().getMapPanel().getScrollPane().getViewport().getViewRect();
            
            int x = view.x * height / (Client.getInstance().getMapPanel().getHeight() - (Client.getInstance().getMapPanel().borderHeight*2));
            int y = view.y * width / (Client.getInstance().getMapPanel().getWidth() - (Client.getInstance().getMapPanel().borderWidth*2));
            int w = view.width * width / (Client.getInstance().getMapPanel().getWidth() - (Client.getInstance().getMapPanel().borderWidth*2));
            int h = view.height * height / (Client.getInstance().getMapPanel().getHeight() - (Client.getInstance().getMapPanel().borderHeight*2));
            g2d.drawRect(x, y, w, h);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (width * zoomlevel), (int) (height * zoomlevel));
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getSize() {
        return getPreferredSize();
    }

    public boolean zoomIn() {
        if (zoomlevel > 8) {
            return false;
        }
        zoomlevel++;
        return true;
    }

    public boolean zoomOut() {
        if (zoomlevel <= 1) {
            return false;
        }
        zoomlevel--;
        return true;
    }

    public double getZoom() {
        return zoomlevel;
    }

    public void setMode(int mode) {
        if (mode == MODE_POPULATION) {
            viewMode = MODE_POPULATION;
        }
        if (mode == MODE_FACTION) {
            viewMode = MODE_FACTION;
        }
        if (mode == MODE_OWN_SYSTEMS) {
            viewMode = MODE_OWN_SYSTEMS;
        }
        setData(Client.getInstance().getLocalMap());
    }

    public void autoZoom() {
        zoomlevel = (double)width / mapWidth;
    }
}

/**
 * A simplified and much smaller version of the StarSystem class, only
 * contains x,y + faction. Used for drawing the minimap
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
class MinimapSystem {

    private int x;
    private int y;
    private int faction;
    private boolean ownSystem;

    MinimapSystem(int x, int y, int faction, boolean ownSystem) {
        this.x = x;
        this.y = y;
        this.faction = faction;
        this.ownSystem = ownSystem;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getFaction() {
        return faction;
    }

    public boolean getOwnSystem() {
        return ownSystem;
    }
}