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

import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import game.Level;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2019
 */
public class Wall extends GameComponent {
	
	private Transform 		transform;
	private MeshRenderer	meshRenderer;
	
	/**
	 * Chunk's wall constructor.
	 * @param transform of wall
	 * @param material of wall
	 * @param mesh of wall
	 */
	public Wall(Transform transform, Material material, Mesh mesh) {
		this.transform = transform;
		this.meshRenderer = new MeshRenderer(mesh, new Transform(), material);
	}
	
	/**
     * Refresh the wall every single frame.
     */
    public void update() {checkDistance();}
    
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
