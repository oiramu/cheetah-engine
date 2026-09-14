package engine.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LodPolicyTest {

	@BeforeEach
	void setConfig() {
		Constants.LOD_MID_DISTANCE = 10;
		Constants.LOD_FAR_DISTANCE = 20;
		Constants.LOD_FAR_TICK_SKIP = 4;
	}

	@Test
	void tierForClassifiesByDistance() {
		assertEquals(LodTier.NEAR, LodPolicy.tierFor(0));
		assertEquals(LodTier.NEAR, LodPolicy.tierFor(9.99f));
		assertEquals(LodTier.MID, LodPolicy.tierFor(10));
		assertEquals(LodTier.MID, LodPolicy.tierFor(19.99f));
		assertEquals(LodTier.FAR, LodPolicy.tierFor(20));
		assertEquals(LodTier.FAR, LodPolicy.tierFor(1000));
	}

	@Test
	void nearTierAlwaysTicks() {
		for(long frame = 0; frame < 10; frame++)
			assertTrue(LodPolicy.shouldTick(LodTier.NEAR, frame));
	}

	@Test
	void midTierTicksEveryOtherFrame() {
		assertTrue(LodPolicy.shouldTick(LodTier.MID, 0));
		assertFalse(LodPolicy.shouldTick(LodTier.MID, 1));
		assertTrue(LodPolicy.shouldTick(LodTier.MID, 2));
		assertFalse(LodPolicy.shouldTick(LodTier.MID, 3));
	}

	@Test
	void farTierTicksEveryConfiguredNthFrame() {
		assertTrue(LodPolicy.shouldTick(LodTier.FAR, 0));
		assertFalse(LodPolicy.shouldTick(LodTier.FAR, 1));
		assertFalse(LodPolicy.shouldTick(LodTier.FAR, 2));
		assertFalse(LodPolicy.shouldTick(LodTier.FAR, 3));
		assertTrue(LodPolicy.shouldTick(LodTier.FAR, 4));
	}

	@Test
	void farTierNeverDividesByZeroWhenMisconfigured() {
		Constants.LOD_FAR_TICK_SKIP = 0;

		assertTrue(LodPolicy.shouldTick(LodTier.FAR, 0));
	}
}
