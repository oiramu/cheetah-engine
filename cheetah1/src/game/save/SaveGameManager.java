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

import java.io.File;
import java.io.IOException;

import engine.core.utils.Log;

/**
 * Slot-based save/load API used by gameplay code - maps a slot identifier
 * ("1", "2", "3", or the reserved "autosave" name) to an encrypted file
 * under saves/, via SaveFileCodec.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class SaveGameManager {

	private static final String SAVE_DIR 	= "saves";
	private static final String EXTENSION 	= ".cesave";

	private SaveGameManager() {}

	/**
	 * Encrypts and writes a save to the given slot.
	 * @param slot identifier.
	 * @param saveGame to persist.
	 * @return true if the write succeeded.
	 */
	public static boolean save(String slot, SaveGame saveGame) {
		try {
			SaveFileCodec.write(saveGame, fileFor(slot));
			return true;
		} catch (IOException e) {
			Log.error("Could not save slot '" + slot + "': " + e.getMessage());
			return false;
		}
	}

	/**
	 * Reads and decrypts a save slot.
	 * @param slot identifier.
	 * @return the decoded save, or null if the slot is empty/unreadable.
	 */
	public static SaveGame load(String slot) {return SaveFileCodec.read(fileFor(slot));}

	/**
	 * Whether a slot has ever been written.
	 * @param slot identifier.
	 * @return existence state.
	 */
	public static boolean exists(String slot) {return fileFor(slot).exists();}

	private static File fileFor(String slot) {return new File(SAVE_DIR, slot + EXTENSION);}

}
