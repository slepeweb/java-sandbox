package com.slepeweb.site.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	@RequestMapping(value="/spring/article")	
	public String applyArticleTemplate(HttpServletRequest req, ModelMap model) {	
		Item i = (Item) req.getAttribute("_item");
		Page page = new Page().
				setTitle(i.getFieldValue("title")).
				setBody(i.getFieldValue("bodytext", "")).
				setTopNavigation(getTopNavigation(i));
		
		page.setHeading(page.getTitle());
		//page.getHeader().getStylesheets().add("/resources/sws/css/slepeweb.css");
		Sidebar rightSidebar = new Sidebar();
		page.setRightSidebar(rightSidebar);
		rightSidebar.setComponents(this.componentService.getComponents(i, "rightside"));
		model.addAttribute("_page", page);
		return "sws.home";
	}

	private List<LinkTarget> getTopNavigation(Item requestItem) {
		List<LinkTarget> nav = new ArrayList<LinkTarget>();
		Item root = this.cmsService.getItemService().getItem(requestItem.getSite().getId(), "/");
		if (root != null) {
			for (Link l : root.getBindings()) {
				nav.add(new LinkTarget(l.getChild()));
			}
		}
		return nav;
	}
}
