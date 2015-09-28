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
package com.frostvoid.trekwar.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import javax.sound.sampled.*;
import org.newdawn.easyogg.OggClip;

/**
 * Responsible for playing all sounds and music in the game
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class SoundSystem {
    private static SoundSystem instance;
    private boolean musicEnabled;
    private boolean sfxEnabled;
    private float musicVolume;
    private float sfxVolume;
    
    private Random rand;


    private HashMap<String, File> sounds;
    private ArrayList<String> musicFileNames;
    private OggClip currentMusicClip;
    
    public static SoundSystem getInstance() {
        if(instance == null)
            instance = new SoundSystem();
        return instance;
    }

    private SoundSystem() {
        sounds = new HashMap<String, File>();
        musicFileNames = new ArrayList<String>();
        rand = new Random();
    }
    
    public void loadSounds() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File dir = new File("sounds");
        String[] children = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if(name.endsWith(".wav"))
                    return true;
                return false;
            }
        });
        
        if (children == null) {
            // empty directory or 'dir' is not a directory
        }
        else {
            for (int i = 0; i < children.length; i++) {
                String filename = children[i];
                Client.LOG.log(Level.FINE, "loaded sound clip: {0}", filename);
                sounds.put(filename, new File("sounds/" + filename));
            }
        }
    }

    public void loadMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        File dir = new File("music");
        String[] children = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".ogg"))
                    return true;
                return false;
            }
        });

        if (children == null) {
            // empty directory or 'dir' is not a directory
        }
        else {
            for (int i = 0; i < children.length; i++) {
                String filename = children[i];
                Client.LOG.log(Level.FINE, "loaded music clip: {0}", filename);
                musicFileNames.add(filename);
            }
        }
    }

    public void loopMusic() throws FileNotFoundException, IOException {
        Thread musicThread = new Thread(new Runnable() {

            public void run() {
                while (musicEnabled) {

                    if (currentMusicClip == null || currentMusicClip.stopped()) {
                        if(currentMusicClip != null) {
                           currentMusicClip.stop();
                           currentMusicClip = null;
                        }
                        try {
                            currentMusicClip = new OggClip(new FileInputStream(new File("music/" + musicFileNames.get(rand.nextInt(musicFileNames.size())))));
                        } catch (IOException ex) { 
                            System.err.println(ex);
                        }
                        Client.LOG.log(Level.FINE, "playing music: {0}", currentMusicClip.toString()); // TODO XXX REMOVE
                        currentMusicClip.play();
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
        musicThread.setPriority(Thread.MAX_PRIORITY);
        musicThread.start();
        

    }

    public long playClip(String name) {
        if(!sfxEnabled) {
            return 0;
        }
        
        long result = 0;
        Client.LOG.log(Level.FINER, "Playing sound clip {0}", name);
        Clip c = null;
        
        if(sounds.get(name) == null) {
            Client.LOG.log(Level.WARNING, "Trying to play clip not found in sounds folder: {0}", name);
            return 0;
        }
        
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(sounds.get(name));
            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            c = (Clip) AudioSystem.getLine(info);
            c.open(ais);
        } catch (LineUnavailableException ex) {
            Client.LOG.log(Level.WARNING, "Line Unavailable when trying to play clip: {0}", sounds.get(name).getName());
        } catch (UnsupportedAudioFileException ex) {
            Client.LOG.log(Level.WARNING, "Unsupported Audio File when trying to play clip: {0}", sounds.get(name).getName());
        } catch (IOException ex) {
            Client.LOG.log(Level.WARNING, "IO error when trying to play clip: {0}", sounds.get(name).getName());
        }
        
        if (c != null) {
            c.stop();
            c.setFramePosition(0);
            FloatControl con = (FloatControl)c.getControl(FloatControl.Type.MASTER_GAIN);
            con.setValue(sfxVolume);
            
            c.start();
            result = c.getMicrosecondLength()/1000;
        } 
        return result;
    }
    
    public long playRandomClip(String[] files) {
        int i = rand.nextInt(files.length);
        return playClip(files[i]);
    }

    public static void main(String args[]) throws Exception {
        // TEST.. plays random files from sound directory
        SoundSystem s = new SoundSystem();
        s.setMusicEnabled(true);
        s.loopMusic();

       String[] keys = (s.sounds.keySet().toArray(new String[s.sounds.keySet().size()]));

        while (true) {
            int i = s.rand.nextInt(100);
            Thread.sleep(300);
            System.out.println(i);
            if (i < 5) {
                s.playClip(keys[s.rand.nextInt(keys.length)]);
            }
        }
    }
    
    public boolean isMusicEnabled() {
        return musicEnabled;
    }


    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }


    public float getMusicVolume() {
        return musicVolume;
    }


    public void setMusicVolume(float musicVolume) {
        this.musicVolume = musicVolume;
    }


    public boolean isSfxEnabled() {
        return sfxEnabled;
    }


    public void setSfxEnabled(boolean sfxEnabled) {
        this.sfxEnabled = sfxEnabled;
    }


    public float getSfxVolume() {
        return sfxVolume;
    }


    public void setSfxVolume(float sfxVolume) {
        this.sfxVolume = sfxVolume;
    }
    
    public void play_movefleet() {
        String[] filenames = { "fleet_general_yes_sir.wav", "fleet_general_affirmative.wav", 
            "fleet_move_course_set.wav", "fleet_move_engaging_warp.wav", "fleet_move_setting_course.wav" , 
            "fleet_move_plotting_course.wav", "fleet_general_as_you_wish.wav"};
        playRandomClip(filenames);
    }
    
    public void play_cancelFleetOrders() {
        String[] filenames = { "fleet_general_as_you_wish.wav", "fleet_general_yes_sir.wav", "fleet_general_affirmative.wav" };
        playRandomClip(filenames);
    }
    
    public void play_colonizeSystem() {
        String[] filenames = { "fleet_colonize_colonizing_system.wav", "fleet_colonize_establishing_new_colony.wav" };
        playRandomClip(filenames);
    }
    
    public void play_invadeSystem() {
        String[] filenames = { "fleet_invade_invading_system.wav", "fleet_invade_deploying_troops.wav",
        "fleet_invade_beaming_down_troops.wav", "fleet_invade_preparing_for_ground_assault.wav" };
        playRandomClip(filenames);
    }
    
    public void play_orbitalBombardment() {
        String[] filenames = { "fleet_bomb_photon_torpedoes_ready.wav", "fleet_bomb_attacking_planet_surface.wav" };
        playRandomClip(filenames);
    }
    
    public void play_miningAsteroid() {
        String[] filenames = { "fleet_resource_engaging_mining_lasers.wav", "fleet_resource_mining_lasers_operational.wav" };
        playRandomClip(filenames);
    }
    
    public void play_gatherDeuteriumFromNebula() {
        String[] filenames = { "fleet_resource_harvesting.wav", "fleet_resource_collecting_gas_from_nebula.wav",
            "fleet_resource_busard_collectors_engaging.wav" };
        playRandomClip(filenames);
    }
    
    public void play_no() {
         String[] filenames = { "fleet_no_unable_to_comply.wav" };
         playRandomClip(filenames);
    }

    public void play_yes() {
        String[] filenames = { "fleet_general_yes_sir.wav", "fleet_general_affirmative.wav" };
        playRandomClip(filenames);
    }
}