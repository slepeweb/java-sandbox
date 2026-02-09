package com.slepeweb.money;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
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
	private static Pattern INTERVAL_PTRN = Pattern.compile("^(\\d+)([mdMD])$");

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		ScheduledTransactionService scheduledTransactionService = 
				(ScheduledTransactionService) context.getJobDetail().getJobDataMap().get("scheduledTransactionService");
		ScheduledSplitService scheduledSplitService = 
				(ScheduledSplitService) context.getJobDetail().getJobDataMap().get("scheduledSplitService");
		TransactionService transactionService = 
				(TransactionService) context.getJobDetail().getJobDataMap().get("transactionService");
		
		// getAll() does NOT retrieve splits. This is done seperately when we iterate over them later
		List<ScheduledTransaction> all = scheduledTransactionService.getAll();
		
		LOG.debug(String.format("There are %d scheduled transactions", all.size()));
		Calendar scheduled = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		Transaction t;
		boolean createdTransaction;
		int count = 0;
		
		for (ScheduledTransaction scht : all) {
			if (! scht.isEnabled()) {
				LOG.debug(String.format("Schedule [%s] is disabled", scht.getLabel()));
				continue;
			}
			
			LOG.debug(String.format("Considering schedule [%s], due [%s] ... ", scht.getLabel(), scht.getNextDate().toLocalDateTime()));
			scheduled.setTime(scht.getNextDate());
			createdTransaction = false;
			
			// Populate the splits
			scht.setSplits(scheduledSplitService.get(scht.getId()));
			
			t = Transaction.adapt(scht);
			
			if (scheduled.before(now)) {
				// Use scht properties to save a Transaction
				t.setId(0);
				t.setEntered(new Timestamp(now.getTimeInMillis()));
				t.setSource(4);
				
				try {
					transactionService.save(t);
					createdTransaction = true;
					LOG.info(String.format("Processed scheduled transaction [%s]", scht.getLabel()));
					count++;
				}
				catch (Exception e) {
					LOG.error("Failed to process scheduled transaction", e);
				}
			}
			
			// Update the scheduled transaction to record lastEntered
			if (createdTransaction) {
				Calendar nextDate = getNextDate(scheduled, scht.getPeriod());
				scht.setNextDate(new Timestamp(nextDate.getTimeInMillis()));
				scheduledTransactionService.updateNextDate(scht);
			}
		}
		
		LOG.info(String.format("Scheduler created %d transactions", count));
	}
	
	private Calendar getNextDate(Calendar lastDate, String intervalStr) {
		if (StringUtils.isBlank(intervalStr)) {
			return null;
		}
		
		Matcher m = INTERVAL_PTRN.matcher(intervalStr);
		if (! m.matches()) {
			return null;
		}
		
		int dayOfMonth = Math.min(28, lastDate.get(Calendar.DAY_OF_MONTH));
		Calendar nextDate = (Calendar) lastDate.clone();
		int value = Integer.valueOf(m.group(1));
		String units = m.group(2);
		
		if (units.equalsIgnoreCase("m")) {
			// Units are months
			nextDate.add(Calendar.MONTH, value);
			nextDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		}
		else {
			// Units must be days
			nextDate.add(Calendar.DAY_OF_MONTH, value);
		}
		
		Util.zeroTimeOfDay(nextDate);
		return nextDate;
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		Calendar start = Calendar.getInstance();
		start.set(Calendar.DAY_OF_MONTH, 31);
		ScheduledTransactionTask task = new ScheduledTransactionTask();
		out("1m: ", task.getNextDate(start, "1m").getTime().toGMTString());
		out("28d: ", task.getNextDate(start, "29d").getTime().toGMTString());
	}
	
	public static void out(String... str) {
		for (String s : str) {
			System.out.print(s);
		}
		System.out.println("");
	}
}

