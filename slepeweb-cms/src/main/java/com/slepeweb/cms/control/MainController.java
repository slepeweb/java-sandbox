package com.slepeweb.cms.control;

import java.sql.Timestamp;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;

@Controller
public class MainController extends BaseController {
	//private static Logger LOG = Logger.getLogger(MainController.class);

	@RequestMapping("/test")
	public String doGeneric(HttpSession session, ModelMap model) {
		
		addType("Root");
		ItemType sectionType = addType("Section");
		ItemType newsType = addType("News");
		addType("Article");
		
		Field titleField = addField("Title", "title", "Page title - also used in links to this page", FieldType.text, 64);
		Field teaserField = addField("Teaser", "teaser", "Used in links to this page", FieldType.text, 256);
		
		newsType.addFieldForType(titleField, 1L, true);
		newsType.addFieldForType(teaserField, 2L, false);
		newsType.save();
		
		addSite("YRP", "www.yrp.com");
		Site s = addSite("Slepeweb", "www.slepeweb.com");
		
		// Create items
		Timestamp now = new Timestamp(System.currentTimeMillis());
		addItem("News section", "news", "/news", now, now, s, sectionType);
		Item newsItem = addItem("News item #1", "101", "/news/101", now, now, s, newsType);
		addItem("News item #2", "102", "/news/102", now, now, s, newsType);
		
		// Set field values
		newsItem.setFieldValue("title", "News item #1");
		newsItem.setFieldValue("teaser", "Ukranian militia return bodies to the Netherlands");
		newsItem.save();
		newsItem.setFieldValue("title", "News item #1 (modified)");
		newsItem.setFieldValue("teaser", "This teaser smells");
		newsItem.save();
				
		model.addAttribute("_page", "helo");
		return "test";
	}
	
	private Site addSite(String name, String hostname) {
		Site s = CmsBeanFactory.getSite().setName(name).setHostname(hostname);	
		s.save();
		return s;
	}
	
	private ItemType addType(String name) {
		ItemType it = CmsBeanFactory.getItemType().setName(name);
		it.save();
		return it;
	}

	private Field addField(String name, String variable, String help, FieldType type, int size) {
		Field f = CmsBeanFactory.getField().setName(name).setVariable(variable).setHelp(help).setType(type).setSize(size);
		f.save();
		return f;
	}
	
	private Item addItem(String name, String simplename, String path, 
			Timestamp dateCreated, Timestamp dateUpdated, Site site, ItemType type) {
		
		Item i = CmsBeanFactory.getItem().setName(name).setSimpleName(simplename).setPath(path).
			setDateCreated(dateCreated).setDateUpdated(dateUpdated).setType(type);
		
		return site.addItem(i);
	}
	
}
