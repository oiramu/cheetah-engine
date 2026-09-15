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
package game.save;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Everything about a Level that has diverged from its bitmap-regenerated
 * baseline - never a full object dump, just what changed. Enemy/pickup
 * deltas are keyed by type name (e.g. "naziSoldiers", "medkits") rather than
 * one field per type, so new types don't need a schema change; the indices
 * in each list are spawn-order indices within that type's list, which is
 * only valid because Level.generateLevel()'s bitmap scan order is
 * deterministic.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class LevelDeltas {

	public Map<String, List<Integer>> 	deadEnemies 		= new HashMap<String, List<Integer>>();
	public Map<String, List<Integer>> 	removedPickups 		= new HashMap<String, List<Integer>>();

	public List<Integer> 				openDoors 			= new ArrayList<Integer>();
	public List<Integer> 				openLockedDoors 	= new ArrayList<Integer>();
	public List<Integer> 				openedSecretWalls 	= new ArrayList<Integer>();
	public List<Integer> 				poppedBarrels 		= new ArrayList<Integer>();

}
