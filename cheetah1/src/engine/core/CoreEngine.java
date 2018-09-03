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

import engine.rendering.RenderingEngine;
import engine.rendering.Window;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.4
 * @since 2017
 */
public class CoreEngine {
	
	private int 					m_width;
	private int 					m_height;
	private double 					m_frameTime;
	private boolean 				m_fullscreen;
	private boolean 				m_isRunning;
	private String 					m_title;
	private Game 					m_game;
	public static RenderingEngine 	m_renderingEngine;
	private static CoreEngine 		m_engine;
	
	/**
	 * Constructor for the engine display.
	 * @param width of the display.
	 * @param height of the display.
	 * @param framerate of the display.
	 */
	public CoreEngine(int width, int height, double framerate, Game game) {
		this.m_width = width;
		this.m_height = height;
		this.m_frameTime = 1.0/framerate;
		this.m_game = game;
		m_engine = this;
	}
	
	/**
	 * Method that creates the window with menu for the program.
	 * @param title of the window.
	 * @param fullscreen If its windowed or full-screen.
	 */
	public void createWindow(String title, boolean fullscreen) {
		this.m_fullscreen = fullscreen;
		this.m_title = title;
		Window.createMenuWindow(m_width, m_height, this.m_title, this.m_fullscreen);
	}
	
	/**
     * Constructor for the core of the game to compile.
     */
    public CoreEngine init() {
        m_isRunning = false;
        return m_engine;
    }

    /**
     * Method that declares that the game should run.
     */
    public void start() {

        if (m_isRunning) {
            return;
        }
        
        runGame();
    }

    /**
     * Method that declares that the game should stop.
     */
    public void stop() {

        if (!m_isRunning) {
            return;
        }

        m_isRunning = false;
    }

    /**
     * Method that sets everything to run, for example the engine, the frames
     * per second, the frame-times, the time of compiling, the processing
     * states, etc.
     */
    private void runGame() {
    	
    	m_isRunning = true;

		int frames = 0;
        long frameCounter = 0;

        m_renderingEngine = new RenderingEngine();
        
        m_game.init();

        double lastTime = Time.getTime();
        double unprocessedTime = 0;

        while (m_isRunning) {

            boolean render = false;

            double startTime = Time.getTime();
            double passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime;
            frameCounter += passedTime;

            while (unprocessedTime > m_frameTime) {

                render = true;

                unprocessedTime -= m_frameTime;

                if (Window.isCloseRequested()) {
                    stop();
                }

                Time.setDelta(m_frameTime);

                m_game.input();
                Input.update();
                
                m_game.update(m_renderingEngine);

                if (frameCounter >= 1.0) {
                    System.out.println(frames);
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
                    e.printStackTrace();
                }
            }
        }

        cleanUp();
    }

    /**
     * Method that renders everything to render.
     */
    private void render() {
        m_game.render(m_renderingEngine);       
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
	 * Returns the main game.
	 * @return Game
	 */
	public Game getGame() {return m_game;}
	
	/**
	 * Returns the main engine instance.
	 * @return Engine instance
	 */
	public static CoreEngine getEngine() {return m_engine;}
	
	/**
     * Method that cleans everything in the program's window.
     */
    private void cleanUp() {Window.dispose();}

}
