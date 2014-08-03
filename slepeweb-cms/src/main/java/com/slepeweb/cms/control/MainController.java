package com.slepeweb.cms.control;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.TestResult;

@Controller
public class MainController extends BaseController {
	
	private static final String TEST_SITE_NAME = "Integration Testing";
	private static final String HOMEPAGE_TYPE_NAME = "ZHomepage";
	private static final String SECTION_TYPE_NAME = "ZSection";
	private static final String NEWS_TYPE_NAME = "ZNews";
	private static final String EVENT_TYPE_NAME = "ZEvent";
	private static final String ARTICLE_TYPE_NAME = "ZArticle";
	private static final String TITLE_FIELD_NAME = "ztitle";
	private static final String TEASER_FIELD_NAME = "zteaser";
	private static final String BODY_FIELD_NAME = "zbodytext";
	private static final String EMBARGO_FIELD_NAME = "zembargodate";
	
	@Autowired private CmsService cmsService;

	@RequestMapping("/test/build")
	public String doPop(ModelMap model) {
		
		List<TestResult> results = new ArrayList<TestResult>();
		TestResult r;
		boolean testCompleted = true;
		
		// First, purge the test site
		Site site = this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
		if (site != null) {
			site.delete();
		}
		
		for (String variable : new String[] {TITLE_FIELD_NAME, TEASER_FIELD_NAME, BODY_FIELD_NAME, EMBARGO_FIELD_NAME}) {			
			Field f = this.cmsService.getFieldService().getField(variable);
			if (f != null) {
				f.delete();
			}
		}
		
		for (String name : new String[] {HOMEPAGE_TYPE_NAME, SECTION_TYPE_NAME, NEWS_TYPE_NAME, EVENT_TYPE_NAME, ARTICLE_TYPE_NAME}) {			
			ItemType it = this.cmsService.getItemTypeService().getItemType(name);
			if (it != null) {
				it.delete();
			}
		}
		
		// Create item types
		ItemType homepageType = addType(HOMEPAGE_TYPE_NAME);
		ItemType sectionType = addType(SECTION_TYPE_NAME);
		ItemType newsType = addType(NEWS_TYPE_NAME);
		ItemType eventType = addType(EVENT_TYPE_NAME);
		ItemType articleType = addType(ARTICLE_TYPE_NAME);
		
		// Assert N types have been created
		results.add(r = new TestResult().setId(2010).setTitle("Check item types have been created"));
		int numItemTypes = this.cmsService.getItemTypeService().getCount();
		r.setNotes(numItemTypes + " items types have been created");
		if (numItemTypes != 5) {
			r.fail();
		}
		
		// Create fields
		Field titleField = addField("Title", TITLE_FIELD_NAME, "Page title - also used in links to this page", FieldType.text, 64, "");
		Field teaserField = addField("Teaser", TEASER_FIELD_NAME, "Used in links to this page", FieldType.text, 256, "");
		Field bodyField = addField("Body text", BODY_FIELD_NAME, "Main content for page", FieldType.markup, 0, "");
		Field embargoField = addField("Embargo date", EMBARGO_FIELD_NAME, "Future date when page can be seen", FieldType.date, 0, "");
		
		// Assert 3 fields have been created
		results.add(r = new TestResult().setId(2020).setTitle("Check N fields have been created"));
		int numFields = this.cmsService.getFieldService().getCount();
		r.setNotes(numFields + " fields have been created");
		if (numFields != 4) {
			r.fail();
		}
		
		// Define fields for all types
		for (ItemType it : new ItemType[] {homepageType, sectionType, newsType, eventType, articleType} ) {
			it.addFieldForType(titleField, 1L, true);
			it.addFieldForType(teaserField, 2L, false);
			it.addFieldForType(bodyField, 3L, false);
			it.save();
		}
		
		// Add the embargo field to the article type
		articleType.addFieldForType(embargoField, 4L, true);
		articleType.save();
		
		// Assert number of fieldfortype rows
		results.add(r = new TestResult().setId(2020).setTitle("Check 16 fieldfortype rows have been created"));
		int numFieldForTypes = this.cmsService.getFieldForTypeService().getCount();
		r.setNotes(numFieldForTypes + " fieldfortype rows have been created");
		if (numFieldForTypes != 16) {
			r.fail();
		}		
				
		// Create test site
		site = addSite(TEST_SITE_NAME, "test.slepeweb.com", HOMEPAGE_TYPE_NAME);
		
		// Assert site has been created
		results.add(r = new TestResult().setId(2030).setTitle("Check test site has been created"));
		if (site != null) {
			r.setNotes(LogUtil.compose("Test site has been created", site.getName()));
		
			// Assert test site has a root item
			Item rootItem = site.getItem("/");
			results.add(r = new TestResult().setId(2040).setTitle("Check root item has been created"));
			if (rootItem != null) {			
				// Create more items
				Timestamp now = new Timestamp(System.currentTimeMillis());
				
				// Create sections section
				Item newsSection = addItem(rootItem, "News section", "news", now, now, site, sectionType);
				addItem(rootItem, "Events section", "events", now, now, site, sectionType);
				Item aboutSection = addItem(rootItem, "About section", "about", now, now, site, sectionType);
				
				// Assert N items have been created
				results.add(r = new TestResult().setId(2050).setTitle("Check total number of section items created"));
				int sectionCount = this.cmsService.getItemService().getCountByType(sectionType.getId());
				r.setNotes(sectionCount + " sections have been created");
				if (sectionCount != 3) {
					r.fail();
				}
				
				if (newsSection != null) {
					// Assert N field values have been created for the news section
					results.add(r = new TestResult().setId(2060).setTitle("Check total number of field values created"));
					int fieldValueCount = this.cmsService.getFieldValueService().getCount(newsSection.getId());
					r.setNotes(fieldValueCount + " field values have been created");
					if (fieldValueCount != 3) {
						r.fail();
					}
				
					// Create 2 news items below the section
					addItem(newsSection, "News item #1", "101", now, now, site, newsType);
					addItem(newsSection, "News item #2", "102", now, now, site, newsType);
					
					// Assert N bindings have been created
					results.add(r = new TestResult().setId(2070).setTitle("Check number of bindings for news section"));
					int bindingCount = this.cmsService.getLinkService().getCount(newsSection.getId());
					r.setNotes(bindingCount + " children bound to parent");
					if (bindingCount != 2) {
						r.fail();
					}					
				}
				else {
					testCompleted = false;
				}
				
				if (aboutSection != null) {
					addItem(aboutSection, "About us", "about-us", now, now, site, articleType);
				}
				else {
					testCompleted = false;
				}
			}
			else {
				testCompleted = false;
				r.fail();
			}
		}		
		else {
			testCompleted = false;
			r.fail();
		}
		
		model.addAttribute("testResults", results);
		model.addAttribute("testCompleted", testCompleted);
		return "test";
	}
	
