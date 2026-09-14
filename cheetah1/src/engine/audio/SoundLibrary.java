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
package engine.audio;

import java.util.HashMap;

/**
 * Static buffer-id cache keyed by sound name, mirroring the already-
 * correct static-cache pattern engine.rendering.Texture uses for GPU
 * textures. Replaces the ~150 individual "private static final Clip"
 * fields that used to be scattered across every enemy/pickup/object
 * class, each independently loading (and never sharing) its own copy.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class SoundLibrary {

	private static final String AUDIO_RES_LOC = "./res/audio/";

	private static final HashMap<String, Integer> buffers = new HashMap<String, Integer>();

	private SoundLibrary() {}

	/**
	 * Gets the OpenAL buffer id for a sound, loading and caching it from
	 * ./res/audio/&lt;name&gt;.wav on first use.
	 * @param name of the sound, without the res/audio/ prefix or .wav
	 * extension - e.g. "naziSoldier/SSSSIT".
	 * @return buffer id.
	 */
	public static int get(String name) {
		Integer buffer = buffers.get(name);

		if (buffer == null) {
			buffer = AudioMaster.loadSound(AUDIO_RES_LOC + name + ".wav");
			buffers.put(name, buffer);
		}

		return buffer;
	}

}
