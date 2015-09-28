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
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;

/**
 * Window that allows user to chat with other online users in real time (also in fake time)
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ChatWindow extends JInternalFrame {

    private JPanel contentPanel;
    private JPanel userListPanel;
    private JScrollPane userListScrollPane;
    private JTabbedPane chatTabbedPane;

    public ChatWindow(String name, Icon icon, int x, int y) {
        super(name,
                true, //resizable
                true, //closable
                false, //maximizable
                false);//iconifiable
        setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);

        setBackground(Colors.TREKWAR_BG_COLOR);

        makeGUI();
        getContentPane().add(contentPanel);
        setFrameIcon(new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(-1, 18, 0)));

        setSize(new Dimension(800, 600));
        setLocation(x, y);
    }

    private void makeGUI() {
        if (contentPanel == null) {
            // FIRST TIME
            contentPanel = new JPanel();
            contentPanel.setLayout(new BorderLayout());
            contentPanel.setBackground(Colors.TREKWAR_BG_COLOR);
        } else {
            contentPanel.removeAll();
        }

        userListPanel = new JPanel();
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));
        userListPanel.setBackground(Colors.TREKWAR_BG_COLOR);

        userListScrollPane = new JScrollPane(userListPanel);
        userListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        userListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentPanel.add(userListScrollPane, BorderLayout.EAST);
        contentPanel.add(makeInputPanel(), BorderLayout.SOUTH);
        contentPanel.add(makeChatPanel(), BorderLayout.CENTER);

    }

    private JPanel makeInputPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBackground(Colors.TREKWAR_BG_COLOR);
        ImageIcon avatar = new ImageIcon(ImageManager.getInstance().getImage("graphics/avatars/" + Client.getInstance().getLocalUser().getAvatarFilename()).getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT));
        p.add(new JLabel(avatar), BorderLayout.WEST);
        final JTextField inputField = new JTextField();
        final JButton sendButton = new JButton(Client.getLanguage().get("chat_send"));
        p.add(inputField, BorderLayout.CENTER);

        ActionListener sendAction = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String dst = chatTabbedPane.getTitleAt(chatTabbedPane.getSelectedIndex());
                if (inputField.getText().length() > 1) {
                    if (dst.equalsIgnoreCase("galaxy") || dst.equalsIgnoreCase("faction")) {
                        Client.getInstance().getComm().server_send_chat(dst, inputField.getText(), false);
                    } else {
                        Client.getInstance().getComm().server_send_chat(dst, inputField.getText(), true);
                        ChatLine ownLine = new ChatLine(Client.getInstance().getLocalUser().getUsername(), new Date(), dst, inputField.getText());
                        ((ChatPanel) (chatTabbedPane.getComponentAt(chatTabbedPane.getSelectedIndex()))).addChat(ownLine);
                    }
                    inputField.setText("");
                    inputField.requestFocus();
                }
            }
        };

        inputField.addActionListener(sendAction);
        sendButton.addActionListener(sendAction);
        p.add(sendButton, BorderLayout.EAST);

        return p;
    }

    public void populateUserList(ArrayList<User> users) {
        userListPanel.removeAll();

        for (final User u : users) {
            ImageIcon avatar = new ImageIcon(ImageManager.getInstance().getImage("graphics/avatars/" + u.getAvatarFilename()).getImage().getScaledInstance(35, 35, Image.SCALE_DEFAULT));
            final JLabel userLabel = new JLabel(u.getUsername(), avatar, SwingConstants.HORIZONTAL);

            JPanel userHolder = new JPanel();
            userHolder.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            userHolder.setOpaque(true);

            userHolder.setPreferredSize(new Dimension(150, 42));
            userHolder.setSize(new Dimension(150, 42));
            userHolder.setMaximumSize(new Dimension(150, 42));

            if (u.getFaction().equals(StaticData.federation)) {
                userHolder.setBackground(new Color(10, 10, 150, 40));
            } else if (u.getFaction().equals(StaticData.klingon)) {
                userHolder.setBackground(new Color(150, 10, 10, 40));
            } else if (u.getFaction().equals(StaticData.romulan)) {
                userHolder.setBackground(new Color(10, 150, 10, 40));
            } else if (u.getFaction().equals(StaticData.cardassian)) {
                userHolder.setBackground(new Color(150, 150, 10, 40));
            } else if (u.getFaction().equals(StaticData.dominion)) {
                userHolder.setBackground(new Color(115, 0, 150, 40));
            } else {
                userHolder.setBackground(new Color(60, 255, 0, 100));
            }

            userHolder.add(userLabel);

            userLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    JPopupMenu menu = new JPopupMenu(u.getUsername());
                    JMenuItem usernameItem = new JMenuItem(u.getUsername());
                    JMenuItem chatItem = new JMenuItem(Client.getLanguage().get("chat_menu_chat"));
                    JMenuItem mailItem = new JMenuItem(Client.getLanguage().get("chat_menu_send_mail"));
                    JMenuItem ignoreItem = new JMenuItem(Client.getLanguage().get("chat_menu_ignore"));

                    chatItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            boolean openTab = true;
                            for (int i = 0; i < chatTabbedPane.getTabCount(); i++) {
                                if (chatTabbedPane.getTitleAt(i).equalsIgnoreCase(u.getUsername())) {
                                    ChatPanel comp = (ChatPanel) chatTabbedPane.getComponentAt(i);
                                    openTab = false;
                                    chatTabbedPane.setSelectedIndex(i);
                                    break;
                                }
                            }
                            if (openTab) {
                                ChatPanel cp = new ChatPanel();
                                chatTabbedPane.addTab(u.getUsername(), cp);
                            }
                        }
                    });

                    menu.add(usernameItem);
                    menu.addSeparator();
                    menu.add(chatItem);
                    menu.add(mailItem);
                    menu.add(ignoreItem);
                    menu.show(userLabel, e.getX(), e.getY());
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }
            });

            userListPanel.add(userHolder);
        }
        repaint();
        validate();
    }

    private JTabbedPane makeChatPanel() {
        ChatPanel galaxyChat = new ChatPanel();
        ChatPanel factionChat = new ChatPanel();

        chatTabbedPane = new JTabbedPane();
        chatTabbedPane.addTab(Client.getLanguage().get("chat_tab_galaxy"), galaxyChat);
        chatTabbedPane.addTab(Client.getLanguage().get("chat_tab_faction"), factionChat);
        return chatTabbedPane;
    }

    public void addChat(final ChatLine c) {
        if (c.isPrivateMsg()) {
            final String fromUser = c.getUserName();
            boolean newPriv = true;
            for (int i = 0; i < chatTabbedPane.getTabCount(); i++) {
                if (chatTabbedPane.getTitleAt(i).equalsIgnoreCase(fromUser)) {
                    final int index = i;
                    java.awt.EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            ChatPanel comp = (ChatPanel) chatTabbedPane.getComponentAt(index);
                            comp.addChat(c);
                        }
                    });

                    newPriv = false;
                    break;
                }
            }
            if (newPriv) {
                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        ChatPanel cp = new ChatPanel();
                        chatTabbedPane.addTab(fromUser, cp);
                        cp.addChat(c);
                    }
                });
            }
        } else {
            for (int i = 0; i < chatTabbedPane.getTabCount(); i++) {
                if (c.getChannel().equalsIgnoreCase(chatTabbedPane.getTitleAt(i))) {
                    ChatPanel comp = (ChatPanel) chatTabbedPane.getComponentAt(i);
                    comp.addChat(c);
                    break;
                }
            }
        }
    }
}