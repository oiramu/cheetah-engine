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
import java.util.Random;

import javax.sound.midi.Sequence;

import static engine.components.Constants.*;
import static engine.core.CoreEngine.*;

import engine.audio.AudioUtil;
import engine.components.Constants;
import engine.core.*;
import engine.menu.CreditsMenu;
import engine.menu.Menu;
import engine.rendering.*;
import game.enemies.*;
import game.walls.SecretWall;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.2
 * @since 2017
 */
public class Auschwitz implements Game {
	
	private static ArrayList<Sequence> 			playlist = new ArrayList<Sequence>();
	public static HashMap<String,HUD> 			text;
	
    private static final int 					EPISODE_1 = 1;
    private static final int 					EPISODE_2 = 2;
    private static final int 					EPISODE_3 = 3;

    public static Level 						level;
    public static Material						material;
    
    private static InGameMenu					gameMenu;
    private static Menu							menu;
    
    public static boolean						isPaused;
    public static boolean						sureToExit;
    public static boolean						toExit;
    public static int 							levelNum;
    public static int 							startingLevel;
    public static int 							track;
    public static int							currentEpisode;
    
    private static boolean 						displayStats = false;
    private static int 							secrets = 0;
    private static int 							deadMonsters = 0;
    private static int 							totalSecrets = 0;
    private static int 							totalMonsters = 0;
    private static double						stateTime = 0;
    
    private String [] 							exitMessages = {
    		"Please don't leave, there's more nazis to toast!",
    		"I wouldn't leave if I were you. " + System.getProperty("os.name") + " is much worse.",
    		"You're trying to say you like " + System.getProperty("os.name") + " better than me, right?",
    		"Don't leave yet.There's a nazi around that corner!",
    		"Go ahead, leave...but don't come begging next time",
    		"Are you sure you want to quit this great game?",
    		"If I were your boss, I'd deathmatch ya in a minute!",
    		"Don't quit now! We're still spending your money!",
    		"Hey, Daniel! Can we say 'fuck' in the game?"
    };

    /**
     * The constructor method of the compiling game.
     */
	public void init() {
		Constants.load("res/config.txt");
		gameMenu = new InGameMenu();
        for (int i = 0; i < 13; i++) playlist.add(AudioUtil.loadMidi("THEME" + i));
        text = gameMenu.text;
        track = startingLevel - 1;
        levelNum = startingLevel - 1;
        loadLevel(1, true);
    }
	
	/**
     * Cleans everything in the GPU and RAM.
     */
    @Override
    protected void finalize() {
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
        	if(!toExit) {
				if (Input.getKeyDown(Input.KEY_ESCAPE)) {
					AudioUtil.playAudio(AudioUtil.loadAudio("button"), 0);
					isPaused = false;
				}
				if (Input.getKeyDown(Input.KEY_R)) {
					AudioUtil.playAudio(AudioUtil.loadAudio("button"), 0);
					reloadLevel();
					isPaused = false;
				}
				if (Input.getKeyDown(Input.KEY_B)) {
					AudioUtil.playAudio(AudioUtil.loadAudio("button"), 0);
					CoreEngine.getCurrent().cleanUp();
				}
				if (Input.getKeyDown(Input.KEY_X)) {
					AudioUtil.playAudio(AudioUtil.loadAudio("button"), 0);
					int textId = new Random().nextInt(exitMessages.length);
					text.get("areYouSure").setText(exitMessages[textId]);
					toExit = true;
				}
        	} else {
				if(toExit && Input.getKeyDown(Input.KEY_Y)) {
					AudioUtil.playAudio(AudioUtil.loadAudio("button"), 0);
					finalize();
				}else if(toExit && Input.getKeyDown(Input.KEY_N)) {
					AudioUtil.playAudio(AudioUtil.loadAudio("button"), 0);
					toExit = false;
				}
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
    	if(!isPaused)
    		level.update(delta);
    	if(menu != null)
    		menu.update();
    }

    /**
     * Renders everything every on screen.
     * @param engine to render
     */
	public void render(RenderingEngine engine) {
    	engine.render(level);
    	printStats(engine);
    	if(isPaused && !toExit) {
    		gameMenu.renderPause(engine);
    	} else if (toExit) {
    		gameMenu.renderQuit(engine);
    	}
    	if(menu != null)
    		menu.draw2D();
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
            menu = new CreditsMenu();
        }

    }

    /**
     * Gets the actual level.
     * @return Level.
     */
    public static Level getLevel() {return level;}

    /**
     * Returns it's own game name
     * @return name
     */
	public String getName() { return "Auschwitz"; }

}