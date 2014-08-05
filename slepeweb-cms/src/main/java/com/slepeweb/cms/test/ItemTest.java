package com.slepeweb.cms.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.utils.LogUtil;

@Service
public class ItemTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet();
		List<TestResult> results = new ArrayList<TestResult>();
		trs.setResults(results);
		boolean testCompleted = false;
		
		Site site = getTestSite();
		
		if (site != null) {
			Item aboutSectionItem = site.getItem("/about");
			Item newsSectionItem = site.getItem("/news");
			
			if (aboutSectionItem != null && newsSectionItem != null) {
				
				// Move the 'about' article item to the 'news' section
				aboutSectionItem.move(newsSectionItem);
				
				// Assert news section now has 3 children
				results.add(r = new TestResult().setId(4010).setTitle("Check news section has 3 children"));
				int count = newsSectionItem.getBoundItems().size();
				r.setNotes("News section has " + count + " children");
				if (count != 3) {
					r.fail();
				}
				
				// Check path of article item has been updated
				results.add(r = new TestResult().setId(4020).setTitle("Check path of about article"));
				String articlePath = "/news/about/about-us";
				aboutSectionItem = site.getItem("/news/about");
				Item article = null;
				for (Item child : aboutSectionItem.getBoundItems()) {
					if (child.getPath().equals(articlePath)) {
						article = child;
						break;
					}
				}
				
				r.setNotes(LogUtil.compose("Article path should be", articlePath));
				if (article == null) {
					r.fail();
				}
				
				// Restore the links
				Item rootItem = site.getItem("/");
				
				if (aboutSectionItem != null && rootItem != null) {
					
					// Move the 'about' article item back to its original location
					aboutSectionItem.move(rootItem);
					
					// Assert news section has original 2 children
					results.add(r = new TestResult().setId(4030).setTitle("Reverse previous move"));
					newsSectionItem = site.getItem("/news");
					count = newsSectionItem.getBoundItems().size();
					r.setNotes("News section has " + count + " children");
					if (count != 2) {
						r.fail();
					}
					
					// Re-check path of article item
					results.add(r = new TestResult().setId(4030).setTitle("Re-check path of about article"));
					aboutSectionItem = site.getItem("/about");
					articlePath = "/about/about-us";
					article = null;
					for (Item child : aboutSectionItem.getBoundItems()) {
						if (child.getPath().equals(articlePath)) {
							article = child;
							break;
						}
					}
					
					r.setNotes(LogUtil.compose("Article path should be", articlePath));
					if (article == null) {
						r.fail();
					}
					
					testCompleted = true;
				}
				
				// Add an inline item to the article
				Item articleItem = site.getItem("/about/about-us");
				Item imageItem = site.getItem("/media/ex1");
				if (articleItem != null && imageItem != null) {
					articleItem.addInline(imageItem);
					articleItem.save();
					
					// Assert that article has an inline item
					results.add(r = new TestResult().setId(4040).setTitle("Check inline item has been added"));
					articleItem = site.getItem("/about/about-us");
					List<Item> inlines = articleItem.getInlineItems();
					if (! inlines.contains(imageItem)) {
						r.setNotes("Image item inline not added").fail();
					}
					
					// Remove inline to restore original state
					articleItem.removeInline(imageItem);
					articleItem.save();
					results.add(r = new TestResult().setId(4050).setTitle("Check inline item has been removed"));
					articleItem = site.getItem("/about/about-us");
					inlines = articleItem.getInlineItems();
					if (inlines.size() > 0) {
						r.setNotes("Image inlines still present").fail();
					}
					
					testCompleted = testCompleted && true;
				}
			}
		}
				
		trs.setSuccess(testCompleted);
		return trs;
	}
}
