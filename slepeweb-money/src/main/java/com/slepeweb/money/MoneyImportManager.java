package com.slepeweb.money;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.service.MoneyImportService;

public class MoneyImportManager {
	private static Logger LOG = Logger.getLogger(MoneyImportManager.class);

	public static void main(String[] args) {
		LOG.info("====================");
		LOG.info("Starting MoneyImportManager");
		
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");		
		MoneyImportService mis = (MoneyImportService) context.getBean("moneyImportService");		
		MoneyImportManager exe = new MoneyImportManager();
		Timestamp from = null;
		
		if (args.length > 1) {
			if (args[0].equals("from")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					from = new Timestamp(sdf.parse(args[1]).getTime());
				}
				catch (ParseException e) {
					LOG.fatal(String.format("Failed to parse date [%s]", args[0]), e);
				}
			}
		}
		
		if (exe.init(mis)) {
			exe.importTransactions(mis, from);
			exe.importTransfers(mis, from);
			exe.importSplitTransactions(mis, from);
		}
		
		LOG.info("... MoneyImportManager has finished");
	}
	
	private boolean init(MoneyImportService mis) {
		try {
			mis.init();
			return true;
		}
		catch (Exception e) {
			LOG.error("Failed to initialise the MS Access service", e);
			return false;
		}
	}
		
	private void importTransactions(MoneyImportService mis, Timestamp from) {
		Transaction t;
		long count = 0L;
		Timestamp now = new Timestamp(new Date().getTime());
		
		LOG.info("Importing transactions");
		LOG.info("======================");
		
		while ((t = mis.importTransaction()) != null) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d transactions", count));
			}
			
			// Does this transaction fit within required time window?
			if ((from == null || t.getEntered().after(from)) && t.getEntered().before(now)) {
				// Has this payment already been imported?
				Transaction dbRecord = mis.getTransactionByOrigId(t.getOrigId());
				if ( dbRecord == null) {			
					mis.saveTransaction(t);				
				}
				else {
					mis.updateTransaction(dbRecord, t);
				}
			}
		}
	}
	
	private void importTransfers(MoneyImportService mis, Timestamp fromDate) {
		long count = 0L;
		Long[] ptArr;
		
		LOG.info("Importing transaction transfer data");
		LOG.info("===================================");
		
		while ((ptArr = mis.importTransfer()) != null) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d transfers", count));
			}
			
			if (ptArr != null) {
				Transaction fromTrn = mis.getTransactionByOrigId(ptArr[0]);
				if (fromTrn == null) {
					LOG.error(String.format("Failed to identify source transaction [%d]", ptArr[0]));
				}
				
				Transaction toTrn = mis.getTransactionByOrigId(ptArr[1]);
				if (toTrn == null) {
					LOG.error(String.format("Failed to identify target transaction [%d]", ptArr[1]));
				}
				
				// If the transfer is for a future date, then the source and target transactions
				// will not exist in the MySql database, causing errors to be logged.
				if (fromTrn != null && toTrn != null && (fromDate == null || fromTrn.getEntered().after(fromDate))) {
					if (! (fromTrn.matchesTransfer(toTrn))) {
						mis.updateTransfer(fromTrn.getId(), toTrn.getId());
					}
					else {
						LOG.debug(String.format("No change in transfer details [%d]", ptArr[1]));
					}
				}
			}
		}
	}
	
	private void importSplitTransactions(MoneyImportService mis, Timestamp from) {
		long count = 0L;
		Transaction t;
		
		LOG.info("Importing split transactions");
		LOG.info("============================");
		
		while ((t = mis.importSplitTransactions()) != null) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d split transactions", count));
			}
			
			if (t.isSplit() && (from == null || t.getEntered().after(from))) {
				mis.saveSplitTransactions(t);
			}
		}
	}
}
