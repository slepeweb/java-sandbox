package com.slepeweb.common.service;

import java.util.List;
import java.util.Map;

import com.slepeweb.common.bean.NameValuePair;

public interface HttpService {
	String get(String url);
	String get(String url, List<NameValuePair> headers);
	byte[] getBytes(String url, List<NameValuePair> headers);
	NameValuePair getAuthorisationHeader(String username, String password);
	Map<String, String> getHeaders(String url);
}
