package com.slepeweb.common.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SOLR_SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public static String formatTimestamp(Date d) {
		if (d != null) {
			return SDF.format(d);
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
			return new Timestamp(SDF.parse(s).getTime());
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
	
}
