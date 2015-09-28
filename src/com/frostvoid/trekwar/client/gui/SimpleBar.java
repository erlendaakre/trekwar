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

import javax.swing.*;
import java.awt.*;

/**
 * A simple graphical bar, horizontal or vertical
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SimpleBar extends JComponent {

    public static enum Alignment {
        HORIZONTAL,
        VERTICAL
    }

    private int height;
    private int width;
    private int percentage;
    private Color foreground;
    private Color background;
    private Alignment alignment;

    public SimpleBar(int height, int width, int percentage, Color foreground, Color background, Alignment alignment) {
        this.height = height;
        this.width = width;
        this.percentage = percentage;
        this.foreground = foreground;
        this.background = background;
        this.alignment = alignment;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;

        if (this.percentage < 0) {
            this.percentage = 0;
        }
        if (this.percentage > 100) {
            this.percentage = 100;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, width, height);
        }
        g.setColor(foreground);
        if (alignment == Alignment.HORIZONTAL) {
            g.fillRect(0, 0, (int) (((double) width / 100) * percentage), height);
        } else if (alignment == Alignment.VERTICAL) {
            g.fillRect(0, height - (int) (((double) height / 100) * percentage), width, height);
        }
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public Dimension getSize() {
        return new Dimension(width, height);
    }
}