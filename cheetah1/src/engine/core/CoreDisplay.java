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
package engine.core;

import engine.rendering.Window;

/**
*
* @author Carlos Rodriguez
* @version 1.4
* @since 2017
*/
public class CoreDisplay {
	
	private int width;
	private int height;
	private static double frameTime;
	private boolean fullscreen;
	private static Game game;
	
	/**
	 * Constructor for the engine display.
	 * @param width of the display.
	 * @param height of the display.
	 * @param framerate of the display.
	 */
	public CoreDisplay(int width, int height, double framerate, Game game) {
		this.width = width;
		this.height = height;
		frameTime = 1.0/framerate;
		CoreDisplay.game = game;
	}
	
	/**
	 * Method that creates the window with menu for the program.
	 * @param title of the window.
	 * @param fullscreen If its windowed or full-screen.
	 */
	public void createWindow(String title, int aliasing, boolean fullscreen) {
		this.fullscreen = fullscreen;
		Window.createMenuWindow(width, height, aliasing, title, this.fullscreen);
	}
	
	/**
	 * Method that sets to run everything when the program ask it for.
	 */
	public void run() {
		while(!Window.isCloseRequested()) {
			Window.updateMenu();
			Window.renderMenu();
			//Update Window
		}
		Window.dispose();
	}
	/**
	 * Return the limit of refreshing time.
	 * @return FrameTime.
	 */
	public static double getFrameTime() {
		return frameTime;
	}

	/**
	 * Returns the main game.
	 * @return game
	 */
	public static Game getGame() {
		return game;
	}

}
