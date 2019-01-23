package com.slepeweb.money.bean;

import org.apache.commons.lang3.StringUtils;

public class SplitTransaction extends DbEntity {
	
	private long transactionId;
	private Category category;
	private long amount;
	private String memo = "";
	
	@Override
	public void assimilate(Object obj) {
		if (obj instanceof SplitTransaction) {
			SplitTransaction st = (SplitTransaction) obj;
			setTransactionId(st.getTransactionId());
			setCategory(st.getCategory());
			setAmount(st.getAmount());
			setMemo(st.getMemo());
		}
	}
	
	@Override
	public boolean isDefined4Insert() {
		return  
			getTransactionId() > 0 &&
			getCategory() != null &&
			getCategory().getId() > 0L;
	}
	
	@Override
	public String toString() {
		return String.format("%s - %s", getCategory(), getAmountInPounds());
	}
	
	public SplitTransaction setId(long id) {
		super.setId(id);
		return this;
	}
	
	public boolean isDebit() {
		return getAmount() <= 0L;
	}
	
	public boolean isPopulated() {
		return getCategory() != null && StringUtils.isNotBlank(getCategory().getMajor());
	}
	
	public String getAmountInPounds() {
		return Transaction.DF.format(amount / 100.0);
	}
	
	public long getAmount() {
		return amount;
	}
	
	public long getAmountValue() {
		return isDebit() ? getAmount() * -1L : getAmount();
	}
	
	public SplitTransaction setAmount(long value) {
		this.amount = value;
		return this;
	}

	public Category getCategory() {
		return category;
	}

	public SplitTransaction setCategory(Category category) {
		this.category = category;
		return this;
	}

	public String getMemo() {
		return this.memo;
	}

	public SplitTransaction setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public SplitTransaction setTransactionId(long l) {
		this.transactionId = l;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + (int) (amount ^ (amount >>> 32));
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + (int) (transactionId ^ (transactionId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (equalsBarTransactionId(obj)) {
			SplitTransaction other = (SplitTransaction) obj;
			if (transactionId != other.transactionId)
				return false;
			
			return true;
		}
		
		return false;	
	}

	public boolean equalsBarTransactionId(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SplitTransaction other = (SplitTransaction) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (amount != other.amount)
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;

		return true;
	}

	@Override
	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		SplitTransaction other = (SplitTransaction) obj;
		return getId() == other.getId();
	}

}
