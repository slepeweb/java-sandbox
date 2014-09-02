package com.slepeweb.cms.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class MediaServiceImpl extends BaseServiceImpl implements MediaService {
	
	private static Logger LOG = Logger.getLogger(MediaServiceImpl.class);
	
	public void save(Long itemId, InputStream is) {
		if (getMedia(itemId) != null) {
			updateMedia(itemId, is);
		}
		else {
			insertMedia(itemId, is);
		}
	}
	
	private void insertMedia(Long itemId, InputStream is) {
		this.jdbcTemplate.update(
				"insert into media (itemid, data) values (?, ?)", 
				itemId, getBytesFromStream(is));
		
		LOG.info(compose("Added new media", itemId));
	}

	private void updateMedia(Long itemId, InputStream is) {
		this.jdbcTemplate.update(
				"update media set data = ? where itemid = ?", 
				getBytesFromStream(is), itemId);
		
		LOG.info(compose("Updated media", itemId));
	}
	
	public void deleteMedia(Long id) {
		if (this.jdbcTemplate.update("delete from media where itemid = ?", id) > 0) {
			LOG.warn(compose("Deleted media", String.valueOf(id)));
		}
	}
	
	public boolean hasMedia(Item i) {
		return this.jdbcTemplate.queryForInt("select count(*) from media where itemid = ?", i.getId()) > 0;

	}

	public void writeMedia(Long id, String outputFilePath) {
		Blob blob = getMedia(id);
		
		if (blob != null) {
			BufferedInputStream is = null;
			FileOutputStream fos = null;
			
			try {
				is = new BufferedInputStream(blob.getBinaryStream());
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
	
	public Blob getMedia(Long id) {
		return (Blob) getFirstInList(
			this.jdbcTemplate.query("select data from media where itemid = ?", 
				new Object[]{id},
				new RowMapperUtil.MediaMapper()));
	}
	
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
