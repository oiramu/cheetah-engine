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

import java.util.ArrayList;
import java.util.List;

import engine.components.Camera;
import engine.components.Constants;
import engine.core.Vector3f;

/**
 * Per-frame coordinator for the OpenAL system: owns the fixed-size
 * source pool every sound (entity voice or one-shot) is played through,
 * and keeps the listener synced to the player's camera every frame so
 * OpenAL can compute real panning/attenuation instead of the old flat
 * mono distance fade.
 *
 * The pool is shared by every AudioEmitter rather than giving each
 * entity its own dedicated Source deliberately: entities are discarded
 * and rebuilt on every level reload, and a dedicated-Source-per-entity
 * design with no disposal hook would leak OpenAL sources across
 * reloads. A fixed pool never grows, so there's nothing to leak.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class AudioManager {

	private static SourcePool<Source> pool;
	private static boolean initialized;

	private AudioManager() {}

	/**
	 * Creates the OpenAL context and the source pool. Safe to call more
	 * than once - only the first call does anything.
	 */
	public static void init() {
		if (initialized)
			return;

		AudioMaster.init();

		List<Source> sources = new ArrayList<Source>();
		int poolSize = Math.max(1, Constants.MAX_AUDIO_SOURCES);
		for (int i = 0; i < poolSize; i++)
			sources.add(new Source());

		pool = new SourcePool<Source>(sources);
		initialized = true;
	}

	/**
	 * Syncs the OpenAL listener to the player's camera. Called once per
	 * frame.
	 * @param camera the player's camera.
	 */
	public static void updateListener(Camera camera) {
		if (!initialized)
			return;

		AudioMaster.setListenerData(camera.getPos(), camera.getForward(), camera.getUp());
	}

	/**
	 * Plays a sound at a world position through a pooled source.
	 * @param buffer to play.
	 * @param position of the sound in world space.
	 * @param looping whether it should loop.
	 * @return the source it's playing on, or null if the audio system
	 * hasn't been initialized yet.
	 */
	public static Source play(int buffer, Vector3f position, boolean looping) {
		if (!initialized)
			return null;

		Source source = pool.obtain();
		source.setPosition(position);
		source.setLooping(looping);
		source.play(buffer);
		return source;
	}

	/**
	 * Destroys the OpenAL context. Safe to call even if init() was never
	 * called.
	 */
	public static void cleanUp() {
		if (!initialized)
			return;

		AudioMaster.cleanUp();
		initialized = false;
	}

}
