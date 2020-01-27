package com.slepeweb.site.servlet;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.StringWrapper;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.site.util.HttpUtil;

@Component
public class CmsDeliveryServlet {
	private static Logger LOG = Logger.getLogger(CmsDeliveryServlet.class);
	
	//private String[] bypass2DefaultPatterns = new String[] {"/\\w\\w/notfound", "/\\w\\w/error"};	
	private final Object buffPoolLock = new Object();
	private java.lang.ref.WeakReference <List<byte[]>> buffPool;
	private long defaultPrivateCacheTime, defaultPublicCacheTime;
	private Map<Long, Long> lastDeliveryTable = new HashMap<Long, Long>(127);

	@Autowired private CmsService cmsService;

	public void setBypass2Default(String s) {
		// This method should be called when Spring does its injection stuff, 
		// but it has been disabled for the foreseeable future.
//		this.bypass2DefaultPatterns = s != null ? s.split("\\|") : new String[] {};
	}
	
	private boolean bypass2Default(String path) {
		/*
		 * Forwarding the request to the default servlet is not working in this Spring
		 * environment, thereby breaking this bypass functionality.
		 * 
		*/
//		for (String regex : this.bypass2DefaultPatterns) {
//			if (path.matches(regex)) {
//				return true;
//			}
//		}
		return false;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		doGet(req, res, model);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		
		String path = getItemPath(req);
		String trimmedPath = path;
		boolean isBypass2Default = bypass2Default(path);
		long requestTime = System.currentTimeMillis();
		
		if (! isBypass2Default) {
			Site site = getSite(req);
			if (site != null) {
				req.setAttribute("_site", site);
				LOG.trace(LogUtil.compose("Site ...", site));

				String language = site.getLanguage();
				boolean redirect = false;
				
				if (site.isMultilingual() && ! path.equals("/favicon.ico")) {
					if (path.length() > 2) {
						String[] slugs = path.substring(1).split("/");
						if (slugs.length > 1 && slugs[0].length() == 2) {
							trimmedPath = path.substring(3);
							language = path.substring(1, 3);
						}
						else {
							redirect = true;
						}
					}
					else {
						redirect = true;
					}
				}
				
				if (redirect) {
					// language is missing on a multilingual site - redirect to default language
					res.sendRedirect(String.format("/%s%s", language, path));
					return;
				}
				
				Item item = site.getItem(trimmedPath);
				
				if (item != null) {
					LOG.info(LogUtil.compose("Requesting", item));
					item.setLanguage(language);
					logRequestHeaders(req, path);					
					long ifModifiedSince = getDateHeader(req, "If-Modified-Since");

					if (isCacheable(item) && isFresh(item, requestTime, ifModifiedSince, req.getMethod())) {
						res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
					}
					else {
						req.setAttribute("_item", item);
						setCacheHeaders(item, requestTime, res);
						
						if (item.getType().isMedia()) {
							LOG.debug(LogUtil.compose("Streaming binary content ...", item));
							stream(item, req, res, requestTime);
						}
						else {
							res.setContentType("text/html;charset=utf-8");
							res.setCharacterEncoding("utf-8");
							Template tmplt = item.getTemplate();
							String view = req.getParameter("view");
							
							if (tmplt != null) {
								String springMapping = StringUtils.isBlank(view) ? tmplt.getForward() : tmplt.getForward() + "/" + view;
								LOG.debug(LogUtil.compose("Forwarding request to template", springMapping));
								req.getRequestDispatcher(springMapping).forward(req, res);
								this.lastDeliveryTable.put(item.getId(), zeroMillis(requestTime));
							}
							else {
								notFound(res, "Item has no template", item);
							}
						}
					}
				}
				else {
					notFound(res, "Item not found", path);
				}
			}
			else {
				LOG.error(LogUtil.compose("Site not registered here", req.getServerName()));
			}
		}
		else {
			/*
			 * The bypass functionality has been effectively disabled, so this block of code
			 * should never get executed. It has been disabled, because forwarding to Tomcat's default
			 * servlet has been 'broken' by Spring's use of the 'default' servlet.
			 */
			LOG.debug(LogUtil.compose("Forwarding bypassed request to default servlet", path));
			HttpUtil.setCacheHeaders(requestTime, -1L, this.defaultPrivateCacheTime, this.defaultPublicCacheTime, res);
			req.getServletContext().getNamedDispatcher("default").forward(req, res);
		}
		
		logResponseHeaders(res, path);
	}
	
