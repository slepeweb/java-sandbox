package com.slepeweb.site.service;

import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.service.ItemService;

@Service
public class XimgServiceImpl implements XimgService {
	
	@Autowired private ItemService itemService;

    public String parse(String markup, String passkey) {
    	Document doc = Jsoup.parse(markup);
    	doc.outputSettings()
    	   .syntax(Document.OutputSettings.Syntax.xml) // ← forces XHTML output
    	   .escapeMode(Entities.EscapeMode.xhtml)
    	   .charset(StandardCharsets.UTF_8);
    	
    	Elements divs = doc.getElementsByClass("ximg");
    	String id, url, width, body, caption, cssfloat;
    	String template;
    	Item i;
    	String hostname;
    	 
    	for (Element e : divs) {
    		if (! e.is("div.ximg")) {
    			continue;
    		}
    		
    		id = e.attr("data-id");
    		i = this.itemService.getItem(Long.valueOf(id));
    		if (i == null) {
    			continue;
    		}
    		
    		/*
    		Media m = i.getMedia();
    		if (m == null) {
    			continue;
    		}
    		
    		m.getFolder();
    		path = String.format("%s/%s", m.getFolder(), id);
    		*/
    		
    		hostname = i.getSite().getDeliveryHost().getNamePortAndProtocol();
    		url = String.format("%s/$_%d%s", hostname, i.getId(), 
    				passkey != null ? String.format("?_passkey=%s", passkey) : "");
    		
    		width = e.attr("data-width");
    		if (width != null) {
    			width = String.format("style=\"width: %s\"", width); 
    		}
    		
    		cssfloat = e.attr("data-float");    		
    		cssfloat = cssfloat != null ? "class=\"right\"" : "";

    		caption = e.attr("data-caption");
    		body = e.html();
    		
    		e.removeAttr("data-width");
    		e.removeAttr("data-caption");
    		e.removeAttr("data-float");
    		
    		template = """    				
    			<figure %s %s>
    				<a href=\"%s\" target=\"_blank\" title=\"Click to see image in new tab\">
    					<img src=\"%s\">
    				</a>
    				<figcaption>%s</figcaption>
    			</figure>
    			%s
    			<p class="clearfix"></p>
    		""";
    		
    		e.append(String.format(template, cssfloat, width, url, url, caption, body));
    	}
    	
    	return doc.body().html();
    }
    
    @SuppressWarnings("unused")
	private String notNull(String s, String replacement) {
    	return s == null ? replacement : s;
    }
}
