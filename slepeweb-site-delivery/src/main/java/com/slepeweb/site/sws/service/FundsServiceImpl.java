package com.slepeweb.site.sws.service;

import java.io.FileInputStream;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service("fundsService")
public class FundsServiceImpl implements FundsService {
	
	private static Logger LOG = Logger.getLogger(FundsServiceImpl.class);

	//@Cacheable(value="serviceCache")
	public String scrapeJs(String path) {
		
		String res = getResource(path);
		
		if (res != null) {
			Document doc = Jsoup.parse(res);
			
			if (doc != null) {
				String elementId = "head";
			    Elements scripts = doc.select(elementId).first().children();
			    
			    if (scripts != null && scripts.size() > 1) {
				    return scripts.get(1).html();
			    }
			    else {
			    	LOG.error(String.format("Failed to select element [%s]", elementId));
			    }
			}
		}
		
		return null;
	}
	
	private String getResource(String path) {
		FileInputStream in = null;
		StringWriter out = null;
		
		try {
			in = new FileInputStream(path);
			out = new StringWriter();
			int bufflen = 1024;
			byte[] bytes = new byte[bufflen];
			int count = -1;
			while ((count = in.read(bytes, 0, bufflen)) > -1) {
				out.append(new String(bytes), 0, count);
			}
			
			return out.toString();
		}
		catch (Exception e) {
	    	LOG.error(String.format("Failed to read file [%s]", path));
		}
		finally {
			if (in != null) {
				try {in.close();}
				catch (Exception e) {}
			}
			
			if (out != null) {
				try {out.close();}
				catch (Exception e) {}
			}
		}
		
		return null;
	}
}
