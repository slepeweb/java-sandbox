package com.slepeweb.site.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.slepeweb.cms.bean.Item;

@Service
public class PdfServiceImpl implements PdfService {
	
	public String assemble(Item root, String sessionId) {
		String localHostname = root.getSite().getDeliveryHost().getNamePortAndProtocol();
		StringBuilder body = new StringBuilder();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		drillDown(root, localHostname, httpclient, sessionId, body);	
		return body.toString();
	}
	
	@SuppressWarnings("unused")
	private void output(String html, String baseUri) {
		
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream("/tmp/html2.pdf"))) {
			PdfRendererBuilder builder = new PdfRendererBuilder();
			
			// Need to use a font like this in order to render special html entities, such as &diams; aka &#9830;
			builder.useFont(() -> getClass().getClassLoader().getResourceAsStream("DejaVuSans.ttf"), "DejaVuSans");
			//builder.useFastMode();
			builder.withHtmlContent(html, baseUri);
			builder.toStream(os);
			builder.run();
		} 
		catch (Exception e) {
			System.err.println(e.getMessage());
		}		
	}
	
	private void drillDown(Item parent, String localHostname, CloseableHttpClient httpclient, 
			String sessionId, StringBuilder body) {

		if (parent.isAccessible()) {
			String url = String.format("%s%s?view=pdf", localHostname, parent.getPath());
			appendPage(httpclient, url, localHostname, sessionId, body);
			
			for (Item child : parent.getBoundPages()) {
				drillDown(child, localHostname, httpclient, sessionId, body);
			}
		}
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
    
    private void appendPage(CloseableHttpClient httpclient, String url, String localHostname, 
    		String sessionId, StringBuilder body) {
    	
		try {
			ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url).build();
			httpGet.setHeader("Cookie", "JSESSIONID=" + sessionId);
			
			// The underlying HTTP connection is still held by the response object
			// to allow the response content to be streamed directly from the network socket.
			// In order to ensure correct deallocation of system resources
			// the user MUST call CloseableHttpResponse#close() from a finally clause.
			// Please note that if response content is not fully consumed the underlying
			// connection cannot be safely re-used and will be shut down and discarded
			// by the connection manager.
			
			httpclient.execute(httpGet, response -> {
				final HttpEntity entity1 = response.getEntity();
				String html = readInputStream(entity1.getContent());
				body.append(html);
				
				EntityUtils.consume(entity1);
				response.close();
				return null;
			});
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
    }
    
    @SuppressWarnings("unused")
	private String notNull(String s, String replacement) {
    	return s == null ? replacement : s;
    }
}