	private void logRequestHeaders(HttpServletRequest req, String path) {
		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format("REQUEST HEADERS (%s) >>>", path));
			Enumeration<String> enumer = req.getHeaderNames();
			String name;
			while (enumer.hasMoreElements()) {
				name = enumer.nextElement();
				if (matches(name, "pragma,cache-control,if-modified-since")) {
					LOG.trace(String.format("   %-20s: [%s]", name, req.getHeader(name)));
				}
			}
		}
	}
	
	private void logResponseHeaders(HttpServletResponse res, String path) {
		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format("RESPONSE HEADERS (%s) >>>", path));
			Iterator<String> enumer = res.getHeaderNames().iterator();
			String name;
			while (enumer.hasNext()) {
				name = enumer.next();
				if (matches(name, "cache-control,expires,last-modified,content-type")) {
					LOG.trace(String.format("   %-20s: [%s]", name, res.getHeader(name)));
				}
			}
		}
	}
	
	private boolean matches(String name, String delimited) {
		for (String target : delimited.split(",")) {
			if (name.toLowerCase().equals(target.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	private long zeroMillis(long millis) {
		return (millis / 1000) * 1000;
	}

	private boolean isFresh(Item i, long requestTime, long ifModifiedSince, String method) {
		boolean flag = false;
		boolean isMedia = i.getType().isMedia();
		long ttl = 0L;
		
		if (ifModifiedSince != -1L && ("GET".equals(method) || "HEAD".equals(method))) {
			requestTime = zeroMillis(requestTime);
			
			if (isMedia) {
				long lastModified = i.getDateUpdated().getTime();				
				flag = lastModified <= ifModifiedSince && 
						ifModifiedSince <= requestTime;
				LOG.trace(String.format("Media> isFresh: %s, lastModified: %d, ifModifiedSince: %d, requestTime: %d", 
						flag, lastModified, ifModifiedSince, requestTime));
			}
			else {
				/*
				long pageLastDelivered = getPageLastDeliveredDate(i, requestTime);
				long pageExpiry = pageLastDelivered + (i.getType().getPublicCache() * 1000);
				
				if (pageLastDelivered > -1L) {
					ttl = (pageExpiry - requestTime) / 1000;
					flag = requestTime < pageExpiry;
				}
				
				LOG.trace(String.format("Page> isFresh: %s, pageLastDelivered: %d, pageExpiry: %d, requestTime: %d", 
						flag, pageLastDelivered, pageExpiry, requestTime));
				*/
				
				/* 
				 * A page can never be considered fresh, since it's nigh-impossible to easily determine whether
				 * all its components are fresh.
				 */
				flag = false;
			}
		}
		
		StringBuilder sb = new StringBuilder("Content [%s] is ").append(flag ? "fresh" : "stale");
		if (! isMedia) {
			sb.append(" (%d secs to live)");
			LOG.debug(String.format(sb.toString(), i.getPath(), ttl));
		}
		else {
			LOG.debug(String.format(sb.toString(), i.getPath()));
		}

		return flag;
	}
	
	@SuppressWarnings("unused")
	private long getPageLastDeliveredDate(Item i, long requestTime) {
		Long l = this.lastDeliveryTable.get(i.getId());
		if (l != null) {
			return new Timestamp(l).getTime();
		}
		return -1L;
	}
	
	private void notFound(HttpServletResponse res, String msg, Object arg) throws Exception
    {
		LOG.error(LogUtil.compose(msg, arg));
		res.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
	
	private Site getSite(HttpServletRequest req) {
		String hostname = req.getServerName();
		Host h = this.cmsService.getHostService().getHost(hostname);
		if (h != null) {
			return h.getSite();
		}
		return null;
	}

	private String getItemPath(HttpServletRequest req) {
		String servletPath = req.getServletPath();
		servletPath = (servletPath == null) ? "" : servletPath;

		String pathInfo = req.getPathInfo();
		pathInfo = pathInfo != null ? servletPath + pathInfo : servletPath;
		return pathInfo;
	}
	
	
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

	private void stream(Item item, HttpServletRequest req, HttpServletResponse res, long requestTime)
			throws ServletException, IOException {

		Media media = this.cmsService.getMediaService().getMedia(item.getId());
		if (media == null) {
			LOG.error(String.format("No media found for item", item));
			return;
		}
		
		res.setContentType(item.getType().getMimeType());
		
		if (media.getBlob() != null) {
			InputStream in = null;
			
			try {
				in = media.getBlob().getBinaryStream();
				int width = getImageSizeParam(req.getParameter("width"));
				int height = getImageSizeParam(req.getParameter("height"));
				if (width == -1 && height == -1) {
					res.setHeader("Content-Length", String.valueOf(media.getSize()));
					streamOldSchool(in, res.getOutputStream());
				}
				else {
					streamScaled(in, res, width, height, item.getType().getMimeType());
				}
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
	
	private int getImageSizeParam(String value) {
		if (value == null) {
			return -1;
		}
		return Integer.parseInt(value); 
	}
	
	private void streamOldSchool(InputStream in, ServletOutputStream out) throws ServletException, IOException {
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
	
	private void streamScaled(InputStream in, HttpServletResponse res, int width, int height, String mediaType) 
			throws ServletException, IOException {
		
		// Create the tumbnail
		BufferedImage src = ImageIO.read(in);
		Image img = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);		
		BufferedImage thumb = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = thumb.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		int c = mediaType.lastIndexOf("/");
		String thumbType = mediaType.toLowerCase().substring(c + 1);
		if (thumbType.equals("jpeg")) {
			thumbType = "jpg";
		}
		
		// Write it to a temporary in-memory stream, so that you can work out the content length
	    ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
	    ImageIO.write(thumb, thumbType, tmpStream);
	    tmpStream.close();

	    // Write the temporary stream to the servlet output
	    res.setHeader("Content-Length", String.valueOf(tmpStream.size()));
	    OutputStream out = res.getOutputStream();
	    out.write(tmpStream.toByteArray());
	    out.close();
	}
	
	private boolean isCacheable(Item i) {
		return 
				this.cmsService.isLiveServer() &&
				i.isPublished() && 
				! i.getFieldValue(FieldName.CACHEABLE, new StringWrapper("yes")).equalsIgnoreCase("no");
	}

	private void setCacheHeaders(Item item, long requestTime, HttpServletResponse res) {

		long lastModified = item.getType().isMedia() ? 
				item.getDateUpdated().getTime() :
				requestTime;
				
		long publicCacheTime, privateCacheTime;
		
		if (! isCacheable(item)) {
			privateCacheTime = publicCacheTime = 0L;
		}
		else {
			privateCacheTime = item.getType().getPrivateCache() * 1000;
			publicCacheTime = item.getType().getPublicCache() * 1000;
		}

		HttpUtil.setCacheHeaders(requestTime, lastModified, privateCacheTime, publicCacheTime, res);
	}
	
	/**
	 * Need this method because IE generates If-modified-since headers of the
	 * form "Fri, 26 May 2000 15:34:20 GMT; length=544" (which I don't think is
	 * valid according to the HTTP RFC) and can cause the date parsing to go
	 * awry.
	 */
	private long getDateHeader(HttpServletRequest req, String header) {
		long failed = -1L;
		String value = req.getHeader(header);
		if (value == null) {
			return failed;
		} else {
			final int firstSemicolon = value.indexOf((int) ';');
			if (firstSemicolon != -1) {
				value = value.substring(0, firstSemicolon);
			}

			try {
				SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
				return sdf.parse(value).getTime();
			}
			catch (ParseException e) {
				LOG.error(String.format("Failed to parse date [%s]", value));
			}
			
			return failed;
		}
	}
	
	private long toLong(String s) {
		if (StringUtils.isNumeric(s)) {
			return Long.parseLong(s);
		}
		return 0L;
	}

	public long getDefaultPrivateCacheTime() {
		return defaultPrivateCacheTime;
	}

	public void setDefaultPrivateCacheTime(String s) {
		this.defaultPrivateCacheTime = toLong(s);
	}

	public long getDefaultPublicCacheTime() {
		return defaultPublicCacheTime;
	}

	public void setDefaultPublicCacheTime(String s) {
		this.defaultPublicCacheTime = toLong(s);
	}

}
