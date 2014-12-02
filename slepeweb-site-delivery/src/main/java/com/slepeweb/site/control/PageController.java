package com.slepeweb.site.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Link;
import com.slepeweb.cms.service.CmsService;
import com.slepeweb.site.model.LinkTarget;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.model.Sidebar;
import com.slepeweb.site.service.ComponentService;
import com.slepeweb.site.servlet.CmsDeliveryServlet;

@Controller
public class PageController extends BaseController {
	
	@Autowired private CmsService cmsService;
	@Autowired private CmsDeliveryServlet cmsDeliveryServlet;
	@Autowired private ComponentService componentService;
	
	
	@RequestMapping(value="/**")	
	public void mainController(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {		
		this.cmsDeliveryServlet.doGet(req, res, model);
	}
	
	@RequestMapping(value="/spring/homepage")	
	public String homepage(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortHostname") String shortHostname, 
			ModelMap model) {	
		
		standardTemplateRightSidebar(i, shortHostname, model);
		return getFullyQualifiedViewName(shortHostname, "home");
	}

	private Page standardTemplate(Item i) {	
		
		Page page = new Page().
				setTitle(i.getFieldValue("title")).
				setBody(i.getFieldValue("bodytext", "")).
				setTopNavigation(getTopNavigation(i));
		
		page.setHeading(page.getTitle());
		page.setComponents(this.componentService.getComponents(i.getComponents(), "main"));
		page.getHeader().setBreadcrumbs(i);
		
		return page;
	}

	@RequestMapping(value="/spring/article/rightsidebar")	
	public String standardTemplateRightSidebar(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortHostname") String shortHostname, 
			ModelMap model) {	
		
		Page page = standardTemplate(i);
		page.setRightSidebar(new Sidebar());
		page.getRightSidebar().setComponents(this.componentService.getComponents(i.getComponents(), "rightside"));
		
		model.addAttribute("_page", page);
		return getFullyQualifiedViewName(shortHostname, "article-011");
	}
	
	@RequestMapping(value="/spring/article/3col")	
	public String standardTemplate3Col(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortHostname") String shortHostname, 
			ModelMap model) {	
		
		Page page = standardTemplate(i);
		page.setRightSidebar(new Sidebar());
		page.getRightSidebar().setComponents(this.componentService.getComponents(i.getComponents(), "rightside"));
		page.setLeftSidebar(new Sidebar());
		page.getLeftSidebar().setComponents(this.componentService.getComponents(i.getComponents(), "leftside"));
		
		model.addAttribute("_page", page);
		return getFullyQualifiedViewName(shortHostname, "article-111");
	}
	
	@RequestMapping(value="/spring/article/leftnav")	
	public String standardTemplateLeftNav(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortHostname") String shortHostname, 
			ModelMap model) {	
		
		Page page = standardTemplate(i);
		
		// Left navigation
		if (page.getHeader().getBreadcrumbs().size() > 1) {
			Item levelOneItem = page.getHeader().getBreadcrumbItems().get(1);
			List<Item> levelOneBindings = levelOneItem.getBoundItems();
			
			if (levelOneBindings.size() > 0) {
				List<LinkTarget> leftNav = page.getLeftSidebar().getNavigation();
				LinkTarget levelOneTarget = new LinkTarget(levelOneItem).setSelected(true);
				LinkTarget levelTwoTarget, levelThreeTarget;
				leftNav.add(levelOneTarget);
				
				for (Item levelTwoItem : levelOneBindings) {
					levelTwoTarget = new LinkTarget(levelTwoItem);
					levelTwoTarget.setSelected(i.getPath().startsWith(levelTwoItem.getPath()));
					levelOneTarget.getChildren().add(levelTwoTarget);
					for (Item levelThreeItem : levelTwoItem.getBoundItems()) {
						levelThreeTarget = new LinkTarget(levelThreeItem);
						levelTwoTarget.getChildren().add(levelThreeTarget);
						levelThreeTarget.setSelected(i.getPath().startsWith(levelThreeItem.getPath()));
					}
				}
			}
		}
		
		model.addAttribute("_page", page);
		return getFullyQualifiedViewName(shortHostname, "article-leftnav");
	}
		
	@RequestMapping(value="/spring/projects")
	public String projects(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortHostname") String shortHostname, 
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout,
		ModelMap model) {
 
		standardTemplateRightSidebar(i, shortHostname, model);
		return getFullyQualifiedViewName(shortHostname, "article-010");
 
	}

	@RequestMapping(value="/spring/wsdemo")
	public String wsdemo(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortHostname") String shortHostname, 
		ModelMap model) {
 
		String view = standardTemplateLeftNav(i, shortHostname, model);
		Page page = (Page) model.get("_page");
		page.addJavascript("/resources/sws/js/sandbox.js");
		return view;
 
	}

	@RequestMapping(value="/spring/login")
	public String login(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortHostname") String shortHostname, 
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout,
		ModelMap model) {
 
		standardTemplateRightSidebar(i, shortHostname, model);

		if (error != null) {
			model.addAttribute("error", "Invalid username and password!");
		}
 
		if (logout != null) {
			model.addAttribute("msg", "You've been logged out successfully.");
		}
 
		return getFullyQualifiedViewName(shortHostname, "login"); 
	}

	private List<LinkTarget> getTopNavigation(Item i) {
		List<LinkTarget> nav = new ArrayList<LinkTarget>();
		Item root = this.cmsService.getItemService().getItem(i.getSite().getId(), "/");
		LinkTarget lt;
		
		if (root != null) {
			for (Link l : root.getBindings()) {
				lt = new LinkTarget(l.getChild()).
						setSelected(i.getPath().startsWith(l.getChild().getPath()));
				nav.add(lt);
			}
		}
		return nav;
	}
}
