/*
 * Copyright 2017 Julio Vergara.
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
import engine.rendering.Shader;
import engine.rendering.Texture;
import engine.rendering.Vertex;
import game.Level;

/**
 *
 * @author Julio Vergara.
 * @version 1.1
 * @since 2017
 */
public class DeadJew extends GameComponent {
	
	private static final String RES_LOC = "deadJew/";
	private static final int STATE_1 = 0;
	private static final int STATE_2 = 1;
    
    private static Mesh mesh;
    private static Material material;
    private MeshRenderer meshRenderer;
    
    private int state;
    
    private float sizeY;
    private float sizeX;

    private Transform transform;
    
    private ArrayList<Texture> animation;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
    public DeadJew(Transform transform) {
        if (mesh == null) {
            sizeY = 0.3095238095238095f;
            sizeX = (float) ((double) sizeY / (3.230769230769231 / 4));

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
        
        if(animation == null) {
        	animation = new ArrayList<Texture>();
        	
        	animation.add(new Texture(RES_LOC+"CMPGG0"));
        	animation.add(new Texture(RES_LOC+"CMPGH0"));
        }

        if (material == null) {
			material = new Material(animation.get(1));
        }
        this.state = STATE_1;
        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, getTransform(), material);
    }

    /**
     * Method that updates the object's data.
     */
    public void update() {
    	Vector3f playerDistance = transform.getPosition().sub(Level.getPlayer().getCamera().getPos());
        Vector3f orientation = playerDistance.normalized();
		float distance = playerDistance.length();

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() > 0) {
            angle = 180 + angle;
        }

        transform.setRotation(0, angle + 90, 0);
        
        double time = (double) Time.getTime() / Time.SECOND;
        double timeDecimals = (time - (double) ((int) time));

        if(state == STATE_1) {
        	material.setDiffuse(animation.get(0));
        	if(distance<1.25f && timeDecimals <= 0.25f) state = STATE_2;
        } else if(state == STATE_2) {
        	material.setDiffuse(animation.get(1));
        	if(distance>1.25f && timeDecimals <= 0.25f) state = STATE_1;
        } 
    }

    /**
     * Method that renders the object's mesh to screen.
     * @param shader to render
     */
    public void render(Shader shader) {meshRenderer.render(shader);}
    
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