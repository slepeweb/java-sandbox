package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.slepeweb.money.service.CategoryService;

@JsonIgnoreProperties({"id", "visible", "lastVisible", "size", "root"})
public class Category_Group {
	
	private int id = 1;
	private String label = "";
	private List<Category_> categories = new ArrayList<Category_>();
	private boolean visible, lastVisible;
	private Map<String,List<String>> options = new HashMap<String,List<String>>();
	private Category_GroupSet root;
	
	public Category_Group() {}
	
	public Category_Group(int i, String label) {
		this.id = i;
		this.label = label;
	}
	
	@Override
	public String toString() {
		return String.format("Category group#%d, %s", this.id, this.label);
	}
	
	public boolean hasCompletedEntries() {
		for (Category_ c : getCategories()) {
			if (StringUtils.isNotBlank(c.getMajor())) {
				return true;
			}
		}
		
		return false;
	}
	
	public void addOptions(String major, List<String> allMinors) {
		if (! this.options.containsKey(major)) {
			this.options.put(major, allMinors);
		}
	}
	
	public List<SplitTransaction> toSplitTransactions(CategoryService categoryService, long amountPlusOrMinus) {
		List<SplitTransaction> splits = new ArrayList<SplitTransaction>();
		SplitTransaction st;
		Category c;
		
		for (Category_ c_ : this.categories) {
			c = categoryService.get(c_.getMajor(), c_.getMinor());
			st = new SplitTransaction().
				setCategory(c).
				setMemo(c_.getMemo()).
				setAmount(c_.getAmount() * amountPlusOrMinus);
			
			splits.add(st);
		}
		
		return splits;
	}
	
	public int getSize() {
		return this.categories.size();
	}
	
	public int getId() {
		return id;
	}
	
	public Category_Group setId(int id) {
		this.id = id;
		return this;
	}
	
	public List<Category_> getCategories() {
		return categories;
	}
	
	public Category_Group setCategories(List<Category_> categories) {
		this.categories = categories;
		return this;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public Category_Group setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}
	
	public Category_Group setAllCategoriesVisible() {
		Category_ c;
		int numCats = getSize();
		
		for (int i = 0; i < getSize(); i++) {
			c = getCategories().get(i);
			c.setVisible(true);
			c.setLastVisible(i == numCats - 1);
		}
		return this;
	}
	
	public boolean isLastVisible() {
		return lastVisible;
	}
	
	public Category_Group setLastVisible(boolean lastVisible) {
		this.lastVisible = lastVisible;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public Category_Group setLabel(String label) {
		this.label = label;
		return this;
	}

	public Map<String, List<String>> getOptions() {
		return options;
	}
	
	public Category_Group setOptions(Map<String, List<String>> options) {
		this.options = options;
		return this;
	}
	
	public Category_GroupSet getRoot() {
		return root;
	}

	public Category_Group setRoot(Category_GroupSet root) {
		this.root = root;
		return this;
	}
}
