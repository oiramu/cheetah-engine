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
package game.projectiles;

import static engine.components.Constants.GRAVITY;

import engine.components.GameComponent;
import engine.components.MeshRenderer;
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
import game.enemies.Commander;
import game.objects.Explosion;

/**
 *
 * @author Carlos Rodriguez.
 * @version 1.0
 * @since 2018
 */
public class Rocket extends GameComponent {
    
	private static final String RES_LOC = "Rocket/";
	private static final float	SPEED = 7.5f;
	
    private static Mesh 		mesh;
    private static Material 	material;
    private MeshRenderer 		meshRenderer;
    private float 				sizeX;
    private float 				upAmt = 0;
    private int 				state;
    private boolean 			playerShoots;

    private Transform 			transform;
    private Vector3f 			objetiveOrientation;
    private Explosion 			explosion;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the data.
     */
    public Rocket(Transform transform, boolean playerShoots) {
        if (mesh == null) {
            float sizeY = 0.25f;
            sizeX = sizeY;

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

        if (material == null) {
        	if(playerShoots)
        		material = new Material(new Texture(RES_LOC+"MISLA5"));
        	else
        		material = new Material(new Texture(RES_LOC+"MISLA1"));
        }

        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        if(playerShoots)
        	objetiveOrientation = this.transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).mul(-1).normalized();
        else
        	objetiveOrientation = this.transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).normalized();
        this.state = 0;
        this.playerShoots = playerShoots;
    }

    /**
     * Updates the object every single frame.
     * @param delta of time
     */
    public void update(double delta) {
    	if(state == 0) {
	    	Vector3f playerDistance = transform.getPosition().sub(Level.getPlayer().getCamera().getPos());
	        Vector3f orientation = playerDistance.normalized();
			float distance = playerDistance.length();
	        setDistance(distance);
	
	        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));
	
	        if (orientation.getX() > 0)
	            angle = 180 + angle;
	        
	        transform.setRotation(0, angle + 90, 0);
	        
	        upAmt -= (GRAVITY/(SPEED * 100)) * delta;
            transform.setPosition(transform.getPosition().add(new Vector3f(0, (float) (upAmt * delta), 0)));
            if(transform.getPosition().getY() == 0) {
            	upAmt = 0;
            	transform.getPosition().setY(0);
            	state = 1;
            }
	        
	        objetiveOrientation.setY(0);
	        float moveSpeed = SPEED;
	
	        Vector3f oldPos = transform.getPosition();
	        Vector3f newPos = transform.getPosition().add(objetiveOrientation.mul((float) (-moveSpeed * delta)));
	
	        Vector3f collisionVector = Auschwitz.getLevel().checkCollisions(oldPos, newPos, sizeX, sizeX);
	
	        Vector3f movementVector = collisionVector.mul(objetiveOrientation.normalized());
	        
	        if (movementVector.length() > 0.33f)
	            transform.setPosition(transform.getPosition().add(movementVector.mul((float) (-moveSpeed * delta))));
	        else
	        	state = 1;
	        
	        if(Auschwitz.getLevel().getShootingObjective() != null)
		        if(getTransform().getPosition().sub(Auschwitz.getLevel().getShootingObjective().getTransform().getPosition()).length() < 1.0f)
		        	state = 1;
    	}
    	if(state == 1){
    		if(explosion == null)
    			explosion = new Explosion(new Transform(getTransform().getPosition()));
    		explosion.update(delta);
    		if (explosion.getState() == 2)
    			state = 2;
    	}
    	if(state == 2) {
    		if(playerShoots)
				Level.getPlayer().removeRocket(this);
    		else
    			Commander.removeRocket(this);
    	}
    }

    /**
     * Method that renders the object's mesh.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {
    	if(state == 0)
    		meshRenderer.render(shader, renderingEngine);
    	if(state == 1)
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
    
}