package com.slepeweb.cms.bean;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class ItemType extends CmsBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	
	public void assimilate(ItemType it) {
		setName(it.getName());
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName());
	}
	
	public ItemType addField(Field f, Long ordering, boolean mandatory) {
		if (isServiced()) {
			FieldForType fft = new FieldForType().setType(this).setField(f).setOrdering(ordering).setMandatory(mandatory);
			getCmsService().getFieldForTypeService().insertFieldForType(fft);
		}
		
		return this;
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
}
