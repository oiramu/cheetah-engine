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
import java.util.Random;

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
import engine.core.GameComponent;
import engine.core.Input;
import engine.core.ResourceLoader;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Util;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.physics.PhysicsUtil;
import engine.rendering.BaseLight;
import engine.rendering.Bitmap;
import engine.rendering.DirectionalLight;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.MeshRenderer;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Vertex;
import game.doors.Door;
import game.doors.SecretWall;
import game.enemies.Dog;
import game.enemies.Ghost;
import game.enemies.NaziSergeant;
import game.enemies.NaziSoldier;
import game.enemies.SsSoldier;
import game.objects.Barrel;
import game.objects.Bones;
import game.objects.Clock;
import game.objects.DeadJew;
import game.objects.DeadNazi;
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
import game.powerUp.Armor;
import game.powerUp.Bag;
import game.powerUp.Bullet;
import game.powerUp.Food;
import game.powerUp.Helmet;
import game.powerUp.Machinegun;
import game.powerUp.Medkit;
import game.powerUp.Shotgun;
import game.powerUp.SuperShotgun;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.5
 * @since 2017
 */
public class Level implements GameComponent {

	//Constants
    public static final float SPOT_WIDTH = 1;
    public static final float SPOT_LENGTH = 1;
    public static final float LEVEL_HEIGHT = 1;

    private static final float NUM_TEX_X = 4f;
    private static final float NUM_TEX_Y = 4f;
    
    private static final float MELEE_RANGE = 0.55f;
    private static final float BULLET_RANGE = 2f;
    private static final float SHELL_RANGE = 3f;
    
    private static final float PLAYER_HEIGHT = 0.4375f;

    private static final String PLAYER_RES_LOC = "player/";
    
    //Player's sounds
    private static final Clip misuseNoise = ResourceLoader.loadAudio(PLAYER_RES_LOC + "OOF");
    private static final Clip punchNoise = ResourceLoader.loadAudio(PLAYER_RES_LOC + "PLSPNCH6");
    private static final Clip punchSolidNoise = ResourceLoader.loadAudio(PLAYER_RES_LOC + "PUNCH2");

    //Remove list
    private static ArrayList<Medkit> removeMedkitList;
    private static ArrayList<Food> removeFoodList;
    private static ArrayList<Bullet> removeBulletList;
    private static ArrayList<Bag> removeBagList;
    private static ArrayList<Shotgun> removeShotgunList;
    private static ArrayList<Machinegun> removeMachineGunList;
    private static ArrayList<Ghost> removeGhostList;
    private static ArrayList<Armor> removeArmorList;
    private static ArrayList<SuperShotgun> removeSuperShotgunList;
    private static ArrayList<Helmet> removeHelmets;
    private static ArrayList<Barrel> removeBarrels;
    
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
    
    //Power-Ups
    private ArrayList<Shotgun> shotguns;
    private ArrayList<Medkit> medkits;
    private ArrayList<Food> foods;
    private ArrayList<Bullet> bullets;
    private ArrayList<Bag> bags;
    private ArrayList<Machinegun> machineguns;
    private ArrayList<Armor> armors;
    private ArrayList<SuperShotgun> superShotguns;
    private ArrayList<Helmet> helmets;
    
    //Static objects
    private ArrayList<Tree> trees;
    private ArrayList<Lantern> flares;
    private ArrayList<LightBeam> lightPoints;
    private ArrayList<Bones> bones;
    private ArrayList<DeadNazi> deadNazi;
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

    //Level
    private Mesh geometry;
    private Bitmap level;
    private Material material;
    private Transform transform;
    private MeshRenderer meshRenderer;

