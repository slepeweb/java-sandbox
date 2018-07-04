package com.slepeweb.money;

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
			//exe.importTransactions(mis);
			exe.importTransfers(mis);
			exe.importSplits(mis);
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
		
	private void importTransactions(MoneyImportService mis) {
		Transaction pt;
		long count = 0L;
		
		while ((pt = mis.importTransaction()) != null) {
			
			// Has this payment already been imported?
			if (mis.getTransactionByOrigId(pt.getOrigId()) == null) {			
				pt = mis.saveTransaction(pt);				
				if (++count % 100 == 0) {
					System.out.print(String.format("%d ", count));
				}
				
				/*
				if (pt.isSplit()) {
					for (PartPayment ppt : pt.getPartPayments()) {
						ppt.setPaymentId(pt.getId());
					}
					mis.savePartPayments(pt);
				}				
				*/
			}
			else {
				LOG.debug(String.format("Payment [%d] already imported", pt.getOrigId()));
			}
		}
	}
	
	private void importTransfers(MoneyImportService mis) {
		long count = 0L;
		
		while (mis.importTransfer()) {
			if (++count % 100 == 0) {
				System.out.print(String.format("%d ", count));
			}
		}
	}
	
	private void importSplits(MoneyImportService mis) {
		long count = 0L;
		
		while (mis.importSplit()) {
			if (++count % 100 == 0) {
				System.out.print(String.format("%d ", count));
			}
		}
	}
}
