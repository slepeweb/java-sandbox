package com.slepeweb.money;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.TimeWindow;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.service.MoneyImportService;

public class MoneyImportManager {
	private static Logger LOG = Logger.getLogger(MoneyImportManager.class);
	
	private Map<Long, Transaction> transactionCache;
	private Set<Long> processedSplitTransactions = new HashSet<Long>(913);

	public static void main(String[] args) {
		LOG.info("====================");
		LOG.info("Starting MoneyImportManager");
		
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");		
		MoneyImportService mis = (MoneyImportService) context.getBean("moneyImportService");		
		MoneyImportManager exe = new MoneyImportManager();
		exe.transactionCache = new HashMap<Long, Transaction>(17001);
		TimeWindow twin = new TimeWindow();
		
		if (args.length > 1) {
			if (args[0].equals("-from")) {
				Timestamp from = Util.parseTimestamp(args[1]);
				if (from != null) {
					twin.setFrom(from);
					LOG.info(String.format("Time window starts at %s", args[1]));
				}
				else {
					LOG.fatal(String.format("Failed to parse date [%s]", args[1]));
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
				t = mis.saveTransaction(t);				
			}
			else {
				// Yes - update the existing record
				t = mis.updateTransaction(dbRecord, t);
			}
			
			// Cache transactions. Note that if we're doing an update, this cache will only 
			// contain Transaction objects corresponding to the specified time window.
			this.transactionCache.put(t.getOrigId(), t);
			
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
				// First try finding the transaction in the cache
				Transaction fromTrn = getCachedTransaction(ptArr[0], mis);				
				// Now deal with the linked transaction in the same way
				Transaction toTrn = getCachedTransaction(ptArr[1], mis);
				
				if (fromTrn == null || toTrn == null) {
					continue;
				}
				
				// If the transfer is for a future date, then the source and target transactions
				// will not exist in the MySql database, causing errors to be logged.
				if (twin.wraps(fromTrn.getEntered())) {
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
	
	private Transaction getCachedTransaction(long origId, MoneyImportService mis) {
		Transaction t = this.transactionCache.get(origId);
//		if (t == null) {
//			// If not cached, look in the db
//			t = mis.getTransactionByOrigId(origId);
//			if (t == null) {
//				LOG.error(String.format("Failed to find transaction [%d] in MySQL database", origId));
//			}
//			else {
//				this.transactionCache.put(origId, t);
//			}
//		}
		
		return t;
	}
	
	private void importSplitTransactions(MoneyImportService mis, TimeWindow twin) {
		long count = 0L;
		Transaction partialTransaction;
		Long parentId;
		
		LOG.info("============================");
		LOG.info("Importing split transactions");
		LOG.info("============================");
		
		while ((partialTransaction = mis.importSplitTransactionsParentId()) != null) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d split transactions", count));
			}
			
			parentId = partialTransaction.getOrigId();
			if (this.processedSplitTransactions.contains(parentId)) {
				// The splits corresponding to this parent have already been processed in full.
				continue;
			}
			
			// This imported transaction (partialTransaction) will have the correct MSAccess original id, 
			// and its splits will be empty.
			// Get the (full) corresponding transaction: first try the cache, otherwise the database. 
			Transaction fullTransaction = getCachedTransaction(parentId, mis);
			
			if (fullTransaction != null) {
				if (twin.wraps(fullTransaction.getEntered())) {
					mis.populateSplitTransactions(partialTransaction);

					if (! fullTransaction.matchesSplits(partialTransaction)) {
						// Use the imported splits
						fullTransaction.setSplits(partialTransaction.getSplits());
						fullTransaction.setSplit(partialTransaction.isSplit());
						
						// Correct the transactionid properties of each split
						for (SplitTransaction st : fullTransaction.getSplits()) {
							st.setTransactionId(fullTransaction.getId());
						}
						
						// Save the splits
						mis.saveSplitTransactions(fullTransaction);
					}
					else {
						LOG.debug(String.format("No change in splits [%d]", partialTransaction.getOrigId()));
					}
				}
			}
			else {
				LOG.debug(String.format("Failed to identify parent transaction [%d]", parentId));
			}
			
			// Mark this parent transaction as processed, regardless of success or failure
			this.processedSplitTransactions.add(parentId);
		}
		
		LOG.info(String.format("Processed %d split transactions in TOTAL", count));
	}
}
