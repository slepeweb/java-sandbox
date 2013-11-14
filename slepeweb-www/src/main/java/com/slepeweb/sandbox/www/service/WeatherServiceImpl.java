package com.slepeweb.sandbox.www.service;

import net.webservicex.GlobalWeather;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.slepeweb.sandbox.ws.soap.WeatherBean;

@Service( "weatherService" )
public class WeatherServiceImpl implements WeatherService {
	private static Logger LOG = Logger.getLogger(WeatherServiceImpl.class);
	
	@Autowired
	XmlMarshallingService xmlMarshallingService;

	@Cacheable(value="componentCache", key="'weather-' + #country + '-' + #city")
	public WeatherBean getWeather(String country, String city) {
		LOG.info(String.format("Getting weather for %s, %s at %3$tH:%3$tM:%3$tS", country, city, System.currentTimeMillis()));
		
		try {
			GlobalWeather gw = new GlobalWeather();
			String xml = gw.getGlobalWeatherSoap().getWeather(city, country);
			Object obj = this.xmlMarshallingService.unmarshall(xml, new WeatherBean());
			if (obj != null && obj instanceof WeatherBean) {
				return (WeatherBean) obj;
			}
		}
		catch (Exception e) {
			LOG.error("Failed to get weather report", e);
		}
		
		WeatherBean error = new WeatherBean();
		error.setStatus("Failed");
		return error;
	}

}
