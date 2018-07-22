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
package engine.core;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class Vector3f {

    private float m_x;
    private float m_y;
    private float m_z;

    /**
     * Constructor of the float vector.
     * @param x data.
     * @param y data.
     * @param z data.
     */
    public Vector3f(float x, float y, float z) {
        this.m_x = x;
        this.m_y = y;
        this.m_z = z;
    }

    /**
     * Gets the length of the vector.
     * @return Vector's length.
     */
    public float length() {
        return (float) Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z);
    }
    
    /**
     * Gets the maximum between two numbers.
     * @return The maximum number.
     */
    public float max() { return Math.max(m_x, Math.max(m_y, m_z)); }

    /**
     * Dot vector.
     * @param r vector
     * @return Vector
     */
    public float dot(Vector3f r) {
        return m_x * r.getX() + m_y * r.getY() + m_z * r.getZ();
    }

    /**
     * Cross vector.
     * @param r vector
     * @return Vector
     */
    public Vector3f cross(Vector3f r) {
        float x_ = m_y * r.getZ() - m_z * r.getY();
        float y_ = m_z * r.getX() - m_x * r.getZ();
        float z_ = m_x * r.getY() - m_y * r.getX();

        return new Vector3f(x_, y_, z_);
    }

    /**
     * Normalizes the data in vector.
     * @return Vector's data normalized.
     */
    public Vector3f normalized() {
        float length = length();

        return new Vector3f(m_x / length, m_y / length, m_z / length);
    }

    /**
     * Rotates the vector's data by an float angle
     * @param angle Rotating angle.
     * @param axis Rotating axis.
     * @return Vector's data rotated.
     */
    public Vector3f rotate(float angle, Vector3f axis) {
        float sinHalfAngle = (float) Math.sin(Math.toRadians(angle / 2));
        float cosHalfAngle = (float) Math.cos(Math.toRadians(angle / 2));

        float rX = axis.getX() * sinHalfAngle;
        float rY = axis.getY() * sinHalfAngle;
        float rZ = axis.getZ() * sinHalfAngle;
        float rW = cosHalfAngle;

        Quaternion rotation = new Quaternion(rX, rY, rZ, rW);
        Quaternion conjugate = rotation.conjugate();

        Quaternion w = rotation.mul(this).mul(conjugate);

        return new Vector3f(w.getX(), w.getY(), w.getZ());
    }

    /**
     * Adds the data of one new vector to the main vector.
     * @param r Vector data to join.
     * @return Vector with more data.
     */
    public Vector3f add(Vector3f r) {
        return new Vector3f(m_x + r.getX(), m_y + r.getY(), m_z + r.getZ());
    }

    /**
     * Adds float data to the vector.
     * @param r Vector's new data.
     * @return Vector with more data.
     */
    public Vector3f add(float r) {
        return new Vector3f(m_x + r, m_y + r, m_z + r);
    }

    /**
     * Subtracts the vector's data minus r float vector data.
     * @param r Vector to subtract.
     * @return Vector subtracted.
     */
    public Vector3f sub(Vector3f r) {
        return new Vector3f(m_x - r.getX(), m_y - r.getY(), m_z - r.getZ());
    }

    /**
     * Subtracts the vector's data minus r float number.
     * @param r Number to subtract.
     * @return Vector subtracted.
     */
    public Vector3f sub(float r) {
        return new Vector3f(m_x - r, m_y - r, m_z - r);
    }

    /**
     * Multiplies the vector by r float vector.
     * @param r Vector to multiply.
     * @return Vector multiplied.
     */
    public Vector3f mul(Vector3f r) {
        return new Vector3f(m_x * r.getX(), m_y * r.getY(), m_z * r.getZ());
    }

    /**
     * Multiplies the vector by r float number.
     * @param r Number to multiply.
     * @return Vector multiplied.
     */
    public Vector3f mul(float r) {
        return new Vector3f(m_x * r, m_y * r, m_z * r);
    }

    /**
     * Divides the vector between r float vector number.
     * @param r Number to divide.
     * @return Vector divided.
     */
    public Vector3f div(Vector3f r) {
        return new Vector3f(m_x / r.getX(), m_y / r.getY(), m_z / r.getZ());
    }

    /**
     * Divides the vector between r float number.
     * @param r Number to divide.
     * @return Vector divided.
     */
    public Vector3f div(float r) {
        return new Vector3f(m_x / r, m_y / r, m_z / r);
    }

    /**
     * Gets the absolute value of the vector's data.
     * @return Absolute value of the vector's data.
     */
    public Vector3f abs() {
        return new Vector3f(Math.abs(m_x), Math.abs(m_y), Math.abs(m_z));
    }

    /**
     * Leaps the vector into an new vector depending on the amount
     * of size the vector adds data.
     * @param newVector The new vector.
     * @param amt Amount of multiplication for the new vector.
     * @return Leaped new vector.
     */
    public Vector3f lerp(Vector3f newVector, float amt) {
        return this.sub(newVector).mul(amt).add(newVector);
    }
    
    /**
     * Sets float values to a new vector of 3.
     * @param x value.
     * @param y value.
     * @return The vector.
     */
    public Vector3f set(float x, float y, float z) { this.m_x = x; this.m_y = y; this.m_z = z; return this; }
    
    /**
     * Sets float values to an existing vector of 3.
     * @param r the new vector to set.
     * @return A vector.
     */
    public Vector3f set(Vector3f r) { set(r.getX(), r.getY(), r.getZ()); return this; }

    /**
     * Converts the vector's data to string.
     * @return Data in string.
     */
    public String toString() {
        return "(" + m_x + " " + m_y + " " + m_z + ")";
    }

    /**
     * Gets the vectors data and compares to other vector.
     * @param r Vector to compare.
     * @return A boolean state.
     */
    public boolean equals(Vector3f r) {
        return r.getX() == m_x && r.getY() == m_y && r.getZ() == m_z;
    }

    /**
     * Gets the x-y value in vector.
     * @return XY's value.
     */
    public Vector2f getXY() {
        return new Vector2f(m_x, m_y);
    }

    /**
     * Gets the x-z value in vector.
     * @return XZ's value.
     */
    public Vector2f getXZ() {
        return new Vector2f(m_x, m_z);
    }

    /**
     * Gets the y-z value in vector.
     * @return YZ's value.
     */
    public Vector2f getYZ() {
        return new Vector2f(m_y, m_z);
    }

    /**
     * Gets the y-x value in vector.
     * @return YX's value.
     */
    public Vector2f getYX() {
        return new Vector2f(m_y, m_x);
    }

    /**
     * Gets the z-x value in vector.
     * @return ZX's value.
     */
    public Vector2f getZX() {
        return new Vector2f(m_z, m_x);
    }
    
    /**
     * Gets the z-y value in vector.
     * @return ZY's value.
     */
    public Vector2f getZY() {
        return new Vector2f(m_z, m_y);
    }

    /**
     * Gets the x value in vector.
     * @return X's value.
     */
    public float getX() { return m_x; }

    /**
     * Sets the data in x part of the vector.
     * @param x Data to the vector.
     */
    public void setX(float x) { this.m_x = x; }

    /**
     * Gets the y value in vector.
     * @return Y's value.
     */
    public float getY() { return m_y; }

    /**
     * Sets the data in y part of the vector.
     * @param y Data to the vector.
     */
    public void setY(float y) { this.m_y = y; }

    /**
     * Gets the z value in vector.
     * @return Z's value.
     */
    public float getZ() { return m_z; }

    /**
     * Sets the data in z part of the vector.
     * @param z Data to the vector.
     */
    public void setZ(float z) { this.m_z = z; }
    
}
