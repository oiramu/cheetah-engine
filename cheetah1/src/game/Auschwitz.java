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

import static engine.core.CoreEngine.*;

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

    /**
     * The constructor method of the compiling game.
     */
    public void init() {
        for (int i = 0; i < 13; i++)
        	m_playlist.add(AudioUtil.loadMidi("THEME" + i));

        m_track = m_startingLevel - 1;
        m_levelNum = m_startingLevel - 1;
        m_isRunning = true;
        loadLevel(1);
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
    public void update(RenderingEngine engine) {
        if (m_isRunning) {
            m_level.update(engine);
        }
    }

    /**
     * Renders everything every on screen.
     */
	public void render(RenderingEngine engine) {
        if (m_isRunning) {
        	engine.render(m_level);
        }
    }
	
	/**
     * Reloads the last level played.
     */
	public static void reloadLevel() {loadLevel(m_levelNum-m_levelNum);}

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

            if (m_level != null) {
                totalMonsters = m_level.getNaziSoldiers().size() + m_level.getSsSoldiers().size()
                		+ m_level.getDogs().size() + + m_level.getNaziSergeants().size();
                
                totalSecrets = m_level.getSecretWalls().size();
                
                for (SecretWall secret : m_level.getSecretWalls()) {
                    if (secret.opens()) {
                    	secrets++;
                    }
                }

                for (NaziSoldier monster : m_level.getNaziSoldiers()) {
                    if (!monster.isAlive()) {
                        deadMonsters++;
                    }
                }
                
                for (SsSoldier ssSoldier : m_level.getSsSoldiers()) {
                    if (!ssSoldier.isAlive()) {
                        deadMonsters++;
                    }
                }
                
                for (Dog dog : m_level.getDogs()) {
                    if (!dog.isAlive()) {
                        deadMonsters++;
                    }
                }
                
                for (NaziSergeant naziSargent : m_level.getNaziSergeants()) {
                    if (!naziSargent.isAlive()) {
                        deadMonsters++;
                    }
                }

                displayMonsters = true;
                
                armoriTemp = m_level.getPlayer().getArmori();
                bulletTemp = m_level.getPlayer().getBullets();
                shellTemp = m_level.getPlayer().getShells();
                shotgunTemp = m_level.getPlayer().getShotgun();
                machinegunTemp = m_level.getPlayer().getMachinegun();
                superShotgunTemp = m_level.getPlayer().getSuperShotgun();
                chaingunTemp = m_level.getPlayer().getChaingun();
                weaponStateTemp = m_level.getPlayer().getWeaponState();
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
            
            switch("1texDetail") {
            	case "0texDetail":
            		m_material = new Material(new Texture("mapTexture" + m_currentEpisode));
            		break;
            	case "1texDetail":
            		m_material = new Material(new Texture("mapTexture" + m_currentEpisode), 1, 8, 
            				new Texture("mapTextureNormal" + m_currentEpisode), 
            				new Texture("mapTextureBump" + m_currentEpisode), 0.0004f, -1.0f);
            		break;
            }
            
            m_level = new Level(new Bitmap("level" + m_levelNum).flipX(), m_material, m_renderingEngine);
            
            switch("1light") {
	        	case "0light":
	        		m_renderingEngine.clearLights();
	        		break;
	        	case "1light":
	        		break;
            }

            if(m_levelNum % 2 == 2) sector = "B."; else sector = "A.";   
            
            if(m_level.getPlayer().getArmori() == 0) {
            	if(armoriTemp == 0)
            		m_level.getPlayer().addArmori(0);
            	m_level.getPlayer().addArmori(armoriTemp);
            }
            if(m_level.getPlayer().getBullets() == 0) {
            	if(bulletTemp == 0)
            		m_level.getPlayer().addBullets(20);
            	m_level.getPlayer().addBullets(bulletTemp);
            }
            if(m_level.getPlayer().getShells() == 0) {
            	if(shellTemp == 0)
            		m_level.getPlayer().addShells(20);
            	m_level.getPlayer().addShells(shellTemp);
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

            while (m_track >= m_playlist.size()) {
                m_track -= m_playlist.size();
            }

            System.out.println("=============================");
            System.out.println("Level " + m_levelNum + " floor " + m_levelNum + sector);
            System.out.println("=============================");

            if (displayMonsters) {
            	//engine.clearLights();	//For slower machines
            	System.out.println("Killed " + deadMonsters + "/" + totalMonsters + " baddies: " +
            	((float) deadMonsters / (float) totalMonsters) * 100f + "%");        	
            	System.out.println("Secrets " + secrets + "/" + totalSecrets + " secrets: " +
                    	((float) secrets / (float) totalSecrets) * 100f + "%");
            	
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
            System.out.println("GAME OVER!");
            System.exit(0);
            AudioUtil.stopMidi();
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