package com.slepeweb.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.stereotype.Service;

import com.slepeweb.common.bean.NameValuePair;

@Service
public class HttpServiceImpl implements HttpService {

	//private static Logger LOG = Logger.getLogger(HttpServiceImpl.class);
	
	public String get(CloseableHttpClient httpclient, String url) {		
		return get(httpclient, url, null);
	}

	public String get(CloseableHttpClient httpclient, String url, List<NameValuePair> headers) {
		
		StringBuilder html = new StringBuilder();
		
		try {
			ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url).build();
			
			if (headers != null) {
				for (NameValuePair nvp : headers) {
					httpGet.setHeader(nvp.getName(), nvp.getValue());
				}
			}
			
			// The underlying HTTP connection is still held by the response object
			// to allow the response content to be streamed directly from the network socket.
			// In order to ensure correct deallocation of system resources
			// the user MUST call CloseableHttpResponse#close() from a finally clause.
			// Please note that if response content is not fully consumed the underlying
			// connection cannot be safely re-used and will be shut down and discarded
			// by the connection manager.
			
			httpclient.execute(httpGet, response -> {
				final HttpEntity entity1 = response.getEntity();
				html.append(readInputStream(entity1.getContent()));
				
				EntityUtils.consume(entity1);
				response.close();
				return null;
			});
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		return html.toString();
	}

    private String readInputStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        return content.toString();
    }
    

}
