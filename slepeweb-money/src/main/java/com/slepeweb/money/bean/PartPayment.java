package com.slepeweb.money.bean;

public class PartPayment {
	
	private Long paymentId;
	private Category category;
	private Long charge;
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
			getPaymentId() != null &&
			getCategory() != null &&
			getCategory().getId() > 0L;
	}
	
	@Override
	public String toString() {
		return String.format("%s - %s", getCategory(), getValueInPounds());
	}
	
	public String getValueInPounds() {
		return Payment.DF.format(charge / 100.0);
	}
	
	public Long getCharge() {
		return charge;
	}
	
	public PartPayment setCharge(Long value) {
		this.charge = value;
		return this;
	}

	public Category getCategory() {
		return category;
	}

	public PartPayment setCategory(Category category) {
		this.category = category;
		return this;
	}

	public String getMemo() {
		return this.memo;
	}

	public PartPayment setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public PartPayment setPaymentId(Long l) {
		this.paymentId = l;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((charge == null) ? 0 : charge.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + paymentId.hashCode();
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
		PartPayment other = (PartPayment) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (charge == null) {
			if (other.charge != null)
				return false;
		} else if (!charge.equals(other.charge))
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (paymentId == null) {
			if (other.paymentId != null)
				return false;
		} else if (!paymentId.equals(other.paymentId))
			return false;
		return true;
	}

}
