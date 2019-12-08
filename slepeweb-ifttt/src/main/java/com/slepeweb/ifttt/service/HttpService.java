package com.slepeweb.ifttt.service;

import java.util.Map;

public interface HttpService {
	String getResource(String url);
	Map<String, String> getHeaders(String url);
	void postJsonBody(String url, String json);
}
