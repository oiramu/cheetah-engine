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

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
import engine.components.BaseLight;
import engine.components.DirectionalLight;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.core.CoreEngine;
import engine.core.Debug;
import engine.core.GameObject;
import engine.core.Input;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Util;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.physics.PhysicsUtil;
import engine.rendering.Bitmap;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Vertex;
import game.doors.Door;
import game.doors.LockedDoor;
import game.doors.SecretWall;
import game.enemies.Dog;
import game.enemies.Ghost;
import game.enemies.NaziSergeant;
import game.enemies.NaziSoldier;
import game.enemies.SsSoldier;
import game.enemies.Zombie;
import game.objects.Barrel;
import game.objects.Bones;
import game.objects.Clock;
import game.objects.DeadJew;
import game.objects.Furnace;
import game.objects.Hanged;
import game.objects.Kitchen;
import game.objects.Lamp;
import game.objects.Lantern;
import game.objects.LightBeam;
import game.objects.Pendule;
import game.objects.Pillar;
import game.objects.Pipe;
import game.objects.Table;
import game.objects.Tree;
import game.pickUps.Armor;
import game.pickUps.Bag;
import game.pickUps.Bullet;
import game.pickUps.Food;
import game.pickUps.Helmet;
import game.pickUps.Key;
import game.pickUps.Machinegun;
import game.pickUps.Medkit;
import game.pickUps.Shell;
import game.pickUps.Shotgun;
import game.pickUps.SuperShotgun;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.6
 * @since 2017
 */
public class Level extends GameComponent {

	//Constants
    public static final float SPOT_WIDTH = 1;
    public static final float SPOT_LENGTH = 1;
    public static final float LEVEL_HEIGHT = 1;

    private static final float NUM_TEX_X = 4f;
    private static final float NUM_TEX_Y = 4f;
    
    private static final float MELEE_RANGE = 0.55f;
    private static final float BULLET_RANGE = 2f;
    private static final float SHELL_RANGE = 3f;

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
    private static ArrayList<Key> removeKeys;
    
    //Player
    private static Player player;

    //Level brain
    private ArrayList<Vector3f> exitPoints;
    private ArrayList<Integer> exitOffsets;
    private ArrayList<Vector2f> collisionPosStart;
    private ArrayList<Vector2f> collisionPosEnd;
    
    //Doors
    private ArrayList<Door> doors;
    private ArrayList<SecretWall> secretWalls;
    private ArrayList<LockedDoor> lockedDoors;
    
    //Pick-Ups
    private ArrayList<Shotgun> shotguns;
    private ArrayList<Medkit> medkits;
    private ArrayList<Food> foods;
    private ArrayList<Bullet> bullets;
    private ArrayList<Bag> bags;
    private ArrayList<Machinegun> machineguns;
    private ArrayList<Armor> armors;
    private ArrayList<SuperShotgun> superShotguns;
    private ArrayList<Helmet> helmets;
    private ArrayList<Key> keys;
    
    //Static objects
    private ArrayList<Tree> trees;
    private ArrayList<Lantern> flares;
    private ArrayList<LightBeam> lightPoints;
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
    private ArrayList<Furnace> furnaces;
    private ArrayList<Kitchen> kitchens;
    private ArrayList<Barrel> barrels;
   
    //Enemies
    private ArrayList<NaziSoldier> naziSoldiers;
    private ArrayList<Dog> dogs;
    private ArrayList<SsSoldier> ssSoldiers;
    private ArrayList<NaziSergeant> naziSeargeants;
    private ArrayList<Ghost> ghosts;
    private ArrayList<Zombie> zombies;

    //Level
    private Mesh geometry;
    private Bitmap bitmap;
    private Material material;
    private Transform transform;
    private MeshRenderer meshRenderer;
    private RenderingEngine renderingEngine;
    private BaseLight directionalLight;
    private GameObject objects;

