package com.slepeweb.money.bean;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.slepeweb.money.Util;

public class Transaction extends DbEntity implements Cloneable {
	
	public static SimpleDateFormat SDF = new SimpleDateFormat("dd/MM''yyyy");
	public static NumberFormat NF = NumberFormat.getInstance();
	public static DecimalFormat DF = new DecimalFormat("#.00");
	public static DecimalFormat DF_TOTAL = new DecimalFormat("###,###");
	
	private Account account;
	private long xferId = 0L;
	private Payee payee;
	private Category category;
	private Timestamp entered = new Timestamp(new Date().getTime());
	private boolean split, reconciled;
	private long amount;
	private String reference = "", memo = "";
	private List<SplitTransaction> splits = new ArrayList<SplitTransaction>();
	private Transaction previous;
	
	public void assimilate(Object obj) {
		Transaction source = (Transaction) obj;
		setAccount(source.getAccount());
		setPayee(source.getPayee());
		setCategory(source.getCategory());
		setEntered(source.getEntered());
		setXferId(source.getTransferId());
		setReconciled(source.isReconciled());
		setAmount(source.getAmount());
		setReference(source.getReference());
		setMemo(source.getMemo());
		setOrigId(source.getOrigId());
		setSplit(source.isSplit());
		
		assimilateSplits(source);
	}
	
	public void assimilateSplits(Transaction source) {
		getSplits().clear();
		for (SplitTransaction st : source.getSplits()) {
			getSplits().add(st.setTransactionId(getId()));
		}
	}
	
	@Override 
	public Object clone() throws CloneNotSupportedException {
		Transaction t = new Transaction();
		t.assimilate(this);
		t.setId(getId());
		return t;
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
		return String.format("(id: %d / %d) A.%s | P.%s: £%s (%6$td/%6$tm/%6$tY)", getId(), getOrigId(), 
				getAccount(), getPayee(), getAmountInPounds(), getEntered().getTime());
	}
	
	public List<FlatTransaction> flatten() {
		List<FlatTransaction> list = new ArrayList<FlatTransaction>();
		
		// Make a solr document representing the transaction
		FlatTransaction parent = flattenParent();
		list.add(parent);

		// Make solr documents for each split transaction
		if (isSplit()) {
			list.addAll(flattenSplits());
			parent.setType(1);
		}

		return list;
	}

	/*
	 * This solr document is made from a transaction that is NOT split
	 */
	private FlatTransaction flattenParent() {
		FlatTransaction doc = new FlatTransaction();

		return doc.
				setId(String.valueOf(getId())).
				setEntered(getEntered()).
				setAmount(getAmount()).
				setAccount(getAccount().getName()).
				setPayee(getPayee().getName()).
				setMajorCategory(getCategory().getMajor()).
				setMinorCategory(getCategory().getMinor()).
				setMemo(getMemo()).
				setType(0);
	}

	/*
	 * These (multiple) solr documents are made from SPLIT transactions
	 */
	private List<FlatTransaction> flattenSplits() {
		List<FlatTransaction> list = new ArrayList<FlatTransaction>();

		for (SplitTransaction st : getSplits()) {
			list.add(new FlatTransaction().
					setId(String.format("%d-%d", getId(), st.getId())).
					setEntered(getEntered()).
					setAmount(st.getAmount()).
					setAccount(getAccount().getName()).
					setPayee(getPayee().getName()).
					setMajorCategory(st.getCategory().getMajor()).
					setMinorCategory(st.getCategory().getMinor()).
					setMemo(st.getMemo()).
					setType(2));
		}

		return list;
	}

	public boolean isDebit() {
		return getAmount() <= 0L;
	}
	
	public boolean isTransfer() {
		return this instanceof Transfer || getTransferId() > 0L;
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
	
	public long getAmount() {
		return amount;
	}
	
	public long getAmountValue() {
		return isDebit() ? getAmount() * -1L : getAmount();
	}
	
	public String getAmountInPounds() {
		return Transaction.DF.format(amount / 100.0);
	}
	
	public Transaction setAmount(long value) {
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

//	public boolean isTransfer() {
//		return false;
//	}

	public long getTransferId() {
		return this.xferId;
	}

	public Transaction setXferId(long id) {
		this.xferId = id;
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

	@Override
	public Transaction setId(long id) {
		super.setId(id);
		return this;
	}

	@Override
	public Transaction setOrigId(long id) {
		super.setOrigId(id);
		return this;
	}

	public Transaction getPrevious() {
		return previous;
	}

	public Transaction setPrevious(Transaction previous) {
		this.previous = previous;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + (int) (amount ^ (amount >>> 32));
		result = prime * result + ((entered == null) ? 0 : entered.hashCode());
		result = prime * result + ((memo == null) ? 0 : memo.hashCode());
		result = prime * result + ((payee == null) ? 0 : payee.hashCode());
		result = prime * result + (reconciled ? 1231 : 1237);
		result = prime * result + (split ? 1231 : 1237);
		result = prime * result + ((reference == null) ? 0 : reference.hashCode());
		result = prime * result + (int) (xferId ^ (xferId >>> 32));
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
		if (getTransferId() != other.getId() || other.getTransferId() != getId()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (equalsBarTransferId(obj)) {
			Transaction other = (Transaction) obj;
			if (xferId != other.xferId)
				return false;
//			public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + ((account == null) ? 0 : account.hashCode());
//			result = prime * result + ((category == null) ? 0 : category.hashCode());
//			result = prime * result + ((amount == null) ? 0 : amount.hashCode());
//			result = prime * result + ((entered == null) ? 0 : entered.hashCode());
//			result = prime * result + ((memo == null) ? 0 : memo.hashCode());
//			result = prime * result + ((payee == null) ? 0 : payee.hashCode());
//			result = prime * result + (reconciled ? 1231 : 1237);
//			result = prime * result + (split ? 1231 : 1237);
//			result = prime * result + ((reference == null) ? 0 : reference.hashCode());
//			result = prime * result + ((xferId == null) ? 0 : xferId.hashCode());
//			return result;
//		}

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
		if (amount != other.amount)
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
		if (split != other.split)
			return false;

		return true;
	}	
}
