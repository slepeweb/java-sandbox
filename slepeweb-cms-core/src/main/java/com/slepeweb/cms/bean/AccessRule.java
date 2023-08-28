package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class AccessRule extends CmsBean {

	private static final long serialVersionUID = 3525217730680353953L;
	private Long id;
	private String name, mode, siteShortname, itemTypePattern, templatePattern, itemPathPattern, rolePattern;
	private boolean access, enabled;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		append("name", this.name, sb);
		append("itemType", this.itemTypePattern, sb);
		append("template", this.templatePattern, sb);
		append("itemPath", this.itemPathPattern, sb);
		append("role", this.rolePattern, sb);
		append("access", String.valueOf(this.access), sb);
		return sb.toString();
	}
	
	private void append(String name, String value, StringBuilder sb) {
		if (StringUtils.isNotBlank(name)) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(name).append("=").append(value);
		}
	}
	
	@Override
	public AccessRule save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub

	}

	@Override
	public Long getId() {
		return this.id;
	}

	public AccessRule setId(Long id) {
		this.id = id;
		return this;
	}

	@Override
	public boolean isDefined4Insert() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void assimilate(Object obj) {
		// TODO Auto-generated method stub

	}

	public String getMode() {
		return mode;
	}

	public AccessRule setMode(String mode) {
		this.mode = mode;
		return this;
	}

	public String getName() {
		return name;
	}

	public AccessRule setName(String name) {
		this.name = name;
		return this;
	}

	public String getSiteShortname() {
		return siteShortname;
	}

	public AccessRule setSiteShortname(String name) {
		this.siteShortname = name;
		return this;
	}

	public String getRolePattern() {
		return rolePattern;
	}

	public AccessRule setRolePattern(String pattern) {
		this.rolePattern = pattern;
		return this;
	}

	public String getItemTypePattern() {
		return itemTypePattern;
	}

	public AccessRule setItemTypePattern(String pattern) {
		this.itemTypePattern = pattern;
		return this;
	}

	public String getTemplatePattern() {
		return templatePattern;
	}

	public AccessRule setTemplatePattern(String pattern) {
		this.templatePattern = pattern;
		return this;
	}

	public String getItemPathPattern() {
		return itemPathPattern;
	}

	public AccessRule setItemPathPattern(String pattern) {
		this.itemPathPattern = pattern;
		return this;
	}

	public boolean isAccess() {
		return access;
	}

	public boolean givesAccess() {
		return access;
	}

	public AccessRule setAccess(boolean access) {
		this.access = access;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public AccessRule setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

}
