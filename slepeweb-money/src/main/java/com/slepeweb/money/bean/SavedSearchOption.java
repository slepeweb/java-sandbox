package com.slepeweb.money.bean;

public class SavedSearchOption {
	
	private SavedSearch savedSearch;
	private boolean selected;
	
	public SavedSearchOption(SavedSearch ss, boolean yesno) {
		this.savedSearch = ss;
		this.selected = yesno;
	}
	
	public void setSavedSearch(SavedSearch savedSearch) {
		this.savedSearch = savedSearch;
	}

	public SavedSearch getSavedSearch() {
		return savedSearch;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}
}
