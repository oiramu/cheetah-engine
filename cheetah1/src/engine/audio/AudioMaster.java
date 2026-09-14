/*
 * Copyright 2018 Carlos Rodriguez.
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

import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.util.WaveData;

import engine.core.Vector3f;
import engine.core.utils.Log;

import static org.lwjgl.openal.AL.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.1
 * @since 2018
 */
public class AudioMaster {
	
	private static List<Integer> buffers = new ArrayList<Integer>();
	
	/**
	 * Initializes the audio master system.
	 */
	public static void init() {
		try {
			create();
		} catch (LWJGLException e) {
			Log.error("Failed to initialize OpenAL - audio will be disabled: " + e.getMessage());
		}
	}
	
	/**
	 * Sets the position and orientation of the main listener (the
	 * player's camera), so OpenAL can compute real panning/attenuation
	 * relative to it instead of the old flat mono distance fade.
	 * @param position of the listener.
	 * @param forward direction the listener is facing.
	 * @param up direction of the listener.
	 */
	public static void setListenerData(Vector3f position, Vector3f forward, Vector3f up) {
		alListener3f(AL_POSITION, position.getX(), position.getY(), position.getZ());
		alListener3f(AL_VELOCITY, 0, 0, 0);
		alDistanceModel(AL_LINEAR_DISTANCE_CLAMPED);

		FloatBuffer orientation = BufferUtils.createFloatBuffer(6);
		orientation.put(forward.getX()).put(forward.getY()).put(forward.getZ());
		orientation.put(up.getX()).put(up.getY()).put(up.getZ());
		orientation.flip();
		alListener(AL_ORIENTATION, orientation);
	}

	/**
	 * Loads a sound file from disk into the audio's data structure.
	 * @param filePath to load, e.g. "./res/audio/name.wav".
	 * @return sound buffer.
	 */
	public static int loadSound(String filePath) {
		int buffer = alGenBuffers();
		buffers.add(buffer);
		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filePath));
			WaveData waveFile = WaveData.create(stream);
			alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
			waveFile.dispose();
		} catch (Exception e) {
			Log.error("Could not load sound '" + filePath + "': " + e.getMessage());
		}
		return buffer;
	}
	
	/**
	 * Cleans up everything when closes.
	 */
	public static void cleanUp() {
		for(int buffer : buffers)
			alDeleteBuffers(buffer);
		destroy();
	}
	
}
