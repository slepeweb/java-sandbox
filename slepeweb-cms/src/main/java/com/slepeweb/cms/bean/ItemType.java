package com.slepeweb.cms.bean;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ItemType extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private boolean media;
	private List<FieldForType> fieldsForType;
	
	public void assimilate(ItemType it) {
		setName(it.getName());
		setMedia(it.isMedia());
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName());
	}
	
	public FieldForType addFieldForType(Field f, Long ordering, boolean mandatory) {
		FieldForType fft = CmsBeanFactory.getFieldForType().setTypeId(getId()).
				setField(f).setOrdering(ordering).setMandatory(mandatory);
		getFieldsForType().add(fft);		
		return fft;
	}
	
	public List<FieldForType> getFieldsForType() {
		if (this.fieldsForType == null) {
			this.fieldsForType = getFieldForTypeService().getFieldsForType(getId());
		}
		return this.fieldsForType;
	}
	
	@Override
	public String toString() {
		return String.format("%s", getName());
	}
	
	public ItemType save() {
		return getItemTypeService().save(this);
	}
	
	public void delete() {
		getItemTypeService().deleteItemType(getId());
	}
	
	public Long getId() {
		return id;
	}
	
	public ItemType setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemType setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ItemType other = (ItemType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public boolean isMedia() {
		return media;
	}

	public ItemType setMedia(boolean media) {
		this.media = media;
		return this;
	}

}
