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

import engine.components.Attenuation;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.components.PointLight;
import engine.core.CoreEngine;
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
 * @author Julio Vergara
 * @version 1.2
 * @since 2017
 */
public class Lantern extends GameComponent {
    
    private static Mesh 	m_mesh;
    private Material 		m_material;
    private MeshRenderer 	m_meshRenderer;
    private RenderingEngine m_renderingEngine;
    private PointLight 		m_light;
    private float 			m_sizeX;
    
    private static final String RES_LOC = "lantern/MEDIA";

    private Transform 		m_transform;

    /**
     * Constructor of the actual object.
     * @param transform the transform of the object in a 3D space.
     */
    public Lantern(Transform transform) {
        if (m_mesh == null) {
            float sizeY = 0.3f;
            m_sizeX = (float) ((double) sizeY / (1.5f * 2.0));

            float offsetX = 0.0f;
            float offsetY = 0.0f;

            float texMinX = -offsetX;
            float texMaxX = -1 - offsetX;
            float texMinY = -offsetY;
            float texMaxY = 1 - offsetY;

            Vertex[] verts = new Vertex[]{new Vertex(new Vector3f(-m_sizeX, 0, 0), new Vector2f(texMaxX, texMaxY)),
                new Vertex(new Vector3f(-m_sizeX, sizeY, 0), new Vector2f(texMaxX, texMinY)),
                new Vertex(new Vector3f(m_sizeX, sizeY, 0), new Vector2f(texMinX, texMinY)),
                new Vertex(new Vector3f(m_sizeX, 0, 0), new Vector2f(texMinX, texMaxY))};

            int[] indices = new int[]{0, 1, 2,
                                    0, 2, 3};

            m_mesh = new Mesh(verts, indices, true);
        }

        if (m_material == null) {
            m_material = new Material(new Texture(RES_LOC));
        }
        
        if(m_renderingEngine == null) m_renderingEngine = CoreEngine.m_renderingEngine;
        
        this.m_transform = transform;
        this.m_meshRenderer = new MeshRenderer(m_mesh, getTransform(), m_material);
        
        if(m_light == null) {
	        this.m_light = new PointLight(new Vector3f(0.5f,0.5f,0.6f), 0.8f, 
	        		new Attenuation(0,0,1), new Vector3f(getTransform().getPosition().getX(), 0.25f, 
	        				getTransform().getPosition().getZ()));
	        m_renderingEngine.addLight(m_light);
    	}
    }

    /**
     * Method that updates the object's data.
     * @param delta of time
     */
    public void update(double delta) {
    	Vector3f playerDistance = m_transform.getPosition().sub(Level.getPlayer().getCamera().getPos());
        Vector3f orientation = playerDistance.normalized();
		float distance = playerDistance.length();
		setDistance(distance);

        float angle = (float) Math.toDegrees(Math.atan(orientation.getZ() / orientation.getX()));

        if (orientation.getX() > 0) {
            angle = 180 + angle;
        }
        m_transform.setRotation(0, angle + 90, 0);
       }

    /**
     * Method that renders the object's mesh.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {m_meshRenderer.render(shader, renderingEngine);}
    
    /**
     * Gets the transform of the object in projection.
     * @return transform.
     */
	public Transform getTransform() {return m_transform;}
	
	/**
	 * Gets the size of the object in the 3D space and saves it on a vector.
	 * @return the vector size.
	 */
    public Vector2f getSize() {return new Vector2f(m_sizeX, m_sizeX);}
    
}