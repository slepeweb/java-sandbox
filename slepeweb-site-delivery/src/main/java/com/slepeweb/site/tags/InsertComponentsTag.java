package com.slepeweb.site.tags;

import java.util.List;

import com.slepeweb.site.model.SimpleComponent;


public class InsertComponentsTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = 1L;
	
	private List<SimpleComponent> list;
	private String type;

	public void delegate() {
		if (this.list != null) {
			for (SimpleComponent c : this.list) {
				if (matchesFilter(c)) {					
					this.render(c);
				}
			}
		}
	}
	
	@Override
	public void release() {
		this.type = null;
	}
	
	public void setList(List<SimpleComponent> list) {
		this.list = list;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	private boolean matchesFilter(SimpleComponent c) {
		return
			this.type == null || this.type.equals(c.getType());
	}
}