    /**
     * Constructor of the level in the game.
     * @param bitmap to load and use.
     * @param material to load and use.
     */
    public Level(Bitmap bitmap, Material material) {	
        if(objects == null) this.objects = new GameObject();
        
        //Level stuff
        if(this.bitmap == null) this.bitmap = bitmap;
        if(this.material == null) this.material = material;
        if(transform == null) this.transform = new Transform();
        if(collisionPosStart == null) this.collisionPosStart = new ArrayList<Vector2f>();
        if(collisionPosEnd == null) this.collisionPosEnd = new ArrayList<Vector2f>();
    	if(renderingEngine == null) renderingEngine = CoreEngine.renderingEngine;
        
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
        //Objects
        objects.add(doors);
        objects.add(secretWalls);
        objects.add(trees);
        objects.add(flares);
        objects.add(lightPoints);
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
        //Power-ups
        objects.add(medkits);
        objects.add(foods);
        objects.add(bullets);
        objects.add(bags);
        objects.add(shotguns);
        objects.add(machineguns);
        objects.add(armors);
        objects.add(helmets);
        objects.add(superShotguns); 
        objects.add(keys);
        
        renderingEngine.setMainCamera(player.getCamera());
    }
    
    /**
     * Checks the damage of some enemy.
     * @param array of enemies
     * @param sound to play
     * @param times to check patron
     */
    private <E> void checkDamage(ArrayList<E> array, Clip sound, int times) {
    		for (E component : array) {
    			if(player.isBulletBased) {
    					if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
                	if(times == 3)
                		if(sound != null)
    	            		AudioUtil.playAudio(sound, 0);
                	((GameComponent) component).damage(player.getDamage());
                }
        	}else if(player.isShellBased) {
        		if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
        			if(times == 2 || times == 3)
                		if(sound != null)
    	            		AudioUtil.playAudio(sound, 0);
        			((GameComponent) component).damage(player.getDamage());
        		}
        	}else if(player.isMelee) {
	            if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
	            	if(times == 1 || times == 3)
	            		if(sound != null)
	            			AudioUtil.playAudio(sound, 0);
	               ((GameComponent) component).damage(player.getDamage());
	            }
            }
        	if(player.isMelee == true && times == 69) {
  	           if (Math.abs(((GameComponent) component).getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
 	                AudioUtil.playAudio(sound, 0);
 	           }
            }
        }
    }

    /**
     * Inputs accessible in the level.
     */
	public void input() {
		double time = Time.getTime();
    		double timeDecimals = (time - (double) ((int) time));
    	
		if (!player.isAlive) {
	        if(Input.getKeyDown(Input.KEY_E) || Input.getKeyDown(Input.KEY_SPACE)) {
	    		if (timeDecimals <= 5.0f) {
		            Auschwitz.reloadLevel();
		            player.gotPistol();
	    		}
	        }
        }

        if (player.fires && !player.isReloading) {
        	
        	checkDamage(naziSoldiers, punchNoise, 1);
        	checkDamage(dogs, punchNoise, 1);
        	checkDamage(ssSoldiers, punchNoise, 1);
        	checkDamage(naziSeargeants, punchNoise, 1);
        	checkDamage(ghosts, null, 255);
        	checkDamage(zombies, punchNoise, 1);
        	checkDamage(lamps, punchSolidNoise, 1);
        	checkDamage(pillars, punchSolidNoise, 1);
        	checkDamage(barrels, barrelNoise, 3);
        	checkDamage(hangeds, punchNoise, 69);
        	checkDamage(doors, punchSolidNoise, 69);
        	checkDamage(pipes, punchSolidNoise, 69);
        	checkDamage(tables, punchSolidNoise, 69);
        	checkDamage(clocks, punchSolidNoise, 69);
        	checkDamage(furnaces, punchSolidNoise, 69);
        	checkDamage(clocks, punchSolidNoise, 69);
        	checkDamage(furnaces, punchSolidNoise, 69);
        	checkDamage(kitchens, punchSolidNoise, 69);
        	checkDamage(lockedDoors, punchSolidNoise, 69);
 
        }

        player.input();
    }

    /**
     * Updates everything rendered in the level.
     * @param delta of time
     */
    public void update(double delta) {
        
    	objects.update(delta);

        objects.updateAndKillToRenderPipeline(deadNazi, delta);
        
        objects.removeListToRenderPipeline(removeMedkitList);
        objects.removeListToRenderPipeline(removeFoodList);
        objects.removeListToRenderPipeline(removeBulletList);
        objects.removeListToRenderPipeline(removeShellList);
        objects.removeListToRenderPipeline(removeBagList);
        objects.removeListToRenderPipeline(removeShotgunList);
        objects.removeListToRenderPipeline(removeMachineGunList);
        objects.removeListToRenderPipeline(removeGhostList);
        objects.removeListToRenderPipeline(removeArmorList);
        objects.removeListToRenderPipeline(removeHelmets);
        objects.removeListToRenderPipeline(removeSuperShotgunList);
        objects.removeListToRenderPipeline(removeBarrels);
        objects.removeListToRenderPipeline(removeKeys);
        
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
        removeKeys.clear();
    }

    /**
     * Renders everything in the level.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {
    	meshRenderer.render(shader, renderingEngine);
    	objects.render(shader, renderingEngine);
        
   		objects.sortNumberComponents(secretWalls);
   		objects.sortNumberComponents(naziSoldiers);
   		objects.sortNumberComponents(dogs);
   		objects.sortNumberComponents(ssSoldiers);
   		objects.sortNumberComponents(naziSeargeants);
   		objects.sortNumberComponents(zombies);
    }
    
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
                }
            }
        }

        if (!worked && playSound) {
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
            
            for (Furnace furnace : furnaces)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, furnace.getTransform().getPosition().getXZ(), furnace.getSize()));
            
            for (Barrel barrel : barrels)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, barrel.getTransform().getPosition().getXZ(), barrel.getSize()));
            
            for (LockedDoor lockedDoor : lockedDoors)
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, lockedDoor.getTransform().getPosition().getXZ(), lockedDoor.getSize()));
            
            for (Zombie zombie : zombies)
            	if(zombie.isQuiet)
            		collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, zombie.getTransform().getPosition().getXZ(), zombie.getSize()));
            
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

        if (hurtMonsters) {
            Vector2f monsterIntersect = null;
            NaziSoldier nearestMonster = null;
            
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

            for (NaziSoldier monster : naziSoldiers) {
                Vector2f collision = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, monster.getTransform().getPosition().getXZ(), monster.getSize());

                if (collision != null && (monsterIntersect == null
                        || monsterIntersect.sub(lineStart).length() > collision.sub(lineStart).length())) {
                    monsterIntersect = collision;
                    nearestMonster = monster;
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

            if((player.isBulletBased && player.getBullets()!=0)||(player.isShellBased && player.getShells()!=0)) {
	            if (monsterIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > monsterIntersect.sub(lineStart).length())) {
	                nearestMonster.damage(player.getDamage());
	            }
	            
	            if (dogIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > dogIntersect.sub(lineStart).length())) {
	            	nearestDog.damage(player.getDamage());
	            }
	            
	            if (ssSoldierIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > ssSoldierIntersect.sub(lineStart).length())) {
	            	nearestSsSoldier.damage(player.getDamage());
	            }
	            if (naziSergeantsIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > naziSergeantsIntersect.sub(lineStart).length())) {
	            	nearestNaziSargent.damage(player.getDamage());
	            }
	            
	            if (ghostIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > ghostIntersect.sub(lineStart).length())) {
	            	nearestGhost.damage(player.getDamage());
	            }
	            if (zombieIntersect != null && (nearestIntersect == null
	                    || nearestIntersect.sub(lineStart).length() > zombieIntersect.sub(lineStart).length())) {
	            	nearestZombie.damage(player.getDamage());
	            }
        	}
            if(player.isMelee && player.isAlive) {
        		if (monsterIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ monsterIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	                nearestMonster.damage(player.getDamage());
	            }
	            
	            if (dogIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ dogIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestDog.damage(player.getDamage());
	            }
	            
	            if (ssSoldierIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ ssSoldierIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestSsSoldier.damage(player.getDamage());
	            }
	            
	            if (naziSergeantsIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ naziSergeantsIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestNaziSargent.damage(player.getDamage());
	            }
	            
	            if (ghostIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ ghostIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestGhost.damage(player.getDamage());
	            }
	            
	            if (zombieIntersect != null && (nearestIntersect == null
	                    || /*nearestIntersect.sub(lineStart).length() >*/ zombieIntersect.sub(lineStart).length() < MELEE_RANGE)) {
	            	nearestZombie.damage(player.getDamage());
	            }
        	}
            
            for (Barrel barrel : barrels) {
                if(barrel.kBooms) {
                    if (monsterIntersect != null && (nearestIntersect == null
                                                     || nearestIntersect.sub(lineStart).length() > monsterIntersect.sub(lineStart).length())) {
                        nearestMonster.damage(barrel.damage);
                    }
                    
                    if (dogIntersect != null && (nearestIntersect == null
                                                 || nearestIntersect.sub(lineStart).length() > dogIntersect.sub(lineStart).length())) {
                        nearestDog.damage(barrel.damage);
                    }
                    
                    if (ssSoldierIntersect != null && (nearestIntersect == null
                                                       || nearestIntersect.sub(lineStart).length() > ssSoldierIntersect.sub(lineStart).length())) {
                        nearestSsSoldier.damage(barrel.damage);
                    }
                    
                    if (naziSergeantsIntersect != null && (nearestIntersect == null
                                                           || nearestIntersect.sub(lineStart).length() > naziSergeantsIntersect.sub(lineStart).length())) {
                        nearestNaziSargent.damage(barrel.damage);
                    }
                    
                    if (ghostIntersect != null && (nearestIntersect == null
                                                   || nearestIntersect.sub(lineStart).length() > ghostIntersect.sub(lineStart).length())) {
                        nearestGhost.damage(barrel.damage);
                    }
                    
                    if (zombieIntersect != null && (nearestIntersect == null
    	                    || nearestIntersect.sub(lineStart).length() > zombieIntersect.sub(lineStart).length())) {
    	            	nearestZombie.damage(barrel.damage);
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
			Debug.printErrorMessage("Invalid plane used in level generator", "Level generation error!");
			System.err.println("Invalid plane used in level generator");
			new Exception().printStackTrace();
			System.exit(1);
		}
	}

    /**
     * Method that generates the level for the game.
     * @param engine of the level.
     */
    private void generateLevel() {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Integer> indices = new ArrayList<Integer>();
        
        //Remove list
    	if(removeMedkitList == null) Level.removeMedkitList = new ArrayList<Medkit>();
    	if(removeFoodList == null) Level.removeFoodList = new ArrayList<Food>();
    	if(removeBulletList == null) Level.removeBulletList = new ArrayList<Bullet>();
    	if(removeShellList == null) Level.removeShellList = new ArrayList<Shell>();
    	if(removeBagList == null) Level.removeBagList = new ArrayList<Bag>();
    	if(removeShotgunList == null) Level.removeShotgunList = new ArrayList<Shotgun>();
    	if(removeMachineGunList == null) Level.removeMachineGunList = new ArrayList<Machinegun>();
    	if(removeGhostList == null) Level.removeGhostList = new ArrayList<Ghost>();
    	if(removeArmorList == null) Level.removeArmorList = new ArrayList<Armor>();
    	if(removeSuperShotgunList == null) Level.removeSuperShotgunList = new ArrayList<SuperShotgun>();
    	if(removeHelmets == null) Level.removeHelmets = new ArrayList<Helmet>();
    	if(removeBarrels == null) Level.removeBarrels = new ArrayList<Barrel>();
    	if(removeKeys == null) Level.removeKeys = new ArrayList<Key>();
        //Doors and stuff
        if(doors == null) this.doors = new ArrayList<Door>();
        if(lockedDoors == null) this.lockedDoors = new ArrayList<LockedDoor>();
        if(secretWalls == null) this.secretWalls = new ArrayList<SecretWall>();
        if(exitOffsets == null) this.exitOffsets = new ArrayList<Integer>();
        if(exitPoints == null) this.exitPoints = new ArrayList<Vector3f>();
        //Enemies
        if(naziSoldiers == null) this.naziSoldiers = new ArrayList<NaziSoldier>();
        if(dogs == null) this.dogs = new ArrayList<Dog>();
        if(ssSoldiers == null) this.ssSoldiers = new ArrayList<SsSoldier>();
        if(naziSeargeants == null) this.naziSeargeants = new ArrayList<NaziSergeant>();
        if(ghosts == null) this.ghosts = new ArrayList<Ghost>();
        if(zombies == null) this.zombies = new ArrayList<Zombie>();
        //Power-ups
        if(medkits == null) this.medkits = new ArrayList<Medkit>();
        if(foods == null) this.foods = new ArrayList<Food>();
        if(bullets == null) this.bullets = new ArrayList<Bullet>();
        if(shotguns == null) this.shotguns = new ArrayList<Shotgun>();
        if(bags == null) this.bags = new ArrayList<Bag>();
        if(machineguns == null) this.machineguns = new ArrayList<Machinegun>();
        if(armors == null) this.armors = new ArrayList<Armor>();
        if(superShotguns == null) this.superShotguns = new ArrayList<SuperShotgun>();
        if(helmets == null) this.helmets = new ArrayList<Helmet>();
        if(keys == null) this.keys = new ArrayList<Key>();
        //Objects
        if(trees == null) this.trees = new ArrayList<Tree>();
        if(flares == null) this.flares = new ArrayList<Lantern>();
        if(lightPoints == null) this.lightPoints = new ArrayList<LightBeam>();
        if(bones == null) this.bones = new ArrayList<Bones>();
        if(deadNazi == null) this.deadNazi = new ArrayList<NaziSoldier>();
        if(pipes == null) this.pipes = new ArrayList<Pipe>();
        if(pendules == null) this.pendules = new ArrayList<Pendule>();
        if(lamps == null) this.lamps = new ArrayList<Lamp>();
        if(hangeds == null) this.hangeds = new ArrayList<Hanged>();
        if(deadJews == null) this.deadJews = new ArrayList<DeadJew>();
        if(tables == null) this.tables = new ArrayList<Table>();
        if(pillars == null) this.pillars = new ArrayList<Pillar>();
        if(clocks == null) this.clocks = new ArrayList<Clock>();
        if(furnaces == null) this.furnaces = new ArrayList<Furnace>();
        if(kitchens == null) this.kitchens = new ArrayList<Kitchen>();
        if(barrels == null) this.barrels = new ArrayList<Barrel>();

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
                            System.err.println("Level Generation Error at (" + i + ", " + j + "): Doors must be between two solid walls.");
                            Debug.printErrorMessage("Level Generation Error at (" + i + ", " + j + "): Doors must be between two solid walls.", "Level generation error!");
                            new Exception().printStackTrace();
                            System.exit(1);
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
                            System.err.println("Level Generation Error at (" + i + ", " + j + "): Secret Walls must be between two solid walls.");
                            Debug.printErrorMessage("Level Generation Error at (" + i + ", " + j + "): Secret Walls must be between two solid walls.", "Level generation error!");
                            new Exception().printStackTrace();
                            System.exit(1);
                        }

                        if (ySecretWall) {
                            wallTransform.setPosition(i, 0, j + SPOT_LENGTH);
                            secretWalls.add(new SecretWall(wallTransform, material, wallTransform.getPosition().add(new Vector3f(-0.9f, 0, 0))));
                        } else if (xSecretWall) {
                            wallTransform.setPosition(i + SPOT_LENGTH, 0, j);
                            wallTransform.setRotation(0, 90, 0);
                            secretWalls.add(new SecretWall(wallTransform, material, wallTransform.getPosition().add(new Vector3f(0, 0, -0.9f))));
                        }
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 24) {
                    	//Gold
                        Transform lockedTransform = new Transform();

                        boolean xLockedDoor = (bitmap.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (bitmap.getPixel(i, j + 1) & 0xFFFFFF) == 0;
                        boolean yLockedDoor = (bitmap.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (bitmap.getPixel(i + 1, j) & 0xFFFFFF) == 0;

                        if ((yLockedDoor && xLockedDoor) || !(yLockedDoor || xLockedDoor)) {
                            System.err.println("Level Generation Error at (" + i + ", " + j + "): Gold Locked Doors must be between two solid walls.");
                            Debug.printErrorMessage("Level Generation Error at (" + i + ", " + j + "): Gold Locked Doors must be between two solid walls.", "Level generation error!");
                            new Exception().printStackTrace();
                            System.exit(1);
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
                            System.err.println("Level Generation Error at (" + i + ", " + j + "): Bronze Locked Doors must be between two solid walls.");
                            Debug.printErrorMessage("Level Generation Error at (" + i + ", " + j + "): Bronze Locked Doors must be between two solid walls.", "Level generation error!");
                            new Exception().printStackTrace();
                            System.exit(1);
                        }

                        if (yLockedDoor) {
                        	lockedTransform.setPosition(i, 0,j + SPOT_LENGTH / 2);
                            lockedDoors.add(new LockedDoor(lockedTransform, lockedTransform.getPosition().add(new Vector3f(-0.9f, 0, 0)), false));
                        } else if (xLockedDoor) {
                        	lockedTransform.setPosition(i + SPOT_LENGTH / 2, 0, j);
                        	lockedTransform.setRotation(0, 90, 0);
                            lockedDoors.add(new LockedDoor(lockedTransform, lockedTransform.getPosition().add(new Vector3f(0, 0, -0.9f)), false));
                        }
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 128) {
                    	naziSoldiers.add(new NaziSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 1) {
                        player = new Player(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0.5f, (j + 0.5f) * SPOT_LENGTH));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 192) {
                        medkits.add(new Medkit(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 100) {
                        trees.add(new Tree(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 50) {
                    	flares.add(new Lantern(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, LEVEL_HEIGHT * 0.75f, (j + 0.5f) * SPOT_LENGTH))));
                    	//lightPoints.add(new LightBeam(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.04f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 51) {
                    	//lightPoints.add(new LightBeam(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.04f, (j + 0.5f) * SPOT_LENGTH))));
                    	lamps.add(new Lamp(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 55) {
                        bones.add(new Bones(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 60) {
                        deadNazi.add(new NaziSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 70) {
                        deadJews.add(new DeadJew(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.05f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 80) {
                        foods.add(new Food(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 90) {
                        dogs.add(new Dog(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 110) {
                        ssSoldiers.add(new SsSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 120) {
                    	tables.add(new Table(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 121) {
                    	furnaces.add(new Furnace(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
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
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 169) {
                    	//GoldKey
                    	keys.add(new Key(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true, true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 170) {
                    	//BronzeKey
                    	keys.add(new Key(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), false, true));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 160) {
                        barrels.add(new Barrel(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 166) {
                    	zombies.add(new Zombie(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), false));
                    } else if ((bitmap.getPixel(i, j) & 0x0000FF) == 167) {
                    	zombies.add(new Zombie(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH)), true));
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
    				addFace(indices, vertices.size(), false);
    				addVertices(vertices, i, j, 1, true, false, true, texCoords);
                    
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
        Vertex[] vertArray = new Vertex[vertices.size()];
        Integer[] intArray = new Integer[indices.size()];
        
        vertices.toArray(vertArray);
        indices.toArray(intArray);
        
        if(geometry == null)
        	geometry = new Mesh(vertArray, Util.toIntArray(intArray), true, true);
        if(meshRenderer == null)
        	meshRenderer = new MeshRenderer(geometry, transform, material);
        if(directionalLight == null)
        	directionalLight = new DirectionalLight(new Vector3f(0.75f,0.75f,0.75f), 
            		1f, new Vector3f(bitmap.getWidth()/2,10,bitmap.getHeight()/2));
        
        renderingEngine.setFogDensity(0.07f);
        renderingEngine.setFogGradient(1.5f);
        renderingEngine.setFogColor(new Vector3f(0.5f,0.5f,0.5f));
        renderingEngine.setAmbientLight(new Vector3f(0.75f,0.75f,0.75f));
        renderingEngine.addLight(directionalLight);
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
	 * Get access to the main player object in game.
	 * @return Player.
	 */
	public static Player getPlayer() {return player;}
	
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

}