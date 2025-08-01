package com.slepeweb.site.service;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.component.Passkey;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.service.ItemService;

@Service
public class MagicMarkupServiceImpl implements MagicMarkupService {

	@Autowired private ItemService itemService;
	
	public String transform(String html) {
		Document doc = createDocument(html);
		transformXImages(doc);
		transformXComponents(doc);
		
		return doc.body().html();
	}
	
	
	public String transform4Pdf(String html, String localHostname, Passkey passkey) {
		Document doc = createDocument(html);
		transformMinipathImages(doc, localHostname, passkey);
		transformXImages(doc);
		transformMinipathImages(doc, localHostname, passkey);
		transformXComponents(doc);
		
		return doc.body().html();
	}
	
	
	private Document createDocument(String html) {
    	Document doc = Jsoup.parse(html);
    	doc.outputSettings()
	 	   .syntax(Document.OutputSettings.Syntax.xml) // ← forces XHTML output
	 	   .escapeMode(Entities.EscapeMode.xhtml)
	 	   .charset(StandardCharsets.UTF_8);
    	
    	return doc;
	}
	
	private void transformMinipathImages(Document doc, String localHostnamePortAndProtocol, Passkey passkey) {
		String src = null;
		boolean srcHasParams;
		
    	for (Element e : doc.getElementsByTag("img")) {
    		src = e.attr("src");
    		srcHasParams = src.indexOf('?') > -1;
    		
    		if (src.matches("^/\\$_\\d+.*")) {
    			src = localHostnamePortAndProtocol + src;
    			if (passkey != null) {
        			src += (srcHasParams ? "&" : "?") + "_passkey=" + passkey.encode();
    			}
    			
    			e.attr("src", src);
    		}
    	}
 	}
	
	private void transformXImages(Document doc) {
    	
    	String id, url, width, body, caption, cssfloat;
    	String template;
    	Item i;
    	 
    	for (Element e : doc.getElementsByClass("ximg")) {
    		if (! e.is("div.ximg")) {
    			continue;
    		}
    		
    		id = e.attr("data-id");
    		i = this.itemService.getItem(Long.valueOf(id));
    		if (i == null) {
    			continue;
    		}
    		
     		url = String.format("/$_%d", i.getId());
    		
    		width = e.attr("data-width");
    		if (StringUtils.isNotBlank(width)) {
    			width = String.format("style=\"width: %s\"", width); 
    		}
    		
    		cssfloat = e.attr("data-float");    		
    		cssfloat = StringUtils.isNotBlank(cssfloat) ? "class=\"right\"" : "";

    		caption = e.attr("data-caption");
    		if (StringUtils.isBlank(caption)) {
    			caption = i.getFieldValue(FieldName.CAPTION);
        		if (StringUtils.isBlank(caption)) {
        			caption = i.getFieldValue(FieldName.TEASER);
            		if (StringUtils.isBlank(caption)) {
            			caption = i.getName();
            		}
        		}
    		}
    		
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
    }

	private void transformXComponents(Document doc) {
    	
    	Elements divs = doc.getElementsByClass("xcomp");
    	Element comp;
    	String id;
    	 
    	for (Element parent : divs) {
    		if (! parent.is("div.xcomp")) {
    			continue;
    		}
    		
    		id = parent.attr("data-enum");
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
    }
}
