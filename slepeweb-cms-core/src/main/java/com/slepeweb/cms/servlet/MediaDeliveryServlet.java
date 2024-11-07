package com.slepeweb.cms.servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.cms.utils.CmsUtil;
import com.slepeweb.cms.utils.LogUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class MediaDeliveryServlet {
	private static Logger LOG = Logger.getLogger(MediaDeliveryServlet.class);
	
	@Autowired private CmsService cmsService;

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
					String msg = CmsUtil.forward2MediaStreamer(item, req, res);
					if (StringUtils.isNotBlank(msg)) {
						notFound(res, msg, path);
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
		String prefix = "/media";
		return pathInfo.substring(prefix.length());
	}
}
