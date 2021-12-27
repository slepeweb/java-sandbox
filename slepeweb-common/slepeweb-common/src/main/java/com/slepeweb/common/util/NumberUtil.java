package com.slepeweb.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;


public class NumberUtil {
	private static BigDecimal ONE_HUNDRED = new BigDecimal(100.0);
	public static DecimalFormat DF0 = new DecimalFormat("#.#");
	public static DecimalFormat DF1 = new DecimalFormat("#.0");
	
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
	
	public static String formatBytes(Long n) {
		if (n != null) {
			long divisor = 1024 * 1024;
			
			if (n > (100 * divisor)) {
				return formatBytes(DF0, n, divisor, "Mb");
			}
			else if (n > (divisor)) {
				return formatBytes(DF1, n, divisor, "Mb");
			}
			else {
				divisor = 1024;
				
				if (n > (100 * divisor)) {
					return formatBytes(DF0, n, divisor, "kb");
				}
				else if (n > (divisor)) {
					return formatBytes(DF1, n, divisor, "kb");
				}
				else {
					return formatBytes(DF0, n, 1L, "bytes");
				}
			}
		}
		
		return null;
	}
	
	public static String formatBytes(DecimalFormat df, long size, long denominator, String units) {
		return String.format("%s %s", df.format((float) size / (float) denominator), units);
	}
	
	public static void main(String[] args) {
		System.out.println(formatBytes(12345678L));
		System.out.println(formatBytes(1234567L));
		System.out.println(formatBytes(123456L));
		System.out.println(formatBytes(12345L));
		System.out.println(formatBytes(1234L));
		System.out.println(formatBytes(123L));
		System.out.println(formatBytes(12L));
	}
}
