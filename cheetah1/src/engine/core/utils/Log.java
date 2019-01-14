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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import engine.core.CoreEngine;
import engine.core.Debug;
import engine.core.Time;
import engine.core.crash.CrashReport;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.0
 * @since 2019
 */
public class Log {

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface NonLoggable
	{
	}

	public static boolean	   	showCaller = true;

	private static Formatter	formatter  = ((message) -> "[" + CoreEngine.getCurrent().getGame().getName() + " " + Time.getTimeAsString() + (showCaller ? " in " + getCaller() : "") + "] " + message);
	private static OutputStream savingOutput;

	public static boolean	   	save	   = false;
	private static File		 	outputFile;

	static
	{
	}

	/**
	 * Prints a message.
	 * @param msg to print
	 */
	@NonLoggable
	public static void message(String msg) {
		message(msg, true);
	}

	/**
	 * Gets their caller.
	 * @return source
	 */
	@NonLoggable
	private static String getCaller() {
		StackTraceElement[] elems = Thread.currentThread().getStackTrace();
		if(elems != null) {
			elementsIteration: for(int i = 1; i < elems.length; i++ ) {
				StackTraceElement elem = elems[i];
				if(elem.getClassName().contains(Log.class.getCanonicalName())) continue;
				try {
					Class<?> c = Class.forName(elem.getClassName());
					if(c.isAnnotationPresent(NonLoggable.class)) continue;
					for(Method method : c.getDeclaredMethods()) {
						if(method.getName().equals(elem.getMethodName())) if(method.isAnnotationPresent(NonLoggable.class)) continue elementsIteration;
					}
					String s = elem.getClassName() + "." + elem.getMethodName() + ":" + elem.getLineNumber();
					return s;
				} catch(ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return "Unknown source";
	}

	/**
	 * Prints a message with a format.
	 * @param msg to print
	 * @param format to set
	 */
	@NonLoggable
	public static void message(String msg, boolean format) {
		log("[INFO] " + msg, System.out, format);
	}

	/**
	 * Prints an error message.
	 * @param msg to print
	 */
	@NonLoggable
	public static void error(String msg) {
		error(msg, true);
	}

	/**
	 * Prints an error message with a format.
	 * @param msg to print
	 * @param format to set
	 */
	@NonLoggable
	public static void error(String msg, boolean format) {
		log("[ERROR] " + msg, System.err, format);
	}

	/**
	 * Process some info to get printed with it's format
	 * @param msg to print
	 * @param out output
	 * @param format to set
	 */
	@NonLoggable
	private static void log(String msg, PrintStream out, boolean format) {
		String finalMessage = msg;
		if(format) finalMessage = formatter.format(msg);
		if(save) {
			try {
				if(outputFile == null || !outputFile.exists() || savingOutput == null) {
					savingOutput = new BufferedOutputStream(new FileOutputStream(outputFile));
				}
				savingOutput.write((finalMessage + "\n").getBytes());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		out.println(finalMessage);
	}

	/**
	 * Sets a save output to it's safe output.
	 * @param output to set
	 */
	public static void setSaveOutput(OutputStream output) { Log.savingOutput = output; }

	/**
	 * Finish everything.
	 * @throws Exception
	 */
	public static void finish() throws Exception {
		if(save) {
			savingOutput.flush();
			savingOutput.close();
		}
	}

	/**
	 * Throws a fatal crash report.
	 * @param string message
	 */
	@NonLoggable
	public static void fatal(String string) {
		Debug.crash(new CrashReport(string));
	}
}
