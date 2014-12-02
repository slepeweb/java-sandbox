package com.slepeweb.site.sws.service;

import com.slepeweb.site.sws.bean.WeatherBean;

public interface WeatherService {
	WeatherBean getWeather(String country, String city);
}
