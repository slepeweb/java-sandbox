package com.slepeweb.site.servlet;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Redirector;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.StringWrapper;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.SiteAccessService;
import com.slepeweb.cms.utils.CmsUtil;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.common.util.HttpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CmsDeliveryServlet {
	private static Logger LOG = Logger.getLogger(CmsDeliveryServlet.class);
	
	private long defaultPrivateCacheTime, defaultPublicCacheTime;
	private Map<Long, Long> lastDeliveryTable = new HashMap<Long, Long>(127);
	
	@Autowired private CmsService cmsService;

	public void doPost(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		doGet(req, res, model);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		
		String path = getItemPath(req);
		String trimmedPath = path;
		long requestTime = System.currentTimeMillis();		
		Site site = getSite(req);
		
		if (site != null) {
			req.setAttribute("_site", site);
			LOG.trace(LogUtil.compose("Site ...", site));

			String language = site.getLanguage();				
			Redirector redirector = multilingualPathChecker(site, path, language);
			language = redirector.getLanguage();
			trimmedPath = redirector.getPath();
			
			if (redirector.isRequired()) {
				// language is missing on a multilingual site - redirect to default language
				res.sendRedirect(String.format("/%s%s", language, trimmedPath));
				return;
			}
			
			Item item = site.getItem(trimmedPath);
			String view = req.getParameter("view");
			
			if (item != null) {
				LOG.info(LogUtil.compose("Requesting", item));
				String springTemplatePath = getTemplatePath(item, view);
				item.setLanguage(language);
				item.setUser(identifyUser(req));
				redirector = accessibilityChecker(item);
				
				if (redirector.isRequired()) {
					res.sendRedirect(redirector.getPath());
					return;
				}
				
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
						
						String msg = CmsUtil.forward2MediaStreamer(item, req, res);
						if (StringUtils.isNotBlank(msg)) {
							notFound(res, msg, item);
						}
					}
					else {
						res.setContentType("text/html;charset=utf-8");
						res.setCharacterEncoding("utf-8");
						
						if (springTemplatePath != null) {
							LOG.debug(LogUtil.compose("Forwarding request to template", springTemplatePath));
							req.getRequestDispatcher(springTemplatePath).forward(req, res);
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
			notFound(res, "Item not found", path);
		}
		
		logResponseHeaders(res, path);
	}
	
	private User identifyUser(HttpServletRequest req) {
		
		User u = (User) req.getSession().getAttribute("_user");
		
		if (u == null) {
			// Check to see if user credentials have been provided in the request headers
			String authHeader = req.getHeader("Authorization");
			String prefix = "Basic ";
			if (authHeader != null && authHeader.startsWith(prefix)) {
				String encoded = authHeader.substring(prefix.length());
				byte[] bytes = Base64.decodeBase64(encoded);
				
				if (bytes != null) {
					String[] s = new String(bytes).split("___");
					if (s.length == 2) {
						u = this.cmsService.getUserService().get(s[0]);
						if (u != null && ! u.getPassword().equals(s[1])) {
							// Right username, wrong password
							u = null;
						}
					}
				}
			}
		}
		
		return u;
	}
	
	private String getTemplatePath(Item i, String view) {
		Template tmplt = i.getTemplate();
		if (tmplt != null) {
			return StringUtils.isBlank(view) ? tmplt.getController() : tmplt.getController() + "/" + view;
		}
		return null;
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
		Host h = this.cmsService.getHostService().getHost(req.getServerName(), req.getServerPort());
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
	private boolean isCacheable(Item i) {
		return 
				this.cmsService.isDeliveryContext() &&
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
	
	// Returns true if resource is accessible
	private Redirector accessibilityChecker(Item i) {
		
		Redirector r = new Redirector();

		if (! i.isAccessible()) {
			String notAuthorisedPath = i.getSite().isMultilingual() ? 
						String.format("/%s%s", i.getLanguage(), SiteAccessService.LOGIN_PATH) :
							SiteAccessService.LOGIN_PATH;
			
			if (! i.getPath().equals(notAuthorisedPath)) {
				// Redirect to not-authorised page
				r.setPath(notAuthorisedPath);
				r.setRequired(true);
			}
		}
		
		return r;
	}
	
	private Redirector multilingualPathChecker(Site site, String path, String defaultLanguage) {
		Redirector r = new Redirector().setPath(path).setLanguage(defaultLanguage);
		
		if (site.isMultilingual() && ! path.equals("/favicon.ico")) {
			if (path.length() > 2) {
				String[] slugs = path.substring(1).split("/");
				if (slugs.length > 0 && slugs[0].length() == 2) {
					r.setLanguage(path.substring(1, 3));
					r.setPath(path.substring(3));
					
					// Special treatment for homepage
					if (StringUtils.isBlank(r.getPath())) {
						r.setPath("/");
					}
				}
				else {
					r.setRequired(true);
				}
			}
			else {
				// Special treatment for homepage
				r.setPath("");
				r.setRequired(true);
			}
		}
		
		return r;
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
