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
 * @since 2019
 */
public class Fire extends GameComponent {
	
	private static final String 		RES_LOC = "fire/";
	private static final int 			STATE_FIRE = 1;
	private static final int 			STATE_DONE = 2;
	private static final int			DAMAGE = 15;
	private int 						state;
	private int 						fireSeed;
    
	private PointLight 					light;
    private static Mesh 				mesh;
    private Clip						fireSound;
    
    private Material 					material;
    private MeshRenderer 				meshRenderer;
    private Transform 					transform;
    
    private float 						sizeX;
    private double 						temp;
    
    private static ArrayList<Texture> 	animation;
    private static ArrayList<Clip> 		sounds;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
	public Fire(Transform transform) {
		
		this.fireSeed = new Random().nextInt(3) + 1;
    	
		if(fireSeed == 1) {
	    	if (animation == null) {
	            animation = new ArrayList<Texture>();
	            
	            animation.add(new Texture(RES_LOC + "F1REA0"));
	            animation.add(new Texture(RES_LOC + "F1REB0"));
	            animation.add(new Texture(RES_LOC + "F1REC0"));
	            animation.add(new Texture(RES_LOC + "F1RED0"));
	            animation.add(new Texture(RES_LOC + "F1REE0"));
	            animation.add(new Texture(RES_LOC + "F1REF0"));
	            animation.add(new Texture(RES_LOC + "F1REG0"));
	            animation.add(new Texture(RES_LOC + "F1REH0"));
	        }
		} else if(fireSeed == 2) {
			if (animation == null) {
	            animation = new ArrayList<Texture>();
	            
	            animation.add(new Texture(RES_LOC + "F2REA0"));
	            animation.add(new Texture(RES_LOC + "F2REB0"));
	            animation.add(new Texture(RES_LOC + "F2REC0"));
	            animation.add(new Texture(RES_LOC + "F2RED0"));
	            animation.add(new Texture(RES_LOC + "F2REE0"));
	            animation.add(new Texture(RES_LOC + "F2REF0"));
	            animation.add(new Texture(RES_LOC + "F2REG0"));
	            animation.add(new Texture(RES_LOC + "F2REH0"));
	        }
		} else if(fireSeed == 3) {
			if (animation == null) {
	            animation = new ArrayList<Texture>();
	            
	            animation.add(new Texture(RES_LOC + "FLMEA0"));
	            animation.add(new Texture(RES_LOC + "FLMEB0"));
	            animation.add(new Texture(RES_LOC + "FLMEC0"));
	            animation.add(new Texture(RES_LOC + "FLMED0"));
	            animation.add(new Texture(RES_LOC + "FLMEE0"));
	            animation.add(new Texture(RES_LOC + "FLMEF0"));
	            animation.add(new Texture(RES_LOC + "FLMEG0"));
	            animation.add(new Texture(RES_LOC + "FLMEH0"));
	        }
		}
		
		if(sounds == null) {
			sounds = new ArrayList<Clip>();
			
			for (int i = 1; i < 3; i++)
				sounds.add(AudioUtil.loadAudio(RES_LOC + "FIRE" + i));
		}
    	
        if (mesh == null) {
            float sizeY = 0.75f;
            if(fireSeed == 3)
            	sizeX = (float) ((double) sizeY / (1.0f * 4.0));
            else
            	sizeX = (float) ((double) sizeY / (1.0f * 2.0));

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
        this.state = STATE_FIRE;
        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        if(light == null) {
	        light = new PointLight(new Vector3f(0.75f,0.5f,0.1f), 0.8f, 
				   new Attenuation(0,0,1), getTransform().getPosition());
	        light.addToEngine();
        }
        this.fireSound = sounds.get(new Random().nextInt(sounds.size()));
    	AudioUtil.playAudio(fireSound, transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length());
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
		
        if (state == STATE_FIRE) {
            float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

            if (orientation.getX() > 0)
                angle = 180 + angle;

            transform.setRotation(0, angle + 90, 0);
            temp = delta;
            light.setPosition(transform.getPosition());
            light.setPosition(new Vector3f(light.getPosition().getX(), 0.05f * (float)(Math.sin(temp*2.5)+1.0/2.0) + 0.45f, light.getPosition().getZ()));
            
            double time = Time.getTime();
            double timeDecimals = (time - (double) ((int) time));
            timeDecimals *= 12.1f;
            
            if (timeDecimals <= 0.25f) {
                material.setDiffuse(animation.get(0));
                if(transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length() < 1.0f && !Level.getPlayer().isShooting) {
        			if(Level.getPlayer().isArmor() == false)
        				Level.getPlayer().addHealth((int) -DAMAGE/2, "FIRE");
                	else
                		Level.getPlayer().addArmor((int) -DAMAGE/2);
        		}
                if(Auschwitz.getLevel().getShootingObjective() != null)
        	        if(getTransform().getPosition().sub(Auschwitz.getLevel().getShootingObjective().getTransform().getPosition()).length() < 1.0f)
        	        	Auschwitz.getLevel().getShootingObjective().damage(DAMAGE);
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
                material.setDiffuse(animation.get(6));
                if(transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length() < 1.0f && !Level.getPlayer().isShooting) {
        			if(Level.getPlayer().isArmor() == false)
        				Level.getPlayer().addHealth((int) -DAMAGE/2, "FIRE");
                	else
                		Level.getPlayer().addArmor((int) -DAMAGE/2);
        		}
                if(Auschwitz.getLevel().getShootingObjective() != null)
        	        if(getTransform().getPosition().sub(Auschwitz.getLevel().getShootingObjective().getTransform().getPosition()).length() < 1.0f)
        	        	Auschwitz.getLevel().getShootingObjective().damage(DAMAGE);
            } else if (timeDecimals <= 2.5f) {
                material.setDiffuse(animation.get(5));
            } else if (timeDecimals <= 2.75f) {
                material.setDiffuse(animation.get(4));
            } else if (timeDecimals <= 3f) {
                material.setDiffuse(animation.get(3));
            } else if (timeDecimals <= 3.25f) {
                material.setDiffuse(animation.get(2));
            } else if (timeDecimals <= 3.5f) {
                material.setDiffuse(animation.get(1));
            } else if (timeDecimals <= 3.75f) {
                material.setDiffuse(animation.get(0));
            } else if (timeDecimals <= 4f) {
                material.setDiffuse(animation.get(1));
            } else if (timeDecimals <= 4.25f) {
                material.setDiffuse(animation.get(2));
                if(transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length() < 1.0f && !Level.getPlayer().isShooting) {
        			if(Level.getPlayer().isArmor() == false)
        				Level.getPlayer().addHealth((int) -DAMAGE/2, "FIRE");
                	else
                		Level.getPlayer().addArmor((int) -DAMAGE/2);
        		}
                if(Auschwitz.getLevel().getShootingObjective() != null)
        	        if(getTransform().getPosition().sub(Auschwitz.getLevel().getShootingObjective().getTransform().getPosition()).length() < 1.0f)
        	        	Auschwitz.getLevel().getShootingObjective().damage(DAMAGE);
            } else if (timeDecimals <= 4.5f) {
                material.setDiffuse(animation.get(3));
            } else if (timeDecimals <= 4.75f) {
                material.setDiffuse(animation.get(4));
            } else if (timeDecimals <= 5f) {
                material.setDiffuse(animation.get(5));
            } else if (timeDecimals <= 5.25f) {
                material.setDiffuse(animation.get(6));
            } else if (timeDecimals <= 5.5f) {
                material.setDiffuse(animation.get(7));
            } else if (timeDecimals <= 5.75f) {
                material.setDiffuse(animation.get(0));
            } else if (timeDecimals <= 6f) {
                material.setDiffuse(animation.get(1));
            } else if (timeDecimals <= 6.25f) {
                material.setDiffuse(animation.get(2));
                if(transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length() < 1.0f && !Level.getPlayer().isShooting) {
        			if(Level.getPlayer().isArmor() == false)
        				Level.getPlayer().addHealth((int) -DAMAGE/2, "FIRE");
                	else
                		Level.getPlayer().addArmor((int) -DAMAGE/2);
        		}
                if(Auschwitz.getLevel().getShootingObjective() != null)
        	        if(getTransform().getPosition().sub(Auschwitz.getLevel().getShootingObjective().getTransform().getPosition()).length() < 1.0f)
        	        	Auschwitz.getLevel().getShootingObjective().damage(DAMAGE);
            } else if (timeDecimals <= 6.5f) {
                material.setDiffuse(animation.get(3));
            } else if (timeDecimals <= 6.75f) {
                material.setDiffuse(animation.get(4));
            } else if (timeDecimals <= 7f) {
                material.setDiffuse(animation.get(5));
            } else if (timeDecimals <= 7.25f) {
                material.setDiffuse(animation.get(6));
            } else if (timeDecimals <= 7.5f) {
                material.setDiffuse(animation.get(7));
            } else if (timeDecimals <= 7.75f) {
                material.setDiffuse(animation.get(6));
            } else if (timeDecimals <= 8f) {
                material.setDiffuse(animation.get(5));
            } else if (timeDecimals <= 8.25f) {
                material.setDiffuse(animation.get(4));
                if(transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length() < 1.0f && !Level.getPlayer().isShooting) {
        			if(Level.getPlayer().isArmor() == false)
        				Level.getPlayer().addHealth((int) -DAMAGE/2, "FIRE");
                	else
                		Level.getPlayer().addArmor((int) -DAMAGE/2);
        		}
                if(Auschwitz.getLevel().getShootingObjective() != null)
        	        if(getTransform().getPosition().sub(Auschwitz.getLevel().getShootingObjective().getTransform().getPosition()).length() < 1.0f)
        	        	Auschwitz.getLevel().getShootingObjective().damage(DAMAGE);
            } else if (timeDecimals <= 8.5f) {
                material.setDiffuse(animation.get(3));
            } else if (timeDecimals <= 8.75f) {
                material.setDiffuse(animation.get(2));
            } else if (timeDecimals <= 9f) {
                material.setDiffuse(animation.get(1));
            } else if (timeDecimals <= 9.25f) {
                material.setDiffuse(animation.get(0));
            } else if (timeDecimals <= 9.5f) {
                material.setDiffuse(animation.get(1));
            } else if (timeDecimals <= 9.75f) {
                material.setDiffuse(animation.get(2));
            } else if (timeDecimals <= 10f) {
                material.setDiffuse(animation.get(3));
            } else if (timeDecimals <= 10.25f) {
                material.setDiffuse(animation.get(4));
                if(transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length() < 1.0f && !Level.getPlayer().isShooting) {
        			if(Level.getPlayer().isArmor() == false)
        				Level.getPlayer().addHealth((int) -DAMAGE/2, "FIRE");
                	else
                		Level.getPlayer().addArmor((int) -DAMAGE/2);
        		}
                if(Auschwitz.getLevel().getShootingObjective() != null)
        	        if(getTransform().getPosition().sub(Auschwitz.getLevel().getShootingObjective().getTransform().getPosition()).length() < 1.0f)
        	        	Auschwitz.getLevel().getShootingObjective().damage(DAMAGE);
            } else if (timeDecimals <= 10.5f) {
                material.setDiffuse(animation.get(5));
            } else if (timeDecimals <= 10.75f) {
                material.setDiffuse(animation.get(6));
            } else if (timeDecimals <= 11f) {
                material.setDiffuse(animation.get(7));
            } else if (timeDecimals <= 11.25f) {
                material.setDiffuse(animation.get(0));
            } else if (timeDecimals <= 11.5f) {
                material.setDiffuse(animation.get(1));
            } else if (timeDecimals <= 11.75f) {
                material.setDiffuse(animation.get(2));
            } else if (timeDecimals <= 12f) {
                material.setDiffuse(animation.get(3));
            } else {
                state = STATE_DONE;
            }
        }
        
        if(state == STATE_DONE) {
        	light.removeToEngine();
        	fireSound.stop();
        	Level.removeFire(this);
        }

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