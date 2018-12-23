package com.slepeweb.money;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100.0);
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	
	public static long decimal2long(BigDecimal d) {
		return d.multiply(ONE_HUNDRED).longValue();
	}
	
	public static String formatPounds(long pence) {
		return String.format("%,.2f", pence / 100.0F);
	}
	
	public static String formatTimestamp(Date d) {
		if (d != null) {
			return SDF.format(d);
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
	
	public static Calendar today() {
		Calendar c = Calendar.getInstance();
		zeroTimeOfDay(c);
		return c;
	}
	
	public static Timestamp today(Calendar c) {
		return new Timestamp(c.getTimeInMillis());
	}
	
	public static void zeroTimeOfDay(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 10);
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
	
	public static boolean isPositive(String str) {
		if (str != null) {
			String s = str.trim();
			return 
					s.equalsIgnoreCase("yes") ||
					s.equalsIgnoreCase("true") ||
					s.equalsIgnoreCase("1");
		}
		
		return false;
	}
}
