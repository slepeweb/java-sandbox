package com.slepeweb.money;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.service.acorn.AcornMoneyImportService;

public class AcornMoneyImportManager {
	private static Logger LOG = Logger.getLogger(AcornMoneyImportManager.class);
	
	public static void main(String[] args) {
		LOG.info("====================");
		LOG.info("Starting AcornMoneyImportManager");
		
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");		
		AcornMoneyImportService mis = (AcornMoneyImportService) context.getBean("acornMoneyImportService");		
		AcornMoneyImportManager exe = new AcornMoneyImportManager();
		
		if (exe.init(mis)) {
			exe.importTransactions(mis);
		}
		
		LOG.info("... AcornMoneyImportManager has finished");
	}
	
	private boolean init(AcornMoneyImportService mis) {
		try {
			mis.init();
			return true;
		}
		catch (Exception e) {
			LOG.error("Failed to initialise the AcornMoneyImportService", e);
			return false;
		}
	}
		
	private void importTransactions(AcornMoneyImportService mis) {
		Transaction t;
		long count = 0L;
		
		LOG.info("======================");
		LOG.info("Importing transactions");
		LOG.info("======================");
		
		while ((t = mis.importTransaction()) != null) {
			if (count++ % 100 == 0) {
				LOG.info(String.format("Processed %d transactions", count));
			}
			
			t = mis.saveTransaction(t);				
		}
		LOG.info(String.format("Processed %d transactions in TOTAL", count));
	}
}
