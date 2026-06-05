package com.slepeweb.money;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import org.springframework.stereotype.Component;

@Component
public class TimeUtil {
	public final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	public final SimpleDateFormat SOLR_SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public String formatSimple(java.util.Date d) {
		if (d != null) {
			return SDF.format(d);
		}
		return "";
	}
	
	public String formatSolr(java.sql.Date d) {
		return formatSolr(new java.util.Date(d.getTime()));
	}
	
	public String formatSolr(java.util.Date d) {
		if (d != null) {
			ZonedDateTime zdt = d.toInstant().atZone(ZoneId.systemDefault());
			if (! zdt.getOffset().equals(ZoneOffset.UTC)) {
				// We're in summer time - bump argument by 1 hour
				//zdt = zdt.plusHours(1L);
			}

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00Z'");
			return zdt.format(formatter);
		}
		return "";
	}
	
	public Timestamp parseSimple(String s) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(SDF.parse(s));
			zeroTimeOfDay(c);
			return new Timestamp(c.getTimeInMillis());
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	public Date parseSqlDate(String s) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(SDF.parse(s));
			return new Date(c.getTimeInMillis());
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	public Date parseSolrDate(String s) {
		try {
			return new java.sql.Date(SOLR_SDF.parse(s).getTime());
		} 
		catch (Exception e) {
			return null;
		}
	}
	
	public Calendar today() {
		Calendar c = Calendar.getInstance();
		zeroTimeOfDay(c);
		return c;
	}
	
	public java.sql.Date todaySQ() {
		return new java.sql.Date(System.currentTimeMillis());
	}
	
	public Timestamp toTimestamp(Calendar c) {
		return new Timestamp(c.getTimeInMillis());
	}
	
	public Timestamp now() {
		return toTimestamp(Calendar.getInstance());
	}
	
	public void zeroTimeOfDay(Calendar c) {
		/*
		 * Well, actually 2am.
		 * 2am BST is 1am UTC
		 * MySQL stores Timestamps in UTC, but select SQL statments return the values 
		 * according to the current timezone of the server. By setting times to 2am, we
		 * avoid '00' hours rolling back to '23' when we're not in BST.
		 */
		c.set(Calendar.HOUR_OF_DAY, 2);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
	}
	
	public Timestamp zeroTimeOfDay(Timestamp ts) {
		Calendar c = Calendar.getInstance();
		c.setTime(ts);
		zeroTimeOfDay(c);
		return new Timestamp(c.getTimeInMillis());
	}
	
	public void startOfYear(Calendar c) {
		c.set(Calendar.DATE, 1);
		c.set(Calendar.MONTH, 0);
		zeroTimeOfDay(c);
	}
	
	public void endOfYear(Calendar c) {
		c.set(Calendar.DATE, 31);
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 10);
	}
	
	public int getYear(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.YEAR);
	}
	
	/**
	 * Timestamp @to must always be non-null, as must @t.
	 * Timestamp @from can be null.
	 */
	public boolean isWithinTimeWindow(Timestamp from, Timestamp t, Timestamp to) {
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
	
	public int monthsDifference(Timestamp from, Timestamp to) {
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
	
	public static void main(String[] args) {
		TimeUtil u = new TimeUtil();
		java.sql.Date d = u.todaySQ();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		out(d.toString());
		out(d.toLocalDate().format(formatter));
		/*
		String f1 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		String f2 = "yyyy-MM-dd'T00:00:00Z'";
		String f3 = "yyyy-MM-dd'T'HH:mm:ssO";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(f3);
		
		// Just before clocks spring forward (last Sunday of March, 1:00 AM → 2:00 AM)
		ZoneId london = ZoneId.of("Europe/London");
		ZonedDateTime beforeDST = ZonedDateTime.of(2026, 3, 29, 0, 0, 1, 0, london);
		ZonedDateTime afterDST  = beforeDST.plusDays(1); // Correctly jumps to 02:00 BST
		out("beforeDST", beforeDST);
		out("beforeDST", beforeDST.format(formatter));
		out("afterDST", afterDST);
		out("afterDST", afterDST.format(formatter));
		*/

		//System.out.println(beforeDST.getOffset()); // +00:00
		//System.out.println(afterDST.getOffset());  // +01:00

		//zdt.format(formatter);

		/*
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/London"));
		ZonedDateTime gmt = now.minusMonths(4);
		ZonedDateTime utc = gmt.withZoneSameInstant(ZoneOffset.UTC);
		out("now", u.format(now));
		out("feb", gmt);
		out("utc", utc);
		out(gmt.getOffset().equals(utc.getOffset()) ? "Equals" : "NOT equals");
		*/
		
		/*
		 * If offset == 1 hour, then solr dates nust be advanced by 1 hour, so that
		 * when solr stores the value in UTC, the time component is 00:00:00
		 */

		/*
		// Adding and subtracting
		ZonedDateTime nextWeek   = zdt.plusWeeks(1); out("nextWeek", nextWeek);
		ZonedDateTime yesterday  = zdt.minusDays(1); out("yesterday", yesterday);
		ZonedDateTime inTwoHours = zdt.plusHours(2); out("inTwoHours", inTwoHours);

		// Adjusting specific fields
		ZonedDateTime startOfDay = zdt.withHour(0).withMinute(0).withSecond(0).withNano(0); out("startOfDay", startOfDay);
		ZonedDateTime firstOfMonth = zdt.withDayOfMonth(1); out("firstOfMonth", firstOfMonth);

		// Using TemporalAdjusters
		ZonedDateTime nextMonday = zdt.with(TemporalAdjusters.next(DayOfWeek.MONDAY)); out("nextMonday", nextMonday);
		*/
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
