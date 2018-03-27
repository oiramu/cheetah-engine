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

import java.nio.ByteBuffer;

import javax.sound.midi.Sequence;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import engine.ResourceLoader;
import engine.audio.AudioUtil;
import engine.menu.DefaultMenu;
import engine.menu.Menu;
import engine.menu.Rendering2DEngine;
import engine.menu.system.SGameTime;

/**
*
* @author Carlos Rodriguez
* @version 1.1
* @since 2017
*/
public class CoreDisplay {
	
	private int width;
	private int height;
	private static double frameTime;
	private boolean isRunning;
	private Menu menu;
	private Rendering2DEngine twoDimensionalEngine;
	private static CoreDisplay enginePointer;
	
	private Sequence song = ResourceLoader.loadMidi("THEME0");

	/**
	 * Constructor for the engine display.
	 * @param width of the display.
	 * @param height of the display.
	 * @param FRAME_RATE of the display.
	 */
	public CoreDisplay(int width, int height,final int FRAME_RATE) {
		this.width = width;
		this.height = height;
		frameTime = 1.0/FRAME_RATE;
		enginePointer = this;
	}
	
	/**
	 * Method that creates the window for the program.
	 */
	public void createWindow(String title) {
		
		Display.setTitle(title);
		Display.setIcon(new ByteBuffer[] {
				ResourceLoader.loadIcon("icon32"),
		});
		try {
			
			/**
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setFullscreen(false);
			*/
			
			@SuppressWarnings("unused")
			DisplayMode displayMode = null;
	        DisplayMode[] modes = Display.getAvailableDisplayModes();

	         for (int i = 0; i < modes.length; i++)
	         {
	             if (modes[i].getWidth() == width
	             && modes[i].getHeight() == height
	             && modes[i].isFullscreenCapable())
	               {
	                    displayMode = modes[i];
	               }
	         }
			Display.setFullscreen(true);
			
			Display.create();
			
			//Hide mouse
			Cursor emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
			Mouse.setNativeCursor(emptyCursor);
			AudioUtil.playMidi(song);
			twoDimensionalEngine = new Rendering2DEngine(getWidth(), getHeight());
			menu = new DefaultMenu();
			isRunning = true;
			
			//SEngineUtil.getInstance().setInputType(InputType.MOUSE); //Default input type
		} catch(LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/**
	 * Updates everything related for the window like the inputs or objects
	 * and time.
	 */
	public void update() {	
		SGameTime.getInstance().update();
		
		//Input type
		Keyboard.next();
		/**
		if(Keyboard.getEventKeyState() && SEngineUtil.getInstance().getInputType() != InputType.KEYBOARD) //Keyboard
			SEngineUtil.getInstance().setInputType(InputType.KEYBOARD);
		else if(Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) { //Mouse
			if(SEngineUtil.getInstance().getInputType() != InputType.MOUSE)
				SEngineUtil.getInstance().setInputType(InputType.MOUSE);
		}
		*/
		//Update objects
		menu.update();
	}
	
	/**
	 * Renders all the "2D" related stuff like the menus.
	 */
	public void render() {
		twoDimensionalEngine.restore();
		
		//2D
		twoDimensionalEngine.context2D();
		menu.draw2D();
	}
	
	/**
	 * Method that sets to run everything when the program ask it for.
	 */
	public void run() {
		while(isRunning) {
			this.update();
			this.render();

			Display.update(); //Update Window
		}
	}
	
	/**
	 * Method that stops everything that the program is doing.
	 */
	public void stop() {
		isRunning = false;
	}
	
	/**
	 * Gets the menu that is showing.
	 * @return
	 */
	public Menu getMenu() {
		return menu;
	}
	
	/**
	 * Return the limit of refreshing time.
	 * @return FrameTime.
	 */
	public static double getFrameTime() {
		return frameTime;
	}
	
	/**
     * Removes everything when the program closes
     */
    public static void dispose() {
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
    }

    /**
     * Gets if the window closes.
     * @return window's state.
     */
    public static boolean isCloseRequested() {
        return Display.isCloseRequested();
    }

    /**
     * Returns the window width.
     * @return Width.
     */
    public static int getWidth() {
        return Display.getDisplayMode().getWidth();
    }

    /**
     * Returns the window height.
     * @return Height.
     */
    public static int getHeight() {
        return Display.getDisplayMode().getHeight();
    }

    /**
     * Returns the window title.
     * @return Title.
     */
    public static String getTitle() {
        return Display.getTitle();
    }

	/**
	 * Returns the engine pointer to display.
	 * @return Engine pointer.
	 */
	public static CoreDisplay getEnginePointer() {
		return enginePointer;
	}

}
