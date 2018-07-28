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
import org.newdawn.slick.Graphics;

import engine.menu.CoolDown;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class WidgetAnimation extends WidgetModel {
	
	/*
	 * List of type:
	 * 		- "0" = translate
	 */
	private int 		m_maxPos; 
	private int 		m_speed;
	private float 		m_time;
	private String 		m_axis;
	private Graphics 	m_device;
	private CoolDown 	m_coolDown;
	
	private Vector2f 	m_transPos;
	
	/**
	 * Constructor for the animated widget.
	 * @param device To draw.
	 * @param speed of animation.
	 * @param axis to animate.
	 * @param max Where to end.
	 * @param time of animation.
	 */
	public WidgetAnimation(Graphics device, int speed, String axis, int max, float time) {
		this.m_device = device;
		this.m_speed = speed;
		this.m_axis = axis;
		this.m_maxPos = max;
		this.m_time = time;
		
		this.m_coolDown = new CoolDown();
		this.m_transPos = new Vector2f();
	}

	/**
	 * Deletes...
	 */
	public void delete() {
		// TODO Auto-generated method stub
	}

	/**
	 * Updates the animation rendering every single frame.
	 */
	public void update() {
		if(m_coolDown.coolDownTime(m_time)) { //CoolDown
			if(m_axis.equals("x")) {
				if(m_transPos.x < m_maxPos)
					m_transPos.x += m_speed;
			}
			else if(m_axis.equals("y")) {
				if(m_transPos.y < m_maxPos || m_transPos.y > m_maxPos)
					m_transPos.y += m_speed;
			}
		}
	}

	@Override
	public void draw() {m_device.translate(m_transPos.x, m_transPos.y);}
	
}
