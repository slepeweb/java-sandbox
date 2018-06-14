package com.slepeweb.money;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Payment;
import com.slepeweb.money.service.MoneyImportService;

public class MoneyImportManager {
	private static Logger LOG = Logger.getLogger(MoneyImportManager.class);

	public static void main(String[] args) {
		
		LOG.info("====================");
		LOG.info("Starting MoneyImportManager");
		
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		MoneyImportService mis = (MoneyImportService) context.getBean("moneyImportService");
		
		// Create null entries for Payee and Category, if not already created
		Payee noPayee = mis.identifyNoPayee();
		Category noCategory = mis.identifyNoCategory();
		
		Payment pt;
		long count = 0L;
		
		while ((pt = mis.createPayment(noPayee, noCategory)) != null) {
			
			// Has this payment already been imported?
			if (mis.getPaymentByOrigId(pt.getOrigId()) == null) {
			
				pt = mis.savePayment(pt);
				count++;
				
				if (count % 100 == 0) {
					System.out.print(String.format("%d ", count));
				}
				
				/*
				if (pt.isSplit()) {
					for (PartPayment ppt : pt.getPartPayments()) {
						ppt.setPaymentId(pt.getId());
					}
					mis.savePartPayments(pt);
				}
				
				if (pt.isTransfer()) {
					// Put the same payment details into the target account, as a credit
					pt.setId(0L);
					pt.setAccount(pt.getTransfer());
					pt.setCharge(- pt.getCharge());
					pt.setTransfer(null);
					mis.savePayment(pt);
				}
				*/
			}
			else {
				LOG.debug(String.format("Payment [%d] already imported", pt.getOrigId()));
			}
		}
		
		//mis.resetAccountBalance(a);
		
		LOG.info("... MoneyImportManager has finished");
	}
}
