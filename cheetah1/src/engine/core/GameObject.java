/*
 * Copyright 2018 Carlos Rodriguez.
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
package engine.core;

import java.util.ArrayList;

import static engine.core.Constants.*;

import engine.components.GameComponent;
import engine.rendering.RenderingEngine;
import engine.rendering.Shader;
import game.Level;
import game.enemies.NaziSoldier;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class GameObject {
	
	private ArrayList <GameComponent> m_list;
	
	/**
	 * Game Object's constructor.
	 */
	public GameObject() {
		m_list = new ArrayList<GameComponent>();
	}
	
	/**
	 * Adds a component for the component's list.
	 * @param component to add
	 */
	public <E> void add(E component) {
		m_list.add((GameComponent) component);
	}
	
	/**
	 * Adds a component list to the component's list.
	 * @param list to add
	 */
	public <E> void add(ArrayList<E> list) {
		for(E component : list)
			m_list.add((GameComponent) component);
	}
	
	/**
	 * Input method for all the components on
	 * the list.
	 */
	public void input() {
		for(GameComponent component : m_list)
			component.input();
	}
	
	/**
	 * Render method for all the components on
	 * the list.
	 * @param shader to render
	 * @param renderingEngine to render
	 */
	public void render(Shader shader, RenderingEngine renderingEngine) {
		for(GameComponent component : m_list)
			if(component.getDistance() < POP_IN)
				component.render(shader, renderingEngine);
	}
	
	/**
	 * Update method for all the components on
	 * the list.
	 * @param delta of time
	 */
	public void update(double delta) {
		for(GameComponent component : m_list)
			component.update(delta);
	}
	
	/**
     * Sorts the number of components added.
     * @param list of objects.
     */
    public <E> void sortNumberComponents(ArrayList<E> list) {
    	if (list.size() > 0) {
    		sortComponents(list, 0, list.size() - 1);
        }
    }
    
    /**
     * Sorts all the objects in the level.
     * @param list of objects.
     * @param low of the array
     * @param high of the array
     */
    private <E> void sortComponents(ArrayList<E> list, int low, int high) {
    	int i = low;
        int j = high;

        E pivot = list.get(low + (high - low) / 2);
        float pivotDistance = ((GameComponent) pivot).getTransform().getPosition().sub(Level.getPlayer().getCamera().getPos()).length();

        while (i <= j) {
            while (((GameComponent) list.get(i)).getTransform().getPosition().sub(Level.getPlayer().getCamera().getPos()).length() > pivotDistance) {
                i++;
            }
            while (((GameComponent) list.get(j)).getTransform().getPosition().sub(Level.getPlayer().getCamera().getPos()).length() < pivotDistance) {
                j--;
            }

            if (i <= j) {
            	E temp = list.get(i);

            	list.set(i, list.get(j));
            	list.set(j, temp);

                i++;
                j--;
            }
        }

        if (low < j) {
        	sortComponents(list, low, j);
        }
        if (i < high) {
        	sortComponents(list, i, high);
        }
    }
    
    /**
     * Removes to the render pipeline the lists of objects to render.
     * @param removeList of objects
     */
    public <E> void removeListToRenderPipeline(ArrayList<E> removeList) {
    	for (E component : removeList) 
    		m_list.remove(component);
    	
    }
    
    /**
     * Kills everything on the list of objects.
     * @param list of objects
     * @param delta of time
     */
    public <E> void updateAndKillToRenderPipeline(ArrayList<E> list, double delta) {
    	for (E component : list) {
    		((NaziSoldier) component).setState(4);
    		((NaziSoldier) component).update(delta);
    	}
    }
	
}
