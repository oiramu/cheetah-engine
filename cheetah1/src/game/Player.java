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

import static engine.components.Constants.*;

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
import engine.rendering.HUD;
import engine.rendering.Vertex;
import engine.rendering.Window;
import game.objects.Bleed;
import game.projectiles.Flame;
import game.projectiles.Rocket;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.6
 * @since 2017
 */
public class Player extends GameComponent {

    private static final float GUN_SIZE = 0.1f; 
    private static final float GUN_OFFSET = -0.0877f;
    private static final float GUN_OFFSET_X = 0f;
    private static final float MOUSE_SENSITIVITY = 0.25f;
    private static final float MAX_LOOK_ANGLE = 30;//45
    private static final float MIN_LOOK_ANGLE = -17;//-45
    private static final float PLAYER_WIDTH = 0.2f;
    private static final float BULLET_DAMAGE = 25f;
    private static final float SHELL_DAMAGE = 50f;
    private static final float ROCKET_DAMAGE = 200f;
    private static final float FLAME_DAMAGE = 300f;
    
    private static float gunTransformMultiplicator;
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
    public static final String ROCKET_LAUNCHER = "rocketLauncher";
    public static final String FLAME_THROWER = "flameThrower";
    
    public final String MELEE = "Melee";
    public final String BULLET = "Bullet";
    public final String SHELL = "Shell";
    public final String ROCKET = "Rocket";
    public final String GAS = "Gas";
    
    private static final String PLAYER_RES_LOC = "player/";
    private static final String WEAPONS_RES_LOC = "weapons/";
    private static final String FLASHLIGHT_RES_LOC = "flashLight/";
    private static final String HAND_RES_LOC = WEAPONS_RES_LOC + HAND + "/";
    private static final String PISTOL_RES_LOC = WEAPONS_RES_LOC + PISTOL + "/";
    private static final String SHOTGUN_RES_LOC = WEAPONS_RES_LOC + SHOTGUN +"/";
    private static final String MACHINEGUN_RES_LOC = WEAPONS_RES_LOC + MACHINEGUN + "/";
    private static final String SUPER_SHOTGUN_RES_LOC = WEAPONS_RES_LOC + SUPER_SHOTGUN + "/";
    private static final String CHAINGUN_RES_LOC = WEAPONS_RES_LOC + CHAINGUN + "/";
    private static final String ROCKET_LAUNCHER_RES_LOC = WEAPONS_RES_LOC + ROCKET_LAUNCHER + "/";
    private static final String FLAME_THROWER_RES_LOC = WEAPONS_RES_LOC + FLAME_THROWER + "/";
    private static final String PISGB0 = "PISGB0";
    private static final String PISFA0 = "PISFA0";
    private static final String PISFC0 = "PISFC0";
    private static final String PISFD0 = "PISFD0";
    private static final String PISFE0 = "PISFE0";
    private static final String UMGGD0 = "UMGGD0";
    private static final String UMGGA0 = "UMGGA0";
    private static final String UMGGB0 = "UMGGB0";
    //private static final String MIGGD0 = "MIGGD0";
    //private static final String MIGFA0 = "MIGFA0";
    //private static final String MIGFB0 = "MIGFB0";
    private static final String EMPTY = "EMPTY";
    private static final String GUNSOUND = "GUN";
    private static final String CLIPSOUND = "CLIPIN";
    private static final String RELOADSOUND = "RELOAD";
    private static final String LOADSOUND = "LOAD";
    
    private String weaponState;
    
    public HashMap<String, HUD> playerText;
    
    private static ArrayList<Texture> gunsAnimationMaterial0;
    private static ArrayList<Texture> gunsAnimationMaterial1;
    private static ArrayList<Texture> gunsAnimationMaterial2;
    private static ArrayList<Texture> gunsAnimationMaterial3;
    private static ArrayList<Texture> gunsAnimationMaterial4;
    private static ArrayList<Texture> gunsAnimationMaterial5;
    private static ArrayList<Texture> gunsAnimationMaterial6;
    private static ArrayList<Texture> gunsAnimationMaterial7;
    private static ArrayList<Texture> legMaterials;
    
    private static ArrayList<Clip> gunsNoiseSounds;
    private static ArrayList<Clip> gunsReloadSounds;
    private static ArrayList<Clip> gunsClippingSounds;
    private static ArrayList<Clip> gunsEmptyNoiseSounds;
    private static ArrayList<Clip> gunsLoadingSounds;
    private static ArrayList<Clip> playerMovementNoises;
    private static ArrayList<Clip> playerNoises;
    private static ArrayList<Clip> playerJumpNoises;
    private static ArrayList<Clip> flashLightNoises;
    
    private static ArrayList<Rocket> rocketsArray;
    private static ArrayList<Rocket> removeRockets;
    private static ArrayList<Flame> flamesArray;
    private static ArrayList<Flame> removeFlames;
    private static ArrayList<Bleed> bleedingArray;
    private static ArrayList<Bleed> removeBleedingList;

    private static final Vector2f centerPosition = new Vector2f(Display.getWidth()/2, Display.getHeight()/2);
    private static final Vector3f zeroVector = new Vector3f(0, 0, 0);
    
    private static float toTerrain = 0;
    private static float toGround;
    private static float speed;
    
    private static Clip gunNoise;
    private static Clip gunEmptyNoise;
    private static Clip gunLoad;
    private static Clip gunReload;
    private static Clip gunClipp;

    private Mesh gunMesh;
    private Material gunMaterial;
    private Material legMaterial;
    private Texture gunAnimationMaterial0;
    private Texture gunAnimationMaterial1;
    private Texture gunAnimationMaterial2;
    private Texture gunAnimationMaterial3;
    private Texture gunAnimationMaterial4;
    private Texture gunAnimationMaterial5;
    private Texture gunAnimationMaterial6;
    private Texture gunAnimationMaterial7;
    private Transform gunTransform;
    private MeshRenderer gunRenderer;
    private MeshRenderer legRenderer;
    private RenderingEngine renderingEngine;

    private Camera camera;
    private Random rand;
    private Vector3f movementVector;
    private Vector3f gunLightColor;
    
    private SpotLight fireLight;
    private SpotLight flashLight;
    
    public double notificationTime;
    
    private double gunFireTime;
    private double kickingTime;
    private float width;
    private float upAmt = 0;
    private int currentAmmo = 0;
    private int health;
    private int armori;
    private int bullets;
    private int shells;
    private int rockets;
    private int gas;
    private int maxHealth;
    private int maxArmori;
    private int maxBullets;
    private int maxShells;
    private int maxRockets;
    private int maxGas;
    private boolean goldkey;
    private boolean bronzekey;
    private boolean armorb;
    private boolean shotgun;
    private boolean machinegun;
    private boolean SShotgun;
    private boolean chaingun;
    private boolean rocketLauncher;
    private boolean flameThrower;
    private boolean isInAir = false;
    
    public static boolean mouseLocked;
    public static boolean isFlashLightOn;
    
    public String weaponType;
    
    public boolean fires;
    public boolean isAlive;
    public boolean isShooting;
    public boolean isReloading;
    public boolean isAutomatic;
    public boolean isDoubleShooter;
    public boolean chaingunCanFire;
    public boolean trowsKick;
    public boolean kickCanHurt;
    
