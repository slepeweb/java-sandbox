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
		
		// If request parameter indicates content deletion:
		// Delete all items - cascade should delete fieldvalues, fieldfortypes and links
		// Delete all item types
		// Delete all field definitions
		// Delete site 'Test'
		
		// Create item types
		addType("Root");
		ItemType sectionType = addType("Section");
		ItemType newsType = addType("News");
		addType("Article");
		
		// Assert N types have been created
		// Assert Test site has been created
		
		// Create fields
		Field titleField = addField("Title", "title", "Page title - also used in links to this page", FieldType.text, 64);
		Field teaserField = addField("Teaser", "teaser", "Used in links to this page", FieldType.text, 256);
		
		// Assert N fields have been created
		// Assert properties of title field
		
		// Defined fields for type News
		newsType.addFieldForType(titleField, 1L, true);
		newsType.addFieldForType(teaserField, 2L, false);
		newsType.save();
		
		// Assert news type has 2 fields
		// Assert news type has a teaser field
		// Assert properties of teaser field
		
		// Create test site
		Site s = addSite("Integeration Test", "test.slepeweb.com");
		
		// Assert test site has a root item
		Item root = s.getItem("/");
		
		// Create items
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		// Create a news section
		Item section = addItem(root, "News section", "news", now, now, s, sectionType);
		
		// Create 2 news items below the section
		Item newsItem = addItem(section, "News item #1", "101", now, now, s, newsType);
		addItem(section, "News item #2", "102", now, now, s, newsType);
		
		// Assert section has 2 children
		
		// Set field values for first news item
		newsItem.setFieldValue("title", "News item #1");
		newsItem.setFieldValue("teaser", "Ukranian militia return bodies to the Netherlands");
		newsItem.save();
		
		// Assert field changes have been made
		
		// Update field values for the same news item
		newsItem.setFieldValue("title", "News item #1 (modified)");
		newsItem.setFieldValue("teaser", "This teaser smells");
		newsItem.save();
				
		// Assert field changes have been made
		
		// END
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
	
	private Item addItem(Item parent, String name, String simplename, 
			Timestamp dateCreated, Timestamp dateUpdated, Site site, ItemType type) {
		
		Item i = CmsBeanFactory.getItem().setName(name).setSimpleName(simplename).setPath(parent.getPath() + "/" + simplename).
			setDateCreated(dateCreated).setDateUpdated(dateUpdated).setType(type);
		
		return parent.addChild(i);
	}
	
}
