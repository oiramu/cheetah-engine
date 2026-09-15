package game.save;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SaveGameManagerTest {

	private static final String TEST_SLOT = "unittest-slot";

	@AfterEach
	void cleanUp() {
		new File("saves", TEST_SLOT + ".cesave").delete();
	}

	@Test
	void savesAndLoadsThroughTheManagerApi() {
		assertFalse(SaveGameManager.exists(TEST_SLOT));

		SaveGame saveGame = new SaveGame();
		saveGame.levelNum = 3;
		saveGame.player.health = 55;

		assertTrue(SaveGameManager.save(TEST_SLOT, saveGame));
		assertTrue(SaveGameManager.exists(TEST_SLOT));

		SaveGame reloaded = SaveGameManager.load(TEST_SLOT);
		assertEquals(3, reloaded.levelNum);
		assertEquals(55, reloaded.player.health);
	}

	@Test
	void loadReturnsNullForAnUnknownSlot() {
		assertNull(SaveGameManager.load("no-such-slot-should-ever-exist"));
	}

}
