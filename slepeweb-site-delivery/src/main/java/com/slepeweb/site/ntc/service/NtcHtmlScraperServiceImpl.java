package com.slepeweb.site.ntc.service;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.slepeweb.site.service.HttpServiceImpl;

@Service("htmlScraperService")
public class NtcHtmlScraperServiceImpl extends HttpServiceImpl implements NtcHtmlScraperService {
	//private static Logger LOG = Logger.getLogger(NtcHtmlScraperServiceImpl.class);
	
	@Cacheable(value="serviceCache")
	public String scrape(String url, Integer organiserId, Integer tableId) {
	    String html = getResource(url);
	    Document doc = Jsoup.parse(html);
	    String result = null;
	    
	    switch (organiserId) {
		    case 1:
		    	result = processLTAFeed(doc);
		    	break;
		    case 2:
		    	result = processHPFeed(doc, tableId);
		    	break;
	    }
	    
	    return result;
	}
	
	private String processLTAFeed(Document doc) {
	    Element table = doc.select("table.teamstandings").first();
	    
	    if (table != null) {
	    	String caption = table.select("caption").text();
		    table.select("caption").html(String.format("League table - %s", caption));
		    table.select("a").unwrap();
		    table.select("td[colspan=4]").attr("colspan", "3");
		    table.select("tbody td:empty").remove();
		    
		    Iterator<Element> iter = table.select("tbody td").iterator();
		    Element e;
		    
		    while (iter.hasNext()) {
		    	e = iter.next();
		    	if (e.attr("align").equals("right") && StringUtils.isBlank(e.attr("class"))) { 
		    		e.attr("align", "center");
		    	}
		    }
		    
		    return table.outerHtml();
	    }
	    
	    return "";
	}
	
	private String processHPFeed(Document doc, int tableId) {
	    Element span = doc.select("span:contains(League Table)").first();
	    
	    if (span != null) {
	    	Iterator<Element> iter = span.parents().iterator();
		    Element elem, table = null, thead, tr, td;
	    	
		    // Identify the nearest tbody ancestor to this span
	    	while (iter.hasNext()) {
	    		elem = iter.next();
	    		if (elem.tagName().equals("table")) {
	    			table = elem;
	    			break;
	    		}
	    	}
	    	
	    	// Get all the tr and td's, and wipe their attributes.
	    	// Identify where a new table starts, and
	    	if (table != null) {
	    		iter = table.select("tr").iterator();
	    		int rowCounter = 0, tableCounter = 0;
	    		
	    		// Ignore the first two rows
			    while (iter.hasNext() && rowCounter++ < 2) {
			    	tr = iter.next();
			    	tr.remove();
			    }
			    
			    // This should bring us to the first table
			    while (iter.hasNext()) {
	    			// The next row is a new table
			    	tr = iter.next();
		    		if (tableCounter++ != tableId) {
		    			skipToNextHPTable(iter);
		    			tr.remove();
		    		}
		    		else {
		    			grabHPTable(tr, iter);
		    		}
			    }
			    
			    // Introduce <caption> and <thead>; move first <tr> into <thead>
			    removeAttributes(table);
			    table.prepend("<thead></thead>");
			    thead = table.select("thead").first();
			    tr = table.select("tr").first();
			    td = tr.select("td").first();
			    String division = td.text();
			    td.html("&nbsp;");
			    thead.appendChild(tr);
			    table.prepend(String.format("<caption>League table - %s</caption>", division));
			    
			    // Highlight Needingworth entry
			    for (Element e : table.select("tr")) {
			    	if (e.select("td").first().text().toLowerCase().startsWith("needingworth")) {
			    		e.attr("class", "selected");
			    		break;
			    	}
			    }
			    
			    return table.outerHtml();
	    	}
	    }
		return null;
	}
	
	private void skipToNextHPTable(Iterator<Element> iter) {
		Element tr;
		boolean isComplete = false;
		
		while (iter.hasNext()) {
			tr = iter.next();
			isComplete = StringUtils.isBlank(tr.text());
			tr.remove();
			if (isComplete) {
				break;
			}
		}
	}
	
	private void grabHPTable(Element firstRow, Iterator<Element> iter) {
    	removeAttributes(firstRow);   
    	cleanupHPCells(firstRow);
    	Element tr;
    	boolean isComplete = false;
    	
    	while (iter.hasNext()) {
    		tr = iter.next();
    		if (isComplete || StringUtils.isBlank(tr.text())) {
    			// Remove this and all subsequent rows
    			isComplete = true;
    			tr.remove();
    		}
    		else {
    	    	removeAttributes(tr);   
    	    	cleanupHPCells(tr);
    		}
    	}
	}
	
	private void cleanupHPCells(Element tr) {
    	Iterator<Element> iter = tr.select("td").iterator();
    	Element td;
    	
    	while (iter.hasNext()) {
    		td = iter.next();
    		removeAttributes(td);
    		td.html(td.text());
    	}
	}
	
	private void removeAttributes(Element e) {
		Attributes at = e.attributes();
	    for (Attribute a : at) {
	        e.removeAttr(a.getKey());
	    }
	}
	
}
