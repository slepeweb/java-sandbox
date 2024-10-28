package com.slepeweb.cms.control;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemGist;
import com.slepeweb.cms.bean.Ownership;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.service.CookieService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/rest")
public class RefreshController extends BaseController {
	
	@Autowired private CookieService cookieService;
	
	@RequestMapping(value="/item/{origId}/refresh/core", method=RequestMethod.GET)
	public String refreshCoreTab(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		User u = getUser(req);
		Item i = getEditableVersion(origId, u);
		model.addAttribute("editingItem", i);
		model.addAttribute("site", i.getSite());
		model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
		
		if (i.isProduct()) {
			model.addAttribute("availableAxes", this.cmsService.getAxisService().get());
		}
				
		// Get recently-used tags, and full list of tags for the site
		model.addAttribute(AttrName.TAG_INPUT_SUPPORT, getTagInfo(i.getSite().getId(), req));
		
		// Site content ownership
		model.addAttribute(AttrName.OWNERSHIP, new Ownership(i, u));

		return "refresh/core";		
	}
	
	@RequestMapping(value="/item/{origId}/refresh/field", method=RequestMethod.GET)
	public String refreshFieldTab(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {
		
		Item i = getEditableVersion(origId, getUser(req));
		model.addAttribute("editingItem", i);
		model.addAttribute("_fieldSupport", fieldEditorSupport(i));

		return "refresh/field";		
	}
	
	@RequestMapping(value="/item/{origId}/refresh/links", method=RequestMethod.GET)
	public String refreshLinksTab(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {
		
		Item i = getEditableVersion(origId, getUser(req));
		model.addAttribute("editingItem", i);

		return "refresh/links";		
	}
	
	@RequestMapping(value="/item/{origId}/refresh/version", method=RequestMethod.GET)
	public String refreshVersionTab(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = getEditableVersion(origId, getUser(req));
		model.addAttribute("editingItem", i);
		model.addAttribute("allVersions", i.getAllVersions());
		return "refresh/version";		
	}
		
	@RequestMapping(value="/item/{origId}/refresh/media", method=RequestMethod.GET)
	public String refreshMediaTab(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = getEditableVersion(origId, getUser(req));
		model.addAttribute("editingItem", i);
		model.addAttribute("allVersions", i.getAllVersions());
		
		// Hostname to render content.
		// TODO: should really be a staging host
		// TODO: This code block is duplicated in RestController - refactor
		List<Host> hosts = this.cmsService.getHostService().getHosts(i.getSite().getId());
		if (hosts != null && hosts.size() > 0) {
			model.addAttribute("host", hosts.get(0));
		}
		
		return "refresh/media";		
	}
	
	@RequestMapping(value="/item/{origId}/refresh/move", method=RequestMethod.GET)
	public String refreshMoveTab(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = getEditableVersion(origId, getUser(req));
		model.addAttribute("editingItem", i);
		return "refresh/move";		
	}
		
	@RequestMapping(value="/item/{origId}/refresh/add", method=RequestMethod.GET)
	public String refreshAddTab(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = getEditableVersion(origId, getUser(req));
		model.addAttribute("editingItem", i);
		
		// Last relative position selection for 'addnew'
		model.addAttribute("_stickyAddNewControls", this.cookieService.getStickyAddNewControls(req));
		
		return "refresh/add";		
	}
		
	@RequestMapping(value="/item/{origId}/refresh/copy", method=RequestMethod.GET)
	public String refreshCopyTab(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = getEditableVersion(origId, getUser(req));
		model.addAttribute("editingItem", i);
		return "refresh/copy";		
	}
		
	@RequestMapping(value="/item/{origId}/refresh/flaggedItems", method=RequestMethod.GET)
	public String refreshFlaggedItems(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = getEditableVersion(origId, getUser(req));
		Map<Long, ItemGist> flaggedItems = getFlaggedItems(req);
		
		model.put("editingItem", i);
		model.addAttribute("_flaggedItems", getSortedFlaggedItems(flaggedItems));
		model.addAttribute("_itemIsFlagged", flaggedItems.get(origId) != null);
		model.addAttribute("_fieldSupport", fieldEditorSupport(i));
		return "refresh/flaggedItems";		
	}
		
	@RequestMapping(value="/item/{origId}/refresh/copyFlaggedForm", method=RequestMethod.GET)
	public String refreshCopyFlaggedForm(
			@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = getEditableVersion(origId, getUser(req));
		model.put("editingItem", i);
		model.addAttribute("_fieldSupport", fieldEditorSupport(i));
		return "refresh/copyFlaggedForm";		
	}
		
}
