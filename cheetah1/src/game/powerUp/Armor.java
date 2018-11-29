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
package game.powerUp;

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
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
import game.Level;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class Armor extends GameComponent {

    private static final float PICKUP_THRESHHOLD = 0.75f;
    private static final boolean STATE = true;
    private static final int AMOUNT = 100;
	private static final String RES_LOC = "armor/MEDIA";
	private static final Clip PICKUP_NOISE = AudioUtil.loadAudio(RES_LOC);

    private static Mesh 	m_mesh;
    private static Material	m_material;
    private MeshRenderer 	m_meshRenderer;
    private Transform 		m_transform;

    /**
     * Constructor of the actual power-up.
     * @param transform the transform of the data.
     */
    public Armor(Transform transform) {
        if (m_mesh == null) {
            final float sizeY = 0.4f;
            final float sizeX = (float) ((double) sizeY / (sizeY * 4.0));

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

            m_mesh = new Mesh(verts, indices, true);
        }

        if (m_material == null) {
            m_material = new Material(new Texture(RES_LOC));
        }

        this.m_transform = transform;
        this.m_meshRenderer = new MeshRenderer(m_mesh, this.m_transform, m_material);
    }

    /**
     * Updates the power-up every single frame.
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
        
		if (distance < PICKUP_THRESHHOLD) {
            Level.getPlayer().setArmorb(STATE);
            Level.getPlayer().addArmori(AMOUNT);
            Level.removeArmor(this);
            AudioUtil.playAudio(PICKUP_NOISE, 0);
        }
    }

    /**
     * Method that renders the power-up's mesh.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {m_meshRenderer.render(shader, renderingEngine);}
    
}
