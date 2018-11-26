package com.slepeweb.money.bean;

public class Payee extends DbEntity {
	private String name;
	
	public boolean isAccount() {
		return false;
	}
	
	public void assimilate(Object obj) {
		if (obj instanceof Payee) {
			Payee pe = (Payee) obj;
			setName(pe.getName());
			setOrigId(pe.getOrigId());
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
	
	@Override
	public Payee setId(long id) {
		super.setId(id);
		return this;
	}
	
	@Override
	public Payee setOrigId(long origId) {
		super.setOrigId(origId);
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
		
		// TODO: resolve this question:
		
//		if (origId != other.origId) {
//			return false;
//		}
		
		return true;
	}	
}
