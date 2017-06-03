package com.slepeweb.commerce.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.bean.Variant;

public class CommerceRowMapper {

	public static final class ProductMapper implements RowMapper<Product> {
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Product().
				setOrigItemId(rs.getLong("origitemid")).
				setStock(rs.getInt("stock")).
				setPrice(rs.getInt("price")).
				setPartNum(rs.getString("partnum")).
				setAlphaAxisId(rs.getLong("alphaid")).
				setBetaAxisId(rs.getLong("betaid"));
		}
	}	

	public static final class VariantMapper implements RowMapper<Variant> {
		public Variant mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Variant().
				setOrigItemId(rs.getLong("origitemid")).
				setSku(rs.getString("sku")).
				setStock(rs.getInt("stock")).
				setPrice(rs.getInt("price")).
				setAlphaAxisValueId(rs.getLong("alphavalueid")).
				setBetaAxisValueId(rs.getLong("betavalueid"));
		}
	}	
}
