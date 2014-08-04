package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class FieldValueServiceImpl extends BaseServiceImpl implements FieldValueService {
	
	private static Logger LOG = Logger.getLogger(FieldValueServiceImpl.class);
	private static final String SELECTOR_TEMPLATE = "select f.*, fv.* from field f, fieldvalue fv where " +
			"fv.fieldid = f.id and %s";
	
	public FieldValue save(FieldValue fv) {
		if (fv.isDefined4Insert()) {
			FieldValue dbRecord = getFieldValue(fv.getField().getId(), fv.getItemId());		
			if (dbRecord != null) {
				updateFieldValue(dbRecord, fv);
			}
			else {
				insertFieldValue(fv);
			}
		}
		
		return fv;
	}
	
	private void insertFieldValue(FieldValue fv) {
		this.jdbcTemplate.update(
				"insert into fieldvalue (fieldid, itemid, stringvalue, integervalue, datevalue) values (?, ?, ?, ?, ?)", 
				fv.getField().getId(), fv.getItemId(), fv.getStringValue(), fv.getIntegerValue(), fv.getDateValue());
		
		// Note: No new id generated for this insert
		LOG.info(compose("Inserted new field value", fv));
	}

	private void updateFieldValue(FieldValue dbRecord, FieldValue fv) {
		if (! dbRecord.equals(fv)) {
			dbRecord.assimilate(fv);
			
			this.jdbcTemplate.update(
					"update fieldvalue set stringvalue = ?, integervalue = ?, datevalue = ? where fieldid = ? and itemid = ?", 
					fv.getStringValue(), fv.getIntegerValue(), fv.getDateValue(), fv.getField().getId(), fv.getItemId());
			
			LOG.info(compose("Updated field value", fv));
		}
		else {
			LOG.info(compose("Field value unchanged", fv));
		}
	}

	public void deleteFieldValue(Long fieldId, Long itemId) {
		if (this.jdbcTemplate.update("delete from fieldvalue where fieldid = ? and itemid = ?", fieldId, itemId) > 0) {
			LOG.warn(compose("Deleted field value", "-"));
		}
	}

	public FieldValue getFieldValue(Long fieldId, Long itemId) {
		String sql = String.format(SELECTOR_TEMPLATE, "f.id = ? and fv.itemid = ?");		
		return (FieldValue) getFirstInList(this.jdbcTemplate.query(sql, new Object[] {fieldId, itemId}, 
				new RowMapperUtil.FieldValueMapper()));
	}

	public int deleteFieldValues(Long itemId) {
		return this.jdbcTemplate.update("delete from fieldvalue where itemid = ?", itemId);
	}

	public List<FieldValue> getFieldValues(Long itemId) {
		String sql = String.format(SELECTOR_TEMPLATE, "fv.itemid = ?");		
		return this.jdbcTemplate.query(sql, new Object[] {itemId}, new RowMapperUtil.FieldValueMapper());
	}

	public int getCount() {
		return getCount(null);
	}
	
	public int getCount(Long itemId) {
		if (itemId != null) {
			return this.jdbcTemplate.queryForInt("select count(*) from fieldvalue where itemid = ?", itemId);
		}
		else {
			return this.jdbcTemplate.queryForInt("select count(*) from fieldvalue");
		}
	}
	
}
