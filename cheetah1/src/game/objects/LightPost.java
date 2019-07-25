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

import engine.components.Attenuation;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.components.PointLight;
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
 * @version 1.1
 * @since 2019
 */
public class LightPost extends GameComponent {
    
    private static Mesh 		mesh;
    private static Material 	material;
    private MeshRenderer 		meshRenderer;
    
    private float 				sizeX;

    private Transform 			transform;
    private Vector3f			position;

    /**
     * Constructor of the light's post object.
     * @param transform the transform of the data.
     * @param right location of light post
     */
    public LightPost(Transform transform, boolean right) {
        if (mesh == null) {
        	float sizeY = 1.5f;
            sizeX = (float) ((double) sizeY / (1.12835820896f * 2.0));

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
        
        this.transform = transform;
        if(right) {
        	material = new Material(new Texture("/LightPost/Right"));
        	position = new Vector3f(getTransform().getPosition().getX() - 0.275f, 0.1f, 
    				getTransform().getPosition().getZ());
        } else {
        	material = new Material(new Texture("/LightPost/Left"));
        	position = new Vector3f(getTransform().getPosition().getX() + 0.275f, 0.1f, 
    				getTransform().getPosition().getZ());
        }
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
        new PointLight(new Vector3f(0.5f,0.5f,0.6f), 0.8f, 
        		new Attenuation(0,0,1), position).addToEngine();
    }

    /**
     * Updates the object every single frame.
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