package com.slepeweb.cms.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.constant.AttrName;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/rest/session")
public class SessionController {
	
	@RequestMapping(value="/check", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse sessionTimeout(HttpServletRequest req) {
		
		RestResponse resp = new RestResponse();
		
		long maxInterval = req.getSession().getMaxInactiveInterval() * 1000;
		long now = System.currentTimeMillis() /*+ (26 * 60000)*/;
		
		Object attr = req.getSession().getAttribute(AttrName.LAST_INTERACTION);
		long lastAccessed = attr != null ? (long) attr : now;
		long expires =  lastAccessed + maxInterval;
		long remaining = expires - now;
		
		return resp.setData(new Object[] {remaining <= (180 * 1000), Math.floor(remaining / 1000)});
	}

	@RequestMapping(value="/logout", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse sessionLogout(HttpServletRequest req) {
		
		req.getSession().removeAttribute(AttrName.USER);		
		return new RestResponse().setData("/login");
	}
}
