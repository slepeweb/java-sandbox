package com.slepeweb.cms.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Repository
public class MediaDeliveryServiceImpl extends BaseServiceImpl implements MediaDeliveryService {
	private static Logger LOG = Logger.getLogger(MediaDeliveryServiceImpl.class);
	
	public void stream(Item item, HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String viewParam = req.getParameter("view");
		boolean thumbnailRequired = false;
		if (StringUtils.isNotBlank(viewParam)) {
			thumbnailRequired = viewParam.equals("thumbnail");
		}
		
		Media media = thumbnailRequired ? item.getThumbnail() : item.getMedia();
		LOG.info(String.format("Streaming media for item id %d, length %d bytes ...", item.getId(), media.getSize()));
		
		if (media == null || ! media.isBinaryContentLoaded()) {
			LOG.error(String.format("... No binary content found for media item", item));
			res.sendError(404);
			return;
		}
		
		// TODO: Temporary hack - this assumes all thumbnails are jpeg's
		res.setContentType(thumbnailRequired ? "image/jpeg" : item.getType().getMimeType());
		
		// With this header, there's no need to specify content length
		res.setHeader("Transfer-Encoding", "chunked");
		//res.setHeader("Content-Length", String.valueOf(media.getSize()));
		
		InputStream in = media.getDownloadStream();
		ServletOutputStream out = res.getOutputStream();
		
		if (in != null) {			
			try {
				int n = stream(in, out);
				
				if (n != media.getSize()) {
					LOG.error(String.format("... DISCREPANCY: streamed %d bytes for item %d", n, item.getId()));
				}
			}
			catch (Exception e) {
				LOG.error(String.format("... Error streaming media for id %d", item.getId()), e);
			}
			finally {
				try {
					in.close();
				}
				catch (Exception ee) {
					LOG.error("... Failed to close input stream");
				}
			}
		}
		else {
			LOG.warn(String.format("... No binary content for this media", item));
		}
	}
	
	private int stream(InputStream in, ServletOutputStream out) throws ServletException, IOException {

		byte[] buff = new byte[8192];
		int total = 0;
		int len;
		
		for (;;) {
			len = in.read(buff);
			if (len != -1) {
				total += len;
				out.write(buff, 0, len);
				out.flush();
			}
			else {
				LOG.info(".");
				return total;
			}
		}
	}	
}
