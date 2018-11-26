package com.slepeweb.money.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Index;
import com.healthmarketscience.jackcess.IndexBuilder;
import com.healthmarketscience.jackcess.IndexCursor;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.TimeWindow;
import com.slepeweb.money.bean.Transaction;

/*
 * TRANSACTION TRANSFERS
 * 
select tr1.htrn, tr1.amt, ac1.szFull as From, tr2.htrn, tr2.amt, ac2.szFull as To
from trn_xfer trx, trn tr1, trn tr2
join acct ac1 on tr1.hacct = ac1.hacct
join acct ac2 on tr2.hacct = ac2.hacct
where trx.htrnFrom = tr1.htrn and trx.htrnLink = tr2.htrn
 */

/*
 * SPLIT TRANSACTIONS
 * 
select tr1.htrn as parent, tr1.amt, tr2.htrn as child, tr2.amt
from trn_split trs, trn tr1, trn tr2
where tr1.htrn = trs.htrnParent and tr2.htrn = trs.htrn
 */

@Service("msAccessService")
public class MSAccessServiceImpl extends BaseServiceImpl implements MSAccessService {
	
	private static Logger LOG = Logger.getLogger(MSAccessServiceImpl.class);
	
	public static final String ACCOUNT_TBL = "ACCT";
	public static final String PAYEE_TBL = "PAY";
	public static final String CATEGORY_TBL = "CAT";
	public static final String TRANSACTION_TBL = "TRN";
	public static final String TRANSACTION_XFER_TBL = "TRN_XFER";
	public static final String TRANSACTION_SPLIT_TBL = "TRN_SPLIT";
	
	public static final String TRANSACTION_DATE_IDX = "TRN_DT_IDX";

	public static final String FULL_NAME = "szFull";
	public static final String ACCOUNT_ID = "hacct";
	public static final String PAYEE_ID = "hpay";
	public static final String CATEGORY_ID = "hcat";
	public static final String PARENT_CATEGORY_ID = "hcatParent";
	public static final String TRANSACTION_ID = "htrn";
	public static final String TRANSACTION_PARENT_ID = "htrnParent";
	public static final String DATE_ENTERED = "dt";
	public static final String MEMO = "mMemo";
	public static final String AMOUNT = "amt";
	public static final String OPENING_AMOUNT = "amtOpen";
	public static final String CLOSED = "fClosed";
	public static final String COMMENT = "mComment";

	private String accessFilePath = "/home/george/slepeweb-money/home.MDB";
	
	private Map<Long, Account> accountMap = new HashMap<Long, Account>();
	private Map<Long, Payee> payeeMap = new HashMap<Long, Payee>();
	private Map<Long, Category> categoryMap = new HashMap<Long, Category>();
	
	private Payee noPayee;
	private Category noCategory;
	
	private Table catTable;
	private Cursor acctCursorSeq, payCursorSeq, catCursorSeq, parentCatCursorFinder;
	private Cursor trnCursorFinder, trnXferCursorSeq, trnSplitCursorSeq, trnSplitCursorFinder;
	private IndexCursor trnCursorSeq;
	
