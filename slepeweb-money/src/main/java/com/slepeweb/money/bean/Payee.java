package com.slepeweb.money.bean;

public class Payee extends DbEntity {
	private String name;
	private long id;
	
	public boolean isAccount() {
		return false;
	}
	
	public void assimilate(Object obj) {
		if (obj instanceof Payee) {
			Payee pe = (Payee) obj;
			setName(pe.getName());
		}
	}
	
	public boolean isDefined4Insert() {
		return  
			getName() != null;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public long getId() {
		return id;
	}
	
	public Payee setId(long id) {
		this.id = id;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public Payee setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Payee other = (Payee) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public boolean matches(Object obj) {
		return equals(obj);
	}
}
