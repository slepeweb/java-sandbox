package com.slepeweb.money.bean;

public class RunningBalance extends Transaction {

	private long balance;
	
	public RunningBalance(Transaction t) {
		assimilate(t);
		setId(t.getId());
	}

	public long getBalance() {
		return balance;
	}

	public RunningBalance setBalance(long balance) {
		this.balance = balance;
		return this;
	}
	
	
}
