package com.slepeweb.site.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("httpService")
public class HttpServiceImpl implements HttpService {

	private static Logger LOG = Logger.getLogger(HttpServiceImpl.class);

	@Cacheable(value="serviceCache")
	public String getResource(String url) {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse res = null;
		
		try {
			res = client.execute(httpGet);
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to retrieve page [%s]", url), e);
			return null;
		}
		
		// In order to ensure correct deallocation of system resources
		// the user MUST call CloseableHttpResponse#close() from a finally clause.
		// Please note that if response content is not fully consumed the underlying
		// connection cannot be safely re-used and will be shut down and discarded
		// by the connection manager. 
		try {
			try {
			    LOG.info(String.format("Retrieved resource [%s] with status [%s]", url, res.getStatusLine()));
			    HttpEntity entity = res.getEntity();
			    return EntityUtils.toString(entity);
			} finally {
			    res.close();
			}
		}
		catch (IOException e) {
			LOG.error(String.format("Failed to consume resource [%s]", url), e);
		}
		
		return null;
	}

	@Cacheable(value="serviceCache")
	public Map<String, String> getHeaders(String url) {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpHead httpHead = new HttpHead(url);
		CloseableHttpResponse res = null;
		
		try {
			res = client.execute(httpHead);
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to retrieve resource [%s]", url), e);
			return null;
		}
		
		try {
			try {
			    LOG.info(String.format("Retrieved resource [%s] with status [%s]", url, res.getStatusLine()));
			    Header[] headers = res.getAllHeaders();
			    Map<String, String> map = new HashMap<String, String>(headers.length);
			    for (Header h : headers) {
			    	map.put(h.getName(), h.getValue());
			    }
			    return map;
			} finally {
			    res.close();
			}
		}
		catch (IOException e) {
			LOG.error(String.format("Failed to get header information for resource [%s]", url), e);
		}
		
		return null;
	}
}
