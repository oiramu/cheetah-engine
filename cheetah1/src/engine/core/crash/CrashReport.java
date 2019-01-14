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

import engine.core.CoreEngine;
import engine.core.Debug;
import engine.core.utils.Log;
import engine.core.utils.SystemUtils;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2019
 */
public class CrashReport {

	private static String[] comments = new String[] {
			null,
			null,
			"Well, this was a disappointment.",
			"I'm sorry " + SystemUtils.getUserName() + ". I think I can't let you do that",
			"Here, have a gift http://xkcd.com/953/ "};

	static {
		try {
			comments[0] = new String(":P");
			comments[1] = new String("x_x");
		} catch(Exception e) {
			Debug.crash(new CrashReport(e));
		}
	}

	/**
	 *
	 * @author Carlos Rodriguez
	 * @version 1.0
	 * @since 2019
	 */
	public static class UndefinedException extends Exception {

		/**
		 * Constructor of an undefined exception with a message.
		 * @param message of the exception
		 */
		public UndefinedException(String message) {
			super(message);
		}

		private static final long serialVersionUID = 3352250643266742630L;
	}

	private Throwable exception;

	/**
	 * Constructor of a crash report, with a report message.
	 * @param message to report
	 */
	public CrashReport(String message) {
		this(new UndefinedException(message).fillInStackTrace());
	}

	/**
	 * Constructor of a crash report, with the throwable exception.
	 * @param throwable exception
	 */
	public CrashReport(Throwable throwable) { this.exception = throwable; }

	/**
	 * Prints the stack to trace.
	 */
	public void printStack() {
		if(!Log.save) Log.save = true; // Force output
		StringBuffer buffer = new StringBuffer();
		buffer.append(CrashInfos.SECTION_START + " Crash " + CrashInfos.SECTION_END + "\n");
		String comment = generateRandomComment();
		buffer.append(comment + "\n");
		buffer.append("\n" + exception.getClass().getCanonicalName());
		StackTraceElement[] stackTrace = exception.getStackTrace();
		if(exception.getLocalizedMessage() != null) {
			buffer.append(" reason: " + exception.getLocalizedMessage());
		} else if(exception.getMessage() != null) buffer.append(" reason: " + exception.getMessage());
		buffer.append("\n");
		if(stackTrace != null && stackTrace.length > 0) {
			for(StackTraceElement elem : stackTrace) {
				buffer.append("\tat " + elem.toString() + "\n");
			}
		} else {
			buffer.append("\t**** Stack Trace is empty ****");
		}
		add(buffer, () -> CrashInfos.SECTION_START + " Game " + CrashInfos.SECTION_END + "\n\tName: " + CoreEngine.getCurrent().getGame().getName());
		add(buffer, new DateInfos());
		add(buffer, new OSInfos());
		//add(buffer, new OpenALInfos());
		add(buffer, new OpenGLInfos());
		System.err.println(buffer.toString());
	}

	/**
	 * Adds a string to other string.
	 * @param buffer to add
	 * @param infos to set
	 */
	private void add(StringBuffer buffer, CrashInfos infos) { buffer.append(infos.getInfos() + "\n"); }

	/**
	 * Generates a random message.
	 * @return random message
	 */
	private String generateRandomComment() { return comments[(int)Math.floor(Math.random() * comments.length)]; }

}
