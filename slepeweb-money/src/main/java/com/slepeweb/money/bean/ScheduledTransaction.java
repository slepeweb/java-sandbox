package com.slepeweb.money.bean;

import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.money.service.ScheduledSplitService;

public class ScheduledTransaction extends Transaction {
	
	private String label = "";
	private Timestamp nextDate;
	private String period;
	private boolean enabled;
	
	/*
	 *  When a transfer is entered into the transaction table, a second transaction
	 *  that mirrors the first is also added. So, there are 2 transaction records 
	 *  for every transfer. Here are 2 example transaction records representing a
	 *  single transfer:
	 *  
	 *  id       accountid   payeeid    categoryid     split     amount      transferid
	 *  -------------------------------------------------------------------------------
	 *  27510    71          985        1              0         -16800      27511
	 *  27511    21          985        1              0         16800       27510
	 *  
	 *  Notice how id and transferid are linked.
	 *  
	 *  As far as a ScheduledTransaction that represents a transfer is concerned, all it 
	 *  needs to know is the 'other' account. So, using the above example, account 71 is
	 *  debited 16800 pence, and account 21 is credited with the same amount.
	 */
	private Account mirror;

	
	@Override
	public String toString() {
		return getLabel() + ": " + super.toString();
	}
	
	public void assimilate(Object obj) {
		super.assimilate(obj);
		ScheduledTransaction source = (ScheduledTransaction) obj;
		setLabel(source.getLabel());
		setMirror(source.getMirror());
		setEnabled(source.isEnabled());
		setNextDate(source.getNextDate());
		
		assimilateSplits(source);
	}
	
	public void assimilateSplits(Transaction source) {
		getSplits().clear();
		for (SplitTransaction st : source.getSplits()) {
			getSplits().add(st.setTransactionId(getId()));
		}
	}
	
	@Override
	public boolean isDefined4Insert() {
		return  
			super.isDefined4Insert() &&
			StringUtils.isNotBlank(this.label) &&
			this.nextDate != null &&
			StringUtils.isNotBlank(this.period);
	}
	
	@Override
	public String getTypeIdentifier() {
		return "schedule";
	}
	

	public boolean isTransfer() {
		return getMirror() != null && getMirror().getId() > 0L;
	}

	public void setSplits(ScheduledSplitService svc) {
		setSplits(svc.get(getId()));
	}
	
	public String getLabel() {
		return label;
	}

	public ScheduledTransaction setLabel(String label) {
		this.label = label;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public ScheduledTransaction setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public String getPeriod() {
		return period;
	}

	public ScheduledTransaction setPeriod(String interval) {
		this.period = interval;
		return this;
	}

	public Timestamp getNextDate() {
		return nextDate;
	}

	public ScheduledTransaction setNextDate(Timestamp nextDate) {
		this.nextDate = nextDate;
		return this;
	}

	public Account getMirror() {
		return mirror;
	}

	public ScheduledTransaction setMirror(Account mirror) {
		this.mirror = mirror;
		return this;
	}	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((nextDate == null) ? 0 : nextDate.hashCode());
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
		ScheduledTransaction other = (ScheduledTransaction) obj;
		if (enabled != other.enabled)
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (nextDate == null) {
			if (other.nextDate != null)
				return false;
		} else if (!nextDate.equals(other.nextDate))
			return false;
		return true;
	}	

}
