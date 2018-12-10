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
package engine.core;

import static org.lwjgl.Sys.*;
import static org.lwjgl.opengl.GL11.*;

import java.lang.management.ManagementFactory;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import engine.rendering.RenderingEngine;
import engine.rendering.TextureFont;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2018
 */
public class Debug {
	
	private static final float X_MARGIN = 0.5f;
	private static final int MB = 1048576;
	
	private static int 		worstFPS = -1;
	private static int 		averageFPS;
	private static int 		bestFPS;
	
	private static int 		totalMemory = 0;
	private static int 		freeMemory = 0;
	private static int 		cpu = 0;
	
	public static boolean 	state;
	
	private static HashMap<String,TextureFont> debugText = new HashMap<String,TextureFont>();
	
	/**
	 * Defines the hash map of variables to test.
	 */
	public static void init() {
		debugText.put("Engine", new TextureFont("Cheetah Engine 1.0", new Vector2f(X_MARGIN,1.9f), new Vector2f(0.5f,0.5f)));
		debugText.put("FPS",new TextureFont("", new Vector2f(X_MARGIN,1.7f), new Vector2f(0.5f,0.5f)));
		debugText.put("FrameTime",new TextureFont("", new Vector2f(X_MARGIN,1.6f), new Vector2f(0.5f,0.5f)));
		debugText.put("Memory",new TextureFont("", new Vector2f(X_MARGIN,1.5f), new Vector2f(0.5f,0.5f)));
		debugText.put("CPU",new TextureFont("", new Vector2f(X_MARGIN,1.4f), new Vector2f(0.5f,0.5f)));
		debugText.put("FPSMeasure",new TextureFont("", new Vector2f(X_MARGIN,1.3f), new Vector2f(0.5f,0.5f)));
		debugText.put("OS",new TextureFont("OS:"+System.getProperty("os.name"), new Vector2f(X_MARGIN,1.1f), new Vector2f(0.5f,0.5f)));
		debugText.put("LWJGL",new TextureFont("LWJGL:"+getVersion(), new Vector2f(X_MARGIN,1.0f), new Vector2f(0.5f,0.5f)));
		debugText.put("OpenGL",new TextureFont("OpenGL:"+glGetString(GL_VERSION), new Vector2f(X_MARGIN,0.9f), new Vector2f(0.5f,0.5f)));
	}
	
	/**
	 * Prints the debug stuff to the tester's screen.
	 * @param renderingEngine to use
	 */
	public static void printToEngine(RenderingEngine renderingEngine) {
		if(state) {
			debugText.get("Engine").render(renderingEngine);
			debugText.get("FPS").setText("FPS:"+(int)Time.getFPS());
			debugText.get("FPS").render(renderingEngine);
			debugText.get("FrameTime").setText("FrameTime:"+(float)Time.getFrametime()+"ms");
			debugText.get("FrameTime").render(renderingEngine);
			totalMemory = (int)Runtime.getRuntime().totalMemory()/MB;
	    	freeMemory = (int)Runtime.getRuntime().freeMemory()/MB;
	    	int usingMemory = totalMemory - freeMemory;
	    	int mem = (usingMemory*100/totalMemory);
	    	debugText.get("Memory").setText("Mem:"+mem+"% "+usingMemory+"/"+totalMemory+"MB");
	    	debugText.get("Memory").render(renderingEngine);
	    	long end = System.nanoTime();
	    	int cpus = Runtime.getRuntime().availableProcessors();
	        long totalAvailCPUTime = cpus * (end-System.nanoTime());
	        long totalUsedCPUTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime()-ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
	        cpu = (int) (((float)totalUsedCPUTime*10)/(float)totalAvailCPUTime);
	        debugText.get("CPU").setText("CPU:"+Util.clamp(100, cpu)+"% "+cpus+" cores");
	        debugText.get("CPU").render(renderingEngine);
	        if(Time.getFPS() >= bestFPS) bestFPS = (int) Time.getFPS();
	        averageFPS = (bestFPS+worstFPS)/2;
	        if(worstFPS == -1) worstFPS = averageFPS; else
	        if(Time.getFPS() <= worstFPS) worstFPS = (int) Time.getFPS();
	        debugText.get("FPSMeasure").setText("wFPS:"+worstFPS+" aFPS:"+averageFPS+" bFPS:"+bestFPS);
	        debugText.get("FPSMeasure").render(renderingEngine);
	        debugText.get("OS").render(renderingEngine);
	        debugText.get("LWJGL").render(renderingEngine);
	        debugText.get("OpenGL").render(renderingEngine);
		}
	}
	
	/**
	 * Prints an error message to a window, keep in
	 * mind to put this along a try and catch
	 * @param message to show
	 * @param errorId a title to the window
	 */
	public static void printErrorMessage(String message, String errorId) {
		JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);    
		JDialog dialog = optionPane.createDialog(errorId);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		System.exit(-1);
	}

}
