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
 * @since 2018
 */
public class Barrel extends GameComponent {
	
	private static final String RES_LOC = "barrel/";
	private static final int STATE_IDLE = 0;
	private static final int STATE_BOOM = 1;
	private static final int STATE_DEAD = 2;
	private static final int STATE_DONE = 3;
	public int damage;
	public boolean kBooms;
	private int state;
    
    private static Mesh mesh;
    private Material material;
    private MeshRenderer meshRenderer;
    private Explosion explosion;
    
    private float sizeX;
    private double health;
    private boolean dead;
    
    private static ArrayList<Texture> animation;

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
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        this.dead = false;
        this.health = 200;
        this.damage = 0;
        this.kBooms = false;
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

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() > 0) {
            angle = 180 + angle;
        }

        transform.setRotation(0, angle + 90, 0);
        
        double time = Time.getTime();
        
        if(!dead) {
	        if (state == STATE_IDLE) {
	            material.setDiffuse(animation.get(0));
	        }
        }
        
        if (!dead && health <= 0) {
            dead = true;
            state = STATE_BOOM;
        }
        
        if (state == STATE_BOOM) {
        	double timeDecimals = (time - (double) ((int) time));
            timeDecimals *= 4.5f;

        	if (timeDecimals <= 0.25f) {
        		dead = true;
                material.setDiffuse(animation.get(1));
            } else {
                state = STATE_DEAD;
            }
        }
        
        if (state == STATE_DEAD) {
        	kBooms = true;
        	if(explosion == null)
            	explosion = new Explosion(new Transform(getTransform().getPosition()));
            explosion.update(delta);
            if(explosion.getState() == 3)
            	state = STATE_DONE;
        }
        
        if(state == STATE_DONE) {
        	kBooms = false;
        	Level.removeBarrel(this);
        }

    }

    /**
     * Method that renders the object's mesh to screen.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {
    	if(state != STATE_DEAD)
    		meshRenderer.render(shader, renderingEngine);
    	if(explosion != null)
    		explosion.render(shader, renderingEngine);
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