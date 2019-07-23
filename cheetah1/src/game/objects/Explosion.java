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
package game.objects;

import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
import engine.components.Attenuation;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.components.PointLight;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import game.Auschwitz;
import game.Level;

/**
 *
 * @author Carlos Rodriguez.
 * @version 1.0
 * @since 2018
 */
public class Explosion extends GameComponent {
	
	private static final String 		RES_LOC = "explotion/";
	private static final int 			STATE_BOOM = 1;
	private static final int 			STATE_DEAD = 2;
    
    private static Mesh 				mesh;
    
    private Material 					material;
    private MeshRenderer 				meshRenderer;
    private Transform 					transform;
    
    public PointLight 					light;
    
    private float 						sizeX;
    private int 						state;
	private double 						temp;
    
    private static ArrayList<Texture> 	animation;
    
    private static ArrayList<Clip> 		boomNoice;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
	public Explosion(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();
            
            animation.add(new Texture(RES_LOC + "BEXPD0"));
            animation.add(new Texture(RES_LOC + "BEXPE0"));
            animation.add(new Texture(RES_LOC + "BEXPF0"));
            animation.add(new Texture(RES_LOC + "BEXPG0"));
            animation.add(new Texture(RES_LOC + "BEXPH0"));
            animation.add(new Texture(RES_LOC + "BEXPI0"));
            animation.add(new Texture(RES_LOC + "BEXPJ0"));
            animation.add(new Texture(RES_LOC + "BEXPK0"));
            animation.add(new Texture(RES_LOC + "BEXPL0"));
            animation.add(new Texture(RES_LOC + "BEXPM0"));
            animation.add(new Texture(RES_LOC + "BEXPN0"));
            animation.add(new Texture(RES_LOC + "BEXPO0"));
            animation.add(new Texture(RES_LOC + "BEXPP0"));
            animation.add(new Texture(RES_LOC + "BEXPQ0"));
        }
    	
    	if(boomNoice == null) {
    		boomNoice = new ArrayList<Clip>();
    		
    		for (int i = 1; i < 3; i++)
    			boomNoice.add(AudioUtil.loadAudio(RES_LOC + "Explode"+i));
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
        this.state = STATE_BOOM;
        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        if(light == null) {
	        light = new PointLight(new Vector3f(0.9f,0.7f,0.2f), 0.8f, 
				   new Attenuation(0,0,1), getTransform().getPosition());
	        light.addToEngine();
        }
    	AudioUtil.playAudio(boomNoice.get(new Random().nextInt(boomNoice.size())),
    			transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length());
    	if(getTransform().getPosition().sub(Level.getPlayer().getCamera().getPos()).length() < 1.0f) {
			if(!Level.getPlayer().isArmor())
				Level.getPlayer().addHealth((int) -85, "Explosion");
        	else
        		Level.getPlayer().addArmor((int) -85);
		}
        if(Auschwitz.getLevel().getShootingObjective() != null)
	        if(getTransform().getPosition().sub(Auschwitz.getLevel().getShootingObjective().getTransform().getPosition()).length() < 1.0f)
	        	Auschwitz.getLevel().getShootingObjective().damage(Level.getPlayer().getDamage());
    }

    /**
     * Method that updates the object's data.
     * @param delta of time
     */
    public void update(double delta) {
    	Vector3f playerDistance = transform.getPosition().sub(Level.getPlayer().getCamera().getPos());
        Vector3f orientation = playerDistance.normalized();
		float distance = playerDistance.length();
		setDistance(distance);
		
        if (state == STATE_BOOM) {
            float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

            if (orientation.getX() > 0) {
                angle = 180 + angle;
            }

            transform.setRotation(0, angle + 90, 0);
            
            double time = Time.getTime();
            temp = delta;
            light.setPosition(new Vector3f(light.getPosition().getX(), 0.05f * (float)(Math.sin(temp*2.5)+1.0/2.0) + 0.45f, light.getPosition().getZ()));
        	double timeDecimals = (time - (double) ((int) time));
            timeDecimals *= 3.75f;
            
            if (timeDecimals <= 0.25f) {
                material.setDiffuse(animation.get(0));
            } else if (timeDecimals <= 0.5f) {
                material.setDiffuse(animation.get(1));
            } else if (timeDecimals <= 0.75f) {
                material.setDiffuse(animation.get(2));
            } else if (timeDecimals <= 1f) {
                material.setDiffuse(animation.get(3));
            } else if (timeDecimals <= 1.25f) {
                material.setDiffuse(animation.get(4));
            } else if (timeDecimals <= 1.5f) {
                material.setDiffuse(animation.get(5));
            } else if (timeDecimals <= 1.75f) {
                material.setDiffuse(animation.get(6));
            } else if (timeDecimals <= 2f) {
                material.setDiffuse(animation.get(7));
            } else if (timeDecimals <= 2.25f) {
                material.setDiffuse(animation.get(8));
            } else if (timeDecimals <= 2.5f) {
                material.setDiffuse(animation.get(9));
            } else if (timeDecimals <= 2.75f) {
                material.setDiffuse(animation.get(10));
            } else if (timeDecimals <= 3f) {
                material.setDiffuse(animation.get(11));
            } else if (timeDecimals <= 3.25f) {
                material.setDiffuse(animation.get(12));
            } else if (timeDecimals <= 3.5f) {
            	material.setDiffuse(animation.get(13));
            } else {
            	light.removeToEngine();
                state = STATE_DEAD;
            }
        }

    }

    /**
     * Method that renders the object's mesh to screen.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {
    	if(state == STATE_BOOM)
    		meshRenderer.render(shader, renderingEngine);
    }
    
    /**
     * Returns the explosion state.
     * @return state
     */
    public int getState() {return state;}
    
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
    
}