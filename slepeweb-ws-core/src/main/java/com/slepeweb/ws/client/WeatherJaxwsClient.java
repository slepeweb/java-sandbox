package com.slepeweb.ws.client;

import com.slepeweb.ws.bean.WeatherBean;
import com.slepeweb.ws.bean.WeatherBeanWrapper;

public interface WeatherJaxwsClient {
	WeatherBean getWeather(String country, String city);
	WeatherBeanWrapper getWeatherWrapper(String country, String city);
}
