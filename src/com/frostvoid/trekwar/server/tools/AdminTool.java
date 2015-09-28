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
package com.frostvoid.trekwar.server.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.frostvoid.trekwar.client.net.ClientCommunication;

/**
 * Console app that connects to the server and gives admin commands
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class AdminTool extends ClientCommunication {

    public static void main(String[] args) throws IOException {
        AdminTool at = new AdminTool();
    }

    public AdminTool() throws IOException {
        String line = "";
        BufferedReader con = new BufferedReader(new InputStreamReader(System.in));
        while (!line.equals("exit")) {
            System.out.print("> ");
            line = con.readLine();

            if (line.equals("connect")) {
                System.out.print("IP: ");
                String ip = con.readLine();

                System.out.print("Port: ");
                String port = con.readLine();

                try {
                    connect(ip, new Integer(port));
                    System.out.println("> Connected!");
                } catch (Exception ex) {
                    System.out.println("> ERROR: unable to connect!\n" + ex.getMessage());
                }
            }

            if (line.equals("login")) {
                System.out.print("Username: ");
                String user = con.readLine();

                System.out.print("Password: ");
                String pass = con.readLine();
                login(user, pass);

                System.out.println("> Logged in = " + isLoggedIn());
            }

            if (line.equals("help")) {
                System.out.println("> connect - connects to server");
                System.out.println("> login - logs in as a user");
                System.out.println("> exit - closes admin tool");
                System.out.println("> -------------------------");
                System.out.println("> listusers - lists online users");
            }

            if (line.equals("listusers")) {
                admin_listUsers();
            }
        }
    }

    private void admin_listUsers() throws IOException {
//            out.println("admin listusers");
//            out.flush();
//            String response = in.readLine();
//            System.out.println("> " + response);
    }
}