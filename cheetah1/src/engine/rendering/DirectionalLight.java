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
public class DirectionalLight {
	
	private BaseLight base;
	private Vector3f direction;
	
	/**
	 * Constructor of the directional light.
	 * @param base Light-point.
	 * @param direction for the light-point.
	 */
	public DirectionalLight(BaseLight base, Vector3f direction) {
		this.base = base;
		this.direction = direction.normalized();
	}

	/**
	 * Returns the light-point of the directional light.
	 * @return Light-point.
	 */
	public BaseLight getBase() {
		return base;
	}

	/**
	 * Sets a new light-point for the directional light.
	 * @param base New light-point.
	 */
	public void setBase(BaseLight base) {
		this.base = base;
	}

	/**
	 * Returns the direction the directional light.
	 * @return Direction the directional light.
	 */
	public Vector3f getDirection() {
		return direction;
	}

	/**
	 * Sets a new direction for the directional light.
	 * @param direction
	 */
	public void setDirection(Vector3f direction) {
		this.direction = direction.normalized();
	}
}
