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
import engine.rendering.ForwardSpot;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2018
 */
public class SpotLight extends PointLight {

	private Vector3f direction;
	private float cutoff;
	
	/**
	 * Constructor of the spot of light.
	 * @param pointLight for the spotLight
	 * @param direction of the spotLight
	 * @param cutoff of the spotLight
	 */
	public SpotLight(Vector3f color, float intensity, Attenuation atten, Vector3f position, Vector3f direction, float cutoff) {
		super(color, intensity, atten, position);
		this.direction = direction.normalized();
		this.cutoff = cutoff;
		setShader(ForwardSpot.getInstance());
	}
	
	/**
	 * Returns the direction of the spot of light.
	 * @return Direction
	 */
	public Vector3f getDirection() {return direction;}
	
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
	public float getCutoff() {return cutoff;}
	
	/**
	 * Sets a new cut-off to the spot of light.
	 * @param Cut-off to set
	 */
	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}
	
}
