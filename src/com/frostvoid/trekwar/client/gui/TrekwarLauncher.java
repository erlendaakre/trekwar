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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.FontFactory;
import com.frostvoid.trekwar.client.ImageManager;

/**
 * The launcher... Fuck Yeah!
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TrekwarLauncher extends JFrame {
    
    private static int launcherWidth = 600;
    private static String htmlURL = "http://trekwar.org/launcher.html";
    private static String versionURL = "http://trekwar.org/latestversion.txt";
    
    private static JLabel statusLabel;
    private static JEditorPane htmlPane;
    
    public TrekwarLauncher() {
        setTitle(Client.getLanguage().getCC("trekwar_launcher"));
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        
        // set application icon
        ArrayList<Image> iconImages = new ArrayList<Image>(4);
        iconImages.add(ImageManager.getInstance().getImage("graphics/icon_128.png").getImage());
        iconImages.add(ImageManager.getInstance().getImage("graphics/icon_64.png").getImage());
        iconImages.add(ImageManager.getInstance().getImage("graphics/icon_32.png").getImage());
        iconImages.add(ImageManager.getInstance().getImage("graphics/icon_16.png").getImage());
        setIconImages(iconImages);
        
        JPanel header = generateHeader();
        JPanel htmlPanel = generateHTMLPanel();
        JPanel footer = generateFooter();
                
        add(header);
        add(htmlPanel);
        add(footer);
        
    }
    
    public static void main(String[] args) {
        TrekwarLauncher launcher = new TrekwarLauncher();
        launcher.pack();
        launcher.setVisible(true);
        launcher.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Center Launcher
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenDimension.width-launcher.getSize().width)/2;
        int y = (screenDimension.height-launcher.getSize().height)/2;
        launcher.setLocation(x, y);

        Thread loadHTMLThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    htmlPane.setText(Client.getLanguage().getU("loading..."));
                    htmlPane.setPage(htmlURL);
                } catch (IOException ioe) {
                    htmlPane.setText(Client.getLanguage().getU("error_unable_to_load_page_from_trekwar_server") + ": " + ioe.getMessage());
                }
            }
        });
        
        Thread checkForUpdatesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    statusLabel.setText(Client.getLanguage().getU("status") + ": " + Client.getLanguage().get("checking_version"));
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException ex) { }
                    
                    URL connection = new URL(versionURL);
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.openStream()));

                    String remoteVersion = in.readLine();
                    in.close();
                    
                    String updateString = "";
                    if(remoteVersion.equals(Client.VERSION)) {
                        updateString = Client.getLanguage().getU("up_to_date");
                    }
                    else {
                        statusLabel.setForeground(Color.RED);
                        updateString = Client.getLanguage().getUC("update_available");
                    }
                    
                    statusLabel.setText(Client.getLanguage().getU("status") + ": " + Client.getLanguage().getU("remote_version") + " = " + remoteVersion + " (" + updateString + ")");
                } catch (IOException ex) {
                    statusLabel.setText(Client.getLanguage().getU("error_checking_latest_version_on_server"));
                }
            }
        });
        
        loadHTMLThread.start();
        checkForUpdatesThread.start();
    }

    private JPanel generateHeader() {
        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Image img = ImageManager.getInstance().getImage("graphics/launcher_header.png").getImage();
                g.drawImage(img, 0, 0, null);
            }
        };
        panel.setLayout(new GridLayout(1,1));
        panel.setPreferredSize(new Dimension(launcherWidth, 95));
        JLabel trekwarLabel = new JLabel("Trekwar " + Client.VERSION);
        trekwarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        trekwarLabel.setForeground(Color.WHITE);
        trekwarLabel.setFont(FontFactory.getInstance().getLauncherHeading());
        panel.add(trekwarLabel);
        
        return panel;
    }

    private JPanel generateHTMLPanel() {
        JPanel panel = new JPanel() {
            
        };
        panel.setLayout(new GridLayout(1,1));
        
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false); 
        htmlPane.setContentType("text/html");
        
        htmlPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                javax.swing.event.HyperlinkEvent.EventType type = evt.getEventType();
                if (type == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(new URI(evt.getURL().toString()));
                        } else {
                            
                            JOptionPane.showMessageDialog(htmlPane, Client.getLanguage().getU("your_java_version_does_not_support_opening_links_using_the_desktop_api"));
                        }
                    } catch (Throwable excep) {
                        JOptionPane.showMessageDialog(htmlPane, Client.getLanguage().getU("error_while_trying_to_open_browser_to_url") + ":\n" + evt.getURL());
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(htmlPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(scrollPane);
        panel.setPreferredSize(new Dimension(launcherWidth, 260));
        
        return panel;
    }

    private JPanel generateFooter() {
        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                Image img = ImageManager.getInstance().getImage("graphics/launcher_footer.png").getImage();
                g.drawImage(img, 0, 0, null);
            }
        };
        panel.setPreferredSize(new Dimension(launcherWidth, 45));
        panel.setLayout(new BorderLayout());
        
        statusLabel = new JLabel(Client.getLanguage().getU("status") + ": " + Client.getLanguage().getU("have_not_contacted_server_yet"));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panel.add(statusLabel, BorderLayout.WEST);
        
        
        JButton launchButton = new JButton(Client.getLanguage().getU("launch"));
        launchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
                Client.main(null);
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(launchButton);
        
        panel.add(buttonPanel, BorderLayout.EAST);
        return panel;
    }
}