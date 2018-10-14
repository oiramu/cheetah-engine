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

import engine.core.Time;
import engine.core.Vector2f;
import engine.rendering.TextureFont;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class Debug {
	
	private static HashMap<String,TextureFont> debugText = new HashMap<String,TextureFont>();
	
	/**
	 * Defines the hash map of variables to test.
	 */
	public static void init() {
		debugText.put("FPS",new TextureFont("", new Vector2f(0.75f,1.75f), new Vector2f(0.5f,0.5f)));
		debugText.put("FrameTime",new TextureFont("", new Vector2f(0.75f,1.65f), new Vector2f(0.5f,0.5f)));
		debugText.put("Memory",new TextureFont("", new Vector2f(0.75f,1.55f), new Vector2f(0.5f,0.5f)));
		debugText.put("CPU",new TextureFont("", new Vector2f(0.75f,1.45f), new Vector2f(0.5f,0.5f)));
		debugText.put("LWJGL",new TextureFont("LWJGL:"+getVersion(), new Vector2f(0.75f,1.35f), new Vector2f(0.5f,0.5f)));
		debugText.put("OpenGL",new TextureFont("OpenGL:"+glGetString(GL_VERSION), new Vector2f(0.75f,1.25f), new Vector2f(0.5f,0.5f)));
	}
	
	/**
	 * Prints the debug stuff to the tester's screen.
	 */
	public static void print() {
		debugText.get("FPS").setText("FPS:"+(int)Time.getFPS());
		debugText.get("FrameTime").setText("FrameTime:"+(float)Time.getFrametime());
		int totalMemory = (int)Runtime.getRuntime().totalMemory()/1048576;
    	int freeMemory = (int)Runtime.getRuntime().freeMemory()/1048576;
    	int usingMemory = totalMemory - freeMemory;
    	int mem = (usingMemory*100/totalMemory);
    	debugText.get("Memory").setText("Mem:"+mem+"% "+usingMemory+"/"+totalMemory+"MB");
    	long end = System.nanoTime();
        long totalAvailCPUTime = Runtime.getRuntime().availableProcessors() * (end-System.nanoTime());
        long totalUsedCPUTime = ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime()-ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();;
        int cpu = (int) (((float)totalUsedCPUTime*1000)/(float)totalAvailCPUTime);
        debugText.get("CPU").setText("CPU:"+cpu+"% "+Runtime.getRuntime().availableProcessors()+" cores");
        debugText.get("LWJGL").render();
        debugText.get("OpenGL").render();
	}

}
