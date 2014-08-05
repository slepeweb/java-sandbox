package com.slepeweb.cms.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.utils.LogUtil;

@Service
public class BuildTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet();
		List<TestResult> results = new ArrayList<TestResult>();
		trs.setResults(results);
		boolean testCompleted = false;
		
		// First, purge the test site
		Site site = this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
		if (site != null) {
			site.delete();
		}
		
		for (String variable : new String[] {
				TITLE_FIELD_NAME, TEASER_FIELD_NAME, BODY_FIELD_NAME, EMBARGO_FIELD_NAME, ALTTEXT_FIELD_NAME}) {			
			Field f = this.cmsService.getFieldService().getField(variable);
			if (f != null) {
				f.delete();
			}
		}
		
		for (String name : new String[] {
				HOMEPAGE_TYPE_NAME, SECTION_TYPE_NAME, NEWS_TYPE_NAME, EVENT_TYPE_NAME, ARTICLE_TYPE_NAME, IMAGE_TYPE_NAME}) {			
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
		ItemType imageType = addType(IMAGE_TYPE_NAME, true);
		
		// Assert N types have been created
		results.add(r = new TestResult().setId(2010).setTitle("Check N item types have been created"));
		int numItemTypes = this.cmsService.getItemTypeService().getCount();
		r.setNotes(numItemTypes + " items types have been created");
		if (numItemTypes != 6) {
			r.fail();
		}
		
		// Create fields
		Field titleField = addField("Title", TITLE_FIELD_NAME, "Page title - also used in links to this page", FieldType.text, 64, "");
		Field teaserField = addField("Teaser", TEASER_FIELD_NAME, "Used in links to this page", FieldType.text, 256, "");
		Field bodyField = addField("Body text", BODY_FIELD_NAME, "Main content for page", FieldType.markup, 0, "");
		Field embargoField = addField("Embargo date", EMBARGO_FIELD_NAME, "Future date when page can be seen", FieldType.date, 0, "");
		Field alttextField = addField("Alt text", ALTTEXT_FIELD_NAME, "Alt text for image", FieldType.text, 128, "*");
		
		// Assert 3 fields have been created
		results.add(r = new TestResult().setId(2020).setTitle("Check N fields have been created"));
		int numFields = this.cmsService.getFieldService().getCount();
		r.setNotes(numFields + " fields have been created");
		if (numFields != 5) {
			r.fail();
		}
		
		// Define fields for all types
		for (ItemType it : new ItemType[] {homepageType, sectionType, newsType, eventType, articleType} ) {
			it.addFieldForType(titleField, 1L, true);
			it.addFieldForType(teaserField, 2L, false);
			it.addFieldForType(bodyField, 3L, false);
			it.save();
		}
		
		// Define fields for image type
		imageType.addFieldForType(titleField, 1L, true);
		imageType.addFieldForType(alttextField, 2L, true);
		imageType.save();
		
		// Add the embargo field to the article type
		articleType.addFieldForType(embargoField, 4L, true);
		articleType.save();
		
		// Assert number of fieldfortype rows
		results.add(r = new TestResult().setId(2020).setTitle("Check N fieldfortype rows have been created"));
		int numFieldForTypes = this.cmsService.getFieldForTypeService().getCount();
		r.setNotes(numFieldForTypes + " fieldfortype rows have been created");
		if (numFieldForTypes != 18) {
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
				Item mediaSection = addItem(rootItem, "Media section", "media", now, now, site, sectionType);
				
				// Assert N items have been created
				results.add(r = new TestResult().setId(2050).setTitle("Check N section items created"));
				int sectionCount = this.cmsService.getItemService().getCountByType(sectionType.getId());
				r.setNotes(sectionCount + " sections have been created");
				if (sectionCount != 4) {
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
				
				if (mediaSection != null) {
					addItem(mediaSection, "Example image", "ex1", now, now, site, imageType);
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
		
		trs.setSuccess(testCompleted);
		return trs;
	}
}
