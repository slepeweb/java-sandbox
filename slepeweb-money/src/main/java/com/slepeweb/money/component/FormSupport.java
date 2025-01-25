package com.slepeweb.money.component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Category_;
import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.Category_GroupSet;
import com.slepeweb.money.bean.SearchCategory;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class FormSupport {
	
	public static final int NUM_EMPTY_CATS = 6;
	public static final int NUM_EMPTY_GROUPS = 2;
	@Autowired private CategoryService categoryService;
	
	/*
	 * This method constructs a Category_GroupSet given a list of either 
	 * a) SplitTransaction or
	 * b) SearchCategory objects.
	 */
	public Category_Group populateCategory_Group(int groupId, String label, 
			List<?> categoryLikeObjects, Class<?> clazz, Category_GroupSet cgs) {
		
		boolean isTransactionMode = clazz.equals(SplitTransaction.class);
		Category_Group cg = new Category_Group(groupId, label, cgs);		
		Category_ c;
		int count = 0;
		int numVisible = categoryLikeObjects != null ? categoryLikeObjects.size() : 0;
		
		// For splits already stored in the db ...
		if (categoryLikeObjects != null) {
			for (Object in : categoryLikeObjects) {
				c = isTransactionMode ? new Category_((SplitTransaction) in) : 
					new Category_((SearchCategory) in);
				
				c.setVisible(true);
				
				count++;
				if (count == numVisible) {
					c.setLastVisible(true);
				}
				
				cgs.addOptions(c.getMajor(), this.categoryService.getAllMinorValues(c.getMajor()));
				
				if (! isTransactionMode) {
					c.setExclude(((SearchCategory) in).isExclude());
				}
				
				cg.getCategories().add(c);
			}
		}
		
		addEmptyCategories(cg);				
		return cg;
	}
	
	public void addEmptyCategories(Category_Group cg) {
		Category_ c;
		int numVisible = cg.getSize();
		
		Category noCategory = this.categoryService.getNoCategory();
		
		for (int i = 0; i < NUM_EMPTY_CATS; i++) {
			c = new Category_(noCategory);
			c.setVisible(i == 0 && numVisible == 0);			
			c.setLastVisible(c.isVisible());
			
			cg.getCategories().add(c);
		}
	}
	
	/*
	 * Empty groups in the set are only required for charts.
	 */
	public void addEmptyGroups(Category_GroupSet cgs) {
		List<Category_Group> groups = cgs.getGroups();
		Category_Group cg;
		int numVisible = cgs.getSize();
		int startId = numVisible + 1;
		
		for (int i = 0; i < NUM_EMPTY_GROUPS; i++) {
			cg = populateCategory_Group(startId + i, "Update label", null, SearchCategory.class, cgs);
			cg.setVisible(i == 0 && numVisible == 0);
			groups.add(cg);
		}
	}
	
	/*
	 * This method constructs a Category_GroupSet using posted form data 
	 */
	public Category_GroupSet readCategoryInputs(HttpServletRequest req, int groupId) {
		Category_GroupSet cgs = new Category_GroupSet();
		cgs.setAllMajors(this.categoryService.getAllMajorValues());
		Category_Group cg;
		Category_ c;
		
		int numGroups = Integer.valueOf(req.getParameter("numGroups"));
		String major, minor, memo, amountStr;
		boolean excluded;		
		String suffix, name ;
		int numCategories;
		
		for (int i = 1; i <= numGroups; i++) {
			name = "numCategories" + "_" + i;
			numCategories = Integer.valueOf(req.getParameter(name));
			cg = new Category_Group(i, "", null);
			cgs.getGroups().add(cg);
			
			for (int j = 1; j <= numCategories; j++) {
				suffix = String.format("_%d_%d", i, j);
				major = req.getParameter("major" + suffix);
				
				if (StringUtils.isNotBlank(major)) {			
					minor = req.getParameter("minor" + suffix);
					excluded = Util.isPositive(req.getParameter("logic" + suffix));
					memo = req.getParameter("memo" + suffix);
					amountStr = req.getParameter("amount" + suffix);
					
					c = new Category_().
						setMajor(major).
						setMinor(minor).
						setMemo(memo).
						setAmount(StringUtils.isBlank(amountStr) ? 0L : Util.parsePounds(amountStr));
					
					cgs.addOptions(major, this.categoryService.getAllMinorValues(major));
					c.setExclude(excluded);
					cg.getCategories().add(c);
				}
			}
		}		
		
		return cgs;
	}

	public List<Integer> getDaysOfMonth() {
		List<Integer> list = new ArrayList<Integer>(28);
		for (int i = 1; i <= 28; i++) {
			list.add(i);
		}
		return list;
	}

	public List<Integer> getYearRange() {
		List<Integer> range = new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		for (int y = 1990; y <= cal.get(Calendar.YEAR); y++) {
			range.add(y);
		}
		return range;
	}
	
}
