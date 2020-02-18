package com.slepeweb.money.bean;

import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.money.Util;

public class Property {
	
	private String key, value;
	
	public Property(String k, String v) {
		this.key = k;
		this.value = v;
	}

	public String getKey() {
		return key;
	}

	public Property setKey(String key) {
		this.key = key;
		return this;
	}

	public String getValue() {
		return value;
	}

	public Property setValue(String value) {
		this.value = value;
		return this;
	}
	
	public long getLongValue() {
		if (StringUtils.isNumeric(getValue())) {
			return Long.parseLong(getValue());
		}
		
		return -1L;
	}
	
	public Timestamp getTimestampValue() {
		if (StringUtils.isNotBlank(getValue())) {
			return Util.parseTimestamp(getValue());
		}
		
		return Util.now();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Property other = (Property) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
