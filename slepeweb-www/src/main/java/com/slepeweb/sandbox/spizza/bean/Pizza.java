package com.slepeweb.sandbox.spizza.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class Pizza implements Serializable {
	private static final long serialVersionUID = 1L;
	private String size;
	private List<Topping> toppings = new ArrayList<Topping>();

	public void addTopping(Topping t) {
		if (t != null) {
			getToppings().add(t);
		}
	}
	
	public String getToppingsAsString() {
		return StringUtils.collectionToCommaDelimitedString(getToppings());
	}
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public List<Topping> getToppings() {
		return toppings;
	}

	public void setToppings(List<Topping> toppings) {
		this.toppings = toppings;
	}
}
