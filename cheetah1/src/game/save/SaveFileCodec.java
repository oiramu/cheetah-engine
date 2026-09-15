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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import engine.core.utils.Log;

/**
 * Encrypts a SaveGame to JSON-then-AES/GCM bytes on write, and reverses that
 * on read - save files must not be hand-editable in a text editor, unlike
 * res/config.txt.
 *
 * The AES key is a fixed constant embedded below: there is no server and no
 * user-supplied passphrase in this design, so the game itself has to be able
 * to decrypt without prompting, which means the key has to live in the
 * binary. That deters casual editing (no text editor, no "just change health
 * to 999") but doesn't withstand a determined person decompiling the jar to
 * recover the constant - an inherent limit of any offline single-player
 * scheme with no external key source, not a gap in this class specifically.
 *
 * GCM's authentication tag is a deliberate bonus: a hand-edited byte makes
 * decryption fail loudly instead of silently loading corrupted data, so
 * read() treats that as an invalid/empty slot rather than crashing.
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2026
 */
public class SaveFileCodec {

	private static final String 	ALGORITHM 			= "AES/GCM/NoPadding";
	private static final int 		GCM_NONCE_BYTES 	= 12;
	private static final int 		GCM_TAG_BITS 		= 128;

	private static final byte[] KEY_BYTES = {
		(byte)0x4a,(byte)0x1f,(byte)0x8c,(byte)0x2e,(byte)0x77,(byte)0x9b,(byte)0x03,(byte)0xd6,
		(byte)0x5f,(byte)0xa4,(byte)0x21,(byte)0xe8,(byte)0x6c,(byte)0x39,(byte)0xb0,(byte)0x92
	};

	private static final Gson GSON = new GsonBuilder().create();

	private SaveFileCodec() {}

	/**
	 * Serializes a save to JSON, encrypts it, and writes it to file -
	 * creating the parent directory if needed.
	 * @param saveGame to write.
	 * @param file destination.
	 * @throws IOException if the file can't be written or encryption fails.
	 */
	public static void write(SaveGame saveGame, File file) throws IOException {
		byte[] plaintext = GSON.toJson(saveGame).getBytes(StandardCharsets.UTF_8);

		byte[] nonce = new byte[GCM_NONCE_BYTES];
		new SecureRandom().nextBytes(nonce);

		try {
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(GCM_TAG_BITS, nonce));
			byte[] ciphertext = cipher.doFinal(plaintext);

			File parent = file.getParentFile();
			if(parent != null) parent.mkdirs();

			byte[] out = new byte[nonce.length + ciphertext.length];
			System.arraycopy(nonce, 0, out, 0, nonce.length);
			System.arraycopy(ciphertext, 0, out, nonce.length, ciphertext.length);
			Files.write(file.toPath(), out);
		} catch (GeneralSecurityException e) {
			throw new IOException("Could not encrypt save file '" + file + "'", e);
		}
	}

	/**
	 * Reads and decrypts a save file. Returns null (after logging why)
	 * rather than throwing if the file is missing, corrupted, hand-edited,
	 * or has an invalid schema - an unreadable slot is treated as
	 * empty/invalid, not a crash.
	 * @param file to read.
	 * @return the decoded save, or null if it can't be read.
	 */
	public static SaveGame read(File file) {
		if(!file.exists()) return null;

		try {
			byte[] in = Files.readAllBytes(file.toPath());
			if(in.length < GCM_NONCE_BYTES) {
				Log.error("Save file '" + file + "' is too short to be valid");
				return null;
			}
			byte[] nonce = Arrays.copyOfRange(in, 0, GCM_NONCE_BYTES);
			byte[] ciphertext = Arrays.copyOfRange(in, GCM_NONCE_BYTES, in.length);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(GCM_TAG_BITS, nonce));
			byte[] plaintext = cipher.doFinal(ciphertext);

			return GSON.fromJson(new String(plaintext, StandardCharsets.UTF_8), SaveGame.class);
		} catch (AEADBadTagException e) {
			Log.error("Save file '" + file + "' failed its integrity check (corrupted or hand-edited)");
			return null;
		} catch (GeneralSecurityException | IOException e) {
			Log.error("Could not read save file '" + file + "': " + e.getMessage());
			return null;
		} catch (JsonSyntaxException e) {
			Log.error("Save file '" + file + "' decrypted but has an invalid schema: " + e.getMessage());
			return null;
		}
	}

	private static SecretKeySpec key() {return new SecretKeySpec(KEY_BYTES, "AES");}

}
