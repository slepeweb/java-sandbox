package com.slepeweb.money;

import java.sql.Date;
import java.time.LocalDate;
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
		LocalDate scheduled = null, today = Util.today();
		Transaction t;
		boolean createdTransaction;
		int count = 0;
		
		for (ScheduledTransaction scht : all) {
			if (! scht.isEnabled()) {
				LOG.debug(String.format("Schedule [%s] is disabled", scht.getLabel()));
				continue;
			}
			
			LOG.debug(String.format("Considering schedule [%s], due [%s] ... ", scht.getLabel(), scht.getNextDate().toLocalDate()));
			scheduled = scht.getNextDate().toLocalDate();
			createdTransaction = false;
			
			// Populate the splits
			scht.setSplits(scheduledSplitService.get(scht.getId()));
			
			// This returns either a Transaction or a Transfer object, nearly fully populated
			t = Transaction.adapt(scht);					
			
			if (scheduled.isBefore(today)) {
				t.setId(0); // Indicating a new transaction is required
				t.setEntered(Util.todayAsDate());
				t.setSource(4);
				
				try {
					/*
					 *  The transactionService will take care of mirrored transactions
					 *  should t be a Transfer object
					 */
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
				Date nextDate = getNextDate(scheduled, scht.getPeriod());
				scht.setNextDate(nextDate);
				scheduledTransactionService.updateNextDate(scht);
			}
		}
		
		LOG.info(String.format("Scheduler created %d transactions", count));
	}
	
	private Date getNextDate(LocalDate lastDate, String intervalStr) {
		if (StringUtils.isBlank(intervalStr)) {
			return null;
		}
		
		Matcher m = INTERVAL_PTRN.matcher(intervalStr);
		if (! m.matches()) {
			return null;
		}
		
		int dayOfMonth = Math.min(28, lastDate.lengthOfMonth());
		LocalDate nextDate = null;
		int value = Integer.valueOf(m.group(1));
		String units = m.group(2);
		
		if (units.equalsIgnoreCase("m")) {
			// Units are months
			nextDate = lastDate.plusMonths(value).withDayOfMonth(dayOfMonth);
		}
		else {
			// Units must be days
			nextDate = lastDate.plusDays(value);
		}
		
		return Date.valueOf(nextDate);
	}
	
	public static void main(String[] args) {
		LocalDate start = Util.today();
		start = start.withDayOfMonth(start.lengthOfMonth());
		ScheduledTransactionTask task = new ScheduledTransactionTask();
		out("1m: ", Util.formatSimple(task.getNextDate(start, "1m").toLocalDate()));
		out("28d: ", Util.formatSimple(task.getNextDate(start, "40d").toLocalDate()));
	}
	
	public static void out(String... str) {
		for (String s : str) {
			System.out.print(s);
		}
		System.out.println("");
	}
}

