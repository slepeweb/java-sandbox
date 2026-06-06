package com.slepeweb.money.bean;

import java.time.LocalDate;

public class TransactionList {
	private Account account;
	private RunningBalance[] runningBalances;
	private LocalDate periodStart, periodEnd;
	private MonthPager pager;
	
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
	
	public LocalDate getPeriodStart() {
		return periodStart;
	}
	
	public TransactionList setPeriodStart(LocalDate periodStart) {
		this.periodStart = periodStart;
		return this;
	}
	
	public LocalDate getPeriodEnd() {
		return periodEnd;
	}
	
	public TransactionList setPeriodEnd(LocalDate periodEnd) {
		this.periodEnd = periodEnd;
		return this;
	}
	
	public MonthPager getPager() {
		return pager;
	}
	
	public TransactionList setPager(MonthPager p) {
		this.pager = p;
		return this;
	}
	
	public String getPeriod() {
		// Use month and year of either start/end
		return String.format("%1$tB %1$tY", getPeriodStart());
	}
}
