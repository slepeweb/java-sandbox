package com.slepeweb.site.geo.control;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.model.Page;

@Controller
@RequestMapping("/spring/geo")
public class GeoPageController extends BaseController {
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute(ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		return page.getView();
	}

	@RequestMapping(value="/pagewide")	
	public String standardWide(
			@ModelAttribute(ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standardWide", model);
		return page.getView();
	}

	@RequestMapping(value="/page3col")	
	public String standard3Col(
			@ModelAttribute(ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standard3Col", model);
		return page.getView();
	}

}