    /**
     * Constructor of the level in the game.
     * @param bitmap to load and use.
     * @param material to load and use.
     */
    public Level(Bitmap bitmap, Material material) {	
        //Remove list
        Level.removeMedkitList = new ArrayList<Medkit>();
        Level.removeFoodList = new ArrayList<Food>();
        Level.removeBulletList = new ArrayList<Bullet>();
        Level.removeBagList = new ArrayList<Bag>();
        Level.removeShotgunList = new ArrayList<Shotgun>();
        Level.removeMachineGunList = new ArrayList<Machinegun>();
        Level.removeGhostList = new ArrayList<Ghost>();
        Level.removeArmorList = new ArrayList<Armor>();
        Level.removeSuperShotgunList = new ArrayList<SuperShotgun>();
        Level.removeHelmets = new ArrayList<Helmet>();
        Level.removeBarrels = new ArrayList<Barrel>();
        //Doors and stuff
        this.doors = new ArrayList<Door>();
        this.secretWalls = new ArrayList<SecretWall>();
        this.exitOffsets = new ArrayList<Integer>();
        this.exitPoints = new ArrayList<Vector3f>();
        //Enemies
        this.naziSoldiers = new ArrayList<NaziSoldier>();
        this.dogs = new ArrayList<Dog>();
        this.ssSoldiers = new ArrayList<SsSoldier>();
        this.naziSeargeants = new ArrayList<NaziSergeant>();
        this.ghosts = new ArrayList<Ghost>();
        //Power-ups
        this.medkits = new ArrayList<Medkit>();
        this.foods = new ArrayList<Food>();
        this.bullets = new ArrayList<Bullet>();
        this.shotguns = new ArrayList<Shotgun>();
        this.bags = new ArrayList<Bag>();
        this.machineguns = new ArrayList<Machinegun>();
        this.armors = new ArrayList<Armor>();
        this.superShotguns = new ArrayList<SuperShotgun>();
        this.helmets = new ArrayList<Helmet>();
        //Objects
        this.trees = new ArrayList<Tree>();
        this.flares = new ArrayList<Lantern>();
        this.lightPoints = new ArrayList<LightBeam>();
        this.bones = new ArrayList<Bones>();
        this.deadNazi = new ArrayList<DeadNazi>();
        this.pipes = new ArrayList<Pipe>();
        this.pendules = new ArrayList<Pendule>();
        this.lamps = new ArrayList<Lamp>();
        this.hangeds = new ArrayList<Hanged>();
        this.deadJews = new ArrayList<DeadJew>();
        this.tables = new ArrayList<Table>();
        this.pillars = new ArrayList<Pillar>();
        this.clocks = new ArrayList<Clock>();
        this.furnaces = new ArrayList<Furnace>();
        this.kitchens = new ArrayList<Kitchen>();
        this.barrels = new ArrayList<Barrel>();
        //Level stuff
        this.level = bitmap;
        this.geometry = new Mesh();
        this.material = material;
        this.transform = new Transform();
        this.collisionPosStart = new ArrayList<Vector2f>();
        this.collisionPosEnd = new ArrayList<Vector2f>();
        generateLevel();
        
        Transform.setCamera(player.getCamera());
    }

    Random rand = new Random();

