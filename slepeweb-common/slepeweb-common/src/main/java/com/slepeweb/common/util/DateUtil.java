package com.slepeweb.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
	public static final SimpleDateFormat DATE_PATTERN_A = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat DATE_PATTERN_B = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat TIME_PATTERN = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat DATE_AND_TIME_PATTERN = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	public static final SimpleDateFormat SOLR_SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static final Pattern LOOSE_DATE_PATTERN = Pattern.compile("^.*?(\\d{1,2}/)?(\\d{1,2}/)?(\\d{4}).*$");
	
	public static String formatDate(Date d, String pattern) {
		if (d != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.format(d);
		}
		return "";
	}
	
	public static String formatTimestamp(Date d) {
		if (d != null) {
			return DATE_PATTERN_A.format(d);
		}
		return "";
	}
	
	public static String formatSolrDate(Date d) {
		if (d != null) {
			return SOLR_SDF.format(d);
		}
		return "";
	}
	
	public static Timestamp parseTimestamp(String s) {
		try {
			return new Timestamp(DATE_PATTERN_A.parse(s).getTime());
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	public static Date parseSolrDate(String s) {
		try {
			return SOLR_SDF.parse(s);
		} 
		catch (Exception e) {
			return null;
		}
	}
	
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
	
	public static Long nowInMillis() {
		return Calendar.getInstance().getTimeInMillis();
	}
	
	public static void zeroTimeOfDay(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 10);
	}
	
	public static void startOfYear(Calendar c) {
		c.set(Calendar.DATE, 1);
		c.set(Calendar.MONTH, 0);
		zeroTimeOfDay(c);
	}
	
	public static void endOfYear(Calendar c) {
		c.set(Calendar.DATE, 31);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 10);
	}
	
	public static int getYear(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * Timestamp @to must always be non-null, as must @t.
	 * Timestamp @from can be null.
	 */
	public static boolean isWithinTimeWindow(Timestamp from, Timestamp t, Timestamp to) {
		if (to == null) {
			return false;
		}
		else if (from != null) {
			return t.after(from) && t.before(to);
		}
		else {
			return t.before(to);
		}
	}
	
	public static int monthsDifference(Timestamp from, Timestamp to) {
		Calendar calA = Calendar.getInstance();
		calA.setTime(from);
		int monthA = calA.get(Calendar.MONTH);
		int yearA = calA.get(Calendar.YEAR);
		
		Calendar calB = Calendar.getInstance();
		calB.setTime(to);
		int monthB = calB.get(Calendar.MONTH);
		int yearB = calB.get(Calendar.YEAR);
		
		return (yearB - yearA) * 12 + (monthB - monthA) + 1;
	}
	
	public static Date parseLooseDateString(String str) {
		if (str == null) {
			return null;
		}
	
		Matcher m = LOOSE_DATE_PATTERN.matcher(str);
		if (m.matches()) {
			Calendar cal = DateUtil.today();
			cal.set(Calendar.DATE, getDatePart(m.group(1), 1));
			cal.set(Calendar.MONTH, getDatePart(m.group(2), 1) - 1);
			cal.set(Calendar.YEAR, getDatePart(m.group(3), 1970));
			return cal.getTime();
		}
		
		return null;
	}
	
	private static int getDatePart(String s, int dflt) {
		if (s == null) {
			return dflt;
		}
		
		if (s.endsWith("/")) {
			s = s.substring(0, s.length() - 1);
		}
		
		return Integer.parseInt(s);
	}

}
