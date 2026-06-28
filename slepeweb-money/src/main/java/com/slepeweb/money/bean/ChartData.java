package com.slepeweb.money.bean;

import java.util.HashMap;
import java.util.Map;

public class ChartData {
	private String label;
	private Map<Integer, Long> data = new HashMap<Integer, Long>();
	private Boolean expense;
	
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
	
	public boolean isExpense() {
		if (this.expense == null) {
			int pos = 0, neg = 0;
			for (Long lng : this.data.values()) {
				if (lng.longValue() > 0) {
					pos++;
				}
				else {
					neg++;
				}
			}
			
			this.expense = (pos + neg) > 0 && (neg * 1.0 / (pos + neg)) > 0.67;
		}
		
		return this.expense.booleanValue();
	}
	
	public static void main(String[] args) {
		ChartData cd = new ChartData();
		cd.data = new HashMap<Integer, Long>();
		cd.data.put(2000, -700L);
		cd.data.put(2001, -2700L);
		cd.data.put(2002, -1700L);
		cd.data.put(2003, 700L);
		cd.data.put(2004, 700L);
		cd.data.put(2005, 700L);
		cd.data.put(2006, 700L);
		cd.data.put(2007, 700L);
		
		System.out.println(cd.isExpense() ? "Expense" : "Income");
		
	}
}
