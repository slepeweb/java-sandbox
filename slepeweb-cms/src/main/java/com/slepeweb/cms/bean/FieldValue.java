package com.slepeweb.cms.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class FieldValue extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long itemId;
	private Field field;
	private String stringValue;
	private Integer integerValue;
	private Timestamp dateValue;
	
	public void assimilate(FieldValue fv) {
		setItemId(fv.getItemId());
		setField(fv.getField());
		setStringValue(fv.getStringValue());
		setIntegerValue(fv.getIntegerValue());
		setDateValue(fv.getDateValue());
	}
	
	public boolean isDefined4Insert() {
		return 
			getItemId() != null &&
			getField() != null && getField().getId() != null;
	}
	
	public Field getField() {
		return field;
	}

	public FieldValue setField(Field field) {
		this.field = field;
		return this;
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
		return this;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

}
