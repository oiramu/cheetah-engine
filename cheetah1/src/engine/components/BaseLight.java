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
* @version 1.0
* @since 2018
*/
public class BaseLight {
	
	private Vector3f 	m_color;
	private float 		m_intensity;
	private Shader 		m_shader;
	
	/**
	 * Constructor of a basic light-point.
	 * @param color of the light.
	 * @param intensity of the light.
	 */
	public BaseLight(Vector3f color, float intensity) {	
		this.m_color = color;
		this.m_intensity = intensity;
	}
	
	/**
	 * Gets the shader used by the light.
	 * @return Shader.
	 */
	public Shader getShader() { return m_shader; }
	
	/**
	 * Sets a shader for a light.
	 * @param shader to set.
	 */
	public void setShader(Shader shader) {this.m_shader = shader;}

	/**
	 * Returns the color of the light.
	 * @return Color of the light.
	 */
	public Vector3f getColor() {return m_color;}

	/**
	 * Sets a new color for the light-point.
	 * @param color of the light.
	 */
	public void setColor(Vector3f color) {this.m_color = color;}

	/**
	 * Returns the light-point's intensity.
	 * @return Light-point's intensity.
	 */
	public float getIntensity() {return m_intensity;}

	/**
	 * Sets a new intensity for the light-point.
	 * @param intensity of the light-point.
	 */
	public void setIntensity(float intensity) {this.m_intensity = intensity;}
	
}
