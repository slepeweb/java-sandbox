package com.slepeweb.site.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.site.constant.FieldName;

@Component
public class CmsDeliveryServlet {
	private static Logger LOG = Logger.getLogger(CmsDeliveryServlet.class);
	
	private String[] bypass2DefaultPatterns = new String[] {};	
	private final Object buffPoolLock = new Object();
	private java.lang.ref.WeakReference <List<byte[]>> buffPool;
	private long defaultPrivateCacheTime, defaultPublicCacheTime;
	private Map<Long, Long> lastDeliveryTable = new HashMap<Long, Long>(127);

	@Autowired private CmsService cmsService;

	public void setBypass2Default(String s) {
		this.bypass2DefaultPatterns = s != null ? s.split("\\|") : new String[] {};
	}
	
	private boolean bypass2Default(String path) {
		for (String regex : this.bypass2DefaultPatterns) {
			if (path.matches(regex)) {
				return true;
			}
		}
		return false;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		
		String path = getItemPath(req);
		boolean isBypass2Default = bypass2Default(path);
		long requestTime = System.currentTimeMillis();
		
		if (! isBypass2Default) {
			Site site = getSite(req);
			if (site != null) {
				req.setAttribute("_site", site);
				LOG.trace(LogUtil.compose("Site ...", site));
				Item item = site.getItem(path);
				
				if (item != null) {
					logRequestHeaders(req);					
					long ifModifiedSince = getDateHeader(req, "If-Modified-Since");

					if (isFresh(item, requestTime, ifModifiedSince, req.getMethod())) {
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
							Template tmplt = item.getTemplate();
							
							if (tmplt != null) {
								LOG.debug(LogUtil.compose("Forwarding request to template", tmplt.getForward()));
								req.getRequestDispatcher(tmplt.getForward()).forward(req, res);
								this.lastDeliveryTable.put(item.getId(), zeroMillis(requestTime));
							}
							else {
								notFound(req, res, "Item has no template", item);
							}
						}
					}
				}
				else {
					notFound(req, res, "Item not found", path);
				}
			}
			else {
				LOG.error(LogUtil.compose("Site not registered here", req.getServerName()));
			}
		}
		else {
			LOG.debug(LogUtil.compose("Forwarding bypassed request to default servlet", path));
			setCacheHeaders(requestTime, -1L, this.defaultPrivateCacheTime, this.defaultPublicCacheTime, res);
			req.getServletContext().getNamedDispatcher("default").forward(req, res);
		}
		
		logResponseHeaders(res);
	}
	
	private void logRequestHeaders(HttpServletRequest req) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("REQUEST HEADERS >>>");
			Enumeration<String> enumer = req.getHeaderNames();
			String name;
			while (enumer.hasMoreElements()) {
				name = enumer.nextElement();
				LOG.trace(String.format("   %-20s: [%s]", name, req.getHeader(name)));
			}
		}
	}
	
	private void logResponseHeaders(HttpServletResponse res) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("RESPONSE HEADERS >>>");
			Iterator<String> enumer = res.getHeaderNames().iterator();
			String name;
			while (enumer.hasNext()) {
				name = enumer.next();
				LOG.trace(String.format("   %-20s: [%s]", name, res.getHeader(name)));
			}
		}
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
				long pageLastDelivered = getPageLastDeliveredDate(i, requestTime);
				long pageExpiry = pageLastDelivered + (i.getType().getPublicCache() * 1000);
				
				if (pageLastDelivered > -1L) {
					ttl = (pageExpiry - requestTime) / 1000;
					flag = requestTime < pageExpiry;
				}
				
				LOG.trace(String.format("Page> isFresh: %s, pageLastDelivered: %d, pageExpiry: %d, requestTime: %d", 
						flag, pageLastDelivered, pageExpiry, requestTime));
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
	
	private long getPageLastDeliveredDate(Item i, long requestTime) {
		Long l = this.lastDeliveryTable.get(i.getId());
		if (l != null) {
			return new Timestamp(l).getTime();
		}
		return -1L;
	}
	
	private void notFound(HttpServletRequest req, HttpServletResponse res, String msg, Object arg) throws Exception
    {
		LOG.error(LogUtil.compose(msg, arg));
		res.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
	
	private Site getSite(HttpServletRequest req) {
		String host = req.getServerName();
		return this.cmsService.getSiteService().getSiteByHostname(host);
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
		
		res.setHeader("Content-Length", String.valueOf(media.getSize()));
		res.setContentType(item.getType().getMimeType());
		
		if (media.getBlob() != null) {
			final ServletOutputStream out = res.getOutputStream();
			InputStream in = null;
			
			try {
				in = media.getBlob().getBinaryStream();
		
				// We assume the JVM's memory management is efficient!
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
					putBuff(buff);
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
	
	private boolean isCacheable(Item i) {
		return 
				this.cmsService.isLiveServer() &&
				i.isPublished() && 
				! i.getFieldValue(FieldName.CACHEABLE, "yes").equalsIgnoreCase("no");
	}

	private void setCacheHeaders(Item item, long requestTime, HttpServletResponse res) {

		long lastModified = item.getDateUpdated().getTime();
		long publicCacheTime, privateCacheTime;
		
		if (! isCacheable(item)) {
			privateCacheTime = publicCacheTime = 0L;
		}
		else {
			privateCacheTime = item.getType().getPrivateCache() * 1000;
			publicCacheTime = item.getType().getPublicCache() * 1000;
		}

		setCacheHeaders(requestTime, lastModified, privateCacheTime, publicCacheTime, res);
	}
	
	private void setCacheHeaders(long requestTime, long lastModified, 
			long privateCacheTime, long publicCacheTime, HttpServletResponse res) {

		long expireTime;
		StringBuffer cacheControl = new StringBuffer();
		
		if (0L == privateCacheTime && 0L == publicCacheTime) {
			expireTime = requestTime;
			cacheControl.append("no-cache, s-maxage=0, max-age=0");
		} 
		else {			
			cacheControl.
				append("s-maxage=").append(publicCacheTime / 1000L).
				append(", max-age=").append(privateCacheTime / 1000L);
			expireTime = requestTime + publicCacheTime;
		}

		res.setHeader("Cache-Control", cacheControl.toString());
		res.setDateHeader("Expires", expireTime);
		if (lastModified > -1L) {
			res.setDateHeader("Last-Modified", lastModified);
		}
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
