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
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.StarSystem;
import org.jvnet.substance.SubstanceLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * A KPI panel for an inhabited starsystem, shows output surplus or deficit
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class KPIPanel extends JPanel {
    private JLabel powerLabel, industryLabel, researchLabel, foodLabel;
    private JLabel deuteriumLabel, oreLabel, defenseLabel, sensorLabel;

    public KPIPanel() {
        setOpaque(false);
        setSize(210, 140);
        setLayout(null);

        int width = 80;
        int x_offset = width + 10;
        int y = 0;
        int y_increment = 25;

        powerLabel = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/lightning.png"));
        powerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        powerLabel.setBounds(0, y, width, 25);
        powerLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        powerLabel.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
        add(powerLabel);

        industryLabel = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/cog.png"));
        industryLabel.setHorizontalAlignment(SwingConstants.LEFT);
        industryLabel.setBounds(x_offset, y, width, 25);
        industryLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        industryLabel.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
        add(industryLabel);

        y += y_increment;

        researchLabel = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/lightbulb_off.png"));
        researchLabel.setHorizontalAlignment(SwingConstants.LEFT);
        researchLabel.setBounds(0, y, width, 25);
        researchLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        researchLabel.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
        add(researchLabel);

        foodLabel = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/wheat.png"));
        foodLabel.setHorizontalAlignment(SwingConstants.LEFT);
        foodLabel.setBounds(x_offset, y, width, 25);
        foodLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        foodLabel.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
        add(foodLabel);

        y += y_increment;

        deuteriumLabel = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/deuterium.png"));
        deuteriumLabel.setHorizontalAlignment(SwingConstants.LEFT);
        deuteriumLabel.setBounds(0, y, width * 2, 25);
        deuteriumLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        add(deuteriumLabel);

        y += y_increment;

        oreLabel = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/mineral.png"));
        oreLabel.setHorizontalAlignment(SwingConstants.LEFT);
        oreLabel.setBounds(0, y, width * 2, 25);
        oreLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        add(oreLabel);

        y += y_increment;

        defenseLabel = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/shield.png"));
        defenseLabel.setHorizontalAlignment(SwingConstants.LEFT);
        defenseLabel.setBounds(0, y, width, 25);
        defenseLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        add(defenseLabel);

        sensorLabel = new JLabel(ImageManager.getInstance().getImage("graphics/misc_icons/remote.png"));
        sensorLabel.setHorizontalAlignment(SwingConstants.LEFT);
        sensorLabel.setBounds(x_offset, y, width, 25);
        sensorLabel.setFont(FontFactory.getInstance().getSystemKPIMainFont());
        add(sensorLabel);
    }

    public void setSystem(StarSystem s) {
        // Power 
        int powerSurplus = s.getSystemPowerSurplus();
        Color powerColor = Color.BLACK;
        if (powerSurplus >= 0) {
            powerColor = Colors.SYSTEM_KPI_POSITIVE;
        }
        if (powerSurplus < 0) {
            powerColor = Colors.SYSTEM_KPI_NEGATIVE;
        }

        String powerTooltip = Client.getLanguage().get("power") + ":" + Client.getLanguage().get("producing") + " " + s.getSystemPowerProduced() + ", " + Client.getLanguage().get("using") + " " + s.getSystemPowerConsumed() + ", " + Client.getLanguage().get("surplus") + " " + s.getSystemPowerSurplus();
        powerLabel.setToolTipText(powerTooltip);
        powerLabel.setForeground(powerColor);
        powerLabel.setText("" + powerSurplus);

        // Industry
        int industrySurplus = s.getSystemIndustrySurplus();
        Color industryColor = Color.BLACK;
        if (industrySurplus >= 0) {
            industryColor = Colors.SYSTEM_KPI_POSITIVE;
        }
        if (industrySurplus < 0) {
            industryColor = Colors.SYSTEM_KPI_NEGATIVE;
        }
        String industryTooltip = Client.getLanguage().get("industry") + ": " + Client.getLanguage().get("producing") + " " + s.getSystemIndustryProduced() + ", " + Client.getLanguage().get("using") + " " + s.getSystemIndustryConsumed() + ", " + Client.getLanguage().get("surplus") + " " + s.getSystemIndustrySurplus() + ", " + Client.getLanguage().get("upkeep_contribution") + s.getShipUpkeepContribution();
        industryLabel.setToolTipText(industryTooltip);
        industryLabel.setForeground(industryColor);
        industryLabel.setText("" + industrySurplus);

        // Research
        int researchSurplus = s.getSystemResearchSurplus();
        Color researchColor = Color.BLACK;
        if (researchSurplus >= 0) {
            researchColor = Colors.SYSTEM_KPI_POSITIVE;
        }
        if (researchSurplus < 0) {
            researchColor = Colors.SYSTEM_KPI_NEGATIVE;
        }
        String researchTooltip = Client.getLanguage().get("research") + ": " + Client.getLanguage().get("producing") + " " + s.getSystemResearchProduced() + ", " + Client.getLanguage().get("using") + " " + s.getSystemResearchConsumed() + ", " + Client.getLanguage().get("surplus") + " " + s.getSystemResearchSurplus();
        researchLabel.setText("" + researchSurplus);
        researchLabel.setForeground(researchColor);
        researchLabel.setToolTipText(researchTooltip);


        // Food
        int foodSurplus = s.getSystemFoodSurplus();
        Color foodColor = Color.BLACK;
        if (foodSurplus >= 0) {
            foodColor = Colors.SYSTEM_KPI_POSITIVE;
        }
        if (foodSurplus < 0) {
            foodColor = Colors.SYSTEM_KPI_NEGATIVE;
        }
        String foodTooltip = Client.getLanguage().get("food") + ": " + Client.getLanguage().get("producing") + " " + s.getSystemFoodProduced() + ", " + Client.getLanguage().get("using") + " " + s.getSystemFoodConsumed() + ", " + Client.getLanguage().get("surplus") + " " + s.getSystemFoodSurplus();
        foodLabel.setText("" + foodSurplus);
        foodLabel.setForeground(foodColor);
        foodLabel.setToolTipText(foodTooltip);

        // Deuterium 
        Color deuteriumColor = Color.WHITE;
        String deuteriumTooltip = Client.getLanguage().get("deuterium") + ": " + s.getDeuterium();
        deuteriumLabel.setText("" + s.getDeuterium() + " / " + s.getMaxDeuterium());
        deuteriumLabel.setForeground(deuteriumColor);
        deuteriumLabel.setToolTipText(deuteriumTooltip);

        // Ore
        Color oreColor = Color.BLACK;
        String oreTooltip = Client.getLanguage().get("ore") + ": " + s.getDeuterium();
        oreLabel.setText("" + s.getOre() + " / " + s.getMaxOreStorage());
        oreLabel.setForeground(oreColor);
        oreLabel.setToolTipText(oreTooltip);

        // Defense
        Color defenseColor = Color.BLACK;
        String defenseTooltip = Client.getLanguage().get("defense") + ": " + s.getDeuterium();
        defenseLabel.setText("" + s.getDefenseRating());
        defenseLabel.setForeground(defenseColor);
        defenseLabel.setToolTipText(defenseTooltip);

        // Defense
        Color sensorColor = Color.BLACK;
        String sensorTooltip = Client.getLanguage().get("sensor_strength") + ": " + s.getDeuterium();
        sensorLabel.setText("" + Client.getInstance().getLocalUser().getSensorOverlay()[s.getX()][s.getY()]);
        sensorLabel.setForeground(sensorColor);
        sensorLabel.setToolTipText(sensorTooltip);
    }

    @Override
    public void paint(Graphics g) {
        paintComponents(g);
    }
}