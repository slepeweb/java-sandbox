package com.slepeweb.site.geo.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.site.bean.SolrParams4Site;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.geo.bean.SectionMenu;
import com.slepeweb.site.geo.service.SolrService4Geo;
import com.slepeweb.site.model.Page;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/spring/geo")
public class GeoPageController extends BaseController {
	
	@Autowired SolrService4Geo solrService4Geo;
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		return page.getView();
	}

	@RequestMapping(value="/searchresults")	
	public String searchResults(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "searchresults", model);
		
		String terms = req.getParameter("terms");
		SolrParams4Site params = new SolrParams4Site(i, new SolrConfig().setPageSize(20).setMaxPages(1));
		params.setSearchText(terms).setUser(i.getUser());
		model.addAttribute("_searchResponse", this.solrService4Geo.query(params));
		
		return page.getView();
	}

	@RequestMapping(value="/pagewide")	
	public String standardWide(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standardWide", model);
		return page.getView();
	}

	@RequestMapping(value="/page3col")	
	public String standard3Col(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standard3Col", model);
		addInThisSection(i, model);
		return page.getView();
	}

	private void addInThisSection(Item i, ModelMap model) {
		model.addAttribute("_inThisSection", new SectionMenu(i));
	}
}
