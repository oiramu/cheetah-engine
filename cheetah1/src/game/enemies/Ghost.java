/*
 * Copyright 2018 Carlos Rodriguez.
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
package game.enemies;

import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.physics.PhysicsUtil;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import game.Auschwitz;
import game.Level;
import game.Player;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2018
 */
public class Ghost extends GameComponent {

	private static final float MAX_HEALTH = 10f;
    private static final float SHOT_ANGLE = 10.0f;
    private static final float DAMAGE_MIN = 50f;
    private static final float DAMAGE_RANGE = 30f;
    private static final float GHOST_WIDTH = 0.2f;

    private static final int STATE_IDLE = 0;
    private static final int STATE_CHASE = 1;
    private static final int STATE_ATTACK = 2;
    private static final int STATE_DYING = 3;
    private static final int STATE_DEAD = 4;
    private static final int STATE_DONE = 5;
    
    private static final String RES_LOC = "ghost/";

    private static final Clip seeNoise = AudioUtil.loadAudio(RES_LOC + "SSSSIT");
    private static final Clip shootNoise = AudioUtil.loadAudio(RES_LOC + "SSHOTGN");
    private static final Clip hitNoise = AudioUtil.loadAudio(RES_LOC + "SPOPAIN");
    private static final Clip deathNoise = AudioUtil.loadAudio(RES_LOC + "SSSDTH");

    private static ArrayList<Texture> animation;
    private static Mesh mesh;
    private static Random rand;

    private Transform transform;
    private Material material;
    private MeshRenderer meshRenderer;

    private int state;
    private boolean canAttack;
    private boolean canLook;
    private boolean dead;
    private double deathTime;
    private double health;

    /**
     * Constructor of the actual enemy.
     * @param transform the transform of the data.
     */
    public Ghost(Transform transform) {
        if (rand == null) {
            rand = new Random();
        }

        if (animation == null) {
            animation = new ArrayList<Texture>();

            animation.add(new Texture(RES_LOC + "GHSTA0"));
            //animation.add(new Texture(RES_LOC + "GHSTB0"));
            animation.add(new Texture(RES_LOC + "GHSTC0"));
            //animation.add(new Texture(RES_LOC + "GHSTD0"));
            
            animation.add(new Texture(RES_LOC + "GHSTE0"));
            animation.add(new Texture(RES_LOC + "GHSTF0"));
            animation.add(new Texture(RES_LOC + "GHSTG0"));
            animation.add(new Texture(RES_LOC + "GHSTH0"));
        }

        if (mesh == null) {
            final float sizeY = 0.75f;
            final float sizeX = 1.0f;

            final float offsetX = 0.00f;
            final float offsetY = 0.00f;

            final float texMinX = -offsetX;
            final float texMaxX = -1 - offsetX;
            final float texMinY = -offsetY;
            final float texMaxY = 1 - offsetY;

            Vertex[] verts = new Vertex[]{new Vertex(new Vector3f(-sizeX, 0, 0), new Vector2f(texMaxX, texMaxY)),
                new Vertex(new Vector3f(-sizeX, sizeY, 0), new Vector2f(texMaxX, texMinY)),
                new Vertex(new Vector3f(sizeX, sizeY, 0), new Vector2f(texMinX, texMinY)),
                new Vertex(new Vector3f(sizeX, 0, 0), new Vector2f(texMinX, texMaxY))};

            int[] indices = new int[]{0, 1, 2,
                                      0, 2, 3};

            mesh = new Mesh(verts, indices, true);
        }

        this.transform = transform;
        this.material = new Material(animation.get(0));
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        this.state = 0;
        this.canAttack = true;
        this.canLook = true;
        this.dead = false;
        this.deathTime = 0.0;
        this.health = MAX_HEALTH;
    }

    float offsetX = 0;
    float offsetY = 0;

    /**
     * Updates the enemy every single frame.
     * @param delta of time
     */
    public void update(double delta) {
        //Set Height
        transform.setPosition(transform.getPosition().getX(), 0f, transform.getPosition().getZ());
        
        Vector3f playerDistance = transform.getPosition().sub(Level.getPlayer().getCamera().getPos());

        Vector3f orientation = playerDistance.normalized();
        float distance = playerDistance.length();
        setDistance(distance);

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() > 0) {
            angle = 180 + angle;
        }

        transform.setRotation(0, angle + 90, 0);

        //Action/Animation
        double time = Time.getTime();

        if (!dead && health <= 0) {
            dead = true;
            deathTime = time;
            state = STATE_DYING;
            seeNoise.stop();
            shootNoise.stop();
            hitNoise.stop();
            AudioUtil.playAudio(deathNoise, distance);
        }

