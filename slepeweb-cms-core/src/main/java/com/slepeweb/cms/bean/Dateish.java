package com.slepeweb.cms.bean;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class Dateish {
	public static final String REGEXP = "^(\\d{4})\\/(\\d{1,2})\\/(\\d{1,2})(\\s*s)?$|^(\\d{4})\\/(\\d{1,2})(\\s*s)?$|^(\\d{4})(\\s*s)?$";
	public static final Pattern PATTERN = Pattern.compile(REGEXP, Pattern.CASE_INSENSITIVE);
	
	private static String[] MONTHS = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

	private Integer day, month, year;
	private Date date;
	private boolean isForSorting;
	
	public Dateish(String s) {
		if (StringUtils.isNotBlank(s)) {
			Matcher m = PATTERN.matcher(s.trim());
			if (m.matches()) {
				if (m.group(8) != null) {
					this.year = Integer.valueOf(m.group(8));
					this.isForSorting = StringUtils.contains(m.group(9), "s");
				}
				else if (m.group(6) != null) {
					this.year = Integer.valueOf(m.group(5));
					this.month = Integer.valueOf(m.group(6));
					this.isForSorting = StringUtils.contains(m.group(7), "s");
				}
				else {
					this.year = Integer.valueOf(m.group(1));
					this.month = Integer.valueOf(m.group(2));
					this.day = Integer.valueOf(m.group(3));
					this.isForSorting = StringUtils.contains(m.group(4), "s");
				}
				
				this.month = checkBounds(this.month, 12);
				this.day = checkBounds(this.day, 31);
				
				if (this.day != null && this.month != null && this.year != null) {
					Calendar cal = Calendar.getInstance();
					cal.set(this.year, this.month - 1, this.day);
					
					if (cal.get(Calendar.MONTH) > this.month - 1) {
						this.day = cal.get(Calendar.DATE);
						this.month = cal.get(Calendar.MONTH) + 1;
						cal.set(this.year, this.month - 1, this.day);
					}
					
					this.date = cal.getTime();
				}
			}
		}
	}
	
	@Override
	public String toString() {
		String partA = toStringA();
		if (partA != null && this.isForSorting) {
			return partA + "s";
		}
		
		return partA;
	}
	
	public String toSortableString() {
		String partA = StringUtils.rightPad(toStringA(), 10);
		if (partA != null && this.isForSorting) {
			return partA + "s";
		}
		return partA;
	}
	
	public String getDeliveryString() {
		if (this.isForSorting) {
			// This date is approximate, and provided mainly for sorting purposes.
			// Only use the year part.
			return "~" + this.getYear();
		}
		
		StringBuilder sb = new StringBuilder();
		
		if (this.getDay() != null) {
			sb.append(this.getDay()).append(" ");
		}
		
		if (this.getMonth() != null) {
			sb.append(MONTHS[this.getMonth() - 1]);
		}
		
		if (this.getYear() != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(this.getYear());
		}
		
		return sb.toString();
	}
	
	private String toStringA() {
		StringBuilder sb = new StringBuilder();
		if (this.year != null) {
			sb.append(this.year);
			
			if (this.month != null) {
				sb.append("/").append(pad(this.month));
				
				if (this.day != null) {
					sb.append("/").append(pad(this.day));
				}
			}
		}
		
		return sb.toString();
	}
	
	private String pad(Integer n) {
		if (n == null) {
			return "";
		}
		else if (n < 10) {
			return "0" + n;
		}
		
		return String.valueOf(n);
	}
	
	private Integer checkBounds(Integer i, int max) {
		if (i == null || i <= max) {
			return i;
		}

		return max;
	}
	
	public Integer getDay() {
		return day;
	}

	public Integer getMonth() {
		return month;
	}

	public Integer getYear() {
		return year;
	}

	public Date getDate() {
		return date;
	}

	public boolean isForSorting() {
		return isForSorting;
	}

	public boolean isBlank() {
		return this.year == null;
	}
	
	public boolean isComplete() {
		return this.date != null;
	}
	
	public static void main(String[] args) {
		test("201");
		test("2010");
		test("2010s");
		test("2010/1");
		test("2010/01   s");
		test("2010/13");
		test("2010/01s");
		test("2010/10/1");
		test("2010/10/11");
		test("2010/10/11s");
		test("2010/10/41");
		test("2010/10/111");
		test("2010/2/31");
	}
	
	private static void test(String s) {
		Dateish d = new Dateish(s);
		String str = d.getDate() == null ? "not set" : d.getDate().toString();
		String in = StringUtils.rightPad(String.format("In: [%s]", s), 24);
		String f1 = StringUtils.rightPad(String.format("Out1: [%s]", d.toString()), 24);
		String f2 = StringUtils.rightPad(String.format("Out2: [%s]", d.toSortableString()), 24);
		String dat = StringUtils.rightPad(String.format("Date: [%s]", str), 32);
		System.out.println(in + f1 + f2 + dat);
	}
}
