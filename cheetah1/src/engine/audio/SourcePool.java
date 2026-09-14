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

/**
 * Fixed-size pool of sound sources, so a burst of concurrent one-shot
 * sounds (e.g. two soldiers firing at once) don't have to share a single
 * source and cut each other off - the bug the old javax.sound.sampled
 * setup had, since every instance of an enemy type shared one static
 * Clip. When every source in the pool is already busy, the
 * least-recently-obtained one is recycled rather than growing the pool.
 *
 * Generic over Playable (not the concrete Source class) so it can be
 * unit-tested against a fake, without a real OpenAL context.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class SourcePool<T extends Playable> {

	private final List<T> sources;

	/**
	 * Constructor for the pool.
	 * @param sources to pool - copied, so the caller's own list is
	 * unaffected by the pool's internal reordering.
	 */
	public SourcePool(List<T> sources) {
		this.sources = new ArrayList<T>(sources);
	}

	/**
	 * Gets a source ready to play a new sound: a free (not currently
	 * playing) one if any exists, otherwise the least-recently-obtained
	 * source is recycled.
	 * @return a source.
	 */
	public T obtain() {
		for (T source : sources) {
			if (!source.isPlaying()) {
				moveToEnd(source);
				return source;
			}
		}

		T recycled = sources.get(0);
		moveToEnd(recycled);
		return recycled;
	}

	private void moveToEnd(T source) {
		sources.remove(source);
		sources.add(source);
	}

}
