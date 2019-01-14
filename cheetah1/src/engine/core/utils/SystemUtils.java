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

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2019
 */
public class SystemUtils {

	/**
	 * Returns the operating system thats compiling.
	 * @return operating system
	 */
	public static OperatingSystem getOS() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win"))
			return OperatingSystem.WINDOWS;
		if(os.contains("sunos") || os.contains("solaris"))
			return OperatingSystem.SOLARIS;
		if(os.contains("unix"))
			return OperatingSystem.LINUX;
		if(os.contains("linux"))
			return OperatingSystem.LINUX;
		if(os.contains("mac"))
			return OperatingSystem.MACOSX;
		return OperatingSystem.UNKNOWN;
	}

	/**
	 * Returns the machine's name thats it's compiling.
	 * @return machine's name
	 */
	public static String getUserName() { return System.getProperty("user.name"); }
}
