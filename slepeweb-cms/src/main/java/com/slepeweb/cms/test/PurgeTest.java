package com.slepeweb.cms.test;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.ItemType;

@Service
public class PurgeTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet().
			register(1010, "Delete News type").
			register(1020, "Confirm all news items have been cascade-deleted").
			register(1030, "Check N field values have been cascade-deleted").
			register(1040, "Check N fieldfortype records have been cascade-deleted");
		
		// Delete the News item type
		ItemType it = this.cmsService.getItemTypeService().getItemType(NEWS_TYPE_NAME);
		
		// Assert item type has been deleted
		if (it == null) {
			trs.execute(1010).setNotes("News type not available for test").fail();
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
			r = trs.execute(1010).setNotes(diff + " item type(s) have been deleted");
			if (diff != 1) {
				r.fail();
			}
			
			// 1020: Check the number of news items that have been deleted
			diff = itemCount - this.cmsService.getItemService().getCount();
			r = trs.execute(1020).setNotes(diff + " news items have been deleted");
			if (diff != newsCount) {
				r.setNotes(diff + " news items have been deleted - should have been " + newsCount).fail();
			}
			
			// 1030: Check that field values have been cascade-deleted
			diff = fieldValueCount - this.cmsService.getFieldValueService().getCount();
			r = trs.execute(1030).setNotes(diff + " field value records have been deleted");
			if (diff <= 0) {
				r.fail();
			}
			
			// 1040: Check that fieldfortype records have been cascade-deleted
			diff = fieldForTypeCount - this.cmsService.getFieldForTypeService().getCount();
			r = trs.execute(1040).setNotes(diff + " fieldfortype records have been deleted");
			if (diff <= 0) {
				r.fail();
			}
		}
		
		return trs;
	}
}
