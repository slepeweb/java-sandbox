package com.slepeweb.cms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.Template;
import com.slepeweb.cms.service.CmsService;

public class CmsControllerServlet extends javax.servlet.http.HttpServlet {
	private static final long serialVersionUID = 1L;
	private String[] bypassPatterns;
	private CmsService cmsService;

	public void setCmsService(CmsService cmsService) {
		this.cmsService = cmsService;
	}

	public void init() throws ServletException {
		String bypassPatternStr = getInitParameter("bypass", "*.wsdl|*.dtd");
		this.bypassPatterns = bypassPatternStr.split("|");
	}

	private String getInitParameter(String name, String defaultValue) {
		String value = getInitParameter(name);
		return value == null ? defaultValue : value;
	}
	
	private boolean bypass(String path) {
		for (String regex : this.bypassPatterns) {
			if (path.matches(regex)) {
				return true;
			}
		}
		return false;
	}

	protected void service(HttpServletRequest req, HttpServletResponse res)
			throws javax.servlet.ServletException, java.io.IOException {
		
		// Forward to default servlet if path matches pattern
		final String path = getItemPath(req);
		if (! bypass(path)) {
			Site s = this.cmsService.getSiteService().getSite(1L);
			Item i = s.getItem(path);
			
			if (i.getType().isMedia()) {
//				stream(i);
			}
			else {
				Template t = i.getTemplate();
				if (t != null) {
					req.getRequestDispatcher("default").forward(req, res);
				}
				else {
					notFound(req, res, "/notfound");
				}
			}
		}
		else {
			req.getRequestDispatcher("default").forward(req, res);
		}
	}

	private void notFound( HttpServletRequest req, HttpServletResponse res, String url ) throws IOException
    {
      res.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
	
	private String getItemUrl(HttpServletRequest req) {
		final String path = getItemPath(req);
		final String host = req.getServerName();
		final int port = req.getServerPort();
		return "//" + host + ':' + port + path;
	}

	private String getItemPath(HttpServletRequest req) {
		String servletPath = req.getServletPath();
		servletPath = (servletPath == null) ? "" : servletPath;

		String pathInfo = req.getPathInfo();
		pathInfo = pathInfo != null ? servletPath + pathInfo : servletPath;
		return pathInfo;

	}

}
