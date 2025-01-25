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
	
	public static String formatPounds(Long pence) {
		return pence != null ? String.format("%,.2f", pence / 100.0F) : "";
	}
	
	public static long parsePounds(String value) {
		String val = value.trim();		
		if (StringUtils.isNotBlank(val)) {
			long multiplier = val.startsWith("-") ? -1L : 1L;

			String[] parts = val.split("\\.");
			if (parts.length == 1) {
				return Long.valueOf(cleanAmount(parts[0])).longValue() * 100;
			}
			else if (parts.length == 2){
				if (parts[1].length() == 1) {
					parts[1] = parts[1] + "0";
				}
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
	
	/*
	public static String buildChartCategoryInputMarkup(ChartProperties props, String outerTemplate, String innerTemplate, 
			String categoryOptionsTemplate) {
		
		StringBuilder allGroups = new StringBuilder();
		StringBuilder optionsForGroup = new StringBuilder();
		StringBuilder innerBuilder;
		String outer, inner;
		int groupId = 0, optionsId;
		
		for (CategoryInputGroup group : props.getGroups()) {
			groupId++;
			outer = outerTemplate.<SearchCategoryInput>
					replaceAll("\\[groupId\\]", String.valueOf(groupId)).
					replace("[label]", group.getLabel());
			inner = "";
			innerBuilder = new StringBuilder();
			optionsId = 0;
			
			for (SearchCategoryInput cc : group.getCategories()) {
				optionsId++;
				inner = innerTemplate.
						replaceAll("\\[groupId\\]", String.valueOf(groupId)).
						replaceAll("\\[counter\\]", String.valueOf(optionsId)).
						replace("[major]", cc.getMajor()).
						replace("[exclude-selected]", cc.isExclude() ? "selected" : "").
						replace("[include-selected]", ! cc.isExclude() ? "selected" : "");
				
				optionsForGroup = new StringBuilder();
				
				for (String minor : cc.getOptions()) {
					optionsForGroup.append(categoryOptionsTemplate.
							replace("[minor]", minor).
							replace("[selected]", minor.equals(cc.getMinor()) ? "selected" : "" ));
				}
				
				inner = inner.replace("__categoryOptionsTemplate__", optionsForGroup.toString());
				innerBuilder.append(inner);
			}
			
			outer = outer.replace("__innerTemplate__", 
					buildMinorCategoryInputMarkup(group, innerTemplate, categoryOptionsTemplate));
			
			allGroups.append(outer);
		}
		
		return allGroups.toString();
		
	}
	
	public static String buildMinorCategoryInputMarkup(CategoryInputGroup group, 
			String innerTemplate, String categoryOptionsTemplate) {
		
		StringBuilder minorCategoryOptions = new StringBuilder();
		StringBuilder innerBuilder = new StringBuilder();
		String inner;
		int optionsId = 0;
		
		for (CategoryInput cc : group.getCategories()) {
			optionsId++;
			inner = innerTemplate.
					replaceAll("\\[groupId\\]", String.valueOf(group.getId())).
					replaceAll("\\[counter\\]", String.valueOf(optionsId)).
					replace("[major]", cc.getMajor()).
					replace("[exclude-selected]", cc.isExclude() ? "selected" : "").
					replace("[include-selected]", ! cc.isExclude() ? "selected" : "");
			
			minorCategoryOptions = new StringBuilder();
			
			for (String minor : cc.getOptions()) {
				minorCategoryOptions.append(categoryOptionsTemplate.
						replace("[minor]", minor).
						replace("[selected]", minor.equals(cc.getMinor()) ? "selected" : "" ));
			}
			
			inner = inner.replace("__categoryOptionsTemplate__", minorCategoryOptions.toString());
			innerBuilder.append(inner);
		}
		
		return innerBuilder.toString();		
	}
	*/
	public static String compactMarkup(String in) {
		return in.replaceAll("\\n", "").replaceAll("\\r", "");
	}

	/*
	public static String buildSplitInputMarkup(List<SplitTransactionFormComponent> splits, 
			String innerTemplate, String splitOptionsTemplate) {
		
		StringBuilder splitOptions = new StringBuilder();
		StringBuilder innerBuilder = new StringBuilder();
		String inner;
		int optionsId = 0;
		
		for (SplitTransactionFormComponent comp : splits) {
			optionsId++;
			inner = innerTemplate.
					replaceAll("\\[counter\\]", String.valueOf(optionsId)).
					replace("[major]", comp.getCategory().getMajor());
			
			splitOptions = new StringBuilder();
			
			for (String minor : comp.getAllMinors()) {
				splitOptions.append(splitOptionsTemplate.
						replace("[minor]", minor).
						replace("[selected]", minor.equals(comp.getCategory().getMinor()) ? "selected" : "" ));
			}
			
			inner = inner.
					replace("__splitOptionsTemplate__", splitOptions.toString()).
					replace("[memo]", comp.getMemo() == null ? "" : comp.getMemo()).*/
					
					/* Split amounts are always displayed as positive values, but are evaluated
					 * according to whether the parent transaction is a debit or credit. *//*
					replace("[amount]", formatPositivePounds(comp.getAmount()));
			
			innerBuilder.append(inner);
		}
		
		return innerBuilder.toString();		
	}
	*/
	
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
