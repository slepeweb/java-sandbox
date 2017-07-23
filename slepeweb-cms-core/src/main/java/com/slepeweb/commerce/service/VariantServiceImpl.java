package com.slepeweb.commerce.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.BaseServiceImpl;
import com.slepeweb.commerce.bean.AxisValueSelector;
import com.slepeweb.commerce.bean.Variant;
import com.slepeweb.commerce.util.CommerceRowMapper;

@Repository
public class VariantServiceImpl extends BaseServiceImpl implements VariantService {
	
	private static Logger LOG = Logger.getLogger(VariantServiceImpl.class);
	
	public Variant save(Variant v) throws ResourceException {
		if (! v.isDefined4Insert()) {
			throw new MissingDataException("Variant data not sufficient for db insert");
		}
		
		Variant dbRecord = get(v);	
				
		if (dbRecord != null) {
			update(dbRecord, v);
		}
		else {
			insert(v);
		}
		
		return v;
	}
	
	private void insert(Variant v) throws ResourceException {
		try {
			this.jdbcTemplate.update(
					"insert into variant (origitemid, qualifier, stock, price, alphavalueid, betavalueid) " +
					"values (?, ?, ?, ?, ?, ?)",
					v.getOrigItemId(), v.getQualifier(), v.getStock(), v.getPrice(), 
					v.getAlphaAxisValueId(), v.getBetaAxisValueId());				
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Variant already exists");
		}
		
		LOG.info(compose("Added new variant", v));		
	}

	private void update(Variant dbRecord, Variant v) {
		if (! dbRecord.equals(v)) {
			dbRecord.assimilate(v);
			
			this.jdbcTemplate.update(
					"update variant set qualifier = ?, stock = ?, price = ? where origitemid = ? and alphavalueid = ? and betavalueid = ?",
					dbRecord.getQualifier(), dbRecord.getStock(), dbRecord.getPrice(), 
					v.getOrigItemId(), v.getAlphaAxisValueId(), v.getBetaAxisValueId());
			
			LOG.info(compose("Updated variant", v));
			
		}
		else {
			LOG.info(compose("Variant not modified", v));
		}
		
	}
	
	public Variant get(Variant v) {
		return get(v.getOrigItemId(), v.getQualifier());
	}
	
	public Variant get(Long origItemId, String qualifier) {
		return (Variant) getLastInList(this.jdbcTemplate.query(
				"select * from variant where origitemid = ? and qualifier = ?", 
				new Object[] {origItemId, qualifier}, 
				new CommerceRowMapper.VariantMapper()));
	}
	
	public Variant get(Long origItemId, Long alphaAxisValueId, Long betaAxisValueId) {
		String sql = new String("select * from variant where origitemid = ? and "
				+ "alphavalueid = ? and betavalueid = ?");
		
		return (Variant) getLastInList(this.jdbcTemplate.query(
				sql, 
				new Object[] {origItemId, alphaAxisValueId, betaAxisValueId}, 
				new CommerceRowMapper.VariantMapper()));
	}
	
	public List<Variant> getMany(Long origItemId, Long alphaAxisValueId, Long betaAxisValueId) {
		StringBuilder sb = new StringBuilder("select * from variant where origitemid = ?");
		List<Object> list = new ArrayList<Object>();
		list.add(origItemId);
		appendAxisClause(sb, list, alphaAxisValueId, betaAxisValueId);
		
		return this.jdbcTemplate.query(sb.toString(), list.toArray(), new CommerceRowMapper.VariantMapper());
	}
	
	public List<Variant> getVariantsWithBetaAxis(Long origItemId, Long alphaAxisValueId) {
		return this.jdbcTemplate.query(
				"select v.origitemid, v.qualifier, v.stock, v.price, v.alphavalueid, v.betavalueid, " +
				"bv.id, bv.axisid, bv.value, bv.ordering " +						
				"from variant v " +
				"join axisvalue av on v.alphavalueid = av.id " +
				"join axisvalue bv on v.betavalueid = bv.id " +
				"where v.origitemid = ? and v.alphavalueid = ? order by bv.ordering",

				new Object[] {origItemId, alphaAxisValueId}, 
				new CommerceRowMapper.VariantAndBetaAxisValueMapper());
	}
	
	public AxisValueSelector getAlphaAxisSelector(Long origItemId, Long alphaAxisId) {
		List<AxisValueSelector.Option> alpha = this.jdbcTemplate.query(
				"select distinct av.* " +						
				"from variant v " +
				"join axisvalue av on v.alphavalueid = av.id " +
				"where v.origitemid = ? and " +
				"v.alphavalueid in (select id from axisvalue where axisid=?) " +
				"order by av.ordering",

				new Object[] {origItemId, alphaAxisId}, 
				new CommerceRowMapper.AxisValueSelectorMapper());
		
		return new AxisValueSelector().setOptions(alpha);
	}
	
	public void delete(Variant v) {
		delete(v.getOrigItemId(), v.getAlphaAxisValueId(), v.getBetaAxisValueId());
	}
	
	public void delete(Long origItemId, Long alphaAxisValueId, Long betaAxisValueId) {
		if (this.jdbcTemplate.update("delete from variant where origitemid = ? and alphavalueid = ? and betavalueid = ?", 
				origItemId, alphaAxisValueId, betaAxisValueId) > 0) {
			LOG.warn(compose("Deleted variant", String.valueOf(origItemId)));
		}
	}

	public void deleteMany(Long origItemId, Long alphaAxisValueId, Long betaAxisValueId) {
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
			
			if (betaAxisValueId != null && betaAxisValueId > 0) {
				sb.append(" and betavalueid = ?");
				list.add(betaAxisValueId);
			}
		}
	}

	public void deleteMany(Long origItemId) {
		deleteMany(origItemId, null, null);
	}
	
	@SuppressWarnings("deprecation")
	public long count() {
		return this.jdbcTemplate.queryForInt("select count(*) from variant");
	}
	
	public Long count(Long origId) {
		return this.jdbcTemplate.queryForObject(
				"select count(*) from variant where origitemid = ?", 
				Long.class,
				new Object[] {origId});
	}
}
