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

import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.client.gui.SimplePieChart;
import com.frostvoid.trekwar.client.model.Slice;
import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.StaticData;

import javax.swing.*;
import java.awt.*;

/**
 * Has general functions to generate graphics for BottomMenu views.
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class BottomUIComponentFactory {

    /**
     * Adds population slices to a pie chart
     *
     * @param chart the pie chart (must be empty)
     * @param s     the starsystem to show population for
     */
    public static void addPopulationSlices(SimplePieChart chart, StarSystem s) {

        int populationPercentage = (int) ((100D / s.getMaxPopulation()) * s.getPopulation());

        chart.removeSlices();
        chart.addSlice(new Slice(s.getPopulation(), Colors.POPULATIONCHART_POP));

        boolean increaseOrDecrease = false;
        if (populationPercentage < 100 && s.getSystemFoodSurplus() > 0) {
            chart.addSlice(new Slice(1000, Colors.POPULATIONCHART_INCREASE));
            increaseOrDecrease = true;
        }
        if (s.getSystemFoodSurplus() < 0) {
            chart.addSlice(new Slice(1000, Colors.POPULATIONCHART_DECREASE));
            increaseOrDecrease = true;
        }

        if (s.getMaxPopulation() > s.getPopulation()) {
            int amount = s.getMaxPopulation() - s.getPopulation();
            if (increaseOrDecrease) {
                amount -= 900;
            }
            chart.addSlice(new Slice(amount, Color.GRAY));
        }

        Double maxPopDouble = ((double) s.getMaxPopulation()) / 1000;
        Double popDouble = ((double) s.getPopulation()) / 1000;
        chart.setToolTipText(StaticData.DECIMAL_FORMAT_2D.format(popDouble) + " / " + StaticData.DECIMAL_FORMAT_2D.format(maxPopDouble));
    }

    /**
     * Gets the faction icon for a specific faction
     *
     * @param f the faction
     * @return the ImageIcon with the faction icon
     */
    public static ImageIcon getFactionIcon(Faction f) {
        if (f.equals(StaticData.federation)) {
            return ImageManager.getInstance().getImage("graphics/map_icons/fed.png");
        } else if (f.equals(StaticData.klingon)) {
            return ImageManager.getInstance().getImage("graphics/map_icons/kli.png");
        } else if (f.equals(StaticData.romulan)) {
            return ImageManager.getInstance().getImage("graphics/map_icons/rom.png");
        } else if (f.equals(StaticData.cardassian)) {
            return ImageManager.getInstance().getImage("graphics/map_icons/car.png");
        } else if (f.equals(StaticData.dominion)) {
            return ImageManager.getInstance().getImage("graphics/map_icons/dom.png");
        }

        return null;
    }
}