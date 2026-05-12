package com.slepeweb.money.component;

import java.awt.geom.Rectangle2D;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.slepeweb.common.util.JsonUtil;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.Category_GroupSet;
import com.slepeweb.money.bean.ChartData;
import com.slepeweb.money.bean.ChartProperties;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SavedSearchSupport;
import com.slepeweb.money.bean.SearchCategory;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;
import com.slepeweb.money.control.CategoryController;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SolrService4Money;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ChartFormSupport {

	public static final String CHART_PROPS_ATTR = "_chartProps";
	public static final String YEAR_RANGE_ATTR = "_yearRange";
	
	public static final String FORM_VIEW = "chartForm";
	public static final String LIST_VIEW = "chartList";
	public static final String RESULTS_VIEW = "chartResults";
	
	public static final int NUM_EMPTY_GROUPS = 2;
	
	private static Logger LOG = Logger.getLogger(ChartFormSupport.class);	
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private FormSupport formSupport;
	@Autowired private SearchFormSupport searchFormSupport;
	@Autowired private SolrService4Money solrService4Money;


	public void populateForm(SavedSearch ss, ChartProperties props, String formMode, ModelMap model) {

		Category_GroupSet cgs = null;
		
		if (props.getCategories() == null) {
			cgs = new Category_GroupSet("Chart", SearchFormSupport.CHART_CTX);
			addEmptyGroups(cgs);
			props.setCategories(cgs);
		}
		else {
			cgs = props.getCategories();
			Category_Group cg;
			int numGroups = cgs.getSize();
			
			// Ensure all existing groups are visible, and append some empty categories
			for (int i = 0; i < numGroups; i++) {
				cg = cgs.getGroups().get(i);
				cg.setId(i + 1);
				cg.setVisible(true);
				cg.setLastVisible(i == numGroups - 1);
				cg.setAllCategoriesVisible();
				
				this.formSupport.addEmptyCategories(cg);
			}
			
			// Add some blank groups for user to complete
			addEmptyGroups(cgs);
			
			/* Hide category groups if chart is about money transfers. This is simpler to
			 * do here, rather than muddy the complexity in addEmptyGroups()
			 */
			if (props.isTransfer()) {
				for (Category_Group g : cgs.getGroups()) {
					g.setVisible(false);
				}
			}
		}
		
		model.addAttribute(SearchFormSupport.SAVED_SEARCH_ATTR, ss);
		model.addAttribute(SearchFormSupport.ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(SearchFormSupport.CATEGORY_GROUP_ATTR, cgs);				
		model.addAttribute(CHART_PROPS_ATTR, props);
		model.addAttribute(SearchFormSupport.FORM_MODE_ATTR, formMode);
		model.addAttribute(YEAR_RANGE_ATTR, this.formSupport.getYearRange());
		model.addAttribute(CategoryController.ALL_MAJOR_CATEGORIES_ATTR, 
				this.categoryService.getAllMajorValues());
	}
	

	public void addEmptyGroups(Category_GroupSet cgs) {
		List<Category_Group> groups = cgs.getGroups();
		Category_Group cg;
		int numVisibleGroups = cgs.getSize();
		int startId = numVisibleGroups + 1;
		Category_Group previousGroup = numVisibleGroups > 0 ?  groups.get(numVisibleGroups - 1) : null;
		
		for (int i = 0; i < NUM_EMPTY_GROUPS; i++) {
			cg = this.formSupport.populateCategory_Group(startId + i, "Update label", null, SearchCategory.class);
			cg.setVisible(previousGroup == null || (i == 0 && ! previousGroup.isPopulated()));
			previousGroup = cg;
			cgs.addGroup(cg);
		}
	}
	

	public SavedSearchSupport processFormSubmission(HttpServletRequest req, SavedSearch ss) {
		Category_GroupSet cgs = new Category_GroupSet(req.getParameter("title"), SearchFormSupport.CHART_CTX);
		int numGroups = Integer.parseInt(req.getParameter("numGroups"));
		
		// Read the remaining search parameters from the submitted form
		ChartProperties props = readSearchCriteria(req);
		payeeName2Id(props);
		
		if (! props.isTransfer()) {
			// Create a new Category_Group using the submitted form data, and add it to the set IFF populated
			for (int i = 1; i <= numGroups; i++) {
				this.formSupport.readCategoryInputs(req, i, cgs);
			}
		}		
		
		// Merge the Category_GroupSet object into ChartProperties
		props.setCategories(cgs);
		
		// Update the SavedSearch object, which gets saved to the db
		ss.
			setType(SearchFormSupport.CHART_CTX).
			setName(req.getParameter("name")).
			setDescription(req.getParameter("description")).
			setJson(JsonUtil.toJson(props)).
			setSaved(new Timestamp(new Date().getTime()));
		
		// This support object simplifies matters ???
		SavedSearchSupport sss = new SavedSearchSupport().
				setRequest(req).
				setSavedSearch(ss).
				setChartProperties(props).
				setMode(req.getParameter("formMode")).
				setSave(this.searchFormSupport.isOption("save", req)).
				setExecute(this.searchFormSupport.isOption("execute", req));

		return sss;
	}
	

	public ChartProperties readSearchCriteria(HttpServletRequest req) {
		
		// 'payee' takes precedence over 'transferAccount'
		String payeeName = req.getParameter("payee");
		String transferAccount = req.getParameter("transferAccount");
		
		if (StringUtils.isNotBlank(payeeName)) {
			transferAccount = null;
		}

		ChartProperties p = new ChartProperties();
		p.setAccountId(req.getParameter("account"));
		p.setPayeeName(payeeName);
		p.setTransferAccountId(transferAccount);
		p.setTransferDirection(req.getParameter("transferDirection"));
		
		p.setTitle(req.getParameter("name"));
		p.setFromYear(getYear(req, "from", 2015));		
		p.setToYear(getYear(req, "to", 2019));		
		
		if (p.getToYear() < p.getFromYear()) {
			int tmp = p.getFromYear();
			p.setFromYear(p.getToYear());
			p.setToYear(tmp);
		}
				
		return p;
	}
	
	private record ChartDataSupport(DefaultCategoryDataset ds, Map<String, ChartData> chartDataByLabel, 
			List<String> labels) {}
	
	public String executeSearches(ChartProperties props, HttpServletRequest req, ModelMap model) {
		model.addAttribute(YEAR_RANGE_ATTR, this.formSupport.getYearRange());
		model.addAttribute(CHART_PROPS_ATTR, props);
		
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
		SolrParams p;

		List<Integer> years = new ArrayList<Integer>();
	    model.addAttribute("_years", years);
	    for (int i = props.getFromYear(); i < (props.getFromYear() + props.getToYear()) && i <= currentYear; i++) {
	    	years.add(i);
	    }

	    List<String> labels = new ArrayList<String>();
	    model.addAttribute("_chartLabels", labels);
		Map<String, ChartData> chartDataByLabel = new HashMap<String, ChartData>();
	    model.addAttribute("_chartDataMap", chartDataByLabel);
	    ChartDataSupport supp = new ChartDataSupport(ds, chartDataByLabel, labels);
	    
	    int startYear = props.getFromYear();
	    int endYear = startYear + props.getToYear();
		
		for (int year = props.getFromYear(); year < endYear && year <= currentYear; year++) {
			from.set(Calendar.YEAR, year);
			to.set(Calendar.YEAR, year);
			
			p = new SolrParams(new SolrConfig().setPageSize(10000));
			p.setAccountId(props.getAccountId());
			p.setPayeeId(props.getPayeeId());
			p.setTransferAccountId(props.getTransferAccountId());
			p.setTransferDirection(props.getTransferDirection());
			p.setFrom(from.getTime());
			p.setTo(to.getTime());
			
			if (! p.isTransfer()) {
				for (Category_Group grp : props.getCategories().getGroups()) {
					LOG.info(String.format("Processing category group #%d, '%s', containing %d categories", grp.getId(), grp.getLabel(), grp.getSize()));					
					p.setCategoryGroup(grp);
					queryThenAggregateData(p, grp.getLabel(), year, supp);
				}
			}
			else {
				queryThenAggregateData(p, "-Amounts-", year, supp);
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
	
	private void queryThenAggregateData(SolrParams p, String label, int year, ChartDataSupport supp) {
		SolrResponse<FlatTransaction> resp = this.solrService4Money.query(p);
		long amount = 0;
		
		if ( ! supp.labels.contains(label)) {
			supp.labels.add(label);
		}
		
		ChartData chartData = supp.chartDataByLabel.get(label);
		
		if (chartData == null) {
			chartData = new ChartData().setLabel(label);
			supp.chartDataByLabel.put(label, chartData);
		}
		
		for (FlatTransaction ft : resp.getResults()) {
			amount += ft.getAmount();
		}
		
		supp.ds.addValue(Util.toPounds(amount), label, Integer.valueOf(year));
		chartData.getData().put(year, amount);
	}

	
	private int getYear(HttpServletRequest req, String formElementName, int dflt) {
		String yearStr = req.getParameter(formElementName);
		
		if (StringUtils.isNumeric(yearStr)) {
			return Integer.valueOf(yearStr).intValue();
		}
		
		return dflt;
	}
	
	public void payeeId2Name(ChartProperties props) {
		if (props.getPayeeId() != null && props.getPayeeId().longValue() > 0) {
			Payee p = this.payeeService.get(props.getPayeeId());
			props.setPayeeName(p != null ? p.getName() : "");
		}
	}
	
	public void payeeName2Id(ChartProperties props) {
		if (StringUtils.isNotBlank(props.getPayeeName())) {
			Payee p = this.payeeService.get(props.getPayeeName());
			if (p != null) {
				props.setPayeeId(p.getId());
			}
		}
	}
}
