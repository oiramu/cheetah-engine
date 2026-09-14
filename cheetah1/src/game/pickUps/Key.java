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
package game.pickUps;

import engine.audio.AudioEmitter;
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
 * @version 1.1
 * @since 2018
 */
public class Key extends GameComponent {

    public final float			PICKUP_THRESHHOLD = 0.75f;

	private static final String PICKUP_SOUND = "key/GETKEY";
	private final AudioEmitter pickupEmitter = new AudioEmitter(this);

	private float 				temp = 0;
	private boolean				isGoldKey;
	private boolean				shouldFloat;

    private static Mesh 		mesh;
    private static Material		material;
    private MeshRenderer 		meshRenderer;
    private Transform 			transform;

    /**
     * Constructor of the actual power-up.
     * @param transform the transform of the data
     * @param isGoldKey if it is or not
     * @param shouldFloat if it does
     */
    public Key(Transform transform, boolean isGoldKey, boolean shouldFloat) {
        if (mesh == null) {
            final float sizeY = 0.171875f;
            final float sizeX = (float) ((double) sizeY / (0.861952862f * 1.0));
            
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

    	if(isGoldKey)
    		material = new Material(new Texture("key/YSKUA0"));
    	else
    		material = new Material(new Texture("key/YSKUA1"));
        this.isGoldKey = isGoldKey;
        this.shouldFloat = shouldFloat;
        this.transform = transform;
        this.meshRenderer = new MeshRenderer(mesh, this.transform, material);
    }
    
    /**
     * Updates the power-up every single frame.
     * @param delta of time
     */
    public void update(double delta) {
    	faceCamera(transform);
        float distance = getDistance();
        if (shouldFloat) {
	        temp += (float) delta; 
	        transform.getPosition().setY(0.05f * (float)(Math.sin(temp)+1.0/2.0) + 0.025f);
        }
        
		if (distance < PICKUP_THRESHHOLD) {
			if(isGoldKey)
				Level.getPlayer().setGoldkey(true);
			else
				Level.getPlayer().setBronzekey(true);
            Level.removeArmor(this);
            pickupEmitter.play(PICKUP_SOUND);
        }
    }

    /**
     * Method that renders the power-up's mesh.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {meshRenderer.render(shader, renderingEngine);}
    
}
