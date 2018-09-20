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
import com.slepeweb.money.bean.SplitTransaction;
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
	
	public void init() throws IOException {
		// Create null entries for Payee and Category, if not already created
		this.msAccessService.init(getNoPayee(), getNoCategory());
		
		// Get all accounts from MSAccess, save them in mysql, and store them in a temporary cache
		Account aRecord, a;
		while((a = this.msAccessService.getNextAccount()) != null) {
			aRecord = this.accountService.get(a.getName());
			if (aRecord == null) {
				try {
					aRecord = this.accountService.save(a);
				}
				catch (Exception e) {
					LOG.error("Failed to save account", e);
				}
			}
			
			if (aRecord != null) {
				this.msAccessService.cacheAccount(a.getId(), aRecord);
			}
		}
		
		// Repeat for payments
		Payee pRecord, p;
		while((p = this.msAccessService.getNextPayee()) != null) {
			pRecord = this.payeeService.get(p.getName());
			if (pRecord == null) {
				try {
					pRecord = this.payeeService.save(p);
				}
				catch (Exception e) {
					LOG.error("Failed to save payee", e);
				}
			}
			
			if (pRecord != null) {
				this.msAccessService.cachePayee(p.getId(), pRecord);
			}
		}
		
		// Repeat for categories
		Category cRecord, c;
		while((c = this.msAccessService.getNextCategory()) != null) {
			cRecord = this.categoryService.get(c.getMajor(), c.getMinor());
			if (cRecord == null) {
				try {
					cRecord = this.categoryService.save(c);
				}
				catch (Exception e) {
					LOG.error("Failed to save category", e);
				}
			}
			
			if (cRecord != null) {
				this.msAccessService.cacheCategory(c.getId(), cRecord);
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
	
	public void updateTransaction(Transaction dbRecord, Transaction t) {
		try {
			this.transactionService.update(dbRecord, t);
		}
		catch (Exception e) {
			LOG.error("Failed to update transaction", e);
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
			return this.splitTransactionService.save(t);
		}
		catch (Exception e) {
			LOG.error("Failed to save split transaction", e);
		}
		
		return null;
	}
	
	public Transaction importTransaction() {
		try {
			return this.msAccessService.getNextTransaction();
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
	
	public Transaction importSplitTransactions() {
		try {
			Transaction imported = this.msAccessService.getNextSplitTransactions();
			// This transaction is incomplete - only the splits are useable, although
			// their transactionid property references an MSAccess htrn, and will need to be changed
			
			if (imported != null) {
				// This transaction (imported) has the correct mssql id, but its splits will be empty.
				// Get the (full) corresponding transaction in MySql. This operation will fail
				// if the transaction is for a future date, causing an error to be logged.
				Transaction dbRecord = getTransactionByOrigId(imported.getOrigId());
				
				if (dbRecord != null) {
					if (! dbRecord.matchesSplits(imported)) {
						// Use the imported splits
						dbRecord.setSplits(imported.getSplits());
						dbRecord.setSplit(imported.isSplit());
						
						// Correct the transactionid properties of each split
						for (SplitTransaction st : dbRecord.getSplits()) {
							st.setTransactionId(dbRecord.getId());
						}
						
						return dbRecord;
					}
					else {
						LOG.debug(String.format("No change in splits [%d]", imported.getOrigId()));
						// An empty transaction will be ignored by the caller
						return new Transaction();
					}
				}
				else {
					LOG.error(String.format("Failed to identify parent transaction [%d]", imported.getOrigId()));
					// An empty transaction will be ignored by the caller
					return new Transaction();
				}
			}
		} 
		catch (IOException e) {
			LOG.error("Failed to read row of split transaction data", e);
		}

		// No more split transactions to import
		return null;
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
