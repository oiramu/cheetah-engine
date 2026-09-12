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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import engine.core.Vector2f;

/**
 * Uniform grid over the level's tile space, used as a broad-phase spatial
 * index so collision/line-of-sight queries only have to examine collidables
 * near the query box instead of every collidable in the level.
 *
 * Static collidables (walls, props) are inserted once and stay until
 * explicitly removed; dynamic collidables (enemies, doors) are meant to be
 * cleared and re-inserted every frame.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class SpatialGrid {

	private final int 					width;
	private final int 					height;
	private final float 				cellSize;

	private final List<Collidable>[] 	staticCells;
	private final List<Collidable>[] 	dynamicCells;

	/**
	 * Constructor of the spatial grid.
	 * @param width of the grid, in cells.
	 * @param height of the grid, in cells.
	 * @param cellSize of each cell, in world units.
	 */
	@SuppressWarnings("unchecked")
	public SpatialGrid(int width, int height, float cellSize) {
		this.width = width;
		this.height = height;
		this.cellSize = cellSize;
		staticCells = new List[width * height];
		dynamicCells = new List[width * height];
	}

	/**
	 * Inserts a collidable that never moves - stays in the grid until
	 * removeStatic() is called for it.
	 * @param c to insert.
	 */
	public void insertStatic(Collidable c) {insert(staticCells, c);}

	/**
	 * Inserts a collidable that moves - meant to be cleared every frame
	 * with clearDynamic() and re-inserted.
	 * @param c to insert.
	 */
	public void insertDynamic(Collidable c) {insert(dynamicCells, c);}

	/**
	 * Removes a static collidable, e.g. a destroyed barrel.
	 * @param c to remove.
	 */
	public void removeStatic(Collidable c) {
		for(int index : cellIndices(c))
			if(staticCells[index] != null)
				staticCells[index].remove(c);
	}

	/**
	 * Removes every static collidable on the given list.
	 * @param list to remove.
	 */
	public void removeAllStatic(List<? extends Collidable> list) {
		for(Collidable c : list)
			removeStatic(c);
	}

	/**
	 * Clears every dynamic collidable, ready for this frame's re-insertion.
	 */
	public void clearDynamic() {
		for(List<Collidable> cell : dynamicCells)
			if(cell != null)
				cell.clear();
	}

	/**
	 * Returns every collidable (static and dynamic) whose own box overlaps
	 * a cell within the given world-space AABB, with no duplicates.
	 * @param min corner of the query box.
	 * @param max corner of the query box.
	 * @return matching collidables.
	 */
	public Set<Collidable> queryAabb(Vector2f min, Vector2f max) {
		Set<Collidable> result = new LinkedHashSet<Collidable>();

		int minCx = clamp(cellCoord(Math.min(min.getX(), max.getX())), width);
		int maxCx = clamp(cellCoord(Math.max(min.getX(), max.getX())), width);
		int minCy = clamp(cellCoord(Math.min(min.getY(), max.getY())), height);
		int maxCy = clamp(cellCoord(Math.max(min.getY(), max.getY())), height);

		for(int cy = minCy; cy <= maxCy; cy++) {
			for(int cx = minCx; cx <= maxCx; cx++) {
				int index = cy * width + cx;
				addAll(result, staticCells[index]);
				addAll(result, dynamicCells[index]);
			}
		}
		return result;
	}

	/**
	 * Inserts a collidable into every cell its own box overlaps.
	 * @param cells to insert into.
	 * @param c to insert.
	 */
	private void insert(List<Collidable>[] cells, Collidable c) {
		for(int index : cellIndices(c)) {
			if(cells[index] == null)
				cells[index] = new ArrayList<Collidable>();
			cells[index].add(c);
		}
	}

	/**
	 * Every cell index the collidable's own box (position to
	 * position+size) overlaps, so a query from any of those cells finds it.
	 * @param c to index.
	 * @return the overlapping cell indices.
	 */
	private int[] cellIndices(Collidable c) {
		Vector2f pos = c.getPosition2D();
		Vector2f size = c.getSize();

		int minCx = clamp(cellCoord(pos.getX()), width);
		int maxCx = clamp(cellCoord(pos.getX() + size.getX()), width);
		int minCy = clamp(cellCoord(pos.getY()), height);
		int maxCy = clamp(cellCoord(pos.getY() + size.getY()), height);

		int[] indices = new int[(maxCx - minCx + 1) * (maxCy - minCy + 1)];
		int n = 0;
		for(int cy = minCy; cy <= maxCy; cy++)
			for(int cx = minCx; cx <= maxCx; cx++)
				indices[n++] = cy * width + cx;
		return indices;
	}

	/**
	 * Converts a world-space coordinate to a cell coordinate.
	 * @param worldCoord to convert.
	 * @return the cell coordinate.
	 */
	private int cellCoord(float worldCoord) {return (int) Math.floor(worldCoord / cellSize);}

	/**
	 * Clamps a cell coordinate to a valid index along one axis.
	 * @param value to clamp.
	 * @param size of that axis, in cells.
	 * @return the clamped value.
	 */
	private static int clamp(int value, int size) {return Math.max(0, Math.min(size - 1, value));}

	/**
	 * Adds every collidable in a cell to the result set, if the cell exists.
	 * @param result to add to.
	 * @param cell to read from.
	 */
	private static void addAll(Set<Collidable> result, List<Collidable> cell) {
		if(cell != null) result.addAll(cell);
	}

}
