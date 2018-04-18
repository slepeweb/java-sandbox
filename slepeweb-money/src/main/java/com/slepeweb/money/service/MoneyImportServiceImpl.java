package com.slepeweb.money.service;

import java.io.BufferedReader;
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
	
	public Payment createPayment(Account account, BufferedReader inf) {
		String line, code, value;
		Payment pt = new Payment().setAccount(account);
		
		try {
			while ((line = inf.readLine()) != null) {
				if (line.length() > 0) {
					code = line.substring(0, 1);
					value = line.substring(1);
					
					if (! code.equals("!")) {
						if (code.equals("^")) {
							return pt;
						}
						else if (code.equals("D")) {
							pt.setEntered(parseDate(value));
						}
						else if (code.equals("C")) {
							pt.setReconciled(value.equals("X"));
						}
						else if (code.equals("T")) {
							pt.setCharge(parseCharge(value));
						}
						else if (code.equals("N")) {
							pt.setReference(value);
						}
						else if (code.equals("M")) {
							pt.setMemo(value);
						}
						else if (code.equals("P")) {
							pt.setPayee(getPayee(value));
						}
						else if (code.equals("L")) {
							pt.setTransfer(value.startsWith("["));
							if (! pt.isTransfer()) {
								pt.setCategory(getCategory(value));
							}
						}
						else if (code.equals("S")) {
							pt.setSplit(true);
							
							// Get next line, optional E (memo) record, followd by $ (charge) record
							// This MIGHT be followed by additional S/[E]/$ chunks. For each chunk, 
							// add a part-payment to pt.
							*** TODO
						}
					}
				}
			}
		}
		catch (IOException e) {
			LOG.error("Error reading inout file", e);
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
