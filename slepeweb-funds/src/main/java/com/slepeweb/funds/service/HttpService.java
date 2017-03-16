package com.slepeweb.funds.service;

import java.util.Map;

public interface HttpService {
	String getResource(String url);
	Map<String, String> getHeaders(String url);
}
