package com.slepeweb.money.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.TransactionService;

public class Operations {

	private void log(String... s) {
		System.out.println(StringUtils.join(s));
	}
	
	private int findExcessTransactions(TransactionService transactionService, AccountService accountService) {
		Set<Long> origIds = new HashSet<Long>(10000);
		String filename = "/tmp/ids";
		File f = new File(filename);
		BufferedReader r = null;
		int count = 0;
		
		try {
			r = new BufferedReader(new FileReader(f));
			String s;
			Long l;
			while ( (s = r.readLine()) != null) {
				l = Long.valueOf(s.trim());
				if (origIds.contains(l)) {
					log("!!! Duplicate id", s);
				}
				else {
					origIds.add(l);
				}
			}
		}
		catch (Exception e) {
			log("*** Failed to read file", filename);
			return 1;
		}
		finally {
			if (f != null) {
				try {r.close();}
				catch(Exception ee) {}
			}
		}
		
		String accountName = "Halifax Mastercard";
		Account a = accountService.get(accountName);
		if (a == null) {
			log("*** Failed to identify account", accountName);
			return 2;
		}
		
		for (Transaction t : transactionService.getTransactionsForAccount(a.getId())) {
			count++;
			if (count % 100 == 0) {
				log(String.valueOf(count));
			}
			
			if (! origIds.contains(t.getOrigId())) {
				log("!!! Excess transaction found in mysql db", t.getEntered().toGMTString(), t.getAmountInPounds());
			}
		}
		
		return 0;
	}
	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");		
		TransactionService transactionService = (TransactionService) context.getBean("transactionService");		
		AccountService accountService = (AccountService) context.getBean("accountService");		
		Operations ops = new Operations();
		ops.findExcessTransactions(transactionService, accountService);
	}
}
