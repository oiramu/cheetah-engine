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

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2019
 */
public final class IO {

	/**
	 * Copy a input stream and returns it to other stream
	 * with some data.
	 * @param is input stream
	 * @param os output stream
	 * @return output stream
	 * @throws IOException exception
	 */
	public static OutputStream copy(InputStream is, OutputStream os) throws IOException {
		int i = 0;
		byte[] buffer = new byte[65565];
		while((i = is.read(buffer, 0, buffer.length)) != -1)
		{
			os.write(buffer, 0, i);
		}
		return os;
	}

	/**
	 * Copy a input stream and returns it to a string with 
	 * some data.
	 * @param in input stream
	 * @param output string
	 * @return output stream
	 * @throws FileNotFoundException exception
	 * @throws IOException exception
	 */
	public static OutputStream copy(InputStream in, String output) throws FileNotFoundException, IOException {
		return copy(in, new BufferedOutputStream(new FileOutputStream(output)));
	}

	/**
	 * Reads the character set of a string.
	 * @param in input stream
	 * @param charset string
	 * @return new string read in char set
	 * @throws UnsupportedEncodingException exception
	 * @throws IOException exception
	 */
	public static String readString(InputStream in, String charset) throws UnsupportedEncodingException, IOException {
		return new String(read(in), charset);
	}

	/**
	 * Reads some input stream and returns it's bytes on
	 * an array.
	 * @param in input stream
	 * @return byte array
	 * @throws IOException exception
	 */
	public static byte[] read(InputStream in) throws IOException {
		byte[] buffer = new byte[65565];
		ByteArrayOutputStream ous = new ByteArrayOutputStream(buffer.length);
		int i = 0;
		while((i = in.read(buffer, 0, buffer.length)) != -1)
		{
			ous.write(buffer, 0, i);
		}
		ous.close();
		return ous.toByteArray();
	}

	/**
	 * Deletes some folder content.
	 * @param folder of content.
	 */
	public static void deleteFolderContents(File folder) {
		File[] files = folder.listFiles();
		if(files != null) for(File f : files) {
			if(f.isDirectory()) {
				deleteFolderContents(f);
				f.delete();
			} else
				f.delete();
		}
	}

}
