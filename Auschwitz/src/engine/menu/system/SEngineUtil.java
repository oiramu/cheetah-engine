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
package engine.menu.system;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
*
* @author Carlos Rodriguez
* @version 1.0
* @since 2017
*/
public class SEngineUtil {	
	
	public static boolean g_collisionBox = false;
	
	/**
	 * Constructors of the Tool-set.
	 */
	private static SEngineUtil INSTANCE = new SEngineUtil();
	public static SEngineUtil getInstance()
	{	return INSTANCE;	}
	
	private AngelCodeFont font;
	private AngelCodeFont boldFont;
	
	/**
	 * Constructor of the Menu tools.
	 */
	private  SEngineUtil() {
		try {
			font = new AngelCodeFont("res/textures/fonts/defaultBitmapFont.fnt", new Image("res/textures/fonts/defaultBitmapFont_0.png")); 
			boldFont = new AngelCodeFont("res/textures/fonts/boldBitmapFont.fnt", new Image("res/textures/fonts/boldBitmapFont_0.png")); 
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Splits the string character to a character of the font.
	 * @param line of text.
	 * @param separator of characters.
	 * @return Text to array.
	 */
	public String[] splitString(String line, char separator) {
		ArrayList<String> stringList = new ArrayList<String>();
		StringBuilder stringBuilder = new StringBuilder(); //StringBuilder : Allows to create a string (lenght, modify, etc...)
		char character;
		
		for (int loop = 0; loop < line.length(); loop++) {
			character = line.charAt(loop);
			if(character != separator) 
				stringBuilder.append(character);
			
			if(character == separator || loop == line.length()-1) {
				stringList.add(stringBuilder.toString());
				stringBuilder.setLength(0); //Reset Buffer
			}
		}
		return stringList.toArray(new String[stringList.size()]); //Convert ArrayList to simple Array
	}

	/**
	 * Find the character in a string line and match with a specific character.
	 * @param line of text.
	 * @param character to match.
	 * @return Boolean state.
	 */
	public boolean charMatch(String line, char character) {
		for(int i=0; i < line.length(); i++) {
			if(line.charAt(i) == character)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Check if any character don't collide one to other by AABB.
	 * @param entity1Pos Vector position 1.
	 * @param entity1Box Vector character hitbox1.
	 * @param entity2Pos Vector position 2.
	 * @param entity2Box Vector character hitbox2.
	 * @return Boolean state.
	 */
	public boolean AABB(Vector3f entity1Pos, Vector3f entity1Box, Vector3f entity2Pos, Vector3f entity2Box) {
		if((entity1Pos.x-entity1Box.x <= entity2Pos.x+entity2Box.x && entity1Pos.x+entity1Box.x >= entity2Pos.x-entity2Box.x) &&
		  (entity1Pos.y-entity1Box.y <= entity2Pos.y+entity2Box.y && entity1Pos.y+entity1Box.y >= entity2Pos.y-entity2Box.y) &&
		  (entity1Pos.z-entity1Box.z <= entity2Pos.z+entity2Box.z && entity1Pos.z+entity1Box.z >= entity2Pos.z-entity2Box.z)) {
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Returns the font that is been used.
	 * @return Font.
	 */
	public AngelCodeFont getFont() {
		return font;
	}
	
	/**
	 * Returns the bold font that is been used.
	 * @return Bold font.
	 */
	public AngelCodeFont getBoldFont() {
		return boldFont;
	}
}
