package com.slepeweb.cms.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;

@Repository
public class MediaDeliveryServiceImpl extends BaseServiceImpl implements MediaDeliveryService {
	private static Logger LOG = Logger.getLogger(MediaDeliveryServiceImpl.class);
	
	private final Object buffPoolLock = new Object();
	private java.lang.ref.WeakReference <List<byte[]>> buffPool;
	
	/**
	 * Get a buffer from pool, creating if necessary.
	 */
	private byte[] getBuff() {
		synchronized (this.buffPoolLock) {
			if (this.buffPool != null) {
				List<byte[]> list = this.buffPool.get();
				if (list != null && ! list.isEmpty()) {
					return list.remove(list.size() - 1);
				}
			}
			return new byte[4096];
		}
	}

	/**
	 * Put buffer back into pool.
	 */
	private void putBuff(byte[] buff) {
		if (buff == null) {
			return;
		}
		synchronized (this.buffPoolLock) {
			List<byte[]> list = (this.buffPool == null ? null : this.buffPool.get());
			if (list == null) {
				list = new java.util.ArrayList<byte[]>();
				this.buffPool = new java.lang.ref.WeakReference<List<byte[]>>(list);
			}
			list.add(buff);
		}
	}

	public void stream(Item item, HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String viewParam = req.getParameter("view");
		boolean thumbnailRequired = false;
		if (StringUtils.isNotBlank(viewParam)) {
			thumbnailRequired = viewParam.equals("thumbnail");
		}
		
		Media media = item.getMedia(thumbnailRequired);
		if (media == null) {
			LOG.error(String.format("No media found for item", item));
			return;
		}
		
		res.setContentType(item.getType().getMimeType());
		
		if (media.getBlob() != null) {
			InputStream in = null;
			
			try {
				in = media.getBlob().getBinaryStream();
				res.setHeader("Content-Length", String.valueOf(media.getSize()));
				stream(in, res.getOutputStream());
			}
			catch (SQLException e) {
				LOG.error(String.format("Error getting media", item), e);
			}
			finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}
	
	private void stream(InputStream in, ServletOutputStream out) throws ServletException, IOException {
		// Get a pooled buffer
		byte[] buff = getBuff();
		try {
			for (;;) {
				int len = in.read(buff);
				if (len == -1) {
					break;
				}
				out.write(buff, 0, len);
			}
		} finally {
			// Return buffer to the pool
			putBuff(buff);
		}
	}	
}