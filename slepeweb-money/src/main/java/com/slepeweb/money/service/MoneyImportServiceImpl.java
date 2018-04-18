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
	
	@Autowired AccountService accountService;
	@Autowired PayeeService payeeService;
	@Autowired CategoryService categoryService;
	@Autowired PaymentService paymentService;
	
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
	
	public Payment createPayment(Account account, BufferedReader inf) {
		String line, code, value, major, minor;
		String[] parts;
		Payment pt = new Payment().setAccount(account);
		Payee pe;
		Category c;
		
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
							pe = this.payeeService.get(value);
							if (pe == null) {
								pe = new Payee().setName(value);
								try {
									pe = this.payeeService.save(pe);
								}
								catch (Exception e) {
									LOG.error("Failed to save payee", e);
								}
							}
							pt.setPayee(pe);
						}
						else if (code.equals("L")) {
							major = "";
							minor = "";
							pt.setTransfer(value.startsWith("["));
							if (! pt.isTransfer()) {
								parts = value.split(":");
								if (parts.length > 0) {
									major = parts[0];
									if (parts.length > 1) {
										minor = parts[1];
									}
								}
							}
	
							c = this.categoryService.get(major, minor);
							if (c == null) {
								c = new Category().setMajor(major).setMinor(minor);
								try {
									c = this.categoryService.save(c);
								}
								catch (Exception e) {
									LOG.error("Failed to save category", e);
								}
							}
							pt.setCategory(c);
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
