/*
 * Copyright 2019 Carlos Rodriguez.
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
package engine.core.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import engine.rendering.Window;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2019
 */
public class LWJGLHandler {

	private static boolean 		loaded;
	
	private static int[]	 	screenshotBufferArray;
	private static IntBuffer 	screenshotBuffer;

	/**
	 * Loads the natives from some folder.
	 * @throws Exception
	 */
	public static void load(File folder) throws Exception {
		if(!loaded) {
			if(!folder.exists()) folder.mkdirs();
			if(folder.isDirectory()) {
				installNatives(folder);
				System.setProperty("org.lwjgl.librarypath", folder.getAbsolutePath());
			}
			loaded = true;
		}
	}

	/**
	 * Install the natives from some folder and sets them.
	 * @param folder of the natives
	 * @throws Exception
	 */
	private static void installNatives(File folder) throws Exception {
		OperatingSystem os = SystemUtils.getOS();
		Log.message("OS found : " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
		Log.message("Installing natives...");
		if(os == OperatingSystem.WINDOWS) {
			if(!new File(folder.getPath() + "/jinput-dx8_64.dll").exists()) {
				extractFromClasspath("/windows/jinput-dx8_64.dll", folder);
				extractFromClasspath("/windows/jinput-dx8.dll", folder);
				extractFromClasspath("/windows/jinput-raw_64.dll", folder);
				extractFromClasspath("/windows/jinput-raw.dll", folder);
				extractFromClasspath("/windows/lwjgl.dll", folder);
				extractFromClasspath("/windows/lwjgl64.dll", folder);
				extractFromClasspath("/windows/OpenAL32.dll", folder);
				extractFromClasspath("/windows/OpenAL64.dll", folder);
			} else {
				Log.message("Natives already exist.");
			}
		} else if(os == OperatingSystem.SOLARIS) {
			if(!new File(folder.getPath() + "/liblwjgl.so").exists()) {
				extractFromClasspath("/solaris/liblwjgl.so", folder);
				extractFromClasspath("/solaris/liblwjgl64.so", folder);
				extractFromClasspath("/solaris/libopenal.so", folder);
				extractFromClasspath("/solaris/libopenal64.so", folder);
			} else {
				Log.message("Natives already exist.");
			}

		} else if(os == OperatingSystem.LINUX) {
			if(!new File(folder.getPath() + "/liblwjgl.so").exists()) {
				extractFromClasspath("/linux/liblwjgl.so", folder);
				extractFromClasspath("/linux/liblwjgl64.so", folder);
				extractFromClasspath("/linux/libopenal.so", folder);
				extractFromClasspath("/linux/libopenal64.so", folder);
			} else {
				Log.message("Natives already exist.");
			}

		} else if(os == OperatingSystem.MACOSX) {
			if(!new File(folder.getPath() + "/openal.dylib").exists()) {
				extractFromClasspath("/macosx/liblwjgl.jnilib", folder);
				extractFromClasspath("/macosx/liblwjgl-osx.jnilib", folder);
				extractFromClasspath("/macosx/openal.dylib", folder);
			} else {
				Log.message("Natives already exist.");
			}
		} else {
			Log.fatal("Unknown OS: " + System.getProperty("os.name"));
		}
		System.setProperty("net.java.games.input.librarypath", folder.getAbsolutePath());
	}

	/**
	 * Extracts the names from the class' path
	 * @param fileName of the native
	 * @param folder of the natives
	 */
	private static void extractFromClasspath(String fileName, File folder) {
		String[] split = fileName.split("/");
		String diskFileName = split[split.length - 1];
		try(FileOutputStream in = new FileOutputStream(new File(folder, diskFileName))) {
			IO.copy(LWJGLHandler.class.getResourceAsStream(fileName), in);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes a screenshot of what's on screen.
	 * @return image raw data
	 */
	public static BufferedImage takeScreenshot() {
		int k = Window.getWidth() * Window.getHeight();
		if(screenshotBuffer == null || screenshotBuffer.capacity() < k) {
			screenshotBuffer = BufferUtils.createIntBuffer(k);
			screenshotBufferArray = new int[k];
		}
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		screenshotBuffer.clear();
		GL11.glReadPixels(0, 0, Window.getWidth(), Window.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, screenshotBuffer);
		screenshotBuffer.get(screenshotBufferArray);
		int[] aint1 = new int[Window.getWidth()];
		int j = Window.getHeight() / 2;
		for(int l = 0; l < j; ++l) {
			System.arraycopy(screenshotBufferArray, l * Window.getWidth(), aint1, 0, Window.getWidth());
			System.arraycopy(screenshotBufferArray, (Window.getHeight() - 1 - l) * Window.getWidth(), screenshotBufferArray, l * Window.getWidth(), Window.getWidth());
			System.arraycopy(aint1, 0, screenshotBufferArray, (Window.getHeight() - 1 - l) * Window.getWidth(), Window.getWidth());
		}
		BufferedImage bufferedimage = new BufferedImage(Window.getWidth(), Window.getHeight(), BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < screenshotBufferArray.length; i++ ) {
			Color c = new Color(((screenshotBufferArray[i] >> 16) & 0xFF) / 255f, ((screenshotBufferArray[i] >> 8) & 0xFF) / 255f, ((screenshotBufferArray[i] >> 0) & 0xFF) / 255f, 1f);
			screenshotBufferArray[i] = new Color(c.getBlue(), c.getGreen(), c.getRed()).getRGB();
		}
		bufferedimage.setRGB(0, 0, Window.getWidth(), Window.getHeight(), screenshotBufferArray, 0, Window.getWidth());
		return bufferedimage;
	}
}
