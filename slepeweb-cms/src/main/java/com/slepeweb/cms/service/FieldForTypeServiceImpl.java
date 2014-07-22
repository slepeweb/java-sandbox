package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class FieldForTypeServiceImpl extends BaseServiceImpl implements FieldForTypeService {
	
	private static Logger LOG = Logger.getLogger(FieldForTypeServiceImpl.class);
	
	public void insertFieldForType(FieldForType fft) {
		if (fft.isDefined4Insert()) {
			this.jdbcTemplate.update(
					"insert into fieldfortype (fieldid, itemtypeid, fieldorder, mandatory) values (?, ?, ?, ?)", 
					fft.getField().getId(), fft.getType().getId(), fft.getOrdering(), fft.isMandatory());
			
			LogUtil.info(LOG, "Inserted new field for type", fft.getField().getName());
		}
	}

	public void updateFieldForType(FieldForType fft) {
		if (fft.isDefined4Insert()) {
			FieldForType dbRow = getFieldForType(fft.getField().getId(), fft.getType().getId());
			
			if (dbRow != null) {
				dbRow.assimilate(fft);
				
				this.jdbcTemplate.update(
						"update fieldfortype set fieldorder = ?, mandatory = ? where fieldid = ? and itemtypeid = ?", 
						fft.getOrdering(), fft.isMandatory(), fft.getField().getId(), fft.getType().getId());
				
				LogUtil.info(LOG, "Updated field for type", fft.getType().getName());
			}
			else {
				LogUtil.warn(LOG, "Field for type not found", fft.getType().getName());
			}
		}
	}

	public void deleteFieldForType(Long fieldId, Long itemTypeId) {
		if (this.jdbcTemplate.update("delete from fieldfortype where fieldid = ? and itemtypeid = ?", fieldId, itemTypeId) > 0) {
			LogUtil.warn(LOG, "Deleted field for type", "");
		}
	}

	public FieldForType getFieldForType(Long fieldId, Long itemTypeId) {
		String sql = "select fft.*, f.id as fieldid, f.name as fieldname, f.variable, f.helptext, f.fieldtype, f.size, " +
				"it.name as itemtypename, it.id as itemtypeid from fieldfortype fft, field f, itemtype it where " +
				"fft.fieldid = f.id and fft.itemtypeid = it.id and fft.fieldid = ? and fft.itemtypeid = ?";
		
		Object[] params = new Object[] {fieldId, itemTypeId};
		
		List<FieldForType> group = this.jdbcTemplate.query(sql, params, new RowMapperUtil.FieldForTypeMapper());
			
		if (group.size() > 0) {
				return group.get(0);
			}
			else {
				return null;
			}
	}

}
