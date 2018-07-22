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
package engine.rendering;

import engine.core.Vector3f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2017
 */
public class Material {

	private Texture diffuse;
    private Vector3f color;
	private float specularIntensity;
	private float specularPower;
	
	/**
     * Constructor of the texture material of a mesh.
     */
    public Material() {
        this(new Texture("null"), new Vector3f(1, 1, 1));
    }

    /**
     * Constructor of the texture material of a mesh.
     * @param diffuse texture of a mesh.
     */
    public Material(Texture diffuse) {
        this(diffuse, new Vector3f(1, 1, 1));
    }

    /**
     * Constructor of the texture and color material of a mesh.
     * @param diffuse texture of a mesh.
     * @param color of a mesh.
     */
    public Material(Texture diffuse, Vector3f color) {
        this(diffuse, color, 1, 8);
    }
    
    /**
     * Constructor of the texture with color material and with
     * the "spectacular"-lighting calculations of the mesh.
     * @param diffuse texture of a mesh.
     * @param color of a mesh.
     * @param specularIntensity of the material.
     * @param specularPower of the material.
     */
    public Material(Texture diffuse, Vector3f color, float specularIntensity,
    		float specularPower) {
        this.diffuse = diffuse;
        this.color = color;
        this.specularIntensity = specularIntensity;
        this.specularPower = specularPower;
    }

    /**
     * Returns the diffuse texture of the material.
     * @return Diffuse texture.
     */
    public Texture getDiffuse() {return diffuse;}

    /**
     * Sets the diffuse texture to the material.
     * @param diffuse texture for the material.
     */
    public void setDiffuse(Texture diffuse) {this.diffuse = diffuse;}

    /**
     * Returns the color of the material.
     * @return Color.
     */
    public Vector3f getColor() {return color;}

    /**
     * Sets the color to the material.
     * @param color for the material.
     */
    public void setColor(Vector3f color) {this.color = color;}
	
    /**
     * Returns the spectacular intensity of the material.
     * @return Spectacular intensity.
     */
	public float getSpecularIntensity() {return specularIntensity;}

	/**
     * Sets the spectacular intensity of the material.
     * @param specularIntensity Intensity.
     */
	public void setSpecularIntensity(float specularIntensity) {this.specularIntensity = specularIntensity;}

	/**
     * Returns the spectacular power of the material.
     * @return Spectacular power.
     */
	public float getSpecularPower() {return specularPower;}

	/**
     * Sets the spectacular power of the material.
     * @param specularPower Power.
     */
	public void setSpecularPower(float specularPower) {this.specularPower = specularPower;}
	
}
