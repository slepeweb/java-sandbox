package com.slepeweb.cms.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.LoginSupport;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.service.CookieService;
import com.slepeweb.cms.service.LoginService;
import com.slepeweb.cms.service.SiteService;

@Controller
@RequestMapping("/page")
public class PageController extends BaseController {
	
	//private static Logger LOG = Logger.getLogger(PageController.class);
	@Autowired private CookieService cookieService;
	@Autowired private LoginService loginService;
	@Autowired private SiteService siteService;
	
	@RequestMapping(value="/editor")	
	public String doMain(HttpServletRequest req, ModelMap model) {		
		getAllEditableSites(req, model);
		return "cms.editor";
	}
	
	@RequestMapping(value="/editor/{origId}")	
	public String doWithItem(@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = this.getEditableVersion(origId, getUser(req));
		
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
		
		getAllEditableSites(req, model);
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
		User u = getUser(req);
		List<ItemIdentifier> history = this.cookieService.getBreadcrumbsCookieValue(site, req);
		if (history.size() > 0) {
			i = getEditableVersion(history.get(0).getItemId(), u);
		}
		else {
			i = site.getItem("/");
			if (i != null) {
				i.setUser(getUser(req));
			}
		}
		
		// Get a history of visited items
		model.addAttribute("_history", history);
		model.addAttribute("editingItem", i);
		
		getAllEditableSites(req, model);
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
				List<Site> allEditableSites = this.siteService.getAllSites(supp.getUser(), "editor");
				if (allEditableSites.size() > 0) {
					if (allEditableSites.size() > 1) {
						res.sendRedirect(req.getContextPath() + "/page/editor");
					}
					else {
						res.sendRedirect(req.getContextPath() + "/page/site/select/" + allEditableSites.get(0).getId());
					}
				}
				else {
					model.addAttribute("error", "User not authorised to edit any sites");
					req.getSession().removeAttribute("_user");
				}
			}
		}
		else if (req.getMethod().equalsIgnoreCase("get")) {
			if (req.getParameter("logout") != null) {
				this.loginService.logout(req);
			}
		}
 
		return "cms.login"; 
	}
	
	private void getAllEditableSites(HttpServletRequest req, ModelMap m) {
		User u = getUser(req);
		List<Site> sites;
		
		if (u != null) {
			sites = this.siteService.getAllSites(u, "editor");
		}
		else {
			sites = new ArrayList<Site>();
		}
		
		m.addAttribute("_allEditableSites", sites);
	}
}
