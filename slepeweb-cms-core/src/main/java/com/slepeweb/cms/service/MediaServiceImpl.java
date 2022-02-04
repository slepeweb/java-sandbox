package com.slepeweb.cms.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.FileMetadata;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.SiteConfig;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class MediaServiceImpl extends BaseServiceImpl implements MediaService {
	
	private static Logger LOG = Logger.getLogger(MediaServiceImpl.class);
	
	@Autowired private MediaFileService mediaFileService;
	@Autowired private SiteConfigService siteConfigService;
	@Autowired private ItemService itemService;
	
	public Media save(Media m) throws ResourceException {
		if (m.isDefined4Insert()) {
			Media dbRecord = getMedia(m.getItemId(), m.isThumbnail());
			if (dbRecord != null) {
				updateMedia(dbRecord, m);
			}
			else {
				insertMedia(m);
			}
		}
		else {
			String s = "Media not saved - insufficient data";
			LOG.error(compose(s, m));
			close(m.getUploadStream());
			throw new MissingDataException(s);
		}
						
		close(m.getUploadStream());
		return m;
	}
	
	private void insertMedia(Media m) {
		String storageMode = getStorageMode(m);
		
		// Where is the binary data for the media stored, the database or file store?
		if (storageMode == null || storageMode.equals("db")) {
			// This call consumes the input stream then closes it.
			byte[] bytes = getBytesFromStream(m.getUploadStream());
			
			this.jdbcTemplate.update(
					"insert into media (itemid, data, size, folder, thumbnail) values (?, ?, ?, ?, ?)", 
					m.getItemId(), bytes, bytes.length, null, m.isThumbnail());
			
			LOG.info(compose("Added new media (data stored in db)", m.getItemId()));
		}
		else {
			// Similarly, this call consumes the input stream then closes it.
			FileMetadata meta = writeMediaToRepository(m);
			
			if (meta != null) {
				this.jdbcTemplate.update(
						"insert into media (itemid, data, size, folder, thumbnail) values (?, ?, ?, ?, ?)", 
						m.getItemId(), null, meta.getSize(), meta.getBin(), m.isThumbnail());
				
				LOG.info(compose("Added new media (data stored in file repository)", m.getItemId(), meta.getBin()));
			}
		}
		
		this.cacheEvictor.evict(m);
		close(m.getUploadStream());
	}

	private void updateMedia(Media dbRecord, Media m) {
		if (! dbRecord.equals(m)) {
			this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(m);
			
			String storageMode = getStorageMode(m);
			
			// Where is the binary data for the media stored, the database or file store?
			if (storageMode == null || storageMode.equals("db")) {
				byte[] bytes = getBytesFromStream(m.getUploadStream());
				this.jdbcTemplate.update(
						"update media set data = ?, size = ? folder = null where itemid = ? and thumbnail = ?", 
						bytes, bytes.length, m.getItemId(), m.isThumbnail());
				
				LOG.info(compose("Updated media (data stored in db)", m.getItemId()));
			}
			else {
				FileMetadata meta = writeMediaToRepository(m);
				
				if (meta != null) {
					this.jdbcTemplate.update(
							"update media set data = null, size = ?, folder = ? where itemid = ? and thumbnail = ?", 
							meta.getSize(), meta.getBin(), m.getItemId(), m.isThumbnail());
					
					LOG.info(compose("Updated media (data stored in file repository)", m.getItemId(), meta.getBin()));
				}
			}
		}
		else {
			LOG.debug(compose("Media not changed", m));
		}
		
		close(m.getUploadStream());
	}
	
	private FileMetadata writeMediaToRepository(Media m) {
		BufferedInputStream is = new BufferedInputStream(m.getUploadStream());
		FileMetadata meta = this.mediaFileService.writeMediaToRepository(is, m.getRepositoryFileName());
		close(is);
		return meta;
	}
	
	private String getStorageMode(Media m) {
		Item i = this.itemService.getItem(m.getItemId());
		SiteConfig mode = this.siteConfigService.getSiteConfig(i.getSite().getId(), "media_storage_mode");
		return mode.getValue();
	}
	
	private void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			}
			catch (Exception e) {}
		}
	}
	
	/*
	 * This method isn't currently being called. Note that ordinarily, rows will be deleted from
	 * the media table automatically due to foreign key constraints on the item table.
	 * Might be useful for test purposes some time, so leaving in place.
	 */
	public void delete(Long id) {
		if (this.jdbcTemplate.update("delete from media where itemid = ?", id) > 0) {
			LOG.warn(compose("Deleted media", String.valueOf(id)));
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean hasMedia(Item i) {
		return this.jdbcTemplate.queryForInt("select count(*) from media where itemid = ?", i.getId()) > 0;

	}

	// TODO: Currently, only used by MediaTest.java - review.
	public void writeMedia(Long itemId, String outputFilePath) {
		Media media = getMedia(itemId);
		
		if (media != null) {
			InputStream is = media.getDownloadStream();
			if (is != null) {
				BufferedInputStream bis = new BufferedInputStream(is);
				this.mediaFileService.writeMedia(bis, outputFilePath);
			}
			else {
				LOG.error(compose("No data stream for media", itemId));
			}
		}
		else {
			LOG.warn(compose("No media for item", itemId));
		}
	}
	
	public Media getMedia(Long id) {
		return getMedia(id, false);
	}
	
	public Media getMedia(Long id, boolean thumbnail) {
		return (Media) getFirstInList(
			this.jdbcTemplate.query("select itemid, data, size, folder, thumbnail from media where itemid = ? and thumbnail = ?", 
				new Object[]{id, thumbnail},
				new RowMapperUtil.MediaMapper()));
	}
	
	public List<Media> getAllMedia(Long id) {
		return this.jdbcTemplate.query("select itemid, data, size, folder, thumbnail from media where itemid = ?", 
				new Object[]{id},
				new RowMapperUtil.MediaMapper());
	}
	
	public long getSize(Long id, boolean thumbnail) {
		return (Long) getFirstInList(
			this.jdbcTemplate.query("select size from media where itemid = ? and thumbnail = ?", 
				new Object[]{id, thumbnail},
				new RowMapperUtil.MediaSizeMapper()));
	}
	
	@SuppressWarnings("deprecation")
	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from media");
	}

	private byte[] getBytesFromStream(InputStream fis) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream(100);
			byte[] buffer = new byte[100];
			while (fis.read(buffer) > 0) {
				baos.write(buffer);
			}
			return baos.toByteArray();
		}
		catch (Exception e) {
			return new byte[] {};
		}
		finally {
			try {
				if (fis != null) fis.close();
				if (baos != null) baos.close();
			}
			catch (Exception e) {}
		}
	}
}
