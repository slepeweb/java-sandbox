package com.slepeweb.cms.bean;

import java.io.Serializable;

public class FieldForType extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private ItemType type;
	private Field field;
	private Long ordering;
	private boolean mandatory;
	
	public void assimilate(FieldForType fft) {
		setType(fft.getType());
		setField(fft.getField());
		setOrdering(fft.getOrdering());
		setMandatory(fft.isMandatory());
	}
	
	public boolean isDefined4Insert() {
		return 
			getType() != null && getType().getId() != null &&
			getField() != null && getField().getId() != null;
	}
	
	public ItemType getType() {
		return type;
	}

	public FieldForType setType(ItemType type) {
		this.type = type;
		return this;
	}

	public Field getField() {
		return field;
	}

	public FieldForType setField(Field field) {
		this.field = field;
		return this;
	}

	public Long getOrdering() {
		return ordering;
	}

	public FieldForType setOrdering(Long ordering) {
		this.ordering = ordering;
		return this;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public FieldForType setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
		return this;
	}
}
