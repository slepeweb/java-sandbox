package com.slepeweb.money.bean.chart;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ChartCategory {
	
	public static final int MAX = 3;

	private String major = "", minor = "";
	private List<String> options = new ArrayList<String>();
	
	public List<String> getOptions() {
		return options;
	}
	
	public ChartCategory setOptions(List<String> options) {
		this.options = options;
		return this;
	}

	public String getMajor() {
		return major;
	}

	public ChartCategory setMajor(String major) {
		this.major = major;
		return this;
	}

	public String getMinor() {
		return minor;
	}

	public ChartCategory setMinor(String minor) {
		this.minor = minor;
		return this;
	}
	
	public boolean isReady() {
		return StringUtils.isNotBlank(this.major);
	}
}
