package com.slepeweb.money.service;

import java.math.BigDecimal;
import java.sql.Timestamp;

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
}
