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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.JComponent;
import com.frostvoid.trekwar.client.model.Slice;

/**
 * A simple pie chart
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SimplePieChart extends JComponent {
    private ArrayList<Slice> slices;
    private int width;
    private int height;
    private int total; 
    
    /**
     * Makes a new pie chart
     * 
     * @param width the width in pixels
     * @param height the height in pixels
     * @param total the total value (100 for percent, 360 for degrees)
     */
    public SimplePieChart(int width, int height, int total) {
        slices = new ArrayList<Slice>();
        this.width = width;
        this.height = height;
        this.total = total;
    }

    @Override
    public void paint(Graphics g) {
        drawPie((Graphics2D) g,  slices);
    }

    public void drawPie(Graphics2D g, ArrayList<Slice> slices) {
        double curValue = 0.0D;
        int startAngle = 0;
        for (Slice slice : slices) {
            startAngle = (int) (curValue * 360 / total);
            int arcAngle = (int) (slice.getValue() * 360 / total);
            g.setColor(slice.getColor());
            g.fillArc(0, 0, width, height, startAngle, arcAngle);
            curValue += slice.getValue();
        }
    }
    
    /**
     * Adds a slice to the pie chart
     * 
     * @param slice the slice to add
     */
    public void addSlice(Slice slice) {
        slices.add(slice);
    }
    
    public void removeSlices() {
        slices.clear();
    }
}