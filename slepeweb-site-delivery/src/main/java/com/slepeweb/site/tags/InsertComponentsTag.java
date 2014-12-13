package com.slepeweb.site.tags;

import java.util.List;

import org.apache.log4j.Logger;

import com.slepeweb.site.model.SimpleComponent;


public class InsertComponentsTag extends ComponentTagSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(InsertComponentsTag.class);
	
	private List<SimpleComponent> list;

	public void delegate() {
		if (this.list != null) {
			for (SimpleComponent c : this.list) {
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
	
	public void setList(List<SimpleComponent> list) {
		this.list = list;
	}
}
