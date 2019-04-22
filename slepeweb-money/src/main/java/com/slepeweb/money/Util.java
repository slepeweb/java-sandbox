package com.slepeweb.money;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.money.bean.chart.ChartCategory;
import com.slepeweb.money.bean.chart.ChartCategoryGroup;
import com.slepeweb.money.bean.chart.ChartProperties;

public class Util {
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100.0);
	public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat SOLR_SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public static long decimal2long(BigDecimal d) {
		return  d != null ? d.multiply(ONE_HUNDRED).longValue() : -1L;
	}
	
	public static String formatPounds(long pence) {
		return String.format("%,.2f", pence / 100.0F);
	}
	
	public static long parsePounds(String value) {
		String val = value.trim();		
		if (StringUtils.isNotBlank(val)) {
			long multiplier = val.startsWith("-") ? -1L : 1L;

			String[] parts = val.split("\\.");
			if (parts.length == 1) {
				return Long.valueOf(cleanAmount(parts[0]));
			}
			else if (parts.length == 2){
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
	
	public static Timestamp today(Calendar c) {
		return new Timestamp(c.getTimeInMillis());
	}
	
	public static Timestamp now() {
		return today(Calendar.getInstance());
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
	
	public static Long toLong(String s) {
		if (StringUtils.isNumeric(s)) {
			return Long.valueOf(s);
		}
		
		return 0L;
	}
	
	public static String buildChartPropertyMarkup(ChartProperties props, String outerTemplate, String innerTemplate, 
			String categoryOptionsTemplate) {
		
		StringBuilder allGroups = new StringBuilder();
		StringBuilder optionsForGroup = new StringBuilder();
		StringBuilder innerBuilder;
		String outer, inner;
		int groupId = 0, optionsId;
		
		for (ChartCategoryGroup group : props.getGroups()) {
			groupId++;
			outer = outerTemplate.
					replaceAll("\\[groupId\\]", String.valueOf(groupId)).
					replace("[label]", group.getLabel());
			inner = "";
			innerBuilder = new StringBuilder();
			optionsId = 0;
			
			for (ChartCategory cc : group.getCategories()) {
				optionsId++;
				inner = innerTemplate.
						replaceAll("\\[groupId\\]", String.valueOf(groupId)).
						replaceAll("\\[counter\\]", String.valueOf(optionsId)).
						replace("[major]", cc.getMajor());
				
				optionsForGroup = new StringBuilder();
				
				for (String minor : cc.getOptions()) {
					optionsForGroup.append(categoryOptionsTemplate.
							replace("[minor]", minor).
							replace("[selected]", minor.equals(cc.getMinor()) ? "selected" : "" ));
				}
				
				inner = inner.replace("__categoryOptionsTemplate__", optionsForGroup.toString());
				innerBuilder.append(inner);
			}
			
			outer = outer.replace("__innerTemplate__", innerBuilder.toString());
			allGroups.append(outer);
		}
		
		return allGroups.toString();
		
	}
}
