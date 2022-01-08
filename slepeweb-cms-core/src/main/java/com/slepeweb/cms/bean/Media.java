package com.slepeweb.cms.bean;

import java.io.InputStream;
import java.sql.Blob;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.except.ResourceException;


public class Media extends CmsBean {
	private static final long serialVersionUID = 1L;
	private Long itemId, size;
	private boolean thumbnail;
	private Blob blob;
	private String folder;

	/* 
	 * This property is only used when file data has been submitted via an html form.
	 * It is ignored when media items are retrieved from the db. Instead, the media
	 * data is represented by the blob property.
	 * 
	 * When the media item is saved by MediaServiceImpl, the stream is closed at that
	 * point.
	 */
	private InputStream uploadStream;

	public void assimilate(Object obj) {
		if (obj instanceof Media) {
			Media m = (Media) obj;
			setItemId(m.getItemId());
			setSize(m.getSize());
			setBlob(m.getBlob()).
			setFolder(m.getFolder());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			getItemId() != null &&
			getUploadStream() != null;
	}
	
	public Long getId() {
		return NO_ID;
	}
	
	@Override
	public String toString() {
		return String.format("ItemId=%d: %s bytes", getItemId(), getSize());
	}

	@Override
	protected CmsBean save() throws ResourceException {
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

	public InputStream getUploadStream() {
		return uploadStream;
	}

	public Media setUploadStream(InputStream inputStream) {
		this.uploadStream = inputStream;
		return this;
	}

	public Blob getBlob() {
		return blob;
	}

	public Media setBlob(Blob blob) {
		this.blob = blob;
		return this;
	}
	
	public InputStream getDownloadStream() {
		try {
			if (this.blob != null) {
				return this.blob.getBinaryStream();
			}
			else if (StringUtils.isNotBlank(this.folder)) {
				return getMediaFileService().getInputStream(getFolder(), getRepositoryFileName());
			}
		}
		catch (Exception e) {
			
		}
		
		return null;
	}

	public boolean isThumbnail() {
		return thumbnail;
	}

	public Media setThumbnail(boolean thumbnail) {
		this.thumbnail = thumbnail;
		return this;
	}

	public String getFolder() {
		return folder;
	}

	public Media setFolder(String s) {
		this.folder = s;
		return this;
	}
	
	public boolean isFileStored() {
		return StringUtils.isNotBlank(getFolder());
	}
	
	public String getRepositoryFileName() {
		return String.format("%d%s", getItemId(), isThumbnail() ? "t" : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + (thumbnail ? 1231 : 1237);
		result = prime * result + ((folder == null) ? 0 : folder.hashCode());
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
		if (thumbnail != other.thumbnail)
			return false;
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
		if (folder == null) {
			if (other.folder != null)
				return false;
		} else if (!folder.equals(other.folder))
			return false;
		
		return true;
	}

}
