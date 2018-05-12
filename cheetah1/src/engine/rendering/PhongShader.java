/*
 * Copyright 2018 Carlos Rodriguez.
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

import engine.core.Matrix4f;
import engine.core.ResourceLoader;
import engine.core.Transform;
import engine.core.Vector3f;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class PhongShader extends Shader {

    private static final PhongShader instance = new PhongShader();
    private static Vector3f ambientLight;
    private static DirectionalLight directionalLight = new DirectionalLight(
    		new BaseLight(new Vector3f(0,0,0), 0), new Vector3f(0,0,0));

    /**
     * Instances the shader to be used.
     * @return Shader.
     */
    public static PhongShader getInstance() {
        return instance;
    }
    
    /**
     * Constructor of the basic shader with all his uniforms.
     */
    private PhongShader() {
        super();

        addVertexShader(ResourceLoader.loadShader("phongVertex"));
        addFragmentShader(ResourceLoader.loadShader("phongFragment"));
        compileShader();

        addUniform("transform");
        addUniform("transformProjected");
        addUniform("baseColor");
        addUniform("ambientLight");
        
        addUniform("specularIntensity");
        addUniform("specularPower");
        addUniform("eyePos");
        
        addUniform("directionalLight.base.color");
        addUniform("directionalLight.base.intensity");
        addUniform("directionalLight.direction");
    }

    /**
     * Updates all the uniforms of the shading program.
     * @param worldMatrix World matrix data.
     * @param projectedMatrix Projection matrix data.
     * @param material Material of the object.
     */
    public void updateUniforms(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material) {
        if (material.getTexture() != null) {
            material.getTexture().bind();
        } else {
            RenderUtil.unbindTextures();
        }

        setUniform("transform", worldMatrix);
        setUniform("transformProjected", projectedMatrix);
        setUniform("baseColor", material.getColor());
        
        setUniform("ambientLight", ambientLight);
        setUniform("directionalLight", directionalLight);
        
        setUniformf("specularIntensity", material.getSpecularIntensity());
        setUniformf("specularPower", material.getSpecularPower());
        
        setUniform("eyePos", Transform.getCamera().getPos());
    }

	/**
	 * Returns the actual ambient light.
	 * @return the ambient light.
	 */
	public static Vector3f getAmbientLight() {
		return ambientLight;
	}

	/**
	 * Sets the actual ambient light to a new ambient light.
	 * @param ambientLight to set.
	 */
	public static void setAmbientLight(Vector3f ambientLight) {
		PhongShader.ambientLight = ambientLight;
	}
	
	/**
     * Sets a new directional light for the directional-light.
     * @param directionalLight To set.
     */
	public static void setDirectionalLight(DirectionalLight directionalLight) {
		PhongShader.directionalLight = directionalLight;
	}
	
	/**
	 * Sets a new uniform of color by name and the intensity by name.
	 * @param uniformName Name in baseLight.
	 * @param baseLight of the uniformName.
	 */
	public void setUniform(String uniformName, BaseLight baseLight) {
		setUniform(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());
	}
	
	/**
	 * Sets a new uniform of base by name and the intensity by direction.
	 * @param uniformName Name in directionalLight.
	 * @param directionalLight of the uniformName.
	 */
	public void setUniform(String uniformName, DirectionalLight directionalLight) {
		setUniform(uniformName + ".base", directionalLight.getBase());
		setUniform(uniformName + ".direction", directionalLight.getDirection());
	}
}
