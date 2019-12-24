package com.slepeweb.site.anc.bean;

import com.slepeweb.site.model.LinkTarget;

public class MenuItem extends LinkTarget {

	private static final long serialVersionUID = 1L;
	private boolean enabled = true;

	public String getTag() {
		if (! isSelected()) {
			return String.format("<a href=\"%s\">%s</a>", getHref(), getTitle());
		}
		return String.format("<strong>%s</strong>", getTitle());
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public MenuItem setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
}
