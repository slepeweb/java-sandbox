package com.slepeweb.money;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class Util {
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100.0);
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SOLR_SDF = new SimpleDateFormat("yyyy-MM-dd'T00:02:00Z'");
	public static final SimpleDateFormat MONTH_SDF = new SimpleDateFormat("MMMM");
	
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
		long value = pence != null ? pence.longValue() : 0L;
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
	
	public static String formatSimple(LocalDate d) {
		return formatSimple(Date.valueOf(d));
	}
	
	public static String format4Solr(LocalDate d) {
		return format4Solr(Date.valueOf(d));
	}
	
	public static String formatMonth(LocalDate d) {
		return formatMonth(Date.valueOf(d));
	}
	
	public static String formatSimple(Date d) {
		return format(d, SDF);
	}
	
	public static String format4Solr(Date d) {
		return format(d, SOLR_SDF);
	}
	
	public static String formatMonth(Date d) {
		return format(d, MONTH_SDF);
	}
	
	public static String format(Date d, SimpleDateFormat sdf) {
		if (d != null) {
			return sdf.format(d);
		}
		return "";
	}
	
	public static Date parseSimpleDate(String s) {
		return parseDate(s, SDF);
	}
	
	public static Date parseSolrDate(String s) {
		return parseDate(s, SOLR_SDF);
	}
	
	public static Date parseDate(String s, SimpleDateFormat sdf) {
		try {
			return new Date(sdf.parse(s).getTime());
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	public static Date todayAsDate() {
		return new Date(System.currentTimeMillis());
	}
	
	public static LocalDate today() {
		return todayAsDate().toLocalDate();
	}
	
	public static LocalDate startOfYear(LocalDate d) {
		return d.withDayOfYear(1);
	}
	
	public static LocalDate endOfYear(LocalDate d) {
		return d.withMonth(12).withDayOfMonth(31);
	}
	
	/**
	 * Date @to must always be non-null, as must @t.
	 * Date @from can be null.
	 */
	public static boolean isWithinTimeWindow(LocalDate from, LocalDate t, LocalDate to) {
		
		if (to == null) {
			// Usage error
			return false;
		}
		
		if (from == null) {
			// No 'from' date
			return t.isBefore(to) || t.isEqual(to);
		}

		// Both 'from' and 'to' dates provided
		return (t.isAfter(from) && t.isBefore(to)) || t.isEqual(from) || t.isEqual(to);
	}
	
	public static int monthsDifference(LocalDate from, LocalDate to) {
		int monthA = from.getMonthValue();
		int yearA = from.getYear();
		int monthB = to.getMonthValue();
		int yearB = to.getYear();
		
		return (yearB - yearA) * 12 + (monthB - monthA) + 1;
	}

	public static void main(String[] args) {
		LocalDate today = Util.today();
		LocalDate start = Util.startOfYear(today);
		LocalDate end = Util.endOfYear(today);
		LocalDate leftOut = start.minusDays(1);
		LocalDate rightOut = end.plusDays(1);
		LocalDate leftBoundary = start.minusDays(0);
		LocalDate rightBoundary = end.plusDays(0);
		
		DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		out("start", start.format(f));
		out("target", today.format(f));
		out("end", end.format(f));
		
		out("today", String.valueOf(Util.isWithinTimeWindow(start, today, end)));
		out("leftOut", String.valueOf(Util.isWithinTimeWindow(start, leftOut, end)));
		out("rightOut", String.valueOf(Util.isWithinTimeWindow(start, rightOut, end)));
		out("leftBoundary", String.valueOf(Util.isWithinTimeWindow(start, leftBoundary, end)));
		out("rightBoundary", String.valueOf(Util.isWithinTimeWindow(start, rightBoundary, end)));

		out("today", String.valueOf(Util.monthsDifference(start, today)));
		out("less12months", String.valueOf(Util.monthsDifference(start.plusYears(-1), today)));
}

	public static void out(String label, ZonedDateTime zdt) {
		out(label, zdt.toString());
	}
	
	public static void out(String label, String value) {
		out(String.format("%1$12s: %2$s", label, value));
	}
	
	public static void out(String s) {
		System.out.println(s);
	}
}