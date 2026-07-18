package com.slepeweb.money;

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
		
		/*
		 * NOTE: 
		 * 		getAll() retrieves all scheduled transactions, and binds those objects to account objects, and
		 * 		accounts have balances. As we loop through the schedules, we are updating account balances, and so
		 * 		the objects returned by getAll() may need to have there associated balances updated. An object of
		 * 		class AccountBalanceTracker is used to record updated Accounts and apply those changes to the
		 * 		ScheduledTransaction objects retrieved earlier.
		 * 
		 *  getAll() does NOT retrieve splits. This is done separately when we iterate over them later.
		 */
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
			
			LOG.debug(String.format("Considering schedule [%s], due [%s] ... ", scht.getLabel(), scht.getNextDate()));
			scheduled = scht.getNextDate();
			createdTransaction = false;
			
			// Populate the splits
			scht.setSplits(scheduledSplitService.get(scht.getId()));
			
			// This returns either a Transaction or a Transfer object, nearly fully populated
			t = Transaction.adapt(scht);
			
			if (scheduled.isEqual(today) || scheduled.isBefore(today)) {
				t.setId(0); // Indicating a new transaction is required
				t.setEntered(Util.today());
				t.setSource(4);
				
				try {
					// The transactionService will take care of mirrored transactions
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
				LocalDate nextDate = getNextDate(scheduled, scht.getPeriod());
				scht.setNextDate(nextDate);
				scheduledTransactionService.updateNextDate(scht);
			}
		}
		
		LOG.info(String.format("Scheduler created %d transactions", count));
	}
	
	private LocalDate getNextDate(LocalDate lastDate, String intervalStr) {
		if (StringUtils.isBlank(intervalStr)) {
			return null;
		}
		
		Matcher m = INTERVAL_PTRN.matcher(intervalStr);
		if (! m.matches()) {
			return null;
		}
		
		int dayOfMonth = Math.min(28, lastDate.getDayOfMonth());
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
		
		return nextDate;
	}
	
	public static void main(String[] args) {
		LocalDate start = Util.today();
		ScheduledTransactionTask task = new ScheduledTransactionTask();
		out("1m: ", Util.formatSimple(task.getNextDate(start, "1m")));
		out("28d: ", Util.formatSimple(task.getNextDate(start, "28d")));
	}
	
	public static void out(String... str) {
		for (String s : str) {
			System.out.print(s);
		}
		System.out.println("");
	}
}

