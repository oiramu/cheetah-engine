/*
 * Copyright 2019 Carlos Rodriguez.
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
 * @since 2019
 */
public class CreditsMenu extends Menu {

	private GUIMouse m_mouse;

	/**
	 * Default menu constructor.
	 */
	public CreditsMenu() {
		m_menu = new MenuModel("res/menu/credit.txt");
		m_mouse = new GUIMouse();
	}
	
	/**
	 * Updates the menu and mouse rendering every frame.
	 */
	public void update() {
		m_mouse.update();
		
		if(m_menu != null)
			m_menu.update();
	}
	
	/**
	 * Draws the menu and mouse on screen.
	 */
	public void draw2D() {
		if(m_menu != null)
			m_menu.draw2D();
		
		m_mouse.draw2D();
	}
}
