package com.slepeweb.money.bean;

import java.sql.Timestamp;

public class TransactionList {
	private Account account;
	private RunningBalance[] runningBalances;
	private Timestamp periodStart, periodEnd;
	private int page;
	
	public Account getAccount() {
		return account;
	}
	
	public TransactionList setAccount(Account account) {
		this.account = account;
		return this;
	}
	
	public RunningBalance[] getRunningBalances() {
		return runningBalances;
	}
	
	public TransactionList setRunningBalances(RunningBalance[] runningBalances) {
		this.runningBalances = runningBalances;
		return this;
	}
	
	public Timestamp getPeriodStart() {
		return periodStart;
	}
	
	public TransactionList setPeriodStart(Timestamp periodStart) {
		this.periodStart = periodStart;
		return this;
	}
	
	public Timestamp getPeriodEnd() {
		return periodEnd;
	}
	
	public TransactionList setPeriodEnd(Timestamp periodEnd) {
		this.periodEnd = periodEnd;
		return this;
	}
	
	public int getPage() {
		return page;
	}
	
	public TransactionList setPage(int page) {
		this.page = page;
		return this;
	}
	
	public String getPeriod() {
		// Use month and year of either start/end
		return String.format("%1$tB %1$tY", getPeriodStart());
	}
	
	public int getNext() {
		return getPage() == 1 ? 1 : getPage() - 1;
	}
	
	public int getPrevious() {
		return getPage() + 1;
	}
	
	public boolean isNextExists() {
		return getPage() > 1;
	}
}
