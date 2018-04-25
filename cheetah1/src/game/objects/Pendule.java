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

import engine.ResourceLoader;
import engine.core.GameComponent;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import game.Auschwitz;

/**
 *
 * @author Julio Vergara.
 * @version 1.0
 * @since 2018
 */
public class Pendule implements GameComponent {
	
	private static final String RES_LOC = "pendule/";
	private static final int STATE_IDLE = 0;
	private int state;
    
    private static Mesh mesh;
    private Material material;
    
    private float sizeX;
    
    private static ArrayList<Texture> animation;

    private Transform transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
    public Pendule(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();

            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFI0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFJ0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFK0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFL0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFM0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFN0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFO0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFP0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFQ0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFR0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "SPDFS0"));
        }
    	
        if (mesh == null) {
            mesh = new Mesh();

            float sizeY = 1f;
            sizeX = sizeY;

            float offsetX = 0.05f;
            float offsetY = 0.01f;

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

            mesh.addVertices(verts, indices);
        }
        
        this.material = new Material(animation.get(5));
        this.state = STATE_IDLE;
        this.transform = transform;
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

            timeDecimals *= 4.75f;

        	if (timeDecimals <= 0.25f) {
                material.setTexture(animation.get(5));
            } else if (timeDecimals <= 0.5f) {
                material.setTexture(animation.get(4));
            } else if (timeDecimals <= 0.75f) {
                material.setTexture(animation.get(3));
            } else if (timeDecimals <= 1f) {
                material.setTexture(animation.get(2));
            } else if (timeDecimals <= 1.25f) {
                material.setTexture(animation.get(1));
            } else if (timeDecimals <= 1.5f) {
                material.setTexture(animation.get(0));
            } else if (timeDecimals <= 1.75f) {
                material.setTexture(animation.get(6));
            } else if (timeDecimals <= 2f) {
                material.setTexture(animation.get(7));
            } else if (timeDecimals <= 2.25f) {
                material.setTexture(animation.get(8));
            } else if (timeDecimals <= 2.5f) {
                material.setTexture(animation.get(9));
            } else if (timeDecimals <= 2.75f) {
                material.setTexture(animation.get(10));
            } else if (timeDecimals <= 3f) {
                material.setTexture(animation.get(9));
            } else if (timeDecimals <= 3.25f) {
                material.setTexture(animation.get(8));
            } else if (timeDecimals <= 3.5f) {
                material.setTexture(animation.get(7));
            } else if (timeDecimals <= 3.75f) {
                material.setTexture(animation.get(6));
            } else if (timeDecimals <= 4f) {
                material.setTexture(animation.get(0));
            } else if (timeDecimals <= 4.25f) {
                material.setTexture(animation.get(1));
            } else if (timeDecimals <= 4.5f) {
                material.setTexture(animation.get(2));
            } else if (timeDecimals <= 4.75f) {
                material.setTexture(animation.get(3));
            } else {
                material.setTexture(animation.get(4));
            }
        }

    }

    /**
     * Method that renders the object's mesh to screen.
     */
    public void render() {
        Auschwitz.updateShader(transform.getTransformation(), transform.getPerspectiveTransformation(), material);
        mesh.draw();
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
