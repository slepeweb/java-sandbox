package com.slepeweb.cms.except;

public class ResourceException extends Exception {

	private static final long serialVersionUID = 1L;

	public ResourceException(String message) {
		super(message);
	}
	
	@Override
	public String getMessage() {
		return String.format("%s: %s", getClass().getSimpleName(), super.getMessage());
	}
}
