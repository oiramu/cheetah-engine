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

import java.util.HashMap;

import engine.rendering.resourceManagement.MappedValues;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.2
 * @since 2017
 */
public class Material extends MappedValues {

	private HashMap<String, Texture> textureHashMap;

    /**
     * Constructor of the texture material of a mesh.
     * @param diffuse texture of a mesh.
     */
    public Material(Texture diffuse) {this(diffuse, 1, 8);}
    
    /**
     * Constructor of the texture with color material and with
     * the "spectacular"-lighting calculations of the mesh.
     * @param diffuse texture of a mesh.
     * @param specularIntensity of the material.
     * @param specularPower of the material.
     */
    public Material(Texture diffuse, float specularIntensity, float specularPower) {
        this(diffuse, specularIntensity, specularPower, new Texture("default_normalMap"), 
        		new Texture("default_dispMap"), 0, 0);//0.03f, -0.5f
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
    public Material(Texture diffuse, float specularIntensity, float specularPower, Texture normal,
            Texture dispMap, float dispMapScale, float dispMapOffset){
    	super();
    	if(textureHashMap == null)
    		textureHashMap = new HashMap<String, Texture>();
    	AddTexture("diffuse", diffuse);
		AddFloat("specularIntensity", specularIntensity);
		AddFloat("specularPower", specularPower);
		AddTexture("normalMap", normal);
		AddTexture("dispMap", dispMap);

		float baseBias = dispMapScale/2.0f;
		AddFloat("dispMapScale", dispMapScale);
		AddFloat("dispMapBias", -baseBias + baseBias * dispMapOffset);
    }
    
    /**
     * Adds a texture to the texture's hash-map.
     * @param name of the texture.
     * @param texture to add.
     */
    public void AddTexture(String name, Texture texture) { textureHashMap.put(name, texture); }

    /**
     * Returns the diffuse texture of the material.
     * @return Diffuse texture.
     */
    public Texture getTexture(String texture) {
    	Texture result = textureHashMap.get(texture);
    	if(result != null)
			return result;
    	return new Texture("default_"+texture);
    }

    /**
     * Sets the diffuse texture to the material.
     * @param diffuse texture for the material.
     */
    public void setDiffuse(Texture diffuse) { AddTexture("diffuse", diffuse); }
	
}