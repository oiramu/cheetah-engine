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

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2018
*/
public class PointLight {
	
	private BaseLight baseLight;
	private Attenuation atten;
	private Vector3f position;
	private float range;
	
	/**
	 * Point of light object instance.
	 * @param baseLight of the light
	 * @param atten Attenuation of the light
	 * @param position of the light
	 * @param range of the light
	 */
	public PointLight(BaseLight baseLight, Attenuation atten, Vector3f position, float range) {
		this.baseLight = baseLight;
		this.atten = atten;
		this.position = position;		
		this.range = range;
	}
	/**
	 * Returns the base light of the point-light.
	 * @return Base light
	 */
	public BaseLight getBaseLight() {
		return baseLight;
	}
	
	/**
	 * Sets a new base light to the point-light.
	 * @param Base light to set
	 */
	public void setBaseLight(BaseLight baseLight) {
		this.baseLight = baseLight;
	}
	
	/**
	 * Returns the attenuation of the point-light.
	 * @return Attenuation
	 */
	public Attenuation getAtten() {
		return atten;
	}
	
	/**
	 * Sets a new attenuation to the point-light.
	 * @param Attenuation to set
	 */
	public void setAtten(Attenuation atten) {
		this.atten = atten;
	}
	
	/**
	 * Returns the position of the point-light.
	 * @return Position
	 */
	public Vector3f getPosition() {
		return position;
	}
	
	/**
	 * Sets a new position to the point-light.
	 * @param Position to set
	 */
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	/**
	 * Returns the range of the point-light.
	 * @return Range
	 */
	public float getRange() {
		return range;
	}
	
	/**
	 * Sets a new range to the point-light.
	 * @param Range to set
	 */
	public void setRange(float range) {
		this.range = range;
	}
	
}