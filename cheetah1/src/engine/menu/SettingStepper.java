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
 * A menu control that steps one numeric Constants field up or down within
 * [min, max] each time it's clicked - the left half of the control steps
 * down, the right half steps up. Reuses Button's sprite sheet and
 * hover/click pattern, applies the change to Constants immediately for
 * instant feedback, and leaves persisting it to res/config.txt to the
 * settings screen's "Save &amp; Back" Button.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class SettingStepper extends WidgetModel {

	private static final String CLICK_SOUND = "button";

	private Vector2f 		m_size;
	private SpriteSheet 	m_texture;
	private String 			m_label;
	private String 			m_settingKey;
	private float 			m_min, m_max, m_step;
	private float 			m_value;

	private boolean 		g_hover = false;

	/**
	 * Setting-stepper constructor.
	 * @param label shown before the current value.
	 * @param x position.
	 * @param y position.
	 * @param w width.
	 * @param h height.
	 * @param settingKey Constants field this control changes.
	 * @param min value.
	 * @param max value.
	 * @param step size applied per click.
	 */
	public SettingStepper(String label, float x, float y, float w, float h, String settingKey, float min, float max, float step) {
		m_componentType = "SettingStepper";
		try {
			m_label = label;
			m_settingKey = settingKey;
			m_min = min;
			m_max = max;
			m_step = step;
			m_value = clamp(readCurrentValue());

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
	 * Updates hover state and steps the value up or down on click,
	 * depending on which half of the control was clicked.
	 */
	public void update() {
		float mouseX = Mouse.getX();
		float mouseY = -Mouse.getY() + Window.getHeight();

		g_hover = mouseX > m_position.x && mouseX < m_position.x + m_size.x
				&& mouseY > m_position.y && mouseY < m_position.y + m_size.y;

		if(g_hover && Input.getMouseDown(0)) {
			boolean rightHalf = mouseX > m_position.x + (m_size.x / 2);
			m_value = clamp(m_value + (rightHalf ? m_step : -m_step));
			SSettingsBridge.apply(m_settingKey, formatValue());
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
			m_label + ": " + formatValue());
	}

	private float clamp(float value) {return Math.max(m_min, Math.min(m_max, value));}

	/**
	 * Formats the current value the way the target field's parser expects -
	 * whole numbers with no decimal point when min/max/step are all whole,
	 * which covers every field this control is used for so far (all the
	 * int/short fields need this; Float.parseFloat also accepts it fine for
	 * the one float field, ANISOTROPIC_LEVEL).
	 * @return formatted value.
	 */
	private String formatValue() {
		if(m_step == Math.floor(m_step) && m_min == Math.floor(m_min) && m_max == Math.floor(m_max))
			return Integer.toString(Math.round(m_value));
		return Float.toString(m_value);
	}

	private float readCurrentValue() {
		try {
			return Float.parseFloat(SSettingsBridge.currentValue(m_settingKey));
		} catch (NumberFormatException e) {
			return m_min;
		}
	}

}
