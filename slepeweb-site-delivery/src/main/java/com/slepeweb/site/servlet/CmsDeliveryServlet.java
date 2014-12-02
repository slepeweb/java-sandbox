package com.slepeweb.site.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.utils.LogUtil;

@Component
public class CmsDeliveryServlet {
	private static Logger LOG = Logger.getLogger(CmsDeliveryServlet.class);
	
	private String[] bypassPatterns = "/jaxws/.*|.*?\\.wsdl|.*?\\.dtd".split("\\|");
	private final Object buffPoolLock = new Object();
	private java.lang.ref.WeakReference <List<byte[]>> buffPool;

	@Autowired private CmsService cmsService;

	private boolean bypass(String path) {
		for (String regex : this.bypassPatterns) {
			if (path.matches(regex)) {
				return true;
			}
		}
		return false;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		
		// Forward to default servlet if path matches pattern
		final String path = getItemPath(req);
		if (! bypass(path)) {
			Site site = getSite(req);
			if (site != null) {
				Item item = site.getItem(path);
				
				if (item != null) {
					req.setAttribute("_item", item);
					
					if (item.getType().isMedia()) {
						LOG.debug(LogUtil.compose("Streaming binary content ...", item));
						stream(item, req, res);
					}
					else {
						Template tmplt = item.getTemplate();
						if (tmplt != null) {
							LOG.debug(LogUtil.compose("Forwarding request to template", tmplt.getForward()));
							req.getRequestDispatcher(tmplt.getForward()).forward(req, res);
						}
						else {
							notFound(res, "Item has no template", item);
						}
					}
				}
				else {
					notFound(res, "Item not found", path);
				}
			}
			else {
				notFound(res, "Site not found", req.getServerName());
			}
		}
		else {
			LOG.debug(LogUtil.compose("Forwarding request to default servlet", path));
			req.getServletContext().getNamedDispatcher("default").forward(req, res);
//			req.getRequestDispatcher("default").forward(req, res);
		}
	}

	private void notFound(HttpServletResponse res, String msg, Object arg) throws IOException
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

	private void stream(Item item, HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		long lastModified = item.getDateUpdated().getTime();
		long publicCacheTime = 20 * 60 * 1000L; // 20 minutes
		long cacheTime = publicCacheTime;
		long requestTime = System.currentTimeMillis();
		long expireTime;
		StringBuffer cacheControl = new StringBuffer();

		if (0 == cacheTime && 0 == publicCacheTime) {
			expireTime = requestTime;
			cacheControl.append("no-cache, s-maxage=0, max-age=0");
		} 
		else {
			long privateExpireTimeMillis;
			long publicExpireTimeMillis;
			
			if (0 == cacheTime) {
				privateExpireTimeMillis = requestTime;
			} 
			else {
				// Here [private|public]ExpireTimeMillis is calculated as a
				// rolling expiry time from
				// the lastModified and hence cachetime may not equal
				// [public]CacheTime.
				// This is done to ensure that in a multi server architecture
				// all copies expire
				// at the same time. Otherwise a client may get a flip flopping
				// item as it is
				// re-requested from different servers.
				//
				// Example:
				// 1. lastModified = 1000 ( updatedate in seconds )
				// 2. cacheTime = 60 ( 1 min )
				//
				// Test 1
				// Request comes in at 1100 ( time in seconds )
				// privateExpireTimeMillis will = 1160 and hence cachetime = 60
				//
				// Test 2
				// Request comes in at 1110 ( time in seconds )
				// privateExpireTimeMillis will still be = 1160 and hence
				// cachetime = 50
				privateExpireTimeMillis = ((((requestTime - lastModified) / cacheTime) + 1) * cacheTime)
						+ lastModified;
			}

			if (0 == publicCacheTime) {
				publicExpireTimeMillis = requestTime;
			} else {
				publicExpireTimeMillis = ((((requestTime - lastModified) / publicCacheTime) + 1) * publicCacheTime)
						+ lastModified;
			}

			long privateCacheTimeSecs = (privateExpireTimeMillis - requestTime) / 1000L;

			long publicCacheTimeSecs = (publicExpireTimeMillis - requestTime) / 1000L;
			cacheControl.append("s-maxage=").append(publicCacheTimeSecs);
			cacheControl.append(", max-age=").append(privateCacheTimeSecs);
			expireTime = publicExpireTimeMillis;
		}

		res.setHeader("Cache-Control", cacheControl.toString());
		res.setDateHeader("Expires", expireTime);
		res.setDateHeader("Last-Modified", lastModified);

		String method = req.getMethod();
		if ("GET".equals(method) || "HEAD".equals(method)) {
			// Idempotent methods.
			long ifModifiedSince = getDateHeader(req, "If-Modified-Since");
			if (
					ifModifiedSince != -1 && 
					lastModified <= ifModifiedSince && 
					ifModifiedSince <= requestTime) {

				res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
		}

		Blob blob = this.cmsService.getMediaService().getMedia(item.getId());
		if (blob != null) {
			final ServletOutputStream out = res.getOutputStream();
			InputStream in = null;
			
			try {
				in = blob.getBinaryStream();
				res.setContentType(item.getType().getMimeType());
				res.setContentLength((int) blob.length());
		
				if ("HEAD".equals(method)) {
					// No point stream body, which will be discarded.
					return;
				}
		
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

	/**
	 * Need this method because IE generates If-modified-since headers of the
	 * form "Fri, 26 May 2000 15:34:20 GMT; length=544" (which I don't think is
	 * valid according to the HTTP RFC) and can cause the date parsing to go
	 * awry.
	 */
	private long getDateHeader(HttpServletRequest req, String header) {
		String value = req.getHeader(header);
		if (value == null) {
			return -1L;
		} else {
			final int firstSemicolon = value.indexOf((int) ';');
			if (firstSemicolon != -1) {
				value = value.substring(0, firstSemicolon);
			}
			// We know this isn't going to throw an IllegalArgumentException
			return 1L; // TODO: HttpHelper.parseDate(value);
		}
	}

}
