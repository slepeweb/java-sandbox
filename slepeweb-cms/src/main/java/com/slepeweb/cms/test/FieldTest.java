package com.slepeweb.cms.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;

@Service
public class FieldTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet();
		List<TestResult> results = new ArrayList<TestResult>();
		trs.setResults(results);
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
				
		trs.setSuccess(testCompleted);
		return trs;
	}
}
