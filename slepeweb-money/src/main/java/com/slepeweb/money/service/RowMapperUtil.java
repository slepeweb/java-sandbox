package com.slepeweb.money.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.PartPayment;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Payment;

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
	
	public static final class PaymentMapper implements RowMapper<Payment> {
		public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Payment().
					setAccount(makeAccount(rs)).
					setPayee(makePayee(rs)).
					setCategory(makeCategory(rs)).
					setEntered(rs.getTimestamp("entered")).
					setTransfer(makeAccount(rs)).
					setReconciled(rs.getBoolean("reconciled")).
					setCharge(rs.getLong("charge")).
					setReference(rs.getString("reference")).
					setMemo(rs.getString("memo"));
		}
	}
	
	public static final class PartPaymentMapper implements RowMapper<PartPayment> {
		public PartPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new PartPayment().
					setPaymentId(rs.getLong("paymentid")).
					setCategory(makeCategory(rs)).
					setCharge(rs.getLong("charge")).
					setMemo(rs.getString("memo"));
		}
	}
	
	private static Payee makePayee(ResultSet rs) throws SQLException {
		return new Payee().
			setId(rs.getLong("payeeid")).
			setName(rs.getString("payeename"));
	}
	
	private static Account makeAccount(ResultSet rs) throws SQLException {
		long id = rs.getLong("accountid");
		if (id != -1) {
			return new Account().
				setId(id).
				setName(rs.getString("accountname"));
		}
		return null;
	}
	
	private static Category makeCategory(ResultSet rs) throws SQLException {
		return new Category().
			setId(rs.getLong("categoryid")).
			setMajor(rs.getString("major")).
			setMinor(rs.getString("minor"));
	}
}
