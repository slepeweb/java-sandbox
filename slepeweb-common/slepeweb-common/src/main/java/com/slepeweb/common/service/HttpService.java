package com.slepeweb.common.service;

import java.util.Map;

public interface HttpService {
	String get(String url);
	String get(String url, String name, String password);
	byte[] getBytes(String url, String name, String password);
	Map<String, String> getHeaders(String url);
}
