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
package engine.menu.widget;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import engine.core.Debug;
import engine.core.crash.CrashReport;
import engine.rendering.Window;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class WidgetImage extends WidgetModel {
	
	private Image 		m_texture;
	private Vector2f 	m_size;
	
	/**
	 * Constructor of a widget image.
	 * @param imgPath Image's path.
	 * @param x position.
	 * @param y position.
	 * @param w Width.
	 * @param h Height.
	 */
	public WidgetImage(String imgPath, float x, float y, float w, float h) {
		m_componentType = "Image";
		try {
			m_texture = new Image(imgPath);
		} catch (SlickException e) {
			Debug.crash(new CrashReport(e));
		}
		
		m_position = new Vector2f(x * Window.getWidth(), y * Window.getHeight());
		m_size = new Vector2f(w * Window.getWidth(), h * Window.getHeight());
	}
	
	/**
	 * Deletes the texture when need it.
	 */
	public void delete() { 
		try {
			m_texture.destroy();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Updates...
	 */
	public void update() { }

	/**
	 * Draws it to screen.
	 */
	public void draw() {
		m_texture.draw(m_position.x, m_position.y, m_size.x, m_size.y);
	}

}
