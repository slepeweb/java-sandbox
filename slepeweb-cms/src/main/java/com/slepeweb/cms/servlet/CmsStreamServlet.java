package com.slepeweb.cms.servlet;

import java.io.FileInputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemType;

public class CmsStreamServlet extends javax.servlet.http.HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * Object to lock to while manipulating {@link buffPool}.
	 */
	private final Object buffPoolLock = new Object();
	/**
	 * Reference to pool list of byte buffers.
	 */
	private java.lang.ref.WeakReference /* <List<byte[]>> */buffPool;

	public void init() throws ServletException {
	}

	private String getInitParameter(String name, String defaultValue) {
		String value = getInitParameter(name);
		return value == null ? defaultValue : value;
	}

	/**
	 * Get a buffer from pool, creating if necessary.
	 */
	private byte[] getBuff() {
		synchronized (buffPoolLock) {
			if (buffPool != null) {
				List list = (List) buffPool.get();
				if (list != null && !list.isEmpty()) {
					return (byte[]) list.remove(list.size() - 1);
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
		synchronized (buffPoolLock) {
			List /* <byte[]> */
			list = (List) (buffPool == null ? null : buffPool.get());
			if (list == null) {
				list = new java.util.ArrayList();
				buffPool = new java.lang.ref.WeakReference(list);
			}
			list.add(buff);
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws javax.servlet.ServletException, java.io.IOException {
		byte[] content;
		// com.mediasurface.client.BinaryContent content = null;
		Item item = (Item) req.getAttribute("requestItem");
		ItemType type = item.getType();

		long lastModified = item.getDateUpdated().getTime();
		long publicCacheTime = 1000000L;
		long cacheTime = publicCacheTime;
		long requestTime = System.currentTimeMillis();
		long expireTime;
		StringBuffer cacheControl = new StringBuffer();

		if (0 == cacheTime && 0 == publicCacheTime) {
			expireTime = requestTime;
			cacheControl.append("no-cache, s-maxage=0, max-age=0");
		} else {
			long privateExpireTimeMillis;
			long publicExpireTimeMillis;
			if (0 == cacheTime) {
				privateExpireTimeMillis = requestTime;
			} else {
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
			if (ifModifiedSince != -1 && lastModified <= ifModifiedSince
					&& ifModifiedSince <= requestTime) {
				// If-Modified-Since header set
				// includively between last mod and now.
				res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
		}

		content = new byte[1000]; // item.getBinaryContent();

		final javax.servlet.ServletOutputStream out = res.getOutputStream();
		java.io.InputStream in = new FileInputStream(""); //content.getContentStream();
//		res.setContentType(content.getContentType());
//		res.setContentLength(content.getContentLength());

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
		if (content != null) {
			// content.getContentStream().close();
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
			return 1L; // HttpHelper.parseDate(value);
		}
	}
}
