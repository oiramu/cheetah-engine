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
public class SpotLight {
	
	private PointLight pointLight;
	private Vector3f direction;
	private float cutoff;
	
	/**
	 * Constructor of the spot of light.
	 * @param pointLight for the spotLight
	 * @param direction of the spotLight
	 * @param cutoff of the spotLight
	 */
	public SpotLight(PointLight pointLight, Vector3f direction, float cutoff) {
		this.pointLight = pointLight;
		this.direction = direction.normalized();
		this.cutoff = cutoff;
	}
	
	/**
	 * Returns the pointLight of the spot of light.
	 * @return PointLight
	 */
	public PointLight getPointLight() {
		return pointLight;
	}
	
	/**
	 * Sets a new pointLight to the spot of light.
	 * @param pointLight to set
	 */
	public void setPointLight(PointLight pointLight) {
		this.pointLight = pointLight;
	}
	
	/**
	 * Returns the direction of the spot of light.
	 * @return Direction
	 */
	public Vector3f getDirection() {
		return direction;
	}
	
	/**
	 * Sets a new direction to the spot of light.
	 * @param Direction to set
	 */
	public void setDirection(Vector3f direction) {
		this.direction = direction.normalized();
	}
	
	/**
	 * Returns the cut-off of the spot of light.
	 * @return Cut-off
	 */
	public float getCutoff() {
		return cutoff;
	}
	
	/**
	 * Sets a new cut-off to the spot of light.
	 * @param Cut-off to set
	 */
	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}
	
}
