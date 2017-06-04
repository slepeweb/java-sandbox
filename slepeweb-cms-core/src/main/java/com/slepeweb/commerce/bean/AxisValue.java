package com.slepeweb.commerce.bean;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.CmsBean;
import com.slepeweb.cms.except.DuplicateItemException;
import com.slepeweb.cms.except.MissingDataException;
import com.slepeweb.cms.utils.SpringContext;
import com.slepeweb.commerce.service.AxisValueService;

public class AxisValue extends CmsBean {
	
	private static final long serialVersionUID = 1L;
	
	private Long id, axisId;
	private int ordering;
	private String value;
	private AxisValueService axisValueService;
	
	private AxisValueService getAxisValueService() {
		if (this.axisValueService == null) {
			this.axisValueService = (AxisValueService) SpringContext.getApplicationContext().getBean("axisValueService");
		}
		return this.axisValueService;
	}
	
	@Override
	public CmsBean save() throws MissingDataException, DuplicateItemException {
		return getAxisValueService().save(this);
	}

	@Override
	public void delete() {
		getAxisValueService().delete(this);
	}

	@Override
	public boolean isDefined4Insert() throws MissingDataException {
		return getId() != null && getAxisId() != null && StringUtils.isNotBlank(getValue());
	}

	@Override
	public void assimilate(Object obj) {
		if (obj instanceof AxisValue) {
			AxisValue v = (AxisValue) obj;
			setAxisId(v.getAxisId());
			setOrdering(v.getOrdering());
			setValue(v.getValue());
		}
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
		AxisValue other = (AxisValue) obj;
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
	
	public AxisValue setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Long getAxisId() {
		return axisId;
	}
	
	public AxisValue setAxisId(Long axisId) {
		this.axisId = axisId;
		return this;
	}
	
	public int getOrdering() {
		return ordering;
	}
	
	public AxisValue setOrdering(int ordering) {
		this.ordering = ordering;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	
	public AxisValue setValue(String value) {
		this.value = value;
		return this;
	}

}
