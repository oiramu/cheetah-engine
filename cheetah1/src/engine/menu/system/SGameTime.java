/*
aaA * Copyright 2017 Carlos Rodriguez.
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
package engine.menu.system;

import engine.core.Time;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class SGameTime {
	
	/**
	 * Constructors of the gameTime.
	 */
	private SGameTime() {
		start = 0.0f;
		totalGameTime = 0.0f;
		elapsedGameTime = 0.0f;
	}
	
	private static SGameTime INSTANCE = new SGameTime();
	public static SGameTime getInstance()
	{	return INSTANCE;	}
	
	private double start, totalGameTime, elapsedGameTime;

	/**
	 * Updates the time while the program is running.
	 */
	public void update() {
		double time = Time.getTime() / 1.0;
		elapsedGameTime = (time - start) / 1.0;
		
		start = time;
		totalGameTime += elapsedGameTime;
	}
	
	/**
	 * Returns the total time of gaming.
	 * @return total time.
	 */
	public double getTotalGameTime() {
		return totalGameTime;
	}
	
	/**
	 * Returns the total time of elapsed.
	 * @return total elapsed time.
	 */
	public double getElapsedGameTime() {
		return elapsedGameTime;
	}
}
