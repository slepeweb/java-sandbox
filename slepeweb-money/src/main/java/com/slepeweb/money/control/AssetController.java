package com.slepeweb.money.control;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.NakedTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.YearlyAssetHistory;
import com.slepeweb.money.bean.YearlyAssetStatus;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.AssetService;
import com.slepeweb.money.service.TransactionService;

@Controller
@RequestMapping(value="/asset")
public class AssetController extends BaseController {
	
	@Autowired private AssetService assetService;
	@Autowired private AccountService accountService;
	@Autowired private TransactionService transactionService;
	
	public static final String INCOME_LABEL = "Income";
	public static final String EXPENSE_LABEL = "Expense";
	public static final String BALANCE_LABEL = "Balance";
	
	/*
	 * The 'history' methods retrieve transaction records directly from the
	 * database, and not from solr. They determine whether the transaction is
	 * income or an expense by the category.
	 */
	@RequestMapping(value="/history")	
	public String history(ModelMap model) { 
		int thisYear = Util.getYear(new Date());
		return historyStart(thisYear - 10, model);
	}
	
	@RequestMapping(value="/history/{displayYearStart}")	
	public String historyStart(@PathVariable int displayYearStart, ModelMap model) { 
		int thisYear = Util.getYear(new Date());
		return historyWindow(displayYearStart, thisYear, model);
	}
	
	@RequestMapping(value="/history/{displayYearStart}/{displayYearEnd}")	
	public String historyWindow(@PathVariable int displayYearStart, @PathVariable int displayYearEnd, ModelMap model) { 
		YearlyAssetHistory history = new YearlyAssetHistory();
		YearlyAssetStatus assetStatus;
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		Util.startOfYear(from);
		Util.endOfYear(to);
		Transaction mirror;
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		Long openingBalance, closingBalance;
		
		// Work out opening and closing balances of asset accounts
		Map<Integer, Long> yearlyOpeningBalance = new HashMap<Integer, Long>();
		Map<Integer, Long> yearlyClosingBalance = new HashMap<Integer, Long>();
		int openingYear, closingYear, minYear = 2020;

		for (Account a : this.accountService.getAssets()) {
			openingYear = Util.getYear(this.transactionService.getTransactionDateForAccount(a.getId(), true));
			openingBalance = yearlyOpeningBalance.get(openingYear);
			
			if (openingBalance == null) {
				openingBalance = Long.valueOf(0);	
			}
			
			// Note that there could be more than one account that was opened in a given year
			yearlyOpeningBalance.put(openingYear, openingBalance + a.getOpeningBalance());
			
			if (a.isClosed()) {
				closingYear = Util.getYear(this.transactionService.getTransactionDateForAccount(a.getId(), false));
				closingBalance = yearlyClosingBalance.get(closingYear);
				
				if (closingBalance == null) {
					closingBalance = Long.valueOf(0);	
				}
				
				// Note that there could be more than one account that was closed in a given year
				yearlyClosingBalance.put(closingYear, closingBalance + this.transactionService.calculateBalance(a.getId()));				
			}
			
			if (openingYear < minYear) {
				minYear = openingYear;
			}
		}
		
		int thisYear = Util.getYear(new Date());
		YearlyAssetStatus totalStatus = new YearlyAssetStatus(thisYear);
		model.addAttribute("_totals", totalStatus);
		
		long overallBalance = 0L;
		List<YearlyAssetStatus> data = new ArrayList<YearlyAssetStatus>();
		model.addAttribute("_data", data);
		
		String accountType;
				
		for (int yearStepper = minYear; yearStepper <= thisYear; yearStepper++) {
			from.set(Calendar.YEAR, yearStepper);
			to.set(Calendar.YEAR, yearStepper);
			assetStatus = new YearlyAssetStatus(yearStepper);
			history.add(assetStatus);
			
			// Do we apply any opening balances to this year?
			openingBalance = yearlyOpeningBalance.get(yearStepper);
			if (openingBalance != null) {
				assetStatus.credit(openingBalance);
			}
			
			// Do we remove any non-zero closing balances to this year?
			// (If the account is closed, it shouldn't have any funds in it, but some older ones do!)
			closingBalance = yearlyClosingBalance.get(yearStepper);
			if (closingBalance != null) {
				//assetStatus.debit(closingBalance);
				assetStatus.credit(-closingBalance);
			}
			
			for (NakedTransaction t : this.assetService.get(Util.toTimestamp(from), Util.toTimestamp(to))) {
				if (t.isTransfer()) {
					mirror = this.transactionService.get(t.getTransferid());
					accountType = mirror.getAccount().getType();
					if (accountType == null || accountType.equals("other")) {
						// Some of the old (now closed) accounts used to be (for example) for Gas and Electricity, etc
						assetStatus.count(t);
					}
					else {
						// Ignore transfers between accounts that are considered assets,
						// ie, savings, current, pension. Such transfers do not affect
						// overall wealth.
					}
				}
				else {
					assetStatus.count(t);
				}
			}
			
			overallBalance += assetStatus.getGrowth();
			totalStatus.add(assetStatus);
			data.add(assetStatus);
			
			if (yearStepper >= displayYearStart && yearStepper <= displayYearEnd) {
				ds.addValue(Util.toPounds(assetStatus.getIncome()), INCOME_LABEL, Integer.valueOf(yearStepper));
				ds.addValue(Util.toPounds(assetStatus.getExpense()), EXPENSE_LABEL, Integer.valueOf(yearStepper));
				ds.addValue(Util.toPounds(overallBalance), BALANCE_LABEL, Integer.valueOf(yearStepper));
			}
		}

		JFreeChart chart = ChartFactory.createBarChart(
		         "Asset history", "Years", "Amount (£)",
		         ds,
		         PlotOrientation.VERTICAL, true, true, false);
		
		CategoryPlot categoryplot = chart.getCategoryPlot();
		BarRenderer bar = new BarRenderer();		
		bar.setSeriesPaint(0, Color.BLUE);
		bar.setSeriesPaint(1, Color.RED);
		bar.setSeriesPaint(2, Color.GREEN);
		categoryplot.setRenderer(bar);

		int width = 1170, height = 600;
		SVGGraphics2D svg2d = new SVGGraphics2D(width, height);
		chart.draw(svg2d,new Rectangle2D.Double(0, 0, width, height));
		model.addAttribute("_assetSVG", svg2d.getSVGElement());
		model.addAttribute("_history", history);
		return "assetHistory";
	}	
}