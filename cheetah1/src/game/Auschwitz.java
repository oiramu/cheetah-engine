/*
 * Copyright 2017 Carlos Rodriguez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package game;

import java.util.ArrayList;

import javax.sound.midi.Sequence;

import engine.audio.AudioUtil;
import engine.core.*;
import engine.rendering.*;
import game.doors.SecretWall;
import game.enemies.*;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Auschwitz implements Game {
	
	private static ArrayList<Sequence> playlist = new ArrayList<Sequence>();
	private static RenderingEngine engine;
    private static final int EPISODE_1 = 1;
    private static final int EPISODE_2 = 2;
    private static final int EPISODE_3 = 2;
    
    private static boolean isRunning;

    public static Level level;
    public static int levelNum;
    public static int startingLevel;
    public static int track;
    public static int currentEpisode;

    /**
     * The constructor method of the compiling game.
     */
    public void init() {
        for (int i = 0; i < 13; i++) {
        	playlist.add(AudioUtil.loadMidi("THEME" + i));
		}

        track = startingLevel - 1;
        levelNum = startingLevel - 1;
        loadLevel(1);

        isRunning = true;
    }
    
    /**
     * Sets the starting level depending of what level you want to start;
     * @param level
     */
    public static void setStartingLevel(int level) {
    	startingLevel = level;
    }

    /**
     * Checks all the inputs.
     */
    public void input() {
        level.input();

        /**
        if (Input.getKey(Input.KEY_1)) {
            System.exit(0);
        }

        if (Input.getKeyDown(Input.KEY_R)) {
            AudioUtil.playMidi(playlist.get(track));
        }
        */
    }

    /**
     * Updates everything renderer every single frame.
     */
    public void update() {
        if (isRunning) {
            level.update();
        }
    }

    /**
     * Renders everything every on screen.
     */
    @SuppressWarnings("static-access")
	public void render(RenderingEngine engine) {
    	this.engine = engine;
        if (isRunning) {
        	//engine.clearLights();
        	engine.render(level);
        }
    }

    /**
     * Load the level and also charges the next level when the last end.
     * @param offset count of level offset by offset.
     */
	@SuppressWarnings("static-access")
	public static void loadLevel(int offset) {
        try {
        	int secrets = 0;
            int deadMonsters = 0;
            int totalSecrets = 0;
            int totalMonsters = 0;
            int bulletTemp = 0;
            int shellTemp = 0;
            int armoriTemp = 0;
            boolean displayMonsters = false;
            boolean shotgunTemp = false;
            boolean machinegunTemp = false;
            boolean superShotgunTemp = false;
            boolean chaingunTemp = false;
            boolean armorbTemp = false;
            boolean mouseLocktemp = false;
            String weaponStateTemp = "";
            String sector;

            if (level != null) {
                totalMonsters = level.getNaziSoldiers().size() + level.getSsSoldiers().size()
                		+ level.getDogs().size() + + level.getNaziSergeants().size();
                
                totalSecrets = level.getSecretWalls().size();
                
                for (SecretWall secret : level.getSecretWalls()) {
                    if (secret.opens()) {
                    	secrets++;
                    }
                }

                for (NaziSoldier monster : level.getNaziSoldiers()) {
                    if (!monster.isAlive()) {
                        deadMonsters++;
                    }
                }
                
                for (SsSoldier ssSoldier : level.getSsSoldiers()) {
                    if (!ssSoldier.isAlive()) {
                        deadMonsters++;
                    }
                }
                
                for (Dog dog : level.getDogs()) {
                    if (!dog.isAlive()) {
                        deadMonsters++;
                    }
                }
                
                for (NaziSergeant naziSargent : level.getNaziSergeants()) {
                    if (!naziSargent.isAlive()) {
                        deadMonsters++;
                    }
                }

                displayMonsters = true;
                
                armoriTemp = level.getPlayer().getArmori();
                bulletTemp = level.getPlayer().getBullets();
                shellTemp = level.getPlayer().getShells();
                shotgunTemp = level.getPlayer().getShotgun();
                machinegunTemp = level.getPlayer().getMachinegun();
                superShotgunTemp = level.getPlayer().getSuperShotgun();
                chaingunTemp = level.getPlayer().getChaingun();
                weaponStateTemp = level.getPlayer().getWeaponState();
                mouseLocktemp = level.getPlayer().mouseLocked;
            }

            if(levelNum > 9) {
            	currentEpisode = EPISODE_2;
            } else if(levelNum > 19) {
            	currentEpisode = EPISODE_3;
            } else {
            	currentEpisode = EPISODE_1;
            }

            levelNum += offset;
            level = new Level(new Bitmap("level" + levelNum).flipX(), 
            		new Material(new Texture("mapTexture" + currentEpisode), new Vector3f(1,1,1)));

            if((levelNum/2) * 2 == levelNum) sector = "B."; else sector = "A.";   
            
            if(level.getPlayer().getArmori() == 0) {
            	if(armoriTemp == 0) {
            		level.getPlayer().addArmori(0);
            	}
            	level.getPlayer().addArmori(armoriTemp);
            }
            if(level.getPlayer().getBullets() == 0) {
            	if(bulletTemp == 0) {
            		level.getPlayer().addBullets(20);
            	}
            	level.getPlayer().addBullets(bulletTemp);
            }
            if(level.getPlayer().getShells() == 0) {
            	if(shellTemp == 0) {
            		level.getPlayer().addShells(20);
            	}
            	level.getPlayer().addShells(shellTemp);
            }
            level.getPlayer().setShotgun(shotgunTemp);
            level.getPlayer().setMachinegun(machinegunTemp);
            level.getPlayer().setSuperShotgun(superShotgunTemp);
            level.getPlayer().setChaingun(chaingunTemp);
            level.getPlayer().setArmorb(armorbTemp);
            level.getPlayer().setWeaponState(weaponStateTemp);
            level.getPlayer().mouseLocked = mouseLocktemp;
            
            track += offset;

            AudioUtil.playMidi(playlist.get(track));

            while (track >= playlist.size()) {
                track -= playlist.size();
            }

            System.out.println("=============================");
            System.out.println("Level " + levelNum + " floor " + levelNum + sector);
            System.out.println("=============================");

            if (displayMonsters) {  
            	engine.clearLights();
            	System.out.println("Killed " + deadMonsters + "/" + totalMonsters + " baddies: " +
            	((float) deadMonsters / (float) totalMonsters) * 100f + "%");        	
            	System.out.println("Secrets " + secrets + "/" + totalSecrets + " secrets: " +
                    	((float) secrets / (float) totalSecrets) * 100f + "%");
            	
            	if(level.getPlayer().getWeaponState() == level.getPlayer().HAND){
            		level.getPlayer().gotHand();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().PISTOL) {
            		level.getPlayer().gotPistol();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().MACHINEGUN && machinegunTemp == true) {
            		level.getPlayer().gotMachinegun();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().SHOTGUN && shotgunTemp == true) {
            		level.getPlayer().gotShotgun();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().SUPER_SHOTGUN && superShotgunTemp == true) {
            		level.getPlayer().gotSShotgun();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().CHAINGUN && chaingunTemp == true) {
            		level.getPlayer().gotChaingun();
            	}
            }
        } catch (RuntimeException ex) {
        	ex.printStackTrace();
            isRunning = false;
            System.out.println("GAME OVER!");
            AudioUtil.stopMidi();
        }

    }

    /**
     * Gets the actual level.
     * @return Level.
     */
    public static Level getLevel() {
        return level;
    }

    /**
     * Allow to stop the game rendering without stop all the program.
     * @param value True or false.
     */
    public static void setIsRunning(boolean value) {
        isRunning = value;
    }
}
