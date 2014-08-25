package com.slepeweb.cms.bean;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ItemType extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String CONTENT_FOLDER_TYPE_NAME = "Content Folder";
	public static final String PAGE_MIMETYPE = "application/cms";
	
	private Long id;
	private String name, mimeType = PAGE_MIMETYPE;
	private List<FieldForType> fieldsForType;
	
	public void assimilate(Object obj) {
		if (obj instanceof ItemType) {
			ItemType it = (ItemType) obj;
			setName(it.getName());
			setMimeType(it.getMimeType());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getMimeType());
	}
	
	public FieldForType addFieldForType(Field f, Long ordering, boolean mandatory) {
		FieldForType fft = CmsBeanFactory.makeFieldForType().setTypeId(getId()).
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
		return getMimeType().equals(PAGE_MIMETYPE);
	}

	public String getMimeType() {
		return mimeType;
	}

	public ItemType setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

}
