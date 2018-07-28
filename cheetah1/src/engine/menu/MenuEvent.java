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

import engine.menu.system.SEventListener;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class MenuEvent {
	
	/*
	 * List of event:
	 * 		- "0" = void
	 * 		- "1" = exit 
	 * 		- "2" = load Menu (1 parameter)
	 * 		- "3" = load World (1 parameter)
	 */
	private int 	m_actionId = 0;
	private String 	m_parameter = null;
	
	/**
	 * Constructor for the events of the menu.
	 * @param eventName String name of the event.
	 */
	public MenuEvent(String eventName) {
		switch(eventName) {
			case "exit":
				m_actionId = 1;
				break;
			case "loadEpisode1":
				m_actionId = 3;
				break;
			case "loadEpisode2":
				m_actionId = 4;
				break;
			case "loadEpisode3":
				m_actionId = 5;
				break;
			case "highDetailBtn":
				m_actionId = 6;
				break;
			case "mediumDetailBtn":
				m_actionId = 7;
				break;
			case "lowDetailBtn":
				m_actionId = 8;
				break;
			default:
				m_actionId = 0;
				break;
		}
	}
	
	/**
	 * Constructor for the events of the menu.
	 * @param eventName String name of the event.
	 * @param parameter for events whit it.
	 */
	public MenuEvent(String eventName, String parameter) {
		this.m_parameter = parameter;
		switch(eventName) {
			case "exit":
				m_actionId = 1;
				break;
			case "loadMenu":
				m_actionId = 2;
				break;
			default:
				m_actionId = 0;
				break;
		}
	}
	
	/**
	 * Updates the index and parameter.
	 */
	public void update() {
		if(m_parameter == null)
			SEventListener.getInstance().addEvent(m_actionId);
		else 
			SEventListener.getInstance().addEvent(m_actionId, m_parameter);
	}
}
