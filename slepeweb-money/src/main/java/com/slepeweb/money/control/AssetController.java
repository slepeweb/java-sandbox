package com.slepeweb.money.control;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
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
	
	@RequestMapping(value="/history")	
	public String history(ModelMap model) { 
		YearlyAssetHistory history = new YearlyAssetHistory();
		YearlyAssetStatus assetStatus;
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		Util.startOfYear(from);
		Util.endOfYear(to);
		Transaction mirror;
		List<String> validAccountTypes = Arrays.asList(new String[] {"current", "savings", "pension"});
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
				openingBalance = new Long(0L);	
			}
			
			// Note that there could be more than one account that was opened in a given year
			yearlyOpeningBalance.put(openingYear, openingBalance + a.getOpeningBalance());
			
			if (a.isClosed()) {
				closingYear = Util.getYear(this.transactionService.getTransactionDateForAccount(a.getId(), false));
				closingBalance = yearlyClosingBalance.get(closingYear);
				
				if (closingBalance == null) {
					closingBalance = new Long(0L);	
				}
				
				// Note that there could be more than one account that was closed in a given year
				yearlyClosingBalance.put(closingYear, closingBalance + this.transactionService.getBalance(a.getId()));				
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
				assetStatus.debit(closingBalance);
			}
			
			for (Transaction t : this.assetService.get(Util.toTimestamp(from), Util.toTimestamp(to))) {
				if (t.isTransfer()) {
					mirror = this.transactionService.get(t.getTransferId());
					if (! validAccountTypes.contains(mirror.getAccount().getType())) {
						assetStatus.count(t.getAmount());
					}
					else {
						// Ignore transfers to accounts that do no reflect wealth measurement.
					}
				}
				else {
					assetStatus.count(t.getAmount());
				}
			}
			
			overallBalance += assetStatus.getNetAmount();
			totalStatus.add(assetStatus);
			data.add(assetStatus);
			
			ds.addValue(Util.toPounds(assetStatus.getIncome()), INCOME_LABEL, Integer.valueOf(yearStepper));
			ds.addValue(Util.toPounds(assetStatus.getExpense()), EXPENSE_LABEL, Integer.valueOf(yearStepper));
			ds.addValue(Util.toPounds(overallBalance), BALANCE_LABEL, Integer.valueOf(yearStepper));
		}

		JFreeChart chart = ChartFactory.createBarChart(
		         "Asset history", "Years", "Amount (£)",
		         ds,
		         PlotOrientation.VERTICAL, true, true, false);
		
		int width = 1600, height = 600;
		SVGGraphics2D svg2d = new SVGGraphics2D(width, height);
		chart.draw(svg2d,new Rectangle2D.Double(0, 0, width, height));
		model.addAttribute("_assetSVG", svg2d.getSVGElement());
		model.addAttribute("_history", history);
		return "assetHistory";
	}	
}