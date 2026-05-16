package com.slepeweb.money.bean;

import org.apache.commons.lang3.StringUtils;

public class SavedSearch extends DbEntity {
	
	public static final int ADHOC_ID = -1;
		
	private String name, description, json;
	
	public void assimilate(Object obj) {
		if (obj instanceof SavedSearch) {
			SavedSearch ss = (SavedSearch) obj;
			setName(ss.getName()).
			setJson(ss.getJson()).
			setDescription(ss.getDescription());
		}
	}
		

	@Override
	public boolean isDefined4Insert() {
		return 
				StringUtils.isNotBlank(getName()) &&
				StringUtils.isNotBlank(getJson());
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public String getJson() {
		return json;
	}

	public SavedSearch setJson(String note) {
		this.json = note;
		return this;
	}

	@Override
	public SavedSearch setId(long id) {
		super.setId(id);
		return this;
	}
	
	public String getName() {
		return StringUtils.isNotBlank(this.name) ? this.name : "Not set";
	}

	public SavedSearch setName(String s) {
		this.name = s;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((json == null) ? 0 : json.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		SavedSearch other = (SavedSearch) obj;
		if (json == null) {
			if (other.json != null)
				return false;
		} else if (!json.equals(other.json))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			
			return false;
		return true;
	}


	public String getDescription() {
		return description;
	}


	public SavedSearch setDescription(String description) {
		this.description = description;
		return this;
	}


}
