package com.slepeweb.money.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;

public class Util {
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100.0);
	
	public static long decimal2long(BigDecimal d) {
		return d.multiply(ONE_HUNDRED).longValue();
	}
	
	public static String formatPounds(long pence) {
		return String.format("%.2f", pence / 100.0F);
	}
	
	public static String formatTimestamp(Timestamp t) {
		if (t != null) {
			return String.format("%1$tY-%1$tm-%1$td", t);
		}
		return "";
	}
	
	public static Calendar today() {
		Calendar c = Calendar.getInstance();
		zeroTimeOfDay(c);
		return c;
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
}
