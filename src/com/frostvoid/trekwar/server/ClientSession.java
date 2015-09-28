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

import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.exceptions.InvalidOrderException;
import com.frostvoid.trekwar.common.exceptions.NotUniqueException;
import com.frostvoid.trekwar.common.exceptions.SlotException;
import com.frostvoid.trekwar.common.exceptions.UserNotFoundException;
import com.frostvoid.trekwar.common.net.messaging.*;
import com.frostvoid.trekwar.common.net.messaging.requests.*;
import com.frostvoid.trekwar.common.net.messaging.responses.*;
import com.frostvoid.trekwar.common.orders.*;
import com.frostvoid.trekwar.common.shipComponents.ShipComponent;
import com.frostvoid.trekwar.common.shipHulls.HullClass;
import com.frostvoid.trekwar.common.structures.Structure;
import com.frostvoid.trekwar.common.utils.Language;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

/**
 * Handles communication with a single client
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ClientSession implements Runnable {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Galaxy galaxy;
    private User currentUser;
    private long lastActivity = 0; // Unix time
    private long turnOfLastTransferedGalaxy = 0; // TODO don't let client download same galaxy twice

    /**
     * Creates a new client session
     *
     * @param socket the socket to communicate on
     * @param galaxy the galaxy object being used
     */
    public ClientSession(Socket socket, Galaxy galaxy) {
        this.socket = socket;
        this.galaxy = galaxy;
    }

    @Override
    public void run() {
        InetAddress remoteIP = socket.getInetAddress();
        TrekwarServer.getLog().log(Level.INFO, "Client connected from {0}", remoteIP);

        lastActivity = (System.currentTimeMillis() / 1000) + TrekwarServer.clientTimeoutLimit;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            writeResponse(new WelcomeMessage(TrekwarServer.serverName, TrekwarServer.VERSION, TrekwarServer.serverURL, TrekwarServer.getMOTD()));

            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ioe) {
            TrekwarServer.getLog().log(Level.WARNING, "IO error while connecting streams to socket for client with ip " + remoteIP, ioe);
        }

        boolean done = false;
        boolean loggedIn = false;
        // TODO: this class needs to be refactored.
        // - split all commands into methods
        // - create a command object that the server will use at turn execution
        //      to do the actual command, don't let this class modify game 
        //      objects directly. 
        try {
            while (!done) {
                if (lastActivity < ((System.currentTimeMillis() / 1000) - 30)) {
                    System.out.println("============================== CLIENT TIMED OUT");
                    TrekwarServer.getLog().log(Level.INFO, "Client timed out, ip: " + remoteIP);
                    lastActivity = -1;
                    throw new IOException("Client timed out, no activity for " + TrekwarServer.clientTimeoutLimit + " seconds");
                }

                Request requestObject = null;
                try {
                    requestObject = (Request) in.readObject();
                } catch (Exception ex) {
                    TrekwarServer.LOG.log(Level.SEVERE, "Bad request object from {0}:\n{1}", new Object[]{socket.getRemoteSocketAddress().toString(), ex.getMessage()});
                    break;
                }


                String userString = "not logged in";
                if (currentUser != null) {
                    userString = currentUser.getUsername();
                }
                TrekwarServer.LOG.log(Level.FINEST, "User {0} @ {1} sent request: {2}", new Object[]{userString, remoteIP.getHostAddress(), requestObject.getClass().getSimpleName()});

                //---------------- EXIT / LOGOUT ----------------\\
                if (requestObject instanceof LogoutRequest) {
                    done = true;
                    break;
                }
                //---------------- LOGIN ----------------\\
                else if (requestObject instanceof LoginRequest) {
                    LoginRequest login = (LoginRequest) requestObject;
                    User u = TrekwarServer.login(login.getUsername(), login.getPassword());
                    if (u != null) {
                        loggedIn = true;
                        currentUser = u;
                        galaxy.loginUser(currentUser, remoteIP.getHostAddress());
                        TrekwarServer.getLog().log(Level.INFO, "User {0} logged in from {1}", new Object[]{currentUser.getUsername(), remoteIP});
                        updateLastActivity();
                        writeResponse(new LoginResponse(true));
                    } else {
                        writeResponse(new LoginResponse(false));
                    }
                }

                //---------------- ALL COMMANDS EXCEPT LOGIN & EXIT (REQUIRES THAT USER IS LOGGED IN) ----------------\\
                else {

                    if (!loggedIn) {
                        // ignore command if not logged in
                    }
                    //---------------- TIME TO NEXT TURN ----------------\\
                    else if (requestObject instanceof TimeToNextTurnRequest) {
                        long res = (galaxy.nextTurnDate - System.currentTimeMillis());
                        updateLastActivity();
                        writeResponse(new TimeToNextTurnResponse(res, galaxy.getCurrentTurn(), galaxy.getCurrentTurn() + 1));
                    }
                    //---------------- CHANGE RESEARCH ----------------\\
                    else if (requestObject instanceof ResearchRequest) {
                        ResearchRequest researchRequest = (ResearchRequest) requestObject;
                        Technology researchTech = TechnologyGenerator.getTech(researchRequest.getTechToResearch());

                        if (researchTech != null) {
                            // make sure user only researches techs within range, and not techs already researched
                            if (researchTech.getLevel() == (currentUser.getHighestTech(researchTech.getType()).getLevel() + 1) && !currentUser.getTechs().contains(researchTech)) {
                                currentUser.setCurrentResearch(researchTech);
                                currentUser.setResearchPoints(0);
                                writeResponse(new ResearchResponse(true));
                                TrekwarServer.getLog().log(Level.FINER, "User {0} changed research to {1} - {2}", new Object[]{currentUser.getUsername(), researchTech.getType(), researchTech.getName()});
                            } else {
                                ResearchResponse response = new ResearchResponse(false);
                                response.setErrorMessage("invalid tech: out of range");
                                writeResponse(response);
                            }
                        } else {
                            ResearchResponse response = new ResearchResponse(false);
                            response.setErrorMessage("invalid tech: " + researchRequest.getTechToResearch());
                            writeResponse(response);
                        }

                    }
                    //---------------- RENAME FLEET ----------------\\
                    else if (requestObject instanceof RenameFleetRequest) {
                        RenameFleetRequest renameFleetRequest = (RenameFleetRequest) requestObject;
                        Fleet f = currentUser.getFleetByName(renameFleetRequest.getCurrentName());

                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to rename fleet {1} to {2}", new Object[]{currentUser.getUsername(), renameFleetRequest.getCurrentName(), renameFleetRequest.getNewName()});

                        if (renameFleetRequest.getNewName().length() < 2 || renameFleetRequest.getNewName().length() > 20) {
                            RenameFleetResponse response = new RenameFleetResponse(false);
                            response.setErrorMessage("fleet name must be between 2 and 20 characters long");
                            writeResponse(response);
                        } else if (f != null && f.getUser().equals(currentUser)) {
                            if (currentUser.isFleetNameAvailable(renameFleetRequest.getNewName())) {
                                f.setName(renameFleetRequest.getNewName());
                                writeResponse(new RenameFleetResponse(true));
                            } else {
                                RenameFleetResponse response = new RenameFleetResponse(false);
                                response.setErrorMessage("Fleet " + renameFleetRequest.getCurrentName() + " not found, or name " + renameFleetRequest.getNewName() + " is not valid");
                                writeResponse(response);
                            }
                        } else {
                            RenameFleetResponse response = new RenameFleetResponse(false);
                            response.setErrorMessage("Fleet " + renameFleetRequest.getCurrentName() + " not found, or does not belong to current user");
                            TrekwarServer.getLog().log(Level.FINER, "Fleet not found or does not belong to current user'");
                            writeResponse(response);
                        }
                    }
                    //---------------- NEW FLEET ----------------\\
                    else if (requestObject instanceof NewFleetRequest) {
                        NewFleetRequest newFleetRequest = (NewFleetRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to make new fleet named {1} at {2},{3}", new Object[]{currentUser.getUsername(), newFleetRequest.getName(), newFleetRequest.getX(), newFleetRequest.getY()});

                        if (newFleetRequest.getName().length() < 2 || newFleetRequest.getName().length() > 20) {
                            NewFleetResponse response = new NewFleetResponse(false);
                            response.setErrorMessage("fleet name must be between 2 and 20 characters long");
                            writeResponse(response);
                        } else if (galaxy.getMap()[newFleetRequest.getX()][newFleetRequest.getY()] == null) {
                            NewFleetResponse response = new NewFleetResponse(false);
                            response.setErrorMessage("fleet location [" + newFleetRequest.getX() + "," + newFleetRequest.getY() + "] is not a valid starsystem");
                            writeResponse(response);
                        } else {
                            if (currentUser.isFleetNameAvailable(newFleetRequest.getName())) {
                                Fleet f = new Fleet(currentUser, newFleetRequest.getName(), galaxy.getSystem(newFleetRequest.getX(), newFleetRequest.getY()));
                                try {
                                    currentUser.addFleet(f);
                                    galaxy.getMap()[newFleetRequest.getX()][newFleetRequest.getY()].addFleet(f);
                                    writeResponse(new NewFleetResponse(true));
                                    TrekwarServer.getLog().log(Level.FINER, "Fleet added'");
                                } catch (NotUniqueException ex) {
                                    NewFleetResponse response = new NewFleetResponse(false);
                                    response.setErrorMessage("NotUniqueException: " + ex.getMessage());
                                    writeResponse(response);
                                    TrekwarServer.getLog().log(Level.SEVERE, "New fleet name become unavailable before fleet could be added to user/galaxy");
                                }
                            } else {
                                NewFleetResponse response = new NewFleetResponse(false);
                                response.setErrorMessage("Fleet name unavailable: " + newFleetRequest.getName());
                                writeResponse(response);
                                TrekwarServer.getLog().log(Level.FINER, "Fleet name not available'");
                            }
                        }
                    }
                    //---------------- MOVE SHIP TO FLEET ----------------\\
                    else if (requestObject instanceof MoveShipToFleetRequest) {
                        MoveShipToFleetRequest moveShipToFleetRequest = (MoveShipToFleetRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to move ship with id {1} from fleet {2} to fleet {3}", new Object[]{currentUser.getUsername(), moveShipToFleetRequest.getShipID(), moveShipToFleetRequest.getSourceFleet(), moveShipToFleetRequest.getDestinationFleet()});
                        Fleet sourceFleet = currentUser.getFleetByName(moveShipToFleetRequest.getSourceFleet());
                        Fleet destinationFleet = currentUser.getFleetByName(moveShipToFleetRequest.getDestinationFleet());

                        Ship ship = sourceFleet.getShipById(moveShipToFleetRequest.getShipID());

                        if (moveShipToFleetRequest.getSourceFleet().equals(moveShipToFleetRequest.getDestinationFleet())) {
                            writeResponse(new MoveShipToFleetResponse("source and destination fleet are identical"));
                        } else if (sourceFleet == null || destinationFleet == null) {
                            writeResponse(new MoveShipToFleetResponse("Invalid source or destination fleet (null)"));
                        } else if (ship == null) {
                            writeResponse(new MoveShipToFleetResponse("ship not found (null)"));
                        } else {
                            sourceFleet.removeShip(ship);
                            destinationFleet.addShip(ship);
                            writeResponse(new MoveShipToFleetResponse(true));
                            TrekwarServer.getLog().finer("Ship moved between fleets");
                        }
                    }
                    //---------------- DECOMMISSION / SELF DESTRUCT SHIP ----------------\\
                    else if (requestObject instanceof DestroyDecommissionShipRequest) {
                        DestroyDecommissionShipRequest ddsRequest = (DestroyDecommissionShipRequest) requestObject;
                        Fleet sourceFleet = currentUser.getFleetByName(ddsRequest.getSourceFleet());
                        Ship ship = sourceFleet.getShipById(ddsRequest.getShipID());

                        TrekwarServer.getLog().log(Level.FINE, "User {0} is deleting ship with id {1} in fleet {2}", new Object[]{currentUser.getUsername(), ddsRequest.getShipID(), ddsRequest.getSourceFleet()});
                        if (ship == null) {
                            writeResponse(new DestroyDecommissionShipResponse("ship not found (null)"));
                        } else {
                            TurnReportItem tr = new TurnReportItem(galaxy.getCurrentTurn(), sourceFleet.getX(), sourceFleet.getY(), TurnReportItem.TurnReportSeverity.MEDIUM);
                            tr.setSummary(TrekwarServer.getLanguage().get("turn_report_ship_self_destruct_1"));
                            tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_ship_self_destruct_1"), ship.getName(), sourceFleet.getName()));

                            if (galaxy.getSystem(sourceFleet).getUser().equals(sourceFleet.getUser())) {
                                tr.setSummary(TrekwarServer.getLanguage().get("turn_report_ship_decommissioned_1"));
                                tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_ship_decommissioned_2"), ship.getName(), sourceFleet.getName(), galaxy.getSystem(sourceFleet).getName()));
                                if (!galaxy.getSystem(sourceFleet).getBuildQueue().isEmpty()) {
                                    int industryBonus = 0;
                                    if (galaxy.getSystem(sourceFleet).getBuildQueue().get(0) instanceof BuildShipOrder) {
                                        BuildShipOrder bso = (BuildShipOrder) galaxy.getSystem(sourceFleet).getBuildQueue().get(0);
                                        industryBonus = ship.getCost() / StaticData.SHIP_DECOMMISSION_FACTOR_INDUSTRY_TO_SHIP;
                                        bso.setIndustryInvested(bso.getIndustryInvested() + industryBonus);
                                        tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_ship_decommissioned_3"), ship.getName(), sourceFleet.getName(), galaxy.getSystem(sourceFleet).getName(), industryBonus, bso.getTemplate().getName()));
                                    }
                                    if (galaxy.getSystem(sourceFleet).getBuildQueue().get(0) instanceof BuildStructureOrder) {
                                        BuildStructureOrder bso = (BuildStructureOrder) galaxy.getSystem(sourceFleet).getBuildQueue().get(0);
                                        industryBonus = ship.getCost() / StaticData.SHIP_DECOMMISSION_FACTOR_INDUSTRY_TO_STRUCTURE;
                                        bso.setIndustryInvested(bso.getIndustryInvested() + industryBonus);
                                        tr.setDetailed(Language.pop(TrekwarServer.getLanguage().get("turn_report_ship_decommissioned_4"), ship.getName(), sourceFleet.getName(), galaxy.getSystem(sourceFleet).getName(), industryBonus, bso.getStructure().getName()));
                                    }
                                }
                            }
                            ship.destroy();
                            TrekwarServer.getLog().finer("ship destroyed");
                            currentUser.addTurnReport(tr);
                            writeResponse(new DestroyDecommissionShipResponse(true));
                        }
                    }
                    //---------------- MOVE FLEET ----------------\\
                    else if (requestObject instanceof MoveFleetRequest) {
                        MoveFleetRequest moveFleetRequest = (MoveFleetRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to move fleet named {1} to: {2},{3}'", new Object[]{currentUser.getUsername(), moveFleetRequest.getFleetName(), moveFleetRequest.getX(), moveFleetRequest.getY()});
                        Fleet fleet = currentUser.getFleetByName(moveFleetRequest.getFleetName());

                        if (fleet != null && moveFleetRequest.getX() >= 0 && moveFleetRequest.getX() < galaxy.getMap().length &&
                                moveFleetRequest.getY() >= 0 && moveFleetRequest.getY() < galaxy.getMap()[moveFleetRequest.getX()].length) {
                            MoveOrder mo = new MoveOrder(fleet, moveFleetRequest.getX(), moveFleetRequest.getY());
                            fleet.setOrder(mo);
                            writeResponse(new MoveFleetResponse(true));
                            TrekwarServer.getLog().log(Level.FINER, "Fleet found at {0},{1}. Move order set", new Object[]{fleet.getX(), fleet.getY()});

                        } else {
                            MoveFleetResponse response = new MoveFleetResponse(false);
                            response.setErrorMessage("invalid fleet or coordinates out of bounds");
                            TrekwarServer.getLog().finer("Fleet object not found, or target coordinates out of bounds");
                            writeResponse(response);
                        }
                    }
                    //---------------- CANCEL ORDERS ----------------\\
                    else if (requestObject instanceof CancelOrdersRequest) {
                        CancelOrdersRequest cancelOrderRequest = (CancelOrdersRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to cancel orders for fleet named {1}", new Object[]{currentUser.getUsername(), cancelOrderRequest.getFleetName()});

                        Fleet f = currentUser.getFleetByName(cancelOrderRequest.getFleetName());
                        if (f != null) {
                            f.setOrder(null);
                            TrekwarServer.getLog().finer("Fleet found, current order removed");
                            CancelOrdersResponse response = new CancelOrdersResponse(true);
                            writeResponse(response);
                        } else {
                            CancelOrdersResponse response = new CancelOrdersResponse(false);
                            TrekwarServer.getLog().finer("Fleet not found");
                            response.setErrorMessage("fleet with name " + cancelOrderRequest.getFleetName() + " not found for current user");
                            writeResponse(response);
                        }
                    }
                    //---------------- COLONIZE SYSTEM ----------------\\
                    else if (requestObject instanceof ColonizeRequest) {
                        ColonizeRequest colonizeRequest = (ColonizeRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to colonize with fleet named {1} ", new Object[]{currentUser.getUsername(), colonizeRequest.getFleetName()});
                        Fleet f = currentUser.getFleetByName(colonizeRequest.getFleetName());

                        if (f != null) {
                            StarSystem system = galaxy.getMap()[f.getX()][f.getY()];
                            Ship colonyship = null;
                            for (Ship s : f.getShips()) {
                                if (s.canColonize()) {
                                    colonyship = s;
                                    TrekwarServer.getLog().finer("found a colonyship with id " + s.getShipId());
                                    break;
                                }
                            }
                            try {
                                if (colonyship != null && colonyship.canColonize()) {
                                    synchronized (system) {
                                        int numberOfColonizeOrdersInSystem = StaticData.countNumberOfColonizeOrdersInSystem(system);
                                        TrekwarServer.getLog().finer("Number of colonize orders in this system: " + numberOfColonizeOrdersInSystem);
                                        if (numberOfColonizeOrdersInSystem == 0) {
                                            if (system.getMaxStructures() >= StaticData.MAX_STRUCTURES_NEEDED_TO_COLONIZE) {
                                                ColonizeOrder co = new ColonizeOrder(currentUser, galaxy.getMap()[f.getX()][f.getY()], f, colonyship);
                                                TrekwarServer.getLog().finer("Fleet ordered to colonize system");
                                                f.setOrder(co);
                                                writeResponse(new ColonizeResponse(true));
                                            } else {
                                                TrekwarServer.getLog().finer("System too small to colonize");
                                                writeResponse(new ColonizeResponse("System too small, must have room for at least 10 structures"));
                                            }
                                        } else {
                                            TrekwarServer.getLog().finer("System is already being colonized by another fleet");
                                            writeResponse(new ColonizeResponse("Someone already started colonizing that system"));
                                        }
                                    }
                                } else {
                                    throw new InvalidOrderException("Ship did not have ability to colonize");
                                }
                            } catch (InvalidOrderException ex) {
                                TrekwarServer.getLog().finer("Fleet unable to colonize system: " + ex.getMessage());
                                writeResponse(new ColonizeResponse("Fleet unable to colonize: " + ex.getMessage()));
                            }
                        } else {
                            writeResponse(new ColonizeResponse("Invalid fleet name"));
                        }
                    }
                    //---------------- INVADE SYSTEM ----------------\\
                    else if (requestObject instanceof InvadeRequest) {
                        InvadeRequest invadeRequest = (InvadeRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to invade a system with fleet named {1} ", new Object[]{currentUser.getUsername(), invadeRequest.getFleetName()});

                        Fleet f = currentUser.getFleetByName(invadeRequest.getFleetName());
                        if (f != null && f.getTroops() > 0) {
                            InvadeSystemOrder iso = new InvadeSystemOrder(f, galaxy.getSystem(f.getX(), f.getY()));
                            TrekwarServer.getLog().finer("Invading system at " + f.getX() + "," + f.getY() + " named " + galaxy.getSystem(f).getName());
                            f.setOrder(iso);
                            writeResponse(new InvadeResponse(true));
                        } else {
                            TrekwarServer.getLog().finer("Unable to invade, fleet not found or has no troops");
                            writeResponse(new InvadeResponse("Fleet not found, or had no troops"));
                        }
                    }
                    //---------------- ORBITAL BOMBARDMENT ----------------\\
                    else if (requestObject instanceof OrbitalBombardmentRequest) {
                        OrbitalBombardmentRequest bombRequest = (OrbitalBombardmentRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to invade a system with fleet named {1} ", new Object[]{currentUser.getUsername(), bombRequest.getFleetName()});

                        Fleet f = currentUser.getFleetByName(bombRequest.getFleetName());
                        StarSystem s = galaxy.getSystem(f);
                        if (f != null && f.canBombPlanets() && !f.getUser().getFaction().equals(s.getUser().getFaction())) {
                            try {
                                TrekwarServer.getLog().finer("Fleet can bomb planets, bombing planets at " + f.getX() + "," + f.getY() + " named " + galaxy.getSystem(f).getName());
                                OrbitalBombardmentOrder obo = new OrbitalBombardmentOrder(f, s);
                                f.setOrder(obo);
                                writeResponse(new OrbitalBombardmentResponse(true));
                            } catch (InvalidOrderException ioe) {
                                writeResponse(new OrbitalBombardmentResponse("Invalid order: " + ioe.getMessage()));
                            }
                        } else {
                            writeResponse(new OrbitalBombardmentResponse("fleet null, can't bomb planets or target system is not enemy"));
                        }
                    }
                    //---------------- MINE ASTEROID ----------------\\
                    else if (requestObject instanceof MineRequest) {
                        MineRequest mineRequest = (MineRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to mine asteroids with fleet named {1} ", new Object[]{currentUser.getUsername(), mineRequest.getFleetName()});

                        Fleet f = currentUser.getFleetByName(mineRequest.getFleetName());
                        try {
                            if (f.canMine()) {
                                TrekwarServer.getLog().finer("Fleet can mine, will mine at " + f.getX() + "," + f.getY() + " named " + galaxy.getSystem(f).getName());
                                MiningOrder mo = new MiningOrder(currentUser, galaxy.getMap()[f.getX()][f.getY()], f);
                                f.setOrder(mo);
                                writeResponse(new MineResponse(true));
                            } else {
                                throw new InvalidOrderException("Fleet could not mine asteroids");
                            }
                        } catch (InvalidOrderException ex) {
                            writeResponse(new MineResponse("Unable to mine: " + ex.getMessage()));
                        }
                    }
                    //---------------- HARVEST DEUTERIUM FROM NEBULA ----------------\\
                    else if (requestObject instanceof HarvestRequest) {
                        HarvestRequest harvestRequest = (HarvestRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to harvest deuterium with fleet named {1} ", new Object[]{currentUser.getUsername(), harvestRequest.getFleetName()});

                        Fleet f = currentUser.getFleetByName(harvestRequest.getFleetName());
                        try {
                            if (f.canHarvestDeuterium()) {
                                TrekwarServer.getLog().finer("Fleet can mine, will mine at " + f.getX() + "," + f.getY() + " named " + galaxy.getSystem(f).getName());
                                HarvestDeuteriumOrder hdo = new HarvestDeuteriumOrder(currentUser, galaxy.getMap()[f.getX()][f.getY()], f);
                                f.setOrder(hdo);
                                writeResponse(new HarvestResponse(true));
                            } else {
                                throw new InvalidOrderException("Fleet could not harvest deuterium from nebula");
                            }
                        } catch (InvalidOrderException ex) {
                            writeResponse(new HarvestResponse("Unable to mine: " + ex.getMessage()));
                        }
                    }
                    //---------------- TRANSFER TROOPS BETWEEN SHIP AND SYSTEM ----------------\\
                    else if (requestObject instanceof TroopTransferRequest) {
                        TroopTransferRequest troopTransferRequest = (TroopTransferRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to transfer {1} troops to/from ship with id {2} ", new Object[]{currentUser.getUsername(), troopTransferRequest.getAmount(), troopTransferRequest.getShipID()});

                        try {
                            StarSystem system = galaxy.getMap()[troopTransferRequest.getX()][troopTransferRequest.getY()];
                            Ship ship = system.getShipById(currentUser, troopTransferRequest.getShipID());
                            if (troopTransferRequest.getAmount() < 1) {
                                throw new NumberFormatException("amount less than 1");
                            }

                            if (system != null && system.getUser().equals(currentUser) && ship != null && ship.getUser().equals(currentUser)) {
                                if (troopTransferRequest.getType().equals(TroopTransferRequestType.SHIPTOSYSTEM)) {
                                    if (troopTransferRequest.getAmount() <= ship.getTroops()) {
                                        ship.setTroops(ship.getTroops() - troopTransferRequest.getAmount());
                                        system.setTroopCount(system.getTroopCount() + troopTransferRequest.getAmount());
                                        writeResponse(new TroopTransferResponse(true));
                                        TrekwarServer.getLog().log(Level.FINER, "user {0} transferred {1} troops to system: {2}", new Object[]{system.getUser(), troopTransferRequest.getAmount(), system.getName()});
                                    } else {
                                        throw new InvalidOrderException("Could not transfer more troops than ship has");
                                    }
                                } else if (troopTransferRequest.getType().equals(TroopTransferRequestType.SYSTEMTOSHIP)) {
                                    if (troopTransferRequest.getAmount() <= system.getTroopCount()) {
                                        ship.setTroops(ship.getTroops() + troopTransferRequest.getAmount());
                                        system.setTroopCount(system.getTroopCount() - troopTransferRequest.getAmount());
                                        writeResponse(new TroopTransferResponse(true));
                                        TrekwarServer.getLog().log(Level.FINER, "user {0} transferred {1} troops from system: {2}", new Object[]{system.getUser(), troopTransferRequest.getAmount(), system.getName()});
                                    } else {
                                        throw new InvalidOrderException("Could not transfer more troops than starsystem has");
                                    }
                                } else {
                                    throw new InvalidOrderException("Invalid Transfer type");
                                }
                            } else {
                                throw new InvalidOrderException("System not found, not owned by you or ship not found or not owned by you");
                            }
                        } catch (Exception ex) {
                            writeResponse(new TroopTransferResponse("Error: " + ex.getMessage()));
                        }
                    }
                    //---------------- TRANSFER CARGO BETWEEN SHIP AND SYSTEM ----------------\\
                    else if (requestObject instanceof CargoTransferRequest) {
                        CargoTransferRequest cargoTransferRequest = (CargoTransferRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to transfer {1} {2} to/from ship with id {3} ", new Object[]{currentUser.getUsername(), cargoTransferRequest.getAmount(), cargoTransferRequest.getCargoClassification(), cargoTransferRequest.getShipID()});

                        try {
                            StarSystem system = galaxy.getMap()[cargoTransferRequest.getX()][cargoTransferRequest.getY()];
                            Ship ship = system.getShipById(currentUser, cargoTransferRequest.getShipID());
                            if (cargoTransferRequest.getAmount() < 1) {
                                throw new NumberFormatException("amount less than 1");
                            }

                            if (cargoTransferRequest.getType().equals(CargoTransferRequestType.SHIPTOSYSTEM)) {
                                if (system != null && system.getUser().equals(currentUser) && ship != null && ship.getUser().equals(currentUser)) {
                                    if (cargoTransferRequest.getCargoClassification().equals(CargoClassification.deuterium) && system.hasDeuteriumPlant()) {
                                        if (cargoTransferRequest.getAmount() <= ship.getCargoDeuterium()) {
                                            ship.setCargoDeuterium(ship.getCargoDeuterium() - cargoTransferRequest.getAmount());
                                            system.addDeuterium(cargoTransferRequest.getAmount());
                                            TrekwarServer.getLog().log(Level.FINER, "user {0} added {1} deuterium to system: {2}", new Object[]{system.getUser(), cargoTransferRequest.getAmount(), system.getName()});
                                        } else {
                                            throw new InvalidOrderException("Could not add more deuterium than ship has");
                                        }
                                    } else if (cargoTransferRequest.getCargoClassification().equals(CargoClassification.ore) && system.hasOreRefinery()) {
                                        if (cargoTransferRequest.getAmount() <= ship.getCargoOre()) {
                                            ship.setCargoOre(ship.getCargoOre() - cargoTransferRequest.getAmount());
                                            system.addOre(cargoTransferRequest.getAmount());
                                            TrekwarServer.getLog().log(Level.FINER, "user {0} added {1} ore to system: {2}", new Object[]{system.getUser(), cargoTransferRequest.getAmount(), system.getName()});
                                        } else {
                                            throw new InvalidOrderException("Could not add more ore than ship has");
                                        }
                                    } else {
                                        throw new InvalidOrderException("System " + system + " could not handle cargo of type: " + cargoTransferRequest.getCargoClassification().toString());
                                    }
                                    writeResponse(new CargoTransferResponse(true));
                                } else {
                                    throw new InvalidOrderException("System not found, not owned by you or ship not found or not owned by you");
                                }
                            } else if (cargoTransferRequest.getType().equals(CargoTransferRequestType.SYSTEMTOSHIP)) {
                                if (!ship.canLoadUnloadCargo() && ship.getAvailableCargoSpace() <= 0) {
                                    throw new InvalidOrderException("Ship has no cargo space available");
                                }

                                if (system != null && system.getUser().equals(currentUser) && ship != null && ship.getUser().equals(currentUser)) {
                                    if (cargoTransferRequest.getCargoClassification().equals(CargoClassification.deuterium) && system.hasDeuteriumPlant()) {
                                        if (cargoTransferRequest.getAmount() <= ship.getAvailableCargoSpace()) {
                                            ship.setCargoDeuterium(ship.getCargoDeuterium() + cargoTransferRequest.getAmount());
                                            system.removeDeuterium(cargoTransferRequest.getAmount());
                                            TrekwarServer.getLog().log(Level.FINER, "user {0} removed {1} deuterium from system: {2}", new Object[]{system.getUser(), cargoTransferRequest.getAmount(), system.getName()});
                                        } else {
                                            throw new InvalidOrderException("Could not add more deuterium than can fit in cargo hold");
                                        }
                                    } else if (cargoTransferRequest.getCargoClassification().equals(CargoClassification.ore) && system.hasOreRefinery()) {
                                        if (cargoTransferRequest.getAmount() <= ship.getAvailableCargoSpace()) {
                                            ship.setCargoOre(ship.getCargoOre() + cargoTransferRequest.getAmount());
                                            system.removeOre(cargoTransferRequest.getAmount());
                                            TrekwarServer.getLog().log(Level.FINER, "user {0} removed {1} ore from system: {2}", new Object[]{system.getUser(), cargoTransferRequest.getAmount(), system.getName()});
                                        } else {
                                            throw new InvalidOrderException("Could not add more ore than can fit in cargo hold");
                                        }
                                    } else {
                                        throw new InvalidOrderException("System " + system + " could not handle cargo of type: " + cargoTransferRequest.getCargoClassification().toString());
                                    }
                                    writeResponse(new CargoTransferResponse(true));
                                } else {
                                    throw new InvalidOrderException("System not found, not owned by you or ship not found or not owned by you");
                                }
                            } else {
                                throw new InvalidOrderException("Invalid Transfer type");
                            }
                        } catch (Exception ex) {
                            TrekwarServer.getLog().warning("Unable to move cargo: " + ex.getMessage());
                            writeResponse(new CargoTransferResponse("Error: " + ex.getMessage()));
                        }
                    }
                    //---------------- ENABLE / DISABLE STRUCTURE ----------------\\
                    else if (requestObject instanceof StructureStateChangeRequest) {
                        StructureStateChangeRequest structureStateChangeRequest = (StructureStateChangeRequest) requestObject;

                        StarSystem system = galaxy.getSystem(structureStateChangeRequest.getX(), structureStateChangeRequest.getY());
                        Planet planet = system.getPlanetByNumber(structureStateChangeRequest.getPlanetNumber());

                        if (system.getUser().equals(currentUser) && planet != null) {
                            try {
                                if (structureStateChangeRequest.getType().equals(StructureStateChangeRequestType.ENABLE)) {
                                    planet.setStructureEnabled(structureStateChangeRequest.getSlotNumber(), true);
                                    TrekwarServer.getLog().log(Level.FINE, "User {0} enabled a {1} structure at slot {2} in the {3} system", new Object[]{currentUser.getUsername(), planet.getStructuresMap().get(structureStateChangeRequest.getSlotNumber()).getName(), structureStateChangeRequest.getSlotNumber(), system.getName()});
                                } else {
                                    planet.setStructureEnabled(structureStateChangeRequest.getSlotNumber(), false);
                                    TrekwarServer.getLog().log(Level.FINE, "User {0} disabled a {1} structure at slot {2} in the {3} system", new Object[]{currentUser.getUsername(), planet.getStructuresMap().get(structureStateChangeRequest.getSlotNumber()).getName(), structureStateChangeRequest.getSlotNumber(), system.getName()});
                                }
                                writeResponse(new StructureStateChangeResponse(true));
                            } catch (IndexOutOfBoundsException ioobe) {
                                writeResponse(new StructureStateChangeResponse("Wrong planet slot: " + ioobe.getMessage()));
                            }
                        } else {
                            writeResponse(new StructureStateChangeResponse("Failed to get planet, or you are not owner of the starsystem"));
                        }
                    }
                    //---------------- BUILD SHIP ----------------\\
                    else if (requestObject instanceof BuildShipRequest) {
                        BuildShipRequest buildShipRequest = (BuildShipRequest) requestObject;

                        StarSystem system = galaxy.getSystem(buildShipRequest.getX(), buildShipRequest.getY());
                        ShipTemplate template = currentUser.getShipTemplate(buildShipRequest.getTemplate());

                        if (template != null && system != null && system.getUser().equals(currentUser) && system.hasShipyard()) {
                            TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to build a {1} class ship in the {2} system ", new Object[]{currentUser.getUsername(), template.getName(), system.getName()});
                            BuildShipOrder bso = new BuildShipOrder(currentUser, system, template);
                            try {
                                system.addBuildOrder(bso);
                                writeResponse(new BuildShipResponse(true));
                            } catch (InvalidOrderException ex) {
                                TrekwarServer.getLog().log(Level.WARNING, "User '" + currentUser.getUsername() + "' unable to add ship build order in system: '" + system.getName() + "'", ex);
                                writeResponse(new BuildShipResponse("Unable to add order to build queue: " + ex.getMessage()));
                            }
                        } else {
                            writeResponse(new BuildShipResponse("Unable to get template or system, or system has wrong user or no shipyard"));
                        }
                    }
                    //---------------- HURRY PRODUCTION----------------\\
                    else if (requestObject instanceof HurryProductionRequest) {
                        HurryProductionRequest hurryProductionRequest = (HurryProductionRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to hurry production in the system at {1},{2} ", new Object[]{currentUser.getUsername(), hurryProductionRequest.getX(), hurryProductionRequest.getY()});

                        StarSystem system = galaxy.getSystem(hurryProductionRequest.getX(), hurryProductionRequest.getY());

                        if (system != null && system.getUser().equals(currentUser)) {
                            if (system.getBuildQueue().get(hurryProductionRequest.getIndex() - 1) != null) {
                                if (hurryProductionRequest.getAmount() <= system.getOre()) {
                                    Order o = system.getBuildQueue().get(hurryProductionRequest.getIndex() - 1);
                                    if (o instanceof BuildStructureOrder) {
                                        BuildStructureOrder bso = (BuildStructureOrder) o;
                                        bso.setIndustryInvested(bso.getIndustryInvested() + hurryProductionRequest.getAmount());
                                    }
                                    if (o instanceof BuildShipOrder) {
                                        BuildShipOrder bso = (BuildShipOrder) o;
                                        bso.setIndustryInvested(bso.getIndustryInvested() + hurryProductionRequest.getAmount());
                                    }
                                    system.setOre(system.getOre() - hurryProductionRequest.getAmount());
                                    TrekwarServer.getLog().log(Level.FINE, "User {0} hurried production by {1} in system {2}", new Object[]{currentUser.getUsername(), hurryProductionRequest.getAmount(), system.getName()});
                                    writeResponse(new HurryProductionResponse(true));
                                } else {
                                    writeResponse(new HurryProductionResponse("Insufficient ore in system"));
                                }
                            } else {
                                writeResponse(new HurryProductionResponse("Invalid index for build queue"));
                            }
                        } else {
                            writeResponse(new HurryProductionResponse("Invalid starsystem (null or not owned by you)"));
                        }
                    }
                    //---------------- MOVE/DELETE ITEM IN BUILD QUEUE ----------------\\
                    else if (requestObject instanceof BuildQueueRequest) {
                        BuildQueueRequest buildQueueRequest = (BuildQueueRequest) requestObject;

                        StarSystem system = galaxy.getSystem(buildQueueRequest.getX(), buildQueueRequest.getY());

                        if (system != null && system.getUser().equals(currentUser)) {
                            TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to modify the build queue of system {1}, by applying {2} to item at index {3} ", new Object[]{currentUser.getUsername(), system.getName(), buildQueueRequest.getAction().toString(), buildQueueRequest.getIndex()});
                            if (system.getBuildQueue().size() > 0 && system.getBuildQueue().get(buildQueueRequest.getIndex() - 1) != null) {
                                // remove from build queue
                                if (buildQueueRequest.getAction().equals(BuildQueueRequestType.REMOVE)) {
                                    system.getBuildQueue().remove(buildQueueRequest.getIndex() - 1);
                                    writeResponse(new BuildQueueResponse(true));
                                }
                                // Move up or down
                                else {
                                    if (system.getBuildQueue().size() >= 2 && buildQueueRequest.getIndex() > 0
                                            && buildQueueRequest.getIndex() <= system.getBuildQueue().size()) {
                                        if (buildQueueRequest.getAction().equals(BuildQueueRequestType.MOVEUP) && buildQueueRequest.getIndex() > 1) {
                                            Collections.swap(system.getBuildQueue(), buildQueueRequest.getIndex() - 1, buildQueueRequest.getIndex() - 2);
                                            writeResponse(new BuildQueueResponse(true));
                                        } else if (buildQueueRequest.getAction().equals(BuildQueueRequestType.MOVEDOWN) && buildQueueRequest.getIndex() < system.getBuildQueue().size()) {
                                            Collections.swap(system.getBuildQueue(), buildQueueRequest.getIndex() - 1, buildQueueRequest.getIndex());
                                            writeResponse(new BuildQueueResponse(true));
                                        } else {
                                            writeResponse(new BuildQueueResponse("ERROR: invalid direction or index"));
                                        }
                                    } else {
                                        writeResponse(new BuildQueueResponse("ERROR: invalid build queue index or destination"));
                                    }
                                }
                            } else {
                                writeResponse(new BuildQueueResponse("ERROR: invalid build queue index"));
                            }
                        } else {
                            writeResponse(new BuildQueueResponse("ERROR: invalid starsystem (null or not owned by you)"));
                        }
                    }
                    //---------------- BUILD STRUCTURE ----------------\\
                    else if (requestObject instanceof BuildStructureRequest) {
                        BuildStructureRequest buildStructureRequest = (BuildStructureRequest) requestObject;

                        StarSystem system = galaxy.getSystem(buildStructureRequest.getX(), buildStructureRequest.getY());
                        Planet planet = system.getPlanetByNumber(buildStructureRequest.getPlanetNumber());

                        if (system != null && planet != null && system.getUser().equals(currentUser)) {
                            Structure structure = StaticData.getStructureByName(buildStructureRequest.getStructure());
                            if (structure != null) {
                                TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to build a {1} structure in the {2} system on planet {3}", new Object[]{currentUser.getUsername(), structure.getName(), system.getName(), buildStructureRequest.getPlanetNumber()});
                                BuildStructureOrder buildOrder = new BuildStructureOrder(system, planet, buildStructureRequest.getSlot(), structure);
                                try {
                                    system.addBuildOrder(buildOrder);
                                    writeResponse(new BuildStructureResponse(true));
                                } catch (InvalidOrderException ex) {
                                    writeResponse(new BuildStructureResponse("Unable to add build order: " + ex.getMessage()));
                                }
                            } else {
                                writeResponse(new BuildStructureResponse("Invalid building (null)"));
                            }
                        } else {
                            writeResponse(new BuildStructureResponse("Invalid system or planet"));
                        }
                    }
                    //---------------- DEMOLISH STRUCTURE ----------------\\
                    else if (requestObject instanceof DemolishStructureRequest) {
                        DemolishStructureRequest demolishStructureRequest = (DemolishStructureRequest) requestObject;

                        StarSystem system = galaxy.getSystem(demolishStructureRequest.getX(), demolishStructureRequest.getY());
                        Planet planet = system.getPlanetByNumber(demolishStructureRequest.getPlanetNumber());
                        Structure structure = planet.getStructuresMap().get(demolishStructureRequest.getSlot());

                        if (system != null && planet != null && structure != null && system.getUser().equals(currentUser)) {
                            TrekwarServer.getLog().log(Level.FINE, "User {0} is deleting a {1} structure in the {2} system on planet {3}", new Object[]{currentUser.getUsername(), structure.getName(), system.getName(), demolishStructureRequest.getPlanetNumber()});
                            planet.delStructure(demolishStructureRequest.getSlot());
                            writeResponse(new DemolishStructureResponse(true));
                        } else {
                            writeResponse(new DemolishStructureResponse("Invalid system, planet, structure or invalid user"));
                        }
                        out.flush();
                    }
                    //---------------- GET GALAXY ----------------\\
                    else if (requestObject instanceof GetGalaxyRequest) {
                        // TODO: IF next turn is LESS than 2 seconds away, don't send galaxy.
                        // TODO: check that this clientsessions last galaxy object sent turn number is lower than current galaxy turn number.
                        // TODO: limit number of galaxy transfers a single client can get in a given timeframe
                        if (!TrekwarServer.getGalaxy().getExecutingTurn()) {
                            writeResponse(new GetGalaxyResponse(TrekwarServer.getGalaxyFor(currentUser)));
                            turnOfLastTransferedGalaxy = galaxy.getCurrentTurn();
                        } else {
                            writeResponse(new GetGalaxyResponse(null));
                        }
                    }
                    //---------------- LIST USERS ----------------\\
                    else if (requestObject instanceof ListUsersRequest) {
                        ListUsersResponse response = new ListUsersResponse();
                        for (User u : galaxy.getLoggedInUsers()) {
                            response.addUsername(u.getUsername());
                        }
                        writeResponse(response);
                    }
                    //---------------- MESSAGE CHANNEL OR USER ----------------\\
                    else if (requestObject instanceof SendChatRequest) {
                        SendChatRequest sendChatRequest = (SendChatRequest) requestObject;

                        ChatLine c = null;
                        if (sendChatRequest.isPrivateMessage()) {
                            c = new ChatLine(currentUser.getUsername(), new Date(), "PRIV " + sendChatRequest.getDestination(), sendChatRequest.getMessage());
                            try {
                                User u = galaxy.getUser(sendChatRequest.getDestination());
                                if (u != null) {
                                    u.addChat(c);
                                    writeResponse(new SendChatResponse(true));
                                } else {
                                    throw new UserNotFoundException("user object was null");
                                }
                            } catch (UserNotFoundException ex) {
                                writeResponse(new SendChatResponse("Error: " + ex.getMessage()));
                            }
                        } else {
                            c = new ChatLine(currentUser.getUsername(), new Date(), sendChatRequest.getDestination(), sendChatRequest.getMessage());
                            for (User u : galaxy.getLoggedInUsers()) {
                                if (c.getChannel().equalsIgnoreCase("galaxy")) {
                                    u.addChat(c);
                                } else if (c.getChannel().equalsIgnoreCase("faction") && u.getFaction().equals(currentUser.getFaction())) {
                                    u.addChat(c);
                                }
                            }
                            writeResponse(new SendChatResponse(true));
                        }
                    }
                    //---------------- GET CHAT ----------------\\
                    else if (requestObject instanceof GetChatRequest) {

                        if (!currentUser.getChat().isEmpty()) {
                            GetChatResponse response = new GetChatResponse(true);
                            response.setChatLines(currentUser.getChat());
                            writeResponse(response);
                            currentUser.getChat().clear();
                        } else {
                            writeResponse(new GetChatResponse(false));
                        }
                    }
                    //---------------- SAVE TEMPLATE ----------------\\
                    else if (requestObject instanceof UpdateTemplateRequest) {
                        UpdateTemplateRequest templateRequest = (UpdateTemplateRequest) requestObject;
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to save or edit template {1}", new Object[]{currentUser.getUsername(), templateRequest.getTemplateName()});

                        ShipTemplate template = currentUser.getShipTemplate(templateRequest.getTemplateName());
                        boolean makeNewTemplate = false;

                        // creating NEW template
                        if (template == null) {
                            TrekwarServer.getLog().log(Level.FINER, "template {0} not found for user, making new template object!", new Object[]{templateRequest.getTemplateName()});
                            makeNewTemplate = true;
                            HullClass hullClass = null;
                            for (HullClass hc : currentUser.getAvailableShipHulls()) {
                                if (hc.getName().equalsIgnoreCase(templateRequest.getHullClass())) {
                                    hullClass = hc;
                                    break;
                                }
                            }
                            if (hullClass != null) {
                                template = new ShipTemplate(currentUser, templateRequest.getTemplateName(), hullClass);
                                TrekwarServer.getLog().finer("Template object initialized");
                            } else {
                                writeResponse(new UpdateTemplateResponse("Error: invalid hull class: " + templateRequest.getHullClass()));
                                ;
                                continue;
                            }
                        }

                        // add components
                        boolean invalidComponentFound = false;
                        TrekwarServer.getLog().finer("Adding components to template");
                        for (Map.Entry<Integer, String> entry : templateRequest.getComponents().entrySet()) {
                            ShipComponent c = null;
                            for (ShipComponent temp : currentUser.getAvailableShipComponents()) {
                                if (temp.getName().equalsIgnoreCase(entry.getValue())) {
                                    c = temp;
                                    break;
                                }
                            }
                            if (c != null) {
                                try {
                                    template.setComponent(entry.getKey(), c);
                                } catch (SlotException e) {
                                    TrekwarServer.getLog().finer("Invalid component found in component list: " + e.getMessage());
                                    invalidComponentFound = true;
                                    break;
                                }
                            } else {
                                TrekwarServer.getLog().finer("Invalid component found in component list");
                                invalidComponentFound = true;
                                break;
                            }
                        }

                        if (!template.isValid()) {
                            TrekwarServer.getLog().finer("Template is invalid");
                            writeResponse(new UpdateTemplateResponse("Error: template is not valid"));
                            continue;
                        }

                        // save if adding new
                        if (makeNewTemplate) {
                            TrekwarServer.getLog().finer("Added new template object to user object");
                            currentUser.addShipTemplate(template);
                        }

                        if (!invalidComponentFound) {
                            writeResponse(new UpdateTemplateResponse(true));
                        } else {
                            writeResponse(new UpdateTemplateResponse("Error: one or more components not found"));
                        }
                    }
                    //---------------- DELETE TEMPLATE ----------------\\
                    else if (requestObject instanceof DeleteTemplateRequest) {
                        String name = ((DeleteTemplateRequest) requestObject).getTemplateName();
                        TrekwarServer.getLog().log(Level.FINE, "User {0} is trying to delete template {1}", new Object[]{currentUser.getUsername(), name});

                        ShipTemplate s = currentUser.getShipTemplate(name);
                        if (s != null) {
                            currentUser.removeShipTemplate(s);
                            TrekwarServer.getLog().finer("Template found and deleted");
                            writeResponse(new DeleteTemplateResponse(true));
                        } else {
                            TrekwarServer.getLog().finer("Template not found");
                            writeResponse(new DeleteTemplateResponse("ERROR: template " + name + " not found"));
                        }
                    } else {
                        Response error = new Response();
                        error.setErrorMessage("Invalid request object received, class: " + requestObject.getClass().getCanonicalName());
                        TrekwarServer.getLog().log(Level.WARNING, "Invalid request object of class: {0} received from user: {1} from ip: {2}",
                                new Object[]{requestObject.getClass().getCanonicalName(), userString, socket.getInetAddress()});
                        writeResponse(error);
                    }
                }
            }
        } catch (IOException e) {
            TrekwarServer.getLog().log(Level.WARNING, "IO Error while talking to client " + remoteIP + ": " + e.getMessage(), e);
            done = true;
        } finally {
            // clean up
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (socket != null) {
                    socket.close();
                }
                TrekwarServer.getLog().log(Level.INFO, "Closed connection with client at {0}", remoteIP);
            } catch (IOException e) {
                TrekwarServer.getLog().log(Level.SEVERE, "Error while cleaning up after cliensession from '" + remoteIP + "'", e);
            } finally {
                if (currentUser != null) {
                    galaxy.logoutUser(currentUser);
                }
            }
        }
    }

    private void updateLastActivity() {
        lastActivity = System.currentTimeMillis() / 1000;
    }

    public long getLastActivity() {
        return lastActivity;
    }

    public User getLoggedInUser() {
        return currentUser;
    }

    private void writeResponse(Response response) throws IOException {
        response.setSender_ip(socket.getInetAddress().toString());
        String username = "";
        if (currentUser != null) {
            username = currentUser.getUsername();
        }
        String error = "";
        if (response.getErrorMessage().length() > 1) {
            error = "   ERROR: " + response.getErrorMessage();
        }
        response.setUsername(username);
        TrekwarServer.getLog().finest("RESPONSE to " + response.getUsername() + " @ " + response.getSender_ip() + " class: " + response.getClass().getSimpleName() + error);
        out.reset();
        out.writeUnshared(response);
        out.flush();
    }
}