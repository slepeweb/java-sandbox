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
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.service.HostService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.site.service.PasskeyService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/rest")
public class RestController extends BaseController {
	
	@Autowired private HostService hostService;
	@Autowired private ItemService itemService;
	@Autowired private PasskeyService passkeyService;
	
	@RequestMapping(value="/passkey/{origId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse issuePasskey(
			@PathVariable long origId,
			HttpServletRequest req) {
		RestResponse r = new RestResponse();
		
		Site thisSite = getSite(req);
		User u = null;
		
		if (thisSite.isSecured()) {
			u = (User) req.getSession().getAttribute(USER);
		}
		
		Item targetItem = this.itemService.getItemByOriginalId(origId);
		if (targetItem == null) {
			return r.setError(true).addMessage(String.format("Item with origId = [%d] not recognised", origId));
		}
		
		Item4Json data4Json = new Item4Json(targetItem);
		
		Site targetSite = targetItem.getSite();
		if (thisSite.getId() == targetSite.getId()) {
			return r.setData(data4Json);
		}
			
		if (targetSite.isSecured()) {
			// You must be logged into this site in order to be able to get a passkey for a secured site.
			if (u == null) {
				return r.setError(true).addMessage("Failed attempt to access item on secured site");
			}
			else {
				String passkey = this.passkeyService.issueKey();
				data4Json.setPath(String.format("%s?passkey=%s$$%s", data4Json.getPath(), u.getId(), passkey));

				if (req.getServerPort() != 80) {
					data4Json.setHostname(targetItem.getSite().getDeliveryHost().getNameAndPort());
				}

				return r.setData(data4Json);
			}
		}
		else {
			return r.setData(data4Json);
		}
	}
	
	@RequestMapping(value="/item/{origId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse getItem(
			@PathVariable long origId, 
			HttpServletRequest req) {
	
		RestResponse r = new RestResponse();

		Item i = this.itemService.getItemByOriginalId(origId);
		if (i == null) {
			return r.setError(true).addMessage(String.format("No item matching origId=%d", origId));
		}
		
		User u = null;
		if (i.getSite().isSecured()) {
			u = (User) req.getSession().getAttribute(USER);
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
	
	private Site getSite(HttpServletRequest req) {
		if (req.getServerPort() == 80) {
			return getSiteByPublicName(req.getServerName());
		}
		else {
			return getSite(req.getServerName(), req.getServerPort());
		}
	}
	
	private Site getSite(String hostname, int port) {
		Host h = this.hostService.getHost(hostname, port);
		if (h != null) {
			return h.getSite();
		}
		return null;
	}

	private Site getSiteByPublicName(String hostname) {
		Host h = this.hostService.getHostByPublicName(hostname);
		if (h != null) {
			return h.getSite();
		}
		return null;
	}
}