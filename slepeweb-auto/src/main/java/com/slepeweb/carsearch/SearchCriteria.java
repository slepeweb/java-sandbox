package com.slepeweb.carsearch;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<SearchParameter> parseSearchParameters() {
		List<SearchParameter> list = new ArrayList<SearchParameter>();
		SearchParameter p;
		String[] pair;
		int cursor = getUrl().indexOf("?");
		
		for (String param : getUrl().substring(cursor + 1).split("&")) {
			pair = param.split("=");
			p = new SearchParameter().setName(pair[0]);
			if (pair.length > 1) {
				try {
					p.setValue(URLDecoder.decode(pair[1], "utf-8"));
				} 
				catch (UnsupportedEncodingException e) {
				}
			}
			list.add(p);
		}
		
		return list;
	}
}
