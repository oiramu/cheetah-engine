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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashMap;

import engine.core.crash.CrashReport;
import engine.core.utils.Util;
import engine.rendering.RenderingEngine;
import engine.rendering.HUD;
import game.Player;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.2
 * @since 2018
 */
public class Debug {
	
	private static final float 					X_MARGIN = 0.5f;
	private static final int 					MB = 1048576;
	
	private static double 						fps;
	private static double						frametime;
	
	private static int 							worstFPS = -1;
	private static int 							averageFPS;
	private static int 							bestFPS;
	
	private static int 							totalMemory = 0;
	private static int 							freeMemory = 0;
	private static int 							cpu = 0;
	
	public static boolean 						state;
	public static boolean 						godMode;
	
	private static Player						player;
	private static File	 						folder;
	
	private static HashMap<String,HUD> 	debugText = new HashMap<String,HUD>();
	
	/**
	 * Defines the hash map of variables to test.
	 */
	public static void init(Player player) {
		Debug.player = player;
		debugText.put("Engine", new HUD("Cheetah Engine 1.0", new Vector2f(X_MARGIN,1.9f), new Vector2f(0.5f,0.5f)));
		debugText.put("FPS",new HUD("", new Vector2f(X_MARGIN,1.7f), new Vector2f(0.5f,0.5f)));
		debugText.put("FrameTime",new HUD("", new Vector2f(X_MARGIN,1.6f), new Vector2f(0.5f,0.5f)));
		debugText.put("Memory",new HUD("", new Vector2f(X_MARGIN,1.5f), new Vector2f(0.5f,0.5f)));
		debugText.put("CPU",new HUD("", new Vector2f(X_MARGIN,1.4f), new Vector2f(0.5f,0.5f)));
		debugText.put("FPSMeasure",new HUD("", new Vector2f(X_MARGIN,1.3f), new Vector2f(0.5f,0.5f)));
		debugText.put("Position",new HUD("Position:", new Vector2f(X_MARGIN,1.2f), new Vector2f(0.5f,0.5f)));
		debugText.put("X",new HUD("", new Vector2f(X_MARGIN + 0.25f,1.1f), new Vector2f(0.5f,0.5f)));
		debugText.put("Y",new HUD("", new Vector2f(X_MARGIN + 0.25f,1.0f), new Vector2f(0.5f,0.5f)));
		debugText.put("Z",new HUD("", new Vector2f(X_MARGIN + 0.25f,0.9f), new Vector2f(0.5f,0.5f)));
		debugText.put("OS",new HUD("OS:"+System.getProperty("os.name"), new Vector2f(X_MARGIN,0.8f), new Vector2f(0.5f,0.5f)));
		debugText.put("LWJGL",new HUD("LWJGL:"+getVersion(), new Vector2f(X_MARGIN,0.7f), new Vector2f(0.5f,0.5f)));
		debugText.put("OpenGL",new HUD("OpenGL:"+glGetString(GL_VERSION), new Vector2f(X_MARGIN,0.6f), new Vector2f(0.5f,0.5f)));
		debugText.put("Damage",new HUD("", new Vector2f(X_MARGIN,0.5f), new Vector2f(0.5f,0.5f)));
		debugText.put("Speed",new HUD("", new Vector2f(X_MARGIN,0.4f), new Vector2f(0.5f,0.5f)));
	}
	
	/**
	 * Prints the debug stuff to the tester's screen.
	 * @param renderingEngine to use
	 */
	public static void printToEngine(RenderingEngine renderingEngine) {
		if(state) {
			debugText.get("Engine").render(renderingEngine);
			debugText.get("FPS").setText("FPS:"+(int)fps);
			debugText.get("FPS").render(renderingEngine);
			debugText.get("FrameTime").setText("FrameTime:"+(float)frametime+"ms");
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
	        if(fps >= bestFPS) bestFPS = (int) fps;
	        averageFPS = (bestFPS+worstFPS)/2;
	        if(worstFPS == -1) worstFPS = averageFPS; else
	        if(fps <= worstFPS) worstFPS = (int) fps;
	        debugText.get("FPSMeasure").setText("wFPS:"+worstFPS+" aFPS:"+averageFPS+" bFPS:"+bestFPS);
	        debugText.get("FPSMeasure").render(renderingEngine);
	        debugText.get("Position").render(renderingEngine);
	        debugText.get("X").setText("X:"+player.getTransform().getPosition().getX());
	        debugText.get("X").render(renderingEngine);
	        debugText.get("Y").setText("Y:"+player.getTransform().getPosition().getY());
	        debugText.get("Y").render(renderingEngine);
	        debugText.get("Z").setText("Y:"+player.getTransform().getPosition().getZ());
	        debugText.get("Z").render(renderingEngine);
	        debugText.get("OS").render(renderingEngine);
	        debugText.get("LWJGL").render(renderingEngine);
	        debugText.get("OpenGL").render(renderingEngine);
	        if (Debug.godMode && player != null) {
	        	debugText.get("Damage").setText("Damage: " + player.getDamage());
				debugText.get("Speed").setText("Speed: " + player.getSpeed());
		        debugText.get("Damage").render(renderingEngine);
		        debugText.get("Speed").render(renderingEngine);
	        }
		}
	}
	
	/**
	 * Enables or disables the god mode to some player.
	 * @param godMode to set
	 */
	public static void enableGod(boolean godMode) {
		if (Debug.godMode) {
			player.setMaxHealth(100000);
			player.setMaxBullets(100000);
			player.setMaxShells(100000);
			player.setMaxRockets(100000);
			player.setMaxGas(100000);
			player.addBullets(100000);
			player.addShells(100000);
			player.addRockets(100000);
			player.addGas(100000);
			player.addHealth(1000000, "");
			player.setArmor(godMode);
			player.setMaxArmor(100000);
			player.addArmor(100000);
			player.setBronzekey(godMode);
			player.setGoldkey(godMode);
			player.setShotgun(godMode);
			player.setMachinegun(godMode);
			player.setSuperShotgun(godMode);
			player.setChaingun(godMode);
			player.setRocketLauncher(godMode);
			player.setFlameThrower(godMode);
		}
	}
	
	/**
	 * Print some message to the console or terminal.
	 * @param message to show
	 */
	public static <E> void print(E message) { System.out.print(message);}
	
	/**
	 * Print some message to the console or terminal
	 * by lines.
	 * @param message to show
	 */
	public static <E> void println(E message) { System.out.println(message);}
	
	/**
	 * Prints an error message to a window, keep in
	 * mind to put this along a try and catch
	 * @param message to show
	 * @param errorId a title to the window
	 */
	public static void crash(CrashReport report) {
		report.printStack();
		CoreEngine.getCurrent().kill();
		System.exit(-1);
	}
	
	/**
	 * Gets the project's folder.
	 * @return project's folder
	 */
	public static File getEngineFolder() {
		if(folder == null) {
			String appdata = System.getenv("APPDATA");
			if(appdata != null)
				folder = new File(appdata, ".cheetah1");
			else
				folder = new File(System.getProperty("user.home"), ".cheetah1");

			if(!folder.exists()) folder.mkdirs();
		}
		return folder;
	}

	/**
	 * Sets the frames per second reference.
	 * @param fps to reference
	 */
	public static void setFps(double fps) { Debug.fps = fps; }

	/**
	 * Sets the frames time reference.
	 * @param frametime to reference
	 */
	public static void setFrametime(double frametime) { Debug.frametime = frametime; }

}
