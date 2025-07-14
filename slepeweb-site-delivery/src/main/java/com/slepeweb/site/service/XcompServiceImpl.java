package com.slepeweb.site.service;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class XcompServiceImpl implements XcompService {
	
	/*
	 * This class moves components around the page WHEN the page is being processed
	 * for conversion to PDF.
	 */
	
    public String parse(String markup) {
    	Document doc = Jsoup.parse(markup);
    	doc.outputSettings()
    	   .syntax(Document.OutputSettings.Syntax.xml) // ← forces XHTML output
    	   .escapeMode(Entities.EscapeMode.xhtml)
    	   .charset(StandardCharsets.UTF_8);
    	
    	Elements divs = doc.getElementsByClass("xcomp");
    	Element comp;
    	String id;
    	 
    	for (Element parent : divs) {
    		if (! parent.is("div.xcomp")) {
    			continue;
    		}
    		
    		id = parent.attr("data-id");
    		if (! StringUtils.isNumeric(id)) {
    			continue;
    		}
    		
    		comp = doc.getElementById("component-" + id);
    		if (comp == null) {
    			continue;
    		}
    		
    		comp.remove();
    		parent.appendChild(comp);
    	}
    	
    	return doc.body().html();
    }
}
