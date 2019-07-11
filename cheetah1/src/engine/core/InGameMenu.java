/*
 * Copyright 2019 Carlos Rodriguez.
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

import java.util.HashMap;

import engine.rendering.HUD;
import engine.rendering.Material;
import engine.rendering.RenderingEngine;
import engine.rendering.Texture;
import game.Level;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2019
 */
public class InGameMenu {
	
	public HashMap<String,HUD> 	text = new HashMap<String,HUD>();
	
	private final String		WEAPON_RES_LOC = "weapons/";
	
	public InGameMenu() {
		text.put("Level",new HUD("", new Vector2f(-1.25f,1.2f), new Vector2f(0.75f,0.75f)));
        text.put("Enemies",new HUD("", new Vector2f(-1.25f,1.1f), new Vector2f(0.75f,0.75f)));
        text.put("Secrets",new HUD("", new Vector2f(-1.25f,1.0f), new Vector2f(0.75f,0.75f)));
        text.put("Paused",new HUD("Pause Menu", new Vector2f(-0.3f,0.175f), new Vector2f(1.5f,1.5f)));
        text.put("Unpause",new HUD("Press escape to unpause", new Vector2f(-1.75f,0.0f), new Vector2f(0.4f,0.8f)));
        text.put("Reload",new HUD("Press R to reload level", new Vector2f(-1.75f,-0.15f), new Vector2f(0.4f,0.8f)));
        text.put("Back",new HUD("Press B to get back to menu", new Vector2f(-1.75f,-0.3f), new Vector2f(0.4f,0.8f)));
        text.put("Exit",new HUD("Press X to quit the game", new Vector2f(-1.75f,-0.45f), new Vector2f(0.4f,0.8f)));
        text.put("areYouSure",new HUD("Are you sure you wanna quit?", new Vector2f(-1.75f,0.175f), new Vector2f(0.5f,1.0f)));
        text.put("yesornot",new HUD("Press: Y/N", new Vector2f(-0.45f,0.0f), new Vector2f(0.5f,1.0f)));
        text.put("inventory",new HUD("Inventory:", new Vector2f(0.1f,0.0f), new Vector2f(0.8f,0.8f)));
        text.put("pistol", new HUD(new Material(new Texture(WEAPON_RES_LOC+"pistol/2PISE0")), new Vector2f(1.5f,-2.0f), new Vector2f(0.1f,0.1f)));
        text.put("shotgun", new HUD(new Material(new Texture(WEAPON_RES_LOC+"shotgun/MEDIA")), new Vector2f(1.75f,-2.0f), new Vector2f(0.2f,0.1f)));
        text.put("machinegun", new HUD(new Material(new Texture(WEAPON_RES_LOC+"machinegun/MEDIA")), new Vector2f(3.0f,-2.0f), new Vector2f(0.2f,0.1f)));
        text.put("superShotgun", new HUD(new Material(new Texture(WEAPON_RES_LOC+"superShotgun/MEDIA")), new Vector2f(4.0f,-2.0f), new Vector2f(0.2f,0.1f)));
        text.put("chaingun", new HUD(new Material(new Texture(WEAPON_RES_LOC+"chaingun/UMGPA0")), new Vector2f(0.85f,-2.25f), new Vector2f(0.3f,0.2f)));
        text.put("rocketLauncher", new HUD(new Material(new Texture(WEAPON_RES_LOC+"rocketLauncher/MEDIA")), new Vector2f(2.25f,-3.75f), new Vector2f(0.25f,0.1f)));
        text.put("flameThrower", new HUD(new Material(new Texture(WEAPON_RES_LOC+"flameThrower/FLMTF0")), new Vector2f(4.0f,-3.75f), new Vector2f(0.2f,0.1f)));
        text.put("bullet", new HUD(new Material(new Texture("bullet/MEDIA")), new Vector2f(3.0f,-6.0f), new Vector2f(0.05f,0.1f)));
        text.put("shell", new HUD(new Material(new Texture("shell/MEDIA")), new Vector2f(3.0f,-7.5f), new Vector2f(0.05f,0.1f)));
        text.put("bulletText", new HUD("0", new Vector2f(0.25f,-0.41f), new Vector2f(0.75f,1.5f)));
        text.put("shellText", new HUD("0", new Vector2f(0.25f,-0.51f), new Vector2f(0.75f,1.5f)));
        text.put("rocket", new HUD(new Material(new Texture("rocket/MEDIA1")), new Vector2f(7.0f,-6.0f), new Vector2f(0.05f,0.1f)));
        text.put("gas", new HUD(new Material(new Texture(WEAPON_RES_LOC+"flameThrower/FLMTF0")), new Vector2f(7.0f,-7.5f), new Vector2f(0.05f,0.1f)));
        text.put("rocketText", new HUD("0", new Vector2f(0.5f,-0.41f), new Vector2f(0.75f,1.5f)));
        text.put("gasText", new HUD("0", new Vector2f(0.5f,-0.51f), new Vector2f(0.75f,1.5f)));
	}
	
	public void renderPause(RenderingEngine engine) {
		text.get("Level").render(engine);
		text.get("Paused").render(engine);
		text.get("Unpause").render(engine);
		text.get("Reload").render(engine);
		text.get("Back").render(engine);
		text.get("Exit").render(engine);
		text.get("inventory").render(engine);
		text.get("pistol").render(engine);
		if(Level.getPlayer().isShotgun())
			text.get("shotgun").render(engine);
		if(Level.getPlayer().isMachinegun())
			text.get("machinegun").render(engine);
		if(Level.getPlayer().isSuperShotgun())
			text.get("superShotgun").render(engine);
		if(Level.getPlayer().isChaingun())
			text.get("chaingun").render(engine);
		text.get("bullet").render(engine);
		text.get("bulletText").setText(Level.getPlayer().getBullets());
		text.get("bulletText").render(engine);
		if(Level.getPlayer().isShotgun() || Level.getPlayer().isSuperShotgun()) {
    		text.get("shell").render(engine);
    		text.get("shellText").setText(Level.getPlayer().getShells());
    		text.get("shellText").render(engine);
		}
		if(Level.getPlayer().isRocketLauncher()) {
			text.get("rocketLauncher").render(engine);
    		text.get("rocket").render(engine);
    		text.get("rocketText").setText(Level.getPlayer().getRockets());
    		text.get("rocketText").render(engine);
		}
		if(Level.getPlayer().isFlameThrower()) {
			text.get("flameThrower").render(engine);
    		text.get("gas").render(engine);
    		text.get("gasText").setText(Level.getPlayer().getGas());
    		text.get("gasText").render(engine);
		}
	}
	
	public void renderQuit(RenderingEngine engine) {
		text.get("areYouSure").render(engine);
		text.get("yesornot").render(engine);
	}

}
