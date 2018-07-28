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
    private Vector3f m_forward;
    private Vector3f m_up;

    /**
     * Camera empty constructor.
     */
    public Camera() {this(new Vector3f(0, 0, 0));}

    /**
     * Camera constructor on a 3D space.
     * @param pos Actual position.
     */
    public Camera(Vector3f pos) {
        this(pos, new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));
    }

    /**
     * Movable camera constructor on a 3D space.
     * @param pos Actual position.
     * @param forward direction.
     * @param up direction.
     */
    public Camera(Vector3f pos, Vector3f forward, Vector3f up) {
        this.m_pos = pos;
        this.m_forward = forward.normalized();
        this.m_up = up.normalized();
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
    public void move(Vector3f dir, float amt) {m_pos = m_pos.add(dir.mul(amt));}

    /**
     * Rotates the camera in the Y axis.
     * @param angle of rotation.
     */
    public void rotateY(float angle) {
        Vector3f Haxis = yAxis.cross(m_forward).normalized();

        m_forward = m_forward.rotate(angle, yAxis).normalized();

        m_up = m_forward.cross(Haxis).normalized();
    }

    /**
     * Rotates the camera in the X axis.
     * @param angle of rotation.
     */
    public void rotateX(float angle) {
        Vector3f Haxis = yAxis.cross(m_forward).normalized();

        m_forward = m_forward.rotate(angle, Haxis).normalized();

        m_up = m_forward.cross(Haxis).normalized();
    }

    /**
     * Returns the left side coordinates of the camera.
     * @return Left coordinates.
     */
    public Vector3f getLeft() {return m_forward.cross(m_up).normalized();}

    /**
     * Returns the right side coordinates of the camera.
     * @return Right coordinates.
     */
    public Vector3f getRight() {return m_up.cross(m_forward).normalized();}

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
    public Vector3f getForward() {return m_forward;}

    /**
     * Returns the forward side coordinates of the camera.
     * @return Forward coordinates.
     */
    public void setForward(Vector3f forward) {this.m_forward = forward;}

    /**
     * Returns the up side coordinates of the camera.
     * @return Up coordinates.
     */
    public Vector3f getUp() {return m_up;}

    /**
     * Returns the up side coordinates of the camera.
     * @return Up coordinates.
     */
    public void setUp(Vector3f up) {this.m_up = up;}
    
}
