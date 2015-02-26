package com.slepeweb.ws.client;

import net.webservicex.GlobalWeatherSoap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.slepeweb.ws.bean.WeatherBean;
import com.slepeweb.ws.bean.WeatherBeanWrapper;
import com.slepeweb.ws.service.XmlMarshallingService;

@Service("weatherService")
public class WeatherJaxwsClientImpl implements WeatherJaxwsClient {
	private static Logger LOG = Logger.getLogger(WeatherJaxwsClientImpl.class);
	
	@Autowired XmlMarshallingService xmlMarshallingService;
	
	@Autowired
	private GlobalWeatherSoap globalWeatherSoapService;	

	@Cacheable(value="serviceCache", key="'weather-' + #country + '-' + #city")
	public WeatherBean getWeather(String country, String city) {
		LOG.info(String.format("Getting weather for %s, %s at %3$tH:%3$tM:%3$tS", country, city, System.currentTimeMillis()));
		
		try {
			String xml = this.globalWeatherSoapService.getWeather(city, country);
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

	@Cacheable(value="serviceCache", key="'weatherw-' + #country + '-' + #city")
	public WeatherBeanWrapper getWeatherWrapper(String country, String city) {
		return new WeatherBeanWrapper(getWeather(country, city));
	}
}
