package com.slepeweb.sandbox.www.service;

import com.slepeweb.sandbox.ws.soap.WeatherBean;

public interface WeatherService {
	WeatherBean getWeather(String country, String city);
}
