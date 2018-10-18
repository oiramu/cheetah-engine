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
import engine.components.Attenuation;
import engine.components.MeshRenderer;
import engine.components.PointLight;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.Shader;
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
	private double fireTime;
    
    private static Mesh mesh;
    private Material material;
    private MeshRenderer meshRenderer;
    private PointLight light;
    
    private float sizeX;
    private double health;
    private boolean dead;
    
    private static ArrayList<Texture> animation;
    
    private static final Clip breakNoice = AudioUtil.loadAudio(RES_LOC_2 + "EXPLOSIO");

    private Transform transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
	public Barrel(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();

            animation.add(new Texture(RES_LOC + "BURBA0"));
            animation.add(new Texture(RES_LOC + "BEXPC0"));
            
            animation.add(new Texture(RES_LOC_2 + "BEXPD0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPE0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPF0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPG0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPH0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPI0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPJ0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPK0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPL0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPM0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPN0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPO0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPP0"));
            animation.add(new Texture(RES_LOC_2 + "BEXPQ0"));
        }
    	
        if (mesh == null) {
            float sizeY = 1.2f;
            sizeX = (float) ((double) sizeY / (0.8333333333333333 * 2.0));

            float offsetX = 0.0f;
            float offsetY = 0.0f;

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

            mesh = new Mesh(verts, indices, true);
        }
        this.material = new Material(animation.get(0));
        this.state = STATE_IDLE;
        this.transform = transform;
        if(light == null)
	        this.light = new PointLight(new Vector3f(0.9f,0.7f,0.2f), 0.8f, 
	        		new Attenuation(0,0,1), new Vector3f(getTransform().getPosition().getX(), 0, 
	        				getTransform().getPosition().getZ()));
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        this.dead = false;
        this.health = 200;
        this.damage = 0;
        this.fireTime = 0;
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
	            material.setDiffuse(animation.get(0));
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
                material.setDiffuse(animation.get(1));
            } else if (timeDecimals <= 0.5f) {
            	fireTime = (double) Time.getTime() / Time.SECOND;
                material.setDiffuse(animation.get(2));
            } else if (timeDecimals <= 0.75f) {
            	if(distance<1) {
                	damage = 200;
                	if(!Level.getPlayer().getArmorb())
                		Level.getPlayer().addHealth(-damage);
                	else
                		Level.getPlayer().addArmori(-damage);
                }
                material.setDiffuse(animation.get(3));
            } else if (timeDecimals <= 1f) {
                material.setDiffuse(animation.get(4));
            } else if (timeDecimals <= 1.25f) {
                material.setDiffuse(animation.get(5));
            } else if (timeDecimals <= 1.5f) {
                material.setDiffuse(animation.get(6));
            } else if (timeDecimals <= 1.75f) {
                material.setDiffuse(animation.get(7));
            } else if (timeDecimals <= 2f) {
                material.setDiffuse(animation.get(8));
            } else if (timeDecimals <= 2.25f) {
                material.setDiffuse(animation.get(9));
            } else if (timeDecimals <= 2.5f) {
                material.setDiffuse(animation.get(10));
            } else if (timeDecimals <= 2.75f) {
                material.setDiffuse(animation.get(11));
            } else if (timeDecimals <= 3f) {
                material.setDiffuse(animation.get(12));
            } else if (timeDecimals <= 3.25f) {
                material.setDiffuse(animation.get(13));
            } else if (timeDecimals <= 3.5f) {
                material.setDiffuse(animation.get(14));
            } else if (timeDecimals <= 3.75f) {
            	material.setDiffuse(animation.get(15));
            } else {
                state = STATE_DEAD;
            }
        }
        
        if (state == STATE_DEAD) {
        	boom = false;
        	Level.removeBarrel(this);
        }

    }

    /**
     * Method that renders the object's mesh to screen.
     * @param shader to render
     */
    public void render(Shader shader) {
    	double time = (double) Time.getTime() / Time.SECOND;
    	if((double)time < fireTime + 1.0f)
    		shader.getRenderingEngine().addLight(light);
    	else
    		shader.getRenderingEngine().removeLight(light);
    	meshRenderer.render(shader);
    }
    
    /**
     * Gets the transform of the object in projection.
     * @return transform.
     */
	public Transform getTransform() {return transform;}
	
	/**
	 * Gets the size of the object in the 3D space and saves it on a vector.
	 * @return the vector size.
	 */
    public Vector2f getSize() {return new Vector2f(sizeX, sizeX);}
    
    /**
     * Method that calculates the damage.
     * @param amt amount.
     */
    public void damage(int amt) {health -= amt;}
    
}