package com.slepeweb.funds;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.slepeweb.funds.bean.Fund;
import com.slepeweb.funds.bean.FundPrice;
import com.slepeweb.funds.service.FundPriceService;
import com.slepeweb.funds.service.FundService;

public class FundReporter {

	private static Logger LOG = Logger.getLogger(FundReporter.class);
	private String template;
	
	@Autowired FundService fundService;
	@Autowired FundPriceService fundPriceService;
	
	public void graphReport() {
		
		StringBuilder sb = new StringBuilder();
		Calendar now = Calendar.getInstance();
		Calendar before = Calendar.getInstance();
		before.add(Calendar.MONTH, -3);
		
		Timestamp from = new Timestamp(before.getTimeInMillis());
		Timestamp to = new Timestamp(now.getTimeInMillis());
		
		List<Fund> funds = this.fundService.getAllFunds();
		List<FundPrice> allPrices = this.fundPriceService.getAllPrices(from, to);
		
		int column = 0;
		int numColumns = funds.size();
		int maxColumnIndex = numColumns - 1;
		
		sb.append("\ndata.addColumn('date', 'Date');");
		for (Fund f : funds) {
			sb.append(String.format("\ndata.addColumn('number', '%s (%s)');", f.getAlias(), 
					FundPrice.DF_TOTAL.format(f.getUnits())));
		}
		sb.append("\ndata.addColumn('number', 'Total value');");
		
		sb.append("\ndata.addRows([");
		
		Timestamp ts = null;
		float[] totalFundValues = new float[numColumns];
		
		for (FundPrice fp : allPrices) {
			if (ts == null) {
				// first row
				ts = fp.getEntered();
				newRow(sb, fp);
				column = 0;
			}
			
			if (fp.getEntered().getTime() > ts.getTime()) {
				// new row starts with '['
				newRow(sb, fp);
				column = 0;
			}			
			
			// value for column N
			while (column < numColumns) {
				if (funds.get(column).equals(fp.getFund())) {
					totalFundValues[column] = fp.getValue() * fp.getFund().getUnits();
					column = appendColumn(sb, FundPrice.DF.format(fp.getValue()), column, maxColumnIndex, 
							totalFundValues);
					break;
				}
				else {
					column = appendColumn(sb, "", column, maxColumnIndex, totalFundValues);
				}
			}
			
			ts = fp.getEntered();
		}
		
		sb.append("]);");
		
		float totalValue = sum(totalFundValues);
		LOG.info(String.format("Total value of all funds = %6.0f", totalValue / 100));
		sb.append(String.format("\nvar _totalValue = '%s';", FundPrice.DF_TOTAL.format(totalValue / 100)));
		
		streamReport(sb.toString());
		LOG.info("Finished");
	}
	
	private float sum(float[] totalFundValues) {
		float totalValue = 0F;
		for (float f : totalFundValues) {
			totalValue += f;
		}
		return totalValue;
	}
	
	private void streamReport(String data) {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream("/tmp/funds.html");
			streamTemplate(os, "head");
			os.write(data.getBytes());
			os.flush();
			streamTemplate(os, "tail");
		}
		catch (IOException e) {
			LOG.error("Problem streaming the report", e);
		}
		finally {
			if (os != null) {
				try {
					os.close();
				}
				catch (Exception e) {}
			}
		}
	}
	
	private Date toDate(Timestamp ts) {
		return new Date(ts.getTime());
	}
	
	private void newRow(StringBuilder sb, FundPrice fp) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(toDate(fp.getEntered()));
		sb.append("[").
			append(String.format("new Date(%d, %d, %d)",
					cal.get(Calendar.YEAR), 
					cal.get(Calendar.MONTH), 
					cal.get(Calendar.DAY_OF_MONTH))).
			append(",");
	}
	
	private int appendColumn(StringBuilder sb, String value, int column, int maxColumnIndex, float[] totalValues) {
		sb.append(value);
		
		// seperate columns by ','
		sb.append(",");
		
		// terminate complete row with '['
		if (column == maxColumnIndex) {
			sb.append(FundPrice.DF_TOTAL.format(sum(totalValues) / 100000));
			sb.append("],\n");
			return 0;
		}
		
		return column + 1;
	}
	
	private void streamTemplate(OutputStream os, String fileSuffix) {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL url = cl.getResource(String.format("%s-%s.html", this.template, fileSuffix));
		if (url != null) {
			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(url.openStream());
				int bufflen = 100;
				byte[] bytes = new byte[bufflen];
				int count = -1;
				while ((count = in.read(bytes, 0, bufflen)) > -1) {
					os.write(bytes, 0, count);
				}
			}
			catch (IOException e) {
				LOG.error("Template error", e);
			}
			finally {
				try {
					os.flush();
					if (in != null) {
						in.close();
					}
				}
				catch (Exception e) {}
			}
		}
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
