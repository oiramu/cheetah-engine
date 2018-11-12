/*
 * Copyright 2018 Carlos Rodriguez.
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
package engine.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

import javax.sound.midi.Sequence;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.opengl.PNGDecoder;

import engine.audio.AudioUtil;
import engine.core.CoreEngine;
import engine.menu.DefaultMenu;
import engine.menu.Menu;
import engine.menu.Rendering2DEngine;
import engine.menu.system.SGameTime;
import game.Auschwitz;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class Window {
	
	private static Menu 			m_menu;
	private final static Sequence 	THEME = AudioUtil.loadMidi("THEME0");
	
	/**
	 * Method that creates the window.
	 * @param title of the window.
	 * @param fullscreen If its windowed or full-screen.
	 */
	public static void createWindow(int width, int height, String title, boolean fullscreen) {
		
		Display.setTitle(title);
		Display.setIcon(new ByteBuffer[] {
				loadIcon("/textures/coreDisplay/icon32"),
		});
		try {
			
			if(fullscreen) {
				@SuppressWarnings("unused")
				DisplayMode displayMode = null;
		        DisplayMode[] modes = Display.getAvailableDisplayModes();
	
		         for (int i = 0; i < modes.length; i++) {
		             if (modes[i].getWidth() == width
		             && modes[i].getHeight() == height
		             && modes[i].isFullscreenCapable()) { displayMode = modes[i]; }
		         }
			} else {
				Display.setDisplayMode(new DisplayMode(width, height));
			}
			
			Display.setFullscreen(fullscreen);
	        Display.create();
	        Keyboard.create();
	        Mouse.create();
	        
	        Auschwitz.setStartingLevel(1);
			CoreEngine.getEngine().start();

		} catch(LWJGLException e) {
			e.printStackTrace();
			dispose();
		}
	}

	/**
	 * Method that creates the window with menu for the program.
	 * @param title of the window.
	 * @param fullscreen If its windowed or full-screen.
	 */
	public static void createMenuWindow(int width, int height, String title, boolean fullscreen) {
		
		Display.setTitle(title);
		Display.setIcon(new ByteBuffer[] {
				loadIcon("/textures/coreDisplay/icon32"),
		});
		try {
			
			if(fullscreen) {
				@SuppressWarnings("unused")
				DisplayMode displayMode = null;
		        DisplayMode[] modes = Display.getAvailableDisplayModes();
	
		         for (int i = 0; i < modes.length; i++) {
		             if (modes[i].getWidth() == width
		             && modes[i].getHeight() == height
		             && modes[i].isFullscreenCapable()) { displayMode = modes[i]; }
		         }
			} else {
				Display.setDisplayMode(new DisplayMode(width, height));
			}
			
			Display.setFullscreen(fullscreen);
	        Display.create();
	        Keyboard.create();
            Mouse.create();
			
			//Hide mouse
			Cursor emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
			Mouse.setNativeCursor(emptyCursor);
			AudioUtil.playMidi(THEME);
			m_menu = new DefaultMenu();

			//SEngineUtil.getInstance().setInputType(InputType.MOUSE); //Default input type
		} catch(LWJGLException e) {
			e.printStackTrace();
			dispose();
		}
	}
	
	/**
	 * Loads a PNG image file named like that and uses it as the window's icon.
	 * @param fileName Name of the icon file.
	 * @return Icon.
	 */
    public static ByteBuffer loadIcon(String fileName) {
        FileInputStream in;
        ByteBuffer buffer = null;
        try {
        	in = new FileInputStream(new File(("./res/" + fileName + ".png")));
            PNGDecoder decoder = new PNGDecoder(in);
            buffer = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
            decoder.decode(buffer, decoder.getWidth()*4, PNGDecoder.RGBA);
            buffer.flip();
        }catch (Exception e) {
        	System.err.println("Error loading " + fileName);
            e.printStackTrace();
        }
        return buffer;
    }
	
    /**
     * Renders the display.
     */
	public static void render() {Display.update();}
	
	/**
	 * Updates everything related for the window like the inputs or objects
	 * and time.
	 */
	public static void updateMenu() {	
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
		render();
		m_menu.update();
	}
	
	/**
	 * Renders all the "2D" related stuff like the menus.
	 */
	public static void renderMenu() {
		Rendering2DEngine.restore();	
		//2D
		Rendering2DEngine.context2D();
		m_menu.draw2D();
	}
	
	/**
	 * Gets the menu that is showing.
	 * @return
	 */
	public static Menu getMenu() {return m_menu;}
	
	/**
     * Removes everything when the program closes
     */
    public static void dispose() {
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
        System.exit(0);
    }

    /**
     * Binds as a render target all of the screen.
     */
    public static void bindAsRenderTarget() {
    	glBindFramebuffer(GL_DRAW_BUFFER, 0);
    	glViewport(0, 0, getWidth(), getHeight());
    }

    /**
     * Gets if the window closes.
     * @return window's state.
     */
    public static boolean isCloseRequested() {return Display.isCloseRequested();}

    /**
     * Returns the window width.
     * @return Width.
     */
    public static int getWidth() {return Display.getDisplayMode().getWidth();}

    /**
     * Returns the window height.
     * @return Height.
     */
    public static int getHeight() {return Display.getDisplayMode().getHeight();}

    /**
     * Returns the window title.
     * @return Title.
     */
    public static String getTitle() {return Display.getTitle();}

}