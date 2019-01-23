package com.slepeweb.money.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.solr.SolrPager;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;

@Service
public class SolrServiceImpl implements SolrService {

	private static Logger LOG = Logger.getLogger(SolrServiceImpl.class);
	private static final String SPACE = " ";

	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;

	//@Value("${solr.enabled:no}") 
	private String solrIsEnabled = "yes";
	//@Value("${solr.server.url:http://localhost:8983/solr/money}") 
	private String serverUrl = "http://localhost:8983/solr/money";

	private SolrClient client;

	public boolean isEnabled() {
		return Util.isPositive(this.solrIsEnabled);
	}

	private SolrClient getClient() {
		if (isEnabled()) {
			if (this.client == null) {
				try {
					this.client = new HttpSolrClient(this.serverUrl);
					this.client.ping();
					LOG.info(String.format("Initialised solr server [%s]", this.serverUrl));
				}
				catch (Exception e) {
					LOG.error("Failed to initialise Solr");
					this.solrIsEnabled = "no";
				}
			}
		}
		return this.client;
	}

	public FlatTransaction getDocument(long transactionId) {
		if (isEnabled()) {
			try {
				org.apache.solr.common.SolrDocument doc = getClient().getById(String.valueOf(transactionId));
				DocumentObjectBinder binder = new DocumentObjectBinder();
				return binder.getBean(FlatTransaction.class, doc);
			} catch (Exception e) {
				LOG.error(String.format("Failed to retrieve Solr document: %s", e.getMessage()));
			}
		}

		return null;
	}

	public boolean save(Transaction t) {
		if (isEnabled()) {
			try {
				// Remove all documents matching this transaction's id. This is possibly
				// the simplest way of dealing with changes in splits
				removeTransactionsById(t.getId());
				
				// Make a solr document representing the transaction
				FlatTransaction parent = makeDoc(t);

				// Make solr documents for each split transaction
				if (t.isSplit()) {
					getClient().addBeans(makeDocsFromSplits(t));
					parent.setType(1);
				}
				else if (t.isTransfer()) {
					getClient().addBean(makeDoc(this.transactionService.get(t.getTransferId())));
				}

				getClient().addBean(parent);
				this.client.commit();
				LOG.debug("Transaction(s) successfully indexed by Solr");
				return true;
			} catch (Exception e) {
				LOG.error(String.format("Solr failed to index transaction(s): %s", e.getMessage()));
			}
		}

		return false;
	}

	public boolean removeTransactionsById(long transactionId) {
		// Note that split transactions have an id that begins with the parent transaction
		return removeTransactions(String.format("id:%d*", transactionId));
	}

	public boolean removeTransactionsByAccount(String name) {
		return removeTransactions(String.format("account:\"%s\"", name));
	}

	public boolean removeTransactionsByPayee(String name) {
		return removeTransactions(String.format("payee:\"%s\"", name));
	}

	public boolean removeTransactionsByCategory(String major, String minor) {
		if (StringUtils.isNotBlank(major)) {
			if (StringUtils.isNotBlank(minor)) {
				return removeTransactions(String.format("major:\"%s\" AND minor:\"%s\"", major, minor));
			} else {
				return removeTransactions(String.format("major:\"%s\"", major));
			}
		}

		return false;
	}

	private boolean removeTransactions(String query) {
		if (isEnabled()) {
			try {
				// Note that split transactions have an id that begins with the parent transaction
				getClient().deleteByQuery(query);
				this.client.commit();
				LOG.debug("Document(s) successfully removed from Solr index");
				return true;
			} catch (Exception e) {
				LOG.error(String.format("Solr failed to remove document(s) from Solr index: %s", e.getMessage()));
			}
		}

		return false;
	}

	@SuppressWarnings("unused")
	private void appendField(StringBuilder sb, String field, String value) {
		if (sb.length() > 0) {
			sb.append(" AND ");
		}
		sb.append(String.format("%s:\"%s\"", field, value));
	}

