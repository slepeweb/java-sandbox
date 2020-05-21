package com.slepeweb.cms.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.service.HostService;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.commerce.service.AxisService;

@Controller
@RequestMapping("/rest")
public class RefreshController extends BaseController {
	
	@Autowired private ItemService itemService;
//	@Autowired private ItemTypeService itemTypeService;
//	@Autowired private TemplateService templateService;
//	@Autowired private MediaService mediaService;
//	@Autowired private LinkService linkService;
//	@Autowired private LinkTypeService linkTypeService;
//	@Autowired private LinkNameService linkNameService;
	@Autowired private HostService hostService;
	@Autowired private AxisService axisService;
//	@Autowired private CookieService cookieService;
//	@Autowired private NavigationController navigationController;
	
	@RequestMapping(value="/item/{origId}/refresh/core", method=RequestMethod.GET)
	public String refreshCoreTab(
			@PathVariable long origId, ModelMap model) {	
		
		Item i = this.itemService.getEditableVersion(origId);
		model.addAttribute("editingItem", i);
		model.addAttribute("site", i.getSite());
		model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
		
		if (i.isProduct()) {
			model.addAttribute("availableAxes", this.axisService.get());
		}

		return "cms.refresh.core";		
	}
	
	@RequestMapping(value="/item/{origId}/refresh/field", method=RequestMethod.GET)
	public String refreshFieldTab(
			@PathVariable long origId, ModelMap model) {
		
		Item i = this.itemService.getEditableVersion(origId);
		model.addAttribute("editingItem", i);
		model.addAttribute("_fieldSupport", fieldEditorSupport(i));

		return "cms.refresh.field";		
	}
	
	@RequestMapping(value="/item/{origId}/refresh/links", method=RequestMethod.GET)
	public String refreshLinksTab(
			@PathVariable long origId, ModelMap model) {
		
		Item i = this.itemService.getEditableVersion(origId);
		model.addAttribute("editingItem", i);

		return "cms.refresh.links";		
	}
	
	@RequestMapping(value="/item/{origId}/refresh/version", method=RequestMethod.GET)
	public String refreshVersionTab(
			@PathVariable long origId, ModelMap model) {	
		
		Item i = this.itemService.getEditableVersion(origId);
		model.addAttribute("editingItem", i);
		model.addAttribute("allVersions", i.getAllVersions());
		return "cms.refresh.version";		
	}
		
	@RequestMapping(value="/item/{origId}/refresh/media", method=RequestMethod.GET)
	public String refreshMediaTab(
			@PathVariable long origId, ModelMap model) {	
		
		Item i = this.itemService.getEditableVersion(origId);
		model.addAttribute("editingItem", i);
		model.addAttribute("allVersions", i.getAllVersions());
		
		// Hostname to render content.
		// TODO: should really be a staging host
		// TODO: This code block is duplicated in RestController - refactor
		List<Host> hosts = this.hostService.getAllHosts(i.getSite().getId());
		if (hosts != null && hosts.size() > 0) {
			model.addAttribute("host", hosts.get(0));
		}
		
		return "cms.refresh.media";		
	}
}
