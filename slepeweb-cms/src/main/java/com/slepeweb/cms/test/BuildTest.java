package com.slepeweb.cms.test;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.bean.Field.FieldType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.utils.LogUtil;

@Service
public class BuildTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet("Test site build").
			register(2010, "Check 6 item types have been created").
			register(2020, "Check 5 fields have been created").
			register(2030, "Check 15 fieldfortype rows have been created").
			register(2040, "Check test site has been created").
			register(2045, "Check news template has been created").
			register(2050, "Check root item has been created").
			register(2060, "Check 3 section items created").
			register(2070, "Check 3 field values created for news section page").
			register(2080, "Check number of bindings for news section").
			register(2090, "Check template applied to news item").
			register(2100, "Check NO template applied to news section item");

		// First, purge the test site
		Site site = this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
		if (site != null) {
			site.delete();
		}
		
		// Purge fields
		for (String variable : new String[] {
				TITLE_FIELD_NAME, TEASER_FIELD_NAME, BODY_FIELD_NAME, EMBARGO_FIELD_NAME, ALTTEXT_FIELD_NAME}) {			
			Field f = this.cmsService.getFieldService().getField(variable);
			if (f != null) {
				f.delete();
			}
		}
		
		// Purge item types (BUT NOT content folder)
		for (String name : new String[] {
				HOMEPAGE_TYPE_NAME, SECTION_TYPE_NAME, NEWS_TYPE_NAME, EVENT_TYPE_NAME, ARTICLE_TYPE_NAME, 
				IMAGE_TYPE_NAME}) {			
			ItemType it = this.cmsService.getItemTypeService().getItemType(name);
			if (it != null) {
				it.delete();
			}
		}
		
		
		// Create item types
		ItemType cfolderType = addType(ItemType.CONTENT_FOLDER_TYPE_NAME);
		ItemType sectionType = addType(SECTION_TYPE_NAME);
		ItemType newsType = addType(NEWS_TYPE_NAME);
		ItemType eventType = addType(EVENT_TYPE_NAME);
		ItemType articleType = addType(ARTICLE_TYPE_NAME);
		ItemType imageType = addType(IMAGE_TYPE_NAME, "image/jpeg");
		
		// 2010: Assert N types have been created
		int numItemTypes = this.cmsService.getItemTypeService().getCount();
		r = trs.execute(2010).setNotes(numItemTypes + " items types have been created");
		if (numItemTypes != 6) {
			r.fail();
		}
		
		// Create fields
		Field titleField = addField("Title", TITLE_FIELD_NAME, "Page title - also used in links to this page", FieldType.text, 64, "");
		Field teaserField = addField("Teaser", TEASER_FIELD_NAME, "Used in links to this page", FieldType.text, 256, "");
		Field bodyField = addField("Body text", BODY_FIELD_NAME, "Main content for page", FieldType.markup, 0, "");
		Field embargoField = addField("Embargo date", EMBARGO_FIELD_NAME, "Future date when page can be seen", FieldType.date, 0, "");
		Field alttextField = addField("Alt text", ALTTEXT_FIELD_NAME, "Alt text for image", FieldType.text, 128, "*");
		
		// 2020: Assert N fields have been created
		int numFields = this.cmsService.getFieldService().getCount();
		r = trs.execute(2020).setNotes(numFields + " fields have been created");
		if (numFields != 5) {
			r.fail();
		}
		
		// Define fields for all types
		for (ItemType it : new ItemType[] {sectionType, newsType, eventType, articleType} ) {
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
		
		// 2030: Assert number of fieldfortype rows
		int numFieldForTypes = this.cmsService.getFieldForTypeService().getCount();
		r = trs.execute(2030).setNotes(numFieldForTypes + " fieldfortype rows have been created");
		if (numFieldForTypes != 15) {
			r.fail();
		}		
				
		// Create test site
		site = addSite(TEST_SITE_NAME, "test.slepeweb.com", HOMEPAGE_TYPE_NAME);
		
		// 2040: Assert site has been created
		r = trs.execute(2040);
		if (site != null) {
			r.setNotes(LogUtil.compose("Test site has been created", site.getName()));
			
			Template newsTemplate = addTemplate(NEWS_TEMPLATE_NAME, "/page/news", site.getId(), newsType.getId());
			
			// 2045: Check news template has been created
			r = trs.execute(2045);
			newsTemplate = this.cmsService.getTemplateService().getTemplate(site.getId(), NEWS_TEMPLATE_NAME);
			if (newsTemplate == null) {
				r.setNotes("Template not in DB").fail();
			}
		
			// 2050: Assert test site has a root item
			Item rootItem = site.getItem("/");
			r = trs.execute(2050);
			if (rootItem != null) {
				
				// Create more items
				Timestamp now = new Timestamp(System.currentTimeMillis());
				
				// Create sections section
				Item newsSection = addItem(rootItem, "News section", "news", now, now, site, sectionType, null);
				addItem(rootItem, "Events section", "events", now, now, site, sectionType, null);
				Item aboutSection = addItem(rootItem, "About section", "about", now, now, site, sectionType, null);
				Item contentFolder = site.getItem("/content");
				Item mediaFolder = addItem(contentFolder, "Media section", "media", now, now, site, cfolderType, null);
				
				// 2060: Assert N section items have been created
				int sectionCount = this.cmsService.getItemService().getCountByType(sectionType.getId());
				r = trs.execute(2060).setNotes(sectionCount + " sections have been created");
				if (sectionCount != 3) {
					r.fail();
				}
				
				if (newsSection != null) {
					// 2070: Assert N field values have been created for the news section
					int fieldValueCount = this.cmsService.getFieldValueService().getCount(newsSection.getId());
					r = trs.execute(2070).setNotes(fieldValueCount + " field values have been created");
					if (fieldValueCount != 3) {
						r.fail();
					}
				
					// Create 2 news items below the section
					Item newsItem = addItem(newsSection, "News item #1", "101", now, now, site, newsType, newsTemplate);
					addItem(newsSection, "News item #2", "102", now, now, site, newsType, newsTemplate);
					
					// 2080: Assert N bindings have been created
					int bindingCount = this.cmsService.getLinkService().getCount(newsSection.getId());
					r = trs.execute(2080).setNotes(bindingCount + " children bound to parent");
					if (bindingCount != 2) {
						r.fail();
					}
					
					// 2090: Check template applied to news item
					r = trs.execute(2090);
					if (newsItem == null) {
						r.setNotes("News item not available for this test").fail();
					}
					else if (newsItem.getTemplate() == null) {
						r.setNotes("News template not applied to this item").fail();
					}
					else if (! newsItem.getTemplate().getName().equals(NEWS_TEMPLATE_NAME)) {
						r.setNotes(String.format("'%s' template applied to this item", newsItem.getTemplate().getName())).fail();
					}
					
					// 2100: Check NO template applied to news section item
					r = trs.execute(2100);
					if (newsSection.getTemplate() != null) {
						r.setNotes(String.format("'%s' template applied to this item", newsSection.getTemplate().getName())).fail();
					}
				}
				
				if (aboutSection != null) {
					addItem(aboutSection, "About us", "about-us", now, now, site, articleType, null);
				}
				
				if (mediaFolder != null) {
					addItem(mediaFolder, "Example image", "ex1", now, now, site, imageType, null);
				}
			}
			else {
				r.fail();
			}
		}		
		else {
			r.fail();
		}
		
		return trs;
	}
}
