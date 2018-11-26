package com.slepeweb.money.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.Transaction;

public class RowMapperUtil {

	public static final class CategoryMapper implements RowMapper<Category> {
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			return makeCategory(rs);
		}
	}
	
	public static final class PayeeMapper implements RowMapper<Payee> {
		public Payee mapRow(ResultSet rs, int rowNum) throws SQLException {
			return makePayee(rs);
		}
	}
	
	public static final class AccountMapper implements RowMapper<Account> {
		public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
			return makeAccount(rs);
		}
	}
	
	public static final class TransactionMapper implements RowMapper<Transaction> {
		public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Transaction().
					setAccount(makeAccountX(rs)).
					setPayee(makePayeeX(rs)).
					setCategory(makeCategoryX(rs)).
					setSplit(rs.getBoolean("split")).
					setEntered(rs.getTimestamp("entered")).
					setXferId(rs.getLong("transferid")).
					setReconciled(rs.getBoolean("reconciled")).
					setAmount(rs.getLong("amount")).
					setReference(rs.getString("reference")).
					setMemo(rs.getString("memo")).
					setId(rs.getLong("id")).
					setOrigId(rs.getLong("origid"));
		}
	}
	
	public static final class TransactionDateMapper implements RowMapper<Timestamp> {
		public Timestamp mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getTimestamp("entered");
		}
	}
	
	public static final class FlatTransactionMapper implements RowMapper<FlatTransaction> {
		public FlatTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new FlatTransaction().
					setAccount(rs.getString("account")).
					setPayee(rs.getString("payee")).
					setCategory(rs.getString("major") + " > " + rs.getString("minor")).
					setEntered(rs.getTimestamp("entered")).
					setAmount(rs.getLong("amount")).
					setReference(rs.getString("reference")).
					setMemo(rs.getString("memo"));
		}
	}
	
	public static final class SplitTransactionMapper implements RowMapper<SplitTransaction> {
		public SplitTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new SplitTransaction().
					setTransactionId(rs.getLong("transactionid")).
					setCategory(makeCategoryX(rs)).
					setAmount(rs.getLong("amount")).
					setMemo(rs.getString("memo"));
		}
	}
	
	private static Payee makePayee(ResultSet rs) throws SQLException {
		return makePayee(rs, "id", "origid", "name");
	}
	
	private static Payee makePayeeX(ResultSet rs) throws SQLException {
		return makePayee(rs, "payeeid", "payeeorigid", "payeename");
	}
	
	private static Payee makePayee(ResultSet rs, String idStr, String origIdStr, String name) throws SQLException {
		return new Payee().
			setId(rs.getLong(idStr)).
			setOrigId(rs.getLong(origIdStr)).
			setName(rs.getString(name));
	}
	
	private static Account makeAccount(ResultSet rs) throws SQLException {
		return makeAccount(rs, "id", "origid", "name", "type");
	}
	
	private static Account makeAccountX(ResultSet rs) throws SQLException {
		return makeAccount(rs, "accountid", "accountorigid", "accountname", "accounttype");
	}
	
	private static Account makeAccount(ResultSet rs, String idStr, String origIdStr, 
			String name, String type) throws SQLException {
		
		return new Account().
			setId(rs.getLong(idStr)).
			setOrigId(rs.getLong(origIdStr)).
			setName(rs.getString(name)).
			setType(rs.getString(type)).
			setOpeningBalance(rs.getLong("openingbalance")).
			setClosed(rs.getBoolean("closed")).
			setNote(rs.getString("note"));
	}
	
	private static Category makeCategory(ResultSet rs) throws SQLException {
		return makeCategory(rs, "id", "origid", "major", "minor");
	}
	
	private static Category makeCategoryX(ResultSet rs) throws SQLException {
		return makeCategory(rs, "categoryid", "categoryorigid", "major", "minor");
	}
	
	private static Category makeCategory(ResultSet rs, String id, String origId, String major, String minor) throws SQLException {
		return new Category().
			setId(rs.getLong(id)).
			setOrigId(rs.getLong(origId)).
			setMajor(rs.getString(major)).
			setMinor(rs.getString(minor));
	}
}
