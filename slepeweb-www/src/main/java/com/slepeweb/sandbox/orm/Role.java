package com.slepeweb.sandbox.orm;


public class Role {
	
	public static final String GLOBAL_ADMIN_ROLE = "GLOBAL_ADMIN";
	public static final String USER_ADMIN_ROLE = "USER_ADMIN";
	public static final String AGENT_ROLE = "AGENT";
	
    private Integer id;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Role && 
				getName().equals(((Role) other).getName());
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
}
