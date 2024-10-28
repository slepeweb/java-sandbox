package com.slepeweb.money.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.common.solr.service.SolrServiceBase;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.solr.SolrIterator;
import com.slepeweb.money.bean.solr.SolrPager;
import com.slepeweb.money.bean.solr.SolrParams;
import com.slepeweb.money.bean.solr.SolrResponse;

import jakarta.annotation.PostConstruct;

@Service("solrService4Money")
public class SolrService4MoneyImpl extends SolrServiceBase implements SolrService4Money {

	private static Logger LOG = Logger.getLogger(SolrService4MoneyImpl.class);

	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;

	@PostConstruct
	public void init() throws Exception {
		setServerUrl("http://localhost:8983/solr/money");
	}
	
	public FlatTransaction getDocument(long transactionId) {
		try {
			org.apache.solr.common.SolrDocument doc = getClient().getById(String.valueOf(transactionId));
			DocumentObjectBinder binder = new DocumentObjectBinder();
			return binder.getBean(FlatTransaction.class, doc);
		} catch (Exception e) {
			LOG.error(String.format("Failed to retrieve document: %s", e.getMessage()));
		}

		return null;
	}

	public boolean save(Transaction t) {
		try {
			removeChildTransactionsById(t.getId());
			getClient().addBeans(t.flatten());
			return commit(getClient());
		}
		catch (Exception e) {
			LOG.error(String.format("Solr failed to index transaction(s): %s [%d]", e.getMessage(), t.getId()));
		}
		
		return false;
	}
	
	public boolean save(List<Transaction> list) {
		SolrIterator iter = new SolrIterator(list.iterator());
		
		try {
			getBatchingClient().addBeans(iter);
			return commit(getBatchingClient());
		}
		catch (Exception e) {
			LOG.error(String.format("Solr failed to index transaction batch: %s", e.getMessage()));
		}
		
		return false;
	}
	
	public boolean removeChildTransactionsById(long transactionId) {
		// Note that split transactions have an id that begins with the parent transaction
		return removeTransactions(String.format("id:%d-*", transactionId));
	}

