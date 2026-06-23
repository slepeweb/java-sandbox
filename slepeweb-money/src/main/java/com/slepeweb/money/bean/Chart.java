package com.slepeweb.money.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.money.Util;

public class Chart extends DbEntity {
	
	private static int CURRENT_YEAR = Util.today().getYear();
	private String name, description, searchIds, notes;
	private int fromYear = CURRENT_YEAR, toYear = CURRENT_YEAR;
	
	public void assimilate(Object obj) {
		if (obj instanceof Chart) {
			Chart ch = (Chart) obj;
			setName(ch.getName()).
			setDescription(ch.getDescription()).
			setSearchIds(ch.getSearchIds()).
			setFromYear(ch.getFromYear()).
			setToYear(ch.getToYear()).
			setNotes(ch.getNotes());
		}
	}
		
	@Override
	public boolean isDefined4Insert() {
		return StringUtils.isNotBlank(getName()) && 
				getFromYear() > 0 && 
				getToYear() > 0;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public String getSearchIds() {
		return searchIds;
	}
	
	public List<Long> getSearchIdsAsList() {
		List<Long> list = new ArrayList<Long>();
		
		if (StringUtils.isBlank(this.searchIds)) {
			return list;
		}
		
		for (String s : this.searchIds.split(",")) {
			list.add(Long.parseLong(s));
		}
		
		return list;
	}
	
	public List<String> getNotesAsList() {
		List<String> list = new ArrayList<String>();
		
		if (StringUtils.isBlank(this.notes)) {
			return list;
		}
		
		for (String s : this.notes.split("(\\r\\n)")) {
			if (StringUtils.isNotBlank(s)) {
				list.add(s.trim());
			}
		}
		
		return list;
	}
	
	public List<SavedSearchOption> identifySelectedOptions(List<SavedSearch> options) {
		List<Long> ids = getSearchIdsAsList();
		List<SavedSearchOption> flaggedOptions = new ArrayList<SavedSearchOption>(options.size());
		
		for (SavedSearch ss : options) {
			flaggedOptions.add(new SavedSearchOption(ss, ids.contains(ss.getId())));
		}
		
		return flaggedOptions;
	}

	public Chart setSearchIds(String s) {
		this.searchIds = s;
		return this;
	}

	public String getName() {
		return this.name;
	}

	public Chart setName(String s) {
		this.name = s;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + fromYear;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((searchIds == null) ? 0 : searchIds.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + toYear;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chart other = (Chart) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (fromYear != other.fromYear)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (searchIds == null) {
			if (other.searchIds != null)
				return false;
		} else if (!searchIds.equals(other.searchIds))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (toYear != other.toYear)
			return false;
		return true;
	}


	public String getDescription() {
		return description;
	}


	public Chart setDescription(String description) {
		this.description = description;
		return this;
	}


	public int getFromYear() {
		return fromYear;
	}


	public Chart setFromYear(int from) {
		this.fromYear = from;
		return this;
	}


	public int getToYear() {
		return toYear;
	}


	public Chart setToYear(int to) {
		this.toYear = to;
		return this;
	}


	public String getNotes() {
		return notes;
	}


	public Chart setNotes(String notes) {
		this.notes = notes;
		return this;
	}


}
