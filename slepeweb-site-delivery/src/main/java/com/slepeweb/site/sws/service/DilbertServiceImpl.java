package com.slepeweb.site.sws.service;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.slepeweb.site.service.HttpService;

@Service("dilbertService")
public class DilbertServiceImpl implements DilbertService {
	
	private static Logger LOG = Logger.getLogger(DilbertServiceImpl.class);
	@Autowired HttpService httpService;

	@Cacheable(value="serviceCache")
	public String getTodaysDilbert(String url) {
		String res = this.httpService.getResource(url);
		if (res != null) {
			Document doc = Jsoup.parse(res);
			
			if (doc != null) {
				String elementId = "#content-body .about-section-inner";
			    Element div = doc.select(elementId).first();
			    
			    if (div != null) {
				    Element para = div.select("p").first();
				    return para.html();
			    }
			    else {
			    	LOG.error(String.format("Failed to select element [%s]", elementId));
			    }
			}
		    
		    return "";
		}
		return null;
	}
}
