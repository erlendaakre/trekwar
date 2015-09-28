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

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.Colors;
import com.frostvoid.trekwar.client.ImageManager;
import com.frostvoid.trekwar.common.ChatLine;
import com.frostvoid.trekwar.common.User;

import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * Panel that shows chat messages in the Chat window
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ChatPanel extends JPanel {

    private static final long serialVersionUID = 2284271226107770864L;
    private JScrollablePanel chatContainer;
    private JScrollPane sp;
    SimpleDateFormat sdf;

    public ChatPanel() {
        super();
        sdf = new SimpleDateFormat("HH:mm:ss");
        setLayout(new GridLayout(1, 1));
        setBackground(Colors.TREKWAR_BG_COLOR);

        chatContainer = new JScrollablePanel();
        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
        chatContainer.setBackground(Colors.TREKWAR_BG_COLOR);

        sp = new JScrollPane(chatContainer);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(sp);

    }

    public void addChat(final ChatLine line) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                User user = Client.getInstance().getUserByUsername(line.getUserName());

                JScrollablePanel chatLineContainer = new JScrollablePanel();
                chatLineContainer.setLayout(new BorderLayout());

                JLabel avatar = new JLabel(new ImageIcon(ImageManager.getInstance().getImage("graphics/avatars/" + user.getAvatarFilename()).getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT)));
                avatar.setVerticalAlignment(SwingConstants.TOP);

                JMultilineLabel chatLabel = new JMultilineLabel(sdf.format(line.getTimestamp()) + " " + Client.getLanguage().get("chat_by") + " " + user.getUsername() + ": " + line.getMessage());

                chatLineContainer.add(chatLabel, BorderLayout.CENTER);
                chatLineContainer.add(avatar, BorderLayout.WEST);
                chatLineContainer.setOpaque(false);
                chatLineContainer.setBorder(BorderFactory.createEmptyBorder(2, 5, 5, 25));

                chatContainer.add(chatLineContainer);
                chatContainer.scrollRectToVisible(new Rectangle(0, chatContainer.getHeight() * 2, 1, 1));


            }
        });
    }

    /**
     * Returns the preferred size to set a component at in order to render
     * an html string.  You can specify the size of one dimension.
     */
    public static java.awt.Dimension getPreferredSize(JLabel label, String html,
                                                      boolean width, int prefSize) {
        View view = (View) label.getClientProperty(
                javax.swing.plaf.basic.BasicHTML.propertyKey);

        view.setSize(width ? prefSize : 0, width ? 0 : prefSize);

        float w = view.getPreferredSpan(View.X_AXIS);
        float h = view.getPreferredSpan(View.Y_AXIS);

        if (h < 37) {
            h = 37; // makes sure vertical size is always AT LEAST user icon + 2px border
        }

        return new java.awt.Dimension((int) Math.ceil(w),
                (int) Math.ceil(h));
    }
}