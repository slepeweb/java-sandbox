package com.slepeweb.cms.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.ItemType;

@Service
public class PurgeTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet();
		List<TestResult> results = new ArrayList<TestResult>();
		trs.setResults(results);
		boolean testCompleted = false;
		
		// Delete the News item type
		ItemType it = this.cmsService.getItemTypeService().getItemType(NEWS_TYPE_NAME);
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
		
		trs.setSuccess(testCompleted);
		return trs;
	}
}
