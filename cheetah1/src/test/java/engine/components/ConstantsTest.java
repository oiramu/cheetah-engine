package engine.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ConstantsTest {

	@Test
	void saveRoundTripsAllTrackedFields(@TempDir Path tempDir) throws IOException {
		Constants.load("res/config.txt");

		String expectedTextureFilter = Constants.TEXTURE_FILTER;
		String expectedGameGraphics = Constants.GAME_GRAPHICS;
		boolean expectedClearLights = Constants.CLEAR_LIGHTS;
		float expectedMipmapLevel = Constants.MIPMAP_LEVEL;
		float expectedAnisotropicLevel = Constants.ANISOTROPIC_LEVEL;
		float expectedPopIn = Constants.POP_IN;
		float expectedParticlesPopIn = Constants.PARTICLES_POP_IN;
		float expectedGrassPopIn = Constants.GRASS_POP_IN;
		float expectedLightPopIn = Constants.LIGHT_POP_IN;
		float expectedGravity = Constants.GRAVITY;
		short expectedParticlesLevel = Constants.PARTICLES_LEVEL;
		int expectedEffectsAudioLevel = Constants.EFFECTS_AUDIO_LEVEL;
		boolean expectedLightRangeCulling = Constants.LIGHT_RANGE_CULLING;
		boolean expectedFrustumCulling = Constants.FRUSTUM_CULLING;
		float expectedLodMidDistance = Constants.LOD_MID_DISTANCE;
		float expectedLodFarDistance = Constants.LOD_FAR_DISTANCE;
		int expectedLodFarTickSkip = Constants.LOD_FAR_TICK_SKIP;
		int expectedMaxAudioSources = Constants.MAX_AUDIO_SOURCES;
		int expectedTargetWidth = Constants.TARGET_WIDTH;
		int expectedTargetHeight = Constants.TARGET_HEIGHT;
		boolean expectedFullscreen = Constants.FULLSCREEN;

		Path copy = tempDir.resolve("config.txt");
		Files.copy(Paths.get("res/config.txt"), copy, StandardCopyOption.REPLACE_EXISTING);
		Constants.save(copy.toString());

		//Reset every field so re-loading is the only thing that can make the assertions below pass
		Constants.TEXTURE_FILTER = null;
		Constants.GAME_GRAPHICS = null;
		Constants.CLEAR_LIGHTS = false;
		Constants.MIPMAP_LEVEL = 0;
		Constants.ANISOTROPIC_LEVEL = 0;
		Constants.POP_IN = 0;
		Constants.PARTICLES_POP_IN = 0;
		Constants.GRASS_POP_IN = 0;
		Constants.LIGHT_POP_IN = 0;
		Constants.GRAVITY = 0;
		Constants.PARTICLES_LEVEL = 0;
		Constants.EFFECTS_AUDIO_LEVEL = 0;
		Constants.LIGHT_RANGE_CULLING = false;
		Constants.FRUSTUM_CULLING = false;
		Constants.LOD_MID_DISTANCE = 0;
		Constants.LOD_FAR_DISTANCE = 0;
		Constants.LOD_FAR_TICK_SKIP = 0;
		Constants.MAX_AUDIO_SOURCES = 0;
		Constants.TARGET_WIDTH = 0;
		Constants.TARGET_HEIGHT = 0;
		Constants.FULLSCREEN = false;

		Constants.load(copy.toString());

		assertEquals(expectedTextureFilter, Constants.TEXTURE_FILTER);
		assertEquals(expectedGameGraphics, Constants.GAME_GRAPHICS);
		assertEquals(expectedClearLights, Constants.CLEAR_LIGHTS);
		assertEquals(expectedMipmapLevel, Constants.MIPMAP_LEVEL);
		assertEquals(expectedAnisotropicLevel, Constants.ANISOTROPIC_LEVEL);
		assertEquals(expectedPopIn, Constants.POP_IN);
		assertEquals(expectedParticlesPopIn, Constants.PARTICLES_POP_IN);
		assertEquals(expectedGrassPopIn, Constants.GRASS_POP_IN);
		assertEquals(expectedLightPopIn, Constants.LIGHT_POP_IN);
		assertEquals(expectedGravity, Constants.GRAVITY);
		assertEquals(expectedParticlesLevel, Constants.PARTICLES_LEVEL);
		assertEquals(expectedEffectsAudioLevel, Constants.EFFECTS_AUDIO_LEVEL,
			"EFFECTS_AUDIO_LEVEL's -20 load() offset must be reversed by save()");
		assertEquals(expectedLightRangeCulling, Constants.LIGHT_RANGE_CULLING);
		assertEquals(expectedFrustumCulling, Constants.FRUSTUM_CULLING);
		assertEquals(expectedLodMidDistance, Constants.LOD_MID_DISTANCE);
		assertEquals(expectedLodFarDistance, Constants.LOD_FAR_DISTANCE);
		assertEquals(expectedLodFarTickSkip, Constants.LOD_FAR_TICK_SKIP);
		assertEquals(expectedMaxAudioSources, Constants.MAX_AUDIO_SOURCES);
		assertEquals(expectedTargetWidth, Constants.TARGET_WIDTH);
		assertEquals(expectedTargetHeight, Constants.TARGET_HEIGHT);
		assertEquals(expectedFullscreen, Constants.FULLSCREEN);
	}

	@Test
	void savePreservesCommentsAndUntrackedLines(@TempDir Path tempDir) throws IOException {
		Constants.load("res/config.txt");

		Path copy = tempDir.resolve("config.txt");
		Files.copy(Paths.get("res/config.txt"), copy, StandardCopyOption.REPLACE_EXISTING);
		Constants.save(copy.toString());

		List<String> savedLines = Files.readAllLines(copy);
		assertTrue(savedLines.contains("#Cheetah Engine configuration file"),
			"Header comment should survive save() verbatim");
		assertTrue(savedLines.contains("RESOURCE_PACK pack=auschwitz"),
			"Untracked key RESOURCE_PACK should survive save() verbatim, since Constants has no backing field for it");
	}

	@Test
	void saveWritesAFreshFileWhenNoneExists(@TempDir Path tempDir) throws IOException {
		Constants.load("res/config.txt");
		Path missing = tempDir.resolve("new-config.txt");

		Constants.save(missing.toString());

		assertTrue(Files.exists(missing));
		assertTrue(Files.readAllLines(missing).stream().anyMatch(line -> line.startsWith("TEXTURE_FILTER")));
	}
}
