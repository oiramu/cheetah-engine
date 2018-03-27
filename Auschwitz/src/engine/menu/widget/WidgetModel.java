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

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public abstract class WidgetModel {

	protected String componentName;
	protected String componentType;
	protected Vector2f position;
	
	/*Deletes...*/
	public abstract void delete();
	/*Updates...*/
	public abstract void update();
	/*Draws...*/
	public abstract void draw();
	
	/**
	 * Returns the position in a vector.
	 * @return Position.
	 */
	public Vector2f getPosition() {
		return position;
	}
	
	/**
	 * Sets the position of a vector in the main vector.
	 * @param position Position coordinates.
	 */
	public void setPosition(Vector2f position) {
		this.position = position;
	}
	
	/**
	 * Returns the component's type.
	 * @return Component's type.
	 */
	public String getComponentType() {
		return componentType;
	}
	
	/**
	 * Returns the component's name.
	 * @return Component's name.
	 */
	public String getComponentName() {
		return componentName;
	}
	
	/**
	 * Sets the component's name in a string.
	 * @param name Component's name.
	 */
	public void setComponentName(String name) {
		componentName = name;
	}
}
