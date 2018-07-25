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
package engine.menu;

import java.util.ArrayList;
import java.util.List;

import engine.menu.widget.WidgetModel;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class MenuObject {

	private List<WidgetModel> children = new ArrayList<WidgetModel>();
	
	/**
	 * Menu object constructor.
	 * @param child Node.
	 */
	public void addChild(WidgetModel child) {
		children.add(child);
	}
	
	/**
	 * Menu object constructor.
	 * @param child Node.
	 * @param name of the child.
	 */
	public void addChild(WidgetModel child, String name) {
		child.setComponentName(name);
		children.add(child);
	}
	
	/**
	 * Deletes the child node by his index.
	 * @param id Index.
	 */
	public void deleteChild(int id) {
		children.remove(id).delete();
	}
	
	/**
	 * Deletes the child node by his name.
	 * @param name of the child.
	 * @return The state of child.
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
	 * Returns the children by his index.
	 * @param id index.
	 * @return Child.
	 */
	public WidgetModel getChildren(int id) { 
		return children.get(id);
	}
	
	/**
	 * Returns the children by his name.
	 * @param name of the child.
	 * @return Child.
	 */
	public WidgetModel getChildren(String name) {
		for(int i=0; i < children.size(); i++) {
			if(children.get(i).getComponentName().equals(name))
				return children.get(i);
		}
		return null;
	}
	
	/**
	 * Returns the number of children.
	 * @return Number of children.
	 */
	public int getChildNumber() {
		return children.size();
	}
}
