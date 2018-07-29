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

	private Texture 	m_diffuse;
	private Texture 	m_normalMap;
	private Texture 	m_dispMap;
    private Vector3f 	m_color;
	private float 		m_specularIntensity;
	private float 		m_specularPower;
	private float 		m_dispMapScale;
	private float 		m_dispMapBias;
	
	/**
     * Constructor of the texture material of a mesh.
     */
    public Material() {this(new Texture("default_diffuse"), new Vector3f(1, 1, 1));}

    /**
     * Constructor of the texture material of a mesh.
     * @param diffuse texture of a mesh.
     */
    public Material(Texture diffuse) {this(diffuse, new Vector3f(1, 1, 1));}

    /**
     * Constructor of the texture and color material of a mesh.
     * @param diffuse texture of a mesh.
     * @param color of a mesh.
     */
    public Material(Texture diffuse, Vector3f color) {this(diffuse, color, 1, 8);}
    
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
        this(diffuse, color, specularIntensity, specularPower, new Texture("default_normal"), 
        		new Texture("default_disp"), 0, 0);//0.03f, -0.5f
    }
    
    /**
     * Constructor of the texture with color material, with
     * the "spectacular"-lighting calculations of the mesh, 
     * normal mapping and the displacement map.
     * @param diffuse texture of a mesh.
     * @param specularIntensity of the material.
     * @param specularPower of the material.
     * @param normal map sampler.
     * @param dispMap sampler.
     * @param dispMapScale of the displacement map.
     * @param dispMapOffset of the displacement map.
     */
    public Material(Texture diffuse, float specularIntensity, float specularPower,
    		Texture normal, Texture dispMap, float dispMapScale, float dispMapOffset) {
    	this(diffuse, new Vector3f(1,1,1), specularIntensity, specularPower, normal, 
    			dispMap, dispMapScale, dispMapOffset);
    }
    
    /**
     * Constructor of the texture with color material, with
     * the "spectacular"-lighting calculations of the mesh, 
     * normal mapping and the displacement map.
     * @param diffuse texture of a mesh.
     * @param color of a mesh.
     * @param specularIntensity of the material.
     * @param specularPower of the material.
     * @param normal map sampler.
     * @param dispMap sampler.
     * @param dispMapScale of the displacement map.
     * @param dispMapOffset of the displacement map.
     */
    public Material(Texture diffuse, Vector3f color, float specularIntensity,
    		float specularPower, Texture normal, Texture dispMap, float dispMapScale,
    		float dispMapOffset) {
        this.m_diffuse = diffuse;
        this.m_color = color;
        this.m_specularIntensity = specularIntensity;
        this.m_specularPower = specularPower;
        this.m_normalMap = normal;
        this.m_dispMap = dispMap;
        float baseBias = dispMapScale/2.0f;
        this.m_dispMapScale = dispMapScale;
        this.m_dispMapBias = -baseBias + baseBias * dispMapOffset;
    }

    /**
     * Returns the diffuse texture of the material.
     * @return Diffuse texture.
     */
    public Texture getDiffuse() {return m_diffuse;}

    /**
     * Sets the diffuse texture to the material.
     * @param diffuse texture for the material.
     */
    public void setDiffuse(Texture diffuse) {this.m_diffuse = diffuse;}

    /**
     * Returns the color of the material.
     * @return Color.
     */
    public Vector3f getColor() {return m_color;}

    /**
     * Sets the color to the material.
     * @param color for the material.
     */
    public void setColor(Vector3f color) {this.m_color = color;}
	
    /**
     * Returns the spectacular intensity of the material.
     * @return Spectacular intensity.
     */
	public float getSpecularIntensity() {return m_specularIntensity;}

	/**
     * Sets the spectacular intensity for the material.
     * @param specularIntensity Intensity.
     */
	public void setSpecularIntensity(float specularIntensity) {this.m_specularIntensity = specularIntensity;}

	/**
     * Returns the spectacular power of the material.
     * @return Spectacular power.
     */
	public float getSpecularPower() {return m_specularPower;}

	/**
     * Sets the spectacular power for the material.
     * @param specularPower Power.
     */
	public void setSpecularPower(float specularPower) {this.m_specularPower = specularPower;}

	/**
	 * Returns the normal map of the material.
	 * @return the normalMap.
	 */
	public Texture getNormalMap() {return m_normalMap;}

	/**
	 * Sets the normal map for the material.
	 * @param normalMap to set.
	 */
	public void setNormalMap(Texture m_normalMap) {this.m_normalMap = m_normalMap;}

	/**
	 * Returns the displacement map of the material.
	 * @return the dispMap.
	 */
	public Texture getDispMap() {return m_dispMap;}

	/**
	 * Sets the displacement map for the material.
	 * @param dispMap to set.
	 */
	public void setDispMap(Texture dispMap) {this.m_dispMap = dispMap;}

	/**
	 * Returns the displacement map's scale of the material.
	 * @return the dispMapScale.
	 */
	public float getDispMapScale() {return m_dispMapScale;}

	/**
	 * Sets the displacement map's scale for the material.
	 * @param dispMapScale to set.
	 */
	public void setDispMapScale(float dispMapScale) {this.m_dispMapScale = dispMapScale;}

	/**
	 * Returns the displacement map's offset of the material.
	 * @return the dispMapOffset.
	 */
	public float getDispMapBias() {return m_dispMapBias;}

	/**
	 * Sets the displacement map's offset for the material.
	 * @param dispMapOffset to set
	 */
	public void setDispMapBias(float dispMapOffset) {
		float baseBias = getDispMapScale()/2.0f;
		this.m_dispMapBias = -baseBias + baseBias * dispMapOffset;
	}
	
}