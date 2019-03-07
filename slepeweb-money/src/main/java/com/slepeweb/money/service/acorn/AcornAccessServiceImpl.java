package com.slepeweb.money.service.acorn;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.slepeweb.money.Util;
import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.AcornCategory;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.Transfer;
import com.slepeweb.money.service.BaseServiceImpl;

/*
 * TRANSACTIONS
 * 
select m.date, m.description, m.amount, ap1.title, ap1.account, ap1.payment, ap1.income, ap2.title, ap2.account, ap2.payment, ap2.income from main m
left join accountspayees ap1 on m.fromref=ap1.id
left join accountspayees ap2 on m.toref=ap2.id
order by m.date desc
 */

@Service("acornAccessService")
public class AcornAccessServiceImpl extends BaseServiceImpl implements AcornAccessService {
	
	private static Logger LOG = Logger.getLogger(AcornAccessServiceImpl.class);
	
	public static final String ACCOUNT_TBL = "AccountsPayees";
	public static final String TRANSACTION_TBL = "Main";	

	public static final String FULL_NAME = "title";
	public static final String ACCOUNT_ID = "id";
	public static final String FROM_ACCOUNT_ID = "fromref";
	public static final String TO_ACCOUNT_ID = "toref";
	public static final String DATE_ENTERED = "date";
	public static final String MEMO = "description";
	public static final String AMOUNT = "amount";

	private String accessFilePath = "/home/george/slepeweb-money/acorn-money.mdb";
	
	private Map<Long, Account> accountMap = new HashMap<Long, Account>();
	private Map<Long, AcornCategory> categoryMap = new HashMap<Long, AcornCategory>();
	
	private Account johnDoeAccount;
	private Payee noPayee, johnReeks;
	private AcornCategory noCategory;
	private Cursor acctCursorSeq, trnCursorSeq;
	
	public void init(Payee noPayee, Payee johnReeks, AcornCategory noCategory, 
			Account johnDoeAccount) throws IOException {
		this.noPayee = noPayee;
		this.johnReeks = johnReeks;
		this.noCategory = noCategory;
		this.johnDoeAccount = johnDoeAccount;
		
		Database db = DatabaseBuilder.open(new File(this.accessFilePath));
		this.acctCursorSeq = CursorBuilder.createCursor(db.getTable(ACCOUNT_TBL));
		this.trnCursorSeq = CursorBuilder.createCursor(db.getTable(TRANSACTION_TBL));
	}
	
	public static long decimal2long(BigDecimal dec) {
		return Float.valueOf(dec.floatValue() * 100).longValue();
	}
	
	public Account getNextAccount() throws IOException {
		Row r = this.acctCursorSeq.getNextRow();
		
		if (r != null) {
			if (r.getBoolean("account")) {
				return new Account().
						setOrigId(r.getInt(ACCOUNT_ID)).
						setName(r.getString(FULL_NAME));
			}
			else {
				return getNextAccount();
			}
		}
		
		return null;
	}
	
	public AcornCategory getNextAcornCategory() throws IOException {
		Row r = this.acctCursorSeq.getNextRow();
		
		if (r != null) {
			if (r.getBoolean("account")) {
				return getNextAcornCategory();
			}
			else {
				return new AcornCategory().
						setOrigId(r.getInt(ACCOUNT_ID)).
						setTitle(r.getString(FULL_NAME)).
						setPayment(r.getBoolean("payment")).
						setIncome(r.getBoolean("income"));
			}
		}
		
		return null;
	}
	
	public void resetAccountOrPayeeCursor() {
		this.acctCursorSeq.beforeFirst();
	}
	
	public void cacheAccount(Long origId, Account a) {
		this.accountMap.put(origId, a);
	}
	
	public void cacheAcornCategory(Long id, AcornCategory c) {
		this.categoryMap.put(id, c);
	}
	
	private Long int2Long(Integer i) {
		return i == null ? -1L : Long.valueOf(i);
	}
	
	public Transaction getNextTransaction() throws IOException {

		Row r = this.trnCursorSeq.getNextRow();	
		
		if (r != null) {
			Long fromId = int2Long(r.getInt(FROM_ACCOUNT_ID));
			Long toId = int2Long(r.getInt(TO_ACCOUNT_ID));
			Date date = r.getDate(DATE_ENTERED);
			
			// Last row in acorn data has all null values
			if (date == null) {
				return null;
			}
			
			Transaction t = new Transaction().
					setOrigId(Long.valueOf(r.getInt("ID"))).
					setSource(2).
					// Default to johnDoe, noPayee, noCategory, and debit amounts
					setAccount(this.johnDoeAccount).
					setPayee(this.noPayee).
					setCategory(this.noCategory).
					setAmount(- Util.decimal2long(r.getBigDecimal(AMOUNT))).
					setEntered(new Timestamp(date.getTime())).
					setMemo(r.getString(MEMO));
			
			Account leftAccount = this.accountMap.get(fromId);
			Account rightAccount = this.accountMap.get(toId);
			AcornCategory leftCategory = this.categoryMap.get(fromId);
			AcornCategory rightCategory = this.categoryMap.get(toId);
			
			if (leftAccount != null) {
				// 'from' is an account
				t.setAccount(leftAccount);				
				
				if (rightAccount != null) {
					// 'to' is an account - must be a transfer from one account to another
					Transfer tt = new Transfer(t);
					tt.setMirrorAccount(rightAccount);
					
					/*
					 * The Acorn database doesn't store mirror transactions, so what should we set
					 * origId to? Using -1L
					 */
					if (tt.getOrigId() == 0L) {
						tt.setOrigId(-1L);
					}
					
					return tt;
				}
				else if (rightCategory != null) {
					// This is a normal payment/credit
					t.setCategory(rightCategory);
				}
			}
			else if (leftCategory != null) {
				t.setCategory(leftCategory);
				
				if (rightAccount != null) {
					// This must be a credit payment into an account
					t.setAmount(- t.getAmount());
					t.setAccount(rightAccount);	
	
					// Special hack !!!
					if (leftCategory.getTitle().equals(AcornCategory.JOHN_REEKS)) {
						t.setPayee(this.johnReeks);
					}
				}
			}
			else {
				t.setMemo(String.format("%s [%s]", t.getMemo(), "CHECK DATA"));
				LOG.warn(String.format("Failed to categorise transaction [%s]", t));
			}
			
			return t;
		}
		
		return null;
	}
	
	@SuppressWarnings("unused")
	private AcornCategory identifyCategory(Long hcat) {
		// Category might be null ...
		AcornCategory c = null;
		
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
}
