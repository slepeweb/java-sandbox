package com.slepeweb.commerce.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.CmsBeanFactory;

public class Basket {
	private List<OrderItem> items = new ArrayList<OrderItem>();
	private static final String DELIM = "|";
	
	public void add(OrderItem oi) {
		if (! getItems().contains(oi)) {
			getItems().add(oi);
		}
		else {
			int c = getItems().indexOf(oi);
			oi = getItems().get(c);
			oi.setQuantity(oi.getQuantity() + 1);
		}
	}
	
	public int getSize() {
		return getItems().size();
	}
	
	public static Basket parseCookieStringValue(String value) {
		Basket b = new Basket();
		if (StringUtils.isNotBlank(value)) {
			String[] parts = value.split("\\" + DELIM);
			int c = 0;
			int numItems = Integer.parseInt(parts[c]);
			OrderItem oi;
			
			for (int i = 0; i < numItems; i++) {
				c = (3 * i) + 1;
				oi = CmsBeanFactory.makeOrderItem(
						Integer.parseInt(parts[c]), 
						Long.parseLong(parts[c + 1]), 
						parts[c + 2].equals("null") ? null : parts[c + 2]);
				
				b.getItems().add(oi);
			}
		}
		
		return b;
	}
	
	public String formatCookieStringValue() {
		StringBuilder sb = new StringBuilder();
		sb.append(getItems().size());
		for (OrderItem oi : getItems()) {
			sb.
				append(DELIM).append(oi.getQuantity()).
				append(DELIM).append(oi.getOrigItemId()).
				append(DELIM).append(oi.getQualifier() == null ? "null" : oi.getQualifier());
		}
		
		return sb.toString();
	}
	
	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
	
	public boolean isNotEmpty() {
		return getItems().size() > 0;
	}
	
	public float getTotalValue() {
		float total = 0F;
		
		for (OrderItem oi : getItems()) {
			total += oi.getTotalValue();
		}
		
		return total;
	}
	
	public String getTotalValueAsString() {
		return Product.CURRENCY_FORMAT.format(getTotalValue());
	}
}
