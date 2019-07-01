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
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
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

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.CategoryGroup;
import com.slepeweb.money.bean.CategoryInput;
import com.slepeweb.money.bean.ChartData;
import com.slepeweb.money.bean.ChartProperties;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.MultiCategoryCounter;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SavedSearchService;
import com.slepeweb.money.service.SolrService;
import com.slepeweb.money.service.TransactionService;

@Controller
public class SearchController extends BaseController {
	
	private static Logger LOG = Logger.getLogger(SearchController.class);
	
	private static final String CHART_PROPS_ATTR = "_chartProps";
	private static final String CATEGORY_GROUP_ATTR = "_categoryGroup";
	private static final String SEARCH_RESPONSE_ATTR = "_response";
	private static final String TYPE_ADVANCED = "advanced";
	private static final String ALL_ACCOUNTS_ATTR = "_allAccounts";
	private static final String ALL_PAYEES_ATTR = "_allPayees";
	private static final String TYPE_CHART = "chart";
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;
	@Autowired private SolrService solrService;
	@Autowired private SavedSearchService savedSearchService;
	@Autowired private CategoryController categoryController;
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String advancedSearchForm(ModelMap model) {
		
		model.addAttribute("_allAccounts", this.accountService.getAll(true));
		
		List<Payee> payees = this.payeeService.getAll();
		if (payees.size() > 0 && StringUtils.isBlank(payees.get(0).getName())) {
			payees.remove(0);
		}
		model.addAttribute("_allPayees", payees);
		
		// Create a single, default category group
		CategoryGroup grp = new CategoryGroup().setId(1).setLabel("unset");
		grp.getCategories().add(new CategoryInput().setId(1));
		model.addAttribute(CATEGORY_GROUP_ATTR, grp);
		
		return "advancedSearch";
	}
	
	@RequestMapping(value="/search", method=RequestMethod.POST)
	public String advancedSearchResultsDefault(HttpServletRequest req, ModelMap model) {
		return advancedSearchResults(1, req, model);
	}
	
	@RequestMapping(value="/search/{page}", method=RequestMethod.POST)
	public String advancedSearchResults(@PathVariable int page, HttpServletRequest req, ModelMap model) {
		
		SolrParams params = getAdvancedSearchCriteriaFromRequest(req).setPageNum(page);
		
		String jsonStr = req.getParameter("counterStore");
		List<MultiCategoryCounter> counters = fromJson(new TypeReference<List<MultiCategoryCounter>>() {}, jsonStr);
		int groupId = 1;
		List<CategoryInput> inputs = readMultiCategoryInput(req, groupId, getNumCategoriesForGroup(counters, groupId));
		CategoryGroup grp = new CategoryGroup().setId(1).setCategories(inputs);
		params.setCategories(grp.toCategoryList());
				
		model.addAttribute(SEARCH_RESPONSE_ATTR, this.solrService.query(params));				
		model.addAttribute(CATEGORY_GROUP_ATTR, grp);				
		model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
		model.addAttribute(ALL_PAYEES_ATTR, getAllPayees());

		return "advancedSearch";
	}	
	
	private List<Payee> getAllPayees() {
		List<Payee> payees = this.payeeService.getAll();
		if (payees.size() > 0 && StringUtils.isBlank(payees.get(0).getName())) {
			payees.remove(0);
		}
		return payees;
	}
	
	private List<Integer> getYearRange() {
		List<Integer> range = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		for (int y = 1990; y <= cal.get(Calendar.YEAR); y++) {
			range.add(y);
		}
		return range;
	}
	
	private SolrParams getAdvancedSearchCriteriaFromRequest(HttpServletRequest req) {
		// Payee may be specified by either name or id, but not both!
		return 
			new SolrParams(new SolrConfig()).
			setAccountId(req.getParameter("accountId")).
			setPayeeId(req.getParameter("payeeId")).
			setPayeeName(req.getParameter("payee")).
			setMemo(req.getParameter("memo")).
			setFrom(req.getParameter("from")).
			setTo(req.getParameter("to")).
			setPageNum(1);
	}
	
