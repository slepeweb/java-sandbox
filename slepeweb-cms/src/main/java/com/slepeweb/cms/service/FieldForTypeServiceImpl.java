package com.slepeweb.cms.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.slepeweb.cms.bean.FieldForType;
import com.slepeweb.cms.utils.RowMapperUtil;

@Repository
public class FieldForTypeServiceImpl extends BaseServiceImpl implements FieldForTypeService {
	
	private static Logger LOG = Logger.getLogger(FieldForTypeServiceImpl.class);
	private static final String SELECTOR_TEMPLATE = 
			"select fft.*, f.* from fieldfortype fft, field f where " +
			"fft.fieldid = f.id and %s";
	
	public FieldForType save(FieldForType fft) {
		if (fft.isDefined4Insert()) {
			FieldForType dbRecord = getFieldForType(fft.getField().getId(), fft.getTypeId());		
			if (dbRecord != null) {
				updateFieldForType(dbRecord, fft);
			}
			else {
				insertFieldForType(fft);
			}
		}
		
		return fft;
	}
	
	private void insertFieldForType(FieldForType fft) {
		this.jdbcTemplate.update(
				"insert into fieldfortype (fieldid, itemtypeid, fieldorder, mandatory) values (?, ?, ?, ?)", 
				fft.getField().getId(), fft.getTypeId(), fft.getOrdering(), fft.isMandatory());
		
		// Note: No new key generated for this insert
		LOG.info(compose("Inserted new field for type", fft));
	}

	private void updateFieldForType(FieldForType dbRecord, FieldForType fft) {
		if (! dbRecord.equals(fft)) {
			dbRecord.assimilate(fft);
			
			this.jdbcTemplate.update(
					"update fieldfortype set fieldorder = ?, mandatory = ? where fieldid = ? and itemtypeid = ?", 
					fft.getOrdering(), fft.isMandatory(), fft.getField().getId(), fft.getTypeId());
			
			LOG.info(compose("Updated field for type", fft));
		}
		else {
			LOG.debug(compose("Field for type already defined", fft));
		}
	}

	public void deleteFieldForType(Long fieldId, Long itemTypeId) {
		if (this.jdbcTemplate.update("delete from fieldfortype where fieldid = ? and itemtypeid = ?", fieldId, itemTypeId) > 0) {
			LOG.warn(compose("Deleted field for type", ""));
		}
	}

	public FieldForType getFieldForType(Long fieldId, Long itemTypeId) {
		Object[] params = new Object[] {fieldId, itemTypeId};
		
		String sql = String.format(SELECTOR_TEMPLATE, "fft.fieldid = ? and fft.itemtypeid = ?");
		List<FieldForType> group = this.jdbcTemplate.query(sql, params, new RowMapperUtil.FieldForTypeMapper());
			
		if (group.size() > 0) {
			return group.get(0);
		}
		else {
			return null;
		}
	}

	public List<FieldForType> getFieldsForType(Long itemTypeId) {
		Object[] params = new Object[] {itemTypeId};
		
		String sql = String.format(SELECTOR_TEMPLATE, "fft.itemtypeid = ?");
		return this.jdbcTemplate.query(sql, params, new RowMapperUtil.FieldForTypeMapper());
	}

	public int getCount() {
		return getCount(null);
	}
	
	public int getCount(Long itemTypeId) {
		if (itemTypeId != null) {
			return this.jdbcTemplate.queryForInt("select count(*) from fieldfortype where itemtypeid = ?", itemTypeId);
		}
		else {
			return this.jdbcTemplate.queryForInt("select count(*) from fieldfortype");
		}
	}
}
