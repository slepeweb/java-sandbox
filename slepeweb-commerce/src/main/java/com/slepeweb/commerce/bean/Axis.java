package com.slepeweb.commerce.bean;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.CmsBean;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.utils.SpringContext;
import com.slepeweb.commerce.service.AxisService;

public class Axis extends CmsBean {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String label, units, description;
	private AxisService axisService;
	
	private AxisService getAxisService() {
		if (this.axisService == null) {
			this.axisService = (AxisService) SpringContext.getApplicationContext().getBean("axisService");
		}
		return this.axisService;
	}
	
	@Override
	public void assimilate(Object obj) {
		if (obj instanceof Axis) {
			Axis v = (Axis) obj;
			setLabel(v.getLabel());
			setUnits(v.getUnits());
			setDescription(v.getDescription());
		}
	}
	
	@Override
	public String toString() {
		return String.format("Axis '%s'", getLabel());
	}
	
	@Override
	protected CmsBean save() throws MissingDataException, DuplicateItemException {
		return getAxisService().save(this);
	}

	@Override
	public void delete() {
		getAxisService().delete(this);
	}

	@Override
	public boolean isDefined4Insert() throws MissingDataException {
		return getId() != null && StringUtils.isNotBlank(getLabel());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Axis other = (Axis) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}
	
	public Axis setId(Long id) {
		this.id = id;
		return this;
	}
	
	public String getLabel() {
		return label;
	}
	
	public Axis setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public String getUnits() {
		return units;
	}
	
	public Axis setUnits(String units) {
		this.units = units;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Axis setDescription(String description) {
		this.description = description;
		return this;
	}

}
