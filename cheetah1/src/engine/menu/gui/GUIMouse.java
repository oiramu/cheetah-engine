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
package engine.menu.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import engine.rendering.Window;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class GUIMouse {
	
	private Image mouseTexture;
	private Vector2f mousePos;
	
	/**
	 * Constructor for the mouse cursor.
	 */
	public GUIMouse() {
		Mouse.setCursorPosition(Window.getWidth()/2, Window.getHeight()/2);
		mousePos = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
		try {
			mouseTexture = new Image("res/textures/coreDisplay/mouse.png");
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes the mouse cursor when need it.
	 */
	public void delete() {
		try {
			mouseTexture.destroy();
		} catch (SlickException e) {
				e.printStackTrace();
		}
	}

	/**
	 * Updates the mouse cursor rendering every single frame.
	 */
	public void update() {
		mousePos.x = Mouse.getX();
		mousePos.y = -Mouse.getY()+Window.getHeight(); //Change landmark position, bottom-left -> top-left
	}
	
	/**
	 * Draws the texture respectably.
	 */
	public void draw2D() {mouseTexture.draw(mousePos.x, mousePos.y);}

}
