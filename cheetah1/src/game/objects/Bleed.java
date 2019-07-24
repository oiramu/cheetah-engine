/*
 * Copyright 2019 Carlos Rodriguez.
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

import static engine.components.Constants.*;

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
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import game.Level;

/**
 *
 * @author Carlos Rodriguez.
 * @version 1.0
 * @since 2019
 */
public class Bleed extends GameComponent {
	
	private static final String 		RES_LOC = "bleed/";
	private static final int 			STATE_BLEED = 1;
	private static final int 			STATE_DONE = 2;
	private int 						state;
	private int 						bleedSeed;
    
    private static Mesh 				mesh;
    
    private Material 					material;
    private MeshRenderer 				meshRenderer;
    private Transform 					transform;
    
    private float 						sizeX;
    private float 						upAmt = 0;
    
    private static ArrayList<Texture> 	animation;
    private static ArrayList<Clip> 		sounds;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
	public Bleed(Transform transform) {
		
		this.bleedSeed = new Random().nextInt(4) + 1;
		animation = new ArrayList<Texture>();
    	
		if(bleedSeed == 1) {
            animation.add(new Texture(RES_LOC + "BLHTA0"));
            animation.add(new Texture(RES_LOC + "BLHTB0"));
            animation.add(new Texture(RES_LOC + "BLHTC0"));
            animation.add(new Texture(RES_LOC + "BLHTD0"));
            animation.add(new Texture(RES_LOC + "BLHTE0"));
            animation.add(new Texture(RES_LOC + "BLHTF0"));
            animation.add(new Texture(RES_LOC + "BLHTG0"));
            animation.add(new Texture(RES_LOC + "BLHTH0"));
		} else if(bleedSeed == 2) { 
            animation.add(new Texture(RES_LOC + "BLHMA0"));
            animation.add(new Texture(RES_LOC + "BLHMB0"));
            animation.add(new Texture(RES_LOC + "BLHMC0"));
            animation.add(new Texture(RES_LOC + "BLHMD0"));
            animation.add(new Texture(RES_LOC + "BLHME0"));
            animation.add(new Texture(RES_LOC + "BLHMF0"));
            animation.add(new Texture(RES_LOC + "BLHMG0"));
            animation.add(new Texture(RES_LOC + "BLHMH0"));
		} else if(bleedSeed == 3) {
            animation.add(new Texture(RES_LOC + "BLHNA0"));
            animation.add(new Texture(RES_LOC + "BLHNB0"));
            animation.add(new Texture(RES_LOC + "BLHNC0"));
            animation.add(new Texture(RES_LOC + "BLHND0"));
            animation.add(new Texture(RES_LOC + "BLHNE0"));
            animation.add(new Texture(RES_LOC + "BLHNF0"));
            animation.add(new Texture(RES_LOC + "BLHNG0"));
            animation.add(new Texture(RES_LOC + "BLHNH0"));
		} else if(bleedSeed == 4) { 
            animation.add(new Texture(RES_LOC + "BSPRA0"));
            animation.add(new Texture(RES_LOC + "BSPRB0"));
            animation.add(new Texture(RES_LOC + "BSPRC0"));
            animation.add(new Texture(RES_LOC + "BSPRD0"));
            animation.add(new Texture(RES_LOC + "BSPRE0"));
            animation.add(new Texture(RES_LOC + "BSPRF0"));
            animation.add(new Texture(RES_LOC + "BSPRG0"));
            animation.add(new Texture(RES_LOC + "BSPRH0"));
		}
		
		sounds = new ArrayList<Clip>();
			
			for (int i = 1; i < 8; i++)
				sounds.add(AudioUtil.loadAudio(RES_LOC + "SOUND" + i));
    	
        if (mesh == null) {
            float sizeY = 0.6f;
            sizeX = (float) ((double) sizeY / (sizeY * 2.0));

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
        this.componentType = "particle";
        this.material = new Material(animation.get(0));
        this.state = STATE_BLEED;
        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
    	AudioUtil.playAudio(sounds.get(new Random().nextInt(sounds.size())), transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length());
    }

    /**
     * Method that updates the object's data.
     * @param delta of time
     */
    public void update(double delta) {
    	Vector3f playerDistance = transform.getPosition().sub(Level.getPlayer().getCamera().getPos());
        Vector3f orientation = playerDistance.normalized();
		float distance = playerDistance.length();
		setDistance(distance + (int) (POP_IN/PARTICLES_POP_IN));
		
        if (state == STATE_BLEED) {
            float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

            if (orientation.getX() > 0)
                angle = 180 + angle;

            transform.setRotation(0, angle + 90, 0);
            
            upAmt -= GRAVITY * delta;
            transform.setPosition(transform.getPosition().add(new Vector3f(0, (float) (upAmt * delta), 0)));
            if(transform.getPosition().getY() == 0) {
            	upAmt = 0;
            	transform.getPosition().setY(0);
            }
            
            double time = Time.getTime();
            double timeDecimals = (time - (double) ((int) time));
            timeDecimals *= 2.25f;
            
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
            } else {
                state = STATE_DONE;
            }
        }
        
        if(state == STATE_DONE)
        	Level.removeBleeding(this);

    }

    /**
     * Method that renders the object's mesh to screen.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) { meshRenderer.render(shader, renderingEngine); }
    
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