	public SolrResponse<FlatTransaction> query(SolrParams params) {
		if (isEnabled()) {
			SolrResponse<FlatTransaction> response = new SolrResponse<FlatTransaction>(params);
			SolrQuery q = new SolrQuery();
			boolean isCriteriaSet = false, isCategorySearch = false;

			if (params.getAccountId() != null) {
				Account a = this.accountService.get(params.getAccountId());
				if (a != null) {
					q.addFilterQuery(String.format("account:\"%s\"", a.getName()));
					isCriteriaSet = true;
				}
			}

			if (params.getPayeeId() != null) {
				Payee p = this.payeeService.get(params.getPayeeId());
				if (p != null) {
					q.addFilterQuery(String.format("payee:\"%s\"", p.getName()));
					isCriteriaSet = true;
				}
			}

			if (params.getCategoryId() != null) {
				isCategorySearch = true;
				Category c = this.categoryService.get(params.getCategoryId());
				if (c != null) {
					if (StringUtils.isNotBlank(c.getMinor())) {
						q.addFilterQuery(String.format("major:\"%s\"", c.getMajor()));
						q.addFilterQuery(String.format("minor:\"%s\"", c.getMinor()));
					} else {
						q.addFilterQuery(String.format("major:\"%s\"", c.getMajor()));
					}
					isCriteriaSet = true;
				}
			}
			else if (StringUtils.isNotBlank(params.getMajorCategory())) {
				isCategorySearch = true;
				q.addFilterQuery(String.format("major:\"%s\"", params.getMajorCategory()));
				isCriteriaSet = true;
			}

			if (StringUtils.isNotBlank(params.getMemo())) {
				q.setQuery(String.format("memo:%s", params.getMemo()));
				isCriteriaSet = true;
			}
			else {
				q.setQuery("*:*");
			}
			
			if (isCriteriaSet) {
				if (isCategorySearch) {
					q.addFilterQuery(String.format("type:%d OR type:%d", 0, 2));
				}
				else {
					q.addFilterQuery(String.format("type:%d OR type:%d", 0, 1));
				}
	
				//				q.add("defType", "dismax");
				//			q.add("qf", "title^10 subtitle^4 bodytext");
				q.addSort("entered", SolrQuery.ORDER.desc);
				q.setStart(params.getStart());
				q.setRows(params.getPageSize());
				LOG.info(String.format("Solr query: [%s]", q.getQuery()));				

				try {
					QueryResponse qr = getClient().query(q);
					response.setResults(qr.getBeans(FlatTransaction.class));
					response.setTotalHits(qr.getResults().getNumFound());
					LOG.info(String.format("Query returned %d results [%s]", response.getResults().size(),
							qr.getHeader().toString()));

					response.setPager(new SolrPager<FlatTransaction>(response.getTotalHits(), response.getResults(),
							params.getConfig().getPageSize(), params.getPageNum()));

					return response;

				} catch (Exception e) {
					response.setError(true);
					response.setMessage("Search system error");
					LOG.error(String.format(response.getMessage(), e.getMessage()));
				}
			}
			else {
				response.setError(true);
				response.setMessage("Please provide one or more search criteria. Refine your results by applying additional criteria.");
			}

			response.setTotalHits(0);
			response.setResults(new ArrayList<FlatTransaction>(0));
			return response;
		}

		return null;
	}

	/*
	 * This solr document is made from a transaction that is NOT split
	 */
	private FlatTransaction makeDoc(Transaction t) {
		FlatTransaction doc = new FlatTransaction();

		return doc.
				setId(String.valueOf(t.getId())).
				setEntered(t.getEntered()).
				setAmount(t.getAmount()).
				setAccount(t.getAccount().getName()).
				setPayee(t.getPayee().getName()).
				setMajorCategory(t.getCategory().getMajor()).
				setMinorCategory(t.getCategory().getMinor()).
				setMemo(t.getMemo()).
				setType(0);
	}

	/*
	 * These (multiple) solr documents are made from SPLIT transactions
	 */
	private List<FlatTransaction> makeDocsFromSplits(Transaction t) {
		List<FlatTransaction> list = new ArrayList<FlatTransaction>();

		for (SplitTransaction st : t.getSplits()) {
			list.add(new FlatTransaction().
					setId(String.format("%d-%d", t.getId(), st.getId())).
					setEntered(t.getEntered()).
					setAmount(st.getAmount()).
					setAccount(t.getAccount().getName()).
					setPayee(t.getPayee().getName()).
					setMajorCategory(st.getCategory().getMajor()).
					setMinorCategory(st.getCategory().getMinor()).
					setMemo(st.getMemo()).
					setType(2));
		}

		return list;
	}

	@SuppressWarnings("unused")
	private void append(StringBuilder sb, String s) {
		if (sb.length() > 0) {
			sb.append(SPACE);
		}
		sb.append(s);
	}
}
