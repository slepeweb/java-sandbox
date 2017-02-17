package com.slepeweb.cms.bean;

import java.io.InputStream;
import java.sql.Blob;

import com.slepeweb.cms.except.MissingDataException;


public class Media extends CmsBean {
	private static final long serialVersionUID = 1L;
	private Long itemId, size;
	private InputStream inputStream;
	private Blob blob;
	
	public void assimilate(Object obj) {
		if (obj instanceof Media) {
			Media m = (Media) obj;
			setItemId(m.getItemId());
			setSize(m.getSize());
			setInputStream(m.getInputStream());
			setBlob(m.getBlob());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			getItemId() != null &&
			getSize() != null && 
			getInputStream() != null;
	}
	
	public Long getId() {
		return NO_ID;
	}
	
	@Override
	public String toString() {
		return String.format("ItemId=%d: %s bytes", getItemId(), getSize());
	}

	@Override
	protected CmsBean save() throws MissingDataException {
		return getMediaService().save(this);
	}

	@Override
	protected void delete() {
		// TODO Auto-generated method stub
		
	}
	
	public Long getItemId() {
		return itemId;
	}

	public Media setItemId(Long itemId) {
		this.itemId = itemId;
		return this;
	}

	public Long getSize() {
		return size;
	}

	public Media setSize(Long size) {
		this.size = size;
		return this;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public Media setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		return this;
	}

	public Blob getBlob() {
		return blob;
	}

	public Media setBlob(Blob blob) {
		this.blob = blob;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
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
		Media other = (Media) obj;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		return true;
	}

}
