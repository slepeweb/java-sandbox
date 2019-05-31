package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import com.slepeweb.money.bean.CategoryInput;

public class CategoryGroup {
	
	private String label = "";
	private int id;
	private List<CategoryInput> categories = new ArrayList<CategoryInput>();
	
	public String getFilterStr() {
		StringBuilder sb = new StringBuilder();
		for (CategoryInput cc : this.categories) {
			if (sb.length() > 0) {
				sb.append(" OR ");
			}
			
			sb.append(cc.toString());
		}
		return sb.toString();
	}
	
	public String getLabel() {
		return label;
	}
	
	public CategoryGroup setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public List<CategoryInput> getCategories() {
		return this.categories;
	}
	
	public CategoryGroup setCategories(List<CategoryInput> cats) {
		this.categories = cats;
		return this;
	}

	public int getId() {
		return id;
	}

	public CategoryGroup setId(int id) {
		this.id = id;
		return this;
	}
	
}
