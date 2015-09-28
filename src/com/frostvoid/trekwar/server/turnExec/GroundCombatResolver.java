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

import java.util.logging.Level;

import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.TechnologyGenerator.techType;
import com.frostvoid.trekwar.common.User;
import com.frostvoid.trekwar.server.TrekwarServer;

/**
 * This class handles all combat resolution for ground combat
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class GroundCombatResolver {

    // some constants used in the calculations
    public static final double WEAPON_TECH_FACTOR = 10;
    public static final double ATTACKER_CONSTANT = 1.4;
    public static final double MORALE_FACTOR = 50;
    public static final double STARVATION_FACTOR = 50; // TODO -10% defence
    public static final double BUNKER_CONSTRUCTION_FACTOR = 50;
    public static final int RANDOM_WEIGHT = 6;

    private Fleet attacker;
    private StarSystem defender;

    private User winner;

    private int attacker_losses;
    private int attacker_troops;
    private int attacker_weaponsTech;
    private double attacker_strength;

    private int defender_losses;
    private int defender_troops;
    private int defender_weaponsTech;
    private double defender_strength;

    /**
     * Set up the object, calculating strengths, etc..
     *
     * @param attacker the fleet that is invading a system
     * @param defender the system that is being invaded
     */
    public GroundCombatResolver(Fleet attacker, StarSystem defender) {
        this.attacker = attacker;
        this.defender = defender;

        reset();
    }

    /**
     * Gets the attackers strength
     *
     * @return attacker strength
     */
    public double getAttackerStrength() {
        return attacker_strength;
    }

    /**
     * Gets the defenders strength
     *
     * @return defender strength
     */
    public double getDefenderStrength() {
        return defender_strength;
    }

    /**
     * Does x number of simulation of this battle
     * (used from the GUI to calculate % chance to win)
     *
     * @param simulations number of simulations to run
     * 
     * @return [total attacker losses, total defender losses, attacker wins]
     */
    public int[] simulate(int simulations) {
        int attackerAvgLosses = 0;
        int defenderAvgLosses = 0;
        int attackerWins = 0;
        while(simulations-- > 0) {
            resolve(true);
            defenderAvgLosses += defender_losses;
            attackerAvgLosses += attacker_losses;
            if(didAttackerWin()) {
                attackerWins++;
            }
            reset();
        }
        return new int[] {attackerAvgLosses, defenderAvgLosses, attackerWins };
    }

    /**
     * Resolves this battle
     *
     * @param simulation if true, only calculations will be made, if false attacker/defender objects will change
     */
    public void resolve(boolean simulation) {
        if(defender_troops < 1) {
            winner = attacker.getUser();
        }
        if(attacker_troops < 1) {
            winner = defender.getUser();
        }

        if(!simulation) {
            TrekwarServer.LOG.log(Level.INFO, "RESOLVING GROUND COMBAT");
            TrekwarServer.LOG.log(Level.INFO, "ATTACKER = user:{0}, troops:{1}, str:{2}", new Object[]{attacker.getUser().getUsername(), attacker_troops, attacker_strength});
            TrekwarServer.LOG.log(Level.INFO, "DEFENDER = user:{0}, troops:{1}, str:{2}", new Object[]{defender.getUser().getUsername(), defender_troops, defender_strength});
        }

        while(winner == null) {

            double attacker_rand = (TrekwarServer.PRNG.nextDouble() * RANDOM_WEIGHT) + attacker_strength;
            double defender_rand = (TrekwarServer.PRNG.nextDouble() * RANDOM_WEIGHT) + defender_strength;

            if(attacker_rand > defender_rand) {
                defender_troops--;
                defender_losses++;
                if(!simulation) {
                    defender.setTroopCount(defender_troops);
                }
            }
            else {
                attacker_troops--;
                attacker_losses++;
                if(!simulation) {
                    attacker.decrementTroops();
                }
            }

            if(defender_troops < 1) {
                winner = attacker.getUser();
            }
            if(attacker_troops < 1) {
                winner = defender.getUser();
            }
        }
        if (!simulation) {
            TrekwarServer.LOG.log(Level.INFO, "COMBAT DONE... WINNER = {0}", winner.getUsername());
            TrekwarServer.LOG.log(Level.INFO, "attacker losses = {0}", attacker_losses);
            TrekwarServer.LOG.log(Level.INFO, "defender losses = {0}", defender_losses);
        }
    }

    /**
     * Gets the winner of this battle
     *
     * @return the winner
     */
    public User getWinner() {
        return winner;
    }

    /**
     * Check if the attacker won (invasion successful)
     *
     * @return true if attacker won
     */
    public boolean didAttackerWin() {
        return winner.equals(attacker.getUser());
    }

    /**
     * Gets the looser of this battle
     *
     * @return the looser
     */
    public User getLooser() {
        if(attacker.getUser().equals(winner)) {
            return defender.getUser();
        }
        else {
            return attacker.getUser();
        }
    }

    /**
     * Gets the number of attacker troops lost in the attack
     *
     * @return attackers troop loss
     */
    public int getAttackerLosses() {
        return attacker_losses;
    }

    /**
     * Gets the number of defender troops lost in the attack
     *
     * @return defender troop loss
     */
    public int getDefenderLosses() {
        return defender_losses;
    }

    /**
     * Gets the weapon tech level of the attacker
     *
     * @return attacker weapons tech level
     */
    public int getAttackerWeaponTechLevel() {
        return attacker_weaponsTech;
    }

    /**
     * Gets the weapon tech level of the defender
     *
     * @return defender weapons tech level
     */
    public int getDefenderWeaponTechLevel() {
        return defender_weaponsTech;
    }

    /**
     * Calculates number of troops, strength, tech levels
     * Called by constructor to init data.
     * Called between every simulation to reset data
     */
    private void reset() {
        attacker_troops = attacker.getTroops();
        attacker_weaponsTech = attacker.getUser().getHighestTech(techType.weaponstech).getLevel();

        attacker_losses = 0;
        defender_losses = 0;

        defender_troops = defender.getTroopCount();
        defender_weaponsTech = defender.getUser().getHighestTech(techType.weaponstech).getLevel();
        int defender_constructionTech = defender.getUser().getHighestTech(techType.constructiontech).getLevel();
        int defender_morale = defender.getMorale();
        int defender_defence_bonus = defender.getDefenseRating();

        attacker_strength = 1 + ((attacker_weaponsTech/WEAPON_TECH_FACTOR) * ATTACKER_CONSTANT );
        defender_strength = 1 + (defender_weaponsTech/WEAPON_TECH_FACTOR) * (defender_morale/MORALE_FACTOR)
                + (((defender_defence_bonus * 2.5) + (defender_constructionTech*1.6)) / BUNKER_CONSTRUCTION_FACTOR );

        winner = null;
    }
}