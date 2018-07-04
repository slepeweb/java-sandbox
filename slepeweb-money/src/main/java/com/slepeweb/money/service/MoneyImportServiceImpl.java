package com.slepeweb.money.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

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
		Account a, acct;
		while((acct = this.msAccessService.getNextAccount()) != null) {
			a = getAccount(acct.getName());
			if (a != null) {
				this.msAccessService.cacheAccount(acct.getId(), a);
			}
		}
		
		// Repeat for payments
		Payee p, payee;
		while((payee = this.msAccessService.getNextPayee()) != null) {
			p = getPayee(payee.getName());
			if (p != null) {
				this.msAccessService.cachePayee(payee.getId(), p);
			}
		}
		
		// Repeat for categories
		Category c, cat;
		while((cat = this.msAccessService.getNextCategory()) != null) {
			c = getCategory(cat.getMajor(), cat.getMinor());
			if (c != null) {
				this.msAccessService.cacheCategory(cat.getId(), c);
			}
		}
	}
	
	public Account getAccount(String accountName) {
		Account a = this.accountService.get(accountName);
		if (a == null) {
			a = new Account().setName(accountName);
			try {
				a = this.accountService.save(a);
			}
			catch (Exception e) {
				LOG.error("Failed to save account", e);
			}
		}
		return a;
	}
	
	private Payee getNoPayee() {
		Payee p = this.payeeService.get("");
		if (p == null) {
			p = new Payee().setName("");
			try {
				return this.payeeService.save(p);
			}
			catch (DuplicateItemException die) {
			}
			catch (MissingDataException mde) {
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
			catch (DuplicateItemException die) {
			}
			catch (MissingDataException mde) {
				
			}
		}
		else {
			return c;
		}
		
		return null;
	}
	
	public Transaction saveTransaction(Transaction pt) {
		try {
			return this.transactionService.save(pt);
		}
		catch (MissingDataException mde) {
		}
		catch (DuplicateItemException die) {
		}
		
		return null;
	}
	
	public void updateTransfer(Long from, Long to) {
		try {
			this.transactionService.updateTransfer(from, to);
		}
		catch (MissingDataException mde) {
		}
		catch (DuplicateItemException die) {
		}
	}
	
	public Transaction saveSplitTransactions(Transaction pt) {
		try {
			return this.splitTransactionService.save(pt);
		}
		catch (MissingDataException mde) {
		}
		catch (DuplicateItemException die) {
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
	
	public boolean importTransfer() {
		try {
			Long[] ptArr = this.msAccessService.getNextTransfer();
			if (ptArr != null) {
				Transaction from = getTransactionByOrigId(ptArr[0]);
				if (from == null) {
					LOG.error(String.format("Failed to identify source transaction [%d]", ptArr[0]));
				}
				
				Transaction to = getTransactionByOrigId(ptArr[1]);
				if (to == null) {
					LOG.error(String.format("Failed to identify target transaction [%d]", ptArr[1]));
				}
				
				if (from != null && to != null) {
					this.transactionService.updateTransfer(from.getId(), to.getId());
				}
			}
			else {
				// Return false only when there are no more transfer records to import
				return false;
			}
		} 
		catch (Exception e) {
			LOG.error("Failed to import transaction transfer", e);
		}
		
		// More transfer records to follow
		return true;
	}
	
	public boolean importSplit() {
		/*
		try {
			Long[] ptArr = this.msAccessService.getNextTrnXfer();
			if (ptArr != null) {
				Transaction from = getPaymentByOrigId(ptArr[0]);
				Transaction to = getPaymentByOrigId(ptArr[1]);
				
				if (from != null && to != null) {
					this.paymentService.updateXferIds(from.getId(), to.getId());
					return true;
				}
			}
		} 
		catch (Exception e) {
			LOG.error("Failed to read row of transaction transfer data", e);
		}
		*/

		return false;
	}
	
	@SuppressWarnings("unused")
	private List<SplitTransaction> createSplits(String firstCategoryStr, Category noCategory, BufferedReader inf) {
		List<SplitTransaction> list = new ArrayList<SplitTransaction>();
		String line, code, value;
		/*
		PartPayment ppt = new PartPayment().setCategory(getCategory(firstCategoryStr));
		
		
		try {
			while ((line = inf.readLine()) != null) {
				if (line.length() > 0) {
					code = line.substring(0, 1);
					value = line.substring(1);
					
					if (code.equals("^")) {
						return list;
					}
					// There is always a Category record
					else if (code.equals("S")) {
						ppt.setCategory(getCategory(value));
					}
					// The memo record is optional
					else if (code.equals("E")) {
						ppt.setMemo(value);
					}
					// There is always a Charge record, marking the completion of the part-payment
					else if (code.equals("$")) {
						ppt.setCharge(parseCharge(value));
						list.add(ppt);
						ppt = new PartPayment().setCategory(noCategory);
					}
				}
			}
		}
		catch (IOException e) {
			LOG.error("Error reading input file", e);
		}
		*/
		return null;
	}
	
	private Payee getPayee(String name) {
		Payee pe = this.payeeService.get(name);
		if (pe == null) {
			pe = new Payee().setName(name);
			try {
				pe = this.payeeService.save(pe);
			}
			catch (Exception e) {
				LOG.error("Failed to save payee", e);
			}
		}
		return pe;
	}
	
	private Category getCategory(String major, String minor) {
		Category c = this.categoryService.get(major, minor);
		if (c == null) {
			c = new Category().setMajor(major).setMinor(minor);
			try {
				c = this.categoryService.save(c);
			}
			catch (Exception e) {
				LOG.error("Failed to save category", e);
			}
		}
		
		return c;
	}
		
	public Account resetAccountBalance(Account a) {
		a = this.accountService.resetBalance(a);
		LOG.info("Account balance updated");
		return a;
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
