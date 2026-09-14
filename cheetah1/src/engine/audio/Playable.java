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

/**
 * The one piece of Source's behavior SourcePool actually needs. Pulled
 * out as its own interface so SourcePool can be unit-tested against a
 * fake implementation, without a real OpenAL context (which doesn't
 * exist in a plain test JVM).
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public interface Playable {

	/**
	 * Gets the playing state.
	 * @return playing state.
	 */
	boolean isPlaying();

}
