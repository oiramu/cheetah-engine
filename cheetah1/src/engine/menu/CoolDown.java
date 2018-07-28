/*
 * Copyright 2017 Carlos Rodriguez.
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
package engine.menu;

import engine.menu.system.SGameTime;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class CoolDown {
	
	private float m_timerInstance;
	
	/**
	 * Time cooler constructor.
	 */
	public CoolDown() {m_timerInstance = 0.0f;}
	
	/**
	 * Cools down the time counter.
	 * @param time to cool.
	 * @return time at 0 or else the same time.
	 */
	public boolean coolDownTime(float time) {
		m_timerInstance += SGameTime.getInstance().getElapsedGameTime();

		if (m_timerInstance >= time) {
			m_timerInstance = 0.0f;
			return true;
		} else
			return false;
	}
}
