package com.slepeweb.money.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.money.bean.Account;
import com.slepeweb.money.bean.Category;
import com.slepeweb.money.bean.FlatTransaction;
import com.slepeweb.money.bean.NakedTransaction;
import com.slepeweb.money.bean.Payee;
import com.slepeweb.money.bean.Property;
import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.bean.ScheduledTransaction;
import com.slepeweb.money.bean.SplitTransaction;
import com.slepeweb.money.bean.Transaction;
import com.slepeweb.money.bean.Transfer;
import com.slepeweb.money.bean.User;

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
			long transferId = rs.getLong("transferid");
			Transaction t = transferId > 0 ? new Transfer() : new Transaction();
			
			return t.
					setAccount(makeAccountX(rs)).
					setPayee(makePayeeX(rs)).
					setCategory(makeCategoryX(rs)).
					setSplit(rs.getBoolean("split")).
					setEntered(rs.getTimestamp("entered")).
					setXferId(transferId).
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
					setId(rs.getString("id")).
					setAccount(rs.getString("account")).
					setPayee(rs.getString("payee")).
					setMajorCategory(rs.getString("major")).
					setMinorCategory(rs.getString("minor")).
					setEntered(rs.getTimestamp("entered")).
					setAmount(rs.getLong("amount")).
//					setReference(rs.getString("reference")).
					setMemo(rs.getString("memo"));
		}
	}
	
	public static final class SplitTransactionMapper implements RowMapper<SplitTransaction> {
		public SplitTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new SplitTransaction().
					setId(rs.getLong("id")).
					setTransactionId(rs.getLong("transactionid")).
					setCategory(makeCategoryX(rs)).
					setAmount(rs.getLong("amount")).
					setMemo(rs.getString("memo"));
		}
	}
	
	public static final class NakedTransactionMapper implements RowMapper<NakedTransaction> {
		public NakedTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new NakedTransaction().
					setEntered(rs.getTimestamp("entered")).
					setTransferid(rs.getLong("transferid")).
					setAmount(rs.getLong("amount")).
					setExpense(rs.getBoolean("expense"));			
		}
	}
	
	public static final class SavedSearchMapper implements RowMapper<SavedSearch> {
		public SavedSearch mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new SavedSearch().
					setId(rs.getLong("id")).
					setSaved(rs.getTimestamp("saved")).
					setName(rs.getString("name")).
					setType(rs.getString("type")).
					setJson(rs.getString("json")).
					setDescription(rs.getString("description"));
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
		return makeAccount(rs, "id", "origid", "name", "type", "reconciled");
	}
	
	private static Account makeAccountX(ResultSet rs) throws SQLException {
		return makeAccount(rs, "accountid", "accountorigid", "accountname", "accounttype", "accountreconciled");
	}
	
	private static Account makeAccount(ResultSet rs, String idStr, String origIdStr, 
			String name, String type, String reconciled) throws SQLException {
		
		return new Account().
			setId(rs.getLong(idStr)).
			setOrigId(rs.getLong(origIdStr)).
			setName(rs.getString(name)).
			setType(rs.getString(type)).
			setOpeningBalance(rs.getLong("openingbalance")).
			setClosed(rs.getBoolean("closed")).
			setNote(rs.getString("note")).
			setReconciled(rs.getLong(reconciled)).
			setBalance(rs.getLong("balance"));
	}
	
	private static Category makeCategory(ResultSet rs) throws SQLException {
		return makeCategory(rs, "id", "origid", "major", "minor", "expense");
	}
	
	private static Category makeCategoryX(ResultSet rs) throws SQLException {
		return makeCategory(rs, "categoryid", "categoryorigid", "major", "minor", "expense");
	}
	
	private static Category makeCategory(ResultSet rs, String id, String origId, String major, String minor, 
			String expense) throws SQLException {
		
		return new Category().
			setId(rs.getLong(id)).
			setOrigId(rs.getLong(origId)).
			setMajor(rs.getString(major)).
			setMinor(rs.getString(minor)).
			setExpense(rs.getBoolean(expense));
	}

	public static final class ScheduledTransactionMapper implements RowMapper<ScheduledTransaction> {
		public ScheduledTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			ScheduledTransaction scht = 
					new ScheduledTransaction().
					setLabel(rs.getString("label")).
					setNextDate(rs.getTimestamp("nextdate")).
					setPeriod(rs.getString("period")).
					setEnabled(rs.getBoolean("enabled"));
			
			scht.
					setAccount(
							new Account().
							setId(rs.getLong("accountid")).
							setName(rs.getString("accountname"))).
					setPayee(
							new Payee().
							setId(rs.getLong("payeeid")).
							setName(rs.getString("payeename"))).
					setCategory(
							new Category().
							setId(rs.getLong("categoryid")).
							setMajor(rs.getString("major")).
							setMinor(rs.getString("minor"))).
					setSplit(rs.getBoolean("split")).
					setAmount(rs.getLong("amount")).
					setReference(rs.getString("reference")).
					setMemo(rs.getString("memo")).
					setId(rs.getLong("id"));
			
			String mirrorName = rs.getString("mirrorname");
			if (StringUtils.isNotBlank(mirrorName)) {
				scht.setMirror(							
						new Account().
						setId(rs.getLong("mirrorid")).
						setName(rs.getString("mirrorname")));
			}
			
			return scht;
		}
	}
	
	public static final class ScheduledSplitMapper implements RowMapper<SplitTransaction> {
		public SplitTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new SplitTransaction().
					setId(rs.getLong("id")).
					setTransactionId(rs.getLong("scheduledtransactionid")).
					setCategory(
							new Category().
							setId(rs.getLong("categoryid")).
							setMajor(rs.getString("major")).
							setMinor(rs.getString("minor"))).
					setAmount(rs.getLong("amount")).
					setMemo(rs.getString("memo"));
		}
	}
	
	public static final class PropertyMapper implements RowMapper<Property> {
		public Property mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Property(rs.getString("name"), rs.getString("value"));
		}
	}
	
	public static final class UserMapper implements RowMapper<User> {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new User().
					setName(rs.getString("name")).
					setAlias(rs.getString("alias")).
					setPassword(rs.getString("password")).
					setEnabled(rs.getBoolean("enabled")).
					setRoles(rs.getString("roles"));
		}
	}
}
