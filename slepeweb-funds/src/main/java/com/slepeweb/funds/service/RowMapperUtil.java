package com.slepeweb.funds.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.funds.bean.Fund;
import com.slepeweb.funds.bean.FundPrice;

public class RowMapperUtil {

	public static final class FundMapper implements RowMapper<Fund> {
		public Fund mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Fund().
					setId(rs.getLong("id")).
					setName(rs.getString("name")).
					setAlias(rs.getString("alias")).
					setUnits(rs.getLong("units"));
		}
	}
	
	public static final class FundPriceMapper implements RowMapper<FundPrice> {
		public FundPrice mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new FundPrice().
					setFund(new Fund().
							setId(rs.getLong("fundid")).
							setName(rs.getString("name")).
							setAlias(rs.getString("alias")).
							setUnits(rs.getLong("units"))).
					setEntered(rs.getTimestamp("entered")).
					setValue(rs.getLong("value"));
		}
	}
	
	public static final class DistinctTimestampMapper implements RowMapper<Timestamp> {
		public Timestamp mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getTimestamp("entered");
		}
	}

}
