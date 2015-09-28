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

import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.Ship;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * The area that holds all the ship panels for a single fleet
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class FleetShipsPanel extends JPanel {

    private Dimension size;

    public FleetShipsPanel() {
        setLayout(null);
    }

    public void setFleet(Fleet f) {
        removeAll();

        int i = 0;
        int x = 0;
        int y = 3;

        ArrayList<Ship> ships = f.getShips();
        Comparator<Ship> shipSorter = new Comparator<Ship>() {

            @Override
            public int compare(Ship o1, Ship o2) {
                int s1 = sum(o1);
                int s2 = sum(o2);
                if (s1 < s2) return 1;
                if (s1 > s2) return -1;
                return 0;
            }

            public int sum(Ship s) {
                int sum = 0;
                sum += s.getTroops() * 5000;
                sum += (s.getCargoDeuterium() + s.getCargoOre() * 10);
                sum += s.getCost();
                sum += s.getShipId();
                return sum;
            }
        };

        Collections.sort(ships, shipSorter);

        for (Ship ship : ships) {
            FleetShipsPanelShipComponent b = new FleetShipsPanelShipComponent(ship);
            b.setLocation(x, y);
            add(b);

            i++;
            x += 95;
            if (ship.canLoadUnloadCargo()) {
                x += 12;
            }
            if (ship.hasTroopTransport()) {
                x += 12;
            }
            if (i % 3 == 0) {
                x = 0;
                y += 48;
            }
        }

        size = new Dimension(375, y + 48);
    }

    @Override
    public void paint(Graphics g) {
        paintComponents(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }
}