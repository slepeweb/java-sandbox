package com.slepeweb.money.bean;

public class SplitTransaction {
	
	private Long transactionId;
	private Category category;
	private Long amount;
	private String memo = "";
	
	/* NOT REQUIRED ?
	public void assimilate(Object obj) {
		if (obj instanceof PartPayment) {
			PartPayment pt = (PartPayment) obj;
			setCategory(pt.getCategory());
			setCharge(pt.getCharge());
			setMemo(pt.getMemo());
		}
	}
	*/
	
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
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		return true;
	}

}
