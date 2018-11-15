package com.slepeweb.money.bean;

import java.sql.Timestamp;

import com.slepeweb.money.Util;

public class FlatTransaction {

	private Timestamp entered;
	private String payee, category, account, memo, reference;
	private long amount;
	
	@Override
	public String toString() {
		return String.format("%s | %s: %s (%4$td/%4$tm/%4$tY)", 
				getAccount(), getPayee(), Util.formatPounds(getAmount()), getEntered());
	}
	
	public Timestamp getEntered() {
		return entered;
	}
	
	public String getEnteredStr() {
		return Util.formatTimestamp(getEntered());
	}
	
	public FlatTransaction setEntered(Timestamp entered) {
		this.entered = entered;
		return this;
	}
	
	public String getPayee() {
		return payee;
	}
	
	public FlatTransaction setPayee(String payee) {
		this.payee = payee;
		return this;
	}
	
	public String getCategory() {
		return category;
	}
	
	public FlatTransaction setCategory(String category) {
		this.category = category;
		return this;
	}
	
	public String getAccount() {
		return account;
	}
	
	public FlatTransaction setAccount(String account) {
		this.account = account;
		return this;
	}
	
	public String getMemo() {
		return memo;
	}
	
	public FlatTransaction setMemo(String memo) {
		this.memo = memo;
		return this;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public String getAmountInPounds() {
		return Util.formatPounds(getAmount());
	}
	
	public FlatTransaction setAmount(long amount) {
		this.amount = amount;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public FlatTransaction setReference(String reference) {
		this.reference = reference;
		return this;
	}
}
