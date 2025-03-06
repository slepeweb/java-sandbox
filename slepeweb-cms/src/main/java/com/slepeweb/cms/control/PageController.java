package com.slepeweb.cms.control;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.cms.bean.Host;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemIdentifier;
import com.slepeweb.cms.bean.LoginSupport;
import com.slepeweb.cms.bean.RestResponse;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.UndoRedoStatus;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.service.CookieService;
import com.slepeweb.cms.service.LoginService;
import com.slepeweb.cms.service.XPasskeyService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/page")
public class PageController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(PageController.class);
	@Autowired private CookieService cookieService;
	@Autowired private LoginService loginService;
	@Autowired private XPasskeyService xPasskeyService;
	
	@RequestMapping(value="/editor")	
	public String doMain(HttpServletRequest req, HttpServletResponse res, ModelMap model) 
		throws IOException {
		
		Host h = this.cmsService.getHostService().getHost(req.getServerName(), req.getServerPort());
		long itemId = 0;
		
		if (h != null) {
			Site s = h.getSite();
			
			if (s != null) {
				Item i = null;
				User u = getUser(req);
				List<ItemIdentifier> history = this.cookieService.getBreadcrumbsCookieValue(s, req);
				
				if (history.size() > 0) {
					i = getEditableVersion(history.get(0).getItemId(), u);
				}
				else {
					i = s.getItem("/");
					if (i != null) {
						i.setUser(getUser(req));
					}
				}
				
				itemId = i.getOrigId();
			}
		}
		
		res.sendRedirect(req.getContextPath() + "/page/editor/" + itemId);
		
		return null;
	}
	
	@RequestMapping(value="/editor/{origId}")	
	public String doWithItem(@PathVariable long origId, HttpServletRequest req, ModelMap model) {	
		
		Item i = this.getEditableVersion(origId, getUser(req));
		
		if (i == null) {
			throw new RuntimeException("Item doesn't exist!");
		}
		
		model.addAttribute("editingItem", i);
		model.addAttribute("site", i.getSite());
		model.addAttribute("rootItem", i.getSite().getItem("/"));
		model.addAttribute("availableTemplatesForType", i.getSite().getAvailableTemplates(i.getType().getId()));
		
		if (i.isProduct()) {
			model.addAttribute("availableAxes", this.cmsService.getAxisService().get());
		}
		
		// Get a history of visited items
		model.addAttribute("_history", this.cookieService.getBreadcrumbsCookieValue(i.getSite(), req));			
			
		String flash = req.getParameter("status");
		if (StringUtils.isNotBlank(flash)) {
			RestResponse status = new RestResponse();
			String msg = req.getParameter("msg");
			status.setError(flash.equals("error")).parseMessages(msg);
			model.addAttribute("_flashMessage", status);
		}
		
		// undo/redo status
		getUndoRedoStatus(req, model);
		
		// Provide link name options for each link type
		model.addAttribute("_linkTypeNameOptions", getLinkTypeNameOptions(i.getSite()));
		
		return "editor";
	}
	
	@RequestMapping(value="/login")
	public String login(
		HttpServletRequest req,
		HttpServletResponse res,
		ModelMap model) throws ServletException, IOException {
 		
		if (req.getMethod().equalsIgnoreCase("post")) {
			String alias = req.getParameter("alias");
			String pwd = req.getParameter("password");
			LoginSupport supp = this.loginService.login(alias, pwd, true, req);
			
			if (! supp.isSuccess()) {
				model.addAttribute("error", supp.getUserMessage());
			}
			else {
				res.sendRedirect(req.getContextPath() + "/page/editor");
			}
		}
		else if (req.getMethod().equalsIgnoreCase("get")) {
			/* This request is for the login form page.
			 * However, if the xpass parameter is provided, as in the following example path:
			 * 	/cms/page/login?xpass=MGDEBYtUVmlRmLPigICCGWoSQTlJMQjIGIhbeRUVNlKkOJkhIHIIaWRWVPTPKhPIeigeBsUrOnQPmIII&origid=4138
			 * 
			 * then log in as the corresponding user,
			 * and forward to the url identified by the origid parameter.
			 */
			String passkey = req.getParameter("xpass");
			if (StringUtils.isNotBlank(passkey)) {
				User u = this.xPasskeyService.identifyUser(passkey);
				if (u != null) {
					req.getSession().setAttribute(AttrName.USER, u);
					LOG.info(String.format("User %s signed in using passkey", u.getFullName()));
					
					String idStr = req.getParameter("origid");
					if (StringUtils.isNotBlank(idStr)) {
						String path = String.format("/cms/page/editor/%s", idStr);
						res.sendRedirect(path);
						return null;
					}
				}
			}
			
			if (req.getParameter("logout") != null) {
				this.loginService.logout(req);
				model.addAttribute("msg", "You've been successfully logged out.");
			}
		}
 
		return "login"; 
	}
	
	private void getUndoRedoStatus(HttpServletRequest req, ModelMap m) {
		UndoRedoStatus status = new UndoRedoStatus(getItemUpdateHistory(req));
		
		try {
			m.addAttribute(AttrName.UNDO_REDO_STATUS, new ObjectMapper().writeValueAsString(status));
		}
		catch (Exception e) {
			// Log error
		}
	}

}
