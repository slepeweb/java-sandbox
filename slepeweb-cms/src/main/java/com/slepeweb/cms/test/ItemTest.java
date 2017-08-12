package com.slepeweb.cms.test;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.TagService;
import com.slepeweb.cms.utils.LogUtil;

@Service
public class ItemTest extends BaseTest {
	
	private static Logger LOG = Logger.getLogger(ItemTest.class);
	
	@Autowired TagService tagService;
		
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet("Item testbed").
				register(4010, "Move the About section to below the News section", "The News section should have 3 children").
				register(4020, "Check path of About article after the move", "Should be /news/about/about-us").
				register(4030, "Reverse previous move", "The News section should have 2 children").
				register(4040, "Re-check path of About article", "Should be /about/about-us").
				register(4050, "Add an inline to the About article", "Article should have 1 inline").
				register(4060, "Remove the inline from the About article", "Article should have 0 inlines").
				register(4070, "Check simplename change propagates to descendants", "First news item should be at /newz/101").
				register(4080, "Revert simplename change", "First news item should be at /news/101").
				register(4090, "Trash a branch and its descendants", "N more items should appear in the bin").
				register(4100, "Trashed branch should not appear as child of homepage", "Homepage should now have one less child").
				register(4110, "Restore top-level branch only", "the bin size should reduce by 1").
				register(4120, "Get parent of news item", "should be /news").
				register(4130, "Get parent of news section", "should be /").
				register(4140, "Tag item", "tags should be 'football' and 'cricket'").
				register(4150, "Update tags", "tag should be 'tennis' only").
				register(4160, "Get item by tag value", "item path should be /news/101");
		
		
		try {
			Site site = getTestSite();
			
			if (site == null) {
				LOG.warn("Failed to retrieve test site");
				return trs;
			}
			
			Item aboutSectionItem = site.getItem("/about");
			Item newsSectionItem = site.getItem("/news");
			Item rootItem = site.getItem("/");
			Long newsItemId_101 = site.getItem("/news/101").getId();
			Long newsItemId_102 = site.getItem("/news/102").getId();
			
			if (aboutSectionItem == null || newsSectionItem == null) {
				LOG.warn("Failed to retrieve either 'about' section or 'news' section");
			}
			else {	
				// Move the 'about' article item to the 'news' section
				if (aboutSectionItem.move(rootItem, newsSectionItem, newsSectionItem, false)) {				
					// 4010: Assert news section now has 3 children
					int count = newsSectionItem.getBoundItems().size();
					r = trs.execute(4010);
					r.setNotes("News section has " + count + " children");
					r.test(count == 3);
					
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
						if (article != null) {
							r.setNotes(LogUtil.compose("Path is", articlePath));
							r.test(articlePath.equals("/news/about/about-us"));
						}
						else {
							r.setNotes(LogUtil.compose("No item found with path", articlePath)).fail();
						}
					}
				
					// Restore the links
					if (aboutSectionItem != null && rootItem != null) {
						
						// Move the 'about' article item back to its original location
						if (aboutSectionItem.move(newsSectionItem, rootItem, rootItem, false)) {						
							// 4030: Assert news section has original 2 children
							newsSectionItem = site.getItem("/news");
							count = newsSectionItem.getBoundItems().size();
							r = trs.execute(4030);
							r.setNotes("News section has " + count + " children");
							r.test(count == 2);
							
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
							if (article != null) {
								r.setNotes(LogUtil.compose("Path is", articlePath));
								r.test(articlePath.equals("/about/about-us"));
							}
							else {
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
					r.setNotes(String.format("Article has %d inlines", inlines.size()));
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
				if (newsItem == null) {
						r.setNotes("No item found at " + newPath).fail();
				}
				else {
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
				
				// Trash the news branch an its descendants
				rootItem = site.getItem("/");
				newsSectionItem = site.getItem("/news");
				
				if (rootItem != null && newsSectionItem != null) {
					int bindingCount = rootItem.getBoundItems().size();
					int binCount = this.cmsService.getItemService().getBinCount();
					newsSectionItem.trash();
					
					// 4090: Assert bin has grown in size
					int diff = this.cmsService.getItemService().getBinCount() - binCount;
					int binCount2 = this.cmsService.getItemService().getBinCount();
					r = trs.execute(4090);
					r.setNotes(String.format("Bin has grown from %d to %d entries", binCount, binCount2));
					r.test(diff > 0);
					
					// 4100: Homepage should have 1 less children
					rootItem = site.getItem("/");
					diff = bindingCount - rootItem.getBoundItems().size();
					r = trs.execute(4100);
					r.setNotes(String.format("Homepage has %d less children", diff)).fail();
					r.test(diff == 1);
					
					// Restore the trashed section
					newsSectionItem.restore();
					
					// 4110: Assert bin size back to original
					int finalBinCount = this.cmsService.getItemService().getBinCount();
					r = trs.execute(4110);
					r.setNotes(String.format("Bin has reduced from %d to %d entries", binCount2, finalBinCount));
					r.test(finalBinCount == binCount2 - 1);
				}
			}
			
			// 4120
			// Restore 2 news items trashed earlier when parent folder was trashed
			Item newsItem = this.cmsService.getItemService().restoreItem(newsItemId_101);
			this.cmsService.getItemService().restoreItem(newsItemId_102);
			
			if (newsItem != null) {
				r = trs.execute(4120);
				Item newsSection = newsItem.getParent();
				
				if (newsSection != null) {
					r.setNotes(String.format("Parent path is [%s]", newsSection.getPath()));
					if (! newsSection.getPath().equals("/news")) {
						r.fail();
					}
					else {
						// 4130
						r = trs.execute(4130);
						Item root = newsSection.getParent();
						if (root != null) {
							r.fail().setNotes(String.format("Parent path is [%s]", root.getPath()));
							r.failIf(! root.getPath().equals("/"));
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
			
			// 4140
			List<String> tagsIn, tagsOut;
			newsItem = site.getItem("/news/101");
			if (newsItem != null) {
				r = trs.execute(4140);
				this.tagService.deleteTags(newsItem.getId());
				
				tagsIn = Arrays.asList("football", "cricket");
				this.tagService.save(newsItem, tagsIn);
				tagsOut = newsItem.getTags();
				if (tagsOut.size() != 2 || ! tagsOut.containsAll(tagsIn)) {
					r.fail().setNotes(String.format("Item incorrectly tagged [%s]", newsItem));
				}
				
			}
			
			// 4150
			newsItem = site.getItem("/news/101");
			String tennis = "tennis";
			if (newsItem != null) {
				r = trs.execute(4150);
				this.tagService.deleteTags(newsItem.getId());
				
				tagsIn = Arrays.asList(tennis);
				this.tagService.save(newsItem, tagsIn);
				tagsOut = newsItem.getTags();
				if (tagsOut.size() != 1 || ! tagsOut.containsAll(tagsIn)) {
					r.fail().setNotes(String.format("Item incorrectly tagged [%s]", newsItem));
				}
			}
			
			// 4160
			newsItem = site.getItem("/news/101");
			if (newsItem != null) {
				r = trs.execute(4160);
				Item taggedItem = this.tagService.getTaggedItem(site.getId(), tennis);
				
				if (taggedItem == null) {
					r.fail().setNotes(String.format("Item not found with tag [%s]", tennis));
				}
				else if (! taggedItem.getPath().equals("/news/101")) {
					r.fail().setNotes(String.format("Item found has different path [%s]", taggedItem.getPath()));
				}
			}	
		}
		catch (Exception e) {
			LOG.error("Unexpected exception", e);
		}
				
		return trs;
	}
}
