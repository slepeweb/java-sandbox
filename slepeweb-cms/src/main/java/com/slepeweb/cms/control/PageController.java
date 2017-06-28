package com.slepeweb.cms.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.SiteService;
import com.slepeweb.commerce.service.AxisService;

@Controller
@RequestMapping("/page")
public class PageController extends BaseController {
	
	@Autowired private SiteService siteService;
	@Autowired private ItemService itemService;
	@Autowired private AxisService axisService;
	
	@RequestMapping(value="/editor")	
	public String doMain(ModelMap model) {		
		return "cms.editor";
	}
	
	@RequestMapping(value="/editor/{itemId}")	
	public String doWithItem(@PathVariable long itemId, ModelMap model, HttpServletRequest req) {	
		Item i = this.itemService.getItem(itemId);
		if (i != null) {
			model.addAttribute("editingItem", i);
			model.addAttribute("site", i.getSite());
			model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
			
			if (i.isProduct()) {
				model.addAttribute("availableAxes", this.axisService.get());
			}
		}
		
		String flash = req.getParameter("status");
		if (StringUtils.isNotBlank(flash)) {
			RestResponse status = new RestResponse();
			String msg = req.getParameter("msg");
			status.setError(flash.equals("error")).parseMessages(msg);
			model.addAttribute("_flashMessage", status);
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
