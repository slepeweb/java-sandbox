package com.slepeweb.money.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.NakedTransaction;

@Service("assetService")
public class AssetServiceImpl implements AssetService {
	
	@Autowired protected JdbcTemplate jdbcTemplate;	

	/*
	 * WARNING: This method returns partially-populated Transaction objects.
	 * Classes that call this method must be aware. The populated properties are:
	 * 		entered
	 * 		amount
	 * 		transferId
	 * 		memo (hack: using memo property to store the category type
	 */
	public List<NakedTransaction> get(Timestamp from, Timestamp to) {
		return this.jdbcTemplate.query(
				"select t.entered, t.amount, t.transferid, c.expense " + 
				"from transaction t, account a, category c " + 
				"where " +
				"t.accountid = a.id and " +
				"t.categoryid = c.id and " +
				"a.type != 'other' and " +
				"t.entered >= ? and " +
				"t.entered <= ? " +
				"order by t.entered", 
				new RowMapperUtil.NakedTransactionMapper(),
				new Object[]{from, to});
	}

}
