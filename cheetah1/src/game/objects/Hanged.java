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
import java.util.Random;

import engine.core.IGameComponent;
import engine.core.ResourceLoader;
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
public class Hanged implements IGameComponent {
	
	private static final String RES_LOC = "hanged/";
    
    private static Mesh mesh;
    private Material material;
    @SuppressWarnings("unused")
	private int random;
    
    private float sizeX;
    
    private static ArrayList<Texture> animation;

    private Transform transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
    public Hanged(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();

            //animation.add(ResourceLoader.loadTexture(RES_LOC + "HUNGA0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "HUNGB0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "HUNGC0"));
            animation.add(ResourceLoader.loadTexture(RES_LOC + "HUNGD0"));
        }
    	
        if (mesh == null) {
            mesh = new Mesh();

            float sizeY = 1f;
            sizeX = 1.0f;

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

            mesh.addVertices(verts, indices, true);
        }
        
        this.material = new Material(animation.get(random = new Random().nextInt(animation.size())), new Vector3f(1,1,1));
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
        /**
        double time = (double) Time.getTime() / Time.SECOND;
        
        if (state == STATE_IDLE) {
        	double timeDecimals = (time - (double) ((int) time));

        	if (timeDecimals <= 0.25f) {
                material.setTexture(animation.get(0));
            } else if (timeDecimals <= 0.5f) {
                material.setTexture(animation.get(1));
            } else if (timeDecimals <= 0.75f) {
                material.setTexture(animation.get(3));
            } else if (timeDecimals <= 1.0f) {
                material.setTexture(animation.get(2));
            }
        }
		*/
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
