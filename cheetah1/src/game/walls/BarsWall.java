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
package game.walls;

import static engine.components.Constants.GAME_GRAPHICS;

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
 * @since 2019
 */
public class BarsWall extends GameComponent {
	
	private static final float HEIGHT = Level.LEVEL_HEIGHT;
	private static final float LENGTH = Level.SPOT_LENGTH;
	private static final float WIDTH = 0.05f;
	private static final int START = 0;
	
	private Transform 		transform;
	private MeshRenderer	meshRenderer;
	private Material		material;
	private Mesh			mesh;
	
	/**
	 * Chunk's wall constructor.
	 * @param direction of the face
	 */
	public BarsWall(Transform transform) {
		this.transform = transform;
		switch(GAME_GRAPHICS) {
	    	case "Low":
	    		material = new Material(new Texture("bars/bars"));
	    		break;
	    	case "High":
	    		material = new Material(new Texture("bars/bars"), 1, 8, 
	    				new Texture("bars/bars_normal"), 
	    				new Texture("bars/bars_bump"), 0.04f, -0.75f);
	    		break;
		}
		
		if (mesh == null) {
            Vertex[] doorVerts = new Vertex[]{	new Vertex(new Vector3f(START, START, START), new Vector2f(0.5f, 1)),
												new Vertex(new Vector3f(START, HEIGHT, START), new Vector2f(0.5f, 0.75f)),
												new Vertex(new Vector3f(LENGTH, HEIGHT, START), new Vector2f(0.75f, 0.75f)),
												new Vertex(new Vector3f(LENGTH, START, START), new Vector2f(0.75f, 1)),
												
												new Vertex(new Vector3f(START, START, START), new Vector2f(0.73f, 1)),
												new Vertex(new Vector3f(START, HEIGHT, START), new Vector2f(0.73f, 0.75f)),
												new Vertex(new Vector3f(START, HEIGHT, WIDTH), new Vector2f(0.75f, 0.75f)),
												new Vertex(new Vector3f(START, START, WIDTH), new Vector2f(0.75f, 1)),
												
												new Vertex(new Vector3f(START, START, WIDTH), new Vector2f(0.5f, 1)),
												new Vertex(new Vector3f(START, HEIGHT, WIDTH), new Vector2f(0.5f, 0.75f)),
												new Vertex(new Vector3f(LENGTH, HEIGHT, WIDTH), new Vector2f(0.75f, 0.75f)),
												new Vertex(new Vector3f(LENGTH, START, WIDTH), new Vector2f(0.75f, 1)),
												
												new Vertex(new Vector3f(LENGTH, START, START), new Vector2f(0.73f, 1)),
												new Vertex(new Vector3f(LENGTH, HEIGHT, START), new Vector2f(0.73f, 0.75f)),
												new Vertex(new Vector3f(LENGTH, HEIGHT, WIDTH), new Vector2f(0.75f, 0.75f)),
												new Vertex(new Vector3f(LENGTH, START, WIDTH), new Vector2f(0.75f, 1))};

            int[] doorIndices = new int[]{0, 1, 2,
            								0, 2, 3,
            								6, 5, 4,
            								7, 6, 4,
            								10, 9, 8,
            								11, 10, 8,
            								12, 13, 14,
            								12, 14, 15};

            mesh = new Mesh(doorVerts, doorIndices, true, true);
        }
		
		this.meshRenderer = new MeshRenderer(mesh, transform, material);
	}
	
	/**
     * Refresh the wall every single frame.
     * @param delta of time
     */
    public void update(double delta) {checkDistance();}
    
    /**
     * Checks the distance from the point of view.
     */
    public void checkDistance() {
    	Vector3f playerDistance = transform.getPosition().sub(Level.getPlayer().getCamera().getPos());
        setDistance(playerDistance.length());
    }
    
    /**
     * Renders the wall.
     * @param shader to render
     * @param renderingEngine to use
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {meshRenderer.render(shader, renderingEngine);}
    
    /**
     * Returns the size of the door in the level.
     * @return Size of the door.
     */
	public Vector2f getSize() {
		if (transform.getRotation().getY() == 0) {
            return new Vector2f(Level.LEVEL_HEIGHT, Level.SPOT_WIDTH);
        } else {
            return new Vector2f(Level.SPOT_WIDTH, Level.LEVEL_HEIGHT);
        }
	}

}
