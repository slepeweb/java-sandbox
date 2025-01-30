package com.slepeweb.money.control;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.ChartData;
import com.slepeweb.money.bean.ChartProperties;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SavedSearchSupport;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;
import com.slepeweb.money.component.ChartFormSupport;
import com.slepeweb.money.component.FormSupport;
import com.slepeweb.money.component.SearchFormSupport;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/chart")
public class ChartController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(ChartController.class);	
	
	@Autowired private FormSupport formSupport;
	@Autowired private SearchFormSupport searchFormSupport;
	@Autowired private ChartFormSupport chartFormSupport;
	
	/*
	 * 'Charts' and 'searches' use solr to query data, so the solr
	 * index needs to be up to date for results to be accurate.
	 */
	
	// Empty chart definition form, for adding a new chart
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public String create(HttpServletRequest req, ModelMap model) {
		this.chartFormSupport.populateForm(null, new ChartProperties(), SearchFormSupport.CREATE_MODE, model);
		return ChartFormSupport.FORM_VIEW;
	}
	
	// Form to update an existing chart
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		
		SavedSearch ss = this.savedSearchService.get(id);
		ChartProperties props = this.searchFormSupport.fromJson(new TypeReference<ChartProperties>() {}, ss.getJson());

		model.addAttribute("_numDeletableTransactions", 0);		
		this.chartFormSupport.populateForm(ss, props, SearchFormSupport.UPDATE_MODE, model);
		return ChartFormSupport.FORM_VIEW;
	}
	
	// Handle form submission for updating an existing chart
	@RequestMapping(value="/save/{id}", method=RequestMethod.POST)
	public RedirectView save(@PathVariable int id, HttpServletRequest req, ModelMap model) {

		return save(req, this.savedSearchService.get(id));
	}
	
	@RequestMapping(value="/delete/{id}", method=RequestMethod.GET)
	public RedirectView delete(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		String flash;
		
		try {
			this.savedSearchService.delete(ss.getId());
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
		model.addAttribute("_charts", filterSavedSearches("chart"));
		return ChartFormSupport.LIST_VIEW;
	}
	
	// Handle form submission for creating a new chart
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public RedirectView save(HttpServletRequest req, ModelMap model) {

		return save(req, new SavedSearch().setType(SearchFormSupport.CHART_CTX));
	}	
	
	private RedirectView save(HttpServletRequest req, SavedSearch ss) {
		
		SavedSearchSupport supp = this.chartFormSupport.processFormSubmission(req, ss);
		
		if (supp.isSave()) {
			supp.setFlash(storeSavedSearch(ss));
		}
		else {
			supp.setFlash("success|Search NOT saved");
		}
		
		if (supp.isExecute()) {
			if (! supp.isSave()) {
				supp.setFlash("");
			}
			return this.searchFormSupport.redirect2Execute(supp);
		}
		
		return this.searchFormSupport.redirect2List(supp);
	}	
	
	// Produces a chart from a GET request (ie a link)
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	public String get(@PathVariable int id, HttpServletRequest req, ModelMap model) {

		SavedSearch ss = this.savedSearchService.get(id);
		ChartProperties props = this.searchFormSupport.fromJson(new TypeReference<ChartProperties>() {}, ss.getJson());
		this.chartFormSupport.populateForm(ss, props, SearchFormSupport.EXECUTE_MODE, model);

		model.addAttribute(SearchFormSupport.FORM_MODE_ATTR, SearchFormSupport.EXECUTE_MODE);
		model.addAttribute(SearchFormSupport.SAVED_SEARCH_ATTR, ss);
		return search(props, req, model);
	}	
	
	private String search(ChartProperties props, HttpServletRequest req, ModelMap model) {
		model.addAttribute(ChartFormSupport.YEAR_RANGE_ATTR, this.formSupport.getYearRange());
		model.addAttribute(ChartFormSupport.CHART_PROPS_ATTR, props);
		
		if (props.getCategories().getSize() == 0) {
			model.addAttribute("noCategoriesSpecified", 1);
			return ChartFormSupport.RESULTS_VIEW;
		}
		
		Calendar from = Util.today();
		int currentYear = from.get(Calendar.YEAR);
		
		// from is Jan 1
		from.set(Calendar.DATE, 1);
		from.set(Calendar.MONTH, 0);
		
		// to is Dec 31
		Calendar to = Calendar.getInstance();
		to.setTime(from.getTime());
		to.set(Calendar.DATE, 31);
		to.set(Calendar.MONTH, 11);
		
		DefaultCategoryDataset ds = new DefaultCategoryDataset();		
		
		long amount = 0;
		SolrResponse<FlatTransaction> resp;
		SolrParams p;
		ChartData chartData;

		List<Integer> years = new ArrayList<Integer>();
	    model.addAttribute("_years", years);
	    for (int i = props.getFromYear(); i < (props.getFromYear() + props.getToYear()) && i <= currentYear; i++) {
	    	years.add(i);
	    }

	    List<String> labels = new ArrayList<String>();
	    model.addAttribute("_chartLabels", labels);
		Map<String, ChartData> data = new HashMap<String, ChartData>();
	    model.addAttribute("_chartDataMap", data);
	    
	    int startYear = props.getFromYear();
	    int endYear = startYear + props.getToYear();
		
		for (Category_Group grp : props.getCategories().getGroups()) {
			if (! grp.hasCompletedEntries()) {
				continue;
			}
			
			LOG.info(String.format("Processing category group #%d, '%s', containing %d categories", grp.getId(), grp.getLabel(), grp.getSize()));
			
			labels.add(grp.getLabel());
			chartData = new ChartData().setLabel(grp.getLabel());
			data.put(grp.getLabel(), chartData);
			
			p = new SolrParams(new SolrConfig().setPageSize(10000));
			p.setCategoryGroup(grp);
						
			for (int year = props.getFromYear(); year < endYear && year <= currentYear; year++) {
				from.set(Calendar.YEAR, year);
				to.set(Calendar.YEAR, year);
				p.setFrom(from.getTime());
				p.setTo(to.getTime());
				amount = 0;
				
				resp = this.solrService.query(p);
				
				for (FlatTransaction ft : resp.getResults()) {
					amount += ft.getAmount();
				}
				
				ds.addValue(Util.toPounds(amount), 
						grp.getLabel(),
						Integer.valueOf(year));
				
				chartData.getData().put(year, amount);
			}
		}
	 
		JFreeChart chart = ChartFactory.createBarChart(
		         props.getTitle(), "Years", "Amounts (£)",
		         ds,
		         PlotOrientation.VERTICAL, true, true, false);
		
		SVGGraphics2D svg2d = new SVGGraphics2D(1000, 600);
	    chart.draw(svg2d,new Rectangle2D.Double(0, 0, 1000, 600));
	    model.addAttribute("_chartSVG", svg2d.getSVGElement());
		
		return ChartFormSupport.RESULTS_VIEW; 
	}
		
}