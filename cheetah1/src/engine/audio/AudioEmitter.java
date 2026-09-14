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

import engine.components.GameComponent;

/**
 * Plays sounds at a GameComponent's position through the shared
 * AudioManager source pool. Replaces the old pattern of a
 * "private static final Clip xxxNoise" field per sound per enemy TYPE
 * (shared by every instance of that type, so two instances firing at
 * once cut each other off) with one instance per entity, each sound
 * played by name through real 3D OpenAL positioning instead of a
 * caller-computed mono distance fade.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class AudioEmitter {

	private final GameComponent owner;
	private Source lastSource;

	/**
	 * Constructor for an emitter tied to a component's position.
	 * @param owner whose transform position sounds are played at.
	 */
	public AudioEmitter(GameComponent owner) {
		this.owner = owner;
	}

	/**
	 * Plays a sound at the owner's current position.
	 * @param soundName resource name under res/audio/, without the
	 * .wav extension - e.g. "naziSoldier/SSSSIT".
	 */
	public void play(String soundName) {
		play(soundName, false);
	}

	/**
	 * Plays a sound at the owner's current position.
	 * @param soundName resource name under res/audio/, without the
	 * .wav extension.
	 * @param looping whether it should loop.
	 */
	public void play(String soundName, boolean looping) {
		lastSource = AudioManager.play(SoundLibrary.get(soundName), owner.getTransform().getPosition(), looping);
	}

	/**
	 * Stops whatever this emitter last played, if it's still playing on
	 * the same pooled source (a busy pool can already have recycled it
	 * for something else - a rare, harmless best-effort case).
	 */
	public void stop() {
		if (lastSource != null)
			lastSource.stop();
	}

	/**
	 * Gets whether this emitter's last sound is still playing.
	 * @return playing state.
	 */
	public boolean isPlaying() {
		return lastSource != null && lastSource.isPlaying();
	}

}
