package com.slepeweb.cms.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class MediaServiceImpl extends BaseServiceImpl implements MediaService {
	
	private static Logger LOG = Logger.getLogger(MediaServiceImpl.class);
	
	public Media save(Media m) {
		if (m.isDefined4Insert()) {
			Media dbRecord = getMedia(m.getItemId());
			if (dbRecord != null) {
				updateMedia(dbRecord, m);
			}
			else {
				insertMedia(m);
			}
		}
		else {
			LOG.error(compose("Media not saved - insufficient data", m));
		}
		
		return m;
	}
	
	private void insertMedia(Media m) {
		this.jdbcTemplate.update(
				"insert into media (itemid, data, size) values (?, ?, ?)", 
				m.getItemId(), getBytesFromStream(m.getInputStream()), m.getSize());
		
		this.cacheEvictor.evict(m);
		LOG.info(compose("Added new media", m.getItemId()));
	}

	private void updateMedia(Media dbRecord, Media m) {
		if (! dbRecord.equals(m)) {
			this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(m);
			
			this.jdbcTemplate.update(
					"update media set data = ?, size = ? where itemid = ?", 
					getBytesFromStream(m.getInputStream()), m.getSize(), m.getItemId());
			
			LOG.info(compose("Updated media", m.getItemId()));
		}
		else {
			LOG.debug(compose("Media not changed", m));
		}
	}
	
	public void delete(Long id) {
		if (this.jdbcTemplate.update("delete from media where itemid = ?", id) > 0) {
			LOG.warn(compose("Deleted media", String.valueOf(id)));
		}
	}
	
	@SuppressWarnings("deprecation")
	public boolean hasMedia(Item i) {
		return this.jdbcTemplate.queryForInt("select count(*) from media where itemid = ?", i.getId()) > 0;

	}

	public void writeMedia(Long id, String outputFilePath) {
		Media media = getMedia(id);
		
		if (media != null) {
			BufferedInputStream is = null;
			FileOutputStream fos = null;
			
			try {
				is = new BufferedInputStream(media.getBlob().getBinaryStream());
				fos = new FileOutputStream(outputFilePath);
				int bufflen = 1000;
				byte[] buffer = new byte[bufflen];
				int numBytes;
				while ((numBytes = is.read(buffer, 0, bufflen)) > -1) {
					fos.write(buffer, 0, numBytes);
				}
			}
			catch (Exception e) {
				LOG.warn(compose("Error writing media out to file", outputFilePath), e);
			}
			finally {
				try {
					if (fos != null) fos.close();
					if (is != null) is.close();
				}
				catch (Exception e) {}
			}
		}
		else {
			LOG.warn(compose("No media for item", id));
		}
	}
	
	public Media getMedia(Long id) {
		return (Media) getFirstInList(
			this.jdbcTemplate.query("select itemid, data, size from media where itemid = ?", 
				new Object[]{id},
				new RowMapperUtil.MediaMapper()));
	}
	
	public long getSize(Long id) {
		return (Long) getFirstInList(
			this.jdbcTemplate.query("select size from media where itemid = ?", 
				new Object[]{id},
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
