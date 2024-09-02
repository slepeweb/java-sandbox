package com.slepeweb.site.tags;

import com.slepeweb.site.model.SimpleComponent;


public class InsertSingleComponentTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = 1L;
	
	private SimpleComponent component;

	public void delegate() {
		this.render(this.component);
	}
	
	public void setComponent(SimpleComponent component) {
		this.component = component;
	}

}
