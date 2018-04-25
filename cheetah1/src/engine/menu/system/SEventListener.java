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

import engine.core.CoreDisplay;
import engine.core.CoreGame;
import engine.menu.MenuModel;
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
	
	private CoreDisplay coreEnginePointer = CoreDisplay.getEnginePointer();
	
	/**
	 * Load the information at the event in a button from the menu.
	 * @param actionId Index of action.
	 */
	public void addEvent(int actionId) {
		switch(actionId) {
			case 1:
				//Exit Event
				coreEnginePointer.stop();
				System.exit(0);
				break;
			case 3:
				//Load game
				Auschwitz.setStartingLevel(EPISODE_1);
				CoreGame game1 = new CoreGame();
				game1.start();
		        System.exit(0);
				break;
			case 4:
				//Load game
				Auschwitz.setStartingLevel(EPISODE_2);
				CoreGame game2 = new CoreGame();
				game2.start();
		        System.exit(0);
				break;
			case 5:
				//Load game
				Auschwitz.setStartingLevel(EPISODE_3);	
				CoreGame game3 = new CoreGame();
				game3.start();
		        System.exit(0);
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
				if(coreEnginePointer.getMenu().getMenu() != null) //Destroy last Menu
					coreEnginePointer.getMenu().getMenu().delete();
					
				coreEnginePointer.getMenu().setMenu(new MenuModel(menuPath));
				break;
		}
	}
}
