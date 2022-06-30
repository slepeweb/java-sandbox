package com.slepeweb.cms.service;

import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class FieldServiceImpl extends BaseServiceImpl implements FieldService {
	
	private static Logger LOG = Logger.getLogger(FieldServiceImpl.class);
	
	public Field save(Field f) {
		if (f.isDefined4Insert()) {
			Field dbRecord = getField(f.getVariable());		
			if (dbRecord != null) {
				updateField(dbRecord, f);
				return dbRecord;
			}
			else {
				insertField(f);
			}
		}
		else {
			LOG.error(compose("Field not saved - insufficient data", f));
		}
		
		return f;
	}
	
	private void insertField(Field f) {
		this.jdbcTemplate.update(
				"insert into field (name, variable, multilingual, fieldtype, helptext, size, dflt, valid, validation) values (?, ?, ?, ?, ?, ?, ?, ?)", 
				f.getName(), f.getVariable(), f.isMultilingual(), f.getType().name(), f.getHelp(), f.getSize(), 
				f.getDefaultValue(), f.getValidValues());				
		
		f.setId(getLastInsertId());
		this.cacheEvictor.evict(f);
		LOG.info(compose("Inserted new field", f));
	}

	private void updateField(Field dbRecord, Field field) {
		if (! dbRecord.equals(field)) {
			this.cacheEvictor.evict(dbRecord);
			dbRecord.assimilate(field);
			
			this.jdbcTemplate.update(
					"update field set name = ?, variable = ?, multilingual = ?, fieldtype = ?, helptext = ?, size = ?, dflt = ?, valid = ? where id = ?", 
					dbRecord.getName(), dbRecord.getVariable(), dbRecord.isMultilingual(), dbRecord.getType().name(), 
					dbRecord.getHelp(), dbRecord.getSize(), dbRecord.getDefaultValue(), dbRecord.getValidValues(), dbRecord.getId());
			
			LOG.info(compose("Updated field", field));
		}
		else {
			field.setId(dbRecord.getId());
			LOG.info(compose("Field not modified", field));
		}
	}

	public void deleteField(Field f) {
		if (this.jdbcTemplate.update("delete from field where id = ?", f.getId()) > 0) {
			LOG.warn(compose("Deleted field", f.getName()));
			this.cacheEvictor.evict(f);
		}
	}
	
	@Cacheable(value="serviceCache")
	public Field getField(String variable) {
		return getField("select * from field where variable = ?", new Object[]{variable});
	}

	@Cacheable(value="serviceCache")
	public Field getField(Long id) {
		return getField("select * from field where id = ?", new Object[]{id});
	}
	
	private Field getField(String sql, Object[] params) {
		return (Field) getFirstInList(this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.FieldMapper()));
	}

	@SuppressWarnings("deprecation")
	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from field");
	}
}
