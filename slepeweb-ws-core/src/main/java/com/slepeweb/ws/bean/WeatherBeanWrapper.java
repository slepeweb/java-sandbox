package com.slepeweb.ws.bean;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherBeanWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	private static String WIND_PATTERN_COMMON = "^ from the ([a-z]+) \\(.+?\\) at (\\d+) mph.*";
	private static Pattern WIND_PATTERN_1 = Pattern.compile(WIND_PATTERN_COMMON, Pattern.CASE_INSENSITIVE);
	private static Pattern WIND_PATTERN_2 = Pattern.compile(WIND_PATTERN_COMMON + "?gusting to (\\d+) mph .*$", Pattern.CASE_INSENSITIVE);
	private WeatherBean bean;
	
	public WeatherBeanWrapper(WeatherBean b) {
		this.bean = b;
	}
	
	public String getStatus() {
		return this.bean.getStatus();
	}
		
	public String getTemperature() {
		String str = this.bean.getTemperature();
		String[] arr = str.split("\\s");
		if (arr.length == 2) {
			return String.format("%sC", arr[0]);
		}
		return str;
	}

	public String getWind() {
		String wind = this.bean.getWind();
		Matcher m = WIND_PATTERN_2.matcher(wind);
		if (m.matches()) {
			return String.format("%smph wind from the %s, gusting to %smph", m.group(2), m.group(1), m.group(3));
		}
		else {
			m = WIND_PATTERN_1.matcher(wind);
			if (m.matches()) {
				return String.format("%smph wind from the %s", m.group(2), m.group(1));
			}
		}
		
		return wind;
	}
	
	public String getSkyConditions() {
		return this.bean.getSkyConditions();
	}

	public String getTime() {
		String dateStr = this.bean.getTime();
		return dateStr.substring(0, dateStr.length() - 7);
	}

	public String getHumidity() {
		return this.bean.getRelativeHumidity();
	}
}
