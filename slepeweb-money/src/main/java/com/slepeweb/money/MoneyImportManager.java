package com.slepeweb.money;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
		
		if (exe.init(mis)) {
			exe.importTransactions(mis, args);
			exe.importTransfers(mis);
			exe.importSplitTransactions(mis);
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
		
	private void importTransactions(MoneyImportService mis, String[] args) {
		Timestamp from = null;
		Transaction t;
		long count = 0L;
		
		LOG.info("Importing transactions");
		LOG.info("======================");
		
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
		
		while ((t = mis.importTransaction()) != null) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d transactions", count));
			}
			
			if (from == null || from.after(t.getEntered())) {
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
	
	private void importTransfers(MoneyImportService mis) {
		long count = 0L;
		
		LOG.info("Importing transaction transfer data");
		LOG.info("===================================");
		
		while (mis.importTransfer()) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d transfers", count));
			}
		}
	}
	
	private void importSplitTransactions(MoneyImportService mis) {
		long count = 0L;
		Transaction t;
		
		LOG.info("Importing split transactions");
		LOG.info("============================");
		
		while ((t = mis.importSplitTransactions()) != null) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d split transactions", count));
			}
			
			if (t.isSplit()) {
				mis.saveSplitTransactions(t);
			}
		}
	}
}
