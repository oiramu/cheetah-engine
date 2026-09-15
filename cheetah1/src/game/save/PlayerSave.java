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
package game.save;

/**
 * Plain data snapshot of the fields a save needs to restore a Player -
 * absolute values only, no LWJGL types, so Gson never needs custom adapters.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class PlayerSave {

	public float 	posX, posY, posZ;
	public float 	rotX, rotY, rotZ, rotW;

	public int 		health, maxHealth;
	public int 		armor, maxArmor;
	public boolean 	hasArmor;
	public int 		bullets, maxBullets;
	public int 		shells, maxShells;
	public int 		rockets, maxRockets;
	public int 		gas, maxGas;

	public boolean 	goldKey, bronzeKey;
	public boolean 	hasShotgun, hasMachinegun, hasSuperShotgun, hasChaingun, hasRocketLauncher, hasFlameThrower;
	public String 	weaponState;

	public boolean 	mouseLocked, isFlashLightOn;

}
