package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class Host extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String name, publicName, protocol = "http";
	private Long id;
	private Site site;
	private Integer port;
	private HostType type;
	
	public enum HostType {
		editorial, delivery, both;
	}

	public void assimilate(Object obj) {
		if (obj instanceof Host) {
			Host h = (Host) obj;
			setName(h.getName()).
			setPublicName(h.getPublicName()).
			setProtocol(h.getProtocol()).
			setSite(h.getSite()).
			setPort(h.getPort()).
			setType(h.getType());
		}
	}
	
	public boolean isDefined4Insert() {
		return 
			StringUtils.isNotBlank(getName()) &&
			getSite() != null && 
			getSite().getId() > 0 ;
	}
	
	@Override
	public String toString() {
		return String.format("(%s) %s", getType().name(), getNamePortAndProtocol());
	}
	
	public Host save() {
		return getHostService().save(this);
	}
	
	public void delete() {
		getHostService().deleteHost(this);
	}
	
	public String getName() {
		return name;
	}
	
	public String getNameAndPort() {
		return getCmsService().isDevDeployment() ? getInternalName() : getPublicName();
	}
	
	public String getNamePortAndProtocol() {
		StringBuilder sb = new StringBuilder(getProtocol()).
				append("://").
				append(getNameAndPort());
		
		return sb.toString();
	}
	
	public Host setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getPublicName() {
		return this.publicName;
	}

	public String getInternalName() {
		return String.format("%s:%d", getName(), getPort());
	}

	public Host setPublicName(String publicName) {
		this.publicName = publicName;
		return this;
	}

	public Long getId() {
		return id;
	}
	
	public Host setId(Long id) {
		this.id = id;
		return this;
	}
	
	public Site getSite() {
		return site;
	}

	public Host setSite(Site site) {
		this.site = site;
		return this;
	}
	
	public Integer getPort() {
		return port;
	}

	public Host setPort(Integer port) {
		this.port = port;
		return this;
	}

	public HostType getType() {
		return type;
	}

	public Host setType(HostType type) {
		this.type = type;
		return this;
	}

	public String getProtocol() {
		return protocol;
	}

	public Host setProtocol(String protocol) {
		this.protocol = protocol;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((publicName == null) ? 0 : publicName.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Host other = (Host) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (publicName == null) {
			if (other.publicName != null)
				return false;
		} else if (!publicName.equals(other.publicName))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (port == null) {
			if (other.port != null)
				return false;
		} else if (!port.equals(other.port))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
