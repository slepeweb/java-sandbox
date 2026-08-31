package com.slepeweb.money.bean;

import java.util.Objects;

import com.slepeweb.money.Util;

public class YearlyAssetStatus {

	private int year;
	private long income = 0l, expense = 0l, balance = 0l;
	
	public YearlyAssetStatus() {}
	
	public YearlyAssetStatus(int y) {
		this.year = y;
	}
	
	public void assimilate(Object obj) {
		if (obj instanceof YearlyAssetStatus) {
			YearlyAssetStatus yas = (YearlyAssetStatus) obj;
			setYear(yas.getYear());
			setIncome(yas.getIncome());
			setExpense(yas.getExpense());
			setBalance(yas.getBalance());
		}
	}
	
	public boolean isDefined4Insert() {
		return this.year > 1990; 
	}
	
	@Override
	public String toString() {
		return String.format("Asset growth for %s: (In) %s, (Out) %s, (Bal) %s", 
				this.year,
				Util.formatPounds(this.income), 
				Util.formatPounds(this.expense), 
				Util.formatPounds(this.balance));
	}
	
	public void add(YearlyAssetStatus other) {
		setIncome(getIncome() + other.getIncome());
		setExpense(getExpense() + other.getExpense());
	}
	
	public int getYear() {
		return year;
	}
	
	public long getGrowth() {
		return getIncome() + getExpense();
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
	
	public void count(NakedTransaction nt) {
		if (nt.isExpense()) {
			debit(nt.getAmount());
		}
		else {
			credit(nt.getAmount());
		}
	}

	public long getBalance() {
		return balance;
	}

	public YearlyAssetStatus setBalance(long balance) {
		this.balance = balance;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Long.valueOf(balance), Long.valueOf(expense), Long.valueOf(income));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		YearlyAssetStatus other = (YearlyAssetStatus) obj;
		return balance == other.balance && expense == other.expense && income == other.income;
	}
}
