package com.slepeweb.site.control;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.service.HostService;
import com.slepeweb.site.service.PasskeyService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	
	@Autowired private HostService hostService;
	@Autowired private PasskeyService passkeyService;
	
	@RequestMapping(value="/passkey", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse issuePasskey(HttpServletRequest req) {
		RestResponse r = new RestResponse();
		
		Site thisSite = getSite(req);
		User u = null;
		
		if (thisSite.isSecured()) {
			u = (User) req.getSession().getAttribute(USER);
		}
		
		String targetUrl = req.getParameter("targeturl");
		Pattern p = Pattern.compile("^//([\\w.]+)(:(\\d+))?/.*$");
		Matcher m = p.matcher(targetUrl);
		
		if (! m.matches()) {
			return r.setError(true).addMessage("Badly constructed url - expect '//host.name.aaa/path/to/resource[?optional=query]");
		}

		String hostname = m.group(1);
		String portStr = m.group(3);
		int port = portStr == null ? 80 : Integer.parseInt(portStr);
		
		Site targetSite = getSite(hostname, port);
		if (targetSite == null) {
			return r.setError(true).addMessage(String.format("Site [%s] / port [%d] not recognised", hostname, port));
		}
		
		if (targetSite.isSecured()) {
			if (u == null) {
				return r.setError(true).addMessage("Failed attempt to access item on secured site");
			}
			else {
				String passkey = this.passkeyService.issueKey();
				return r.setData(String.format("%s$$%s", u.getId(), passkey));
			}
		}
		
		return r;
	}
	
	private Site getSite(HttpServletRequest req) {
		return getSite(req.getServerName(), req.getServerPort());
	}
	
	private Site getSite(String hostname, int port) {
		Host h = this.hostService.getHost(hostname, port);
		if (h != null) {
			return h.getSite();
		}
		return null;
	}

}
