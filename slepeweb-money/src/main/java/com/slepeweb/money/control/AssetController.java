package com.slepeweb.money.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Chart;
import com.slepeweb.money.bean.NakedTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.YearlyAssetHistory;
import com.slepeweb.money.bean.YearlyAssetStatus;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.AssetService;
import com.slepeweb.money.service.ChartService;
import com.slepeweb.money.service.NoteService;
import com.slepeweb.money.service.TransactionService;

@Controller
@RequestMapping(value="/asset")
public class AssetController extends BaseController {
	
	@Autowired private AssetService assetService;
	@Autowired private AccountService accountService;
	@Autowired private TransactionService transactionService;
	@Autowired private NoteService noteService;
	@Autowired private ChartService chartService;
	
	public static final String INCOME_LABEL = "Income";
	public static final String EXPENSE_LABEL = "Expense";
	public static final String BALANCE_LABEL = "Balance";
	
	@RequestMapping(value="/history")	
	public String historyWindow(ModelMap model) {
		
		Chart ch = this.chartService.get(-1);
		if (ch == null) {
			throw new RuntimeException("Chart (-1) has not been declared");
		}
		
		int displayYearStart = ch.getFromYear();
		int displayYearEnd = ch.getToYear();
		
		YearlyAssetHistory history = new YearlyAssetHistory();
		YearlyAssetStatus assetStatus;
		LocalDate from = Util.startOfYear(Util.today());
		LocalDate to = Util.endOfYear(Util.today());

		Transaction mirror;
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		Long openingBalance, closingBalance;
		
		// Work out opening and closing balances of asset accounts
		Map<Integer, Long> yearlyOpeningBalance = new HashMap<Integer, Long>();
		Map<Integer, Long> yearlyClosingBalance = new HashMap<Integer, Long>();
		int openingYear, closingYear, minYear = 2020;
		LocalDate d;

		for (Account a : this.accountService.getAssets()) {
			d = this.transactionService.getTransactionDateForAccount(a.getId(), true);
			openingYear = d.getYear();
			openingBalance = yearlyOpeningBalance.get(openingYear);
			
			if (openingBalance == null) {
				openingBalance = Long.valueOf(0);	
			}
			
			// Note that there could be more than one account that was opened in a given year
			yearlyOpeningBalance.put(openingYear, openingBalance + a.getOpeningBalance());
			
			if (a.isClosed()) {
				d = this.transactionService.getTransactionDateForAccount(a.getId(), false);
				closingYear = d.getYear();
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
		
		int thisYear = Util.today().getYear();
		YearlyAssetStatus totalStatus = new YearlyAssetStatus(thisYear);
		model.addAttribute("_totals", totalStatus);
		
		long overallBalance = 0L;
		List<YearlyAssetStatus> data = new ArrayList<YearlyAssetStatus>();
		model.addAttribute("_data", data);
		
		String accountType;
				
		for (int yearStepper = minYear; yearStepper <= thisYear; yearStepper++) {
			from = from.withYear(yearStepper);
			to = to.withYear(yearStepper);
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
			
			for (NakedTransaction t : this.assetService.get(Date.valueOf(from), Date.valueOf(to))) {
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

		JFreeChart chart = ChartFactory.createLineChart(
		         "Asset history", "Years", "Amount (£)",
		         ds,
		         PlotOrientation.VERTICAL, true, true, false);
		
		CategoryPlot plot = chart.getCategoryPlot();
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		
		CategoryItemRenderer renderer = plot.getRenderer();
		Paint[] colors = new Paint[] {Color.BLUE, Color.RED, Color.YELLOW};
		for (int i = 0; i < colors.length; i++) {
		    renderer.setSeriesStroke(i, new BasicStroke(2.5f));
		    renderer.setSeriesPaint(i, colors[i]);
		}
		
		int width = 1170, height = 600;
		SVGGraphics2D svg2d = new SVGGraphics2D(width, height);
		chart.draw(svg2d,new Rectangle2D.Double(0, 0, width, height));
		model.addAttribute("_assetSVG", svg2d.getSVGElement());
		model.addAttribute("_history", history);
		model.addAttribute("_notes", this.noteService.getNotes(-1, displayYearStart, displayYearEnd));
		return "assetHistory";
	}	
}