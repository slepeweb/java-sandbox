package com.slepeweb.common.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("httpService")
public class HttpServiceImpl implements HttpService {

	private static Logger LOG = Logger.getLogger(HttpServiceImpl.class);

	public String get(String url) {		
		return get(url, null, null);
	}

	public String get(String url, String userName, String password) {
		return new String(getBytes(url, userName, password));
	}
	
	public byte[] getBytes(String url, String userName, String password) {
		CloseableHttpResponse res = null;
		
		try {
			res = getResponse(url, userName, password);						
		    HttpEntity entity = res.getEntity();
		    LOG.info(String.format("Retrieved resource [%s] with status [%s]", url, res.getStatusLine()));
		    return EntityUtils.toByteArray(entity);
		} 
		catch (IOException e) {
		    LOG.error(String.format("Failed to retrieve resource [%s]", url));
		}
		finally {
		    try {res.close();}
		    catch (Exception e) {}
		}
		
		return null;
	}

	private CloseableHttpResponse getResponse(String url, String userName, String password) throws IOException{
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		
		if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
			byte[] encoded = null;
			String credentials = String.format("%s___%s", userName, password);
			
			try {
				encoded = Base64.encodeBase64(credentials.getBytes("ISO_8859_1"));
			} 
			catch (UnsupportedEncodingException e1) {
				LOG.error("Failed to encode string", e1);
			}
			
			String authHeader = "Basic " + new String(encoded);
			httpGet.addHeader("Authorization", authHeader);
		}
		
		// In order to ensure correct deallocation of system resources
		// the user MUST call CloseableHttpResponse#close() from a finally clause.
		// Please note that if response content is not fully consumed the underlying
		// connection cannot be safely re-used and will be shut down and discarded
		// by the connection manager. 
		return client.execute(httpGet);
	}

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
	
	/*
	private String getResponseMimetype(HttpEntity e) {
		String[] h = e.getContentType().getValue().split("\\s");
		if (h.length > 0) {
			return h[0];
		}
		return null;
	}
	*/
}
