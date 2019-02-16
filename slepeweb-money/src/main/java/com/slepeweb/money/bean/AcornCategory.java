package com.slepeweb.money.bean;

import java.util.HashMap;
import java.util.Map;

public class AcornCategory extends Category {
	
	private static Map<String, String[]> TRANSLATIONS = new HashMap<String, String[]>();
	private static String[][] DEFINITIONS = new String[][] {
		{"Bldg Insurance", "Insurance", "Buildings"},
		{"Car", "Car", ""},
		{"Cavalier", "Car", ""},
		{"Clothes", "Clothing", ""},
		{"Community Charge", "Services", "Community Charge"},
		{"Computing", "Computing", ""},
		{"Council Tax", "Services", "Community Charge"},
		{"Eating out", "Food", "Dining Out"},
		{"Electricity", "Services", "Electricity"},
		{"Escort", "Ford Escort", ""},
		{"Gas Board", "Services", "Gas"},
		{"Groceries", "Food", "Groceries"},
		{"Holidays", "Holiday", ""},
		{"Home Insurance", "Insurance", "Home Contents + Buildings"},
		{"Household", "Housing", ""},
		{"Leisure", "Leisure", ""},
		{"Milk", "Food", "Milk"},
		{"Mortgage (ass)", "Housing", "Mortgage"},
		{"Mortgage (int)", "Housing", "Mortgage"},
		{"School Dinners", "Food", "School dinners"},
		{"Telephone", "Services", "Telephone"},
		{"TV License", "Leisure", "TV License"},
		{"Water Rates", "Services", "Water Rates"},
		{"Xantia", "Xantia", ""},
		{"Company Expenses", "Job Expenses", ""},
		{"bank cheque", "", ""},
		{"Interest/Bonus", "Investment Income", "Interest"},
		{"John Reeks", "Gifts", "David"},
		{"Salary", "Wages & Salary", ""},
		{"Cash", "Cash", ""},
		{"General", "Miscellaneous", ""},
		{"Presents", "Gifts", ""}
	};
	
	static {
		for (String[] arr : DEFINITIONS) {
			TRANSLATIONS.put(arr[0], new String[] {arr[1], arr[2]});
		}
	}
	
	public static String[] translate(String acornName) {
		return TRANSLATIONS.get(acornName);
	}

	public static final String JOHN_REEKS = "John Reeks";
	private boolean payment, income;
	private String title;

	public AcornCategory() {}
	
	public AcornCategory(Category c) {
		assimilate(c);
		setId(c.getId());
	}
	
	public AcornCategory setOrigId(long id) {
		super.setOrigId(id);
		return this;
	}
	
	public void assimilate(Object obj) {
		if (obj instanceof Category) {
			Category c = (Category) obj;
			setMajor(c.getMajor());
			setMinor(c.getMinor());
			setOrigId(c.getOrigId());
		}
		
		if (obj instanceof AcornCategory) {
			AcornCategory c = (AcornCategory) obj;
			setPayment(c.isPayment());
			setIncome(c.isIncome());
			setTitle(c.getTitle());
		}
	}
	
	public Category getCategory() {
		return (Category) this;
	}
	
	public boolean isPayment() {
		return payment;
	}

	public AcornCategory setPayment(boolean payment) {
		this.payment = payment;
		return this;
	}

	public boolean isIncome() {
		return income;
	}

	public AcornCategory setIncome(boolean income) {
		this.income = income;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public AcornCategory setTitle(String title) {
		this.title = title;
		return this;
	}
}
