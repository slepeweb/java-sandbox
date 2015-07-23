package com.slepeweb.site.sws.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.slepeweb.site.service.HttpService;

@Service("dilbertService")
public class DilbertServiceImpl implements DilbertService {
	
	@Autowired HttpService httpService;

	@Cacheable(value="serviceCache")
	public String getTodaysDilbert(String url) {
		String res = this.httpService.getResource(url);
		if (res != null) {
			Document doc = Jsoup.parse(res);
		    Element div = doc.select("#articleBody").first();
		    Element para = div.select("p").first();
		    return para.html();
		}
		return null;
	}
}
