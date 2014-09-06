package com.slepeweb.cms.bean;

import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

public class FieldValue extends CmsBean {
	private static final long serialVersionUID = 1L;
	private Long itemId;
	private Field field;
	private String stringValue;
	private Integer integerValue;
	private Timestamp dateValue;
	
	public void assimilate(Object obj) {
		if (obj instanceof FieldValue) {
			FieldValue fv = (FieldValue) obj;
			setItemId(fv.getItemId());
			setField(fv.getField());
			setStringValue(fv.getStringValue());
			setIntegerValue(fv.getIntegerValue());
			setDateValue(fv.getDateValue());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			getItemId() != null &&
			getField() != null && getField().getId() != null;
	}
	
	public Long getId() {
		return NO_ID;
	}
	
	@Override
	public String toString() {
		return String.format("itemId=%d: %s {%s}", getItemId(), getField(), StringUtils.abbreviate(getStringValue(), 64));
	}
	
	public FieldValue save() {
		return getFieldValueService().save(this);
	}
	
	public void delete() {
		getFieldValueService().deleteFieldValue(getField().getId(), getItemId());
	}
	
	public String getInputTag() {
		return getField().getInputTag(getStringValue());
	}
	
	public Field getField() {
		return field;
	}

	public FieldValue setField(Field field) {
		this.field = field;
		return this;
	}
	
	public FieldValue setValue(Object value) {
		setStringValue(value.toString());
		
		if (value instanceof Integer) {
			setIntegerValue((Integer) value);
		}
		else if (value instanceof Timestamp) {
			setDateValue((Timestamp) value);
			setStringValue(getDateValue().toString());
		}

		return this;
	}
	
	public String getValue() {
		return getStringValue();
	}
	
	public String getStringValue() {
		return stringValue;
	}

	public FieldValue setStringValue(String stringValue) {
		this.stringValue = stringValue;
		return this;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public FieldValue setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
		return this;
	}

	public Timestamp getDateValue() {
		return dateValue;
	}

	public FieldValue setDateValue(Timestamp dateValue) {		
		this.dateValue = dateValue;
		this.dateValue.setNanos(0);
		return this;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getItemId() {
		return itemId;
	}

	public FieldValue setItemId(Long itemId) {
		this.itemId = itemId;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateValue == null) ? 0 : dateValue.hashCode());
		result = prime * result + ((field == null) ? 0 : field.getId().hashCode());
		result = prime * result + ((integerValue == null) ? 0 : integerValue.hashCode());
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldValue other = (FieldValue) obj;
		if (dateValue == null) {
			if (other.dateValue != null)
				return false;
		} else if (!dateValue.equals(other.dateValue))
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.getId().equals(other.field.getId()))
			return false;
		if (integerValue == null) {
			if (other.integerValue != null)
				return false;
		} else if (!integerValue.equals(other.integerValue))
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (stringValue == null) {
			if (other.stringValue != null)
				return false;
		} else if (!stringValue.equals(other.stringValue))
			return false;
		return true;
	}

}
