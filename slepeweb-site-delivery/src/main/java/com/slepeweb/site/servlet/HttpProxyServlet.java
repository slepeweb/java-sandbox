package com.slepeweb.site.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.slepeweb.cms.utils.SpringContext;
import com.slepeweb.common.service.HttpService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpProxyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(HttpProxyServlet.class);
		
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		String url = req.getParameter("u");
		if (StringUtils.isNotBlank(url)) {
			HttpService svc = (HttpService) SpringContext.getApplicationContext().getBean("httpService");

			try {
				PrintWriter pw = res.getWriter();
				String markup = svc.get(url);
				pw.write(markup);
				pw.flush();
			} catch (IOException e) {
				LOG.error(String.format("Failed to proxy http request [%s]", url), e);
			}
		}
	}
	
}
