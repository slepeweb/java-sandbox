package com.slepeweb.cms.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.service.MediaDeliveryService;
import com.slepeweb.cms.utils.LogUtil;

@Component
public class MediaDeliveryServlet {
	private static Logger LOG = Logger.getLogger(MediaDeliveryServlet.class);
	
	@Autowired private CmsService cmsService;
	@Autowired private MediaDeliveryService mediaDeliveryService;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String path = getItemPath(req);
		Site site = getSite(req);
		
		if (site != null) {
			Item item = site.getItem(path);
			
			if (item != null && item.getType().isMedia()) {
				item.setUser(identifyUser(req));
				
				if (! item.isAccessible()) {
					res.sendError(HttpServletResponse.SC_FORBIDDEN);
				}
				else {
					this.mediaDeliveryService.stream(item, req, res);
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
	}
	
	private User identifyUser(HttpServletRequest req) {		
		return (User) req.getSession().getAttribute("_user");
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
		String prefix = "/media";
		return pathInfo.substring(prefix.length());
	}
}
