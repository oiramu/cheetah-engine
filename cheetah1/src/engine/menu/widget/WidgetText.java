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

import engine.menu.system.SEngineUtil;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class WidgetText extends WidgetModel {
	
	private String 	m_text;
	private boolean m_bold;
	
	/**
	 * Widget text constructor.
	 * @param text Text child.
	 * @param x position.
	 * @param y position.
	 */
	public WidgetText(String text, int x, int y) {
		m_componentType = "Text";
		this.m_text = text;
		m_position = new Vector2f(x, y);
	}
	
	/**
	 * Widget text constructor.
	 * @param text Text child.
	 * @param x position.
	 * @param y position.
	 * @param bold Checks if it is bold or not.
	 */
	public WidgetText(String text, int x, int y, boolean bold) {
		this(text, x, y);
		this.m_bold = bold;
	}

	/**
	 * Deletes...
	 */
	public void delete() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Updates...
	 */
	public void update() { }

	/**
	 * Draws the text and bold text.
	 */
	public void draw() {
		if(m_bold)
			SEngineUtil.getInstance().getBoldFont().drawString(m_position.x, m_position.y, m_text);
		else
			SEngineUtil.getInstance().getFont().drawString(m_position.x, m_position.y, m_text);
	}
}