	@RequestMapping("/test/fields")
	public String doFields(ModelMap model) {
		
		List<TestResult> results = new ArrayList<TestResult>();
		TestResult r;
		boolean testCompleted = false;
		
		// Set field values for first news item
		Site site = this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
		if (site != null) {
			Item aboutItem = this.cmsService.getItemService().getItem(site.getId(), "/about/about-us");
			
			if (aboutItem != null) {
				String title = "About us", teaser = "Find out more about Slepe Web Solutions Ltd";
				aboutItem.setFieldValue(TITLE_FIELD_NAME, title);
				aboutItem.setFieldValue(TEASER_FIELD_NAME, teaser);
				
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, 14);
				Timestamp ts = new Timestamp(cal.getTime().getTime());
				aboutItem.setFieldValue(EMBARGO_FIELD_NAME, ts);
				aboutItem.save();
				
				// Assert title field value update
				results.add(r = new TestResult().setId(3010).setTitle("Check title field value update"));
				Item checkItem = this.cmsService.getItemService().getItem(aboutItem.getId());
				FieldValue titleFieldValue = checkItem.getFieldValue(TITLE_FIELD_NAME);
				if (titleFieldValue == null || ! titleFieldValue.getStringValue().equals(title)) {
					r.setNotes("Text field value update failed").fail();
				}
				
				// Assert embargo field value update
				results.add(r = new TestResult().setId(3020).setTitle("Check embargo date field value update"));
				FieldValue embargoFieldValue = checkItem.getFieldValue(EMBARGO_FIELD_NAME);
				if (embargoFieldValue == null || ! embargoFieldValue.getDateValue().equals(ts)) {
					r.setNotes("Date value update failed").fail();
				}	
				
				testCompleted = true;
			}
		}
				
