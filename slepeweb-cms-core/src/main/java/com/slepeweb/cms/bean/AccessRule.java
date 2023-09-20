package com.slepeweb.cms.bean;

import org.apache.commons.lang3.StringUtils;

public class AccessRule extends CmsBean {

	private static final long serialVersionUID = 3525217730680353953L;
	private Long id, siteId;
	private String name, mode, tagPattern, templatePattern, itemPathPattern, ownerIdPattern, rolePattern;
	private boolean access, enabled;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		append("name", this.name, sb);
		append("tag", this.tagPattern, sb);
		append("ownerId", this.ownerIdPattern, sb);
		append("itemPath", this.itemPathPattern, sb);
		append("access", String.valueOf(this.access), sb);
		append("role2access", this.rolePattern, sb);
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
	
	/*
	 * Example regex patterns:
	 * 
	 * 	1) Match IFF test string does NOT contain complete word 'foo' OR 'bar':
	 * 			^(?!.*?\bfoo|bar\b).*$
	 * 
	 * 		Notes:
	 * 			^		Regexp must address the complete test string, ie from ^ to $
	 * 			?!		Keyword to indicate regexp is negated
	 * 			\b		Word boundaries
	 */


	
	private String negateIf(String p) {
		if (StringUtils.isNotBlank(p) && p.length() > 1 && p.startsWith("!")) {
			return "^(?!.*?\\b" + p.substring(1) + "\\b).*$";
		}
		
		return p;
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

	public Long getSiteId() {
		return siteId;
	}

	public AccessRule setSiteId(Long l) {
		this.siteId = l;
		return this;
	}

	public String getRolePattern() {
		return negateIf(rolePattern);
	}

	public AccessRule setOwnerIdPattern(String pattern) {
		this.ownerIdPattern = pattern;
		return this;
	}

	public String getOwnerIdPattern() {
		return negateIf(ownerIdPattern);
	}

	public AccessRule setRolePattern(String pattern) {
		this.rolePattern = pattern;
		return this;
	}

	public String getTagPattern() {
		return negateIf(tagPattern);
	}

	public AccessRule setTagPattern(String pattern) {
		this.tagPattern = pattern;
		return this;
	}

	public String getTemplatePattern() {
		return negateIf(templatePattern);
	}

	public AccessRule setTemplatePattern(String pattern) {
		this.templatePattern = pattern;
		return this;
	}

	public String getItemPathPattern() {
		return negateIf(itemPathPattern);
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
