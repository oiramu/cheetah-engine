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
package engine.physics;

import engine.core.Transform;
import engine.core.Vector2f;

/**
 * Something that can be placed in a {@link SpatialGrid} and tested for
 * collision/line-of-sight against - a door, a static prop, an enemy, etc.
 * Every implementer here already has a transform and a 2D size, so the
 * position is derived for free.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public interface Collidable {

	/**
	 * Gets the collidable's transform.
	 * @return the transform.
	 */
	Transform getTransform();

	/**
	 * Gets the collidable's 2D size.
	 * @return the size.
	 */
	Vector2f getSize();

	/**
	 * Gets the collidable's position in the XZ plane.
	 * @return the position.
	 */
	default Vector2f getPosition2D() {return getTransform().getPosition().getXZ();}

	/**
	 * Gets if this collidable currently blocks movement/line of sight.
	 * True for almost everything; overridden by entities that can be
	 * walked/shot through under some condition (e.g. a quiet enemy).
	 * @return blocking state.
	 */
	default boolean blocksMovement() {return true;}

}
