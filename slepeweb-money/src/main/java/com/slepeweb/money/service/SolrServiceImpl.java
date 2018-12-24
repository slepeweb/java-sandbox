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

	@Autowired
	private PayeeService payeeService;
	@Autowired
	private CategoryService categoryService;

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
				this.client = new HttpSolrClient(this.serverUrl);
				LOG.info(String.format("Initialised solr server [%s]", this.serverUrl));
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
				LOG.error("Failed to retrieve Solr document", e);
			}
		}

		return null;
	}

	public boolean save(Transaction t) {
		if (isEnabled()) {
			try {
				getClient().addBean(makeDoc(t));

				if (t.isSplit()) {
					getClient().addBeans(makeDocsFromSplits(t));
				}

				this.client.commit();
				LOG.debug("Transaction(s) successfully indexed by Solr");
				return true;
			} catch (Exception e) {
				LOG.error("Solr failed to index transaction(s)", e);
			}
		}

		return false;
	}

	public boolean removeTransactionsById(long transactionId) {
		// Note that split transactions have an id that begins with the parent transaction
		return removeTransactions(String.format("id:%d-*", transactionId));
	}

	public boolean removeTransactionsByAccount(String name) {
		return removeTransactions(String.format("account:%s", name));
	}

	public boolean removeTransactionsByPayee(String name) {
		return removeTransactions(String.format("payee:%s", name));
	}

	public boolean removeTransactionsByCategory(String major, String minor) {
		if (StringUtils.isNotBlank(major)) {
			if (StringUtils.isNotBlank(minor)) {
				return removeTransactions(String.format("major:%s AND minor:%s", major, minor));
			} else {
				return removeTransactions(String.format("major:%s", major));
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
				LOG.error("Solr failed to remove document(s) from Solr index", e);
			}
		}

		return false;
	}

	private void appendField(StringBuilder sb, String field, String value) {
		if (sb.length() > 0) {
			sb.append(" AND ");
		}
		sb.append(String.format("%s:\"%s\"", field, value));
	}

	public SolrResponse<FlatTransaction> query(SolrParams params) {
		if (isEnabled()) {
			SolrResponse<FlatTransaction> response = new SolrResponse<FlatTransaction>(params);
			SolrQuery q;
			StringBuilder sb = new StringBuilder();

			if (params.getPayeeId() != null) {
				Payee p = this.payeeService.get(params.getPayeeId());
				if (p != null) {
					appendField(sb, "payee", p.getName());
				}
			}

			if (params.getCategoryId() != null) {
				Category c = this.categoryService.get(params.getCategoryId());
				if (c != null) {
					if (StringUtils.isNotBlank(c.getMinor())) {
						appendField(sb, "major", c.getMajor());
						appendField(sb, "minor", c.getMinor());
					} else {
						appendField(sb, "major", c.getMajor());
					}
				}
			}

			if (StringUtils.isNotBlank(params.getMemo())) {
				appendField(sb, "memo", params.getMemo());
			}

			if (sb.length() > 0) {
				q = new SolrQuery(sb.toString());
				//				q.add("defType", "dismax");
				//			q.add("qf", "title^10 subtitle^4 bodytext");
				q.addSort("entered", SolrQuery.ORDER.desc);
				q.setStart(params.getStart());
				q.setRows(params.getPageSize());
				//				LOG.info(String.format("Solr query: [%s]", q.getFilterQueries().toString()));				

				// Category searches can only apply to transactions which are NOT splits
				q.addFilterQuery("split:" + String.valueOf(params.getCategoryId() == null));

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
					LOG.error(response.getMessage(), e);
				}
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
				setSplit(t.isSplit());
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
					setSplit(false));
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
