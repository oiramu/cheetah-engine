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

import static engine.core.CoreEngine.*;
import static engine.core.Constants.*;

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
	
	private static HashMap<String,TextureFont> text = new HashMap<String,TextureFont>();
	private static ArrayList<Sequence> m_playlist = new ArrayList<Sequence>();
	
    private static final int EPISODE_1 = 1;
    private static final int EPISODE_2 = 2;
    private static final int EPISODE_3 = 2;

    public static Level 			m_level;
    public static Material			m_material;
    public static int 				m_levelNum;
    public static int 				m_startingLevel;
    public static int 				m_track;
    public static int				m_currentEpisode;
    private static boolean 			m_isRunning;
    private static boolean 			m_displayStats = false;
    private static int 				m_secrets = 0;
    private static int 				m_deadMonsters = 0;
    private static int 				m_totalSecrets = 0;
    private static int 				m_totalMonsters = 0;
    private static double			m_stateTime = 0;

    /**
     * The constructor method of the compiling game.
     */
	public void init() {
    	text.put("Level",new TextureFont("", new Vector2f(-1.25f,1.2f), new Vector2f(0.75f,0.75f)));
        text.put("Enemies",new TextureFont("", new Vector2f(-1.25f,1.1f), new Vector2f(0.75f,0.75f)));
        text.put("Secrets",new TextureFont("", new Vector2f(-1.25f,1.0f), new Vector2f(0.75f,0.75f)));
        for (int i = 0; i < 13; i++) m_playlist.add(AudioUtil.loadMidi("THEME" + i));

        m_track = m_startingLevel - 1;
        m_levelNum = m_startingLevel - 1;
        m_isRunning = true;
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
    	if(time<m_stateTime+5.0f) {
	    	text.get("Level").setText("Level:" + m_levelNum + ";Episode" + m_currentEpisode);
	    	text.get("Level").render(renderingEngine);
	    	if(m_displayStats) {
		    	text.get("Enemies").setText("Killed:" + m_deadMonsters + "/" + m_totalMonsters + " Nazis:" +
		            	(int)(((float) m_deadMonsters / (float) m_totalMonsters) * 100f )+ "%");
		    	text.get("Enemies").render(renderingEngine);
		    	text.get("Secrets").setText("Secrets:" + m_secrets + "/" + m_totalSecrets + " Secrets:" +
		    			(int)(((float) m_secrets / (float) m_totalSecrets) * 100f) + "%");
		    	text.get("Secrets").render(renderingEngine);
	    	}
    	}
    }
    
    /**
     * Sets the starting level depending of what level you want to start;
     * @param level to set.
     */
    public static void setStartingLevel(int level) {m_startingLevel = level;}

    /**
     * Checks all the inputs.
     */
    public void input() {
        m_level.input();
        
        if(Input.getKeyDown(Input.KEY_F4))
        	System.exit(0);
    }

    /**
     * Updates everything renderer every single frame.
     * @param delta of time
     */
    public void update(double delta) {
        if (m_isRunning) {
            m_level.update(delta);
        }
    }

    /**
     * Renders everything every on screen.
     */
	public void render(RenderingEngine engine) {
        if (m_isRunning) {
        	engine.render(m_level);
        	printStats(engine);
        }
    }
	
	/**
     * Reloads the last level played.
     */
	public static void reloadLevel() { loadLevel(m_levelNum-m_levelNum, false); }

    /**
     * Load the level and also charges the next level when the last end.
     * @param offset count of level offset by offset.
     */
	@SuppressWarnings("static-access")
	public static void loadLevel(int offset, boolean displayText) {
        try {
        	m_secrets = 0;
            m_deadMonsters = 0;
            m_totalSecrets = 0;
            m_totalMonsters = 0;
            int healthTemp = 0;
            int bulletTemp = 0;
            int shellTemp = 0;
            int armoriTemp = 0;
            int maxHealthTemp = 0;
            int maxBulletTemp = 0;
            int maxShellTemp = 0;
            int maxArmoriTemp = 0;
            boolean shotgunTemp = false;
            boolean machinegunTemp = false;
            boolean superShotgunTemp = false;
            boolean chaingunTemp = false;
            boolean armorbTemp = false;
            boolean mouseLocktemp = false;
            String weaponStateTemp = "";

            if (m_level != null) {
                m_totalMonsters = m_level.getNaziSoldiers().size() + m_level.getSsSoldiers().size()
                		+ m_level.getDogs().size() + + m_level.getNaziSergeants().size();
                
                m_totalSecrets = m_level.getSecretWalls().size();
                
                for (SecretWall secret : m_level.getSecretWalls()) {
                    if (secret.opens()) {
                    	m_secrets++;
                    }
                }

                for (NaziSoldier monster : m_level.getNaziSoldiers()) {
                    if (!monster.isAlive()) {
                        m_deadMonsters++;
                    }
                }
                
                for (SsSoldier ssSoldier : m_level.getSsSoldiers()) {
                    if (!ssSoldier.isAlive()) {
                        m_deadMonsters++;
                    }
                }
                
                for (Dog dog : m_level.getDogs()) {
                    if (!dog.isAlive()) {
                        m_deadMonsters++;
                    }
                }
                
                for (NaziSergeant naziSargent : m_level.getNaziSergeants()) {
                    if (!naziSargent.isAlive()) {
                        m_deadMonsters++;
                    }
                }

                m_displayStats = true;
                
                healthTemp = m_level.getPlayer().getHealth();
                armoriTemp = m_level.getPlayer().getArmori();
                bulletTemp = m_level.getPlayer().getBullets();
                shellTemp = m_level.getPlayer().getShells();
                maxHealthTemp = m_level.getPlayer().getMaxHealth();
                maxBulletTemp = m_level.getPlayer().getMaxBullets();
                maxShellTemp = m_level.getPlayer().getMaxShells();
                maxArmoriTemp = m_level.getPlayer().getMaxArmori();
                shotgunTemp = m_level.getPlayer().getShotgun();
                machinegunTemp = m_level.getPlayer().getMachinegun();
                superShotgunTemp = m_level.getPlayer().getSuperShotgun();
                chaingunTemp = m_level.getPlayer().getChaingun();
                weaponStateTemp = m_level.getPlayer().getWeaponState();
                armorbTemp = m_level.getPlayer().getArmorb();
                mouseLocktemp = m_level.getPlayer().mouseLocked;
                m_renderingEngine.clearLights();
            }

            if(m_levelNum > 9)
            	m_currentEpisode = EPISODE_2;
            else if(m_levelNum > 19)
            	m_currentEpisode = EPISODE_3;
            else
            	m_currentEpisode = EPISODE_1;

            m_levelNum += offset;
            
            switch(GAME_GRAPHICS) {
            	case "Low":
            		m_material = new Material(new Texture("mapTexture" + m_currentEpisode));
            		break;
            	case "High":
            		m_material = new Material(new Texture("mapTexture" + m_currentEpisode), 1, 8, 
            				new Texture("mapTextureNormal" + m_currentEpisode), 
            				new Texture("mapTextureBump" + m_currentEpisode), 0.0004f, -0.75f);
            		break;
            }
            
            m_level = new Level(new Bitmap("level" + m_levelNum).flipX(), m_material);
            
            switch(CLEAR_LIGHTS) {
    			case "True":
    				m_renderingEngine.clearLights();
    				break;
    			case "False":
    				break;
            }
            
            if(m_level.getPlayer().getArmori() == 0)
            	m_level.getPlayer().setArmori(armoriTemp);
            if(m_level.getPlayer().getHealth() == 0) {
            	if(healthTemp == 0)
            		m_level.getPlayer().setHealth(100);
            	m_level.getPlayer().setHealth(healthTemp);
            }
            if(m_level.getPlayer().getBullets() == 0) {
            	if(bulletTemp == 0)
            		m_level.getPlayer().setBullets(20);
            	m_level.getPlayer().setBullets(bulletTemp);
            }
            if(m_level.getPlayer().getShells() == 0) {
            	if(shellTemp == 0)
            		m_level.getPlayer().setShells(20);
            	m_level.getPlayer().setShells(shellTemp);
            }
            if(m_level.getPlayer().getMaxArmori() == 0) {
            	if(maxArmoriTemp == 0)
            		m_level.getPlayer().setMaxArmori(100);
            	m_level.getPlayer().setMaxArmori(maxArmoriTemp);
            }
            if(m_level.getPlayer().getMaxHealth() == 0) {
            	if(maxHealthTemp == 0)
            		m_level.getPlayer().setMaxHealth(100);
            	m_level.getPlayer().setMaxHealth(maxHealthTemp);
            }
            if(m_level.getPlayer().getMaxBullets() == 0) {
            	if(maxBulletTemp == 0)
            		m_level.getPlayer().setMaxBullets(100);
            	m_level.getPlayer().setMaxBullets(maxBulletTemp);
            }
            if(m_level.getPlayer().getMaxShells() == 0) {
            	if(maxShellTemp == 0)
            		m_level.getPlayer().setMaxShells(50);
            	m_level.getPlayer().setMaxShells(maxShellTemp);
            }
            m_level.getPlayer().setShotgun(shotgunTemp);
            m_level.getPlayer().setMachinegun(machinegunTemp);
            m_level.getPlayer().setSuperShotgun(superShotgunTemp);
            m_level.getPlayer().setChaingun(chaingunTemp);
            m_level.getPlayer().setArmorb(armorbTemp);
            m_level.getPlayer().setWeaponState(weaponStateTemp);
            m_level.getPlayer().mouseLocked = mouseLocktemp;
            
            m_track += offset;

            AudioUtil.playMidi(m_playlist.get(m_track));

            while (m_track >= m_playlist.size()) m_track -= m_playlist.size();
            if(displayText) m_stateTime = Time.getTime();

            if (m_displayStats) {
            	if(m_level.getPlayer().getWeaponState() == m_level.getPlayer().HAND){
            		m_level.getPlayer().gotHand();
            	}else if(m_level.getPlayer().getWeaponState() == m_level.getPlayer().PISTOL) {
            		m_level.getPlayer().gotPistol();
            	}else if(m_level.getPlayer().getWeaponState() == m_level.getPlayer().MACHINEGUN && machinegunTemp == true) {
            		m_level.getPlayer().gotMachinegun();
            	}else if(m_level.getPlayer().getWeaponState() == m_level.getPlayer().SHOTGUN && shotgunTemp == true) {
            		m_level.getPlayer().gotShotgun();
            	}else if(m_level.getPlayer().getWeaponState() == m_level.getPlayer().SUPER_SHOTGUN && superShotgunTemp == true) {
            		m_level.getPlayer().gotSShotgun();
            	}else if(m_level.getPlayer().getWeaponState() == m_level.getPlayer().CHAINGUN && chaingunTemp == true) {
            		m_level.getPlayer().gotChaingun();
            	}
            }
            
        } catch (RuntimeException ex) {
        	ex.printStackTrace();
            m_isRunning = false;
            Debug.printErrorMessage(ex.getMessage(), "GAME OVER!");
            System.out.println("GAME OVER!");
            AudioUtil.stopMidi();
            System.exit(0);
        }

    }

    /**
     * Gets the actual level.
     * @return Level.
     */
    public static Level getLevel() {return m_level;}

    /**
     * Allow to stop the game rendering without stop all the program.
     * @param value True or false.
     */
    public static void setIsRunning(boolean value) {m_isRunning = value;}

}