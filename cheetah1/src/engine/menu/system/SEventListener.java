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
package engine.menu.system;

import engine.core.CoreEngine;
import engine.core.ResourceLoader;
import engine.menu.MenuModel;
import engine.rendering.Window;
import game.Auschwitz;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class SEventListener {
	
	private static final int EPISODE_1 = 1;
	private static final int EPISODE_2 = 10;
	private static final int EPISODE_3 = 20;
	
	/**
	 * Constructors of the event listener.
	 */
	private SEventListener() {}
	private static SEventListener INSTANCE = new SEventListener();
	public static SEventListener getInstance()
	{	return INSTANCE;	}
	
	/**
	 * Load the information at the event in a button from the menu.
	 * @param actionId Index of action.
	 */
	public void addEvent(int actionId) {
		switch(actionId) {
			case 1:
				//Exit Event
				System.exit(0);
				break;
			case 3:
				//Load game
				Auschwitz.setStartingLevel(EPISODE_1);
				CoreEngine game1 = new CoreEngine();
				game1.start();
		        System.exit(0);
				break;
			case 4:
				//Load game
				Auschwitz.setStartingLevel(EPISODE_2);
				CoreEngine game2 = new CoreEngine();
				game2.start();
		        System.exit(0);
				break;
			case 5:
				//Load game
				Auschwitz.setStartingLevel(EPISODE_3);	
				CoreEngine game3 = new CoreEngine();
				game3.start();
		        System.exit(0);
				break;
			case 6:
				ResourceLoader.mipMapSamples = 4;
				break;
			case 7:
				ResourceLoader.mipMapSamples = 1;
				break;
			case 8:
				ResourceLoader.mipMapSamples = -4;
				break;
		}
	}
	
	/**
	 * Load the information at the event in a button from the menu.
	 * @param actionId Index of action.
	 * @param menuPath Next menu.
	 */
	public void addEvent(int actionId, String menuPath) {
		switch(actionId) {
			case 2:
				//Load new menu
				if(Window.getMenu().getMenu() != null) //Destroy last Menu
					Window.getMenu().getMenu().delete();
					
				Window.getMenu().setMenu(new MenuModel(menuPath));
				break;
		}
	}
}
