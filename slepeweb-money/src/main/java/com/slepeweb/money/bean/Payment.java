package com.slepeweb.money.bean;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Payment {
	
	public static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM''yyyy");
	public static NumberFormat NF = NumberFormat.getInstance();
	public static DecimalFormat DF = new DecimalFormat("#.00");
	public static DecimalFormat DF_TOTAL = new DecimalFormat("###,###");
	
	private long id;
	private Account account, transfer;
	private Payee payee;
	private Category category;
	private Timestamp entered;
	private boolean reconciled, split;
	private Long charge;
	private String reference = "", memo = "";
	private List<PartPayment> partPayments = new ArrayList<PartPayment>();
	
	public void assimilate(Object obj) {
		if (obj instanceof Payment) {
			Payment pt = (Payment) obj;
			setAccount(pt.getAccount());
			setPayee(pt.getPayee());
			setCategory(pt.getCategory());
			setEntered(pt.getEntered());
			setTransfer(pt.getTransfer());
			setReconciled(pt.isReconciled());
			setCharge(pt.getCharge());
			setReference(pt.getReference());
			setMemo(pt.getMemo());
		}
	}
	
	public boolean isDefined4Insert() {
		return  
			getAccount() != null &&
			getAccount().getId() > 0L &&
			getPayee() != null &&
			getPayee().getId() > 0L &&
			getCategory() != null &&
			getCategory().getId() > 0L &&
			getEntered() != null;
	}
	
	@Override
	public String toString() {
		return String.format("%s/%s: %s (%4$td/%4$tm/%4$tY)", this.account.getName(), getPayee(), 
				getValueInPounds(), getEntered().getTime());
	}
	
	public Account getAccount() {
		return this.account;
	}
	
	public Payment setAccount(Account f) {
		this.account = f;
		return this;
	}
	
	public Timestamp getEntered() {
		return entered;
	}
	
	public Payment setEntered(Timestamp entered) {
		this.entered = entered;
		return this;
	}
	
	public Long getCharge() {
		return charge;
	}
	
	public String getValueInPounds() {
		return Payment.DF.format(charge / 100.0);
	}
	
	public Payment setCharge(Long value) {
		this.charge = value;
		return this;
	}

	public Payee getPayee() {
		return payee;
	}

	public Payment setPayee(Payee payee) {
		this.payee = payee;
		return this;
	}

	public boolean isTransfer() {
		return transfer != null;
	}

	public Account getTransfer() {
		return transfer;
	}

	public long getTransferId() {
		return getTransfer() == null ? -1 : getTransfer().getId();
	}

	public Payment setTransfer(Account transfer) {
		this.transfer = transfer;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public Payment setReference(String ref) {
		this.reference = ref;
		return this;
	}


	public Category getCategory() {
		return category;
	}

	public Payment setCategory(Category category) {
		this.category = category;
		return this;
	}

	public boolean isReconciled() {
		return reconciled;
	}

	public Payment setReconciled(boolean reconciled) {
		this.reconciled = reconciled;
		return this;
	}
	

	public String getMemo() {
		return this.memo;
	}

	public Payment setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public boolean isSplit() {
		return split;
	}

	public void setSplit(boolean split) {
		this.split = split;
	}

	public List<PartPayment> getPartPayments() {
		return partPayments;
	}

	public void setPartPayments(List<PartPayment> partPayments) {
		this.partPayments = partPayments;
	}

	public long getId() {
		return id;
	}

	public Payment setId(long id) {
		this.id = id;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((charge == null) ? 0 : charge.hashCode());
		result = prime * result + ((entered == null) ? 0 : entered.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + ((payee == null) ? 0 : payee.hashCode());
		result = prime * result + (reconciled ? 1231 : 1237);
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		result = prime * result + ((transfer == null) ? 0 : transfer.hashCode());
		result = prime * result + (split ? 1231 : 1237);
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
		Payment other = (Payment) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
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
		if (entered == null) {
			if (other.entered != null)
				return false;
		} else if (!entered.equals(other.entered))
			return false;
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		if (payee == null) {
			if (other.payee != null)
				return false;
		} else if (!payee.equals(other.payee))
			return false;
		if (reconciled != other.reconciled)
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		if (transfer == null) {
			if (other.transfer != null)
				return false;
		} else if (!transfer.equals(other.transfer))
			return false;
		return true;
	}
}
