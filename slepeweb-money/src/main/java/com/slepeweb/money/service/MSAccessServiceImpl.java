package com.slepeweb.money.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	
	private Payee noPayee;
	private Category noCategory;
	
	private Table catTable;
	private Cursor acctCursor, payCursor, catCursor, parentCatCursor;
	private Cursor trnCursor, trnXferCursor, trnSplitCursor;
	
	public void init(Payee noPayee, Category noCategory) throws IOException {
		this.noPayee = noPayee;
		this.noCategory = noCategory;
		
		Database db = DatabaseBuilder.open(new File(this.accessFilePath));		
		this.acctCursor = CursorBuilder.createCursor(db.getTable("ACCT"));
		this.payCursor = CursorBuilder.createCursor(db.getTable("PAY"));
		this.catCursor = CursorBuilder.createCursor(this.catTable = db.getTable("CAT"));
		this.parentCatCursor = CursorBuilder.createCursor(db.getTable("CAT"));
		this.trnCursor = CursorBuilder.createCursor(db.getTable("TRN"));
		this.trnXferCursor = CursorBuilder.createCursor(db.getTable("TRN_XFER"));
		this.trnSplitCursor = CursorBuilder.createCursor(db.getTable("TRN_SPLIT"));
	}
	
	public Account getNextAccount() throws IOException {
		// Create a new Account object (partial) with data from the ACCT table.
		Row r = this.acctCursor.getNextRow();
		
		if (r != null) {
			return new Account().
					setId(r.getInt("hacct")).
					setName(r.getString("szFull"));
		}
		
		return null;
	}
	
	public Payee getNextPayee() throws IOException {
		// Create a new Payee object (partial) with data from the PAY table.
		Row r = this.payCursor.getNextRow();
		
		if (r != null) {
			return new Payee().
					setId(r.getInt("hpay")).
					setName(r.getString("szFull"));
		}
		
		return null;
	}
	
	public Category getNextCategory() throws IOException {
		// Create a new Category object (partial) with data from the CAT table.
		Row childCategoryRow = this.catCursor.getNextRow();
		
		if (childCategoryRow != null) {
			Category c = new Category().setId(childCategoryRow.getInt("hcat"));
			
			if (this.parentCatCursor.findFirstRow(Collections.singletonMap("hcat", childCategoryRow.getInt("hcatParent")))) {
				String parentCategoryName = (String) this.parentCatCursor.getCurrentRowValue(this.catTable.getColumn(FULL_NAME));
				
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
		// Create a new Payment object with data from the TRN table.
		Row r = this.trnCursor.getNextRow();
		
		if (r != null) {
			Integer htrn = r.getInt("htrn");
			
			// Do NOT process child transactions, ie those that are part of a split
			if ( ! this.trnSplitCursor.findFirstRow(Collections.singletonMap("htrnParent", htrn))) {
				
				Transaction pt = new Transaction().
						setAccount(this.accountMap.get(Long.valueOf(r.getInt("hacct")))).
						setAmount(Float.valueOf(r.getBigDecimal("amt").floatValue() * 100).longValue()).
						setEntered(new Timestamp(r.getDate("dt").getTime())).
						setMemo(r.getString("mMemo")).
						setOrigId(r.getInt("htrn"))/*.
						setReconciled(false).
						setReference("")*/;
				
				Integer h;
	
				// Payee might be null
				h = r.getInt("hpay");
				if (h != null) {
					Payee p = this.payeeMap.get(Long.valueOf(h));
					if (p == null) {
						p = this.noPayee;
					}
					pt.setPayee(p);
				}
				else {
					pt.setPayee(this.noPayee);
				}
		
				// Category might be null ...
				Integer hcat = r.getInt("hcat");
				Category c = null;
				
				if (hcat != null) {
					c = this.categoryMap.get(Long.valueOf(hcat));
					if (c == null) {
						c = this.noCategory;
					}
					pt.setCategory(c);
				}
				else {
					pt.setCategory(this.noCategory);
				}
		
				return pt;
			}
			else {
				return getNextTransaction();
			}
		}
		
		return null;
	}
	
	public Long[] getNextTransfer() throws IOException {
		Row r = this.trnXferCursor.getNextRow();
		
		if (r != null) {
			return new Long[]{new Long(r.getInt("htrnLink")), new Long(r.getInt("htrnFrom"))};
		}
		
		return null;
	}
}
