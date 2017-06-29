package com.slepeweb.cms.test;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.except.ResourceException;

@Service
public class FieldTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet("Fields testbed").
			register(3010, "Check title field value update").
			register(3020, "Check embargo date field value update");
		
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
				
				try {
					aboutItem.saveFieldValues();
				}
				catch (ResourceException e) {
				}
				
				// 3010: Assert title field value update
				Item checkItem = this.cmsService.getItemService().getItem(aboutItem.getId());
				FieldValue titleFieldValue = checkItem.getFieldValueObj(TITLE_FIELD_NAME);
				r = trs.execute(3010);
				if (titleFieldValue == null || ! titleFieldValue.getStringValue().equals(title)) {
					r.setNotes("Text field value update failed").fail();
				}
				
				// 3020: Assert embargo field value update
				FieldValue embargoFieldValue = checkItem.getFieldValueObj(EMBARGO_FIELD_NAME);
				r = trs.execute(3020);
				if (embargoFieldValue == null || ! embargoFieldValue.getDateValue().equals(ts)) {
					r.setNotes("Date value update failed").fail();
				}	
			}
		}
				
		return trs;
	}
}
