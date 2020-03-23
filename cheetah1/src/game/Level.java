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

import static engine.components.Constants.PARTICLES_LEVEL;
import static engine.core.CoreEngine.getRenderingEngine;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
import engine.components.BaseLight;
import engine.components.DirectionalLight;
import engine.components.GameComponent;
import engine.core.GameObject;
import engine.core.Input;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.physics.PhysicsUtil;
import engine.rendering.Bitmap;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Vertex;
import engine.core.utils.Log;
import engine.core.utils.Util;
import game.enemies.Captain;
import game.enemies.Commander;
import game.enemies.Dog;
import game.enemies.Ghost;
import game.enemies.NaziSergeant;
import game.enemies.NaziSoldier;
import game.enemies.SsSoldier;
import game.enemies.Zombie;
import game.objects.Barrel;
import game.objects.Bleed;
import game.objects.Bones;
import game.objects.Clock;
import game.objects.DeadJew;
import game.objects.Explosion;
import game.objects.Fire;
import game.objects.Grass;
import game.objects.Oven;
import game.objects.Hanged;
import game.objects.Kitchen;
import game.objects.Lamp;
import game.objects.Lantern;
import game.objects.LightPost;
import game.objects.Pendule;
import game.objects.Pillar;
import game.objects.Pipe;
import game.objects.Sign;
import game.objects.Table;
import game.objects.Tree;
import game.pickUps.Armor;
import game.pickUps.Bag;
import game.pickUps.Bullet;
import game.pickUps.Chaingun;
import game.pickUps.Food;
import game.pickUps.Helmet;
import game.pickUps.Key;
import game.pickUps.Machinegun;
import game.pickUps.Medkit;
import game.pickUps.RocketLauncher;
import game.pickUps.Shell;
import game.pickUps.Shotgun;
import game.pickUps.SuperShotgun;
import game.walls.BarsWall;
import game.walls.Door;
import game.walls.LockedDoor;
import game.walls.SecretWall;
import game.walls.Wall;
import game.pickUps.Rocket;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.7
 * @since 2017
 */
public class Level extends GameComponent {

	//Constants
    public static final float SPOT_WIDTH = 1f;
    public static final float SPOT_LENGTH = 1f;
    public static final float LEVEL_HEIGHT = 1f;

    private static final float NUM_TEX_X = 4f;
    private static final float NUM_TEX_Y = 4f;
    
    private static final float MELEE_RANGE = 0.55f;
    private static final float BULLET_RANGE = 2f;
    private static final float SHELL_RANGE = 3f;
    private static final float FLAME_RANGE = 6f;

    private static final String PLAYER_RES_LOC = "player/";
    
    //Player's sounds
    private static final Clip misuseNoise = AudioUtil.loadAudio(PLAYER_RES_LOC + "OOF");
    private static final Clip punchNoise = AudioUtil.loadAudio(PLAYER_RES_LOC + "PLSPNCH6");
    private static final Clip punchSolidNoise = AudioUtil.loadAudio(PLAYER_RES_LOC + "PUNCH2");
    private static final Clip barrelNoise = AudioUtil.loadAudio("barrel/BARRELZ");

    //Remove list
    private static ArrayList<Medkit> removeMedkitList;
    private static ArrayList<Food> removeFoodList;
    private static ArrayList<Bullet> removeBulletList;
    private static ArrayList<Shell> removeShellList;
    private static ArrayList<Bag> removeBagList;
    private static ArrayList<Shotgun> removeShotgunList;
    private static ArrayList<Machinegun> removeMachineGunList;
    private static ArrayList<Ghost> removeGhostList;
    private static ArrayList<Armor> removeArmorList;
    private static ArrayList<SuperShotgun> removeSuperShotgunList;
    private static ArrayList<Helmet> removeHelmets;
    private static ArrayList<Barrel> removeBarrels;
    private static ArrayList<Chaingun> removeChaingunList;
    private static ArrayList<Key> removeKeys;
    private static ArrayList<Explosion> removeExplosions;
    private static ArrayList<Rocket> removeRockets;
    private static ArrayList<RocketLauncher> removeRocketLauncherList;
    private static ArrayList<Bleed> removeBleedingList;
    private static ArrayList<Fire> removeFireList;
    
    //Player
    private static Player player;

    //Level brain
    private ArrayList<Vector3f> exitPoints;
    private ArrayList<Integer> exitOffsets;
    private ArrayList<Vector2f> collisionPosStart;
    private ArrayList<Vector2f> collisionPosEnd;
    
    //Walls
    private ArrayList<Door> doors;
    private ArrayList<SecretWall> secretWalls;
    private ArrayList<LockedDoor> lockedDoors;
    private ArrayList<Wall> walls;
    private ArrayList<BarsWall> barsWalls;
    
    //Pick-Ups
    private ArrayList<Shotgun> shotguns;
    private ArrayList<Medkit> medkits;
    private ArrayList<Food> foods;
    private ArrayList<Bullet> bullets;
    private ArrayList<Shell> shells;
    private ArrayList<Bag> bags;
    private ArrayList<Machinegun> machineguns;
    private ArrayList<Rocket> rockets;
    private ArrayList<Armor> armors;
    private ArrayList<SuperShotgun> superShotguns;
    private ArrayList<Helmet> helmets;
    private ArrayList<Chaingun> chainguns;
    private ArrayList<Key> keys;
    private ArrayList<RocketLauncher> rocketLaunchers;
    
    //Static objects
    private ArrayList<Tree> trees;
    private ArrayList<Lantern> flares;
    private ArrayList<Bones> bones;
    private ArrayList<NaziSoldier> deadNazi;
    private ArrayList<DeadJew> deadJews;
    private ArrayList<Table> tables;
    private ArrayList<Pipe> pipes;
    private ArrayList<Pendule> pendules;
    private ArrayList<Lamp> lamps;
    private ArrayList<Hanged> hangeds;
    private ArrayList<Pillar> pillars;
    private ArrayList<Clock> clocks;
    private ArrayList<Oven> furnaces;
    private ArrayList<Kitchen> kitchens;
    private ArrayList<Barrel> barrels;
    private ArrayList<Grass> grass;
    private ArrayList<LightPost> lightPosts;
    private ArrayList<Sign> signs;
    
    //Active objects
    @SuppressWarnings("unused")
	private ArrayList<Explosion> explosions;
    private ArrayList<Bleed> bleeding;
    private ArrayList<Fire> fire;
   
    //Enemies
    private ArrayList<NaziSoldier> naziSoldiers;
    private ArrayList<Dog> dogs;
    private ArrayList<SsSoldier> ssSoldiers;
    private ArrayList<NaziSergeant> naziSeargeants;
    private ArrayList<Ghost> ghosts;
    private ArrayList<Zombie> zombies;
    private ArrayList<Captain> captains;
    private ArrayList<Commander> commanders;

    //Level
    private Bitmap bitmap;
    private Material material;
    private RenderingEngine renderingEngine;
    private BaseLight directionalLight;
    private GameObject objects;
    private GameComponent shootingObjective;
    
	private boolean dayTransition = false;
	private float dayLightValue;

