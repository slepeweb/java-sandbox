package com.slepeweb.money.bean;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

public class SavedSearch extends DbEntity {
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd @ HH:mm");
	
	private String name, type, description, json;
	private Timestamp saved;
	
	public void assimilate(Object obj) {
		if (obj instanceof SavedSearch) {
			SavedSearch ss = (SavedSearch) obj;
			setType(ss.getType()).
				setName(ss.getName()).
				setJson(ss.getJson()).
				setSaved(ss.getSaved()).
				setDescription(ss.getDescription());
		}
	}
		

	@Override
	public boolean isDefined4Insert() {
		return 
				this.saved != null && 
				StringUtils.isNotBlank(getType()) &&
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

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((json == null) ? 0 : json.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((saved == null) ? 0 : saved.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (saved == null) {
			if (other.saved != null)
				return false;
		} else if (!saved.equals(other.saved))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			
			return false;
		return true;
	}


	public SavedSearch setType(String type) {
		this.type = type;
		return this;
	}

	public String getDescription() {
		return description;
	}


	public SavedSearch setDescription(String description) {
		this.description = description;
		return this;
	}


	public Timestamp getSaved() {
		return saved;
	}
	
	public String getSavedWithMinutes() {
		return SDF.format(this.saved);
	}

	public SavedSearch setSaved(Timestamp saved) {
		this.saved = saved;
		return this;
	}
}
