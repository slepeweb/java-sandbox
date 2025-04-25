package com.slepeweb.money.bean;

import java.util.HashSet;
import java.util.Set;

public class ReconciliationData {
	
	private long target, accountId;
	private Set<Long> provisionals = new HashSet<Long>();
	
	public long getTarget() {
		return target;
	}
	
	public long getTargetAsPoundStr() {
		return target;
	}
	
	public ReconciliationData setTarget(long reconcileTarget) {
		this.target = reconcileTarget;
		return this;
	}
	
	public Set<Long> getTransactions() {
		return provisionals;
	}
	
	public ReconciliationData setTransactions(Set<Long> tlist) {
		this.provisionals = tlist;
		return this;
	}

	public long getAccountId() {
		return accountId;
	}

	public ReconciliationData setAccountId(long accountId) {
		this.accountId = accountId;
		return this;
	}
}
