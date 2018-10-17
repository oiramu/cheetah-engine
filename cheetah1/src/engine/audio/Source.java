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

import engine.core.Vector3f;

import static org.lwjgl.openal.AL10.*;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2018
 */
public class Source {
	
	private int m_sourceId;
	
	/**
	 * Constructor for a sound source.
	 */
	public Source() {
		m_sourceId = alGenSources();
		alSourcef(m_sourceId, AL_ROLLOFF_FACTOR, 1);
		alSourcef(m_sourceId, AL_REFERENCE_DISTANCE, 6);
		alSourcef(m_sourceId, AL_MAX_DISTANCE, 25);
	}
	
	/**
	 * Buffer to play by the sound source.
	 * @param buffer to play
	 */
	public void play(int buffer) {
		stop();
		alSourcei(m_sourceId, AL_BUFFER, buffer);
		continuePlaying();
	}
	
	/**
	 * Sets the volume of the sound source.
	 * @param volume to set
	 */
	public void setVolume(int volume) { alSourcef(m_sourceId, AL_GAIN, volume); }
	
	/**
	 * Sets the pitch of the sound source.
	 * @param pitch to set
	 */
	public void setPitch(int pitch) { alSourcef(m_sourceId, AL_PITCH, pitch); }
	
	/**
	 * Sets the position of the sound source.
	 * @param position to set
	 */
	public void setPosition(Vector3f position) {
		alSource3f(m_sourceId, AL_POSITION, position.getX(), position.getY(), position.getZ());
	}
	
	/**
	 * Sets the velocity of the sound source.
	 * @param velocity to set
	 */
	public void setVelocity(Vector3f velocity) {
		alSource3f(m_sourceId, AL_VELOCITY, velocity.getX(), velocity.getY(), velocity.getZ());
	}
	
	/**
	 * Sets the looping of the sound source if it should.
	 * @param loop to set
	 */
	public void setLooping(boolean loop) {
		alSourcei(m_sourceId, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);
	}
	
	/**
	 * Returns the playing state of the sound source.
	 * @return playing state
	 */
	public boolean isPlaying() {
		return alGetBufferi(m_sourceId, AL_SOURCE_STATE) == AL_PLAYING;
	}
	
	/**
	 * Deletes the source when it needed.
	 */
	public void delete() { stop(); alDeleteSources(m_sourceId); }
	
	/**
	 * Pauses the sound playing.
	 */
	public void pause() { alSourcePause(m_sourceId); }
	
	/**
	 * Continues the sound that is been paused.
	 */
	public void continuePlaying() { alSourcePlay(m_sourceId); }
	
	/**
	 * Stops the sound that is been played.
	 */
	public void stop() { alSourceStop(m_sourceId); }
	
}
