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

import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.Colors;

/**
 * Allows user to read/send in-game mail
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class MailWindow extends JInternalFrame {

   public MailWindow(String name, Icon icon, int x, int y) {
      super(name,
            false, //resizable
            false, //closable
            false, //maximizable
            false);//iconifiable

      setBackground(Colors.TREKWAR_BG_COLOR);

      JScrollPane scrollpane = new JScrollPane();
      scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      scrollpane.add(new JLabel(Client.getLanguage().get("mail_messages")));
      getContentPane().add(scrollpane);
//      Border border = BorderFactory.createBevelBorder(0, new Color(166,166,166), Client.BLACK_COLOR);
//      setBorder(border);
      setFrameIcon(new ImageIcon(((ImageIcon)icon).getImage().getScaledInstance(-1,18,0)));

      setSize(new Dimension(480,350));

      setLocation(x, y);
   }

    @Override
   public void removeNotify() {
      setVisible(false);
      dispose();
   }
}