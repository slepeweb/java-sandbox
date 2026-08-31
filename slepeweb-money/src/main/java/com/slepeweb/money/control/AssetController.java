package com.slepeweb.money.control;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.NakedTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.YearlyAssetHistory;
import com.slepeweb.money.bean.YearlyAssetStatus;
import com.slepeweb.money.component.ChartPlottingComponent;
import com.slepeweb.money.service.AccountService;
import com.slepeweb.money.service.AssetService;
import com.slepeweb.money.service.NoteService;
import com.slepeweb.money.service.TransactionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="/asset")
public class AssetController extends BaseController {
	
	@Autowired private AssetService assetService;
	@Autowired private AccountService accountService;
	@Autowired private TransactionService transactionService;
	@Autowired private NoteService noteService;
	@Autowired private ChartPlottingComponent chartPlottingComponent;
	
	public static final String INCOME_LABEL = "Income";
	public static final String EXPENSE_LABEL = "Expense";
	public static final String BALANCE_LABEL = "Balance";
	
	@RequestMapping(value="/history")	
	public String save(ModelMap model) {
		YearlyAssetHistory history = new YearlyAssetHistory();
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		
		for (YearlyAssetStatus yas : this.assetService.getAll()) {
			history.add(yas);
			ds.addValue(Util.toPounds(yas.getIncome()), INCOME_LABEL, Integer.valueOf(yas.getYear()));
			ds.addValue(Util.toPounds(yas.getExpense()), EXPENSE_LABEL, Integer.valueOf(yas.getYear()));
			ds.addValue(Util.toPounds(yas.getBalance()), BALANCE_LABEL, Integer.valueOf(yas.getYear()));
		}
		
		SVGGraphics2D svg2d = this.chartPlottingComponent.plotAssetHistoryAsLine(ds);
		model.addAttribute("_assetSVG", svg2d.getSVGElement());
		model.addAttribute("_history", history);
		
		if (! history.isEmpty()) {
			int first = history.getList().get(0).getYear();
			int size = history.getList().size();
			int last = history.getList().get(size - 1).getYear();
			model.addAttribute("_notes", this.noteService.getNotes(-1, first, last));
		}
		
		return "assetHistory";
	}
	
	@RequestMapping(value="/history/save")	
	public RedirectView historyWindow(HttpServletRequest req, ModelMap model) throws Exception {
		
		// Work out opening and closing balances of asset accounts
		Map<Integer, Long[]> yearlyBalances = sumBalances();
		
		YearlyAssetHistory history = new YearlyAssetHistory();
		model.addAttribute("_data", history.getList());
		
		Transaction mirror;
		String accountType;
		
		YearlyAssetStatus yearlySummary;
		
		Long openingBalance, closingBalance, accumulativeBalance = 0L;
		LocalDate from = Util.startOfYear(Util.today());
		LocalDate to = Util.endOfYear(Util.today());
		int thisYear = to.getYear();		
		Long[] pair;
				
		for (int year = 1991; year <= thisYear; year++) {
			// Do we apply any opening balances to this year?
			pair = yearlyBalances.get(year);
			
			if (pair == null) {
				openingBalance = 0L;
				closingBalance = 0L;
			}
			else {
				openingBalance = pair[0];
				closingBalance = pair[1];
			}
			
			yearlySummary = new YearlyAssetStatus(year);
			history.add(yearlySummary);
			yearlySummary.credit(openingBalance);

			// Do we remove any non-zero closing balances to this year?
			// (If the account is closed, it shouldn't have any funds in it, but some older ones do!)
			yearlySummary.credit(-closingBalance);
						
			from = from.withYear(year);
			to = to.withYear(year);
			
			for (NakedTransaction t : this.assetService.getTransactionsBetween(Date.valueOf(from), Date.valueOf(to))) {
				if (t.isTransfer()) {
					mirror = this.transactionService.get(t.getTransferid());
					accountType = mirror.getAccount().getType();
					if (accountType == null || accountType.equals("other")) {
						// Some of the old (now closed) accounts used to be (for example) for Gas and Electricity, etc
						yearlySummary.count(t);
					}
					else {
						// Ignore transfers between accounts that are considered assets,
						// ie, savings, current, pension. Such transfers do not affect
						// overall wealth.
					}
				}
				else {
					yearlySummary.count(t);
				}
			}
			
			accumulativeBalance += yearlySummary.getGrowth();
			yearlySummary.setBalance(accumulativeBalance);
			history.add(yearlySummary);
		}
		
		// Now save records in the db
		this.assetService.save(history);

		return new RedirectView(String.format("%s/asset/history", req.getContextPath()));
	}	
	
	/*
	 *	This method loops over each account, accumulating opening and closing balances by year.
	 *
	 *  The map returned by this method is keyed by year. Each year is mapped to an array having 2 element, ie opening 
	 *  and closing balances for the year.
	 */
	private Map<Integer, Long[]> sumBalances() {
		
		Map<Integer, Long[]> yearlyBalances = new HashMap<Integer, Long[]>();
		
		int openingYear, closingYear;
		Long[] pair;
		LocalDate d;
	
		for (Account a : this.accountService.getAssets()) {
			
			// Get the date of the FIRST transaction on this account
			d = this.transactionService.getTransactionDateForAccount(a.getId(), true);
			
			openingYear = d.getYear();
			pair = yearlyBalances.get(openingYear);
			
			if (pair == null) {
				pair = new Long[] {Long.valueOf(0), Long.valueOf(0)};
				yearlyBalances.put(openingYear, pair);
			}
			
			pair[0] += a.getOpeningBalance();
			
			if (a.isClosed()) {
				
				// Get the date of the LAST transaction on this account
				d = this.transactionService.getTransactionDateForAccount(a.getId(), false);
				closingYear = d.getYear();
				pair = yearlyBalances.get(closingYear);

				if (pair == null) {
					pair = new Long[] {Long.valueOf(0), Long.valueOf(0)};
					yearlyBalances.put(closingYear, pair);
				}
				
				pair[1] += this.transactionService.calculateBalance(a.getId());				
			}
		}
		
		return yearlyBalances;
	}

}