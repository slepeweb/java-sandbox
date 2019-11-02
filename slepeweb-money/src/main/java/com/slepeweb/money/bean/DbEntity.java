package com.slepeweb.money.bean;

public abstract class DbEntity {
	
	private long id, origId;

	public long getId() {
		return id;
	}
	
	public Object setId(long id) {
		this.id = id;
		return null;
	}
	
	public long getOrigId() {
		return origId;
	}

	public Object setOrigId(long origId) {
		this.origId = origId;
		return null;
	}
	
	public boolean isLegacy() {
		return getOrigId() > 0; 
	}
	
	public boolean isInDatabase() {
		// id = -1 is only relevant to the ad-hoc saved-search, so it's
		// a bit of a hack to put this in the base controller ... sorry!
		return getId() == -1 || getId() > 0; 
	}
	
	/*
	 * The matches() method should indicate that two objects of the same class represent the
	 * same row in the corresponding database table.
	 * 
	 * NOTE that this method is not currently used, raising the question whether it was ill-informed ... ?
	 */
	public boolean matches(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		DbEntity other = (DbEntity) obj;
		if (getId() == other.getId()) {
			return true; 
		}
		
		if ((isLegacy() || other.isLegacy()) && (getOrigId() == other.getOrigId())) {
			return true;
		}
		
		return false;
	}

	public abstract void assimilate(Object obj);
	public abstract boolean isDefined4Insert();
}
