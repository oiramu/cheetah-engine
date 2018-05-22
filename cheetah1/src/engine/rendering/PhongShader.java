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
	
	private static final int MAX_POINT_LIGHTS = 4;
	private static final int MAX_SPOT_LIGHTS = 4;

    private static final PhongShader instance = new PhongShader();
    private static Vector3f ambientLight;
    private static DirectionalLight directionalLight = new DirectionalLight(
    		new BaseLight(new Vector3f(0,0,0), 0), new Vector3f(0,0,0));
    private static PointLight[] pointLights = new PointLight[] {};
	private static SpotLight[] spotLights = new SpotLight[] {};

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
        
        for(int i = 0; i < MAX_POINT_LIGHTS; i++) {
			addUniform("pointLights[" + i + "].base.color");
			addUniform("pointLights[" + i + "].base.intensity");
			addUniform("pointLights[" + i + "].atten.constant");
			addUniform("pointLights[" + i + "].atten.linear");
			addUniform("pointLights[" + i + "].atten.exponent");
			addUniform("pointLights[" + i + "].position");
			addUniform("pointLights[" + i + "].range");
		}
		
		for(int i = 0; i < MAX_SPOT_LIGHTS; i++) {
			addUniform("spotLights[" + i + "].pointLight.base.color");
			addUniform("spotLights[" + i + "].pointLight.base.intensity");
			addUniform("spotLights[" + i + "].pointLight.atten.constant");
			addUniform("spotLights[" + i + "].pointLight.atten.linear");
			addUniform("spotLights[" + i + "].pointLight.atten.exponent");
			addUniform("spotLights[" + i + "].pointLight.position");
			addUniform("spotLights[" + i + "].pointLight.range");
			addUniform("spotLights[" + i + "].direction");
			addUniform("spotLights[" + i + "].cutoff");
		}
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
        
        for(int i = 0; i < pointLights.length; i++)
			setUniform("pointLights[" + i + "]", pointLights[i]);
		
		for(int i = 0; i < spotLights.length; i++)
			setUniform("spotLights[" + i + "]", spotLights[i]);
        
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
	 * Sets a new point of light into the pointlight's array.
	 * @param pointLights to set
	 */
	public static void setPointLight(PointLight[] pointLights) {
		if(pointLights.length > MAX_POINT_LIGHTS) {
			System.err.println("Error: You passed in too many point lights. Max allowed is " + MAX_POINT_LIGHTS + ", you passed in " + pointLights.length);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		PhongShader.pointLights = pointLights;
	}
	
	/**
	 * Sets a new spot of light into the spotLight's array.
	 * @param spotLights to set
	 */
	public static void setSpotLights(SpotLight[] spotLights) {
		if(spotLights.length > MAX_SPOT_LIGHTS) {
			System.err.println("Error: You passed in too many spot lights. Max allowed is " + MAX_SPOT_LIGHTS + ", you passed in " + spotLights.length);
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		PhongShader.spotLights = spotLights;
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
	
	/**
	 * Sets a new uniform of base by name and the pointLight constructor.
	 * @param uniformName Name in pointLight.
	 * @param pointLight's constructor.
	 */
	public void setUniform(String uniformName, PointLight pointLight) {
		setUniform(uniformName + ".base", pointLight.getBaseLight());
		setUniformf(uniformName + ".atten.constant", pointLight.getAtten().getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getAtten().getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getAtten().getExponent());
		setUniform(uniformName + ".position", pointLight.getPosition());
		setUniformf(uniformName + ".range", pointLight.getRange());
	}
	
	/**
	 * Sets a new uniform of base by name and the spotLight constructor.
	 * @param uniformName Name in pointLight.
	 * @param spotLight's constructor.
	 */
	public void setUniform(String uniformName, SpotLight spotLight) {
		setUniform(uniformName + ".pointLight", spotLight.getPointLight());
		setUniform(uniformName + ".direction", spotLight.getDirection());
		setUniformf(uniformName + ".cutoff", spotLight.getCutoff());
	}
}
