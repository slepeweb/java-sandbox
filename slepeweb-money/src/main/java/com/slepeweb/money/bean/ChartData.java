package com.slepeweb.money.bean;

import java.util.HashMap;
import java.util.Map;

public class ChartData {
	private String label;
	private Map<Integer, Long> data = new HashMap<Integer, Long>();
	
	public String getLabel() {
		return label;
	}
	
	public ChartData setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public Map<Integer, Long> getData() {
		return data;
	}
	
	public ChartData setData(Map<Integer, Long> data) {
		this.data = data;
		return this;
	}
	
	public long getTotal() {
		long total = 0;
		for (Long value : data.values()) {
			total += value;
		}
		return total;
	}
	
	public long getAverage() {
		return getTotal() / data.size();
	}
}
