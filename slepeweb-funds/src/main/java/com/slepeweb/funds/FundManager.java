package com.slepeweb.funds;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.slepeweb.funds.bean.FundPrice;
import com.slepeweb.funds.service.FundReporterService;
import com.slepeweb.funds.service.FundScraperService;

public class FundManager {
	private static Logger LOG = Logger.getLogger(FundManager.class);

	public static void main(String[] args) {
		
		LOG.info("====================");
		LOG.info("Starting FundManager");
		LOG.info(String.format("Today's date: %s", FundPrice.SDF.format(new Date())));
		
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		if (args.length > 0) {
			if (args[0].equals("scrape")) {
				FundScraperService fs = (FundScraperService) context.getBean("fundScraperService");
				fs.scrapeHalifax();
				fs.scrapeScottishWidows();				
			}
		}
		
		FundReporterService fr = (FundReporterService) context.getBean("fundReporterService");
		fr.graphReport();	
		
		LOG.info("... FundManager has finished");
	}
}
