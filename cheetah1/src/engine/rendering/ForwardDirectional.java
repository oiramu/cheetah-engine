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

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class ForwardDirectional extends Shader {
	
	private static final ForwardDirectional instance = new ForwardDirectional();
	
	private final String RESOURCE = "FORWARD/";

	/**
     * Instances the shader to be used.
     * @return Shader.
     */
	public static ForwardDirectional getInstance() {return instance;}

	/**
     * Constructor of the basic shader with all his uniforms.
     */
	private ForwardDirectional() {
		super();

		addVertexShader(ResourceLoader.loadShader(RESOURCE+"forward-directional-vs"));
        addFragmentShader(ResourceLoader.loadShader(RESOURCE+"forward-directional-fs"));

		setAttribLocation("position", 0);
		setAttribLocation("texCoord", 1);
		setAttribLocation("normal", 2);

		compileShader();

		addUniform("model");
		addUniform("MVP");

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
		material.getTexture().bind();

		setUniform("model", worldMatrix);
		setUniform("MVP", projectedMatrix);

		setUniformf("specularIntensity", material.getSpecularIntensity());
		setUniformf("specularPower", material.getSpecularPower());

		setUniform("eyePos", getRenderingEngine().getMainCamera().getPos());
		setUniform("directionalLight", getRenderingEngine().getDirectionalLight());
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
