package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class LoggerBean extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String packag, level;

	public Long getId() {
		return NO_ID;
	}
	
	public void assimilate(Object obj) {
		if (obj instanceof LoggerBean) {
			LoggerBean lg = (LoggerBean) obj;
			setPackag(lg.getPackag());
			setLevel(lg.getLevel());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getPackag()) &&
			StringUtils.isNotBlank(getLevel());
	}
	
	public LoggerBean save() {
		return getCmsService().getLoglevelService().save(this);
	}
	
	public void delete() {
		// TODO: Implement
	}
	
	@Override
	public String toString() {
		return String.format("[%s]: [%s]", getPackag(), getLevel());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((packag == null) ? 0 : packag.hashCode());
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
		LoggerBean other = (LoggerBean) obj;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (packag == null) {
			if (other.packag != null)
				return false;
		} else if (!packag.equals(other.packag))
			return false;
		return true;
	}

	public String getPackag() {
		return packag;
	}

	public LoggerBean setPackag(String packag) {
		this.packag = packag;
		return this;
	}

	public String getLevel() {
		return level;
	}

	public LoggerBean setLevel(String level) {
		this.level = level;
		return this;
	}
}
