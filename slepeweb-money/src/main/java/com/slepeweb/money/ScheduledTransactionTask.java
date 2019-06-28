package com.slepeweb.money;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.slepeweb.money.bean.ScheduledTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.service.ScheduledSplitService;
import com.slepeweb.money.service.ScheduledTransactionService;
import com.slepeweb.money.service.TransactionService;

public class ScheduledTransactionTask implements Job {

	private static Logger LOG = Logger.getLogger(ScheduledTransactionTask.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		ScheduledTransactionService scheduledTransactionService = 
				(ScheduledTransactionService) context.getJobDetail().getJobDataMap().get("scheduledTransactionService");
		ScheduledSplitService scheduledSplitService = 
				(ScheduledSplitService) context.getJobDetail().getJobDataMap().get("scheduledSplitService");
		TransactionService transactionService = 
				(TransactionService) context.getJobDetail().getJobDataMap().get("transactionService");
		
		// getAll() does NOT retrieve splits. This is done seperately when we iterate over them later
		List<ScheduledTransaction> all = scheduledTransactionService.getAll();
		
		LOG.info(String.format("There are %d scheduled transactions", all.size()));
		Calendar threshold = Calendar.getInstance();
		Calendar scheduled = Calendar.getInstance();
		Transaction t;
		Timestamp lastEntered;
		long scheduleId;
		boolean createdTransaction;
		int count = 0;
		
		for (ScheduledTransaction scht : all) {
			// Keep a record of the scht properties, as we'll be using the scht for 
			// two different purposes: a) creating a transaction, and b) maintaining the lastEntered date.
			scheduleId = scht.getId();
			lastEntered = scht.getEntered();
			
			// threshold is set to the time the scheduled transaction was last entered
			threshold.setTime(scht.getEntered());
			
			// then roll threshold foward by 1 month
			threshold.add(Calendar.MONTH, 1);
			
			// scheduled is set to the DD-day of this month
			scheduled.set(Calendar.DATE, scht.getDay());
			
			createdTransaction = false;
			
			// Populate the splits
			scht.setSplits(scheduledSplitService.get(scht.getId()));
			t = Transaction.adapt(scht);
			
			while (scheduled.after(threshold)) {
				// Use scht properties to save a Transaction
				t.setId(0);
				t.setEntered(new Timestamp(scheduled.getTimeInMillis()));
				
				try {
					transactionService.save(t);
					lastEntered = t.getEntered();
					createdTransaction = true;
					count++;
				}
				catch (Exception e) {
					LOG.error("Failed to save transaction", e);
				}
				
				threshold.add(Calendar.MONTH, 1);	
			}
			
			// Update the scheduled transaction to record lastEntered
			if (createdTransaction) {
				scht.setId(scheduleId);
				scht.setEntered(lastEntered);
				scheduledTransactionService.updateLastEntered(scht);
			}
		}
		
		LOG.info(String.format("Created %d transactions", count));
	}
}
