package engine.menu.system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import engine.components.Constants;

class SSettingsBridgeTest {

	@Test
	void appliesStringFieldsDirectly() {
		SSettingsBridge.apply("TEXTURE_FILTER", "Bilinear");
		assertEquals("Bilinear", Constants.TEXTURE_FILTER);
		assertEquals("Bilinear", SSettingsBridge.currentValue("TEXTURE_FILTER"));
	}

	@Test
	void appliesNumericFields() {
		SSettingsBridge.apply("ANISOTROPIC_LEVEL", "8");
		assertEquals(8f, Constants.ANISOTROPIC_LEVEL);

		SSettingsBridge.apply("MAX_AUDIO_SOURCES", "32");
		assertEquals(32, Constants.MAX_AUDIO_SOURCES);
		assertEquals("32", SSettingsBridge.currentValue("MAX_AUDIO_SOURCES"));
	}

	@Test
	void appliesBooleanFields() {
		SSettingsBridge.apply("FULLSCREEN", "False");
		assertEquals(false, Constants.FULLSCREEN);
		assertEquals("false", SSettingsBridge.currentValue("FULLSCREEN"));
	}

	@Test
	void effectsAudioLevelReversesTheLoadTimeOffsetAtTheBoundary() {
		//Constants.load() stores EFFECTS_AUDIO_LEVEL with a -20 offset (see Constants.java);
		//the settings menu should always deal in the same numbers config.txt does.
		SSettingsBridge.apply("EFFECTS_AUDIO_LEVEL", "40");
		assertEquals(20, Constants.EFFECTS_AUDIO_LEVEL, "Field itself stays offset internally");
		assertEquals("40", SSettingsBridge.currentValue("EFFECTS_AUDIO_LEVEL"), "Display value reverses the offset");
	}

	@Test
	void unknownKeyIsIgnoredByApplyAndReturnsEmptyFromCurrentValue() {
		Constants.TEXTURE_FILTER = "Nearest";
		SSettingsBridge.apply("NOT_A_REAL_KEY", "whatever");
		assertEquals("Nearest", Constants.TEXTURE_FILTER);
		assertEquals("", SSettingsBridge.currentValue("NOT_A_REAL_KEY"));
	}

}
