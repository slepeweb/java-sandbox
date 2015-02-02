package com.slepeweb.cms.test;

import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.utils.LogUtil;

@Service
public class ItemTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet("Item testbed").
			register(4010, "Move the About section to below the News section", 
					"The News section should have 3 children").
			register(4020, "Check path of About article after the move", "Should be /news/about/about-us").
			register(4030, "Reverse previous move", "The News section should have 2 children").
			register(4040, "Re-check path of About article", "Should be /about/about-us").
			register(4050, "Add an inline to the About article", "Article should have 1 inline").
			register(4060, "Remove the inline from the About article", "Article should have 0 inlines").
			register(4070, "Check simplename change propagates to descendants", "First news item should be at /newz/101").
			register(4080, "Revert simplename change", "First news item should be at /news/101").
			register(4090, "Trash a branch and its descendants", "3 new items should appear in the bin").
			register(4100, "Trashed branch should not appear as child of homepage", "Homepage should now have one less child").
			register(4110, "Restore trashed branch", "the bin size should return to original").
			register(4120, "Get parent of news item", "should be /news").
			register(4130, "Get parent of news section", "should be /");
		
		Site site = getTestSite();
		
		if (site != null) {
			Item aboutSectionItem = site.getItem("/about");
			Item newsSectionItem = site.getItem("/news");
			Item rootItem = site.getItem("/");
			
			if (aboutSectionItem != null && newsSectionItem != null) {
				
				// Move the 'about' article item to the 'news' section
				if (aboutSectionItem.move(rootItem, newsSectionItem, false)) {				
					// 4010: Assert news section now has 3 children
					int count = newsSectionItem.getBoundItems().size();
					r = trs.execute(4010);
					if (count != 3) {
						r.setNotes("News section has " + count + " children").fail();
					}
					
					// 4020: Check path of article item has been updated
					String articlePath = "/news/about/about-us";
					aboutSectionItem = site.getItem("/news/about");
					Item article = null;
					
					if (aboutSectionItem != null) {
						for (Item child : aboutSectionItem.getBoundItems()) {
							if (child.getPath().equals(articlePath)) {
								article = child;
								break;
							}
						}
						
						r = trs.execute(4020);
						if (article == null) {
							r.setNotes(LogUtil.compose("No item found with path", articlePath)).fail();
						}
					}
				
					// Restore the links
					if (aboutSectionItem != null && rootItem != null) {
						
						// Move the 'about' article item back to its original location
						if (aboutSectionItem.move(newsSectionItem, rootItem, false)) {						
							// 4030: Assert news section has original 2 children
							newsSectionItem = site.getItem("/news");
							count = newsSectionItem.getBoundItems().size();
							r = trs.execute(4030);
							if (count != 2) {
								r.setNotes("News section has " + count + " children").fail();
							}
							
							// 4040: Re-check path of article item
							aboutSectionItem = site.getItem("/about");
							articlePath = "/about/about-us";
							article = null;
							for (Item child : aboutSectionItem.getBoundItems()) {
								if (child.getPath().equals(articlePath)) {
									article = child;
									break;
								}
							}
							
							r = trs.execute(4040);
							if (article == null) {
								r.setNotes(LogUtil.compose("No item found with path", articlePath)).fail();
							}
						}
					}
				}
				
				// Add an inline item to the article
				Item articleItem = site.getItem("/about/about-us");
				Item imageItem = site.getItem("/content/media/ex1");
				if (articleItem != null && imageItem != null) {
					articleItem.addInline(imageItem);
					articleItem.saveLinks();
					
					// 4050: Assert that article has an inline item
					articleItem = site.getItem("/about/about-us");
					List<Item> inlines = articleItem.getInlineItems();
					r = trs.execute(4050);
					if (! inlines.contains(imageItem)) {
						r.setNotes("Image item inline not added").fail();
					}
					
					// Remove inline to restore original state
					articleItem.removeInline(imageItem);
					articleItem.saveLinks();
					
					// 4060: Assert inline has been removed
					articleItem = site.getItem("/about/about-us");
					inlines = articleItem.getInlineItems();
					r = trs.execute(4060);
					if (inlines.size() > 0) {
						r.setNotes("Image inlines still present").fail();
					}
					
					// Leave some links in place
					articleItem.addInline(imageItem);
					articleItem.saveLinks();
					newsSectionItem.addRelation(articleItem);
					newsSectionItem.saveLinks();
				}
				
				// Change simplename of news section
				newsSectionItem = site.getItem("/news");
				newsSectionItem.setSimpleName("newz");
				newsSectionItem.save();
				
				// 4070: Assert path of news item has changed
				String newPath = "/newz/101";
				Item newsItem = site.getItem(newPath);
				
				r = trs.execute(4070);
				if (newsItem != null) {
					if (! newPath.equals(newsItem.getPath())) {
						r.setNotes("Path is: " + newsItem.getPath()).fail();
					}
					else {
						// Revert simplename change
						newsSectionItem = site.getItem("/newz");
						newsSectionItem.setSimpleName("news");
						newsSectionItem.save();
						
						// 4080: Assert path of news item has changed
						newPath = "/news/101";
						newsItem = site.getItem(newPath);
						
						r = trs.execute(4080);
						if (newsItem != null) {
							if (! newPath.equals(newsItem.getPath())) {
								r.setNotes("Path is: " + newsItem.getPath()).fail();
							}
						}
						else {
							r.setNotes("No item found at " + newPath).fail();
						}
					}
				}
				else {
					r.setNotes("No item found at " + newPath).fail();
				}
				
				// Trash the news branch an its descendants
				rootItem = site.getItem("/");
				newsSectionItem = site.getItem("/news");
				
				if (rootItem != null && newsSectionItem != null) {
					int bindingCount = rootItem.getBoundItems().size();
					int binCount = this.cmsService.getItemService().getBinCount();
					newsSectionItem.trash();
					
					// 4090: Assert bin has grown in size
					int diff = this.cmsService.getItemService().getBinCount() - binCount;
					r = trs.execute(4090);
					if (diff != 3) {
						r.setNotes(String.format("Bin has %d new entries", diff)).fail();
					}
					
					// 4100: Homepage should have 1 less children
					rootItem = site.getItem("/");
					diff = bindingCount - rootItem.getBoundItems().size();
					r = trs.execute(4100);
					if (diff != 1) {
						r.setNotes(String.format("Homepage has %d less children", diff)).fail();
					}
					
					// Restore the trashed section
					newsSectionItem.restore();
					
					// 4110: Assert bin size back to original
					int finalBinCount = this.cmsService.getItemService().getBinCount();
					r = trs.execute(4110);
					if (finalBinCount != binCount) {
						r.setNotes(String.format("Bin has %d remaining entries", finalBinCount)).fail();
					}					
				}
			}
			
			// 4120
			Item newsItem = site.getItem("/news/101");
			if (newsItem != null) {
				r = trs.execute(4120);
				Item newsSection = newsItem.getParent();
				
				if (newsSection != null) {
					if (! newsSection.getPath().equals("/news")) {
						r.fail().setNotes(String.format("Parent path is [%s]", newsSection.getPath()));
					}
					else {
						// 4130
						r = trs.execute(4130);
						Item root = newsSection.getParent();
						if (root != null) {
							if (! root.getPath().equals("/")) {
								r.fail().setNotes(String.format("Parent path is [%s]", root.getPath()));
							}
						}
						else {
							r.fail().setNotes(String.format("Failed to identify parent of [%s]", newsSection));
						}
					}
				}
				else {
					r.fail().setNotes(String.format("Failed to identify parent of [%s]", newsItem));
				}
			}
		}
				
		return trs;
	}
}
