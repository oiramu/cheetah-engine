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
package game.projectiles;

import static engine.components.Constants.GRAVITY;
import static engine.core.CoreEngine.getRenderingEngine;

import java.util.ArrayList;

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
public class Flame extends GameComponent {
	
	private static final String 		RES_LOC = "flame/";
	private static final int 			STATE_FIRE = 1;
	private static final float			SPEED = 3.33f;
    
    private static Mesh 				mesh;
    
    private Material 					material;
    private MeshRenderer 				meshRenderer;
    private Transform 					transform;
    
    private PointLight 					light;
    private RenderingEngine				renderingEngine;
    private Vector3f 					objetiveOrientation;
    
    private float 						sizeX;
    private int 						state;
	private double 						temp;
	private float 						upAmt = 0;
    
    private static ArrayList<Texture> 	animation;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
	public Flame(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();
            
            animation.add(new Texture(RES_LOC + "FBLXA0"));
            animation.add(new Texture(RES_LOC + "FBLXB0"));
            animation.add(new Texture(RES_LOC + "FBLXC0"));
            animation.add(new Texture(RES_LOC + "FBLXD0"));
            animation.add(new Texture(RES_LOC + "FBLXE0"));
            animation.add(new Texture(RES_LOC + "FBLXF0"));
            animation.add(new Texture(RES_LOC + "FBLXG0"));
            animation.add(new Texture(RES_LOC + "FBLXH0"));
            animation.add(new Texture(RES_LOC + "FBLXI0"));
            animation.add(new Texture(RES_LOC + "FBLXJ0"));
            animation.add(new Texture(RES_LOC + "FBLXK0"));
        }
    	
        if (mesh == null) {
            float sizeY = 0.8f;
            sizeX = (float) ((double) sizeY / (1.0 * 2.0));

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
        this.renderingEngine = getRenderingEngine();
        this.material = new Material(animation.get(0));
        this.state = STATE_FIRE;
        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        light = new PointLight(new Vector3f(1.0f,0.5f,0.2f), 0.8f, 
			   new Attenuation(0,0,1), getTransform().getPosition());
	    renderingEngine.addLight(light);
        if(Auschwitz.getLevel().getShootingObjective() != null)
	        if(getTransform().getPosition().sub(Auschwitz.getLevel().getShootingObjective().getTransform().getPosition()).length() < 1.0f)
	        	Auschwitz.getLevel().getShootingObjective().damage(Level.getPlayer().getDamage());
        objetiveOrientation = this.transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).mul(-1).normalized();
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

        if (orientation.getX() > 0)
            angle = 180 + angle;

        transform.setRotation(0, angle + 90, 0);
        
        upAmt += (GRAVITY/(SPEED*10)) * delta;
        transform.setPosition(transform.getPosition().add(new Vector3f(0, (float) (upAmt * delta), 0)));
        
        objetiveOrientation.setY(0);
        float moveSpeed = SPEED;

        Vector3f oldPos = transform.getPosition();
        Vector3f newPos = transform.getPosition().add(objetiveOrientation.mul((float) (-moveSpeed * delta)));

        Vector3f collisionVector = Auschwitz.getLevel().checkCollisions(oldPos, newPos, sizeX, sizeX);

        Vector3f movementVector = collisionVector.mul(objetiveOrientation.normalized());
        
        if (movementVector.length() > 0)
            transform.setPosition(transform.getPosition().add(movementVector.mul((float) (-moveSpeed * delta))));
        
        double time = Time.getTime();
        temp = delta;
        light.setPosition(new Vector3f(light.getPosition().getX(), 0.05f * (float)(Math.sin(temp*2.5)+1.0/2.0) + 0.45f, light.getPosition().getZ()));
    	double timeDecimals = (time - (double) ((int) time));
        timeDecimals *= 3f;
        
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
        } else {
        	renderingEngine.removeLight(light);
        	Level.getPlayer().removeFlame(this);
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