
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

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

import com.frostvoid.trekwar.client.gui.BottomMenu.BottomMenuPanel;
import com.frostvoid.trekwar.client.net.ClientCommunication;
import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.exceptions.UserNotFoundException;
import com.frostvoid.trekwar.client.gui.BottomMenu.BottomMenuToolbarPanel;
import com.frostvoid.trekwar.client.gui.CargoTransferWindow;
import com.frostvoid.trekwar.client.gui.ChatWindow;
import com.frostvoid.trekwar.client.gui.LoginDialog;
import com.frostvoid.trekwar.client.gui.MapBorder;
import com.frostvoid.trekwar.client.gui.MapPanel;
import com.frostvoid.trekwar.client.gui.MinimapWindow;
import com.frostvoid.trekwar.client.gui.ResearchWindow;
import com.frostvoid.trekwar.client.gui.ShipDesignerWindow;
import com.frostvoid.trekwar.client.gui.SystemControlWindow;
import com.frostvoid.trekwar.client.gui.TopMenuPanel;
import com.frostvoid.trekwar.client.gui.TrekwarSkin;
import com.frostvoid.trekwar.client.gui.TroopTransferWindow;
import com.frostvoid.trekwar.common.utils.Language;
import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * Trekwar client entrypoint and big collection of things and stuff :D
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Client extends JFrame {

    public static final long SYNC_UPDATE_TIME = 5000;
    public static final String VERSION = "0.4.55";
    public static final Logger LOG = Logger.getLogger("trekwar_client");
    private static Language language;
    private static NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private SoundSystem soundSystem;
    private static Client instance;
    private static ClientCommunication comm;
    private Galaxy localGalaxy;
    private User localUser;
    private LinkedList<ChatLine> chatQueue;
    private boolean getMapFromServerThreadRunning = true;
    private long lastSyncTimestamp = 0;
    private boolean downloadNeeded = true;
    private Thread reloadThread;
    private Properties userProperties;
    private String propertiesFilename = "properties.xml";
    private JDesktopPane desktop;
    private TopMenuPanel topMenuPanel;
    private BottomMenuPanel bottomMenuPanel;
    private BottomMenuToolbarPanel bottomMenuToolbarPanel;
    private JPanel glassPanel;
    // components allowed in Desktop.. MODAL
    private MapPanel mapPanel;
    private JScrollPane mapScrollPane;
    private ResearchWindow researchWindow;
    private ShipDesignerWindow shipDesignerWindow;
    private SystemControlWindow systemControlWindow;
    private ChatWindow chatWindow;
    private CargoTransferWindow cargoTransferWindow;
    private TroopTransferWindow troopTransferWindow;
    private MinimapWindow minimapWindow;

    private Client() {
        instance = this;
        comm = new ClientCommunication();

        try {
            LOG.setUseParentHandlers(false);
            initLogging();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Unable to initialize logging to file", ex);
        }
        setTitle("Trekwar " + VERSION);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        chatQueue = new LinkedList<ChatLine>();

        // load user preferences
        try {
            FileInputStream s = new FileInputStream(propertiesFilename);
            userProperties = new Properties();
            userProperties.loadFromXML(s);
        } catch (IOException ioe) {
            showError("Unable to read properties file: " + propertiesFilename, ioe, true, false);
        }

        // load language
        try {
            language = new Language(Language.ENGLISH);
        } catch (IOException ioe) {
            showError("unable to load language files", ioe, true, false);
        }

        // load sound + music + gfx
        try {
            soundSystem = SoundSystem.getInstance();
            Client.LOG.info("Sound system loaded");
            if (userProperties.get("music-enabled").equals("true")) {
                Client.LOG.info("Music Loaded");
                soundSystem.setMusicEnabled(true);
                soundSystem.loadMusic();
                soundSystem.loopMusic();
            } else {
                soundSystem.setMusicEnabled(false);
            }

            if (userProperties.get("sfx-enabled").equals("true")) {
                Client.LOG.info("SFX loaded");
                soundSystem.setSfxEnabled(true);
                soundSystem.loadSounds();
            } else {
                soundSystem.setSfxEnabled(false);
            }

            ImageManager.getInstance().preloadImages();

        } catch (UnsupportedAudioFileException ex) {
            showError(language.get("tried_to_load_unsupported_audio_file"), ex, false, false);
        } catch (IOException ex) {
            showError(language.get("io_error_while_loading_audio_file"), ex, false, false);
        } catch (LineUnavailableException ex) {
            showError(language.get("unable_to_open_audio_device"), ex, false, false);
        }

        // set application icon
        ArrayList<Image> iconImages = new ArrayList<Image>(4);
        iconImages.add(ImageManager.getInstance().getImage("graphics/icon_128.png").getImage());
        iconImages.add(ImageManager.getInstance().getImage("graphics/icon_64.png").getImage());
        iconImages.add(ImageManager.getInstance().getImage("graphics/icon_32.png").getImage());
        iconImages.add(ImageManager.getInstance().getImage("graphics/icon_16.png").getImage());
        setIconImages(iconImages);

        glassPanel = new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                setSize(instance.getWidth(), instance.getHeight());
                g.setColor(Colors.TURN_SCREEN_TRANSPARENT);
                g.fillRect(0, 0, instance.getWidth(), instance.getHeight());

                // gettingdata.png = 735 x 211 pixels
                int imgX = (instance.getHeight() / 2) - 105;
                int imgY = (instance.getWidth() / 2) - 367;
                g.drawImage(ImageManager.getInstance().getImage("graphics/gettingdata.png").getImage(), imgX, imgY, this);
            }
        };
        glassPanel.setSize(200, 200);
        glassPanel.addMouseListener(new MouseAdapter() {
        });
        glassPanel.setLayout(new GridLayout(1, 1));
        glassPanel.setOpaque(false);
        glassPanel.setVisible(false);


        // Reload thread (updates countdown in turns)
        reloadThread = new Thread() {

            @Override
            public void run() {
                while (getMapFromServerThreadRunning) {
                    System.out.print("Reload Thread:  ");
                    try {
                        
                        // DOWNLOAD CHAT + SYNC TIME WITH SERVER EVERY X SECONDS
                        if (comm.isLoggedIn() && localGalaxy != null) {
                            
                            if (lastSyncTimestamp < System.currentTimeMillis() - SYNC_UPDATE_TIME) {
                                final ArrayList<ChatLine> chatLines = comm.server_getChat();
                                final ArrayList<User> userList = Client.getInstance().getComm().server_getUserList();
                                comm.sync();
                                lastSyncTimestamp = System.currentTimeMillis();
                                
                                if(Client.getInstance().getLocalGalaxy() != null && comm.getCurrentServerTurn() > Client.getInstance().getLocalGalaxy().getCurrentTurn()) {
                                    downloadNeeded = true;
                                }
                                if(chatLines != null) {
                                    chatQueue.addAll(chatLines);
                                }
                                while (chatQueue.size() > 300) {
                                    chatQueue.removeFirst();
                                }
                                if (chatWindow != null && chatWindow.isVisible()) {
                                    java.awt.EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            chatWindow.populateUserList(userList);

                                            if (chatWindow != null && chatWindow.isVisible() && chatQueue.size() > 0) {
                                                while (chatQueue.size() > 0) {
                                                    chatWindow.addChat(chatQueue.removeFirst());
                                                }
                                            }
                                        }
                                    });
                                }
                            } // USE LOCAL TIME WHEN NOT SYNCING WITH SERVER (EVERY 0.5 SECONDS)
                            else {
                                java.awt.EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        int time = (int) ((comm.getNextTurnDate() - System.currentTimeMillis()) / 1000);
                                        if (time < 0) {
                                            time = 0;
                                            downloadNeeded = true;
                                        }
                                        topMenuPanel.setTurnProgress(language.get("next_turn_in") + " " + time + " " + language.get("seconds"),
                                                (int) (localGalaxy.getTurnSpeed() / 1000) - time, (int) localGalaxy.getTurnSpeed() / 1000);
                                    }
                                });
                            }
                        }


                        // Download galaxy IF needed
                        System.out.println("download needed = " + downloadNeeded);
                        if (comm.isLoggedIn() && downloadNeeded) {
                            java.awt.EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    glassPanel.setVisible(true);
                                    desktop.moveToFront(glassPanel);
                                    topMenuPanel.setTurnProgressIndeterminate(true);
                                }
                            });

                            comm.downloadGalaxy();

                        }
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                }
            }
        };



        // Set up GUI
        desktop = new JDesktopPane();
