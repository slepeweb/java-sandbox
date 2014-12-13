package com.slepeweb.site.sws.spizza.bean;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.slepeweb.site.sws.spizza.bean.PizzaFactory.Base;
import com.slepeweb.site.sws.spizza.bean.PizzaFactory.Size;
import com.slepeweb.site.sws.spizza.bean.PizzaFactory.Topping;

public class Pizza implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Base base;
	private Size size;
	private float price;
	private List<Topping> toppings = new ArrayList<Topping>();
	
	public void addTopping(Topping t) {
		if (t != null) {
			getToppings().add(t);
		}
	}
	
	public String getToppingsAsString() {
		StringBuilder sb = new StringBuilder();
		
		if (getToppings().size() > 0) {
			for (Topping t : getToppings()) {
				if (sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(t.getLabel());
			}
			return sb.toString();
		}
		
		return "No extra toppings";
	}
	
	public List<Topping> getToppings() {
		return this.toppings;
	}
	
	public Size getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = Size.valueOf(size);
	}
	
	public void setSize(Size size) {
		this.size = size;
	}

	public void setToppings(List<Topping> toppings) {
		this.toppings = toppings;
	}

	public Base getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = Base.valueOf(base);
	}

	public void setBase(Base base) {
		this.base = base;
	}

	public float getPrice() {
		float price = this.price;
		for (Topping t : getToppings()) {
			price += t.getPrice();
		}
		return price;
	}
	
	public String getPriceFormatted() {
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);
		return nf.format(getPrice());
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Override
	public String toString() {		
		StringBuilder sb = new StringBuilder(String.format("%s, %s [%s]", 
				getBase().getLabel(), getSize().getLabel(), getToppingsAsString()));
		
		return sb.toString();
	}
}
