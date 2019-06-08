package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.SavedSearch;
import com.slepeweb.money.except.DataInconsistencyException;
import com.slepeweb.money.except.DuplicateItemException;
import com.slepeweb.money.except.MissingDataException;

@Service("savedSearchService")
public class SavedSearchServiceImpl extends BaseServiceImpl implements SavedSearchService {
	
	private static Logger LOG = Logger.getLogger(SavedSearchServiceImpl.class);
	
	public SavedSearch save(SavedSearch ss) throws MissingDataException, DuplicateItemException, DataInconsistencyException {
		if (ss.isDefined4Insert()) {
			if (ss.isInDatabase()) {
				SavedSearch dbRecord = get(ss.getId());		
				if (dbRecord != null) {
					return update(dbRecord, ss);
				}
				else {
					throw new DataInconsistencyException(error(LOG, "SavedSearch does not exist in DB", ss));
				}
			}
			else {
				return insert(ss);
			}
		}
		else {
			throw new MissingDataException(error(LOG, "SavedSearch not saved - insufficient data", ss));
		}
	}
	
	private SavedSearch insert(SavedSearch ss) throws MissingDataException, DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into search (name, type, saved, json) values (?, ?, ?, ?)", 
					ss.getName(), ss.getType(), ss.getSaved(), ss.getJson());
			
			ss.setId(getLastInsertId());	
			
			LOG.info(compose("Added new saved search", ss));		
			return ss;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("SavedSearch already inserted");
		}
	}

	public SavedSearch update(SavedSearch dbRecord, SavedSearch ss) {
		if (! dbRecord.equals(ss)) {
			dbRecord.assimilate(ss);
			
			this.jdbcTemplate.update(
					"update search set name = ?, type = ?, saved = ?, json = ? where id = ?", 
					dbRecord.getName(), dbRecord.getType(), dbRecord.getSaved(), dbRecord.getJson(), 
					dbRecord.getId());
			
			LOG.info(compose("Updated saved search", ss));
		}
		else {
			LOG.debug(compose("SavedSearch not modified", ss));
		}
		
		return dbRecord;
	}

	public SavedSearch get(long id) {
		return (SavedSearch) getFirstInList(this.jdbcTemplate.query(
				"select * from search where id = ?", 
				new Object[] {id}, 
				new RowMapperUtil.SavedSearchMapper()));
	}
	
	public List<SavedSearch> getAll() {
		return this.jdbcTemplate.query(
				"select * from search order by saved desc, name", 
				new RowMapperUtil.SavedSearchMapper());
	}
	
	public int delete(long id) {
		int num = this.jdbcTemplate.update("delete from search where id = ?", id);
		return num;
	}
}