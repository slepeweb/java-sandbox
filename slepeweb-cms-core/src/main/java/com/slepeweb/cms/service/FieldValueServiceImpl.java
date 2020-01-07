package com.slepeweb.cms.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.FieldValue;
import com.slepeweb.cms.bean.FieldValueSet;
import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class FieldValueServiceImpl extends BaseServiceImpl implements FieldValueService {
	
	private static Logger LOG = Logger.getLogger(FieldValueServiceImpl.class);
	private static final String SELECTOR_TEMPLATE = "select f.*, fv.* from field f, fieldvalue fv where " +
			"fv.fieldid = f.id and %s";
	
	@Autowired private ItemService itemService;
	
	public FieldValue save(FieldValue fv) throws ResourceException {
		if (fv.isDefined4Insert()) {
			FieldValue dbRecord = getFieldValue(fv.getField().getId(), fv.getItemId(), fv.getLanguage());		
			if (dbRecord != null) {
				updateFieldValue(dbRecord, fv);
				return dbRecord;
			}
			else {
				insertFieldValue(fv);
			}
		}
		else {
			String s = "FieldValue not saved - insufficient data";
			LOG.error(compose(s, fv));
			throw new MissingDataException(s);
		}
		
		return fv;
	}
	
	private void insertFieldValue(FieldValue fv) {
		this.jdbcTemplate.update(
				"insert into fieldvalue (fieldid, itemid, language, stringvalue, integervalue, datevalue) values (?, ?, ?, ?, ?, ?)", 
				fv.getField().getId(), fv.getItemId(), fv.getLanguage(), fv.getStringValue(), fv.getIntegerValue(), fv.getDateValue());
		
		// Note: No new id generated for this insert
		LOG.info(compose("Inserted new field value", fv));
	}

	private void updateFieldValue(FieldValue dbRecord, FieldValue fv) {
		if (! dbRecord.equals(fv)) {
			dbRecord.assimilate(fv);
			
			this.jdbcTemplate.update(
					"update fieldvalue set stringvalue = ?, integervalue = ?, datevalue = ? where fieldid = ? and itemid = ? and language = ?", 
					fv.getStringValue(), fv.getIntegerValue(), fv.getDateValue(), fv.getField().getId(), fv.getItemId(), fv.getLanguage());
			
			LOG.info(compose("Updated field value", fv));
		}
		else {
			LOG.info(compose("Field value unchanged", fv));
		}
	}

	public void deleteFieldValue(Long fieldId, Long itemId, String language) {
		if (this.jdbcTemplate.update("delete from fieldvalue where fieldid = ? and itemid = ? and language = ? ", fieldId, itemId, language) > 0) {
			LOG.warn(compose("Deleted field value", "-"));
		}
	}

	public int deleteFieldValues(Long fieldId, Long itemId) {
		return this.jdbcTemplate.update("delete from fieldvalue where fieldid = ? and itemid = ?", fieldId, itemId);
	}

	public int deleteFieldValues(Long itemId) {
		return this.jdbcTemplate.update("delete from fieldvalue where itemid = ?", itemId);
	}

	public FieldValue getFieldValue(Long fieldId, Long itemId, String language) {
		String sql = String.format(SELECTOR_TEMPLATE, "f.id = ? and fv.itemid = ? and fv.language = ?");		
		return (FieldValue) getFirstInList(this.jdbcTemplate.query(sql, new Object[] {fieldId, itemId, language}, 
				new RowMapperUtil.FieldValueMapper()));
	}

	public FieldValueSet getFieldValues(Long fieldId, Long itemId) {
		Item i = this.itemService.getItem(itemId);
		String sql = String.format(SELECTOR_TEMPLATE, "f.id = ? and fv.itemid = ?");		
		return new FieldValueSet(i.getSite(), this.jdbcTemplate.query(sql, new Object[] {fieldId, itemId}, 
				new RowMapperUtil.FieldValueMapper()));
	}

	public FieldValueSet getFieldValues(Long itemId) {
		Item i = this.itemService.getItem(itemId);
		String sql = String.format(SELECTOR_TEMPLATE, "fv.itemid = ?");	
		return new FieldValueSet(i.getSite(), this.jdbcTemplate.query(sql, new Object[] {itemId}, 
				new RowMapperUtil.FieldValueMapper()));
	}
	
	public int getCount() {
		return getCount(null);
	}
	
	@SuppressWarnings("deprecation")
	public int getCount(Long itemId) {
		if (itemId != null) {
			return this.jdbcTemplate.queryForInt("select count(*) from fieldvalue where itemid = ?", itemId);
		}
		else {
			return this.jdbcTemplate.queryForInt("select count(*) from fieldvalue");
		}
	}
	
}
