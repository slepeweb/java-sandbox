package com.slepeweb.money;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.PartPayment;
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
		
		if (args.length > 1) {
			MoneyImportService mis = (MoneyImportService) context.getBean("moneyImportService");
			
			// Get (or create new) the account corresponding to this input QIF
			Account a = mis.identifyAccount(args[0]);
			if (a == null) {
				LOG.error("Failed to identify account");
				return;
			}
			
			// Create null entries for Payee and Category, if not already created
			Payee noPayee = mis.identifyNoPayee();
			Category noCategory = mis.identifyNoCategory();
			
			// Open the input file
			BufferedReader inf = null;
			try {
				inf = new BufferedReader(new FileReader(args[1]));
			}
			catch (FileNotFoundException fnf) {
				LOG.error("Failed to identify input file", fnf);
			}
			
			// Process each payment block, one at a time
			Payment pt;
			long count = 0L;
			while ((pt = mis.createPayment(a, noPayee, noCategory, inf)) != null) {
				pt = mis.savePayment(pt);
				count++;
				if (count % 100 == 0) {
					System.out.print(String.format("%d ", count));
				}
				
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
			}
			
			mis.resetAccountBalance(a);
		}
		
		LOG.info("... MoneyImportManager has finished");
	}
}
