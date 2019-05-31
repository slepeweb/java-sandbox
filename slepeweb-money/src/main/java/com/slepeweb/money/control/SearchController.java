package com.slepeweb.money.control;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
import com.slepeweb.money.bean.CategoryInput;
import com.slepeweb.money.bean.CategoryGroup;
import com.slepeweb.money.bean.MultiCategoryCounter;
import com.slepeweb.money.bean.ChartProperties;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SolrService;
import com.slepeweb.money.service.TransactionService;

@Controller
public class SearchController extends BaseController {
	
	private static final String CHART_PROPS_ATTR = "_chartProps";
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;
	@Autowired private SolrService solrService;
	@Autowired private CategoryController categoryController;
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String form(ModelMap model) {
		this.categoryController.categoryList(model);
		
		model.addAttribute("_allAccounts", this.accountService.getAll(true));
		
		List<Payee> payees = this.payeeService.getAll();
		if (payees.size() > 0 && StringUtils.isBlank(payees.get(0).getName())) {
			payees.remove(0);
		}
		model.addAttribute("_allPayees", payees);
		
		// Create a single, default category group
		CategoryGroup grp = new CategoryGroup().setId(1).setLabel("unset");
		grp.getCategories().add(new CategoryInput().setId(1));
		model.addAttribute("_categoryGroup", grp);
		
		return "advancedSearch";
	}
	
	@RequestMapping(value="/search", method=RequestMethod.POST)
	public String resultsDefault(HttpServletRequest req, ModelMap model) {
		return results(1, req, model);
	}
	
	@RequestMapping(value="/search/{page}", method=RequestMethod.GET)
	public String results(@PathVariable int page, HttpServletRequest req, ModelMap model) {
		
		// Payee may be specified by either name or id, but not both!
		SolrParams params = 
			new SolrParams(new SolrConfig()).
			setAccountId(req.getParameter("accountId")).
			setPayeeId(req.getParameter("payeeId")).
			setPayeeName(req.getParameter("payee")).
			//setCategories(readMultiCategoryInput(req, 1, 10)).
			setMemo(req.getParameter("memo")).
			setFrom(req.getParameter("from")).
			setTo(req.getParameter("to")).
			setPageNum(page);
		
		String jsonStr = req.getParameter("counterStore");
		List<MultiCategoryCounter> counters = fromJson(new TypeReference<List<MultiCategoryCounter>>() {}, jsonStr);
		int groupId = 1;
		List<CategoryInput> inputs = readMultiCategoryInput(req, groupId, getNumCategoriesForGroup(counters, groupId));
		params.setCategories(adaptCategoryData(inputs));
				
		model.addAttribute("_response", this.solrService.query(params));				
		form(model);
		return "advancedSearch";
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
	public RedirectView index(@PathVariable long accountId, HttpServletRequest req) { 
		Account a = this.accountService.get(accountId);
		this.solrService.removeTransactionsByAccount(a.getName());
		this.solrService.save(this.transactionService.getTransactionsForAccount(accountId));
		return new RedirectView(String.format("%s/search/?flash=%s", 
				req.getContextPath(), Util.encodeUrl("success|Indexing complete")));
	}
	
	private int getChartFromYear(HttpServletRequest req) {
		int baseYearDflt = 2000;		
		int baseYear = baseYearDflt;
		String fromYearParam = req.getParameter("from");
		
		if (StringUtils.isNumeric(fromYearParam)) {
			baseYear = Integer.valueOf(fromYearParam).intValue();
			if (baseYear < 1970) {
				baseYear = baseYearDflt;
			}
		}
		
		return baseYear;
	}
	
	private int getChartNumYears(HttpServletRequest req) {
		int numYearsDflt = 10;		
		int numYears = numYearsDflt;		
		String numYearsParam = req.getParameter("numYears");
		
		if (StringUtils.isNumeric(numYearsParam)) {
			numYears = Integer.valueOf(numYearsParam).intValue();
			if (numYears < 2 || numYears > 20) {
				numYears = numYearsDflt;
			}
		}
		
		return numYears;
	}
	
	/*
	 * Chart properties input form
	 */
	@RequestMapping(value="/chart/by/categories", method=RequestMethod.GET)
	public String chartByCategories(HttpServletRequest req, ModelMap model) {
		this.categoryController.categoryList(model);
		
		ChartProperties props = null;
		if (req.getParameterMap().containsKey("repeat")) {
			props = (ChartProperties) req.getSession().getAttribute(CHART_PROPS_ATTR);
		}
		
		// Create a default single-category group
		if (props == null) {
			props = new ChartProperties();
			CategoryGroup g = new CategoryGroup().setLabel("Group 1").setId(1);
			g.getCategories().add(new CategoryInput());
			props.getGroups().add(g);
		}
		
		model.addAttribute(CHART_PROPS_ATTR, props);
		return "chartInput";
	}
	
	/*
	 * Process chart properties and produce corresponding chart.
	 */
	@RequestMapping(value="/chart/by/categories/out", method=RequestMethod.POST)
	public String chartByCategoriesOutput(HttpServletRequest req, ModelMap model) {
		
		ChartProperties props = new ChartProperties();
		props.setFromYear(getChartFromYear(req));		
		props.setNumYears(getChartNumYears(req));		
		String groupName;
		CategoryGroup group;
		
		req.getSession().setAttribute(CHART_PROPS_ATTR, props);
		model.addAttribute(CHART_PROPS_ATTR, props);
		
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
		int yearCounter;
		
		for (CategoryGroup grp : props.getGroups()) {
			
			p = new SolrParams(new SolrConfig().setPageSize(10000));
			p.setCategories(adaptCategoryData(grp.getCategories()));
			
			yearCounter = 0;
			
			for (int year = props.getFromYear(); yearCounter++ < props.getNumYears() && year < currentYear; year++) {
				from.set(Calendar.YEAR, year);
				to.set(Calendar.YEAR, year);
				p.setFrom(from.getTime());
				p.setTo(to.getTime());
				amount = 0;
				
				resp = this.solrService.query(p);
				
				for (FlatTransaction ft : resp.getResults()) {
					amount += ft.getAmount();
				}
				
				ds.addValue((int) (-amount / 100), 
						grp.getLabel(),
						Integer.valueOf(year));
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
	public static <T> T fromJson(final TypeReference<T> type, final String jsonPacket) {

		T data = null;
		try {
			data = new ObjectMapper().readValue(jsonPacket, type);
		} catch (Exception e) {
			// Handle the problem
		}
		return data;
	}
	
	private List<Category> adaptCategoryData(List<CategoryInput> list) {
		List<Category> categories = new ArrayList<Category>();
		
		for (CategoryInput cc : list) {
			categories.add(new Category().
					setMajor(cc.getMajor()).
					setMinor(cc.getMinor()).
					setExclude(cc.isExclude()));
		}
		
		return categories;
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