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
package com.frostvoid.trekwar.server.turnExec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

import com.frostvoid.trekwar.common.structures.Structure;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.Planet;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.TurnReportItem;
import com.frostvoid.trekwar.common.shipComponents.TorpedoLauncher;
import com.frostvoid.trekwar.common.structures.Bunker;
import com.frostvoid.trekwar.common.utils.Language;
import com.frostvoid.trekwar.server.TrekwarServer;

/**
 * Handles bombardment of star systems
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class OrbitalBombardmentResolver {
    
    public static final double TORPEDO_LAUNCHER_MORALE_DECLINE_FACTOR_BONUS = 0.85;
    public static final double TORPEDO_LAUNCHER_POPULATION_DAMAGE_FACTOR = 0.75;
    public static final double TORPEDO_LAUNCHER_TROOPS_DAMAGE_FACTOR = 0.02;
    
    public static final double BUNKER_POPULATION_DEFENSE_FACTOR = 7;
    public static final double BUNKER_TROOPS_DEFENSE_FACTOR = 0.30;
    

    private Fleet attacker;
    private StarSystem defender;
    
    /**
     * Set up the object, calculating strengths, etc..
     *
     * @param attacker the fleet that is invading a system
     * @param defender the system that is being invaded
     */
    public OrbitalBombardmentResolver(Fleet attacker, StarSystem defender) {
        this.attacker = attacker;
        this.defender = defender;
    }
    
    /**
     * Resolves the orbital bombardment
     */
    public void resolve() {
        TrekwarServer.LOG.log(Level.FINE, "RESOLVING ORBITAL BOMBARDMENT BY {0} on {1}", new Object[]{attacker.getUser().getUsername(), defender.getUser().getUsername()});
        long popKilled = 0;
        long troopsKilled = defender.getTroopCount();
        ArrayList<TorpedoLauncher> launchers = attacker.getTorpedoLaunchers();
        ArrayList<Planet> planets = new ArrayList<Planet>(defender.getPlanets().size());
        ArrayList<Structure> structuresDestroyed = new ArrayList<Structure>();
        
        int defenseFromBunkers = 0;
        int numberOfBunkers = 0;
        for(Planet p : defender.getPlanets()) {
            for(Bunker b : p.getBunkers()) {
                defenseFromBunkers += b.getTroopCapacity();
                numberOfBunkers++;
            }
            
            if(p.getPopulation() > 0) {
                planets.add(p);
            }
        }
        
        TrekwarServer.LOG.log(Level.FINE, "Attacker has {0} torpedo launchers", launchers.size());
        TrekwarServer.LOG.log(Level.FINE, "Defender has {0} bunkers, with a defense value of {1}", new Object[]{numberOfBunkers, defenseFromBunkers});
        TrekwarServer.LOG.log(Level.FINE, "Defender has {0} planets with population", planets.size());
        
        for(TorpedoLauncher launcher : launchers) {
            
            // damage to population
            Collections.shuffle(planets);
            Planet p = planets.get(0);
            if(p.getPopulation() > 0) {
                double casualties = (launcher.getDamage() * TORPEDO_LAUNCHER_POPULATION_DAMAGE_FACTOR);
                casualties -= (defenseFromBunkers * BUNKER_POPULATION_DEFENSE_FACTOR);
                if (casualties < 5) {
                    casualties = 5;
                }
                popKilled += casualties;
                p.setPopulation(p.getPopulation() - (int)casualties);
                TrekwarServer.LOG.log(Level.FINE, "{0} million people killed by bombardment, planet pop: " + p.getPopulation() + "/" + p.getMaximumPopulation(), casualties);
            }
            

            // damage to troops
            double casualties = (launcher.getDamage() * TORPEDO_LAUNCHER_TROOPS_DAMAGE_FACTOR);
            casualties -= defenseFromBunkers * BUNKER_TROOPS_DEFENSE_FACTOR;
            if (casualties < 1) {
                 casualties = 1;
            }
            defender.setTroopCount(defender.getTroopCount() - (int)casualties);
            TrekwarServer.LOG.log(Level.FINE, "{0} troops killed by bombardment", casualties);
            
            
            // damage to system structures
            if(TrekwarServer.PRNG.nextInt(100) < launcher.getStructureHitChance()) {
                Structure destroyedStructure = destroyRandomStructure();
                if(destroyedStructure != null) {
                    structuresDestroyed.add(destroyedStructure);
                }
            }
        }

        // decrease morale
        int moraleDrop = (int)(5 + (launchers.size() * TORPEDO_LAUNCHER_MORALE_DECLINE_FACTOR_BONUS));
        if(moraleDrop < 2) {
            moraleDrop = 2;
        }
        defender.setMorale(defender.getMorale() - moraleDrop);
        
        troopsKilled = troopsKilled - defender.getTroopCount(); // troopsKilled = starting troop count - end troop count
        
        
        StringBuilder sb = new StringBuilder();
        sb.append(Language.pop(TrekwarServer.getLanguage().getU("turn_report_system_bombed_2"), popKilled)).append(", ");
        sb.append(Language.pop(TrekwarServer.getLanguage().getU("turn_report_system_bombed_3"), troopsKilled)).append(", ");
        if(structuresDestroyed.size() > 0) {
            sb.append(TrekwarServer.getLanguage().getU("turn_report_system_bombed_4")).append(" ");
            for(Structure s : structuresDestroyed) {
                sb.append(s.getName()).append(", ");
            }
        }
        sb.append(Language.pop(TrekwarServer.getLanguage().getU("turn_report_system_bombed_5"), defender.getMorale()));
        
        TurnReportItem tri_defend = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), defender.getX(), defender.getY(), TurnReportItem.TurnReportSeverity.CRITICAL);
        tri_defend.setSummary(Language.pop(TrekwarServer.getLanguage().getU("turn_report_system_bombed_1"), defender.getName()));
        tri_defend.setDetailed(sb.toString());
        defender.getUser().addTurnReport(tri_defend);
        
        TurnReportItem tri_attack = new TurnReportItem(TrekwarServer.getGalaxy().getCurrentTurn(), attacker.getX(), attacker.getY(), TurnReportItem.TurnReportSeverity.HIGH);
        tri_attack.setSummary(Language.pop(TrekwarServer.getLanguage().getU("turn_report_system_bombed_0"), defender.getName()));
        tri_attack.setDetailed(sb.toString());
        attacker.getUser().addTurnReport(tri_attack);
    }
    
    
    
    private Structure destroyRandomStructure() {
        TrekwarServer.LOG.log(Level.FINE, "Destroying random structure...");
        // find random planet
        ArrayList<Planet> planets = new ArrayList<Planet>();
        for(Planet p : defender.getPlanets()) {
            if(p.getStructuresMap().values().size() > 0) {
                planets.add(p);
            }
        }
        Collections.shuffle(planets);
        
        
        if(planets.size() > 0) {
            // find random structure
            Planet target = planets.get(0);
            TrekwarServer.LOG.log(Level.FINE, "targeting structure on planet {0}", target.getPlanetNumber());
            ArrayList<Integer> structuedIndices = new ArrayList<Integer>();
            for(Integer i : target.getStructuresMap().keySet()) {
                if(target.getStructuresMap().get(i) != null) {
                    structuedIndices.add(i);
                }
            }
            Collections.shuffle(structuedIndices);
             
            // delete random structure
            if(structuedIndices.size() > 0) {
                Structure destroyedStructure = target.getStructuresMap().get(structuedIndices.get(0));
                TrekwarServer.LOG.log(Level.FINE, "destroying structure: {0}", destroyedStructure.getName());
                target.delStructure(structuedIndices.get(0));
                return destroyedStructure;
            }
        }
        
        return null;
    }
}