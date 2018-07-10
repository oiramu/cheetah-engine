/*
 * Copyright 2018 Julio Vergara.
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
package game.objects;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
import engine.core.ResourceLoader;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.MeshRenderer;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import game.Level;

/**
 *
 * @author Carlos Rodriguez.
 * @version 1.0
 * @since 2018
 */
public class Barrel {
	
	private static final String RES_LOC = "barrel/";
	private static final String RES_LOC_2 = "explotion/";
	private static final int STATE_IDLE = 0;
	private static final int STATE_BOOM = 1;
	private static final int STATE_DEAD = 2;
	public int damage;
	public boolean boom;
	private int state;
    
    private static Mesh mesh;
    private Material material;
    private MeshRenderer meshRenderer;
    
    private float sizeX;
    private double health;
    private boolean dead;
    
    private static ArrayList<Texture> animation;
    
    private static final Clip breakNoice = ResourceLoader.loadAudio(RES_LOC_2 + "EXPLOSIO");

    private Transform transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
	public Barrel(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();

            animation.add(ResourceLoader.loadTexture(RES_LOC + "BURBA0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "BEXPC0"));
            
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPD0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPE0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPF0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPG0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPH0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPI0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPJ0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPK0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPL0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPM0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPN0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPO0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPP0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC_2 + "BEXPQ0"));
        }
    	
        if (mesh == null) {
            mesh = new Mesh();

            float sizeY = 1.2f;
            sizeX = (float) ((double) sizeY / (0.8333333333333333 * 2.0));

            float offsetX = 0.05f;
            float offsetY = 0.01f;

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

            mesh.addVertices(verts, indices, true);
        }
        
        this.material = new Material(animation.get(0), new Vector3f(1,1,1));
        this.state = STATE_IDLE;
        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, this.transform, material);
        this.dead = false;
        this.health = 200;
        this.damage = 0;
        this.boom = false;
    }

    /**
     * Method that updates the object's data.
     */
    public void update() {
        Vector3f playerDistance = transform.getPosition().sub(Transform.getCamera().getPos());

        Vector3f orientation = playerDistance.normalized();
		float distance = playerDistance.length();

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() > 0) {
            angle = 180 + angle;
        }

        transform.setRotation(0, angle + 90, 0);
        
        double time = (double) Time.getTime() / Time.SECOND;
        
        if(!dead) {
	        if (state == STATE_IDLE) {
	            material.setTexture(animation.get(0));
	        }
        }
        
        if (!dead && health <= 0) {
            dead = true;
            state = STATE_BOOM;
            AudioUtil.playAudio(breakNoice, distance);
        }
        
        if (state == STATE_BOOM) {
        	double timeDecimals = (time - (double) ((int) time));
            timeDecimals *= 4.5f;

        	if (timeDecimals <= 0.25f) {
        		boom = true;
        		dead = true;
                material.setTexture(animation.get(1));
            } else if (timeDecimals <= 0.5f) {
                material.setTexture(animation.get(2));
            } else if (timeDecimals <= 0.75f) {
            	if(distance<1) {
                	damage = 200;
                	if(!Level.getPlayer().getArmorb())
                		Level.getPlayer().addHealth(-damage);
                	else
                		Level.getPlayer().addArmori(-damage);
                }
                material.setTexture(animation.get(3));
            } else if (timeDecimals <= 1f) {
                material.setTexture(animation.get(4));
            } else if (timeDecimals <= 1.25f) {
                material.setTexture(animation.get(5));
            } else if (timeDecimals <= 1.5f) {
                material.setTexture(animation.get(6));
            } else if (timeDecimals <= 1.75f) {
                material.setTexture(animation.get(7));
            } else if (timeDecimals <= 2f) {
                material.setTexture(animation.get(8));
            } else if (timeDecimals <= 2.25f) {
                material.setTexture(animation.get(9));
            } else if (timeDecimals <= 2.5f) {
                material.setTexture(animation.get(10));
            } else if (timeDecimals <= 2.75f) {
                material.setTexture(animation.get(11));
            } else if (timeDecimals <= 3f) {
                material.setTexture(animation.get(12));
            } else if (timeDecimals <= 3.25f) {
                material.setTexture(animation.get(13));
            } else if (timeDecimals <= 3.5f) {
                material.setTexture(animation.get(14));
            } else if (timeDecimals <= 3.75f) {
            	material.setTexture(animation.get(15));
            } else {
                state = STATE_DEAD;
            }
        }
        
        if (state == STATE_DEAD) {
        	Level.removeBarrel(this);
        }

    }

    /**
     * Method that renders the object's mesh to screen.
     */
    public void render() {
        meshRenderer.render();
    }
    
    /**
     * Gets the transform of the object in projection.
     * @return transform.
     */
	public Transform getTransform() {
		return transform;
	}
	
	/**
	 * Gets the size of the object in the 3D space and saves it on a vector.
	 * @return the vector size.
	 */
    public Vector2f getSize() {
        return new Vector2f(sizeX, sizeX);
    }
    
    /**
     * Method that calculates the damage.
     * @param amt amount.
     */
    public void damage(int amt) {
        health -= amt;
    }
    
}
