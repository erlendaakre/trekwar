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
package com.frostvoid.trekwar.server;

import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.frostvoid.trekwar.common.Galaxy;
import com.frostvoid.trekwar.common.User;
import com.frostvoid.trekwar.common.exceptions.UserNotFoundException;
import com.frostvoid.trekwar.common.utils.Language;
import com.frostvoid.trekwar.server.turnExec.TurnExecutor;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The server console application that loads a galaxy/game file
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TrekwarServer {

    public static final String serverName = "default trekwar server";
    public static final String serverURL = "http://www.trekwar.org";
    public static final String VERSION = "0.4.55";
    public static final String motd = "Thank you for testing out Trekwar alpha";
    
    public static final int clientTimeoutLimit = 20; // seconds
    private static Galaxy galaxy;
    private static String galaxyFileName;
    private static int port = 8472;
    private static int saveInterval = 5; // saves game to disk every X turns
    private static ServerSocket server;
    public static final Logger LOG = Logger.getLogger("trekwar_server");
    public static final SecureRandom PRNG = new SecureRandom();
    
    public static Language lang;

    public static void main(String[] args) {
        // load language
        try {
            lang = new Language(Language.ENGLISH);
        } catch (IOException ioe) {
            System.err.println("FATAL ERROR: Unable to load language file!");
            System.exit(1);
        }
        
            System.out.println(lang.get("trekwar_server") + " " + VERSION);
            System.out.println("==============================================".substring(0, lang.get("trekwar_server").length() + 1 + VERSION.length()));        
        
        // Handle parameters
        Options options = new Options();
        options.addOption(OptionBuilder.withArgName("file").withLongOpt("galaxy").hasArg().withDescription("the galaxy file to load").create("g")); //"g", "galaxy", true, "the galaxy file to load");
        options.addOption(OptionBuilder.withArgName("port number").withLongOpt("port").hasArg().withDescription("the port number to bind to (default 8472)").create("p"));
        options.addOption(OptionBuilder.withArgName("number").withLongOpt("save-interval").hasArg().withDescription("how often (in turns) to save the galaxy to disk (default: 5)").create("s"));
        options.addOption(OptionBuilder.withArgName("log level").withLongOpt("log").hasArg().withDescription("sets the log level: ALL, FINEST, FINER, FINE, CONFIG, INFO, WARNING, SEVERE, OFF").create("l"));
        options.addOption("h", "help", false, "prints this help message");
        
        CommandLineParser cliParser = new BasicParser();
        
        try {
            CommandLine cmd = cliParser.parse(options, args);
            String portStr = cmd.getOptionValue("p");
            String galaxyFileStr = cmd.getOptionValue("g");
            String saveIntervalStr = cmd.getOptionValue("s");
            String logLevelStr = cmd.getOptionValue("l");
            
            if(cmd.hasOption("h")) {
                 HelpFormatter help = new HelpFormatter();
                 help.printHelp("TrekwarServer", options);
                 System.exit(0);
            }
                        
            if(cmd.hasOption("g") && galaxyFileStr != null) {
                galaxyFileName = galaxyFileStr;
            }
            else {
                throw new ParseException("galaxy file not specified");
            }
            
            if(cmd.hasOption("p") && portStr != null) {
                    port = Integer.parseInt(portStr);
                    if (port < 1 || port > 65535) {
                        throw new NumberFormatException(lang.get("port_number_out_of_range"));
                    }
            }
            else {
                port = 8472;
            }
            
            if(cmd.hasOption("s") && saveIntervalStr != null) {
                    saveInterval = Integer.parseInt(saveIntervalStr);
                    if (saveInterval < 1 || saveInterval > 100) {
                        throw new NumberFormatException("Save Interval out of range (1-100)");
                    }                
            }
            else {
                saveInterval = 5;
            }
            
            if(cmd.hasOption("l") && logLevelStr != null) {
                if(logLevelStr.equalsIgnoreCase("finest")) {
                    LOG.setLevel(Level.FINEST);
                }
                else if(logLevelStr.equalsIgnoreCase("finer")) {
                    LOG.setLevel(Level.FINER);
                }
                else if(logLevelStr.equalsIgnoreCase("fine")) {
                    LOG.setLevel(Level.FINE);
                }
                else if(logLevelStr.equalsIgnoreCase("config")) {
                    LOG.setLevel(Level.CONFIG);
                }
                else if(logLevelStr.equalsIgnoreCase("info")) {
                    LOG.setLevel(Level.INFO);
                }
                else if(logLevelStr.equalsIgnoreCase("warning")) {
                    LOG.setLevel(Level.WARNING);
                }
                else if(logLevelStr.equalsIgnoreCase("severe")) {
                    LOG.setLevel(Level.SEVERE);
                }
                else if(logLevelStr.equalsIgnoreCase("off")) {
                    LOG.setLevel(Level.OFF);
                }
                else if(logLevelStr.equalsIgnoreCase("all")) {
                    LOG.setLevel(Level.ALL);
                }
                else {
                    System.err.println("ERROR: invalid log level: " + logLevelStr);
                    System.err.println("Run again with -h flag to see valid log level values");
                    System.exit(1);
                }
            }
            else {
                LOG.setLevel(Level.INFO);
            }
            // INIT LOGGING
            try {
                LOG.setUseParentHandlers(false);
                initLogging();
            } catch (IOException ex) {
                System.err.println("Unable to initialize logging to file");
                System.err.println(ex);
                System.exit(1);
            }
            
        } catch (Exception ex) {
            System.err.println("ERROR: " + ex.getMessage());
            System.err.println("use -h for help");
            System.exit(1);
        }

        LOG.log(Level.INFO, "Trekwar2 server " + VERSION + " starting up");

        // LOAD GALAXY
        File galaxyFile = new File(galaxyFileName);
        if (galaxyFile.exists()) {
            try {
                long timer = System.currentTimeMillis();
                LOG.log(Level.INFO, "Loading galaxy file {0}", galaxyFileName);
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(galaxyFile));
                galaxy = (Galaxy) ois.readObject();
                timer = System.currentTimeMillis() - timer;
                LOG.log(Level.INFO, "Galaxy file loaded in {0} ms", timer);
                ois.close();
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "IO error while trying to load galaxy file", ioe);
            } catch (ClassNotFoundException cnfe) {
                LOG.log(Level.SEVERE, "Unable to find class while loading galaxy", cnfe);
            }
        } else {
            System.err.println("Error: file " + galaxyFileName + " not found");
            System.exit(1);
        }


        // if turn == 0 (start of game), execute first turn to update fog of war.
        if (galaxy.getCurrentTurn() == 0) {
            TurnExecutor.executeTurn(galaxy);
        }
        
        LOG.log(Level.INFO, "Current turn  : {0}", galaxy.getCurrentTurn());
        LOG.log(Level.INFO, "Turn speed    : {0} seconds", galaxy.getTurnSpeed()/1000 );
        LOG.log(Level.INFO, "Save Interval : {0}", saveInterval);
        LOG.log(Level.INFO, "Users / max   : {0} / {1}", new Object[]{galaxy.getUserCount(), galaxy.getMaxUsers()});
        

        // START SERVER
        try {
            server = new ServerSocket(port);
            LOG.log(Level.INFO, "Server listening on port {0}", port);
        } catch (BindException be) {
            LOG.log(Level.SEVERE, "Error: Unable to bind to port {0}", port);
            System.err.println(be);
            System.exit(1);
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "Error: IO error while binding to port {0}", port);
            System.err.println(ioe);
            System.exit(1);
        }

        galaxy.startup();

        Thread timerThread = new Thread(new Runnable() {

            @Override
            @SuppressWarnings("SleepWhileInLoop")
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        // && galaxy.getLoggedInUsers().size() > 0 will make server pause when nobody is logged in (TESTING)
                        if (System.currentTimeMillis() > galaxy.nextTurnDate ) {
                            StringBuffer loggedInUsers = new StringBuffer();
                            for(User u : galaxy.getLoggedInUsers()) {
                                loggedInUsers.append(u.getUsername()).append(", ");
                            }

                            long time = TurnExecutor.executeTurn(galaxy);
                            LOG.log(Level.INFO, "Turn {0} executed in {1} ms", new Object[]{galaxy.getCurrentTurn(), time});
                            LOG.log(Level.INFO, "Logged in users: " + loggedInUsers.toString());
                            LOG.log(Level.INFO, "====================================================================================");

                            if (galaxy.getCurrentTurn() % saveInterval == 0) {
                                saveGalaxy();
                            }

                            galaxy.lastTurnDate = System.currentTimeMillis();
                            galaxy.nextTurnDate = galaxy.lastTurnDate + galaxy.turnSpeed;
                        }

                    } catch (InterruptedException e) {
                        LOG.log(Level.SEVERE, "Error in main server loop, interrupted", e);
                    }
                }
            }
        });
        timerThread.start();


        // ACCEPT CONNECTIONS AND DELEGATE TO CLIENT SESSIONS
        while (true) {
            Socket clientConnection;
            try {
                clientConnection = server.accept();
                ClientSession c = new ClientSession(clientConnection, galaxy);
                Thread t = new Thread(c);
                t.start();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "IO Exception while trying to handle incoming client connection", ex);
            }
        }
    }

    public static Galaxy getGalaxyFor(User currentUser) {
        // TODO, dont copy systems that is in fog of war
        // go trough galaxy and compute sensor ranges for the user.
        // See issue #36
        return galaxy;
    }

    /**
     * Gets the single galaxy instance this server is running
     * 
     * @return a galaxy object
     */
    public static Galaxy getGalaxy() {
        return galaxy;
    }

    /**
     * Gets the server message of the day
     * 
     * @return message of the day
     */
    public static String getMOTD() {
        return motd;
    }

    /**
     * Logs in a user on the server
     * 
     * @param user the username
     * @param password the password
     * 
     * @return the user object if successful, false if login failed
     */
    public static User login(String user, String password) {
        try {
            User u = galaxy.getUser(user);
            if (u != null && !u.getUsername().equals("nobody")
                    && u.getPassword().equals(password)) {
                return u;
            }
        } catch (UserNotFoundException e) {
        }
        return null;
    }

    /**
     * Saves the current game state to disk
     */
    private static void saveGalaxy() {
        File galaxyFile = new File(galaxyFileName);
        ObjectOutputStream oos;
        try {
            long time = System.currentTimeMillis();
            oos = new ObjectOutputStream(new FileOutputStream(galaxyFile));
            oos.writeObject(galaxy);
            time = System.currentTimeMillis() - time;
            LOG.log(Level.INFO, "Galaxy written to file in {0} ms", time);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Unable to write galaxy file to disk:");
            LOG.log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * Initiates logging
     * 
     * @throws IOException 
     */
    private static void initLogging() throws IOException {
        FileHandler fh = new FileHandler(galaxyFileName + ".log");
        fh.setLevel(LOG.getLevel());
        Formatter logFormat = new Formatter() {

            @Override
            public String format(LogRecord rec) {
                StringBuilder buf = new StringBuilder(200);
                buf.append("#");
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
        ch.setLevel(LOG.getLevel());
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

    /**
     * Gets the server Logger object
     * 
     * @return the logger object
     */
    public static Logger getLog() {
        return LOG;
    }
    
    /**
     * Gets the Language (i18n) object
     */
    public static Language getLanguage() {
        return lang;
    }
}