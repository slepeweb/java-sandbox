package com.slepeweb.site.service;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.common.bean.NameValuePair;
import com.slepeweb.common.service.HttpService;

@Service
public class PdfServiceImpl implements PdfService {
	
	@Autowired private HttpService httpService;
	
	public String assemble(Item root, String sessionId) {
		String localHostname = root.getSite().getDeliveryHost().getNamePortAndProtocol();
		StringBuilder body = new StringBuilder();
		CloseableHttpClient httpclient = HttpClients.createDefault();
		List<NameValuePair> headers = new ArrayList<NameValuePair>();
		headers.add(new NameValuePair("Cookie", "JSESSIONID=" + sessionId));
		
		drillDown(root, localHostname, httpclient, headers, body);	
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
			List<NameValuePair> headers, StringBuilder body) {

		if (parent.isAccessible()) {
			String url = String.format("%s%s?view=pdf", localHostname, parent.getPath());
	    	body.append(this.httpService.get(httpclient, url, headers));
			
			for (Item child : parent.getBoundPages()) {
				drillDown(child, localHostname, httpclient, headers, body);
			}
		}
	}
	
    @SuppressWarnings("unused")
	private String notNull(String s, String replacement) {
    	return s == null ? replacement : s;
    }
}
