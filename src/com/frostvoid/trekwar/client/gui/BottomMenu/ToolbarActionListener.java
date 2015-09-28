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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.Fleet;

/**
 * Handles clicks on the toolbar icons (empire, starsystem, fleets)
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ToolbarActionListener implements ActionListener {
    
    public static enum Actions {
        VIEW_EMPIRE,
        VIEW_STARSYSTEM,
        VIEW_FLEET
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(Actions.VIEW_EMPIRE.toString())) {
            Client.getInstance().getBottomGuiPanel().showEmpireView();
        }
        else if(e.getActionCommand().equals(Actions.VIEW_STARSYSTEM.toString())) {
            Client.getInstance().getBottomGuiPanel().showStarsystem();
        }
        else if(e.getActionCommand().equals(Actions.VIEW_FLEET.toString())) {
            Fleet fleet = ((Fleet)( ((JButton)e.getSource()).getClientProperty("fleet")) );
            Client.getInstance().getBottomGuiPanel().showFleet(fleet);
        }
    }   
}