package com.slepeweb.money;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;

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
		LocalDate today = Util.today();
		LocalDate monthBeginning = Util.today().withDayOfMonth(1).plusMonths(-3);
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
							Util.formatPounds(a.getOpeningBalance() + transactionService.calculateBalance(id))));
					pw.write("==========================================================\n");
					
					balance = a.getOpeningBalance();
					
					for (Transaction t : transactionService.getTransactionsForAccount(id, monthBeginning, today)) {
						balance += t.getAmount();
						
						pw.println(
								pack(String.valueOf(t.getId()), 8) + 
								pack(String.valueOf(t.getOrigId()), 8) + 
								pack(Util.formatSimple(t.getEntered()), 16) + 
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
