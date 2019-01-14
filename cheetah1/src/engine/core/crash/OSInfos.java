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
package engine.core.crash;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2019
 */
public class OSInfos implements CrashInfos {

	/**
	 * Get the information about the operating system.
	 */
	@Override
	public String getInfos() {
		String osName = System.getProperty("os.name");
		String osVersion = System.getProperty("os.version");
		return SECTION_START + " Operating System " + SECTION_END + "\n\tName: " + osName + "\n\tVersion: " + osVersion;
	}

}
