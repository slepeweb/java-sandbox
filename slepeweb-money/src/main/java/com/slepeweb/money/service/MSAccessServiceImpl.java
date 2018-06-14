package com.slepeweb.money.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

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
	private static String ACCOUNT = "account";
	private static String PAYEE = "payee";
	private static String DATE_ENTERED = "dateentered";
	private static String MEMO = "memo";
	private static String AMOUNT = "amount";
	private static String ORIG_ID = "origid";

	private String accessFilePath = "jdbc:ucanaccess:///media/george/Windows/Users/gbutt/home.mdb";
	private String trnQuery = String.format(
			"select trn.htrn as %s, acct.szFull as %s, pay.szFull as %s, trn.dt as %s, trn.mMemo as %s, trn.amt as %s " +
			"from trn " +
			"join acct on trn.hacct = acct.hacct " +
			"left join pay on trn.hpay = pay.hpay " +
			"left join trn_split trs on trn.htrn = trs.htrn " +
			"where trs.htrnParent is null", ORIG_ID, ACCOUNT, PAYEE, DATE_ENTERED, MEMO, AMOUNT);
	
	private Connection connection;
	private ResultSet row;
	
	private Connection getConnection() {
		if (this.connection == null) {
			try {
				Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
				this.connection = DriverManager.getConnection(this.accessFilePath);
			} 
			catch (Exception e) {
				LOG.error("Failed to open database file", e);
			}
		}
		
		return this.connection;
	}
	
	public ResultSet getNextTransaction() throws SQLException {
		if (this.row == null) {
				Statement s = getConnection().createStatement();
				this.row = s.executeQuery(this.trnQuery);
		}
		
		if (this.row.next()) {
			return this.row;
		}
		
		return null;
	}


	public String getAccount() throws SQLException {
		return this.row.getString(ACCOUNT);
	}
	

	public String getPayee() throws SQLException {
		return this.row.getString(PAYEE);
	}
	

	public Timestamp getDate() throws SQLException {
		return new Timestamp(this.row.getDate(DATE_ENTERED).getTime());
	}
	

	public String getMemo() throws SQLException {
		return this.row.getString(MEMO);
	}
	
	public long getAmount() throws SQLException {
		return new Float(this.row.getFloat(AMOUNT) * 100.0).longValue();
	}
	
	public long getOrigId() throws SQLException {
		return this.row.getLong(ORIG_ID) ;
	}
}
