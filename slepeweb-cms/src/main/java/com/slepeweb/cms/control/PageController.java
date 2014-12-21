package com.slepeweb.cms.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.SiteService;

@Controller
@RequestMapping("/page")
public class PageController extends BaseController {
	
	@Autowired private SiteService siteService;
	@Autowired private ItemService itemService;
	
	@RequestMapping(value="/editor")	
	public String doMain(ModelMap model) {		
		return "cms.editor";
	}
	
	@RequestMapping(value="/editor/{itemId}")	
	public String doWithItem(@PathVariable long itemId, ModelMap model) {	
		Item i = this.itemService.getItem(itemId);
		if (i != null) {
			model.addAttribute("editingItem", i);
			model.addAttribute("site", i.getSite());
			model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
		}
		return "cms.editor";
	}
	
	@RequestMapping(value="/site/select/{siteId}")	
	public String chooseSite(@PathVariable long siteId, ModelMap model) {	
		Site site = this.siteService.getSite(siteId);
		if (site != null) {
			model.addAttribute("site", site);
			model.addAttribute("editingItem", site.getItem("/"));
		}
		return "cms.editor";
	}
	
	@RequestMapping(value="/login")
	public String login(
		@RequestParam(value="error", required = false) String error,
		@RequestParam(value="logout", required = false) String logout,
		ModelMap model) {
 
		if (error != null) {
			model.addAttribute("error", "Invalid username and password!");
		}
 
		if (logout != null) {
			model.addAttribute("msg", "You've been successfully logged out.");
		}
 
		return "cms.login"; 
	}
	
	@ModelAttribute("allSites")
	public List<Site> getAllSites() {
		return this.siteService.getAllSites();
	}
}
