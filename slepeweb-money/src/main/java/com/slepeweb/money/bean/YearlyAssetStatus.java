package com.slepeweb.money.bean;

public class YearlyAssetStatus {

	private int year;
	private long income, expense;
	
	public YearlyAssetStatus(int y) {
		this.year = y;
	}
	
	public void add(YearlyAssetStatus other) {
		setIncome(getIncome() + other.getIncome());
		setExpense(getExpense() + other.getExpense());
	}
	
	public int getYear() {
		return year;
	}
	
	public long getNetAmount() {
		return getIncome() - getExpense();
	}
	
	public YearlyAssetStatus setYear(int year) {
		this.year = year;
		return this;
	}
	
	public long getIncome() {
		return income;
	}
	
	public YearlyAssetStatus setIncome(long income) {
		this.income = income;
		return this;
	}
	
	public long getExpense() {
		return expense;
	}
	
	public YearlyAssetStatus setExpense(long expense) {
		this.expense = expense;
		return this;
	}
	
	public void credit(long l) {
		this.income += l;
	}
	
	public void debit(long l) {
		this.expense += l;
	}
	
	public void count(long l) {
		if (l < 0) {
			debit(-l);
			return;
		}
		credit(l);
	}
}