        if (!dead) {
            Player player = Level.getPlayer();

            Vector2f playerDirection = transform.getPosition().sub(
                    player.getCamera().getPos().add(
                            new Vector3f(player.getSize().getX(), 0, player.getSize().getY()).mul(0.5f))).getXZ().normalized();

            if (state == STATE_IDLE) {
                double timeDecimals = (time - (double) ((int) time));

                if (timeDecimals >= 0.5) {
                    material.setDiffuse(animation.get(1));
                    canLook = true;
                } else {
                    material.setDiffuse(animation.get(0));
                    if (canLook) {
                        Vector2f lineStart = transform.getPosition().getXZ();
                        Vector2f lineEnd = lineStart.sub(playerDirection.mul(1000.0f));

                        Vector2f nearestIntersect = Auschwitz.getLevel().checkIntersections(lineStart, lineEnd, false);
                        Vector2f playerIntersect = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, player.getCamera().getPos().getXZ(), player.getSize());

                        if (playerIntersect != null && (nearestIntersect == null
                                || nearestIntersect.sub(lineStart).length() > playerIntersect.sub(lineStart).length())) {
                            AudioUtil.playAudio(seeNoise, distance);
                            state = STATE_CHASE;
                        }

                        canLook = false;
                    }
                }
            } else if (state == STATE_CHASE) {
                if (rand.nextDouble() < 0.5f * delta) {
                    state = STATE_ATTACK;
                }

                if (distance > 0.225f) {
                    orientation.setY(0);
                    float moveSpeed = 5f;

                    Vector3f oldPos = transform.getPosition();
                    Vector3f newPos = transform.getPosition().add(orientation.mul((float) (-moveSpeed * delta)));

                    Vector3f collisionVector = Auschwitz.getLevel().checkCollisions(oldPos, newPos, GHOST_WIDTH, GHOST_WIDTH);

                    Vector3f movementVector = collisionVector.mul(orientation.normalized());

                    /**
                     * Just in case you want opening door ghosts.
                    if (!movementVector.equals(orientation.normalized())) {
                        Auschwitz.getLevel().openDoors(transform.getPosition(), false);
                    }*/

                    if (movementVector.length() > 0) {
                        transform.setPosition(transform.getPosition().add(movementVector.mul((float) (-moveSpeed * delta))));
                    }
                } else {
                    state = STATE_ATTACK;
                }

                if (state == STATE_CHASE) {
                    double timeDecimals = (time - (double) ((int) time));

                    while (timeDecimals > 0.5) {
                        timeDecimals -= 0.5;
                    }

                    timeDecimals *= 1.5f;

                    if (timeDecimals <= 0.25f) {
                        material.setDiffuse(animation.get(1));
                    } else if (timeDecimals <= 0.5f) {
                        material.setDiffuse(animation.get(0));
                    } else if (timeDecimals <= 0.75f) {
                        material.setDiffuse(animation.get(1));
                    } else {
                        material.setDiffuse(animation.get(0));
                    }
                }
            }

            if (state == STATE_ATTACK) {
                double timeDecimals = (time - (double) ((int) time));

                if (timeDecimals <= 0.25f) {
                    material.setDiffuse(animation.get(0));
                } else if (timeDecimals <= 0.5f) {
                    material.setDiffuse(animation.get(1));
                } else if (timeDecimals <= 0.7f) {
                    if (canAttack) {
                        Vector2f shootDirection = playerDirection.rotate((rand.nextFloat() - 0.5f) * SHOT_ANGLE);

                        Vector2f lineStart = transform.getPosition().getXZ();
                        Vector2f lineEnd = lineStart.sub(shootDirection.mul(1000.0f));

                        Vector2f nearestIntersect = Auschwitz.getLevel().checkIntersections(lineStart, lineEnd, false);
                        canAttack = false;

                        Vector2f playerIntersect = PhysicsUtil.lineIntersectRect(lineStart, lineEnd, player.getCamera().getPos().getXZ(), player.getSize());

                        if (playerIntersect != null && (nearestIntersect == null
                                || nearestIntersect.sub(lineStart).length() > playerIntersect.sub(lineStart).length())) {

                        	float damage;
                            if(player.getHealth() <= 0) {
                            	damage = 0;
                            	state = STATE_DONE;
                            }else {
                            	damage = DAMAGE_MIN + rand.nextFloat() * DAMAGE_RANGE;
                            	if(player.isArmorb() == false) {
                            		player.addHealth((int) -damage, "Ghost");
                            	}else {
                            		player.addArmor((int) -damage);
                            	}
                            }
                        }
                        AudioUtil.playAudio(shootNoise, distance);
                    }

                    material.setDiffuse(animation.get(0));
                } else {
                    canAttack = true;
                    material.setDiffuse(animation.get(1));
                    state = STATE_CHASE;
                }
            }
        }

        if (state == STATE_DYING) {
            dead = true;

            final float time1 = 0.1f;
            final float time2 = 0.3f;
            final float time3 = 0.45f;

            if (time <= deathTime + 0.2f) {
                material.setDiffuse(animation.get(2));
            } else if (time > deathTime + time1 && time <= deathTime + time2) {
                material.setDiffuse(animation.get(3));
            } else if (time > deathTime + time2 && time <= deathTime + time3) {
                material.setDiffuse(animation.get(4));
            } else if (time > deathTime + time3) {
                state = STATE_DEAD;
            }
        }

        if (state == STATE_DEAD) {
            dead = true;
            material.setDiffuse(animation.get(5));
            state = STATE_DONE;
        }
        
        if (state == STATE_DONE) {
        	Level.removeGhost(this);
        }
    }

    /**
     * Method that calculates the damage that the enemy receives.
     * @param amt amount.
     */
    public void damage(int amt) {
        if (state == STATE_IDLE) {
            state = STATE_CHASE;
        }

        health -= amt;

        if (health > 0) {
            AudioUtil.playAudio(hitNoise, transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length());
        }
    }

    /**
     * Method that renders the enemy's mesh.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {
        Vector3f prevPosition = transform.getPosition();
        transform.setPosition(new Vector3f(transform.getPosition().getX() + offsetX, transform.getPosition().getY() + offsetY, transform.getPosition().getZ()));

        meshRenderer.render(shader, renderingEngine);

        transform.setPosition(prevPosition);
    }

    /**
	 * Gets the enemy's actual transformation.
	 * @return the enemy's transform data.
	 */
    public Transform getTransform() {return transform;}

    /**
	 * Gets if the enemy is dead or not.
	 * @return the enemy's life state.
	 */
    public boolean isAlive() {return !dead;}

    /**
     * Returns the enemy's size depending on the enemy's own width,
     * all of this in a Vector2f.
     * @return vector with the size.
     */
    public Vector2f getSize() {return new Vector2f(GHOST_WIDTH, GHOST_WIDTH);}

}