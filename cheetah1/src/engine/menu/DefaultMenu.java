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

import engine.menu.gui.GUIMouse;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class DefaultMenu extends Menu {

	private GUIMouse mouse;

	/**
	 * Default menu constructor.
	 */
	public DefaultMenu() {
		menu = new MenuModel("res/menu/menu.txt");
		mouse = new GUIMouse();
	}
	
	/**
	 * Updates the menu and mouse rendering every frame.
	 */
	public void update() {
		mouse.update();
		
		if(menu != null)
			menu.update();
	}
	
	/**
	 * Draws the menu and mouse on screen.
	 */
	public void draw2D() {
		if(menu != null)
			menu.draw2D();
		
		mouse.draw2D();
	}
}
