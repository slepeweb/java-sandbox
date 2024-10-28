package com.slepeweb.money;



import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.CategoryService;
import com.slepeweb.money.service.PayeeService;
import com.slepeweb.money.service.ScheduledSplitService;
import com.slepeweb.money.service.ScheduledTransactionService;
import com.slepeweb.money.service.TransactionService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class ScheduledTransactionManager {
	
	private static Logger LOG = Logger.getLogger(ScheduledTransactionManager.class);
	private Scheduler scheduler;
	@Autowired private ScheduledTransactionService scheduledTransactionService;
	@Autowired private ScheduledSplitService scheduledSplitService;
	@Autowired private TransactionService transactionService;
	@Autowired private AccountService accountService;
	@Autowired private PayeeService payeeService;
	@Autowired private CategoryService categoryService;
	

	@PostConstruct
	public void postInit() {
		try {
			this.scheduler = StdSchedulerFactory.getDefaultScheduler();
	        this.scheduler.start();
	        LOG.info("Started the quartz scheduler");
	        
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("scheduledTransactionService", this.scheduledTransactionService);
	        map.put("scheduledSplitService", this.scheduledSplitService);
	        map.put("transactionService", this.transactionService);
	        map.put("accountService", this.accountService);
	        map.put("payeeService", this.payeeService);
	        map.put("categoryService", this.categoryService);
	        JobDataMap params = new JobDataMap(map);

	        JobDetail job = JobBuilder.newJob(ScheduledTransactionTask.class).
	        		withIdentity("job1", "group1").
	        		usingJobData(params).
	        		build();
	        LOG.info("Built the job detail");

	        Trigger trigger = TriggerBuilder.newTrigger().
	        		withIdentity("trigger1", "group1").
	        		startNow().
	        		withSchedule(
	        				SimpleScheduleBuilder.
	        				simpleSchedule().
	        				withIntervalInMinutes(60).
	        				repeatForever()
	        		).
	        		build();
	        LOG.info("Built the trigger");

	        this.scheduler.scheduleJob(job, trigger);

		}
		catch (Exception e) {
			LOG.error("Failed to initialise the quartz scheduler", e);
		}

	}
	
	@PreDestroy
	public void preDestroy() {
		try {
	        this.scheduler.shutdown();
	        LOG.info("Shut down the quartz scheduler");
		}
		catch (Exception e) {
			LOG.error("Failed to shutdown the quartz scheduler", e);
		}
	}
}
