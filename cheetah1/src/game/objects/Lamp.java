/*
 * Copyright 2017 Julio Vergara.
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
import engine.components.BaseLight;
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

/**
 *
 * @author Julio Vergara.
 * @version 1.0
 * @since 2017
 */
public class Lamp {
	
	private static final String RES_LOC = "lamp/";
	private static final int STATE_IDLE = 0;
	private static final int STATE_DEAD = 1;
	private int state;
	
	private static final Clip breakNoice = AudioUtil.loadAudio(RES_LOC + "WINBREA");
    
    private static Mesh mesh;
    private Material material;
    private MeshRenderer meshRenderer;
    private PointLight light;
    
    private float sizeX;
    private double health;
    private boolean dead;
    
    private static ArrayList<Texture> animation;

    private Transform transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
    public Lamp(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();

            animation.add(new Texture(RES_LOC + "TLMPA0"));
            animation.add(new Texture(RES_LOC + "TLMPB0"));
            animation.add(new Texture(RES_LOC + "TLMPC0"));
            animation.add(new Texture(RES_LOC + "TLMPD0"));
            
            animation.add(new Texture(RES_LOC + "TLMPE0"));
        }
    	
        if (mesh == null) {
            float sizeY = 1f;
            sizeX = (float) ((double) sizeY / (3.478260869565217f * 2.0));

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
        
        this.material = new Material(animation.get(0), new Vector3f(1,1,1));
        this.state = STATE_IDLE;
        this.transform = transform;
        this.light = new PointLight(new BaseLight(new Vector3f(0.5f,0.5f,0.6f), 0.8f), 
        		new Attenuation(0,0,1), new Vector3f(getTransform().getPosition().getX(), 0.1f, 
        				getTransform().getPosition().getZ()), 6);
        RenderingEngine.addPointLight(light);
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        this.dead = false;
        this.health = 20;
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
        
        if (!dead && health <= 0) {
            dead = true;
            state = STATE_DEAD;
            AudioUtil.playAudio(breakNoice, distance);
        }
        
        if (state == STATE_IDLE) {
        	double timeDecimals = (time - (double) ((int) time));

            timeDecimals *= 1.25f;

        	if (timeDecimals <= 0.25f) {
                material.setTexture(animation.get(0));
            } else if (timeDecimals <= 0.5f) {
                material.setTexture(animation.get(1));
            } else if (timeDecimals <= 0.75f) {
                material.setTexture(animation.get(2));
            } else if (timeDecimals <= 1f) {
                material.setTexture(animation.get(3));
            } else if (timeDecimals <= 1.25f) {
                material.setTexture(animation.get(2));
            } else {
                material.setTexture(animation.get(1));
            }
        }
        
        if (state == STATE_DEAD) {
        	RenderingEngine.removePointLight(light);
            dead = true;
            material.setTexture(animation.get(4));
        }

    }

    /**
     * Method that renders the object's mesh to screen.
     * @param shader to render
     */
    public void render(Shader shader) {
        meshRenderer.render(shader);
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
