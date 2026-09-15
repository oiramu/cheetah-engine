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
package engine.menu;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import engine.audio.AudioManager;
import engine.audio.SoundLibrary;
import engine.core.Debug;
import engine.core.Input;
import engine.core.Vector3f;
import engine.core.crash.CrashReport;
import engine.core.utils.Log;
import engine.menu.system.SEngineUtil;
import engine.menu.system.SSettingsBridge;
import engine.menu.widget.WidgetModel;
import engine.rendering.Window;

/**
 * A menu control that cycles through a fixed list of string values for one
 * Constants field each time it's clicked - e.g. TEXTURE_FILTER's
 * Nearest/Bilinear/Trilinear. Reuses Button's sprite sheet and hover/click
 * pattern rather than a new texture, applies the change to Constants
 * immediately for instant feedback, and leaves persisting it to
 * res/config.txt to the settings screen's "Save &amp; Back" Button.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class SettingCycle extends WidgetModel {

	private static final String CLICK_SOUND = "button";

	private Vector2f 		m_size;
	private SpriteSheet 	m_texture;
	private String 			m_label;
	private String 			m_settingKey;
	private String[] 		m_options;
	private int 			m_index;

	private boolean 		g_hover = false;

	/**
	 * Setting-cycle constructor.
	 * @param label shown before the current value.
	 * @param x position.
	 * @param y position.
	 * @param w width.
	 * @param h height.
	 * @param settingKey Constants field this control changes.
	 * @param options the fixed list of values to cycle through.
	 */
	public SettingCycle(String label, float x, float y, float w, float h, String settingKey, String[] options) {
		m_componentType = "SettingCycle";
		try {
			m_label = label;
			m_settingKey = settingKey;
			m_options = options;
			m_index = indexOfCurrentValue();

			m_texture = new SpriteSheet("/res/textures/coreDisplay/button.png", 466, 37);
			m_position = new Vector2f(x * Window.getWidth(), y * Window.getHeight());
			m_size = new Vector2f(w * Window.getWidth(), h * Window.getHeight());
		} catch (SlickException e) {
			Debug.crash(new CrashReport(e));
		}
	}

	/**
	 * Deletes the control's texture when no longer needed.
	 */
	public void delete() {
		try {
			m_texture.destroy();
		} catch (SlickException e) {
			Log.error("Could not destroy setting control texture: " + e.getMessage());
		}
	}

	/**
	 * Updates hover state and advances to the next option on click.
	 */
	public void update() {
		float mouseX = Mouse.getX();
		float mouseY = -Mouse.getY() + Window.getHeight();

		g_hover = mouseX > m_position.x && mouseX < m_position.x + m_size.x
				&& mouseY > m_position.y && mouseY < m_position.y + m_size.y;

		if(g_hover && Input.getMouseDown(0)) {
			m_index = (m_index + 1) % m_options.length;
			SSettingsBridge.apply(m_settingKey, m_options[m_index]);
			AudioManager.play(SoundLibrary.get(CLICK_SOUND), new Vector3f(0, 0, 0), false);
		}
	}

	/**
	 * Draws the control's background and its "label: value" text.
	 */
	public void draw() {
		if(g_hover)
			m_texture.getSprite(0, 1).draw(m_position.x, m_position.y, m_size.x, m_size.y);
		else
			m_texture.getSprite(0, 0).draw(m_position.x, m_position.y, m_size.x, m_size.y);

		SEngineUtil.getInstance().getFont().drawString(m_position.x + (int) (0.024 * Window.getWidth()), m_position.y,
			m_label + ": " + m_options[m_index]);
	}

	/**
	 * Finds which option matches the Constants field's current value, so
	 * the control opens already showing the real setting rather than
	 * always starting at index 0.
	 * @return matching index, or 0 if the current value isn't one of the
	 * options (e.g. a hand-edited config.txt value outside this list).
	 */
	private int indexOfCurrentValue() {
		String current = SSettingsBridge.currentValue(m_settingKey);
		for(int i = 0; i < m_options.length; i++)
			if(m_options[i].equals(current))
				return i;
		return 0;
	}

}
