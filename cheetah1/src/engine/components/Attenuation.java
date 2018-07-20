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

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class Attenuation {
	
	private float constant;
	private float linear;
	private float exponent;
	
	/**
	 * Constructor of the attenuation of the light.
	 * @param constant of attenuation
	 * @param linear function of attenuation
	 * @param exponent of attenuation
	 */
	public Attenuation(float constant, float linear, float exponent) {
		this.constant = constant;
		this.linear = linear;
		this.exponent = exponent;
	}
	
	/**
	 * Returns the constant of the attenuation.
	 * @return Constant
	 */
	public float getConstant() {
		return constant;
	}
	
	/**
	 * Sets a new constant to the attenuation.
	 * @param Constant to set
	 */
	public void setConstant(float constant) {
		this.constant = constant;
	}
	
	/**
	 * Returns the linear function of attenuation.
	 * @return Linear function
	 */
	public float getLinear() {
		return linear;
	}
	
	/**
	 * Sets a new linear function to the attenuation.
	 * @param Linear function to set
	 */
	public void setLinear(float linear) {
		this.linear = linear;
	}
	
	/**
	 * Returns the exponent of attenuation.
	 * @return Exponent
	 */
	public float getExponent() {
		return exponent;
	}
	
	/**
	 * Sets a new exponent to the attenuation.
	 * @param Exponent to set
	 */
	public void setExponent(float exponent) {
		this.exponent = exponent;
	}
	
}