	public void init(Payee noPayee, Category noCategory, TimeWindow twin) throws IOException {
		this.noPayee = noPayee;
		this.noCategory = noCategory;
		
		Database db = DatabaseBuilder.open(new File(this.accessFilePath));
		Table trn = db.getTable(TRANSACTION_TBL);
		Index transactionDateIndex = null;
		try {
			transactionDateIndex = trn.getIndex(TRANSACTION_DATE_IDX);
		}
		catch (IllegalArgumentException e) {
			transactionDateIndex = new IndexBuilder(TRANSACTION_DATE_IDX).addColumns(DATE_ENTERED).addToTable(trn);
		}

		this.acctCursorSeq = CursorBuilder.createCursor(db.getTable(ACCOUNT_TBL));
		this.payCursorSeq = CursorBuilder.createCursor(db.getTable(PAYEE_TBL));
		this.catCursorSeq = CursorBuilder.createCursor(this.catTable = db.getTable(CATEGORY_TBL));
		this.parentCatCursorFinder = CursorBuilder.createCursor(db.getTable(CATEGORY_TBL));
		this.trnCursorSeq = CursorBuilder.createCursor(transactionDateIndex);
		this.trnCursorFinder = CursorBuilder.createCursor(db.getTable(TRANSACTION_TBL));
		this.trnXferCursorSeq = CursorBuilder.createCursor(db.getTable(TRANSACTION_XFER_TBL));
		this.trnSplitCursorSeq = CursorBuilder.createCursor(db.getTable(TRANSACTION_SPLIT_TBL));
		this.trnSplitCursorFinder = CursorBuilder.createCursor(db.getTable(TRANSACTION_SPLIT_TBL));
		
		// Locate the first transaction record within the given time window
		this.trnCursorSeq.findClosestRowByEntry(new Date(twin.getFrom().getTime()));
	}
	
	public static long decimal2long(BigDecimal dec) {
		return Float.valueOf(dec.floatValue() * 100).longValue();
	}
	
	public Account getNextAccount() throws IOException {
		// Create a new Account object (partial) with data from the ACCT table.
		Row r = this.acctCursorSeq.getNextRow();
		
		if (r != null) {
			return new Account().
					setOrigId(r.getInt(ACCOUNT_ID)).
					setName(r.getString(FULL_NAME)).
					setOpeningBalance(decimal2long(r.getBigDecimal(OPENING_AMOUNT))).
					setClosed(r.getBoolean(CLOSED)).
					setNote(r.getString(COMMENT));
		}
		
		return null;
	}
	
	public Payee getNextPayee() throws IOException {
		// Create a new Payee object (partial) with data from the PAY table.
		Row r = this.payCursorSeq.getNextRow();
		
		if (r != null) {
			return new Payee().
					setOrigId(r.getInt(PAYEE_ID)).
					setName(r.getString(FULL_NAME));
		}
		
		return null;
	}
	
	public Category getNextCategory() throws IOException {
		// Create a new Category object (partial) with data from the CAT table.
		Row childCategoryRow = this.catCursorSeq.getNextRow();
		
		if (childCategoryRow != null) {
			String childCategoryName = childCategoryRow.getString(FULL_NAME);
			Category c = new Category().setOrigId(childCategoryRow.getInt(CATEGORY_ID));
			
			if (this.parentCatCursorFinder.findFirstRow(Collections.singletonMap("hcat", childCategoryRow.getInt(PARENT_CATEGORY_ID)))) {
				String parentCategoryName = (String) this.parentCatCursorFinder.getCurrentRowValue(this.catTable.getColumn(FULL_NAME));
				
				if (parentCategoryName.equals("INCOME") || parentCategoryName.equals("EXPENSE")) {
					// This is a root category 
					c.setMajor(childCategoryName);
				}
				else {
					c.setMajor(parentCategoryName);
					c.setMinor(childCategoryName);
				}
			}
			else {
				c.setMajor(childCategoryName);
			}
			
			return c;
		}
		
		return null;
	}
	
	public void cacheAccount(Long origId, Account a) {
		this.accountMap.put(origId, a);
	}
	
	public void cachePayee(Long origId, Payee p) {
		this.payeeMap.put(origId, p);
	}
	
	public void cacheCategory(Long origId, Category c) {
		this.categoryMap.put(origId, c);
	}
	
