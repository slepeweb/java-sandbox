package com.slepeweb.site.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TldFunction {

	public static String formatUKDate(Date d, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("en-GB"));
		return sdf.format(d);
	}
	
	public static void main(String[] args) {
		System.out.println(formatUKDate(new Date(), "MMMM d, HH:mm z"));
	}
}
