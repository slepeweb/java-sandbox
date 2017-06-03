package com.slepeweb.commerce.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.service.BaseServiceImpl;
import com.slepeweb.commerce.bean.Variant;
import com.slepeweb.commerce.util.CommerceRowMapper;

@Repository
public class VariantServiceImpl extends BaseServiceImpl implements VariantService {
	
	private static Logger LOG = Logger.getLogger(VariantServiceImpl.class);
	
	public Variant save(Variant v) throws MissingDataException, DuplicateItemException {
		if (! v.isDefined4Insert()) {
			throw new MissingDataException("Variant data not sufficient for db insert");
		}
		
		Variant dbRecord = getVariant(v.getOrigItemId(), 
				v.getAlphaAxisValueId(), 
				v.getBetaAxisValueId());	
				
		if (dbRecord != null) {
			updateVariant(dbRecord, v);
		}
		else {
			insertVariant(v);
		}
		
		return v;
	}
	
	private void insertVariant(Variant v) throws MissingDataException, DuplicateItemException {
		try {
			this.jdbcTemplate.update(
					"insert into variant (origitemid, sku, stock, price, alphavalueid, betavalueid) " +
					"values (?, ?, ?, ?, ?, ?)",
					v.getOrigItemId(), v.getSku(), v.getStock(), v.getPrice(), 
					v.getAlphaAxisValueId(), v.getBetaAxisValueId());				
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Variant already exists");
		}
		
		LOG.info(compose("Added new variant", v));		
	}

	private void updateVariant(Variant dbRecord, Variant v) {
		if (! dbRecord.equals(v)) {
			dbRecord.assimilate(v);
			
			this.jdbcTemplate.update(
					"update product set sku = ?, stock = ?, price = ?, alphavalueid = ?, betavalueid = ? where origitemid = ?",
					dbRecord.getSku(), dbRecord.getStock(), dbRecord.getPrice(), 
					v.getAlphaAxisValueId(), v.getBetaAxisValueId(), v.getOrigItemId());
			
			LOG.info(compose("Updated variant", v));
			
		}
		else {
			LOG.info(compose("Variant not modified", v));
		}
		
	}
	
	public Variant getVariant(Variant v) {
		return getVariant(v.getOrigItemId(), v.getAlphaAxisValueId(), v.getBetaAxisValueId());
	}
	
	public Variant getVariant(Long origItemId, Long alphaAxisValueId, Long betaAxisValueId) {
		return (Variant) getLastInList(this.jdbcTemplate.query(
				"select * from variant where origitemid = ? and alphavalueid = ? and betavalueid = ?", 
				new Object[] {origItemId, alphaAxisValueId, betaAxisValueId}, 
				new CommerceRowMapper.VariantMapper()));
	}
	
	public List<Variant> getVariants(Long origItemId, Long alphaAxisValueId, Long betaAxisValueId) {
		StringBuilder sb = new StringBuilder("select * from variant where origitemid = ?");
		List<Object> list = new ArrayList<Object>();
		list.add(origItemId);
		appendAxisClause(sb, list, alphaAxisValueId, betaAxisValueId);
		
		return this.jdbcTemplate.query(sb.toString(), list.toArray(), new CommerceRowMapper.VariantMapper());
	}
	
	public void deleteVariant(Variant v) {
		deleteVariant(v.getOrigItemId(), v.getAlphaAxisValueId(), v.getBetaAxisValueId());
	}
	
	public void deleteVariant(Long origItemId, Long alphaAxisValueId, Long betaAxisValueId) {
		if (this.jdbcTemplate.update("delete from variant where origitemid = ? and alphavalueid = ? and betavalueid = ?", 
				origItemId, alphaAxisValueId, betaAxisValueId) > 0) {
			LOG.warn(compose("Deleted variant", String.valueOf(origItemId)));
		}
	}

	public void deleteVariants(Long origItemId, Long alphaAxisValueId, Long betaAxisValueId) {
		StringBuilder sb = new StringBuilder("delete from variant where origitemid = ?");
		List<Object> list = new ArrayList<Object>();
		list.add(origItemId);		
		appendAxisClause(sb, list, alphaAxisValueId, betaAxisValueId);
		
		if (this.jdbcTemplate.update(sb.toString(), list.toArray()) > 0) {
			LOG.warn(compose("Deleted variant(s)", String.valueOf(origItemId)));
		}
	}
	
	private void appendAxisClause(StringBuilder sb, List<Object> list, 
			Long alphaAxisValueId, Long betaAxisValueId) {
				
		if (alphaAxisValueId != null) {
			sb.append(" and alphavalueid = ?");
			list.add(alphaAxisValueId);
			
			if (betaAxisValueId != null) {
				sb.append(" and betavalueid = ?");
				list.add(betaAxisValueId);
			}
		}
	}

	public void deleteVariants(Long origItemId) {
		deleteVariants(origItemId, null, null);
	}
}
