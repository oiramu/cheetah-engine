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

import javax.sound.sampled.Clip;

import org.lwjgl.opengl.Display;

import engine.audio.AudioUtil;
import engine.components.Attenuation;
import engine.components.Camera;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.components.SpotLight;
import engine.core.Debug;
import engine.core.Input;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.TextureFont;
import engine.rendering.Vertex;
import engine.rendering.Window;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.6
 * @since 2017
 */
public class Player extends GameComponent {

    private static final float GUN_SIZE = 0.1f; 
    private static final float GUN_OFFSET = -0.077f;
    private static final float GUN_OFFSET_X = 0f;
    private static final float GUN_TRANSFORM_MUL = 0.101f;
    private static final float MOUSE_SENSITIVITY = 0.25f;
    private static final float MAX_LOOK_ANGLE = 45;//45
    private static final float MIN_LOOK_ANGLE = -5;//-45
    private static final float PLAYER_WIDTH = 0.2f;
    private static final float GRAVITY = 9.8f;
    
    private static final float PLAYER_TO_GROUND = 0.4375f;
    
    private static float moveSpeed;
    private static float gunFireAnimationTime;
    
    public float damageMin;
    public float damageRange;
    public float attenuation;
    
    public static final String HAND = "hand";
    public static final String PISTOL = "pistol";
    public static final String SHOTGUN = "shotgun";
    public static final String MACHINEGUN = "machinegun";
    public static final String SUPER_SHOTGUN = "superShotgun";
    public static final String CHAINGUN = "chaingun";
    
    private static final String PLAYER_RES_LOC = "player/";
    private static final String WEAPONS_RES_LOC = "weapons/";
    private static final String FLASHLIGHT_RES_LOC = "flashLight/";
    private static final String HAND_RES_LOC = WEAPONS_RES_LOC + HAND + "/";
    private static final String PISTOL_RES_LOC = WEAPONS_RES_LOC + PISTOL + "/";
    private static final String SHOTGUN_RES_LOC = WEAPONS_RES_LOC + SHOTGUN +"/";
    private static final String MACHINEGUN_RES_LOC = WEAPONS_RES_LOC + MACHINEGUN + "/";
    private static final String SUPER_SHOTGUN_RES_LOC = WEAPONS_RES_LOC + SUPER_SHOTGUN + "/";
    private static final String CHAINGUN_RES_LOC = WEAPONS_RES_LOC + CHAINGUN + "/";
    private static final String PISGB0 = "PISGB0";
    private static final String PISFA0 = "PISFA0";
    private static final String PISFC0 = "PISFC0";
    private static final String PISFD0 = "PISFD0";
    private static final String PISFE0 = "PISFE0";
    //private static final String MIGGD0 = "MIGGD0";
    //private static final String MIGFA0 = "MIGFA0";
    //private static final String MIGFB0 = "MIGFB0";
    private static final String EMPTY = "EMPTY";
    private static final String GUNSOUND = "GUN";
    private static final String CLIPSOUND = "CLIPIN";
    private static final String RELOADSOUND = "RELOAD";
    
    private String weaponState;
    
    public HashMap<String, TextureFont> playerText;
    
    private static ArrayList<Texture> gunsMaterial;
    private static ArrayList<Texture> gunsAnimationMaterial1;
    private static ArrayList<Texture> gunsAnimationMaterial2;
    private static ArrayList<Texture> gunsAnimationMaterial3;
    private static ArrayList<Texture> gunsAnimationMaterial4;
    
    private static ArrayList<Clip> gunsNoiseSounds;
    private static ArrayList<Clip> gunsReloadSounds;
    private static ArrayList<Clip> gunsClippingSounds;
    private static ArrayList<Clip> gunsEmptyNoiseSounds;
    private static ArrayList<Clip> playerMovement;
    private static ArrayList<Clip> playerNoises;
    private static ArrayList<Clip> flashLightNoises;

    private static final Vector2f centerPosition = new Vector2f(Display.getWidth()/2, Display.getHeight()/2);
    private static final Vector3f zeroVector = new Vector3f(0, 0, 0);
    
    private static float toTerrain = 0;
    
    private static Clip gunNoise;
    private static Clip gunEmptyNoise;
    private static Clip gunReload;
    private static Clip gunClipp;

    private Mesh gunMesh;
    private Material gunMaterial;
    private Material gunAnimationMaterial1;
    private Material gunAnimationMaterial2;
    private Material gunAnimationMaterial3;
    private Material gunAnimationMaterial4;
    private Transform gunTransform;
    private MeshRenderer gunRenderer;
    private RenderingEngine m_renderingEngine;

    private Camera playerCamera;
    private Random rand;
    private Vector3f movementVector;
    private Vector3f gunLightColor;
    
    private SpotLight fireLight;
    private SpotLight sLight;
    
    public double notificationTime;
    
    private double gunFireTime;
    private float width;
    private float upAmt = 0;
    private int health;
    private int armori;
    private int bullets;
    private int shells;
    private int maxHealth;
    private int maxArmori;
    private int maxBullets;
    private int maxShells;
    private boolean armorb;
    private boolean shotgun;
    private boolean machinegun;
    private boolean sShotgun;
    private boolean chaingun;
    private boolean isInAir = false;
    
