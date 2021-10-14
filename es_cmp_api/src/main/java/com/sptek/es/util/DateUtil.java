package com.sptek.es.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	public static String convertToCurrentTimeZone(String Date) {
		String converted_date = "";
		try {

			DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			Date date = utcFormat.parse(Date);

			DateFormat currentTFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			currentTFormat.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()));

			converted_date = currentTFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return converted_date;
	}

	//get the current time zone
	public static String getCurrentTimeZone() {
		TimeZone tz = Calendar.getInstance().getTimeZone();
		//System.out.println(tz.getDisplayName());
		return tz.getID();
	}
}
