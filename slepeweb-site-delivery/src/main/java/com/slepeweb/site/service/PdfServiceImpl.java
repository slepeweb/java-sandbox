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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.component.Passkey;
import com.slepeweb.cms.component.PasskeyModel;
import com.slepeweb.cms.service.PasskeyService;

@Service
public class PdfServiceImpl implements PdfService {
	
	@Autowired private PasskeyService passkeyService;

	public String assemble(Item root, User u, String sessionId) {
		String localHostname = root.getSite().getDeliveryHost().getNamePortAndProtocol();
		StringBuilder body = new StringBuilder();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		Passkey passkey = this.passkeyService.issueKey(PasskeyModel.LONG_TTL, u);
		
		drillDown(root, u, localHostname, httpclient, sessionId, passkey, body);	
		String html = header(localHostname) + body.toString() + footer();
		output(html);
		//System.out.println(html);
		return html;
	}
	
	private void output(String html) {
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream("/tmp/html2.pdf"))) {
			PdfRendererBuilder builder = new PdfRendererBuilder();
			
			// Need to use a font like this in order to render special html entities, such as &diams; aka &#9830;
			builder.useFont(() -> getClass().getClassLoader().getResourceAsStream("DejaVuSans.ttf"), "DejaVuSans");
			
			builder.useFastMode();
			builder.withHtmlContent(html, null);
			builder.toStream(os);
			builder.run();
		} 
		catch (Exception e) {
			System.err.println(e.getMessage());
		}		
	}
	
	private void drillDown(Item parent, User u, String localHostname, CloseableHttpClient httpclient, 
			String sessionId, Passkey passkey, StringBuilder body) {

		String url = String.format("%s%s?view=pdf&_passkey=%s", localHostname, parent.getPath(), passkey.encode());
		appendPage(httpclient, url, localHostname, sessionId, body);
		
		for (Item child : parent.getBoundPages()) {
			drillDown(child, u, localHostname, httpclient, sessionId, passkey, body);
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
    
    private String header(String localHostnamePortAndProtocol) {
    	String h = localHostnamePortAndProtocol;
    	
		return String.format("""
			<!DOCTYPE html>
			<html>
				<head>					
					<link rel="stylesheet" href="%s/resources/geo/css/base/main.css" type="text/css" />
					<link rel="stylesheet" href="%s/resources/geo/css/base/framework.css" type="text/css" />
					<link rel="stylesheet" href="%s/resources/geo/css/base/page.css" type="text/css" />
					<link rel="stylesheet" href="%s/resources/geo/css/base/component.css" type="text/css" />
					
					<style>
						body {
						    font-family: 'DejaVuSans', sans-serif;
						}
						
					    @page {
					      size: A4;
					      margin: 2cm;
	
					      @bottom-center {
					        content: "Page " counter(page) " of " counter(pages);
					        font-size: 10pt;
					        color: #666;
					      }
					    }
	
					    h1, h2 {
					      page-break-before: always;
					    }
					</style>
				</head>
				<body>
				""", h, h, h, h);
    }

    private String footer() {
		return """
				</body>
			</html>
			""";
    }
    	
    
    @SuppressWarnings("unused")
	private String notNull(String s, String replacement) {
    	return s == null ? replacement : s;
    }
}
