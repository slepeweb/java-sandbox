package com.slepeweb.money.control;

import java.awt.geom.Rectangle2D;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;
	@Autowired private SolrService solrService;
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public String form(ModelMap model) {
		
		model.addAttribute("_allAccounts", this.accountService.getAll(true));
		
		List<Payee> payees = this.payeeService.getAll();
		if (payees.size() > 0 && StringUtils.isBlank(payees.get(0).getName())) {
			payees.remove(0);
		}
		model.addAttribute("_allPayees", payees);
		
		List<String> categories = this.categoryService.getAllMajorValues();
		if (categories.size() > 0 && StringUtils.isBlank(categories.get(0))) {
			categories.remove(0);
		}
		model.addAttribute("_allMajorCategories", categories);
		
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
			setMajorCategory(req.getParameter("category")).
			setMemo(req.getParameter("memo")).
			setFrom(req.getParameter("from")).
			setTo(req.getParameter("to")).
			setPageNum(page);
				
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
	
	@RequestMapping(value="/chart/by/categories", method=RequestMethod.GET)
	public String chartByCategories(ModelMap model) {
 
		int baseYear = 2000;
		Calendar from = Util.today();
		int lastYear = from.get(Calendar.YEAR);
		
		// from is Jan 1
		from.set(Calendar.DATE, 1);
		from.set(Calendar.MONTH, 0);
		
		// to is Dec 31
		Calendar to = Calendar.getInstance();
		to.setTime(from.getTime());
		to.set(Calendar.DATE, 31);
		to.set(Calendar.MONTH, 11);
		
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		String[][] categories = new String[][] {
				new String[] {"Food", "Groceries"},
				new String[] {"Laguna", "Diesel"},
				new String[] {"Clothing"},
				new String[] {"Holiday"}
		};
		
		long amount;
		SolrResponse<FlatTransaction> resp;
		SolrParams p = new SolrParams(new SolrConfig().setPageSize(10000));
		
		for (String[] parts : categories) {
			p.setMajorCategory(parts[0]);
			p.setMinorCategory(parts.length > 1 ? parts[1] : null);				
			
			for (int year = baseYear; year < lastYear; year++) {
				from.set(Calendar.YEAR,  year);
				to.set(Calendar.YEAR,  year);
				p.setFrom(from.getTime());
				p.setTo(to.getTime());
				amount = 0;
				
				resp = this.solrService.query(p);
				
				for (FlatTransaction ft : resp.getResults()) {
					amount += ft.getAmount();
				}
				
				ds.addValue((int) (-amount / 100), 
						parts[0] + (parts.length > 1 ? " -> " + parts[1] : ""),
						Integer.valueOf(year));
			}
		}
	 
		JFreeChart chart = ChartFactory.createLineChart(
		         "Category spend report", "Years", "Spend (£)",
		         ds,
		         PlotOrientation.VERTICAL, true, true, false);
		
		SVGGraphics2D svg2d = new SVGGraphics2D(1000, 600);
	    chart.draw(svg2d,new Rectangle2D.Double(0, 0, 1000, 600));
	    model.addAttribute("_chartSVG", svg2d.getSVGElement());
		
		/*
		File lineChart = new File( "/tmp/LineChart.jpeg" ); 
		
	    try {
			ChartUtils.saveChartAsJPEG(lineChart , chart, 1000 ,600);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	    
	    model.addAttribute("_chartPath", lineChart.getAbsolutePath());
		*/
		
		return "chart"; 
	}
		
}