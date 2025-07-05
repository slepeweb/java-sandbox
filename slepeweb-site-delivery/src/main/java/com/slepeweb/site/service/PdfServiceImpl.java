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
import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.component.Passkey;
import com.slepeweb.cms.component.PasskeyModel;
import com.slepeweb.cms.service.PasskeyService;

@Service
public class PdfServiceImpl implements PdfService {

	@Autowired private PasskeyService passkeyService;
	
	public String build(Item root, User u) {
		Host host = root.getSite().getDeliveryHost();
		String hostname = root.getSite().getDeliveryHost().getNamePortAndProtocol();
		
		String header = String.format("""
				<html>
				<head>					
					<link rel="stylesheet" href="%s/resources/geo/css/main.css" type="text/css" />
					
					<style>
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
				""", hostname, hostname);

		String footer = """
				</body>
				</html>
				""";

		StringBuilder body = new StringBuilder();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		drillDown(host, u, root, httpclient, body);
		final String html = header + body.toString() + footer;
		output(html);
		
		return html;
	}
	
	private void output(String html) {
		try (OutputStream os = new BufferedOutputStream(new FileOutputStream("/tmp/html2.pdf"))) {
			PdfRendererBuilder builder = new PdfRendererBuilder();
			builder.useFastMode();
			builder.withHtmlContent(html, "file:///home/photos/");
			builder.toStream(os);
			builder.run();
		} 
		catch (Exception e) {
			System.err.println(e.getMessage());
		}		
	}
	
	private void drillDown(Host host, User u, Item parent, CloseableHttpClient httpclient, StringBuilder body) {
		Passkey passkey = this.passkeyService.issueKey(PasskeyModel.LONG_TTL, u);
		String url = String.format("%s%s?view=pdf&_passkey=%s", host.getNamePortAndProtocol(), parent.getPath(), passkey.encode());
		appendPage(httpclient, url, body);
		
		for (Item child : parent.getBoundPages()) {
			drillDown(host, u, child, httpclient, body);
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
    
    private void appendPage(CloseableHttpClient httpclient, String url, StringBuilder body) {
		try {
			ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url).build();
			// The underlying HTTP connection is still held by the response object
			// to allow the response content to be streamed directly from the network socket.
			// In order to ensure correct deallocation of system resources
			// the user MUST call CloseableHttpResponse#close() from a finally clause.
			// Please note that if response content is not fully consumed the underlying
			// connection cannot be safely re-used and will be shut down and discarded
			// by the connection manager.
			
			httpclient.execute(httpGet, response -> {
				final HttpEntity entity1 = response.getEntity();
				body.append(readInputStream(entity1.getContent()));
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
