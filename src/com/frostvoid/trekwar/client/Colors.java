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

import java.awt.Color;

/**
 * Holds all the color objects used in the game
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Colors {
    public static final Color TREKWAR_BG_COLOR = new Color(127, 127, 127);
    public static final Color TURN_SCREEN_TRANSPARENT = new Color(0, 0, 0, 170);
    public static final Color BLACK_COLOR = new Color(0, 0, 0);
    
    public static final Color NOBODY_COLOR = new Color(200, 200, 200);
    public static final Color FEDERATION_COLOR = new Color(0, 0, 200);
    public static final Color KLINGON_COLOR = new Color(200, 0, 0);
    public static final Color ROMULAN_COLOR = new Color(0, 160, 0);
    public static final Color CARDASSIAN_COLOR = new Color(150, 150, 0);
    public static final Color DOMINION_COLOR = new Color(100, 0, 150);
    
    public static final Color BARCOLOR_BACKGROUND = Color.GRAY;
    public static final Color BARCOLOR_SHIELD = new Color(20, 111, 154); // blue
    public static final Color BARCOLOR_ARMOR = new Color(105, 20, 154); // magenta
    public static final Color BARCOLOR_HITPOINTS = new Color(154, 20, 36); // red
    public static final Color BARCOLOR_FUEL = new Color(198, 169, 53); // yellow
    public static final Color BARCOLOR_DEUTERIUM = new Color(121, 198, 53); // green
    public static final Color BARCOLOR_ORE = new Color(192, 82, 23); // orange brown
    public static final Color BARCOLOR_TROOPS = new Color(198, 53, 91); // pink
    
    public static final Color SYSTEM_KPI_POSITIVE = new Color(121, 198, 53); //green
    public static final Color SYSTEM_KPI_NEGATIVE = new Color(255, 0, 0); //Red
    
    
    public static final Color POPULATIONCHART_POP = new Color(30, 104, 188);
    public static final Color POPULATIONCHART_INCREASE = new Color(90, 152, 35);
    public static final Color POPULATIONCHART_DECREASE = new Color(200, 50, 54);
    
    public static final Color FERTILITY_COLOR = new Color(0, 30, 0);
    
    public static final Color RESOURCES_VIEW_COLOR_FOREGROUND = new Color(30, 188, 30);
    public static final Color RESOURCES_VIEW_COLOR_BACKGROUND = new Color(22, 22, 22);
    
    public static final Color INVASION_DIALOG_GREEN = Color.GREEN;
    public static final Color INVASION_DIALOG_RED = Color.RED;

    public static final Color SELFDESTRUCT_DIALOG_RED = Color.RED;
    
    public static final Color MINIMAP_EMPTY_SPACE = Color.BLACK;
    public static final Color MINIMAP_UNINHABITED_SYSTEM = Color.WHITE;
    public static final Color MINIMAP_UNEXPLORED = Color.CYAN;
    public static final Color MINIMAP_ASTEROID = Color.GRAY;
    public static final Color MINIMAP_VIEWPORT = Color.YELLOW;
    
    public static final Color SHIPDESIGNER_FONT_OK = Color.BLACK;
    public static final Color SHIPDESIGNER_FONT_ERROR = Color.RED;
}