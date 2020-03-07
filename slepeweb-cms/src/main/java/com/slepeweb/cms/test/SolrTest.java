package com.slepeweb.cms.test;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.SolrDocument4Cms;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.SolrService4Cms;
import com.slepeweb.cms.service.TemplateService;

@Service
public class SolrTest extends BaseTest {
	
	private static Logger LOG = Logger.getLogger(SolrTest.class);
	private static String TEST_ITEM = "/news/101";
	private static String BEFORE_TEXT = "Before";
	private static String AFTER_TEXT = "After";

	@Autowired private ItemService itemService;
	@Autowired private SolrService4Cms solrService;
	@Autowired private TemplateService templateService;

	public TestResultSet execute() {

		TestResult r;
		TestResultSet trs = new TestResultSet("Solr index and retrieval testbed").
				register(8010, "Examine a new, non-published item", "There shouldn't be a corresponding document in Solr").
				register(8020, "Publish the item", "A document should have been created in Solr").
				register(8030, "Trash the item", "The Solr document should also have been removed").
				register(8040, "Restore the item", "The document should NOT yest have re-appeared").
				register(8050, "Publish the item", "It should now be in Solr").
				register(8060, "Version the item, and update the new version", "The document in Solr should be unchanged").
				register(8070, "Publish the new version", "The change should be reflected in Solr").
				register(8080, "Revert the item to the previous version", "The earlier change should also be backed out").
				register(8090, "Make the item un-searchable", "The Solr entry should be removed");		

		try {
			Site site = getTestSite();
	
			if (site == null) {
				return trs;
			}
			
			Item testItem = site.getItem(TEST_ITEM);
			if (testItem == null) {
				LOG.warn("No item found at " + TEST_ITEM);
				return trs;
			}		
			
			// Clean out all documents for this site
			this.solrService.remove(site);
			
			// Check item not already indexed
			r = trs.execute(8010);
			SolrDocument4Cms doc = getDocument(testItem);
			r.test(doc == null);
			
			// Apply template if necessary
			if (testItem.getTemplate() == null) {
				testItem = testItem.setTemplate(this.templateService.getTemplate(site.getId(), NEWS_TEMPLATE_NAME)).save();
			}
			
			// Re-set test item, if necessary
			if (testItem.isPublished() || ! testItem.isSearchable()) {
				testItem = testItem.setPublished(false).setSearchable(true).save();
			}
			
			// No publish the item and try again
			r = trs.execute(8020);
			testItem = testItem.setPublished(true).save();
			doc = getDocument(testItem);
			r.test(doc != null);
			
			// Trash the item
			r = trs.execute(8030);
			this.itemService.trashItem(testItem.getId());
			doc = getDocument(testItem);
			r.test(doc == null);
			
			// Restore the item
			r = trs.execute(8040);
			testItem = this.itemService.restoreItem(testItem.getId());
			doc = getDocument(testItem);
			r.test(doc == null);

			// Publish the item
			r = trs.execute(8050);
			testItem.setPublished(true);
			testItem = this.itemService.save(testItem);
			doc = getDocument(testItem);
			r.test(doc != null);
			
			// Version the item
			r = trs.execute(8060);
			Item originalVersion = testItem.setFieldValue(TITLE_FIELD_NAME, BEFORE_TEXT).save(true);
			Item newVersion = this.itemService.version(originalVersion).setFieldValue(TITLE_FIELD_NAME, AFTER_TEXT).save(true);
			// The new version should be un-published, so not indexable. So the document we get
			// should match the original version
			doc = getDocument(newVersion);
			r.test(doc.getTitle().equals(BEFORE_TEXT));
			r.setNotes(String.format("Document title = '%s'", doc.getTitle()));
			
			// Publish the new version
			r = trs.execute(8070);
			newVersion = newVersion.setPublished(true).save();
			doc = getDocument(newVersion);
			r.test(doc.getTitle().equals(AFTER_TEXT));
			r.setNotes(String.format("Document title = '%s'", doc.getTitle()));
			
			// Revert to the previous version
			r = trs.execute(8080);
			testItem = this.itemService.revert(newVersion);
			doc = getDocument(testItem);
			r.test(doc.getTitle().equals(BEFORE_TEXT));
			r.setNotes(String.format("Document title = '%s'", doc.getTitle()));
			
			// Make the item un-searchable
			r = trs.execute(8090);
			testItem = testItem.setSearchable(false).save();
			doc = getDocument(testItem);
			r.test((testItem.getVersion() == 1 && doc == null) || (testItem.getVersion() > 1 && doc != null));
			r.setNotes(String.format("Item is version %d, and doc %s null", testItem.getVersion(), doc == null ? "is" : "is not"));
		}
		catch (Exception e) {
			LOG.warn("Unexpected exception", e);
		}
		return trs;
	}
	
	private SolrDocument4Cms getDocument(Item testItem) throws InterruptedException {
		Thread.sleep(1000);
		Object o = this.solrService.getDocument(testItem);
		if (o instanceof SolrDocument4Cms) {
			return (SolrDocument4Cms) o;
		}
		return null;
	}
}
