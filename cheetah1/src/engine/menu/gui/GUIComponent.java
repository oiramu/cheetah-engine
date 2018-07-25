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

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public abstract class GUIComponent {
	
	protected String componentName;
	protected String componentType;
	
	//Not implemented method.
	public abstract void delete();
	public abstract void update();
	
	//Empty methods, different for each child class
	public void draw3D() { };
	public void draw2D() { };
	
	/**
	 * Returns the component name.
	 * @return Component name.
	 */
	public String getComponentName() {return componentName;}
	
	/**
	 * Sets the component name.
	 * @param name Component name.
	 */
	public void setComponentName(String name) {componentName = name;}
	
	/**
	 * Returns the component type.
	 * @return Component type.
	 */
	public String getComponentType() {return componentType;}
	
	/**
	 * Sets the component type.
	 * @param name Component type.
	 */
	public void setComponentType(String name) {componentType = name;}
	
}
