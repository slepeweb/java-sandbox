package com.slepeweb.cms.bean;

public class ItemIdentifier {

	private long itemId;
	private String name;
	
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

	public void setName(String name) {
		this.name = name;
	}

	public long getItemId() {
		return itemId;
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
