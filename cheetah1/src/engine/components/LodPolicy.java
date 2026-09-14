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
package engine.components;

/**
 * Decides how often a distance-tiered entity should run its full update
 * (AI/animation/collision) logic - NEAR every tick, MID every 2nd tick,
 * FAR every Constants.LOD_FAR_TICK_SKIP-th tick. Centralized here so the
 * eventual shared Enemy base class only needs to relocate one call site
 * per enemy instead of re-deriving this logic for each of them.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class LodPolicy {

	private LodPolicy() {}

	/**
	 * Gets the distance tier for a given distance to the player.
	 * @param distance to the player.
	 * @return the tier.
	 */
	public static LodTier tierFor(float distance) {
		if(distance < Constants.LOD_MID_DISTANCE) return LodTier.NEAR;
		if(distance < Constants.LOD_FAR_DISTANCE) return LodTier.MID;
		return LodTier.FAR;
	}

	/**
	 * Gets if an entity at the given tier should run its full update
	 * logic on the given tick.
	 * @param tier of the entity.
	 * @param frameCount the current tick count (Time.getFrameCount()).
	 * @return tick state.
	 */
	public static boolean shouldTick(LodTier tier, long frameCount) {
		switch(tier) {
			case NEAR: return true;
			case MID: return frameCount % 2 == 0;
			case FAR: return frameCount % Math.max(1, Constants.LOD_FAR_TICK_SKIP) == 0;
			default: return true;
		}
	}

}
