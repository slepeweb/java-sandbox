package com.slepeweb.money.bean;

public class RunningBalance extends Transaction {

	private String balance;
	
	public RunningBalance(Transaction t) {
		assimilate(t, this);
	}

	public String getBalance() {
		return balance;
	}

	public RunningBalance setBalance(String balance) {
		this.balance = balance;
		return this;
	}
	
	
}
