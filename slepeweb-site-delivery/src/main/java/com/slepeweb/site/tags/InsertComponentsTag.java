package com.slepeweb.site.tags;

import java.util.List;

import org.apache.log4j.Logger;

import com.slepeweb.site.model.SimpleComponent;


public class InsertComponentsTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(InsertComponentsTag.class);
	
	private List<SimpleComponent> list;
	private String type;

	public void delegate() {
		if (this.list != null) {
			for (SimpleComponent c : this.list) {
				if (matchesFilter(c)) {
					pushComponent(c);
					
					try {
						this.pageContext.include(identifyJspPath(c), false);
					}
					catch (Exception e) {
						LOG.error(String.format("Failed to insert component [%s]", c), e);
					}
					finally {
						popComponent();
					}
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
