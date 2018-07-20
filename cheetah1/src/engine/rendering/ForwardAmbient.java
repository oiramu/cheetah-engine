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

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class ForwardAmbient extends Shader {
	
	private static final ForwardAmbient instance = new ForwardAmbient();

	/**
     * Instances the shader to be used.
     * @return Shader.
     */
	public static ForwardAmbient getInstance() {return instance;}

	/**
     * Constructor of the basic shader with all his uniforms.
     */
	private ForwardAmbient() {
		super();
		
		addVertexShaderFromFile(FORWARD+"forward-ambient-vs");
        addFragmentShaderFromFile(FORWARD+"forward-ambient-fs");

		setAttribLocation("position", 0);
		setAttribLocation("texCoord", 1);

		compileShader();

		addUniform("fogDensity");
        addUniform("fogGradient");
        addUniform("fogColor");
		
        addUniform("transform");
		addUniform("MVP");
		addUniform("eyePos");
		addUniform("ambientIntensity");
	}

	/**
     * Updates all the uniforms of the shading program.
     * @param worldMatrix World matrix data.
     * @param projectedMatrix Projection matrix data.
     * @param material Material of the object.
     */
	@SuppressWarnings("static-access")
	public void updateUniforms(Matrix4f worldMatrix, Matrix4f projectedMatrix, Material material) {
		material.getTexture().bind();

		setUniformf("fogDensity", getRenderingEngine().fogDensity);
        setUniformf("fogGradient", getRenderingEngine().fogGradient);
        setUniform("fogColor", getRenderingEngine().getFogColor());
        
        setUniform("transform", worldMatrix);
		setUniform("MVP", projectedMatrix);
		setUniform("eyePos", getRenderingEngine().getMainCamera().getPos());
		setUniform("ambientIntensity", getRenderingEngine().getAmbientLight());
	}
}
