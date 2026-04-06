package com.slepeweb.money.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.money.Util;

public class SavingsAccount extends Account {
	
	private Timestamp matures;
	private String rate;
	private String access, schedule, owner;
	private int accountId;
	
	public void assimilate(Object obj) {
		if (obj instanceof SavingsAccount) {
			super.assimilate(obj);			
			SavingsAccount a = (SavingsAccount) obj;
			setMatures(a.getMatures());
			setRate(a.getRate());
			setAccess(a.getAccess());
			setSchedule(a.getSchedule());
			setOwner(a.getOwner());
		}
		else if (obj instanceof Account) {
			super.assimilate(obj);
		}
	}
	
	@Override
	public String getSummary() {
		List<String> summary = new ArrayList<String>();
		add2Summary(summary, getSortCode(), getAccountNo(), getRollNo(), getOwner(), getAccess(), getSchedule(), 
				Util.formatTimestamp(getMatures()), getNote());
		return StringUtils.join(summary, " | ");
	}
	
	public boolean isLinked() {
		return this.accountId > 0;
	}
	
	public Timestamp getMatures() {
		return matures;
	}

	public SavingsAccount setMatures(Timestamp matures) {
		this.matures = matures;
		return this;
	}

	public String getRate() {
		return rate;
	}

	public SavingsAccount setRate(String rate) {
		this.rate = rate;
		return this;
	}

	public String getSchedule() {
		return schedule;
	}

	public SavingsAccount setSchedule(String schedule) {
		this.schedule = schedule;
		return this;
	}

	public String getOwner() {
		return owner;
	}

	public SavingsAccount setOwner(String owner) {
		this.owner = owner;
		return this;
	}

	public String getAccess() {
		return access;
	}

	public SavingsAccount setAccess(String access) {
		this.access = access;
		return this;
	}

	public int getAccountId() {
		return accountId;
	}

	public SavingsAccount setAccountId(int accountId) {
		this.accountId = accountId;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((access == null) ? 0 : access.hashCode());
		result = prime * result + accountId;
		result = prime * result + ((matures == null) ? 0 : matures.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((rate == null) ? 0 : rate.hashCode());
		result = prime * result + ((schedule == null) ? 0 : schedule.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SavingsAccount other = (SavingsAccount) obj;
		if (access == null) {
			if (other.access != null)
				return false;
		} else if (!access.equals(other.access))
			return false;
		if (accountId != other.accountId)
			return false;
		if (matures == null) {
			if (other.matures != null)
				return false;
		} else if (!matures.equals(other.matures))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (rate == null) {
			if (other.rate != null)
				return false;
		} else if (!rate.equals(other.rate))
			return false;
		if (schedule == null) {
			if (other.schedule != null)
				return false;
		} else if (!schedule.equals(other.schedule))
			return false;
		return true;
	}
	
}