	public boolean removeTransactionsById(long transactionId) {
		return removeTransactions(String.format("id:%d", transactionId)) &&
				removeChildTransactionsById(transactionId);
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

	public boolean removeAllTransactions() {
		return removeTransactions("*:*");
	}

	public boolean removeTransactionsByDate(Date start, Date end) {
		return removeTransactions(String.format("entered:[%s TO %s]", 
				Util.formatSolrDate(start), Util.formatSolrDate(end)));
	}

	private boolean removeTransactions(String query) {
		try {
			// Note that split transactions have an id that begins with the parent transaction
			getClient().deleteByQuery(query);
			LOG.debug(String.format("Document(s) removed from Solr [%s]", query));
			return true;
		} catch (Exception e) {
			LOG.error(String.format("Solr failed to remove document(s) from Solr: %s [%s]", e.getMessage(), query));
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
	
	private Payee filterByPayee(Payee p, SolrQuery q, SolrParams params) {
		if (p != null) {
			q.addFilterQuery(String.format("payee:\"%s\"", p.getName()));
			params.setPayeeName(p.getName());
		}
		return p;
	}

	public SolrResponse<FlatTransaction> query(SolrParams params) {
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

		Payee p;
		if (params.getPayeeId() != null) {
			if (filterByPayee(p = this.payeeService.get(params.getPayeeId()), q, params) != null) {
				params.setPayeeName(p.getName());
				isCriteriaSet = true;
			}
		}
		else if (StringUtils.isNotBlank(params.getPayeeName()) && 
				filterByPayee(p = this.payeeService.get(params.getPayeeName()), q, params) != null) {
			params.setPayeeId(p.getId());
			isCriteriaSet = true;
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
			
			if (StringUtils.isNotBlank(params.getMinorCategory())) {
				q.addFilterQuery(String.format("minor:\"%s\"", params.getMinorCategory()));
			}
		}
		else if (params.getCategories() != null && params.getCategories().size() > 0) {
			// This category-based search is specific to charting functionality.
			// First, split the category list into included/excluded categories.
			isCategorySearch = true;
			List<Category> 
				includeList = new ArrayList<Category>(), 
				excludeList = new ArrayList<Category>();
			
			for (Category c : params.getCategories()) {
				if (! c.isExclude()) {
					includeList.add(c);
				}
				else {
					excludeList.add(c);
				}
			}
			
			StringBuilder includeStr = setCategoryFilter(includeList);
			StringBuilder excludeStr = setCategoryFilter(excludeList);
			
			if (excludeStr.length() > 0) {
				q.addFilterQuery(includeStr.append(" AND -(").append(excludeStr).append(")").toString());
			}
			else {
				q.addFilterQuery(includeStr.toString());
			}
			
			isCriteriaSet = true;
		}

		if (StringUtils.isNotBlank(params.getMemo())) {
			q.setQuery(params.getMemo());
			q.add("defType", "dismax");
			q.add("qf", "memo");
			isCriteriaSet = true;
		}
		else {
			q.setQuery("*:*");
		}
		
		if (params.getFrom() != null || params.getTo() != null) {
			String from = params.getFrom() == null ? "*" : Util.formatSolrDate(params.getFrom());
			String to = params.getTo() == null ? "*" : Util.formatSolrDate(params.getTo());
			q.addFilterQuery(String.format("entered:[%s TO %s]", from, to));
			isCriteriaSet = true;
		}
		
		if (params.getFromAmount() != null || params.getToAmount() != null) {
			String smaller = params.getFromAmount() == null ? "*" : String.valueOf(params.getFromAmount());
			String larger = params.getToAmount() == null ? "*" : String.valueOf(params.getToAmount());
			q.addFilterQuery(String.format("amount:[%s TO %s]", 
					params.isDebit() ? larger : smaller, 
					params.isDebit() ? smaller : larger));
			isCriteriaSet = true;
		}
		
		if (isCriteriaSet) {
			if (isCategorySearch) {
				q.addFilterQuery(String.format("type:%d OR type:%d", 0, 2));
			}
			else {
				q.addFilterQuery(String.format("type:%d OR type:%d", 0, 1));
			}

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
						params.getPageSize(), params.getPageNum()));

				return response;

			} catch (Exception e) {
				response.setError(true);
				response.setMessage("Search system error");
				LOG.error(String.format(response.getMessage() + "[%s]", e.getMessage()));
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
	
	private StringBuilder setCategoryFilter(List<Category> list) {
		StringBuilder sb = new StringBuilder();
		for (Category c : list) {
			if (StringUtils.isNotBlank(c.getMajor())) {
				if (sb.length() > 0) {
					sb.append(" OR ");
				}
				
				if (StringUtils.isBlank(c.getMinor())) {
					sb.append(String.format("major:\"%s\"", c.getMajor()));
				}
				else {
					sb.append(String.format("(major:\"%s\" AND minor:\"%s\")", c.getMajor(), c.getMinor()));
				}
			}
		}
		
		return sb;
	}

	public FlatTransaction queryLatestTransactionByPayee(String payee) {
		SolrQuery q = new SolrQuery();
		q.setQuery("*:*");
		q.addFilterQuery(String.format("payee:\"%s\"", payee));
		q.addFilterQuery(String.format("type:%d OR type:%d", 0, 1));
		q.addSort("entered", SolrQuery.ORDER.desc);
		q.setStart(0);
		q.setRows(1);

		try {
			QueryResponse qr = getClient().query(q);
			List<FlatTransaction> list = qr.getBeans(FlatTransaction.class);
			if (list.size() > 0) {
				return list.get(0);
			}

		} catch (Exception e) {
			LOG.error(String.format("Search failure: %s", e.getMessage()));
		}

		return null;
	}

	public boolean commit(SolrClient client) {
		try {
			client.commit();
			LOG.debug("Solr successfully committed changes");
			return true;
		} catch (Exception e) {
			LOG.error(String.format("Solr failed to commit changes: %s", e.getMessage()));
		}
		
		return false;
	}
}
