package com.slepeweb.ws.bean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

@XmlRootElement(name="CurrentWeather")
public class WeatherBean {
	private static Logger LOG = Logger.getLogger(WeatherBean.class);
	
	// Example temperature: 72 F (21 C)
	private static Pattern TEMPERATURE_PATTERN = Pattern.compile("^\\d+ F \\((\\d+ C)\\)$");
	
	// Example time: Nov 12, 2013 - 03:50 PM EST / 2013.11.12 2050 UTC
	//private static Pattern TIME_PATTERN = Pattern.compile("^.*? - (\\d{2}):(\\d{2}) (\\w{2}) EST / (\\d{4})\\.(\\d+)\\.(\\d+) .*$");
	private static Pattern TIME_PATTERN = Pattern.compile("^.*? / (\\d{4})\\.(\\d+)\\.(\\d+) (\\d{2})(\\d{2}) UTC$");
	
	private String location, time, wind, visibility, skyConditions, 
		temperature, dewPoint, relativeHumidity, pressure, status;

	public String getLocation() {
		String[] parts = this.location.split(",");
		if (parts.length > 0) {
			return parts[0].trim();
		}
		
		return location;
	}

	@XmlElement(name="Location")
	public void setLocation(String location) {
		this.location = location;
	}

	public String getTime() {
		Matcher m = TIME_PATTERN.matcher(this.time);
		LOG.debug(String.format("Time was [%s]", this.time));

		if (m.matches()) {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.set(Calendar.YEAR, Integer.parseInt(m.group(1)));
			cal.set(Calendar.MONTH, Integer.parseInt(m.group(2)) - 1);
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(m.group(3)));
			//cal.set(Calendar.AM_PM, m.group(3).equals("AM") ? Calendar.AM : Calendar.PM);
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(4)));
			cal.set(Calendar.MINUTE, Integer.parseInt(m.group(5)));
			
			DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm a z");
			String reformatted = df.format(cal.getTime());
			LOG.debug(String.format("Reformatted to [%s]", reformatted));
			return reformatted;
		}

		return time;
	}

	@XmlElement(name="Time")
	public void setTime(String time) {
		this.time = time;
	}

	public String getWind() {
		return stripColon(this.wind);
	}
	
	private String stripColon(String str) {
		int c = str.indexOf(":");
		return c > -1 ? str.substring(0, c) : str;
	}

	@XmlElement(name="Wind")
	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getVisibility() {
		return stripColon(this.visibility);
	}

	@XmlElement(name="Visibility")
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getSkyConditions() {
		return skyConditions;
	}

	@XmlElement(name="SkyConditions")
	public void setSkyConditions(String skyConditions) {
		this.skyConditions = skyConditions;
	}

	public String getTemperature() {
		return getTemperatureCelsius(this.temperature);
	}
	
	private String getTemperatureCelsius(String str) {
		Matcher m = TEMPERATURE_PATTERN.matcher(str.trim());
		if (m.matches()) {
			return m.group(1);
		}
		return str;
	}

	@XmlElement(name="Temperature")
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getDewPoint() {
		return getTemperatureCelsius(this.dewPoint);
	}

	@XmlElement(name="DewPoint")
	public void setDewPoint(String dewPoint) {
		this.dewPoint = dewPoint;
	}

	public String getPressure() {
		return pressure;
	}

	@XmlElement(name="Pressure")
	public void setPressure(String pressure) {
		this.pressure = pressure;
	}

	public String getStatus() {
		return status;
	}

	@XmlElement(name="Status")
	public void setStatus(String status) {
		this.status = status;
	}

	public String getRelativeHumidity() {
		return relativeHumidity;
	}

	@XmlElement(name="RelativeHumidity")
	public void setRelativeHumidity(String relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}

}
