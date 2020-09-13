package com.slepeweb.cms.control;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.LoginSupport;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.service.CookieService;
import com.slepeweb.cms.service.LoginService;

@Controller
@RequestMapping("/page")
public class PageController extends BaseController {
	
	//private static Logger LOG = Logger.getLogger(PageController.class);
	@Autowired private CookieService cookieService;
	@Autowired private LoginService loginService;
	
	@RequestMapping(value="/editor")	
	public String doMain(ModelMap model) {		
		return "cms.editor";
	}
	
	@RequestMapping(value="/editor/{origId}")	
	public String doWithItem(@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = this.getItem(origId, getUser(req));
		
		if (i != null) {
			model.addAttribute("editingItem", i);
			model.addAttribute("site", i.getSite());
			model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
			
			if (i.isProduct()) {
				model.addAttribute("availableAxes", this.cmsService.getAxisService().get());
			}
			
			// Get a history of visited items
			model.addAttribute("_history", this.cookieService.getBreadcrumbsCookieValue(i.getSite(), req));
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
	public String chooseSite(@PathVariable long siteId, 
			HttpServletRequest req, HttpServletResponse res, ModelMap model) {	
		
		Site site = this.cmsService.getSiteService().getSite(siteId);
		if (site != null) {
			model.addAttribute("site", site);
		}

		Item i = null;
		List<ItemIdentifier> history = this.cookieService.getBreadcrumbsCookieValue(site, req);
		if (history.size() > 0) {
			i = this.cmsService.getItemService().getItem(history.get(0).getItemId());
		}
		else {
			i = site.getItem("/");
		}

		// Get a history of visited items
		model.addAttribute("_history", history);
		model.addAttribute("editingItem", i);
		
		return "cms.editor";
	}
	
	@RequestMapping(value="/login")
	public String login(
		HttpServletRequest req,
		HttpServletResponse res,
		ModelMap model) throws IOException {
 
		if (req.getParameter("logout") != null) {
			model.addAttribute("msg", "You've been successfully logged out.");
		}
		
		if (req.getMethod().equalsIgnoreCase("post")) {
			String email = req.getParameter("email");
			String pwd = req.getParameter("password");
			LoginSupport supp = this.loginService.login(email, pwd, req);
			
			if (! supp.isSuccess()) {
				model.addAttribute("error", supp.getErrorMessage());
			}
			else {
				res.sendRedirect(req.getContextPath() + "/page/editor");
			}
		}
		else if (req.getMethod().equalsIgnoreCase("get")) {
			if (req.getParameter("logout") != null) {
				this.loginService.logout(req);
			}
		}
 
		return "cms.login"; 
	}
	
	@ModelAttribute("allSites")
	public List<Site> getAllSites() {
		return this.cmsService.getSiteService().getAllSites();
	}
}