    /**
     * Constructor of the level in the game.
     * @param bitmap to load and use.
     * @param material to load and use.
     */
    public Level(Bitmap bitmap, Material material) {	
        this.objects = new GameObject();
        
        //Level stuff
        this.bitmap = bitmap;
        this.material = material;
        this.collisionPosStart = new ArrayList<Vector2f>();
        this.collisionPosEnd = new ArrayList<Vector2f>();
    	this.renderingEngine = getRenderingEngine();
        
        generateLevel();
        
        //Player
        objects.add(player);
        //Enemies
        objects.add(naziSoldiers);
        objects.add(ssSoldiers);
        objects.add(naziSeargeants);
        objects.add(dogs);
        objects.add(ghosts);
        objects.add(zombies);
        objects.add(captains);
        objects.add(commanders);
        //Objects
        objects.add(doors);
        objects.add(barsWalls);
        objects.add(walls);
        objects.add(secretWalls);
        objects.add(trees);
        objects.add(flares);
        objects.add(bones);
        objects.add(tables);
        objects.add(deadNazi);
        objects.add(deadJews);
        objects.add(pipes);
        objects.add(pendules);
        objects.add(lamps);
        objects.add(hangeds);
        objects.add(pillars);
        objects.add(clocks);
        objects.add(furnaces);
        objects.add(kitchens);
        objects.add(barrels);
        objects.add(lockedDoors);
        objects.add(grass);
        objects.add(lightPosts);
        objects.add(signs);
        //Power-ups
        objects.add(medkits);
        objects.add(foods);
        objects.add(bullets);
        objects.add(shells);
        objects.add(bags);
        objects.add(shotguns);
        objects.add(machineguns);
        objects.add(chainguns);
        objects.add(armors);
        objects.add(helmets);
        objects.add(superShotguns); 
        objects.add(keys);
        objects.add(rocketLaunchers);
        objects.add(rockets);
        
        renderingEngine.setMainCamera(player.getCamera());
    }
    
    /**
     * Cleans everything in the CPU.
     */
    @Override
    protected void finalize() { objects.clearMemory(); }

    /**
     * Inputs accessible in the level.
     */
	public void input() {
		double time = Time.getTime();
    	double timeDecimals = (time - (double) ((int) time));
    	
		if (!player.isAlive) {
	        if(Input.getKeyDown(Input.KEY_E)) {
	    		if (timeDecimals <= 5.0f) {
		            Auschwitz.reloadLevel();
		            player.gotPistol();
	    		}
	        }
        }

        if ((player.fires && !player.isReloading) || (Input.getKeyDown(Input.KEY_Q) && !player.trowsKick)) {
        	
        	checkDamage(naziSoldiers, punchNoise, 1);
        	checkDamage(dogs, punchNoise, 1);
        	checkDamage(ssSoldiers, punchNoise, 1);
        	checkDamage(naziSeargeants, punchNoise, 1);
        	checkDamage(ghosts, null, 255);
        	checkDamage(zombies, punchNoise, 1);
        	checkDamage(captains, punchSolidNoise, 1);
        	checkDamage(commanders, punchSolidNoise, 1);
        	checkDamage(lamps, punchSolidNoise, 1);
        	checkDamage(pillars, punchSolidNoise, 1);
        	checkDamage(barrels, barrelNoise, 3);
        	checkDamage(hangeds, punchNoise, 1);
        	checkDamage(doors, punchSolidNoise, 69);
        	checkDamage(pipes, punchSolidNoise, 69);
        	checkDamage(tables, punchSolidNoise, 69);
        	checkDamage(clocks, punchSolidNoise, 69);
        	checkDamage(furnaces, punchSolidNoise, 69);
        	checkDamage(kitchens, punchSolidNoise, 69);
        	checkDamage(lockedDoors, punchSolidNoise, 69);
        	checkDamage(barsWalls, punchSolidNoise, 69);
        	checkDamage(trees, punchSolidNoise, 69);
        	checkDamage(lightPosts, punchSolidNoise, 69);
        	checkDamage(signs, punchSolidNoise, 69);
        }

        player.input();
    }

    /**
     * Updates everything rendered in the level.
     * @param delta of time
     */
    public void update(double delta) {
        
    	objects.update(delta);

        objects.killList(deadNazi, delta);
        
        objects.removeComponents(removeMedkitList);
        objects.removeComponents(removeFoodList);
        objects.removeComponents(removeBulletList);
        objects.removeComponents(removeShellList);
        objects.removeComponents(removeBagList);
        objects.removeComponents(removeShotgunList);
        objects.removeComponents(removeMachineGunList);
        objects.removeComponents(removeGhostList);
        objects.removeComponents(removeArmorList);
        objects.removeComponents(removeHelmets);
        objects.removeComponents(removeSuperShotgunList);
        objects.removeComponents(removeBarrels);
        objects.removeComponents(removeChaingunList);
        objects.removeComponents(removeKeys);
        objects.removeComponents(removeExplosions);
        objects.removeComponents(removeRockets);
        objects.removeComponents(removeRocketLauncherList);
        objects.removeComponents(removeBleedingList);
        objects.removeComponents(removeFireList);
        
        objects.sortNumberComponents(secretWalls);
   		objects.sortNumberComponents(naziSoldiers);
   		objects.sortNumberComponents(dogs);
   		objects.sortNumberComponents(ssSoldiers);
   		objects.sortNumberComponents(naziSeargeants);
   		objects.sortNumberComponents(zombies);
   		objects.sortNumberComponents(captains);
   		objects.sortNumberComponents(commanders);
        
        if(dayTransition) {
        	float oscillate = (float) Math.sin(Time.getTime() * 0.0035f * (2 * Math.PI));
        	dayLightValue += oscillate/500;
        	renderingEngine.setFogDensity(0.0035f);
            renderingEngine.setFogGradient(5.0f);
            if(dayLightValue >= 1.25f)
            	dayLightValue = 1.25f;
            else if(dayLightValue <= 0.1f)
            	dayLightValue = 0.1f;
        	renderingEngine.setAmbientLight(new Vector3f(dayLightValue, dayLightValue, dayLightValue));
        	renderingEngine.setFogColor(new Vector3f(dayLightValue/20, dayLightValue/2, dayLightValue));
        }
        
        removeMedkitList.clear();
        removeFoodList.clear();
        removeBulletList.clear();
        removeShellList.clear();
        removeBagList.clear();
        removeShotgunList.clear();
        removeMachineGunList.clear();
        removeGhostList.clear();
        removeArmorList.clear();
        removeSuperShotgunList.clear();
        removeHelmets.clear();
        removeBarrels.clear();
        removeChaingunList.clear();
        removeKeys.clear();
        removeExplosions.clear();
        removeRockets.clear();
        removeRocketLauncherList.clear();
        removeBleedingList.clear();
        removeFireList.clear();
    }

    /**
     * Renders everything in the level.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) { objects.render(shader, renderingEngine); }
    
    /**
     * Method that opens the accessible doors in the level.
     * @param position coordinates.
     * @param playSound If can play a sound.
     */
    public void openDoors(Vector3f position, boolean playSound) {
        boolean worked = false;

        for (Door door : doors) {
            if (Math.abs(door.getTransform().getPosition().sub(position).length()) < 1f) {
                worked = true;
                door.open(0.5f, 3f);
            }
        }
        
        for (LockedDoor lockedDoor : lockedDoors) {
        	if(lockedDoor.isGoldKey) {
	            if (Math.abs(lockedDoor.getTransform().getPosition().sub(position).length()) < 1f) {
	            	if(player.isGoldkey()) {
	            		worked = true;
	                	lockedDoor.open(0.5f, 3f);
	            	} else {
	            		player.playerText.get("Notification").setText("You need to find the gold key");
		                player.notificationTime = Time.getTime();
	            	}
	            }
        	} else {
        		if (Math.abs(lockedDoor.getTransform().getPosition().sub(position).length()) < 1f) {
	                if(player.isBronzekey()) {
	                	worked = true;
	                	lockedDoor.open(0.5f, 3f);
        			}else {
		            	player.playerText.get("Notification").setText("You need to find the bronze key");
		                player.notificationTime = Time.getTime();
		            }
	            }
        	}
        }
        
        for (SecretWall secretWall : secretWalls) {
        		if (Math.abs(secretWall.getTransform().getPosition().sub(position).length()) < 1f) {
                worked = true;
                secretWall.open(1.0f, 3f);
                player.playerText.get("Notification").setText("You've found a secret!");
                player.notificationTime = Time.getTime();
            }
        }

        if (playSound) {
            for (int i = 0; i < exitPoints.size(); i++) {
                if (Math.abs(exitPoints.get(i).sub(position).length()) < 1f) {
                	Auschwitz.loadLevel(exitOffsets.get(i), true);
                } else if (Math.abs(exitPoints.get(i).sub(position).length()) < 1.25f) {
                	player.playerText.get("Notification").setText("Press e to scape to other floor");
                    player.notificationTime = Time.getTime();
                }
            }
        }

        if (!worked && playSound) {
        	if(player.getMovementVector().length() <= 0.33f)
            	AudioUtil.playAudio(misuseNoise, 0);
        }
    }

