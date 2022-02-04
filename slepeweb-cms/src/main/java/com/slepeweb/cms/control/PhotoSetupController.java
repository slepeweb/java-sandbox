package com.slepeweb.cms.control;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.MediaService;
import com.slepeweb.cms.service.SiteService;
import com.slepeweb.common.util.ImageUtil;

@Controller
public class PhotoSetupController extends BaseController {
	private static Logger LOG = Logger.getLogger(PhotoSetupController.class);
	private static Pattern JPG_FILENAME_PATTERN = Pattern.compile("^(.*)?\\.(jpg|jpeg)$", Pattern.CASE_INSENSITIVE);
	
	@Autowired private SiteService siteService;
	@Autowired private ItemTypeService itemTypeService;
	@Autowired private ItemService itemService;
	@Autowired private MediaService mediaService;
	
	@RequestMapping(value="/setup/photos/{siteId}", produces="text/text")	
	@ResponseBody
	public String setupPhotos(@PathVariable long siteId) {
		Site s = this.siteService.getSite(siteId);
		Item rootItem = s.getItem("/content");
		String folderPath = "/media/george/Data/Photos";
		File rootFolder = new File(folderPath);
		ItemType contentFolder = this.itemTypeService.getItemType(ItemTypeName.CONTENT_FOLDER);
		ItemType photo = this.itemTypeService.getItemType(ItemTypeName.PHOTO_JPG);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		Data data = new Data().setSite(s).setFolderType(contentFolder).setPhotoType(photo).setNow(now);
		
		if (rootFolder.exists()) {
			try {
				crawlFileSystem(rootFolder, rootItem, data);
			}
			catch (ResourceException e) {
				LOG.error("Crawl failed:", e);
			}
			return "Setup complete";
		}
		else {
			LOG.error(String.format("Failed to open root folder [%s]", folderPath));
			return "Setup failed to start";
		}
	}	
	
	private boolean crawlFileSystem(File dir, Item parent, Data data) 
				throws ResourceException {
		
		Item child;
		String filename;
		Matcher matcher;
		
		/*
		if (data.getCount() > 10) {
			return false;
		}
		*/
		
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				child = createItemIfNotExists(parent, f.getName(), data.getFolderType(), data);
				
				if (! crawlFileSystem(f, child, data)) {
					return false;
				}
			}
			else {
				filename = f.getName();
				matcher = JPG_FILENAME_PATTERN.matcher(filename);
				
				if (matcher.matches()) {
					filename = matcher.group(1);
					child = createItemIfNotExists(parent, filename, data.getPhotoType(), data);
					uploadMediaIfNotExists(child, f);
				}
			}
		}
		
		return true;
	}
	
	private Item createItemIfNotExists(Item parent, String name, ItemType it, Data data) 
		throws ResourceException {
		
		String simpleName = name.toLowerCase().replaceAll("\\W", "");
		String path = parent.getPath() + "/" + simpleName;
		Item item = this.itemService.getItem(data.getSite().getId(), path);
		
		if (item != null) {
			// Item already exists
			LOG.info(String.format("Item already exists [%s]", name));
			return item;
		}
		
		// Item doesn't already exist; create it.
		item = CmsBeanFactory.makeItem(it.getName()).
			setName(name).
			setSimpleName(simpleName).
			setPath(path).
			setDateCreated(data.getNow()).
			setDateUpdated(data.getNow()).
			setSite(data.getSite()).
			setType(it).
			setParent(parent);
		
		item = this.itemService.save(item);
		data.incCounter();
		
		return item;
	}
	
	private boolean uploadMediaIfNotExists(Item item, File f) {
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		
		if (! item.hasMedia()) {
		
			// Load up the image data
			Media m = CmsBeanFactory.makeMedia();
			m.setItemId(item.getId());
			
			try {
				// Main image ...
				is = new FileInputStream(f);
				m.setUploadStream(is);
				this.mediaService.save(m);
				LOG.info(String.format("Saved media for '%s'", item.getName()));
				
				// ... and its thumbnail
				close(is);
				m.setThumbnail(true);
				is = new FileInputStream(f);							
				baos = new ByteArrayOutputStream();
				
				ImageUtil.streamScaled(
						is, baos, 
						200, 
						-1, 
						item.getType().getMimeType());
				
				m.setUploadStream(ImageUtil.pipe(baos));
				this.mediaService.save(m);
				LOG.info(String.format("Saved thumbnail for '%s'", item.getName()));
				
				return true;
			}
			catch (Exception e) {
				LOG.error("Failed to save media", e);
			}
			finally {
				close(is);
				close(baos);
			}			
		}
		else {
			LOG.info(String.format("Media already uploaded [%s]", item.getName()));
		}
		
		return false;
	}
	
	private void close(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		}
		catch (Exception e) {}
	}
	
	private void close(OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		}
		catch (Exception e) {}
	}
	
	static class Data {
		private Site site;
		private ItemType folderType, photoType;
		private Timestamp now;
		private int count;
		
		Site getSite() {
			return site;
		}
		
		Data setSite(Site site) {
			this.site = site;
			return this;
		}
		
		ItemType getFolderType() {
			return folderType;
		}
		
		Data setFolderType(ItemType folderType) {
			this.folderType = folderType;
			return this;
		}
		
		ItemType getPhotoType() {
			return photoType;
		}
		
		Data setPhotoType(ItemType photoType) {
			this.photoType = photoType;
			return this;
		}
		
		Timestamp getNow() {
			return now;
		}
		
		Data setNow(Timestamp now) {
			this.now = now;
			return this;
		}

		int getCount() {
			return count;
		}

		void setCount(int count) {
			this.count = count;
		}
		
		void incCounter() {
			this.count += 1;
		}
	}
}
