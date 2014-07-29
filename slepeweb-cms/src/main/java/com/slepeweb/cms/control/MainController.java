package com.slepeweb.cms.control;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.utils.TestResult;

@Controller
public class MainController extends BaseController {
	
	@Autowired private CmsService cmsService;

	@RequestMapping("/test/delete")
	public String doPurge(ModelMap model) {
		
		List<TestResult> results = new ArrayList<TestResult>();
		TestResult r;
		ItemType it;
		
		// Delete the News item type
		it = this.cmsService.getItemTypeService().getItemType("News");
		results.add(r = new TestResult().setId(10).setTitle("Delete News type"));
		if (it == null) {
			r.setNotes("News type is not defined").fail();
		}
		else {
			int typeCount = this.cmsService.getItemTypeService().getCount();
			int newsCount = this.cmsService.getItemService().getCountByType(it.getId());
			int itemCount = this.cmsService.getItemService().getCount();
			int fieldValueCount = this.cmsService.getFieldValueService().getCount();
			int fieldForTypeCount = this.cmsService.getFieldForTypeService().getCount();
			
			// Delete the news item type
			it.delete();
			
			// Check that exactly 1 item type has been deleted
			int diff = typeCount - this.cmsService.getItemTypeService().getCount();
			r.setNotes(diff + " item types have been deleted");
			if (diff != 1) {
				r.fail();
			}
			
			// Check the number of news items that have been deleted
			results.add(r = new TestResult().setId(20).setTitle("Confirm all news items have been cascade-deleted"));
			diff = itemCount - this.cmsService.getItemService().getCount();
			r.setNotes(diff + " news items have been deleted");
			if (diff != newsCount) {
				r.setNotes(diff + " news items have been deleted - should have been " + newsCount).fail();
			}
			
			// Check that field values have been cascade-deleted
			results.add(r = new TestResult().setId(30).setTitle("Check N field values have been cascade-deleted"));
			diff = fieldValueCount - this.cmsService.getFieldValueService().getCount();
			r.setNotes(diff + " field value records have been deleted");
			if (diff <= 0) {
				r.fail();
			}
			
			// Check that fieldfortype records have been cascade-deleted
			results.add(r = new TestResult().setId(40).setTitle("Check N fieldfortype records have been cascade-deleted"));
			diff = fieldForTypeCount - this.cmsService.getFieldForTypeService().getCount();
			r.setNotes(diff + " fieldfortype records have been deleted");
			if (diff <= 0) {
				r.fail();
			}

		}
				
		// END
		model.addAttribute("testResults", results);
		return "test";
	}
	
	@RequestMapping("/test/build")
	public String doPop(ModelMap model) {
		
		List<TestResult> results = new ArrayList<TestResult>();
		TestResult r;
		int testId = 1;
		String testSiteName = "Integration Testing";
		Site site;
		int count;
		
		// Create item types
		addType("Root");
		ItemType sectionType = addType("Section");
		ItemType newsType = addType("News");
		addType("Article");
		
		// Assert N types have been created
		// Assert Test site has been created
		
		// Create fields
		Field titleField = addField("Title", "title", "Page title - also used in links to this page", FieldType.text, 64, "");
		Field teaserField = addField("Teaser", "teaser", "Used in links to this page", FieldType.text, 256, "");
		
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
		Site s = addSite("Integration Test", "test.slepeweb.com");
		
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

	private Field addField(String name, String variable, String help, FieldType type, int size, Object dflt) {
		Field f = CmsBeanFactory.getField().setName(name).setVariable(variable).setHelp(help).setType(type).
				setSize(size).setDefaultValue(dflt);
		f.save();
		return f;
	}
	
	private Item addItem(Item parent, String name, String simplename, 
			Timestamp dateCreated, Timestamp dateUpdated, Site site, ItemType type) {
		
		String path = parent.isRoot() ? parent.getPath() + simplename : parent.getPath() + "/" + simplename;
		Item i = CmsBeanFactory.getItem().setName(name).setSimpleName(simplename).setPath(path).
			setDateCreated(dateCreated).setDateUpdated(dateUpdated).setSite(site).setType(type);
		
		return parent.addChild(i);
	}
	
}