    /**
     * Constructor of the main player.
     * @param position the position in the 3D space.
     */
    public Player(Vector3f position) {
    	
    	camera = new Camera((float) Math.toRadians(70.0f), (float)Window.getWidth()/(float)Window.getHeight(), 0.01f, 1000f);
    	camera.setPos(position);
    	
		gunsAnimationMaterial0 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial0.add(new Texture(HAND_RES_LOC + PISGB0));
    		gunsAnimationMaterial0.add(new Texture(PISTOL_RES_LOC + PISGB0));
    		gunsAnimationMaterial0.add(new Texture(SHOTGUN_RES_LOC + PISGB0));
    		gunsAnimationMaterial0.add(new Texture(MACHINEGUN_RES_LOC + PISGB0));
    		gunsAnimationMaterial0.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISGB0));
    		gunsAnimationMaterial0.add(new Texture(CHAINGUN_RES_LOC + PISGB0));
    		gunsAnimationMaterial0.add(new Texture(ROCKET_LAUNCHER_RES_LOC + PISGB0));
    		gunsAnimationMaterial0.add(new Texture(FLAME_THROWER_RES_LOC + PISGB0));
    	
		gunsAnimationMaterial1 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial1.add(new Texture(HAND_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(PISTOL_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(SHOTGUN_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(MACHINEGUN_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(CHAINGUN_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(ROCKET_LAUNCHER_RES_LOC + PISFA0));
    		gunsAnimationMaterial1.add(new Texture(FLAME_THROWER_RES_LOC + PISFA0));
    	
		gunsAnimationMaterial2 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial2.add(new Texture(HAND_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(PISTOL_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(SHOTGUN_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(MACHINEGUN_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(CHAINGUN_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(ROCKET_LAUNCHER_RES_LOC + PISFC0));
    		gunsAnimationMaterial2.add(new Texture(FLAME_THROWER_RES_LOC + PISFC0));
    	
		gunsAnimationMaterial3 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial3.add(null);
    		gunsAnimationMaterial3.add(null);
    		gunsAnimationMaterial3.add(new Texture(SHOTGUN_RES_LOC + PISFD0));
    		gunsAnimationMaterial3.add(null);
    		gunsAnimationMaterial3.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISFD0));
    		gunsAnimationMaterial3.add(new Texture(CHAINGUN_RES_LOC + PISFD0));
    		gunsAnimationMaterial3.add(new Texture(ROCKET_LAUNCHER_RES_LOC + PISFD0));
    		gunsAnimationMaterial3.add(new Texture(FLAME_THROWER_RES_LOC + PISFD0));
    	
		gunsAnimationMaterial4 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(new Texture(SHOTGUN_RES_LOC + PISFE0));
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(new Texture(SUPER_SHOTGUN_RES_LOC + PISFE0));
    		gunsAnimationMaterial4.add(new Texture(CHAINGUN_RES_LOC + PISFE0));
    		gunsAnimationMaterial4.add(null);
    		gunsAnimationMaterial4.add(new Texture(FLAME_THROWER_RES_LOC + PISFE0));
    	
		gunsAnimationMaterial5 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial5.add(null);
    		gunsAnimationMaterial5.add(null);
    		gunsAnimationMaterial5.add(null);
    		gunsAnimationMaterial5.add(null);
    		gunsAnimationMaterial5.add(null);
    		gunsAnimationMaterial5.add(new Texture(CHAINGUN_RES_LOC + UMGGD0));
    		gunsAnimationMaterial5.add(null);
    	
		gunsAnimationMaterial6 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial6.add(null);
    		gunsAnimationMaterial6.add(null);
    		gunsAnimationMaterial6.add(null);
    		gunsAnimationMaterial6.add(null);
    		gunsAnimationMaterial6.add(null);
    		gunsAnimationMaterial6.add(new Texture(CHAINGUN_RES_LOC + UMGGA0));
    		gunsAnimationMaterial6.add(null);
    	
		gunsAnimationMaterial7 = new ArrayList<Texture>();
    		
    		gunsAnimationMaterial7.add(null);
    		gunsAnimationMaterial7.add(null);
    		gunsAnimationMaterial7.add(null);
    		gunsAnimationMaterial7.add(null);
    		gunsAnimationMaterial7.add(null);
    		gunsAnimationMaterial7.add(new Texture(CHAINGUN_RES_LOC + UMGGB0));
    		gunsAnimationMaterial7.add(null);
    	

		legMaterials = new ArrayList<Texture>();
    		
    		legMaterials.add(new Texture(WEAPONS_RES_LOC+"kick/"+PISFA0));
    		legMaterials.add(new Texture(WEAPONS_RES_LOC+"kick/"+PISFC0));
    	
		gunsNoiseSounds = new ArrayList<Clip>();
    		
    		gunsNoiseSounds.add(AudioUtil.loadAudio(HAND_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(PISTOL_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(SHOTGUN_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(MACHINEGUN_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(SUPER_SHOTGUN_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(CHAINGUN_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(ROCKET_LAUNCHER_RES_LOC + GUNSOUND));
    		gunsNoiseSounds.add(AudioUtil.loadAudio(FLAME_THROWER_RES_LOC + GUNSOUND));
    	
		gunsReloadSounds = new ArrayList<Clip>();
    		
    		gunsReloadSounds.add(null);
    		gunsReloadSounds.add(null);
    		gunsReloadSounds.add(AudioUtil.loadAudio(SHOTGUN_RES_LOC + RELOADSOUND));
    		gunsReloadSounds.add(null);
    		gunsReloadSounds.add(AudioUtil.loadAudio(SUPER_SHOTGUN_RES_LOC + RELOADSOUND));
    		gunsReloadSounds.add(AudioUtil.loadAudio(CHAINGUN_RES_LOC + RELOADSOUND));
    		gunsReloadSounds.add(null);
    	
		gunsClippingSounds = new ArrayList<Clip>();
    		
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(AudioUtil.loadAudio(SHOTGUN_RES_LOC + CLIPSOUND));
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(AudioUtil.loadAudio(SUPER_SHOTGUN_RES_LOC + CLIPSOUND));
    		gunsClippingSounds.add(AudioUtil.loadAudio(CHAINGUN_RES_LOC + CLIPSOUND));
    		gunsClippingSounds.add(null);
    		gunsClippingSounds.add(AudioUtil.loadAudio(FLAME_THROWER_RES_LOC + CLIPSOUND));
    	
		gunsEmptyNoiseSounds = new ArrayList<Clip>();
    		
    		gunsEmptyNoiseSounds.add(null);
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(PISTOL_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(SHOTGUN_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(MACHINEGUN_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(SUPER_SHOTGUN_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(CHAINGUN_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(ROCKET_LAUNCHER_RES_LOC + EMPTY));
    		gunsEmptyNoiseSounds.add(AudioUtil.loadAudio(FLAME_THROWER_RES_LOC + EMPTY));
    	
		gunsLoadingSounds = new ArrayList<Clip>();
    		
    		gunsLoadingSounds.add(null);
    		gunsLoadingSounds.add(null);
    		gunsLoadingSounds.add(null);
    		gunsLoadingSounds.add(null);
    		gunsLoadingSounds.add(null);
    		gunsLoadingSounds.add(AudioUtil.loadAudio(CHAINGUN_RES_LOC + LOADSOUND));
    		gunsLoadingSounds.add(null);
    	
		playerNoises = new ArrayList<Clip>();
    		
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "MOVE"));
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "OOF"));
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "PLPAIN"));
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "PLDETH"));
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "jump/PLLAND"));
    		playerNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "KICK"));
    	
		playerMovementNoises = new ArrayList<Clip>();
    		
    		for (int i = 1; i < 7; i++)
    			playerMovementNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "walking/FSHARD" + i));
    	
		playerJumpNoises = new ArrayList<Clip>();
    		
    		for (int i = 1; i < 3; i++)
    			playerJumpNoises.add(AudioUtil.loadAudio(PLAYER_RES_LOC + "jump/PLJUMP" + i));
    	
		flashLightNoises = new ArrayList<Clip>();
    		
    		flashLightNoises.add(AudioUtil.loadAudio(FLASHLIGHT_RES_LOC + "FLASHON"));
    		flashLightNoises.add(AudioUtil.loadAudio(FLASHLIGHT_RES_LOC + "FLASHOFF"));
    	
		playerText = new HashMap<String, HUD>();
    		
    		playerText.put("Life", new HUD("", new Vector2f(-0.6f,-0.235f), new Vector2f(1f,4f)));
    		playerText.put("Armor", new HUD("", new Vector2f(-0.6f,-0.175f), new Vector2f(1f,4f)));
    		playerText.put("Ammo", new HUD("", new Vector2f(0.75f,-0.235f), new Vector2f(1f,4f)));
    		playerText.put("Notification", new HUD("", new Vector2f(-1.3f,1.25f), new Vector2f(0.7f,0.7f)));
    		playerText.put("CrossHair", new HUD("", zeroVector.getXY(), new Vector2f(1f,1f)));
    		playerText.put("LifeHUD", new HUD(new Material(new Texture("medkit/MEDIA")), new Vector2f(-3.8f,-4.4f), new Vector2f(0.2f,0.2f)));
    		playerText.put("ArmorHUD", new HUD(new Material(new Texture("armor/MEDIA")), new Vector2f(-3.8f,-3.2f), new Vector2f(0.2f,0.2f)));
    		playerText.put("AmmoHUD", new HUD(new Material(new Texture("EMPTY")), new Vector2f(4.9f,-4.3f), new Vector2f(0.125f,0.2f)));
    	
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
        
        gunMaterial = new Material(null);
        legMaterial = new Material(null);
        
        health = 0;
        armorb = false;
        chaingunCanFire = false;
        armori = 0;
        bullets = 0;
        shells = 0;
        maxHealth = 0;
        maxArmori = 0;
        maxBullets = 0;
        maxShells = 0;

        gunTransform = new Transform(camera.getPos());
        gunRenderer = new MeshRenderer(gunMesh, gunTransform, gunMaterial);
        legRenderer = new MeshRenderer(gunMesh, gunTransform, legMaterial);
        if (weaponState == null) { gotPistol(); }
        
    	flashLight = new SpotLight(new Vector3f(0.3f,0.3f,0.175f), 0.8f, 
    	    	new Attenuation(0.1f,0.1f,0.1f), new Vector3f(-2,0,5f), new Vector3f(1,1,1), 0.7f);
		fireLight = new SpotLight(gunLightColor, 1.6f, 
        		new Attenuation(attenuation,0,attenuation), getCamera().getPos(), new Vector3f(1,1,1), 0.7f);
        
        rocketsArray = new ArrayList<Rocket>();
        removeRockets = new ArrayList<Rocket>();
        flamesArray = new ArrayList<Flame>();
        removeFlames = new ArrayList<Flame>();
        bleedingArray = new ArrayList<Bleed>();
        removeBleedingList = new ArrayList<Bleed>();

        toGround = gunTransform.getPosition().getY();
        kickingTime = 0;
        gunFireTime = 0;
        notificationTime = 0;
        mouseLocked = false;
        isFlashLightOn = false;
        isAlive = true;
        Input.setMousePosition(centerPosition);
        Input.setCursor(false);
        movementVector = zeroVector;
        width = PLAYER_WIDTH;
        rand = new Random();
        goldkey = false;
        bronzekey = false;
        Debug.init(this);
        //Debug.enableGod(true);
    }

    private float upAngle = 0;
    
    /**
     * The settings that the player sets if he chooses the fist.
     */
    public void gotHand() {
    	gunAnimationMaterial0 = gunsAnimationMaterial0.get(0);
    	gunAnimationMaterial1 = gunsAnimationMaterial1.get(0);
    	gunAnimationMaterial2 = gunsAnimationMaterial2.get(0);
        gunNoise = gunsNoiseSounds.get(0);
        gunLightColor = null;
        gunEmptyNoise = null;
        gunFireAnimationTime = 0.1f;
        moveSpeed = 6f;
        speed = moveSpeed;
        attenuation = 0;
        weaponType = MELEE;
        weaponState = HAND;
        isAutomatic = false;
        isDoubleShooter = false;
        playerText.get("CrossHair").setText("");
    }
    
    /**
     * The settings that the player sets if he chooses the pistol of 
     * he's bag.
     */
    public void gotPistol() {
    	gunAnimationMaterial0 = gunsAnimationMaterial0.get(1);
    	gunAnimationMaterial1 = gunsAnimationMaterial1.get(1);
    	gunAnimationMaterial2 = gunsAnimationMaterial2.get(1);
    	gunLightColor = new Vector3f(1.0f,0.6f,0.2f);
        gunNoise = gunsNoiseSounds.get(1);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(1);
        gunFireAnimationTime = 0.1f;
        damageMin = BULLET_DAMAGE + (BULLET_DAMAGE / (gunFireAnimationTime * 100));
        damageRange = 30f;
        moveSpeed = 5f;
        speed = moveSpeed;
        attenuation = 0.25f;
        weaponType = BULLET;
        weaponState = PISTOL;
        isAutomatic = false;
        isDoubleShooter = false;
        playerText.get("CrossHair").setPosition(new Vector2f(-0.025f, 0));
        playerText.get("CrossHair").setText(".");
    }
    
    /**
     * The settings that the player sets if he chooses the shotgun
     * of he's bag, only if he have it on it.
     */
    public void gotShotgun() {
    	gunAnimationMaterial0 = gunsAnimationMaterial0.get(2);
    	gunAnimationMaterial1 = gunsAnimationMaterial1.get(2);
    	gunAnimationMaterial2 = gunsAnimationMaterial2.get(2);
    	gunAnimationMaterial3 = gunsAnimationMaterial3.get(2);
    	gunAnimationMaterial4 = gunsAnimationMaterial4.get(2);
    	gunLightColor = new Vector3f(0.9f,0.7f,0.2f);
        gunNoise = gunsNoiseSounds.get(2);
        gunReload = gunsReloadSounds.get(2);
        gunClipp = gunsClippingSounds.get(2);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(2);
        gunFireAnimationTime = 0.15f;   
        damageMin = SHELL_DAMAGE + (SHELL_DAMAGE / (gunFireAnimationTime * 100));
        damageRange = 50f;
        moveSpeed = 4.25f;
        speed = moveSpeed;
        attenuation = 0.1f;
        weaponType = SHELL;
        weaponState = SHOTGUN;
        isAutomatic = false;
        isDoubleShooter = false;
        playerText.get("CrossHair").setPosition(new Vector2f(-0.065f, 0));
        playerText.get("CrossHair").setText("( )");
    }
    
    /**
     * The settings that the player sets if he chooses the machine-gun
     * of he's bag, only if he have it on it.
     */
    public void gotMachinegun() {
    	gunAnimationMaterial0 = gunsAnimationMaterial0.get(3);
    	gunAnimationMaterial1 = gunsAnimationMaterial1.get(3);
    	gunAnimationMaterial2 = gunsAnimationMaterial2.get(3);
    	gunLightColor = new Vector3f(1.0f,0.6f,0.2f);
        gunNoise = gunsNoiseSounds.get(3);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(3);
        gunFireAnimationTime = 0.075f;   
        damageMin = BULLET_DAMAGE + (BULLET_DAMAGE/ (gunFireAnimationTime * 100));
        damageRange = 30f;
        moveSpeed = 4.5f;
        speed = moveSpeed;
        attenuation = 0.175f;
        weaponType = BULLET;
        weaponState = MACHINEGUN;
        isAutomatic = true;
        isDoubleShooter = false;
        playerText.get("CrossHair").setPosition(new Vector2f(-0.025f, 0));
        playerText.get("CrossHair").setText(".");
    }
    
    /**
     * The settings that the player sets if he chooses the super
     * shotgun of he's bag, only if he have it on it.
     */
    public void gotSShotgun() {
    	gunAnimationMaterial0 = gunsAnimationMaterial0.get(4);
    	gunAnimationMaterial1 = gunsAnimationMaterial1.get(4);
    	gunAnimationMaterial2 = gunsAnimationMaterial2.get(4);
    	gunAnimationMaterial3 = gunsAnimationMaterial3.get(4);
    	gunAnimationMaterial4 = gunsAnimationMaterial4.get(4);
    	gunLightColor = new Vector3f(0.9f,0.7f,0.2f);
        gunNoise = gunsNoiseSounds.get(4);
        gunReload = gunsReloadSounds.get(4);
        gunClipp = gunsClippingSounds.get(4);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(4);
        gunFireAnimationTime = 0.175f;   
        damageMin = (SHELL_DAMAGE + SHELL_DAMAGE) + (SHELL_DAMAGE / (gunFireAnimationTime * 100));
        damageRange = 50f;
        moveSpeed = 4.5f;
        speed = moveSpeed;
        attenuation = 0.05f;
        weaponType = SHELL;
        weaponState = SUPER_SHOTGUN;
        isAutomatic = false;
        isDoubleShooter = true;
        playerText.get("CrossHair").setPosition(new Vector2f(-0.065f, 0));
        playerText.get("CrossHair").setText("( )");
    }
    
    /**
     * The settings that the player sets if he chooses the chain-gun
     * of he's bag, only if he have it on it.
     */
    public void gotChaingun() {
    	gunAnimationMaterial0 = gunsAnimationMaterial0.get(5);
    	gunAnimationMaterial1 = gunsAnimationMaterial1.get(5);
    	gunAnimationMaterial2 = gunsAnimationMaterial2.get(5);
    	gunAnimationMaterial3 = gunsAnimationMaterial3.get(5);
    	gunAnimationMaterial4 = gunsAnimationMaterial4.get(5);
    	gunAnimationMaterial5 = gunsAnimationMaterial5.get(5);
    	gunAnimationMaterial6 = gunsAnimationMaterial6.get(5);
    	gunAnimationMaterial7 = gunsAnimationMaterial7.get(5);
    	gunLightColor = new Vector3f(1.0f,0.7f,0.2f);
    	gunNoise = gunsNoiseSounds.get(5);
        gunReload = gunsReloadSounds.get(5);
        gunClipp = gunsClippingSounds.get(5);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(5);
        gunLoad = gunsLoadingSounds.get(5);
        damageMin = BULLET_DAMAGE + (BULLET_DAMAGE/ (gunFireAnimationTime * 100));
        damageRange = 60f;
        moveSpeed = 3.5f;
        speed = moveSpeed;
        attenuation = 0.025f;
        weaponType = BULLET;
        weaponState = CHAINGUN;
        isAutomatic = true;
        isDoubleShooter = false;
        playerText.get("CrossHair").setPosition(new Vector2f(-0.033f, 0));
        playerText.get("CrossHair").setText(".");
    }
    
    /**
     * The settings that the player sets if he chooses the rocket launcher
     * of he's bag, only if he have it on it.
     */
    public void gotRocketLauncher() {
    	gunAnimationMaterial0 = gunsAnimationMaterial0.get(6);
    	gunAnimationMaterial1 = gunsAnimationMaterial1.get(6);
    	gunAnimationMaterial2 = gunsAnimationMaterial2.get(6);
    	gunAnimationMaterial3 = gunsAnimationMaterial2.get(6);
    	gunLightColor = new Vector3f(1.0f,0.7f,0.2f);
        gunNoise = gunsNoiseSounds.get(6);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(6);
        gunFireAnimationTime = 0.0875f;   
        damageMin = ROCKET_DAMAGE + (ROCKET_DAMAGE/ (gunFireAnimationTime * 100));
        damageRange = 60f;
        moveSpeed = 3.5f;
        speed = moveSpeed;
        attenuation = 0.025f;
        weaponType = ROCKET;
        weaponState = ROCKET_LAUNCHER;
        isAutomatic = false;
        isDoubleShooter = false;
        playerText.get("CrossHair").setPosition(new Vector2f(-0.08f, 0));
        playerText.get("CrossHair").setText("( )");
    }
    
    /**
     * The settings that the player sets if he chooses the rocket launcher
     * of he's bag, only if he have it on it.
     */
    public void gotFlameThrower() {
    	gunAnimationMaterial0 = gunsAnimationMaterial0.get(7);
    	gunAnimationMaterial1 = gunsAnimationMaterial1.get(7);
    	gunAnimationMaterial2 = gunsAnimationMaterial2.get(7);
    	gunAnimationMaterial3 = gunsAnimationMaterial3.get(7);
    	gunAnimationMaterial4 = gunsAnimationMaterial4.get(7);
        gunNoise = gunsNoiseSounds.get(7);
        gunClipp = gunsClippingSounds.get(7);
        gunEmptyNoise = gunsEmptyNoiseSounds.get(7);
        gunFireAnimationTime = 0.1f;   
        damageMin = FLAME_DAMAGE + (FLAME_DAMAGE/ (gunFireAnimationTime * 100));
        damageRange = 60f;
        moveSpeed = 3.5f;
        speed = moveSpeed;
        attenuation = 0.025f;
        weaponType = GAS;
        weaponState = FLAME_THROWER;
        isAutomatic = true;
        isDoubleShooter = false;
        playerText.get("CrossHair").setPosition(new Vector2f(-0.08f, 0));
        playerText.get("CrossHair").setText("( )");
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
    		
	        if (Input.getKeyDown(Input.KEY_E))
	            Auschwitz.getLevel().openDoors(camera.getPos(), true);
	        
	        if (Input.getKeyDown(Input.KEY_1)) {
	        	if(weaponState == HAND || fires) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		if(weaponState != HAND)
	        			AudioUtil.playAudio(playerNoises.get(0), 0);
	        		if(!fires)
	        			gotHand();
	        	}
	        } else if (Input.getKeyDown(Input.KEY_2)) {
	        	if(weaponState == PISTOL || fires) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		if(weaponState != PISTOL)
	        			AudioUtil.playAudio(playerNoises.get(0), 0);
	        		if(!fires)
	        			gotPistol();
	        	}
	        } else if (Input.getKeyDown(Input.KEY_3)) {
	        	if(!shotgun || weaponState == SHOTGUN || fires) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		if(weaponState != SHOTGUN)
	        			AudioUtil.playAudio(playerNoises.get(0), 0);
	        		if(!fires)
	        			gotShotgun();
	        	}
	        } else if (Input.getKeyDown(Input.KEY_4)) {
	        	if(!machinegun || weaponState == MACHINEGUN || fires) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		if(weaponState != MACHINEGUN)
	        			AudioUtil.playAudio(playerNoises.get(0), 0);
	        		if(!fires)
	        			gotMachinegun();
	        	}
	        } else if (Input.getKeyDown(Input.KEY_5)) {
	        	if(!SShotgun || weaponState == SUPER_SHOTGUN || fires) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		if(weaponState != SUPER_SHOTGUN)
	        			AudioUtil.playAudio(playerNoises.get(0), 0);
	        		if(!fires)
	        			gotSShotgun();
	        	}
	        } else if (Input.getKeyDown(Input.KEY_6)) {
	        	if(!chaingun || weaponState == CHAINGUN || fires) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		if(weaponState != CHAINGUN)
	        			AudioUtil.playAudio(playerNoises.get(0), 0);
	        		if(!fires)
	        			gotChaingun();
	        	}
	        } else if (Input.getKeyDown(Input.KEY_7)) {
	        	if(!rocketLauncher || weaponState == ROCKET_LAUNCHER || fires) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		if(weaponState != ROCKET_LAUNCHER)
	        			AudioUtil.playAudio(playerNoises.get(0), 0);
	        		if(!fires)
	        			gotRocketLauncher();
	        	}
	        } else if (Input.getKeyDown(Input.KEY_8)) {
	        	if(!flameThrower || weaponState == FLAME_THROWER || fires) {
	        		AudioUtil.playAudio(playerNoises.get(1), 0);
	        	} else {
	        		if(weaponState != FLAME_THROWER)
	        			AudioUtil.playAudio(playerNoises.get(0), 0);
	        		if(!fires)
	        			gotFlameThrower();
	        	}
	        }
	        
	        if(Input.getMouse(1))
	        	gunTransformMultiplicator = 0.080f;
	        else
	        	gunTransformMultiplicator = 0.0933f;
	        
	        if (Input.getKey(Input.KEY_ESCAPE)) {
	            Input.setCursor(true);
	            mouseLocked = false;
	        }
	        
	        if(Input.getKey(Input.KEY_LSHIFT)) {
	        	toTerrain = toGround/2;
	        	moveSpeed = speed/2;
	        } else {
	        	toTerrain = toGround;
	        	moveSpeed = speed;
	        }
	        
	        if(Input.getKeyDown(Input.KEY_Q) && !trowsKick) {
	        	AudioUtil.playAudio(playerNoises.get(5), 0);
	        	kickingTime = Time.getTime();
	        	moveSpeed = speed*5;
	        }else {
	        	moveSpeed = speed;
	        }
	        
	        if (fires && !isReloading) {
	            if (!mouseLocked) {
	                Input.setMousePosition(centerPosition);
	                Input.setCursor(false);
	                mouseLocked = true;
	            } else {
	            	Vector2f shootDirection = camera.getForward().getXZ().normalized();
	        		
		            Vector2f lineStart = camera.getPos().getXZ();
		            Vector2f lineEnd = lineStart.add(shootDirection.mul(1000.0f));
		
		            Auschwitz.getLevel().checkIntersections(lineStart, lineEnd, true);
	            	
		            if(weaponType == MELEE) {
		            	AudioUtil.playAudio(gunNoise, 0);
		            	gunFireTime = Time.getTime();
		            } else if(bullets != 0 && weaponType == BULLET && getWeaponState() != CHAINGUN) {
		            	AudioUtil.playAudio(gunNoise, 0);
		            	addBullets(-1);
		            	gunFireTime = Time.getTime();
		            } else if(weaponType == BULLET && getWeaponState() == CHAINGUN) {
		            	if(chaingunCanFire) {
			            	AudioUtil.playAudio(gunNoise, 0);
			            	AudioUtil.playAudio(gunReload, 0);
			            	addBullets(-1);
		            	} else AudioUtil.playAudio(gunLoad, 0);
		            	gunFireTime = Time.getTime();
		            } else if(shells != 0 && weaponType == SHELL) {
		            	AudioUtil.playAudio(gunNoise, 0);
		            	gunFireTime = Time.getTime();
		            	if(isDoubleShooter)
		            		addShells(-2);
		            	else
		            		addShells(-1);
		            } else if(rockets != 0 && weaponType == ROCKET) {
		            	AudioUtil.playAudio(gunNoise, 0);
		            	addRockets(-1);
		            	rocketsArray.add(new Rocket(new Transform(gunTransform.getPosition()), true));
		            	gunFireTime = Time.getTime();
		            } else if(gas != 0 && weaponType == GAS) {
		            	AudioUtil.playAudio(gunNoise, 0);
		            	addGas(-1);
		            	flamesArray.add(new Flame(new Transform(new Vector3f(gunTransform.getPosition().getX(), 0, gunTransform.getPosition().getZ()))));
		            	gunFireTime = Time.getTime();
		            }
	            }
	        }

	        if(isFlashLightOn) {
				if (Input.getKeyDown(Input.KEY_F)) {
					renderingEngine.removeLight(flashLight);
					AudioUtil.playAudio(flashLightNoises.get(1), 0);
					isFlashLightOn = false;
				}
            } else {
            	if (Input.getKeyDown(Input.KEY_F)) {
            		AudioUtil.playAudio(flashLightNoises.get(0), 0);
            		renderingEngine.addLight(flashLight);
	            	isFlashLightOn = true;
            	}
            }
	        
	        if(Debug.state) {
				if (Input.getKeyDown(Input.KEY_F3))
					Debug.state = false;
            } else {
            	if (Input.getKeyDown(Input.KEY_F3))
            		Debug.state = true;
            }
	
	        movementVector = zeroVector;
	        if(!isInAir) {
		        if(Input.getKeyDown(Input.KEY_W) || Input.getKeyDown(Input.KEY_UP) ||
		        		Input.getKeyDown(Input.KEY_S) || Input.getKeyDown(Input.KEY_DOWN)
		        		|| Input.getKeyDown(Input.KEY_A) || Input.getKeyDown(Input.KEY_D)) {
		        	AudioUtil.playAudio(playerMovementNoises.get(new Random().nextInt(playerMovementNoises.size())), 0);
		        }
	        }
	        		
	        if (Input.getKey(Input.KEY_W)) {
	            movementVector = movementVector.add(camera.getForward());
	        }
	        if (Input.getKey(Input.KEY_S)) {
	            movementVector = movementVector.sub(camera.getForward());
	        }
	        if (Input.getKey(Input.KEY_A)) {
	            movementVector = movementVector.add(camera.getLeft());
	        }
	        if (Input.getKey(Input.KEY_D)) {
	            movementVector = movementVector.add(camera.getRight());
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
            		AudioUtil.playAudio(playerJumpNoises.get(new Random().nextInt(playerJumpNoises.size())), 0);
            		upAmt = 10.0f;
            		movementVector = movementVector.add(camera.getForward());
            		isInAir = true;
            	}
	        }
	
	        if (mouseLocked) {
	            if (rotY) {
	                camera.rotateY(deltaPos.getX() * MOUSE_SENSITIVITY);
	            }
	            
	            //Looking up and down
	             if(rotX) { float amt = -deltaPos.getY() * MOUSE_SENSITIVITY;
	             if(amt + upAngle > -MIN_LOOK_ANGLE) {
	             camera.rotateX(-MIN_LOOK_ANGLE - upAngle); upAngle =
	             -MIN_LOOK_ANGLE; } else if(amt + upAngle < -MAX_LOOK_ANGLE) {
	             camera.rotateX(-MAX_LOOK_ANGLE - upAngle); upAngle =
	             -MAX_LOOK_ANGLE; } else { camera.rotateX(amt); upAngle +=
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
    	float dy = GUN_OFFSET;
		float dx = GUN_OFFSET_X;
    	if(isAlive) {
    		upAmt -= (GRAVITY * 10f) * delta;
    		movAmt = (float) (moveSpeed * delta);
    	} else {
    		upAmt -= GRAVITY * delta;
    		toTerrain = 0.15f;
    	}
    	
    	camera.setPos(camera.getPos().add(new Vector3f(0, (float) (upAmt * delta), 0)));
        if(camera.getPos().getY() < toTerrain) {
        	upAmt = 0;
        	camera.getPos().setY(toTerrain);
        	if(isInAir)
        		AudioUtil.playAudio(playerNoises.get(4), 0);
        	isInAir = false;
        }  
        
        movementVector.setY(0);

        Vector3f oldPos = camera.getPos();
        Vector3f newPos = oldPos.add(movementVector.normalized().mul(movAmt));

        Vector3f collisionVector = Auschwitz.getLevel().checkCollisions(oldPos, newPos, width, width);

        movementVector = movementVector.normalized().mul(collisionVector);

        if (movementVector.length() > 0 && isAlive) {
        	float bobOscillate = (float) Math.sin(time * moveSpeed * (2 * Math.PI));
        	dy += bobOscillate/500;
            dx += bobOscillate/750;
            camera.move(movementVector, movAmt);
        }

        //Gun movement
        gunTransform.setScale(1,1,1);
		gunTransform.setPosition(camera.getPos().add(camera.getForward().normalized().mul(gunTransformMultiplicator)));
		gunTransform.getPosition().setX(gunTransform.getPosition().getX() + dx);
		gunTransform.getPosition().setY(gunTransform.getPosition().getY() + dy);

        Vector3f playerDistance = gunTransform.getPosition().sub(camera.getPos());

        Vector3f orientation = playerDistance.normalized();
        
        setDistance(playerDistance.length());

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() >= 0)
            angle = 180 + angle;

        gunTransform.setRotation(0, angle + 90, 0);     
        
        if(isFlashLightOn) {
	        flashLight.setPosition(getCamera().getPos());
	        flashLight.setDirection(getCamera().getForward());
        }
        if(!isShooting && (weaponType != MELEE || weaponType != GAS) && renderingEngine != null)
        	renderingEngine.removeLight(fireLight);
        if(fires) {
        	fireLight.setColor(gunLightColor);
	        fireLight.setAtten(new Attenuation(attenuation,0, attenuation));
	        fireLight.setPosition(new Vector3f(getCamera().getPos().getX(), 0, getCamera().getPos().getZ()));
	        fireLight.setDirection(getCamera().getForward());
        }
        
        double gunTime = gunFireTime + gunFireAnimationTime;
    	double gunTime2 = gunTime + gunFireAnimationTime;
    	double gunTime3 = gunTime2 + gunFireAnimationTime;
    	double gunTime4 = gunTime3 + gunFireAnimationTime;
    	
    	if(!rocketsArray.isEmpty())
    		for(Rocket rocket : rocketsArray)
    			rocket.update(delta);
    	if(!flamesArray.isEmpty())
    		for(Flame flame : flamesArray)
    			flame.update(delta);
    	if(!bleedingArray.isEmpty() && PARTICLES_LEVEL >= 1) {
    		for(Bleed bleed : bleedingArray) {
    			bleed.update(delta);
    			if(bleed.getState() == 2)
    				removeBleedingList.add(bleed);
    		}
    	}
    	
    	switch(weaponType) {
    		case MELEE:
    			currentAmmo = 0;
    		break;
    		case BULLET:
    			playerText.get("AmmoHUD").setMaterial(new Material(new Texture("bullet/MEDIA")));
    			currentAmmo = getBullets();
    		break;
    		case SHELL:
    			playerText.get("AmmoHUD").setMaterial(new Material(new Texture("shell/MEDIA")));
    			currentAmmo = getShells();
    		break;
    		case ROCKET:
    			playerText.get("AmmoHUD").setMaterial(new Material(new Texture("rocket/MEDIA1")));
    			currentAmmo = getRockets();
    		break;
    		case GAS:
    			playerText.get("AmmoHUD").setMaterial(new Material(new Texture(FLAME_THROWER_RES_LOC+"FLMTF0")));
    			currentAmmo = getGas();
    		break;
    	}
    	
    	playerText.get("Life").setText(getHealth()+"%");
    	playerText.get("Ammo").setText(currentAmmo);
        if(isArmor()) playerText.get("Armor").setText(getArmor()+"%");
        
        if ((double) time < kickingTime + 0.1f) {
        	trowsKick = true;
        	legMaterial.setDiffuse(legMaterials.get(0));
        } else if ((double) time < kickingTime+0.3f) {
        	kickCanHurt = true;
        	trowsKick = true;
        	legMaterial.setDiffuse(legMaterials.get(1));
        } else {
        	kickCanHurt = false;
        	trowsKick = false;
        }
        
		if(weaponType == MELEE) {
	        if ((double) time < gunTime) {
	        	isReloading = true;
	        	gunMaterial.setDiffuse(gunAnimationMaterial1);
	        } else if ((double) time < gunTime2) {
	        	gunMaterial.setDiffuse(gunAnimationMaterial2);
	        } else {
	        	gunMaterial.setDiffuse(gunAnimationMaterial0);
	            isReloading = false;
	        }
        }
		if(weaponType == BULLET && weaponState != CHAINGUN) {
	        if ((double) time < gunTime) {
	        	isReloading = true;
	        	renderingEngine.addLight(fireLight);
	        	gunMaterial.setDiffuse(gunAnimationMaterial1);
	        } else if ((double) time < gunTime2) {
	        	gunMaterial.setDiffuse(gunAnimationMaterial2);
	        	isShooting = false;
	        } else {
	        	gunMaterial.setDiffuse(gunAnimationMaterial0);
            	isReloading = false;
	        }
		}
		
		if(weaponType == BULLET && weaponState == CHAINGUN) {
			if(chaingunCanFire) {
				if ((double) time < gunTime) {
		        	isReloading = true;
		        	renderingEngine.addLight(fireLight);
		        	gunMaterial.setDiffuse(gunAnimationMaterial1);
		        } else if ((double) time < gunTime2) {
		        	gunMaterial.setDiffuse(gunAnimationMaterial2);
		        } else if ((double) time < gunTime3) {
		        	gunMaterial.setDiffuse(gunAnimationMaterial3);
		        } else if ((double) time < gunTime4) {
		        	gunMaterial.setDiffuse(gunAnimationMaterial4);
		        	isShooting = false;
		        	if(fires && bullets != 0)
		        		chaingunCanFire = true;
		        	else if(!fires || bullets == 0){
		        		AudioUtil.playAudio(gunClipp, 0);
		        		chaingunCanFire = false;
		        	}
		        } else {
		        	gunMaterial.setDiffuse(gunAnimationMaterial0);
		            isReloading = false;
		            if(fires && bullets != 0)
		        		gunFireAnimationTime = 0.025f;
		        	else
		        		gunFireAnimationTime = 0.1f;
		        }
			} else {
				if ((double) time < gunTime) {
		        	isReloading = true;
		        	gunMaterial.setDiffuse(gunAnimationMaterial5);
		        } else if ((double) time < gunTime2) {
		        	gunMaterial.setDiffuse(gunAnimationMaterial6);
		        } else if ((double) time < gunTime3) {
		        	gunMaterial.setDiffuse(gunAnimationMaterial7);
		        	isShooting = false;
		        	if(fires && bullets != 0)
		        		chaingunCanFire = true;
		        	else if(!fires){
		        		AudioUtil.playAudio(gunClipp, 0);
		        		chaingunCanFire = false;
		        	}
		        } else {
		        	gunMaterial.setDiffuse(gunAnimationMaterial0);
	            	isReloading = false;
	            	if(fires && bullets != 0)
		        		gunFireAnimationTime = 0.025f;
		        	else
		        		gunFireAnimationTime = 0.1f;
		        }
			}
		}
		if(weaponType == SHELL) {
	        if ((double) time < gunTime) {
	        	isReloading = true;
	        	renderingEngine.addLight(fireLight);
	        	gunMaterial.setDiffuse(gunAnimationMaterial1);
	        } else if ((double) time < gunTime2) {
	        	AudioUtil.playAudio(gunReload, 0);
	        	gunMaterial.setDiffuse(gunAnimationMaterial2);
		        isShooting = false;
	        } else if ((double) time < gunTime3) {
	        	AudioUtil.playAudio(gunClipp, 0);
	        	gunMaterial.setDiffuse(gunAnimationMaterial3);
	        } else if ((double) time < gunTime4) {
	        	gunMaterial.setDiffuse(gunAnimationMaterial4);
	        } else {
	        	gunMaterial.setDiffuse(gunAnimationMaterial0);
	            isReloading = false;
	        }
		}
		if(weaponType == ROCKET) {
	        if ((double) time < gunTime) {
	        	isReloading = true;
	        	renderingEngine.addLight(fireLight);
	        	gunMaterial.setDiffuse(gunAnimationMaterial1);
	        } else if ((double) time < gunTime2) {
	        	gunMaterial.setDiffuse(gunAnimationMaterial2);
	        } else if ((double) time < gunTime3) {
	        	gunMaterial.setDiffuse(gunAnimationMaterial3);
	        	isShooting = false;
	        } else {
	        	gunMaterial.setDiffuse(gunAnimationMaterial0);
            	isReloading = false;
	        }
		}
		
		if(weaponType == GAS) {
			double timeDecimals = (time - (double) ((int) time));
	        if ((double) time < gunTime) {
	        	isReloading = true;
	        	gunMaterial.setDiffuse(gunAnimationMaterial3);
	        } else if ((double) time < gunTime2) {
	        	gunMaterial.setDiffuse(gunAnimationMaterial4);
	        	if(!fires)
	        		AudioUtil.playAudio(gunClipp, 0);
	        } else {
	        	if(timeDecimals <= 0.1)
	        		gunMaterial.setDiffuse(gunAnimationMaterial0);
	        	else if(timeDecimals <= 0.2)
	        		gunMaterial.setDiffuse(gunAnimationMaterial1);
	        	else
	        		gunMaterial.setDiffuse(gunAnimationMaterial2);
            	isReloading = false;
	        }
		}
		
		if(!removeRockets.isEmpty())
			for (Rocket rocketToDelete : removeRockets) 
				rocketsArray.remove(rocketToDelete);
		if(!removeFlames.isEmpty())
			for (Flame flameToDelete : removeFlames) 
				flamesArray.remove(flameToDelete);
		if(!removeBleedingList.isEmpty())
			for (Bleed bleedToDelete : removeBleedingList) 
				bleedingArray.remove(bleedToDelete); 
		
		removeRockets.clear();
		removeFlames.clear();
		removeBleedingList.clear();
    }

    /**
     * Method that renders the player's mesh.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {
    	if(this.renderingEngine == null) this.renderingEngine = renderingEngine;
    	double time = Time.getTime();

    	if(shader.getName() == "forward-ambient") {
	    	Debug.printToEngine(renderingEngine);
	    	playerText.get("CrossHair").render(renderingEngine);
	    	playerText.get("LifeHUD").render(renderingEngine);
	    	playerText.get("Life").render(renderingEngine);
	    	if(weaponType != MELEE) {
	    		playerText.get("AmmoHUD").render(renderingEngine);
	    		playerText.get("Ammo").render(renderingEngine);
	    	}
	        if(isArmor()) {
	        	playerText.get("ArmorHUD").render(renderingEngine);
	        	playerText.get("Armor").render(renderingEngine);
	        }
	        if(time < notificationTime + 2.5f) playerText.get("Notification").render(renderingEngine);
    	}

    	if(!rocketsArray.isEmpty())
    		for(Rocket rocket : rocketsArray)
    			rocket.render(shader, renderingEngine);
    	if(!flamesArray.isEmpty())
    		for(Flame flame : flamesArray)
    			flame.render(shader, renderingEngine);
    	if(!bleedingArray.isEmpty() && PARTICLES_LEVEL >= 1)
    		for(Bleed bleed : bleedingArray)
    			bleed.render(shader, renderingEngine);
        
        if(gunRenderer != null)gunRenderer.render(shader, renderingEngine);
        if (trowsKick) legRenderer.render(shader, renderingEngine);
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
     * @param provider of the adding.
     */
    public void addHealth(int amt, String provider) {
    	int temp = health;
    	setHealth(amt);
        if(health>temp) {
	        playerText.get("Notification").setText("You've got " + amt + " of health!");
	    	notificationTime = Time.getTime();
        }
        if (health > getMaxHealth()) {
            health = getMaxHealth();
        }
        if (health <= 0) {
        	playerText.get("Notification").setText("You've killed by " + provider + "!");
        	notificationTime = Time.getTime();
        	AudioUtil.playAudio(playerNoises.get(3), 0);
        	health = 0;
            bullets = 0;
            shells = 0;
            armori = 0;
            armorb = false;
            shotgun = false;
            machinegun = false;
            SShotgun = false;
            chaingun = false;
            rocketLauncher = false;
            flameThrower = false;
            isAlive = false;
            gotHand();
        } else {
            if (amt < 0) {
            	if(provider != "FIRE")
            		bleedingArray.add(new Bleed(new Transform(getTransform().getPosition().add(0.01f))));
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
    	setBullets(amt);
        if(bullets>temp) {
	        playerText.get("Notification").setText("You've got " + amt + " bullets!");
	    	notificationTime = Time.getTime();
        }
        if (bullets > getMaxBullets()) {
        	bullets = getMaxBullets();
        }    
        if(weaponType == BULLET) {
        	if (bullets < 0) {
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
		setShells(amt);
		if(shells>temp) {
			playerText.get("Notification").setText("You've got " + amt + " shotgun shells!");
			notificationTime = Time.getTime();
		}
        if (shells > getMaxShells()) {
        	shells = getMaxShells();
        }
        if(weaponType == SHELL) {
        	if (shells < 0) {
        		shells = 0;
        		playerText.get("Notification").setText("You Need More Shells!");
            	notificationTime = Time.getTime();
        		AudioUtil.playAudio(gunEmptyNoise, 1);
        	}
        }
	}
	
	/**
	 * Gets the player's actual rockets.
	 * @return player's rockets.
	 */
	public int getRockets() {return rockets;}
	
	/**
	 * Sets the amount of rockets for the player to have.
	 * @param amt amount of rockets to set
	 */
	public void setRockets(int amt) { rockets += amt; }

	/**
     * Method that sets an amount of rockets if player get some, or lose some.
     * @param amt amount of rockets to set.
     */
	public void addRockets(int amt) {
		int temp = rockets;
		setRockets(amt);
		if(rockets>temp) {
			playerText.get("Notification").setText("You've got " + amt + " rockets!");
			notificationTime = Time.getTime();
		}
        if (rockets > getMaxRockets()) {
        	rockets = getMaxRockets();
        }
        if(weaponType == ROCKET) {
        	if (rockets < 0) {
        		rockets = 0;
        		playerText.get("Notification").setText("You Need More Rockets!");
            	notificationTime = Time.getTime();
        		AudioUtil.playAudio(gunEmptyNoise, 1);
        	}
        }
	}
	
	/**
	 * Gets the player's actual gas.
	 * @return player's gas.
	 */
	public int getGas() {return gas;}
	
	/**
	 * Sets the amount of gas for the player to have.
	 * @param amt amount of gas to set
	 */
	public void setGas(int amt) { gas += amt; }

	/**
     * Method that sets an amount of gas if player get some, or lose some.
     * @param amt amount of gas to set.
     */
	public void addGas(int amt) {
		int temp = gas;
		setGas(amt);
		if(gas>temp) {
			playerText.get("Notification").setText("You've got " + amt + " of gas!");
			notificationTime = Time.getTime();
		}
        if (gas > getMaxGas()) {
        	gas = getMaxGas();
        }
        if(weaponType == GAS) {
        	if (gas < 0) {
        		gas = 0;
        		playerText.get("Notification").setText("You Need More Gas!");
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
    public boolean isShotgun() {return shotgun;}
    
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
    public boolean isMachinegun() {return machinegun;}
    
    /**
     * Method that assigns the super shotgun to the player object.
     * @param amt amount.
     */
    public void setSuperShotgun(boolean amt) {
    	if(amt == true && SShotgun == false && isAlive) {
    		SShotgun = amt;
    		gotSShotgun();
    	}
    	if(SShotgun == true && isAlive && amt != true) {
    		playerText.get("Notification").setText("You've got a double barrel shotgun!");
        	notificationTime = Time.getTime();
    	}
    }
    
    /**
     * Method that returns if the player have or not a super shotgun 
     * on he's bag.
     */
    public boolean isSuperShotgun() {return SShotgun;}
    
    /**
     * Method that returns if the player have or not a chain-gun
     * on he's bag.
     */
    public boolean isChaingun() {return chaingun;}
    
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
    		playerText.get("Notification").setText("Chain-gun, make em' pay!");
        	notificationTime = Time.getTime();
    	}
    }
    
    /**
     * Method that returns if the player have or not a rocket launcher.
     * on he's bag.
     * @returns rocketLauncher rocket launcher
     */
    public boolean isRocketLauncher() {return rocketLauncher;}
    
    /**
     * Method that assigns the rocket launcher to the player object.
     * @param amt amount.
     */
    public void setRocketLauncher(boolean amt) {
    	if(amt == true && rocketLauncher == false && isAlive) {
    		rocketLauncher = amt;
    		gotRocketLauncher();
    	}
    	if(rocketLauncher == true && isAlive) {
    		playerText.get("Notification").setText("Rocket Launcher! time to blow something up");
        	notificationTime = Time.getTime();
    	}
    }
    
    /**
     * Method that returns if the player have or not a rocket launcher.
     * on he's bag.
     * @returns rocketLauncher rocket launcher
     */
    public boolean isFlameThrower() {return flameThrower;}
    
    /**
     * Method that assigns the flame thrower to the player object.
     * @param amt amount.
     */
    public void setFlameThrower(boolean amt) {
    	if(amt == true && flameThrower == false && isAlive) {
    		flameThrower = amt;
    		gotFlameThrower();
    	}
    	if(flameThrower == true && isAlive) {
    		playerText.get("Notification").setText("Flame trower! they'll get fired");
        	notificationTime = Time.getTime();
    	}
    }
    
    /**
     * Method that assigns the armor to the player object.
     * @param amt amount.
     */
    public void setArmor(boolean amt) { armorb = amt; }
    
    /**
     * Method that returns if the player have or not an armor
     * on he's bag.
     */
    public boolean isArmor() { return armorb; }

    /**
     * Returns the in game camera.
     * @return the camera to player.
     */
    public Camera getCamera() {return camera;}

    /**
     * Set the camera to the player and takes it as the only camera
     * in all the "world".
     * @param playerCamera the camera in game.
     */
    public void setCamera(Camera playerCamera) {this.camera = playerCamera;}

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
    public int getDamage() {
    	if(weaponType != MELEE && currentAmmo > 0)
    		return (int) (damageMin + rand.nextFloat() * damageRange);
    	else
    		return 0;
    }
    
    /**
     * Returns all the damage that the player could do, depending of the
     * melee type.
     * @return the possible damage.
     */
    public int getMeleeDamage() {
    	if(kickCanHurt)
    		return (int) (20f + rand.nextFloat() * 0.2f);
    	else
    		return (int) (15f + rand.nextFloat() * 0.1f);
    }
	
	/**
	 * Gets the player's actual armor.
	 * @return player's armor.
	 */
	public int getArmor() {return armori;}
	
	/**
	 * Sets the amount of armor for the player to have.
	 * @param amt amount of armor to set
	 */
	public void setArmori(int amt) { armori += amt; }

	/**
     * Method that sets an amount of armor if player get some, or lose some.
     * @param amt amount of armor to set.
     */
	public void addArmor(int amt) {
		int temp = armori;
		setArmori(amt);
		if(armori>temp) {
			armorb = true;
			playerText.get("Notification").setText("You've got " + amt + " of armor!");
	    	notificationTime = Time.getTime();
		}
		if (armori > getMaxArmor())
        	armori = getMaxArmor();
        
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
	public int getMaxArmor() { return maxArmori; }
	
	/**
	 * Sets a new maximum amount of armor that the player
	 * can handle.
	 * @param amt amount of armor
	 */
	public void setMaxArmor(int amt) {
		if(amt > 200) amt = 200;
		this.maxArmori += amt;
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
		if(amt > 600) amt = 600;
		this.maxBullets += amt;
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
		if(amt > 200) amt = 200;
		this.maxShells += amt;
	}
	
	/**
	 * Returns the maximum of rockets that the player can
	 * handle.
	 * @return maximum of rockets
	 */
	public int getMaxRockets() { return maxRockets; }

	/**
	 * Sets a new maximum amount of rockets that the player
	 * can handle.
	 * @param amt amount of rockets
	 */
	public void setMaxRockets(int amt) {
		if(amt > 200) amt = 200;
		this.maxRockets += amt;
	}
	
	/**
	 * Returns the maximum of gas that the player can
	 * handle.
	 * @return maximum of gas
	 */
	public int getMaxGas() { return maxGas; }

	/**
	 * Sets a new maximum amount of gas that the player
	 * can handle.
	 * @param amt amount of gas
	 */
	public void setMaxGas(int amt) {
		if(amt > 100) amt = 100;
		this.maxGas += amt;
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

	/**
	 * Returns if the player haves the gold key.
	 * @return gold key
	 */
	public boolean isGoldkey() { return goldkey; }

	/**
	 * Sets the state of the gold key.
	 * @param goldkey to set
	 */
	public void setGoldkey(boolean goldkey) {
		this.goldkey = goldkey;
		playerText.get("Notification").setText("You've got the gold key!");
    	notificationTime = Time.getTime();
	}
	
	/**
	 * Returns if the player haves the bronze key.
	 * @return bronze key
	 */
	public boolean isBronzekey() { return bronzekey; }

	/**
	 * Sets the state of the bronze key.
	 * @param bronzekey to set
	 */
	public void setBronzekey(boolean bronzekey) {
		this.bronzekey = bronzekey;
		playerText.get("Notification").setText("You've got the bronze key!");
    	notificationTime = Time.getTime();
	}
	
	/**
	 * Returns the player's own speed.
	 * @return player's speed
	 */
	public float getSpeed() { return moveSpeed; }
	
	/**
	 * Returns the player's movement vector.
	 * @return movement vector
	 */
	public Vector3f getMovementVector() { return movementVector; }
	
	/**
	 * Returns the player's transform.
	 * @return player's transform
	 */
	public Transform getTransform() { return gunTransform; }
	
	/**
	 * Removes the rocket when disappears.
	 * @param rocket rocket.
	 */
	public void removeRocket(Rocket rocket) { removeRockets.add(rocket); }
	
	/**
	 * Removes the flame when disappears.
	 * @param flame flame.
	 */
	public void removeFlame(Flame flame) { removeFlames.add(flame); }

}