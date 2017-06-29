package com.slepeweb.commerce.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.service.BaseServiceImpl;
import com.slepeweb.commerce.bean.Axis;
import com.slepeweb.commerce.util.CommerceRowMapper;

@Repository
public class AxisServiceImpl extends BaseServiceImpl implements AxisService {
	
	private static Logger LOG = Logger.getLogger(AxisServiceImpl.class);
	
	public Axis save(Axis a) throws ResourceException {
		if (! a.isDefined4Insert()) {
			throw new MissingDataException("Axis data not sufficient for db insert");
		}
		
		Axis dbRecord = get(a.getShortname());	
				
		if (dbRecord != null) {
			update(dbRecord, a);
			return dbRecord;
		}
		else {
			insert(a);
			return a;
		}			
	}
	
	private void insert(Axis a) throws ResourceException {
		try {
			this.jdbcTemplate.update(
					"insert into axis (shortname, label, units, description) " +
					"values (?, ?, ?, ?)",
					a.getShortname(), a.getLabel(), a.getUnits(), a.getDescription());	
			
			a.setId(getLastInsertId());
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Axis already exists");
		}
		
		LOG.info(compose("Added new Axis", a));		
	}

	private void update(Axis dbRecord, Axis a) {
		if (! dbRecord.equals(a)) {
			dbRecord.assimilate(a);
			
			this.jdbcTemplate.update(
					"update axis set shortname = ?, label = ?, units = ?, description = ? where id = ?",
					dbRecord.getShortname(), dbRecord.getLabel(), dbRecord.getUnits(), dbRecord.getDescription(), 
					a.getId());
			
			LOG.info(compose("Updated Axis", a));
			
		}
		else {
			LOG.info(compose("Axis not modified", a));
		}
		
	}
	
	public Axis get(String shortname) {
		return (Axis) getLastInList(this.jdbcTemplate.query(
				"select * from axis where shortname = ?", 
				new Object[] {shortname}, 
				new CommerceRowMapper.AxisMapper()));
	}
	
	public List<Axis> get() {
		return this.jdbcTemplate.query(
				"select * from axis order by shortname", 
				new Object[] {}, 
				new CommerceRowMapper.AxisMapper());
	}
	
	public Axis get(Long id) {
		return (Axis) getLastInList(this.jdbcTemplate.query(
				"select * from axis where id = ?", 
				new Object[] {id}, 
				new CommerceRowMapper.AxisMapper()));
	}
	
	public void delete(Long id) {
		if (this.jdbcTemplate.update("delete from axis where id = ?", id) > 0) {
			LOG.warn(compose("Deleted Axis", String.valueOf(id)));
		}
	}

	public void delete(Axis a) {
		delete(a.getId());
	}

}
