package com.slepeweb.money;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.service.TransactionService;
import com.slepeweb.money.service.Util;

public class MoneyTransactionReporter {
	private static Logger LOG = Logger.getLogger(MoneyTransactionReporter.class);

	public static void main(String[] args) {
		LOG.info("====================");
		LOG.info("Starting MoneyTransactionReporter");
		
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");		
		TransactionService transactionService = (TransactionService) context.getBean("transactionService");		
		new MoneyTransactionReporter().report(transactionService, new long[] {20L, 21L});
		
		LOG.info("... MoneyTransactionReporter has finished");
	}
	
	private void report(TransactionService transactionService, long[] accounts) {	
		Calendar today = Calendar.getInstance();
		Calendar monthBeginning = Calendar.getInstance();
		monthBeginning.set(Calendar.DAY_OF_MONTH, 1);
		monthBeginning.add(Calendar.MONTH, -1);
		Timestamp from = new Timestamp(monthBeginning.getTimeInMillis());
		Timestamp to = new Timestamp(today.getTimeInMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		for (long id : accounts) {
			LOG.info("");
			LOG.info(String.format("Balance for account [%d]: %s", id, Util.formatPounds(transactionService.getBalance(id))));
			LOG.info("================================================");
			
			for (Transaction t : transactionService.getTransactionsForAccount(id, from, to)) {
				LOG.info(
						pack(String.valueOf(t.getId()), 8) + 
						pack(String.valueOf(t.getOrigId()), 8) + 
						pack(sdf.format(new Date(t.getEntered().getTime())), 16) + 
						pack(t.getAccount().getName(), 32) + 
						pack(t.getPayee().getName(), 32) + 
						pack(t.getCategory().toString(), 32) + 
						pack(t.getAmountInPounds(), 10));
			}
		}
	}
	
	private String pack(String s, int width) {
		return StringUtils.leftPad(s,  width);
	}
}
