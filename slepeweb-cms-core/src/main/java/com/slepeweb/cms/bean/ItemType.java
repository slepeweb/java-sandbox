package com.slepeweb.cms.bean;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Field.FieldType;

public class ItemType extends CmsBean {
	private static final long serialVersionUID = 1L;
	public static final String CONTENT_FOLDER_TYPE_NAME = "Content Folder";
	public static final String PAGE_MIMETYPE = "application/cms";
	public static final String JS_MIMETYPE = "text/javascript";
	
	private Long id, privateCache = 0L, publicCache = 0L;
	private String name, mimeType = PAGE_MIMETYPE;
	private List<FieldForType> fieldsForType;
	
	public void assimilate(Object obj) {
		if (obj instanceof ItemType) {
			ItemType it = (ItemType) obj;
			setName(it.getName());
			setMimeType(it.getMimeType());
			setPrivateCache(it.getPrivateCache());
			setPublicCache(it.getPublicCache());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			StringUtils.isNotBlank(getMimeType()) &&
			getPrivateCache() != null &&
			getPublicCache() != null;
	}
	
	public FieldForType addFieldForType(Field f, Long ordering, boolean mandatory) {
		FieldForType fft = CmsBeanFactory.makeFieldForType().setTypeId(getId()).
				setField(f).setOrdering(ordering).setMandatory(mandatory);
		getFieldsForType().add(fft);		
		return fft;
	}
	
	public List<FieldForType> getFieldsForType() {
		return getFieldsForType(true);
	}
	
	public List<FieldForType> getFieldsForType(boolean omitLayouts) {
		if (this.fieldsForType == null) {
			this.fieldsForType = getFieldForTypeService().getFieldsForType(getId());
			
			if (omitLayouts) {
				Iterator<FieldForType> iter = this.fieldsForType.iterator();
				FieldForType fft;
				while (iter.hasNext()) {
					fft = iter.next();
					if (fft.getField().getType() == FieldType.layout) {
						iter.remove();
					}
				}
			}
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
		getItemTypeService().deleteItemType(this);
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

	public Long getPrivateCache() {
		return privateCache;
	}

	public ItemType setPrivateCache(Long privateCache) {
		this.privateCache = privateCache;
		return this;
	}

	public Long getPublicCache() {
		return publicCache;
	}

	public ItemType setPublicCache(Long publicCache) {
		this.publicCache = publicCache;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((privateCache == null) ? 0 : privateCache.hashCode());
		result = prime * result + ((publicCache == null) ? 0 : publicCache.hashCode());
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
		if (privateCache == null) {
			if (other.privateCache != null)
				return false;
		} else if (!privateCache.equals(other.privateCache))
			return false;
		if (publicCache == null) {
			if (other.publicCache != null)
				return false;
		} else if (!publicCache.equals(other.publicCache))
			return false;
		return true;
	}

	// TODO: This would be better handled by adding a media column to the itemtype table
	public boolean isMedia() {
		return 
			! getMimeType().equals(PAGE_MIMETYPE) &&
			! getMimeType().equals(JS_MIMETYPE);
	}

	public boolean isImage() {
		return getMimeType().startsWith("image");
	}

	public String getMimeType() {
		return mimeType;
	}

	public ItemType setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}

}
