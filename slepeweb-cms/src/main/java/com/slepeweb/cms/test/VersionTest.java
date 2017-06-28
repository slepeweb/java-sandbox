package com.slepeweb.cms.test;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.NotVersionableException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.ItemService;

@Service
public class VersionTest extends BaseTest {
	
	private static Logger LOG = Logger.getLogger(VersionTest.class);
	private static String NEWS_SECTION_PATH = "/news";

	@Autowired
	private ItemService itemService;

	public TestResultSet execute() {

		TestResult r;
		TestResultSet trs = new TestResultSet("Versioning testbed").
				register(7005, "Should not be able to version an un-published item").
				register(7010, "Create a new version of the news section", "The News section should have a greater id").
				register(7020, "", "The new version should have incremented by 1").
				register(7030, "", "The new version should not be published").
				register(7035, "", "The new version should be editable").
				register(7040, "Check the original item", "Its should still be published").
				register(7045, "", "It should NOT be editable").
				register(7050, "Check the parent of the new item", "It should be the same as before").
				register(7060, "Check the children of the new item", "They should be the same as before").
				register(7070, "Trash the new item", "There should be N more entries in the bin").
				register(7080, "Restore the new item", "The bin size should be reduced by 2").
				register(7090, "Revert the new item", "It's version no. should be 1 less").
				register(7100, "", "It should be editable").
				register(7110, "", "Its status should NOT be published").
				register(7120, "", "The new version should no longer be in the db").
				register(7130, "", "The original version should be editable and accessible").
				register(7140, "Repeat creation of new version of the news section after emptying the bin", "There should"
						+ " be one new row in the item table").
				register(7150, "Remove this specific item from the bin", "The number of records in the item table "
						+ "should be 2 fewer");
		

		try {
			Site site = getTestSite();
	
			if (site == null) {
				return trs;
			}
			
			Item newsSectionItem = site.getItem(NEWS_SECTION_PATH);
	
			if (newsSectionItem == null) {
				LOG.warn("No item found at " + NEWS_SECTION_PATH);
				return trs;
			}
			
			// Try versioning an un-published item
			newsSectionItem.setPublished(false).save();
			r = trs.execute(7005);
			Item newVersionOfNewsSection = versionItem(r, newsSectionItem);
			r.test(newVersionOfNewsSection == null);
			
			if (! trs.isSuccess(7005)) {
				return trs;
			}
			
			// No publish the item and try again
			newsSectionItem.setPublished(true).save();		
			
			// We now have an instance of the newsSectionItem BEFORE it is versioned
			r = trs.execute(7010);
			newVersionOfNewsSection = versionItem(r, newsSectionItem);
			r.test(newVersionOfNewsSection.getId() > newsSectionItem.getId());
			
			if (trs.isSuccess(7010)) {			
				r = trs.execute(7020);
				r.setNotes(String.format("Item version is: %d", newVersionOfNewsSection.getVersion()));
				r.test(newVersionOfNewsSection.getVersion() > newsSectionItem.getVersion());
				
				r = trs.execute(7030);
				r.setNotes(String.format("Version %d is %s", newVersionOfNewsSection.getVersion(), newVersionOfNewsSection.isPublished() ? "published" : "NOT published"));
				r.failIf(newVersionOfNewsSection.isPublished());
		
				r = trs.execute(7035);
				r.setNotes(String.format("Version %d is %s", newVersionOfNewsSection.getVersion(), newVersionOfNewsSection.isEditable() ? "editable" : "NOT editable"));
				r.test(newVersionOfNewsSection.isEditable());
		
				// We now need an instance of the newsSectionItem AFTER it was versioned
				newsSectionItem = this.itemService.getItem(newsSectionItem.getOrigId(), newsSectionItem.getVersion());
				r = trs.execute(7040);
				r.setNotes(String.format("Version %d is %s", newsSectionItem.getVersion(), newsSectionItem.isPublished() ? "published" : "NOT published"));
				r.test(newsSectionItem.isPublished());
				
				r = trs.execute(7045);
				r.setNotes(String.format("Version %d is %s", newsSectionItem.getVersion(), newsSectionItem.isEditable() ? "editable" : "NOT editable"));
				r.failIf(newsSectionItem.isEditable());
	
				Item neuParent = newVersionOfNewsSection.getParent();
				Item parent = newsSectionItem.getParent();
				
				r = trs.execute(7050);
				r.setNotes(String.format("The parent id of the new version of the news section is %d", neuParent.getId()));
				r.test(neuParent.getId().equals(parent.getId()));
				
				r = trs.execute(7060);
				List<Link> origChildren = newsSectionItem.getBindings();
				List<Link> neuChildren = newVersionOfNewsSection.getBindings();
				boolean ok = (origChildren.size() == neuChildren.size());
				if (ok) {
					for (int i = 0; i < origChildren.size(); i++) {
						if (neuChildren.get(i).getChild().getId().longValue() != origChildren.get(i).getChild().getId().longValue()) {
							ok = false;
							break;
						}
					}
					
					if (! ok) {
						r.setNotes("Children are different");
					}
				}
				else {
					r.setNotes(String.format("Child numbers differ [%d -> %d]", origChildren.size(), neuChildren.size()));
				}
				
				r.test(ok);
				
				int binCount = this.cmsService.getItemService().getBinCount();
				newVersionOfNewsSection.trash();
				
				// 7070: Assert bin has grown in size
				int binCount2 = this.cmsService.getItemService().getBinCount();
				r = trs.execute(7070);
				r.setNotes(String.format("Bin has grown from %d to %d entries", binCount, binCount2));
				r.test((binCount2 - binCount) > 0);
						
				// Restore the trashed section
				newVersionOfNewsSection.restore();
				
				// 7080: Assert bin size back to original
				int finalBinCount = this.cmsService.getItemService().getBinCount();
				r = trs.execute(7080);
				r.setNotes(String.format("Bin has reduced from %d to %d entries", binCount2, finalBinCount));
				r.test(finalBinCount == binCount2 - 2);
				
				// 7090: Revert the new item
				r = trs.execute(7090);
				Item revertedItem = this.itemService.revert(newVersionOfNewsSection);
				r.setNotes(String.format("Reverted version is %d", revertedItem.getVersion()));
				r.test(revertedItem.getVersion() == newsSectionItem.getVersion());
				
				// 7100: It should be editable
				r = trs.execute(7100);
				r.setNotes(String.format("Status is %s", revertedItem.isEditable() ? "editable" : "NOT editable"));
				r.test(revertedItem.isEditable());
				
				// 7110: Its status should NOT be published
				r = trs.execute(7110);
				r.setNotes(String.format("Status is %s", revertedItem.isPublished() ? "published" : "NOT published"));
				r.failIf(revertedItem.isPublished());
				
				// 7120: The new version should no longer be in the db
				r = trs.execute(7120);
				r.test(this.itemService.getItem(revertedItem.getOrigId(), revertedItem.getVersion() + 1) == null);
				r.setNotes(String.format("(Tried to get version %d)", revertedItem.getVersion() + 1));
				
				// 7130: The original version should be editable and accessible
				r = trs.execute(7130);
				newsSectionItem = site.getItem(NEWS_SECTION_PATH);
				r.test(newsSectionItem != null);
			}
		}
		catch (Exception e) {
			LOG.warn("Unexpected exception", e);
		}
		return trs;
	}
	
	private Item versionItem(TestResult r, Item item) {
		Item neu = null;
		try {
			neu = this.itemService.version(item);
			r.setNotes(String.format("New version is: %d", neu.getVersion()));
			r.failIf(neu.getId() <= item.getId());
		}
		catch (NotVersionableException e) {
			r.setNotes("Item is not versionable").fail();
			return null;
		}
		catch (MissingDataException e) {
			r.setNotes("Item data missing - could not create new version").fail();
			return null;
		}
		catch (DuplicateItemException e) {
			r.setNotes("Item already exists at new version").fail();
			return null;
		}
		catch (ResourceException e) {
			r.setNotes("Resource exception").fail();
			return null;
		}
		
		return neu;
	}	
}