    /**
     * Method that checks the collisions between all the level objects, the level
     * itself and vice-versa.
     * @param oldPos Objects' old position.
     * @param newPos Objects' new position.
     * @param objectWidth Objects' width.
     * @param objectLength Objects' length.
     * @return Collision's vector.
     */
    public Vector3f checkCollisions(Vector3f oldPos, Vector3f newPos, float objectWidth, float objectLength) {
        Vector2f collisionVector = new Vector2f(1, 1);
        Vector3f movementVector = newPos.sub(oldPos);

        if (movementVector.length() > 0) {
            Vector2f blockSize = new Vector2f(SPOT_WIDTH, SPOT_LENGTH);
            Vector2f objectSize = new Vector2f(objectWidth, objectLength);

            Vector2f oldPos2 = new Vector2f(oldPos.getX(), oldPos.getZ());
            Vector2f newPos2 = new Vector2f(newPos.getX(), newPos.getZ());

            for (int i = 0; i < bitmap.getWidth(); i++) {
                for (int j = 0; j < bitmap.getHeight(); j++) {
                    if ((bitmap.getPixel(i, j) & 0xFFFFFF) == 0) // If it's a black (wall) pixel
                    {
                        collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, blockSize.mul(new Vector2f(i, j)), blockSize));
                    }
                }
            }

