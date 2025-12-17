package com.slepeweb.site.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Host.HostType;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Item4Json;
import com.slepeweb.cms.bean.RequestPack;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.SiteConfigCache;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.component.Passkey;
import com.slepeweb.cms.component.PasskeyModel;
import com.slepeweb.cms.constant.SiteConfigKey;
import com.slepeweb.cms.service.HostService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.PasskeyService;
import com.slepeweb.cms.service.XPasskeyService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	
	@Autowired private ItemService itemService;
	@Autowired private XPasskeyService xPasskeyService;
	@Autowired private PasskeyService passkeyService;
	@Autowired private HostService hostService;
	@Autowired private SiteConfigCache siteConfigCache;

	
	@RequestMapping(value="/xpasskey", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse issueXPasskey(HttpServletRequest req) {
		
		RestResponse resp = new RestResponse();
		
		/* 
		 * User must be signed into this site in order to get a passkey for the editorial app.
		 * This passkey would allow user to login to cms-e.
		 * Not signed in to cms-d? Then, cannot access cms-e.
		 */
		User u = getUser(req);
		if (u == null) {
			return resp.setError(true).addMessage("User is not logged in");
		}
		
		Host deliveryHost = this.hostService.getHost(req.getServerName(), req.getServerPort());
		Host editorialHost = this.hostService.getHost(deliveryHost.getSite().getId(), HostType.editorial);

		String passkey = this.xPasskeyService.issueKey(u);
		resp.setError(passkey == null);
		resp.setData(new Object[] {editorialHost.getNameAndPort(), passkey});
		return resp;
	}

	
	@RequestMapping(value="/passkey", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse issuePasskey(HttpServletRequest req) {
		
		RestResponse resp = new RestResponse();
		
		/* 
		 * User must be signed into this site in order to get a passkey for a page on
		 * a different site managed by this cms.
		 * Not signed in to cms-d? Then, cannot link to a page on a secured-site.
		 */
		User u = getUser(req);
		if (u == null) {
			return resp.setError(true).addMessage("User is not logged in");
		}
		
		Passkey passkey = this.passkeyService.issueKey(PasskeyModel.SHORT_TTL, u);
		resp.setData(new Object[] {passkey.encode()});
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
		
		/*
		 * This request will have come from an ajax request in inlineImages.js, which does not
		 * need to provide a passkey to gain access to the item. Access will be based on user
		 * credentials, stored in the session for THIS request.
		 */
		i.setRequestPack(new RequestPack(req));
		
		if (! i.isAccessible()) {
			return r.setError(true).addMessage("Target item is not accessible to this user");
		}
		
		return r.setData(new Item4Json(i));
	}
	
	@RequestMapping(value="/logout/{siteId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse logout(@PathVariable long siteId, HttpServletRequest req) {
		req.getSession().invalidate();
		String loginPath = this.siteConfigCache.getValue(siteId, SiteConfigKey.LOGIN_PATH, "/login");
		return new RestResponse().addMessage("Done").setData(loginPath);
	}
	
}
