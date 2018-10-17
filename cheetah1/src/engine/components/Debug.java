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
package engine.components;

import static org.lwjgl.Sys.*;
import static org.lwjgl.opengl.GL11.*;

import java.lang.management.ManagementFactory;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import engine.core.Time;
import engine.core.Vector2f;
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
	
	private static int m_worstFPS = -1;
	private static int m_averageFPS;
	private static int m_bestFPS;
	
	public static boolean m_state;
	
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
	 */
	public static void printToEngine() {
		if(m_state) {
			debugText.get("Engine").render();
			debugText.get("FPS").setText("FPS:"+(int)Time.getFPS());
			debugText.get("FrameTime").setText("FrameTime:"+(float)Time.getFrametime()+"ms");
			int totalMemory = (int)Runtime.getRuntime().totalMemory()/MB;
	    	int freeMemory = (int)Runtime.getRuntime().freeMemory()/MB;
	    	int usingMemory = totalMemory - freeMemory;
	    	int mem = (usingMemory*100/totalMemory);
	    	debugText.get("Memory").setText("Mem:"+mem+"% "+usingMemory+"/"+totalMemory+"MB");
	    	long end = System.nanoTime();
	    	int cpus = Runtime.getRuntime().availableProcessors();
	        long totalAvailCPUTime = cpus * (end-System.nanoTime());
	        long totalUsedCPUTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime()-ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();;
	        int cpu = (int) (((float)totalUsedCPUTime*10)/(float)totalAvailCPUTime);
	        debugText.get("CPU").setText("CPU:"+Math.max(0,Math.min(100, cpu))+"% "+cpus+" cores");
	        if(Time.getFPS() >= m_bestFPS) m_bestFPS = (int) Time.getFPS();
	        m_averageFPS = (m_bestFPS+m_worstFPS)/2;
	        if(m_worstFPS == -1) m_worstFPS = m_averageFPS; else
	        if(Time.getFPS() <= m_worstFPS) m_worstFPS = (int) Time.getFPS();
	        debugText.get("FPSMeasure").setText("wFPS:"+m_worstFPS+" aFPS:"+m_averageFPS+" bFPS:"+m_bestFPS);
	        debugText.get("OS").render();
	        debugText.get("LWJGL").render();
	        debugText.get("OpenGL").render();
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
