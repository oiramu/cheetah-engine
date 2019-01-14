/*
 * Copyright 2017 Carlos Rodriguez.
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
package engine.core;

import java.util.Calendar;

/**
 *
 * @author Carlos Rodriguez
 * @version 1.2
 * @since 2017
 */
public class Time {

	private static String[] days   = new String[7];
	private static String[] months = new String[12];

	private static Calendar calendar;
    
    static
	{
		calendar = Calendar.getInstance();
		days[Calendar.MONDAY - 1] = "Monday";
		days[Calendar.TUESDAY - 1] = "Tuesday";
		days[Calendar.WEDNESDAY - 1] = "Wednesday";
		days[Calendar.THURSDAY - 1] = "Thursday";
		days[Calendar.FRIDAY - 1] = "Friday";
		days[Calendar.SATURDAY - 1] = "Saturday";
		days[Calendar.SUNDAY - 1] = "Sunday";

		months[Calendar.JANUARY] = "January";
		months[Calendar.FEBRUARY] = "February";
		months[Calendar.MARCH] = "March";
		months[Calendar.APRIL] = "April";
		months[Calendar.MAY] = "May";
		months[Calendar.JUNE] = "June";
		months[Calendar.JULY] = "July";
		months[Calendar.AUGUST] = "August";
		months[Calendar.SEPTEMBER] = "September";
		months[Calendar.OCTOBER] = "October";
		months[Calendar.NOVEMBER] = "November";
		months[Calendar.DECEMBER] = "December";
	}
    
    /**
     * Returns the actual date as a text.
     * @return Date in text
     */
    public static String getTimeAsText() {
		String second = "" + calendar.get(Calendar.SECOND);
		String minute = "" + calendar.get(Calendar.MINUTE);
		String hour = "" + calendar.get(Calendar.HOUR_OF_DAY);
		String day = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
		String month = months[calendar.get(Calendar.MONTH)];
		String year = "" + calendar.get(Calendar.YEAR);

		if(second.length() == 1) second = "0" + second;

		if(minute.length() == 1) minute = "0" + minute;

		if(hour.length() == 1) hour = "0" + hour;

		return month + ", " + day + " " + year + " at " + hour + "h" + minute + " and " + second + "seconds";
	}

    /**
     * Returns the actual date as a string.
     * @return Date in string
     */
	public static String getTimeAsString() {
		String second = "" + calendar.get(Calendar.SECOND);
		String minute = "" + calendar.get(Calendar.MINUTE);
		String hour = "" + calendar.get(Calendar.HOUR_OF_DAY);
		String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
		String month = "" + (calendar.get(Calendar.MONTH) + 1);
		String year = "" + calendar.get(Calendar.YEAR);

		if(second.length() == 1) second = "0" + second;

		if(minute.length() == 1) minute = "0" + minute;

		if(hour.length() == 1) hour = "0" + hour;

		if(month.length() == 1) month = "0" + month;
		return year + "/" + month + "/" + day + " @ " + hour + ":" + minute + ":" + second;
	}

    /**
     * Gets the time of compiling.
     * @return Program's time.
     */
    public static double getTime() { return (double) System.nanoTime() / (double) 1000000000L; }

}
