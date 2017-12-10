package com.slepeweb.site.ntc.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Site;
import com.slepeweb.site.control.BaseController;
import com.slepeweb.site.model.Page;
import com.slepeweb.site.ntc.bean.CompetitionIndex;
import com.slepeweb.site.ntc.service.CompetitionService;

@Controller
public class CompetitionController extends BaseController {
	
	@Autowired private CompetitionService competitionService;
	
	@RequestMapping(value="/spring/competition/index")	
	public String index(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "competition-index", model);
		model.addAttribute("_competitionIndex", this.competitionService.getCompetitionIndex(site));
		model.addAttribute("_defaultThumb", site.getContentItem("/images/default-thumb"));
		return page.getView();
	}

	@RequestMapping(value="/spring/competition/detail")	
	public String detail(
			@ModelAttribute("_item") Item i, 
			@ModelAttribute("_shortSitename") String shortSitename, 
			@ModelAttribute("_site") Site site, 
			ModelMap model) {	
		
		Page page = getStandardPage(i, shortSitename, "competition-detail", model);
		CompetitionIndex index = this.competitionService.getCompetitionIndex(site);		
		model.addAttribute("_competitionIndex", index);
		model.addAttribute("_competition", index.findCompetition(i.getPath()));
		return page.getView();
	}

}
