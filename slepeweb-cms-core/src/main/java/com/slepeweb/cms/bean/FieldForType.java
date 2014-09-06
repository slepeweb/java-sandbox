package com.slepeweb.cms.bean;


public class FieldForType extends CmsBean {
	private static final long serialVersionUID = 1L;
	private Long typeId;
	private Field field;
	private Long ordering;
	private boolean mandatory;
	
	public void assimilate(Object obj) {
		if (obj instanceof FieldForType) {
			FieldForType fft = (FieldForType) obj;
			setTypeId(fft.getTypeId());
			setField(fft.getField());
			setOrdering(fft.getOrdering());
			setMandatory(fft.isMandatory());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			getTypeId() != null &&
			getField() != null && getField().getId() != null;
	}
	
	public Long getId() {
		return NO_ID;
	}
	
	@Override
	public String toString() {
		return String.format("TypeId=%d: %s", getTypeId(), getField());
	}
	
	public FieldForType save() {
		return getFieldForTypeService().save(this);
	}
	
	public void delete() {
		getFieldForTypeService().deleteFieldForType(this);
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

	public Long getTypeId() {
		return typeId;
	}

	public FieldForType setTypeId(Long typeId) {
		this.typeId = typeId;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.getId().hashCode());
		result = prime * result + (mandatory ? 1231 : 1237);
		result = prime * result + ((ordering == null) ? 0 : ordering.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
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
		FieldForType other = (FieldForType) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.getId().equals(other.field.getId()))
			return false;
		if (mandatory != other.mandatory)
			return false;
		if (ordering == null) {
			if (other.ordering != null)
				return false;
		} else if (!ordering.equals(other.ordering))
			return false;
		if (typeId == null) {
			if (other.typeId != null)
				return false;
		} else if (!typeId.equals(other.typeId))
			return false;
		return true;
	}

}
