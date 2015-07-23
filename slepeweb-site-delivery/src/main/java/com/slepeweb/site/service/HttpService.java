package com.slepeweb.site.service;

import java.util.Map;

public interface HttpService {
	String getResource(String url);
	Map<String, String> getHeaders(String url);
}
