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

import java.io.*;
import java.util.Date;
import java.util.ArrayList;

import com.frostvoid.trekwar.common.StaticData;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.TechnologyGenerator;
import com.frostvoid.trekwar.common.shipHulls.HullClass;

/**
 * Exports all ship hulls in the game into a table using JSPWiki markup
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Ships2html {
   
   public static void main(String[] args) throws Exception {
       
      FileWriter fw = new FileWriter(new File("ships.txt"));
      PrintWriter out = new PrintWriter(fw);

      out.println("Ship hulls from Trekwar2, generated: " + new Date() + "");
      out.println();

      
      ArrayList<HullClass> s = new ArrayList<HullClass>();
      s.add(StaticData.civilian_scoutship);
      s.add(StaticData.civilian_colonyship);
      s.add(StaticData.civilian_freighter);
      s.add(StaticData.civilian_barge);
      s.add(StaticData.civilian_carrier);

      out.println("!![Civilian ships|CivilianShips]");
      out.println("||hull||cost||maintenance||crew||slots||hp||armor||maneuverability||deuterium capacity||deuterium usage||bio||ene||constr||prop||comp||wep");
      for(HullClass c : s) {
         out.print("|" + c.getName());
         out.print("|" + c.getBaseCost());
         out.print("|" + c.getBaseMaintenanceCost());
         out.print("|" + c.getMaxCrew());
         out.print("|" + c.getSlots());
         out.print("|" + c.getBaseHitpoints());
         out.print("|" + c.getBaseArmor());
         out.print("|" + c.getBaseManoeuvrability());
         out.print("|" + c.getBaseDeuteriumStorage());
         out.print("|" + c.getBaseDeuteriumUseage());
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.biotech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.energytech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.constructiontech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.propulsiontech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.computertech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.weaponstech));
         out.println();
      }
      out.println();
      out.println();



      s = new ArrayList<HullClass>();
      s.add(StaticData.federation_oberth);
      s.add(StaticData.federation_miranda);
      s.add(StaticData.federation_constitution);
      s.add(StaticData.federation_excelsior);
      s.add(StaticData.federation_galaxy);
      s.add(StaticData.federation_defiant);

      out.println("!![Federation]");
      out.println("||hull||cost||maintenance||crew||slots||hp||armor||maneuverability||deuterium capacity||deuterium usage||bio||ene||constr||prop||comp||wep");
      for(HullClass c : s) {
         out.print("|" + c.getName());
         out.print("|" + c.getBaseCost());
         out.print("|" + c.getBaseMaintenanceCost());
         out.print("|" + c.getMaxCrew());
         out.print("|" + c.getSlots());
         out.print("|" + c.getBaseHitpoints());
         out.print("|" + c.getBaseArmor());
         out.print("|" + c.getBaseManoeuvrability());
         out.print("|" + c.getBaseDeuteriumStorage());
         out.print("|" + c.getBaseDeuteriumUseage());
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.biotech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.energytech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.constructiontech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.propulsiontech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.computertech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.weaponstech));
         out.println();
      }
      out.println();
      out.println();



      s = new ArrayList<HullClass>();
      s.add(StaticData.klingon_raptor);
      s.add(StaticData.klingon_brel);
      s.add(StaticData.klingon_kvort);
      s.add(StaticData.klingon_ktinga);
      s.add(StaticData.klingon_vorcha);
      s.add(StaticData.klingon_neghvar);

      out.println("!![Klingon]");
      out.println("||hull||cost||maintenance||crew||slots||hp||armor||maneuverability||deuterium capacity||deuterium usage||bio||ene||constr||prop||comp||wep");
      for(HullClass c : s) {
         out.print("|" + c.getName());
         out.print("|" + c.getBaseCost());
         out.print("|" + c.getBaseMaintenanceCost());
         out.print("|" + c.getMaxCrew());
         out.print("|" + c.getSlots());
         out.print("|" + c.getBaseHitpoints());
         out.print("|" + c.getBaseArmor());
         out.print("|" + c.getBaseManoeuvrability());
         out.print("|" + c.getBaseDeuteriumStorage());
         out.print("|" + c.getBaseDeuteriumUseage());
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.biotech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.energytech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.constructiontech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.propulsiontech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.computertech));
         out.print("|" + getTechLevel(c, TechnologyGenerator.techType.weaponstech));
         out.println();
      }
      out.println();
      out.println();

      out.close();
   }

   private static String getTechLevel(HullClass c, TechnologyGenerator.techType techType) {
       for(Technology t : c.getTechsRequired()) {
           if(t.getType().equals(techType)) {
               return "" + t.getLevel();
           }
       }
       return "ERROR";
   }
}