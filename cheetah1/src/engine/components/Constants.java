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
package engine.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import engine.core.utils.Log;
import engine.menu.system.SEngineUtil;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.2
 * @since 2018
 */
public class Constants {
	
	public static String 	TEXTURE_FILTER;
	public static String 	GAME_GRAPHICS;
	public static float 	MIPMAP_LEVEL;
	public static float 	ANISOTROPIC_LEVEL;
	public static float 	POP_IN;
	public static float 	PARTICLES_POP_IN;
	public static float 	LIGHT_POP_IN;
	public static float 	GRASS_POP_IN;
	public static float 	GRAVITY;
	public static int 		EFFECTS_AUDIO_LEVEL;
	public static int 		MUSIC_AUDIO_LEVEL;
	public static short 	PARTICLES_LEVEL;
	public static boolean	CLEAR_LIGHTS;
	public static boolean	GOD;
	public static boolean	LIGHT_RANGE_CULLING;
	public static boolean	FRUSTUM_CULLING;
	public static float 	LOD_MID_DISTANCE;
	public static float 	LOD_FAR_DISTANCE;
	public static int 		LOD_FAR_TICK_SKIP;
	public static int 		MAX_AUDIO_SOURCES;
	public static int 		TARGET_WIDTH;
	public static int 		TARGET_HEIGHT;
	public static boolean	FULLSCREEN;
	
	/**
	 * Loads all the config language and structures all the components.
	 * @param filePath Path of the text file.
	 */
	public static void load(String filePath) {
		//Reset GameObject	
		BufferedReader fileBuffer = null;
		@SuppressWarnings("unused")
		String line, temporaryName, temporaryTreatment, treatment[];
		boolean fileComplete = false;
		
		try {
			fileBuffer = new BufferedReader(new FileReader(filePath));
			//Interpretation
			while (!fileComplete) {
				if((line = fileBuffer.readLine()) != null) {
					if (line != null && !line.isEmpty()) { //Verify if line contain at least a character
						  if(!(line.charAt(0) == ' ' || line.charAt(0) == '#')) {
						    treatment = SEngineUtil.getInstance().splitString(line, ' ');
						    switch(treatment[0]) {
						    	case "TEXTURE_FILTER":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		TEXTURE_FILTER = treatment[0];				    		
						    		break;
						    	case "GAME_GRAPHICS":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		GAME_GRAPHICS = treatment[0];	
						    		break;
						    	case "CLEAR_LIGHTS":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		CLEAR_LIGHTS = Boolean.parseBoolean(treatment[0]);
						    		break;
						    	case "MIPMAP_LEVEL":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		MIPMAP_LEVEL = Float.parseFloat(treatment[0]);
						    		break;
						    	case "ANISOTROPIC_LEVEL":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		ANISOTROPIC_LEVEL = Float.parseFloat(treatment[0]);
						    		break;
						    	case "POP_IN":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		POP_IN = Float.parseFloat(treatment[0]);
						    		break;
						    	case "PARTICLES_POP_IN":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		PARTICLES_POP_IN = Float.parseFloat(treatment[0]);
						    		break;
						    	case "LIGHT_POP_IN":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		LIGHT_POP_IN = Float.parseFloat(treatment[0]);
						    		break;
						    	case "GRASS_POP_IN":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		GRASS_POP_IN = Float.parseFloat(treatment[0]);
						    		break;
						    	case "GRAVITY":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		GRAVITY = Float.parseFloat(treatment[0]);
						    		break;
						    	case "PARTICLES_LEVEL":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		PARTICLES_LEVEL = Short.parseShort(treatment[0]);
						    		break;
						    	case "EFFECTS_AUDIO_LEVEL":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		EFFECTS_AUDIO_LEVEL = Short.parseShort(treatment[0]) - 20;
						    		if(EFFECTS_AUDIO_LEVEL == 0)
						    			EFFECTS_AUDIO_LEVEL = 0;
						    		else if(EFFECTS_AUDIO_LEVEL == 100)
						    			EFFECTS_AUDIO_LEVEL = 100;
						    		break;
						    	case "GOD":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		GOD = Boolean.parseBoolean(treatment[0]);
						    		break;
						    	case "LIGHT_RANGE_CULLING":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		LIGHT_RANGE_CULLING = Boolean.parseBoolean(treatment[0]);
						    		break;
						    	case "FRUSTUM_CULLING":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		FRUSTUM_CULLING = Boolean.parseBoolean(treatment[0]);
						    		break;
						    	case "LOD_MID_DISTANCE":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		LOD_MID_DISTANCE = Float.parseFloat(treatment[0]);
						    		break;
						    	case "LOD_FAR_DISTANCE":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		LOD_FAR_DISTANCE = Float.parseFloat(treatment[0]);
						    		break;
						    	case "LOD_FAR_TICK_SKIP":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		LOD_FAR_TICK_SKIP = Integer.parseInt(treatment[0]);
						    		break;
						    	case "MAX_AUDIO_SOURCES":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		MAX_AUDIO_SOURCES = Integer.parseInt(treatment[0]);
						    		break;
						    	case "TARGET_WIDTH":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		TARGET_WIDTH = Integer.parseInt(treatment[0]);
						    		break;
						    	case "TARGET_HEIGHT":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		TARGET_HEIGHT = Integer.parseInt(treatment[0]);
						    		break;
						    	case "FULLSCREEN":
						    		treatment = SEngineUtil.getInstance().splitString(line.substring(treatment[0].length()), '='); //Removes the type name and separate line with character '='
						    		temporaryName = treatment[0].replaceAll("\\s", ""); //Delete space
						    		treatment = SEngineUtil.getInstance().splitString(treatment[1], ' ');
						    		FULLSCREEN = Boolean.parseBoolean(treatment[0]);
						    		break;
						    	}
						    }
						}
				}
				else
					fileComplete = true;
			}
		} catch (NumberFormatException e) {
			Log.error("Malformed number in config file '" + filePath + "': " + e.getMessage());
		} catch (IOException e) {
			Log.error("Could not read config file '" + filePath + "': " + e.getMessage());
		}
	}

