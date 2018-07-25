/*
 * Copyright 2017 Cheetah Software.
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
package engine.menu;

import org.lwjgl.opengl.GL11;

import engine.rendering.Window;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class Rendering2DEngine {
	
	/**
	 * Add the openGL context to the menu.
	 */
	public static void context2D() {
		GL11.glPopAttrib(); //Empty Attribute List
		GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
    	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    	
		GL11.glViewport(0,0,Window.getWidth(), Window.getHeight());
		
		GL11.glMatrixMode(GL11.GL_PROJECTION); //Projection with camera, eyes
		GL11.glLoadIdentity(); //Replace the current matrix with the identity matrix (1 in diagonally)
		GL11.glOrtho(0, Window.getWidth(), Window.getHeight(),0,-1,1); //Orthogonal landmark for 2D.
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		GL11.glLoadIdentity(); //resets the matrix back to its default state.
	}
	
	/**
	 * Restores the color data to screen.
	 */
	public static void restore() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//GL11.glScaled(0.5, 0.5, 0.5);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
	}
}
