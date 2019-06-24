package com.slepeweb.money.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ScheduledTransaction extends DbEntity {
	
	private int day;
	private long accountId, mirrorId, payeeId, categoryId;
	private String account = "", mirror = "", payee = "", majorCategory = "", minorCategory = ""; 
	private Timestamp lastEntered;
	private boolean split;
	private long amount;
	private String label = "Scheduled transaction", reference = "", memo = "";
	private List<ScheduledSplitBak> splits = new ArrayList<ScheduledSplitBak>();
	
	public void assimilate(Object obj) {
		ScheduledTransaction source = (ScheduledTransaction) obj;
		setLabel(source.getLabel());
		setAccountId(source.getAccountId());
		setMirrorId(source.getMirrorId());
		setPayee(source.getPayee());
		setMajorCategory(source.getMajorCategory());
		setMinorCategory(source.getMinorCategory());
		setLastEntered(source.getLastEntered());
		setAmount(source.getAmount());
		setReference(source.getReference());
		setMemo(source.getMemo());
		setSplit(source.isSplit());
		
		assimilateSplits(source);
	}
	
	public void assimilateSplits(ScheduledTransaction source) {
		getSplits().clear();
		for (ScheduledSplitBak st : source.getSplits()) {
			getSplits().add(st.setScheduledTransactionId(getId()));
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (accountId ^ (accountId >>> 32));
		result = prime * result + (int) (amount ^ (amount >>> 32));
		result = prime * result + (int) (categoryId ^ (categoryId >>> 32));
		result = prime * result + day;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + (int) (mirrorId ^ (mirrorId >>> 32));
		result = prime * result + (int) (payeeId ^ (payeeId >>> 32));
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		result = prime * result + ((splits == null) ? 0 : splits.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScheduledTransaction other = (ScheduledTransaction) obj;
		if (accountId != other.accountId)
			return false;
		if (amount != other.amount)
			return false;
		if (categoryId != other.categoryId)
			return false;
		if (day != other.day)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (mirrorId != other.mirrorId)
			return false;
		if (payeeId != other.payeeId)
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		if (splits == null) {
			if (other.splits != null)
				return false;
		} else if (!splits.equals(other.splits))
			return false;
		return true;
	}

	@Override
	public boolean isDefined4Insert() {
		return  
			StringUtils.isNotBlank(getLabel()) &&
			getDay() > 0 &&
			getAccountId() > 0L &&
			StringUtils.isNotBlank(getPayee()) &&
			StringUtils.isNotBlank(getMajorCategory()) &&
			StringUtils.isNotBlank(getMinorCategory());
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
	
	public boolean isTransfer() {
		return getMirrorId() > 0L;
	}
	
	public ScheduledTransaction setId(long id) {
		super.setId(id);
		return this;
	}
	
	public int getDay() {
		return day;
	}

	public ScheduledTransaction setDay(int day) {
		this.day = day;
		return this;
	}

	public long getAccountId() {
		return accountId;
	}

	public ScheduledTransaction setAccountId(long accountId) {
		this.accountId = accountId;
		return this;
	}

	public long getMirrorId() {
		return mirrorId;
	}

	public ScheduledTransaction setMirrorId(long mirrorId) {
		this.mirrorId = mirrorId;
		return this;
	}

	public Timestamp getLastEntered() {
		return lastEntered;
	}

	public ScheduledTransaction setLastEntered(Timestamp lastEntered) {
		this.lastEntered = lastEntered;
		return this;
	}

	public boolean isSplit() {
		return split;
	}

	public ScheduledTransaction setSplit(boolean split) {
		this.split = split;
		return this;
	}

	public long getAmount() {
		return amount;
	}

	public ScheduledTransaction setAmount(long amount) {
		this.amount = amount;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public ScheduledTransaction setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public String getMemo() {
		return memo;
	}

	public ScheduledTransaction setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public List<ScheduledSplitBak> getSplits() {
		return splits;
	}

	public ScheduledTransaction setSplits(List<ScheduledSplitBak> splits) {
		this.splits = splits;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public ScheduledTransaction setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getPayee() {
		return payee;
	}

	public ScheduledTransaction setPayee(String payee) {
		this.payee = payee;
		return this;
	}

	public String getMajorCategory() {
		return majorCategory;
	}

	public ScheduledTransaction setMajorCategory(String majorCategory) {
		this.majorCategory = majorCategory;
		return this;
	}

	public String getMinorCategory() {
		return minorCategory;
	}

	public ScheduledTransaction setMinorCategory(String minorCategory) {
		this.minorCategory = minorCategory;
		return this;
	}

	public long getPayeeId() {
		return payeeId;
	}

	public ScheduledTransaction setPayeeId(long payeeId) {
		this.payeeId = payeeId;
		return this;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public ScheduledTransaction setCategoryId(long categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public String getAccount() {
		return account;
	}

	public ScheduledTransaction setAccount(String account) {
		this.account = account;
		return this;
	}

	public String getMirror() {
		return mirror;
	}

	public ScheduledTransaction setMirror(String mirror) {
		this.mirror = mirror;
		return this;
	}	

}
