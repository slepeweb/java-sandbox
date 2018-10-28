package com.slepeweb.money;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.money.bean.TimeWindow;
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
		TimeWindow twin = new TimeWindow();
		
		if (args.length > 1) {
			if (args[0].equals("from")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					twin.setFrom(new Timestamp(sdf.parse(args[1]).getTime()));
				}
				catch (ParseException e) {
					LOG.fatal(String.format("Failed to parse date [%s]", args[0]), e);
				}
			}
		}
		
		if (exe.init(mis, twin)) {
			exe.importTransactions(mis, twin);
			exe.importTransfers(mis, twin);
			exe.importSplitTransactions(mis, twin);
		}
		
		LOG.info("... MoneyImportManager has finished");
	}
	
	private boolean init(MoneyImportService mis, TimeWindow twin) {
		try {
			mis.init(twin);
			return true;
		}
		catch (Exception e) {
			LOG.error("Failed to initialise the MS Access service", e);
			return false;
		}
	}
		
	private void importTransactions(MoneyImportService mis, TimeWindow twin) {
		Transaction t;
		long count = 0L;
		
		LOG.info("======================");
		LOG.info("Importing transactions");
		LOG.info("======================");
		
		while ((t = mis.importTransaction(twin)) != null) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d transactions", count));
			}
			
			// Has this payment already been imported?
			Transaction dbRecord = mis.getTransactionByOrigId(t.getOrigId());
			if ( dbRecord == null) {
				// No - save a new record
				mis.saveTransaction(t);				
			}
			else {
				// Yes - update the existing record
				mis.updateTransaction(dbRecord, t);
			}
		}
		LOG.info(String.format("Processed %d transactions in TOTAL", count));
	}
	
	private void importTransfers(MoneyImportService mis, TimeWindow twin) {
		long count = 0L;
		Long[] ptArr;
		
		LOG.info("===================================");
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
				if (fromTrn != null && toTrn != null && (twin.getFrom() == null || fromTrn.getEntered().after(twin.getFrom()))) {
					if (! (fromTrn.matchesTransfer(toTrn))) {
						mis.updateTransfer(fromTrn.getId(), toTrn.getId());
					}
					else {
						LOG.debug(String.format("No change in transfer details [%d]", ptArr[1]));
					}
				}
			}
		}
		LOG.info(String.format("Processed %d transfers in TOTAL", count));
	}
	
	private void importSplitTransactions(MoneyImportService mis, TimeWindow twin) {
		long count = 0L;
		Transaction t;
		
		LOG.info("============================");
		LOG.info("Importing split transactions");
		LOG.info("============================");
		
		while ((t = mis.importSplitTransactions()) != null) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d split transactions", count));
			}
			
			if (t.isSplit() && (twin.getFrom() == null || t.getEntered().after(twin.getFrom()))) {
				mis.saveSplitTransactions(t);
			}
		}
		LOG.info(String.format("Processed %d split transactions in TOTAL", count));
	}
}
