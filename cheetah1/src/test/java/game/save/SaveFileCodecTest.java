package game.save;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SaveFileCodecTest {

	@Test
	void roundTripsAPopulatedSave(@TempDir Path tempDir) throws IOException {
		SaveGame original = new SaveGame();
		original.levelNum = 4;
		original.player.posX = 12.5f;
		original.player.posZ = 30.25f;
		original.player.health = 80;
		original.player.maxHealth = 100;
		original.player.weaponState = "shotgun";
		original.player.hasShotgun = true;
		original.levelDeltas.deadEnemies.put("naziSoldiers", Arrays.asList(0, 3, 7));
		original.levelDeltas.deadEnemies.put("dogs", Collections.<Integer>emptyList());
		original.levelDeltas.removedPickups.put("medkits", Arrays.asList(0, 2));
		original.levelDeltas.openDoors = Arrays.asList(0, 4, 5);

		File file = tempDir.resolve("slot.cesave").toFile();
		SaveFileCodec.write(original, file);
		SaveGame reloaded = SaveFileCodec.read(file);

		assertEquals(original.levelNum, reloaded.levelNum);
		assertEquals(original.player.posX, reloaded.player.posX);
		assertEquals(original.player.health, reloaded.player.health);
		assertEquals(original.player.weaponState, reloaded.player.weaponState);
		assertEquals(original.player.hasShotgun, reloaded.player.hasShotgun);
		assertEquals(original.levelDeltas.deadEnemies, reloaded.levelDeltas.deadEnemies);
		assertEquals(original.levelDeltas.removedPickups, reloaded.levelDeltas.removedPickups);
		assertEquals(original.levelDeltas.openDoors, reloaded.levelDeltas.openDoors);
	}

	@Test
	void writesCiphertextRatherThanPlainJson(@TempDir Path tempDir) throws IOException {
		SaveGame saveGame = new SaveGame();
		saveGame.levelNum = 7;
		File file = tempDir.resolve("slot.cesave").toFile();

		SaveFileCodec.write(saveGame, file);

		String raw = new String(Files.readAllBytes(file.toPath()), StandardCharsets.ISO_8859_1);
		assertFalse(raw.contains("levelNum"), "Save file bytes should be encrypted, not plain JSON");
		assertFalse(raw.contains("schemaVersion"), "Save file bytes should be encrypted, not plain JSON");
	}

	@Test
	void tamperedFileFailsClosedInsteadOfLoadingCorruptData(@TempDir Path tempDir) throws IOException {
		SaveGame saveGame = new SaveGame();
		saveGame.levelNum = 7;
		File file = tempDir.resolve("slot.cesave").toFile();
		SaveFileCodec.write(saveGame, file);

		byte[] bytes = Files.readAllBytes(file.toPath());
		bytes[bytes.length - 1] ^= 0xFF; //Flip a byte inside the GCM auth tag
		Files.write(file.toPath(), bytes);

		assertNull(SaveFileCodec.read(file), "A hand-edited save should fail closed, not load silently-wrong data");
	}

	@Test
	void readReturnsNullForAMissingFile(@TempDir Path tempDir) {
		File missing = tempDir.resolve("does-not-exist.cesave").toFile();
		assertNull(SaveFileCodec.read(missing));
	}

}
