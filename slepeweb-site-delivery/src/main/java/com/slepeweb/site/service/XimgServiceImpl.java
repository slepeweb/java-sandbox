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
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.service.ItemService;

@Service
public class XimgServiceImpl implements XimgService {
	
	/*
	 * This class is required in the process of converting web pages to PDF.
	 * The client that reads the HTML input requires public access to the content, but
	 * this content might be from a secured site (ie requires login). So, a solution
	 * requiring passkeys is needed.
	 * 
	 * If the page contains images (eg) that reference a foreign site, using div.ximg elements,
	 * then the corresponding img src attributes must be amended server site to include a suitable passkey.
	 * Otherwise, the image would not be served up.
	 * 
	 * Javascript is used on the live web pages to perform this conversion, but the 
	 * PDF producer is unable to execute this code, so a server-side solution is required.
	 */
	
	@Autowired private ItemService itemService;

    public String parse(String markup, String passkey) {
    	Document doc = Jsoup.parse(markup);
    	doc.outputSettings()
    	   .syntax(Document.OutputSettings.Syntax.xml) // ← forces XHTML output
    	   .escapeMode(Entities.EscapeMode.xhtml)
    	   .charset(StandardCharsets.UTF_8);
    	
    	Elements divs = doc.getElementsByClass("ximg");
    	String id, url, width, body, caption, cssfloat;
    	String template, hostname;
    	Item i;
    	 
    	for (Element e : divs) {
    		if (! e.is("div.ximg")) {
    			continue;
    		}
    		
    		id = e.attr("data-id");
    		i = this.itemService.getItem(Long.valueOf(id));
    		if (i == null) {
    			continue;
    		}
    		
    		hostname = i.getSite().getDeliveryHost().getNamePortAndProtocol();
    		url = String.format("%s/$_%d%s", hostname, i.getId(), 
    				passkey != null ? String.format("?_passkey=%s", passkey) : "");
    		
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
    	
    	return doc.body().html();
    }
    
    @SuppressWarnings("unused")
	private String notNull(String s, String replacement) {
    	return s == null ? replacement : s;
    }
}
