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
import java.util.HashMap;

import javax.sound.midi.Sequence;

import static engine.components.Constants.*;
import static engine.core.CoreEngine.*;

import engine.audio.AudioUtil;
import engine.components.Constants;
import engine.core.*;
import engine.menu.CreditsMenu;
import engine.menu.Menu;
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
	
	private static HashMap<String,TextureFont> 	text = new HashMap<String,TextureFont>();
	private static ArrayList<Sequence> 			playlist = new ArrayList<Sequence>();
	
    private static final int 					EPISODE_1 = 1;
    private static final int 					EPISODE_2 = 2;
    private static final int 					EPISODE_3 = 3;

    public static Level 						level;
    public static Material						material;
    
    private static Menu							menu;
    
    public static boolean						isPaused;
    public static int 							levelNum;
    public static int 							startingLevel;
    public static int 							track;
    public static int							currentEpisode;
    
    private static boolean 						isRunning;
    private static boolean 						displayStats = false;
    private static int 							secrets = 0;
    private static int 							deadMonsters = 0;
    private static int 							totalSecrets = 0;
    private static int 							totalMonsters = 0;
    private static double						stateTime = 0;

    /**
     * The constructor method of the compiling game.
     */
	public void init() {
		Constants.load("res/config.txt");
    	text.put("Level",new TextureFont("", new Vector2f(-1.25f,1.2f), new Vector2f(0.75f,0.75f)));
        text.put("Enemies",new TextureFont("", new Vector2f(-1.25f,1.1f), new Vector2f(0.75f,0.75f)));
        text.put("Secrets",new TextureFont("", new Vector2f(-1.25f,1.0f), new Vector2f(0.75f,0.75f)));
        text.put("Paused",new TextureFont("PAUSED!", new Vector2f(-0.175f,0.175f), new Vector2f(1.5f,1.5f)));
        for (int i = 0; i < 13; i++) playlist.add(AudioUtil.loadMidi("THEME" + i));

        track = startingLevel - 1;
        levelNum = startingLevel - 1;
        isRunning = true;
        loadLevel(1, true);
    }
	
	/**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {
    	isRunning = false;
    	AudioUtil.stopMidi();
        System.exit(0);
    }
    
    /**
     * Prints the statistics of the level.
     * @param renderingEngine to use.
     */
    private void printStats(RenderingEngine renderingEngine) {
    	double time = Time.getTime();
    	if(time<stateTime+5.0f) {
	    	text.get("Level").setText("Level:" + levelNum + ";Episode" + currentEpisode);
	    	text.get("Level").render(renderingEngine);
	    	if(displayStats) {
		    	text.get("Enemies").setText("Killed:" + deadMonsters + "/" + totalMonsters + " Nazis:" +
		            	(int)(((float) deadMonsters / (float) totalMonsters) * 100f )+ "%");
		    	text.get("Enemies").render(renderingEngine);
		    	text.get("Secrets").setText("Secrets:" + secrets + "/" + totalSecrets + " Secrets:" +
		    			(int)(((float) secrets / (float) totalSecrets) * 100f) + "%");
		    	text.get("Secrets").render(renderingEngine);
	    	}
    	}
    }
    
    /**
     * Sets the starting level depending of what level you want to start;
     * @param level to set.
     */
    public static void setStartingLevel(int level) {startingLevel = level;}

    /**
     * Checks all the inputs.
     */
    public void input() {
    	
    	if(!isPaused)
    		level.input();
    	
        if(isPaused) {
			if (Input.getKeyDown(Input.KEY_ESCAPE)) {
				AudioUtil.playAudio(AudioUtil.loadAudio("button"), 0);
				isPaused = false;
			}
        } else {
        	if (Input.getKeyDown(Input.KEY_ESCAPE)) {
        		AudioUtil.playAudio(AudioUtil.loadAudio("button"), 0);
        		isPaused = true;
        	}
        }
    }

    /**
     * Updates everything renderer every single frame.
     * @param delta of time
     */
    public void update(double delta) {
        if (isRunning) {
        	if(!isPaused)
        		level.update(delta);
        } else {
        	menu.update();
        }
    }

    /**
     * Renders everything every on screen.
     * @param engine to render
     */
	public void render(RenderingEngine engine) {
        if (isRunning) {
        	engine.render(level);
        	printStats(engine);
        	if(isPaused)
        		text.get("Paused").render(engine);
        } else {
        	menu.draw2D();
        }
    }
	
	/**
     * Reloads the last level played.
     */
	public static void reloadLevel() { loadLevel(levelNum-levelNum, false); }

    /**
     * Load the level and also charges the next level when the last end.
     * @param offset count of level offset by offset
     * @param displayText if it does
     */
	@SuppressWarnings("static-access")
	public static void loadLevel(int offset, boolean displayText) {
        try {
        	secrets = 0;
            deadMonsters = 0;
            totalSecrets = 0;
            totalMonsters = 0;
            int healthTemp = 0;
            int bulletTemp = 0;
            int shellTemp = 0;
            int rocketTemp = 0;
            int gasTemp = 0;
            int armoriTemp = 0;
            int maxHealthTemp = 0;
            int maxBulletTemp = 0;
            int maxShellTemp = 0;
            int maxRocketTemp = 0;
            int maxGasTemp = 0;
            int maxArmoriTemp = 0;
            boolean shotgunTemp = false;
            boolean machinegunTemp = false;
            boolean superShotgunTemp = false;
            boolean chaingunTemp = false;
            boolean rocketLauncherTemp = false;
            boolean flameThrowerTemp = false;
            boolean armorbTemp = false;
            boolean mouseLocktemp = false;
            String weaponStateTemp = "";

            if (level != null) {
                totalMonsters = level.getNaziSoldiers().size() + level.getSsSoldiers().size()
                		+ level.getDogs().size() + level.getNaziSergeants().size()
                		+ level.getZombies().size() + level.getCaptains().size();
                
                totalSecrets = level.getSecretWalls().size();
                
                for (SecretWall secret : level.getSecretWalls()) {
                    if (secret.opens())
                    	secrets++;
                }

                for (NaziSoldier naziSoldier : level.getNaziSoldiers()) {
                    if (!naziSoldier.isAlive())
                        deadMonsters++;
                }
                
                for (SsSoldier ssSoldier : level.getSsSoldiers()) {
                    if (!ssSoldier.isAlive())
                        deadMonsters++;
                }
                
                for (Dog dog : level.getDogs()) {
                    if (!dog.isAlive())
                        deadMonsters++;
                }
                
                for (NaziSergeant naziSargent : level.getNaziSergeants()) {
                    if (!naziSargent.isAlive())
                        deadMonsters++;
                }
                
                for (Zombie zombie : level.getZombies()) {
                    if (!zombie.isAlive())
                        deadMonsters++;
                }
                
                for (Captain captain : level.getCaptains()) {
                    if (!captain.isAlive())
                        deadMonsters++;
                }

                displayStats = true;
                
                healthTemp = level.getPlayer().getHealth();
                armoriTemp = level.getPlayer().getArmor();
                bulletTemp = level.getPlayer().getBullets();
                shellTemp = level.getPlayer().getShells();
                rocketTemp = level.getPlayer().getRockets();
                gasTemp = level.getPlayer().getGas();
                maxHealthTemp = level.getPlayer().getMaxHealth();
                maxBulletTemp = level.getPlayer().getMaxBullets();
                maxShellTemp = level.getPlayer().getMaxShells();
                maxRocketTemp = level.getPlayer().getMaxRockets();
                maxGasTemp = level.getPlayer().getMaxGas();
                maxArmoriTemp = level.getPlayer().getMaxArmor();
                shotgunTemp = level.getPlayer().isShotgun();
                machinegunTemp = level.getPlayer().isMachinegun();
                superShotgunTemp = level.getPlayer().isSuperShotgun();
                chaingunTemp = level.getPlayer().isChaingun();
                rocketLauncherTemp = level.getPlayer().isRocketLauncher();
                flameThrowerTemp = level.getPlayer().isFlameThrower();
                weaponStateTemp = level.getPlayer().getWeaponState();
                armorbTemp = level.getPlayer().isArmor();
                mouseLocktemp = level.getPlayer().mouseLocked;
                getRenderingEngine().clearLights();
            }

            if(levelNum > 9)
            	currentEpisode = EPISODE_2;
            else if(levelNum > 19)
            	currentEpisode = EPISODE_3;
            else
            	currentEpisode = EPISODE_1;

            levelNum += offset;
            
            switch(GAME_GRAPHICS) {
            	case "Low":
            		material = new Material(new Texture("mapTexture" + currentEpisode));
            		break;
            	case "High":
            		material = new Material(new Texture("mapTexture" + currentEpisode), 1, 8, 
            				new Texture("mapTextureNormal" + currentEpisode), 
            				new Texture("mapTextureBump" + currentEpisode), 0.0004f, -0.75f);
            		break;
            }
            
            level = new Level(new Bitmap("level" + levelNum).flipX(), material);
            
            switch(CLEAR_LIGHTS) {
    			case "True":
    				getRenderingEngine().clearLights();
    				break;
    			case "False":
    				break;
            }
            
            if(level.getPlayer().getHealth() == 0) {
            	if(healthTemp == 0)
            		level.getPlayer().setHealth(100);
            	level.getPlayer().setHealth(healthTemp);
            }
            if(level.getPlayer().getBullets() == 0) {
            	if(bulletTemp == 0)
            		level.getPlayer().setBullets(20);
            	level.getPlayer().setBullets(bulletTemp);
            }
            if(level.getPlayer().getShells() == 0) {
            	if(shellTemp == 0)
            		level.getPlayer().setShells(20);
            	level.getPlayer().setShells(shellTemp);
            }
            if(level.getPlayer().getRockets() == 0) {
            	if(rocketTemp == 0)
            		level.getPlayer().setRockets(10);
            	level.getPlayer().setRockets(rocketTemp);
            }
            if(level.getPlayer().getGas() == 0) {
            	if(gasTemp == 0)
            		level.getPlayer().setGas(10);
            	level.getPlayer().setGas(gasTemp);
            }
            if(level.getPlayer().getMaxArmor() == 0) {
            	if(maxArmoriTemp == 0)
            		level.getPlayer().setMaxArmor(100);
            	level.getPlayer().setMaxArmor(maxArmoriTemp);
            }
            if(level.getPlayer().getMaxHealth() == 0) {
            	if(maxHealthTemp == 0)
            		level.getPlayer().setMaxHealth(100);
            	level.getPlayer().setMaxHealth(maxHealthTemp);
            }
            if(level.getPlayer().getMaxBullets() == 0) {
            	if(maxBulletTemp == 0)
            		level.getPlayer().setMaxBullets(100);
            	level.getPlayer().setMaxBullets(maxBulletTemp);
            }
            if(level.getPlayer().getMaxShells() == 0) {
            	if(maxShellTemp == 0)
            		level.getPlayer().setMaxShells(50);
            	level.getPlayer().setMaxShells(maxShellTemp);
            }
            if(level.getPlayer().getMaxRockets() == 0) {
            	if(maxRocketTemp == 0)
            		level.getPlayer().setMaxRockets(50);
            	level.getPlayer().setMaxRockets(maxRocketTemp);
            }
            if(level.getPlayer().getMaxGas() == 0) {
            	if(maxGasTemp == 0)
            		level.getPlayer().setMaxGas(50);
            	level.getPlayer().setMaxGas(maxGasTemp);
            }
            level.getPlayer().setArmori(armoriTemp);
            level.getPlayer().setShotgun(shotgunTemp);
            level.getPlayer().setMachinegun(machinegunTemp);
            level.getPlayer().setSuperShotgun(superShotgunTemp);
            level.getPlayer().setChaingun(chaingunTemp);
            level.getPlayer().setRocketLauncher(rocketLauncherTemp);
            level.getPlayer().setFlameThrower(flameThrowerTemp);
            level.getPlayer().setArmor(armorbTemp);
            level.getPlayer().setWeaponState(weaponStateTemp);
            level.getPlayer().mouseLocked = mouseLocktemp;
            
            track += offset;

            AudioUtil.playMidi(playlist.get(track));

            while (track >= playlist.size()) track -= playlist.size();
            if(displayText) stateTime = Time.getTime();

            if (displayStats) {
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
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().ROCKET_LAUNCHER && rocketLauncherTemp == true) {
            		level.getPlayer().gotRocketLauncher();
            	}else if(level.getPlayer().getWeaponState() == level.getPlayer().FLAME_THROWER && flameThrowerTemp == true) {
            		level.getPlayer().gotFlameThrower();
            	}
            	
            }
            
        } catch (RuntimeException ex) {
        	ex.printStackTrace();
            isRunning = false;
            //Debug.printErrorMessage(ex.getMessage(), "GAME OVER!");
            System.out.println("GAME OVER!");
            AudioUtil.stopMidi();
            menu = new CreditsMenu();
        }

    }

    /**
     * Gets the actual level.
     * @return Level.
     */
    public static Level getLevel() {return level;}

    /**
     * Allow to stop the game rendering without stop all the program.
     * @param value True or false.
     */
    public static void setIsRunning(boolean value) {isRunning = value;}

    /**
     * Returns it's own game name
     * @return name
     */
	public String getName() { return "Auschwitz"; }

}