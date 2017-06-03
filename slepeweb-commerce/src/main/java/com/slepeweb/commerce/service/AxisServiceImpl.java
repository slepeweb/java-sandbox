package com.slepeweb.commerce.service;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.service.BaseServiceImpl;
import com.slepeweb.commerce.bean.Axis;
import com.slepeweb.commerce.util.CommerceRowMapper;

@Repository
public class AxisServiceImpl extends BaseServiceImpl implements AxisService {
	
	private static Logger LOG = Logger.getLogger(AxisServiceImpl.class);
	
	public Axis save(Axis a) throws MissingDataException, DuplicateItemException {
		if (! a.isDefined4Insert()) {
			throw new MissingDataException("Axis data not sufficient for db insert");
		}
		
		Axis dbRecord = get(a.getId());	
				
		if (dbRecord != null) {
			update(dbRecord, a);
		}
		else {
			insert(a);
		}
		
		return a;
	}
	
	private void insert(Axis a) throws MissingDataException, DuplicateItemException {
		try {
			this.jdbcTemplate.update(
					"insert into axis (id, label, units, description) " +
					"values (?, ?, ?, ?)",
					a.getId(), a.getLabel(), a.getUnits(), a.getDescription());				
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
					"update axis set label = ?, units = ?, description = ? where id = ?",
					dbRecord.getLabel(), dbRecord.getUnits(), dbRecord.getDescription(), 
					a.getId());
			
			LOG.info(compose("Updated Axis", a));
			
		}
		else {
			LOG.info(compose("Axis not modified", a));
		}
		
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

	@Override
	public void delete(Axis a) {
		delete(a.getId());
	}

}
