package com.slepeweb.money;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.TransactionService;

public class MoneyTransactionReporter {
	private static Logger LOG = Logger.getLogger(MoneyTransactionReporter.class);

	public static void main(String[] args) {
		LOG.info("====================");
		LOG.info("Starting MoneyTransactionReporter");
		
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");		
		AccountService accountService = (AccountService) context.getBean("accountService");		
		TransactionService transactionService = (TransactionService) context.getBean("transactionService");		
		new MoneyTransactionReporter().report(transactionService, accountService, new long[] {20L, 21L, 56L, 66L});
		
		LOG.info("... MoneyTransactionReporter has finished");
	}
	
	private void report(TransactionService transactionService, AccountService accountService, long[] accounts) {	
		Calendar today = Calendar.getInstance();
		Calendar monthBeginning = Calendar.getInstance();
		monthBeginning.set(Calendar.DAY_OF_MONTH, 1);
		monthBeginning.add(Calendar.MONTH, -3);
		Timestamp from = new Timestamp(monthBeginning.getTimeInMillis());
		Timestamp to = new Timestamp(today.getTimeInMillis());
		long balance = 0;
		
		// Open output file
		PrintWriter pw = null;
		String filePath = "/tmp/money-transactions.log";
		Account a;
		
		try {
			pw = new PrintWriter(new File(filePath));

			if (pw != null) {
				for (long id : accounts) {
					a = accountService.get(id);
					
					if (a == null) {
						LOG.error(String.format("Account not found [%d]", id));
						continue;
					}
					
					pw.write("\n");
					pw.write(String.format("Balance for account '%s': %s\n", a.getName(), 
							Util.formatPounds(a.getOpeningBalance() + transactionService.getBalance(id))));
					pw.write("==========================================================\n");
					
					balance = a.getOpeningBalance();
					
					for (Transaction t : transactionService.getTransactionsForAccount(id, from, to)) {
						balance += t.getAmount();
						
						pw.println(
								pack(String.valueOf(t.getId()), 8) + 
								pack(String.valueOf(t.getOrigId()), 8) + 
								pack(Util.formatTimestamp(new Date(t.getEntered().getTime())), 16) + 
								pack(t.getPayee().getName(), 40) + 
								pack(t.getCategory().toString(), 40) + 
								pack(t.getAmountInPounds(), 10) + 
								pack(String.valueOf(Util.formatPounds(balance)), 12));
					}
				}
				
				LOG.info(String.format("Report written to %s", filePath));
			}
		}
		catch (FileNotFoundException e) {
			LOG.fatal(String.format("Could not open output file [%s]", filePath));
			return;
		}
		finally {
			pw.close();
		}
	}
	
	private String pack(String s, int width) {
		return StringUtils.leftPad(s,  width);
	}
}
