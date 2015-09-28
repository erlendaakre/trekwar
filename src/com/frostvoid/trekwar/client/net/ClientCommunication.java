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
package com.frostvoid.trekwar.client.net;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.exceptions.ServerCommunicationException;
import com.frostvoid.trekwar.common.exceptions.UserNotFoundException;
import com.frostvoid.trekwar.common.net.messaging.*;
import com.frostvoid.trekwar.common.net.messaging.requests.*;
import com.frostvoid.trekwar.common.net.messaging.responses.*;
import com.frostvoid.trekwar.common.shipComponents.ShipComponent;
import com.frostvoid.trekwar.common.structures.Structure;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Communicates with the Server
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class ClientCommunication {

    private Socket socket;
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    private String localUsername;
    private boolean isLoggedIn = false;
    private long nextTurnDate = 0;
    private long currentServerTurn = 0;

    public ClientCommunication() {
    }

    public void connect(String server, int port) throws UnknownHostException, IOException, ClassNotFoundException {
        socket = new Socket(server, port);
        in = new ObjectInputStream(socket.getInputStream());
        WelcomeMessage welcome = (WelcomeMessage) readResponse();
        System.out.println("welcome to server " + welcome.getServerName());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public void disconnect() {
        try {
            isLoggedIn = false;
            server_logout();

            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            Client.getInstance().showError(Client.getLanguage().get("io_error_while_disconnecting_from_server"), ioe, false, false);
        }
    }

    public void login(String username, String password) {
        try {
            System.out.println("about to send login request");
            sendRequest(new LoginRequest(username, password));
            LoginResponse response = (LoginResponse) readResponse();
            System.out.println("Got login response");
            if (response.isLoginSuccessful()) {
                isLoggedIn = true;
                this.localUsername = username;
                sync();
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientCommunication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public long getNextTurnDate() {
        return nextTurnDate;
    }

    public void resetNextTurnDate() {
        nextTurnDate = -1;
    }

    public long getCurrentServerTurn() {
        return currentServerTurn;
    }

    /**
     * Downloads the main galaxy object for the logged in user
     *
     * @return true if galaxy downloaded, false if not
     */
    public void downloadGalaxy() {
        synchronized (out) {
            try {
                sync();
                sendRequest(new GetGalaxyRequest());
                GetGalaxyResponse res = (GetGalaxyResponse) readResponse();

                if (res.getGalaxy() != null) {
                    try {
                        Client.getInstance().galaxyDownloaded(res.getGalaxy());
                        //return true;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ClientCommunication.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(ClientCommunication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.out.println("Galaxy downloaded from server was null");
                    //return false;
                }
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_downloading_galaxy_from_server"), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("Error while downloading galaxy from server"), cnfe, false, true);
            }
        }
    }

    /**
     * Syncs up the time with the server, calculating time to next turn starts
     *
     * @throws IOException if shit hits the fan
     */
    public void sync() throws IOException {
        synchronized (out) {
            try {
                sendRequest(new TimeToNextTurnRequest());
                TimeToNextTurnResponse res = (TimeToNextTurnResponse) readResponse();
                nextTurnDate = System.currentTimeMillis() + (res.getMillisecondsToNextTurn());
                currentServerTurn = res.getCurrentTurn();
            } catch (ClassNotFoundException ex) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ex.getMessage(), ex, false, false);
            }
        }
    }

    public ArrayList<User> server_getUserList() {
        ArrayList<User> users = new ArrayList<User>();

        synchronized (out) {
            try {
                sendRequest(new ListUsersRequest());
                ListUsersResponse res = (ListUsersResponse) readResponse();

                for (String username : res.getUsernames()) {
                    try {
                        users.add(Client.getInstance().getLocalGalaxy().getUser(username));
                    } catch (UserNotFoundException ex) {
                        Client.LOG.log(Level.SEVERE, "ClientCommunication.server_getUserList() username not found in local galaxy! should not happen, send bug report =) :\n{0}", ex.getMessage());
                    }
                }
            } catch (ClassNotFoundException ex) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ex.getMessage(), ex, false, false);
            } catch (IOException ioe) {
                Client.getInstance().showError("Error getting user list: ", ioe, false, true);
            }
        }

        // alphabetical sort
        Collections.sort(users, new Comparator<User>() {

            @Override
            public int compare(User o1, User o2) {
                return o1.getUsername().compareToIgnoreCase(o2.getUsername());
            }
        });
        return users;
    }

    public ArrayList<ChatLine> server_getChat() throws IOException {
        synchronized (out) {
            sendRequest(new GetChatRequest());
            try {
                GetChatResponse res = (GetChatResponse) readResponse();
                return res.getChatLines();
            } catch (ClassNotFoundException ex) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ex.getMessage(), ex, false, false);
            }
        }
        return new ArrayList<ChatLine>(0);
    }

    public void server_send_chat(String destination, String message, boolean privateMessage) {
        synchronized (out) {
            try {
                sendRequest(new SendChatRequest(destination, message, privateMessage));
                SendChatResponse res = (SendChatResponse) readResponse();
                if (!res.isMessageDelivered()) {
                    throw new IOException("Message not delivered: " + res.getErrorMessage());
                }
            } catch (IOException ex) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_send_chat_message"), ex, false, false);
            } catch (ClassNotFoundException ex) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ex.getMessage(), ex, false, false);
            }
        }
    }

    public void server_changeResearch(Technology tech) throws ServerCommunicationException, IOException {
        synchronized (out) {
            try {
                sendRequest(new ResearchRequest(tech.toString()));
                ResearchResponse res = (ResearchResponse) readResponse();
                if (!res.isResearchChangeOK()) {
                    throw new ServerCommunicationException(res.getErrorMessage());
                }
            } catch (ClassNotFoundException ex) {
                throw new ServerCommunicationException(Client.getLanguage().get("server_client_communication_problem") + ex.getMessage());
            }
        }
    }

    public void server_sendTemplate(ShipTemplate template) throws ServerCommunicationException {
        synchronized (out) {
            try {
                UpdateTemplateRequest req = new UpdateTemplateRequest(template.getName(), template.getHullClass().getName());
                for (int i : template.getComponents().keySet()) {
                    ShipComponent c = template.getComponents().get(i);
                    req.addComponent(i, c.getName());
                }
                sendRequest(req);
                UpdateTemplateResponse res = (UpdateTemplateResponse) readResponse();
                if (!res.isTemplateUpdated()) {
                    throw new ServerCommunicationException("Unable to update template: " + res.getErrorMessage());
                }
            } catch (IOException ex) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_sending_template_to_server"), ex, false, true);
                throw new ServerCommunicationException();
            } catch (ClassNotFoundException ex) {
                throw new ServerCommunicationException(Client.getLanguage().get("server_client_communication_problem") + ex.getMessage());
            }
        }
    }

    public void server_deleteTemplate(String templateName) {
        synchronized (out) {
            try {
                sendRequest(new DeleteTemplateRequest(templateName));
                DeleteTemplateResponse res = (DeleteTemplateResponse) readResponse();
                if (!res.isTemplateDeleted()) {
                    Client.getInstance().showError(Client.getLanguage().get("error_while_deleting_template") + " " + templateName + "\n" + res.getErrorMessage(), null, false, true);
                }
            } catch (IOException ex) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_delete_template"), ex, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
    }

    public boolean server_moveFleet(String fleetname, int dstx, int dsty) {
        synchronized (out) {
            try {
                sendRequest(new MoveFleetRequest(fleetname, dstx, dsty));
                MoveFleetResponse res = (MoveFleetResponse) readResponse();
                return res.isFleetMoveOK();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_move_fleet") + " " + fleetname, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_cancelFleetOrders(String fleetname) {
        synchronized (out) {
            try {
                sendRequest(new CancelOrdersRequest(fleetname));
                CancelOrdersResponse res = (CancelOrdersResponse) readResponse();
                return res.isFleetOrdersCancelled();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_cancel_fleet_orders") + " " + fleetname, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public void server_renameFleet(String fleetname, String newname) throws ServerCommunicationException {
        synchronized (out) {
            try {
                sendRequest(new RenameFleetRequest(fleetname, newname));
                RenameFleetResponse res = (RenameFleetResponse) readResponse();
                if (!res.isRenameOK()) {
                    throw new ServerCommunicationException(res.getErrorMessage());
                }
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_rename_fleet") + " " + fleetname + " -> " + newname, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
    }

    public boolean server_newFleet(String fleetname, int x, int y) {
        synchronized (out) {
            try {
                sendRequest(new NewFleetRequest(fleetname, x, y));
                NewFleetResponse res = (NewFleetResponse) readResponse();
                return res.isFleetCreationOK();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_create_new_fleet") + " " + fleetname, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_moveShipToFleet(Fleet currentFleet, Ship ship, String newFleetName) {
        synchronized (out) {
            try {
                sendRequest(new MoveShipToFleetRequest(currentFleet.getName(), ship.getShipId(), newFleetName));
                MoveShipToFleetResponse res = (MoveShipToFleetResponse) readResponse();
                return res.isShipMovedSuccessfully();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_move_ship_to_fleet") + " " + "ship=" + ship.getShipId() + " from=" + currentFleet.getName() + " to=" + newFleetName, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_decommissionDestroyShip(Ship ship) {
        synchronized (out) {
            try {
                sendRequest(new DestroyDecommissionShipRequest(ship.getFleet().getName(), ship.getShipId()));
                DestroyDecommissionShipResponse res = (DestroyDecommissionShipResponse) readResponse();
                return res.isShipDestroyedSuccessfully();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_decommission_or_self_destruct_ship") + " " + "ship=" + ship.getShipId() + " fleet=" + ship.getFleet().getName(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public void server_colonize(String fleetname) throws ServerCommunicationException {
        synchronized (out) {
            try {
                sendRequest(new ColonizeRequest(fleetname));
                ColonizeResponse res = (ColonizeResponse) readResponse();

                if (!res.isColonizationStarted()) {
                    throw new ServerCommunicationException(res.getErrorMessage());
                }
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_colonize_system") + " " + fleetname, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
    }

    public boolean server_invade(String fleetname) {
        synchronized (out) {
            try {
                sendRequest(new InvadeRequest(fleetname));
                InvadeResponse res = (InvadeResponse) readResponse();
                return res.isInvasionStarted();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_invade_system") + " " + fleetname, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_bombSystem(String fleetname) {
        synchronized (out) {
            try {
                sendRequest(new OrbitalBombardmentRequest(fleetname));
                OrbitalBombardmentResponse res = (OrbitalBombardmentResponse) readResponse();
                return res.isBombardmentStarted();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_bomb_system") + " " + fleetname, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_mine(String fleetname) {
        synchronized (out) {
            try {
                sendRequest(new MineRequest(fleetname));
                MineResponse res = (MineResponse) readResponse();
                return res.isMiningStarted();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_mine_asteroids") + " " + fleetname, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_harvestDeuterium(String fleetname) {
        synchronized (out) {
            try {
                sendRequest(new HarvestRequest(fleetname));
                HarvestResponse res = (HarvestResponse) readResponse();
                return res.isHarvestingStarted();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_harvest_nebula") + " " + fleetname, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_transferCargoFromShipToSystem(StarSystem s, int shipId, CargoClassification cargo, int amount) {
        synchronized (out) {
            try {
                sendRequest(new CargoTransferRequest(CargoTransferRequestType.SHIPTOSYSTEM, cargo, s.getX(), s.getY(), shipId, amount));
                CargoTransferResponse res = (CargoTransferResponse) readResponse();
                return res.isCargoTransferred();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_transfer_cargo_from_ship_to_system") + " " + s.getName(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_transferCargoFromSystemToShip(StarSystem s, int shipId, CargoClassification cargo, int amount) {
        synchronized (out) {
            try {
                sendRequest(new CargoTransferRequest(CargoTransferRequestType.SYSTEMTOSHIP, cargo, s.getX(), s.getY(), shipId, amount));
                CargoTransferResponse res = (CargoTransferResponse) readResponse();
                return res.isCargoTransferred();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_transfer_cargo_from_system_to_ship") + " " + s.getName(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_transferTroopsFromShipToSystem(StarSystem s, int shipId, int amount) {
        synchronized (out) {
            try {
                sendRequest(new TroopTransferRequest(TroopTransferRequestType.SHIPTOSYSTEM, s.getX(), s.getY(), shipId, amount));
                TroopTransferResponse res = (TroopTransferResponse) readResponse();
                return res.isTroopsTransferred();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_transfer_troops_from_ship_to_system") + " " + s.getName(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_transferTroopsFromSystemToShip(StarSystem s, int shipId, int amount) {
        synchronized (out) {
            try {
                sendRequest(new TroopTransferRequest(TroopTransferRequestType.SYSTEMTOSHIP, s.getX(), s.getY(), shipId, amount));
                TroopTransferResponse res = (TroopTransferResponse) readResponse();
                return res.isTroopsTransferred();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_transfer_troops_from_system_to_ship") + " " + s.getName(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_buildShip(StarSystem s, ShipTemplate t) {
        synchronized (out) {
            try {
                sendRequest(new BuildShipRequest(s.getX(), s.getY(), t.getName()));
                BuildShipResponse res = (BuildShipResponse) readResponse();
                return res.isShipAddedToBuildQueue();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_build_ship") + ": " + s.getName(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_buildStructure(Planet p, Structure s, int slot) {
        synchronized (out) {
            try {
                sendRequest(new BuildStructureRequest(p.getStarSystem().getX(), p.getStarSystem().getY(), p.getPlanetNumber(), slot, s.getName()));
                BuildStructureResponse res = (BuildStructureResponse) readResponse();
                return res.isStructureAddedToBuildQueue();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_build_structure_in_system") + ": " + p.getStarSystem().getName(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_demolishStructure(Planet p, int slot) {
        synchronized (out) {
            try {
                sendRequest(new DemolishStructureRequest(p.getStarSystem().getX(), p.getStarSystem().getY(), p.getPlanetNumber(), slot));
                DemolishStructureResponse res = (DemolishStructureResponse) readResponse();
                return res.isStructureDemolished();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_demolish_structure_in_system") + ": " + p.getStarSystem().getName(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_updateBuildQueue(StarSystem s, int index, BuildQueueRequestType action) {
        synchronized (out) {
            try {
                sendRequest(new BuildQueueRequest(s.getX(), s.getY(), index, action));
                BuildQueueResponse res = (BuildQueueResponse) readResponse();
                return res.isBuildQueueActionsSuccessful();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_update_item_in_build_queue") + ": " + s.getName() + " index=" + index + ", action=" + action.toString(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_hurryProduction(StarSystem s, int index, int amount) {
        synchronized (out) {
            try {
                sendRequest(new HurryProductionRequest(s.getX(), s.getY(), index, amount));
                HurryProductionResponse res = (HurryProductionResponse) readResponse();
                return res.isProductionHurried();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_hurry_production") + ": " + s.getName() + " index=" + index + " amount=" + amount, ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public boolean server_changeStructureState(Planet p, int slot, StructureStateChangeRequestType newState) {
        synchronized (out) {
            try {
                sendRequest(new StructureStateChangeRequest(p.getStarSystem().getX(), p.getStarSystem().getY(), p.getPlanetNumber(), slot, newState));
                StructureStateChangeResponse res = (StructureStateChangeResponse) readResponse();
                return res.isStateChanged();
            } catch (IOException ioe) {
                Client.getInstance().showError(Client.getLanguage().get("io_error_while_telling_server_to_enable_or_disable_structure") + ": " + p.getStarSystem().getName(), ioe, false, true);
            } catch (Exception cnfe) {
                Client.getInstance().showError(Client.getLanguage().get("server_client_communication_problem") + ":\n", cnfe, false, false);
            }
        }
        return false;
    }

    public void server_logout() {
        try {
            sendRequest(new LogoutRequest());
        } catch (IOException ex) {
        }
    }

    private void sendRequest(Request request) throws IOException {
        System.out.println("SENDING OBJECT OF TYPE: " + request.getClass().getName());
        out.reset();
        out.writeUnshared(request);
        out.flush();
    }

    private Response readResponse() throws IOException, ClassNotFoundException {
        System.out.println("READING RESPONSE FROM SERVER....");
        return (Response) in.readObject();
    }

    public String getLocalUsername() {
        return localUsername;
    }
}