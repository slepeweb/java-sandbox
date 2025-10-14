package com.slepeweb.money;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class Util {
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100.0);
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SOLR_SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public static long decimal2long(BigDecimal d) {
		return  d != null ? d.multiply(ONE_HUNDRED).longValue() : -1L;
	}
	
	public static String formatPositivePounds(Long pence) {
		return pence != null ? formatPounds(pence < 0 ? -pence : pence) : "";
	}
	
	/*
	 * Tried using String.format for this, but errors arose on large floating point numbers, ie lots
	 * of significant places, eg. £212,345.72
	 */
	public static String formatPounds(Long pence) {
		long value = pence.longValue();
		boolean debit = value < 0;
		String sign = debit ? "-" : "";
		long abs = debit ? -value : value;
		String str = String.valueOf(abs);
		
		if (value == 0) {
			return "0.00";
		}
		else if (abs < 10) {
			return sign + "0.0" + str;
		}
		else if (abs < 100) {
			return sign + "0." + str;
		}
		else if (abs < 100000) {
			return sign + 
					str.substring(0, str.length() - 2) + "." + 
					str.substring(str.length() - 2);
		}
		else if (abs < 100000000) {
			return sign + 
					str.substring(0, str.length() - 5) + "," + 
					str.substring(str.length() - 5, str.length() - 2) + "." + 
					str.substring(str.length() - 2);
		}
		else {
			return sign + 
					str.substring(0, str.length() - 8) + "," + 
					str.substring(str.length() - 8, str.length() - 5) + "," + 
					str.substring(str.length() - 5, str.length() - 2) + "." + 
					str.substring(str.length() - 2);
		}
	}
	
	public static long parsePounds(String value) {
		String val = value.trim();		
		if (StringUtils.isNotBlank(val)) {
			long multiplier = val.startsWith("-") ? -1L : 1L;

			String[] parts = val.split("\\.");
			
			// Cater for pence value like '24'
			if (parts.length == 1) {
				return Long.valueOf(cleanAmount(parts[0])).longValue() * 100;
			}
			else if (parts.length == 2) {
				// Cater for figure like '.24'
				if (parts[0].length() == 0) {
					parts[0] = "0";
				}
				
				// Cater for value like '2.4'
				if (parts[1].length() == 1) {
					parts[1] = parts[1] + "0";
				}
				// Ignore fractions of pennies
				else if (parts[1].length() > 2) {
					parts[1] = parts[1].substring(0, 2);
				}
				
				return ((Long.valueOf(cleanAmount(parts[0])).longValue() * 100) + 
						(multiplier * (Long.valueOf(parts[1])).longValue()));
			}
		}
		return 0L;
	}
	
	private static String cleanAmount(String s) {
		return s.replaceAll(",", "");
	}
	
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
		c.set(Calendar.MILLISECOND, 0);
	}
	
	public static Timestamp zeroTimeOfDay(Timestamp ts) {
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		zeroTimeOfDay(c);
		return new Timestamp(c.getTimeInMillis());
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
	
	public static boolean isPositive(String str) {
		if (str != null) {
			String s = str.trim();
			return 
					s.equalsIgnoreCase("yes") ||
					s.equalsIgnoreCase("true") ||
					s.equalsIgnoreCase("on") ||
					s.equalsIgnoreCase("1");
		}
		
		return false;
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
	
	public static String renderDefaultIfBlank(Object preferred, Object dflt) {
		if (preferred != null && StringUtils.isNotBlank(preferred.toString())) {
			return StringEscapeUtils.escapeHtml4(preferred.toString());
		}
		return StringEscapeUtils.escapeHtml4(dflt.toString());
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
	
	public static float toPounds(long pence) {
		return pence / 100;
	}
	
	public static String compactMarkup(String in) {
		return in.replaceAll("\\n", "").replaceAll("\\r", "");
	}

	public static String displayAmountNS(long amount) {
		return displayAmount(amount, false);
	}
	
	public static String displayAmountWS(long amount) {
		return displayAmount(amount, true);
	}
	
	private static String displayAmount(long amount, boolean prependSymbol) {
		if (amount < 0) {
			return String.format(
					"<span class=\"debit-amount\">%s%s</span>", 
					prependSymbol ? "&pound;" : "", 
					formatPounds(amount));
		}
		else {
			return String.format(
					"%s%s", 
					prependSymbol ? "&pound;" : "", 
					formatPounds(amount));
		}
	}
	
}
