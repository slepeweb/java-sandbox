package com.slepeweb.site.pho.control;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.common.solr.bean.SolrConfig;
import com.slepeweb.site.bean.SolrParams4Site;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.pho.service.SolrService4Photos;

@Controller
@RequestMapping("/spring/pho")
public class PhotosPageController extends BaseController {
	
	@Autowired private SolrService4Photos solrService4Photos;
	
	@RequestMapping(value="/homepage")	
	public String homepage(
			@ModelAttribute(ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			@ModelAttribute(SITE) Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "homepage", model);
		
		return page.getView();
	}

	@RequestMapping(value="/search", method=RequestMethod.POST)	
	public String search(
			@ModelAttribute(ITEM) Item i, 
			@ModelAttribute(SHORT_SITENAME) String shortSitename, 
			HttpServletRequest request,
			ModelMap model) {	
				
		String searchText = iso2utf8(request.getParameter("searchtext"));
		String pageNum = request.getParameter("page");

		Page page = getStandardPage(i, shortSitename, "search", model);
		page.setTitle(i.getName());
		
		SolrParams4Site params = new SolrParams4Site(i, new SolrConfig());
		params.setPageSize(20).setPageNum(pageNum);
		params.setSearchText(searchText);
		model.addAttribute("_params", params);
		model.addAttribute("_search", this.solrService4Photos.query(params));
		
		return page.getView();
	}
}
