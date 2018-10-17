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

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.util.WaveData;

import engine.core.Vector3f;

import static org.lwjgl.openal.AL.*;
import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL11.*;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class AudioMaster {
	
	private static List<Integer> m_buffers = new ArrayList<Integer>();
	
	/**
	 * Initializes the audio master system.
	 */
	public static void init() {
		try {
			create();	
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the position of the main listener.
	 * @param position of the listener
	 */
	public static void setListenerData(Vector3f position) {
		alListener3f(AL_POSITION, position.getX(), position.getY(), position.getZ());
		alListener3f(AL_VELOCITY, 0, 0, 0);
		alDistanceModel(AL_LINEAR_DISTANCE_CLAMPED);
	}
	
	/**
	 * Loads a sound to the audio's data structure.
	 * @param file to load.
	 * @return sound buffer.
	 */
	public static int loadSound(String file) {
		int buffer = alGenBuffers();
		m_buffers.add(buffer);
		WaveData waveFile = WaveData.create(file);
		alBufferData(buffer, waveFile.format, waveFile.data, waveFile.samplerate);
		waveFile.dispose();
		return buffer;
	}
	
	/**
	 * Cleans up everything when closes.
	 */
	public static void cleanUp() {
		for(int buffer : m_buffers)
			alDeleteBuffers(buffer);
		destroy();
	}
	
}
