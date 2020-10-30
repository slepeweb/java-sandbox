package com.slepeweb.money.bean;

import java.sql.Timestamp;

public class NakedTransaction {
	private Timestamp entered;
	private long transferid, amount;
	private boolean expense;
	
	public boolean isTransfer() {
		return this.transferid > 0;
	}
	
	public Timestamp getEntered() {
		return entered;
	}
	
	public NakedTransaction setEntered(Timestamp entered) {
		this.entered = entered;
		return this;
	}
	
	public long getTransferid() {
		return transferid;
	}
	
	public NakedTransaction setTransferid(long transferid) {
		this.transferid = transferid;
		return this;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public NakedTransaction setAmount(long amount) {
		this.amount = amount;
		return this;
	}
	
	public boolean isExpense() {
		return expense;
	}
	
	public NakedTransaction setExpense(boolean expense) {
		this.expense = expense;
		return this;
	}		
}
