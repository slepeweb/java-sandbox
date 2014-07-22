package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.utils.LogUtil;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class FieldValueServiceImpl extends BaseServiceImpl implements FieldValueService {
	
	private static Logger LOG = Logger.getLogger(FieldValueServiceImpl.class);
	
	public void insertFieldValue(FieldValue fv) {
		if (fv.isDefined4Insert()) {
			this.jdbcTemplate.update(
					"insert into fieldvalue (fieldid, itemid, stringvalue, integervalue, datevalue) values (?, ?, ?, ?, ?)", 
					fv.getField().getId(), fv.getItemId(), fv.getStringValue(), fv.getIntegerValue(), fv.getDateValue());
			
			LogUtil.info(LOG, "Inserted new field value", fv.getField().getName(), fv.getItemId());
		}
	}

	public void updateFieldValue(FieldValue fv) {
		if (fv.isDefined4Insert()) {
			FieldValue dbRow = getFieldValue(fv.getField().getId(), fv.getItemId());
			
			if (dbRow != null) {
				dbRow.assimilate(fv);
				
				this.jdbcTemplate.update(
						"update fieldvalue set stringvalue = ?, integervalue = ?, datevalue = ? where fieldid = ? and itemid = ?", 
						fv.getStringValue(), fv.getIntegerValue(), fv.getDateValue(), fv.getField().getId(), fv.getItemId());
				
				LogUtil.info(LOG, "Updated field value", fv.getField().getName(), fv.getItemId());
			}
			else {
				LogUtil.warn(LOG, "Field value not found", fv.getField().getName(), fv.getItemId());
			}
		}
	}

	public void deleteFieldValue(Long fieldId, Long itemId) {
		if (this.jdbcTemplate.update("delete from fieldvalue where fieldid = ? and itemid = ?", fieldId, itemId) > 0) {
			LogUtil.warn(LOG, "Deleted field value", "-");
		}
	}

	public FieldValue getFieldValue(Long fieldId, Long itemId) {
		String sql = "select f.*, fv.* from field f, fieldvalue fv where " +
				"fv.fieldid = f.id and f.fieldid = ? and fv.itemid = ?";
		
		Object[] params = new Object[] {fieldId, itemId};
		
		List<FieldValue> group = this.jdbcTemplate.query(sql, params, new RowMapperUtil.FieldValueMapper());
			
		if (group.size() > 0) {
				return group.get(0);
			}
			else {
				return null;
			}
	}

}
