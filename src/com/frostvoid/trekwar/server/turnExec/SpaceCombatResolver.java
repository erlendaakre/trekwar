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

import com.frostvoid.trekwar.common.*;
import com.frostvoid.trekwar.common.TechnologyGenerator.techType;
import com.frostvoid.trekwar.common.shipComponents.BeamEmitter;
import com.frostvoid.trekwar.common.shipComponents.MiningLaser;
import com.frostvoid.trekwar.common.shipComponents.ShipComponent;
import com.frostvoid.trekwar.common.shipComponents.TorpedoLauncher;
import com.frostvoid.trekwar.server.TrekwarServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Resolves space combat
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SpaceCombatResolver {

    private SpaceBattle battle;

    /**
     * Creates a new resolver for a specific battle
     *
     * @param battle the battle
     */
    public SpaceCombatResolver(SpaceBattle battle) {
        this.battle = battle;

    }

    /**
     * Resolves combat for this battle
     */
    public void doCombat() {
        long combatStartTime = System.currentTimeMillis();
        TrekwarServer.LOG.log(Level.FINE, "Entering space battke combat loop");
        ArrayList<User> users = battle.getCombatants();
        HashMap<User, BattleReport> reports = new HashMap<User, BattleReport>();
        for (User u : users) {
            reports.put(u, new BattleReport(u, battle.getLocation()));
            // TODO set total ships in each users battle 
        }


        // Combat loop
        while (true) {

            // prevent infinite loops
            if (battle.getRound() > 15000) {
                TrekwarServer.LOG.log(Level.SEVERE, "Space Battle had more than 15'000 rounds, giving up");
                TrekwarServer.LOG.log(Level.SEVERE, "Users");
                for (User u : users) {
                    TrekwarServer.LOG.log(Level.SEVERE, "     {0}", u.getUsername());
                }
                TrekwarServer.LOG.log(Level.SEVERE, "Ships:");
                for (Ship s : battle.getShips()) {
                    TrekwarServer.LOG.log(Level.SEVERE, "     {0} / {1} = {2} {3} = {4} / {5} / {6}", new Object[]{s.getName(), s.getHullClass().getName(), s.hasBeamWeapons(), s.hasTorpedoLauncher(), s.getCurrentShieldStrength(), s.getCurrentArmorStrength(), s.getCurrentHullStrength()});
                }
            }
            TrekwarServer.LOG.log(Level.FINE, "Finding random attacker");
            Ship attacker = battle.getRandomAttacker();
            if (attacker != null) {
                TrekwarServer.LOG.log(Level.FINE, "Attacker = {0} id = {1}", new Object[]{attacker.getName(), attacker.getShipId()});
            } else {
                TrekwarServer.LOG.log(Level.FINE, "Attacker = NULL");
            }

            TrekwarServer.LOG.log(Level.FINE, "Finding target for attacker");
            Ship defender = battle.findTarget(attacker);
            if (defender != null) {
                TrekwarServer.LOG.log(Level.FINE, "Defender = {0} id = {1}", new Object[]{defender.getName(), defender.getShipId()});
            } else {
                TrekwarServer.LOG.log(Level.FINE, "Defender = NULL");
            }

            if (attacker == null || defender == null) {
                TrekwarServer.LOG.log(Level.FINE, "Missing Attacker OR Defender.. Exiting combat loop");
                break; // end of combat
            }

            TrekwarServer.LOG.log(Level.FINE, "Combat round: {0}", battle.getRound());
            TrekwarServer.LOG.log(Level.FINE, "Attacker: {0} / {1} / {2}", new Object[]{attacker.getCurrentShieldStrength(), attacker.getCurrentArmorStrength(), attacker.getCurrentHullStrength()});
            TrekwarServer.LOG.log(Level.FINE, "Defender: {0} / {1} / {2}", new Object[]{defender.getCurrentShieldStrength(), defender.getCurrentArmorStrength(), defender.getCurrentHullStrength()});


            // found target, doing combat
            shipToShip(attacker, defender);

            // handle destroyed ships
            if (attacker.getCurrentHullStrength() < 1) {
                TrekwarServer.LOG.log(Level.FINE, "Attacker destroyed");
                reports.get(attacker.getUser()).shipsLost++;
                reports.get(defender.getUser()).enemiesDestroyed++;
                battle.removeShip(attacker);
                attacker.destroy();
            }

            if (defender.getCurrentHullStrength() < 1) {
                TrekwarServer.LOG.log(Level.FINE, "Defender destroyed");
                reports.get(defender.getUser()).shipsLost++;
                reports.get(attacker.getUser()).enemiesDestroyed++;
                battle.removeShip(defender);
                defender.destroy();
            }
        }

        for (User u : users) {
            BattleReport br = reports.get(u);
            if (br != null) {
                TurnReportItem atri = new TurnReportItem(battle.getTurn(), battle.getLocation().getX(), +battle.getLocation().getY(), TurnReportItem.TurnReportSeverity.CRITICAL);
                String location = battle.getLocation().getX() + ":" + battle.getLocation().getY();
                if (battle.getLocation().getName().length() > 0) {
                    location += " (" + battle.getLocation().getName() + ")";
                }
                atri.setSummary("Battle report from " + location);
                atri.setDetailed("We lost " + br.shipsLost + " ships, our forces managed to destroy " + br.enemiesDestroyed + " enemy ships");
                u.addTurnReport(atri);
            }
        }

        TrekwarServer.LOG.log(Level.FINE, "Exiting combat loop, combat resolution took {0} ms", System.currentTimeMillis() - combatStartTime);
    }

    /**
     * Does a single ship to ship attack
     *
     * @param attacker the attacking ship
     * @param defender the defending ship
     */
    private void shipToShip(Ship attacker, Ship defender) {
        TrekwarServer.LOG.log(Level.FINE, "Calculating Ship to Ship combat");

        // prevent fireing torpedo launchers more times than there are torpedo launchers on ship
        int torpedoLaunchers = attacker.countTorpedoLaunchers();

        // prevent each beam emitter from firering more than 3 times
        int beamEmitters = attacker.countBeamEmitters() * 3;

        TrekwarServer.LOG.log(Level.FINE, "Number of torpedo launchers = {0}, beam emitters = {1}, AP = {2}", new Object[]{torpedoLaunchers, beamEmitters, attacker.getActionPoints()});

        while (attacker.getActionPoints() > 0) {
            TrekwarServer.LOG.log(Level.FINE, "Attacker AP left = {0}", attacker.getActionPoints());

            // remove all action points if weapons have been fired too may times
            if (beamEmitters == 0 && torpedoLaunchers == 0) {
                attacker.spendActionPoints(attacker.getActionPoints());
            }

            // prefer beam weapons if enemy has shields up
            if (defender.getCurrentShieldStrength() > 0 && beamEmitters > 0) {
                TrekwarServer.LOG.log(Level.FINE, "Defender shields are up, preferring beam emitters");
                if (attacker.hasBeamWeapons()) {
                    beamEmitters--;
                    fireBeamWeapon(attacker, defender);
                } else if (attacker.hasTorpedoLauncher() && torpedoLaunchers > 0) {
                    torpedoLaunchers--;
                    fireTorpedo(attacker, defender);
                } else {
                    // could not fire beam OR torpedoes (out of weapons to fire, loose remaining AP)
                    attacker.spendActionPoints(attacker.getActionPoints());
                }
            }
            // prefer torpedoes if shields are down
            else {
                TrekwarServer.LOG.log(Level.FINE, "Defender shields are down, preferring torpedoes");
                if (attacker.hasTorpedoLauncher() && torpedoLaunchers > 0) {
                    torpedoLaunchers--;
                    fireTorpedo(attacker, defender);
                } else if (attacker.hasBeamWeapons() && beamEmitters > 0) {
                    beamEmitters--;
                    fireBeamWeapon(attacker, defender);
                } else {
                    // could not fire beam OR torpedoes (out of weapons to fire, loose remaining AP)
                    attacker.spendActionPoints(attacker.getActionPoints());
                }
            }
        }
    }

    /**
     * Fires a beam weapon
     *
     * @param attacker the attacker
     * @param defender the target
     */
    private void fireBeamWeapon(Ship attacker, Ship defender) {
        int[] result = getRandomBeamEmitterDamage(attacker, attacker.getActionPoints());
        int damage = result[0];
        int apcost = result[1];
        if (damage < 1) {
            // no weapon found.. spend 2 AP point to avoid deadlock
            attacker.spendActionPoints(2);
            return;
        }

        attacker.spendActionPoints(apcost);

        if (chanceToHit(attacker, defender) > 100) {
            TrekwarServer.LOG.log(Level.FINE, "{0} firing beam weapons at {1}", new Object[]{attacker.getShipId(), defender.getShipId()});

            // crits based on skill
            int critRoll = TrekwarServer.PRNG.nextInt(1000);
            if (critRoll < (attacker.getXp() + 50)) {
                damage += (damage / 2);
            }

            // damage against shields
            if (defender.getCurrentShieldStrength() > 0) {
                int damageRemainder = damage - defender.getCurrentShieldStrength();
                defender.setCurrentShieldStrength(defender.getCurrentShieldStrength() - (damage * 2));
                if (damageRemainder > 2) {
                    // took down shields, remaining damage goes to armor/hp
                    damage = damageRemainder;
                } else {
                    // shields still up
                    return;
                }
            }

            // damage against armor
            if (defender.getCurrentArmorStrength() > 0 && damage > 0) {
                int damageRemainder = damage - defender.getCurrentArmorStrength();
                defender.setCurrentArmorStrength(defender.getCurrentArmorStrength() - (damage / 2));
                if (damageRemainder > 0) {
                    // destroyed armor, more damage hp
                    damage = damageRemainder;
                } else {
                    // armor holding
                    return;
                }
            }

            // damage against structure (hitpoints)
            if (defender.getCurrentHullStrength() > 0 && damage > 0) {
                defender.setCurrentHullStrength(defender.getCurrentHullStrength() - damage);
            }
        }
    }


    /**
     * Fires a torpedo on a target
     *
     * @param attacker the attacker
     * @param defender the target
     */
    private void fireTorpedo(Ship attacker, Ship defender) {
        TrekwarServer.LOG.log(Level.FINE, "{0} firing torpedo at {1}", new Object[]{attacker.getShipId(), defender.getShipId()});
        int[] result = getRandomTorpedoLauncherDamage(attacker, attacker.getActionPoints());
        int damage = result[0];
        int apcost = result[1];
        if (damage < 1) {
            // no weapon found.. spend 2 AP point to avoid deadlock
            attacker.spendActionPoints(2);
            return;
        }

        attacker.spendActionPoints(apcost);
        TrekwarServer.LOG.log(Level.FINE, "Torpedo damage = {0}, AP cost = apcost", damage);

        if (chanceToHit(attacker, defender) > 100) {
            TrekwarServer.LOG.log(Level.FINE, "Torpedo hit");
            // crits based on skill
            int critRoll = TrekwarServer.PRNG.nextInt(1000);
            if (critRoll < (attacker.getXp() + 50)) {
                TrekwarServer.LOG.log(Level.FINE, "Critical Hit");
                damage += (damage / 2);
            }

            // damage against shields
            if (defender.getCurrentShieldStrength() > 0) {
                int damageRemainder = damage - defender.getCurrentShieldStrength();
                defender.setCurrentShieldStrength(defender.getCurrentShieldStrength() - damage);
                if (damageRemainder > 2) {
                    // took down shields, half remaining damage goes to armor/hp
                    damage = damageRemainder / 2;
                } else {
                    // shields still up
                    return;
                }
            }

            // damage against armor
            if (defender.getCurrentArmorStrength() > 0 && damage > 0) {
                int damageRemainder = damage - defender.getCurrentArmorStrength();
                defender.setCurrentArmorStrength(defender.getCurrentArmorStrength() - (damage * 2));
                if (damageRemainder > 0) {
                    // destroyed armor, more damage hp
                    damage = damageRemainder - 5;
                } else {
                    // armor holding
                    return;
                }
            }

            // damage against structure (hitpoints)
            if (defender.getCurrentHullStrength() > 0 && damage > 0) {
                defender.setCurrentHullStrength(defender.getCurrentHullStrength() - (damage * 3));
            }
        } else {
            TrekwarServer.LOG.log(Level.FINE, "Torpedo Missed");
        }
    }


    /**
     * Calculates the chance an attacker has to hit another ship
     *
     * @param attacker the attacker
     * @param defender the other ship
     * @return chance to hit, a number from 0 to N (where 100 and above is a hit)
     */
    private int chanceToHit(Ship attacker, Ship defender) {
        int chanceToHit = TrekwarServer.PRNG.nextInt(100) + 60;

        // bonus from large target (signature strength)
        chanceToHit += defender.getSignatureStrength() / 10;

        // bonus from attacker sensor system
        chanceToHit += attacker.getSensorStrength() / 5;

        // bonus from computer tech
        chanceToHit += attacker.getUser().getHighestTech(techType.computertech).getLevel() / 2;

        // penalty for enemy maneuverability
        int agility = defender.getManeuverability() / 2;
        if (agility > 30) {
            agility = 30;
        }
        if (agility < 1) {
            agility = 0;
        }
        chanceToHit -= agility;
        return chanceToHit;
    }


    /**
     * Gets a random torpedo launcher from a ship
     *
     * @param ship        the ship to get torpedo launcher from
     * @param apAvailable only get weapon if it requires less AP's than this
     * @return int array with 2 ints.. [0] = damage,   [1] = action points required to fire weapon
     */
    private int[] getRandomTorpedoLauncherDamage(Ship ship, int apAvailable) {
        ArrayList<Integer> dmg = new ArrayList<Integer>();
        ArrayList<Integer> ap = new ArrayList<Integer>();

        for (ShipComponent c : ship.getComponents().values()) {
            if (c instanceof TorpedoLauncher) {
                if (((TorpedoLauncher) c).getActionPointsRequired() <= apAvailable) {
                    dmg.add(((TorpedoLauncher) c).getDamage());
                    ap.add(((TorpedoLauncher) c).getActionPointsRequired());
                }
            }
        }
        if (dmg.isEmpty()) {
            return new int[]{0, 0};
        }
        if (dmg.size() == 1) {
            return new int[]{dmg.get(0), ap.get(0)};
        } else {
            int index = TrekwarServer.PRNG.nextInt(dmg.size());
            return new int[]{dmg.get(index), ap.get(index)};
        }
    }

    /**
     * Gets a random beam weapon from a ship
     *
     * @param ship        the ship to get beam emitter from
     * @param apAvailable only get weapon if it requires less AP's than this
     * @return int array with 2 ints.. [0] = damage,   [1] = action points required to fire weapon
     */
    private int[] getRandomBeamEmitterDamage(Ship ship, int apAvailable) {
        ArrayList<Integer> dmg = new ArrayList<Integer>();
        ArrayList<Integer> ap = new ArrayList<Integer>();

        for (ShipComponent c : ship.getComponents().values()) {
            if (c instanceof BeamEmitter) {
                if (((BeamEmitter) c).getActionPointsRequired() <= apAvailable) {
                    dmg.add(((BeamEmitter) c).getDamage());
                    ap.add(((BeamEmitter) c).getActionPointsRequired());
                }
            } else if (c instanceof MiningLaser && apAvailable > 2) {
                dmg.add(((MiningLaser) c).getCapacity() / 2);
                ap.add(2);
            }
        }
        if (dmg.isEmpty()) {
            return new int[]{0, 0};
        }
        if (dmg.size() == 1) {
            return new int[]{dmg.get(0), ap.get(0)};
        } else {
            int index = TrekwarServer.PRNG.nextInt(dmg.size());
            return new int[]{dmg.get(index), ap.get(index)};
        }
    }


    /**
     * Checks if this Starsystem has fleets of opposing factions
     *
     * @param s the Starsystem to check
     * @return true if combat will take place in this system
     */
    public static boolean systemHasOpposingFleets(StarSystem s) {
        if (s.getFleets().size() >= 2) {
            Faction first = s.getFleets().get(0).getUser().getFaction();
            for (Fleet f : s.getFleets()) {
                if (!f.getUser().getFaction().equals(first)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if this Starsytem has fleets armed with weapons
     *
     * @param s the Starsystem to check
     * @return true if at least a single ship is armed
     */
    public static boolean systemHasFleetWithArmedShips(StarSystem s) {
        for (Fleet f : s.getFleets()) {
            for (Ship ship : f.getShips()) {
                if (ship.isArmed()) {
                    return true;
                }
            }
        }
        return false;
    }
}


/**
 * Simple statistics about a battle outcome
 */
class BattleReport {
    User user;
    StarSystem system;

    int shipsLost;
    int shipsTotal;
    int enemiesDestroyed;
    int enemiesTotal;

    /**
     * Creates a new battle report
     *
     * @param user   the recipient of the report
     * @param system the starsystem where the battle took place
     */
    BattleReport(User user, StarSystem system) {
        this.user = user;
        this.system = system;
    }
}