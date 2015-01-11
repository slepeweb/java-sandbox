package com.slepeweb.cms.test;

import java.io.File;
import java.io.FileInputStream;

import org.springframework.stereotype.Service;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.Site;

@Service
public class MediaTest extends BaseTest {
	
	public TestResultSet execute() {
		
		TestResult r;
		TestResultSet trs = new TestResultSet("Media testbed").
			register(4010, "Check media table has a new row").
			register(4020, "Get media data from db");
		
		String imageItemPath = "/content/media/ex1";
		
		// Set field values for first news item
		Site site = this.cmsService.getSiteService().getSite(TEST_SITE_NAME);
		if (site != null) {
			Item imageItem = this.cmsService.getItemService().getItem(site.getId(), imageItemPath);
			
			if (imageItem != null) {
				String sourceImageFilename = "/home/george/test-image.jpg";
				long fileSize = new File(sourceImageFilename).length();

				if (! imageItem.hasMedia()) {
					int startCount = this.cmsService.getMediaService().getCount();
					try { 
						Media m = CmsBeanFactory.makeMedia().
								setItemId(imageItem.getId()).
								setInputStream(new FileInputStream(sourceImageFilename)).
								setSize(fileSize);
						this.cmsService.getMediaService().save(m);
					}
					catch (Exception e) {}
					
					// 4010: Assert 1 new row has been added to the media table
					int endCount = this.cmsService.getMediaService().getCount();
					int diff = endCount - startCount;
					r = trs.execute(4010).setNotes("Media table has " + diff + " new rows");
					if (diff != 1) {
						r.fail();
					}
				}
					
				// 4020: Assert media data can be read from db
				String retrievedFilePath = "/home/george/test-image-retrieved.jpg";
				this.cmsService.getMediaService().writeMedia(imageItem.getId(), retrievedFilePath);
				File f = new File(retrievedFilePath);
				r = trs.execute(4020);
				if (! f.exists()) {
					r.setNotes("Media output to file failed").fail();
				}
				else {
					r.setNotes("Output file size = " + f.length());
					if (f.length() != fileSize) {
						r.fail();
					}
				}
			}
		}
				
		return trs;
	}
}
