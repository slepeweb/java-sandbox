package com.slepeweb.site.bean;

import java.util.Calendar;
import java.util.Date;


public class TimeAgo {
	private static final long SECOND = 1000;
	private static final long MINUTE = 60 * SECOND;
	private static final long HOUR = 60 * MINUTE;
	private static final long DAY = 24 * HOUR;
	private static final long MONTH = 31 * DAY;
	private static final long YEAR = 365 * DAY;

	private int quantity;
	private String unit;
	
	private TimeAgo() {}
	
	public static TimeAgo getInstance(Date date) {	
		Calendar now = Calendar.getInstance();
		long nowMillis = now.getTimeInMillis();
		Calendar then = Calendar.getInstance();
		
		if (date != null) {
			then.setTime(date);
			long thenMillis = then.getTimeInMillis();
			
			if (now.after(then)) {
				TimeAgo time = new TimeAgo();
				long diff = nowMillis - thenMillis;
				
				if (diff < YEAR) {
					if (diff < DAY) {	
						if (diff < 2 * HOUR) {
							time.setQuantity(toMinutes(diff));
							time.setUnit("mins");
						}
						else {
							time.setQuantity(toHours(diff));
							time.setUnit("hours");
						}
					}
					else if (diff >= DAY && diff < 2 * DAY) {
						time.setQuantity(1);
						time.setUnit("day");
					}
					else if (diff < MONTH) {
						time.setQuantity(toDays(diff));
						time.setUnit("days");
					}
					else if (diff >= 31 * DAY && diff < 2 * MONTH) {
						time.setQuantity(1);
						time.setUnit("month");
					}
					else {
						time.setQuantity(toMonths(diff));
						time.setUnit("months");
					}
				}
				else if (diff < 2 * YEAR) {
					time.setQuantity(1);
					time.setUnit("year");
				}
				else {
					time.setQuantity(toYears(diff));
					time.setUnit("years");
				}
				
				return time;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("%d %s ago", getQuantity(), getUnit());
	}
	
	private static long toMinutes(long millis) {
		return millis / MINUTE;
	}
	
	private static long toHours(long millis) {
		return millis / HOUR;
	}
	
	private static long toDays(long millis) {
		return millis / DAY;
	}
	
	private static long toMonths(long millis) {
		return millis / MONTH;
	}
	
	private static long toYears(long millis) {
		return millis / YEAR;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int value) {
		this.quantity = value;
	}
	
	public void setQuantity(long value) {
		this.quantity = (int) value;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String bundleKey) {
		this.unit = bundleKey;
	}
}
