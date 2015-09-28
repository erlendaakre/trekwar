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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import com.frostvoid.trekwar.client.Animation;

/**
 * Displays a single tile on the map
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MapTile extends JLabel implements Runnable {

    private ArrayList<Object> layers = new ArrayList<Object>();
    private boolean hasAnimation = false;

    private Thread animThread;
    
    private int xloc;
    private int yloc;

    private static final Border DEFAULTBORDER = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(30,30,30));

    public MapTile(int x, int y) {
        super();
        xloc = x;
        yloc = y;
        setPreferredSize(new Dimension(40, 40));
        setBorder(DEFAULTBORDER);
    }

    public void reset() {
        layers.clear();
        hasAnimation = false;
    }
    
    public void setDefaultBorder() {
        setBorder(DEFAULTBORDER);
    }

    public void addImage(ImageIcon img) {
        layers.add(img);
        repaint();
    }
    
    public void removeImage(ImageIcon img) {
        layers.remove(img);
        repaint();
    }

    public void addAnimation(Animation anim) {
        layers.add(anim);
        hasAnimation = true;
        
        animThread = new Thread(this);
        animThread.start();
    }
    
    public void removeAnimation(Animation anim) {
        layers.remove(anim);
        
        // todo check if any animations left, then stop thread if nobody is running?
    }

    public void setSelected(boolean selected) {
        // TODO this crap only supports ONE animation per tile.. FIXME FIXIT!
        if(selected) {
            addAnimation(Animation.selectionAnimation);
        }
        else {
            layers.remove(Animation.selectionAnimation);
            hasAnimation = false;
        }
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for(Object o : layers) {
            if(o instanceof ImageIcon) {
                g.drawImage(((ImageIcon)o).getImage(), 0, 0, null);
            }
            if(o instanceof Animation) {
                g.drawImage(((Animation)o).next().getImage(), 0, 0, null);
            }
        }
    }

    @Override
    public void run() {
        while(hasAnimation) {
            try {
                Thread.sleep(50);
                repaint();
            } catch (InterruptedException ex) { }
        }
    }
    
    public int getXloc() {
        return xloc;
    }
    
    public int getYloc() {
        return yloc;
    }
}