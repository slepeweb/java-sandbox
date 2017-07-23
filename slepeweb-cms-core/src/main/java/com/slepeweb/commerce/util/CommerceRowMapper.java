package com.slepeweb.commerce.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.slepeweb.cms.bean.CmsBeanFactory;
import com.slepeweb.commerce.bean.Axis;
import com.slepeweb.commerce.bean.AxisValue;
import com.slepeweb.commerce.bean.AxisValueSelector;
import com.slepeweb.commerce.bean.Product;
import com.slepeweb.commerce.bean.Variant;

public class CommerceRowMapper {

	public static final class ProductMapper implements RowMapper<Product> {
		public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeProduct().
				setOrigId(rs.getLong("origitemid")).
				setStock(rs.getLong("stock")).
				setPrice(rs.getLong("price")).
				setPartNum(rs.getString("partnum")).
				setAlphaAxisId(rs.getLong("alphaaxisid")).
				setBetaAxisId(rs.getLong("betaaxisid"));
		}
	}	

	public static final class VariantMapper implements RowMapper<Variant> {
		public Variant mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeVariant().
				setOrigItemId(rs.getLong("origitemid")).
				setQualifier(rs.getString("qualifier")).
				setStock(rs.getLong("stock")).
				setPrice(rs.getLong("price")).
				setAlphaAxisValueId(rs.getLong("alphavalueid")).
				setBetaAxisValueId(rs.getLong("betavalueid"));
		}
	}	

	public static final class VariantAndBetaAxisValueMapper implements RowMapper<Variant> {
		public Variant mapRow(ResultSet rs, int rowNum) throws SQLException {
			Variant v = CmsBeanFactory.makeVariant().
				setOrigItemId(rs.getLong("origitemid")).
				setQualifier(rs.getString("qualifier")).
				setStock(rs.getLong("stock")).
				setPrice(rs.getLong("price")).
				setAlphaAxisValueId(rs.getLong("alphavalueid")).
				setBetaAxisValueId(rs.getLong("betavalueid"));
			
			AxisValue beta = CmsBeanFactory.makeAxisValue().
					setId(rs.getLong("betavalueid")).
					setAxisId(rs.getLong("axisid")).
					setOrdering(rs.getInt("ordering")).
					setValue(rs.getString("value"));
			
			v.setBetaAxisValue(beta);
			return v;
		}
	}	

	public static final class AxisValueSelectorMapper implements RowMapper<AxisValueSelector.Option> {
		public AxisValueSelector.Option mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new AxisValueSelector.Option().
					setBody(rs.getString("value")).
					setValue(rs.getLong("id"));
		}
	}	

	public static final class AxisMapper implements RowMapper<Axis> {
		public Axis mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeAxis().
				setId(rs.getLong("id")).
				setShortname(rs.getString("shortname")).
				setLabel(rs.getString("label")).
				setUnits(rs.getString("units")).
				setDescription(rs.getString("description"));
		}
	}	

	public static final class AxisValueMapper implements RowMapper<AxisValue> {
		public AxisValue mapRow(ResultSet rs, int rowNum) throws SQLException {
			return CmsBeanFactory.makeAxisValue().
				setId(rs.getLong("id")).
				setAxisId(rs.getLong("axisid")).
				setValue(rs.getString("value")).
				setOrdering(rs.getInt("ordering"));
		}
	}	
}
