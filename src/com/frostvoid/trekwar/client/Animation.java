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
package com.frostvoid.trekwar.client;

import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 * Animation framework, provides a series of images for drawing
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Animation {

    private ArrayList<ImageIcon> images;
    private long lastChangedFrame = 0;

    private int speed;
    private boolean loop;
    private int deleyBetweenLoops;

    private int currentFrame = -1;

    public Animation(int speed, boolean loop, int delayBetweenLoops) {
        images = new ArrayList<ImageIcon>();
        this.speed = speed;
        this.loop = loop;
        this.deleyBetweenLoops = delayBetweenLoops; // NOT IN USE YET XXX TODO
    }

    public ImageIcon next() {
        if(System.currentTimeMillis() > lastChangedFrame + speed) {
            lastChangedFrame = System.currentTimeMillis();
            currentFrame++;
            if(currentFrame >= getImages().size())
                currentFrame = 0;
        }
        return getImages().get(currentFrame);
    }

    
    public ArrayList<ImageIcon> getImages() {
        return images;
    }

    public void addImage(ImageIcon img) {
        images.add(img);
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isLoop() {
        return loop;
    }

    public int getDeleyBetweenLoops() {
        return deleyBetweenLoops;
    }
    
    
    
    
    // Static animations
    public static final Animation selectionAnimation;
    public static final Animation fleetMovementCursorAnimation;

    static {
        selectionAnimation = new Animation(100, true, 0);
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_1.png"));
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_2.png"));
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_3.png"));
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_4.png"));
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_5.png"));
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_6.png"));
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_5.png"));
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_4.png"));
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_3.png"));
        selectionAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_selection_2.png"));
        
        fleetMovementCursorAnimation = new Animation(70, true, 0);
        fleetMovementCursorAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_moveto_order_1.png"));
        fleetMovementCursorAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_moveto_order_2.png"));
        fleetMovementCursorAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_moveto_order_3.png"));
        fleetMovementCursorAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_moveto_order_4.png"));
        fleetMovementCursorAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_moveto_order_5.png"));
        fleetMovementCursorAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_moveto_order_6.png"));
        fleetMovementCursorAnimation.addImage(ImageManager.getInstance().getImage("graphics/map_icons/tile_moveto_order_7.png"));
        
    }
}
