package com.slepeweb.money.component;

public class ChartFormSupport {
/*
	public static final String CHART_PROPS_ATTR = "_chartProps";
	public static final String YEAR_RANGE_ATTR = "_yearRange";
	public static final String CHART_TYPE = "chart";
	
	public static final String FORM_VIEW = "chartForm";
	public static final String LIST_VIEW = "chartList";
	public static final String RESULTS_VIEW = "chartResults";
	
	private static final int NUM_EMPTY_GROUPS = 3;

	@Autowired private CategoryService categoryService;

	@Autowired private FormSupport formSupport;
	@Autowired private SearchFormSupport searchFormSupport;

	public ChartProperties populateEmptyForm() {
		ChartProperties props = new ChartProperties().setTitle("No title");
		props.setInputGroups(populateChartCategoryInputs(null));
		
		return props;
	}
	
	public void setCommonChartModelAttributes(SavedSearch ss, String formMode, ModelMap model) {
		ChartProperties props = null;
		if (! formMode.equals("create") && ss != null) {
			props = this.searchFormSupport.fromJson(new TypeReference<ChartProperties>() {}, ss.getJson());
			model.addAttribute(SearchFormSupport.SAVED_SEARCH_ATTR, ss);
		}
		else {
			props = populateEmptyForm();
		}
		
		model.addAttribute(CHART_PROPS_ATTR, props);
		model.addAttribute(SearchFormSupport.FORM_MODE_ATTR, formMode);
		model.addAttribute(YEAR_RANGE_ATTR, this.formSupport.getYearRange());
		model.addAttribute(CategoryController.ALL_MAJOR_CATEGORIES_ATTR, 
				this.categoryService.getAllMajorValues());
	}
	
	public List<SearchCategoryInputGroup> populateChartCategoryInputs(List<SearchCategoryGroup> groups) {
		int count = 0;
		int numVisible = groups != null ? groups.size() : 0;
		SearchCategoryInputGroup inputGroup;
		List<SearchCategoryInputGroup> allGroups = new ArrayList<SearchCategoryInputGroup>();
		
		// In this context, the list holds extended objects, ie SearchCategoryInput's
		List<CategoryInput> categoryInputs;
		
		// For data already stored in the db ...
		if (groups != null) {
			for (SearchCategoryGroup group : groups) {
				inputGroup = new SearchCategoryInputGroup(count);
				categoryInputs = this.formSupport.populateCategoryInputs(group.getCategories(), SearchCategory.class);				
				inputGroup.setCategoryInputs(this.searchFormSupport.cast2SearchCategoryInputs(categoryInputs));
				
				count++;
				if (count == numVisible) {
					inputGroup.setLastVisible(true);
				}
				
				allGroups.add(inputGroup);
			}
		}
		
		// Now add some empty groups, in case user need to input more ...
		for (int i = 0; i < NUM_EMPTY_GROUPS; i++) {
			inputGroup = new SearchCategoryInputGroup(count);
			inputGroup.setVisible(i == 0 && (groups == null || numVisible == 0));
			inputGroup.setLastVisible(inputGroup.isVisible());
			
			allGroups.add(inputGroup);
		}
				
		return allGroups;
	}
	*/
	/*
	public ChartProperties getChartPropertiesFromRequest(HttpServletRequest req) {
		ChartProperties props = new ChartProperties();
		List<SearchCategoryInputGroup> groups = new ArrayList<SearchCategoryInputGroup>();
		props.setGroups(groups);
		props.setTitle(req.getParameter("name"));
		props.setFromYear(getYear(req, "from", 2015));		
		props.setToYear(getYear(req, "to", 2019));		
		
		if (props.getToYear() < props.getFromYear()) {
			int tmp = props.getFromYear();
			props.setFromYear(props.getToYear());
			props.setToYear(tmp);
		}
		
		String groupName;
		SearchCategoryInputGroup group;
		
		int numGroups = Integer.valueOf(req.getParameter("numGroups"));
		int numCategories;
		int n = 0;
				
		for (int groupId = 1; groupId <= numGroups; groupId++) {
			groupName = req.getParameter(String.format("group_%d_name", groupId));
			
			if (StringUtils.isNotBlank(groupName)) {
				group = new SearchCategoryInputGroup().
						setLabel(groupName);
				
				props.getGroups().add(group);				
				group.setCategoryInputs(this.searchFormSupport.readSearchCategoryInputs(req, groupId));
				
				if (++n >= numGroups) {
					break;
				}
			}
		}
		
		return props;
	}
	
	private int getYear(HttpServletRequest req, String formElementName, int dflt) {
		String yearStr = req.getParameter(formElementName);
		
		if (StringUtils.isNumeric(yearStr)) {
			return Integer.valueOf(yearStr).intValue();
		}
		
		return dflt;
	}
	*/
}
