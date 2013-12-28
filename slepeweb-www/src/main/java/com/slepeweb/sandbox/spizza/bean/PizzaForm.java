package com.slepeweb.sandbox.spizza.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.slepeweb.sandbox.spizza.bean.PizzaFactory.Base;
import com.slepeweb.sandbox.spizza.bean.PizzaFactory.Size;
import com.slepeweb.sandbox.spizza.bean.PizzaFactory.Topping;

public class PizzaForm implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String base, size;
	private List<String> toppings = new ArrayList<String>();
	
	public Topping[] getToppingOptions() {
		return PizzaFactory.getToppingOptions();
	}

	public Size[] getSizeOptions() {
		return PizzaFactory.getSizeOptions();
	}

	public Base[] getBaseOptions() {
		return PizzaFactory.getBaseOptions();
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public List<String> getToppings() {
		return toppings;
	}

	public void setToppings(List<String> toppings) {
		this.toppings = toppings;
	}

}
