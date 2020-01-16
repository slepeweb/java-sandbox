package com.slepeweb.cms.test;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.except.ResourceException;

@Service
public class MultilingualFieldTest extends BaseTest {
	
	public TestResultSet execute() {
		
		String secondLanguage = "fr";
		String titleEn = "About multilingual fields";
		String titleFr = "à propos de nous multilingual", teaserEn = "Find out more about multilingual Slepe Web Solutions Ltd";
		String greekWrite = "Γεια σου φίλε μου";

		TestResult r;
		TestResultSet trs = new TestResultSet("Multilingual Fields testbed").
			register(8010, "Check title field value in French", titleFr).
			register(8020, "Check empty French teaser replaced by default English", "Find out more about ...").
			register(8030, "Check item path is prefixed with language", secondLanguage).
			register(8040, "Check non-latin field value is captured ok", greekWrite);
		
		// Set field values for first news item
		Site site = this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
		if (site != null) {
			Item aboutItem = this.cmsService.getItemService().getItem(site.getId(), "/about/about-us");
			
			if (aboutItem != null) {
				aboutItem.setLanguage("fr");
				
				aboutItem.setFieldValue(TITLE_FIELD_NAME, titleEn);
				aboutItem.setFieldValue(TITLE_FIELD_NAME, titleFr, secondLanguage);
				aboutItem.setFieldValue(TEASER_FIELD_NAME, teaserEn);
				aboutItem.setFieldValue(TEASER_FIELD_NAME, "", secondLanguage);
				
				saveFieldValues(aboutItem);
				
				Item checkItem = this.cmsService.getItemService().getItem(aboutItem.getId());
				
				// We're interested in french field values
				checkItem.setLanguage(secondLanguage);
				
				// 8010: Assert title field value update
				String titleFieldValue = checkItem.getFieldValue(TITLE_FIELD_NAME);
				r = trs.execute(8010);
				if (titleFieldValue == null || ! titleFieldValue.equals(titleFr)) {
					r.setNotes(String.format("Wrong title field value [%s]", titleFieldValue)).fail();
				}
				
				// 8020: Assert empty french teaser field value defaults to english
				String teaserFieldValue = checkItem.getFieldValue(TEASER_FIELD_NAME);
				r = trs.execute(8020);
				if (teaserFieldValue == null || ! teaserFieldValue.equals(teaserEn)) {
					r.setNotes(String.format("Wrong teaser value [%s]", teaserFieldValue)).fail();
				}	
				
				// 8030: Item path is prefixed by language
				r = trs.execute(8030);
				if (! checkItem.getUrl().startsWith(String.format("/%s/", secondLanguage))) {
					r.setNotes(String.format("Wrong path [%s]", checkItem.getUrl())).fail();
				}	
				
				// Check non-latin field value is captured ok
				saveFieldValues(checkItem.setFieldValue(TEASER_FIELD_NAME, greekWrite, secondLanguage));
				checkItem = this.cmsService.getItemService().getItem(aboutItem.getId());
				
				// Again, we're interested in field values for the second language
				checkItem.setLanguage(secondLanguage);
				String greekRead = checkItem.getFieldValue(TEASER_FIELD_NAME);
				
				r = trs.execute(8040);
				if (! greekRead.equals(greekWrite)) {
					r.setNotes(String.format("Wrong value [%s]", greekRead)).fail();
				}	
			}
		}
				
		return trs;
	}
	
	private void saveFieldValues(Item i) {
		try {
			i.saveFieldValues();
		}
		catch (ResourceException e) {
		}
	}
}
