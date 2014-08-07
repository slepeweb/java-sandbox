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
		TestResultSet trs = new TestResultSet().
			register(4010, "Check news section has 3 children").
			register(4020, "Check path of 'about' article").
			register(4030, "Reverse previous move").
			register(4040, "Re-check path of 'about' article").
			register(4050, "Check inline item has been added").
			register(4060, "Check inline item has been removed").
			register(4070, "Check simplename change has propagated to descendants").
			register(4080, "Revert simplename change");
		
		Site site = getTestSite();
		
		if (site != null) {
			Item aboutSectionItem = site.getItem("/about");
			Item newsSectionItem = site.getItem("/news");
			
			if (aboutSectionItem != null && newsSectionItem != null) {
				
				// Move the 'about' article item to the 'news' section
				aboutSectionItem.move(newsSectionItem);
				
				// 4010: Assert news section now has 3 children
				int count = newsSectionItem.getBoundItems().size();
				r = trs.execute(4010).setNotes("News section has " + count + " children");
				if (count != 3) {
					r.fail();
				}
				
				// 4020: Check path of article item has been updated
				String articlePath = "/news/about/about-us";
				aboutSectionItem = site.getItem("/news/about");
				Item article = null;
				for (Item child : aboutSectionItem.getBoundItems()) {
					if (child.getPath().equals(articlePath)) {
						article = child;
						break;
					}
				}
				
				r = trs.execute(4020).setNotes(LogUtil.compose("Article path should be", articlePath));
				if (article == null) {
					r.fail();
				}
				
				// Restore the links
				Item rootItem = site.getItem("/");
				
				if (aboutSectionItem != null && rootItem != null) {
					
					// Move the 'about' article item back to its original location
					aboutSectionItem.move(rootItem);
					
					// 4030: Assert news section has original 2 children
					newsSectionItem = site.getItem("/news");
					count = newsSectionItem.getBoundItems().size();
					r = trs.execute(4030).setNotes("News section has " + count + " children");
					if (count != 2) {
						r.fail();
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
					
					r = trs.execute(4040).setNotes(LogUtil.compose("Article path should be", articlePath));
					if (article == null) {
						r.fail();
					}
				}
				
				// Add an inline item to the article
				Item articleItem = site.getItem("/about/about-us");
				Item imageItem = site.getItem("/content/media/ex1");
				if (articleItem != null && imageItem != null) {
					articleItem.addInline(imageItem);
					articleItem.save();
					
					// 4050: Assert that article has an inline item
					articleItem = site.getItem("/about/about-us");
					List<Item> inlines = articleItem.getInlineItems();
					r = trs.execute(4050);
					if (! inlines.contains(imageItem)) {
						r.setNotes("Image item inline not added").fail();
					}
					
					// Remove inline to restore original state
					articleItem.removeInline(imageItem);
					articleItem.save();
					
					// 4060: Assert inline has been removed
					articleItem = site.getItem("/about/about-us");
					inlines = articleItem.getInlineItems();
					r = trs.execute(4060);
					if (inlines.size() > 0) {
						r.setNotes("Image inlines still present").fail();
					}
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
					r.setNotes("Path is: " + newsItem.getPath());
					if (! newPath.equals(newsItem.getPath())) {
						r.fail();
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
							r.setNotes("Path is: " + newsItem.getPath());
							if (! newPath.equals(newsItem.getPath())) {
								r.fail();
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
				
			}
		}
				
		return trs;
	}
}
