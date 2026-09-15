/*
 * Copyright 2026 Carlos Rodriguez.
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

import engine.components.Constants;

/**
 * Applies a settings-menu control's changed value to the matching Constants
 * field, and reads a field's current value back out for display - a
 * switch-per-field bridge deliberately mirroring Constants.load()'s own
 * idiom (the codebase uses string-keyed switches everywhere, never
 * reflection), so each new exposed setting costs one case in each method
 * here, same as load()/save() already cost one case each.
 *
 * EFFECTS_AUDIO_LEVEL is the one field with a twist: Constants.load()
 * stores it with a -20 offset (see Constants.java), so both methods below
 * reverse that at the boundary, the same way Constants.save() already does
 * - the settings menu always deals in the same 0-100-ish numbers config.txt
 * does, never the field's internal offset representation.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class SSettingsBridge {

	private SSettingsBridge() {}

	/**
	 * Applies a new value to the Constants field a setting key names.
	 * @param key Constants field name.
	 * @param value new value, formatted the way the field's type expects.
	 */
	public static void apply(String key, String value) {
		switch(key) {
			case "TEXTURE_FILTER": 		Constants.TEXTURE_FILTER = value; break;
			case "GAME_GRAPHICS": 			Constants.GAME_GRAPHICS = value; break;
			case "PARTICLES_LEVEL": 		Constants.PARTICLES_LEVEL = Short.parseShort(value); break;
			case "ANISOTROPIC_LEVEL": 		Constants.ANISOTROPIC_LEVEL = Float.parseFloat(value); break;
			case "EFFECTS_AUDIO_LEVEL": 	Constants.EFFECTS_AUDIO_LEVEL = Integer.parseInt(value) - 20; break;
			case "MAX_AUDIO_SOURCES": 		Constants.MAX_AUDIO_SOURCES = Integer.parseInt(value); break;
			case "TARGET_WIDTH": 			Constants.TARGET_WIDTH = Integer.parseInt(value); break;
			case "TARGET_HEIGHT": 			Constants.TARGET_HEIGHT = Integer.parseInt(value); break;
			case "FULLSCREEN": 			Constants.FULLSCREEN = Boolean.parseBoolean(value); break;
		}
	}

	/**
	 * Current value of a Constants field a setting key names, formatted the
	 * same way apply() expects to read it back.
	 * @param key Constants field name.
	 * @return formatted value, or "" if key isn't exposed here.
	 */
	public static String currentValue(String key) {
		switch(key) {
			case "TEXTURE_FILTER": 		return Constants.TEXTURE_FILTER;
			case "GAME_GRAPHICS": 			return Constants.GAME_GRAPHICS;
			case "PARTICLES_LEVEL": 		return Short.toString(Constants.PARTICLES_LEVEL);
			case "ANISOTROPIC_LEVEL": 		return Integer.toString(Math.round(Constants.ANISOTROPIC_LEVEL));
			case "EFFECTS_AUDIO_LEVEL": 	return Integer.toString(Constants.EFFECTS_AUDIO_LEVEL + 20);
			case "MAX_AUDIO_SOURCES": 		return Integer.toString(Constants.MAX_AUDIO_SOURCES);
			case "TARGET_WIDTH": 			return Integer.toString(Constants.TARGET_WIDTH);
			case "TARGET_HEIGHT": 			return Integer.toString(Constants.TARGET_HEIGHT);
			case "FULLSCREEN": 			return Boolean.toString(Constants.FULLSCREEN);
			default: return "";
		}
	}

}
