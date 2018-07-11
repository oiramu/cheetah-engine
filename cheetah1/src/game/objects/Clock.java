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

import engine.core.ResourceLoader;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.MeshRenderer;
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;

/**
 *
 * @author Carlos Rodriguez.
 * @version 1.0
 * @since 2018
 */
public class Clock {
	
	private static final String RES_LOC = "clock/";
	private static final int STATE_IDLE = 0;
	private int state;
    
    private static Mesh mesh;
    private Material material;
    private MeshRenderer meshRenderer;
    
    private float sizeX;
    
    private static ArrayList<Texture> animation;

    private Transform transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
    public Clock(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();

            animation.add(ResourceLoader.loadTexture(RES_LOC + "GTCLOCK1"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "GTCLOCK2"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "GTCLOCK3"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "GTCLOCK4"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "GTCLOCK5"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "GTCLOCK6"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "GTCLOCK7"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "GTCLOCK8"));
        }
    	
        if (mesh == null) {
            mesh = new Mesh();

            float sizeY = 1f;
            sizeX = 0.125f;

            float offsetX = 0.00f;
            float offsetY = 0.00f;

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

            mesh.addVertices(verts, indices, true);
        }
        
        this.material = new Material(animation.get(5));
        this.state = STATE_IDLE;
        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, this.transform, material);
    }

    /**
     * Method that updates the object's data.
     */
    public void update() {
        Vector3f playerDistance = transform.getPosition().sub(Transform.getCamera().getPos());

        Vector3f orientation = playerDistance.normalized();
        @SuppressWarnings("unused")
		float distance = playerDistance.length();

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() > 0) {
            angle = 180 + angle;
        }

        transform.setRotation(0, angle + 90, 0);
        
        double time = (double) Time.getTime() / Time.SECOND;
        
        if (state == STATE_IDLE) {
        	double timeDecimals = (time - (double) ((int) time));

            timeDecimals *= 1.75f;

        	if (timeDecimals <= 0.25f) {
                material.setTexture(animation.get(0));
            } else if (timeDecimals <= 0.5f) {
                material.setTexture(animation.get(1));
            } else if (timeDecimals <= 0.75f) {
                material.setTexture(animation.get(2));
            } else if (timeDecimals <= 1f) {
                material.setTexture(animation.get(3));
            } else if (timeDecimals <= 1.25f) {
                material.setTexture(animation.get(4));
            } else if (timeDecimals <= 1.5f) {
                material.setTexture(animation.get(5));
            } else if (timeDecimals <= 1.75f) {
                material.setTexture(animation.get(6));
            } else {
                material.setTexture(animation.get(7));
            }
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
    
}
