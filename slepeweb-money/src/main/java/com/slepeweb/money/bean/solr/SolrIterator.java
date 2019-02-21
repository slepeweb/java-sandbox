package com.slepeweb.money.bean.solr;

import java.util.ArrayList;
import java.util.Iterator;

import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Transaction;

public class SolrIterator implements Iterator<FlatTransaction> {
	private Iterator<Transaction> transactions;
	private Iterator<FlatTransaction> flatties = new ArrayList<FlatTransaction>().iterator();

	public SolrIterator(Iterator<Transaction> iter) {
		this.transactions = iter;
	}
	
	@Override
	public boolean hasNext() {
		return this.flatties.hasNext() || this.transactions.hasNext();
	}

	@Override
	public FlatTransaction next() {
		if (! this.flatties.hasNext()) {
			this.flatties = this.transactions.next().toDocumentList().iterator();
		}
		return this.flatties.next();
	}

	@Override
	public void remove() {
	}

}
