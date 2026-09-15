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
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javax.sound.midi.Sequence;

import static engine.components.Constants.*;
import static engine.core.CoreEngine.*;

import engine.audio.AudioManager;
import engine.audio.AudioUtil;
import engine.audio.SoundLibrary;
import engine.components.Constants;
import engine.components.GameComponent;
import engine.core.*;
import engine.core.utils.Log;
import engine.menu.CreditsMenu;
import engine.menu.Menu;
import engine.rendering.*;
import game.enemies.*;
import game.objects.Barrel;
import game.pickUps.*;
import game.save.LevelDeltas;
import game.save.SaveGame;
import game.save.SaveGameManager;
import game.walls.SecretWall;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.3
 * @since 2017
 */
public class Auschwitz implements Game {
	
	private static ArrayList<Sequence> 			playlist = new ArrayList<Sequence>();
	public static HashMap<String,HUD> 			text;

	private static final String					CLICK_SOUND = "button";

    private static final int 					EPISODE_1 = 1;
    private static final int 					EPISODE_2 = 2;
    private static final int 					EPISODE_3 = 3;

    public static Level 						level;
    public static Material						material;

    // Set by loadSave() right before restarting the engine; consumed and
    // cleared by loadLevel() once the target level has been regenerated.
    private static SaveGame						pendingSave;

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
        for (int i = 0; i < 19; i++) playlist.add(AudioUtil.loadMidi("THEME" + i));
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
					AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
					isPaused = false;
				}
				if (Input.getKeyDown(Input.KEY_R)) {
					AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
					reloadLevel();
					isPaused = false;
				}
				if (Input.getKeyDown(Input.KEY_B)) {
					AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
					CoreEngine.getCurrent().cleanUp();
				}
				if (Input.getKeyDown(Input.KEY_X)) {
					AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
					int textId = new Random().nextInt(exitMessages.length);
					text.get("areYouSure").setText(exitMessages[textId]);
					toExit = true;
				}
				if (Input.getKeyDown(Input.KEY_1)) {
					AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
					saveGame("1");
				}
				if (Input.getKeyDown(Input.KEY_2)) {
					AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
					saveGame("2");
				}
				if (Input.getKeyDown(Input.KEY_3)) {
					AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
					saveGame("3");
				}
        	} else {
				if(toExit && Input.getKeyDown(Input.KEY_Y)) {
					AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
					finalize();
				}else if(toExit && Input.getKeyDown(Input.KEY_N)) {
					AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
					toExit = false;
				}
        	}
        } else {
        	if (Input.getKeyDown(Input.KEY_ESCAPE)) {
        		AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
        		isPaused = true;
        	}
        	if(Debug.state) {
				if (Input.getKeyDown(Input.KEY_F3))
					Debug.state = false;
            } else {
            	if (Input.getKeyDown(Input.KEY_F3))
            		Debug.state = true;
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
    	if(isPaused && !toExit)
    		gameMenu.renderPause(engine);
    	else if (toExit)
    		gameMenu.renderQuit(engine);
    	if(menu != null)
    		menu.draw2D();
    }
	
	/**
     * Reloads the last level played.
     */
	public static void reloadLevel() { loadLevel(levelNum-levelNum, false); }

	/**
	 * Saves current progress (player stats/position, level deltas) to a
	 * slot, encrypted via SaveGameManager. Called both from the pause
	 * menu's 1/2/3 keys and from autoSaveProgress() below.
	 * @param slot identifier ("1", "2", "3", or the reserved "autosave" name).
	 */
	private static void saveGame(String slot) {
		if(SaveGameManager.save(slot, buildSaveGame()))
			level.getPlayer().notifySaved("Game saved");
	}

	/**
	 * Saves automatically the moment a level is completed. Called only
	 * from Level's exit-point trigger (Level.openDoors(), right after it
	 * calls loadLevel() for a reached exit) - never from loadLevel()
	 * itself, since loadLevel() is also used for death/reload/restart
	 * (reloadLevel(), offset 0) and the very first level at game start,
	 * neither of which is "passing a level".
	 */
	public static void autoSaveProgress() { saveGame("autosave"); }

	/**
	 * Loads a save slot and restarts the engine into it - mirrors the
	 * existing loadEpisode* actions (SEventListener's cases 3/4/5), which
	 * also just set state then call CoreEngine.getCurrent().start().
	 * @param slot identifier to load.
	 */
	public static void loadSave(String slot) {
		SaveGame saveGame = SaveGameManager.load(slot);
		if(saveGame == null) {
			Log.error("Could not load save slot '" + slot + "'");
			return;
		}
		pendingSave = saveGame;
		setStartingLevel(saveGame.levelNum);
		CoreEngine.getCurrent().start();
	}

	/**
	 * Applies a loaded save's player stats/position/weapon and level
	 * deltas onto the just-regenerated level/player - called from
	 * loadLevel() right after its own existing carryover-reapplication
	 * block finishes, so this is authoritative over it.
	 * @param saveGame to apply.
	 */
	private static void applySave(SaveGame saveGame) {
		Player player = level.getPlayer();
		player.restoreFromSave(saveGame.player);
		player.getCamera().setPos(new Vector3f(saveGame.player.posX, saveGame.player.posY, saveGame.player.posZ));
		player.getCamera().setRotation(new Quaternion(saveGame.player.rotX, saveGame.player.rotY, saveGame.player.rotZ, saveGame.player.rotW));

		String weaponState = saveGame.player.weaponState == null ? "" : saveGame.player.weaponState;
		switch(weaponState) {
			case Player.HAND: 				player.gotHand(); break;
			case Player.SHOTGUN: 			player.gotShotgun(); break;
			case Player.MACHINEGUN: 		player.gotMachinegun(); break;
			case Player.SUPER_SHOTGUN: 	player.gotSShotgun(); break;
			case Player.CHAINGUN: 			player.gotChaingun(); break;
			case Player.ROCKET_LAUNCHER: 	player.gotRocketLauncher(); break;
			case Player.FLAME_THROWER: 	player.gotFlameThrower(); break;
			default: 						player.gotPistol(); break;
		}

		applyLevelDeltas(saveGame.levelDeltas);
	}

	/**
	 * Builds a SaveGame snapshot of the current level/player.
	 * @return the built save.
	 */
	private static SaveGame buildSaveGame() {
		SaveGame saveGame = new SaveGame();
		saveGame.levelNum = levelNum;
		saveGame.player = level.getPlayer().toSave();
		saveGame.levelDeltas = captureLevelDeltas();
		return saveGame;
	}

	/**
	 * Captures everything about the current level that has diverged from
	 * its bitmap-regenerated baseline: dead enemies and removed
	 * pickups/barrels by spawn-order index, and opened secret walls.
	 * Doors/locked doors are deliberately not captured - they auto-close
	 * on their own timer (see Door.update()/LockedDoor.update()), so
	 * there's no persistent "left open" state to save.
	 * @return the captured deltas.
	 */
	private static LevelDeltas captureLevelDeltas() {
		LevelDeltas deltas = new LevelDeltas();

		deltas.deadEnemies.put("naziSoldiers", deadIndices(level.getNaziSoldiers()));
		deltas.deadEnemies.put("dogs", deadIndices(level.getDogs()));
		deltas.deadEnemies.put("ssSoldiers", deadIndices(level.getSsSoldiers()));
		deltas.deadEnemies.put("naziSergeants", deadIndices(level.getNaziSergeants()));
		deltas.deadEnemies.put("ghosts", deadIndices(level.getGhosts()));
		deltas.deadEnemies.put("zombies", deadIndices(level.getZombies()));
		deltas.deadEnemies.put("captains", deadIndices(level.getCaptains()));
		deltas.deadEnemies.put("commanders", deadIndices(level.getCommanders()));

		deltas.removedPickups.put("medkits", removedIndices(level.getMedkits()));
		deltas.removedPickups.put("foods", removedIndices(level.getFoods()));
		deltas.removedPickups.put("bullets", removedIndices(level.getBullets()));
		deltas.removedPickups.put("shells", removedIndices(level.getShells()));
		deltas.removedPickups.put("bags", removedIndices(level.getBags()));
		deltas.removedPickups.put("shotguns", removedIndices(level.getShotguns()));
		deltas.removedPickups.put("machineguns", removedIndices(level.getMachineguns()));
		deltas.removedPickups.put("armors", removedIndices(level.getArmors()));
		deltas.removedPickups.put("superShotguns", removedIndices(level.getSuperShotguns()));
		deltas.removedPickups.put("helmets", removedIndices(level.getHelmets()));
		deltas.removedPickups.put("chainguns", removedIndices(level.getChainguns()));
		deltas.removedPickups.put("keys", removedIndices(level.getKeys()));
		deltas.removedPickups.put("rockets", removedIndices(level.getRockets()));
		deltas.removedPickups.put("rocketLaunchers", removedIndices(level.getRocketLaunchers()));

		deltas.poppedBarrels = removedIndices(level.getBarrels());

		ArrayList<SecretWall> secretWalls = level.getSecretWalls();
		for(int i = 0; i < secretWalls.size(); i++)
			if(secretWalls.get(i).opens())
				deltas.openedSecretWalls.add(i);

		return deltas;
	}

	/**
	 * Reapplies captured deltas onto a freshly-regenerated level, where
	 * every enemy/pickup/barrel/secret wall has just spawned fresh from
	 * the bitmap.
	 * @param deltas to apply.
	 */
	private static void applyLevelDeltas(LevelDeltas deltas) {
		killByIndices(level.getNaziSoldiers(), deltas.deadEnemies.get("naziSoldiers"));
		killByIndices(level.getDogs(), deltas.deadEnemies.get("dogs"));
		killByIndices(level.getSsSoldiers(), deltas.deadEnemies.get("ssSoldiers"));
		killByIndices(level.getNaziSergeants(), deltas.deadEnemies.get("naziSergeants"));
		killByIndices(level.getGhosts(), deltas.deadEnemies.get("ghosts"));
		killByIndices(level.getZombies(), deltas.deadEnemies.get("zombies"));
		killByIndices(level.getCaptains(), deltas.deadEnemies.get("captains"));
		killByIndices(level.getCommanders(), deltas.deadEnemies.get("commanders"));

		removeByIndices(level.getMedkits(), deltas.removedPickups.get("medkits"), Level::removeMedkit);
		removeByIndices(level.getFoods(), deltas.removedPickups.get("foods"), Level::removeFood);
		removeByIndices(level.getBullets(), deltas.removedPickups.get("bullets"), Level::removeBullets);
		removeByIndices(level.getShells(), deltas.removedPickups.get("shells"), Level::removeShells);
		removeByIndices(level.getBags(), deltas.removedPickups.get("bags"), Level::removeBags);
		removeByIndices(level.getShotguns(), deltas.removedPickups.get("shotguns"), Level::removeShotgun);
		removeByIndices(level.getMachineguns(), deltas.removedPickups.get("machineguns"), Level::removeMachineGun);
		removeByIndices(level.getArmors(), deltas.removedPickups.get("armors"), Level::removeArmor);
		removeByIndices(level.getSuperShotguns(), deltas.removedPickups.get("superShotguns"), Level::removeSuperShotgun);
		removeByIndices(level.getHelmets(), deltas.removedPickups.get("helmets"), Level::removeHelmet);
		removeByIndices(level.getChainguns(), deltas.removedPickups.get("chainguns"), Level::removeChainGun);
		removeByIndices(level.getKeys(), deltas.removedPickups.get("keys"), Level::removeArmor);
		removeByIndices(level.getRockets(), deltas.removedPickups.get("rockets"), Level::removeRockets);
		removeByIndices(level.getRocketLaunchers(), deltas.removedPickups.get("rocketLaunchers"), Level::removeRocketLauncher);

		removeByIndices(level.getBarrels(), deltas.poppedBarrels, Level::removeBarrel);

		ArrayList<SecretWall> secretWalls = level.getSecretWalls();
		if(deltas.openedSecretWalls != null)
			for(int index : deltas.openedSecretWalls)
				if(index >= 0 && index < secretWalls.size())
					secretWalls.get(index).openInstantly();
	}

	/**
	 * Spawn-order indices of the enemies in a list that are already dead.
	 * @param enemies to check.
	 * @return matching indices.
	 */
	private static List<Integer> deadIndices(List<? extends Enemy> enemies) {
		List<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < enemies.size(); i++)
			if(!enemies.get(i).isAlive())
				indices.add(i);
		return indices;
	}

	/**
	 * Spawn-order indices of the pickups/barrels in a list that have
	 * already been removed (picked up or destroyed) - see
	 * Level.wasRemoved()'s doc for why this is needed instead of just
	 * checking list membership.
	 * @param objects to check.
	 * @return matching indices.
	 */
	private static List<Integer> removedIndices(List<? extends GameComponent> objects) {
		List<Integer> indices = new ArrayList<Integer>();
		for(int i = 0; i < objects.size(); i++)
			if(Level.wasRemoved(objects.get(i)))
				indices.add(i);
		return indices;
	}

	/**
	 * Marks the enemies at the given spawn-order indices dead.
	 * @param enemies to kill from.
	 * @param indices to kill.
	 */
	private static void killByIndices(List<? extends Enemy> enemies, List<Integer> indices) {
		if(indices == null) return;
		for(int index : indices)
			if(index >= 0 && index < enemies.size())
				enemies.get(index).killInstantly();
	}

	/**
	 * Removes the pickups/barrels at the given spawn-order indices, via
	 * the same per-type Level.removeXxx() static method a real pickup
	 * calls on collection.
	 * @param <E> pickup/barrel type.
	 * @param objects to remove from.
	 * @param indices to remove.
	 * @param remover Level.removeXxx() method reference for this type.
	 */
	private static <E extends GameComponent> void removeByIndices(List<E> objects, List<Integer> indices, Consumer<E> remover) {
		if(indices == null) return;
		for(int index : indices)
			if(index >= 0 && index < objects.size())
				remover.accept(objects.get(index));
	}

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
                
                for (SecretWall secret : level.getSecretWalls())
                    if (secret.opens())
                    	secrets++;

                for (NaziSoldier naziSoldier : level.getNaziSoldiers())
                    if (!naziSoldier.isAlive())
                        deadMonsters++;
                
                for (SsSoldier ssSoldier : level.getSsSoldiers())
                    if (!ssSoldier.isAlive())
                        deadMonsters++;
                
                for (Dog dog : level.getDogs())
                    if (!dog.isAlive())
                        deadMonsters++;
                
                for (NaziSergeant naziSargent : level.getNaziSergeants())
                    if (!naziSargent.isAlive())
                        deadMonsters++;
                
                for (Zombie zombie : level.getZombies())
                    if (!zombie.isAlive())
                        deadMonsters++;
                
                for (Captain captain : level.getCaptains())
                    if (!captain.isAlive())
                        deadMonsters++;

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

            levelNum += offset;
            
            if(levelNum > 9)
            	currentEpisode = EPISODE_2;
            else if(levelNum > 18)
            	currentEpisode = EPISODE_3;
            else
            	currentEpisode = EPISODE_1;
            
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
            
            if(CLEAR_LIGHTS)
            	getRenderingEngine().clearLights();
            
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

            // Applied last, after the carryover block above, so a loaded
            // save's stats/position/weapon are authoritative over whatever
            // the normal level-transition carryover just computed.
            if(pendingSave != null) {
            	applySave(pendingSave);
            	pendingSave = null;
            }

        } catch (RuntimeException ex) {
        	try {
				Thread.sleep(1);
				menu = new CreditsMenu();
			} catch (InterruptedException e) {
				Log.error("Interrupted while switching to credits menu: " + e.getMessage());
				Thread.currentThread().interrupt();
			}
        	//ex.printStackTrace();
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