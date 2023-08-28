package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class Host extends CmsBean {
	private static final long serialVersionUID = 1L;
	private String name, protocol = "http";
	private Long id;
	private Site site;
	private Integer port;
	private HostType type;
	private Deployment deployment;
	
	public enum HostType {
		editorial, delivery, both;
	}

	public enum Deployment {
		development, production;
	}

		
	public void assimilate(Object obj) {
		if (obj instanceof Host) {
			Host h = (Host) obj;
			setName(h.getName()).
			setProtocol(h.getProtocol()).
			setSite(h.getSite()).
			setPort(h.getPort()).
			setType(h.getType());
			setDeployment(h.getDeployment());
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
		StringBuilder sb = new StringBuilder(getName());
		
		if (getPort() != null && getPort() != 80) {
			sb.append(":").append(getPort());
		}
		return sb.toString();
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

	public Deployment getDeployment() {
		return deployment;
	}

	public Host setDeployment(Deployment deployment) {
		this.deployment = deployment;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((deployment == null) ? 0 : deployment.hashCode());
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
		if (deployment != other.deployment)
			return false;
		return true;
	}

}