    /**
     * Inputs accessible in the level.
     */
	public void input() {
		double time = (double) Time.getTime() / Time.SECOND;
    	double timeDecimals = (time - (double) ((int) time));
		if (!player.isAlive) {
	        if(Input.getKeyDown(Input.KEY_E) || Input.getKeyDown(Input.KEY_SPACE)) {
	    		if (timeDecimals <= 5.0f) {
		            Auschwitz.loadLevel(Auschwitz.levelNum-Auschwitz.levelNum);
		            player.gotPistol();
	    		}
	        }
        }

        if (player.fires && !player.isReloading) {
            for (NaziSoldier monster : naziSoldiers) {
            	if(player.isBulletBased) {
	                if (Math.abs(monster.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                    monster.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(monster.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			monster.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(monster.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchNoise, 0);
    	               monster.damage(player.getDamage());
    	            }
                }
            }
            
            for (Dog dog : dogs) {
            	if(player.isBulletBased) {
	                if (Math.abs(dog.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	dog.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(dog.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			dog.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(dog.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchNoise, 0);
    	               dog.damage(player.getDamage());
    	            }
                }
            }
            
            for (SsSoldier ssSoldier : ssSoldiers) {
            	if(player.isBulletBased) {
	                if (Math.abs(ssSoldier.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	ssSoldier.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(ssSoldier.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			ssSoldier.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(ssSoldier.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchNoise, 0);
    	               ssSoldier.damage(player.getDamage());
    	            }
                }
            }
            
            for (NaziSergeant naziSergeants : naziSeargeants) {
            	if(player.isBulletBased) {
	                if (Math.abs(naziSergeants.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	naziSergeants.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(naziSergeants.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			naziSergeants.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(naziSergeants.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchNoise, 0);
    	               naziSergeants.damage(player.getDamage());
    	            }
                }
            }
            
            for (Ghost ghost : ghosts) {
            	if(player.isBulletBased) {
	                if (Math.abs(ghost.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	ghost.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(ghost.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			ghost.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(ghost.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               ghost.damage(player.getDamage());
    	            }
                }
            }
            
            for (Lamp lamp : lamps) {
            	if(player.isBulletBased) {
	                if (Math.abs(lamp.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	lamp.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(lamp.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			lamp.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(lamp.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchSolidNoise, 0);
    	               lamp.damage(player.getDamage());
    	            }
                }
            }
            
            for (Pillar pillar : pillars) {
            	if(player.isBulletBased) {
	                if (Math.abs(pillar.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < BULLET_RANGE && player.getBullets()!=0) {
	                	pillar.damage(player.getDamage());
	                }
            	}else if(player.isShellBased) {
            		if (Math.abs(pillar.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < SHELL_RANGE && player.getShells()!=0) {
            			pillar.damage(player.getDamage());
            		}
            	}else if(player.isMelee) {
    	            if (Math.abs(pillar.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < MELEE_RANGE && player.isAlive) {
    	               AudioUtil.playAudio(punchSolidNoise, 0);
    	               pillar.damage(player.getDamage());
    	            }
                }
            }
            
            for (Hanged hanged : hangeds) {
                if(player.isMelee == true) {
    	           if (Math.abs(hanged.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
    	                AudioUtil.playAudio(punchNoise, 0);
    	           }
                }
            }
            
            for (Table table : tables) {
                if(player.isMelee == true) {
    	           if (Math.abs(table.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
    	                AudioUtil.playAudio(punchSolidNoise, 0);
    	           }
                }
            }
            
            for (Clock clock : clocks) {
                if(player.isMelee == true) {
    	           if (Math.abs(clock.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
    	                AudioUtil.playAudio(punchSolidNoise, 0);
    	           }
                }
            }
            
            for (Furnace furnace : furnaces) {
                if(player.isMelee == true) {
    	           if (Math.abs(furnace.getTransform().getPosition().sub(player.getCamera().getPos()).length()) < 1.25f && player.isAlive) {
    	                AudioUtil.playAudio(punchSolidNoise, 0);
    	           }
                }
            }
            
        }

        player.input();
    }

    /**
     * Updates everything rendered in the level.
     */
    public void update() {
        player.update();

        for (Door door : doors) {
            door.update();
        }
        
        for (SecretWall secretWall : secretWalls) {
        	secretWall.update();
        }

        for (NaziSoldier monster : naziSoldiers) {
            monster.update();
        }

        for (Medkit medkit : medkits) {
            medkit.update();
        }

        for (Tree tree : trees) {
            tree.update();
        }
        
        for (Lantern flare : flares) {
            flare.update();
        }
        
        for (LightBeam light : lightPoints) {
        	light.update();
        }
        
        for (Bones bone : bones) {
        	bone.update();
        }
        
        for (DeadNazi deadNazi : deadNazi) {
        	deadNazi.update();
        }
        
        for (DeadJew deadJew : deadJews) {
        	deadJew.update();
        }
        
        for (Food food : foods) {
        	food.update();
        }
        
        for (Bullet bullet : bullets) {
        	bullet.update();
        }
        
        for (Bag bag : bags) {
        	bag.update();
        }
        
        for (Shotgun shotgun : shotguns) {
        	shotgun.update();
        }
        
        for (Machinegun machinegun : machineguns) {
        	machinegun.update();
        }
        
        for (Dog dog : dogs) {
        	dog.update();
        }
        
        for (SsSoldier ssSoldier : ssSoldiers) {
        	ssSoldier.update();
        }
        
        for (Table table : tables) {
        	table.update();
        }
        
        for (Pipe pipe : pipes) {
        	pipe.update();
        }
        
        for (Pendule pendule : pendules) {
        	pendule.update();
        }
        
        for (Lamp lamp : lamps) {
        	lamp.update();
        }
        
        for (Hanged hanged : hangeds) {
        	hanged.update();
        }
        
        for (Pillar pillar : pillars) {
        	pillar.update();
        }
        
        for (Clock clock : clocks) {
        	clock.update();
        }
        
        for (Furnace furnace : furnaces) {
        	furnace.update();
        }
        
        for (Kitchen kitchen : kitchens) {
            kitchen.update();
        }
        
        for (NaziSergeant naziSergeants : naziSeargeants) {
        	naziSergeants.update();
        }
        
        for (Ghost ghost : ghosts) {
        	ghost.update();
        }
        
        for (Armor armor : armors) {
        	armor.update();
        }
        
        for (Helmet helmet : helmets) {
            helmet.update();
        }
        
        for (SuperShotgun superShotgun : superShotguns) {
            superShotgun.update();
        }
        
        for (Barrel barrel : barrels) {
            barrel.update();
        }

        for (Medkit medkit : removeMedkitList) {
            medkits.remove(medkit);
        }
        
        for (Food food : removeFoodList) {
            foods.remove(food);
        }
        
        for (Bullet bullet : removeBulletList) {
            bullets.remove(bullet);
        }

        for (Bag bag : removeBagList) {
        	bags.remove(bag);
        }
        
        for (Shotgun shotgun : removeShotgunList) {
        	shotguns.remove(shotgun);
        }
        
        for (Machinegun machinegun : removeMachineGunList) {
        	machineguns.remove(machinegun);
        }
        
        for (Ghost ghost : removeGhostList) {
        	ghosts.remove(ghost);
        }
        
        for (Armor armor : removeArmorList) {
        	armors.remove(armor);
        }
        
        for (Helmet helmet : removeHelmets) {
            helmets.remove(helmet);
        }
        
        for (SuperShotgun superShotgun : removeSuperShotgunList) {
            superShotguns.remove(superShotgun);
        }
        
        for (Barrel barrel : removeBarrels) {
            barrels.remove(barrel);
        }
        
        removeMedkitList.clear();
        removeFoodList.clear();
        removeBulletList.clear();
        removeBagList.clear();
        removeShotgunList.clear();
        removeMachineGunList.clear();
        removeGhostList.clear();
        removeArmorList.clear();
        removeSuperShotgunList.clear();
        removeHelmets.clear();
        removeBarrels.clear();
    }

    /**
     * Renders everything in the level.
     * @param shader to render
     */
    public void render(Shader shader) {
    	shader.getRenderingEngine().setMainCamera(player.getCamera());
    	meshRenderer.render(shader);

        for (Door door : doors) {
            door.render(shader);
        }
        
        for (SecretWall secretWall : secretWalls) {
        	secretWall.render(shader);
        }
        
        if (secretWalls.size() > 0) {
        	sortSecrets(0, secretWalls.size() - 1);
        }

        if (naziSoldiers.size() > 0) {
            sortNaziSoldiers(0, naziSoldiers.size() - 1);
        }
        
        if (dogs.size() > 0) {
            sortDogs(0, dogs.size() - 1);
        }
        
        if (ssSoldiers.size() > 0) {
            sortSsSoldiers(0, ssSoldiers.size() - 1);
        }
        
        if (naziSeargeants.size() > 0) {
            sortNaziSergeants(0, naziSeargeants.size() - 1);
        }

        for (NaziSoldier monster : naziSoldiers) {
            monster.render(shader);
        }

        for (Medkit medkit : medkits) {
            medkit.render(shader);
        }

        for (Tree tree : trees) {
            tree.render(shader);
        }
        
        for (Lantern flare : flares) {
            flare.render(shader);
        }
        
        for (LightBeam light : lightPoints) {
        	light.render(shader);
        }
        
        for (Bones bone : bones) {
        	bone.render(shader);
        }
        
        for (DeadNazi deadNazi : deadNazi) {
        	deadNazi.render(shader);
        }
        
        for (DeadJew deadJew : deadJews) {
        	deadJew.render(shader);
        }
        
        for (Pipe pipe : pipes) {
        	pipe.render(shader);
        }
        
        for (Pendule pendule : pendules) {
        	pendule.render(shader);
        }
        
        for (Lamp lamp : lamps) {
        	lamp.render(shader);
        }
        
        for (Hanged hanged : hangeds) {
        	hanged.render(shader);
        }
        
        for (Pillar pillar : pillars) {
        	pillar.render(shader);
        }
        
        for (Clock clock : clocks) {
        	clock.render(shader);
        }
        
        for (Food food : foods) {
        	food.render(shader);
        }
        
        for (Bullet bullet : bullets) {
        	bullet.render(shader);
        }
        
        for (Bag bag : bags) {
        	bag.render(shader);
        }
        
        for (Shotgun shotgun : shotguns) {
        	shotgun.render(shader);
        }
        
        for (Machinegun machinegun : machineguns) {
        	machinegun.render(shader);
        }
        
        for (Table table : tables) {
        	table.render(shader);
        }
        
        for (Dog dog : dogs) {
        	dog.render(shader);
        }
        
        for (SsSoldier ssSoldier : ssSoldiers) {
        	ssSoldier.render(shader);
        }
        
        for (NaziSergeant naziSergeants : naziSeargeants) {
        	naziSergeants.render(shader);
        }
        
        for (Ghost ghost : ghosts) {
        	ghost.render(shader);
        }
        
        for (Armor armor : armors) {
        	armor.render(shader);
        }
        
        for (Helmet helmet : helmets) {
            helmet.render(shader);
        }
        
        for (Furnace furnace : furnaces) {
        	furnace.render(shader);
        }
        
        for (Kitchen kitchen : kitchens) {
            kitchen.render(shader);
        }
        
        for (SuperShotgun superShotgun : superShotguns) {
        	superShotgun.render(shader);
        }
        
        for (Barrel barrel : barrels) {
            barrel.render(shader);
        }

        player.render(shader);
    }
    
    /**
     * Sorts all the secrets in the level.
     * @param low of the array
     * @param high of the array
     */
    private void sortSecrets(int low, int high) {
        int i = low;
        int j = high;

        SecretWall pivot = secretWalls.get(low + (high - low) / 2);
        float pivotDistance = pivot.getTransform().getPosition().sub(Transform.getCamera().getPos()).length();

        while (i <= j) {
            while (secretWalls.get(i).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() > pivotDistance) {
                i++;
            }
            while (secretWalls.get(j).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() < pivotDistance) {
                j--;
            }

            if (i <= j) {
            	SecretWall temp = secretWalls.get(i);

            	secretWalls.set(i, secretWalls.get(j));
            	secretWalls.set(j, temp);

                i++;
                j--;
            }
        }

        if (low < j) {
        	sortSecrets(low, j);
        }
        if (i < high) {
        	sortSecrets(i, high);
        }
    }

    /**
     * Sorts all the Nazi soldiers in the level.
     * @param low of the array
     * @param high of the array
     */
    private void sortNaziSoldiers(int low, int high) {
        int i = low;
        int j = high;

        NaziSoldier pivot = naziSoldiers.get(low + (high - low) / 2);
        float pivotDistance = pivot.getTransform().getPosition().sub(Transform.getCamera().getPos()).length();

        while (i <= j) {
            while (naziSoldiers.get(i).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() > pivotDistance) {
                i++;
            }
            while (naziSoldiers.get(j).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() < pivotDistance) {
                j--;
            }

            if (i <= j) {
                NaziSoldier temp = naziSoldiers.get(i);

                naziSoldiers.set(i, naziSoldiers.get(j));
                naziSoldiers.set(j, temp);

                i++;
                j--;
            }
        }

        if (low < j) {
            sortNaziSoldiers(low, j);
        }
        if (i < high) {
            sortNaziSoldiers(i, high);
        }
    }
    
    /**
     * Sorts all the Dogs in the level.
     * @param low of the array
     * @param high of the array
     */
    private void sortDogs(int low, int high) {
        int i = low;
        int j = high;

        Dog pivot = dogs.get(low + (high - low) / 2);
        float pivotDistance = pivot.getTransform().getPosition().sub(Transform.getCamera().getPos()).length();

        while (i <= j) {
            while (dogs.get(i).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() > pivotDistance) {
                i++;
            }
            while (dogs.get(j).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() < pivotDistance) {
                j--;
            }

            if (i <= j) {
                Dog temp = dogs.get(i);

                dogs.set(i, dogs.get(j));
                dogs.set(j, temp);

                i++;
                j--;
            }
        }

        if (low < j) {
        	sortDogs(low, j);
        }
        if (i < high) {
        	sortDogs(i, high);
        }
    }
        
    /**
     * Sorts all the SS soldiers in the level.
     * @param low of the array
     * @param high of the array
     */
    private void sortSsSoldiers(int low, int high) {
        int i = low;
        int j = high;

        SsSoldier pivot = ssSoldiers.get(low + (high - low) / 2);
        float pivotDistance = pivot.getTransform().getPosition().sub(Transform.getCamera().getPos()).length();

        while (i <= j) {
            while (ssSoldiers.get(i).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() > pivotDistance) {
                i++;
            }
            while (ssSoldiers.get(j).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() < pivotDistance) {
                j--;
            }

            if (i <= j) {
                SsSoldier temp = ssSoldiers.get(i);

                ssSoldiers.set(i, ssSoldiers.get(j));
                ssSoldiers.set(j, temp);

                i++;
                j--;
            }
        }

        if (low < j) {
        	sortSsSoldiers(low, j);
        }
        if (i < high) {
        	sortSsSoldiers(i, high);
        }
    }
    
    /**
     * Sorts all the Nazi sergeants in the level.
     * @param low of the array
     * @param high of the array
     */
    private void sortNaziSergeants(int low, int high) {
        int i = low;
        int j = high;

        NaziSergeant pivot = naziSeargeants.get(low + (high - low) / 2);
        float pivotDistance = pivot.getTransform().getPosition().sub(Transform.getCamera().getPos()).length();

        while (i <= j) {
            while (naziSeargeants.get(i).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() > pivotDistance) {
                i++;
            }
            while (naziSeargeants.get(j).getTransform().getPosition().sub(Transform.getCamera().getPos()).length() < pivotDistance) {
                j--;
            }

            if (i <= j) {
            	NaziSergeant temp = naziSeargeants.get(i);

                naziSeargeants.set(i, naziSeargeants.get(j));
                naziSeargeants.set(j, temp);

                i++;
                j--;
            }
        }

        if (low < j) {
        	sortNaziSergeants(low, j);
        }
        if (i < high) {
        	sortNaziSergeants(i, high);
        }
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
        
        for (SecretWall secretWall : secretWalls) {
        	if (Math.abs(secretWall.getTransform().getPosition().sub(position).length()) < 1f) {
                worked = true;
                secretWall.open(0.5f, 3f);
            }
        }

        if (playSound) {
            for (int i = 0; i < exitPoints.size(); i++) {
                if (Math.abs(exitPoints.get(i).sub(position).length()) < 1f) {
                    Auschwitz.loadLevel(exitOffsets.get(i));
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

            for (int i = 0; i < level.getWidth(); i++) {
                for (int j = 0; j < level.getHeight(); j++) {
                    if ((level.getPixel(i, j) & 0xFFFFFF) == 0) // If it's a black (wall) pixel
                    {
                        collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, blockSize.mul(new Vector2f(i, j)), blockSize));
                    }
                }
            }

            for (Door door : doors) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, door.getTransform().getPosition().getXZ(), door.getSize()));
            }
            
            for (SecretWall secretWall : secretWalls) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, secretWall.getTransform().getPosition().getXZ(), secretWall.getSize()));
            }
            /**
            for (NaziSoldier monster : naziSoldiers) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, monster.getTransform().getPosition().getXZ(), monster.getSize()));
            }
            
            for (SsSoldier ssSoldier : ssSoldiers) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, ssSoldier.getTransform().getPosition().getXZ(), ssSoldier.getSize()));
            }
            
            for (Dog dog : dogs) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, dog.getTransform().getPosition().getXZ(), dog.getSize()));
            }
            
            for (NaziSergeant naziSergeants : naziSeargeants) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, naziSergeants.getTransform().getPosition().getXZ(), naziSergeants.getSize()));
            }
            */
            for (Bones bone : bones) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, bone.getTransform().getPosition().getXZ(), bone.getSize()));
            }
            
            for (DeadJew deadJew : deadJews) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, deadJew.getTransform().getPosition().getXZ(), deadJew.getSize()));
            }
            
            for (Tree tree : trees) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, tree.getTransform().getPosition().getXZ(), tree.getSize()));
            }
            
            for (Table table : tables) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, table.getTransform().getPosition().getXZ(), table.getSize()));
            }
            
            for (Pipe pipe : pipes) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, pipe.getTransform().getPosition().getXZ(), pipe.getSize()));
            }
            
