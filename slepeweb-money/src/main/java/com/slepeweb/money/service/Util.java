package com.slepeweb.money.service;

import java.math.BigDecimal;

public class Util {
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100.0);
	
	public static long decimal2long(BigDecimal d) {
		return d.multiply(ONE_HUNDRED).longValue();
	}
	
	public static String formatPounds(long pence) {
		return String.format("£%.2f", pence / 100.0F);
	}
}
