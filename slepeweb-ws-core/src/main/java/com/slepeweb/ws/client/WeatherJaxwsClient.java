package com.slepeweb.ws.client;

import com.slepeweb.ws.bean.WeatherBean;

public interface WeatherJaxwsClient {
	WeatherBean getWeather(String country, String city);
}
