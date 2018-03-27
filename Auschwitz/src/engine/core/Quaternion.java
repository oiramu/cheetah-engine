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
public class Quaternion {

    private float x;
    private float y;
    private float z;
    private float w;

    /**
     * Constructor of a QUATERNION calculation.
     * @param x axis.
     * @param y axis.
     * @param z axis.
     * @param w axis.
     */
    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Returns the length of the QUATERNION.
     * @return Length.
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    /**
     * Normalizes the QUATERNION data.
     * @return QUATERNION data normalized.
     */
    public Quaternion normalize() {
        float length = length();

        return new Quaternion(x / length, y / length, z / length, w / length);
    }

    /**
     * CONJUGATES the QUATERNION axis.
     * @return CONJUGATED.
     */
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    /**
	 * Multiply the QUATERNION by other QUATERNION.
	 * @param r multiplier.
	 * @return Multiplied QUATERNION.
	 */
    public Quaternion mul(Quaternion r) {
        float w_ = w * r.getW() - x * r.getX() - y * r.getY() - z * r.getZ();
        float x_ = x * r.getW() + w * r.getX() + y * r.getZ() - z * r.getY();
        float y_ = y * r.getW() + w * r.getY() + z * r.getX() - x * r.getZ();
        float z_ = z * r.getW() + w * r.getZ() + x * r.getY() - y * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }

    /**
	 * Multiply the QUATERNION by a vector.
	 * @param r multiplier.
	 * @return Multiplied QUATERNION.
	 */
    public Quaternion mul(Vector3f r) {
        float w_ = -x * r.getX() - y * r.getY() - z * r.getZ();
        float x_ = w * r.getX() + y * r.getZ() - z * r.getY();
        float y_ = w * r.getY() + z * r.getX() - x * r.getZ();
        float z_ = w * r.getZ() + x * r.getY() - y * r.getX();

        return new Quaternion(x_, y_, z_, w_);
    }

    /**
     * Returns the x axis.
     * @return x axis.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets data to x axis.
     * @param x data.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Returns the y axis.
     * @return y axis.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets data to y axis.
     * @param y data.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Returns the z axis.
     * @return z axis.
     */
    public float getZ() {
        return z;
    }

    /**
     * Sets data to z axis.
     * @param z data.
     */
    public void setZ(float z) {
        this.z = z;
    }

    /**
     * Returns the w axis.
     * @return w axis.
     */
    public float getW() {
        return w;
    }

    /**
     * Sets data to w axis.
     * @param w data.
     */
    public void setW(float w) {
        this.w = w;
    }
}
