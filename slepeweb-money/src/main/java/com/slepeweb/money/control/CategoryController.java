package com.slepeweb.money.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.NamedList;

@Controller
@RequestMapping(value="/category")
public class CategoryController extends BaseController {
	
	public static final String ALL_MAJOR_CATEGORIES_ATTR = "_allMajorCategories";
	
	@RequestMapping(value="/list")	
	public String categoryList(ModelMap model) { 
		List<NamedList<Category>> categories = new ArrayList<NamedList<Category>>();
		NamedList<Category> mapping = null;
		String lastName = null, nextName = null;
		List<Category> all = this.categoryService.getAll();
		
		for (Category c : all) {
			if (c.getMajor().length() == 0) {
				continue;
			}
			
			if (c.getMinor().length() == 0) {
				c.setMinor("(no sub-category)");
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
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addForm(ModelMap model) {
		
		model.addAttribute("_category", new Category());
		model.addAttribute("_formMode", "add");
		model.addAttribute(ALL_MAJOR_CATEGORIES_ATTR, this.categoryService.getAllMajorValues());
		
		return "categoryForm";
	}
	
	@RequestMapping(value="/form/{categoryId}", method=RequestMethod.GET)
	public String updateForm(@PathVariable long categoryId, ModelMap model) {
		
		model.addAttribute("_category", this.categoryService.get(categoryId));
		model.addAttribute("_formMode", "update");
		model.addAttribute(ALL_MAJOR_CATEGORIES_ATTR, this.categoryService.getAllMajorValues());
		model.addAttribute("_numDeletableTransactions", this.transactionService.getNumTransactionsForCategory(categoryId));
		return "categoryForm";
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public RedirectView update(HttpServletRequest req, ModelMap model) {
		
		String flash;	
		boolean isUpdateMode = req.getParameter("formMode").equals("update");
		
		Category c = new Category().
				setId(Long.valueOf(req.getParameter("id"))).
				setMajor(req.getParameter("major")).
				setMinor(req.getParameter("minor")).
				setType(req.getParameter("categorytype"));
		
		try {
			this.categoryService.save(c);
			flash = String.format("success|Category successfully %s", isUpdateMode ? "updated" : "added");
		}
		catch (Exception e) {
			flash = String.format("failure|Failed to %s category", isUpdateMode ? "update" : "add new");
		}
	
		return new RedirectView(String.format("%s/category/form/%d?flash=%s", 
				req.getContextPath(), c.getId(), Util.encodeUrl(flash)));
	}
	
	@RequestMapping(value="/delete/{categoryId}", method=RequestMethod.GET)
	public RedirectView delete(@PathVariable long categoryId, HttpServletRequest req, ModelMap model) {
		
		String flash;
		User u = (User) model.get(USER);
		long numDeletables = this.transactionService.getNumTransactionsForCategory(categoryId);
		
		if ((u != null && u.getUsername().equals("MONEY_ADMIN")) || numDeletables == 0) {		
			try {
				this.categoryService.delete(categoryId);
				flash="success|Category successfully deleted";
			}
			catch (Exception e) {
				flash="failure|Failed to delete category";
			}
		}
		else {
			flash = "failure|Failed to delete category - authorisation failure";		
		}
		
		return new RedirectView(String.format("%s/category/list?flash=%s", 
				req.getContextPath(), Util.encodeUrl(flash)));
	}	
}