            for (Door door : doors)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, door.getTransform().getPosition().getXZ(), door.getSize()));
            
            for (SecretWall secretWall : secretWalls)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, secretWall.getTransform().getPosition().getXZ(), secretWall.getSize()));
            
            for (NaziSoldier monster : naziSoldiers)
            	if(monster.isQuiet)
            		collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, monster.getTransform().getPosition().getXZ(), monster.getSize()));
            
            for (SsSoldier ssSoldier : ssSoldiers)
            	if(ssSoldier.isQuiet)
            		collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, ssSoldier.getTransform().getPosition().getXZ(), ssSoldier.getSize()));
            
            for (Dog dog : dogs)
            	if(dog.isQuiet)
            		collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, dog.getTransform().getPosition().getXZ(), dog.getSize()));
            
            for (NaziSergeant naziSergeants : naziSeargeants)
            	if(naziSergeants.isQuiet)
            		collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, naziSergeants.getTransform().getPosition().getXZ(), naziSergeants.getSize()));
           
            for (Bones bone : bones)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, bone.getTransform().getPosition().getXZ(), bone.getSize()));
            
            for (DeadJew deadJew : deadJews)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, deadJew.getTransform().getPosition().getXZ(), deadJew.getSize()));
            
            for (Tree tree : trees)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, tree.getTransform().getPosition().getXZ(), tree.getSize()));
            
            for (Table table : tables)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, table.getTransform().getPosition().getXZ(), table.getSize()));
            
            for (Pipe pipe : pipes)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, pipe.getTransform().getPosition().getXZ(), pipe.getSize()));
            
            for (Pendule pendule : pendules)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, pendule.getTransform().getPosition().getXZ(), pendule.getSize()));
            
            //for (Lantern flare : flares)
                //collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, flare.getTransform().getPosition().getXZ(), flare.getSize()));
            
            for (Lamp lamp : lamps)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, lamp.getTransform().getPosition().getXZ(), lamp.getSize()));
            
            for (Hanged jew : hangeds)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, jew.getTransform().getPosition().getXZ(), jew.getSize()));
            
            for (Pillar pillar : pillars)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, pillar.getTransform().getPosition().getXZ(), pillar.getSize()));
            
            for (Clock clock : clocks)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, clock.getTransform().getPosition().getXZ(), clock.getSize()));
            
            for (Oven furnace : furnaces)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, furnace.getTransform().getPosition().getXZ(), furnace.getSize()));
            
            for (Barrel barrel : barrels)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, barrel.getTransform().getPosition().getXZ(), barrel.getSize()));
            
            for (LockedDoor lockedDoor : lockedDoors)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, lockedDoor.getTransform().getPosition().getXZ(), lockedDoor.getSize()));
            
            for (BarsWall barsWall : barsWalls)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, barsWall.getTransform().getPosition().getXZ(), barsWall.getSize()));
            
            for (LightPost lightPost : lightPosts)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, lightPost.getTransform().getPosition().getXZ(), lightPost.getSize()));
            
            for (Sign sign : signs)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, sign.getTransform().getPosition().getXZ(), sign.getSize()));
            
            for (Zombie zombie : zombies)
            	if(zombie.isQuiet)
            		collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, zombie.getTransform().getPosition().getXZ(), zombie.getSize()));
            
            for (Captain captain : captains)
            	if(captain.isQuiet)
            		collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, captain.getTransform().getPosition().getXZ(), captain.getSize()));
            
            for (Commander commander : commanders)
            	if(commander.isQuiet)
            		collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, commander.getTransform().getPosition().getXZ(), commander.getSize()));
            
        }

        return new Vector3f(collisionVector.getX(), 0, collisionVector.getY());
    }

    /**
     * Checks if there's any intersections between the objects.
     * @param lineStart The start position of the line-checker.
     * @param lineEnd The end position of the line-checker.
     * @param hurtMonsters If which intersection could hurt something.
     * @return Nearest Intersection.
     */
    public Vector2f checkIntersections(Vector2f lineStart, Vector2f lineEnd, boolean hurtMonsters) {
        Vector2f nearestIntersect = null;

        for (int i = 0; i < collisionPosStart.size(); i++) {
            Vector2f collision = PhysicsUtil.lineIntersect(lineStart, lineEnd, collisionPosStart.get(i), collisionPosEnd.get(i));

            if (collision != null && (nearestIntersect == null
                    || nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                nearestIntersect = collision;
            }
        }

        for (Door door : doors) {
            Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, door.getTransform().getPosition().getXZ(), door.getSize());

            if (collision != null && (nearestIntersect == null
                    || nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                nearestIntersect = collision;
            }
        }
        
        for (SecretWall secretWall : secretWalls) {
            Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, secretWall.getTransform().getPosition().getXZ(), secretWall.getSize());

            if (collision != null && (nearestIntersect == null
                    || nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                nearestIntersect = collision;
            }
        }
        
        for (LockedDoor lockedDoor : lockedDoors) {
            Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, lockedDoor.getTransform().getPosition().getXZ(), lockedDoor.getSize());

            if (collision != null && (nearestIntersect == null
                    || nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                nearestIntersect = collision;
            }
        }
        
        for (BarsWall barsWall : barsWalls) {
            Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, barsWall.getTransform().getPosition().getXZ(), barsWall.getSize());

            if (collision != null && (nearestIntersect == null
                    || nearestIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                nearestIntersect = collision;
            }
        }

        if (hurtMonsters) {
            Vector2f naziIntersect = null;
            NaziSoldier nearestNazi = null;
            
            Vector2f dogIntersect = null;
            Dog nearestDog = null;
            
            Vector2f ssSoldierIntersect = null;
            SsSoldier nearestSsSoldier = null;
            
            Vector2f naziSergeantsIntersect = null;
            NaziSergeant nearestNaziSargent = null;
            
            Vector2f ghostIntersect = null;
            Ghost nearestGhost = null;
            
            Vector2f zombieIntersect = null;
            Zombie nearestZombie = null;
            
            Vector2f captainIntersect = null;
            Captain nearestCaptain = null;
            
            Vector2f commanderIntersect = null;
            Commander nearestCommander = null;

            for (NaziSoldier naziSoldier : naziSoldiers) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, naziSoldier.getTransform().getPosition().getXZ(), naziSoldier.getSize());

                if (collision != null && (naziIntersect == null
                        || naziIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                    naziIntersect = collision;
                    nearestNazi = naziSoldier;
                }
                
            }
            
            for (Dog dog : dogs) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, dog.getTransform().getPosition().getXZ(), dog.getSize());

                if (collision != null && (dogIntersect == null
                        || dogIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	dogIntersect = collision;
                    nearestDog = dog;
                }
            }
            
            for (SsSoldier ssSoldier : ssSoldiers) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, ssSoldier.getTransform().getPosition().getXZ(), ssSoldier.getSize());

                if (collision != null && (ssSoldierIntersect == null
                        || ssSoldierIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	ssSoldierIntersect = collision;
                	nearestSsSoldier = ssSoldier;
                }
            }
            
            for (NaziSergeant naziSargent : naziSeargeants) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, naziSargent.getTransform().getPosition().getXZ(), naziSargent.getSize());

                if (collision != null && (naziSergeantsIntersect == null
                        || naziSergeantsIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	naziSergeantsIntersect = collision;
                	nearestNaziSargent = naziSargent;
                }
            }
            
            for (Ghost ghost : ghosts) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, ghost.getTransform().getPosition().getXZ(), ghost.getSize());

                if (collision != null && (ghostIntersect == null
                        || ghostIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	ghostIntersect = collision;
                	nearestGhost = ghost;
                }
            }
            
            for (Zombie zombie : zombies) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, zombie.getTransform().getPosition().getXZ(), zombie.getSize());

                if (collision != null && (zombieIntersect == null
                        || zombieIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	zombieIntersect = collision;
                	nearestZombie= zombie;
                }
            }
            
            for (Captain captain : captains) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, captain.getTransform().getPosition().getXZ(), captain.getSize());

                if (collision != null && (captainIntersect == null
                        || captainIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	captainIntersect = collision;
                	nearestCaptain= captain;
                }
            }
            
            for (Commander commander : commanders) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, commander.getTransform().getPosition().getXZ(), commander.getSize());

                if (collision != null && (commanderIntersect == null
                        || commanderIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                	commanderIntersect = collision;
                	nearestCommander= commander;
                }
            }

            if((player.weaponType == player.BULLET && player.getWeaponState() != "chaingun" && player.getBullets() > 0) || player.weaponType == player.SHELL && player.getShells() > 0 || (player.weaponType == player.BULLET && player.chaingunCanFire && player.getWeaponState() == "chaingun" && player.getBullets() > 0)) {
	            if (naziIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > naziIntersect.sub(lineStart).length())) {
	                nearestNazi.damage(player.getDamage());
	                addBleeding(nearestNazi);
	            }
	            
	            if (dogIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > dogIntersect.sub(lineStart).length())) {
	            	nearestDog.damage(player.getDamage());
	            	addBleeding(nearestDog);
	            }
	            
	            if (ssSoldierIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > ssSoldierIntersect.sub(lineStart).length())) {
	            	nearestSsSoldier.damage(player.getDamage());
	            	addBleeding(nearestSsSoldier);
	            }
	            if (naziSergeantsIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > naziSergeantsIntersect.sub(lineStart).length())) {
	            	nearestNaziSargent.damage(player.getDamage());
	            	addBleeding(nearestNaziSargent);
	            }
	            
	            if (ghostIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > ghostIntersect.sub(lineStart).length())) {
	            	nearestGhost.damage(player.getDamage());
	            }
	            if (zombieIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > zombieIntersect.sub(lineStart).length())) {
	            	nearestZombie.damage(player.getDamage());
	            }
	            if (captainIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > captainIntersect.sub(lineStart).length())) {
	            	nearestCaptain.damage(player.getDamage());
	            	addBleeding(nearestCaptain);
	            }
	            if (commanderIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > commanderIntersect.sub(lineStart).length())) {
	            	nearestCommander.damage(player.getDamage());
	            	addBleeding(nearestCommander);
	            }
        	}
            if(player.weaponType == player.ROCKET || player.weaponType == player.GAS) {
	            if (naziIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > naziIntersect.sub(lineStart).length())) {
	            	setShootingObjective(nearestNazi);
	            	if(player.weaponType == player.GAS && player.getGas() > 0 && nearestNazi.getDistance() < FLAME_RANGE)
	            		addFire(nearestNazi, false);
	            }
	            
	            if (dogIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > dogIntersect.sub(lineStart).length())) {
	            	setShootingObjective(nearestDog);
	            	if(player.weaponType == player.GAS && player.getGas() > 0 && nearestDog.getDistance() < FLAME_RANGE)
	            		addFire(nearestDog, false);
	            }
	            
	            if (ssSoldierIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > ssSoldierIntersect.sub(lineStart).length())) {
	            	setShootingObjective(nearestSsSoldier);
	            	if(player.weaponType == player.GAS && player.getGas() > 0 && nearestSsSoldier.getDistance() < FLAME_RANGE)
	            		addFire(nearestSsSoldier, false);
	            }
	            if (naziSergeantsIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > naziSergeantsIntersect.sub(lineStart).length())) {
	            	setShootingObjective(nearestNaziSargent);
	            	if(player.weaponType == player.GAS && player.getGas() > 0 && nearestNaziSargent.getDistance() < FLAME_RANGE)
	            		addFire(nearestNaziSargent, false);
	            }
	            
	            if (ghostIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > ghostIntersect.sub(lineStart).length())) {
	            	setShootingObjective(nearestGhost);
	            }
	            if (zombieIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > zombieIntersect.sub(lineStart).length())) {
	            	setShootingObjective(nearestZombie);
	            	if(player.weaponType == player.GAS && player.getGas() > 0 && nearestZombie.getDistance() < FLAME_RANGE)
	            		addFire(nearestZombie, false);
	            }
	            if (captainIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > captainIntersect.sub(lineStart).length())) {
	            	setShootingObjective(nearestCaptain);
	            	if(player.weaponType == player.GAS && player.getGas() > 0 && nearestCaptain.getDistance() < FLAME_RANGE)
	            		addFire(nearestCaptain, false);
	            }         
	            if (commanderIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > commanderIntersect.sub(lineStart).length())) {
	            	setShootingObjective(nearestCommander);
	            	if(player.weaponType == player.GAS && player.getGas() > 0 && nearestCommander.getDistance() < FLAME_RANGE)
	            		addFire(nearestCommander, false);
	            }
        	}
            if(player.weaponType == player.MELEE) {
        		if (naziIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ naziIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	                nearestNazi.damage(player.getMeleeDamage());
	            }
	            
	            if (dogIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ dogIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestDog.damage(player.getMeleeDamage());
	            }
	            
	            if (ssSoldierIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ ssSoldierIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestSsSoldier.damage(player.getMeleeDamage());
	            }
	            
	            if (naziSergeantsIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ naziSergeantsIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestNaziSargent.damage(player.getMeleeDamage());
	            }
	            
	            if (ghostIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ ghostIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestGhost.damage(player.getMeleeDamage());
	            }
	            
	            if (zombieIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ zombieIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestZombie.damage(player.getMeleeDamage());
	            }
	            
	            if (captainIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ captainIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestCaptain.damage(player.getMeleeDamage());
	            }
	            
	            if (commanderIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ commanderIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestCommander.damage(player.getMeleeDamage());
	            }
        	}
            
            for (Barrel barrel : barrels) {
                if(barrel.kBooms) {
                    if (naziIntersect != null && (nearestIntersect == null
                                                     || nearestIntersect.sub(lineStart).length() > naziIntersect.sub(lineStart).length())) {
                        nearestNazi.damage(barrel.damage);
                        addBleeding(nearestNazi);
    	            	addFire(nearestNazi, false);
                    }
                    
                    if (dogIntersect != null && (nearestIntersect == null
                                                 || nearestIntersect.sub(lineStart).length() > dogIntersect.sub(lineStart).length())) {
                        nearestDog.damage(barrel.damage);
                        addBleeding(nearestDog);
                        addFire(nearestDog, false);
                    }
                    
                    if (ssSoldierIntersect != null && (nearestIntersect == null
                                                       || nearestIntersect.sub(lineStart).length() > ssSoldierIntersect.sub(lineStart).length())) {
                        nearestSsSoldier.damage(barrel.damage);
                        addBleeding(nearestSsSoldier);
                        addFire(nearestSsSoldier, false);
                    }
                    
                    if (naziSergeantsIntersect != null && (nearestIntersect == null
                                                           || nearestIntersect.sub(lineStart).length() > naziSergeantsIntersect.sub(lineStart).length())) {
                        nearestNaziSargent.damage(barrel.damage);
                        addBleeding(nearestNaziSargent);
                        addFire(nearestNaziSargent, false);
                    }
                    
                    if (zombieIntersect != null && (nearestIntersect == null
    	                    || nearestIntersect.sub(lineStart).length() > zombieIntersect.sub(lineStart).length())) {
    	            	nearestZombie.damage(barrel.damage);
    	            	addFire(nearestZombie, false);
    	            }
                    
                    if (captainIntersect != null && (nearestIntersect == null
    	                    || nearestIntersect.sub(lineStart).length() > captainIntersect.sub(lineStart).length())) {
    	            	nearestCaptain.damage(barrel.damage);
    	            	addBleeding(nearestCaptain);
    	            	addFire(nearestCaptain, false);
    	            }
                    
                    if (commanderIntersect != null && (nearestIntersect == null
    	                    || nearestIntersect.sub(lineStart).length() > commanderIntersect.sub(lineStart).length())) {
    	            	nearestCommander.damage(barrel.damage);
    	            	addBleeding(nearestCommander);
    	            	addFire(nearestCommander, false);
    	            }
                }
            }
        }

        return nearestIntersect;
    }
    
    /**
     * Adds a face for the level's mesh.
     * @param indices of the mesh.
     * @param startLocation of the mesh.
     * @param direction of face.
     */
    private void addFace(ArrayList<Integer> indices, int startLocation, boolean direction) {
		if(direction) {
			indices.add(startLocation + 2);
			indices.add(startLocation + 1);
			indices.add(startLocation + 0);
			indices.add(startLocation + 3);
			indices.add(startLocation + 2);
			indices.add(startLocation + 0);
		} else {
			indices.add(startLocation + 0);
			indices.add(startLocation + 1);
			indices.add(startLocation + 2);
			indices.add(startLocation + 0);
			indices.add(startLocation + 2);
			indices.add(startLocation + 3);
		}
	}
    
    /**
     * Calculates the texture's coordinates of
     * the level's mesh.
     * @param value of the coordinates.
     * @return Texture's coordinates.
     */
    private float[] calcTextCoords(int value) {
    	int texX = value / 16;
        int texY = texX % 4;
        texX /= 4;
        
        float[] result = new float[4];

        result[0] = 1f - texX / NUM_TEX_X;
        result[1] = result[0] - 1 / NUM_TEX_X;
        result[2] = 1f - texY / NUM_TEX_Y;
        result[3] = result[2] - 1 / NUM_TEX_Y;
        
    	return result;
    }
    
    /**
     * Adds a vertex for the level's mesh.
     * @param vertices of the mesh
     * @param i pixel in bitmap.
     * @param j pixel in bitmap.
     * @param offset of height.
     * @param x position.
     * @param y position.
     * @param z position.
     * @param texCoords of the mesh.
     */
    private void addVertices(ArrayList<Vertex> vertices, int i, int j, float offset, boolean x, boolean y, boolean z, float[] texCoords) {
		if(x && z)
		{
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, offset * LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[2])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, offset * LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[2])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, offset * LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[3])));
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, offset * LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[3])));
		}
		else if(x && y)
		{
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, j * LEVEL_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[2])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, j * LEVEL_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[2])));
			vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, (j + 1) * LEVEL_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[3])));
			vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, (j + 1) * LEVEL_HEIGHT, offset * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[3])));
		}
		else if(y && z)
		{
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, i * LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[2])));
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, i * LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[2])));
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, (i + 1) * LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(texCoords[1],texCoords[3])));
			vertices.add(new Vertex(new Vector3f(offset * SPOT_WIDTH, (i + 1) * LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(texCoords[0],texCoords[3])));
		}
		else
		{
			Log.fatal("Invalid plane used in level generator");
		}
	}

    /**
     * Method that generates the level for the game.
     * @param engine of the level.
     */
    private void generateLevel() {
        
        //Remove list
    	Level.removeMedkitList = new ArrayList<Medkit>();
    	Level.removeFoodList = new ArrayList<Food>();
    	Level.removeBulletList = new ArrayList<Bullet>();
    	Level.removeShellList = new ArrayList<Shell>();
    	Level.removeBagList = new ArrayList<Bag>();
    	Level.removeShotgunList = new ArrayList<Shotgun>();
    	Level.removeMachineGunList = new ArrayList<Machinegun>();
    	Level.removeGhostList = new ArrayList<Ghost>();
    	Level.removeArmorList = new ArrayList<Armor>();
    	Level.removeSuperShotgunList = new ArrayList<SuperShotgun>();
    	Level.removeHelmets = new ArrayList<Helmet>();
    	Level.removeBarrels = new ArrayList<Barrel>();
    	Level.removeKeys = new ArrayList<Key>();
    	Level.removeChaingunList = new ArrayList<Chaingun>();
    	Level.removeExplosions = new ArrayList<Explosion>();
    	Level.removeRockets = new ArrayList<Rocket>();
    	Level.removeRocketLauncherList = new ArrayList<RocketLauncher>();
    	Level.removeBleedingList = new ArrayList<Bleed>();
    	Level.removeFireList = new ArrayList<Fire>();
        //Doors and stuff
        this.doors = new ArrayList<Door>();
        this.lockedDoors = new ArrayList<LockedDoor>();
        this.secretWalls = new ArrayList<SecretWall>();
        this.exitOffsets = new ArrayList<Integer>();
        this.exitPoints = new ArrayList<Vector3f>();
        //Enemies
        this.naziSoldiers = new ArrayList<NaziSoldier>();
        this.dogs = new ArrayList<Dog>();
        this.ssSoldiers = new ArrayList<SsSoldier>();
        this.naziSeargeants = new ArrayList<NaziSergeant>();
        this.ghosts = new ArrayList<Ghost>();
        this.zombies = new ArrayList<Zombie>();
        this.captains = new ArrayList<Captain>();
        this.commanders = new ArrayList<Commander>();
        //Power-ups
        this.medkits = new ArrayList<Medkit>();
        this.foods = new ArrayList<Food>();
        this.bullets = new ArrayList<Bullet>();
        this.shells = new ArrayList<Shell>();
        this.shotguns = new ArrayList<Shotgun>();
        this.bags = new ArrayList<Bag>();
        this.machineguns = new ArrayList<Machinegun>();
        this.armors = new ArrayList<Armor>();
        this.superShotguns = new ArrayList<SuperShotgun>();
        this.helmets = new ArrayList<Helmet>();
        this.chainguns = new ArrayList<Chaingun>();
        this.keys = new ArrayList<Key>();
        this.rocketLaunchers = new ArrayList<RocketLauncher>();
        //Objects
        this.trees = new ArrayList<Tree>();
        this.flares = new ArrayList<Lantern>();
        this.bones = new ArrayList<Bones>();
        this.deadNazi = new ArrayList<NaziSoldier>();
        this.pipes = new ArrayList<Pipe>();
        this.pendules = new ArrayList<Pendule>();
        this.lamps = new ArrayList<Lamp>();
        this.hangeds = new ArrayList<Hanged>();
        this.deadJews = new ArrayList<DeadJew>();
        this.tables = new ArrayList<Table>();
        this.pillars = new ArrayList<Pillar>();
        this.clocks = new ArrayList<Clock>();
        this.furnaces = new ArrayList<Oven>();
        this.kitchens = new ArrayList<Kitchen>();
        this.barrels = new ArrayList<Barrel>();
        this.explosions = new ArrayList<Explosion>();
        this.rockets = new ArrayList<Rocket>();
        this.bleeding = new ArrayList<Bleed>();
        this.fire = new ArrayList<Fire>();
        this.walls = new ArrayList<Wall>();
        this.barsWalls = new ArrayList<BarsWall>();
        this.grass = new ArrayList<Grass>();
        this.lightPosts = new ArrayList<LightPost>();
        this.signs = new ArrayList<Sign>();

        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    	ArrayList<Integer> indices = new ArrayList<Integer>();
        
        for (int i = 1; i < bitmap.getWidth() - 1; i++) {
            for (int j = 1; j < bitmap.getHeight() - 1; j++) {
                if ((bitmap.getPixel(i, j) & 0xFFFFFF) == 0) // If it isn't a black (wall) pixel
                	continue;
                	//LEVEL_HEIGHT = level.getPixel(i, j) & 0x0000FF;
                    if ((bitmap.getPixel(i, j) & 0x0000FF) == 16) {
                        Transform doorTransform = new Transform();

                        boolean xDoor = (bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0;
                        boolean yDoor = (bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0;

                        if ((yDoor && xDoor) || !(yDoor || xDoor)) {
                            Log.fatal("Level Generation Error at (" + i + ", " + j + "): Doors must be between two solid walls.");
                        }

                        if (yDoor) {
                            doorTransform.setPosition(i, 0,j + SPOT_LENGTH / 2);
                            doors.add(new Door(doorTransform, material, doorTransform.getPosition().add(new Vector3f(-0.9f, 0, 0))));
                        } else if (xDoor) {
                            doorTransform.setPosition(i + SPOT_LENGTH / 2, 0, j);
                            doorTransform.setRotation(0, 90, 0);
                            doors.add(new Door(doorTransform, material, doorTransform.getPosition().add(new Vector3f(0, 0, -0.9f))));
                        }
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 20) {
                        Transform wallTransform = new Transform();

                        boolean xSecretWall = (bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0;
                        boolean ySecretWall = (bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0;

                        if ((ySecretWall && xSecretWall) || !(ySecretWall || xSecretWall)) {
                            Log.fatal("Level Generation Error at (" + i + ", " + j + "): Secret Walls must be between two solid walls.");
                        }

                        if (ySecretWall) {
                            wallTransform.setPosition(i, 0, j + SPOT_LENGTH);
                            secretWalls.add(new SecretWall(wallTransform, material, wallTransform.getPosition().add(new Vector3f(-1.0f, 0, -0.01f))));
                        } else if (xSecretWall) {
                            wallTransform.setPosition(i + SPOT_LENGTH, 0, j);
                            wallTransform.setRotation(0, 90, 0);
                            secretWalls.add(new SecretWall(wallTransform, material, wallTransform.getPosition().add(new Vector3f(-0.01f, 0, -1.0f))));
                        }
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 24) {
                    	//Gold
                        Transform lockedTransform = new Transform();

                        boolean xLockedDoor = (bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0;
                        boolean yLockedDoor = (bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0;

                        if ((yLockedDoor && xLockedDoor) || !(yLockedDoor || xLockedDoor)) {
                           Log.fatal("Level Generation Error at (" + i + ", " + j + "): Gold Locked Doors must be between two solid walls.");
                        }

                        if (yLockedDoor) {
                        	lockedTransform.setPosition(i, 0,j + SPOT_LENGTH / 2);
                            lockedDoors.add(new LockedDoor(lockedTransform, lockedTransform.getPosition().add(new Vector3f(-0.9f, 0, 0)), true));
                        } else if (xLockedDoor) {
                        	lockedTransform.setPosition(i + SPOT_LENGTH / 2, 0, j);
                        	lockedTransform.setRotation(0, 90, 0);
                            lockedDoors.add(new LockedDoor(lockedTransform, lockedTransform.getPosition().add(new Vector3f(0, 0, -0.9f)), true));
                        }
                    }else if ((bitmap.getPixel(i, j) & 0x0000FF) == 28) {
                    	//Bronze
                        Transform lockedTransform = new Transform();

                        boolean xLockedDoor = (bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0;
                        boolean yLockedDoor = (bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0;

                        if ((yLockedDoor && xLockedDoor) || !(yLockedDoor || xLockedDoor)) {
                            Log.fatal("Level Generation Error at (" + i + ", " + j + "): Bronze Locked Doors must be between two solid walls.");
                        }

                        if (yLockedDoor) {
                        	lockedTransform.setPosition(i, 0,j + SPOT_LENGTH / 2);
                            lockedDoors.add(new LockedDoor(lockedTransform, lockedTransform.getPosition().add(new Vector3f(-0.9f, 0, 0)), false));
                        } else if (xLockedDoor) {
                        	lockedTransform.setPosition(i + SPOT_LENGTH / 2, 0, j);
                        	lockedTransform.setRotation(0, 90, 0);
                            lockedDoors.add(new LockedDoor(lockedTransform, lockedTransform.getPosition().add(new Vector3f(0, 0, -0.9f)), false));
                        }
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 30) {
                        Transform barTransform = new Transform();

                        if ((bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0) {
                        	barTransform.setPosition(i, 0,j + SPOT_LENGTH / 2);
                            barsWalls.add(new BarsWall(barTransform));
                        }
                        if ((bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0) {
                        	barTransform.setPosition(i + SPOT_LENGTH / 2, 0, j);
                        	barTransform.setRotation(0, 90, 0);
                        	barsWalls.add(new BarsWall(barTransform));
                        }
                        if ((bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0) {
                        	barTransform.setPosition(i, 0,j + SPOT_LENGTH / 2);
                            barsWalls.add(new BarsWall(barTransform));
                        }
                        if ((bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0) {
                        	barTransform.setPosition(i + SPOT_LENGTH / 2, 0, j);
                        	barTransform.setRotation(0, 90, 0);
                        	barsWalls.add(new BarsWall(barTransform));
                        }
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 128) {
                    	naziSoldiers.add(new NaziSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 1) {
                        player = new Player(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0.5f, (j + 0.5f) * SPOT_LENGTH));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 192) {
                        medkits.add(new Medkit(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 100) {
                        trees.add(new Tree(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), "tree/MEDIA", 0.8f));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 101) {
                        trees.add(new Tree(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), "tree/SPDCQ0", Util.randomInRange(1, 3)));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 102) {
                        grass.add(new Grass(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 50) {
                    	flares.add(new Lantern(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, LEVEL_HEIGHT * 0.75f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 51) {
                    	lamps.add(new Lamp(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 52) {
                    	//left
                    	lightPosts.add(new LightPost(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), false));
                    }else if ((bitmap.getPixel(i, j) & 0x0000FF) == 53) {
                    	//Right
                    	lightPosts.add(new LightPost(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 55) {
                        bones.add(new Bones(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 60) {
                        deadNazi.add(new NaziSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 65) {
                        chainguns.add(new Chaingun(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 67) {
                    	rocketLaunchers.add(new RocketLauncher(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 70) {
                        deadJews.add(new DeadJew(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.05f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 73) {
                        rockets.add(new Rocket(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.05f, (j + 0.5f) * SPOT_LENGTH)), true, 1));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 75) {
                        rockets.add(new Rocket(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.05f, (j + 0.5f) * SPOT_LENGTH)), true, 10));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 80) {
                        foods.add(new Food(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 90) {
                        dogs.add(new Dog(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 91) {
                        captains.add(new Captain(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 92) {
                        captains.add(new Captain(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), false));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 93) {
                        commanders.add(new Commander(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 110) {
                        ssSoldiers.add(new SsSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 120) {
                    	tables.add(new Table(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 121) {
                    	furnaces.add(new Oven(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                	} else if ((bitmap.getPixel(i, j) & 0x0000FF) == 122) {
                    	kitchens.add(new Kitchen(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                        foods.add(new Food(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 123) {
                    	clocks.add(new Clock(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 130) {
                    	superShotguns.add(new SuperShotgun(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 140) {
                    	naziSeargeants.add(new NaziSergeant(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 150) {
                    	pipes.add(new Pipe(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0.00000000001f * LEVEL_HEIGHT, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 151) {
                        pendules.add(new Pendule(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 152) {
                        hangeds.add(new Hanged(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 153) {
                        pillars.add(new Pillar(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0.0000000001f * LEVEL_HEIGHT, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 154) {
                        armors.add(new Armor(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 155) {
                        helmets.add(new Helmet(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                        //barrels.add(new Barrel(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 156) {
                        shells.add(new Shell(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 157) {
                        bags.add(new Bag(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 158) {
                        bullets.add(new Bullet(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 169) {
                    	//GoldKey
                    	keys.add(new Key(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true, true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 170) {
                    	//BronzeKey
                    	keys.add(new Key(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), false, true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 175) {
                        signs.add(new Sign(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 160) {
                        barrels.add(new Barrel(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 166) {
                    	//who don't drop a key
                    	zombies.add(new Zombie(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), false));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 167) {
                    	//who do drop a key
                    	zombies.add(new Zombie(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 180) {
                    	//who do drop a key
                    	ghosts.add(new Ghost(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0xFF0000) == 0) {
                    	dayTransition = true;
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) < 128 && (bitmap.getPixel(i, j) & 0x0000FF) > 96) {
                        int offset = (bitmap.getPixel(i, j) & 0x0000FF) - 96;
                        exitPoints.add(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0f, (j + 0.5f) * SPOT_LENGTH));
                        exitOffsets.add(offset);
                    }
                    
                    float[] texCoords = calcTextCoords((bitmap.getPixel(i, j) & 0x00FF00) >> 8);
                    
                    //Generate Floor
                    addFace(indices, vertices.size(), true);
    				addVertices(vertices, i, j, 0, true, false, true, texCoords);
    				
					//Generate Ceiling
    				if (((bitmap.getPixel(i, j) & 0xFF0000) != 0)) {
	                    addFace(indices, vertices.size(), false);
	    				addVertices(vertices, i, j, 1, true, false, true, texCoords);
    				}
    				
    				texCoords = calcTextCoords((bitmap.getPixel(i, j) & 0xFF0000) >> 16);
                    
                    SecretWall.xHigher = texCoords[0];
                    SecretWall.xLower = texCoords[1];
                    SecretWall.yHigher = texCoords[2];
                    SecretWall.yLower = texCoords[3];

                    //Generate Walls
                    if ((bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
                        addFace(indices,vertices.size(),false);
                        addVertices(vertices, i, 0, j, true, true, false, texCoords);
                    }
                    if ((bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                        addFace(indices,vertices.size(),true);
                        addVertices(vertices, i, 0, (j + 1), true, true, false, texCoords);
                    }
                    if ((bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                        addFace(indices,vertices.size(),true);
                        addVertices(vertices, 0, j, i, false, true, true, texCoords);
                    }
                    if ((bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                        addFace(indices,vertices.size(),false);
                        addVertices(vertices, 0, j, (i + 1), false, true, true, texCoords);
                    }
                    
            }
        }
        
        Vertex[] vertaArray = new Vertex[vertices.size()];
        Integer[] intaArray = new Integer[indices.size()];
        vertices.toArray(vertaArray);
        indices.toArray(intaArray);
        walls.add(new Wall(new Transform(), material, new Mesh(vertaArray, Util.toIntArray(intaArray), true, true)));
        
        if(directionalLight == null)
        	directionalLight = new DirectionalLight(new Vector3f(0.75f,0.75f,0.75f), 
            		1f, new Vector3f(bitmap.getWidth()/2,10,bitmap.getHeight()/2));
        
        renderingEngine.setFogDensity(0.07f);
        renderingEngine.setFogGradient(1.5f);
        renderingEngine.setFogColor(new Vector3f(0.5f,0.5f,0.5f));
        renderingEngine.setAmbientLight(new Vector3f(0.75f,0.75f,0.75f));
        directionalLight.addToEngine();
    }
    
    /**
     * Adds a wall into the level.
     * @param i index on bitmap
     * @param j index on bitmap
     * @param verticeX position
     * @param verticeY position
     * @param verticeZ position
     * @param faceX if it's
     * @param faceY if it's
     * @param faceZ if it's
     * @param texCoords to sample
     */
    public void addWall(int i, int j, int verticeX, int verticeY, int verticeZ, boolean faceX, boolean faceY, boolean faceZ, float[] texCoords) {
    	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    	ArrayList<Integer> indices = new ArrayList<Integer>();
    	
    	addFace(indices,vertices.size(),false);
        addVertices(vertices, verticeX, verticeY, verticeZ, faceX, faceY, faceZ, texCoords);
        
        Vertex[] vertArray = new Vertex[vertices.size()];
        Integer[] intArray = new Integer[indices.size()];
        
        vertices.toArray(vertArray);
        indices.toArray(intArray);
        
        Mesh geometry = new Mesh(vertArray, Util.toIntArray(intArray), true, true);
        walls.add(new Wall(new Transform(new Vector3f(i, 0, j)), material, geometry));
    }
    
    /**
     * Checks the damage of some enemy.
     * @param array of enemies
     * @param sound to play
     * @param times to check patron
     */
    private <E> void checkDamage(ArrayList<E> array, Clip sound, int times) {
		for (E component : array) {
			if(player.weaponType == player.BULLET && player.getWeaponState() != "chaingun") {
				if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
					if(times == 3)
						if(sound != null)
							AudioUtil.playAudio(sound, 0);
					((GameComponent) component).damage(player.getDamage());
				}
			}else if(player.weaponType == player.BULLET && player.getWeaponState() == "chaingun" && player.chaingunCanFire) {
				if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
					if(times == 3)
						if(sound != null)
							AudioUtil.playAudio(sound, 0);
					((GameComponent) component).damage(player.getDamage());
				}
			}else if(player.weaponType == player.SHELL) {
				if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
					if(times == 2 || times == 3)
						if(sound != null)
							AudioUtil.playAudio(sound, 0);
					((GameComponent) component).damage(player.getDamage());
				}
			}else if(player.weaponType == player.MELEE) {
				if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
					if(times == 1 || times == 3)
						if(sound != null)
							AudioUtil.playAudio(sound, 0);
					((GameComponent) component).damage(player.getMeleeDamage());
            	}
			}else if(player.weaponType == player.ROCKET || player.weaponType == player.GAS && times == 69) {
				if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.5f) {
					setShootingObjective((GameComponent) component);
					if(player.weaponType == player.GAS)
	            		addFire((GameComponent) component, true);
				}
				
			}		
			if(player.kickCanHurt) {
				if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE + 0.05f && player.isAlive) {
					if(times == 1 || times == 3)
						if(sound != null)
							AudioUtil.playAudio(sound, 0);
					((GameComponent) component).damage(player.getMeleeDamage());
            	}
			}
			
			if((player.weaponType == player.MELEE == true || player.kickCanHurt) && times == 69) {
				if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
					AudioUtil.playAudio(sound, 0);
				}
			}
		}
    }
    
    /**
     * Adds a bleeding to the objects list.
     * @param component to bleed.
     */
    public <E> void addBleeding(E component) {
    	if(PARTICLES_LEVEL >= 2) {
	    	bleeding.add(new Bleed(new Transform(((GameComponent) component).getTransform().getPosition())));
	    	objects.add(bleeding);
    	}
    }
    
    /**
     * Adds a fire to the objects list.
     * @param component to fire.
     */
    public <E> void addFire(E component, boolean isStatic) {
    	if(PARTICLES_LEVEL >= 2) {
	    	if(isStatic)
	    		fire.add(new Fire(new Transform(((GameComponent) component).getTransform().getPosition().add(0.01f))));
	    	else
	    		fire.add(new Fire(((GameComponent) component).getTransform()));
	    	objects.add(fire);
    	}
    }
    
    /**
     * Returns the material of the level
     * @return material
     */
    public Material getMaterial() { return material; } 
	
	/**
	 * Returns all the secret walls in the array-list.
	 * @return Secret walls.
	 */
	public ArrayList<SecretWall> getSecretWalls() {return secretWalls;}
	
	/**
	 * Returns all the Nazi soldiers in the array-list.
	 * @return Nazi Soldiers.
	 */
	public ArrayList<NaziSoldier> getNaziSoldiers() {return naziSoldiers;}

	/**
	 * Returns all the Dogs in the array-list.
	 * @return Dogs.
	 */
	public ArrayList<Dog> getDogs() {return dogs;}
	
	/**
	 * Returns all the SS soldiers in the array-list.
	 * @return SS soldiers.
	 */
	public ArrayList<SsSoldier> getSsSoldiers() {return ssSoldiers;}
	
	/**
	 * Returns all the Nazi sergeants in the array-list.
	 * @return Nazi sergeants.
	 */
	public ArrayList<NaziSergeant> getNaziSergeants() {return naziSeargeants;}
	
	/**
	 * Returns all the zombies in the array-list.
	 * @return zombies.
	 */
	public ArrayList<Zombie> getZombies() {return zombies;}
	
	/**
	 * Returns all the captains in the array-list.
	 * @return captains.
	 */
	public ArrayList<Captain> getCaptains() {return captains;}
	
	/**
	 * Get access to the main player object in game.
	 * @return Player.
	 */
	public static Player getPlayer() {return player;}
	
	/**
	 * Returns the game's objects list.
	 * @return game's objects list
	 */
	public GameObject getObjectsList() { return objects; }

	/**
	 * Gets the nearest targetComponent in the level.
	 * @return the shootingObjetive
	 */
	public GameComponent getShootingObjective() { return shootingObjective; }

	/**
	 * Sets a new nearest targetComponent in the level.
	 * @param shootingObjective the shooting objective to set
	 */
	public void setShootingObjective(GameComponent shootingObjective) { this.shootingObjective = shootingObjective; }
	
	/**
	  * Removes the medical kits when the player grabs it.
	  * @param medkit Medical kit.
	  */
	public static void removeMedkit(Medkit medkit) {removeMedkitList.add(medkit);}
	    
	/**
	 * Removes the food when the player grabs it.
	 * @param food Food.
	 */
	public static void removeFood(Food food) {removeFoodList.add(food);}
	    
	/**
	 * Removes the bullet packs when the player grabs it.
	 * @param bullet Bullet pack.
	 */
	public static void removeBullets(Bullet bullet) {removeBulletList.add(bullet);}
	
	/**
	 * Removes the shell packs when the player grabs it.
	 * @param shell Shell pack.
	 */
	public static void removeShells(Shell shell) {removeShellList.add(shell);}
	    
	/**
	 * Removes the bags when the player grabs it.
	 * @param bag Bag.
	 */
	public static void removeBags(Bag bag) {removeBagList.add(bag);}
	    
	/**
	 * Removes the shotguns when the player grabs it.
	 * @param shotgun Shotgun.
	 */
	public static void removeShotgun(Shotgun shotgun) {removeShotgunList.add(shotgun);}
	    
	/**
	 * Removes the machine-guns when the player grabs it.
	 * @param machineGun Machine-Gun.
	 */
	public static void removeMachineGun(Machinegun machineGun) {removeMachineGunList.add(machineGun);}
	
	/**
	 * Removes the ghost when disappears.
	 * @param ghost Ghost.
	 */
	public static void removeGhost(Ghost ghost) {removeGhostList.add(ghost);}
	
	/**
	 * Removes the armor when disappears.
	 * @param armor Armor.
	 */
	public static void removeArmor(Armor armor) {removeArmorList.add(armor);}
	
	/**
	 * Removes the super shotguns when the player grabs it.
	 * @param sShotgun Super shotgun.
	 */
	public static void removeSuperShotgun(SuperShotgun sShotgun) {removeSuperShotgunList.add(sShotgun);}
	
	/**
	 * Removes the helmet when disappears.
	 * @param helmet Helmet.
	 */
	public static void removeHelmet(Helmet helmet) {removeHelmets.add(helmet);}
	
	/**
	 * Removes the barrel when disappears.
	 * @param barrel Barrels.
	 */
	public static void removeBarrel(Barrel barrel) {removeBarrels.add(barrel);}

	/**
	 * Removes the key when disappears.
	 * @param key keys.
	 */
	public static void removeArmor(Key key) { removeKeys.add(key); }
	
	/**
	 * Removes the explosion when disappears.
	 * @param explosion explosions.
	 */
	public static void removeExplosion(Explosion explosion) { removeExplosions.add(explosion); }
	
	/**
	 * Removes the chain-gun when the player grabs it.
	 * @param chaingun chain-gun.
	 */
	public static void removeChainGun(Chaingun chaingun) {removeChaingunList.add(chaingun);}
	
	/**
	 * Removes the rocket when disappears.
	 * @param rocket rocket.
	 */
	public static void removeRockets(Rocket rocket) { removeRockets.add(rocket); }
	
	/**
	 * Removes the bleeding when disappears.
	 * @param bleed bleed.
	 */
	public static void removeBleeding(Bleed bleed) { removeBleedingList.add(bleed); }
	
	/**
	 * Removes the rocket launcher when the player grabs it.
	 * @param rocketLauncher rocket launcher.
	 */
	public static void removeRocketLauncher(RocketLauncher rocketLauncher) {removeRocketLauncherList.add(rocketLauncher);}
	
	/**
	 * Removes the fire when disappears.
	 * @param fire fire.
	 */
	public static void removeFire(Fire fire) { removeFireList.add(fire); }

}