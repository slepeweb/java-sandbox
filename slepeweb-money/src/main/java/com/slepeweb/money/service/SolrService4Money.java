package com.slepeweb.money.service;

import java.util.Date;
import java.util.List;

import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;

public interface SolrService4Money {
	boolean save(Transaction i);
	boolean save(List<Transaction> i);
	boolean removeAllTransactions();
	boolean removeTransactionsById(long transactionId);
	boolean removeChildTransactionsById(long transactionId);
	boolean removeTransactionsByAccount(String name);
	boolean removeTransactionsByPayee(String name);
	boolean removeTransactionsByCategory(String major, String minor);
	boolean removeTransactionsByDate(Date start, Date end);
	SolrResponse<FlatTransaction> query(SolrParams p);
	FlatTransaction getDocument(long transactionId);
	FlatTransaction queryLatestTransactionByPayee(String payee);
}
