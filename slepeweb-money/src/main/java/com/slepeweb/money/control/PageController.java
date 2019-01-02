package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.NamedList;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.SolrService;
import com.slepeweb.money.service.TransactionService;

@Controller
public class PageController extends BaseController {
	
	@Autowired private AccountService accountService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;
	@Autowired private SolrService solrService;
	
	@RequestMapping(value="/")	
	public String dashboard(ModelMap model) { 
		List<Account> all = this.accountService.getAllWithBalances();
		String lastType = null;
		List<Pair<String, Long>> summary = new ArrayList<Pair<String, Long>>();
		int index = -1;
		long total = 0L, grandTotal = 0L;
		
		for (Account a : all) {
			if (lastType == null || ! lastType.equals(a.getType())) {
				index++;
				grandTotal += total;
				total = 0;
				lastType = a.getType();
				summary.add(Pair.of("dummy", 0L));
			}
			
			total += a.getBalance();
			summary.set(index, Pair.of(a.getType(), total));
		}
		
		grandTotal += total;
		model.addAttribute("_accounts", all);
		model.addAttribute("_summary", summary);
		model.addAttribute("_grandTotal", grandTotal);
		return "dashboard";
	}

	@RequestMapping(value="/notfound")	
	public String notfound(ModelMap model) { 
		return dashboard(model);
	}

	@RequestMapping(value="/category/list")	
	public String categoryList(ModelMap model) { 
		List<NamedList<Category>> categories = new ArrayList<NamedList<Category>>();
		NamedList<Category> mapping = null;
		String lastName = null, nextName = null;
		List<Category> all = this.categoryService.getAll();
		
		for (Category c : all) {
			if (c.getMajor().length() == 0) {
				c.setMajor("(None)");
			}
			
			if (c.getMinor().length() == 0) {
				c.setMinor("(all sub-categories)");
			}
			
			nextName = c.getMajor();
			if (lastName == null || ! lastName.equals(nextName)) {
				mapping = new NamedList<Category>(nextName, new ArrayList<Category>());
				categories.add(mapping);
				lastName = nextName;
			}
			
			mapping.getObjects().add(c);
		}
		
		model.addAttribute("_categories", categories);
		model.addAttribute("_count", all.size());
		return "categoryList";
	}
	
	@RequestMapping(value="/index")	
	public String index() { 
		for (Transaction t : this.transactionService.getTransactionsForAccount(94L, 
				Util.parseTimestamp("2000-01-01"), Util.parseTimestamp("2018-12-31"))) {
			
			this.solrService.save(t);
		}
		
		return null;
	}
	
}