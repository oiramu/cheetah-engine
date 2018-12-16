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

import engine.components.Attenuation;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.components.PointLight;
import engine.core.CoreEngine;
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
public class Furnace extends GameComponent {
	
	private static final String RES_LOC = "furnace/";
	private static final int STATE_IDLE = 0;
	
	private int state;
	private float temp = 0;
    
    private static Mesh mesh;
    private Material material;
    private MeshRenderer meshRenderer;
    private RenderingEngine renderingEngine;
    private PointLight light;
    
    private float sizeX;
    
    private static ArrayList<Texture> animation;

    private Transform transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
    public Furnace(Transform transform) {
    	
    	if (animation == null) {
            animation = new ArrayList<Texture>();

            animation.add(new Texture(RES_LOC + "STOVA0"));
            animation.add(new Texture(RES_LOC + "STOVB0"));
        }
    	
        if (mesh == null) {
            float sizeY = 1.0f;
            sizeX = (float) ((double) sizeY / (1f * 2.0));

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
        
        if(renderingEngine == null) renderingEngine = CoreEngine.renderingEngine;
        
        if(light == null) {
	        this.light = new PointLight(new Vector3f(1.0f,0.5f,0.2f), 0.8f, 
	        		new Attenuation(0,0,1), new Vector3f(getTransform().getPosition().getX(), 0.25f, 
	        				getTransform().getPosition().getZ()));
	        renderingEngine.addLight(light);
    	}
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
        
        temp += delta;

        transform.setRotation(0, angle + 90, 0);
        
        light.setPosition(new Vector3f(light.getPosition().getX(), 0.05f * (float)(Math.sin(temp*2.5)+1.0/2.0) + 0.025f, light.getPosition().getZ()));
        
        double time = Time.getTime();
        
        if (state == STATE_IDLE) {
        	double timeDecimals = (time - (double) ((int) time));

        	if (timeDecimals <= 0.25f) {
                material.setDiffuse(animation.get(1));
            } else {
                material.setDiffuse(animation.get(0));
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
