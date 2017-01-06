package com.slepeweb.cms.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.except.NotVersionableException;
import com.slepeweb.cms.service.ItemService;

@Service
public class VersionTest extends BaseTest {

	@Autowired
	private ItemService itemService;

	public TestResultSet execute() {

		TestResult r;
		TestResultSet trs = new TestResultSet("Versioning testbed").
				register(7010, "Create a new version of the news section", 
						"The News section should have a greater id").
				register(7020, "Check the version number of the new item", "It should have incremented by 1").
				register(7030, "Check the publishing status of the new item", "It should not be published").
				register(7035, "Check the editable status of the new item", "It should be editable").
				register(7040, "Check the publishing status of the original item", "It should NOT have changed").
				register(7045, "Check the editable status of the original item", "It should NOT be editable").
				register(7050, "Check the parent of the new item", "It should be the same as before").
				register(7060, "Check the children of the new item", "They should be the same as before");

		Site site = getTestSite();

		if (site == null) {
			return trs;
		}
		
		String newsSectionPath = "/news";
		Item newsSectionItem = site.getItem(newsSectionPath);

		r = trs.execute(7010);
		if (newsSectionItem == null) {
			r.setNotes("No item found at " + newsSectionPath).fail();
			return trs;
		}
		
		Item neu = null;
		try {
			neu = this.itemService.version(newsSectionItem);
			if (neu.getId() <= newsSectionItem.getId()) {
				r.setNotes(String.format("Item id anomoly: %d v. %d", newsSectionItem.getId(), neu.getId())).fail();
			}
			else {
				r.setNotes(String.format("Valid increment: %d -> %d", newsSectionItem.getId(), neu.getId()));
			}
		}
		catch (NotVersionableException e) {
			r.setNotes("Item is not versionable").fail();
			return trs;
		}
			
		r = trs.execute(7020);
		if (neu.getVersion() <= newsSectionItem.getVersion()) {
			r.setNotes(String.format("Item version anomoly: %d v. %d", newsSectionItem.getVersion(), neu.getVersion())).fail();
		}
		else {
			r.setNotes(String.format("Valid increment: %d -> %d", newsSectionItem.getVersion(), neu.getVersion()));
		}
		
		r = trs.execute(7030);
		if (neu.isPublished()) {
			r.setNotes(String.format("Version %d is published!", neu.getVersion())).fail();
		}

		Item parent = newsSectionItem.getParent();
		Item newsSectionItem_2 = site.getItem(newsSectionItem.getId());
		
		if (parent == null || newsSectionItem_2 == null) {
			return trs;
		}
		
		r = trs.execute(7035);
		if (! neu.isEditable()) {
			r.setNotes(String.format("Version %d is not editable!", neu.getVersion())).fail();
		}

		r = trs.execute(7040);
		if (newsSectionItem.isPublished() ^ newsSectionItem_2.isPublished()) {
			r.setNotes(String.format("The published status of the old version has changed [%s]!", 
					newsSectionItem_2.isPublished() ? "Published" : "Unpublished")).fail();
		}
		
		r = trs.execute(7045);
		if (newsSectionItem_2.isEditable()) {
			r.setNotes(String.format("Old version %d is still editable!", newsSectionItem_2.getVersion())).fail();
		}

		Item neuParent = neu.getParent();
		if (neuParent == null) {
			return trs;
		}
		
		r = trs.execute(7050);
		if (! neuParent.getId().equals(parent.getId())) {
			r.setNotes(String.format("The parent item has changed [%d -> %d]", parent.getId(), neuParent.getId())).fail();
		}
		
		return trs;
	}
}
