package com.slepeweb.site.geo.control;

import java.util.ArrayList;
import java.util.List;

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
import com.slepeweb.site.geo.service.GeoCookieService;
import com.slepeweb.site.geo.service.SolrService4Geo;
import com.slepeweb.site.model.LinkTarget;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.service.XimgService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/spring/geo")
public class GeoPageController extends BaseController {
	
	public static final String HISTORY = "_history";
	public static final String XIMG_SERVICE = "_ximgService";
	
	@Autowired SolrService4Geo solrService4Geo;
	@Autowired GeoCookieService geoCookieService;
	@Autowired XimgService ximgService;
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/searchresults")	
	public String searchResults(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "searchresults", model);
		
		String terms = req.getParameter("terms");
		SolrParams4Site params = new SolrParams4Site(i, new SolrConfig().setPageSize(20).setMaxPages(1));
		params.setSearchText(terms).setUser(i.getUser());
		model.addAttribute("_searchResponse", this.solrService4Geo.query(params));
		
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/pagewide")	
	public String standardWide(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standardWide", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/pagewide/pdf")	
	public String standardWidePdf(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "pdf/standardWide", model);
<<<<<<< Upstream, based on branch 'master' of https://github.com/slepeweb/java-sandbox.git
		addPdfExtras(i, req, res, model);
=======
		addPdfExtras(page, req, res, model);
>>>>>>> 5c146fe cms-d: pdf gen, stage 1
		return page.getView();
	}

	@RequestMapping(value="/page3col")	
	public String standard3Col(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standard3Col", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/page3col/pdf")	
	public String standard3ColPdf(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "pdf/standard3Col", model);
<<<<<<< Upstream, based on branch 'master' of https://github.com/slepeweb/java-sandbox.git
		addPdfExtras(i, req, res, model);
=======
		addPdfExtras(page, req, res, model);
>>>>>>> 5c146fe cms-d: pdf gen, stage 1
		return page.getView();
	}

	@RequestMapping(value="/notfound")	
	public String notfound(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(AttrName.SITE) Site site, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standard3Col", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	private void addGeoExtras(Item i, HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		model.addAttribute("_inThisSection", new SectionMenu(i));
		model.addAttribute(HISTORY, this.geoCookieService.updateBreadcrumbsCookie(i, req, res));
	}

<<<<<<< Upstream, based on branch 'master' of https://github.com/slepeweb/java-sandbox.git
	private void addPdfExtras(Item i, HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		model.addAttribute(XIMG_SERVICE, this.ximgService);
		model.addAttribute(AttrName.PASSKEY, req.getAttribute(AttrName.PASSKEY));
=======
	private void addPdfExtras(Page p, HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		Item i = p.getItem();
		model.addAttribute(XIMG_SERVICE, this.ximgService);
		model.addAttribute(AttrName.PASSKEY, i.getRequestPack().getPasskey().encode());
		
		List<LinkTarget> crumbs = p.getHeader().getBreadcrumbs();
		// Remove root item from breadcrumbs
		crumbs.remove(0);
		int len = crumbs.size();
		List<String> topRow = new ArrayList<String>();
		String bottomRow = null;
		
		if (len > 0) {
			if (len == 1) {
				bottomRow = crumbs.get(0).getTitle();
			}
			else if (len == 2) {
				topRow.add(crumbs.get(0).getTitle());
				bottomRow = crumbs.get(1).getTitle();
			}
			else {
				for (int n = 0; n < (len - 1); n++) {
					topRow.add(crumbs.get(n).getTitle());
				}
				bottomRow = crumbs.get(len - 1).getTitle();
			}
		}
		
		model.addAttribute("toptitle", topRow);
		model.addAttribute("bottomtitle", bottomRow);
>>>>>>> 5c146fe cms-d: pdf gen, stage 1
	}
}
