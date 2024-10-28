package com.slepeweb.cms.control;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.ItemTypeService;
import com.slepeweb.cms.service.MediaService;
import com.slepeweb.cms.service.SiteService;
import com.slepeweb.common.util.ImageUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PhotoSetupController extends BaseController {
	private static Logger LOG = Logger.getLogger(PhotoSetupController.class);
	private static Pattern MEDIA_FILENAME_PATTERN = Pattern.compile("^(IMG|VID|PXL)[-_](.*)?[-_].*?\\.(jpg|jpeg|mp4)$", Pattern.CASE_INSENSITIVE);
	private static Pattern DATE_PATTERN = Pattern.compile("^(\\d{4})(\\d{2})(\\d{2})$");
	
	@Autowired private SiteService siteService;
	@Autowired private ItemTypeService itemTypeService;
	@Autowired private ItemService itemService;
	@Autowired private MediaService mediaService;
	
	@RequestMapping(value="/setup/photos/{siteId}", produces="text/text")	
	@ResponseBody
	public String setupPhotos(@PathVariable long siteId, HttpServletRequest req) {
		Site s = this.siteService.getSite(siteId);
		Item rootItem = s.getItem("/content");
		String folderPath = req.getParameter("path");
		File rootFolder = new File(folderPath);
		
		Data data = new Data().
				setSite(s).
				setFolderType(this.itemTypeService.getItemType(ItemTypeName.CONTENT_FOLDER)).
				setPhotoType(this.itemTypeService.getItemType(ItemTypeName.PHOTO_JPG)).
				setVideoType(this.itemTypeService.getItemType(ItemTypeName.MOVIE_MP4)).
				setNow(new Timestamp(System.currentTimeMillis()));
		
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
		String dateStr, mediaType;
		Matcher matcher;
		
		/*
		if (data.getCount() > 10) {
			return false;
		}
		*/
		
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				child = createItemIfNotExists(parent, f.getName(), null, "FOLDER", data);
				
				if (! crawlFileSystem(f, child, data)) {
					return false;
				}
			}
			else {
				mediaType = null;
				dateStr = null;
				
				// Determine media type, ie. image or video
				if (f.getName().endsWith(".jpg") || f.getName().endsWith(".jpeg")) {
					mediaType = "IMG";
				}
				else if (f.getName().endsWith(".mp4")) {
					mediaType = "VID";
				}
				
				if (mediaType != null) {
					// Can we work out the date from the filename?
					matcher = MEDIA_FILENAME_PATTERN.matcher(f.getName());
					
					if (matcher.matches()) {
						dateStr = matcher.group(2);
					}
					
					// Create the item, and upload the media
					child = createItemIfNotExists(parent, f.getName(), dateStr, mediaType, data);
					uploadMediaIfNotExists(child, f, mediaType);
				}
			}
		}
		
		return true;
	}
	
	private Item createItemIfNotExists(Item parent, String filename, String dateStr, String mediaType, Data data) 
		throws ResourceException {
		
		ItemType itemType = null;
		
		if (mediaType.equals("FOLDER")) {
			itemType = data.getFolderType();
		}
		else if (mediaType.equals("VID")) {
			itemType = data.getVideoType();
		}
		else if (mediaType.equals("IMG")) {
			itemType = data.getPhotoType();
		}
		
		String simpleName = filename.toLowerCase().replaceAll("\\W", "");
		String path = parent.getPath() + "/" + simpleName;
		Item item = this.itemService.getItem(data.getSite().getId(), path);
		
		if (itemType == null) {
			LOG.info(String.format("Media type not recognised [%s]", mediaType));
			return item;
		}
		
		if (item != null) {
			LOG.info(String.format("Item already exists [%s]", item.getPath()));
			return item;
		}
		
		// Item doesn't already exist; create it.
		item = CmsBeanFactory.makeItem(itemType.getName()).
			setName(filename).
			setSimpleName(simpleName).
			setPath(path).
			setDateCreated(data.getNow()).
			setDateUpdated(data.getNow()).
			setSite(data.getSite()).
			setType(itemType).
			setParent(parent);
		
		boolean dateSpecified = false;
		String formattedDate = null;
		
		if (StringUtils.isNotBlank(dateStr)) {
			Matcher m = DATE_PATTERN.matcher(dateStr);
			dateSpecified = m.matches();
			
			if (dateSpecified) {
				formattedDate = String.format("%s/%s/%s", m.group(1), m.group(2), m.group(3));
			}
		}
		
		if (dateSpecified) {
			item.setName(dateStr);
		}
		
		// Save item
		item = this.itemService.save(item);
		
		// Save field values
		if (dateSpecified) {
			item.setFieldValue(FieldName.DATEISH, formattedDate);
			item.saveFieldValues();
		}
		
		data.incCounter();
		
		return item;
	}
	
	private boolean uploadMediaIfNotExists(Item item, File f, String mediaType) {
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		
		if (! item.hasMedia()) {
		
			// Load up the image data
			Media m = CmsBeanFactory.makeMedia();
			m.setItemId(item.getId());
			
			try {
				// Main media ...
				is = new FileInputStream(f);
				m.setUploadStream(is);
				this.mediaService.save(m);
				LOG.info(String.format("Saved media for '%s'", item.getName()));
				
				// ... and its thumbnail
				if (mediaType.equals("IMG")) {
					close(is);
					m.setThumbnail(true);
					is = new FileInputStream(f); // This will get closed in the finally block							
					
					m.setUploadStream(ImageUtil.scaleImage(is, 200, -1, item.getType().getMimeType()));
					this.mediaService.save(m);
					LOG.info(String.format("Saved thumbnail for '%s'", item.getName()));
				}
				
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
		private ItemType folderType, photoType, videoType;
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
		
		Data setPhotoType(ItemType t) {
			this.photoType = t;
			return this;
		}
		
		ItemType getVideoType() {
			return videoType;
		}
		
		Data setVideoType(ItemType t) {
			this.videoType = t;
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
