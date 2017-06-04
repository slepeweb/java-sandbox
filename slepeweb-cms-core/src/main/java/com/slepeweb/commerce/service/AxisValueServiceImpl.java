package com.slepeweb.commerce.service;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.service.BaseServiceImpl;
import com.slepeweb.commerce.bean.AxisValue;
import com.slepeweb.commerce.util.CommerceRowMapper;

@Repository
public class AxisValueServiceImpl extends BaseServiceImpl implements AxisValueService {
	
	private static Logger LOG = Logger.getLogger(AxisValueServiceImpl.class);
	
	public AxisValue save(AxisValue av) throws MissingDataException, DuplicateItemException {
		if (! av.isDefined4Insert()) {
			throw new MissingDataException("AxisValue data not sufficient for db insert");
		}
		
		AxisValue dbRecord = get(av.getId());	
				
		if (dbRecord != null) {
			update(dbRecord, av);
		}
		else {
			insert(av);
		}
		
		return av;
	}
	
	private void insert(AxisValue av) throws MissingDataException, DuplicateItemException {
		try {
			this.jdbcTemplate.update(
					"insert into axisvalue (axisid, value, ordering) " +
					"values (?, ?, ?)",
					av.getAxisId(), av.getValue(), av.getOrdering());				
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("AxisValue already exists");
		}
		
		LOG.info(compose("Added new AxisValue", av));		
	}

	private void update(AxisValue dbRecord, AxisValue av) {
		if (! dbRecord.equals(av)) {
			dbRecord.assimilate(av);
			
			this.jdbcTemplate.update(
					"update axisvalue set axisid = ?, value = ?, ordering = ? where id = ?",
					dbRecord.getAxisId(), dbRecord.getValue(), dbRecord.getOrdering(), 
					av.getId());
			
			LOG.info(compose("Updated AxisValue", av));
			
		}
		else {
			LOG.info(compose("AxisValue not modified", av));
		}
		
	}
	
	public AxisValue get(Long id) {
		return (AxisValue) getLastInList(this.jdbcTemplate.query(
				"select * from axisvalue where id = ?", 
				new Object[] {id}, 
				new CommerceRowMapper.AxisValueMapper()));
	}
	
	public void delete(Long id) {
		if (this.jdbcTemplate.update("delete from axisvalue where id = ?", id) > 0) {
			LOG.warn(compose("Deleted AxisValue", String.valueOf(id)));
		}
	}

	public void delete(AxisValue av) {
		delete(av.getId());
	}

}
