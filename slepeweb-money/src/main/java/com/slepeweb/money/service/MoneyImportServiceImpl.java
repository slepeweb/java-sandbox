package com.slepeweb.money.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.TimeWindow;
import com.slepeweb.money.bean.Transaction;

@Service("moneyImportService")
public class MoneyImportServiceImpl implements MoneyImportService {
	private static Logger LOG = Logger.getLogger(MoneyImportServiceImpl.class);
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;
	@Autowired private SplitTransactionService splitTransactionService;
	@Autowired private MSAccessService msAccessService;
	@Autowired private SolrService solrService;
	
	public void init(TimeWindow twin) throws IOException {
		// Create null entries for Payee and Category, if not already created
		this.msAccessService.init(getNoPayee(), getNoCategory(), twin);
		
		// Get all accounts from MSAccess, save them in mysql, and store them in a temporary cache
		Account mysqlAccount, accessAccount;

		while((accessAccount = this.msAccessService.getNextAccount()) != null) {
			try {
				mysqlAccount = this.accountService.getByOrigId(accessAccount.getOrigId());
				
				if (mysqlAccount != null) {
					// Do not overwrite the account type field
					accessAccount.setType(mysqlAccount.getType());
					mysqlAccount = this.accountService.update(mysqlAccount, accessAccount);
				}
				else {
					mysqlAccount = this.accountService.save(accessAccount);
				}
				
				this.msAccessService.cacheAccount(accessAccount.getOrigId(), mysqlAccount);
			}
			catch (Exception e) {
				LOG.error("Failed to save account", e);
			}
		}
		
		// Repeat for payments
		Payee mysqlPayee, accessPayee;
		while((accessPayee = this.msAccessService.getNextPayee()) != null) {
			try {
				mysqlPayee = this.payeeService.getByOrigId(accessPayee.getOrigId());
				
				if (mysqlPayee != null) {
					mysqlPayee = this.payeeService.update(mysqlPayee, accessPayee);
				}
				else {
					mysqlPayee = this.payeeService.save(accessPayee);
				}
				
				this.msAccessService.cachePayee(accessPayee.getOrigId(), mysqlPayee);
			}
			catch (Exception e) {
				LOG.error("Failed to save payee", e);
			}
		}
		
		// Repeat for categories
		Category mysqlCategory, accessCategory;
		while((accessCategory = this.msAccessService.getNextCategory()) != null) {
			try {
				mysqlCategory = this.categoryService.getByOrigId(accessCategory.getOrigId());
				
				if (mysqlCategory != null) {
					mysqlCategory = this.categoryService.update(mysqlCategory, accessCategory);
				}
				else {
					mysqlCategory = this.categoryService.save(accessCategory);
				}
				
				this.msAccessService.cacheCategory(accessCategory.getOrigId(), mysqlCategory);
			}
			catch (Exception e) {
				LOG.error("Failed to save category", e);
			}
		}
	}
		
	private Payee getNoPayee() {
		Payee p = this.payeeService.get("");
		if (p == null) {
			p = new Payee().setName("");
			try {
				return this.payeeService.save(p);
			}
			catch (Exception e) {
				LOG.error("Failed to save no-payee", e);
			}
		}
		else {
			return p;
		}		
		
		return null;
	}
	
	private Category getNoCategory() {
		Category c = this.categoryService.get("", "");
		if (c == null) {
			c = new Category().setMajor("").setMinor("");
			try {
				return this.categoryService.save(c);
			}
			catch (Exception e) {
				LOG.error("Failed to save no-category", e);
			}
		}
		else {
			return c;
		}
		
		return null;
	}
	
	public Transaction saveTransaction(Transaction t) {
		try {
			return this.transactionService.save(t);
		}
		catch (Exception e) {
			LOG.error("Failed to save transaction", e);
		}
		
		return null;
	}
	
	public Transaction updateTransaction(Transaction dbRecord, Transaction t) {
		try {
			return this.transactionService.update(dbRecord, t);
		}
		catch (Exception e) {
			LOG.error("Failed to update transaction", e);
			return dbRecord;
		}
	}
	
	public void updateTransfer(Long from, Long to) {
		try {
			this.transactionService.updateTransfer(from, to);
		}
		catch (Exception e) {
			LOG.error("Failed to update transfer", e);
		}
	}
	
	public Transaction saveSplitTransactions(Transaction t) {
		try {
			this.transactionService.updateSplit(t);
			t = this.splitTransactionService.save(t);
			this.solrService.save(t);
			return t;
		}
		catch (Exception e) {
			LOG.error("Failed to save split transaction", e);
		}
		
		return null;
	}
	
	public Transaction importTransaction(TimeWindow twin) {
		try {
			return this.msAccessService.getNextTransaction(twin);
		} 
		catch (IOException e) {
			LOG.error("Failed to read row of transaction data", e);
		}

		return null;
	}
	
	public Long[] importTransfer() {
		try {
			return this.msAccessService.getNextTransfer();
		} 
		catch (Exception e) {
			LOG.error("Failed to import transaction transfer", e);
		}
		
		return null;
	}
	
	public Transaction importSplitTransactionsParentId() {
		try {
			return this.msAccessService.getNextSplitTransactionsParentOrigId();			
		} 
		catch (IOException e) {
			LOG.error("Failed to read row of split transaction data", e);
		}

		// No more split transactions to import
		return null;
	}
	
	public void populateSplitTransactions(Transaction t) {
		try {
			this.msAccessService.populateSplitTransactions(t);			
		} 
		catch (IOException e) {
			LOG.error("Failed to read row of split transaction data", e);
		}
	}
	
	public Transaction getTransactionByOrigId(long id) {
		return this.transactionService.getByOrigId(id);
	}
	
	@SuppressWarnings("unused")
	private Timestamp parseDate(String dateStr) {
		try {
	    	Date date = Transaction.SDF.parse(dateStr);
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(date);
	    	cal.set(Calendar.HOUR, 0);
	    	cal.set(Calendar.MINUTE, 0);
	    	cal.set(Calendar.SECOND, 0);
	    	cal.set(Calendar.MILLISECOND, 0);
	    	return new Timestamp(cal.getTimeInMillis());
	    }
	    catch (ParseException e) {
	    	LOG.error("Price not parseable", e);
	    }
		
		return null;
	}
	
	@SuppressWarnings("unused")
	private long parseAmount(String s) {
		try {
			return Math.round(Transaction.NF.parse(s).floatValue() * 100.0);
	    }
	    catch (ParseException e) {
	    	LOG.error("Price not parseable", e);
	    }
		
		return 0L;
	}
	
}