	/** Every key this class tracks, in the order a freshly-written file should list them in. */
	private static final String[] TRACKED_KEYS = {
		"TEXTURE_FILTER", "GAME_GRAPHICS", "CLEAR_LIGHTS", "MIPMAP_LEVEL", "ANISOTROPIC_LEVEL",
		"POP_IN", "PARTICLES_POP_IN", "GRASS_POP_IN", "LIGHT_POP_IN", "GRAVITY", "PARTICLES_LEVEL",
		"EFFECTS_AUDIO_LEVEL", "GOD", "LIGHT_RANGE_CULLING", "FRUSTUM_CULLING", "LOD_MID_DISTANCE",
		"LOD_FAR_DISTANCE", "LOD_FAR_TICK_SKIP", "MAX_AUDIO_SOURCES", "TARGET_WIDTH", "TARGET_HEIGHT",
		"FULLSCREEN"
	};

	/**
	 * Writes the current field values back to the config file, in place: every
	 * line whose key this class tracks gets its value replaced, everything
	 * else (comments, blank lines, untracked keys like RESOURCE_PACK) is left
	 * as written. Any tracked key missing from the file gets appended. If the
	 * file doesn't exist yet, a fresh one is written from the current fields.
	 * @param filePath Path of the text file.
	 */
	public static void save(String filePath) {
		File file = new File(filePath);
		List<String> lines = new ArrayList<String>();

		if(file.exists()) {
			try(BufferedReader fileBuffer = new BufferedReader(new FileReader(file))) {
				String line;
				while((line = fileBuffer.readLine()) != null)
					lines.add(line);
			} catch (IOException e) {
				Log.error("Could not read config file '" + filePath + "' before saving: " + e.getMessage());
			}
		}

		Set<String> rewritten = new HashSet<String>();
		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if(line.isEmpty() || line.charAt(0) == ' ' || line.charAt(0) == '#')
				continue;

			String key = SEngineUtil.getInstance().splitString(line, ' ')[0];
			String value = valueFor(key);
			if(value == null)
				continue; //Not a key this class tracks - leave the line as written

			int equals = line.indexOf('=', key.length());
			if(equals < 0)
				continue; //Malformed line for this key - leave it as written

			lines.set(i, line.substring(0, equals + 1) + value);
			rewritten.add(key);
		}

		for(String key : TRACKED_KEYS)
			if(!rewritten.contains(key))
				lines.add(key + " value=" + valueFor(key));

		try(PrintWriter writer = new PrintWriter(new FileWriter(file))) {
			for(String line : lines)
				writer.println(line);
		} catch (IOException e) {
			Log.error("Could not write config file '" + filePath + "': " + e.getMessage());
		}
	}

	/**
	 * Current value of a tracked config key, formatted the way load() expects
	 * to read it back (e.g. EFFECTS_AUDIO_LEVEL's -20 offset is reversed here).
	 * @param key config key.
	 * @return formatted value, or null if key isn't tracked by this class.
	 */
	private static String valueFor(String key) {
		switch(key) {
			case "TEXTURE_FILTER": 		return TEXTURE_FILTER;
			case "GAME_GRAPHICS": 			return GAME_GRAPHICS;
			case "CLEAR_LIGHTS": 			return Boolean.toString(CLEAR_LIGHTS);
			case "MIPMAP_LEVEL": 			return Float.toString(MIPMAP_LEVEL);
			case "ANISOTROPIC_LEVEL": 		return Float.toString(ANISOTROPIC_LEVEL);
			case "POP_IN": 					return Float.toString(POP_IN);
			case "PARTICLES_POP_IN": 		return Float.toString(PARTICLES_POP_IN);
			case "GRASS_POP_IN": 			return Float.toString(GRASS_POP_IN);
			case "LIGHT_POP_IN": 			return Float.toString(LIGHT_POP_IN);
			case "GRAVITY": 				return Float.toString(GRAVITY);
			case "PARTICLES_LEVEL": 		return Short.toString(PARTICLES_LEVEL);
			case "EFFECTS_AUDIO_LEVEL": 	return Integer.toString(EFFECTS_AUDIO_LEVEL + 20);
			case "GOD": 					return Boolean.toString(GOD);
			case "LIGHT_RANGE_CULLING": 	return Boolean.toString(LIGHT_RANGE_CULLING);
			case "FRUSTUM_CULLING": 		return Boolean.toString(FRUSTUM_CULLING);
			case "LOD_MID_DISTANCE": 		return Float.toString(LOD_MID_DISTANCE);
			case "LOD_FAR_DISTANCE": 		return Float.toString(LOD_FAR_DISTANCE);
			case "LOD_FAR_TICK_SKIP": 		return Integer.toString(LOD_FAR_TICK_SKIP);
			case "MAX_AUDIO_SOURCES": 		return Integer.toString(MAX_AUDIO_SOURCES);
			case "TARGET_WIDTH": 			return Integer.toString(TARGET_WIDTH);
			case "TARGET_HEIGHT": 			return Integer.toString(TARGET_HEIGHT);
			case "FULLSCREEN": 			return Boolean.toString(FULLSCREEN);
			default: return null;
		}
	}

}
