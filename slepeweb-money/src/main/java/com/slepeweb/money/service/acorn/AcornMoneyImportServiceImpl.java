package com.slepeweb.money.service.acorn;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.AcornCategory;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.TransactionService;

@Service("acornMoneyImportService")
public class AcornMoneyImportServiceImpl implements AcornMoneyImportService {
	private static Logger LOG = Logger.getLogger(AcornMoneyImportServiceImpl.class);
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private TransactionService transactionService;
	@Autowired private AcornAccessService acornAccessService;
	
	public void init() throws IOException {
		// Create null entries for Payee and Category, if not already created. Same for 'special' payee JR
		this.acornAccessService.init(getPayee(""), getPayee(AcornCategory.JOHN_REEKS), 
				getCategory("", ""), getAccount("John Doe Account"));
		
		// Get all accounts from MSAccess, save them in mysql, and store them in a temporary cache
		Account mysqlAccount, accessAccount;

		while((accessAccount = this.acornAccessService.getNextAccount()) != null) {
			try {
				mysqlAccount = this.accountService.get(accessAccount.getName());
				
				if (mysqlAccount == null) {
					accessAccount.setNote("Acorn data");
					mysqlAccount = this.accountService.save(accessAccount);
				}
				
				this.acornAccessService.cacheAccount(accessAccount.getOrigId(), mysqlAccount);
			}
			catch (Exception e) {
				LOG.error("Failed to save account", e);
			}
		}
		
		// Repeat for categories
		this.acornAccessService.resetAccountOrPayeeCursor();
		AcornCategory acornCategory;
		Category mysqlCategory;
		String[] names;
		long origId;
		
		while((acornCategory = this.acornAccessService.getNextAcornCategory()) != null) {
			// Translate acorn name into major/minor names
			names = AcornCategory.translate(acornCategory.getTitle());
			
			if (names != null) {
				origId = acornCategory.getOrigId();
				mysqlCategory = this.categoryService.get(names[0], names[1]);
				
				if (mysqlCategory == null) {
					mysqlCategory = new Category().setMajor(names[0]).setMinor(names[1]);
					
					try {
						mysqlCategory = this.categoryService.save(mysqlCategory);
					}
					catch (Exception e) {
						LOG.error("Failed to save category", e);
					}
				}
				
				if (mysqlCategory != null) {
					acornCategory.assimilate(mysqlCategory);
					acornCategory.setId(mysqlCategory.getId());
					this.acornAccessService.cacheAcornCategory(origId, acornCategory);
				}
			}
			else {
				LOG.error(String.format("Failed to translate acorn category [%s]", acornCategory.getTitle()));
			}
		}
	}
		
	private Account getAccount(String name) {
		Account a = this.accountService.get(name);
		if (a == null) {
			a = new Account().setName(name).setNote("acorn");
			try {
				return this.accountService.save(a);
			}
			catch (Exception e) {
				LOG.error("Failed to save account", e);
			}
		}
		else {
			return a;
		}		
		
		return null;
	}
	
	private Payee getPayee(String name) {
		Payee p = this.payeeService.get(name);
		if (p == null) {
			p = new Payee().setName(name);
			try {
				return this.payeeService.save(p);
			}
			catch (Exception e) {
				LOG.error("Failed to save payee", e);
			}
		}
		else {
			return p;
		}		
		
		return null;
	}
	
	private AcornCategory getCategory(String major, String minor) {
		Category c = this.categoryService.get(major, minor);
		if (c == null) {
			c = new Category().setMajor(major).setMinor(minor);
			try {
				c = this.categoryService.save(c);
				return new AcornCategory(c);
			}
			catch (Exception e) {
				LOG.error("Failed to save category", e);
			}
		}
		else {
			return new AcornCategory(c);
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
	
	public Transaction importTransaction() {
		try {
			return this.acornAccessService.getNextTransaction();
		} 
		catch (IOException e) {
			LOG.error("Failed to read row of transaction data", e);
		}

		return null;
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
