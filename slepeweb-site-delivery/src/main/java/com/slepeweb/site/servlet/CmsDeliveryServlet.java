package com.slepeweb.site.servlet;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.RequestPack;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.StringWrapper;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.constant.FieldName;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.SiteAccessService;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.common.util.HttpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CmsDeliveryServlet {
	private static Logger LOG = Logger.getLogger(CmsDeliveryServlet.class);
	
	private long defaultPrivateCacheTime, defaultPublicCacheTime;
	private Map<Long, Long> lastDeliveryTable = new HashMap<Long, Long>(127);
	private static Pattern PATH_PATTERN_A = Pattern.compile("^(/(\\w\\w))?/\\$_(\\d+)$");
	private static Pattern PATH_PATTERN_B = Pattern.compile("^(/(\\w\\w))?(/.*?)$");
	
	@Autowired private CmsService cmsService;
	@Autowired private ItemService itemService;

	private Item identifyItem(HttpServletRequest req) {
		Item i = null;
		String language = null;
		String path = getItemPath(req);
		RequestPack r = new RequestPack(req);
		
		if (path.equals("/favicon.ico")) {
			return null;
		}

		/*
		 *  PATH_PATTERN_A is used in requests for 'foreign' items, ie on a different site,
		 *  most commonly media items that you want to share between sites, but also pages 
		 *  to link to.
		 */
		Matcher m = PATH_PATTERN_A.matcher(path);
		
		if (m.matches()) {
			language = m.group(2);
			i = this.itemService.getItemByOriginalId(Long.valueOf(m.group(3)));
			r.setMiniPath(true);
		}
		else {
			m = PATH_PATTERN_B.matcher(path);
			
			if (m.matches()) {
				language = m.group(2);
				path = m.group(3);
				Site site = getSite(req);
				
				if (site != null) {
					path = site.isMultilingual() ? path : m.group(0);
					i = site.getItem(path);
				}
			}
		}
		
		if (i != null) {
			r.setLanguage(language == null ? i.getSite().getLanguage() : language);
			i.setRequestPack(r);
			
			req.setAttribute(AttrName.ITEM, i);
			req.setAttribute(AttrName.SITE, i.getSite());
		}

		return i;
	}
	
	private String getLoginPath(Item i) {
		return i.getSite().isMultilingual() ? 
				String.format("/%s%s", i.getLanguage(), SiteAccessService.LOGIN_PATH) :
					SiteAccessService.LOGIN_PATH;
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		doGet(req, res, model);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {
		
		Item item = identifyItem(req);
		
		if (item == null) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		/*
		 * This might be a request for a page/item on a secured site that the user does not have access to,
		 * simply because he hasn't (yet) logged into that site.
		 */
		if (
				item.getSite().isSecured() &&
				item.isPage() && 
				item.getRequestPack().isMiniPath() &&
				item.getRequestPack().hasPasskey()) {
			
			/*
			 * Add a passkey to the url, and specify the delivery host for the site that this item belongs to,
			 * before redirecting to the modified item url.
			 */
			String url = String.format("//%s%s?_passkey=%s", 
					item.getSite().getDeliveryHost().getNameAndPort(), item.getUrl(), item.getRequestPack().getPasskey().encode());
			
			res.sendRedirect(url);
			return;
		}

		// Redirect request to login page if user does not have access to item
		if (! item.isAccessible()) {
			// Site access rules deny access to this user, AND no suitable passkey provided
			if (! item.getPath().equals(getLoginPath(item))) {
				LOG.warn(String.format("Item [%s] is not accessible", item.getPath()));
				res.sendRedirect(getLoginPath(item));
				return;
			}
		}
		
		String view = req.getParameter("view");		
		String springTemplatePath = updateControllerIf(item, view);
		
		long requestTime = System.currentTimeMillis();
		req.getSession().setAttribute(AttrName.LAST_INTERACTION, requestTime);
		
		long ifModifiedSince = getDateHeader(req, "If-Modified-Since");

		if (isCacheable(item) && isFresh(item, requestTime, ifModifiedSince, req.getMethod())) {
			res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
		}
		else {
			setCacheHeaders(item, requestTime, res);
			
			if (item.getType().isMedia()) {
				LOG.debug(LogUtil.compose("Streaming binary content ...", item));
				String mediaType = view != null && view.equals("thumbnail") ? 
						"jpg" : item.getType().getShortMimeType();
				String servletPath = String.format("/stream/item/%s", mediaType);
				req.getRequestDispatcher(servletPath).forward(req, res);
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
					LOG.error(LogUtil.compose("Item has no template", item));
					res.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
			}
		}
	}
	
	private String updateControllerIf(Item i, String view) {
		Template tmplt = i.getTemplate();
		if (tmplt != null) {
			if (StringUtils.isNotBlank(view)) {
				tmplt.setController(tmplt.getController() + "/" + view);
			}
			return tmplt.getController();
		}
		return null;
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
	
	/*
	private boolean itemIsAccessibleByUser(Item i, HttpServletRequest req) {
		
		boolean siteIsSecured = i.getSite().isSecured();
		boolean passkeyIsValid = false;	
		
		if (siteIsSecured) {
			User u = i.getUser();
			
			if (u == null) {
				LOG.info("Request is from user who has not logged in ...");
				String passkey = req.getParameter("passkey");
				
				if (passkey != null) {
					String[] parts = passkey.split("\\$\\$");
					if (parts.length != 2) {
						LOG.error(String.format("Badly formed passkey [%s]", passkey));
						return false;
					}
					
					long userId = Long.parseLong(parts[0]);
					u = this.userService.get(userId);
					if (u == null) {
						LOG.error(String.format("User ID not recognised [%d]", userId));
						return false;
					}
					
					i.setUser(u);
					if (passkeyIsValid = this.passkeyService.validateKey(parts[1])) {
						i.setUser(u);
					}
				}
			}
			
			if (i.getSite().isSecured() && ! i.isAccessible() && ! passkeyIsValid && i.getUser() == null) {
				// Site access rules deny access to this user, AND he has no passkey.
				if (! i.getPath().equals(getLoginPath(i))) {
					LOG.warn(String.format("Item [%s] is not accessible by un-identified user", i.getPath()));
					return false;
				}
			}			
		}	
		
		return true;
	}
	*/
	
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
