package com.slepeweb.commerce.bean;

import java.util.ArrayList;
import java.util.List;

public class AxisValueSelector {
	
	private List<Option> options = new ArrayList<Option>();
	
	public List<Option> getOptions() {
		return options;
	}

	public AxisValueSelector setOptions(List<Option> options) {
		this.options = options;
		return this;
	}
	
	public static class Option {
		private String body;
		private long value, stock;
		private boolean selected;
		
		public String getBody() {
			return body;
		}
		
		public Option setBody(String body) {
			this.body = body;
			return this;
		}
		
		public long getValue() {
			return value;
		}
		
		public Option setValue(long value) {
			this.value = value;
			return this;
		}
		
		public boolean isSelected() {
			return selected;
		}
		
		public Option setSelected(boolean selected) {
			this.selected = selected;
			return this;
		}

		public long getStock() {
			return stock;
		}

		public Option setStock(long stock) {
			this.stock = stock;
			return this;
		}
	}
}
