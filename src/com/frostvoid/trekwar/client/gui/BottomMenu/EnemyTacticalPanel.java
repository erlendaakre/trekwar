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
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.TechnologyGenerator;

import javax.swing.*;
import java.awt.*;

/**
 * Tactical panel, shows enemy system stats.
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class EnemyTacticalPanel extends JPanel {

    public EnemyTacticalPanel() {
        setSize(220, 150);
        setLayout(null);
    }

    public void setSystem(StarSystem s) {
        removeAll();

        JLabel tacticalHeaderLabel = new JLabel(Client.getLanguage().getU("tactical"));
        tacticalHeaderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tacticalHeaderLabel.setVerticalAlignment(SwingConstants.CENTER);
        tacticalHeaderLabel.setOpaque(true);
        tacticalHeaderLabel.setBackground(Color.RED);
        tacticalHeaderLabel.setFont(FontFactory.getInstance().getHeading1());
        tacticalHeaderLabel.setBounds(5, 0, 220, 32);
        add(tacticalHeaderLabel);

        JPanel foo = generateTacticalContent(s);

        JScrollPane tacticalSP = new JScrollPane(foo);
        tacticalSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tacticalSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tacticalSP.setBounds(5, 32, 220, 118);
        tacticalSP.setOpaque(true);
        add(tacticalSP);
    }

    private JPanel generateTacticalContent(StarSystem s) {
        JPanel p = new JPanel() {
            @Override
            public void paint(Graphics g) {
                paintComponents(g);
            }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        int enemyShips = 0;
        int enemyFleetStrength = 0;
        for (Fleet f : s.getFleets()) {
            if (!f.getUser().getFaction().equals(Client.getInstance().getLocalUser().getFaction())) {
                enemyShips += f.getShips().size();
                enemyFleetStrength += (f.getDefence() / 2);
                enemyFleetStrength += f.getWeapons();
            }
        }
        JLabel enemyShipLabel = new JLabel(Client.getLanguage().getU("enemy_ships") + ": " + enemyShips);
        JLabel enemyFleetStrLabel = new JLabel(Client.getLanguage().getU("fleet_strength") + ": " + enemyFleetStrength);
        JLabel troopCountLabel = new JLabel(Client.getLanguage().getU("troops") + ": " + s.getTroopCount() + " (" + s.getTroopCapacity()
                + " " + Client.getLanguage().get("max") + ")");
        JLabel defenseLabel = new JLabel(Client.getLanguage().getU("defense_rating") + ": " + s.getDefenseRating());

        Font f = FontFactory.getInstance().getTacticalBoxFont();
        enemyShipLabel.setFont(f);
        enemyFleetStrLabel.setFont(f);
        troopCountLabel.setFont(f);
        defenseLabel.setFont(f);

        p.add(enemyShipLabel);
        p.add(enemyFleetStrLabel);
        p.add(troopCountLabel);
        p.add(defenseLabel);

        p.add(new JLabel(Client.getLanguage().getU("troops_per_turn") + ": " + s.getTroopProduction()));
        p.add(new JLabel(Client.getLanguage().getU("bunkers") + ": " + s.getNumberOfBunkers() + "   (" + s.getDefenseRating() + " " + Client.getLanguage().getU("defense") + ")"));
        p.add(new JLabel(Client.getLanguage().getU("wep_tech") + ": " + s.getUser().getHighestTech(TechnologyGenerator.techType.weaponstech).getLevel()));
        p.add(new JLabel(Client.getLanguage().getU("constr_tech") + ": " + s.getUser().getHighestTech(TechnologyGenerator.techType.constructiontech).getLevel()));

        return p;
    }

    @Override
    public void paint(Graphics g) {
        paintComponents(g);
    }
}