package com.slepeweb.site.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.slepeweb.site.model.SimpleComponent;


public abstract class ComponentTagSupport extends TagSupport {
	
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATES_BASE = "/WEB-INF/jsp/";
	
	private String site;
	private Object poppedComponent;
	
	public abstract void delegate();
	
	@Override
	public int doStartTag() throws JspException {
		delegate();
		return SKIP_BODY;
	}
	
	protected void pushComponent(Object obj) {
		this.poppedComponent = this.pageContext.getAttribute("_comp", PageContext.REQUEST_SCOPE);
		this.pageContext.setAttribute("_comp", obj, PageContext.REQUEST_SCOPE);
	}

	protected void popComponent() {
		this.pageContext.setAttribute("_comp", this.poppedComponent, PageContext.REQUEST_SCOPE);
	}

	protected String identifyJspPath(SimpleComponent c) {
		return new StringBuilder(TEMPLATES_BASE).
				append(this.site).append("/component/").
				append(c.getType()).append(".jsp").
				toString();
	}

	public void setSite(String site) {
		this.site = site;
	}

}
