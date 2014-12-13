package com.slepeweb.site.sws.spizza.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PizzaFactory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static Map<Base, Float[]> PRICES = new HashMap<Base, Float[]>();
	private static Object[][] DATA = new Object[][] {
		{"Margherita", 6.99f, 9.99f, 12.99f},
		{"Vegetarian", 7.99f, 10.99f, 13.99f},
		{"Pepperoni", 8.49f, 11.49f, 14.49f},
		{"Hawaiian", 8.49f, 11.49f, 14.49f},
		{"HotAndSpicy", 8.99f, 11.99f, 14.99f}
	};
	
	static {
		for (Object[] a : DATA) {
			if (a.length == 4) {
				PRICES.put(Base.valueOf((String)a[0]), 
						new Float[] {(Float)a[1], (Float)a[2], (Float)a[3]});
			}
		}
	}
	
	public static Pizza getPizza(Base b, Size s) {
		Pizza p = new Pizza();
		p.setBase(b);
		p.setSize(s);
		
		Float[] prices = PRICES.get(b);
		if (prices != null) {
			switch (s) {
				case Small:
					p.setPrice(prices[0]);
					break;
				case Medium:
					p.setPrice(prices[1]);
					break;
				case Large:
					p.setPrice(prices[2]);
					break;
				default:
					p.setPrice(20.00f);
					break;
			}
		}
		
		return p;
	}

	public enum Base {
		Margherita("Margherita"), Vegetarian("Vegetarian"), Pepperoni("Pepperoni"), 
		Hawaiian("Hawaiian"), HotAndSpicy("Hot & Spicy");
		
		private final String label;
		
		public String getKey() {
			return this.name();
		}
		
		public String getLabel() {
			return this.label;
		}
		
		Base(String s) {
			this.label = s;
		}
	}
	
	public enum Size {
		Small("Small (4 slices)"), Medium("Medium (6 slices)"), Large("Large (8 slices)");
		
		private final String label;
		
		public String getKey() {
			return this.name();
		}
		
		public String getLabel() {
			return this.label;
		}
		
		Size(String s) {
			this.label = s;
		}
	}
	
	public enum Topping {
		Anchovies("Anchovies", 0.99f), Olives("Olives", 0.99f), Jalapenos("Jalapenos", 0.99f), 
		GreenPepper("Green Peppers", 0.99f), Onions("Onions", 0.99f);
		
		private String label;
		private float price;
		
		Topping(String s, float f) {
			this.label = s;
			this.price = f;
		}

		public String getKey() {
			return this.name();
		}
		
		public String getLabel() {
			return this.label;
		}

		public float getPrice() {
			return this.price;
		}

		public void setPrice(float price) {
			this.price = price;
		}
	}
	
	public static Topping[] getToppingOptions() {
		return Topping.values();
	}

	public static Size[] getSizeOptions() {
		return Size.values();
	}

	public static Base[] getBaseOptions() {
		return Base.values();
	}
}