	private ChartProperties getChartSearchCriteriaFromRequest(HttpServletRequest req) {
		ChartProperties props = new ChartProperties();
		props.setFromYear(getChartYear(req, "from", 2015));		
		props.setToYear(getChartYear(req, "to", 2019));		
		
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
	
	@RequestMapping(value="/search/save/list", method=RequestMethod.GET)
	public String listSavedSearches(ModelMap model) {
		
		// Organise searches into respective groups, by search type
		List<SavedSearch> all = this.savedSearchService.getAll();
		List<SavedSearch> group;
		List<String> groupTypes = new ArrayList<String>();
		Map<String,List<SavedSearch>> map = new HashMap<String,List<SavedSearch>>();
		
		for (SavedSearch ss : all) {
			if (! groupTypes.contains(ss.getType())) {
				groupTypes.add(ss.getType());
				group = new ArrayList<SavedSearch>();
				map.put(ss.getType(), group);
			}
			else {
				group = map.get(ss.getType());
			}
			
			group.add(ss);
		}
		
		model.addAttribute("_types", groupTypes);
		model.addAttribute("_map", map);
		return "savedSearchList";
	}
	
	@RequestMapping(value="/search/save/advanced", method=RequestMethod.POST)
	public RedirectView saveAdvancedSearchParameters(HttpServletRequest req, ModelMap model) {
		
		// Payee may be specified by either name or id, but not both!
		SolrParams params = getAdvancedSearchCriteriaFromRequest(req);
		
		String countersJson = req.getParameter("counterStore");
		List<MultiCategoryCounter> counters = fromJson(new TypeReference<List<MultiCategoryCounter>>() {}, countersJson);
		int groupId = 1;
		List<CategoryInput> inputs = readMultiCategoryInput(req, groupId, getNumCategoriesForGroup(counters, groupId));
		CategoryGroup grp = new CategoryGroup().setId(1).setCategories(inputs);
		params.setCategories(grp.toCategoryList());
				
		SavedSearch ss = new SavedSearch().
				setType(TYPE_ADVANCED).
				setName(req.getParameter("save-identifier")).
				setJson(toJson(params)).
				setSaved(new Timestamp(new Date().getTime()));
		
		String flash;
		
		try {
			this.savedSearchService.save(ss);
			flash = "success|Search successfully saved";
		}
		catch (Exception e) {
			flash = "failure|Failed to save search";
		}

		return new RedirectView(String.format("%s/search/save/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}	
	
	@RequestMapping(value="/search/save/chart", method=RequestMethod.POST)
	public RedirectView saveChartParameters(HttpServletRequest req, ModelMap model) {
		
		ChartProperties props = getChartSearchCriteriaFromRequest(req);
		SavedSearch ss = new SavedSearch().
				setType(TYPE_CHART).
				setName(req.getParameter("save-identifier")).
				setJson(toJson(props)).
				setSaved(new Timestamp(new Date().getTime()));
		
		String flash;
		
		try {
			this.savedSearchService.save(ss);
			flash = "success|Search successfully saved";
		}
		catch (Exception e) {
			flash = "failure|Failed to save search";
		}

		return new RedirectView(String.format("%s/search/save/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}	
	
	@RequestMapping(value="/search/save/edit/{id}", method=RequestMethod.GET)
	public String savedSearchUpdateForm(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		model.addAttribute("_ss", this.savedSearchService.get(id));
		model.addAttribute("_numDeletableTransactions", 0);
		return "savedSearchForm";
	}
	
	@RequestMapping(value="/search/save/edit/{id}", method=RequestMethod.POST)
	public RedirectView savedSearchUpdateAction(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		ss.setName(req.getParameter("name"));
		String flash;
		
		try {
			this.savedSearchService.save(ss);
			flash = "success|Search label successfully updated";
		}
		catch (Exception e) {
			flash = "failure|Failed to update search label";
		}
		
		return new RedirectView(String.format("%s/search/save/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}
	
	@RequestMapping(value="/search/save/delete/{id}", method=RequestMethod.GET)
	public RedirectView savedSearchDeleteAction(@PathVariable int id, HttpServletRequest req, ModelMap model) {
		SavedSearch ss = this.savedSearchService.get(id);
		String flash;
		
		try {
			this.savedSearchService.delete(ss.getId());
			flash = "success|Saved search successfully deleted";
		}
		catch (Exception e) {
			flash = "failure|Failed to delete saved search";
		}
		
		return new RedirectView(String.format("%s/search/save/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}
	
	@RequestMapping(value="/search/save/repeat", method=RequestMethod.GET)
	public String repeatSavedSearch(HttpServletRequest req, ModelMap model) {
		
		String jsonStr = req.getParameter("json");
		if (req.getParameter("type").equals("advanced")) {
			SolrParams params = toSolrParams(jsonStr);				
			model.addAttribute(SEARCH_RESPONSE_ATTR, this.solrService.query(params));		
			
			CategoryGroup grp = new CategoryGroup().setId(1);
			CategoryInput ci;
			for (Category c : params.getCategories()) {
				ci = new CategoryInput().
						setMajor(c.getMajor()).
						setMinor(c.getMinor()).
						setOptions(this.categoryService.getAllMinorValues(c.getMajor()));
				ci.setExclude(c.isExclude());
				grp.getCategories().add(ci);
						
			}
			
			model.addAttribute(CATEGORY_GROUP_ATTR, grp);				
			model.addAttribute(ALL_ACCOUNTS_ATTR, this.accountService.getAll(true));
			model.addAttribute(ALL_PAYEES_ATTR, getAllPayees());

			return "advancedSearch";
		}
		else if (req.getParameter("type").equals("chart")) {
			ChartProperties props = toChartProperties(jsonStr);				
			return chartByCategoriesOutput(props, req, model);
		}

		return "";
	}	
	
	@RequestMapping(value="/index/by/dates", method=RequestMethod.GET)
	public String indexByDates(ModelMap model) {
 
		return "indexForm"; 
	}
		
	@RequestMapping(value="/index/by/dates", method=RequestMethod.POST)	
	public RedirectView indexByDatesAction(HttpServletRequest req) { 
		String startStr = req.getParameter("from");
		String endStr = req.getParameter("to");
		Calendar mark = Util.today();
		mark.add(Calendar.DATE, 1);
		Util.zeroTimeOfDay(mark);
		
		Date start = StringUtils.isBlank(startStr) ? 
				new Date(0L) :
					Util.parseSolrDate(startStr + SolrParams.START_OF_DAY);
			
		Date end = StringUtils.isBlank(endStr) ? 
				mark.getTime() :
					Util.parseSolrDate(endStr + SolrParams.END_OF_DAY);
		
		
		this.solrService.removeTransactionsByDate(start, end);
		boolean ok = this.solrService.save(this.transactionService.getTransactionsByDate(start, end));
		
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl(
						ok ? "success|Indexing complete" : "failure|Problem indexing by dates")));
	}
	
	@RequestMapping(value="/index/all")	
	public RedirectView indexEverything(HttpServletRequest req) { 
		this.solrService.removeAllTransactions();
		this.solrService.save(this.transactionService.getAll());
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl("success|Indexing complete")));
	}
	
	@RequestMapping(value="/index/by/account/{accountId}")	
	public RedirectView indexByAccount(@PathVariable long accountId, HttpServletRequest req) { 
		Account a = this.accountService.get(accountId);
		this.solrService.removeTransactionsByAccount(a.getName());
		this.solrService.save(this.transactionService.getTransactionsForAccount(accountId));
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl("success|Indexing complete")));
	}
	
	private int getChartYear(HttpServletRequest req, String formElementName, int dflt) {
		String yearStr = req.getParameter(formElementName);
		
		if (StringUtils.isNumeric(yearStr)) {
			return Integer.valueOf(yearStr).intValue();
		}
		
		return dflt;
	}
	
	/*
	 * Chart properties input form
	 */
	@RequestMapping(value="/chart/by/categories", method=RequestMethod.GET)
	public String chartByCategoriesForm(HttpServletRequest req, ModelMap model) {
		this.categoryController.categoryList(model);
		
		ChartProperties props = new ChartProperties();
		CategoryGroup g = new CategoryGroup().setLabel("Group 1").setId(1);
		g.getCategories().add(new CategoryInput());
		props.getGroups().add(g);
		
		model.addAttribute(CHART_PROPS_ATTR, props);
		model.addAttribute("_yearRange", getYearRange());
		return "chart";
	}
	
	/*
	 * Process chart properties and produce corresponding chart.
	 */
	@RequestMapping(value="/chart/by/categories/out", method=RequestMethod.POST)
	public String chartByCategoriesOutput(HttpServletRequest req, ModelMap model) {
		
		ChartProperties props = getChartSearchCriteriaFromRequest(req);
		return chartByCategoriesOutput(props, req, model);
	}
	
	private String chartByCategoriesOutput(ChartProperties props, HttpServletRequest req, ModelMap model) {
		model.addAttribute("_yearRange", getYearRange());
		model.addAttribute(CHART_PROPS_ATTR, props);
		
		if (! props.isReady()) {
			model.addAttribute("noCategoriesSpecified", 1);
			return "chart";
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
		
		long amount;
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
				
				ds.addValue(Util.toPounds(-amount), 
						grp.getLabel(),
						Integer.valueOf(year));
				
				chartData.getData().put(year, -amount);
			}
		}
	 
		JFreeChart chart = ChartFactory.createBarChart(
		         "Category spend report", "Years", "Spend (£)",
		         ds,
		         PlotOrientation.VERTICAL, true, true, false);
		
		SVGGraphics2D svg2d = new SVGGraphics2D(1000, 600);
	    chart.draw(svg2d,new Rectangle2D.Double(0, 0, 1000, 600));
	    model.addAttribute("_chartSVG", svg2d.getSVGElement());
		
		return "chart"; 
	}
		
	/*
	 * This method allows us to de-serialize a json string into a list of objects. This is a neater way
	 * than returning a convenience object with a single property that is the list we are after.
	 * 
	 * (I don't know how this works, but it does!)
	 */
	private static <T> T fromJson(final TypeReference<T> type, final String jsonPacket) {

		T data = null;
		try {
			data = new ObjectMapper().readValue(jsonPacket, type);
		} catch (Exception e) {
			// Handle the problem
		}
		return data;
	}
	
	private static SolrParams toSolrParams(String jsonPacket) {

		try {
			return new ObjectMapper().readValue(jsonPacket, SolrParams.class);
		} catch (Exception e) {
			LOG.error("Jackson marshalling error", e);
		}
		return null;
	}
	
	private static ChartProperties toChartProperties(String jsonPacket) {

		try {
			return new ObjectMapper().readValue(jsonPacket, ChartProperties.class);
		} catch (Exception e) {
			LOG.error("Jackson marshalling error", e);
		}
		return null;
	}
	
	private static String toJson(Object o) {

		String s = null;
		try {
			s = new ObjectMapper().writeValueAsString(o);
		} catch (Exception e) {
			LOG.error("Jackson marshalling error", e);
		}
		return s;
	}
	
	private List<CategoryInput> readMultiCategoryInput(HttpServletRequest req, int groupId, int numCategories) {
		int m = 0;	
		String major, minor;
		boolean excluded;
		CategoryInput cat;
		List<CategoryInput>	list = new ArrayList<CategoryInput>();
		
		for (int i = 1; i < 10; i++) {
			major = req.getParameter(String.format("major-%d-%d", groupId, i));
			minor = req.getParameter(String.format("minor-%d-%d", groupId, i));
			excluded = Util.isPositive(req.getParameter(String.format("logic-%d-%d", groupId, i)));
			
			if (StringUtils.isNotBlank(major)) {					
				cat = new CategoryInput().
					setMajor(major).
					setMinor(minor).
					setExclude(excluded).
					setOptions(this.categoryService.getAllMinorValues(major));
				
				list.add(cat);
				
				if (++m >= numCategories) {
					break;
				}
			}
		}
		
		return list;
	}
	
	// How many categories are in this group?
	private int getNumCategoriesForGroup(List<MultiCategoryCounter> counters, int groupId) {
		for (MultiCategoryCounter c : counters) {
			if (c.getGroupId() == groupId) {
				return c.getCategoryCount();
			}
		}
		
		return 0;
	}
}