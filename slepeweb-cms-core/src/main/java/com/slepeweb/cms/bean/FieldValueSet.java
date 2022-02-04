package com.slepeweb.cms.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Field.FieldType;

public class FieldValueSet {
	private List<FieldValue> allValues;
	private Map<String, Map<String, FieldValue>> mappedFieldValues;

	private Site site;
	
	public FieldValueSet(Site s) {
		this(s, new ArrayList<FieldValue>());
	}
	
	public FieldValueSet(Site s, List<FieldValue> all) {
		this.site = s;
		this.allValues = all;
		this.mappedFieldValues = new HashMap<String, Map<String, FieldValue>>();
		
		// Place all field values into mapped structures
		for (FieldValue fv : getAllValues()) {
			getFieldValues(fv.getLanguage()).put(fv.getField().getVariable(), fv);
		}
	}

	public Site getSite() {
		return site;
	}

	public List<FieldValue> getAllValues() {
		return allValues;
	}

	public FieldValue getFieldValueObj(String variable, String language) {
		return getFieldValues(language).get(variable);
	}
	
	public String getFieldValue(String variable, String language) {
		return getStringValue(variable, language);
	}
	
	public String getStringValue(String variable, String language) {
		return getFieldValueObj(variable, language).getStringValue();
	}
	
	public Timestamp getDateFieldValue(String variable) {
		return getFieldValueObj(variable, getSite().getLanguage()).getDateValue();
	}
	
	public Integer getIntegerValue(String variable) {
		return getFieldValueObj(variable, getSite().getLanguage()).getIntegerValue();
	}
	
	public FieldValue getFallbackFieldValueObj(String variable, String language) {
		FieldValue fv = getFieldValueObj(variable, language);
		
		// Fallback to default site language if necessary
		if ((fv == null || StringUtils.isBlank(fv.getStringValue())) && ! language.equals(getSite().getLanguage())) {
			return getFieldValueObj(variable, getSite().getLanguage());
		}
		
		return fv;
	}
	
	public String getFallbackFieldValue(String variable, String language) {
		FieldValue fv = getFallbackFieldValueObj(variable, language);
		if (fv != null) {
			return fv.getStringValue();
		}
		return "";
	}
	
	public Timestamp getFallbackDateValue(String variable, String language) {
		FieldValue fv = getFallbackFieldValueObj(variable, language);
		if (fv != null) {
			return fv.getDateValue();
		}
		return new Timestamp(0);
	}
	
	public Integer getFallbackIntegerValue(String variable, String language) {
		FieldValue fv = getFallbackFieldValueObj(variable, language);
		if (fv != null) {
			return fv.getIntegerValue();
		}
		return 0;
	}
	
	/*
	 * This returns a map of merged field values that can easily be used by jsp's
	 */
	public Map<String, Object> getFields(String language) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		/* 
		 * First, get all values for the default language - the multilingual
		 * fields will be a subset of these.
		 */
		merge(getFieldValues(getSite().getLanguage()).values(), map);
		if (! language.equals(getSite().getLanguage())) {
			merge(getFieldValues(language).values(), map);
		}

		return map;
	}
	
	private void merge(Collection<FieldValue> values, Map<String, Object> map) {
		Object o, existing;
		String var;
		boolean doMerge;
		
		for (FieldValue fv : values) {
			doMerge = false;
			var = fv.getField().getVariable();
			existing = map.get(var);
			
			if (fv.getField().getType() == FieldType.integer) {
				o = fv.getIntegerValue();
				doMerge = existing == null || fv.getIntegerValue() != null;
			}
			else if (fv.getField().getType() == FieldType.date) {
				o = fv.getDateValue();
				doMerge = true;
			}
			else {
				o = fv.getStringValue();
				doMerge = existing == null || ! StringUtils.isBlank(fv.getStringValue());
			}
			
			if (doMerge) {
				map.put(var, o);
			}
		}
	}
	
	public void addFieldValue(FieldValue fv) {
		if (! this.allValues.contains(fv)) {
			this.allValues.add(fv);
			getFieldValues(fv.getLanguage()).put(fv.getField().getVariable(), fv);
		}
	}
	
	public Map<String, FieldValue> getFieldValues(String language) {
		Map<String, FieldValue> variableMap = this.mappedFieldValues.get(language);
		if (variableMap == null) {
			variableMap = new HashMap<String, FieldValue>();
			this.mappedFieldValues.put(language, variableMap);
		}
		
		return variableMap;
	}

	public Map<String, Map<String, FieldValue>> getMappedFieldValues() {
		return mappedFieldValues;
	}
}
