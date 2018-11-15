package com.slepeweb.money.bean;

public class Category extends DbEntity {
	private String major = "", minor = "";
	private long id;
	
	public void assimilate(Object obj) {
		if (obj instanceof Category) {
			Category c = (Category) obj;
			setMajor(c.getMajor());
			setMinor(c.getMinor());
		}
	}
	
	public boolean isDefined4Insert() {
		return true; 
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getMajor());
		if (sb.length() > 0 && getMinor().length() > 0) {
			sb.append(" > ").append(getMinor());
		}
		return sb.toString();
	}
	
	public long getId() {
		return id;
	}
	
	public Category setId(long id) {
		this.id = id;
		return this;
	}
	
	public String getMajor() {
		return this.major;
	}
	
	public Category setMajor(String name) {
		this.major = name;
		return this;
	}

	public String getMinor() {
		return this.minor;
	}

	public Category setMinor(String minor) {
		this.minor = minor;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((major == null) ? 0 : major.hashCode());
		result = prime * result + ((minor == null) ? 0 : minor.hashCode());
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
		Category other = (Category) obj;
		if (major == null) {
			if (other.major != null)
				return false;
		} else if (!major.equals(other.major))
			return false;
		if (minor == null) {
			if (other.minor != null)
				return false;
		} else if (!minor.equals(other.minor))
			return false;
		return true;
	}

	@Override
	public boolean matches(Object obj) {
		return equals(obj);
	}
}
