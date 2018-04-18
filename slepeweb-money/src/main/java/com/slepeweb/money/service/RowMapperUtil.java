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
			return makeCategory(rs.getLong("id"), rs.getString("major"), rs.getString("minor"));
		}
	}
	
	public static final class PayeeMapper implements RowMapper<Payee> {
		public Payee mapRow(ResultSet rs, int rowNum) throws SQLException {
			return makePayee(rs.getLong("id"), rs.getString("name"));
		}
	}
	
	public static final class AccountMapper implements RowMapper<Account> {
		public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
			return makeAccount(rs.getLong("id"), rs.getString("name"));
		}
	}
	
	public static final class PaymentMapper implements RowMapper<Payment> {
		public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Payment().
					setAccount(makeAccount(rs.getLong("accountid"), rs.getString("accountname"))).
					setPayee(makePayee(rs.getLong("payeeid"), rs.getString("payeename"))).
					setCategory(makeCategory(rs.getLong("categoryid"), rs.getString("major"), rs.getString("minor"))).
					setEntered(rs.getTimestamp("entered")).
					setTransfer(rs.getBoolean("transfer")).
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
					setCategory(makeCategory(rs.getLong("categoryid"), rs.getString("major"), rs.getString("minor"))).
					setCharge(rs.getLong("charge")).
					setMemo(rs.getString("memo"));
		}
	}
	
	private static Payee makePayee(long id, String name) {
		return new Payee().
			setId(id).
			setName(name);
	}
	
	private static Account makeAccount(long id, String name) {
		return new Account().
			setId(id).
			setName(name);
	}
	
	private static Category makeCategory(long id, String major, String minor) {
		return new Category().
			setId(id).
			setMajor(major).
			setMinor(minor);
	}
}
