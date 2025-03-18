package com.slepeweb.site.anc.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.site.anc.service.AncCookieService;
import com.slepeweb.site.control.BaseController;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/rest/anc")
public class AncestryRestController extends BaseController {
	
	@Autowired private ItemService itemService;
	@Autowired private AncCookieService ancCookieService;

	@RequestMapping(value="/login/redirect/{origId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RestResponse redirectOnLogin(
			@PathVariable long origId,
			HttpServletRequest req) {
		
		RestResponse r = new RestResponse();
		Item i = this.itemService.getItemByOriginalId(origId);
		
		if (i == null) {
			return r.setError(true).addMessage("Item not found");
		}
		
		ItemIdentifier iid = this.ancCookieService.getLatestBreadcrumb(i.getSite(), req);
		if (iid != null) {
			r.setData(iid.getPath());
		}
		else {
			r.setData("/");
		}
		
		return r;
	}
	
}
