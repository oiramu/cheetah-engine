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

import engine.core.Transform;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class ForwardDirectional extends Shader {
	
	private static final ForwardDirectional instance = new ForwardDirectional();

	/**
     * Instances the shader to be used.
     * @return Shader.
     */
	public static ForwardDirectional getInstance() {return instance;}

	/**
     * Constructor of the shader of the directional-light.
     */
	private ForwardDirectional() {
		super("forward-directional");
	}

	/**
     * Updates all the uniforms of the shading program.
     * @param transform of the object.
     * @param material Material of the object.
     */
	@Override
	public void updateUniforms(Transform transform, Material material) {
		super.updateUniforms(transform, material);
	}

}