	public Transaction getNextTransaction(TimeWindow twin) throws IOException {

		this.trnCursorSeq.moveToNextRow();
		if (this.trnCursorSeq.isAfterLast()) {
			// No more transactions
			return null;
		}
		
		// Create a new Transaction object with data from the TRN table.
		Row r = this.trnCursorSeq.getCurrentRow();
		
		if (r != null) {
			Date entered = r.getDate(DATE_ENTERED);
			if (entered.before(twin.getTo())) {
				Integer htrn = r.getInt(TRANSACTION_ID);
				
				// Do NOT process child transactions, ie those that are part of a split transaction
				if (! this.trnSplitCursorFinder.findFirstRow(Collections.singletonMap(TRANSACTION_ID, htrn))) {
					
					Transaction t = new Transaction().
							setAccount(this.accountMap.get(Long.valueOf(r.getInt(ACCOUNT_ID)))).
							setAmount(Util.decimal2long(r.getBigDecimal(AMOUNT))).
							setEntered(new Timestamp(r.getDate(DATE_ENTERED).getTime())).
							setMemo(r.getString(MEMO)).
							setOrigId(r.getInt(TRANSACTION_ID))/*.
							setReconciled(false).
							setReference("")*/;
					
					t.setPayee(identifyPayee(r.getInt(PAYEE_ID)));
					t.setCategory(identifyCategory(r.getInt(CATEGORY_ID)));							
					return t;
				}
				else {
					LOG.debug(compose("Ignoring child transaction (split)", htrn));
					return getNextTransaction(twin);
				}
			}
			else {
				LOG.debug(compose("Transaction outside time window", entered));
				return getNextTransaction(twin);
			}
		}
		
		return null;
	}
	
	public Transaction getNextSplitTransactionsParentOrigId() throws IOException {
		Row r = this.trnSplitCursorSeq.getNextRow();
		
		if (r != null) {
			return new Transaction().setOrigId(new Long(r.getInt(TRANSACTION_PARENT_ID)));
		}
		
		return null;
	}
	
	public void populateSplitTransactions(Transaction result) throws IOException {

		Long parentId = result.getOrigId();
		Long childId;		
		SplitTransaction st;
		Row parentTransactionRow, childTransactionRow;
		
		// Find corresponding child transactions
		Map<String, Long> parentCriteria = Collections.singletonMap(TRANSACTION_PARENT_ID, parentId);
		if (this.trnSplitCursorFinder.findFirstRow(parentCriteria)) {
			this.trnSplitCursorFinder.beforeFirst();
		
			while (this.trnSplitCursorFinder.findNextRow(parentCriteria)) {
				parentTransactionRow = this.trnSplitCursorFinder.getCurrentRow();
				childId = new Long(parentTransactionRow.getInt(TRANSACTION_ID));
				
				// Find the child transaction in trn table
				if (this.trnCursorFinder.findFirstRow(Collections.singletonMap(TRANSACTION_ID, childId))) {
					childTransactionRow = this.trnCursorFinder.getCurrentRow();
					st = new SplitTransaction().
						// This parentId is an MSAccess id - needs updating by caller
						setTransactionId(parentId).
						setAmount(Float.valueOf(childTransactionRow.getBigDecimal(AMOUNT).floatValue() * 100).longValue()).
						setMemo(childTransactionRow.getString(MEMO));
			
					st.setCategory(identifyCategory(childTransactionRow.getInt(CATEGORY_ID)));					
					result.getSplits().add(st);
					result.setSplit(true);
				}
				else {
					LOG.error(compose("Couldn't find child transaction", childId));
				}
			}
		}
		else {
			LOG.error(compose("Failed to identify parent transaction", parentId));
		}
	}
	
	private Payee identifyPayee(Integer hpay) {
		// Payee might be null
		if (hpay != null) {
			Payee p = this.payeeMap.get(Long.valueOf(hpay));
			if (p == null) {
				p = this.noPayee;
			}
			return p;
		}
		else {
			return this.noPayee;
		}
	}
	
	private Category identifyCategory(Integer hcat) {
		// Category might be null ...
		Category c = null;
		
		if (hcat != null) {
			c = this.categoryMap.get(Long.valueOf(hcat));
			if (c == null) {
				c = this.noCategory;
			}
			return c;
		}
		else {
			return this.noCategory;
		}
	}
	
	public Long[] getNextTransfer() throws IOException {
		Row r = this.trnXferCursorSeq.getNextRow();
		
		if (r != null) {
			return new Long[]{new Long(r.getInt("htrnLink")), new Long(r.getInt("htrnFrom"))};
		}
		
		return null;
	}
}
