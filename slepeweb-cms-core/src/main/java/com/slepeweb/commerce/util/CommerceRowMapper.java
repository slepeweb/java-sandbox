package com.slepeweb.commerce.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.commerce.bean.Axis;
import com.slepeweb.commerce.bean.AxisValue;
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

	public static final class AxisMapper implements RowMapper<Axis> {
		public Axis mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Axis().
				setId(rs.getLong("id")).
				setLabel(rs.getString("label")).
				setUnits(rs.getString("units")).
				setDescription(rs.getString("description"));
		}
	}	

	public static final class AxisValueMapper implements RowMapper<AxisValue> {
		public AxisValue mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new AxisValue().
				setId(rs.getLong("id")).
				setAxisId(rs.getLong("axisid")).
				setValue(rs.getString("value")).
				setOrdering(rs.getInt("ordering"));
		}
	}	
}
