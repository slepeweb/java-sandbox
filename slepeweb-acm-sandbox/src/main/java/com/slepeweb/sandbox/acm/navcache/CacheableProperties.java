package com.slepeweb.sandbox.acm.navcache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.sandbox.acm.utils.SdlUtils;

/**
 * This class is used by NavigationLink to determine:
 * - which fields are cached as properties
 * - which properties need to be cached regardless of whether the item has corresponding fields
 * - whether the field value needs processing/conversion before being cached.
 * 
 * @author George
 *
 */
class CacheableProperties implements ICacheableProperties {
	
	private static Map<String, Set<String>> PROPERTIES_NEVER_CACHED = new HashMap<String, Set<String>>(PROPERTIES_NEVER_CACHED_ARR.length);
	private static List<FieldDefinition> PROPERTIES_ALWAYS_CACHED = new ArrayList<FieldDefinition>(PROPERTIES_ALWAYS_CACHED_ARR.length);
	private static Map<String, CacheablePropertyRule> PROPERTY_RULES = new HashMap<String, CacheablePropertyRule>(PROPERTY_RULES_ARR.length);
	
	static {
		// Properties that are always cached
		for (String[] arr : PROPERTIES_ALWAYS_CACHED_ARR) {
			if (arr.length > 1) {
				PROPERTIES_ALWAYS_CACHED.add(new FieldDefinition(arr[0], arr[1]));
			}
		}
		
		// Properties that are never cached
		Set<String> types;
		for (String[] arr : PROPERTIES_NEVER_CACHED_ARR) {
			if (arr.length == 2) {
				types = new HashSet<String>(arr[1].length());
				PROPERTIES_NEVER_CACHED.put(arr[0], types);
				
				for (String type : arr[1].split("\\|")) {
					types.add(type);
				}
			}
		}
		
		// Special handlers
		CacheablePropertyRule handler;
		
		for (String[] props : PROPERTY_RULES_ARR) {
			if (props.length > 2) {
				handler = new CacheablePropertyRule();
				handler.setVariable(props[0]);
				handler.setType(props[1]);
				handler.setDefaultValueStr(props[2]);
				handler.setNeverNone(false);
				
				PROPERTY_RULES.put(handler.getVariable(), handler);
			}
		}
	}
	
	/**
	 * Produces a pipe-seperated String of item types
	 * 
	 * @param itemTypes
	 * @return
	 */
	static String concat(String ... itemTypes) {
		StringBuilder sb = new StringBuilder();
		
		if (itemTypes.length > 0) {
			for (String type : itemTypes) {
				sb.append(type).append("|");
			}
			return sb.toString().substring(0, sb.length() - 1);
		}
		
		return "";
	}
	
	/**
	 * Determines whether a property should be cached.
	 * 
	 * @param property
	 * @param type
	 * @param value
	 * @return
	 */
	static boolean isCacheable(String property, String type, Object value) {
		if (PROPERTIES_NEVER_CACHED.containsKey(property)) {
			// Check if there is a type-match
			Set<String> types4thisRule = PROPERTIES_NEVER_CACHED.get(property);
			for (String type4thisRule : types4thisRule) {
				if (type4thisRule.equals("*") || type4thisRule.equals(type)) {
					return false;
				}
			}
		}
		
		// Getting this far means this field is potentially cacheable.
		// Final check is for 'never-none'
		CacheablePropertyRule rules = getRules(property);
		if (rules != null) {
			return ! rules.isNeverNone() || (value != null && value instanceof String && ! value.equals("none"));
		}
		
		return true;
	}
	
	static CacheablePropertyRule getRules(String property) {
		return PROPERTY_RULES.get(property);
	}
	
	static List<FieldDefinition> getPropertiesAlwaysCached() {
		return PROPERTIES_ALWAYS_CACHED;
	}
	
	/**
	 * Supporting class, holding the definition of a single rule
	 * 
	 * @author George
	 */
	static class CacheablePropertyRule {
		String variable, type;
		boolean neverNone;
		String defaultValueStr;
		
		/**
		 * This method applies this rule to a given field value.
		 * 
		 * @param value
		 * @param navLinkUrl
		 * @return
		 */
		Object apply(Object value, String navLinkUrl) {
			// Deal with null values for which a default is available
			if (value == null && getDefaultValueStr() != null) {
				if (getType().equals(STRING) || getType().equals(URL)) {
					return getDefaultValueStr();
				}
				else if (getType().equals(INTEGER) && StringUtils.isNumeric(getDefaultValueStr())) {
					return Integer.valueOf(getDefaultValueStr());
				}
				else if (getType().equals(DATE)) {
					Matcher m = DATE_PATTERN.matcher(getDefaultValueStr());
					if (m.matches()) {
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.DATE, Integer.parseInt(m.group(1)));
						cal.set(Calendar.MONTH, Integer.parseInt(m.group(2)) - 1);
						cal.set(Calendar.YEAR, Integer.parseInt(m.group(3)));
						return cal.getTime();
					}
				}
				else if (getType().equals(BOOLEAN)) {
					return getDefaultValueStr().equals(YES);
				}
				else if (getType().equals(STRING_TO_INTEGER)) {
					return Integer.valueOf(getDefaultValueStr());
				}
			}
			else if (value != null) {
				// Value is not null - may need some sort of processing	
				// Type conversion?
				if (value instanceof String) {
					if (getType().equals(STRING_TO_INTEGER)) {
						return Integer.valueOf((String) value);
					}
					else if (getType().equals(BOOLEAN)) {
						return ((String) value).trim().toLowerCase().matches("y|yes|true");
					}
					else if (getType().equals(RELATIVE_URL)) {
						return SdlUtils.getPathPartIfSameDomain(navLinkUrl, (String) value);
					}
				}
			}
			
			// No special handling taking place
			return value;
		}
		
		String getVariable() {
			return variable;
		}
		void setVariable(String variable) {
			this.variable = variable;
		}
		String getType() {
			return type;
		}
		void setType(String type) {
			this.type = type;
		}
		boolean isNeverNone() {
			return neverNone;
		}
		void setNeverNone(boolean neverNone) {
			this.neverNone = neverNone;
		}

		public String getDefaultValueStr() {
			return defaultValueStr;
		}

		public void setDefaultValueStr(String defaultValueStr) {
			this.defaultValueStr = defaultValueStr;
		}
	}
	
	static class FieldDefinition {
		String name;
		Integer type;
		
		FieldDefinition(String n, String t) {
			setName(n);
			setType(Integer.valueOf(t));
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getType() {
			return type;
		}
		public void setType(Integer type) {
			this.type = type;
		}
	}
}
