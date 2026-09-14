package engine.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class SourcePoolTest {

	private static final class FakeSource implements Playable {
		boolean playing;

		@Override
		public boolean isPlaying() {return playing;}
	}

	@Test
	void obtainReturnsAFreeSourceWhenOneExists() {
		FakeSource busy = new FakeSource();
		busy.playing = true;
		FakeSource free = new FakeSource();
		free.playing = false;

		SourcePool<FakeSource> pool = new SourcePool<FakeSource>(List.of(busy, free));

		assertSame(free, pool.obtain());
	}

	@Test
	void obtainRecyclesLeastRecentlyObtainedWhenAllBusy() {
		FakeSource a = new FakeSource();
		a.playing = true;
		FakeSource b = new FakeSource();
		b.playing = true;
		FakeSource c = new FakeSource();
		c.playing = true;

		SourcePool<FakeSource> pool = new SourcePool<FakeSource>(List.of(a, b, c));

		// All busy: obtain() must fall back to the least-recently-obtained
		// source rather than refusing or crashing.
		FakeSource first = pool.obtain();
		assertSame(a, first);

		// a was just moved to the end (most-recently-obtained), so the
		// next recycle should pick b, not a again.
		FakeSource second = pool.obtain();
		assertSame(b, second);
		assertNotSame(first, second);
	}

	@Test
	void obtainDoesNotMutateTheCallersOriginalList() {
		FakeSource a = new FakeSource();
		List<FakeSource> original = new ArrayList<FakeSource>();
		original.add(a);

		SourcePool<FakeSource> pool = new SourcePool<FakeSource>(original);
		pool.obtain();

		assertEquals(1, original.size());
	}
}
