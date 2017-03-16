package com.slepeweb.funds.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slepeweb.funds.bean.Fund;
import com.slepeweb.funds.bean.FundPrice;
import com.slepeweb.funds.except.DuplicateItemException;
import com.slepeweb.funds.except.MissingDataException;

@Service("fundScraperService")
public class FundScraperServiceImpl extends HttpServiceImpl implements FundScraperService {
	private static Logger LOG = Logger.getLogger(FundScraperServiceImpl.class);
	
	@Autowired FundService fundService;
	@Autowired FundPriceService fundPriceService;
	
	public void scrapeHalifax() {
	    String html = getResource("http://webfund6.financialexpress.net/clients/hbos/Ourfunds.aspx?range=0,2");
	    Document doc = Jsoup.parse(html);
	    Element table = doc.select("#SinglePriceTable").first();
	    Element td;
	    Elements allCells;
	    String selector, priceStr, dateStr;
	    Fund fund;
	    FundPrice price;
	    Timestamp ts;
	    float value;
	    String[] halifaxFundNames = {
	    		"Halifax U.K FTSE All-Share Stakeholder Pens",
	    		"Halifax Pelican Stakeholder Pens",
	    		"Halifax High Income Stakeholder Pens",
	    		"Halifax Gilt & Fixed Interest Stakeholder Pens"
	    };
	    
	    if (table != null) {
	    	for (String fundName : halifaxFundNames) {
	    		selector = String.format(".ldata:contains(%s)", fundName);
		    	td = table.select(selector).first();
		    	allCells = td.parent().children();
		    	
		    	if (allCells.size() == 7) {
				    priceStr = allCells.get(1).text();
				    dateStr = allCells.get(6).text();
			    	fund = this.fundService.getFund(fundName);
			    	ts = str2Timestamp(dateStr, FundPrice.SDF);
			    	value = str2Float(priceStr);
			    	
			    	if (value > -1F) {			    	
					    try {
					    	price = new FundPrice().
					    			setFund(fund).
					    			setEntered(ts).
					    			setValue(value);
					    	
					    	this.fundPriceService.save(price);
					    }
					    catch (MissingDataException e) {
					    	LOG.error("Missing data", e);
					    }
					    catch (DuplicateItemException e) {
					    	LOG.error("Price already entered", e);
					    }
			    	}
		    	}
	    	}
	    }
	}	
	
	public void scrapeScottishWidows() {
	    scrapeScottishWidowsDetail("http://webfund6.financialexpress.net/clientsv21/scottishwidows2/pricetable.aspx?fname=&strRec=0&selCat1=Pension%20Funds%20-%20Series%202&selCat2=&selCat3=", 
	    		new String[] {
	    				"Scottish Widows Cash Series 2",
	    	    		"Scottish Widows Pension Portfolio Four Series 2"
	    	    });
	    
	    scrapeScottishWidowsDetail("http://webfund6.financialexpress.net/clientsv21/scottishwidows2/pricetable.aspx?fname=&strRec=50&selCat1=Pension%20Funds%20-%20Series%202&selCat2=&selCat3=", 
	    		new String[] {
	    				"Scottish Widows Pension Protector Series 2"
	    	    });
	}	
	
	private void scrapeScottishWidowsDetail(String url, String[] fundNames) {
	    String html = getResource(url);	    
	    Document doc = Jsoup.parse(html);
	    Element table, target;
	    Elements allCells;
	    String selector, priceStr, dateStr;
	    Fund fund;
	    FundPrice price;
	    Timestamp ts;
	    float value;
	    
	    // First identify the date
	    selector = "table td:contains(Prices as at)";
	    target = doc.select(selector).first();
	    if (target == null) {
	    	LOG.error("Failed to identify date");
	    	return;
	    }
	    
	    Pattern pattern = Pattern.compile("^.*?Prices as at (\\d\\d/\\d\\d/\\d\\d) unless otherwise stated.*$", Pattern.MULTILINE);
	    Matcher m = pattern.matcher(target.text());
	    if (! m.matches()) {
	    	LOG.error("Failed to parse date");
	    	return;
	    }
	    
	    dateStr = m.group(1);
	    
	    table = doc.select(".headtable").first();
	    if (table != null) {
	    	for (String fundName : fundNames) {
	    		selector = String.format("td.hdr2a a:contains(%s)", fundName);
		    	target = table.select(selector).first();
		    	allCells = target.parent().parent().children();
		    	
		    	if (allCells.size() == 12) {
				    priceStr = allCells.get(4).text();
			    	fund = this.fundService.getFund(fundName);
			    	ts = str2Timestamp(dateStr, FundPrice.SDF_SHORT);
			    	value = str2Float(priceStr);
			    	
			    	if (value > -1F) {			    	
					    try {
					    	price = new FundPrice().
					    			setFund(fund).
					    			setEntered(ts).
					    			setValue(value);
					    	
					    	this.fundPriceService.save(price);
					    }
					    catch (MissingDataException e) {
					    	LOG.error("Missing data", e);
					    }
					    catch (DuplicateItemException e) {
					    	LOG.error("Price already entered", e);
					    }
			    	}
		    	}
	    	}
	    }
	}	
	
	private Timestamp str2Timestamp(String dateStr, SimpleDateFormat sdf) {
		try {
	    	Date date = sdf.parse(dateStr);
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(date);
	    	cal.set(Calendar.HOUR, 0);
	    	cal.set(Calendar.MINUTE, 0);
	    	cal.set(Calendar.SECOND, 0);
	    	cal.set(Calendar.MILLISECOND, 0);
	    	return new Timestamp(cal.getTimeInMillis());
	    }
	    catch (ParseException e) {
	    	LOG.error("Price not parseable", e);
	    }
		
		return null;
	}
	
	private float str2Float(String priceStr) {
		return new Float(priceStr);
		/*
		try {
			return DF.parse(priceStr).floatValue();
		}
		catch (ParseException e) {
			return -1F;
		}
		*/
	}
}