    public static boolean mouseLocked;
    public static boolean isOn;
    
    public boolean fires;
    
    public boolean isAlive;
    public boolean isShooting;
    public boolean isReloading;
    public boolean isMelee;
    public boolean isBulletBased;
    public boolean isShellBased;
    public boolean isAutomatic;
    public boolean isDoubleShooter;
    
    /**
     * Constructor of the main player.
     * @param position the position in the 3D space.
     * @param renderingEngine of the player.
     */
    public Player(Vector3f position, RenderingEngine renderingEngine) {
    	
    	if(playerCamera == null) {
    		playerCamera = new Camera((float) Math.toRadians(70.0f), (float)Window.getWidth()/(float)Window.getHeight(), 0.01f, 1000f);
    		playerCamera.setPos(position);
    	}
    	
    	if(gunsMaterial == null) {
    		gunsMaterial = new ArrayList<Texture>();
    		
    		gunsMaterial.add(new Texture(HAND_RES_LOC + PISGB0));
    		gunsMaterial.add(new Texture(PISTOL_RES_LOC + PISGB0));
    		gunsMaterial.add(new Texture(SHOTGUN_RES_LOC + PISGB0));
    		gunsMaterial.add(new Texture(MACHINEGUN_RES_LOC + PISGB0));
    		gunsMaterial.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISGB0));
    		gunsMaterial.add(new Texture(CHAINGUN_RES_LOC + PISGB0));
    	}
    	
    	if(gunsAnimationMaterial1 == null) {
    		gunsAnimationMaterial1 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial1.add(new Texture(HAND_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(PISTOL_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(SHOTGUN_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(MACHINEGUN_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(CHAINGUN_RES_LOC + PISFA0));
    	}
    	
    	if(gunsAnimationMaterial2 == null) {
    		gunsAnimationMaterial2 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial2.add(new Texture(HAND_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(PISTOL_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(SHOTGUN_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(MACHINEGUN_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(CHAINGUN_RES_LOC + PISFC0));
    	}
    	
    	if(gunsAnimationMaterial3 == null) {
    		gunsAnimationMaterial3 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial3.add(null);
    		gunsAnimationMaterial3.add(null);
    		gunsAnimationMaterial3.add(new Texture(SHOTGUN_RES_LOC + PISFD0));
    		gunsAnimationMaterial3.add(null);
    		gunsAnimationMaterial3.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISFD0));
    		gunsAnimationMaterial3.add(null);
    	}
    	
    	if(gunsAnimationMaterial4 == null) {
    		gunsAnimationMaterial4 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(new Texture(SHOTGUN_RES_LOC + PISFE0));
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISFE0));
    		gunsAnimationMaterial4.add(null);
    	}
    	
    	if(gunsNoiseSounds == null) {
    		gunsNoiseSounds = new ArrayList<Clip>();
    		
    		gunsNoiseSounds.add(AudioUtil.loadAudio(HAND_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(PISTOL_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(SHOTGUN_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(MACHINEGUN_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(SUPER_SHOTGUN_RES_LOC + GUNSOUND));
    	}
    	
    	if(gunsReloadSounds == null) {
    		gunsReloadSounds = new ArrayList<Clip>();
    		
    		gunsReloadSounds.add(null);
    		gunsReloadSounds.add(null);
    		gunsReloadSounds.add(AudioUtil.loadAudio(SHOTGUN_RES_LOC + RELOADSOUND));
    		gunsReloadSounds.add(null);
    		gunsReloadSounds.add(AudioUtil.loadAudio(SUPER_SHOTGUN_RES_LOC + RELOADSOUND));
    	}
    	
    	if(gunsClippingSounds == null) {
    		gunsClippingSounds = new ArrayList<Clip>();
    		
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(AudioUtil.loadAudio(SHOTGUN_RES_LOC + CLIPSOUND));
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(AudioUtil.loadAudio(SUPER_SHOTGUN_RES_LOC + CLIPSOUND));
    	}
    	
    	if(gunsEmptyNoiseSounds == null) {
    		gunsEmptyNoiseSounds = new ArrayList<Clip>();
    		
    		gunsEmptyNoiseSounds.add(null);
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(PISTOL_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(SHOTGUN_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(MACHINEGUN_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(SUPER_SHOTGUN_RES_LOC + EMPTY));
    	}
    	
    	if(playerNoises == null) {
    		playerNoises = new ArrayList<Clip>();
    		
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "MOVE"));
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "OOF"));
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "PLPAIN"));
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "PLDETH"));
    	}
    	
    	if(playerMovement == null) {
    		playerMovement = new ArrayList<Clip>();
    		
    		for (int i = 1; i < 7; i++)
    			playerMovement.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "walking/FSHARD" + i));
    	}
    	
    	if(flashLightNoises == null) {
    		flashLightNoises = new ArrayList<Clip>();
    		
    		flashLightNoises.add(AudioUtil.loadAudio(FLASHLIGHT_RES_LOC + "FLASHON"));
    		flashLightNoises.add(AudioUtil.loadAudio(FLASHLIGHT_RES_LOC + "FLASHOFF"));
    	}
    	
    	if(playerText == null) {
    		playerText = new HashMap<String, TextureFont>();
    		
    		playerText.put("Life", new TextureFont("", new Vector2f(-0.9f,-0.8f), new Vector2f(1f,1f)));
    		playerText.put("Armor", new TextureFont("", new Vector2f(-0.9f,-0.7f), new Vector2f(1f,1f)));
    		playerText.put("Ammo", new TextureFont("", new Vector2f(-0.9f,-0.9f), new Vector2f(1f,1f)));
    		playerText.put("Notification", new TextureFont("", new Vector2f(-1.25f,1.2f), new Vector2f(0.75f,0.75f)));
    		playerText.put("CrossHair", new TextureFont("+", new Vector2f(0,0), new Vector2f(1f,1f)));
    	}
    		
    	if(weaponState == null) { gotPistol(); }
    	
        if (gunMesh == null) {
            float sizeY = GUN_SIZE;
            float sizeX = (float) ((double) sizeY / (0.5f * 2.0));

            float offsetX = 0.00f;
            float offsetY = 0.00f;

            float texMinX = -offsetX;
            float texMaxX = -1 - offsetX;
            float texMinY = -offsetY;
            float texMaxY = 1 - offsetY;

            Vertex[] verts = new Vertex[]{new Vertex(new Vector3f(-sizeX, 0, 0), new Vector2f(texMaxX, texMaxY)),
            		new Vertex(new Vector3f(-sizeX, sizeY, 0), new Vector2f(texMaxX, texMinY)),
            		new Vertex(new Vector3f(sizeX, sizeY, 0), new Vector2f(texMinX, texMinY)),
            		new Vertex(new Vector3f(sizeX, 0, 0), new Vector2f(texMinX, texMaxY))};

            int[] indices = new int[]{0, 1, 2,
            						  0, 2, 3};

            gunMesh = new Mesh(verts, indices, true);
        }

        health = 0;
        armorb = false;
        armori = 0;
        bullets = 0;
        shells = 0;
        maxHealth = 0;
        maxArmori = 0;
        maxBullets = 0;
        maxShells = 0;

        if(gunTransform == null) gunTransform = new Transform(playerCamera.getPos());
        if(gunRenderer == null) gunRenderer = new MeshRenderer(gunMesh, gunTransform, gunMaterial);  
        
        if(sLight == null && fireLight == null) {
        	sLight = new SpotLight(new Vector3f(0.3f,0.3f,0.175f), 0.8f, 
        	    	new Attenuation(0.1f,0.1f,0.1f), new Vector3f(-2,0,5f), new Vector3f(1,1,1), 0.7f);
    		fireLight = new SpotLight(gunLightColor, 1.6f, 
            		new Attenuation(attenuation,0,attenuation), getCamera().getPos(), new Vector3f(1,1,1), 0.7f);
    	}
        if(m_renderingEngine == null) this.m_renderingEngine = renderingEngine;
        gunFireTime = 0;
        notificationTime = 0;
        mouseLocked = false;
        isOn = false;
        isAlive = true;
        Input.setMousePosition(centerPosition);
        Input.setCursor(false);
        movementVector = zeroVector;
        width = PLAYER_WIDTH;
        rand = new Random();
        Debug.init();
    }

    private float upAngle = 0;
    
    /**
     * The settings that the player sets if he chooses the fist.
     */
    public void gotHand() {
    	gunMaterial = new Material(gunsMaterial.get(0));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(0));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(0));
        gunNoise = gunsNoiseSounds.get(0);
        gunLightColor = null;
        gunEmptyNoise = null;
        damageMin = 15f;
        damageRange = 0.1f;
        gunFireAnimationTime = 0.1f;
        moveSpeed = 6f;
        attenuation = 0;
        isMelee = true;    
        isBulletBased = false;
        isShellBased = false;
        weaponState = HAND;
        isAutomatic = false;
        isDoubleShooter = false;
    }
    
    /**
     * The settings that the player sets if he chooses the pistol of 
     * he's bag.
     */
    public void gotPistol() {
    	gunMaterial = new Material(gunsMaterial.get(1));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(1));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(1));
    	gunLightColor = new Vector3f(0.5f,0.3f,0.1f);
        gunNoise = gunsNoiseSounds.get(1);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(1);
        gunFireAnimationTime = 0.1f;
        damageMin = 25f;
        damageRange = 30f;
        moveSpeed = 5f;
        attenuation = 0.25f;
        isMelee = false;
        isBulletBased = true;
        isShellBased = false;
        weaponState = PISTOL;
        isAutomatic = false;
        isDoubleShooter = false;
    }
    
    /**
     * The settings that the player sets if he chooses the shotgun
     * of he's bag, only if he have it on it.
     */
    public void gotShotgun() {
    	gunMaterial = new Material(gunsMaterial.get(2));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(2));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(2));
    	gunAnimationMaterial3 = new Material(gunsAnimationMaterial3.get(2));
    	gunAnimationMaterial4 = new Material(gunsAnimationMaterial4.get(2));
    	gunLightColor = new Vector3f(0.45f,0.35f,0.1f);
        gunNoise = gunsNoiseSounds.get(2);
        gunReload = gunsReloadSounds.get(2);
        gunClipp = gunsClippingSounds.get(2);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(2);
        gunFireAnimationTime = 0.175f;   
        damageMin = 60f;
        damageRange = 60f;
        moveSpeed = 4f;
        attenuation = 0.1f;
        isMelee = false;
        isBulletBased = false;
        isShellBased = true;
        weaponState = SHOTGUN;
        isAutomatic = false;
        isDoubleShooter = false;
    }
    
    /**
     * The settings that the player sets if he chooses the machine-gun
     * of he's bag, only if he have it on it.
     */
    public void gotMachinegun() {
    	gunMaterial = new Material(gunsMaterial.get(3));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(3));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(3));
    	gunLightColor = new Vector3f(0.5f,0.3f,0.1f);
        gunNoise = gunsNoiseSounds.get(3);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(3);
        gunFireAnimationTime = 0.05f;   
        damageMin = 25f;
        damageRange = 60f;
        moveSpeed = 4.5f;
        attenuation = 0.175f;
        isMelee = false;
        isBulletBased = true;
        isShellBased = false;
        weaponState = MACHINEGUN;
        isAutomatic = true;
        isDoubleShooter = false;
    }
    
    /**
     * The settings that the player sets if he chooses the super
     * shotgun of he's bag, only if he have it on it.
     */
    public void gotSShotgun() {
    	gunMaterial = new Material(gunsMaterial.get(4));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(4));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(4));
    	gunAnimationMaterial3 = new Material(gunsAnimationMaterial3.get(4));
    	gunAnimationMaterial4 = new Material(gunsAnimationMaterial4.get(4));
    	gunLightColor = new Vector3f(0.45f,0.35f,0.1f);
        gunNoise = gunsNoiseSounds.get(4);
        gunReload = gunsReloadSounds.get(4);
        gunClipp = gunsClippingSounds.get(4);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(4);
        gunFireAnimationTime = 0.2f;   
        damageMin = 120f;
        damageRange = 60f;
        moveSpeed = 4f;
        attenuation = 0.05f;
        isMelee = false;
        isBulletBased = false;
        isShellBased = true;
        weaponState = SUPER_SHOTGUN;
        isAutomatic = false;
        isDoubleShooter = true;
    }
    
    /**
     * The settings that the player sets if he chooses the chain-gun
     * of he's bag, only if he have it on it.
     */
    public void gotChaingun() {
    	gunMaterial = new Material(gunsMaterial.get(5));
    	gunAnimationMaterial1 = new Material(gunsAnimationMaterial1.get(5));
    	gunAnimationMaterial2 = new Material(gunsAnimationMaterial2.get(5));
    	gunLightColor = new Vector3f(0.45f,0.35f,0.1f);
        gunNoise = gunsNoiseSounds.get(3);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(3);
        gunFireAnimationTime = 0.05f;   
        damageMin = 25f;
        damageRange = 120f;
        moveSpeed = 4.5f;
        attenuation = 0.025f;
        isMelee = false;
        isBulletBased = true;
        isShellBased = false;
        weaponState = CHAINGUN;
        isAutomatic = true;
        isDoubleShooter = false;
    }

    /**
     * The player's input system.
     */
    public void input() {
    	Vector2f deltaPos = Input.getMousePosition().sub(centerPosition);
    	
        boolean rotY = deltaPos.getX() != 0;
        boolean rotX = deltaPos.getY() != 0;
        
        if(isAutomatic)
        	fires = Input.getMouse(0);
        else
        	fires = Input.getMouseDown(0);
        
    	if(isAlive) {
	        if (Input.getKeyDown(Input.KEY_E)) {
	            Auschwitz.getLevel().openDoors(playerCamera.getPos(), true);
	        }
	        
	        if (Input.getKeyDown(Input.KEY_1)) {
	        	if(weaponState == HAND && isReloading) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		gotHand();
	        		AudioUtil.playAudio(playerNoises.get(0), 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_2)) {
	        	if(weaponState == PISTOL && isReloading) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		gotPistol();
	        		AudioUtil.playAudio(playerNoises.get(0), 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_3)) {
	        	if(shotgun == false || weaponState == SHOTGUN && isReloading) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		gotShotgun();
	        		AudioUtil.playAudio(playerNoises.get(0), 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_4)) {
	        	if(machinegun == false || weaponState == MACHINEGUN && isReloading) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		gotMachinegun();
	        		AudioUtil.playAudio(playerNoises.get(0), 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_5)) {
	        	if(sShotgun == false || weaponState == SUPER_SHOTGUN && isReloading) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		gotSShotgun();
	        		AudioUtil.playAudio(playerNoises.get(0), 0);
	        	}
	        } else if (Input.getKeyDown(Input.KEY_6)) {
	        	if(chaingun == false || weaponState == CHAINGUN && isReloading) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		gotChaingun();
	        		AudioUtil.playAudio(playerNoises.get(0), 0);
	        	}
	        }
	        
	        if (Input.getKey(Input.KEY_ESCAPE)) {
	            Input.setCursor(true);
	            mouseLocked = false;
	        }
	        
	        if (fires && !isReloading) {
	            if (!mouseLocked) {
	                Input.setMousePosition(centerPosition);
	                Input.setCursor(false);
	                mouseLocked = true;
	            } else {
	            	Vector2f shootDirection = playerCamera.getForward().getXZ().normalized();
	        		
		            Vector2f lineStart = playerCamera.getPos().getXZ();
		            Vector2f lineEnd = lineStart.add(shootDirection.mul(1000.0f));
		
		            Auschwitz.getLevel().checkIntersections(lineStart, lineEnd, true);
		            
		            gunFireTime = Time.getTime();
	            	
		            if(bullets != 0 && isBulletBased) {
		            	addBullets(-1);
		            	AudioUtil.playAudio(gunNoise, 0);
		            }
		            if(shells != 0 && isShellBased) {
		            	AudioUtil.playAudio(gunNoise, 0);
		            	if(isDoubleShooter)
		            		addShells(-2);
		            	else
		            		addShells(-1);
		            }
	            }
	        }

	        if(isOn) {
				if (Input.getKeyDown(Input.KEY_F)) {
					m_renderingEngine.removeLight(sLight);
					AudioUtil.playAudio(flashLightNoises.get(1), 0);
					isOn = false;
				}
            } else {
            	if (Input.getKeyDown(Input.KEY_F)) {
            		AudioUtil.playAudio(flashLightNoises.get(0), 0);
            		m_renderingEngine.addLight(sLight);
	            	isOn = true;
            	}
            }
	        
	        if(Debug.m_state) {
				if (Input.getKeyDown(Input.KEY_F3))
					Debug.m_state = false;
            } else {
            	if (Input.getKeyDown(Input.KEY_F3))
            		Debug.m_state = true;
            }
	
	        movementVector = zeroVector;
	        if(playerCamera.getPos().getY() == toTerrain) {
		        if(Input.getKeyDown(Input.KEY_W) || Input.getKeyDown(Input.KEY_UP) ||
		        		Input.getKeyDown(Input.KEY_S) || Input.getKeyDown(Input.KEY_DOWN)
		        		|| Input.getKeyDown(Input.KEY_A) || Input.getKeyDown(Input.KEY_D)) {
		        	AudioUtil.playAudio(playerMovement.get(new Random().nextInt(playerMovement.size())), 0);
		        }
	        }
	        		
	        if (Input.getKey(Input.KEY_W)) {
	            movementVector = movementVector.add(playerCamera.getForward());
	        }
	        if (Input.getKey(Input.KEY_S)) {
	            movementVector = movementVector.sub(playerCamera.getForward());
	        }
	        if (Input.getKey(Input.KEY_A)) {
	            movementVector = movementVector.add(playerCamera.getLeft());
	        }
	        if (Input.getKey(Input.KEY_D)) {
	            movementVector = movementVector.add(playerCamera.getRight());
	        }
	        /**
	        if(Input.getKey(Input.KEY_LEFT)) {
            	playerCamera.rotateY(deltaPos.getX() - MOUSE_SENSITIVITY * 3);
	        }
            if(Input.getKey(Input.KEY_RIGHT)) {
            	playerCamera.rotateY(deltaPos.getX() + MOUSE_SENSITIVITY * 3);
            }*/
            if (Input.getKey(Input.KEY_SPACE)) {
            	if(!isInAir) {
            		upAmt = 0.8f;
            		isInAir = true;
            	}
	        }
	
	        if (mouseLocked) {
	            if (rotY) {
	                playerCamera.rotateY(deltaPos.getX() * MOUSE_SENSITIVITY);
	            }
	            
	            //Looking up and down
	             if(rotX) { float amt = -deltaPos.getY() * MOUSE_SENSITIVITY;
	             if(amt + upAngle > -MIN_LOOK_ANGLE) {
	             playerCamera.rotateX(-MIN_LOOK_ANGLE - upAngle); upAngle =
	             -MIN_LOOK_ANGLE; } else if(amt + upAngle < -MAX_LOOK_ANGLE) {
	             playerCamera.rotateX(-MAX_LOOK_ANGLE - upAngle); upAngle =
	             -MAX_LOOK_ANGLE; } else { playerCamera.rotateX(amt); upAngle +=
	             amt; } }
	            
	            if (rotY || rotX) {
	                Input.setMousePosition(new Vector2f(Display.getWidth() / 2, Display.getHeight() / 2));
	            }
	        }
    	}
    }

    /**
     * Updates the player every single frame.
     * @param delta of time
     */
    public void update(double delta) {
    	float movAmt = 0;
    	double time = Time.getTime();
    	float dy = 0;
        float dx = 0;
    	if(isAlive) { 
    		upAmt += (-GRAVITY * 0.2f) * delta;
    		movAmt = (float) (moveSpeed * delta);
    		toTerrain = PLAYER_TO_GROUND;
    		dy = GUN_OFFSET; 
    		dx = GUN_OFFSET_X;
    	} else {
    		dy = GUN_OFFSET; 
    		dx = GUN_OFFSET_X;
    		upAmt += (-GRAVITY * 0.1f / 4) * delta;
    		toTerrain = 0.15f;
    	}
    	
    	playerCamera.getPos().setY(upAmt);
        if(playerCamera.getPos().getY() < toTerrain) {
        	upAmt = 0;
        	playerCamera.getPos().setY(toTerrain);
        	isInAir = false;
        }  
        
        movementVector.setY(0);

        Vector3f oldPos = playerCamera.getPos();
        Vector3f newPos = oldPos.add(movementVector.normalized().mul(movAmt));

        Vector3f collisionVector = Auschwitz.getLevel().checkCollisions(oldPos, newPos, width, width);

        movementVector = movementVector.normalized().mul(collisionVector);

        if (movementVector.length() > 0) {
        	float bobOscillate = (float) Math.sin(time * moveSpeed * (2 * Math.PI));
        	dy += bobOscillate * movAmt/20;
            dx += (bobOscillate * movAmt/20) * 5;
            playerCamera.move(movementVector, movAmt);
        }

        //Gun movement
        gunTransform.setScale(1, 1, 1);
        gunTransform.setPosition(playerCamera.getPos().add(playerCamera.getForward().normalized().mul(GUN_TRANSFORM_MUL)));
        gunTransform.setPosition(gunTransform.getPosition().add(playerCamera.getLeft().normalized().mul(GUN_OFFSET_X)));
        gunTransform.setRotation(gunTransform.getPosition().add(playerCamera.getLeft().normalized().mul(dx)));
        gunTransform.getPosition().setY(gunTransform.getPosition().getY() + dy);

        Vector3f playerDistance = gunTransform.getPosition().sub(playerCamera.getPos());

        Vector3f orientation = playerDistance.normalized();
        
        setDistance(playerDistance.length());

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() >= 0) {
            angle = 180 + angle;
        }

        gunTransform.setRotation(0, angle + 90, 0);
        
        if(isOn) {
	        sLight.setPosition(getCamera().getPos());
	        sLight.setDirection(getCamera().getForward());
        }
        if(!isShooting && (!isBulletBased || !isShellBased)) {
        	m_renderingEngine.removeLight(fireLight);
        }
        fireLight.setPosition(new Vector3f(getCamera().getPos().getX(), 0, getCamera().getPos().getZ()));
        fireLight.setDirection(getCamera().getForward());
    }

    /**
     * Method that renders the player's mesh.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {
    	int ammo = 0;
    	double time = Time.getTime();
    	double gunTime = gunFireTime + gunFireAnimationTime;
    	double gunTime2 = gunTime + gunFireAnimationTime;
    	double gunTime3 = gunTime2 + gunFireAnimationTime;
    	double gunTime4 = gunTime3 + gunFireAnimationTime;
    	
    	if(isBulletBased) ammo = getBullets();else if(isShellBased) ammo = getShells();else ammo = 0;
    	Debug.printToEngine(renderingEngine);
    	playerText.get("CrossHair").render(renderingEngine);
    	playerText.get("Life").setText("Life:"+getHealth());
    	playerText.get("Life").render(renderingEngine);
    	playerText.get("Ammo").setText("Ammo:"+ammo);
    	playerText.get("Ammo").render(renderingEngine);
        if(armorb) { 
        	playerText.get("Armor").setText("Armor:"+getArmori());
        	playerText.get("Armor").render(renderingEngine);
        }
        if(time < notificationTime + 2.5f) playerText.get("Notification").render(renderingEngine);
        
		if(isMelee) {
	        if ((double) time < gunTime) {
	        	isReloading = true;
	        	gunRenderer.setMaterial(gunAnimationMaterial1);
	        	gunRenderer.render(shader, renderingEngine); 
	        } else if ((double) time < gunTime2) {
	        	gunRenderer.setMaterial(gunAnimationMaterial2);
	        	gunRenderer.render(shader, renderingEngine); 
	        } else {
	        	gunRenderer.setMaterial(gunMaterial);
	            gunRenderer.render(shader, renderingEngine);
	            isReloading = false;
	        }
        }
		if(isBulletBased) {
			if(bullets != 0) {
		        if ((double) time < gunTime) {
		        	isReloading = true;
		        	m_renderingEngine.addLight(fireLight);
		        	gunRenderer.setMaterial(gunAnimationMaterial1);
		        	gunRenderer.render(shader, renderingEngine); 
		        } else if ((double) time < gunTime2) {
		        	gunRenderer.setMaterial(gunAnimationMaterial2);
		        	gunRenderer.render(shader, renderingEngine);
		        	isShooting = false;
		        } else {
		        	gunRenderer.setMaterial(gunMaterial);
	            	gunRenderer.render(shader, renderingEngine);
	            	isReloading = false;
		        }
			} else {
				gunRenderer.setMaterial(gunMaterial);
            	gunRenderer.render(shader, renderingEngine);
            	isReloading = false;
			}
		}
		if(isShellBased) {
			if(shells != 0) {
		        if ((double) time < gunTime) {
		        	isReloading = true;
		        	m_renderingEngine.addLight(fireLight);
		        	gunRenderer.setMaterial(gunAnimationMaterial1);
		        	gunRenderer.render(shader, renderingEngine); 
		        } else if ((double) time < gunTime2) {
		        	gunRenderer.setMaterial(gunAnimationMaterial2);
			        gunRenderer.render(shader, renderingEngine);
			        AudioUtil.playAudio(gunReload, 0);
			        isShooting = false;
		        } else if ((double) time < gunTime3) {
		        	gunRenderer.setMaterial(gunAnimationMaterial3);
			        gunRenderer.render(shader, renderingEngine); 
			        AudioUtil.playAudio(gunClipp, 0);
		        } else if ((double) time < gunTime4) {
		        	gunRenderer.setMaterial(gunAnimationMaterial4);
		        	gunRenderer.render(shader, renderingEngine); 
		        } else {
		        	gunRenderer.setMaterial(gunMaterial);
		            gunRenderer.render(shader, renderingEngine);
		            isReloading = false;
		        }
			} else {
				gunRenderer.setMaterial(gunMaterial);
	            gunRenderer.render(shader, renderingEngine);
	            isReloading = false;
			}
		} 
    }
    
    /**
     * Gets the player's actual health.
     * @return player's health.
     */
    public int getHealth() { return health; }
    
    /**
	 * Sets the amount of health for the player to have.
	 * @param amt amount of health to set
	 */
	public void setHealth(int amt) { health += amt; }

    /**
     * Method that sets an amount of health if player get some, or lose some.
     * @param amt amount.
     */
    public void addHealth(int amt) {
    	int temp = health;
        health += amt;
        if(health>temp) {
	        playerText.get("Notification").setText("You've got " + amt + " of health!");
	    	notificationTime = Time.getTime();
        }
        if (health > getMaxHealth()) {
            health = getMaxHealth();
        }
        if (health <= 0) {
        	playerText.get("Notification").setText("You Died! (press e)");
        	notificationTime = Time.getTime();
        	AudioUtil.playAudio(playerNoises.get(3), 0);
        	health = 0;
            bullets = 0;
            shells = 0;
            armori = 0;
            armorb = false;
            shotgun = false;
            machinegun = false;
            sShotgun = false;
            isAlive = false;
            gotHand();
        } else {
            if (amt < 0) {
                AudioUtil.playAudio(playerNoises.get(2), 0);
            }
        }
    }
    
	/**
	 * Gets the player's actual bullets.
	 * @return player's bullets.
	 */
	public int getBullets() {return bullets;}
	
	/**
	 * Sets the amount of bullets for the player to have.
	 * @param amt amount of bullets to set
	 */
	public void setBullets(int amt) { bullets += amt; }
    
    /**
     * Method that sets an amount of bullets if player get some, or lose some.
     * @param amt amount of bullets to set
     */
    public void addBullets(int amt) {
    	int temp = bullets;
    	bullets += amt;
        if(bullets>temp) {
	        playerText.get("Notification").setText("You've got " + amt + " bullets!");
	    	notificationTime = Time.getTime();
        }
        if (bullets > getMaxBullets()) {
        	bullets = getMaxBullets();
        }    
        if(isBulletBased) {
        	if (bullets <= 0) {
        		bullets = 0;
        		playerText.get("Notification").setText("You Need More Bullets!");
            	notificationTime = Time.getTime();
        		AudioUtil.playAudio(gunEmptyNoise, 1);
        	}
        }
    }
    
    /**
	 * Gets the player's actual shells.
	 * @return player's shells.
	 */
	public int getShells() {return shells;}
	
	/**
	 * Sets the amount of shells for the player to have.
	 * @param amt amount of shells to set
	 */
	public void setShells(int amt) { shells += amt; }

	/**
     * Method that sets an amount of shells if player get some, or lose some.
     * @param amt amount of shells to set.
     */
	public void addShells(int amt) {
		int temp = shells;
		shells += amt;
		if(shells>temp) {
			playerText.get("Notification").setText("You've got " + amt + " shotgun shells!");
			notificationTime = Time.getTime();
		}
        if (shells > getMaxShells()) {
        	shells = getMaxShells();
        }
        if(isShellBased) {
        	if (shells <= 0) {
        		shells = 0;
        		playerText.get("Notification").setText("You Need More Shells!");
            	notificationTime = Time.getTime();
        		AudioUtil.playAudio(gunEmptyNoise, 1);
        	}
        }
	}
    
    /**
     * Method that assigns the shotgun to the player object.
     * @param amt amount.
     */
    public void setShotgun(boolean amt) {
        if(amt == true && shotgun == false && isAlive) {
    		shotgun = amt;
    		gotShotgun();
        }
        if(shotgun == true && isAlive && amt != true) {
        	playerText.get("Notification").setText("You've got a shotgun!");
        	notificationTime = Time.getTime();
    	}
    }
    
    /**
     * Method that returns if the player have or not a shotgun on 
     * he's bag.
     */
    public boolean getShotgun() {return shotgun;}
    
    /**
     * Method that assigns the machine-gun to the player object.
     * @param amt amount.
     */
    public void setMachinegun(boolean amt) {
    	if(amt == true && machinegun == false && isAlive) {
    		machinegun = amt;
    		gotMachinegun();
    	}
    	if(machinegun == true && isAlive && amt != true) {
    		playerText.get("Notification").setText("You've got a MP90 Machinegun!");
        	notificationTime = Time.getTime();
    	}
    }
    
    /**
     * Method that returns if the player have or not a machine-gun 
     * on he's bag.
     */
    public boolean getMachinegun() {return machinegun;}
    
    /**
     * Method that assigns the super shotgun to the player object.
     * @param amt amount.
     */
    public void setSuperShotgun(boolean amt) {
    	if(amt == true && sShotgun == false && isAlive) {
    		sShotgun = amt;
    		gotSShotgun();
    	}
    	if(sShotgun == true && isAlive && amt != true) {
    		playerText.get("Notification").setText("You've got a double barrel shotgun!");
        	notificationTime = Time.getTime();
    	}
    }
    
    /**
     * Method that returns if the player have or not a super shotgun 
     * on he's bag.
     */
    public boolean getSuperShotgun() {return sShotgun;}
    
    /**
     * Method that returns if the player have or not a chain-gun
     * on he's bag.
     */
    public boolean getChaingun() {return chaingun;}
    
    /**
     * Method that assigns the chain-gun to the player object.
     * @param amt amount.
     */
    public void setChaingun(boolean amt) {
    	if(amt == true && chaingun == false && isAlive) {
    		chaingun = amt;
    		gotChaingun();
    	}
    	if(chaingun == true && isAlive) {
    		playerText.get("Notification").setText("You've got a Chaingun!");
        	notificationTime = Time.getTime();
    	}
    } 
    
    /**
     * Method that assigns the armor to the player object.
     * @param amt amount.
     */
    public void setArmorb(boolean amt) {armorb = amt;}
    
    /**
     * Method that returns if the player have or not an armor
     * on he's bag.
     */
    public boolean getArmorb() {return armorb;}

    /**
     * Returns the in game camera.
     * @return the camera to player.
     */
    public Camera getCamera() {return playerCamera;}

    /**
     * Set the camera to the player and takes it as the only camera
     * in all the "world".
     * @param playerCamera the camera in game.
     */
    public void setCamera(Camera playerCamera) {this.playerCamera = playerCamera;}

    /**
     * Returns the player's size depending on the player's own width,
     * all of this in a Vector2f.
     * @return vector with the size.
     */
    public Vector2f getSize() {return new Vector2f(PLAYER_WIDTH, PLAYER_WIDTH);}

    /**
     * Returns all the damage that the player could do, depending of the
     * weapon.
     * @return the possible damage.
     */
    public int getDamage() {return (int) (damageMin + rand.nextFloat() * damageRange);}
	
	/**
	 * Gets the player's actual armor.
	 * @return player's armor.
	 */
	public int getArmori() {return armori;}
	
	/**
	 * Sets the amount of armor for the player to have.
	 * @param amt amount of armor to set
	 */
	public void setArmori(int amt) { armori += amt; }

	/**
     * Method that sets an amount of armor if player get some, or lose some.
     * @param amt amount of armor to set.
     */
	public void addArmori(int amt) {
		int temp = armori;
		armori += amt;
		if(armori>temp) {
			playerText.get("Notification").setText("You've got " + amt + " of armor!");
	    	notificationTime = Time.getTime();
		}
		if (armori > getMaxArmori())
        	armori = getMaxArmori();
        
        if (armori <= 0) {
        	armori = 0;
        	armorb = false;
        }
	}

	/**
	 * Returns the maximum of health that the player can
	 * handle.
	 * @return maximum of health
	 */
	public int getMaxHealth() { return maxHealth; }

	/**
	 * Sets a new maximum amount of health that the player
	 * can handle.
	 * @param amt amount of health
	 */
	public void setMaxHealth(int amt) { 
		this.maxHealth += amt; 
		if(maxHealth > 200) maxHealth = 200;
	}

	/**
	 * Returns the maximum of armor that the player can
	 * handle.
	 * @return maximum of armor
	 */
	public int getMaxArmori() { return maxArmori; }
	
	/**
	 * Sets a new maximum amount of armor that the player
	 * can handle.
	 * @param amt amount of armor
	 */
	public void setMaxArmori(int amt) {
		this.maxArmori += amt;
		if(maxArmori > 200) maxArmori = 200;
	}

	/**
	 * Returns the maximum of bullets that the player can
	 * handle.
	 * @return maximum of bullets
	 */
	public int getMaxBullets() { return maxBullets; }

	/**
	 * Sets a new maximum amount of bullets that the player
	 * can handle.
	 * @param amt amount of bullets
	 */
	public void setMaxBullets(int amt) {
		this.maxBullets += amt;
		if(maxBullets > 600) maxArmori = 600;
	}

	/**
	 * Returns the maximum of shells that the player can
	 * handle.
	 * @return maximum of shells
	 */
	public int getMaxShells() { return maxShells; }

	/**
	 * Sets a new maximum amount of shells that the player
	 * can handle.
	 * @param amt amount of shells
	 */
	public void setMaxShells(int amt) {
		this.maxShells += amt;
		if(maxShells > 200) maxArmori = 200;
	}
	
	/**
	 * Gets the player's weapon that currently is using.
	 * @return the weapon that player is using.
	 */
	public String getWeaponState() { return weaponState; }
	
	/**
	 * Sets the player's weapon that currently is using.
	 * @param amt the weapon that player is using.
	 */
	public void setWeaponState(String state) { this.weaponState = state; }

}
