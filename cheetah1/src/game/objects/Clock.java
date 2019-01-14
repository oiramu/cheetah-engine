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
public class Clock extends GameComponent {
	
	private static final String 		RES_LOC = "clock/";
	private static final int 			STATE_IDLE = 0;
	private int 						state;
    
    private static Mesh 				mesh;
    private Material 					material;
    private MeshRenderer 				meshRenderer;
    
    private float 						sizeX;
    
    private static ArrayList<Texture> 	animation;

    private Transform 					transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
    public Clock(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();

            animation.add(new Texture(RES_LOC + "GTCLOCK1"));
            animation.add(new Texture(RES_LOC + "GTCLOCK2"));
            animation.add(new Texture(RES_LOC + "GTCLOCK3"));
            animation.add(new Texture(RES_LOC + "GTCLOCK4"));
            animation.add(new Texture(RES_LOC + "GTCLOCK5"));
            animation.add(new Texture(RES_LOC + "GTCLOCK6"));
            animation.add(new Texture(RES_LOC + "GTCLOCK7"));
            animation.add(new Texture(RES_LOC + "GTCLOCK8"));
        }
    	
        if (mesh == null) {
            float sizeY = 0.8f;
            sizeX = 0.125f;

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
        
        this.material = new Material(animation.get(5));
        this.state = STATE_IDLE;
        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
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
        
        if (state == STATE_IDLE) {
        	double timeDecimals = (time - (double) ((int) time));

            timeDecimals *= 1.75f;

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
            } else {
                material.setDiffuse(animation.get(7));
            }
        }

    }

    /**
     * Method that renders the object's mesh.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {meshRenderer.render(shader, renderingEngine);}
    
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