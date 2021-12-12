package com.slepeweb.money.control;

import java.awt.geom.Rectangle2D;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.CategoryGroup;
import com.slepeweb.money.bean.CategoryInput;
import com.slepeweb.money.bean.ChartData;
import com.slepeweb.money.bean.ChartProperties;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.MultiCategoryCounter;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;

@Controller
@RequestMapping(value="/chart")
public class ChartController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(ChartController.class);	
	private static final String CHART_PROPS_ATTR = "_chartProps";
	private static final String YEAR_RANGE_ATTR = "_yearRange";
	private static final String CHART_TYPE = "chart";
	
	private static String FORM_VIEW = "chartForm";
	private static String LIST_VIEW = "chartList";
	private static String RESULTS_VIEW = "chartResults";
	
	@Autowired private CategoryController categoryController;
	
	/*
	 * 'Charts' and 'searches' use solr to query data, so the solr
	 * index needs to be up to date for results to be accurate.
	 */
	
	// Empty chart definition form, for adding a new chart
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public String create(HttpServletRequest req, ModelMap model) {
		this.categoryController.categoryList(model);
		
		setCommonModelAttributes(null, CREATE_MODE, model);
		return FORM_VIEW;
	}
	
	// Form to update an existing chart
	@RequestMapping(value="/edit/{id}", method=RequestMethod.GET)
	public String edit(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		model.addAttribute("_numDeletableTransactions", 0);		
		setCommonModelAttributes(ss, UPDATE_MODE, model);
		return FORM_VIEW;
	}
	
	private void setCommonModelAttributes(SavedSearch ss, String formMode, ModelMap model) {
		if (ss != null) {
			model.addAttribute(SAVED_SEARCH_ATTR, ss);
			model.addAttribute(CHART_PROPS_ATTR, toProperties(ss.getJson()));
		}
		else {
			model.addAttribute(CHART_PROPS_ATTR, getDefaultChartProperties());
		}
		
		model.addAttribute(FORM_MODE_ATTR, formMode);
		model.addAttribute(YEAR_RANGE_ATTR, getYearRange());
		model.addAttribute(CategoryController.ALL_MAJOR_CATEGORIES_ATTR, 
				this.categoryService.getAllMajorValues());
	}
	
	private ChartProperties getDefaultChartProperties() {
		ChartProperties props = new ChartProperties().setTitle("No title");
		CategoryGroup g = new CategoryGroup().setLabel("Group 1").setId(1);
		g.getCategories().add(new CategoryInput());
		props.getGroups().add(g);
		
		return props;
	}
	
	// Handle form submission for updating an existing chart
	@RequestMapping(value="/update/{id}", method=RequestMethod.POST)
	public RedirectView update(@PathVariable int id, HttpServletRequest req, ModelMap model) {

		ChartProperties props = getSearchCriteriaFromRequest(req);
		String flash = save(this.savedSearchService.get(id), props, req, model);
		return new RedirectView(String.format("%s/chart/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
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
	
	private List<Integer> getYearRange() {
		List<Integer> range = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		for (int y = 1990; y <= cal.get(Calendar.YEAR); y++) {
			range.add(y);
		}
		return range;
	}
	
	private ChartProperties getSearchCriteriaFromRequest(HttpServletRequest req) {
		ChartProperties props = new ChartProperties();
		props.setTitle(req.getParameter("name"));
		props.setFromYear(getYear(req, "from", 2015));		
		props.setToYear(getYear(req, "to", 2019));		
		
		if (props.getToYear() < props.getFromYear()) {
			int tmp = props.getFromYear();
			props.setFromYear(props.getToYear());
			props.setToYear(tmp);
		}
		
		String groupName;
		CategoryGroup group;
		
		String jsonStr = req.getParameter("counterStore");
		List<MultiCategoryCounter> counters = fromJson(new TypeReference<List<MultiCategoryCounter>>() {}, jsonStr);
		int numGroups = counters.size();
		int numCategories;
		int n = 0;
				
		for (int groupId = 1; groupId < 10; groupId++) {
			groupName = req.getParameter(String.format("group-%d-name", groupId));
			
			if (StringUtils.isNotBlank(groupName)) {
				group = new CategoryGroup().
						setLabel(groupName).
						setId(groupId);
				
				props.getGroups().add(group);
				
				// How many categories are in this group?
				numCategories = getNumCategoriesForGroup(counters, groupId);
				
				// Read category criteria from form inputs
				group.setCategories(readMultiCategoryInput(req, groupId, numCategories));
				
				if (++n >= numGroups) {
					break;
				}
			}
		}
		
		return props;
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String list(ModelMap model) {
		model.addAttribute("_charts", filterSavedSearches("chart"));
		return LIST_VIEW;
	}
	
	// Handle form submission for creating a new chart
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public RedirectView save(HttpServletRequest req, ModelMap model) {

		ChartProperties props = getSearchCriteriaFromRequest(req);
		String flash = save(new SavedSearch(), props, req, model);
		return new RedirectView(String.format("%s/chart/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}	
	
	private String save(SavedSearch ss, ChartProperties props, HttpServletRequest req, ModelMap model) {
		
		ss.
				setType(CHART_TYPE).
				setName(req.getParameter("name")).
				setDescription(req.getParameter("description")).
				setJson(toJson(props)).
				setSaved(new Timestamp(new Date().getTime()));
		
		try {
			ss = this.savedSearchService.save(ss);
			model.addAttribute(SAVED_SEARCH_ATTR, ss);
			return "success|Chart successfully saved";
		}
		catch (Exception e) {
			return "failure|Failed to save chart";
		}
	}	
	
	// Produces a chart from a GET request (ie a link)
	@RequestMapping(value="/get/{id}", method=RequestMethod.GET)
	public String get(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		model.addAttribute(FORM_MODE_ATTR, EXECUTE_MODE);
		model.addAttribute(SAVED_SEARCH_ATTR, ss);
		ChartProperties props = toProperties(ss.getJson());				
		return search(props, req, model);
	}	
	
	private int getYear(HttpServletRequest req, String formElementName, int dflt) {
		String yearStr = req.getParameter(formElementName);
		
		if (StringUtils.isNumeric(yearStr)) {
			return Integer.valueOf(yearStr).intValue();
		}
		
		return dflt;
	}
	
	// Updates chart properties, then reproduces the chart
	@RequestMapping(value="/post/{id}", method=RequestMethod.POST)
	public String post(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		
		ChartProperties props = getSearchCriteriaFromRequest(req);
		String flash = save(this.savedSearchService.get(id), props, req, model);
		
		if (flash.startsWith("success")) {
			model.addAttribute(FORM_MODE_ATTR, EXECUTE_MODE);
			return search(props, req, model);
		}
		
		return "";
	}

	
	private String search(ChartProperties props, HttpServletRequest req, ModelMap model) {
		model.addAttribute(YEAR_RANGE_ATTR, getYearRange());
		model.addAttribute(CHART_PROPS_ATTR, props);
		
		if (! props.isReady()) {
			model.addAttribute("noCategoriesSpecified", 1);
			return RESULTS_VIEW;
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
		
		for (CategoryGroup grp : props.getGroups()) {
			
			labels.add(grp.getLabel());
			chartData = new ChartData().setLabel(grp.getLabel());
			data.put(grp.getLabel(), chartData);
			
			p = new SolrParams(new SolrConfig().setPageSize(10000));
			p.setCategories(grp.toCategoryList());
						
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
		
		return RESULTS_VIEW; 
	}
		
	private static ChartProperties toProperties(String jsonPacket) {

		try {
			return new ObjectMapper().readValue(jsonPacket, ChartProperties.class);
		} catch (Exception e) {
			LOG.error("Jackson marshalling error", e);
		}
		return null;
	}
	
}