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

import engine.core.Vector3f;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2018
*/
public class BaseLight {
	
	private Vector3f color;
	private float intensity;
	
	/**
	 * Constructor of a basic light-point.
	 * @param color of the light.
	 * @param intensity of the light.
	 */
	public BaseLight(Vector3f color, float intensity) {	
		this.color = color;
		this.intensity = intensity;
	}

	/**
	 * Returns the color of the light.
	 * @return Color of the light.
	 */
	public Vector3f getColor() {
		return color;
	}

	/**
	 * Sets a new color for the light-point.
	 * @param color of the light.
	 */
	public void setColor(Vector3f color) {
		this.color = color;
	}

	/**
	 * Returns the light-point's intensity.
	 * @return Light-point's intensity.
	 */
	public float getIntensity() {
		return intensity;
	}

	/**
	 * Sets a new intensity for the light-point.
	 * @param intensity of the light-point.
	 */
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}
