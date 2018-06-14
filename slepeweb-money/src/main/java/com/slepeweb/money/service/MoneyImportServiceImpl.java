package com.slepeweb.money.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
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
import com.slepeweb.money.bean.PartPayment;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Payment;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("moneyImportService")
public class MoneyImportServiceImpl implements MoneyImportService {
	private static Logger LOG = Logger.getLogger(MoneyImportServiceImpl.class);
	
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	@Autowired private PaymentService paymentService;
	@Autowired private PartPaymentService partPaymentService;
	@Autowired private MSAccessService msAccessService;
	
	private Payee NO_PAYEE = null;
	private Category NO_CATEGORY = null;
	
	public Account identifyAccount(String accountName) {
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
	
	public Payee identifyNoPayee() {
		if (NO_PAYEE == null) {
			Payee p = this.payeeService.get("");
			if (p == null) {
				p = new Payee().setName("");
				try {
					NO_PAYEE = this.payeeService.save(p);
				}
				catch (DuplicateItemException die) {
				}
				catch (MissingDataException mde) {
				}
			}
			else {
				NO_PAYEE = p;
			}
		}
		return NO_PAYEE;
		
	}
	
	public Category identifyNoCategory() {
		if (NO_CATEGORY == null) {
			Category c = this.categoryService.get("", "");
			if (c == null) {
				c = new Category().setMajor("").setMinor("");
				try {
					NO_CATEGORY = this.categoryService.save(c);
				}
				catch (DuplicateItemException die) {
				}
				catch (MissingDataException mde) {
					
				}
			}
			else {
				NO_CATEGORY = c;
			}
		}
		return NO_CATEGORY;
		
	}
	
	public Payment savePayment(Payment pt) {
		try {
			return this.paymentService.save(pt);
		}
		catch (MissingDataException mde) {
		}
		catch (DuplicateItemException die) {
		}
		
		return null;
	}
	
	public Payment savePartPayments(Payment pt) {
		try {
			return this.partPaymentService.save(pt);
		}
		catch (MissingDataException mde) {
		}
		catch (DuplicateItemException die) {
		}
		
		return null;
	}
	
	public Payment createPayment(Payee noPayee, Category noCategory) {

		try {
			if (this.msAccessService.getNextTransaction() != null) {

				Payment pt = new Payment().
						setAccount(identifyAccount(this.msAccessService.getAccount())).
						setPayee(getPayee(this.msAccessService.getPayee())).
						setCategory(getCategory("")).
						//setCategory(getCategory(rs.getString("category"))).
						setEntered(this.msAccessService.getDate()).
						//setReconciled(value.equals("X")).
						setCharge(this.msAccessService.getAmount()).
						//pt.setCharge(parseCharge(value));
						//pt.setReference(value);
						setMemo(this.msAccessService.getMemo()).
						setOrigId(this.msAccessService.getOrigId());

				return pt;
			}
		} 
		catch (SQLException e) {
			LOG.error("Failed to read row of transaction data", e);
		}

		return null;
	}
	
	private List<PartPayment> createPartPayments(String firstCategoryStr, Category noCategory, BufferedReader inf) {
		List<PartPayment> list = new ArrayList<PartPayment>();
		String line, code, value;
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
	
	private Category getCategory(String value) {
		String major = "";
		String minor = "";
		String[] parts = value.split(":");
			if (parts.length > 0) {
				major = parts[0];
				if (parts.length > 1) {
					minor = parts[1];
				}
			}

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
	
	private Account getAccount(String value) {
		if (value.length() > 2 && value.startsWith("[")) {
			String name = value.substring(1,  value.length() - 1);
			Account a = this.accountService.get(name);
			
			if (a == null) {
				a = new Account().setName(name);
				try {
					a = this.accountService.save(a);
				}
				catch (Exception e) {
					LOG.error("Failed to save account", e);
				}
			}
			
			return a;
		}
		
		return null;
	}
	
	public Account resetAccountBalance(Account a) {
		a = this.accountService.resetBalance(a);
		LOG.info("Account balance updated");
		return a;
	}
	
	public Payment getPaymentByOrigId(long id) {
		return this.paymentService.getByOrigId(id);
	}
	
	private Timestamp parseDate(String dateStr) {
		try {
	    	Date date = Payment.SDF.parse(dateStr);
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
	
	private long parseCharge(String s) {
		try {
			return Math.round(Payment.NF.parse(s).floatValue() * 100.0);
	    }
	    catch (ParseException e) {
	    	LOG.error("Price not parseable", e);
	    }
		
		return 0L;
	}
	
}
