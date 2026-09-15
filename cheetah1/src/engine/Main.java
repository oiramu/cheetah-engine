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
package engine;

import java.util.Arrays;

import engine.components.Constants;
import engine.core.CoreEngine;
import game.Auschwitz;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.4
 * @since 2017
 */
public class Main {
	
	/**
	 * The main method of the program, takes everything to show and put it
	 * To work like it should.
	 * @param args arguments. Pass {@code --skip-menu} to jump straight into
	 * the game for quicker dev iteration, bypassing the main menu.
	 */
	public static void main(String[] args) {
		boolean skipMenu = Arrays.asList(args).contains("--skip-menu");

		// Loaded here, before the window exists, so the settings menu's
		// resolution/fullscreen fields (which only take effect on next
		// launch - see Constants.TARGET_WIDTH/TARGET_HEIGHT/FULLSCREEN) can
		// actually reach Window.createDisplay(). Auschwitz.init() also
		// loads this later for the rest of the engine's config; reloading
		// here first is harmless and required for display mode specifically.
		Constants.load("res/config.txt");

		CoreEngine engine = new CoreEngine(Constants.TARGET_WIDTH, Constants.TARGET_HEIGHT, 120, new Auschwitz());
		engine.createWindow("Auschwitz", Constants.FULLSCREEN, skipMenu);
		engine.run();
	}

}
