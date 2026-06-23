package com.slepeweb.money.control;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Chart;
import com.slepeweb.money.bean.SavedSearchOption;
import com.slepeweb.money.component.ChartFormSupport;
import com.slepeweb.money.component.SearchFormSupport;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/chart")
public class ChartController extends BaseController {
		
	@Autowired private SearchFormSupport searchFormSupport;
	@Autowired private ChartFormSupport chartFormSupport;
	
	/*
	 * 'Charts' and 'searches' use solr to query data, so the solr
	 * index needs to be up to date for results to be accurate.
	 */
	
	// Empty chart definition form, for adding a new chart
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String create(HttpServletRequest req, ModelMap model) {
		model.addAttribute("_formMode", "add");
		return prepareFormView(new Chart(), model);
	}
	
	// Form to update an existing chart
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		model.addAttribute("_formMode", "update");
		return prepareFormView(this.chartService.get(id), model);
	}
	
	private String prepareFormView(Chart ch, ModelMap model) {
		if (ch == null) {
			return null;
		}
		
		List<SavedSearchOption> options = ch.identifySelectedOptions(this.savedSearchService.getAll());
		String json = this.chartFormSupport.toJson(options);		
		model.addAttribute("_chart", ch);
		model.addAttribute("_searchOptions", options);
		model.addAttribute("_searchOptionsJson", json);
		
		model.addAttribute("_yearRange", this.chartFormSupport.getYearRange());
		return ChartFormSupport.FORM_VIEW;
	}
	
	// Handle form submission for updating an existing chart
	@RequestMapping(value="/save/{id}", method=RequestMethod.POST)
	public RedirectView save(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		return save(req, this.chartService.get(id));
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public RedirectView delete(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		Chart ch = this.chartService.get(id);
		String flash;
		
		try {
			this.chartService.delete(ch.getId());
			flash = "success|Chart successfully deleted";
		}
		catch (Exception e) {
			flash = "failure|Failed to delete chart";
		}
		
		return new RedirectView(String.format("%s/chart/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}
	
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String list(ModelMap model) {
		model.addAttribute("_charts", this.chartService.getAll());
		return ChartFormSupport.LIST_VIEW;
	}
	
	// Handle form submission for creating a new chart
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public RedirectView save(HttpServletRequest req, ModelMap model) {
		return save(req, new Chart());
	}	
	
	private RedirectView save(HttpServletRequest req, Chart ch) {
		
		ch.setName(req.getParameter("name"));
		ch.setDescription(req.getParameter("description"));
		ch.setFromYear(this.chartFormSupport.getYear(req, "from", 2015));		
		ch.setToYear(this.chartFormSupport.getYear(req, "to", 2019));
		ch.setNotes(req.getParameter("notes"));
		
		if (ch.getToYear() < ch.getFromYear()) {
			int tmp = ch.getFromYear();
			ch.setFromYear(ch.getToYear());
			ch.setToYear(tmp);
		}
		
		ch.setSearchIds(req.getParameter("idlist"));
		
		boolean doSave = this.searchFormSupport.isOption("save", req);
		boolean doExecute = this.searchFormSupport.isOption("execute", req);
		String flash = "";
		String redirection;
		
		if (doSave) {
			flash = saveChart(ch);
		}
		
		if (doExecute) {
			redirection = String.format("/chart/get/%s?flash=%s", 
					ch.getId(), Util.encodeUrl(flash));
			
			return new RedirectView(redirection, true, true, false);
		}
		
		redirection = String.format("/chart/list?flash=%s", 
				Util.encodeUrl(flash));
				
		return new RedirectView(redirection, true, true, false);
		
	}	
	
	protected String saveChart(Chart ch) {
		String flash;
		try {
			this.chartService.save(ch);
			flash = "success|Chart successfully saved";
		}
		catch (Exception e) {
			flash = "failure|Failed to save chart";
		}
		
		return flash;
	}
	

	
	// Produces a chart from a GET request (ie a link)
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	public String get(@PathVariable int id, HttpServletRequest req, ModelMap model) {

		Chart ch = this.chartService.get(id);
		prepareFormView(ch, model);
		model.addAttribute("_formMode", "execute");
		return this.chartFormSupport.executeSearches(ch, req, model);
	}	
			
}