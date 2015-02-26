package com.slepeweb.site.tags;

import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public abstract class WrapMarkupTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger(WrapMarkupTag.class);
	
	private String clazz;
	protected abstract String getElementName();
	
	@Override
	public int doStartTag() {
		return EVAL_BODY_BUFFERED; 
	}
	
	@Override
	public int doEndTag() throws JspException {
		String targetPattern = String.format("<%s[ >]", getElementName());
		String startElement = String.format("<%s%s>", 
				getElementName(),
				this.clazz == null ? "" : " class=\"" + this.clazz + "\"");
		
		String endElement = String.format("</%s>", getElementName());
		BodyContent body = getBodyContent();
		String originalContent = body.getString();
		String replacement = originalContent;
		StringReader sr = new StringReader(originalContent);
		int bufflen = 100;
		char[] buff = new char[bufflen];
		
		try {
			if (sr.read(buff, 0, bufflen) > -1) {
				Pattern p = Pattern.compile("^\\s*" + targetPattern + ".*$");
				Matcher m = p.matcher(String.valueOf(buff));
				if (! m.matches()) {
					replacement = startElement + originalContent + endElement;
					LOG.debug(String.format("Body content wrapped with '%s' [%s]", 
							startElement, StringUtils.abbreviate(replacement, 50)));
				}
			}
			
			this.pageContext.getOut().print(replacement);
		}
		catch (IOException e) {
			LOG.warn(String.format("Failed to wrap body content with '%s' [%s]", 
					startElement, StringUtils.abbreviate(originalContent, 50)));
		}
		
		return EVAL_PAGE;
	}
	
	public void setClass(String s) {
		this.clazz = s;
	}
	
	public void release() {
		this.clazz = null;
	}
}
