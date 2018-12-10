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
 * @version 1.1
 * @since 2017
 */
public class Transform {

    private Vector3f 		position;
    private Vector3f 		rotation;
    private Vector3f 		scale;
    
    /**
     * Basic constructor for a transform.
     */
    public Transform() { this(new Vector3f(0, 0, 0)); }

    /**
     * The main transform constructor of an object in a 3D space.
     * @param position of the object.
     */
    public Transform(Vector3f position) {
        this.position = position;
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
    }
    
    /**
     * Returns the transformation data on the translation matrix,
     * rotation matrix and the scale matrix.
     * @return Transformation.
     */
    public Matrix4f getTransformation() {
        Matrix4f translationMatrix = new Matrix4f().initTranslation(position.getX(), position.getY(), position.getZ());
        Matrix4f rotationMatrix = new Matrix4f().initRotation(rotation.getX(), rotation.getY(), rotation.getZ());
        Matrix4f scaleMatrix = new Matrix4f().initScale(scale.getX(), scale.getY(), scale.getZ());

        return translationMatrix.mul(rotationMatrix.mul(scaleMatrix));
    }

    /**
     * Returns the 3D position of the transform.
     * @return 3D position of the transform.
     */
    public Vector3f getPosition() {return position;}

    /**
     * Sets a new 3D position for the transform into a vector.
     * @param position in a 3D space.
     */
    public void setPosition(Vector3f position) {this.position = position;}

    /**
     * Sets a new 3D position for the transform into a vector but
     * with floating point data.
     * @param x axis.
     * @param y axis.
     * @param z axis.
     */
    public void setPosition(float x, float y, float z) {this.position = new Vector3f(x, y, z);}

    /**
     * Returns the 3D rotation of the transform.
     * @return 3D rotation of the transform.
     */
    public Vector3f getRotation() {return rotation;}
    
    /**
     * Sets a new 3D rotation for the transform into a vector.
     * @param rotation to set.
     */
    public void setRotation(Vector3f rotation) {this.rotation = rotation;}

    /**
     * Sets a new 3D rotation for the transform into a vector but
     * with floating point data.
     * @param x axis.
     * @param y axis.
     * @param z axis.
     */
    public void setRotation(float x, float y, float z) {this.rotation = new Vector3f(x, y, z);}

    /**
     * Gets the scale of the transform
     * @return scale.
     */
    public Vector3f getScale() {return scale;}

    /**
     * Sets a new scale for the transform into a vector.
     * @param scale vector data.
     */
    public void setScale(Vector3f scale) {this.scale = scale;}

    /**
     * Sets a new scale for the transform into a vector but
     * with floating point data.
     * @param x axis.
     * @param y axis.
     * @param z axis.
     */
    public void setScale(float x, float y, float z) {this.scale = new Vector3f(x, y, z);}

    /**
     * Sets a linear scaling for the transform.
     * @param amt Amount.
     */
    public void setScale(float amt) {setScale(amt, amt, amt);}
    
}