            for (Pendule pendule : pendules) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, pendule.getTransform().getPosition().getXZ(), pendule.getSize()));
            }
            
            for (Lamp lamp : lamps) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, lamp.getTransform().getPosition().getXZ(), lamp.getSize()));
            }
            
            for (Hanged jew : hangeds) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, jew.getTransform().getPosition().getXZ(), jew.getSize()));
            }
            
            for (Pillar pillar : pillars) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, pillar.getTransform().getPosition().getXZ(), pillar.getSize()));
            }
            
            for (Clock clock : clocks) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, clock.getTransform().getPosition().getXZ(), clock.getSize()));
            }
            
            for (Furnace furnace : furnaces) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, furnace.getTransform().getPosition().getXZ(), furnace.getSize()));
            }
            
            for (Barrel barrel : barrels) {
                collisionVector = collisionVector.mul(PhysicsUtil.rectCollide(oldPos2, newPos2, objectSize, barrel.getTransform().getPosition().getXZ(), barrel.getSize()));
            }
            
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
            Ghost nearestghost = null;

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
                	nearestghost = ghost;
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
	            	nearestghost.damage(player.getDamage());
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
	            	nearestghost.damage(player.getDamage());
	            }
        	}
            
            for (Barrel barrel : barrels) {
                if(barrel.boom == true) {
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
                        nearestghost.damage(barrel.damage);
                    }
                }
            }
        }

        return nearestIntersect;
    }

    /**
     * Method that generates the level for the game.
     */
    private void generateLevel() {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Integer> indices = new ArrayList<Integer>();

        for (int i = 1; i < level.getWidth() - 1; i++) {
            for (int j = 1; j < level.getHeight() - 1; j++) {
                if ((level.getPixel(i, j) & 0xFFFFFF) != 0) // If it isn't a black (wall) pixel
                {
                    if ((level.getPixel(i, j) & 0x0000FF) == 16) {
                        Transform doorTransform = new Transform();

                        boolean xDoor = (level.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (level.getPixel(i, j + 1) & 0xFFFFFF) == 0;
                        boolean yDoor = (level.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (level.getPixel(i + 1, j) & 0xFFFFFF) == 0;

                        if ((yDoor && xDoor) || !(yDoor || xDoor)) {
                            System.err.println("Level Generation Error at (" + i + ", " + j + "): Doors must be between two solid walls.");
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
                    }else if ((level.getPixel(i, j) & 0x0000FF) == 20) {
                        Transform wallTransform = new Transform();

                        boolean xSecretWall = (level.getPixel(i, j - 1) & 0xFFFFFF) == 0 && (level.getPixel(i, j + 1) & 0xFFFFFF) == 0;
                        boolean ySecretWall = (level.getPixel(i - 1, j) & 0xFFFFFF) == 0 && (level.getPixel(i + 1, j) & 0xFFFFFF) == 0;

                        if ((ySecretWall && xSecretWall) || !(ySecretWall || xSecretWall)) {
                            System.err.println("Level Generation Error at (" + i + ", " + j + "): Secret Walls must be between two solid walls.");
                            new Exception().printStackTrace();
                            System.exit(1);
                        }

                        if (ySecretWall) {
                            wallTransform.setPosition(i, 0, j);
                            secretWalls.add(new SecretWall(wallTransform, material, wallTransform.getPosition().add(new Vector3f(-0.9f, 0, 0))));
                        } else if (xSecretWall) {
                            wallTransform.setPosition((i+ (SPOT_LENGTH / 2)*2), 0, j);
                            wallTransform.setRotation(0, 90, 0);
                            secretWalls.add(new SecretWall(wallTransform, material, wallTransform.getPosition().add(new Vector3f(0, 0, -0.9f))));
                        }
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 128) {
                    	naziSoldiers.add(new NaziSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                        //bullets.add(new Bullet(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.025f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 1) {
                        player = new Player(new Vector3f((i + 0.5f) * SPOT_WIDTH, PLAYER_HEIGHT, (j + 0.5f) * SPOT_LENGTH));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 192) {
                        medkits.add(new Medkit(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 100) {
                        trees.add(new Tree(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.25f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 50) {
                    	flares.add(new Lantern(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, LEVEL_HEIGHT * 0.70f, (j + 0.5f) * SPOT_LENGTH))));
                        lightPoints.add(new LightBeam(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.04f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 51) {
                    	lightPoints.add(new LightBeam(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.04f, (j + 0.5f) * SPOT_LENGTH))));
                    	lamps.add(new Lamp(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.35f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 55) {
                        bones.add(new Bones(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 60) {
                        deadNazi.add(new DeadNazi(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                        bullets.add(new Bullet(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 70) {
                        deadJews.add(new DeadJew(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.05f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 80) {
                        foods.add(new Food(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 90) {
                        dogs.add(new Dog(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 110) {
                        ssSoldiers.add(new SsSoldier(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                        //machineguns.add(new Machinegun(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 120) {
                    	tables.add(new Table(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 121) {
                    	furnaces.add(new Furnace(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    	//lightPoints.add(new LightBeam(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.04f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 122) {
                    	kitchens.add(new Kitchen(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                        foods.add(new Food(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 123) {
                    	clocks.add(new Clock(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.25f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 130) {
                    	superShotguns.add(new SuperShotgun(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.04f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 140) {
                    	naziSeargeants.add(new NaziSergeant(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                        shotguns.add(new Shotgun(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 150) {
                    	pipes.add(new Pipe(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 151) {
                        pendules.add(new Pendule(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 152) {
                        hangeds.add(new Hanged(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 153) {
                        pillars.add(new Pillar(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 154) {
                        armors.add(new Armor(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.1f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 155) {
                        helmets.add(new Helmet(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.1f, (j + 0.5f) * SPOT_LENGTH))));
                        //barrels.add(new Barrel(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.2f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) == 160) {
                        barrels.add(new Barrel(new Transform(new Vector3f((i + 0.5f) * SPOT_WIDTH, -0.2f, (j + 0.5f) * SPOT_LENGTH))));
                    } else if ((level.getPixel(i, j) & 0x0000FF) < 128 && (level.getPixel(i, j) & 0x0000FF) > 96) {
                        int offset = (level.getPixel(i, j) & 0x0000FF) - 96;
                        exitPoints.add(new Vector3f((i + 0.5f) * SPOT_WIDTH, 0f, (j + 0.5f) * SPOT_LENGTH));
                        exitOffsets.add(offset);
                    }

                    int texX = ((level.getPixel(i, j) & 0x00FF00) >> 8) / 16;
                    int texY = texX % 4;
                    texX /= 4;

                    float XHigher = 1f - texX / NUM_TEX_X;
                    float XLower = XHigher - 1 / NUM_TEX_X;
                    float YHigher = 1f - texY / NUM_TEX_Y;
                    float YLower = YHigher - 1 / NUM_TEX_Y;
                    
                    //Generate Floor
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 1);
                    indices.add(vertices.size() + 0);
                    indices.add(vertices.size() + 3);
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 0);

                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XLower, YLower)));
                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XHigher, YLower)));
                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XHigher, YHigher)));
                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XLower, YHigher)));
                    
                    //Generate Ceiling
                    indices.add(vertices.size() + 0);
                    indices.add(vertices.size() + 1);
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 0);
                    indices.add(vertices.size() + 2);
                    indices.add(vertices.size() + 3);

                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XLower, YLower)));
                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XHigher, YLower)));
                    vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XHigher, YHigher)));
                    vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XLower, YHigher)));
                    
                    texX = ((level.getPixel(i, j) & 0xFF0000) >> 16) / 16;
                    texY = texX % 4;
                    texX /= 4;

                    XHigher = 1f - texX / NUM_TEX_X;
                    XLower = XHigher - 1 / NUM_TEX_X;
                    YHigher = 1f - texY / NUM_TEX_Y;
                    YLower = YHigher - 1 / NUM_TEX_Y;
                    
                    SecretWall.xHigher = YHigher;
                    SecretWall.xLower = XLower;
                    SecretWall.yHigher = YHigher;
                    SecretWall.yLower = YLower;

                    //Generate Walls
                    if ((level.getPixel(i, j - 1) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));

                        indices.add(vertices.size() + 0);
                        indices.add(vertices.size() + 1);
                        indices.add(vertices.size() + 2);
                        indices.add(vertices.size() + 0);
                        indices.add(vertices.size() + 2);
                        indices.add(vertices.size() + 3);

                        vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XHigher, YHigher)));
                        vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XLower, YHigher)));
                        vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XLower, YLower)));
                        vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XHigher, YLower)));
                    }
                    if ((level.getPixel(i, j + 1) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));

                        indices.add(vertices.size() + 2);
                        indices.add(vertices.size() + 1);
                        indices.add(vertices.size() + 0);
                        indices.add(vertices.size() + 3);
                        indices.add(vertices.size() + 2);
                        indices.add(vertices.size() + 0);

                        vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XHigher, YHigher)));
                        vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XLower, YHigher)));
                        vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XLower, YLower)));
                        vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XHigher, YLower)));
                    }
                    if ((level.getPixel(i - 1, j) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f(i * SPOT_WIDTH, j * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f(i * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));

                        indices.add(vertices.size() + 2);
                        indices.add(vertices.size() + 1);
                        indices.add(vertices.size() + 0);
                        indices.add(vertices.size() + 3);
                        indices.add(vertices.size() + 2);
                        indices.add(vertices.size() + 0);

                        vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XHigher, YHigher)));
                        vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XLower, YHigher)));
                        vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XLower, YLower)));
                        vertices.add(new Vertex(new Vector3f(i * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XHigher, YLower)));
                    }
                    if ((level.getPixel(i + 1, j) & 0xFFFFFF) == 0) {
                        collisionPosStart.add(new Vector2f((i + 1) * SPOT_WIDTH, j * SPOT_LENGTH));
                        collisionPosEnd.add(new Vector2f((i + 1) * SPOT_WIDTH, (j + 1) * SPOT_LENGTH));

                        indices.add(vertices.size() + 0);
                        indices.add(vertices.size() + 1);
                        indices.add(vertices.size() + 2);
                        indices.add(vertices.size() + 0);
                        indices.add(vertices.size() + 2);
                        indices.add(vertices.size() + 3);

                        vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, j * SPOT_LENGTH), new Vector2f(XHigher, YHigher)));
                        vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, 0, (j + 1) * SPOT_LENGTH), new Vector2f(XLower, YHigher)));
                        vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, (j + 1) * SPOT_LENGTH), new Vector2f(XLower, YLower)));
                        vertices.add(new Vertex(new Vector3f((i + 1) * SPOT_WIDTH, LEVEL_HEIGHT, j * SPOT_LENGTH), new Vector2f(XHigher, YLower)));
                    }
                }
            }
        }

        Vertex[] vertArray = new Vertex[vertices.size()];
        Integer[] intArray = new Integer[indices.size()];

        vertices.toArray(vertArray);
        indices.toArray(intArray);
        geometry.addVertices(vertArray, Util.toIntArray(intArray), true);
        meshRenderer = new MeshRenderer(geometry, transform, material);
        RenderingEngine.fogDensity = 0.07f;
        RenderingEngine.fogGradient = 1.5f;
        RenderingEngine.fogColor = new Vector3f(0.5f,0.5f,0.5f);
        RenderingEngine.ambientLight = new Vector3f(0.8f,0.8f,0.8f);
        RenderingEngine.directionalLights.add(new DirectionalLight(
   	 			new BaseLight(new Vector3f(1,1,1), 1f), new Vector3f(level.getWidth()/2,10,level.getHeight()/2)));
    }
	
	/**
	 * Returns all the secret walls in the array-list.
	 * @return Secret walls.
	 */
	public ArrayList<SecretWall> getSecretWalls() {
	    return secretWalls;
	}
	
	/**
	 * Returns all the Nazi soldiers in the array-list.
	 * @return Nazi Soldiers.
	 */
	public ArrayList<NaziSoldier> getNaziSoldiers() {
	    return naziSoldiers;
	}

	/**
	 * Returns all the Dogs in the array-list.
	 * @return Dogs.
	 */
	public ArrayList<Dog> getDogs() {
	    return dogs;
	}
	
	/**
	 * Returns all the SS soldiers in the array-list.
	 * @return SS soldiers.
	 */
	public ArrayList<SsSoldier> getSsSoldiers() {
	    return ssSoldiers;
	}
	
	/**
	 * Returns all the Nazi sergeants in the array-list.
	 * @return Nazi sergeants.
	 */
	public ArrayList<NaziSergeant> getNaziSergeants() {
	    return naziSeargeants;
	}
	
	/**
	 * Get access to the main player object in game.
	 * @return Player.
	 */
	public static Player getPlayer() {
	    return player;
	}
	
	/**
	  * Removes the medical kits when the player grabs it.
	  * @param medkit Medical kit.
	  */
	public static void removeMedkit(Medkit medkit) {
	    removeMedkitList.add(medkit);
	}
	    
	/**
	 * Removes the food when the player grabs it.
	 * @param food Food.
	 */
	public static void removeFood(Food food) {
	    removeFoodList.add(food);
	}
	    
	/**
	 * Removes the bullet packs when the player grabs it.
	 * @param bullet Bullet pack.
	 */
	public static void removeBullets(Bullet bullet) {
	    removeBulletList.add(bullet);
	}
	    
	/**
	 * Removes the bags when the player grabs it.
	 * @param bag Bag.
	 */
	public static void removeBags(Bag bag) {
		removeBagList.add(bag);
	}
	    
	/**
	 * Removes the shotguns when the player grabs it.
	 * @param shotgun Shotgun.
	 */
	public static void removeShotgun(Shotgun shotgun) {
		removeShotgunList.add(shotgun);
	}
	    
	/**
	 * Removes the machine-guns when the player grabs it.
	 * @param machineGun Machine-Gun.
	 */
	public static void removeMachineGun(Machinegun machineGun) {
	    removeMachineGunList.add(machineGun);
	}
	
	/**
	 * Removes the ghost when disappears.
	 * @param ghost Ghost.
	 */
	public static void removeGhost(Ghost ghost) {
	    removeGhostList.add(ghost);
	}
	
	/**
	 * Removes the armor when disappears.
	 * @param armor Armor.
	 */
	public static void removeArmor(Armor armor) {
	    removeArmorList.add(armor);
	}
	
	/**
	 * Removes the super shotguns when the player grabs it.
	 * @param sShotgun Super shotgun.
	 */
	public static void removeSuperShotgun(SuperShotgun sShotgun) {
	    removeSuperShotgunList.add(sShotgun);
	}
	
	/**
	 * Removes the helmet when disappears.
	 * @param helmet Helmet.
	 */
	public static void removeHelmet(Helmet helmet) {
	    removeHelmets.add(helmet);
	}
	
	/**
	 * Removes the barrel when disappears.
	 * @param barrel Barrels.
	 */
	public static void removeBarrel(Barrel barrel) {
	    removeBarrels.add(barrel);
	}

}
