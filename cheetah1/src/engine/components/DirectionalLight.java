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
package engine.components;

import engine.core.Vector3f;
import engine.rendering.Shader;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2018
 */
public class DirectionalLight extends BaseLight {
	
	private Vector3f m_direction;
	
	/**
	 * Constructor of the directional light.
	 * @param color of the light.
	 * @param intensity of the light.
	 * @param direction for the light-point.
	 */
	public DirectionalLight(Vector3f color, float intensity, Vector3f direction) {
		super(color, intensity);
		this.m_direction = direction.normalized();
		setShader(new Shader("forward-directional"));
	}

	/**
	 * Returns the direction the directional light.
	 * @return Direction the directional light.
	 */
	public Vector3f getDirection() {return m_direction;}

	/**
	 * Sets a new direction for the directional light.
	 * @param direction
	 */
	public void setDirection(Vector3f direction) {this.m_direction = direction.normalized();}
	
}
