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
	private int maxPos, speed;
	private float time;
	private String axis;
	private Graphics device;
	private CoolDown coolDown;
	
	private Vector2f transPos;
	
	/**
	 * Constructor for the animated widget.
	 * @param device To draw.
	 * @param speed of animation.
	 * @param axis to animate.
	 * @param max Where to end.
	 * @param time of animation.
	 */
	public WidgetAnimation(Graphics device, int speed, String axis, int max, float time) {
		this.device = device;
		this.speed = speed;
		this.axis = axis;
		this.maxPos = max;
		this.time = time;
		
		this.coolDown = new CoolDown();
		this.transPos = new Vector2f();
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
		if(coolDown.coolDownTime(time)) { //CoolDown
			if(axis.equals("x")) {
				if(transPos.x < maxPos)
					transPos.x += speed;
			}
			else if(axis.equals("y")) {
				if(transPos.y < maxPos || transPos.y > maxPos)
					transPos.y += speed;
			}
		}
	}

	@Override
	public void draw() {
		device.translate(transPos.x, transPos.y);
	}
}
