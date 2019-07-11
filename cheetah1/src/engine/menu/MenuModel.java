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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;

import engine.core.Input;
import engine.menu.gui.GUIComponent;
import engine.menu.system.SEngineUtil;
import engine.menu.widget.WidgetAnimation;
import engine.menu.widget.WidgetImage;
import engine.menu.widget.WidgetModel;
import engine.menu.widget.WidgetText;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class MenuModel extends GUIComponent {

	private MenuObject 		menuObject = new MenuObject();
	private List<Button> 	buttonPointer = new ArrayList<Button>();
	
	private Graphics 		m_m2DRender;
	
	//real time designer 
	String 					m_filePath;
	
	/**
	 * Menu model constructor.
	 * @param filePath of the menu.
	 */
	public MenuModel(String filePath) {
		this.m_filePath = filePath;
		m_m2DRender = new Graphics();
		load(filePath);
		
		if(buttonPointer.size() > 0)
			buttonPointer.get(0).setHover(true);
	}
	
	/**
	 * Deletes the object's child if need it.
	 */
	public void delete() {
		for(int i=0; i < menuObject.getChildNumber(); i++) {
			menuObject.deleteChild(i); //Delete child
		}
	}
	
	/**
	 * Updates the menu rendering every single frame, also can reload it
	 */
	public void update() {
		// Reload menu
		if(Input.getKeyDown(Input.KEY_R)) {
			this.delete();
			load(m_filePath);
		}
		
		//Update Objects
		for(int i=0; i < menuObject.getChildNumber(); i++) {
			menuObject.getChildren(i).update();
		}
	}
	
	/**
	 * Draws all the components of the menu.
	 */
	public void draw2D() {
		//m_2DRender.scale(0.5f, 0.5f);
		m_m2DRender.pushTransform();
		for(int i=0; i < menuObject.getChildNumber(); i++) {
			menuObject.getChildren(i).draw(); //Render Objects
		}
		m_m2DRender.popTransform();
	}
	
	/**
	 * Loads all the menu language and structures all the components.
	 * @param filePath Path of the text file.
	 */
	public void load(String filePath) {
		//Reset GameObject	
		BufferedReader fileBuffer = null;
		String line, temporaryName, temporaryTreatment, treatment[];
		boolean fileComplete = false;
		
		try {
			fileBuffer = new BufferedReader(new FileReader(filePath));
			//Interpretation
			while (!fileComplete) {
				if((line = fileBuffer.readLine()) != null)
				{
					if (line != null && !line.isEmpty()) { //Verify if line contain at least a character
						  if(!(line.charAt(0) == ' ' || line.charAt(0) == '#')) {
						    treatment = SEngineUtil.getInstance().splitString(line, ' ');
						    switch(treatment[0]) {
						    	case "Button":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ':');	
						    		temporaryTreatment = treatment[0];
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		
						    		menuObject.addChild(new Button("/res/textures/coreDisplay/button.png", temporaryTreatment, Float.parseFloat(treatment[0]), Float.parseFloat(treatment[1]), Float.parseFloat(treatment[2]), Float.parseFloat(treatment[3]))
						    				, temporaryName);
						    		break;
						    	case "Image":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    			
						    		menuObject.addChild(new WidgetImage(treatment[0], Float.parseFloat(treatment[1]), Float.parseFloat(treatment[2]), Float.parseFloat(treatment[3]), Float.parseFloat(treatment[4]))
						    				, temporaryName);
						    		break;
						    	case "Background":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    			
						    		menuObject.addChild(new WidgetImage(treatment[0], Integer.parseInt(treatment[1]), Integer.parseInt(treatment[2]), Float.parseFloat(treatment[3]), Float.parseFloat(treatment[4]))
						    				, temporaryName);
						    		break;
						    	case "Text":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ':');	
						    		temporaryTreatment = treatment[0];
						    		
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		
						    		menuObject.addChild(new WidgetText(temporaryTreatment, Float.parseFloat(treatment[0]), Float.parseFloat(treatment[1]))
						    				, temporaryName);
						    		break;
						    	case "TextBold":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ':');	
						    		temporaryTreatment = treatment[0];
						    		
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		
						    		menuObject.addChild(new WidgetText(temporaryTreatment, Float.parseFloat(treatment[0]), Float.parseFloat(treatment[1]), true)
						    				, temporaryName);
						    		break;
						    	case "Animation":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		
						    		menuObject.addChild(new WidgetAnimation(m_m2DRender, Integer.parseInt(treatment[0]), treatment[1], Integer.parseInt(treatment[2]), Float.parseFloat(treatment[3]))
						    				, temporaryName);
						    		break;
						    	default: //I suppose exists
						    		treatment = SEngineUtil.getInstance().splitString(line, '>');
						    		WidgetModel widget = menuObject.getChildren(treatment[0]);
						    		if(widget != null) { //Found
						    			switch(widget.getComponentType()) {
						    				case "Button":
						    					treatment = SEngineUtil.getInstance().splitString(treatment[1], ':');
						    					if(treatment[0].equals("addAction")) {
							    					//Retransform in button
							    					if(widget instanceof Button) {				//Use funtion
							    						Button button = (Button)widget;
							    						
							    						if(treatment.length == 2)
							    							button.addEvent(new MenuEvent(treatment[1]));
							    						else 
							    							button.addEvent(new MenuEvent(treatment[1], treatment[2]));
							    					}
						    					}
						    					else if(treatment[0].equals("selectId")) {
						    						buttonPointer.add((Button) widget); //Retransform in Button
						    					}
						    					break;
						    			}
						    		}
						    		break;
						    	}
						    }
						}
				}
				else
					fileComplete = true;
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
