package com.slepeweb.money.service;

import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;

public interface SolrService {
	boolean save(Transaction i);
	boolean removeTransactionsById(long transactionId);
	boolean removeTransactionsByAccount(String name);
	boolean removeTransactionsByPayee(String name);
	boolean removeTransactionsByCategory(String major, String minor);
	SolrResponse<FlatTransaction> query(SolrParams p);
	FlatTransaction getDocument(long transactionId);
}
