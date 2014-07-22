package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.Field;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class FieldServiceImpl extends BaseServiceImpl implements FieldService {
	
	private static Logger LOG = Logger.getLogger(FieldServiceImpl.class);
	
	public void insertField(Field f) {
		if (f.isDefined4Insert()) {
			this.jdbcTemplate.update(
					"insert into field (name, variable, fieldtype, helptext, size) values (?, ?, ?, ?, ?)", 
					f.getName(), f.getVariable(), f.getType().name(), f.getHelp(), f.getSize());				
			
			LogUtil.info(LOG, "Inserted new field", f.getName());
		}
	}

	public void updateField(Field field) {
		if (field.isDefined4Insert()) {
			Field dbRow = getField(field.getId());
			
			if (dbRow != null) {
				dbRow.assimilate(field);
				
				this.jdbcTemplate.update(
						"update field set name = ?, variable = ? fieldtype = ?, helptext = ?, size = ? where id = ?", 
						dbRow.getName(), dbRow.getVariable(), dbRow.getType().name(), dbRow.getHelp(), dbRow.getSize());
				
				LogUtil.info(LOG, "Updated field", field.getName());
			}
			else {
				LogUtil.warn(LOG, "Field not found", field.getName());
			}
		}
	}

	public void deleteField(Long id) {
		if (this.jdbcTemplate.update("delete from field where id = ?", id) > 0) {
			LogUtil.warn(LOG, "Deleted field", String.valueOf(id));
		}
	}

	public void deleteField(Field s) {
		deleteField(s.getId());
	}

	public Field getField(String name) {
		return getField("select * from field where name = ?", new Object[]{name});
	}

	public Field getField(Long id) {
		return getField("select * from field where id = ?", new Object[]{id});
	}
	
	private Field getField(String sql, Object[] params) {
		List<Field> group = this.jdbcTemplate.query(
			sql, params, new RowMapperUtil.FieldMapper());
		
		if (group.size() > 0) {
			return group.get(0);
		}
		else {
			return null;
		}
	}

}
