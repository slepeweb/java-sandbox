package com.slepeweb.site.tags;

import org.apache.log4j.Logger;

import com.slepeweb.site.model.SimpleComponent;


public class InsertSingleComponentTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(InsertSingleComponentTag.class);
	
	private SimpleComponent component;

	public void delegate() {
		pushComponent(this.component);
		
		try {
			this.pageContext.include(identifyJspPath(this.component));
		}
		catch (Exception e) {
			LOG.error(String.format("Failed to insert component [%s]", this.component), e);
		}
		finally {
			popComponent();
		}
	}
	
	public void setComponent(SimpleComponent component) {
		this.component = component;
	}

}
