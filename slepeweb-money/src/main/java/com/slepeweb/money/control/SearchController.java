package com.slepeweb.money.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.solr.SolrConfig;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.SolrService;

@Controller
@RequestMapping(value="/search")
public class SearchController extends BaseController {
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private SolrService solrService;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String form(ModelMap model) {
		
		model.addAttribute("_allAccounts", this.accountService.getAll(false));
		
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
	
	@RequestMapping(value="/", method=RequestMethod.POST)
	public String resultsDefault(HttpServletRequest req, ModelMap model) {
		return results(1, req, model);
	}
	
	@RequestMapping(value="/{page}", method=RequestMethod.GET)
	public String results(@PathVariable int page, HttpServletRequest req, ModelMap model) {
		
		SolrParams params = 
			new SolrParams(new SolrConfig()).
			setAccountId(req.getParameter("accountId")).
			setPayeeId(req.getParameter("payeeId")).
			setMajorCategory(req.getParameter("category")).
			setMemo(req.getParameter("memo")).
			setPageNum(page);
		
		model.addAttribute("_response", this.solrService.query(params));				
		form(model);
		return "advancedSearch";
	}	
}