package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Chart;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("chartService")
public class ChartServiceImpl extends BaseServiceImpl implements ChartService {
	
	private static Logger LOG = Logger.getLogger(ChartServiceImpl.class);
	
	public Chart save(Chart ch) throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		if (ch.isDefined4Insert()) {
			if (ch.isInDatabase()) {
				Chart dbRecord = get(ch.getId());		
				if (dbRecord != null) {
					return update(dbRecord, ch);
				}
				else {
					throw new DataInconsistencyException(error(LOG, "SavedSearch does not exist in DB", ch));
				}
			}
			else {
				return insert(ch);
			}
		}
		else {
			throw new MissingDataException(error(LOG, "Chart not saved - insufficient data", ch));
		}
	}
	
	private Chart insert(Chart ch) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into chart (name, description, fromyear, toyear, searchids, notes) values (?, ?, ?, ?, ?, ?)", 
					ch.getName(), ch.getDescription(), ch.getFromYear(), ch.getToYear(), ch.getSearchIds(), ch.getNotes());
			
			ch.setId(getLastInsertId());	
			
			LOG.info(compose("Added new chart", ch));
			return ch;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Chart already inserted");
		}
	}

	public Chart update(Chart dbRecord, Chart ch) {
		if (! dbRecord.equals(ch)) {
			dbRecord.assimilate(ch);
			
			this.jdbcTemplate.update(
					"update chart set name = ?, description = ?, fromyear = ?, toyear = ?, searchids = ?, notes = ? where id = ?", 
					dbRecord.getName(), dbRecord.getDescription(), 
					dbRecord.getFromYear(), dbRecord.getToYear(), dbRecord.getSearchIds(), dbRecord.getNotes(),
					dbRecord.getId());
			
			LOG.info(compose("Updated chart", ch));
		}
		else {
			LOG.debug(compose("Chart not modified", ch));
		}
		
		return dbRecord;
	}

	public Chart get(long id) {
		try {
			return this.jdbcTemplate.queryForObject(
					"select * from chart where id = ?", 
					new RowMapperUtil.ChartMapper(), 
					id);
		}
		catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Chart> getAll() {
		return this.jdbcTemplate.query(
				"select * from chart order by name", 
				new RowMapperUtil.ChartMapper());
	}
	
	public int delete(long id) {
		int num = this.jdbcTemplate.update("delete from chart where id = ?", id);
		return num;
	}
}