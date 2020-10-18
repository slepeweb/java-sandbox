package com.slepeweb.common.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.slepeweb.common.bean.NameValuePair;

@Service("httpService")
public class HttpServiceImpl implements HttpService {

	private static Logger LOG = Logger.getLogger(HttpServiceImpl.class);
	private CloseableHttpClient client;
	
	public CloseableHttpClient getClient() {
		if (this.client == null) {
			this.client = HttpClients.custom().build();
		}
		return this.client;
	}

	public NameValuePair getAuthorisationHeader(String userName, String password) {
		if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
			byte[] encoded = null;
			String credentials = String.format("%s___%s", userName, password);
			
			try {
				encoded = Base64.encodeBase64(credentials.getBytes("ISO_8859_1"));
			} 
			catch (UnsupportedEncodingException e1) {
				LOG.error("Failed to encode string", e1);
			}
			
			return new NameValuePair("Authorization", "Basic " + new String(encoded));
		}
		
		return null;
	}

	public String get(String url) {		
		return get(url, null);
	}

	public String get(String url, List<NameValuePair> headers) {
		byte[] bytes = getBytes(url, headers);
		return bytes == null ? new String() : new String(bytes);
	}
	
	public byte[] getBytes(String url, List<NameValuePair> headers) {
		CloseableHttpResponse res = null;
		
		try {
			res = getResponse(url, headers);						
		    HttpEntity entity = res.getEntity();
		    LOG.info(String.format("Retrieved resource [%s] with status [%s]", url, res.getStatusLine()));
		    return EntityUtils.toByteArray(entity);
		} 
		catch (IOException e) {
		    LOG.error(String.format("Failed to retrieve resource [%s]", url), e);
		    
		    // Force the client object to be re-created, in case it was the cause
		    this.client = null;
		}
		finally {
		    try {res.close();}
		    catch (Exception e) {}
		}
		
		return null;
	}

	private CloseableHttpResponse getResponse(String url, List<NameValuePair> headers) throws IOException {
		
		RequestBuilder builder = RequestBuilder.get().setUri(url);

		if (headers != null) {
			for (NameValuePair nvp : headers) {
				builder.setHeader(nvp.getName(), nvp.getValue());
			}
		}
		
		// In order to ensure correct deallocation of system resources
		// the user MUST call CloseableHttpResponse#close() from a finally clause.
		// Please note that if response content is not fully consumed the underlying
		// connection cannot be safely re-used and will be shut down and discarded
		// by the connection manager. 
		HttpUriRequest request = builder.build();
		return getClient().execute(request);
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
	
}
