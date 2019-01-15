package com.slepeweb.money.bean;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.slepeweb.money.Util;

public class Transaction extends DbEntity {
	
	public static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM''yyyy");
	public static NumberFormat NF = NumberFormat.getInstance();
	public static DecimalFormat DF = new DecimalFormat("#.00");
	public static DecimalFormat DF_TOTAL = new DecimalFormat("###,###");
	
	private long id, origId;
	private Account account;
	private Long xferId = 0L;
	private Payee payee;
	private Category category;
	private Timestamp entered = new Timestamp(new Date().getTime());
	private boolean split, reconciled;
	private Long amount;
	private String reference = "", memo = "";
	private List<SplitTransaction> splits = new ArrayList<SplitTransaction>();
	
	public void assimilate(Object obj) {
		if (obj instanceof Transaction) {
			Transaction t = (Transaction) obj;
			assimilate(t, this);
		}
	}
	
	protected void assimilate(Transaction source, Transaction target) {
		target.setAccount(source.getAccount());
		target.setPayee(source.getPayee());
		target.setCategory(source.getCategory());
		target.setEntered(source.getEntered());
		target.setXferId(source.getTransferId());
		target.setReconciled(source.isReconciled());
		target.setAmount(source.getAmount());
		target.setReference(source.getReference());
		target.setMemo(source.getMemo());
		target.setOrigId(source.getOrigId());
		target.setSplit(source.isSplit());
		target.setSplits(source.getSplits());
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
		return String.format("(%d / %d) %s | %s: %s (%6$td/%6$tm/%6$tY)", getId(), getOrigId(), 
				this.account.getName(), getPayee(), getAmountInPounds(), getEntered().getTime());
	}
	
	public boolean isDebit() {
		return getAmount() == null || getAmount() < 0L;
	}
	
	public long getOrigId() {
		return origId;
	}

	public Transaction setOrigId(long origId) {
		this.origId = origId;
		return this;
	}

	public Account getAccount() {
		return this.account;
	}
	
	public Transaction setAccount(Account f) {
		this.account = f;
		return this;
	}
	
	public Timestamp getEntered() {
		return entered;
	}
	
	public String getEnteredStr() {
		return Util.formatTimestamp(getEntered());
	}
	
	public Transaction setEntered(Timestamp entered) {
		this.entered = entered;
		return this;
	}
	
	public Long getAmount() {
		return amount;
	}
	
	public String getAmountInPounds() {
		return Transaction.DF.format(amount / 100.0);
	}
	
	public Transaction setAmount(Long value) {
		this.amount = value;
		return this;
	}

	public Payee getPayee() {
		return payee;
	}

	public Transaction setPayee(Payee payee) {
		this.payee = payee;
		return this;
	}

	public boolean isTransfer() {
		return this.xferId != null && this.xferId != 0;
	}

	public Long getTransferId() {
		return this.xferId;
	}

	public Transaction setXferId(Long transfer) {
		this.xferId = transfer;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public Transaction setReference(String ref) {
		this.reference = ref;
		return this;
	}


	public Category getCategory() {
		return category;
	}

	public Transaction setCategory(Category category) {
		this.category = category;
		return this;
	}

	public boolean isReconciled() {
		return reconciled;
	}

	public Transaction setReconciled(boolean reconciled) {
		this.reconciled = reconciled;
		return this;
	}
	

	public String getMemo() {
		return this.memo;
	}

	public Transaction setMemo(String memo) {
		this.memo = memo;
		return this;
	}

	public boolean isSplit() {
		return this.split;
	}

	public Transaction setSplit(boolean split) {
		this.split = split;
		return this;
	}
	
	public List<SplitTransaction> getSplits() {
		return splits;
	}

	public void setSplits(List<SplitTransaction> partPayments) {
		this.splits = partPayments;
	}

	public long getId() {
		return id;
	}

	public Transaction setId(long id) {
		this.id = id;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((entered == null) ? 0 : entered.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + ((payee == null) ? 0 : payee.hashCode());
		result = prime * result + (reconciled ? 1231 : 1237);
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		result = prime * result + ((xferId == null) ? 0 : xferId.hashCode());
		return result;
	}

	public boolean matchesSplits(Transaction other) {
		if (! (isSplit() && other.isSplit())) {
			return false;
		}
		
		// Both transactions are split. Are they the same size?
		if (getSplits().size() != other.getSplits().size()) {
			return false;
		}
		
		// Is each split identical?
		for (int i = 0; i < getSplits().size(); i++) {
			if (! getSplits().get(i).equalsBarTransactionId(other.getSplits().get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean matchesTransfer(Transaction other) {
		if (! (isTransfer() && other.isTransfer())) {
			return false;
		}
		
		if (getTransferId() != other.getId() || other.getTransferId() != getId()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (equalsBarTransferId(obj)) {
			Transaction other = (Transaction) obj;
			if (xferId == null) {
				if (other.xferId != null)
					return false;
			} else if (!xferId.equals(other.xferId))
				return false;
			
			return true;
		}

		return false;
	}
	
	public boolean equalsBarTransferId(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
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
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
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

		return true;
	}	
}
