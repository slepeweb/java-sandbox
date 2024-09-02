package com.slepeweb.site.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import com.slepeweb.site.model.SimpleComponent;


public abstract class ComponentTagSupport extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATES_BASE = "/WEB-INF/jsp/";
	private static Logger LOG = Logger.getLogger(ComponentTagSupport.class);

	private String site;
	private Object poppedComponent;
	
	public abstract void delegate();
	
	@Override
	public int doStartTag() throws JspException {
		delegate();
		return SKIP_BODY;
	}
	
	protected void render(SimpleComponent c) {
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
	
	private void pushComponent(Object obj) {
		this.poppedComponent = this.pageContext.getAttribute("_comp", PageContext.REQUEST_SCOPE);
		this.pageContext.setAttribute("_comp", obj, PageContext.REQUEST_SCOPE);
	}

	private void popComponent() {
		this.pageContext.setAttribute("_comp", this.poppedComponent, PageContext.REQUEST_SCOPE);
	}

	private String identifyJspPath(SimpleComponent c) {
		return new StringBuilder(TEMPLATES_BASE).
				append(this.site).append("/component/").
				append(c.getType()).append(".jsp").
				toString();
	}

	public void setSite(String site) {
		this.site = site;
	}

}
