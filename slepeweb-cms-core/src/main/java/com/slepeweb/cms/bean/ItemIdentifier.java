package com.slepeweb.cms.bean;

public class ItemIdentifier {

	private long itemId;
	private String name, path;
	
	public ItemIdentifier(long i) {
		this.itemId = i;
	}

	public ItemIdentifier(String key) {
		this.itemId = Long.valueOf(key);
	}
	
	@Override
	public String toString() {
		return String.valueOf(getItemId());
	}

	public String getName() {
		return name;
	}

	public ItemIdentifier setName(String name) {
		this.name = name;
		return this;
	}

	public long getItemId() {
		return itemId;
	}

	public String getPath() {
		return path;
	}

	public ItemIdentifier setPath(String path) {
		this.path = path;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (itemId ^ (itemId >>> 32));
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
		ItemIdentifier other = (ItemIdentifier) obj;
		if (itemId != other.itemId)
			return false;
		return true;
	}
}
