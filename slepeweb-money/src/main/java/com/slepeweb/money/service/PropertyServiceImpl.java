package com.slepeweb.money.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.slepeweb.money.bean.Property;
import com.slepeweb.money.except.DuplicateItemException;

@Service("propertyService")
public class PropertyServiceImpl extends BaseServiceImpl implements PropertyService {
	
	private static Logger LOG = Logger.getLogger(PropertyServiceImpl.class);

	public Property save(Property pair) throws DuplicateItemException {
		Property dbRecord = get(pair.getKey());		
		if (dbRecord != null) {
			return update(dbRecord, pair);
		}
		else {
			return insert(pair);
		}
	}
	
	private Property insert(Property pair) throws DuplicateItemException {
		
		try {
			this.jdbcTemplate.update(
					"insert into property (name, value) values (?, ?)", 
					pair.getKey(), pair.getValue());
			
			LOG.info(compose("Added new property", pair));		
			return pair;
		}
		catch (DuplicateKeyException e) {
			throw new DuplicateItemException("Property already inserted");
		}
	}

	public Property update(Property dbRecord, Property pair) {
		if (! dbRecord.equals(pair)) {
			dbRecord = pair;
			
			this.jdbcTemplate.update(
					"update property set value = ? where name = ?", 
					dbRecord.getValue(), dbRecord.getKey());
			
			LOG.info(compose("Updated property", pair));
		}
		else {
			LOG.debug(compose("Property not modified", pair));
		}
		
		return dbRecord;
	}

	public Property get(String name) {
		return (Property) getFirstInList(this.jdbcTemplate.query(
			"select * from property where name = ?", 
			new Object[]{name}, 
			new RowMapperUtil.PropertyMapper()));
	}

	public List<Property> getAll() {
		String sql = "select * from property order by name";
		return this.jdbcTemplate.query(sql, new RowMapperUtil.PropertyMapper());
	}
	
	public int delete(String name) {
		return this.jdbcTemplate.update("delete from property where name = ?", name);
	}
}
