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
package engine.components;

import engine.core.Quaternion;
import engine.core.Vector3f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class Camera {

    public static final Vector3f yAxis = new Vector3f(0, 1, 0);

    private Vector3f m_pos;
    private Quaternion m_rotation;

    /**
     * Camera empty constructor.
     */
    public Camera() {this(new Vector3f(0, 0, 0));}

    /**
     * Camera constructor on a 3D space.
     * @param pos Actual position
     */
    public Camera(Vector3f pos) {
        this(pos, new Quaternion(0,0,0,1));
    }

    /**
     * Movable camera constructor on a 3D space.
     * @param pos Actual position
     * @param rotation of the position
     */
    public Camera(Vector3f pos, Quaternion rotation) {
        this.m_pos = pos;
        this.m_rotation = rotation;
    }

    /**
     * Input Method (Just in case you only need a camera).
     */
    public void input() {
        /**
        if (Input.getKey(Input.KEY_UP)) {
            rotateX(-rotAmt);
        }
        if (Input.getKey(Input.KEY_DOWN)) {
            rotateX(rotAmt);
        }
        if (Input.getKey(Input.KEY_LEFT)) {
            rotateY(-rotAmt);
        }
        if (Input.getKey(Input.KEY_RIGHT)) {
            rotateY(rotAmt);
        }
        */
    }

    /**
     * Moves the camera into a direction and with an velocity 
     * amount.
     * @param dir Direction.
     * @param amt Velocity amount.
     */
    public void move(Vector3f dir, float amt) { m_pos = m_pos.add(dir.mul(amt)); }
    
    /**
     * Rotates the camera in the y axis by an angle.
     * @param angle to rotate
     */
    public void rotateY(float angle) {
		Quaternion newRotation = new Quaternion(yAxis, -angle).normalized();
		
		m_rotation = m_rotation.mul(newRotation).normalized();
	}
	
    /**
     * Rotates the camera in the x axis by an angle.
     * @param angle to rotate
     */
	public void rotateX(float angle) {
		Quaternion newRotation = new Quaternion(m_rotation.getRight(), -angle).normalized();
		
		m_rotation = m_rotation.mul(newRotation).normalized();
	}

	/**
	 * Rotates the camera by some QUATERNION.
	 * @param quaternion to rotate
	 */
    public void rotate(Quaternion quaternion) {
		m_rotation = m_rotation.mul(quaternion).normalized();
	}

    /**
     * Returns the left side coordinates of the camera.
     * @return Left coordinates.
     */
    public Vector3f getLeft() {return m_rotation.getLeft();}

    /**
     * Returns the right side coordinates of the camera.
     * @return Right coordinates.
     */
    public Vector3f getRight() {return m_rotation.getRight();}
	
    /**
     * Returns the rotation of the camera.
     * @return rotation
     */
	public Quaternion getRotation() { return m_rotation; }
	
	/**
	 * Sets a rotation for the camera.
	 * @param rotation to set
	 */
	public void setRotation(Quaternion rotation) { this.m_rotation = rotation; }

    /**
     * Returns the camera's actual position.
     * @return Position Coordinates.
     */
    public Vector3f getPos() {return m_pos;}

    /**
     * Sets the camera to a new position.
     * @param pos New position Coordinates.
     */
    public void setPos(Vector3f pos) {this.m_pos = pos;}

    /**
     * Returns the forward vector of the actual camera.
     * @return forward vector.
     */
    public Vector3f getForward() {return m_rotation.getForward();}
    
}
