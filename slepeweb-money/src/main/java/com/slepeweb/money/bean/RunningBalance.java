package com.slepeweb.money.bean;

public class RunningBalance extends Transaction {

	private long balance;
	private Account mirror;
	
	public RunningBalance(Transaction t) {
		assimilate(t);
		setId(t.getId());
		
		if (t.isTransfer()) {
			this.mirror = t.getMirrorAccount();
		}
	}

	public long getBalance() {
		return balance;
	}

	public RunningBalance setBalance(long balance) {
		this.balance = balance;
		return this;
	}

	public Account getMirror() {
		return mirror;
	}
}
