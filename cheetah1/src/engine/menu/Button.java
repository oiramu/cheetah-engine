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

import javax.sound.sampled.Clip;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import engine.audio.AudioUtil;
import engine.core.Input;
import engine.menu.system.SEngineUtil;
import engine.menu.widget.WidgetModel;
import engine.rendering.Window;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2017
 */
public class Button extends WidgetModel {

	private Vector2f size;
	private MenuEvent buttonEvent = null;
	private SpriteSheet texture;
	private String text;
	
	private static final Clip clickSound = AudioUtil.loadAudio("button");
	
	private boolean hover = false;
	
	/**
	 * Menu's button constructor.
	 * @param texturePath Path of the button's texture.
	 * @param text of the button.
	 * @param x position.
	 * @param y position.
	 * @param w width.
	 * @param h height.
	 */
	public Button(String texturePath, String text, int x, int y, int w, int h) {
		componentType = "Button";
		try {
			this.text = text;
			texture = new SpriteSheet(texturePath, 466, 37);
			
			position = new Vector2f(x, y);
			size = new Vector2f(w, h);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes the button when need it.
	 */
	public void delete() {
		try {
			texture.destroy();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the button rendering every frame per second.
	 */
	public void update() {
		if(buttonEvent != null) {
			if(Mouse.getX() > position.x && Mouse.getX() < position.x+ size.x && (-Mouse.getY()+Window.getHeight()) > position.y && (-Mouse.getY()+Window.getHeight()) < position.y + size.y) {
				hover = true;
			}else {
				hover = false;
			}
			if(hover && Input.getMouseDown(0)) //mouse left button
				callEvent();
		}
	}

	/**
	 * Draws the button if it hover or not.
	 */
	public void draw() {
		//Button Texture
		if(hover) {
			texture.getSprite(0, 1).draw(position.x, position.y, size.x, size.y);
			//Text
			SEngineUtil.getInstance().getFont().drawString(position.x + 24, position.y, text);
		} else {
			texture.getSprite(0, 0).draw(position.x, position.y, size.x, size.y);
			//Text
			SEngineUtil.getInstance().getFont().drawString(position.x + 24, position.y, text);
		}
	}
	
	/**
	 * Calls a new event when it has been clicked.
	 */
	public void callEvent() {
		if(buttonEvent != null) {
			buttonEvent.update();
			AudioUtil.playAudio(clickSound, 0);
		}
	}
	
	/**
	 * Adds a event to be click-able in the menu. 
	 * @param event to be click-able.
	 */
	public void addEvent(MenuEvent event) {buttonEvent = event;}
	
	/**
	 * Returns the texture of the button.
	 * @return Texture of the button.
	 */
	public SpriteSheet getTexture() {return texture;}
	
	/**
	 * Sets a texture for the button.
	 * @param texture for the button.
	 */
	public void setTexture(SpriteSheet texture) {this.texture = texture;}
	
	/**
	 * Check if hover or not when need it.
	 * @param hover State.
	 */
	public void setHover(boolean hover) {this.hover = hover;}
	
}
