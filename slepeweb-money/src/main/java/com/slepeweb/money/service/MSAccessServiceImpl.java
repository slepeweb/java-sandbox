package com.slepeweb.money.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SplitTransaction;
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

	private String accessFilePath = "/home/george/home.mdb";
	
	/*
	private String trnQuery = String.format(
			"select " + 
					"trn.htrn as %s, acct.szFull as %s, pay.szFull as %s, trn.dt as %s, trn.mMemo as %s, trn.amt as %s, " +
					"parent.szFull as %s, child.szFull as %s "	+		
			"from trn, cat child " +
			"join acct on trn.hacct = acct.hacct " +
			"left join pay on trn.hpay = pay.hpay " +
			"left join cat parent on child.hcatParent=parent.hcat " +
			"left join trn_split trs on trn.htrn = trs.htrn " +
			"where trs.htrnParent is null", 
				ORIG_ID, ACCOUNT, PAYEE, DATE_ENTERED, MEMO, AMOUNT, PARENT_CATEGORY, CHILD_CATEGORY);
				*/
	
	private Map<Long, Account> accountMap = new HashMap<Long, Account>();
	private Map<Long, Payee> payeeMap = new HashMap<Long, Payee>();
	private Map<Long, Category> categoryMap = new HashMap<Long, Category>();
	private Set<Long> processedParentTransactions = new HashSet<Long>();
	
	private Payee noPayee;
	private Category noCategory;
	
	private Table catTable;
	private Cursor acctCursorSeq, payCursorSeq, catCursorSeq, parentCatCursorFinder;
	private Cursor trnCursorSeq, trnCursorFinder, trnXferCursorSeq, trnSplitCursorSeq, trnSplitCursorFinder;
	
	public void init(Payee noPayee, Category noCategory) throws IOException {
		this.noPayee = noPayee;
		this.noCategory = noCategory;
		
		Database db = DatabaseBuilder.open(new File(this.accessFilePath));		
		this.acctCursorSeq = CursorBuilder.createCursor(db.getTable("ACCT"));
		this.payCursorSeq = CursorBuilder.createCursor(db.getTable("PAY"));
		this.catCursorSeq = CursorBuilder.createCursor(this.catTable = db.getTable("CAT"));
		this.parentCatCursorFinder = CursorBuilder.createCursor(db.getTable("CAT"));
		this.trnCursorSeq = CursorBuilder.createCursor(db.getTable("TRN"));
		this.trnCursorFinder = CursorBuilder.createCursor(db.getTable("TRN"));
		this.trnXferCursorSeq = CursorBuilder.createCursor(db.getTable("TRN_XFER"));
		this.trnSplitCursorSeq = CursorBuilder.createCursor(db.getTable("TRN_SPLIT"));
		this.trnSplitCursorFinder = CursorBuilder.createCursor(db.getTable("TRN_SPLIT"));
	}
	
	public static long decimal2long(BigDecimal dec) {
		return Float.valueOf(dec.floatValue() * 100).longValue();
	}
	
	public Account getNextAccount() throws IOException {
		// Create a new Account object (partial) with data from the ACCT table.
		Row r = this.acctCursorSeq.getNextRow();
		
		if (r != null) {
			return new Account().
					setId(r.getInt(ACCOUNT_ID)).
					setName(r.getString(FULL_NAME)).
					setOpeningBalance(decimal2long(r.getBigDecimal("amtOpen"))).
					setClosed(r.getBoolean("fClosed")).
					setNote(r.getString("mComment"));
		}
		
		return null;
	}
	
	public Payee getNextPayee() throws IOException {
		// Create a new Payee object (partial) with data from the PAY table.
		Row r = this.payCursorSeq.getNextRow();
		
		if (r != null) {
			return new Payee().
					setId(r.getInt(PAYEE_ID)).
					setName(r.getString(FULL_NAME));
		}
		
		return null;
	}
	
	public Category getNextCategory() throws IOException {
		// Create a new Category object (partial) with data from the CAT table.
		Row childCategoryRow = this.catCursorSeq.getNextRow();
		
		if (childCategoryRow != null) {
			Category c = new Category().setId(childCategoryRow.getInt(CATEGORY_ID));
			
			if (this.parentCatCursorFinder.findFirstRow(Collections.singletonMap("hcat", childCategoryRow.getInt(PARENT_CATEGORY_ID)))) {
				String parentCategoryName = (String) this.parentCatCursorFinder.getCurrentRowValue(this.catTable.getColumn(FULL_NAME));
				
				if (parentCategoryName.equals("INCOME") || parentCategoryName.equals("EXPENSE")) {
					// This is a root category 
					c.setMajor(childCategoryRow.getString(FULL_NAME));
				}
				else {
					c.setMajor(parentCategoryName);
					c.setMinor(childCategoryRow.getString(FULL_NAME));
				}
			}
			else {
				c.setMajor(childCategoryRow.getString(FULL_NAME));
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
	
	public Transaction getNextTransaction() throws IOException {
		// Create a new Transaction object with data from the TRN table.
		Row r = this.trnCursorSeq.getNextRow();
		
		if (r != null) {
			Integer htrn = r.getInt(TRANSACTION_ID);
			
			// Do NOT process child transactions, ie those that are part of a split transaction
			if (! this.trnSplitCursorFinder.findFirstRow(Collections.singletonMap(TRANSACTION_ID, htrn))) {
				
				Transaction t = new Transaction().
						setAccount(this.accountMap.get(Long.valueOf(r.getInt(ACCOUNT_ID)))).
						setAmount(Float.valueOf(r.getBigDecimal(AMOUNT).floatValue() * 100).longValue()).
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
				return getNextTransaction();
			}
		}
		
		return null;
	}
	
	/*
	 * Returns a 'bare' Transaction object representing the parent, with just a few properties set, in particular:
	 * 	- its child (ie split) transactions
	 * 	- its origid
	 * 	- its split status
	 */
	public Transaction getNextSplitTransactions() throws IOException {
		Row r = this.trnSplitCursorSeq.getNextRow();
		
		if (r != null) {
			Long parentId = new Long(r.getInt(TRANSACTION_PARENT_ID));
			Long childId;
			
			// Has parent already been processed?
			if (! this.processedParentTransactions.contains(parentId)) {		
				Transaction result = new Transaction().setOrigId(parentId);
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
					
					// Mark this parent transaction as completed
					this.processedParentTransactions.add(parentId);
					return result;
				}
				else {
					LOG.error(compose("Failed to identify parent transaction", parentId));
				}
			}
			else {
				// Skip this parent (already processed), and try the next
				return getNextSplitTransactions();
			}
		}
		
		return null;
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