		model.addAttribute("testResults", results);
		model.addAttribute("testCompleted", testCompleted);
		return "test";
	}
	
	@RequestMapping("/test/items")
	public String doItems(ModelMap model) {
		
		List<TestResult> results = new ArrayList<TestResult>();
		TestResult r;
		boolean testCompleted = false;
		
		// Set field values for first news item
		Site site = this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
		if (site != null) {
			Item aboutSectionItem = this.cmsService.getItemService().getItem(site.getId(), "/about");
			Item newsSectionItem = this.cmsService.getItemService().getItem(site.getId(), "/news");
			
			if (aboutSectionItem != null && newsSectionItem != null) {
				aboutSectionItem.move(newsSectionItem);
				
				// Assert news section has 3 children
				results.add(r = new TestResult().setId(4010).setTitle("Check news section has 3 children"));
				int count = newsSectionItem.getBoundItems().size();
				r.setNotes("News section has " + count + " children");
				if (count != 3) {
					r.fail();
				}
				
				// Check path of article item
				results.add(r = new TestResult().setId(4020).setTitle("Check path of about article"));
				String articlePath = "/news/about/about-us";
				aboutSectionItem = this.cmsService.getItemService().getItem(site.getId(), "/news/about");
				Item article = null;
				for (Item child : aboutSectionItem.getBoundItems()) {
					if (child.getPath().equals(articlePath)) {
						article = child;
						break;
					}
				}
				
				r.setNotes(LogUtil.compose("Article path should be", articlePath));
				if (article == null) {
					r.fail();
				}
				
				// Restore the links
				Item rootItem = this.cmsService.getItemService().getItem(site.getId(), "/");
				
				if (aboutSectionItem != null && rootItem != null) {
					aboutSectionItem.move(rootItem);
					
					// Assert news section has original 2 children
					results.add(r = new TestResult().setId(4030).setTitle("Reverse previous move"));
					newsSectionItem = this.cmsService.getItemService().getItem(site.getId(), "/news");
					count = newsSectionItem.getBoundItems().size();
					r.setNotes("News section has " + count + " children");
					if (count != 2) {
						r.fail();
					}
					
					// Re-check path of article item
					results.add(r = new TestResult().setId(4030).setTitle("Re-check path of about article"));
					aboutSectionItem = this.cmsService.getItemService().getItem(site.getId(), "/about");
					articlePath = "/about/about-us";
					article = null;
					for (Item child : aboutSectionItem.getBoundItems()) {
						if (child.getPath().equals(articlePath)) {
							article = child;
							break;
						}
					}
					
					r.setNotes(LogUtil.compose("Article path should be", articlePath));
					if (article == null) {
						r.fail();
					}
					
					testCompleted = true;
				}
			}
		}
				
		model.addAttribute("testResults", results);
		model.addAttribute("testCompleted", testCompleted);
		return "test";
	}
	
	@RequestMapping("/test/delete")
	public String doPurge(ModelMap model) {
		
		List<TestResult> results = new ArrayList<TestResult>();
		TestResult r;
		ItemType it;
		boolean testCompleted = false;
		
		// Delete the News item type
		it = this.cmsService.getItemTypeService().getItemType(NEWS_TYPE_NAME);
		results.add(r = new TestResult().setId(1010).setTitle("Delete News type"));
		if (it == null) {
			r.setNotes("News type is not created").fail();
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
			results.add(r = new TestResult().setId(1020).setTitle("Confirm all news items have been cascade-deleted"));
			diff = itemCount - this.cmsService.getItemService().getCount();
			r.setNotes(diff + " news items have been deleted");
			if (diff != newsCount) {
				r.setNotes(diff + " news items have been deleted - should have been " + newsCount).fail();
			}
			
			// Check that field values have been cascade-deleted
			results.add(r = new TestResult().setId(1030).setTitle("Check N field values have been cascade-deleted"));
			diff = fieldValueCount - this.cmsService.getFieldValueService().getCount();
			r.setNotes(diff + " field value records have been deleted");
			if (diff <= 0) {
				r.fail();
			}
			
			// Check that fieldfortype records have been cascade-deleted
			results.add(r = new TestResult().setId(1040).setTitle("Check N fieldfortype records have been cascade-deleted"));
			diff = fieldForTypeCount - this.cmsService.getFieldForTypeService().getCount();
			r.setNotes(diff + " fieldfortype records have been deleted");
			if (diff <= 0) {
				r.fail();
			}

			testCompleted = true;
		}
				
		model.addAttribute("testResults", results);
		model.addAttribute("testCompleted", testCompleted);
		return "test";
	}
	
	private Site addSite(String name, String hostname, String homepageTypeName) {
		Site s = CmsBeanFactory.getSite().setName(name).setHostname(hostname);	

		
		String rootName = "Homepage";
		ItemType type = this.cmsService.getItemTypeService().getItemType(homepageTypeName);
		Item homepageItem = null;
		
		if (type != null) {
			homepageItem = CmsBeanFactory.getItem().
				setName(rootName).
				setSimpleName("").
				setPath("/").
				setSite(s).
				setType(type).
				setDateCreated(new Timestamp(System.currentTimeMillis()));
			
			homepageItem.
				setDateUpdated(homepageItem.getDateCreated()).
				setDeleted(false);
		}
		
		s.save(homepageItem);
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
