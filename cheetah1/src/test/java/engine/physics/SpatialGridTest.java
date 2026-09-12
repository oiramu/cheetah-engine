package engine.physics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

import engine.core.Transform;
import engine.core.Vector2f;
import engine.core.Vector3f;

/**
 * Characterization tests for the broad-phase contract SpatialGrid must
 * hold: queryAabb() must never miss a collidable a naive brute-force scan
 * would find (false negatives would silently break collision/line-of-sight
 * checks). Extra candidates are fine and expected - callers re-check them
 * with an exact narrow-phase test (PhysicsUtil.rectCollide) anyway.
 */
class SpatialGridTest {

	private static final class FakeCollidable implements Collidable {
		private final Transform transform;
		private final Vector2f size;

		FakeCollidable(float x, float z, Vector2f size) {
			this.transform = new Transform(new Vector3f(x, 0, z));
			this.size = size;
		}

		@Override
		public Transform getTransform() {return transform;}

		@Override
		public Vector2f getSize() {return size;}
	}

	private static boolean overlaps(Collidable c, Vector2f min, Vector2f max) {
		Vector2f pos = c.getPosition2D();
		Vector2f size = c.getSize();

		return pos.getX() <= max.getX() && pos.getX() + size.getX() >= min.getX()
				&& pos.getY() <= max.getY() && pos.getY() + size.getY() >= min.getY();
	}

	private static Set<Collidable> bruteForceQuery(List<Collidable> all, Vector2f min, Vector2f max) {
		Set<Collidable> result = new HashSet<Collidable>();
		for(Collidable c : all)
			if(overlaps(c, min, max))
				result.add(c);
		return result;
	}

	@Test
	void queryNeverMissesWhatBruteForceWouldFind() {
		Random rand = new Random(42);
		SpatialGrid grid = new SpatialGrid(16, 16, 1f);
		List<Collidable> all = new ArrayList<Collidable>();

		for(int i = 0; i < 60; i++) {
			float x = rand.nextFloat() * 16;
			float z = rand.nextFloat() * 16;
			Vector2f size = new Vector2f(0.2f + rand.nextFloat() * 1.5f, 0.2f + rand.nextFloat() * 1.5f);
			FakeCollidable c = new FakeCollidable(x, z, size);
			all.add(c);
			if(i % 2 == 0)
				grid.insertStatic(c);
			else
				grid.insertDynamic(c);
		}

		for(int q = 0; q < 25; q++) {
			float x1 = rand.nextFloat() * 16;
			float z1 = rand.nextFloat() * 16;
			Vector2f min = new Vector2f(x1, z1);
			Vector2f max = new Vector2f(x1 + rand.nextFloat() * 3, z1 + rand.nextFloat() * 3);

			Set<Collidable> expected = bruteForceQuery(all, min, max);
			Set<Collidable> actual = grid.queryAabb(min, max);

			assertTrue(actual.containsAll(expected),
					"grid missed a true overlap for query [" + min + ", " + max + "]");
		}
	}

	@Test
	void clearDynamicRemovesOnlyDynamicCollidables() {
		SpatialGrid grid = new SpatialGrid(4, 4, 1f);
		FakeCollidable staticOne = new FakeCollidable(1, 1, new Vector2f(0.5f, 0.5f));
		FakeCollidable dynamicOne = new FakeCollidable(1, 1, new Vector2f(0.5f, 0.5f));

		grid.insertStatic(staticOne);
		grid.insertDynamic(dynamicOne);
		grid.clearDynamic();

		Set<Collidable> found = grid.queryAabb(new Vector2f(0, 0), new Vector2f(4, 4));

		assertEquals(new HashSet<Collidable>(Collections.singletonList(staticOne)), found);
	}

	@Test
	void removeStaticDropsItFromFutureQueries() {
		SpatialGrid grid = new SpatialGrid(4, 4, 1f);
		FakeCollidable barrel = new FakeCollidable(2, 2, new Vector2f(0.5f, 0.5f));

		grid.insertStatic(barrel);
		grid.removeStatic(barrel);

		Set<Collidable> found = grid.queryAabb(new Vector2f(0, 0), new Vector2f(4, 4));

		assertEquals(0, found.size());
	}
}
