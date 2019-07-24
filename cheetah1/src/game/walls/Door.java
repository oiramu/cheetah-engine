/*
 * Copyright 2017 Carlos Rodriguez.
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

import javax.sound.sampled.Clip;

import engine.audio.AudioUtil;
import engine.components.GameComponent;
import engine.components.MeshRenderer;
import engine.core.Time;
import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;
import engine.rendering.Material;
import engine.rendering.Mesh;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import engine.rendering.Vertex;
import game.Level;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Door extends GameComponent {
	
	private static final float HEIGHT = 1.0f;
	private static final float LENGTH = Level.SPOT_LENGTH;
	private static final float WIDTH = 0.1f;
	private static final int START = 0;
	
	private static final String RES_LOC = "door/";

    private static final Clip openNoise = AudioUtil.loadAudio(RES_LOC + "MEDIA0");
    private static final Clip closeNoise = AudioUtil.loadAudio(RES_LOC + "MEDIA1");

    private static Mesh door;

    private Material material;
    private Transform transform;
    private MeshRenderer meshRenderer;
    private Vector3f closedPos;
    private Vector3f openPos;

    private boolean opening;
    private boolean closing;
    private boolean open;

    private double startTime;
    private double openTime;
    private double startCloseTime;
    private double closeTime;

    /**
     * Constructor of the door object.
     * @param transform of the door.
     * @param material of the door.
     * @param openPosition of the door.
     */
    public Door(Transform transform, Material material, Vector3f openPosition) {
        this.transform = transform;
        this.openPos = openPosition;
        this.closedPos = transform.getPosition();
        this.material = material;

        opening = false;
        closing = false;
        open = false;
        startTime = 0;
        openTime = 0;
        closeTime = 0;
        startCloseTime = 0;

        if (door == null) {
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

            door = new Mesh(doorVerts, doorIndices, true, true);
        }
        
        this.meshRenderer = new MeshRenderer(door, getTransform(), this.material);
    }

    /**
     * Opening door's method.
     * @param time of opening.
     * @param delay of opening.
     */
    public void open(float time, float delay) {
        if (opening || open) {
            return;
        }

        startTime = Time.getTime();
        openTime = startTime + time;
        startCloseTime = openTime + delay;
        closeTime = startCloseTime + time;

        opening = true;
        closing = false;
        AudioUtil.playAudio(openNoise, transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length());
    }

    /**
     * Refresh the door every single frame.
     * @param delta of time
     */
    public void update(double delta) {
    	Vector3f playerDistance = transform.getPosition().sub(Level.getPlayer().getCamera().getPos());
        float distance = playerDistance.length();
        setDistance(distance);
        if (opening) {
            double time = Time.getTime();

            if (time < openTime) {
                double lerpFactor = (time - startTime) / (openTime - startTime);

                transform.setPosition(openPos.lerp(closedPos, (float) lerpFactor));
            } else if (time > openTime && time < startCloseTime) {
                transform.setPosition(openPos);
                open = true;
            } else if (time > startCloseTime && time < closeTime) {
                if (!closing) {
                    AudioUtil.playAudio(closeNoise, transform.getPosition().sub(Level.getPlayer().getCamera().getPos()).length());
                }

                closing = true;
                open = false;
                double lerpFactor = (time - startCloseTime) / (closeTime - startCloseTime);

                transform.setPosition(closedPos.lerp(openPos, (float) lerpFactor));
            } else {
                closing = true;
                opening = false;
                open = false;
            }
        } else {
            transform.setPosition(closedPos);
        }
    }

    /**
     * Renders the door.
     * @param shader to render
     * @param renderingEngine to render
     */
    public void render(Shader shader, RenderingEngine renderingEngine) {meshRenderer.render(shader, renderingEngine);}

    /**
     * Checks if the door is open or not.
     * @return Door state.
     */
    public boolean isOpen() {return open;}

    /**
     * Returns the transform of the door.
     * @return Transform.
     */
    public Transform getTransform() {return transform;}

    /**
     * Returns the size of the door in the level.
     * @return Size of the door.
     */
    public Vector2f getSize() {
        if (transform.getRotation().getY() == 0) {
            return new Vector2f(HEIGHT, WIDTH);
        } else {
            return new Vector2f(WIDTH, HEIGHT);
        }
    }
    
    /**
     * Returns the XZ position of the door in transform.
     * @return XZ position.
     */
    public Vector2f getPosXZ() {
        return new Vector2f(transform.getPosition().getX(), transform.getPosition().getZ());
    }
}
