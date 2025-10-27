package com.slepeweb.common.service;

import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import com.slepeweb.common.bean.NameValuePair;

public interface HttpService {
	String get(CloseableHttpClient httpclient, String url);
	String get(CloseableHttpClient httpclient, String url, List<NameValuePair> headers);
}
