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

import java.util.ArrayList;
import java.util.List;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class GUIObject {
	
	private List<GUIComponent> children = new ArrayList<GUIComponent>();
	
	/**
	 * Adds a child GUIobject to a component.
	 * @param child GUIComponent.
	 * @param name of the child.
	 */
	public void addChild(GUIComponent child, String name) {
		child.setComponentName(name);
		children.add(child);
	}
	
	/**
	 * Adds a child GUIobject to a component.
	 * @param child GUIComponent.
	 * @param name of the child.
	 * @param type of the child.
	 */
	public void addChild(GUIComponent child, String name, String type) {
		child.setComponentName(name);
		child.setComponentType(type);
		children.add(child);
	}
	
	/**
	 * Delete the child by his index id.
	 * @param id index of child.
	 */
	public void deleteChild(int id) {
		children.remove(id).delete();
	}
	
	/**
	 * Delete the child by his name.
	 * @param name of the child.
	 * @return State if it deletes it.
	 */
	public boolean deleteChild(String name) {
		for(int i=0; i < children.size(); i++) {
			if(children.get(i).getComponentName().equals(name)) {
				children.remove(i).delete();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the children by his index id.
	 * @param id index of child.
	 * @return Children.
	 */
	public GUIComponent getChildren(int id) { 
		return children.get(id);
	}
	
	/**
	 * Returns the children by by his name.
	 * @param id index of child.
	 * @return Children.
	 */
	public GUIComponent getChildren(String name) {
		for(int i=0; i < children.size(); i++) {
			if(children.get(i).getComponentName().equals(name))
				return children.get(i);
		}
		return null;
	}
	
	/**
	 * Returns the number on the children node.
	 * @return children node's size.
	 */
	public int getChildNumber() {
		return children.size();
	}

	/**
	 * Returns the type of children that is in the node.
	 * @param type of child to find.
	 * @return the child found.
	 */
	public GUIObject getChildrenType(String type) {
		GUIObject newGameObject = new GUIObject();
		
		for(int i=0; i < children.size(); i++) {
			GUIComponent temporary = children.get(i);
			if(temporary.getComponentType() != null) {
				if(temporary.getComponentType().equals(type))
					newGameObject.addChild(temporary, temporary.getComponentName());
			}
		}
		
		return newGameObject;
	}
}
