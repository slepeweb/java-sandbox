package com.slepeweb.money.service;

import java.math.BigDecimal;

public class Util {
	public static long decimal2long(BigDecimal d) {
		return Float.valueOf(d.floatValue() * 100).longValue();
	}
	
	public static String formatPounds(long pence) {
		return String.format("%s£%.2f", pence < 0 ? "-" : "", pence / 100.0F);
	}
}
