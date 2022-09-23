package com.slepeweb.site.pho.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.pho.bean.PhoCookieValues;
import com.slepeweb.site.pho.bean.SolrParams4Pho;
import com.slepeweb.site.pho.service.PhoCookieService;
import com.slepeweb.site.pho.service.SolrService4Photos;

@Controller
@RequestMapping("/spring/pho")
public class PhotosPageController extends BaseController {
	
	@Autowired private SolrService4Photos solrService4Photos;
	@Autowired private PhoCookieService phoCookieService;
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute(ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(SITE) Site site, 
			HttpServletRequest req,
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		model.addAttribute("_latestCookieValues", this.phoCookieService.getAllCookieValues(req));
		
		return page.getView();
	}

	@RequestMapping(value="/search", method=RequestMethod.POST)	
	public String search(
			@ModelAttribute(ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest request,
			HttpServletResponse response,
			ModelMap model) {	
				
		Page page = getStandardPage(i, shortSitename, "search", model);
		page.setTitle(i.getName());
		
		PhoCookieValues formData = new PhoCookieValues(request);
		this.phoCookieService.saveAllCookieValues(formData, response);
		
		SolrParams4Pho params = new SolrParams4Pho(i, new SolrConfig());		
		params.setSearchText(formData.getText());
		params.
			setFrom(formData.getFrom()).
			setTo(formData.getTo());
		
		params.
			setPageSize(20).
			setPageNum(request.getParameter("page"));
		
		model.addAttribute("_params", params);
		model.addAttribute("_search", this.solrService4Photos.query(params));
		
		return page.getView();
	}
}
