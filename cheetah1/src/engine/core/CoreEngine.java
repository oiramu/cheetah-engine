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

import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.openal.AL10.*;

import engine.audio.AudioUtil;
import engine.core.utils.Log;
import engine.rendering.RenderingEngine;
import engine.rendering.Window;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.5
 * @since 2017
 */
public class CoreEngine {
	
	private int 					width;
	private int 					height;
	private double 					frameTime;
	private boolean 				fullscreen;
	private boolean 				isRunning;
	private String 					title;
	private Game 					game;
	private static RenderingEngine 	renderingEngine;
	private static CoreEngine 		coreEngine;
	
	/**
	 * Constructor for the engine display.
	 * @param width of the display.
	 * @param height of the display.
	 * @param framerate of the display.
	 */
	public CoreEngine(int width, int height, double framerate, Game game) {
		this.width = width;
		this.height = height;
		this.frameTime = 1.0/framerate;
		this.game = game;
		coreEngine = this;
	}
	
	/**
	 * Method that creates the window with menu for the program.
	 * @param title of the window.
	 * @param fullscreen If its windowed or full-screen.
	 */
	public void createWindow(String title, boolean fullscreen) {
		this.fullscreen = fullscreen;
		this.title = title;
		Window.createMenuWindow(width, height, this.title, this.fullscreen);
		printCompilationStuff();
	}
	
	/**
     * Constructor for the core of the game to compile.
     */
    public CoreEngine init() {
        isRunning = false;
        return coreEngine;
    }

    /**
     * Method that declares that the game should run.
     */
    public void start() {

        if (isRunning) {
            return;
        }
        
        runGame();
    }

    /**
     * Method that declares that the game should stop.
     */
    public void stop() {

        if (!isRunning) {
            return;
        }

        isRunning = false;
    }

    /**
     * Method that sets everything to run, for example the engine, the frames
     * per second, the frame-times, the time of compiling, the processing
     * states, etc.
     */
    private void runGame() {
    	
    	isRunning = true;

		int frames = 0;
        double frameCounter = 0;

        renderingEngine = new RenderingEngine();
        
        game.init();
        
        double lastTime = Time.getTime();
        double unprocessedTime = 0;

        while (isRunning) {

            boolean render = false;

            double startTime = Time.getTime();
            double passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime;
            frameCounter += passedTime;

            while (unprocessedTime > frameTime) {

                render = true;

                unprocessedTime -= frameTime;

                if (Window.isCloseRequested())
                    stop();

                game.input();
                Input.update();
                
                game.update(frameTime);

                if (frameCounter >= 1.0) {
                	Debug.setFps(frames);
                	Debug.setFrametime(1000.0f/frames);
                    frames = 0;
                    frameCounter = 0;
                }
            }
            
            if (render) {
                render();
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                	Log.fatal("Core Error!");
                }
            }
        }
        cleanUp();
    }

    /**
     * Method that renders everything to render.
     */
    private void render() {
        game.render(renderingEngine);   
        Window.render();
    }
	
	/**
	 * Method that sets to run everything when the program ask it for.
	 */
	public void run() {
		while(!Window.isCloseRequested()) {
			Window.updateMenu();
			Window.renderMenu();
		}
		cleanUp();
	}
	
	/**
	 * Prints the compilation configuration.
	 */
	private void printCompilationStuff() {
		System.out.println("==============================");
        System.out.println("||CHEETAH ENGINE; BUILD v1.0||");
        System.out.println("==============================");
        System.out.println("Compiliation specs: ");
        System.out.println("-OS name: " + System.getProperty("os.name"));
        System.out.println("-OS version: " + System.getProperty("os.version"));
        System.out.println("-LWJGL version: " + org.lwjgl.Sys.getVersion());
        System.out.println("-OpenGL version: " + glGetString(GL_VERSION));
        System.out.println("-OpenGL vendor: " + glGetString(GL_VENDOR));
        //System.out.println("-OpenAL version: " + alGetString(AL_VERSION));
        //System.out.println("-OpenAL vendor: " + alGetString(AL_VENDOR));
        System.out.println("Compiled by: " + System.getProperty("user.name") + "; in : " + Time.getTimeAsString());
	}
	
	/**
     * Method that cleans everything in the program's window.
     */
    public void cleanUp() {Window.dispose(); AudioUtil.stopMidi();}

	/**
	 * Returns the main game.
	 * @return Game
	 */
	public Game getGame() {return game;}
	
	/**
	 * Returns the main engine instance.
	 * @return Engine instance
	 */
	public static CoreEngine getCurrent() {return coreEngine;}
	
	/**
	 * Returns the rendering engine instance.
	 * @return Rendering engine instance
	 */
	public static RenderingEngine getRenderingEngine() {return renderingEngine;}

}
