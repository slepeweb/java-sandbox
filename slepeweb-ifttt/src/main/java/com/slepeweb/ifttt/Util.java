package com.slepeweb.ifttt;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

public class Util {
	public static Calendar today() {
		Calendar c = Calendar.getInstance();
		zeroTimeOfDay(c);
		return c;
	}
	
	public static Timestamp toTimestamp(Calendar c) {
		return new Timestamp(c.getTimeInMillis());
	}
	
	public static Timestamp now() {
		return toTimestamp(Calendar.getInstance());
	}
	
	public static void zeroTimeOfDay(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 10);
	}
	
	public static String encodeUrl(String s) {
		try {
			return URLEncoder.encode(s, "utf-8");
		}
		catch (UnsupportedEncodingException e) {}
		return s;
	}

	public static Object tertiaryOp(boolean test, Object trueResult, Object falseResult) {
		return test ? trueResult : falseResult;
	}
	
	public static Long toLong(String s) {
		return toLong(s, 0L);
	}
	
	public static Long toLong(String s, Long dflt) {
		if (StringUtils.isNumeric(s)) {
			return Long.valueOf(s);
		}
		
		return dflt;
	}
	
	public static Integer toInteger(String s) {
		return toInteger(s, 0);
	}
	
	public static Integer toInteger(String s, Integer dflt) {
		if (StringUtils.isNumeric(s)) {
			return Integer.valueOf(s);
		}
		
		return 0;
	}
	
}
