package com.slepeweb.money.bean;

import org.apache.commons.lang3.StringUtils;

public class ScheduledSplitBak extends DbEntity {
	
	private long scheduledTransactionId, categoryId;
	private String majorCategory, minorCategory;
	private long amount;
	private String memo = "";
	
	@Override
	public void assimilate(Object obj) {
		if (obj instanceof ScheduledSplitBak) {
			ScheduledSplitBak st = (ScheduledSplitBak) obj;
			setScheduledTransactionId(st.getScheduledTransactionId());
			setCategoryId(st.getCategoryId());
			setAmount(st.getAmount());
			setMemo(st.getMemo());
		}
	}
	
	@Override
	public boolean isDefined4Insert() {
		return  
			getScheduledTransactionId() > 0 &&
			getCategoryId() > 0;
	}
		
	public boolean isDebit() {
		return getAmount() <= 0L;
	}
	
	public boolean isPopulated() {
		return StringUtils.isNotBlank(getMajorCategory());
	}
	
	public long getAmountValue() {
		return isDebit() ? getAmount() * -1L : getAmount();
	}
	
	public ScheduledSplitBak setId(long id) {
		super.setId(id);
		return this;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public ScheduledSplitBak setAmount(long value) {
		this.amount = value;
		return this;
	}

	public long getCategoryId() {
		return this.categoryId;
	}

	public ScheduledSplitBak setCategoryId(long id) {
		this.categoryId = id;
		return this;
	}

	public String getMemo() {
		return this.memo;
	}

	public ScheduledSplitBak setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public long getScheduledTransactionId() {
		return this.scheduledTransactionId;
	}

	public ScheduledSplitBak setScheduledTransactionId(long l) {
		this.scheduledTransactionId = l;
		return this;
	}

	public String getMajorCategory() {
		return majorCategory;
	}

	public ScheduledSplitBak setMajorCategory(String majorCategory) {
		this.majorCategory = majorCategory;
		return this;
	}

	public String getMinorCategory() {
		return minorCategory;
	}

	public ScheduledSplitBak setMinorCategory(String minorCategory) {
		this.minorCategory = minorCategory;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (amount ^ (amount >>> 32));
		result = prime * result + (int) (categoryId ^ (categoryId >>> 32));
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + (int) (scheduledTransactionId ^ (scheduledTransactionId >>> 32));
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
		ScheduledSplitBak other = (ScheduledSplitBak) obj;
		if (amount != other.amount)
			return false;
		if (categoryId != other.categoryId)
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (scheduledTransactionId != other.scheduledTransactionId)
			return false;
		return true;
	}

}
