package com.slepeweb.cms.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;

@Service
public class MediaTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet();
		List<TestResult> results = new ArrayList<TestResult>();
		trs.setResults(results);
		boolean testCompleted = false;
		String imageItemPath = "/media/ex1";
		
		// Set field values for first news item
		Site site = this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
		if (site != null) {
			Item imageItem = this.cmsService.getItemService().getItem(site.getId(), imageItemPath);
			
			if (imageItem != null) {
				String sourceImageFilename = "/home/george/test-image.jpg";
				long fileSize = new File(sourceImageFilename).length();

				if (! imageItem.hasMedia()) {
					int startCount = this.cmsService.getMediaService().getCount();
					imageItem.setMediaUploadFilePath(sourceImageFilename);
					this.cmsService.getMediaService().save(imageItem);
					
					// Assert 1 new row has been added to the media table
					results.add(r = new TestResult().setId(4010).setTitle("Check media table has a new row"));
					int endCount = this.cmsService.getMediaService().getCount();
					int diff = endCount - startCount;
					r.setNotes("Media table has " + diff + " new rows");
					if (diff != 1) {
						r.fail();
					}
				}
					
				// Assert media data can be read from db
				String retrievedFilePath = "/home/george/test-image-retrieved.jpg";
				results.add(r = new TestResult().setId(4020).setTitle("Get media data from db"));
				this.cmsService.getMediaService().writeMedia(imageItem.getId(), retrievedFilePath);
				File f = new File(retrievedFilePath);
				if (! f.exists()) {
					r.setNotes("Media output to file failed").fail();
				}
				else {
					r.setNotes("Output file size = " + f.length());
					if (f.length() != fileSize) {
						r.fail();
					}
				}
				
				testCompleted = true;
			}
		}
				
		trs.setSuccess(testCompleted);
		return trs;
	}
}
