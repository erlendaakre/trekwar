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
package com.frostvoid.trekwar.server.tools.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Represents a task gotten from  Toodledoo
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
class Task {
    String id;
    String title;
    String modified;
    String completed;
    
    String folder;
    String context;
    String tag;
    String status;
    String priority;
    String length;
    String note;
}

/**
 * Gets all the tasks from Toodledoo and turns them into JSPWiki format
 * 
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Toodledoo2html {
    private static String appID = "trekwar";
    private static String userID = "";
    private static String userPW = "";
    private static String applicationToken = "api4ccbd472e88a3";
    private static String outputFilename = "wiki_bugs.txt";
    
    private static String fields = "folder,context,tag,status,priority,length,note"; // get these fields when downloading tasks
    
    private static String sessionToken;
    private static String key;
    
    private static String toodledoTokenURL = "https://api.toodledo.com/2/account/token.php";
    private static String toodledoGetTaskURL = "https://api.toodledo.com/2/tasks/get.php";
    
    public static void main(String[] args) {
        sessionToken = getSessionToken(md5(userID+applicationToken));
        key = md5(md5(userPW)+applicationToken+sessionToken);
        ArrayList<Task> tasks = getTasks();
        tasks = filterTasksByTag(tasks, "bug");
        ArrayList<Task> completed = getCompletedTasks(tasks);
        tasks = filterTasksByCompleted(tasks);
        writeTasksToFile(tasks, completed);
        
        System.out.println("Done!");
    }
    

    private static void writeTasksToFile(ArrayList<Task> tasks, ArrayList<Task>  completed) {
        try {
            System.out.println("Writing output to file.");
            File f = new File(outputFilename);
            if(!f.exists()) {
                f.createNewFile();
            }
            
            Writer out = new OutputStreamWriter(new FileOutputStream(f));
            out.write("Last updated: " + new Date() + "\r\n");
            out.write("!!!Open bugs\r\n");
            out.write("||Title||Priority||Context for fix||Time estimate||Component||Description\r\n");
            for(Task t : tasks) {
                out.write("|");
                out.write(t.title);
                out.write("|");
                out.write(t.priority);
                out.write("|");
                out.write(getContextName(t.context));
                out.write("|");
                out.write(t.length);
                out.write("|");
                out.write(getFolderName(t.folder));
                out.write("|");
                out.write(t.note);
                out.write("\r\n");
            }
            
            out.write("!!!Recently closed\r\n");
            out.write("||Title||Completed||Description\r\n");
            for(Task t : completed) {
                Date comDate = new Date(Long.parseLong(t.completed + "000"));
                SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d. yyyy");
                out.write("|");
                out.write(t.title);
                out.write("|");
                out.write(sdf.format(comDate));
                out.write("|");
                out.write(t.note);
                out.write("\r\n");
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
        catch(IOException ioe) {
            System.out.println(ioe);
        }
    }
    
    private static String getContextName(String id) {
        if(id.equals("303125"))
            return "0.4.5 (Alpha 2)";
        else if(id.equals("303129"))
            return "0.5.0 (Beta)";        
        else if(id.equals("303137"))
            return "0.6.0 (Release)";
        else
            return id;
        
    }
    
    private static String getFolderName(String id) {
        if(id.equals("1042573"))
            return "client";
        else if(id.equals("1042571"))
            return "base";
        else
            return id;
    }
    
    private static ArrayList<Task> filterTasksByTag(ArrayList<Task> tasks, String tag) {
        ArrayList<Task> res = new ArrayList<Task>();
        for(Task t: tasks) {
            if(t.tag.contains(tag)) {
                res.add(t);
            }
        }
        System.out.println("Tasks filtered, bugs found: " + res.size());
        return res;
    }
    
    private static ArrayList<Task> filterTasksByCompleted(ArrayList<Task> tasks) {
        ArrayList<Task> res = new ArrayList<Task>();
        for(Task t: tasks) {
            if(t.completed != null && t.completed.length() > 1) {
            }
            else {
                res.add(t);
            }
        }
        return res;
    }
    
    private static ArrayList<Task> getCompletedTasks(ArrayList<Task> tasks) {
        ArrayList<Task> res = new ArrayList<Task>();
        
        for(Task t : tasks) {
            if(t.completed != null && t.completed.length() > 1) {
                res.add(t);
            }
        }
        return res;
    }
    
    private static ArrayList<Task> getTasks() {
        try {
            String data = "?key=" + key + ";fields=" + fields + ";f=xml";
            
            System.out.println("Getting tasks: " + toodledoGetTaskURL + data);
            
            URL url = new URL(toodledoGetTaskURL + data);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder xml = new StringBuilder();
            String line = in.readLine();
            while( line != null) {
                xml.append(line);
                line = in.readLine();
            }
            in.close();
            
            ArrayList<Task> tasks = xmlToTasks(xml.toString());
            return tasks;
            
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
        return null;
    }

    private static ArrayList<Task> xmlToTasks(String xml) {
        ArrayList<Task> taskList = new ArrayList<Task>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xml)));

            Element root = doc.getDocumentElement();

            NodeList tasks = root.getElementsByTagName("task");
            for (int i = 0; i < tasks.getLength(); i++) {
                Element task = (Element) tasks.item(i);
                
                Task t = new Task();
                t.id = getDomTextValue(task, "id");
                t.title = getDomTextValue(task, "title");
                t.modified = getDomTextValue(task, "modified");
                t.completed = getDomTextValue(task, "completed");
                
                t.folder = getDomTextValue(task, "folder");
                t.context = getDomTextValue(task, "context");
                t.tag = getDomTextValue(task, "tag");
                t.status = getDomTextValue(task, "status");
                t.priority = getDomTextValue(task, "priority");
                t.length = getDomTextValue(task, "length");
                t.note = getDomTextValue(task, "note");
                taskList.add(t);
            }

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Toodledoo2html.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(SAXException se) {
            
        }
        catch(IOException ioe) {
            
        }

        System.out.println("xmlToTasks got " + taskList.size() + " tasks");
        return taskList;
    }
    
    private static String getSessionToken(String signature) {
        try {
            String data = "?userid=" + userID + ";appid=" + appID + ";sig=" + signature + ";f=xml";
            
            System.out.println("Getting session token: " + toodledoTokenURL+data);
            
            URL url = new URL(toodledoTokenURL + data);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String xml = in.readLine();
            in.close();
            String token = xml.substring(xml.indexOf("<token>")+7, xml.indexOf("</token>"));
            return token;
        } catch (MalformedURLException ex) {
            System.out.println(ex);
        }
        catch(IOException ioe) {
            System.out.println(ioe);
        }
        return "";
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            
            byte byteData[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException nse) {
            System.err.println("ERROR. MD5 ALGORITHM NOT FOUND");
            return "";
        }
    }
    
    public static String getDomTextValue(Element element, String tag) {
        if (element != null) {
            NodeList nl = element.getElementsByTagName(tag);
            if (nl != null && nl.getLength() > 0) {
                Element el = (Element) nl.item(0);
                if(el != null && el.getFirstChild() != null) {
                    return el.getFirstChild().getNodeValue();
                }
            }
        }
        return "";
    }
}