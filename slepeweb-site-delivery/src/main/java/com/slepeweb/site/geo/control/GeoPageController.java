package com.slepeweb.site.geo.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.QandAList;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.constant.AttrName;
import com.slepeweb.cms.service.QandAService;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.site.bean.SolrParams4Site;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.geo.bean.SectionMenu;
import com.slepeweb.site.geo.service.GeoCookieService;
import com.slepeweb.site.geo.service.SolrService4Geo;
import com.slepeweb.site.model.LinkTarget;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.service.MagicMarkupService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/spring/geo")
public class GeoPageController extends BaseController {
	
	public static final String HISTORY = "_history";
	public static final String MAGIC_MARKUP_SERVICE = "_magicMarkupService";
	public static final String LOCAL_HOSTNAME = "_localHostname";
	public static final String PASSKEY = "_passkey";
	
	@Autowired SolrService4Geo solrService4Geo;
	@Autowired GeoCookieService geoCookieService;
	@Autowired MagicMarkupService magicMarkupService;
	@Autowired QandAService qandAService;
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
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
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standardPdf", model);
		addPdfExtras(page, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/page3col")	
	public String standard3Col(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
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
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standardPdf", model);
		addPdfExtras(page, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/topsecret")	
	public String topSecret(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws Exception {	
		
		// Does this user have top-secret access?
		if (! isSuperUser(req, res, i.getPath())) {
			return null;
		}
		
		Page page = getStandardPage(i, shortSitename, "standard3Col", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	@RequestMapping(value="/superlogin")	
	public String superLogin(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) throws Exception {	
		
		User u = getUser(req);
		QandAList qalStoredInDb = this.qandAService.getQandAList(u);
		
		String targetOnSuccess = req.getParameter("success");
		
		if (req.getMethod().equalsIgnoreCase("get")) {
			model.addAttribute("_qal", qalStoredInDb);
			model.addAttribute("_success", targetOnSuccess);
			Page page = getStandardPage(i, shortSitename, "superLogin", model);
			addGeoExtras(i, req, res, model);
			return page.getView();
		}
		
		// Dealing with form submission ...
		String q, a;
		QandAList qalProvidedInForm = new QandAList();
		for (int j = 0; j < 3; j++) {
			q = req.getParameter("question" + j);
			a = req.getParameter("answer" + j);
			if (StringUtils.isNotBlank(q) && StringUtils.isNotBlank(a)) {
				qalProvidedInForm.add(q, a.trim());
			}
		}		
		
		if (qalProvidedInForm.equals(qalStoredInDb)) {
			req.getSession().setAttribute(AttrName.SUPER_USER, u);
			res.sendRedirect(targetOnSuccess);
			LOG.info(String.format("User '%s' correctly answered %d security questions", u.getFullName(), qalStoredInDb.getList().size()));
			return null;
		}
		
		LOG.info(String.format("User '%s' FAILED to correctly answer %d security questions", u.getFullName(), qalStoredInDb.getList().size()));
		String loginFormUrl = String.format("/superlogin?warning=%s&success=%s", "***+Invalid+credentials+***", targetOnSuccess);
		res.sendRedirect(loginFormUrl);
		return null;
	}

	@RequestMapping(value="/notfound")	
	public String notfound(
			@ModelAttribute(AttrName.ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest req,
			HttpServletResponse res,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "standard3Col", model);
		addGeoExtras(i, req, res, model);
		return page.getView();
	}

	private void addGeoExtras(Item i, HttpServletRequest req, HttpServletResponse res, ModelMap model) {
		model.addAttribute(MAGIC_MARKUP_SERVICE, this.magicMarkupService);
		model.addAttribute("_inThisSection", new SectionMenu(i));
		model.addAttribute(HISTORY, this.geoCookieService.updateBreadcrumbsCookie(i, req, res));
	}
	
	private boolean isSuperUser(HttpServletRequest req, HttpServletResponse res, String targetItemPath) throws IOException {
		// Does this user have top-secret access?
		User superUser = (User) req.getSession().getAttribute(AttrName.SUPER_USER);
		
		if (superUser == null) {
			res.sendRedirect("/superlogin?success=" + targetItemPath);
			return false;
		}
		
		return true;
	}

	private void addPdfExtras(Page p, HttpServletRequest req, HttpServletResponse res, ModelMap model) {

		model.addAttribute(LOCAL_HOSTNAME, p.getItem().getSite().getDeliveryHost().getNamePortAndProtocol());
		model.addAttribute(MAGIC_MARKUP_SERVICE, this.magicMarkupService);
		
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
		
		model.addAttribute("_toptitle", topRow);
		model.addAttribute("_bottomtitle", bottomRow);
	}
}
