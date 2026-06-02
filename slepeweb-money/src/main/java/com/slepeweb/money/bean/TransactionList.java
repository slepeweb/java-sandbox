package com.slepeweb.money.bean;

import java.sql.Date;

public class TransactionList {
	private Account account;
	private RunningBalance[] runningBalances;
	private Date periodStart, periodEnd;
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
	
	public Date getPeriodStart() {
		return periodStart;
	}
	
	public TransactionList setPeriodStart(Date periodStart) {
		this.periodStart = periodStart;
		return this;
	}
	
	public Date getPeriodEnd() {
		return periodEnd;
	}
	
	public TransactionList setPeriodEnd(Date periodEnd) {
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
