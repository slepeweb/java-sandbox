package com.slepeweb.money.bean;

import org.apache.commons.lang3.StringUtils;

public class SplitTransaction extends DbEntity {
	
	private Long transactionId;
	private Category category;
	private Long amount;
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
			getTransactionId() != null &&
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
	
	public boolean isPopulated() {
		return getCategory() != null && StringUtils.isNotBlank(getCategory().getMajor()) && getAmount() != null;
	}
	
	public String getAmountInPounds() {
		return Transaction.DF.format(amount / 100.0);
	}
	
	public Long getAmount() {
		return amount;
	}
	
	public SplitTransaction setAmount(Long value) {
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

	public Long getTransactionId() {
		return transactionId;
	}

	public SplitTransaction setTransactionId(Long l) {
		this.transactionId = l;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + transactionId.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (equalsBarTransactionId(obj)) {
			SplitTransaction other = (SplitTransaction) obj;
			if (transactionId == null) {
				if (other.transactionId != null)
					return false;
			} else if (!transactionId.equals(other.transactionId))
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
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
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
