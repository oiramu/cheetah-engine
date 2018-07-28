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

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class Menu {
	
	protected MenuModel m_menu;
	
	/**
	 * Constructor for a menu node.
	 */
	public Menu() {m_menu = null;}
	
	//Empty methods, different for each game
	public void initialization() {}
	public void update() {}
	public void draw3D() {}
	public void draw2D() {}
	
	/**
	 * Returns of a simple menu.
	 * @return Menu.
	 */
	public MenuModel getMenu() {return m_menu;}

	/**
	 * Sets a new menu node.
	 * @param menu Menu.
	 */
	public void setMenu(MenuModel menu) {this.m_menu = menu;}
	
}