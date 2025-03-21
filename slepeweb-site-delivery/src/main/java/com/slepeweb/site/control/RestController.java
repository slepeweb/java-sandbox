package com.slepeweb.site.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Item4Json;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.bean.Host.HostType;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.service.HostService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.XPasskeyService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	
	@Autowired private ItemService itemService;
	@Autowired private XPasskeyService xPasskeyService;
	@Autowired private HostService hostService;

	
	@RequestMapping(value="/passkey", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse issuePasskey(HttpServletRequest req) {
		
		RestResponse resp = new RestResponse();
		
		/* 
		 * User must be signed into this site in order to get a passkey for the editorial app.
		 * Not signed in ? Cannot get a pass to cms-e.
		 */
		User u = getUser(req);
		if (u == null) {
			return resp.setError(true).addMessage("User is not logged in");
		}
		
		// Need to return the editorial host:port too, in case it's different to the delivery host
		Host deliveryHost = this.hostService.getHost(req.getServerName(), req.getServerPort());
		Host editorialHost = this.hostService.getHost(deliveryHost.getSite().getId(), HostType.editorial);

		String passkey = this.xPasskeyService.issueKey(u);
		resp.setError(passkey == null);
		resp.setData(new Object[] {editorialHost.getNameAndPort(), passkey});
		return resp;
	}

	
	@RequestMapping(value="/item/{origId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse getItem(@PathVariable long origId, HttpServletRequest req) {
	
		RestResponse r = new RestResponse();

		Item i = this.itemService.getItemByOriginalId(origId);
		if (i == null) {
			return r.setError(true).addMessage(String.format("No item matching origId=%d", origId));
		}
		
		if (i.getSite().isSecured()) {
			User u = (User) req.getSession().getAttribute(AttrName.USER);
			
			if (u == null) {
				return r.setError(true).addMessage("Not authorized");
			}
			else {
				i.setUser(u);
				if (i.isAccessible()) {
					return r.setData(new Item4Json(i));
				}
				else {
					return r.setError(true).addMessage("Target item is not accessible to this user");
				}
			}
		}

		return r.setData(new Item4Json(i));
	}
	
	/*
	@RequestMapping(value="/session/check", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse sessionTimeout(HttpServletRequest req) {
		
		RestResponse resp = new RestResponse();
		
		long lastAccessed = (long) req.getSession().getAttribute(AttrName.LAST_INTERACTION);
		long maxInterval = req.getSession().getMaxInactiveInterval() * 1000;
		long expires =  lastAccessed + maxInterval;
		long now = System.currentTimeMillis() /*+ (26 * 60000)*//*;
		long remaining = expires - now;
		
		return resp.setData(new Object[] {remaining <= (180 * 1000), Math.floor(remaining / 1000)});
	}

	@RequestMapping(value="/session/logout", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse sessionLogout(HttpServletRequest req) {
		
		req.getSession().removeAttribute(AttrName.USER);		
		return new RestResponse().setData("/login");
	}
	*/
}
