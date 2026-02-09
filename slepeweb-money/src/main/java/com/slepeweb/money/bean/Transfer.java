package com.slepeweb.money.bean;

import com.slepeweb.money.service.TransactionService;

public class Transfer extends Transaction {

	private Account mirrorAccount;
	private TransactionService transactionService;
	
	public Transfer() {}
	
	public Transfer(Transaction t) {
		assimilate(t);
		setId(t.getId());
		setOrigId(t.getOrigId());
	}
	
	@Override
	public void assimilate(Object obj) {
		if (obj instanceof Transaction) {
			Transaction t = (Transaction) obj;
			super.assimilate(t);
		}
		
		if (obj instanceof Transfer) {
			Transfer t = (Transfer) obj;
			setMirrorAccount(t.getMirrorAccount());
		}
	}
		
	protected FlatTransaction flattenParent() {
		FlatTransaction doc = super.flattenParent();

		return doc.
				setMajorCategory("Transfer").
				setMinorCategory("").
				setMemo(String.format("%s '%s'", isDebit() ? "To" : "From", getMirrorAccount().getName()));
	}

	public Transfer setTransactionService(TransactionService transactionService) {
		this.transactionService = transactionService;
		return this;
	}

	public Account getMirrorAccount() {
		if (this.mirrorAccount == null && this.transactionService != null) {
			Transaction t = this.transactionService.get(getTransferId());
			if (t != null) {
				this.mirrorAccount = t.getAccount();
			}
		}
		return this.mirrorAccount == null ? new Account().setName("Unknown") : this.mirrorAccount;
	}

	public Transfer setMirrorAccount(Account mirrorAccount) {
		this.mirrorAccount = mirrorAccount;
		return this;
	}
}
