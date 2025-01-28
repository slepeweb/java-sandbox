package com.slepeweb.money.component;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.slepeweb.money.bean.Category_Group;
import com.slepeweb.money.bean.Category_GroupSet;
import com.slepeweb.money.bean.ChartProperties;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.SearchCategory;
import com.slepeweb.money.control.CategoryController;
import com.slepeweb.money.service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ChartFormSupport {

	public static final String CHART_PROPS_ATTR = "_chartProps";
	public static final String YEAR_RANGE_ATTR = "_yearRange";
	public static final String CHART_TYPE = "chart";
	
	public static final String FORM_VIEW = "chartForm";
	public static final String LIST_VIEW = "chartList";
	public static final String RESULTS_VIEW = "chartResults";
	
	public static final int NUM_EMPTY_GROUPS = 2;
	
	@Autowired private CategoryService categoryService;
	@Autowired private FormSupport formSupport;
	@Autowired private SearchFormSupport searchFormSupport;


	public void populateForm(SavedSearch ss, ChartProperties props, String formMode, ModelMap model) {

		List<String> allMajors = this.categoryService.getAllMajorValues();
		Category_GroupSet cgs = null;
		
		if (props.getCategories() == null) {
			cgs = new Category_GroupSet("Chart", Category_GroupSet.CHART_CTX, allMajors);
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
		}
		
		model.addAttribute(SearchFormSupport.SAVED_SEARCH_ATTR, ss);
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
			cg = this.formSupport.populateCategory_Group(startId + i, "Update label", null, SearchCategory.class, cgs);
			cg.setVisible(previousGroup == null || (i == 0 && ! previousGroup.hasCompletedEntries()));
			previousGroup = cg;
			groups.add(cg);
		}
	}
	

	public ChartProperties readSearchCriteria(HttpServletRequest req) {
		
		ChartProperties props = new ChartProperties();
		props.setTitle(req.getParameter("name"));
		props.setFromYear(getYear(req, "from", 2015));		
		props.setToYear(getYear(req, "to", 2019));		
		
		if (props.getToYear() < props.getFromYear()) {
			int tmp = props.getFromYear();
			props.setFromYear(props.getToYear());
			props.setToYear(tmp);
		}
		
		/*
		Category_Group group;
		String groupName;
		int numGroups = Integer.valueOf(req.getParameter("numGroups"));
		int numCategories;
		int n = 0;
				
		for (int groupId = 1; groupId <= numGroups; groupId++) {
			groupName = req.getParameter(String.format("group_%d_name", groupId));
			
			if (StringUtils.isNotBlank(groupName)) {
				group = this.formSupport.readCategoryInputs(req, groupId);
				group.setLabel(groupName);
				
				props.getGroups().add(group);				
				group.setCategoryInputs(this.searchFormSupport.readSearchCategoryInputs(req, groupId));
				
				if (++n >= numGroups) {
					break;
				}
			}
		}
		*/
		
		return props;
	}
	
	private int getYear(HttpServletRequest req, String formElementName, int dflt) {
		String yearStr = req.getParameter(formElementName);
		
		if (StringUtils.isNumeric(yearStr)) {
			return Integer.valueOf(yearStr).intValue();
		}
		
		return dflt;
	}
}
