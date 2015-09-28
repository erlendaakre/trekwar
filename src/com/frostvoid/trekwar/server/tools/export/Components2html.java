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

import com.frostvoid.trekwar.common.Faction;
import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.shipComponents.ShipComponent;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Exports all ship components in the game into a table using JSPWiki markup
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Components2html {

    public static void main(String[] args) throws Exception {
        FileWriter fw = new FileWriter(new File("components.txt"));
        PrintWriter out = new PrintWriter(fw);

        out.println("[{TableOfContents}]\\\\");
        out.println("Ship components are used when [designing new starships|ShipDesigner]\\\\");
        out.println("");
        out.println("Ship components from Trekwar2, generated: " + new Date() + "\\\\");
        out.println("Number of components: " + StaticData.allShipComponents.size() + "\\\\");
        out.println("");

        ArrayList<ShipComponent> components = StaticData.allShipComponents;
        Collections.sort(components, new Comparator<ShipComponent>() {
            @Override
            public int compare(ShipComponent o1, ShipComponent o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });


        for (ShipComponent c : StaticData.allShipComponents) {
            out.println("!!" + c.getName());
            out.println("[http://www.trekwar.org/images/shipcomponents/" + c.getIconFileName() + "]\\\\\\");
            out.println("''" + c.getDescription() + "''\\\\");
            out.println("Available to: " + list(c.getFactionsRequired()) + "\\\\");
            out.println("Cost: " + c.getCost() + "\\\\");
            out.println("civillian: " + c.isCivilian() + "\\\\");
            out.println("energy output/input: " + c.getEnergy() + "\\\\");
            out.println("Technologies required:\\\\" + listTech(c.getTechsRequired()));

            out.println("");
            out.println("\\\\");
        }

        out.close();
    }

    private static String list(ArrayList<Faction> list) {
        String res = "";

        for (Faction f : list) {
            res += f.getName() + ", ";
        }
        return res.substring(0, res.length() - 2);
    }

    private static String listTech(ArrayList<Technology> list) {
        String res = "";
        if (list.isEmpty()) {
            res += "*''none''";
        }

        for (Technology t : list) {
            res += "\n*";
            res += t.getType() + " level " + t.getLevel() + "  (" + t.getName() + ")";
        }
        return res;
    }
}