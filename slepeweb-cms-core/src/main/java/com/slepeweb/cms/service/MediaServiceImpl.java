package com.slepeweb.cms.service;

import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.cms.bean.FileMetadata;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class MediaServiceImpl extends BaseServiceImpl implements MediaService {
	
	private static Logger LOG = Logger.getLogger(MediaServiceImpl.class);
	
	@Autowired private MediaFileService mediaFileService;
	
	public Media save(Long itemId, InputStream is, boolean isThumbnail) throws ResourceException {
		Media m = make(itemId, is, isThumbnail);		
		save(m);
		return m;
	}
	
	public Media make(Long itemId, InputStream is, boolean isThumbnail) {
		return CmsBeanFactory.makeMedia().
				setItemId(itemId).
				setUploadStream(is).
				setThumbnail(isThumbnail);
	}
	
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
						
		return m;
	}
	
	private void insertMedia(Media m) {
		// This call consumes the input stream then closes it.
		FileMetadata meta = this.mediaFileService.writeMediaToRepository(m);
		
		if (meta != null) {
			this.jdbcTemplate.update(
					"insert into media (itemid, size, folder, thumbnail) values (?, ?, ?, ?)", 
					m.getItemId(), meta.getSize(), meta.getBin(), m.isThumbnail());
			
			LOG.info(compose("Added new media", m.getItemId(), meta.getBin()));
		}
	}

	private void updateMedia(Media dbRecord, Media m) {
		
		// The Media.equals() method does not check whether the file content has changed
		// - assume it has
		m.setFolder(dbRecord.getFolder());
		FileMetadata meta = this.mediaFileService.writeMediaToRepository(m);
		
		// The new media item will probably have a different size
		dbRecord.setSize(meta.getSize());
		
		if (! dbRecord.equals(m)) {
			this.jdbcTemplate.update(
					"update media set size = ? where itemid = ? and thumbnail = ?", 
					meta.getSize(), dbRecord.getItemId(), dbRecord.isThumbnail());
			
			LOG.debug(compose("Updated " + (dbRecord.isThumbnail() ? "thumbnail" : "media"), 
					dbRecord.getItemId(), meta.getBin()));
		}
		else {
			LOG.info(compose("Media object not changed", m));
		}
	}
	
	private void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			}
			catch (Exception e) {}
		}
	}
	
	public boolean delete(Media m) {
		return delete(m.getItemId(), m.isThumbnail());
	}
	
	public boolean delete(Long id) {
		return delete(id, false);
	}
	
	public boolean delete(Long id, boolean thumbnail) {
		if (this.jdbcTemplate.update("delete from media where itemid = ? and thumbnail = ?", id, thumbnail) > 0) {
			LOG.warn(compose("Deleted media", String.valueOf(id), (thumbnail ? "thumbnail" : "")));
			return true;
		}
		return false;
	}
	
	public boolean hasMedia(Item i) {
		return this.jdbcTemplate.queryForObject("select count(*) from media where itemid = ?", Integer.class, i.getId()) > 0;

	}

	public boolean hasThumbnail(Item i) {
		return this.jdbcTemplate.queryForObject("select count(*) from media where itemid = ? and thumbnail = true", Integer.class, i.getId()) > 0;

	}

	// TODO: Currently, only used by MediaTest.java - review.
	public void writeMedia(Long itemId, String outputFilePath) {
		Media media = getMedia(itemId);
		
		if (media != null) {
			InputStream is = media.getDownloadStream();
			if (is != null) {
				this.mediaFileService.writeMedia(is, outputFilePath);
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
			this.jdbcTemplate.query("select itemid, size, folder, thumbnail from media where itemid = ? and thumbnail = ?", 
				new RowMapperUtil.MediaMapper(), id, thumbnail));
	}
	
	public List<Media> getAllMedia(Long id) {
		return this.jdbcTemplate.query("select itemid, size, folder, thumbnail from media where itemid = ?", 
				new RowMapperUtil.MediaMapper(), id);
	}
	
	public long getSize(Long id, boolean thumbnail) {
		return (Long) getFirstInList(
			this.jdbcTemplate.query("select size from media where itemid = ? and thumbnail = ?", 
				new RowMapperUtil.MediaSizeMapper(), id, thumbnail));
	}
	
	public int getCount() {
		return this.jdbcTemplate.queryForObject("select count(*) from media", Integer.class);
	}
	
	public void wipeBinaryContent(Media m) {
		this.jdbcTemplate.update(
				"update media set size = null where itemid = ? and thumbnail = ?", 
				m.getItemId(), m.isThumbnail());
		
		this.mediaFileService.wipeBinaryContent(m);
	}
}
