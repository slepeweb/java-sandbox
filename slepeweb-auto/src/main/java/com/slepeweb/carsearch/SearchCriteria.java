package com.slepeweb.carsearch;

import org.apache.commons.lang3.StringUtils;

public class SearchCriteria {
	private String url, heading;
	private String[] trimFilter;

	public SearchCriteria() {}
	
	public SearchCriteria(String heading, String trimFilter, String url) {
		this.heading = heading;
		this.url = url;
		
		if (StringUtils.isNotBlank(trimFilter)) {
			this.trimFilter = trimFilter.split("[\\|,]");
		}
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String[] getTrimRegex() {
		return trimFilter;
	}

	public void setTrimRegex(String[] trimRegex) {
		this.trimFilter = trimRegex;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}
}