//        ImageIcon bgImage = ImageManager.getInstance().getImage("graphics/bg.jpg");
        desktop.setBorder(new MapBorder(new Insets(0, 0, 0, 0)));
//        desktop.setBorder(new BackgroundBorder(bgImage.getImage(), 300, 0));


        TrekwarDesktopManager manager = new TrekwarDesktopManager();
        if (userProperties.getProperty("preventMoveWindowsOutOfDesktop").equals("true")) {
            manager.preventMoveWindowsOutOfDesktop(true);
        } else {
            manager.preventMoveWindowsOutOfDesktop(false);
        }
        desktop.setDesktopManager(manager);

        topMenuPanel = new TopMenuPanel();
        bottomMenuToolbarPanel = new BottomMenuToolbarPanel();
        bottomMenuPanel = new BottomMenuPanel(bottomMenuToolbarPanel);

        desktop.add(topMenuPanel, 1);
        desktop.add(bottomMenuPanel, 2);
        desktop.add(bottomMenuToolbarPanel, 3);

        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent evt) {
                resizeAndPosition();
            }
        });



        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        confirmExit();
                    }
                });


        desktop.add(glassPanel);
        desktop.moveToBack(glassPanel);


        getContentPane().setLayout(new GridLayout(1, 1));
        getContentPane().add(desktop);

        // Full screen
        boolean fullscreen = false;
        if (((String) userProperties.get("fullscreen")).equals("true")) {
            fullscreen = true;
        }
        if (fullscreen) {
            final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            setUndecorated(true);
            setResizable(false);
            device.setFullScreenWindow(instance);
        } else {
            try {
                int x = Integer.parseInt((String) userProperties.get("window.x"));
                int y = Integer.parseInt((String) userProperties.get("window.y"));
                int h = Integer.parseInt((String) userProperties.get("window.height"));
                int w = Integer.parseInt((String) userProperties.get("window.width"));

                setBounds(x, y, w, h);
            } catch (NumberFormatException nfe) {
                showError(language.get("invalid_data_in_properties_file,_window_x"), nfe, false, false);
            } catch (NullPointerException npe) {
                showError(language.get("data_missing_in_properties_file,_window_x"), npe, false, false);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
                try {
                    SubstanceLookAndFeel.setSkin(new TrekwarSkin());
                    //UIManager.put(SubstanceLookAndFeel.COLORIZATION_FACTOR, 1.0);
                } catch (Exception e) {
                    System.err.println(language.get("substance_raven_graphite_failed_to_initialize"));
                }
                Client c = new Client();
                c.setVisible(true);

                c.resizeAndPosition();
            }
        });
    }

    public void confirmExit() {
        int n = JOptionPane.showConfirmDialog(
                desktop,
                language.get("do_you_want_to_quit_the_game?"),
                language.get("confirm_exit"),
                JOptionPane.YES_NO_OPTION);

        if (n == JOptionPane.YES_OPTION) {
            if (reloadThread != null) {
                getMapFromServerThreadRunning = false;
                reloadThread.interrupt();
            }
            if (comm.isLoggedIn()) {
                comm.disconnect();
            }

            // save properties
            try {
                userProperties.setProperty("window.x", "" + getX());
                userProperties.setProperty("window.y", "" + getY());
                userProperties.setProperty("window.width", "" + getWidth());
                userProperties.setProperty("window.height", "" + getHeight());

                ImageManager.getInstance().storeAllImageFilenamesToFile();

                FileOutputStream s = new FileOutputStream(propertiesFilename);
                userProperties.storeToXML(s, "Trekwar2 user profile");
            } catch (IOException ioe) {
                showError(language.get("io_error_while_trying_to_save_properties_file"), ioe, false, false);
            }
            System.exit(0);
        }
    }

    public void connect() {
        LoginDialog loginDialog = new LoginDialog(true);
        if (!loginDialog.isAborted()) {
            try {
                String server = loginDialog.getServer();
                int port = loginDialog.getPort();
                String username = loginDialog.getUsername();
                String password = loginDialog.getPassword();

                if (server == null || username == null || password == null
                        || server.length() < 1 || username.length() < 1 || password.length() < 1) {
                    throw new Exception("Please provide server address/ip, port, username and password");
                }

                comm.connect(server, port);
                comm.login(username, password);

                if (comm.isLoggedIn()) {
                    soundSystem.playClip("server_connected.wav");
                    comm.resetNextTurnDate();
                    reloadThread.start();
                } else {
                    soundSystem.playClip("server_unable_to_connect.wav");
                    showMessage(language.get("login_error"));
                }

            } catch (UnknownHostException uhe) {
                loginDialog.dispose();
                showError(language.get("unable_to_connect_to_server,_host_not_found"), uhe, false, true);
            } catch (IOException ioe) {
                loginDialog.dispose();
                showError(language.get("io_error_while_trying_to_connect_to_server"), ioe, false, true);
            } catch (Exception e) {
                loginDialog.dispose();
                showError(language.get("error_while_trying_to_connect_to_server"), e, false, true);
            }
        }
        loginDialog.dispose();
    }

    public CargoTransferWindow getCargoTransferWindow() {
        if (cargoTransferWindow == null) {
            int cargoBoxX = Integer.parseInt(userProperties.getProperty("window.CargoTransferWindow.x"));
            int cargoBoxY = Integer.parseInt(userProperties.getProperty("window.CargoTransferWindow.x"));
            cargoTransferWindow = new CargoTransferWindow(Client.getLanguage().get("cargotransferwindow_title"), ImageManager.getInstance().getImage("graphics/misc_icons/cargotransfer.png"), cargoBoxX, cargoBoxY);
            cargoTransferWindow.setVisible(true);
            desktop.add(cargoTransferWindow, 1);
        }
        return cargoTransferWindow;
    }

    public void showCargoTransferWindow(boolean show) {
        if (cargoTransferWindow != null) {
            cargoTransferWindow.setVisible(show);
            if (show) {
                desktop.moveToFront(cargoTransferWindow);
            }
        }
    }

    public TroopTransferWindow getTroopTransferWindow() {
        if (troopTransferWindow == null) {
            int troopBoxX = Integer.parseInt(userProperties.getProperty("window.TroopTransferWindow.x"));
            int troopBoxY = Integer.parseInt(userProperties.getProperty("window.TroopTransferWindow.y"));
            troopTransferWindow = new TroopTransferWindow(Client.getLanguage().get("trooptransferwindow_title"), ImageManager.getInstance().getImage("graphics/misc_icons/trooptransfer.png"), troopBoxX, troopBoxY);
            troopTransferWindow.setVisible(true);
            desktop.add(troopTransferWindow, 1);
        }
        return troopTransferWindow;
    }

    public void showTroopTransferWindow(boolean show) {
        if (troopTransferWindow != null) {
            troopTransferWindow.setVisible(show);
            if (show) {
                desktop.moveToFront(troopTransferWindow);
            }
        }
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    public TopMenuPanel getTopMenuPanel() {
        return topMenuPanel;
    }

    public BottomMenuPanel getBottomGuiPanel() {
        return bottomMenuPanel;
    }

    public void showMinimapWindow() {
        if (comm.isLoggedIn() && localUser != null) {
            if (minimapWindow == null) {
                int x = 100; //Integer.parseInt(userProperties.getProperty("window.ChatWindow.x"));
                int y = 100; //Integer.parseInt(userProperties.getProperty("window.ChatWindow.y"));
                minimapWindow = new MinimapWindow(x, y);
                minimapWindow.setVisible(true);
                desktop.add(minimapWindow, -1);
                desktop.moveToFront(minimapWindow);
            } else {
                minimapWindow.setVisible(true);
                minimapWindow.reloadData(localGalaxy.getCurrentTurn());
            }
        }
    }

    public void openSystemControlView(StarSystem s) {
        if (systemControlWindow == null) {
            int scwX = Integer.parseInt(userProperties.getProperty("window.SystemControlWindow.x"));
            int scwY = Integer.parseInt(userProperties.getProperty("window.SystemControlWindow.y"));
            systemControlWindow = new SystemControlWindow(language.get("system_control"), ImageManager.getInstance().getImage("graphics/misc_icons/planet.png"), scwX, scwY);
            desktop.add(systemControlWindow);
            desktop.moveToFront(systemControlWindow);
        }
        systemControlWindow.setStarSystem(s);
        systemControlWindow.setVisible(true);
    }

    public void openResearchView() {
        if (researchWindow == null) {
            int x = Integer.parseInt(userProperties.getProperty("window.ResearchWindow.x"));
            int y = Integer.parseInt(userProperties.getProperty("window.ResearchWindow.y"));
            researchWindow = new ResearchWindow(x, y);
            researchWindow.setVisible(true);
            desktop.add(researchWindow, -1);
            desktop.moveToFront(researchWindow);
        } else {
            researchWindow.makeTechTree(localUser);
            researchWindow.setVisible(true);
        }
    }

    public void openShipDesignerView() {
        if (shipDesignerWindow == null) {
            int x = Integer.parseInt(userProperties.getProperty("window.ShipDesignerWindow.x"));
            int y = Integer.parseInt(userProperties.getProperty("window.ShipDesignerWindow.y"));
            shipDesignerWindow = new ShipDesignerWindow(language.get("ship_designer"), ImageManager.getInstance().getImage("graphics/misc_icons/brick.png"), x, y);
            shipDesignerWindow.setVisible(true);
            desktop.add(shipDesignerWindow, -1);
            desktop.moveToFront(shipDesignerWindow);
        } else {
            shipDesignerWindow.setVisible(true);
            shipDesignerWindow.populateComponentList();
        }
    }

    public void openChatWindow() {
        if (chatWindow == null) {
            int x = Integer.parseInt(userProperties.getProperty("window.ChatWindow.x"));
            int y = Integer.parseInt(userProperties.getProperty("window.ChatWindow.y"));
            chatWindow = new ChatWindow(language.get("chat"), ImageManager.getInstance().getImage("graphics/misc_icons/chat.png"), x, y);
            chatWindow.setVisible(true);
            desktop.add(chatWindow, -1);
            desktop.moveToFront(chatWindow);
        } else {
            chatWindow.setVisible(true);
        }
    }

    /**
     * Shows a message as a dialog window
     * 
     * @param message the message to show
     */
    public void showMessage(final String message) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JOptionPane.showInternalMessageDialog(desktop, message);
            }
        });
    }

    /**
     * Shows an error message
     * 
     * @param message the message to show
     * @param e the exception that caused the error (printed to log/console)
     * @param terminal true if the error should cause the game to terminate
     * @param logout true if error should log server out of server
     */
    public final void showError(final String message, final Exception e, final boolean terminal, final boolean logout) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                String details = "none";
                System.err.println(message + "\n====================================================\n\n" + e);
                System.err.println("\n\n============================================================");
                if (e != null) {
                    details = e.getMessage();
                    e.printStackTrace(System.err);
                }

                JOptionPane.showInternalMessageDialog(desktop, message + "\n\nDetails: " + details, language.get("error"), JOptionPane.ERROR_MESSAGE);

                Level level = Level.WARNING;
                if (terminal || logout) {
                    level = Level.SEVERE;
                }
                LOG.log(level, message, e);

                if (logout && comm != null) {
                    comm.disconnect();
                }

                if (terminal) {
                    System.exit(1);
                }
            }
        });
    }

    /**
     * Gets the currently logged in user or null if not logged in
     * 
     * @return the user playing the game
     */
    public User getLocalUser() {
        return localUser;
    }

    public void setLocalUser(User u) {
        localUser = u;
    }

    /**
     * Gets the local galaxy map
     * 
     * @return galaxy map
     */
    public StarSystem[][] getLocalMap() {
        return localGalaxy.getMap();
    }

    public JDesktopPane getDesktop() {
        return desktop;
    }

    public Properties getUserProperties() {
        return userProperties;
    }

    public SystemControlWindow getSystemControlWindow() {
        return systemControlWindow;
    }

    /**
     * Gets the sound system components, used for playing sounds
     * 
     * @return sound system
     */
    public SoundSystem getSoundSystem() {
        return soundSystem;
    }

    /**
     * Gets a user by username from the local galaxy object
     * 
     * @param username the username to find
     * 
     * @return the User object or null if not found
     */
    public User getUserByUsername(String username) {
        for (User u : localGalaxy.getUsers()) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Gets the local galaxy object
     * 
     * @return galaxy object
     */
    public Galaxy getLocalGalaxy() {
        return localGalaxy;
    }

    public void setLocalGalaxy(Galaxy g) {
        localGalaxy = g;
    }

    /**
     * Gets the I18N system
     * 
     * @return language system
     */
    public static Language getLanguage() {
        if (language == null) {
            try {
                language = new Language(Language.ENGLISH);
            } catch (IOException ioe) {
                System.out.println("FATAL ERROR::::: UNABLE TO LOAD DEFAULT LANGUAGE FILE (ENGLISH)");
                System.exit(1);
            }
        }
        return language;
    }

    public static NumberFormat getNumberFormat() {
        return numberFormat;
    }

    private static void initLogging() throws IOException {
        FileHandler fh = new FileHandler("client.log");
        Formatter logFormat = new Formatter() {

            @Override
            public String format(LogRecord rec) {
                StringBuilder buf = new StringBuilder(200);
                buf.append(new java.util.Date());
                buf.append(' ');
                buf.append(rec.getLevel());
                buf.append(' ');
                buf.append(rec.getSourceClassName()).append(".").append(rec.getSourceMethodName());
                buf.append(":\n");
                buf.append(formatMessage(rec));
                buf.append('\n');
                return buf.toString();
            }
        };
        fh.setFormatter(logFormat);

        ConsoleHandler ch = new ConsoleHandler();
        Formatter conlogFormat = new Formatter() {

            @Override
            public String format(LogRecord rec) {
                StringBuilder buf = new StringBuilder(200);
                buf.append(rec.getLevel());
                buf.append(": ");
                buf.append(formatMessage(rec));
                buf.append('\n');
                return buf.toString();
            }
        };
        ch.setFormatter(conlogFormat);

        LOG.addHandler(fh);
        LOG.addHandler(ch);
    }

    public static Client getInstance() {
        return instance;
    }

    public ClientCommunication getComm() {
        return comm;
    }

    public LinkedList<ChatLine> getChatQueue() {
        return chatQueue;
    }

    public void resizeAndPosition() {
        // keeps top menu horizontally centered

        if (mapPanel != null) {
            mapScrollPane.setSize(desktop.getSize());
            desktop.validate();
        }

        topMenuPanel.setLocation((Client.getInstance().getWidth() / 2) - (topMenuPanel.getWidth() / 2), 0);
        bottomMenuPanel.setLocation((Client.getInstance().getWidth() / 2) - (bottomMenuPanel.getWidth() / 2),
                Client.getInstance().getContentPane().getHeight() - bottomMenuPanel.getHeight());
        bottomMenuToolbarPanel.setLocation(bottomMenuPanel.getLocation().x, bottomMenuPanel.getLocation().y - 65);
    }

    public void galaxyDownloaded(Galaxy galaxy) throws InterruptedException, InvocationTargetException {
        Client.getInstance().getTopMenuPanel().setTurnProgressIndeterminate(false);
        Client.getInstance().setLocalGalaxy(galaxy);
        try {
            Client.getInstance().setLocalUser(galaxy.getUser(comm.getLocalUsername()));
        } catch (UserNotFoundException unfe) {
            Client.getInstance().showError(Client.getLanguage().get("unable_to_locate_your_user_object_in_local_galaxy_object"), unfe, false, true);
        }

        System.out.println("====== galaxyDownloaded() " + new Date() + "  CURRENT TURN= " + localGalaxy.getCurrentTurn());
        
        downloadNeeded = false;

        // Initial map setup (done once)
        if (mapPanel == null) {
            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    desktop.setBorder(null);
                    mapPanel = new MapPanel();
                    mapScrollPane = mapPanel.getScrollPane();
                    mapScrollPane.setBounds(0, 0, desktop.getWidth(), desktop.getHeight());

                    desktop.add(mapScrollPane, 0);
                    desktop.moveToFront(topMenuPanel);
                    desktop.moveToFront(bottomMenuPanel);
                    desktop.moveToFront(bottomMenuToolbarPanel);
                    resizeAndPosition();
                    mapPanel.drawMap();

                    bottomMenuPanel.updateMinimap();
                }
            });
        }

        java.awt.EventQueue.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                mapPanel.drawMap();
            }
        });

        // Update research
        java.awt.EventQueue.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                if (localUser.getCurrentResearch() != null) {
                    bottomMenuPanel.setResearchProgress(localUser);
                } else {
                    if(localUser.getTechs().size() < TechnologyGenerator.getAllTechs().size()) {
                        bottomMenuPanel.setNoResearch(true);
                    }
                }
            }
        });
      
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (topMenuPanel != null) {
                
                    topMenuPanel.setCurrentTurn(localGalaxy.getCurrentTurn());
                    topMenuPanel.setUpkeep(localUser.getShipUpkeepSupply(), localUser.getShipUpkeepUsed());
                }

                if (Client.getInstance().getMapPanel().getLastClickedTile() != null) {
                    bottomMenuPanel.displaySystem(localGalaxy.getMap()[Client.getInstance().getMapPanel().getLastClickedTile().getXloc()]
                                                                      [Client.getInstance().getMapPanel().getLastClickedTile().getYloc()]);
                }

                if (researchWindow != null && researchWindow.isVisible()) {
                    researchWindow.makeTechTree(localUser);
                }

                if (shipDesignerWindow != null && shipDesignerWindow.isVisible()) {
                    shipDesignerWindow.populateComponentList();
                }

                if (systemControlWindow != null && systemControlWindow.isVisible()) {
                    systemControlWindow.setStarSystem(localGalaxy.getMap()[Client.getInstance().getMapPanel().getLastClickedTile().getXloc()]
                                                                          [Client.getInstance().getMapPanel().getLastClickedTile().getYloc()]);
                }

                if (minimapWindow != null && minimapWindow.isVisible()) {
                    minimapWindow.reloadData(localGalaxy.getCurrentTurn());
                }
            }
        });


        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                glassPanel.setVisible(false);
                desktop.moveToBack(glassPanel);
            }
        });
    }
}