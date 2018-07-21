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
 * @version 1.1
 * @since 2018
 */
public class Attenuation extends Vector3f {
	
	/**
	 * Constructor of the attenuation of the light.
	 * @param constant of attenuation
	 * @param linear function of attenuation
	 * @param exponent of attenuation
	 */
	public Attenuation(float constant, float linear, float exponent) {
		super(constant, linear, exponent);
	}
	
	/**
	 * Returns the constant of the attenuation.
	 * @return Constant
	 */
	public float getConstant() { return getX(); }
	
	/**
	 * Returns the linear function of attenuation.
	 * @return Linear function
	 */
	public float getLinear() { return getY(); }
	
	/**
	 * Returns the exponent of attenuation.
	 * @return Exponent
	 */
	public float getExponent() { return getZ(); }
	
}
