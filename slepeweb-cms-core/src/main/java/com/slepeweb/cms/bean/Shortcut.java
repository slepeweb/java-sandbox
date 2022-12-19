package com.slepeweb.cms.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Shortcut extends Item {
	
	private static final long serialVersionUID = 1L;
	private Item referred;
		
	public Item getReferred() {
		return this.referred;
	}

	public Shortcut setReferred(Item referred) {
		this.referred = referred;
		return this;
	}

	@Override
	public boolean isShortcut() {
		return true;
	}
	
	public boolean isComplete() {
		return this.referred != null;
	}
	
	private boolean isMergeable() {
		return isComplete() && ! getCmsService().isEditorialContext();
	}

	// There should only be one version of a Shortcut item.
	@Override
	public boolean isEditable() {
		if (isMergeable()) {
			return this.referred.isEditable();
		}
		return true;
	}

	// There should only be one version of a Shortcut item.
	@Override
	public boolean isPublished() {
		if (isMergeable()) {
			return this.referred.isPublished();
		}
		return true;
	}

	@Override
	public boolean isSearchable() {
		if (isMergeable()) {
			return this.referred.isSearchable();
		}
		return false;
	}

	@Override
	public List<Link> getLinks() {
		if (isMergeable()) {
			return this.referred.getLinks();
		}
		else if (getCmsService().isEditorialContext()) {
			return super.getLinks();
		}
		
		return new ArrayList<Link>();
	}
	
	@Override
	public List<Item> getAllVersions() {
		return new ArrayList<Item>();
	}
	
	@Override
	public FieldValueSet getFieldValueSet() {
		if (isMergeable()) {
			return this.referred.getFieldValueSet();
		}
		
		return new FieldValueSet(getSite(), new ArrayList<FieldValue>());
	}
	
	@Override
	public Template getTemplate() {
		if (isMergeable()) {
			return this.referred.getTemplate();
		}
		
		return null;
	}

	@Override
	public ItemType getType() {
		if (isMergeable()) {
			return this.referred.getType();
		}
		
		return super.getType();
	}

	@Override
	public boolean hasMedia() {
		if (isMergeable()) {
			return getCmsService().getMediaService().hasMedia(this.referred);
		}
		
		return false;
	}
	
	@Override
	public Media getMedia() {
		return getMedia(false);
	}
	
	@Override
	public Media getMedia(boolean thumbnailRequired) {
		if (isMergeable()) {
			return this.cmsService.getMediaService().getMedia(this.referred.getId(), thumbnailRequired);
		}
		
		return null;
	}
	
	@Override
	public boolean isDeleted() {
		if (isMergeable()) {
			return this.referred.isDeleted();
		}
		
		return super.isDeleted();
	}

	@Override
	public Timestamp getDateCreated() {
		if (isMergeable()) {
			return this.referred.getDateCreated();
		}
		
		return super.getDateCreated();
	}

	@Override
	public Timestamp getDateUpdated() {
		if (isMergeable()) {
			return this.referred.getDateUpdated();
		}
		
		return super.getDateUpdated();
	}
	
	@Override
	public long getIdentifier() {
		return isComplete() ? this.referred.getId() : -1;
	}

}
