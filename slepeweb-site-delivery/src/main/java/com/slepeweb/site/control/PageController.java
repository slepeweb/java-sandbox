package com.slepeweb.site.control;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.ItemFilter;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.cms.bean.solr.SolrConfig;
import com.slepeweb.cms.bean.solr.SolrParams;
import com.slepeweb.cms.service.SiteConfigService;
import com.slepeweb.cms.service.SolrService;
import com.slepeweb.commerce.bean.Basket;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.model.SiblingItemPager;
import com.slepeweb.site.servlet.CmsDeliveryServlet;
import com.slepeweb.site.util.SiteUtil;

@Controller
public class PageController extends BaseController {
	
	@Autowired private CmsDeliveryServlet cmsDeliveryServlet;
	@Autowired private SiteConfigService siteConfigService;
	@Autowired private SolrService solrService;
	
	@RequestMapping(value="/**")	
	public void mainController(HttpServletRequest req, HttpServletResponse res, ModelMap model) throws Exception {		
		this.cmsDeliveryServlet.doGet(req, res, model);
	}
	
	@RequestMapping(value="/spring/homepage")	
	public String homepage(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		// On SWS site, this view forwards to '/about'
		// Doing the forward in the JSP, which is site dependant, whereas this controller
		// might be used in multiple sites.
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		return page.getView();
	}

	@RequestMapping(value="/spring/article/rightsidebar")	
	public String standardTemplateRightSidebar(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "article-011", model);
		return page.getView();
	}
	
	@RequestMapping(value="/spring/article/3col")	
	public String standardTemplate3Col(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "article-111", model);
		return page.getView();
	}
	
	@RequestMapping(value="/spring/article/leftnav")	
	public String standardTemplateLeftNav(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "article-leftnav", model);
		return page.getView();
	}
		
	@RequestMapping(value="/spring/search-results")	
	public String searchResults(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "search-results", model);
		
		Long siteId = i.getSite().getId();
		SolrConfig config = new SolrConfig().
				setPageSize(this.siteConfigService.getIntegerProperty(siteId, SolrConfig.PAGE_SIZE_KEY, 5)).
				setMaxPages(this.siteConfigService.getIntegerProperty(siteId, SolrConfig.MAX_PAGES_KEY, 5));
		
		SolrParams params = new SolrParams(i, config).
				setSearchText(req.getParameter("searchText")).
				setPageNum(req.getParameter("page"));
		
		model.addAttribute("_searchResults", this.solrService.query(params));		
		return page.getView();
	}
	
	@RequestMapping(value="/spring/projects")
	public String projects(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortSitename") String shortSitename, 
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout,
		ModelMap model) {
 
		Page page = getStandardPage(i, shortSitename, "article-010", model);
		return page.getView();
	}

	@RequestMapping(value="/spring/wsdemo")
	public String wsdemo(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortSitename") String shortSitename, 
		ModelMap model) {
 
		// Specific page required for ws demo, so as to forward to a view that loads extra js
		Page page = getStandardPage(i, shortSitename, "wsdemo", model);
		page.addJavascript("/resources/sws/js/sandbox.js");
		
		return page.getView();
 
	}

	@RequestMapping(value="/spring/login")
	public String login(
		@ModelAttribute("_item") Item i, 
		@ModelAttribute("_shortSitename") String shortSitename, 
		@RequestParam(value="error", required = false) String error,
		@RequestParam(value="logout", required = false) String logout,
		ModelMap model) {
 
		Page page = getStandardPage(i, shortSitename, "login", model);

		if (error != null) {
			model.addAttribute("error", "Invalid username and password!");
		}
 
		if (logout != null) {
			model.addAttribute("msg", "You've been successfully logged out.");
		}
 
		return page.getView(); 
	}

	@RequestMapping(value="/spring/spizza")	
	public String spizza(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "dummy", model);
		req.setAttribute("_page", page);
		
		// Now hand-off the request to spring webflow
		return "forward:/webflow/spizza";
	}
	
	@RequestMapping(value="/spring/section")	
	public String section(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "section", model);
		model.addAttribute("_defaultThumb", site.getContentItem("/images/default-thumb"));
		return page.getView();
	}

	@RequestMapping(value="/spring/event/index")	
	public String eventsIndex(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "eventsIndex", model);
		model.addAttribute("_defaultThumb", site.getContentItem("/images/default-thumb"));
		return page.getView();
	}

	@RequestMapping(value="/spring/news/index")	
	public String newsIndex(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "newsIndex", model);
		model.addAttribute("_defaultThumb", site.getContentItem("/images/default-thumb"));
		return page.getView();
	}

	@RequestMapping(value="/spring/event")	
	public String eventDetail(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "eventDetail", model);
		if (i.getImage() == null) {
			i.addInline(site.getContentItem("/images/logo"));
		}
		
		model.addAttribute("_siblingPager", getSiblings(i, new String[] {"Event"}, 4));
		return page.getView();
	}	

	@RequestMapping(value="/spring/news")	
	public String newsDetail(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "newsDetail", model);
		if (i.getImage() == null) {
			i.addInline(site.getContentItem("/images/logo"));
		}

		model.addAttribute("_siblingPager", getSiblings(i, new String[] {"News"}, 4));
		return page.getView();
	}
	
	@RequestMapping(value="/spring/angular-spa")	
	public String angularSpa(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "angular-spa", model);
		return page.getView();
	}

	@RequestMapping(value="/spring/product")	
	public String standardProduct(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "commerce/product-111", model);
		return page.getView();
	}
	
	@RequestMapping(value="/spring/basket")	
	public String standardBasket(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "commerce/basket-111", model);
		Cookie basketCookie = SiteUtil.getCookie(req.getCookies(), SiteRestController.BASKET_COOKIE);
		Basket basket = null;
		
		if (basketCookie != null) {
			basket = Basket.parseCookieStringValue(basketCookie.getValue());
		}
		else {
			basket = new Basket();
		}
		
		model.addAttribute(SiteRestController.BASKET_COOKIE, basket);

		return page.getView();
	}
	
	private SiblingItemPager getSiblings(Item i, String[] typesOfInterest, int max) {
		ItemFilter f = new ItemFilter().setTypes(typesOfInterest);
		List<Item> children = i.getParent().getBoundItems(f);		
		return new SiblingItemPager(children, i, max);
	}
}
